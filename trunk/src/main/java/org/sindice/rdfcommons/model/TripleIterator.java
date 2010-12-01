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

package org.sindice.rdfcommons.model;

/**
 * Defines the iterator over a {@link org.sindice.rdfcommons.model.TripleSet}.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public interface TripleIterator {

    /**
     * @return <code>true</code> if the iterator has more elements, <code>false</code> otherwise.
     */
    boolean hasNext();

    /**
     * Moves to the next triple of the iterator.
     */
    void next();

    /**
     * @return the current triple subject.
     */
    String subject();

    /**
     * @return the current triple predicate.
     */
    String predicate();

    /**
     * @return the current triple object.
     */
    Object object();

    /**
     * @return the current triple object as string.
     */
    String objectStr();

    /**
     * @return <code>true</code> if the subject is a blank node, <code>false</code> otherwise.
     */
    boolean isBlankSubject();

    /**
     * @return <code>true</code> if the object is a blank node, <code>false</code> otherwise.
     */
    boolean isBlankObject();

    /**
     * @return <code>true</code> if the object is a literal node, <code>false</code> otherwise.
     */    
    boolean isObjLiteral();

}
