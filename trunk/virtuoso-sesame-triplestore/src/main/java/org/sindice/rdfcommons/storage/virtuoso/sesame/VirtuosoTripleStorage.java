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

import org.apache.log4j.Logger;
import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.sindice.rdfcommons.adapter.sesame.SesameConversionUtil;
import org.sindice.rdfcommons.model.Triple;
import org.sindice.rdfcommons.model.TripleBuffer;
import org.sindice.rdfcommons.model.TripleIterator;
import org.sindice.rdfcommons.model.TripleSet;
import org.sindice.rdfcommons.storage.SparqlEndPoint;
import org.sindice.rdfcommons.storage.StorageException;
import org.sindice.rdfcommons.storage.TripleStorage;
import org.sindice.rdfcommons.storage.TripleStorageConfig;
import org.sindice.rdfcommons.storage.TripleStorageFilter;
import virtuoso.sesame2.driver.VirtuosoRepository;
import virtuoso.sesame2.driver.VirtuosoRepositoryConnection;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * {@link org.sindice.rdfcommons.storage.TripleStorage} implementation for <i>Virtuoso</i>.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id: VirtuosoTripleStorage.java 143 2011-05-23 21:41:46Z michele.mostarda $
 */
public class VirtuosoTripleStorage implements TripleStorage {

    private static final Logger logger = Logger.getLogger(VirtuosoTripleStorage.class);

    private static final ValueFactory valueFactory = new ValueFactoryImpl();

    private static final SesameConversionUtil sesameConversionUtil = SesameConversionUtil.getInstance();

    private VirtuosoTripleStorageConfig virtuosoConfig;

    private VirtuosoRepository virtuosoRepository;

    private VirtuosoRepositoryConnection repositoryConnection;

    public void openStorage(TripleStorageConfig config) throws StorageException {
        if( ! (config instanceof VirtuosoTripleStorageConfig) ) {
            throw new IllegalArgumentException("config must be instance of " + VirtuosoTripleStorageConfig.class);
        }
        virtuosoConfig = (VirtuosoTripleStorageConfig) config;
        virtuosoRepository = new VirtuosoRepository(
                virtuosoConfig.getUrl(),
                virtuosoConfig.getUsername(),
                virtuosoConfig.getPassword()
        );
        try {
            repositoryConnection = (VirtuosoRepositoryConnection) virtuosoRepository.getConnection();
        } catch (RepositoryException re) {
            throw new StorageException("Error while establishing connection.", re);
        }
    }

    public void closeStorage() {
        virtuosoConfig = null;

        try {
            if(repositoryConnection != null) {
                repositoryConnection.close();
            }
        } catch (Exception e) {
            logger.error("Error while closing master graph.", e);
        }
    }

    public boolean isOpen() {
        return repositoryConnection != null;
    }

    public void store(String graph, TripleSet tripleSet) throws StorageException {
        checkOpened();
        final Graph convertedTripleSet;
        try {
            convertedTripleSet = sesameConversionUtil.convertTripleSetToSesameGraph(tripleSet);
        } catch (Exception e) {
            throw new StorageException("Error while converting data.", e);
        }
        beginTransaction();
        try {
            repositoryConnection.add(convertedTripleSet, valueFactory.createURI(graph));
        } catch (RepositoryException re) {
            rollbackTransaction();
            throw new StorageException("Error while storing triple set.", re);
        }
        commitTransaction();
    }

    public void store(TripleSet tripleSet) throws StorageException {
        store( virtuosoConfig.getDefaultGraphName(), tripleSet );
    }

    public void remove(String graph, TripleSet tripleSet) throws StorageException {
        checkOpened();
        final Graph toBeRemoved = sesameConversionUtil.convertTripleSetToSesameGraph(tripleSet);
        beginTransaction();
        try {
            repositoryConnection.remove(toBeRemoved, valueFactory.createURI(graph));
        } catch (RepositoryException re) {
            rollbackTransaction();
            throw new StorageException("Error while removing graph.", re);
        }
        commitTransaction();
    }

    public void remove(TripleSet tripleSet) throws StorageException {
        remove( virtuosoConfig.getDefaultGraphName(), tripleSet );
    }

    public TripleIterator getTriplesIterator(TripleStorageFilter tripleFilter)
    throws StorageException {
        return getTriplesIterator( virtuosoConfig.getDefaultGraphName(), tripleFilter );
    }

    public TripleIterator getTriplesIterator(String graph, TripleStorageFilter tripleFilter)
    throws StorageException {
        final RepositoryResult result = getTriplesInternal(tripleFilter);
        return new InternalTripleIterator(result);
    }

    public TripleSet getTriples(TripleStorageFilter tripleStorageFilter)
    throws StorageException {
        return getTriples( virtuosoConfig.getDefaultGraphName(), tripleStorageFilter );
    }

    public TripleSet getTriples(String graph, TripleStorageFilter tripleStorageFilter)
    throws StorageException {
        final RepositoryResult repositoryResult = getTriplesInternal(tripleStorageFilter);
        final TripleSet result = new TripleBuffer();
        try {
            while (repositoryResult.hasNext()) {
                result.addTriple(
                        sesameConversionUtil.convertSesameStatementToTriple( (Statement) repositoryResult.next() )
                );
            }
        } catch (Exception e) {
            throw new StorageException("Error while iterating statements.", e);
        }
        return result;
    }

    public SparqlEndPoint getSparqlEndPoint() {
        checkOpened();
        return new VirtuosoSparqlEndPoint(repositoryConnection);
    }

    public void clear(String graph) throws StorageException {
        checkOpened();
        try {
            repositoryConnection.remove((Resource) null, (URI) null, (Value) null, valueFactory.createURI(graph));
        } catch (RepositoryException re) {
            throw new StorageException("Error while removing graph.", re);
        }
    }

    public void clearAll() throws StorageException {
        final Connection connection = ((VirtuosoRepositoryConnection) repositoryConnection).getQuadStoreConnection();
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
        if(repositoryConnection == null) {
            throw new RuntimeException("The storage must be opened first.");
        }
    }

    private void beginTransaction() throws StorageException {
        try {
            repositoryConnection.setAutoCommit(false);
        } catch (RepositoryException re) {
            throw new StorageException("Error while setting up transaction.", re);
        }
    }

    private void commitTransaction() throws StorageException {
        try {
            repositoryConnection.commit();
        } catch (RepositoryException re) {
            throw new StorageException("Error while committing transaction.", re);
        }
    }

    private void rollbackTransaction() throws StorageException {
        try {
            repositoryConnection.rollback();
        } catch (RepositoryException re) {
            throw new StorageException("Error while rolling back transaction.", re);
        }
    }

    private RepositoryResult getTriplesInternal(TripleStorageFilter tripleStorageFilter)
    throws StorageException {
        checkOpened();
        final Value[] pattern = sesameConversionUtil.convertToTripleMatch(tripleStorageFilter);
        try {
            return repositoryConnection.getStatements(
                    (Resource) pattern[0],
                    (URI) pattern[1],
                    pattern[2],
                    false
            );
        } catch (RepositoryException re) {
            throw new StorageException("Error while matching filter.", re);
        }
    }

    private class InternalTripleIterator implements TripleIterator {

        private final RepositoryResult result;

        private Triple next;

        private InternalTripleIterator(RepositoryResult result) {
            this.result = result;
        }

        public boolean hasNext() {
            try {
                return result.hasNext();
            } catch (RepositoryException re) {
                throw new RuntimeException("Error while moving result cursor.", re);
            }
        }

        public void next() {
            try {
                next = sesameConversionUtil.convertSesameStatementToTriple(
                        (Statement) result.next()
                );
            } catch (Exception e) {
                throw new RuntimeException("Error while retrieving next statement.", e);
            }
        }

        public String subject() {
            return next.getSubject();
        }

        public String predicate() {
            return next.getPredicate();
        }

        public Object object() {
            return next.getObject();
        }

        public String objectStr() {
            return next.getObjectAsString();
        }

        public boolean isBlankSubject() {
            return next.isSubjectBNode();
        }

        public boolean isBlankObject() {
            return next.isObjectBNode();
        }

        public boolean isObjLiteral() {
            return next.isObjectLiteral();
        }
    }

}
