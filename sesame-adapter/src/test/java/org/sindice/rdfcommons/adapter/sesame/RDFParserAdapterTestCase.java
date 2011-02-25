package org.sindice.rdfcommons.adapter.sesame;

import org.sindice.rdfcommons.adapter.LiteralFactoryException;
import org.sindice.rdfcommons.adapter.sesame.nquads.NQuadsParser;
import org.sindice.rdfcommons.model.Triple;
import org.sindice.rdfcommons.parser.RDFHandler;
import org.sindice.rdfcommons.parser.RDFHandlerException;
import org.sindice.rdfcommons.parser.RDFParserException;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Test case for {@link org.sindice.rdfcommons.adapter.sesame.RDFParserAdapter} class.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class RDFParserAdapterTestCase {

    private RDFParserAdapter adapter;

    @BeforeMethod
    public void setUp() {
        adapter = new RDFParserAdapter( new NQuadsParser() );
    }

    @AfterMethod
    public void tearDown() {
        adapter = null;
    }

    @Test
    public void testBulkParse() throws LiteralFactoryException, RDFParserException {
        final TestRDFHandler rdfHandler = new TestRDFHandler();
        adapter.parse( this.getClass().getResourceAsStream("nquads/test2.nq"), "http://base", rdfHandler);
        rdfHandler.verify();
    }

    class TestRDFHandler implements RDFHandler {

        private boolean begin = false;
        private boolean end = false;

        private List<Triple> triples = new ArrayList<Triple>();

        @Override
        public void beginRDFStream() {
            begin = true;
        }

        @Override
        public void endRDFStream() {
            end = true;
        }

        @Override
        public void handleStatement(Triple triple) throws RDFHandlerException {
            triples.add(triple);
        }

        @Override
        public void error(int row, int col, String msg) {
            throw new IllegalStateException("Unexpected error.");
        }

        @Override
        public void fatalError(int row, int col, Throwable t) {
            throw new IllegalStateException("Unexpected error.");
        }

        void verify() {
            Assert.assertTrue(begin, "begin stream not received.");
            Assert.assertTrue(end, "end stream not received.");
            Assert.assertEquals(triples.size(), 400, "Unexpected number of triples.");
        }
    }

}
