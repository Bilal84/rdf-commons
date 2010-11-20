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

import org.sindice.rdfcommons.ResultSet;
import org.apache.commons.beanutils.BeanMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 * Bean deserializer implementation.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class BeanDeserializer extends BaseDeserializer {

    public boolean acceptClass(Class clazz, Annotation[] annotations) {
        return !isPrimitive(clazz);
    }

    public Identifier getIdentifier(Class clazz, Annotation[] annotations) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    public <T> T deserialize(
            DeserializationContext context,
            Class<T> clazz,
            Annotation[] annotations,
            Identifier identifier,
            QueryEndpoint endPoint
    ) throws DeserializationException {

        final T bean;
        try {
            bean = clazz.newInstance();
        } catch (Exception e) {
            throw new DeserializationException("Error while instantiating object.", e);
        }
        context.registerInstance(identifier, bean);

        final String  classUrl = getClassURL( bean.getClass() );
        final BeanMap beanMap  = new BeanMap(bean);
        String propertyURL;
        for (Map.Entry<String, Object> entry : (Set<Map.Entry<String, Object>>) beanMap.entrySet()) {

            // Skipping self description.
            if ("class".equals(entry.getKey())) {
                continue;
            }

            final String propertyName = entry.getKey();
            final Method propertyWriteMethod = beanMap.getWriteMethod(propertyName);
            if(propertyWriteMethod == null) {
                throw new DeserializationException(
                        String.format("Cannot find setter for property '%s' in bean %s", propertyName, bean)
                );
            }
            final Class propertyType = propertyWriteMethod.getParameterTypes()[0];

            // Skipping properties marked as ignored.
            if (isIgnored(propertyWriteMethod)) {
                continue;
            }
            
            final Object value = retrieveObject(
                    classUrl, propertyName, propertyWriteMethod, identifier, endPoint, context
            );

            // Skipping missing values.
            if(value == null) {
                continue;
            }

            Object deserializedValue = context.deserialize(
                    context,
                    propertyType,
                    propertyWriteMethod.getAnnotations(),
                    new Identifier(value, Identifier.Type.resource),
                    endPoint
            );

            try {
                propertyWriteMethod.invoke(bean, deserializedValue);
            } catch (Exception e) {
                throw new DeserializationException(
                    String.format(
                            "Error while setting value [%s] on bean [%s] using setter [%s].",
                            value,
                            bean,
                            propertyWriteMethod
                    )
                    ,
                    e
                );
            }
        }

        return bean;
    }

    private Object retrieveObject(
            String classUrl,
            String propertyName,
            Method propertyWriteMethod,
            Identifier identifier,
            QueryEndpoint endPoint,
            DeserializationContext context
    ) {
        final String propertyURL = getPropertyURL(classUrl, propertyName, propertyWriteMethod);
        endPoint.addQuery((String) identifier.getId(), propertyURL, "?value");
        ResultSet rs = endPoint.execute();
        if (!rs.hasNext()) {
            context.reportIssue(
                    String.format(
                            "Cannot find triple for identifier [%s] and property[%s].",
                            identifier,
                            propertyURL
                    )
            );
            return null;
        }
        return rs.getVariableValue("?value");
    }

}
