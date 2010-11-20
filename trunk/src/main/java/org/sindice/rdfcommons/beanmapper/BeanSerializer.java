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
import org.sindice.rdfcommons.beanmapper.annotations.Adapt;
import org.sindice.rdfcommons.vocabulary.RDFVocabulary;
import org.apache.commons.beanutils.BeanMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 * Default serializer for Java <i>beans</i>.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class BeanSerializer extends BaseSerializer<Object> {

    public boolean acceptClass(Class clazz, Annotation[] annotations) {
        return ! isPrimitive(clazz);
    }

    public boolean isComplex() {
        return true;
    }

    public Identifier getIdentifier(
            SerializationContext context,
            Object bean,
            Annotation[] annotations,
            TripleSet buffer
    ) {
          return new Identifier(getObjectURL(bean), Identifier.Type.resource);
    }

    @SuppressWarnings("unchecked")    
    public Identifier serialize(SerializationContext context, Object bean, Annotation[] annotations, TripleSet buffer)
    throws SerializationException {

        // Handling adapted object.
        Object adapted = getAdapted(bean);
        if(adapted != null) {
            return context.serialize(context, adapted, annotations, buffer);
        }

        // Handling common bean.
        try {
            BeanMap beanMap = new BeanMap(bean);
            final String classUrl = getClassURL( bean.getClass() );
            final String instanceUrl = getObjectURL(bean);

            buffer.addTriple(instanceUrl, RDFVocabulary.TYPE, classUrl);

            for (Map.Entry<String, Object> entry : (Set<Map.Entry<String, Object>>) beanMap.entrySet()) {
                final String propertyName = entry.getKey();
                final Method propertyReadMethod = beanMap.getReadMethod(propertyName);

                // Skipping self description.
                if ("class".equals(entry.getKey())) {
                    continue;
                }
                // Skipping properties marked as ignored.
                if( isIgnored(propertyReadMethod) ) {
                    continue;
                }

                Object propertyValue = entry.getValue();

                Identifier identifierEntry = context.serialize(
                        context,
                        propertyValue,
                        getAnnotations(propertyReadMethod),
                        buffer
                );

                buffer.addTriple(
                        instanceUrl,
                        getPropertyURL(classUrl, propertyName, propertyReadMethod),
                        identifierEntry.getId(),
                        identifierEntry.isLiteral()
                );

            }
            return null;
        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }

    /**
     * Returns the adapted bean if any, <code>null</code> otherwise.
     *
     * @param in input bean.
     * @return the adapted bean.
     * @throws SerializationException if an error occurs during the retrieval of the adapter.
     */
    private Object getAdapted(Object in) throws SerializationException {
        Method annotated = null;
        for( Method m : in.getClass().getMethods() ) {
            if(m.getAnnotation(Adapt.class) != null) {
                if(annotated != null) {
                    throw new SerializationException(
                        String.format("Just one method an be annotated with the the annotation %s. Found more than one.", Adapt.class)
                    );
                }
                annotated = m;
            }
        }

        if(annotated == null) {
            return null;
        }

        if(annotated.getParameterTypes().length > 0) {
            throw new SerializationException(
                String.format("The method %s annotated as %s must take no parameters.", annotated, Adapt.class)
            );
        }

        try {
            return annotated.invoke(in);
        } catch (Exception e) {
            throw new SerializationException("Error while retrieving adapted object.", e);
        }
    }

}
