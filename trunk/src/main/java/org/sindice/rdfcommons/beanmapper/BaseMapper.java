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

import org.sindice.rdfcommons.model.Triple;
import org.sindice.rdfcommons.model.TripleSet;
import org.sindice.rdfcommons.beanmapper.annotations.Id;
import org.sindice.rdfcommons.beanmapper.annotations.Ignore;
import org.sindice.rdfcommons.beanmapper.annotations.Namespace;
import org.sindice.rdfcommons.beanmapper.annotations.Property;
import org.sindice.rdfcommons.beanmapper.annotations.Subject;
import org.sindice.rdfcommons.vocabulary.SerializerVocabulary;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class BaseMapper {

    /**
     * Formatting utility used to serialize / deserialize dates.
     */
    protected static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

    /**
     * The null value.
     */
    protected static final String NULL_VALUE = "null";

    /**
     * List of primitive types.
     */
    protected static final Class[] PRIMITIVE_TIPES = new Class[]{
            String.class,  
            Boolean.class, boolean.class,
            Short.class,   short.class,
            Integer.class, int.class,
            Long.class,    long.class,
            Float.class,   float.class,
            Double.class,  double.class,
            Date.class,
            URL.class
    };

    /**
     * Returns the list of annotations associated to the given object.
     *
     * @param o
     * @return list of annotations.
     */
    protected static Annotation[] getAnnotations(Object o) {
        Class clazz = o.getClass();
        return clazz.getAnnotations();
    }

    /**
     * Returns the list of annotations associated to the given method.
     *
     * @param m
     * @return list of methods.
     */
    protected static Annotation[] getAnnotations(Method m) {
        return m.getAnnotations();
    }

    /**
     * Checks whether the method has to be ignored.
     *
     * @param method
     * @return <code>true</code> if ignored, <code>false</code> otherwise.
     */
    protected static boolean isIgnored(Method method) {
        for(Annotation a : method.getAnnotations()) {
            if(a.annotationType().equals(Ignore.class)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks wether a class is representing a primitive type or not.
     *
     * @param in the class of the type.
     * @return <code>true</code> if the type is primitive, <code>false</code> otherwise.
     */
    protected static boolean isPrimitive(Class in) {
        for(Class clazz : PRIMITIVE_TIPES) {
            if( clazz.equals( in) ) {
                return true;
            }
        }
        return false;
    }

     /**
     * Checks wether an object is representing a primitive type or not.
     *
     * @param o the type instance.
     * @return <code>true</code> if the object is primitive, <code>false</code> otherwise.
     */
    protected static boolean isPrimitive(Object o) {
        return isPrimitive( o.getClass() );
    }

    /**
     * Converts the primitive to a literal.
     *
     * @param obj
     * @return string representation of literal.
     */
    protected static Object primitiveAsLiteral(Object obj) {
        return obj == null ? NULL_VALUE : obj;
    }

    /**
     * Converts a literal string to the given type.
     *
     * @param literal the literal to be converted.
     * @param type the destination type.
     * @return the converted value.
     * @throws IllegalArgumentException if a conversion error occurs.
     * @throws IllegalArgumentException if the given <i>type</i> is not supported.
     */
    protected static Object literalAsPrimitive(String literal, Class type) {
        if(String.class.equals(type)) {
            return literal;
        }
        if(Boolean.class.equals(type) || boolean.class.equals(type)) {
            return Boolean.parseBoolean(literal);
        }
        if(Short.class.equals(type) || short.class.equals(type)) {
            return Short.parseShort(literal);
        }
        if(Integer.class.equals(type) || int.class.equals(type)) {
            return Integer.parseInt(literal);
        }
        if(Long.class.equals(type) || long.class.equals(type)) {
            return Long.parseLong(literal);
        }
        if(Float.class.equals(type) || float.class.equals(type)) {
            return Float.parseFloat(literal);
        }
        if(Double.class.equals(type) || double.class.equals(type)) {
            return Double.parseDouble(literal);
        }
        if(Date.class.equals(type)) {
            try {
                return simpleDateFormat.parse(literal);
            } catch (ParseException e) {
                throw new IllegalArgumentException( String.format("Expected a Date for literal '%s'.", literal) );
            }
        }
        if(URL.class.equals(type)) {
            try {
                return new URL(literal);
            } catch (MalformedURLException murle) {
                throw new IllegalArgumentException( String.format("Expected a URL for literal '%s'.", literal) );
            }
        }
        throw new UnsupportedOperationException(
                String.format("Cannot convert literal %s to type %s", literal, type)
        );
    }

    /**
     * Returns the URL representing the class.
     *
     * @param clazz
     * @return URL string representing the class and package.
     */
    protected static String getClassURL(Class clazz) {
        String subject = findSubjectAnnotation(clazz);
        if(subject != null) {
            return subject;
        }
        String packageName = clazz.getPackage() != null ? clazz.getPackage().getName() : "";
        return String.format("http://%s/%s", packageName, clazz.getSimpleName());
    }

    /**
     * Generates the URL representing an instance URL.
     *
     * @param o object to be URLified.
     * @return string containing URL.
     */
    public static String getObjectURL(Object o) {
        Method target = null;
        for(Method method : o.getClass().getMethods()) {
            if(method.getAnnotation(Id.class) != null) {
                target = method;
                break;
            }
        }

        if(target == null) {
            return String.format("%s#%s", SerializerVocabulary.INSTANCE, Integer.toHexString( o.hashCode()) );
        }

        if(target.getParameterTypes().length != 0) {
            throw new IllegalArgumentException(
                    "The method " + target + " to be annotated with " + Id.class + " must accept 0 parameters."
            );
        }
        try {
            return target.invoke(o).toString();
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving object identifier from method " + target, e);
        }
    }

    /**
     * Returns the URL representing a property.
     *
     * @param classUrl
     * @param property
     * @param method the method associated to the property.
     * @return URL string representing property.
     */
    protected static String getPropertyURL(String classUrl, String property, Method method) {
        String propertyAnnotation = findPropertyAnnotation(method);
        if(propertyAnnotation != null) {
            return propertyAnnotation;
        }
        String namespaceAnnotation = findNamespaceAnnotation( method.getDeclaringClass() );
        if(namespaceAnnotation != null) {
            return String.format("%s/%s", namespaceAnnotation, property);
        }
        return String.format("%s#%s", classUrl, property);
    }

    /**
     * Returns the appropriate URL for the given serialized key.
     *
     * @param key key object ot be serialized.
     * @param buffer buffer to serialize key if it is not an URL.
     * @return the URL representing the urified key.
     */
    protected static String urifyKey(Identifier key, TripleSet buffer) {
        if(key.isLiteral()) {
            return buffer.addBNodeSubjectTriple(SerializerVocabulary.KEY, key.getId(), Triple.ObjectType.literal);
        } else {
            return (String) key.getId();
        }
    }

    /**
     * Finds an interface in the class hierarchy.
     *
     * @param clazz
     * @param target
     * @return <code>true</code> if the interface is found, <code>false</code> otherwise.
     */
    protected static boolean definesInterface(Class clazz, Class target) {
        return target.isAssignableFrom(clazz);
    }

    /**
     * Finds an annotation in the class hierarchy.
     *
     * @param annotations
     * @param target
     * @return <code>true</code> if the annotation is found, <code>false</code> otherwise.
     */
    protected static boolean definesAnnotation(Annotation[] annotations, Class<? extends Annotation> target) {
        for(Annotation annotation : annotations) {
            if(annotation.annotationType().equals(target)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds an annotation in the given list.
     *
     * @param annotations
     * @param target
     * @return the annotation if found, <code>null</code> otherwise.
     */
    protected static Annotation findAnnotation(Annotation[] annotations, Class<? extends Annotation> target) {
        for(Annotation annotation : annotations) {
            if(annotation.annotationType().equals(target)) {
                return annotation;
            }
        }
        return null;
    }

    /**
     * Finds a specific annotation in a method.
     *
     * @param method
     * @param target
     * @return the annotation if found, <code>null</code> otherwise.
     */
    protected static Annotation findAnnotation(Method method, Class<? extends Annotation> target) {
        for(Annotation annotation : method.getAnnotations()) {
            if(annotation.annotationType().equals(target)) {
                return annotation;
            }
        }
        return null;
    }

    /**
     * Finds a specific annotation in a class.
     *
     * @param clazz
     * @param target
     * @return the annotation if found, <code>null</code> otherwise.
     */
    protected static Annotation findAnnotation(Class clazz, Class<? extends Annotation> target) {
        for(Annotation annotation : clazz.getAnnotations()) {
            if(annotation.annotationType().equals(target)) {
                return annotation;
            }
        }
        return null;
    }

    /**
     * Finds the property string possibly defined for a method.
     *
     * @param method
     * @return the property string.
     * @throws java.net.MalformedURLException if the property string is not an URL.
     */
    protected static String findPropertyAnnotation(Method method) {
        Property property = (Property) findAnnotation(method, Property.class);
        if(property == null) {
            return null;
        }
        try {
            return new URL( property.value() ).toString();
        } catch (MalformedURLException murle) {
            throw new RuntimeException(
                    String.format(
                        "Invalid annotation for method %s, the %s annotation value must be a valid URL.",
                        method,
                        Property.class
                    )
            );
        }
    }

    /**
     * Finds the subject string possibly defined for a class.
     *
     * @param clazz
     * @return the property string.
     * @throws MalformedURLException if the subject string is not an URL.
     */
    protected static String findSubjectAnnotation(Class clazz) {
        Subject subject = (Subject) findAnnotation(clazz, Subject.class);
        if(subject == null) {
            return null;
        }
        try {
            return new URL( subject.value() ).toString();
        } catch (MalformedURLException murle) {
            throw new RuntimeException(
                    String.format(
                        "Invalid annotation for class %s, the %s annotation value must be a valid URL.",
                        clazz,
                        Subject.class
                    )
            );
        }
    }

    /**
     * Finds the namespace string possibly defined for a class.
     *
     * @param clazz
     * @return the property string.
     * @throws MalformedURLException if the subject string is not an URL.
     */
    protected static String findNamespaceAnnotation(Class clazz) {
        Namespace namespace = (Namespace) findAnnotation(clazz, Namespace.class);
        if(namespace == null) {
            return null;
        }
        try {
            return new URL( namespace.value() ).toString();
        } catch (MalformedURLException murle) {
            throw new RuntimeException(
                    String.format(
                        "Invalid annotation for class %s, the %s annotation value must be a valid URL.",
                        clazz,
                        Namespace.class
                    )
            );
        }
    }

}
