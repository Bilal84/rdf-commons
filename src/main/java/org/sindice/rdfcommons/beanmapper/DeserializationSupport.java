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

/**
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public interface DeserializationSupport {

    /**
     * Deserializes a class object returning a new instance.
     *
     * @param context deserialization context.
     * @param clazz class to be deserialized.
     * @param annotations annotations associated to <i>clazz</i>.
     * @param identifier identifier representing the <i>RDF</i> instance.
     * @param endPoint <i>SPARQL</i> endopoint to be used.
     * @return list of deserialized objects.
     */
    <T> T deserialize(
            DeserializationContext context,
            Class<T> clazz,
            Annotation[] annotations,
            Identifier identifier,
            QueryEndpoint endPoint
    ) throws DeserializationException;

}
