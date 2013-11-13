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

package org.sindice.rdfcommons.storage.virtuoso.sesame;

import org.openrdf.model.BNode;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.sindice.rdfcommons.adapter.LiteralFactoryException;
import org.sindice.rdfcommons.model.Literal;
import org.sindice.rdfcommons.storage.ResultSet;

import java.util.Set;

public class VirtuosoResultSet implements ResultSet {

        private final TupleQueryResult queryResult;

        private BindingSet bindingSet;

        protected VirtuosoResultSet(TupleQueryResult queryResult) {
            this.queryResult = queryResult;
        }

        public boolean hasNext() {
            try {
                return queryResult.hasNext();
            } catch (QueryEvaluationException qee) {
                throw new RuntimeException("Error while checking query result.", qee);
            }
        }

        public void next() {
            try {
                bindingSet = queryResult.next();
            } catch (QueryEvaluationException qee) {
                throw new RuntimeException("Error while moving to next result.", qee);
            }
        }

        public Object getVariableValue(String s) {
            try {
                return VirtuosoSparqlEndPoint.SESAME_CONVERSION_UTIL.convertValueToObject( bindingSet.getValue(s) );
            } catch (LiteralFactoryException lfe) {
                throw new RuntimeException("Error while converting binding to object.", lfe);
            }
        }

        public String getVariableString(String s) {
            return bindingSet.getValue(s).stringValue();
        }

        public VariableType getVariableType(String s) {
            final Value v = bindingSet.getValue(s);
            if(v instanceof URI) {
                return VariableType.RESOURCE;
            }
            if(v instanceof BNode) {
                return VariableType.BLANK;
            }
            if(v instanceof Literal) {
                return VariableType.LITERAL;
            }
            throw new IllegalStateException();
        }

        public String[] getVariables() {
            final Set<String> bindingNames = bindingSet.getBindingNames();
            return bindingNames.toArray( new String[bindingNames.size()] );
        }
    }