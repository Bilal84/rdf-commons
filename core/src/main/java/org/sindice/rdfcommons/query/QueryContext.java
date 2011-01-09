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

package org.sindice.rdfcommons.query;

import java.util.HashSet;
import java.util.Set;

/**
 * Context class for building query elements.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class QueryContext {

    private static final String VAR_PREFIX = "v";

    private int counter = 0;

    private Set<String> definedVars;

    protected QueryContext() {
        definedVars = new HashSet<String>();
    }

    protected String nextVariable() {
        String var = String.format("?%s%d", VAR_PREFIX, counter++);
        definedVars.add(var);
        return var;
    }

    protected boolean variableAlreadyDefined(String var) {
        return definedVars.contains(var);
    }

}
