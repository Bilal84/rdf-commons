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

import org.openrdf.model.Graph;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.GraphImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.sindice.rdfcommons.adapter.sesame.RDFHelper;
import org.testng.annotations.Test;
import virtuoso.sesame2.driver.VirtuosoRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests the basic access to the <i>Virtuoso</i> triple storage.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id: VirtuosoDriverTestCase.java 143 2011-05-23 21:41:46Z michele.mostarda $
 */
// TODO: common test with jena
public class VirtuosoDriverTestCase {

    /**
     * Test querying by triple match.
     *
     * @throws org.openrdf.repository.RepositoryException
     */
    @Test
    public void testTriples() throws RepositoryException {
        VirtuosoRepository repository = TestUtil.getTestRepository();
        RepositoryConnection connection = repository.getConnection();
        addTestTriples(connection);
        RepositoryResult result = connection.getStatements(null, null, null, true, (Resource) null);
        List<Statement> statements = toList(result);
        assert statements.size() > 0 : "Expected something.";
        connection.close();
    }


    /**
     * Test querying by SPARQL endpoint.
     *
     * @throws java.sql.SQLException
     * @throws org.openrdf.query.MalformedQueryException
     * @throws org.openrdf.query.QueryEvaluationException
     * @throws org.openrdf.repository.RepositoryException
     */
    @Test
    public void testDataRetrieval()
    throws SQLException, RepositoryException, MalformedQueryException, QueryEvaluationException {
        VirtuosoRepository repository = TestUtil.getTestRepository();
        RepositoryConnection connection = repository.getConnection();
        try {
        TupleQuery query = connection.prepareTupleQuery(
                QueryLanguage.SPARQL,
                "SELECT * WHERE { GRAPH ?graph { ?s ?p ?o } } limit 100"
        );
		TupleQueryResult results = query.evaluate();
		int counter = 0;
        while (results.hasNext()) {
			BindingSet bindingSet = results.next();
		    Value graph = bindingSet.getValue("graph");
		    Value s = bindingSet.getValue("s");
		    Value p = bindingSet.getValue("p");
		    Value o = bindingSet.getValue("o");
            assert graph != null && s != null && p != null && o != null;
            counter++;
        }

        assert counter == 100 : "Unexpected graph size: " + counter ;
        } finally {
            connection.close();
        }
    }

    /**
     * Tests the literal support.
     *
     * @throws org.openrdf.query.MalformedQueryException
     * @throws org.openrdf.query.QueryEvaluationException
     * @throws org.openrdf.repository.RepositoryException
     */
    @Test
    public void testLiteralSupport() throws RepositoryException, MalformedQueryException, QueryEvaluationException {
        final int SIZE = 100;

        Graph graph = new GraphImpl();
        for(int i = 0; i < SIZE; i++) {
            URI sub = RDFHelper.uri("http://test#Sub" + i);
            URI pre = RDFHelper.uri("http://test#pre");
            Literal obj = RDFHelper.literal(123.456);
            graph.add(sub, pre, obj);
        }

        VirtuosoRepository repository = TestUtil.getTestRepository();
        RepositoryConnection connection = repository.getConnection();
        connection.clear();
        connection.add(graph);

        assert executeQueryAndCountResults(
                connection,
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> SELECT * WHERE {?s <http://test#pre> 123.456 }"
        ) == SIZE;
        assert executeQueryAndCountResults(
                connection,
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> SELECT * WHERE " +
                "{?s <http://test#pre> \"123.456\"^^xsd:double }"
        ) == SIZE;
        assert executeQueryAndCountResults(
                connection,
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> SELECT * WHERE {?s <http://test#pre> \"123.456\" }"
        ) == 0;
    }

    /**
     * Tests the ability of the driver to perform CONSTRUCT queries.
     *
     * @throws org.openrdf.query.MalformedQueryException
     * @throws org.openrdf.query.QueryEvaluationException
     * @throws org.openrdf.repository.RepositoryException
     */
    @Test
    public void testConstruct() throws RepositoryException, MalformedQueryException, QueryEvaluationException {
        final String qry = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
                           "CONSTRUCT {?s ?p \"123.456\"^^xsd:double} " +
                            String.format("FROM <%s> ", TestUtil.NAMED_GRAPH_URI) +
                            "WHERE {?s ?p \"123.456\"^^xsd:double   }";
        VirtuosoRepository repository = TestUtil.getTestRepository();
        RepositoryConnection connection = repository.getConnection();
        GraphQuery query = connection.prepareGraphQuery(QueryLanguage.SPARQL, qry);
        query.evaluate();
    }

    /**
     * Executes a query on the given graph.
     *
     * @param connection
     * @param qry
     * @return the number of retrieved triples.
     * @throws org.openrdf.query.MalformedQueryException
     * @throws org.openrdf.query.QueryEvaluationException
     * @throws org.openrdf.repository.RepositoryException
     */
    private int executeQueryAndCountResults(RepositoryConnection connection, String qry)
    throws QueryEvaluationException, RepositoryException, MalformedQueryException {
        TupleQuery query = connection.prepareTupleQuery(QueryLanguage.SPARQL, qry);
        TupleQueryResult trs = query.evaluate();

        int count = 0;
        while(trs.hasNext()) {
            trs.next();
            count++;
        }
        return count;
    }

    private void addTestTriples(RepositoryConnection connection) throws RepositoryException {
        connection.add(
                RDFHelper.triple(
                        RDFHelper.uri("http://fake/sub"),
                        RDFHelper.uri("http://fake/pred"),
                        RDFHelper.literal(
                                "123.456",
                                RDFHelper.uri("http://www.w3.org/2001/XMLSchema#double")
                        )
                )
        );
    }

    private List<Statement> toList(RepositoryResult repositoryResult) throws RepositoryException {
        final List<Statement> result = new ArrayList<Statement>();
        while(repositoryResult.hasNext()) {
            result.add((Statement) repositoryResult.next());
        }
        return result;
    }

}
