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

import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import org.testng.annotations.Test;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

import java.sql.SQLException;
import java.util.List;

/**
 * Tests the basic access to the <i>Virtuoso</i> triple storage.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id$
 */
public class VirtuosoDriverTestCase {

    private static final String NAMED_GRAPH_URI = "http://test/named/graph/jena";

    /**
     * Test querying by triple match.
     */
    @Test
    public void testTriples() {
        VirtGraph virtGraph = createNamedGraph();
        addTestTriples(virtGraph);
        ExtendedIterator iter = virtGraph.graphBaseFind( new Triple( Node.ANY, Node.ANY, Node.ANY ) );
        List list = iter.toList();
        assert list.size() > 0 : "Expected something.";
        virtGraph.close();
    }


    /**
     * Test querying by SPARQL endpoint.
     *
     * @throws java.sql.SQLException
     */
    @Test
    public void testDataRetrieval() throws SQLException {
        VirtGraph virtGraph = createGraph();
        Query sparql = QueryFactory.create("SELECT * WHERE { GRAPH ?graph { ?s ?p ?o } } limit 100");
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (sparql, virtGraph);
		ResultSet results = vqe.execSelect();
		int counter = 0;
        while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
		    RDFNode graph = result.get("graph");
		    RDFNode s = result.get("s");
		    RDFNode p = result.get("p");
		    RDFNode o = result.get("o");
            assert graph != null && s != null && p != null && o != null;
            counter++;
        }

        assert counter == 100 : "Unexpected graph size: " + counter ;
        virtGraph.close();
    }

    /**
     * Tests the literal support.
     */
    @Test
    public void testLiteralSupport() {
        final int SIZE = 100;

        Model model = ModelFactory.createDefaultModel();
        for(int i = 0; i < SIZE; i++) {
            Resource sub = model.createResource("http://test#Sub" + i);
            Property pre = model.createProperty("http://test#pre");
            Literal obj = model.createTypedLiteral(123.456);
            model.add(sub, pre, obj);
        }

        VirtGraph virtGraph = createNamedGraph();
        virtGraph.clear();
        virtGraph.getBulkUpdateHandler().add( model.getGraph() );


        assert executeQueryAndCountResults(
                virtGraph,
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> SELECT * WHERE {?s <http://test#pre> 123.456 }"
        ) == SIZE;
        assert executeQueryAndCountResults(
                virtGraph,
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> SELECT * WHERE " +
                "{?s <http://test#pre> \"123.456\"^^xsd:double }"
        ) == SIZE;
        assert executeQueryAndCountResults(
                virtGraph,
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> SELECT * WHERE {?s <http://test#pre> \"123.456\" }"
        ) == 0;
    }

    /**
     * Tests the ability of the driver to perform CONSTRUCT queries.
     */
    @Test
    public void testConstruct() {
        final String qry = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
                           "CONSTRUCT {?s ?p \"123.456\"^^xsd:double} " +
                            String.format("FROM <%s> ", NAMED_GRAPH_URI) +
                            "WHERE {?s ?p \"123.456\"^^xsd:double   }";
        final VirtGraph virtGraph = createGraph();
        addTestTriples(virtGraph);
        Query sparql = QueryFactory.create(qry);
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, virtGraph);
        vqe.execConstruct();
    }

    /**
     * Executes a query on the given graph.
     *
     * @param virtGraph
     * @param qry
     * @return the number of retrieved triples.
     */
    private int executeQueryAndCountResults(VirtGraph virtGraph, String qry) {
        Query sparql = QueryFactory.create(qry);
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (sparql, virtGraph);
        ResultSet rs = vqe.execSelect();

        int count = 0;
        while(rs.hasNext()) {
            rs.next();
            count++;
        }
        return count;
    }

    /**
     * @return a named instance of the virtuoso graph.
     */
    private VirtGraph createNamedGraph() {
		final String url = "jdbc:virtuoso://localhost:1111";
        return new VirtGraph (NAMED_GRAPH_URI, url, "dba", "dba");
    }

    /**
     * @return an anonymous instance of the virtuoso graph.
     */
    private VirtGraph createGraph() {
		final String url = "jdbc:virtuoso://localhost:1111";
        return new VirtGraph (url, "dba", "dba");
    }

    private void addTestTriples(VirtGraph virtGraph) {
        virtGraph.add(
                new Triple(
                        Node.createURI("http://fake/sub"),
                        Node.createURI("http://fake/pred"),
                        Node.createLiteral(
                                "123.456",
                                null,
                                TypeMapper.getInstance().getSafeTypeByName("http://www.w3.org/2001/XMLSchema#double")
                        )
                )
        );
    }

}
