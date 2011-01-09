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

import org.sindice.rdfcommons.model.Triple;

/**
 * Default implementation of {@link TripleStorageFilter}.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class TripleStorageFilterImpl implements TripleStorageFilter {

    private String subjectMatch;

    private String predicateMatch;

    private String objectMatch;

    private Triple.SubjectType subjectType;

    private Triple.ObjectType objectType;

    public TripleStorageFilterImpl(
            String sm, String pm, String om,
            Triple.SubjectType st, Triple.ObjectType ot
    ) {
        subjectMatch   = sm;
        predicateMatch = pm;
        objectMatch    = om;
        subjectType = st;
        objectType  = ot;
    }

    public String getSubjectMatching() {
        return subjectMatch;
    }

    public String getPredicateMatching() {
        return predicateMatch;
    }

    public String getObjectMatching() {
        return objectMatch;
    }

    public boolean requireLiteral() {
        return objectType == Triple.ObjectType.literal;
    }

    public boolean requireSubjectBlank() {
        return subjectType == Triple.SubjectType.bnode;
    }

    public boolean requireObjectBlank() {
        return objectType == Triple.ObjectType.bnode;
    }
}
