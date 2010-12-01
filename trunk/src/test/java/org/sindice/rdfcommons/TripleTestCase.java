package org.sindice.rdfcommons;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.GregorianCalendar;

import static org.sindice.rdfcommons.Triple.*;

/**
 * Test case for {@link org.sindice.rdfcommons.Triple} class.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TripleTestCase {

    @Test
    public void testSPO() {
        final Triple<String> triple = new Triple<String>("sub-value", "pred-value", "obj-value");
        Assert.assertEquals(triple.getSubject()  , "sub-value" );
        Assert.assertEquals(triple.getPredicate(), "pred-value");
        Assert.assertEquals(triple.getObject()   , "obj-value" );
        Assert.assertEquals(triple.getObjectAsString(), "obj-value" );
        Assert.assertNull(triple.getObjectImplicitType() );
        Assert.assertNull(triple.getObjectType() );
        Assert.assertNull(triple.getObjectLanguage() );
        Assert.assertFalse(triple.isSubjectBNode());
        Assert.assertFalse(triple.isObjectBNode());
        Assert.assertFalse(triple.isObjLiteral());
        Assert.assertEquals(triple.toTripleString(), "<sub-value> <pred-value> <obj-value>" );
    }

    @Test
    public void testSPOLiteral() {
        final Triple<String> triple = new Triple<String>(
                "sub-value", "pred-value", "obj-value", ObjectType.literal
        );
        Assert.assertEquals(triple.getSubject()  , "sub-value" );
        Assert.assertEquals(triple.getPredicate(), "pred-value");
        Assert.assertEquals(triple.getObject()   , "obj-value" );
        Assert.assertEquals(triple.getObjectAsString(), "obj-value" );
        Assert.assertNull(triple.getObjectImplicitType() );
        Assert.assertNull(triple.getObjectType() );
        Assert.assertNull(triple.getObjectLanguage() );
        Assert.assertFalse(triple.isSubjectBNode());
        Assert.assertFalse(triple.isObjectBNode());
        Assert.assertTrue(triple.isObjLiteral());
        Assert.assertEquals(triple.toTripleString(), "<sub-value> <pred-value> \"obj-value\"" );
    }

    @Test
    public void testSPOImplicitTypedLiteral1() {
        final Triple<Object> triple = new Triple<Object>(
                "sub-value", "pred-value", 13.4F, ObjectType.literal
        );
        Assert.assertEquals(triple.getSubject()  , "sub-value" );
        Assert.assertEquals(triple.getPredicate(), "pred-value");
        Assert.assertEquals(triple.getObject()   , 13.4F );
        Assert.assertEquals(triple.getObjectAsString(), "13.4" );
        Assert.assertEquals(triple.getObjectImplicitType(), "http://www.w3.org/2001/XMLSchema/float" );
        Assert.assertEquals(triple.getObjectType()        , "http://www.w3.org/2001/XMLSchema/float" );
        Assert.assertNull(triple.getObjectLanguage());
        Assert.assertFalse(triple.isSubjectBNode());
        Assert.assertFalse(triple.isObjectBNode());
        Assert.assertTrue(triple.isObjLiteral());
        Assert.assertEquals(
                triple.toTripleString(),
                "<sub-value> <pred-value> \"13.4\"^^<http://www.w3.org/2001/XMLSchema/float>"
        );
    }

    @Test
    public void testSPOImplicitTypedLiteral2() {
        final GregorianCalendar gc = new GregorianCalendar();
        gc.set(2010, GregorianCalendar.NOVEMBER, 23);
        final Triple<Object> triple = new Triple<Object>(
                "sub-value", "pred-value", gc.getTime(), ObjectType.literal
        );
        Assert.assertEquals(triple.getSubject()  , "sub-value" );
        Assert.assertEquals(triple.getPredicate(), "pred-value");
        Assert.assertEquals(triple.getObject()   , gc.getTime());
        Assert.assertEquals(triple.getObjectAsString(), "2010-11-23" );
        Assert.assertEquals(triple.getObjectImplicitType(), "http://www.w3.org/2001/XMLSchema/date" );
        Assert.assertEquals(triple.getObjectType()        , "http://www.w3.org/2001/XMLSchema/date" );
        Assert.assertNull(triple.getObjectLanguage());
        Assert.assertFalse(triple.isSubjectBNode());
        Assert.assertFalse(triple.isObjectBNode());
        Assert.assertTrue(triple.isObjLiteral());
        Assert.assertEquals(
                triple.toTripleString(),
                "<sub-value> <pred-value> \"2010-11-23\"^^<http://www.w3.org/2001/XMLSchema/date>"
        );
    }

    @Test
    public void testSPOExplicitTypedLiteral() {
        final Point point = new Point(12.7F, 13.9F);
        final Triple<Object> triple = new Triple<Object>(
                "sub-value", "pred-value", point, ObjectType.literal
        );
        Assert.assertEquals(triple.getSubject()  , "sub-value" );
        Assert.assertEquals(triple.getPredicate(), "pred-value");
        Assert.assertEquals(triple.getObject()   , point );
        Assert.assertEquals(triple.getObjectAsString(), "POINT(12.7,13.9)" );
        Assert.assertNull( triple.getObjectImplicitType() );
        Assert.assertEquals(triple.getObjectType(), "http://www.sindice.org/test/custom-geometry" );
        Assert.assertNull(triple.getObjectLanguage() );
        Assert.assertFalse(triple.isSubjectBNode());
        Assert.assertFalse(triple.isObjectBNode());
        Assert.assertTrue(triple.isObjLiteral());
        Assert.assertEquals(
                triple.toTripleString(),
                "<sub-value> <pred-value> \"POINT(12.7,13.9)\"^^<http://www.sindice.org/test/custom-geometry>"
        );
    }

    @Test
    public void testSPOLangLiteral() {
        final Message message = new Message("Hello World!!");
        final Triple<Object> triple = new Triple<Object>(
                "sub-value", "pred-value", message, ObjectType.literal
        );
        Assert.assertEquals(triple.getSubject()  , "sub-value" );
        Assert.assertEquals(triple.getPredicate(), "pred-value");
        Assert.assertEquals(triple.getObject()   , message );
        Assert.assertEquals(triple.getObjectAsString(), "Hello World!!" );
        Assert.assertNull( triple.getObjectImplicitType() );
        Assert.assertNull(triple.getObjectType());
        Assert.assertEquals(triple.getObjectLanguage(), "en");
        Assert.assertFalse(triple.isSubjectBNode());
        Assert.assertFalse(triple.isObjectBNode());
        Assert.assertTrue(triple.isObjLiteral());
        Assert.assertEquals(
                triple.toTripleString(),
                "<sub-value> <pred-value> \"Hello World!!\"@en"
        );
    }

    @Test
    public void testSBlankPO() {
        final Point point = new Point(12.7F, 13.9F);
        final Triple<Object> triple = new Triple<Object>(
                "sub-bnode", "pred-value", point, SubjectType.bnode, ObjectType.literal);
        Assert.assertEquals(triple.getSubject()  , "sub-bnode" );
        Assert.assertEquals(triple.getPredicate(), "pred-value");
        Assert.assertEquals(triple.getObject()   , point );
        Assert.assertEquals(triple.getObjectAsString(), "POINT(12.7,13.9)" );
        Assert.assertNull( triple.getObjectImplicitType() );
        Assert.assertEquals(triple.getObjectType(), "http://www.sindice.org/test/custom-geometry" );
        Assert.assertNull(triple.getObjectLanguage() );
        Assert.assertTrue(triple.isSubjectBNode());
        Assert.assertFalse(triple.isObjectBNode());
        Assert.assertTrue(triple.isObjLiteral());
        Assert.assertEquals(
                triple.toTripleString(),
                "_:sub-bnode <pred-value> \"POINT(12.7,13.9)\"^^<http://www.sindice.org/test/custom-geometry>"
        );
    }

    @Test
    public void testSBlankPOBlank() {
        final Triple<Object> triple = new Triple<Object>(
                "sub-bnode", "pred-value", "obj-bnode",  SubjectType.bnode, ObjectType.bnode
        );
        Assert.assertEquals(triple.getSubject()  , "sub-bnode" );
        Assert.assertEquals(triple.getPredicate(), "pred-value");
        Assert.assertEquals(triple.getObject()   , "obj-bnode" );
        Assert.assertEquals(triple.getObjectAsString(), "obj-bnode" );
        Assert.assertNull( triple.getObjectImplicitType() );
        Assert.assertNull(triple.getObjectType() );
        Assert.assertNull(triple.getObjectLanguage() );
        Assert.assertTrue(triple.isSubjectBNode());
        Assert.assertTrue(triple.isObjectBNode());
        Assert.assertFalse(triple.isObjLiteral());
        Assert.assertEquals(
                triple.toTripleString(),
                "_:sub-bnode <pred-value> _:obj-bnode"
        );
    }

    class Point implements TypedLiteral {

        private final float x;
        private final float y;

        Point(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public String getType() {
            return "http://www.sindice.org/test/custom-geometry";
        }

        @Override
        public String toString() {
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

        @Override
        public String toString() {
            return content;
        }
    }

}
