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
import java.lang.reflect.Array;
import java.util.List;

/**
 * Default deserializer for <i>Java arrays</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ArrayDeserializer extends SequenceDeserializer {

    public boolean acceptClass(Class clazz, Annotation[] annotations) {
        return clazz.isArray();
    }

    public Identifier getIdentifier(Class clazz, Annotation[] annotations) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    public <T> T deserialize(
            DeserializationContext context,
            Class<T> clazz, Annotation[] annotations,
            Identifier identifier, QueryEndpoint endPoint
    ) throws DeserializationException {
        if(!clazz.isArray()) {
            throw new IllegalStateException("The deserialized class " + clazz + " must be an array." );
        }
        final List result = internalDeserialize(context, annotations, identifier, endPoint);
        return (T) result.toArray( (Object[]) Array.newInstance(clazz.getComponentType(), result.size() ) );
    }

}
