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

package org.sindice.rdfcommons.storage.virtuoso;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import org.sindice.rdfcommons.storage.ResultSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Defines the virtuoso result set.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id$
 */
public class VirtuosoResultSet implements ResultSet {

    private com.hp.hpl.jena.query.ResultSet resultSet;

    private QuerySolution solution;

    private String[] variables;

    protected VirtuosoResultSet(com.hp.hpl.jena.query.ResultSet rs) {
        resultSet = rs;
    }

    public boolean hasNext() {
        return resultSet.hasNext();
    }

    public void next() {
        solution = resultSet.nextSolution();
    }

    public Object getVariableValue(String var) {
        RDFNode valueNode = solution.get(var);
        if(valueNode.isLiteral()) {
            return ((Literal) valueNode).getValue();
        }
        return valueNode.toString();
    }

    public String getVariableString(String var) {
        return solution.get(var).toString();
    }

    public VariableType getVariableType(String var) {
        RDFNode node = solution.get(var);
        if(node.isAnon()) {
            return VariableType.BLANK;
        }
        if(node.isResource()) {
            return VariableType.RESOURCE;
        }
        if(node.isLiteral()) {
            return VariableType.LITERAL;
        }
        throw new IllegalArgumentException("Invalid node: " + node);
    }

    public String[] getVariables() {
        if(variables == null) {
            List<String> vars = new ArrayList<String>();
            Iterator varIter = solution.varNames();
            while (varIter.hasNext()) {
                vars.add( (String) varIter.next() );
            }
            variables = vars.toArray( new String[ vars.size() ] );
        }
        return variables;
    }
}
