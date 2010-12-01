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

import org.sindice.rdfcommons.model.TripleSet;
import org.sindice.rdfcommons.beanmapper.annotations.Id;
import org.sindice.rdfcommons.beanmapper.annotations.Property;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests the serialization and deserialization with
 * {@link org.sindice.rdfcommons.beanmapper.annotations.Id} annotation.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * // TODO: move inside an existing test.
 */
public class FakeBeanWithCustomIdTestCase {

    private final Logger logger = Logger.getLogger(FakeBeanWithCustomIdTestCase.class);

    private SerializationManager serializationManager;
    private DeserializationManager deserializationManager;

    @BeforeMethod
    public void setUp() {
        serializationManager   = new SerializationManager();
        deserializationManager = new DeserializationManager();
    }

    @AfterMethod
    public void tearDown() {
        serializationManager   = null;
        deserializationManager = null;
    }

    @Test
    public void testSerializationDeserializationWithCustomId()
    throws SerializationException, DeserializationException {
        // Serialization.
        final TripleSet tripleSet =  serializationManager.serializeObject( new FakeBeanWithFakeId() );
        logger.info("Extracted triples: " + tripleSet);
        String triplesStr = tripleSet.toString();
        Assert.assertTrue(triplesStr.contains("<fake-bean-id>"), "Cannot detect the fake identifier.");
        Assert.assertTrue(triplesStr.contains("<http://fake-bean-with-id/fake-prop>"), "Cannot detect property.");

        // TODO: fix the deserialization part.
        // Deserialization.
        /*
        List<FakeBeanWithFakeId> deserialized =
                (List<FakeBeanWithFakeId>) deserializationManager.deserialize(
                        FakeBeanWithFakeId.class,
                        new QueryEndpoint(tripleSet)
                );
        Assert.assertTrue(deserialized.size() == 1, "Invalid list size.");
        logger.info("Deserialized: " + deserialized.get(0));
        */
    }

    public static class FakeBeanWithFakeId {

        @Id
        public String getId() {
            return "fake-bean-id";
        }

        public void setId(String id) {
           Assert.assertEquals("fake-bean-id", id);
        }

        @Property("http://fake-bean-with-id/fake-prop")
        public String getProp() {
            return "prop value";
        }

        public void setProp(String prop) {
              Assert.assertEquals("prop value", prop);
        }
    }
}
