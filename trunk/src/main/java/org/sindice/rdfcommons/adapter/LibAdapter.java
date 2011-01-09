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
 * Interface modeling an adapter class
 * using the {@link LiteralFactory}.
 */
public interface LibAdapter<LIN, LOUT> {

    /**
     * Sets the literal factory for this adapter.
     *
     * @param lf not <code>null</code> literal factory.
     * @throws NullPointerException if <code>lf</code> is <code>null</code>.
     */
    void setLiteralFactory(LiteralFactory lf) throws NullPointerException;

    /**
     * @return the literal factory currently configured for this adapter.
     */
    LiteralFactory getLiteralFactory();

    /**
     * Converts an object literal type to another type.
     *
     * @param literal input library literal.
     * @return outout converted literal.
     * @throws LiteralFactoryException
     */
    LOUT getObjectLiteral(LIN literal) throws LiteralFactoryException;
}
