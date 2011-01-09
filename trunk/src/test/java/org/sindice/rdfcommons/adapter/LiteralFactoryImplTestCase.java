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

package org.sindice.rdfcommons.adapter;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test case for {@link LiteralFactory}.
 */
public class LiteralFactoryImplTestCase {

    private LiteralFactoryImpl literalFactory;

    @BeforeMethod
    public void setUp() {
        literalFactory = new LiteralFactoryImpl();
    }

    @AfterMethod
    public void tearDown() {
        literalFactory = null;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddAndRemoveFactory() {
        final LiteralFactory secondary = new LiteralFactoryImpl();
        Assert.assertTrue( literalFactory.addLiteralFactory(secondary) );
        Assert.assertTrue( literalFactory.contains(secondary) );
        Assert.assertTrue( literalFactory.removeLiteralFactory(secondary) );
        Assert.assertFalse(literalFactory.contains(secondary));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateLiteral() throws LiteralFactoryException {
        literalFactory.addLiteralFactory( new FakeIntLiteralFactory() );
        literalFactory.addLiteralFactory( new FakeFloatLiteralFactory() );

        final Object literal1 = literalFactory.createLiteral("http://float", "12.5");
        Assert.assertTrue(literal1 instanceof Float);
        Assert.assertEquals(literal1, new Float(12.5));

        final Object literal2 = literalFactory.createLiteral("http://int", "190");
        Assert.assertTrue(literal2 instanceof Integer);
        Assert.assertEquals(literal2, 190);

    }

    @Test(expectedExceptions = {LiteralFactoryException.class})
    @SuppressWarnings("unchecked")
    public void testCreateLiteralFailure() throws LiteralFactoryException {
        literalFactory.addLiteralFactory( new FakeIntLiteralFactory() );
        literalFactory.addLiteralFactory( new FakeFloatLiteralFactory() );

        literalFactory.createLiteral("http://float", "xyz");
    }

    class FakeIntLiteralFactory implements LiteralFactory<Integer> {

        private final String DATATYPE = "http://int";

        public String getName() {
            return this.getClass().getName();
        }

        public boolean supportDatatype(String datatype) {
            return DATATYPE.equals(datatype);
        }

        public Integer createLiteral(String datatype, String value) throws LiteralFactoryException {
            if( ! DATATYPE.equals(datatype)) {
                throw new LiteralFactoryException("Unsupported datatype.");
            }
            return Integer.parseInt(value);
        }
    }

    class FakeFloatLiteralFactory implements LiteralFactory<Float> {

        private final String DATATYPE = "http://float";

        public String getName() {
            return this.getClass().getName();
        }

        public boolean supportDatatype(String datatype) {
            return DATATYPE.equals(datatype);
        }

        public Float createLiteral(String datatype, String value) throws LiteralFactoryException {
            if( ! DATATYPE.equals(datatype)) {
                throw new LiteralFactoryException("Unsupported datatype.");
            }
            return Float.parseFloat(value);
        }
    }

}
