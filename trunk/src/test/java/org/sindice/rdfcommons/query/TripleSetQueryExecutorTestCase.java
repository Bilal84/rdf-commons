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

import org.sindice.rdfcommons.InMemoryResultSet;
import org.sindice.rdfcommons.Triple;
import org.sindice.rdfcommons.TripleBuffer;
import org.sindice.rdfcommons.TripleSet;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Test case for the {@link TripleSetQueryExecutor}.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class TripleSetQueryExecutorTestCase {

    private static final Logger logger = Logger.getLogger(TripleSetQueryExecutorTestCase.class);

    private QueryContext queryContext;

    private MatchChain matchChain;

    private TripleSet tripleSet;

    private TripleSetQueryExecutor tripleSetQueryExecutor;

    @BeforeMethod
    public void setUp() {
        queryContext = new QueryContext();
        matchChain = new MatchChain(queryContext);
        tripleSet = new TripleBuffer();
        tripleSet.addTriple("", "", "");
        tripleSetQueryExecutor = new TripleSetQueryExecutor();
    }

    @AfterMethod
    public void tearDown() {
        matchChain = null;
        tripleSet.clear();
        tripleSet = null;
        tripleSetQueryExecutor = null;
    }

    /**
     * Tests the {@link TripleSetQueryExecutor#createMatch(org.sindice.rdfcommons.Triple , Match, Match)}
     * method.
     */
    @Test
    public void testCreateMatch() {
        Match match = TripleSetQueryExecutor.createMatch(
                new Triple<String>("1", "2", "3"),
                new Match(queryContext, "?a",  "2", "?c"),
                new Match(queryContext, "?c", "?d", "?e")
        );
        logger.info("Match: " + match);
        assert match.getSub().equals("3");
        assert match.getPred().equals("?d");
        assert match.getObj().equals("?e");
    }

    /**
     * Tests the {@link TripleSetQueryExecutor#progressIndex(int, int[], org.sindice.rdfcommons.TripleSet[])}
     * method. 
     */
    @Test
    public void testProgressIndex() {
        int indexes[] = new int[2];

        TripleSet ts1 = new TripleBuffer();
        ts1.addTriple("1", "1", "1");
        ts1.addTriple("2", "2", "2");

        TripleSet ts2 = new TripleBuffer();
        ts2.addTriple("3", "3", "3");
        ts2.addTriple("4", "4", "4");
        ts2.addTriple("5", "5", "5");

        TripleSet[] tss = new TripleSet[]{ts1, ts2};
        int begin = 0;
        Set<Object> sequence = new HashSet<Object>();
        while(begin >= 0) {
            begin = TripleSetQueryExecutor.progressIndex(begin, indexes, tss);
            logger.info( "Indexes: " + Arrays.toString(indexes) );
            assert sequence.add( indexes.clone() );
        }
        assert sequence.size() == ts1.getSize() * ts2.getSize();
    }

    /**
     * Tests the {@link TripleSetQueryExecutor#multiply(Match[], org.sindice.rdfcommons.TripleSet[], org.sindice.rdfcommons.InMemoryResultSet)}.
     */
    @Test
    public void testMultiply() {
        Match[] matches = {
            new Match(queryContext, "?a", "?b", "?c"),
            new Match(queryContext, "?d", "?e", "?f")
        };

        TripleSet ts1 = new TripleBuffer();
        ts1.addTriple("A", "B", "C");
        ts1.addTriple("D", "E", "F");

        TripleSet ts2 = new TripleBuffer();
        ts2.addTriple("G", "H", "I");
        ts2.addTriple("L", "M", "N");
        ts2.addTriple("O", "P", "Q");

        TripleSet[] tss = new TripleSet[]{ts1, ts2};

        InMemoryResultSet imrs = new InMemoryResultSet( new String[]{"?a", "?b", "?c", "?d", "?e", "?f"});

        TripleSetQueryExecutor.multiply(matches, tss, imrs);

        logger.info("imrs: " + imrs);

        assert imrs.size() == ts1.getSize() * ts2.getSize();
    }

}
