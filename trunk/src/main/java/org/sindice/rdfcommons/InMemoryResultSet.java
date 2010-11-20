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

package org.sindice.rdfcommons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In memory implementation of {@link ResultSet}.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class InMemoryResultSet implements ResultSet {

    private final String[] variables;

    private final Map<String,Integer> varToIndex;

    private final List<VarEntry[]> data;

    private int index;

    // private String lastVarName;
    // private int    lastVarIndex;

    public InMemoryResultSet(String[] vs) {
        if(vs == null || vs.length == 0) {
            throw new IllegalArgumentException("Invalid vs.");
        }
        variables = Arrays.copyOf(vs, vs.length);
        varToIndex = createVarToIndexMap(vs);
        data = new ArrayList<VarEntry[]>();
    }

    public void addResult(VarEntry ... ve) {
        if(ve.length != variables.length) {
            throw new IllegalArgumentException(
                String.format("Invalid list size of var entries, expected %d, found %d.", variables.length, ve.length)
            );
        }
        data.add(ve);
    }

    public int size() {
        return data.size();
    }

    public void reset() {
        index = 0;
    }

    public void clear() {
        reset();
        data.clear();
    }

    public boolean hasNext() {
        return index < data.size();
    }

    public void next() {
        index++;
    }

    public Object getVariableValue(String var) {
        return data.get(index)[getVarIndex(var)].value;
    }

    public String getVariableString(String var) {
        return (String) getVariableValue(var);
    }

    public VariableType getVariableType(String var) {
        return data.get(index)[ getVarIndex(var) ].type;
    }

    public String[] getVariables() {
        return Arrays.copyOf(variables, variables.length);
    }

    private int getVarIndex(String var) {
        // if( lastVarName != null && lastVarName.equals(var) ) {
        //     return lastVarIndex;
        // }
        Integer i = varToIndex.get(var);
        if(i == null) {
            throw new IllegalArgumentException( String.format("Unknown variable [%s]", var) );
        }
        return i;
    }

    private Map<String,Integer> createVarToIndexMap(String[] vs) {
        Map<String,Integer> map = new HashMap<String, Integer>();
        for(int i = 0; i < vs.length; i++) {
            map.put(vs[i], i);
        }
        return map;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( Arrays.toString(variables) );
        int headerLength = sb.length();
        sb.append("\n");
        for(int i = 0; i < headerLength; i++) {
            sb.append("-");
        }
        sb.append("\n");
        for(VarEntry[] ves : data) {
            for(VarEntry ve : ves) {
                sb.append(ve).append("\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static class VarEntry {

        Object       value;
        VariableType type;

        public VarEntry(Object vn, VariableType vt) {
            value = vn;
            type  = vt;
        }

        public VarEntry(String vn) {
            this(vn, VariableType.RESOURCE);
        }

        public Object getValue() {
            return value;
        }

        public VariableType getType() {
            return type;
        }

        @Override
        public String toString() {
            return String.format("%s[%s]", value, type);
        }
    }

}
