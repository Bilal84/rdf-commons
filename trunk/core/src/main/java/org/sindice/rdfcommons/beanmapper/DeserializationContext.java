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
 * Context used for deserialization.
 * 
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public interface DeserializationContext extends DeserializationSupport {

    /**
     * Returns the map of already visited objects.
     *
     * @return map of visited objects.
     */
    Map<Identifier, Object> getDeserializedObjects();

    /**
     * Allows to register a new instance inside the map of the deserialized objects.
     *
     * @param identifier identifier of the instance.
     * @param instance instance object.
     * @throws IllegalArgumentException if the <i>identifier</i> already has an instance.
     */
    void registerInstance(Identifier identifier, Object instance);

    /**
     * Returns the deserializer for the given class.
     *
     * @param clazz class for which is needed a deserializer.
     * @param annotations annotations associated to clazz.
     * @return deserializer associated to the class.
     * @throws DeserializationException
     */
    Deserializer getDeserializerFor(Class clazz, Annotation[] annotations) throws DeserializationException;

    /**
     * Reports an issue occurred during deserialization.
     *
     * @param msg error message.
     */
    void reportIssue(String msg);

    /**
     * Reports an issue occurred during deserialization.
     *
     * @param msg error message.
     * @param cause error cause.
     */
    void reportIssue(String msg, Throwable cause);

}
