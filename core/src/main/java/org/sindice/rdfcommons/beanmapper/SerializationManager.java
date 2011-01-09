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

import org.sindice.rdfcommons.model.TripleBuffer;
import org.sindice.rdfcommons.model.TripleSet;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class performs the <i>bean<i> to <i>RDF</i> conversion.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class SerializationManager {

    class InternalSerializationContext implements SerializationContext {

        private Map<Object, Identifier> visitedObjects;

        InternalSerializationContext(Map<Object, Identifier> vo) {
            visitedObjects = vo;
        }

        public Map<Object, Identifier> getSerializedObjects() {
            return visitedObjects;
        }

        public Serializer getSerializerFor(Class clazz, Annotation[] annotations) throws SerializationException {
            return SerializationManager.this.getSerializerFor(clazz, annotations);
        }
     
        public Identifier serialize(SerializationContext context, Object object, Annotation[] annotations, TripleSet buffer)
        throws SerializationException {
            return SerializationManager.this.serialize(context, object, annotations, buffer);
        }
    }

    private static final Identifier NULL = new Identifier(BaseSerializer.NULL_VALUE, Identifier.Type.literal);

    private List<CriteriaSerializer> serializers;

    public SerializationManager() {
        serializers = new ArrayList<CriteriaSerializer>();
        configureDefaultSerializers();
    }

    public boolean addSerializer(CriteriaSerializer serializer) {
        if(serializer == null) {
            throw new NullPointerException("serializer cannot be null.");
        }
        return serializers.add(serializer);
    }

    public boolean removeSerializer(Serializer serializer) {
        return serializers.remove(serializer);
    }

    public Serializer getSerializerFor(Class clazz, Annotation[] annotations) throws SerializationException {
        for(CriteriaSerializer serializer : serializers) {
            if(serializer.acceptClass(clazz, annotations)) {
                return serializer;
            }
        }
        throw new SerializationException(String.format("Cannot find serialized for %s", clazz.getName()) );
    }

    public Identifier serialize(
            SerializationContext context,
            Object obj,
            Annotation[] annotations,
            TripleSet buffer
    ) throws SerializationException {

        // Serialization of null returns a NULL representation.
        if(obj == null) {
            return NULL;
        }

        final Map<Object, Identifier> visitedObjects = context.getSerializedObjects();

        // If the object to serialize has been already visited then the old result is returned.
        Identifier identifier;
        identifier = visitedObjects.get(obj);
        if(identifier != null) {
            return identifier;
        }

        // The object is serialized and recorded.
        Serializer serializer = getSerializerFor( obj.getClass(), annotations );

        if(serializer.isComplex()) {
            identifier = serializer.getIdentifier(context, obj, annotations, buffer);
            visitedObjects.put(obj, identifier);
            serializer.serialize(context, obj, annotations, buffer);
        } else {
            identifier = serializer.serialize(context, obj, annotations, buffer);
            visitedObjects.put(obj, identifier);
        }

        return identifier;
    }

    public void serializeObject(Object obj, Annotation[] annotations, TripleSet buffer) throws SerializationException {
        Map<Object, Identifier> visitedObjects = new HashMap<Object, Identifier>();
        serialize( new InternalSerializationContext(visitedObjects) , obj, annotations, buffer);
    }

    public TripleBuffer serializeObject(Object obj, Annotation[] annotations) throws SerializationException {
        TripleBuffer buffer = new TripleBuffer();
        serializeObject(obj, annotations, buffer);
        return buffer;
    }

    public TripleBuffer serializeObject(Object obj) throws SerializationException {
        TripleBuffer buffer = new TripleBuffer();
        serializeObject(obj, BaseSerializer.getAnnotations(obj), buffer);
        return buffer;
    }

    /**
     * Configures the default serializers in the right order.
     */
    private void configureDefaultSerializers() {
        // Primitive serializers.
        addSerializer( new PrimitiveSerializer() );

        // Static serializers.
        addSerializer( new StaticCollectionSerializer() );
        addSerializer( new StaticMapSerializer() );
        addSerializer( new StaticBeanSerializer() );

        // Instance serializers.
        addSerializer( new CollectionSerializer() );
        addSerializer( new ArraySerializer()      );
        addSerializer( new MapSerializer()        );
        addSerializer( new EnumSerializer()       );
        addSerializer( new BeanSerializer()       );
    }

}
