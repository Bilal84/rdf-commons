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

import org.sindice.rdfcommons.storage.ResultSet;
import org.sindice.rdfcommons.vocabulary.RDFVocabulary;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class performs the <i>RDF<i> to <i>bean</i> conversion.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class DeserializationManager {

    private List<CriteriaDeserializer> deserializers;

    private Map<Identifier,Object> deserializedObjects;

    private List<Issue> issues;

    private final InternalDeserializationContext internalDeserializationContext;

    public DeserializationManager() {
        deserializers       = new ArrayList<CriteriaDeserializer>();
        deserializedObjects = new HashMap<Identifier, Object>();
        internalDeserializationContext = new InternalDeserializationContext();
        issues              = new ArrayList<Issue>();
        registerDefaultDeserializers();
    }

    /**
     * Returns the most appropriate {@link Deserializer} for the given class.
     *
     * @param clazz class to be deserialized.
     * @param annotations annotations associated to the given <code>clazz</code>.
     * @return the best deserializer chosen for the given class.
     * @throws DeserializationException
     */
    public CriteriaDeserializer getDeserializerFor(Class clazz, Annotation[] annotations)
    throws DeserializationException {
        for(CriteriaDeserializer deserializer : deserializers) {
            if(deserializer.acceptClass(clazz, annotations)) {
                return deserializer;
            }
        }
        throw new DeserializationException( String.format("Cannot find a deserializer for class %s", clazz) );
    }

    /**
     * Performs a static deserialization.
     * 
     * @param clazz class of object to be deserialized.
     * @param endpoint endpoint used to retrieve triples to be deserialized.
     * @return an instance of class <code>T</code>
     *         representing the static deserialization of class <code>T</code>.
     * @throws DeserializationException
     */
    public <T> T staticDeserialize(Class<T> clazz, QueryEndpoint endpoint)
    throws DeserializationException {
        clear();
        final Annotation[] annotations = clazz.getAnnotations();
        return deserialize(internalDeserializationContext, clazz, annotations, null, endpoint);
    }

    /**
     * Deserializes the given class returning a list of objects with type <code>clazz</code>
     * for every instance detected in the RDF representation exposed with the query <code>endPoint</code>. 
     *
     * @param clazz class to be deserialized.
     * @param endPoint endpoint exposing the RDF data to be mapped to beans.
     * @return list of deserialized beans.
     */
    public <T> Collection<T> deserialize(Class<T> clazz, QueryEndpoint endPoint)
    throws DeserializationException {
        clear();
        final Annotation[] annotations = clazz.getAnnotations();
        return deserializeInstancesOf(clazz, annotations, endPoint);
    }

    /**
     * Registers a deserializer within the manager.
     *
     * @param deserializer registers a deserializer to this manager.
     * @return <code>true</code> if the deserializer is effectively added,
     *         <code>false</code> otherwise.
     */
    public boolean addDeserializer(CriteriaDeserializer deserializer) {
        if(deserializer == null) {
            throw new NullPointerException("deserializer cannot be null.");
        }
        return deserializers.add(deserializer);
    }

    /**
     * Deregisters a deserializer from the manager.
     *
     * @param deserializer deserializer to be deregistered.
     * @return <code>true</code> if the deserializer is effectively removed, <code>false</code> otherwise.
     */
    public boolean removeDeserializer(CriteriaDeserializer deserializer) {
        return deserializers.remove(deserializer);
    }

    public List<Issue> getIssues() {
        return Collections.unmodifiableList(issues);
    }

    public boolean hasIssues() {
        return ! issues.isEmpty();
    }

    /**
     * Performs a type deserialization.
     * 
     * @param context context used for deserialization.
     * @param clazz class to be deserialized.
     * @param annotations annotations associated to <code>clazz</code>.
     * @param identifier identifier associated to to <code>clazz</code>.
     * @param endpoint endpoint exposing the RDF dataset mapped to <code>clazz</code>.
     * @return the Java object representing the deserialization of <code>clazz</code>.
     * @throws DeserializationException
     */
    protected <T> T deserialize(
            DeserializationContext context,
            Class<T> clazz,
            Annotation[] annotations,
            Identifier identifier,
            QueryEndpoint endpoint
    ) throws DeserializationException {

        final Deserializer deserializer = context.getDeserializerFor(clazz, annotations);

        Object deserialized = deserializedObjects.get(identifier);
        if(deserialized != null) {
            try {
                return (T) deserialized;
            } catch (ClassCastException cce) {
                throw new DeserializationException(
                    String.format(
                            "The class %s is associated to the identifier %s that has been " +
                            "already deserialized with object %s that is not of the expected type.",
                            clazz, identifier, deserialized
                    )
                );
            }
        } else {
            deserialized = deserializer.deserialize(
                    new InternalDeserializationContext(),
                    clazz,
                    annotations,
                    identifier,
                    endpoint
            );
            deserializedObjects.put(identifier, deserialized);
            return (T) deserialized;
        }
    }

    protected <T> Collection<T> deserializeInstancesOf(
            Class<T> clazz,
            Annotation[] annotations,
            QueryEndpoint endPoint
    ) throws DeserializationException {
        final String classUrl = BaseSerializer.getClassURL(clazz);
        endPoint.addQuery("?instance", RDFVocabulary.TYPE, classUrl);
        ResultSet rs = endPoint.execute();
        Identifier identifier;
        List<T> result = new ArrayList<T>();
        while (rs.hasNext()) {
            identifier = new Identifier( rs.getVariableString("?instance"), Identifier.Type.resource);
            result.add( deserialize(internalDeserializationContext, clazz, annotations, identifier, endPoint) );
            rs.next();
        }
        return result;
    }

    protected void clear() {
        deserializedObjects.clear();
        issues.clear();
    }

    private void registerDefaultDeserializers() {
        addDeserializer( new EnumDeserializer()       );
        addDeserializer( new CollectionDeserializer() );
        addDeserializer( new ArrayDeserializer()      );
        addDeserializer( new StaticBeanDeserializer() );
        addDeserializer( new PrimitiveDeserializer()  );
        addDeserializer( new BeanDeserializer()       );
    }

    private class InternalDeserializationContext implements DeserializationContext {

        private final Map<Identifier, Object> unmodifiableDeserilizedObjects =
                Collections.unmodifiableMap(deserializedObjects);

        public Map<Identifier, Object> getDeserializedObjects() {
            return unmodifiableDeserilizedObjects;
        }

        public void registerInstance(Identifier identifier, Object instance) {
            if(identifier == null) {
                throw new NullPointerException("identifier cannot be null.");
            }
            if(instance == null) {
                throw new NullPointerException("instance cannot be null.");
            }

            Object currInstance = deserializedObjects.get(identifier);
            if( currInstance != null && ! instance.equals(currInstance) ) {
                throw new IllegalArgumentException(
                    String.format(
                        "The identifier %s cannot be associated to the instance %s " +
                        "because already associated to instance %s.",
                        identifier,
                        instance,
                        currInstance
                    )
                );
            }
            deserializedObjects.put(identifier, instance);
        }

        public Deserializer getDeserializerFor(Class clazz, Annotation[] annotations)
        throws DeserializationException {
            return DeserializationManager.this.getDeserializerFor(clazz, annotations);
        }

        public void reportIssue(String msg) {
            issues.add( new Issue(msg) );
        }

        public void reportIssue(String msg, Throwable cause) {
            issues.add( new Issue(msg, cause) );
        }

        public <T> T deserialize(
                DeserializationContext context,
                Class<T> clazz,
                Annotation[] annotations,
                Identifier identifier,
                QueryEndpoint endPoint
        ) throws DeserializationException {
            try {
                return DeserializationManager.this.deserialize(context, clazz, annotations, identifier, endPoint);
            } catch (StackOverflowError soe) {
                throw new DeserializationException(
                        "A stack overflow occurred. " +
                        "May be you have a circular reference. " +
                        "Remember to register beans within deserialization context when defining a new deserializer.",
                        soe
                ); 
            }
        }
    }

}
