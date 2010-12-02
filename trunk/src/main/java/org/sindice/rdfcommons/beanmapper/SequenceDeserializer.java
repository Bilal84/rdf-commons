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
import org.sindice.rdfcommons.storage.ResultSet;
import org.sindice.rdfcommons.vocabulary.RDFSVocabulary;
import org.sindice.rdfcommons.vocabulary.RDFVocabulary;
import org.sindice.rdfcommons.vocabulary.SerializerVocabulary;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for <i>RDF</i> sequence deserialization.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public abstract class SequenceDeserializer extends BaseDeserializer {

    protected Integer getItemIndex(QueryEndpoint endpoint, String member) {
        endpoint.addQuery(member, SerializerVocabulary.ITEM_INDEX, "?itemindex");
        ResultSet rs = endpoint.execute();
        if(! rs.hasNext()) {
            throw new IllegalArgumentException("The member " + member + " doesn't declare an index.");
        }
        try {
            return Integer.parseInt( rs.getVariableString("?itemindex") );
        } catch (NumberFormatException nfe) {
            throw new IllegalStateException("itemindex is expected to be an integer for member " + member, nfe);
        }
    }

    protected String getItemValue(QueryEndpoint endpoint, String member) {
        endpoint.addQuery(member, SerializerVocabulary.ITEM_VALUE, "?itemvalue");
        ResultSet rs = endpoint.execute();
        if(! rs.hasNext()) {
            throw new IllegalArgumentException("The member " + member + " doesn't declare a value.");
        }
        return rs.getVariableValue("?itemvalue").toString();
    }

    protected String getClassName(QueryEndpoint endpoint, String member) {
        endpoint.addQuery(member, RDFVocabulary.TYPE, "?type");
        ResultSet rs = endpoint.execute();
        if(! rs.hasNext()) {
            throw new IllegalArgumentException("The identifier " + member + " doesn't declare a type.");
        }
        return rs.getVariableValue("?type").toString();
    }

    protected List<Object> internalDeserialize(
            DeserializationContext context,
            Annotation[] annotations,
            Identifier identifier, QueryEndpoint endPoint
    ) throws DeserializationException {
        Property propertyAnnotation = (Property) findAnnotation(annotations, Property.class);
        final String property;
        List<SortableObject> sortableList = new ArrayList<SortableObject>();
        if(propertyAnnotation != null) {
            property = propertyAnnotation.value();
        } else {
            property = RDFSVocabulary.MEMBER;
            endPoint.addQuery(identifier.getId().toString(), property, "?member");
            final ResultSet rs = endPoint.execute();
            String member;
            while(rs.hasNext()) {
                member = rs.getVariableValue("?member").toString();
                final int itemIndex = getItemIndex(endPoint, member);
                final String itemValue = getItemValue(endPoint, member);
                final String itemValueClassURL = getClassName(endPoint, itemValue);
                final String itemValueClass = urlToClass(itemValueClassURL);
                final Class elementClazz;
                try {
                    elementClazz = this.getClass().getClassLoader().loadClass(itemValueClass);
                } catch (ClassNotFoundException cnfe) {
                    throw new DeserializationException("Error while loading class '" + itemValueClass + "'", cnfe);
                }
                final Object deserialized = context.deserialize(
                        context,
                        elementClazz,
                        annotations,
                        new Identifier(itemValue, Identifier.Type.resource),
                        endPoint
                );
                sortableList.add( new SortableObject(deserialized, itemIndex) );
                rs.next();
            }
        }
        Collections.sort(sortableList);
        final List<Object> result = new ArrayList<Object>(sortableList.size());
        for(SortableObject sortableObject : sortableList) {
            result.add( sortableObject.object);
        }
        return result;
    }

    protected class SortableObject implements Comparable<SortableObject> {
        final Object object;
        final int index;

        public SortableObject(Object object, int index) {
            this.object = object;
            this.index = index;
        }

        public int compareTo(SortableObject other) {
            return index - other.index;
        }
    }

}
