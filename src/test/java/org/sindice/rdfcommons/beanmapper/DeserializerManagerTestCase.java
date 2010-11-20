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

import org.sindice.rdfcommons.TripleBuffer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;

/**
 * Test case for {@link DeserializationManager} class.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class DeserializerManagerTestCase {

    private DeserializationManager deserializationManager;

    @BeforeMethod
    protected void setUp() {
        deserializationManager = new DeserializationManager();
    }

    @AfterMethod
    protected void tearDown() {
        deserializationManager = null;
    }

    @Test
    public void testStaticBeanDeserializer()
    throws SerializationException, DeserializationException {
        // Prepare data.
        final StaticFakeBean staticFakeBean = new StaticFakeBean();
        final SerializationManager serializationManager = new SerializationManager();
        TripleBuffer tripleBuffer = serializationManager.serializeObject(staticFakeBean);

        // Deserialize data.
        QueryEndpoint queryEndpoint = new QueryEndpoint(tripleBuffer);
        StaticFakeBean deserialized = deserializationManager.staticDeserialize(StaticFakeBean.class, queryEndpoint);

        assert staticFakeBean.equals(deserialized);
    }

    @Test
    public void testBeanDeserializer()
    throws SerializationException, DeserializationException {
        // Prepare data.
        final FakeBean fakeBean = new FakeBean();
        final SerializationManager serializationManager = new SerializationManager();
        TripleBuffer tripleBuffer = serializationManager.serializeObject(fakeBean);

        QueryEndpoint queryEndpoint = new QueryEndpoint(tripleBuffer);
        Collection<FakeBean> deserialized = deserializationManager.deserialize(FakeBean.class, queryEndpoint);

        assert deserialized.size() == 1;
        final FakeBean deserializedBean = (FakeBean) deserialized.toArray()[0];
        assert fakeBean.equals(deserializedBean);
    }

}
