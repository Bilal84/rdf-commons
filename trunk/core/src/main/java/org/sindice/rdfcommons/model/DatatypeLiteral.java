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
 * Declares any typed
 * {@link org.sindice.rdfcommons.model.Triple} object literal.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface DatatypeLiteral<T> extends Literal {

    /**
     * Returns the literal datatype.
     *
     * @return a valid <i>URI</i> representing a literal type.
     */
    String getDatatype();

    /**
     * Returns the original typed value.
     *
     * @return the datatype original value.
     */
    T getTypedValue();

}
