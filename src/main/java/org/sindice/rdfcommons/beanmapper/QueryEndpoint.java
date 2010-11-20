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
import org.sindice.rdfcommons.SparqlEndpointException;
import org.sindice.rdfcommons.TripleSet;
import org.sindice.rdfcommons.query.QueryBuilder;
import org.sindice.rdfcommons.storage.TripleStorage;

/**
 * Endpoint to perform query on the triple data set.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class QueryEndpoint {

    private TripleSet tripleSet;

    private TripleStorage tripleStorage;

    private QueryBuilder queryBuilder = new QueryBuilder();

    public QueryEndpoint(TripleSet ts) {
        if(ts == null) {
            throw new NullPointerException("triple set cannot be null.");
        }
        tripleSet = ts;
    }

    public QueryEndpoint(TripleStorage ts) {
        if(ts == null) {
            throw new NullPointerException("triple storage cannot be null.");
        }
        tripleStorage = ts;
    }

    public void addQuery(String subCriteria, String predCriteria, String objCriteria) {
        queryBuilder.addQuery(subCriteria, predCriteria, objCriteria);
    }

    public void removeQuery(String subCriteria, String predCriteria, String objCriteria) {
        queryBuilder.removeQuery(subCriteria, predCriteria, objCriteria);
    }

    public ResultSet execute() {
        assert logicalXOR(tripleSet != null, tripleStorage != null);
        try {
            if(tripleSet != null) {
                return queryBuilder.execOnTriples(tripleSet);
            } else {
                try {
                    return queryBuilder.execOnStorage(tripleStorage);
                } catch (SparqlEndpointException see) {
                    throw new RuntimeException("Error while performing query.", see);
                }
            }
        } finally {
            queryBuilder.clear();
        }
    }

    private static boolean logicalXOR(boolean x, boolean y) {
        return ((x || y) && !(x && y));
    }

}
