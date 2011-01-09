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

import java.util.ArrayList;
import java.util.List;

/**
 * Defines a match over a triple set.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class Match extends Query {

    private final String sub;
    private final String pred;
    private final String obj;

    protected Match(QueryContext context, String sub, String pred, String obj) {
        super(context);
        this.sub  = prepareVariable(sub);
        this.pred = prepareVariable(pred);
        this.obj  = prepareVariable(obj);
    }

    public String getSub() {
        return sub;
    }

    public String getPred() {
        return pred;
    }

    public String getObj() {
        return obj;
    }

    public boolean isSubVar() {
        return isVariable(sub);
    }

    public boolean isPredVar() {
        return isVariable(pred);
    }

    public boolean isObjVar() {
        return isVariable(obj);
    }

    public int getVarIndex(String v) {
        if(! isVariable(v)) {
            throw new IllegalArgumentException();
        }
        if(sub.equals(v)) {
            return 0;
        }
        if(pred.equals(v)) {
            return 1;
        }
        if(obj.equals(v)) {
            return 2;
        }
        return -1;
    }

    @Override
    public String toSparqlTriplePattern() {
        return String.format("{%s %s %s}", sub, pred, obj);
    }

    protected String[] getVars() {
        List<String> vars = new ArrayList<String>(3);
        if(isVariable(sub)) {
            vars.add(sub);
        }
        if(isVariable(pred)) {
            vars.add(pred);
        }
        if(isVariable(obj)) {
            vars.add(obj);
        }
        return vars.toArray( new String[vars.size()] );
    }

    @Override
    public String toString() {
        return String.format("%s{%s,%s,%s}", this.getClass(), sub, pred, obj);
    }
}
