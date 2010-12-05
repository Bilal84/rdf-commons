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

import org.sindice.rdfcommons.model.Triple.*;

import java.io.PrintStream;
import java.util.List;

/**
 * Defines a triple container.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public interface TripleSet extends Iterable<Triple> {

    /**
     * Adds a triple to the triple set.
     *
     * @param sub the URI triple subject.
     * @param pred the URI triple predicate.
     * @param obj the URI triple object
     */
     <O> void addTriple(String sub, String pred, O obj);

    /**
     * Adds a triple to the triple set with object literal.
     *
     * @param sub the URI triple subject.
     * @param pred the URI triple predicate.
     * @param obj the triple object, of type declared by {#objectType}.
     * @param objectType the object type.
     */
     <O> void addTriple(String sub, String pred, O obj, ObjectType objectType);

    /**
     * Adds a blank subject triple with explicit value.
     *
     * @param sub the subject URI or identifier.
     * @param pred the predicate string URI.
     * @param obj the object URI or literal.
     * @param subjectType the type of the subject.
     * @param objectType the type of the object.
     */
    <O> void addTriple(String sub, String pred, O obj, SubjectType subjectType, ObjectType objectType);

    /**
     * Adds a triple to the set.
     *
     * @param t triple to add.
     */
    void addTriple(Triple t);

    /**
     * Removes a triple from the set.
     *
     * @param t triple to remove.
     */
    void removeTriple(Triple t);

    /**
     * Adds a triple with blank subject.
     *
     * @param pred the predicate URI.
     * @param obj the object string.
     * @param objectType the type of the object.
     * @return the blank subject.
     */
    <O> String addBNodeSubjectTriple(String pred, O obj, ObjectType objectType);

    /**
     * Adds a triple with blank object.
     *
     * @param sub
     * @param pred
     * @param subjectType
     * @return the blank object.
     */
    String addBNodeObjectTriple(String sub, String pred, SubjectType subjectType);

    /**
     * Removes a triple from the triple set.
     *
     * @param sub
     * @param pred
     * @param obj
     */
    <O> void removeTriple(String sub, String pred, O obj);

    /**
     * Removes a triple from the triple set with object literal.
     *
     * @param sub
     * @param pred
     * @param obj
     * @param objectType
     */
    <O> void removeTriple(String sub, String pred, O obj, ObjectType objectType);

    /**
     * Checks if a triple is contained in the triple set.
     *
     * @param sub
     * @param pred
     * @param obj
     * @param subjectType
     * @param objectType
     * @return <code>true</code> if contained, <code>false</code> otherwise.
     */
    <O> boolean containsTriple(String sub, String pred, O obj, SubjectType subjectType, ObjectType objectType);

    /**
     * Checks if a triple pattern is contained in the triple set.
     * 
     * @param sub
     * @param pred
     * @param obj
     * @param subjectType
     * @param objectType
     * @param <O>
     * @return
     */
    <O> boolean containsTriplePattern(
            String sub, String pred, O obj,
            SubjectType subjectType, ObjectType objectType
    );

    /**
     * Returns a new triple set of triples matching the given pattern.
     *
     * @param sub
     * @param pred
     * @param obj
     * @param subjectType
     * @param objectType
     * @param <O>
     * @return
     */
    <O> TripleSet getTriplesWithPattern(
            String sub, String pred, O obj,
            SubjectType subjectType, ObjectType objectType
    );

    /**
     * Returns the i-th triple.
     *
     * @param i i-th triple
     * @return the triple to be returned.
     */
    Triple getTriple(int i);

    /**
     * Returns the triple set iterator.
     *
     * @return an iterator on the triples in the set.
     */
    TripleIterator tripleIterator();

    /**
     * Returns all the triples in the buffer.
     *
     * @return list of triples in buffer.
     */
    List<Triple> getTriples();

    /**
     * Returns all the triples in the buffer matching the filtering criteria.
     *
     * @param filter filter to be applied.
     * @return filtered triples.
     */
    TripleSet getTriples(TripleFilter filter);

    /**
     * Adds the content of a triple set in another triple set.
     * 
     * @param ts the triples to be added.
     */
    void add(TripleSet ts);

    /**
     * Removes the content of the input triple set from this triple set.
     *
     * @param ts the triples to be removed.
     */
    void remove(TripleSet ts);

    /**
     * @return the size of this buffer.
     */
    int getSize();

    /**
     * Clears the content of this buffer.
     */
    void clear();

    /**
     * Dumps the content of the set in the given print stream.
     *
     * @param ps print stream to dump content.
     */
    void dumpContent(PrintStream ps);
}
