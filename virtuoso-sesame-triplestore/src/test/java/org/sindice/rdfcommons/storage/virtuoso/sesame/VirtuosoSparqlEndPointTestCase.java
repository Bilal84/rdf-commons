package org.sindice.rdfcommons.storage.virtuoso.sesame;

import org.openrdf.repository.RepositoryException;
import org.sindice.rdfcommons.model.Triple;
import org.sindice.rdfcommons.model.TripleSet;
import org.sindice.rdfcommons.storage.ResultSet;
import org.sindice.rdfcommons.storage.SparqlEndPoint;
import org.sindice.rdfcommons.storage.SparqlEndpointException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import virtuoso.sesame2.driver.VirtuosoRepositoryConnection;

/**
 * Test case for {@link VirtuosoSparqlEndPoint}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
// TODO: also for Jena
public class VirtuosoSparqlEndPointTestCase {

    private VirtuosoSparqlEndPoint endPoint;

    @BeforeClass
    public void setUp() throws RepositoryException {
        endPoint = new VirtuosoSparqlEndPoint(
                (VirtuosoRepositoryConnection) TestUtil.getTestRepository().getConnection()
        );
    }

    @AfterClass
    public void tearDown() {
        endPoint = null;
    }

    @Test
    public void testProcessSelectQuery() throws SparqlEndpointException {
        final ResultSet resultSet = endPoint.processSelectQuery("SELECT * WHERE {?s ?p ?o}");
        int records = 0;
        while(resultSet.hasNext()) {
            resultSet.next();
            records++;
            Assert.assertEquals(resultSet.getVariables().length, 3, "Invalid variable list.");
            Assert.assertEquals(resultSet.getVariables()[0], "s","Invalid variable list.");
            Assert.assertEquals(resultSet.getVariables()[1], "p","Invalid variable list.");
            Assert.assertEquals(resultSet.getVariables()[2], "o","Invalid variable list.");
        }
        Assert.assertTrue(records > 1000, "Invalid resultset size.");
    }

    @Test
    public void testProcessDescribeQuery() throws SparqlEndpointException {
        final TripleSet tripleSet = endPoint.processDescribeQuery("DESCRIBE ?s WHERE {?s a ?o}");
        Assert.assertTrue(tripleSet.getSize() > 1000, "Invalid resultset size.");
    }

    @Test
    public void testProcessConstructQuery() throws SparqlEndpointException {
        final TripleSet tripleSet = endPoint.processConstructQuery(
                "CONSTRUCT {?s <http://fake/pred> ?o} WHERE {?s a ?o}"
        );
        Assert.assertTrue(tripleSet.getSize() > 10, "Invalid resultset size.");
        for(Triple triple : tripleSet) {
            Assert.assertEquals(triple.getPredicate(), "http://fake/pred", "Unexpected predicate.");
        }
    }

    @Test
    public void testProcessAskQuery() throws SparqlEndpointException {
        Assert.assertTrue( endPoint.processAskQuery("ASK {?s a ?o}"), "Invalid ASK result.");
        Assert.assertFalse( endPoint.processAskQuery("ASK {?s <http://unexisting/prop> ?o}"), "Invalid ASK result.");
    }

    @Test
    public void testProcessQuery() throws SparqlEndpointException {
        Assert.assertEquals(
                endPoint.processQuery("SELECT ?s ?p ?o WHERE {?s a ?o}").getType(),
                SparqlEndPoint.QueryType.select,
                "Invalid query processing."
        );
        Assert.assertEquals(
                endPoint.processQuery("DESCRIBE ?s WHERE {?s a ?o}").getType(),
                SparqlEndPoint.QueryType.describe,
                "Invalid query processing."
        );
        Assert.assertEquals(
                endPoint.processQuery("CONSTRUCT {?s <http://appo/prop> ?o} WHERE {?s a ?o}").getType(),
                SparqlEndPoint.QueryType.construct,
                "Invalid query processing."
        );
        Assert.assertEquals(
                endPoint.processQuery("ASK {?s a ?o}").getType(),
                SparqlEndPoint.QueryType.ask,
                "Invalid query processing."
        );
    }

}
