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

import org.apache.commons.beanutils.BeanMap;
import org.sindice.rdfcommons.model.TripleSet;
import org.sindice.rdfcommons.beanmapper.annotations.Static;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import static org.sindice.rdfcommons.model.Triple.ObjectType;

/**
 * Defines the default static serializer for Java <i>beans</i>.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class StaticBeanSerializer extends BaseSerializer<Object> {

    public boolean acceptClass(Class clazz, Annotation[] annotations) {
        return ! isPrimitive(clazz) && definesAnnotation(annotations, Static.class);
    }

    public boolean isComplex() {
        return true;
    }

    public Identifier getIdentifier(
            SerializationContext context, Object bean, Annotation[] annotations, TripleSet buffer
    ) {
        return new Identifier(getClassURL(bean.getClass()), Identifier.Type.resource);
    }

    public Identifier serialize(SerializationContext context, Object bean, Annotation[] annotations,  TripleSet buffer)
    throws SerializationException {
        try {
            BeanMap beanMap = new BeanMap(bean);
            final String classURL = getClassURL( bean.getClass() );
            for(Map.Entry<String,Object> entry : (Set<Map.Entry<String,Object>>) beanMap.entrySet() ) {

                final String propertyName = entry.getKey();
                final Method propertyReadMethod = beanMap.getReadMethod(propertyName);

                // Skipping self description.
                if( "class".equals(propertyName) ) {
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
                        classURL,
                        getPropertyURL(classURL, propertyName, propertyReadMethod),
                        identifierEntry.getId(),
                        identifierEntry.isLiteral() ? ObjectType.literal : ObjectType.uri
                );

            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SerializationException(e);
        }
    }

}
