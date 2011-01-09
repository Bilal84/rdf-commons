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

import org.sindice.rdfcommons.model.TripleIterator;
import org.sindice.rdfcommons.model.TripleSet;


/**
 * Defines the triples storage.
 *
 * @see org.sindice.rdfcommons.model.TripleFilter
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public interface TripleStorage {

    /**
     * Opens the triple storage.
     *
     * @param config configuration for the triple storage.
     */
    void openStorage(TripleStorageConfig config);

    /**
     * Closes the triple storage.
     */
    void closeStorage();

    /**
     * Checks if a triple storage is open or not.
     *
     * @return <code>true</code> if opened, <code>false</code> otherwise.
     */
    boolean isOpen();

    /**
     * Stores a triple set in the default graph.
     *
     * @param ts
     * @throws StorageException if an error occurs during storaging.
     */
    void store(TripleSet ts) throws StorageException;

    /**
     * Stores the triple set in the named <i>graph</i>.
     *
     * @param graph name of the graph.
     * @param ts
     * @throws StorageException
     */
    void store(String graph, TripleSet ts) throws StorageException;

    /**
     * Removes the triples listed in the triple set from the default graph.
     * 
     * @param ts list of triples to be removed.
     * @throws StorageException if an error occurs during the triples removal.
     */
    void remove(TripleSet ts) throws StorageException;

    /**
     * Removes the triples listed in the triple set from the named <i>graph</i>.
     *
     * @param graph name of the graph.
     * @param ts
     * @throws StorageException
     */
    void remove(String graph, TripleSet ts) throws StorageException;

    /**
     * Returns an iterator over the triples matching the filter searched in the default graph.
     *
     * @param tripleFilter the filter to be applied on the triples. 
     * @return all the triples matching the filter.
     */
    TripleIterator getTriplesIterator(TripleStorageFilter tripleFilter);

    /**
     * Returns an iterator over the triples matching the filter searched in the named <i>graph</i>.
     *
     * @param graph name of the graph.
     * @param tripleFilter
     * @return all the triples matching the filter.
     */
    TripleIterator getTriplesIterator(String graph, TripleStorageFilter tripleFilter);

    /**
     * Returns a set of triples matching the triple filter searched in the default graph.
     *  
     * @param tripleFilter
     * @return set of triples matching with filter criteria.
     */
    TripleSet getTriples(TripleStorageFilter tripleFilter);

    /**
     * Returns a set of triples matching the triple filter searched in the named <i>graph</i>.
     *
     * @param graph
     * @param tripleFilter
     * @return set of triples matching with filter criteria.
     */
    TripleSet getTriples(String graph, TripleStorageFilter tripleFilter);

    /**
     * Returns the <i>SPARQL</i> endpoint of the triple storage.
     *
     * @return  the endpoint.
     */
    SparqlEndPoint getSparqlEndPoint();

    /**
     * Removes the entire content of the named <i>graph</i>.
     */
    void clear(String graph);

    /**
     * Removes the content of the entire storage.
     */
    void clearAll();

}
