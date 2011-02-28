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

import org.apache.log4j.Logger;
import org.sindice.rdfcommons.model.Triple;
import org.sindice.rdfcommons.model.TripleBuffer;
import org.sindice.rdfcommons.model.TripleSet;
import org.sindice.rdfcommons.util.FileUtils;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Test case for {@link DefaultXMLToRDFConverter}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultXMLToRDFConverterTestCase {

    private static final Logger logger = Logger.getLogger(DefaultXMLToRDFConverterTestCase.class);

    private DefaultXMLToRDFConverter converter;

    @BeforeMethod
    public void setUp() {
        converter = new DefaultXMLToRDFConverter();
    }

    @AfterMethod
    public void tearDown() {
        converter = null;
    }

    @Test
    public void testConversion()
    throws RDFConverterHandlerException, ConversionException, URISyntaxException, IOException {
        final InputStream inputXML = this.getClass().getResourceAsStream("xml-conversion-test.xml");
        final TestRDFConverterHandler rdfConverterHandler = new TestRDFConverterHandler();
        converter.convertXMLStream(inputXML, new URI("http://test/xml/rdf"), rdfConverterHandler);
        validate(rdfConverterHandler);
    }

    private void validate(TestRDFConverterHandler testHandler) throws IOException {
        Assert.assertTrue(testHandler.begin, "Not begun.");
        Assert.assertTrue(testHandler.end, "Not ended");
        Assert.assertEquals(testHandler.triples.getSize(), 17, "Unexpected number of triples.");

        final ByteArrayOutputStream generatedNQuads = new ByteArrayOutputStream();
        final PrintStream ps = new PrintStream(generatedNQuads);
        testHandler.triples.toNQuads(ps);
        final String expectedNQuads = FileUtils.readStream(
                this.getClass().getResourceAsStream("xml-conversion-test-out.nq")
        );
        Assert.assertEquals(generatedNQuads.toString(), expectedNQuads, "Unexpected result.");
    }


    class TestRDFConverterHandler implements RDFConverterHandler {

        private final TripleSet triples = new TripleBuffer();
        private boolean begin;
        private boolean end;

        @Override
        public void beginStream() throws RDFConverterHandlerException {
            if(begin || end) {
                throw new IllegalStateException();
            }
            begin = true;
        }

        @Override
        public void endStream() throws RDFConverterHandlerException {
            if(!begin || end) {
                throw new IllegalStateException();
            }
            end = true;
        }

        @Override
        public void handleStatement(XMLLocation path, Triple triple) throws RDFConverterHandlerException {
            if(!begin || end) {
                throw new IllegalStateException();
            }
            logger.info("handleStatement: triple: " + triple.toNQuadsString() + " at location: " + path);
            triples.addTriple(triple);
        }

        @Override
        public void notifyError(XMLLocation path, String error) throws RDFConverterHandlerException {
            logger.error("Unexpected error " + error + " at location " + path);
            throw new IllegalStateException();
        }

        @Override
        public void notifyFatalError(XMLLocation path, Throwable t) throws RDFConverterHandlerException {
            logger.error("Unexpected fatal error " + t + " at location " + path);
            throw new IllegalStateException();
        }

    }

}
