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

import org.sindice.rdfcommons.ResultSet;
import org.sindice.rdfcommons.TripleBuffer;
import org.sindice.rdfcommons.TripleSet;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;

/**
 * Test case of class {@link QueryBuilder} class.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class QueryBuilderTestCase {

    private static final Logger logger = Logger.getLogger(QueryBuilderTestCase.class);

    private QueryBuilder queryBuilder;

    @BeforeMethod
    public void setUp() {
        queryBuilder = new QueryBuilder();
    }

    @AfterMethod
    public void tearDown() {
        queryBuilder = null;
    }

    /**
     * Tests the query building.
     */
    @Test
    public void testQueryBuild() {
        buildQuery();
        final String qry = queryBuilder.toSparqlSelect();
        logger.info(qry);
        assert qry.equals("SELECT * WHERE {{?v0 ?v1 ?a}{?a ?v2 ?v3}}"); 
    }

    /**
     * Tests the var retrieval.
     */
    @Test
    public void testGetVars() {
        buildQuery();
        String[] vars = queryBuilder.getVars();
        logger.info(Arrays.toString(vars));
        assert vars.length == 5;
    }

    /**
     * Tests the query execution on a single match.
     */
    @Test
    public void testSingleMatchQuery() {
        QueryBuilder qb = new QueryBuilder();
        qb.addQuery("?a", "2", "?c");

        TripleSet ts = getTripleSet();

        ResultSet rs = qb.execOnTriples(ts);
        logger.info(rs);

        checkSimpleQueryResult(rs);
    }

    /**
     * Tests the query execution on a complex match.
     */
    //@Test TODO: reactivate this.
    public void testComplexMatchQuery() {
        QueryBuilder qb = new QueryBuilder();
        qb.addQuery("?a",  "2", "?c");
        qb.addQuery("?c", "?d", "?e");

        TripleSet ts = getTripleSet();

        ResultSet rs = qb.execOnTriples(ts);
        logger.info(rs);

        int resultscount = countResults(rs);
        assert resultscount == 2;
    }

    private TripleSet getTripleSet() {
        TripleSet ts = new TripleBuffer();
        ts.addTriple("A", "2", "C");
        ts.addTriple("D", "2", "F");
        ts.addTriple("C", "H", "I");
        ts.addTriple("J", "K", "L");
        ts.addTriple("M", "N", "O");
        ts.addTriple("P", "Q", "R");
        ts.addTriple("C", "T", "U");
        ts.addTriple("V", "W", "X");
        return ts;
    }

    private void buildQuery() {
        queryBuilder.addQuery(null, null, "?a");
        queryBuilder.addQuery("?a", null, null);
    }

    private void checkSimpleQueryResult(ResultSet rs) {
        assert rs.hasNext();
        assert "D".equals( rs.getVariableValue("?a") );
        assert "F".equals( rs.getVariableValue("?c") );
        rs.next();

        assert rs.hasNext();
        assert "A".equals( rs.getVariableValue("?a") );
        assert "C".equals( rs.getVariableValue("?c") );
        rs.next();

        assert ! rs.hasNext();
    }

    private int countResults(ResultSet rs) {
        int count = 0;
        while(rs.hasNext()) {
            rs.next();
            count++;
        }
        return count;
    }
}
