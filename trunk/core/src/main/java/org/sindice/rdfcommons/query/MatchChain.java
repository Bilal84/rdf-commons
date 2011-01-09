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
 * Defines a chain of {@link Match}es.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class MatchChain extends Query {

    private List<Match> matches;

    protected MatchChain(QueryContext qc) {
        super(qc);
        matches = new ArrayList<Match>();
    }

    /**
     * Converts the chain query in <i>SPARQL</i>.
     *
     * @return SPARQL query.
     */
    @Override
    protected String toSparqlTriplePattern() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * WHERE {");
        for(int i = 0; i < matches.size(); i++) {
            sb.append( matches.get(i).toSparqlTriplePattern() );
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    protected String[] getVars() {
        List<String> vars = new ArrayList<String>();
        for(Match m : matches) {
            for(String matchVar : m.getVars()) {
                if( ! vars.contains(matchVar) ) {
                    vars.add(matchVar);
                }
            }
        }
        return vars.toArray( new String[vars.size()] );
    }

    protected void add(Match m) {
        matches.add(m);
    }

    protected void remove(Match m) {
        matches.remove(m);
    }

    protected int size() {
        return matches.size();
    }

    protected Match get(int i) {
        return matches.get(i);
    }

    protected void clear() {
        matches.clear();
    }

    protected boolean isEmpty() {
        return matches.isEmpty();
    }

    protected Match[] getMatches() {
        return matches.toArray( new Match[matches.size()] );
    }

}
