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

import org.sindice.rdfcommons.model.TripleSet;

/**
 * Endpoint to access to the <i>SPARQL</i> interface.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public interface SparqlEndPoint {

    enum QueryType {
        select,
        construct,
        describe,
        ask
    }

    class EndpointResponse {

        private QueryType type;
        private Object result;

        public EndpointResponse(QueryType qt, Object o) {
            type = qt;
            result = o;
        }

        public QueryType getType() {
            return type;
        }

        public Object getResult() {
            return result;
        }

        public ResultSet getSelectResult() {
            return (ResultSet) result;
        }

        public TripleSet getConstructResult() {
            return (TripleSet) result;
        }

        public TripleSet getDescribeResult() {
            return (TripleSet) result;
        }

        public boolean getAskResult() {
            return (Boolean) result;
        }
    }

    /**
     * Processes a <i>SPARQL SELECT</i> query returning the relative result set.
     *
     * @param qry select query.
     * @return query result.
     */
    ResultSet processSelectQuery(String qry) throws SparqlEndpointException;

    /**
     * Processes a <i>SPARQL CONSTRUCT</i> query returning the model triple set.
     *
     * @param qry construct query.
     * @return triple model containing the constructed graph.
     */
    TripleSet processConstructQuery(String qry) throws SparqlEndpointException;

    /**
     * Processes a <i>SPARQL DESCRIBE</i> query returning the model triple set.
     *
     * @param qry the describe query.
     * @return the triple model containing the description.
     * @throws SparqlEndpointException
     */
    TripleSet processDescribeQuery(String qry) throws SparqlEndpointException;

    /**
     * Processes a <i>SPARQL ASK</i> query returning the boolean result.
     *
     * @param qry
     * @return a boolean result.
     */
    boolean processAskQuery(String qry) throws SparqlEndpointException;

    /**
     * Processes a generic <i>SPARQL</i> query returning the computed result.
     *
     * @param qry input query.
     * @return query result.
     * @throws SparqlEndpointException
     */
    EndpointResponse processQuery(String qry) throws SparqlEndpointException;;

    /**
     * Returns the kind of query type.
     *
     * @param qry the query to check.
     * @return kind of query type.
     * @throws SparqlEndpointException if the query cannot be processed.
     */
    QueryType getQueryType(String qry) throws SparqlEndpointException;

}
