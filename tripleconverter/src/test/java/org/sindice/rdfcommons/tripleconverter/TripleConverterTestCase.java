/**
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sindice.rdfcommons.tripleconverter;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Test case for {@link org.sindice.rdfcommons.tripleconverter.TripleConverter}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TripleConverterTestCase {

    private TripleConverter tripleConverter;

    @Before
    public void setUp() {
        tripleConverter = new TripleConverter();
    }

    @After
    public void tearDown() {
        tripleConverter = null;
    }

    /**
     * Tests the conversion from trig to NQuads.
     * 
     * @throws IOException
     */
    @Test
    public void testTrigToNQuadsConversion() throws IOException {
        final File OUT_FILE_NAME = File.createTempFile("tripleconverter.test.out", "nquads");
        OUT_FILE_NAME.delete();
        tripleConverter.processCommand(
                new String[]{
                        "-if", "trig",
                        "-of", "nquads",
                        "-O", OUT_FILE_NAME.getAbsolutePath(),
                        "src/test/resources/dataset.trig"
                }
        );

        // Check line count.
        FileInputStream fis = new FileInputStream(OUT_FILE_NAME);
        BufferedInputStream bis = new BufferedInputStream(fis);
        int intc;
        char c;
        int lineCounter = 0;
        while( (intc = bis.read()) != -1) {
            c = (char) intc;
            if(c == '\n') {
                lineCounter++;
            }
        }
        Assert.assertEquals(3773, lineCounter);
        bis.close();
    }

}
