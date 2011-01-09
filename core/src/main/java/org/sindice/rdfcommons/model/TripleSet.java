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
     * Adds a triple to the triple-set.
     *
     * @param sub the URI triple subject.
     * @param pred the URI triple predicate.
     * @param obj the URI triple object
     * @param graph the graph graph for the triple.
     * @param <O> the triple object type.
     */
     <O> void addTriple(String sub, String pred, O obj, String graph);

    /**
     * Adds a triple to the triple-set with default graph.
     *
     * @param sub the URI triple subject.
     * @param pred the URI triple predicate.
     * @param obj the URI triple object.
     * @param <O> the triple object type.
     */
     <O> void addTriple(String sub, String pred, O obj);

    /**
     * Adds a triple to the triple-set with object literal.
     *
     * @param sub the URI triple subject.
     * @param pred the URI triple predicate.
     * @param obj the triple object, of type declared by {#objectType}.
     * @param objectType the object type.
     * @param graph the graph context for the triple.
     * @param <O> the triple object type.
     */
     <O> void addTriple(String sub, String pred, O obj, ObjectType objectType, String graph);

    /**
     * Adds a triple to the triple-set with object literal with default graph.
     *
     * @param sub the URI triple subject.
     * @param pred the URI triple predicate.
     * @param obj the triple object, of type declared by {#objectType}.
     * @param objectType the object type.
     * @param <O> the triple object type.
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
     * @param graph the graph context of the triple.
     * @param <O> the triple object type.
     */
    <O> void addTriple(String sub, String pred, O obj, SubjectType subjectType, ObjectType objectType, String graph);

    /**
     * Adds a blank subject triple with explicit value and default graph.
     *
     * @param sub the subject URI or identifier.
     * @param pred the predicate string URI.
     * @param obj the object URI or literal.
     * @param subjectType the type of the subject.
     * @param objectType the type of the object.
     * @param <O> the triple object type.
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
     * @param graph the graph context of the triple.
     * @param <O> the triple object type.
     * @return the blank subject.
     */
    <O> String addBNodeSubjectTriple(String pred, O obj, ObjectType objectType, String graph);

    /**
     * Adds a triple with blank subject and default graph.
     *
     * @param pred the predicate URI.
     * @param obj the object string.
     * @param objectType the type of the object.
     * @param <O> the triple object type.
     * @return the blank subject.
     */
    <O> String addBNodeSubjectTriple(String pred, O obj, ObjectType objectType);

    /**
     * Adds a triple with blank object.
     *
     * @param sub the triple subject.
     * @param pred the triple predicate.
     * @param subjectType the subject type.
     * @param graph graph context for the triple.
     * @return the blank object identifier.
     */
    String addBNodeObjectTriple(String sub, String pred, SubjectType subjectType, String graph);

    /**
     * Adds a triple with blank object and default graph.
     *
     * @param sub the triple subject.
     * @param pred the triple predicate.
     * @param subjectType the subject type.
     * @return the blank object identifier.
     */
    String addBNodeObjectTriple(String sub, String pred, SubjectType subjectType);

    /**
     * Removes a triple from the triple set.
     *
     * @param sub the triple subject.
     * @param pred the triple predicate.
     * @param obj the triple object.
     * @param graph the triple graph.
     * @param <O> the triple object type.
     */
    <O> void removeTriple(String sub, String pred, O obj, String graph);

    /**
     * Removes a triple from the triple set with default graph.
     *
     * @param sub the triple subject.
     * @param pred the triple predicate.
     * @param obj the triple object.
     * @param <O> the triple object type.
     */
    <O> void removeTriple(String sub, String pred, O obj);

    /**
     * Removes a triple from the triple set with object literal.
     *
     * @param sub the triple subject.
     * @param pred the triple predicate.
     * @param obj the triple object.
     * @param objectType the type of the object.
     */
    <O> void removeTriple(String sub, String pred, O obj, ObjectType objectType);

    /**
     * Checks if a triple is contained in the triple set.
     *
     * @param sub the triple subject.
     * @param pred the triple predicate.
     * @param obj the triple object.
     * @param subjectType the subject type.
     * @param objectType the type of the object.
     * @param graph the triple graph.
     * @param <O> the triple object type.
     * @return <code>true</code> if contained, <code>false</code> otherwise.
     */
    <O> boolean containsTriple(
            String sub, String pred, O obj, SubjectType subjectType, ObjectType objectType, String graph
    );

    /**
     * Checks if a triple is contained in the triple set in default graph.
     *
     * @param sub the triple subject.
     * @param pred the triple predicate.
     * @param obj the triple object.
     * @param subjectType the subject type.
     * @param objectType the type of the object.
     * @param <O> the triple object type.
     * @return <code>true</code> if contained, <code>false</code> otherwise.
     */
    <O> boolean containsTriple(
            String sub, String pred, O obj, SubjectType subjectType, ObjectType objectType
    );

    /**
     * Checks if a triple pattern is contained in the triple set.
     * 
     * @param sub the triple subject, if <code>null</code> matches any value.
     * @param pred the triple predicate, if <code>null</code> matches any value.
     * @param obj the triple object, if <code>nul;</code> matches any value.
     * @param subjectType the subject type, if <code>null</code> matches any value.
     * @param objectType the type of the object, if <code>null</code> matches any value.
     * @param graph the triple graph, if <code>null</code> matches any value.
     * @param <O> the triple object type.
     * @return <code>true</code> if pattern is detected, <code>false</code> otherwise.
     */
    <O> boolean containsTriplePattern(
            String sub, String pred, O obj,
            SubjectType subjectType, ObjectType objectType,
            String graph
    );

    /**
     * Returns a new triple set of triples matching the given pattern.
     *
     * @param sub the triple subject, if <code>null</code> matches any value.
     * @param pred the triple predicate, if <code>null</code> matches any value.
     * @param obj the triple object, if <code>null</code> matches any value.
     * @param subjectType the subject type, if <code>null</code> matches any value.
     * @param objectType the type of the object, if <code>null</code> matches any value.
     * @param graph the triple graph, if <code>null</code> matches any value.
     * @param <O> the triple object type.
     * @return a triple set with the matching triples.
     */
    <O> TripleSet getTriplesWithPattern(
            String sub, String pred, O obj,
            SubjectType subjectType, ObjectType objectType,
            String graph
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
