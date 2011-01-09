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
import org.sindice.rdfcommons.vocabulary.RDFVocabulary;
import org.sindice.rdfcommons.vocabulary.SerializerVocabulary;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Map;

import static org.sindice.rdfcommons.model.Triple.ObjectType;

/**
 * Default serializer for {@link java.util.Map} classes.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class MapSerializer extends BaseSerializer<Map> {

    public boolean acceptClass(Class clazz, Annotation[] annotations) {
       return definesInterface(clazz, Map.class);
    }

    public boolean isComplex() {
        return true;
    }

    public Identifier getIdentifier(SerializationContext context, Map map, Annotation[] annotations, TripleSet buffer) {
        return new Identifier(getObjectURL(map), Identifier.Type.resource);
    }

    public Identifier serialize(SerializationContext context, Map map, Annotation[] annotations, TripleSet buffer)
    throws SerializationException {
        Iterator<Map.Entry> iter = map.entrySet().iterator();
        Map.Entry current;
        final String mapClassURL = getClassURL(map.getClass());
        final String mapInstanceURL = getObjectURL(map);
        buffer.addTriple(mapInstanceURL, RDFVocabulary.TYPE, mapClassURL);
        while (iter.hasNext()) {
            current = iter.next();
            Object itemKey = current.getKey();
            Object itemValue = current.getValue();
            Identifier keyRoot   = context.serialize(context, itemKey  , getAnnotations(itemKey)  , buffer);
            Identifier valueRoot = context.serialize(context, itemValue, getAnnotations(itemValue), buffer);
            final String urifiedKey = urifyKey(keyRoot, buffer);
            buffer.addTriple(mapInstanceURL, SerializerVocabulary.ENTRY, urifiedKey);
            buffer.addTriple(
                    urifiedKey, SerializerVocabulary.VALUE, valueRoot.getId(),
                    valueRoot.isLiteral() ? ObjectType.literal : ObjectType.uri
            );
        }
        return null;
    }
}
