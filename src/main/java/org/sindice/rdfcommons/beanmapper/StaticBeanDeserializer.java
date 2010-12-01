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
import org.sindice.rdfcommons.beanmapper.annotations.Static;
import org.apache.commons.beanutils.BeanMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Static deserializer for a generic <i>Bean</i>.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class StaticBeanDeserializer extends BaseDeserializer {

    public boolean acceptClass(Class clazz, Annotation[] annotations) {
        return ! isPrimitive(clazz) && definesAnnotation(annotations, Static.class);
    }

    public Identifier getIdentifier(Class clazz, Annotation[] annotations) {
        return new Identifier( getClassURL(clazz), Identifier.Type.resource );
    }

    public <T> T deserialize(
            DeserializationContext context,
            Class<T> clazz,
            Annotation[] annotations,
            Identifier identifier,
            QueryEndpoint endPoint
    ) throws DeserializationException {

        if(identifier != null) {
            throw new IllegalArgumentException("for static deserialization the identifier is expected to be null.");
        }

        final Identifier staticIdentifier = getIdentifier(clazz, annotations);

        // Create the bean instance.
        final T instance;
        try {
            instance = clazz.newInstance();
        } catch (Exception e) {
            throw new DeserializationException(
                    String.format("Error while creating instance of class %s: defualt constructor required.", clazz)
            );
        }
        context.registerInstance(staticIdentifier, instance);

        // Define the bean map.
        final BeanMap beanMap = new BeanMap(instance);

        // Extracts the class property URLs.
        final Map<String,Method> propertyURLs = new HashMap<String,Method>();
        final String classURL = getClassURL(clazz);
        String propertyName;
        Method propertyWriteMethod;
        for (Map.Entry<String, Object> entry : (Set<Map.Entry<String, Object>>) beanMap.entrySet()) {
            // Skipping self description.
            if ("class".equals(entry.getKey())) {
                continue;
            }
            propertyName = entry.getKey();
            propertyWriteMethod = beanMap.getWriteMethod(propertyName);
            propertyURLs.put(getPropertyURL(classURL, propertyName, propertyWriteMethod), propertyWriteMethod);
        }

        // Retrieve the class triples.
        endPoint.addQuery( (String) staticIdentifier.getId(), "?prop", "?value" );
        ResultSet rs = endPoint.execute();

        // Coupling object properties with actual ones.
        Object property;
        Object value;
        Class propertyType;
        while (rs.hasNext()) {
            property = rs.getVariableValue("?prop");
            value    = rs.getVariableValue("?value");
            rs.next();

            propertyWriteMethod = propertyURLs.get(property);
            if (propertyWriteMethod == null) {
                context.reportIssue(String.format("Cannot find write method for property URL %s", property));
                continue;
            }
            propertyType = propertyWriteMethod.getParameterTypes()[0];

            // Deserializing recursively.
            Object deserialized = context.deserialize(
                    context,
                    propertyType,
                    propertyType.getAnnotations(),
                    new Identifier(value, Identifier.Type.resource),
                    endPoint
            );

            try {
                propertyWriteMethod.invoke(instance, deserialized);
            } catch (Exception e) {
                throw new DeserializationException(
                        String.format(
                                "Error while invoking write method %s of instance %s on value %s[%s]",
                                propertyWriteMethod,
                                instance,
                                deserialized,
                                deserialized.getClass()
                        )
                );
            }
        }
        return instance;
    }
    
}
