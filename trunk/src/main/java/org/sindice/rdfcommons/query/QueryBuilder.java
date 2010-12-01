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

package org.sindice.rdfcommons.query;

import org.sindice.rdfcommons.storage.ResultSet;
import org.sindice.rdfcommons.storage.SparqlEndPoint;
import org.sindice.rdfcommons.storage.SparqlEndpointException;
import org.sindice.rdfcommons.model.TripleSet;
import org.sindice.rdfcommons.storage.TripleStorage;

/**
 * Defines a list of matches.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class QueryBuilder {

    /**
     * Internal chain of matches.
     */
    private MatchChain matchChain;

    /**
     * Query context.
     */
    private QueryContext context;

    /**
     * Constructor.
     */
    public QueryBuilder() {
        context = new QueryContext();
        matchChain = new MatchChain(context);
    }

    /**
     * @return the internal query context.
     */
    public QueryContext getContext() {
        return context;
    }

    /**
     * Adds a query criteria.
     *
     * @param subCriteria
     * @param predCriteria
     * @param objCriteria
     */
    public QueryBuilder addQuery(String subCriteria, String predCriteria, String objCriteria) {
        addQuery( new Match(context, subCriteria, predCriteria, objCriteria) );
        return this;
    }

    /**
     * Removes a query criteria.
     *
     * @param subCriteria
     * @param predCriteria
     * @param objCriteria
     */
    public QueryBuilder removeQuery(String subCriteria, String predCriteria, String objCriteria) {
        removeQuery( new Match(context, subCriteria, predCriteria, objCriteria) );
        return this;
    }

    /**
     * Clears the chain query content.
     */
    public QueryBuilder clear() {
        matchChain.clear();
        return this;
    }

    /**
     * @return the <i>SPARQL</i> representation of the built query.
     */
    public String toSparqlSelect() {
        return matchChain.toSparqlTriplePattern();
    }

    /**
     * Executes the built query on the given triple storage.
     *
     * @param ts the input storage.
     * @return the obtained result set.
     * @throws org.sindice.rdfcommons.storage.SparqlEndpointException if the triple storage doesn't support a <i>SPARQL</i> endpoint.
     */
    public ResultSet execOnStorage(TripleStorage ts) throws SparqlEndpointException {
        SparqlEndPoint se = ts.getSparqlEndPoint();
        return se.processSelectQuery( matchChain.toSparqlTriplePattern() );
    }

    /**
     * Executes the built query on the given triple set.
     *
     * @param ts input triple set.
     * @return result set.
     */
    public ResultSet execOnTriples(TripleSet ts) {
        TripleSetQueryExecutor queryExecutor = new TripleSetQueryExecutor();
        return queryExecutor.filter(matchChain, ts);
    }

    public String[] getVars() {
        return matchChain.getVars();
    }

    /**
     * Adds a query criteria triple.
     * 
     * @param query
     */
    protected void addQuery(Match query) {
        if(query == null) {
            throw new IllegalArgumentException();
        }
        matchChain.add(query);
    }

    /**
     * Removes a query criteria triple.
     *
     * @param query
     */
    protected void removeQuery(Match query) {
        if(query == null) {
            throw new IllegalArgumentException();
        }
        matchChain.remove(query);
    }

}
