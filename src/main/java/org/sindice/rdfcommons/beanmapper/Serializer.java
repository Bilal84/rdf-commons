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

import java.lang.annotation.Annotation;

/**
 * This class serializes a specific bean. 
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public interface Serializer<T> extends SerializationSupport<T> {

    /**
     * @return <code>true</code> if the serialization must be done in two steps,
     *         <code>false</code> otherwise.
     */
    boolean isComplex();

    /**
     * Returns the identifier of the serialized object before performing the serialization itself.
     *
     * @param context context used to serialize the <i>T</i> object.
     * @param object target object.
     * @param annotations annotations associated to the object.
     * @param buffer triple set buffer used to store triples representing the serialization result.
     * @return the identifier of the serialization.
     */
    Identifier getIdentifier(SerializationContext context, T object, Annotation[] annotations, TripleSet buffer);

}
