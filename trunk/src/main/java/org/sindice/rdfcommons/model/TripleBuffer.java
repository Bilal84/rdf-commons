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

package org.sindice.rdfcommons.model;

import org.sindice.rdfcommons.model.Triple.ObjectType;
import org.sindice.rdfcommons.model.Triple.SubjectType;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Defines a buffer of {@link Triple}s.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class TripleBuffer implements TripleSet {

    private final List<Triple> triples;

    public TripleBuffer() {
        triples = new ArrayList<Triple>();
    }

    public TripleBuffer(List<Triple> triplesList) {
        if(triplesList instanceof ArrayList) {
            triples = triplesList;
        } else {
            triples = new ArrayList<Triple>( triplesList );
        }
    }

    public TripleBuffer(Triple... triples) {
        this( Arrays.asList(triples) );
    }

    public <O> void addTriple(String sub, String pred, O obj, String graph) {
        internalAdd( new TripleImpl<O>(sub, pred, obj, graph) );
    }

    public <O> void addTriple(String sub, String pred, O obj) {
        internalAdd( new TripleImpl<O>(sub, pred, obj) );
    }

    public <O> void addTriple(String sub, String pred, O obj, ObjectType objectType, String graph) {
        internalAdd( new TripleImpl<O>(sub, pred, obj, objectType, graph) );
    }

    public <O> void addTriple(String sub, String pred, O obj, ObjectType objectType) {
        internalAdd( new TripleImpl<O>(sub, pred, obj, objectType) );
    }

    public <O> void addTriple(
            String sub, String pred, O obj, SubjectType subjectType, ObjectType objectType, String graph
    ) {
        internalAdd( new TripleImpl<O>(sub, pred, obj, objectType, graph) );
    }

    public <O> void addTriple(
            String sub, String pred, O obj,
            SubjectType subjectType, ObjectType objectType
    ) {
        internalAdd( new TripleImpl<O>(sub, pred, obj, subjectType, objectType) );
    }

    public void addTriple(Triple triple) {
        if(triple == null) {
            throw new NullPointerException("triple cannot be null");
        }
        internalAdd(triple);
    }

    public void removeTriple(Triple triple) {
        if(triple == null) {
            throw new NullPointerException("triple cannot be null");
        }
        internalRemove(triple);
    }

    public <O> String addBNodeSubjectTriple(String pred, O obj, ObjectType objectType, String graph) {
        Triple triple = TripleImpl.createBNodeSubjectTriple(pred, obj, objectType, graph);
        internalAdd(triple);
        return triple.getSubject();
    }

    public <O> String addBNodeSubjectTriple(String pred, O obj, ObjectType objectType) {
        return addBNodeSubjectTriple(pred, obj, objectType, null);
    }

    public String addBNodeObjectTriple(String sub, String pred, SubjectType subjectType, String graph) {
         Triple triple = TripleImpl.createBNodeObjectTriple(sub, pred, subjectType, graph);
        internalAdd(triple);
        return triple.getObjectAsString();
    }

    public String addBNodeObjectTriple(String sub, String pred, SubjectType subjectType) {
        return addBNodeObjectTriple(sub, pred, subjectType, null);
    }

    public <O> void removeTriple(String sub, String pred, O obj, String graph) {
        triples.remove( new TripleImpl<O>(sub, pred, obj, graph)  );
    }

    public <O> void removeTriple(String sub, String pred, O obj) {
        triples.remove( new TripleImpl<O>(sub, pred, obj)  );
    }

    public <O> void removeTriple(String sub, String pred, O obj, ObjectType objectType) {
        triples.remove( new TripleImpl<O>(sub, pred, obj, objectType)  );
    }

    public <O> boolean containsTriple(
            String sub, String pred, O obj, SubjectType subjectType, ObjectType objectType, String graph
    ) {
        return triples.contains( new TripleImpl<O>(sub, pred, obj, objectType) );
    }

    public <O> boolean containsTriple(String sub, String pred, O obj) {
        return triples.contains( new TripleImpl<O>(sub, pred, obj) );
    }

    public <O> boolean containsTriple(String sub, String pred, O obj, ObjectType objectType) {
        return triples.contains( new TripleImpl<O>(sub, pred, obj, objectType)  );
    }

    public <O> boolean containsTriple(
            String sub, String pred, O obj,
            SubjectType subjectType, ObjectType objectType
    ) {
        return triples.contains( new TripleImpl<O>(sub, pred, obj, subjectType, objectType) );
    }

    public <O> boolean containsTriplePattern(
            String sub, String pred, O obj, SubjectType subjectType, ObjectType objectType, String graph
    ) {
       for(Triple triple : triples) {
            if(matchPattern(triple, sub, pred, obj, subjectType, objectType, graph)) {
                return true;
            }
        }
        return false;
    }

    public <O> boolean containsTriplePattern(
            String sub, String pred, O obj,
            SubjectType subjectType, ObjectType objectType
    ) {
        return containsTriplePattern(sub, pred, obj, subjectType, objectType, null);
    }

    public <O> TripleSet getTriplesWithPattern(
            String sub, String pred, O obj, SubjectType subjectType, ObjectType objectType, String graph
    ) {
       final TripleBuffer result = new TripleBuffer();
        for(Triple triple : triples) {
            if(matchPattern(triple, sub, pred, obj, subjectType, objectType, graph)) {
                result.addTriple(triple);
            }
        }
        return result;
    }

    public <O> TripleSet getTriplesWithPattern(
            String sub, String pred, O obj,
            SubjectType subjectType, ObjectType objectType
    ) {
        return getTriplesWithPattern(sub, pred, obj, subjectType, objectType, null);
    }

    public Triple getTriple(int i) {
        return triples.get(i);
    }

    public void add(TripleSet ts) {
        for(Triple triple : ts) {
            addTriple(triple);
        }
    }

    public void remove(TripleSet ts) {
        for(Triple triple : ts) {
            removeTriple(triple);
        }
    }

    public int getSize() {
        return triples.size();
    }

    public void clear() {
        triples.clear();
    }

    public TripleIterator tripleIterator() {
        return new TripleIteratorImpl( triples.iterator() );
    }

    public Iterator<Triple> iterator() {
        return triples.iterator();
    }

    public List<Triple> getTriples() {
        return Collections.unmodifiableList(triples);
    }

    public TripleSet getTriples(TripleFilter filter) {
        List<Triple> result = new ArrayList<Triple>();
        for(Triple triple : triples) {
            if( filter.acceptTriple(triple) ) {
                result.add(triple);
            }
        }
        return new TripleBuffer(result);
    }
    
    public void dumpContent(PrintStream printStream) {
        for(Triple triple : triples) {
            printStream.println( triple );
        }
    }

    @Override
    public String toString() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        dumpContent(ps);
        ps.close();
        return baos.toString();
    }

    protected void internalAdd(Triple triple) {
        triples.add(triple);
    }

    protected void internalRemove(Triple triple) {
        triples.remove(triple);
    }

    private <O> boolean matchPattern(
            Triple triple,
            String sub, String pred, O obj,
            SubjectType subjectType, ObjectType objectType, String graph
    ) {
        return (sub == null || sub.equals(triple.getSubject()))
                &&
                (subjectType == null || subjectType.equals(triple.getSubjectType()))
                &&
                (pred == null || pred.equals(triple.getPredicate()))
                &&
                (obj == null || obj.equals(triple.getObject()))
                &&
                (objectType == null || objectType.equals(triple.getObjectType()))
                &&
                (graph == null || graph.equals(triple.getGraph()));
    }

}
