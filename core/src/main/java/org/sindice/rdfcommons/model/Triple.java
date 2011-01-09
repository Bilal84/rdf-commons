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

/**
 * Models an <i>RDF</i> triple.
 *
 * @param <O> the triple object type.
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public interface Triple<O> extends Quad {

    /**
     * Defines the possible triple subject types.
     */
    public enum SubjectType {
        bnode,
        uri
    }

    /**
     * Defines the possible triple object types.
     */
    public enum ObjectType {
        bnode,
        uri,
        literal
    }

    /**
     * @return the triple subject.
     * @see #getSubjectType()
     */
    String getSubject();

    /**
     * @return the triple predicate.
     */
    String getPredicate();

    /**
     * Returns the triple object.
     * If the object type is not a literal then a
     * {@link String} is expected to be returned.
     *
     * @return the triple object.
     * @see #getObjectType()
     */
    O getObject();

    /**
     * @return the subject type of the triple.
     */
    SubjectType getSubjectType();

    /**
     * @return the object type of the triple.
     */
    ObjectType getObjectType();

    /**
     * @return <code>true</code> if the subject is a bnode.
     */
    boolean isSubjectBNode();

    /**
     * @return <code>true</code> if the object is a literal.
     */
    boolean isObjectLiteral();

    /**
     * @return <code>true</code> if the object is a literal.
     */
    boolean isObjectBNode();

    /**
     * @return returns the triple literal datatype if defined,
     *         <code>null</code> otherwise.
     */
    String getLiteralDatatype();

    /**
     * @return the triple literal language if defined,
     *         <code>null</code> otherwise.
     */
    String getLiteralLanguage();

    /**
     * @return the object with its object representation.
     */
    String getObjectAsString();

    /**
     * @return the <i>N-Triples</i> representation for this triple.
     */
    String toNTriplesString();

}
