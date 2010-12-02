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

import org.sindice.rdfcommons.beanmapper.annotations.Property;
import org.sindice.rdfcommons.model.Triple;
import org.sindice.rdfcommons.model.TripleSet;
import org.sindice.rdfcommons.vocabulary.RDFSVocabulary;
import org.sindice.rdfcommons.vocabulary.RDFVocabulary;
import org.sindice.rdfcommons.vocabulary.SerializerVocabulary;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;

/**
 * Default serializer for <i>Java</i> native arrays.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ArraySerializer extends BaseSerializer<Object> {

    public boolean acceptClass(Class clazz, Annotation[] annotations) {
        return clazz.isArray();
    }

    public boolean isComplex() {
        return true;
    }

    public Identifier getIdentifier(
            SerializationContext context,
            Object array,
            Annotation[] annotations,
            TripleSet buffer
    ) {
        return new Identifier(getObjectURL(array), Identifier.Type.resource);
    }

    public Identifier serialize(
            SerializationContext context,
            Object array,
            Annotation[] annotations,
            TripleSet buffer
    ) throws SerializationException {
        int index = 0;
        final String instanceNode = getObjectURL(array);

        // If the collection is annotated then the collection is serialized with that annotation.
        Property propertyAnnotation = (Property) findAnnotation(annotations, Property.class);
        if( propertyAnnotation != null ) {
            Object arrayItem;
            for(int i = 0; i < Array.getLength(array); i++) {
                arrayItem = Array.get(array, i);
                Identifier root = context.serialize(context, arrayItem, getAnnotations(arrayItem), buffer);
                buffer.addTriple(
                        instanceNode,
                        propertyAnnotation.value(),
                        root.getId(),
                        Triple.SubjectType.uri,
                        getObjectType(root)
                );
            }
            return null;
        }

        // Otherwise a sequence pattern is used.
        buffer.addTriple(instanceNode, RDFVocabulary.TYPE, RDFVocabulary.SEQ);
        Object arrayItem;
        for(int i = 0; i < Array.getLength(array); i++) {
            arrayItem = Array.get(array, i);
            final String blankObject = buffer.addBNodeObjectTriple(
                    instanceNode, RDFSVocabulary.MEMBER, Triple.SubjectType.uri
            );
            buffer.addTriple(
                    blankObject, SerializerVocabulary.ITEM_INDEX, Integer.toString(index),
                    Triple.SubjectType.bnode, Triple.ObjectType.literal
            );
            Identifier root = context.serialize(context, arrayItem, getAnnotations(arrayItem), buffer);
            buffer.addTriple(
                    blankObject,
                    SerializerVocabulary.ITEM_VALUE,
                    root.getId(),
                    Triple.SubjectType.bnode,
                    getObjectType(root)
            );
            index++;
        }
        return null;
    }
    
}
