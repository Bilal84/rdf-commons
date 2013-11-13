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

package org.sindice.rdfcommons.storage.virtuoso.sesame;

import org.openrdf.query.BooleanQuery;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.QueryParser;
import org.openrdf.query.parser.QueryParserRegistry;
import org.openrdf.repository.RepositoryException;
import org.sindice.rdfcommons.adapter.sesame.SesameConversionUtil;
import org.sindice.rdfcommons.model.TripleBuffer;
import org.sindice.rdfcommons.model.TripleSet;
import org.sindice.rdfcommons.storage.ResultSet;
import org.sindice.rdfcommons.storage.SparqlEndPoint;
import org.sindice.rdfcommons.storage.SparqlEndpointException;
import virtuoso.sesame2.driver.VirtuosoRepositoryConnection;

/**
 * <i>Virtuoso</i> implementation of {@link org.sindice.rdfcommons.storage.SparqlEndPoint}.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id$
 */
public class VirtuosoSparqlEndPoint implements SparqlEndPoint {

    protected static final SesameConversionUtil SESAME_CONVERSION_UTIL = SesameConversionUtil.getInstance();

    private final VirtuosoRepositoryConnection repositoryConnection;

    protected VirtuosoSparqlEndPoint(VirtuosoRepositoryConnection repositoryConnection) {
        this.repositoryConnection = repositoryConnection;
    }

    public ResultSet processSelectQuery(String qry)
    throws SparqlEndpointException {
        final TupleQuery tupleQuery;
        try {
            tupleQuery = repositoryConnection.prepareTupleQuery(QueryLanguage.SPARQL, qry);
        } catch (MalformedQueryException mqe) {
            throw new SparqlEndpointException("Error while parsing query", mqe);
        } catch (RepositoryException re) {
            throw new SparqlEndpointException("Error while connecting with repository.", re);
        }

        final TupleQueryResult tupleQueryResult;
        try {
            tupleQueryResult = tupleQuery.evaluate();
        } catch (QueryEvaluationException qee) {
            throw new SparqlEndpointException("Error while evaluating query.", qee);
        }
        return new VirtuosoResultSet(tupleQueryResult);
    }

    public TripleSet processConstructQuery(String qry) throws SparqlEndpointException {
        return processGraphQuery(qry);
    }

    public TripleSet processDescribeQuery(String qry) throws SparqlEndpointException {
        return processGraphQuery(qry);
    }

    public boolean processAskQuery(String qry) throws SparqlEndpointException {
        final BooleanQuery booleanQuery;
        try {
            booleanQuery = repositoryConnection.prepareBooleanQuery(QueryLanguage.SPARQL, qry);
        } catch (Exception e) {
            throw new SparqlEndpointException("Error while parsing query.", e);
        }
        try {
            return booleanQuery.evaluate();
        } catch (QueryEvaluationException qee) {
            throw new SparqlEndpointException("Error while evaluating query.", qee);
        }
    }

    public EndpointResponse processQuery(String qry) throws SparqlEndpointException {
        final ParsedQuery query;
        try {
            query = parseQuery(qry);
        } catch (MalformedQueryException mqe) {
            throw new SparqlEndpointException("Error while parsing query.", mqe);
        }
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
            return  getQueryType( parseQuery(qry) );
        } catch (MalformedQueryException mqe) {
            throw new UnsupportedOperationException();
        }
    }

    public QueryType getQueryType(ParsedQuery qry) throws SparqlEndpointException {
        return null;
    }

    private ParsedQuery parseQuery(String qry) throws MalformedQueryException {
        // TODO: migrate to: http://stackoverflow.com/questions/17040208/how-can-i-parse-optional-statements-from-a-sparql-query-using-sesame
        final QueryParser queryParser = QueryParserRegistry.getInstance().get(QueryLanguage.SPARQL).getParser();
        return queryParser.parseQuery(qry, null);
    }

    private TripleSet processGraphQuery(String qry) throws SparqlEndpointException {
        final GraphQuery graphQuery;
        try {
            graphQuery = repositoryConnection.prepareGraphQuery(QueryLanguage.SPARQL, qry);
        } catch (Exception e) {
            throw new SparqlEndpointException("Error while parsing query.", e);
        }

        final GraphQueryResult queryResult;
        try {
            queryResult = graphQuery.evaluate();
        } catch (QueryEvaluationException qee) {
            throw new SparqlEndpointException("Error while evaluating query.", qee);
        }

        final TripleSet result = new TripleBuffer();
        try {
            while (queryResult.hasNext()) {
                result.addTriple(
                        SESAME_CONVERSION_UTIL.convertSesameStatementToTriple(queryResult.next())
                );
            }
        } catch (Exception e) {
            throw new SparqlEndpointException("Error while iterating query result.", e);
        }
        return result;
    }

}

