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

import org.sindice.rdfcommons.model.DatatypeLiteral;

/**
 * Default implementation of {@link org.sindice.rdfcommons.model.DatatypeLiteral}.
 */
public class DefaultDatatypeLiteral<T> implements DatatypeLiteral<T> {

    private final String datatype;
    private final T value;

    public DefaultDatatypeLiteral(String datatype, T value) {
        if(datatype == null) {
            throw new NullPointerException("datatype cannot be null.");
        }
        if(value == null) {
            throw new NullPointerException("value cannot be null.");
        }
        this.datatype = datatype;
        this.value = value;
    }

    public String getDatatype() {
        return datatype;
    }

    public T getTypedValue() {
        return value;
    }

    public String getValue() {
        return value.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(obj == this) {
            return true;
        }
        if(obj instanceof DatatypeLiteral) {
            final DatatypeLiteral other = (DatatypeLiteral) obj;
            return datatype.equals(other.getDatatype()) && value.equals(other.getTypedValue());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return datatype.hashCode() * value.hashCode() * 2;
    }
}
