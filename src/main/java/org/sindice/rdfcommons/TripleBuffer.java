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

package org.sindice.rdfcommons;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Defines a buffer of triples.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class TripleBuffer implements TripleSet {

    private List<Triple> triples;

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

    public void addTriple(String sub, String pred, Object obj) {
        internalAdd( new Triple(sub, pred, obj) );
    }

    public void addTriple(String sub, String pred, Object obj, boolean literal) {
        internalAdd( new Triple(sub, pred, obj, literal) );
    }

    public void addTriple(String sub, boolean bsub, String pred, Object obj, boolean literal, boolean bobj) {
        internalAdd( new Triple(sub, pred, obj, literal, bsub, bobj) );
    }

    public String addBlankSubjectTriple(String pred, Object obj, boolean literal, boolean bobj) {
        Triple triple = Triple.createBNodeSubjectTriple(pred, obj, literal, bobj);
        internalAdd(triple);
        return triple.getSubject();
    }

    public String addBlankObjectTriple(String sub, boolean blank, String pred) {
        Triple triple = Triple.createBNodeObjectTriple(sub, blank, pred);
        internalAdd(triple);
        return triple.getObjectAsString();
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

    public void removeTriple(String sub, String pred, Object obj, boolean literal) {
        triples.remove( new Triple(sub, pred, obj, literal)  );
    }

    public void removeTriple(String sub, String pred, Object obj) {
        triples.remove( new Triple(sub, pred, obj)  );
    }

    public boolean containsTriple(String sub, String pred, Object obj,  boolean literal, boolean blankSub, boolean blankObj) {
        return triples.contains( new Triple(sub, pred, obj, literal, blankSub, blankObj) );
    }

    public boolean containsTriple(String sub, String pred, Object obj, boolean literal) {
        return triples.contains( new Triple(sub, pred, obj, literal)  );
    }

    public boolean containsTriple(String sub, String pred, Object obj) {
        return triples.contains( new Triple(sub, pred, obj)  );
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

}
