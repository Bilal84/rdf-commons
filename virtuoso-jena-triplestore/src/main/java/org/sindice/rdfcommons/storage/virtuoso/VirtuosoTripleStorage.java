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

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import org.apache.log4j.Logger;
import org.sindice.rdfcommons.adapter.jena.JenaConversionUtil;
import org.sindice.rdfcommons.model.TripleBuffer;
import org.sindice.rdfcommons.model.TripleIterator;
import org.sindice.rdfcommons.model.TripleSet;
import org.sindice.rdfcommons.storage.SparqlEndPoint;
import org.sindice.rdfcommons.storage.StorageException;
import org.sindice.rdfcommons.storage.TripleStorage;
import org.sindice.rdfcommons.storage.TripleStorageConfig;
import org.sindice.rdfcommons.storage.TripleStorageFilter;
import virtuoso.jena.driver.VirtGraph;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link org.sindice.rdfcommons.storage.TripleStorage} implementation for <i>Virtuoso</i>.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id$
 */
public class VirtuosoTripleStorage implements TripleStorage {

    private static final Logger logger = Logger.getLogger(VirtuosoTripleStorage.class);

    private VirtuosoTripleStorageConfig virtuosoConfig;

    private Map<String,VirtGraph> virtuosoGraphs;

    /**
     * Represents the master graph.
     */
    private VirtGraph defaultGraph;

    public void openStorage(TripleStorageConfig config) {
        if( ! (config instanceof VirtuosoTripleStorageConfig) ) {
            throw new IllegalArgumentException("config must be instance of " + VirtuosoTripleStorageConfig.class);
        }
        virtuosoConfig = (VirtuosoTripleStorageConfig) config;
        virtuosoGraphs = new HashMap<String,VirtGraph>();
    }

    public void closeStorage() {
        virtuosoConfig = null;

        try {
            if(defaultGraph != null) {
                defaultGraph.close();
            }
        } catch (Exception e) {
            logger.error("Error while closing master graph.", e);
        }

        for(VirtGraph vg : virtuosoGraphs.values()) {
            try {
                vg.close();
            } catch (Exception e) {
                logger.error("Error while closing a storage.", e);
            }
        }
        virtuosoGraphs.clear();
    }

    public boolean isOpen() {
        return defaultGraph != null || virtuosoGraphs.size() > 0;
    }

    public void store(TripleSet tripleSet) throws StorageException {
        store( virtuosoConfig.getDefaultGraphName(), tripleSet );
    }

    public void store(String graph, TripleSet tripleSet) throws StorageException {
        checkOpened();
        VirtGraph vg = getGraph(graph);
        vg.getBulkUpdateHandler().add(
            JenaConversionUtil.getInstance().convertToJenaTriples(tripleSet)
        );
    }

    public void remove(TripleSet tripleSet) throws StorageException {
        remove( virtuosoConfig.getDefaultGraphName(), tripleSet );
    }

    public void remove(String graph, TripleSet tripleSet) throws StorageException {
        checkOpened();
        VirtGraph vg = getGraph(graph);
        vg.getBulkUpdateHandler().delete(
            JenaConversionUtil.getInstance().convertToJenaTriples(tripleSet)
        );
    }

    public TripleIterator getTriplesIterator(TripleStorageFilter tripleFilter) {
        return getTriplesIterator( virtuosoConfig.getDefaultGraphName(), tripleFilter );
    }

    public TripleIterator getTriplesIterator(String graph, TripleStorageFilter tripleFilter) {
        checkOpened();
        VirtGraph vg = getGraph(graph);
        final ExtendedIterator iterator = vg.graphBaseFind(
                JenaConversionUtil.getInstance().convertToJenaTripleMatch(tripleFilter)
        );
        return new TripleIterator() {

            private com.hp.hpl.jena.graph.Triple triple;

            public boolean hasNext() {
                return iterator.hasNext();
            }

            public void next() {
                triple = (com.hp.hpl.jena.graph.Triple) iterator.next();
            }

            public String subject() {
                return triple.getSubject().getURI();
            }

            public String predicate() {
                return triple.getPredicate().getURI();
            }

            public Object object() {
                return triple.getObject().getLiteralValue();
            }

            public String objectStr() {
                return triple.getObject().getURI();
            }

            public boolean isBlankSubject() {
                return triple.getSubject().isBlank();
            }

            public boolean isBlankObject() {
                return triple.getObject().isBlank();
            }

            public boolean isObjLiteral() {
                return triple.getObject().isLiteral();
            }
        };
    }

    public TripleSet getTriples(TripleStorageFilter tripleStorageFilter) {
        return getTriples( virtuosoConfig.getDefaultGraphName(), tripleStorageFilter );
    }

    public TripleSet getTriples(String graph, TripleStorageFilter tripleStorageFilter) {
        checkOpened();

        final JenaConversionUtil jenaConversionUtil = JenaConversionUtil.getInstance();
        final VirtGraph vg = getGraph(graph);
        final TripleMatch tripleMatch = jenaConversionUtil.convertToJenaTripleMatch(tripleStorageFilter);
        final ExtendedIterator extendedIterator = vg.find(
                tripleMatch.getMatchSubject(),
                tripleMatch.getMatchPredicate(),
                tripleMatch.getMatchObject()
        );

        final TripleSet result = new TripleBuffer();
        Triple triple;
        try {
            while (extendedIterator.hasNext()) {
                triple = (Triple) extendedIterator.next();
                result.addTriple(jenaConversionUtil.convertJenaTripleToTriple(triple, graph));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while converting triple.", e);
        }
        return result;
    }

    public SparqlEndPoint getSparqlEndPoint() {
        checkOpened();
        return new VirtuosoSparqlEndPoint( getDefaultGraph() );
    }

    public void clear(String graph) {
        checkOpened();
        VirtGraph vg = getGraph(graph);
        vg.clear();
    }

    public void clearAll() throws StorageException {
        final VirtGraph vg = getGraph(null);
        final Connection connection = vg.getConnection();
        try {
            connection.prepareStatement("RDF_GLOBAL_RESET()").execute();
        } catch (SQLException sqle) {
            throw new StorageException("Error while deleting all triple store content.", sqle);
        }
    }

    /**
     * Checks if the virtuoso connector is opened. 
     */
    private void checkOpened() {
        if(virtuosoConfig == null) {
            throw new RuntimeException("The storage must be opened first.");
        }
    }

    private VirtGraph getGraph(String graphURI) {
        // TODO: (MED) add graph pool to avoid creating too much connections.
        VirtGraph vg = virtuosoGraphs.get(graphURI);
        if(vg == null) {
            vg = new VirtGraph(
                graphURI,
                virtuosoConfig.getUrl(),
                virtuosoConfig.getUsername(),
                virtuosoConfig.getPassword()
            );
            virtuosoGraphs.put(graphURI, vg);
        }
        return vg;
    }

    private VirtGraph getDefaultGraph() {
        if(defaultGraph == null) {
            defaultGraph = new VirtGraph(
                virtuosoConfig.getUrl(),
                virtuosoConfig.getUsername(),
                virtuosoConfig.getPassword()
            );
        }
        return defaultGraph;
    }

}
