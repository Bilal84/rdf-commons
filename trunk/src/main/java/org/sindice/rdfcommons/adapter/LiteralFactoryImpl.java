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

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of {@link LiteralFactory}.
 */
public class LiteralFactoryImpl<L> implements LiteralFactory<L> {

    private final List<LiteralFactory<L>> literalFactories = new ArrayList<LiteralFactory<L>>();

    public LiteralFactoryImpl() {}

    /**
     * Checks if a literal factory is already registered.
     *
     * @param lf factory to be found.
     * @return <code>true</code> if contained, <code>false</code> otherwise.
     */
    public boolean contains(LiteralFactory lf) {
        return literalFactories.contains(lf);
    }

    public boolean addLiteralFactory(LiteralFactory<L> literalFactory) {
        return literalFactories.add(literalFactory);
    }

    public boolean removeLiteralFactory(LiteralFactory literalFactory) {
        return literalFactories.remove(literalFactory);
    }

    public String getName() {
        return this.getClass().getName();
    }

    public boolean supportDatatype(String datatype) {
        for(LiteralFactory lf : literalFactories) {
            if(lf.supportDatatype(datatype)) {
                return true;
            }
        }
        return false;
    }

    public L createLiteral(String datatype, String value) throws LiteralFactoryException {
        for(LiteralFactory<L> lf : literalFactories) {
            if(lf.supportDatatype(datatype)) {
                try {
                return lf.createLiteral(datatype, value);
                } catch (Exception e) {
                    throw new LiteralFactoryException(
                        String.format(
                                "Error while applying factory '%s' to datatype '%s' with value '%s'",
                                lf.getName(), datatype, value
                        ),
                        e
                    );
                }
            }
        }
        throw new LiteralFactoryException("Datatype '" + datatype + "' is not supported.");
    }
}
