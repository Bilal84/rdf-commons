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

package org.sindice.rdfcommons.beanmapper;

import org.sindice.rdfcommons.model.Triple;
import org.sindice.rdfcommons.model.TripleBuffer;
import org.sindice.rdfcommons.model.TripleFilter;
import org.sindice.rdfcommons.model.TripleSet;
import org.sindice.rdfcommons.beanmapper.annotations.Static;
import org.sindice.rdfcommons.vocabulary.RDFSVocabulary;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.sindice.rdfcommons.model.Triple.*;

/**
 * Test case for {@link SerializationManager} class.
 *
 * @see SerializationManager
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class SerializerManagerTestCase {

    private static final Logger logger = Logger.getLogger(SerializerManagerTestCase.class);

    private SerializationManager serializationManager;

    @BeforeTest
    public void setUp() {
        serializationManager = new SerializationManager();
    }

    @AfterTest
    public void tearDown() {
        serializationManager = null;
        logger.debug("");
        logger.debug("-----------------------");
        logger.debug("");
    }

    @Test
    public void testSerializePrimitive() throws SerializationException {
        TripleBuffer tripleBuffer = serializationManager.serializeObject(12);
        assert tripleBuffer.getSize() == 0 : "Primitive objects are not converted in triples.";
    }

    @Test
    public void testStaticBeanSerializer() throws SerializationException {
        final StaticFakeBean staticFakeBean = new StaticFakeBean();
        TripleBuffer tripleBuffer = serializationManager.serializeObject(staticFakeBean);
        logger.debug(tripleBuffer);
        containsStaticFakeBean(staticFakeBean, tripleBuffer);
    }

    @Test
    public void testBeanSerializer() throws SerializationException, MalformedURLException {
        final FakeBean fakeBean = new FakeBean();
        TripleBuffer tripleBuffer = serializationManager.serializeObject(fakeBean);
        logger.debug(tripleBuffer);
        containsFakeBean(fakeBean, tripleBuffer);
    }

   @Test
    public void testRealBeanSerialization() throws SerializationException {
        TripleBuffer tripleBuffer = serializationManager.serializeObject( new Rectangle() );
        logger.debug(tripleBuffer);
        assert tripleBuffer.getSize() > 0 : "Unespected triples size: " + tripleBuffer.getSize();
    }

    @Test
    public void testStaticCollectionSerializer() throws SerializationException {
        final StaticFakeBean staticFakeBean = new StaticFakeBean();
        ArrayList<Object> list = new ArrayList<Object>();
        list.add("Appo");
        list.add(123);
        list.add(false);
        list.add(staticFakeBean);

        TripleBuffer tripleBuffer = serializationManager.serializeObject( new StaticContainer(list) );
        logger.debug(tripleBuffer);

        containsStaticFakeBean(staticFakeBean, tripleBuffer);
        assert tripleBuffer.containsTriple(
                "http://java.util/Collection",
                RDFSVocabulary.MEMBER,
                "Appo", ObjectType.literal
        );
        assert tripleBuffer.containsTriple(
                "http://java.util/Collection",
                RDFSVocabulary.MEMBER,
                123, ObjectType.literal
        );
        assert tripleBuffer.containsTriple(
                "http://java.util/Collection",
                RDFSVocabulary.MEMBER,
                false, ObjectType.literal
        );
    }

    @Test
    public void testCollectionSerializer() throws SerializationException {
        final FakeBean fakeBean = new FakeBean();
        ArrayList<Object> list = new ArrayList<Object>();
        list.add("Appo");
        list.add(123);
        list.add(false);
        list.add(fakeBean);

        TripleBuffer tripleBuffer = serializationManager.serializeObject( list );
        logger.debug(tripleBuffer);

        TripleSet blankSubjects = tripleBuffer.getTriples( new TripleFilter() {
            public boolean acceptTriple(Triple triple) {
                return triple.isSubjectBNode();
            }
        });
        logger.debug("-----");
        logger.debug(blankSubjects);
        assert blankSubjects.getSize() == 12 : "Unexpected number of elements: " + blankSubjects.getSize();
    }

    @Test
    public void testArraySerializer() throws SerializationException {
        final String[] array = new String[]{"string1", "string2", "string3"};
        TripleBuffer tripleBuffer = serializationManager.serializeObject(array);
        logger.debug(tripleBuffer);

        assert tripleBuffer.containsTriplePattern(
                null, "http://sindice.com/vocab/#itemindex", "0",
                SubjectType.bnode, ObjectType.literal
        );
        assert tripleBuffer.containsTriplePattern(
                null, "http://sindice.com/vocab/#itemvalue", "string1",
                SubjectType.bnode, ObjectType.literal
        );
        assert tripleBuffer.containsTriplePattern(
                null, "http://sindice.com/vocab/#itemindex", "1",
                SubjectType.bnode, ObjectType.literal
        );
        assert tripleBuffer.containsTriplePattern(
                null, "http://sindice.com/vocab/#itemvalue", "string2",
                SubjectType.bnode, ObjectType.literal
        );
        assert tripleBuffer.containsTriplePattern(
                null, "http://sindice.com/vocab/#itemindex", "2",
                SubjectType.bnode, ObjectType.literal
        );
        assert tripleBuffer.containsTriplePattern(
                null, "http://sindice.com/vocab/#itemvalue", "string3",
                SubjectType.bnode, ObjectType.literal
        );
    }

    @Test
    public void testStaticMapSerializer() throws SerializationException {
        final StaticFakeBean staticFakeBean = new StaticFakeBean();
        Map<Object,Object> map = new HashMap<Object,Object>();
        map.put("Appo", "value");
        map.put("key", staticFakeBean);
        map.put(false, "false");
        TripleBuffer tripleBuffer = serializationManager.serializeObject( new StaticContainer(map) );
        logger.debug(tripleBuffer);
        containsStaticFakeBean(staticFakeBean, tripleBuffer);
    }

    @Test
    public void testMapSerializer() throws SerializationException, MalformedURLException {
        final FakeBean fakeBean = new FakeBean();
        final Map<Object,Object> map = new HashMap<Object,Object>();
        map.put("Appo", "value");
        map.put("key", fakeBean);
        map.put(false, "false");
        TripleBuffer tripleBuffer = serializationManager.serializeObject( new Wrapper(map) );
        logger.debug(tripleBuffer);
        containsFakeBean(fakeBean, tripleBuffer);
    }

    /**
     * Tests the custom {@link org.sindice.rdfcommons.beanmapper.annotations.Type} annotation on beans.
     *
     * @throws SerializationException
     */
    @Test
    public void testCustomTypeBean() throws SerializationException {
        TripleBuffer tripleBuffer = serializationManager.serializeObject(new CustomTypeBean());

        logger.debug(tripleBuffer);

        assert tripleBuffer.containsTriple(
                "http://path/to#Class", "http://path/to#Class#field1", "value1", ObjectType.literal
        );
        assert tripleBuffer.containsTriple(
                "http://path/to#Class", "http://custom/predicate"    , "value2", ObjectType.literal
        );
    }

    /**
     * Tests the {@link org.sindice.rdfcommons.beanmapper.annotations.Namespace} annotation.
     *
     * @throws SerializationException
     */
    @Test
    public void testNamespaceAnnotation() throws SerializationException {
        TripleBuffer tripleBuffer = serializationManager.serializeObject( new DefaultNamespaceBean() );
        logger.debug(tripleBuffer);
        assert tripleBuffer.getSize() == 3;
    }

    /**
     * Tests that the {@link org.sindice.rdfcommons.beanmapper.annotations.Property} annotations works fine also for
     * collections.
     *
     * Expected result:
     * <pre>
     *    { <http://com.sindice/instance#c5af2e> <http://www.w3.org/1999/02/22-rdfcommons-syntax-ns#type> <http://fake/Collection> }
     *    { <http://com.sindice/instance#42fcc> <http://www.w3.org/1999/02/22-rdfcommons-syntax-ns#type> <http://fake/Item> }
     *    { <http://com.sindice/instance#42feb> <http://fake/hasItem> <http://com.sindice/instance#42fcc> }
     *    { <http://com.sindice/instance#c5af2e> <http://fake/hasItem> <http://com.sindice/instance#42feb> }
     * </pre>
     *
     * @throws SerializationException
     */
    @Test
    public void testCollectionWithCustomAnnotation() throws SerializationException {
        TripleBuffer tripleBuffer = serializationManager.serializeObject( new FakeCollectionBean() );
        logger.debug(tripleBuffer);
        assert tripleBuffer.getSize() == 4;
    }

    /**
     * Tests the support for the {@link org.sindice.rdfcommons.beanmapper.annotations.Adapt} annotation.
     *
     * Expected result:
     * <pre>
     *   { <http://com.sindice/instance#e80761> <http://www.w3.org/1999/02/22-rdfcommons-syntax-ns#type> <http://com.sindice.rdfcommons.beanmapper/Adapted> }
     *   { <http://com.sindice/instance#e80761> <http://com.sindice.rdfcommons.beanmapper/Adapted#field1> "adapted_v1"}
     *   { <http://com.sindice/instance#e80761> <http://com.sindice.rdfcommons.beanmapper/Adapted#field2> "adapted_v2"}
     *   { <http://com.sindice/instance#e80761> <http://com.sindice.rdfcommons.beanmapper/Adapted#field3> "adapted_v3"}
     * </pre>
     * @throws SerializationException
     */
    @Test
    public void testAdaptAnnotation() throws SerializationException {
        TripleBuffer tripleBuffer = serializationManager.serializeObject( new FakeAdapterBean() );
        logger.debug(tripleBuffer);
        assert tripleBuffer.getSize() == 4;
        for(Triple t : tripleBuffer) {
            if( t.getPredicate().contains("field") ) {
                assert t.getObjectAsString().contains("adapted");
            }
            assert ! t.getObjectAsString().contains("value");
        }
    }

    /*
        { <http://com.sindice.rdfcommons.beanmapper/StaticFakeBean>, <http://com.sindice.rdfcommons.beanmapper/StaticFakeBean#field1>, "field1 value"}
        { <http://com.sindice.rdfcommons.beanmapper/StaticFakeBean>, <http://com.sindice.rdfcommons.beanmapper/StaticFakeBean#field2>, "true"}
        { <http://com.sindice.rdfcommons.beanmapper/StaticFakeBean>, <http://com.sindice.rdfcommons.beanmapper/StaticFakeBean#field3>, "1"}
        { <http://com.sindice.rdfcommons.beanmapper/StaticFakeBean>, <http://com.sindice.rdfcommons.beanmapper/StaticFakeBean#field4>, "Wed Jul 15 18:24:31 CEST 2009"}
    */
    private void containsStaticFakeBean(StaticFakeBean original, TripleBuffer tripleBuffer) {
        assert tripleBuffer.containsTriple(
                "http://org.sindice.rdfcommons.beanmapper/StaticFakeBean",
                "http://org.sindice.rdfcommons.beanmapper/StaticFakeBean#field1",
                "field1 value", ObjectType.literal
        );
        assert tripleBuffer.containsTriple(
                "http://org.sindice.rdfcommons.beanmapper/StaticFakeBean",
                "http://org.sindice.rdfcommons.beanmapper/StaticFakeBean#field2",
                true, ObjectType.literal
        );
        assert tripleBuffer.containsTriple(
                "http://org.sindice.rdfcommons.beanmapper/StaticFakeBean",
                "http://specific/property#field3",
                1, ObjectType.literal
        );
        assert tripleBuffer.containsTriple(
                "http://org.sindice.rdfcommons.beanmapper/StaticFakeBean",
                "http://org.sindice.rdfcommons.beanmapper/StaticFakeBean#field4",
                original.getField4(), ObjectType.literal
        );
        assert ! tripleBuffer.containsTriple(
                "http://org.sindice.rdfcommons.beanmapper/StaticFakeBean",
                "http://org.sindice.rdfcommons.beanmapper/StaticFakeBean#field5",
                "this should be skipped.", ObjectType.literal
        ) : "Field5 is expected to be skipped.";

    }

    private void containsFakeBean(FakeBean original, TripleBuffer tripleBuffer) throws MalformedURLException {
        TripleSet ts = tripleBuffer.getTriples( new TripleFilter(){
            public boolean acceptTriple(Triple triple) {
                return triple.getObject().equals("http://org.sindice.rdfcommons.beanmapper/FakeBean");
            }
        });
        assert ts.getSize() == 1;
        final String instanceURL = ts.getTriples().get(0).getSubject();
        assert tripleBuffer.containsTriple(
            instanceURL,
            "http://org.sindice.rdfcommons.beanmapper/FakeBean#field1",
            "value1", ObjectType.literal
        );
        assert tripleBuffer.containsTriple(
            instanceURL,
            "http://org.sindice.rdfcommons.beanmapper/FakeBean#field2",
            true, ObjectType.literal
        );
        assert tripleBuffer.containsTriple(
            instanceURL,
            "http://org.sindice.rdfcommons.beanmapper/FakeBean#field3",
            11, ObjectType.literal
        );
        assert tripleBuffer.containsTriple(
            instanceURL,
            "http://org.sindice.rdfcommons.beanmapper/FakeBean#field4",
            original.getField4(), ObjectType.literal
        );

        assert tripleBuffer.containsTriple(
            instanceURL,
            "http://org.sindice.rdfcommons.beanmapper/FakeBean#field5",
            new URL("http://www.afakeulr.com/aqua"), ObjectType.literal
        );

        assert tripleBuffer.containsTriple(
            instanceURL,
            "http://org.sindice.rdfcommons.beanmapper/FakeBean#field6",
            3.1415f, ObjectType.literal
        );

    }

    @Static
    public class StaticContainer {

        private Object content;

        StaticContainer(Object content) {
            this.content = content;
        }

        public void setContent(Object content) {
            this.content = content;
        }

        @Static
        public Object getContent() {
            return content;
        }
    }

    public class Wrapper {

        private Object content;

        Wrapper(Object content) {
            this.content = content;
        }

        public Object getContent() {
            return content;
        }

        public void setContent(Object content) {
            this.content = content;
        }
    }


}
