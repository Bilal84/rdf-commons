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

package org.sindice.rdfcommons.beanmapper;

import org.sindice.rdfcommons.model.DatatypeLiteral;

/**
 * Fake bean used to test serialization and deserialization
 * of typed literals.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class FakeDatatypeLiteral implements DatatypeLiteral {

    public String getDatatype() {
        return "http://sindice.com/test#fake-type";
    }

    public Object getTypedValue() {
        return this;
    }

    public String getValue() {
        return "fake value";
    }

    @Override
     public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(obj == this) {
            return true;
        }
        if(obj instanceof FakeDatatypeLiteral) {
            final FakeDatatypeLiteral other = (FakeDatatypeLiteral) obj;
            return getDatatype().equals( other.getDatatype() ) && getValue().equals( other.getValue() );
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getDatatype().hashCode() * getValue().hashCode() * 2;
    }
}
