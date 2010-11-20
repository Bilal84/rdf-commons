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

import org.sindice.rdfcommons.TripleSet;
import org.sindice.rdfcommons.beanmapper.annotations.Static;
import org.sindice.rdfcommons.vocabulary.SerializerVocabulary;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Map;

/**
 * Default static serializer for {@link java.util.Map} classes.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class StaticMapSerializer extends BaseSerializer<Map> {

    public boolean acceptClass(Class clazz, Annotation[] annotations) {
        return definesInterface(clazz, Map.class) && definesAnnotation(annotations, Static.class);
    }

    public boolean isComplex() {
        return true;
    }

    public Identifier getIdentifier(SerializationContext context, Map map, Annotation[] annotations, TripleSet buffer) {
        return new Identifier(getClassURL(map.getClass()), Identifier.Type.resource);
    }

    public Identifier serialize(SerializationContext context, Map map, Annotation[] annotations, TripleSet buffer)
    throws SerializationException {
        Iterator<Map.Entry> iter = map.entrySet().iterator();
        Map.Entry current;
        final String mapClass = getClassURL(map.getClass());
        while (iter.hasNext()) {
            current = iter.next();
            Object key = current.getKey();
            Object value = current.getValue();
            Identifier keyRoot   = context.serialize(context, key  , getAnnotations(key)  , buffer);
            Identifier valueRoot = context.serialize(context, value, getAnnotations(value), buffer);
            final String urified =  urifyKey(keyRoot, buffer);
            buffer.addTriple(mapClass, SerializerVocabulary.ENTRY, urified, false);
            buffer.addTriple(urified , SerializerVocabulary.VALUE, valueRoot.getId(), valueRoot.isLiteral());
        }
        return null;
    }

}
