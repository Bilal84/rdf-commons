/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sindice.rdfcommons.converter;

import org.sindice.rdfcommons.model.Triple;
import org.sindice.rdfcommons.model.TripleImpl;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * Default implementation of {@link XMLToRDFConverter} based on <i>SAX</i> parser.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultXMLToRDFConverter implements XMLToRDFConverter {

    /**
     * Internal SAX parser factory.
     */
    private static final SAXParserFactory SPF = SAXParserFactory.newInstance();

    /**
     * URI for the generated RDF graph.
     */
    private String graphURI;

    /**
     * Handler listening for generated RDD data.
     */
    private RDFConverterHandler rdfHandler;

    /**
     * Handler listening for XML parsing massages and responsible for conversion.
     */
    private InternalXMLHandler xmlHandler;

    public DefaultXMLToRDFConverter() {}

    @Override
    public void convertXMLStream(InputStream is, URI graphURI, RDFConverterHandler rdfHandler)
    throws ConversionException, RDFConverterHandlerException {
        if(is == null) {
            throw new NullPointerException("input stream cannot be null.");
        }
        if(graphURI == null) {
            throw new NullPointerException("The RDF graph URI cannot be null.");
        }
        if(rdfHandler == null) {
            throw new NullPointerException("RDF handler cannot be null.");
        }
        this.graphURI   = graphURI.toString();
        this.rdfHandler = rdfHandler;

        final SAXParser parser = getParserInstance();
        xmlHandler = new InternalXMLHandler(rdfHandler);
        try {
            parser.parse(is, xmlHandler);
        } catch (IOException ioe) {
            throw new ConversionException("Error while parsing XML stream.", ioe);
        } catch (SAXException saxe) {
            if(saxe.getCause() instanceof RDFConverterHandlerException) {
                throw (RDFConverterHandlerException) saxe.getCause();
            }
        }

    }

    protected SAXParser getParserInstance() {
        try {
            final SAXParser saxParser = SPF.newSAXParser();
            saxParser.getXMLReader().setFeature("http://xml.org/sax/features/namespaces", true);
            return saxParser;
        } catch (Exception e) {
            throw new IllegalStateException("Error while initializing SAX parser.", e);
        }
    }

    /**
     * Generates the name representing an XML node.
     *
     * @param location
     * @param uri
     * @param localName
     * @param qName
     * @return a URI string.
     */
    protected String getNodeName(XMLLocation location, String uri, String localName, String qName) {
        return qName;
    }

    /**
     * Generates the URI representing an XML node.
     *
     * @param location
     * @param uri
     * @param localName
     * @param qName
     * @return a URI string.
     */
    protected String getNodeURI(XMLLocation location, String uri, String localName, String qName) {
        return location.path();
    }

    /**
     * Generates the triples representing the prefix mapping.
     *
     * @param prefix
     * @param uri
     * @param converterHandler
     */
    protected void notifyPrefixMapping(String prefix, String uri, RDFConverterHandler converterHandler)
    throws RDFConverterHandlerException {
        converterHandler.handleStatement(
                xmlHandler.getLocation(),
                new TripleImpl<String>(
                        prefix,
                        DefaultXMLToRDFConverterVocabulary.NAMESPACE,
                        uri,
                        graphURI
                )
        );
    }

    /**
     * This method is responsible for converting a set of XML attributes in triples.
     *
     * @param nodeURI
     * @param attributes
     * @param converterHandler
     * @throws RDFConverterHandlerException
     */
    protected void notifyAttributes(String nodeURI, Attributes attributes, RDFConverterHandler converterHandler)
    throws RDFConverterHandlerException {
        for(int i = 0; i < attributes.getLength(); i++) {
            final String attribute =
                    xmlHandler.getLocation().path() +
                    DefaultXMLToRDFConverterVocabulary.ATTRIBUTE_SEPARATOR +
                    attributes.getQName(i);

            final String attributeValue = attributes.getValue(i);
            converterHandler.handleStatement(
                    xmlHandler.getLocation(),
                    new TripleImpl<String>(
                            nodeURI,
                            DefaultXMLToRDFConverterVocabulary.ATTRIBUTE,
                            attribute,
                            graphURI
                    )
            );
            converterHandler.handleStatement(
                    xmlHandler.getLocation(),
                    new TripleImpl<String>(
                            attribute,
                            DefaultXMLToRDFConverterVocabulary.ATTRIBUTE_VALUE,
                            attributeValue, Triple.ObjectType.literal,
                            graphURI
                    )
            );
        }
    }

    /**
     * This method is responsible for converting an XML node content in triples.
     *
     * @param nodeURI
     * @param content
     * @param converterHandler
     * @throws RDFConverterHandlerException
     */
    protected void notifyNodeContent(String nodeURI, String content, RDFConverterHandler converterHandler)
    throws RDFConverterHandlerException {
        converterHandler.handleStatement(
                xmlHandler.getLocation(),
                new TripleImpl<String>(
                        nodeURI,
                        DefaultXMLToRDFConverterVocabulary.CONTENT,
                        content, Triple.ObjectType.literal,
                        graphURI
                )
        );
    }

    /**
     * This method is responsible for converting an XML parent child relationship in triples.
     *
     * @param nodeURI
     * @param lastNodeURI
     * @param converterHandler
     * @throws RDFConverterHandlerException
     */
    protected void notifyKinship(String nodeURI, String lastNodeURI, RDFConverterHandler converterHandler)
    throws RDFConverterHandlerException {
        if(lastNodeURI == null) return;
        converterHandler.handleStatement(
                xmlHandler.getLocation(),
                new TripleImpl<String>(
                        lastNodeURI,
                        DefaultXMLToRDFConverterVocabulary.CHILD,
                        nodeURI,
                        graphURI
                )
        );
    }

    /**
     * Internal handler responsible for driving the conversion process.
     */
    private class InternalXMLHandler extends DefaultHandler {

        private final RDFConverterHandler converterHandler;

        private final StringBuilder textBuffer = new StringBuilder();

        private DefaultXMLLocation xmlLocation;

        private String lastNodeURI;

        public InternalXMLHandler(RDFConverterHandler converterHandler) {
            this.converterHandler = converterHandler;
        }

        @Override
        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            super.startPrefixMapping(prefix, uri);
            try {
                notifyPrefixMapping(prefix, uri, converterHandler);
            } catch (RDFConverterHandlerException rdfce) {
                throw new SAXException(rdfce);
            }
        }

        @Override
        public void setDocumentLocator(Locator locator) {
            super.setDocumentLocator(locator);
            xmlLocation = new DefaultXMLLocation(locator);
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            try {
                converterHandler.beginStream();
            } catch (RDFConverterHandlerException rdfhe) {
                throw new SAXException("Error while processing beginStream message.", rdfhe);
            }
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
            try {
                converterHandler.endStream();
            } catch (RDFConverterHandlerException rdfhe) {
                throw new SAXException("Error while processing endStream message.", rdfhe);
            }
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
        throws SAXException {
            super.startElement(uri, localName, qName, attributes);

            textBuffer.delete(0, textBuffer.length());
            final String nodeName = getNodeName(xmlLocation, uri, localName, qName);
            xmlLocation.push(nodeName);
            final String nodeURI  = getNodeURI(xmlLocation, uri, localName, qName);
            try {
                notifyAttributes(nodeURI, attributes, converterHandler);
                notifyKinship(nodeURI, lastNodeURI, converterHandler);
            } catch (RDFConverterHandlerException rdfche) {
                throw new SAXException(rdfche);
            }
            lastNodeURI = nodeURI;
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);

            final String nodeContent = textBuffer.toString().trim();
            textBuffer.delete(0, textBuffer.length());
            xmlLocation.pop();

            if (nodeContent.length() > 0) {
                try {
                    notifyNodeContent(lastNodeURI, nodeContent, converterHandler);
                } catch (RDFConverterHandlerException rdfche) {
                    throw new SAXException(rdfche);
                }
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            textBuffer.append(ch, start, length);
        }

        @Override
        public void warning(SAXParseException e) throws SAXException {
            super.warning(e);
            notifyError(e);
        }

        @Override
        public void error(SAXParseException e) throws SAXException {
            super.error(e);
            notifyError(e);
        }

        @Override
        public void fatalError(SAXParseException e) throws SAXException {
            super.fatalError(e);
             try {
                converterHandler.notifyFatalError( xmlLocation, e );
            } catch (RDFConverterHandlerException rdfce) {
                throw new SAXException("Error while processing notifyFatalError message.", rdfce);
            }
        }

        protected XMLLocation getLocation() {
            return xmlLocation;
        }

        private void notifyError(SAXParseException e) throws SAXException {
            try {
                converterHandler.notifyError( xmlLocation, e.getMessage() );
            } catch (RDFConverterHandlerException rdfce) {
                throw new SAXException("Error while processing notifyError message.", rdfce);
            }
        }
    }

}
