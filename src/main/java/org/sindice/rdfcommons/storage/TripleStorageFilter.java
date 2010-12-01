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

package org.sindice.rdfcommons.storage;

/**
 * Defines the filter for a triple storage.
 * This filter differs from the {@link org.sindice.rdfcommons.model.TripleFilter}
 * because is works in positive mode.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public interface TripleStorageFilter {

    /**
     * @return the accepted subject criteria.
     */
    String getSubjectMatching();

    /**
     * @return the accepted predicate criteria.
     */
    String getPredicateMatching();

    /**
     * @return the accepted object criteria.
     */
    Object getObjectMatching();

    /**
     * @return <code>true</code> if literal is required,
     *         <code>false</code> otherwise.
     */
    boolean requireLiteral();

    /**
     * @return <code>true</code> if the subject is required to be blank.
     */
    boolean requireSubjectBlank();

    /**
     * @return <code>true</code> if the object is required to be blank.
     */
    boolean requireObjectBlank();

}
