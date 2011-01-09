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
import org.sindice.rdfcommons.beanmapper.annotations.Static;
import org.sindice.rdfcommons.vocabulary.RDFSVocabulary;

import java.lang.annotation.Annotation;
import java.util.Collection;

import static org.sindice.rdfcommons.model.Triple.ObjectType;

/**
 * Serializer for bags.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class StaticCollectionSerializer extends BaseSerializer<Collection> {

    public boolean acceptClass(Class clazz, Annotation[] annotations) {
        return definesInterface(clazz, Collection.class) && definesAnnotation(annotations, Static.class);
    }

    public boolean isComplex() {
        return true;
    }

    public Identifier getIdentifier(
            SerializationContext context,
            Collection object,
            Annotation[] annotations,
            TripleSet buffer
    ) {
        return new Identifier(getClassURL(Collection.class), Identifier.Type.resource);
    }

    public Identifier serialize(
            SerializationContext context,
            Collection collection,
            Annotation[] annotations,
            TripleSet buffer
    ) throws SerializationException {
        final String classURL = getClassURL(Collection.class);
        for (Object collectionItem : collection) {
            Identifier identifierItem = context.serialize(context, collectionItem, annotations, buffer);
            buffer.addTriple(
                    classURL, RDFSVocabulary.MEMBER, identifierItem.getId(),
                    identifierItem.isLiteral() ? ObjectType.literal : ObjectType.uri
            );
        }
        return null;
    }

}