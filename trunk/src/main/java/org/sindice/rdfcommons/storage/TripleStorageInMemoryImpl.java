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

import org.sindice.rdfcommons.model.Triple;
import org.sindice.rdfcommons.model.TripleBuffer;
import org.sindice.rdfcommons.model.TripleIterator;
import org.sindice.rdfcommons.model.TripleIteratorImpl;
import org.sindice.rdfcommons.model.TripleSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides the default implementation of the {@link TripleStorage}.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class TripleStorageInMemoryImpl implements TripleStorage {

    private TripleSet defaultGraph;

    private Map<String,TripleSet> namedGraphs;

    /**
     * Public constructor. Don't need to pass for the {@link TripleStorageFactory}.
     */
    public TripleStorageInMemoryImpl() {
        defaultGraph = new TripleBuffer();
        namedGraphs  = new HashMap<String,TripleSet>();
    }

    public void openStorage(TripleStorageConfig config) {
        // Empty.
    }

    public void closeStorage() {
        // Empty.
    }

    public boolean isOpen() {
        return true;
    }

    public synchronized void store(TripleSet ts) throws StorageException {
        defaultGraph.add(ts);
    }

    public void store(String graph, TripleSet ts) throws StorageException {
        TripleSet namedGraph = getGraph(graph);
        namedGraph.add(ts);
    }

    public synchronized void remove(TripleSet ts) throws StorageException {
        defaultGraph.remove(ts);
    }

    public void remove(String graph, TripleSet ts) throws StorageException {
        TripleSet namedGraph = getGraph(graph);
        namedGraph.remove(ts);
    }

    public synchronized TripleIterator getTriplesIterator(TripleStorageFilter tripleFilter) {
        return new TripleIteratorImpl(filterTriples(defaultGraph, tripleFilter).iterator());
    }

    public TripleIterator getTriplesIterator(String graph, TripleStorageFilter tripleFilter) {
        TripleSet namedGraph = getGraph(graph);
        return new TripleIteratorImpl(filterTriples(namedGraph, tripleFilter).iterator());
    }

    public synchronized TripleSet getTriples(TripleStorageFilter tripleFilter) {
        return new TripleBuffer(filterTriples(defaultGraph, tripleFilter));
    }

    public TripleSet getTriples(String graph, TripleStorageFilter tripleFilter) {
        TripleSet namedGraph = getGraph(graph);
        return new TripleBuffer(filterTriples(namedGraph, tripleFilter));
    }


    public SparqlEndPoint getSparqlEndPoint() {
        // TODO (LOW) this should be handled with an effective SPARQL Endpoint
        throw new UnsupportedOperationException();
    }

    public void clear(String graph) {
        TripleSet namedGraph = getGraph(graph);
        namedGraph.clear();
    }

    public synchronized void clearAll() {
        defaultGraph.clear();
    }

    private TripleSet getGraph(String graph) {
        TripleSet ts = namedGraphs.get(graph);
        if(ts == null) {
            ts = new TripleBuffer();
            namedGraphs.put(graph, ts);
        }
        return ts;
    }

    private synchronized List<Triple> filterTriples(TripleSet ts, TripleStorageFilter tripleFilter) {
        List<Triple> set = ts.getTriples();
        List<Triple> result = new ArrayList<Triple>();

        for (Triple triple : set) {
            if (
                    acceptSubject(tripleFilter, triple)
                            &&
                    matches( tripleFilter.getPredicateMatching(), triple.getPredicate() )
                            &&
                    acceptObject(tripleFilter, triple)
            ) {
                result.add(triple);
            }
        }
        return result;
    }

    private boolean acceptSubject(TripleStorageFilter filter, Triple triple) {
        if (filter.requireSubjectBlank()) {
            return triple.isSubjectBNode();
        }
        return matches(filter.getSubjectMatching(), triple.getSubject() );
    }

    private boolean acceptObject(TripleStorageFilter filter, Triple triple) {
        if (filter.requireObjectBlank()) {
            return triple.isObjectBNode();
        }
        return
            matches( filter.getObjectMatching(), triple.getObject() )
                &&
            (! filter.requireLiteral() || triple.isObjectLiteral() == filter.requireLiteral());
    }

    private boolean matches(Object match, Object value) {
        return match == null || match.equals(value);
    }

}