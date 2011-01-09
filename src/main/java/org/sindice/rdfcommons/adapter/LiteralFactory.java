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

package org.sindice.rdfcommons.adapter;

/**
 * Defines a factory able to create instantiate literals.
 *
 * @param <L> type of created literals.
 */
public interface LiteralFactory<L> {

    /**
     * @return the identified of this factory.
     */
    String getName();

    /**
     * Verifies if this factory supports the specified datatype.
     *
     * @param datatype datatype to be verified.
     * @return <code>true</code> if supported, <code>false</code> otherwise.
     */
    boolean supportDatatype(String datatype);

    /**
     *  Creates a new literal instance.
     *
     * @param datatype literal datatype.
     * @param value literal value.
     * @return the literal instance.
     * @throws LiteralFactoryException if an error occurs during the literal instantiation.
     */
    L createLiteral(String datatype, String value) throws LiteralFactoryException;

}