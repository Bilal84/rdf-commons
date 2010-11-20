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
 * Filters all the triples in a triple storage.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class AllTriplesStorageFilter implements TripleStorageFilter {

    private static AllTriplesStorageFilter instance;

    public static AllTriplesStorageFilter getInstance() {
        if(instance == null) {
            instance = new AllTriplesStorageFilter();
        }
        return instance;
    }

    /**
     * Singleton.
     */
    private AllTriplesStorageFilter() {}

    public String getSubjectMatching() {
        return null;
    }

    public String getPredicateMatching() {
        return null;
    }

    public String getObjectMatching() {
        return null;
    }

    public boolean requireLiteral() {
        return false;
    }

    public boolean requireSubjectBlank() {
        return false;
    }

    public boolean requireObjectBlank() {
        return false;
    }

}
