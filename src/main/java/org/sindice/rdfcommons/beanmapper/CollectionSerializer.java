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
import org.sindice.rdfcommons.beanmapper.annotations.Property;
import org.sindice.rdfcommons.vocabulary.RDFSVocabulary;
import org.sindice.rdfcommons.vocabulary.RDFVocabulary;
import org.sindice.rdfcommons.vocabulary.SerializerVocabulary;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * Dafault serializer for {@link java.util.Collection} classes.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class CollectionSerializer extends BaseSerializer<Collection> {

    public boolean acceptClass(Class clazz, Annotation[] annotations) {
        return definesInterface(clazz, Collection.class);
    }

    public boolean isComplex() {
        return true;
    }

    public Identifier getIdentifier(SerializationContext context, Collection collection, Annotation[] annotations, TripleSet buffer) {
        return new Identifier(getObjectURL(collection), Identifier.Type.resource);
    }

    public Identifier serialize(
            SerializationContext context,
            Collection collection,
            Annotation[] annotations,
            TripleSet buffer
    ) throws SerializationException {
        int index = 0;

        final String instanceNode = getObjectURL(collection);

        // If the collection is annotated then the collection is serialized with that annotation.
        Property propertyAnnotation = (Property) findAnnotation(annotations, Property.class);
        if( propertyAnnotation != null ) {
            for(Object collectionItem : collection) {
                Identifier root = context.serialize(context, collectionItem, getAnnotations(collectionItem), buffer);
                buffer.addTriple(
                        instanceNode, false,
                        propertyAnnotation.value(),
                        root.getId(), root.isLiteral(), root.isBlank()
                );
            }
            return null;
        }

        // Otherwise a sequence pattern is used.
        buffer.addTriple(instanceNode, RDFVocabulary.TYPE, RDFVocabulary.SEQ);
        for(Object collectionItem : collection) {
            final String blankObject = buffer.addBlankObjectTriple(instanceNode, false, RDFSVocabulary.MEMBER);
            buffer.addTriple(blankObject, true, SerializerVocabulary.ITEM_INDEX, Integer.toString(index), true, false );
            Identifier root = context.serialize(context, collectionItem, getAnnotations(collectionItem), buffer);
            buffer.addTriple(
                    blankObject, true,
                    SerializerVocabulary.ITEM_VALUE,
                    root.getId(), root.isLiteral(), root.isBlank()
            );
            index++;
        }
        return null;
    }

}