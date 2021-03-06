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

package org.sindice.rdfcommons.model;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.GregorianCalendar;

import static org.sindice.rdfcommons.model.Triple.ObjectType;
import static org.sindice.rdfcommons.model.Triple.SubjectType;

/**
 * Test case for {@link TripleImpl} class.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TripleTestCase {

    @Test
    public void testSPO() {
        final TripleImpl<String> triple = new TripleImpl<String>("sub-value", "pred-value", "obj-value");
        Assert.assertEquals(triple.getSubject()  , "sub-value" );
        Assert.assertEquals(triple.getPredicate(), "pred-value");
        Assert.assertEquals(triple.getObject()   , "obj-value" );
        Assert.assertEquals(triple.getObjectAsString(), "obj-value" );
        Assert.assertNull(triple.getObjectImplicitDatatype() );
        Assert.assertNull(triple.getLiteralDatatype() );
        Assert.assertNull(triple.getLiteralLanguage() );
        Assert.assertFalse(triple.isSubjectBNode());
        Assert.assertFalse(triple.isObjectBNode());
        Assert.assertFalse(triple.isObjectLiteral());
        Assert.assertEquals(triple.toNTriplesString(), "<sub-value> <pred-value> <obj-value>" );
    }

    @Test
    public void testSPOLiteral() {
        final TripleImpl<String> triple = new TripleImpl<String>(
                "sub-value", "pred-value", "obj-value", ObjectType.literal
        );
        Assert.assertEquals(triple.getSubject()  , "sub-value" );
        Assert.assertEquals(triple.getPredicate(), "pred-value");
        Assert.assertEquals(triple.getObject()   , "obj-value" );
        Assert.assertEquals(triple.getObjectAsString(), "obj-value" );
        Assert.assertNull(triple.getObjectImplicitDatatype() );
        Assert.assertNull(triple.getLiteralDatatype() );
        Assert.assertNull(triple.getLiteralLanguage() );
        Assert.assertFalse(triple.isSubjectBNode());
        Assert.assertFalse(triple.isObjectBNode());
        Assert.assertTrue(triple.isObjectLiteral());
        Assert.assertEquals(triple.toNTriplesString(), "<sub-value> <pred-value> \"obj-value\"" );
    }

    @Test
    public void testSPOImplicitTypedLiteral1() {
        final TripleImpl<Object> triple = new TripleImpl<Object>(
                "sub-value", "pred-value", 13.4F, ObjectType.literal
        );
        Assert.assertEquals(triple.getSubject()  , "sub-value" );
        Assert.assertEquals(triple.getPredicate(), "pred-value");
        Assert.assertEquals(triple.getObject()   , 13.4F );
        Assert.assertEquals(triple.getObjectAsString(), "13.4" );
        Assert.assertEquals(triple.getObjectImplicitDatatype(), "http://www.w3.org/2001/XMLSchema/float" );
        Assert.assertEquals(triple.getLiteralDatatype()       , "http://www.w3.org/2001/XMLSchema/float" );
        Assert.assertNull(triple.getLiteralLanguage());
        Assert.assertFalse(triple.isSubjectBNode());
        Assert.assertFalse(triple.isObjectBNode());
        Assert.assertTrue(triple.isObjectLiteral());
        Assert.assertEquals(
                triple.toNTriplesString(),
                "<sub-value> <pred-value> \"13.4\"^^<http://www.w3.org/2001/XMLSchema/float>"
        );
    }

    @Test
    public void testSPOImplicitTypedLiteral2() {
        final GregorianCalendar gc = new GregorianCalendar();
        gc.set(2010, GregorianCalendar.NOVEMBER, 23);
        final TripleImpl<Object> triple = new TripleImpl<Object>(
                "sub-value", "pred-value", gc.getTime(), ObjectType.literal
        );
        Assert.assertEquals(triple.getSubject()  , "sub-value" );
        Assert.assertEquals(triple.getPredicate(), "pred-value");
        Assert.assertEquals(triple.getObject()   , gc.getTime());
        Assert.assertEquals(triple.getObjectAsString(), "2010-11-23" );
        Assert.assertEquals(triple.getObjectImplicitDatatype(), "http://www.w3.org/2001/XMLSchema/date" );
        Assert.assertEquals(triple.getLiteralDatatype()       , "http://www.w3.org/2001/XMLSchema/date" );
        Assert.assertNull(triple.getLiteralLanguage());
        Assert.assertFalse(triple.isSubjectBNode());
        Assert.assertFalse(triple.isObjectBNode());
        Assert.assertTrue(triple.isObjectLiteral());
        Assert.assertEquals(
                triple.toNTriplesString(),
                "<sub-value> <pred-value> \"2010-11-23\"^^<http://www.w3.org/2001/XMLSchema/date>"
        );
    }

    @Test
    public void testSPOExplicitTypedLiteral() {
        final Point point = new Point(12.7F, 13.9F);
        final TripleImpl<Object> triple = new TripleImpl<Object>(
                "sub-value", "pred-value", point, ObjectType.literal
        );
        Assert.assertEquals(triple.getSubject()  , "sub-value" );
        Assert.assertEquals(triple.getPredicate(), "pred-value");
        Assert.assertEquals(triple.getObject()   , point );
        Assert.assertEquals(triple.getObjectAsString(), "POINT(12.7,13.9)" );
        Assert.assertNull( triple.getObjectImplicitDatatype() );
        Assert.assertEquals(triple.getLiteralDatatype(), "http://www.sindice.org/test/custom-geometry" );
        Assert.assertNull(triple.getLiteralLanguage() );
        Assert.assertFalse(triple.isSubjectBNode());
        Assert.assertFalse(triple.isObjectBNode());
        Assert.assertTrue(triple.isObjectLiteral());
        Assert.assertEquals(
                triple.toNTriplesString(),
                "<sub-value> <pred-value> \"POINT(12.7,13.9)\"^^<http://www.sindice.org/test/custom-geometry>"
        );
    }

    @Test
    public void testSPOLangLiteral() {
        final Message message = new Message("Hello World!!");
        final TripleImpl<Object> triple = new TripleImpl<Object>(
                "sub-value", "pred-value", message, ObjectType.literal
        );
        Assert.assertEquals(triple.getSubject()  , "sub-value" );
        Assert.assertEquals(triple.getPredicate(), "pred-value");
        Assert.assertEquals(triple.getObject()   , message );
        Assert.assertEquals(triple.getObjectAsString(), "Hello World!!" );
        Assert.assertNull( triple.getObjectImplicitDatatype() );
        Assert.assertNull(triple.getLiteralDatatype());
        Assert.assertEquals(triple.getLiteralLanguage(), "en");
        Assert.assertFalse(triple.isSubjectBNode());
        Assert.assertFalse(triple.isObjectBNode());
        Assert.assertTrue(triple.isObjectLiteral());
        Assert.assertEquals(
                triple.toNTriplesString(),
                "<sub-value> <pred-value> \"Hello World!!\"@en"
        );
    }

    @Test
    public void testSBlankPO() {
        final Point point = new Point(12.7F, 13.9F);
        final TripleImpl<Object> triple = new TripleImpl<Object>(
                "sub-bnode", "pred-value", point, SubjectType.bnode, ObjectType.literal);
        Assert.assertEquals(triple.getSubject()  , "sub-bnode" );
        Assert.assertEquals(triple.getPredicate(), "pred-value");
        Assert.assertEquals(triple.getObject()   , point );
        Assert.assertEquals(triple.getObjectAsString(), "POINT(12.7,13.9)" );
        Assert.assertNull( triple.getObjectImplicitDatatype() );
        Assert.assertEquals(triple.getLiteralDatatype(), "http://www.sindice.org/test/custom-geometry" );
        Assert.assertNull(triple.getLiteralLanguage() );
        Assert.assertTrue(triple.isSubjectBNode());
        Assert.assertFalse(triple.isObjectBNode());
        Assert.assertTrue(triple.isObjectLiteral());
        Assert.assertEquals(
                triple.toNTriplesString(),
                "_:sub-bnode <pred-value> \"POINT(12.7,13.9)\"^^<http://www.sindice.org/test/custom-geometry>"
        );
    }

    @Test
    public void testSBlankPOBlank() {
        final TripleImpl<Object> triple = new TripleImpl<Object>(
                "sub-bnode", "pred-value", "obj-bnode",  SubjectType.bnode, ObjectType.bnode
        );
        Assert.assertEquals(triple.getSubject()  , "sub-bnode" );
        Assert.assertEquals(triple.getPredicate(), "pred-value");
        Assert.assertEquals(triple.getObject()   , "obj-bnode" );
        Assert.assertEquals(triple.getObjectAsString(), "obj-bnode" );
        Assert.assertNull( triple.getObjectImplicitDatatype() );
        Assert.assertNull(triple.getLiteralDatatype() );
        Assert.assertNull(triple.getLiteralLanguage() );
        Assert.assertTrue(triple.isSubjectBNode());
        Assert.assertTrue(triple.isObjectBNode());
        Assert.assertFalse(triple.isObjectLiteral());
        Assert.assertEquals(
                triple.toNTriplesString(),
                "_:sub-bnode <pred-value> _:obj-bnode"
        );
    }

    class Point implements DatatypeLiteral {

        private final float x;
        private final float y;

        Point(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public String getDatatype() {
            return "http://www.sindice.org/test/custom-geometry";
        }

        public Object getTypedValue() {
            return this;
        }

        public String getValue() {
            return String.format("POINT(%s,%s)", x, y);
        }
    }

    class Message implements LanguageLiteral {

        private final String content;

        Message(String content) {
            this.content = content;
        }

        public String getLanguage() {
            return "en";
        }

        public String getValue() {
            return content;
        }
    }

}
