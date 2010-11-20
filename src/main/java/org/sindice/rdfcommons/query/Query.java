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

/**
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public abstract class Query {

    private QueryContext context;

    protected Query(QueryContext qc) {
        context = qc;
    }

    protected abstract String toSparqlTriplePattern();

    protected abstract String[] getVars();

    protected QueryContext getQueryContext() {
        return context;
    }

    protected String prepareVariable(String in) {
        if( in == null ) {
            return context.nextVariable();
        } else {
            String ret = in.trim();
            checkVariable(ret);
            return ret;
        }
    }

    protected boolean isVariable(String var) {
        return var.charAt(0) == '?';
    }

    protected void checkVariable(String var) {
        if(isVariable(var) && context.variableAlreadyDefined(var)) {
            throw new IllegalArgumentException( String.format("Variable '%s' already defined.", var)  );
        }
    }


}
