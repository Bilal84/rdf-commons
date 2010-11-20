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

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Defines the context of serialization.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public interface SerializationContext extends SerializationSupport<Object> {

    /**
     * Returns the map of already visited objects.
     *
     * @return map of visited objects.
     */
    Map<Object, Identifier> getSerializedObjects();

    /**
     * Returns the serializer for the given class.
     *
     * @param clazz class for which is needed a serializer.
     * @param annotations annotations associated to the class.
     * @return serializer associated to the class.
     * @throws SerializationException
     */
    Serializer getSerializerFor(Class clazz, Annotation[] annotations) throws SerializationException;

}
