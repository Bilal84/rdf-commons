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

package org.sindice.rdfcommons.storage;

/**
 * Defines the result for a <i>SPARQL</i> select query.
 *
 * @see org.sindice.rdfcommons.storage.SparqlEndPoint
 * @see org.sindice.rdfcommons.storage.SparqlEndPoint#processSelectQuery(String)
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public interface ResultSet {

    enum VariableType{
        RESOURCE,
        BLANK,
        LITERAL
    }

    /**
     * @return <code>true</code> if there is another result in the result set,
     *         <code>false</code> otherwise.
     */
    boolean hasNext();

    /**
     * Moved to the next result.
     */
    void next();

    /**
     * Retrieves the value of the given variable for the current result. 
     *
     * @param var
     * @return the value of variable.
     */
    Object getVariableValue(String var);

    /**
     * Retrieves the value of the given variable as string.
     *
     * @param var
     * @return the value of variable.
     */
    String getVariableString(String var);

    /**
     * Returns the type of variable in the result set.
     *
     * @param var
     * @return variable type. 
     */
    VariableType getVariableType(String var);

    /**
     * Returns the variables defined in the query. 
     *
     * @return list of variables used in the query.
     */
    String[] getVariables();

}
