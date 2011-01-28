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

package org.sindice.rdfcommons.storage.virtuoso;

import org.sindice.rdfcommons.model.Triple;
import org.sindice.rdfcommons.model.TripleBuffer;
import org.sindice.rdfcommons.model.TripleImpl;
import org.sindice.rdfcommons.model.TripleSet;
import org.sindice.rdfcommons.storage.AllTriplesStorageFilter;
import org.sindice.rdfcommons.storage.ResultSet;
import org.sindice.rdfcommons.storage.SparqlEndPoint;
import org.sindice.rdfcommons.storage.SparqlEndpointException;
import org.sindice.rdfcommons.storage.StorageException;
import org.sindice.rdfcommons.storage.TripleStorageFilter;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.MalformedURLException;

/**
 * Test case for {@link org.sindice.rdfcommons.storage.virtuoso.VirtuosoTripleStorage}.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id$
 */
public class VirtuosoTripleStorageTestCase {

    private VirtuosoTripleStorage storage;

    public static VirtuosoTripleStorageConfig createLocalTestConfig() {
        return new VirtuosoTripleStorageConfig(
            "jdbc:virtuoso://localhost:1111",
            "http://default/graph/test",
            "dba",
            "dba"
        );
    }

    @BeforeClass
    public void setUp() throws MalformedURLException {
        storage = new VirtuosoTripleStorage();
        VirtuosoTripleStorageConfig config = createLocalTestConfig();
        storage.openStorage(config);
    }

    @AfterClass
    public void tearDown() {
        storage.closeStorage();
        storage = null;
    }

    /**
     * Tests the #store method.
     *
     * @throws StorageException
     */
    @Test
    public void testBasicStore() throws StorageException {
        final int SIZE = 100;
        final String discriminant = "_" + Long.toString( System.currentTimeMillis() );

        // Storing.
        TripleSet tripleSet = new TripleBuffer();
        for(int i = 0; i < SIZE; i++) {
            tripleSet.addTriple(
                "http//org.sindice.rdfcommons#subject_"   + i + discriminant,
                "http//org.sindice.rdfcommons#predicate_" + i + discriminant,
                "http//org.sindice.rdfcommons#object_"    + i + discriminant
            );
        }
        storage.store(tripleSet);

        // Retrieving.
        TripleSet allTriples = storage.getTriples( AllTriplesStorageFilter.getInstance() );
        assert allTriples.getSize() >= SIZE : "Expected at least the same number of inserted triples.";
        for(int i = 0; i < SIZE; i++) {
            assert allTriples.containsTriple(
                "http//org.sindice.rdfcommons#subject_"   + i + discriminant,
                "http//org.sindice.rdfcommons#predicate_" + i + discriminant,
                "http//org.sindice.rdfcommons#object_"    + i + discriminant,
                Triple.SubjectType.uri, Triple.ObjectType.uri
            );
        }
    }

    @Test
    public void testBasicStoreOnNamedGraph() throws StorageException {
        final String graphName = "http://www.openlinksw.com/schemas/virtrdf#test-named-graph";
        final int SIZE = 100;
        final String discriminant = "_" + Long.toString( System.currentTimeMillis() );

        // Storing.
        TripleSet tripleSet = new TripleBuffer();
        for(int i = 0; i < SIZE; i++) {
            tripleSet.addTriple(
                "http//org.sindice.rdfcommons#subject_"   + i + discriminant,
                "http//org.sindice.rdfcommons#predicate_" + i + discriminant,
                "http//org.sindice.rdfcommons#object_"    + i + discriminant
            );
        }
        storage.store(graphName, tripleSet);

        // Retrieving.
        TripleSet allTriples = storage.getTriples( graphName,  AllTriplesStorageFilter.getInstance() );
        assert allTriples.getSize() >= SIZE : "Expected at least the same number of inserted triples.";
        for(int i = 0; i < SIZE; i++) {
            assert allTriples.containsTriple(
                "http//org.sindice.rdfcommons#subject_"   + i + discriminant,
                "http//org.sindice.rdfcommons#predicate_" + i + discriminant,
                "http//org.sindice.rdfcommons#object_"    + i + discriminant,
                Triple.SubjectType.uri, Triple.ObjectType.uri
            );
        }
    }

    /**
     * Tests the #store method with blank subjects and objects.
     * 
     * @throws StorageException
     */
    @Test
    public void testStoreWithBlanks() throws StorageException {
        final int SIZE = 100;
        final String discriminant = "_" + Long.toString( System.currentTimeMillis() );

        // Storing.
        TripleSet tripleSet = new TripleBuffer();
        for(int i = 0; i < SIZE; i++) {
            tripleSet.addTriple(
                new TripleImpl<String>(
                    "http//org.sindice.rdfcommons#blanksub_"  + i + discriminant,
                    "http//org.sindice.rdfcommons#predicate_" + i + discriminant,
                    "http//org.sindice.rdfcommons#blankobj_"  + i + discriminant
                )
            );
        }
        storage.store(tripleSet);

        // Retrieving.
        TripleSet allTriples = storage.getTriples( AllTriplesStorageFilter.getInstance() );
        assert allTriples.getSize() >= SIZE : "Expected at least the same number of inserted triples.";
        for(int i = 0; i < SIZE; i++) {
            assert allTriples.containsTriple(
                "http//org.sindice.rdfcommons#blanksub_"  + i + discriminant,
                "http//org.sindice.rdfcommons#predicate_" + i + discriminant,
                "http//org.sindice.rdfcommons#blankobj_"  + i + discriminant,
                Triple.SubjectType.uri, Triple.ObjectType.uri
            ) : String.format("Triple %s differs to expected one.", i);
        }
    }

    /**
     * Tests the #getTriples method.
     */
    @Test(dependsOnMethods = {"testBasicStore"} )
    public void testGetTriples() {
        TripleSet set = storage.getTriples( AllTriplesStorageFilter.getInstance() );
        assert set.getSize() > 0;
    }

    /**
     * Tests the #getSparqlEndPoint method.
     *
     * @throws org.sindice.rdfcommons.storage.SparqlEndpointException
     */
    @Test(dependsOnMethods = {"testBasicStore"} )
    public void testGetSparqlEndPoint() throws SparqlEndpointException {
        SparqlEndPoint endPoint = storage.getSparqlEndPoint();
        ResultSet rs = endPoint.processSelectQuery("SELECT * WHERE { graph ?g { ?s ?p ?o } } LIMIT 100");
        int counter = 0;
        while( rs.hasNext() ) {
            rs.next();
            assert rs.getVariableValue("s") != null;
            assert rs.getVariableValue("p") != null;
            assert rs.getVariableValue("o") != null;
            counter++;
        }
        Assert.assertEquals(counter, 100, "Unexpected number of results.");
    }

    /**
     * Tests the filtering with blank subject.
     *
     * @throws StorageException
     */
    @Test
    public void testFilterWithBlankSubjectCriteria() throws StorageException {
        final String graphName = "blank-graph-subject-test";

        // Preparing storage.
        storage.clear(graphName);
        TripleBuffer buffer = new TripleBuffer();
        for(int i = 0; i < 10; i++) {
            buffer.addTriple(
                    TripleImpl.createBNodeSubjectTriple(
                            "http://gramm/#pred", "http://gramm/#Obj" + i,
                            Triple.ObjectType.uri
                    ) 
            );
        }
        storage.store(graphName, buffer);

        TripleSet response = storage.getTriples(graphName, new TripleStorageFilter() {
            public String getSubjectMatching() {
                return null;
            }

            public String getPredicateMatching() {
                return "http://gramm/#pred";
            }

            public Object getObjectMatching() {
                return null;
            }

            public boolean requireLiteral() {
                return false;
            }

            public boolean requireSubjectBlank() {
                return false;
            }

            public boolean requireObjectBlank() {
                return false;
            }
        });

        assert response.getSize() == 10;
    }

    /**
     * Tests the filtering with blank object.
     *
     * @throws StorageException
     */
    @Test
    public void testFilterWithBlankObjectCriteria() throws StorageException {
        final String graphName = "blank-graph-object-test";

        // Preparing storage.
        storage.clear(graphName);
        TripleBuffer buffer = new TripleBuffer();
        for(int i = 0; i < 10; i++) {
            buffer.addTriple(
                    TripleImpl.createBNodeObjectTriple(
                            "http://gramm/#Sub" + i,
                            "http://gramm/#pred",
                            Triple.SubjectType.uri
                    )
            );
        }
        storage.store(graphName, buffer);

        TripleSet response = storage.getTriples(
                graphName,
                new TripleStorageFilter() {
                    public String getSubjectMatching() {
                        return null;
                    }

                    public String getPredicateMatching() {
                        return "http://gramm/#pred";
                    }

                    public Object getObjectMatching() {
                        return null;
                    }

                    public boolean requireLiteral() {
                        return false;
                    }

                    public boolean requireSubjectBlank() {
                        return false;
                    }

                    public boolean requireObjectBlank() {
                        return true;
                    }
                });

        Assert.assertEquals(response.getSize(), 10);
    }

    /**
     * Tests the filtering with literal object.
     *
     * @throws StorageException
     */
    /*
        TODO HIGH - Serialization of typed literals is wrong.
        After ingestion the triple store shows the following content:
        http://gramm/#Sub0  http://gramm/#pred0  com.hp.hpl.jena.datatypes.BaseDatatype$TypedValue@3b93a1fd
        http://gramm/#Sub1  http://gramm/#pred1  com.hp.hpl.jena.datatypes.BaseDatatype$TypedValue@3b93a1fd
        http://gramm/#Sub2  http://gramm/#pred2  com.hp.hpl.jena.datatypes.BaseDatatype$TypedValue@3b93a1fd
        http://gramm/#Sub3  http://gramm/#pred3  com.hp.hpl.jena.datatypes.BaseDatatype$TypedValue@3b93a1fd
        http://gramm/#Sub4  http://gramm/#pred4  com.hp.hpl.jena.datatypes.BaseDatatype$TypedValue@3b93a1fd
        http://gramm/#Sub5  http://gramm/#pred5  com.hp.hpl.jena.datatypes.BaseDatatype$TypedValue@3b93a1fd
        http://gramm/#Sub6  http://gramm/#pred6  com.hp.hpl.jena.datatypes.BaseDatatype$TypedValue@3b93a1fd
        http://gramm/#Sub7  http://gramm/#pred7  com.hp.hpl.jena.datatypes.BaseDatatype$TypedValue@3b93a1fd
        http://gramm/#Sub8  http://gramm/#pred8  com.hp.hpl.jena.datatypes.BaseDatatype$TypedValue@3b93a1fd
        http://gramm/#Sub9  http://gramm/#pred9  com.hp.hpl.jena.datatypes.BaseDatatype$TypedValue@3b93a1fd
     */
    @Test(enabled = false)
    public void testFilterWithLiteralObjectCriteria() throws StorageException {
        final String graphName = "http://test/testFilterWithLiteralObjectCriteria";
        final int SIZE = 10;

        // Preparing storage.
        storage.clear(graphName);
        TripleBuffer buffer = new TripleBuffer();
        for(int i = 0; i < SIZE; i++) {
            buffer.addTriple(
                    new TripleImpl<Double>(
                            "http://gramm/#Sub" + i,
                            "http://gramm/#pred" + i,
                            123.456,
                            Triple.ObjectType.literal
                    )
            );
        }
        storage.store(graphName, buffer);

        TripleSet response = storage.getTriples(
                graphName,
                new TripleStorageFilter() {
                    public String getSubjectMatching() {
                        return null;
                    }

                    public String getPredicateMatching() {
                        return null;
                    }

                    public Object getObjectMatching() {
                        return 123.456D;
                    }

                    public boolean requireLiteral() {
                        return true;
                    }

                    public boolean requireSubjectBlank() {
                        return false;
                    }

                    public boolean requireObjectBlank() {
                        return false;
                    }
                });

        Assert.assertEquals(response.getSize(), SIZE);
    }

    @Test
    public void testRemove() throws StorageException {
        // Data to be canceled.
        TripleSet tripleSet = new TripleBuffer();
        final String discriminant = "_" + Long.toString(System.currentTimeMillis());
        final int SIZE = 10;
        final String predicate = "http//org.sindice.rdfcommons#predicate_" + discriminant;
        for (int i = 0; i < SIZE; i++) {
            tripleSet.addTriple(
                    "http//org.sindice.rdfcommons#subject_" + i + discriminant,
                    predicate,
                    "http//org.sindice.rdfcommons#object_" + i + discriminant
            );
        }
        storage.store(tripleSet);

        // Data filter.
        TestDeleteFilter filter = new TestDeleteFilter(predicate);

        // The data is present.
        TripleSet before = storage.getTriples(filter);
        assert before.getSize() == SIZE;

        storage.remove(tripleSet);

        // The data is no longer present.
        TripleSet after = storage.getTriples(filter);
        assert after.getSize() == 0;
    }

    @Test
    public void testClearAndClearAll() throws StorageException {
        final String graphName = "http://graph/to/be/deleted";
        final int SIZE = 100;

        // Storing data to be deleted.
        TripleSet tripleSet = new TripleBuffer();
        for(int i = 0; i < SIZE; i++) {
            tripleSet.addTriple(
                new TripleImpl<String>(
                    "http://org.sindice.rdfcommons#subect"  + i,
                    "http://org.sindice.rdfcommons#predicate",
                    "http://org.sindice.rdfcommons#object"  + i
                )
            );
        }
        storage.clear(graphName);
        storage.store(graphName, tripleSet);

        TripleSet ts = getTBDTriples(graphName);
        assert ts.getSize() == SIZE : "Unexpected size: "+ ts.getSize();

        storage.clear("http://non/existing/graph");
        assert ts.getSize() == SIZE : "Unexpected size: "+ ts.getSize();

        storage.clearAll();
        ts = getTBDTriples(graphName);
        assert ts.getSize() == 0 : "Unexpected size: "+ ts.getSize();
    }

    private TripleSet getTBDTriples(String graphName) {
        return storage.getTriples(graphName, new TripleStorageFilter(){
            public String getSubjectMatching() {
                return null;
            }

            public String getPredicateMatching() {
                return "http://org.sindice.rdfcommons#predicate";
            }

            public String getObjectMatching() {
                return null;
            }

            public boolean requireLiteral() {
                return false;
            }

            public boolean requireSubjectBlank() {
                return false;
            }

            public boolean requireObjectBlank() {
                return false;
            }
        });
    }

    class TestDeleteFilter implements TripleStorageFilter {

        private String predicate;

        TestDeleteFilter(String p) {
            predicate = p;
        }

        public String getSubjectMatching() {
            return null;
        }

        public String getPredicateMatching() {
            return predicate;
        }

        public String getObjectMatching() {
            return null;
        }

        public boolean requireLiteral() {
            return false;
        }

        public boolean requireSubjectBlank() {
            return false;
        }

        public boolean requireObjectBlank() {
            return false;
        }
    }

}
