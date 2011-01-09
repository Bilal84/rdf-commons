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
import java.util.Collection;

/**
 * Default deserializer for {@link java.util.Collection}s.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class CollectionDeserializer extends SequenceDeserializer {

    public boolean acceptClass(Class clazz, Annotation[] annotations) {
        for(Class iface : clazz.getInterfaces()) {
            if(iface.equals(Collection.class)) {
                return true;
            }
        }
        return false;
    }

    public Identifier getIdentifier(Class clazz, Annotation[] annotations) {
        throw new UnsupportedOperationException();
    }

    public <T> T deserialize(
            DeserializationContext context,
            Class<T> clazz,
            Annotation[] annotations,
            Identifier identifier,
            QueryEndpoint endPoint
    ) throws DeserializationException {
        return (T) internalDeserialize(context, annotations, identifier, endPoint);
    }
}
