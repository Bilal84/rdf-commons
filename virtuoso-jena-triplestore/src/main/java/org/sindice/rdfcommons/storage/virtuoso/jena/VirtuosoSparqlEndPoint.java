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

package org.sindice.rdfcommons.storage.virtuoso.jena;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import org.sindice.rdfcommons.adapter.jena.JenaConversionUtil;
import org.sindice.rdfcommons.model.TripleSet;
import org.sindice.rdfcommons.storage.SparqlEndPoint;
import org.sindice.rdfcommons.storage.SparqlEndpointException;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

/**
 * <i>Virtuoso</i> implementation of {@link org.sindice.rdfcommons.storage.SparqlEndPoint}.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id$
 */
public class VirtuosoSparqlEndPoint implements SparqlEndPoint {

    private VirtGraph graph;

    protected VirtuosoSparqlEndPoint(VirtGraph vg) {
        graph = vg;
    }

    public org.sindice.rdfcommons.storage.ResultSet processSelectQuery(String qry)
    throws SparqlEndpointException {
        try {
            ResultSet resultSet = createQueryExecution(qry).execSelect();
            return new VirtuosoResultSet(resultSet);
        } catch (Exception e) {
            throw new SparqlEndpointException("Error while performing select operation.", e);
        }
    }

    public TripleSet processConstructQuery(String qry) throws SparqlEndpointException {
        try {
            Model model = createQueryExecution(qry).execConstruct();
            return JenaConversionUtil.getInstance().convertJenaModelToTripleSet(model, null);
        } catch (Exception e) {
            throw new SparqlEndpointException("Error while performing construct operation.", e);
        }
    }

    public TripleSet processDescribeQuery(String qry) throws SparqlEndpointException {
        try {
            Model model = createQueryExecution(qry).execDescribe();
            return JenaConversionUtil.getInstance().convertJenaModelToTripleSet(model, null);
        } catch (Exception e) {
            throw new SparqlEndpointException("Error while performing construct operation.", e);
        }
    }

    public boolean processAskQuery(String qry) throws SparqlEndpointException {
        try {
            return createQueryExecution(qry).execAsk();
        } catch (Exception e) {
            throw new SparqlEndpointException("Error while performing ask operation.", e);
        }
    }

    public EndpointResponse processQuery(String qry) throws SparqlEndpointException {
        Query query = parseQuery(qry);
        switch ( getQueryType(query) ) {
            case select:
                return new EndpointResponse( QueryType.select, processSelectQuery(qry));
            case construct:
                return new EndpointResponse( QueryType.construct, processConstructQuery(qry));
            case describe:
                return new EndpointResponse( QueryType.describe, processDescribeQuery(qry));
            case ask:
                return new EndpointResponse( QueryType.ask, processAskQuery(qry));
            default:
                throw new SparqlEndpointException( String.format("Illegal query type for query[%s]", qry) );
        }
    }

    public QueryType getQueryType(String qry) throws SparqlEndpointException {
        try {
            Query query = QueryFactory.create(qry);
            return getQueryType(query);
        } catch (Exception e) {
            throw new SparqlEndpointException("Error while parsing query.", e);
        }
    }

    private QueryType getQueryType(Query query) {
        if(query.isSelectType()) {
            return QueryType.select;
        }
        if(query.isConstructType()) {
            return QueryType.construct;
        }
        if(query.isDescribeType()) {
            return QueryType.describe;
        }
        if(query.isAskType()) {
            return QueryType.ask;
        }
        throw new IllegalArgumentException( String.format("Invalid query: [%s]", query.toString()) );
    }

    private VirtuosoQueryExecution createQueryExecution(String queryStr) throws SparqlEndpointException {
        try {
            return VirtuosoQueryExecutionFactory.create( parseQuery(queryStr), graph);
        } catch (Exception e) {
            throw new SparqlEndpointException("Error while creating query executor.", e);
        }
    }

    private Query parseQuery(String qry) throws SparqlEndpointException {
        try {
            return QueryFactory.create(qry);
        } catch (Exception e) {
            throw new SparqlEndpointException("Error while parsing query.", e);
        }
    }

}

