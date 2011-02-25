package org.sindice.rdfcommons.adapter.sesame;

import org.openrdf.model.Statement;
import org.openrdf.rio.ParseErrorListener;
import org.openrdf.rio.RDFHandlerException;
import org.sindice.rdfcommons.adapter.LiteralFactoryException;
import org.sindice.rdfcommons.model.Triple;
import org.sindice.rdfcommons.model.TripleBuffer;
import org.sindice.rdfcommons.model.TripleSet;
import org.sindice.rdfcommons.parser.RDFHandler;
import org.sindice.rdfcommons.parser.RDFParser;
import org.sindice.rdfcommons.parser.RDFParserException;

import java.io.InputStream;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class RDFParserAdapter implements RDFParser {

    private final SesameConversionUtil sciInstance = SesameConversionUtil.getInstance();

    private final org.openrdf.rio.RDFParser internalParser;

    public RDFParserAdapter(org.openrdf.rio.RDFParser in) {
        if(in == null) {
            throw new NullPointerException("Invalid parser: cannot be null.");
        }
        internalParser = in;
    }

    public org.openrdf.rio.RDFParser getInternalParser() {
        return internalParser;
    }

    @Override
    public TripleSet parse(InputStream is, String graph)
    throws RDFParserException, LiteralFactoryException {
        final InternalBulkRDFHandler bulkRDFHandler = new InternalBulkRDFHandler();
        internalParser.setRDFHandler(bulkRDFHandler);
        try {
            internalParser.parse(is, graph);
        } catch (Exception e) {
            throw new RDFParserException("Error while parsing input stream.", e);
        }
        return bulkRDFHandler.triples;
    }

    @Override
    public void parse(InputStream is, String graph, RDFHandler handler)
    throws LiteralFactoryException, RDFParserException {
        final InternalParseErrorListener parseErrorListener = new InternalParseErrorListener(handler);
        final InternalRDFHandler rdfHandler = new InternalRDFHandler(handler);
        internalParser.setParseErrorListener(parseErrorListener);
        internalParser.setRDFHandler(rdfHandler);
        try {
            internalParser.parse(is, graph);
        } catch (Exception e) {
            if(e.getCause() instanceof LiteralFactoryException) {
                throw (LiteralFactoryException) e.getCause();
            }
            throw new RDFParserException("Error while parsing input stream.", e);
        }
    }

    private class InternalParseErrorListener implements ParseErrorListener {

        private final RDFHandler handler;

        public InternalParseErrorListener(RDFHandler handler) {
            this.handler = handler;
        }

        @Override
        public void warning(String msg, int row, int col) {
            handler.error(row, col, msg);
        }

        @Override
        public void error(String msg, int row, int col) {
            handler.error(row, col, msg);
        }

        @Override
        public void fatalError(String msg, int row, int col) {
             handler.fatalError(row, col, new RuntimeException(msg));
        }
    }

    private Triple toStriple(Statement statement) throws LiteralFactoryException {
        return sciInstance.convertSesameStatementToTriple(statement);
    }

    private class InternalBulkRDFHandler implements org.openrdf.rio.RDFHandler {

        private final TripleSet triples = new TripleBuffer();

        @Override
        public void startRDF() throws RDFHandlerException {
            triples.clear();
        }

        @Override
        public void endRDF() throws RDFHandlerException {
            // Empty.
        }

        @Override
        public void handleNamespace(String s, String s1) throws RDFHandlerException {
            // Empty.
        }

        @Override
        public void handleStatement(Statement statement) throws RDFHandlerException {
            final Triple triple;
            try {
                triple = toStriple(statement);
            } catch (LiteralFactoryException lfe) {
                throw new RDFHandlerException(lfe);
            }
            triples.addTriple(triple);
        }

        @Override
        public void handleComment(String s) throws RDFHandlerException {
            // Empty.
        }
    }

    private class InternalRDFHandler implements org.openrdf.rio.RDFHandler {

        private final RDFHandler handler;

        public InternalRDFHandler(RDFHandler handler) {
            this.handler = handler;
        }

        @Override
        public void startRDF() throws RDFHandlerException {
            handler.beginRDFStream();
        }

        @Override
        public void endRDF() throws RDFHandlerException {
            handler.endRDFStream();
        }

        @Override
        public void handleNamespace(String s, String s1) throws RDFHandlerException {
            // Empty.
        }

        @Override
        public void handleStatement(Statement statement) throws RDFHandlerException {
            final Triple triple;
            try {
                triple = toStriple(statement);
            } catch (LiteralFactoryException lfe) {
                throw new RDFHandlerException(lfe);
            }
            try {
                handler.handleStatement(triple);
            } catch (org.sindice.rdfcommons.parser.RDFHandlerException rdfhe) {
                throw new RDFHandlerException("Error while handing statement.", rdfhe);
            }
        }

        @Override
        public void handleComment(String s) throws RDFHandlerException {
            // Empty.
        }
    }
}
