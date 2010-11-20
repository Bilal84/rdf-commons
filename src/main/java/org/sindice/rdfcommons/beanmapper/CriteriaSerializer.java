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

import java.lang.annotation.Annotation;

/**
 * Decides wether or not a seriazer can be applied on an object.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public interface CriteriaSerializer<T> extends Serializer<T> {

    /**
     * Checks if the serializer accepts the class.
     *
     * @param clazz class input.
     * @return <code>true</code> if accepted, <code>false</code> otherwise.
     */
    boolean acceptClass(Class clazz, Annotation[] annotations);

}
