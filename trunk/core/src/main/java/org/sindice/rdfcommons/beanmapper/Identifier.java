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

/**
 * Defines the result of a serialization.
 *
 * @see Serializer
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class Identifier {

    enum Type {
        resource,
        blank,
        literal
    }

    private Object id;

    private Type type;

    public Identifier(Object id, Type type) {
        if(id == null) {
            throw new NullPointerException("id cannot be null.");
        }
        if(type == null) {
            throw new NullPointerException("type cannot be null.");
        }
        this.id   = id;
        this.type = type;
    }

    public Object getId() {
        return id;
    }

    public boolean isResource() {
        return Type.resource.equals(type);
    }

    public boolean isBlank() {
        return Type.blank.equals(type);
    }

    public boolean isLiteral() {
        return Type.literal.equals(type);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(obj == this) {
            return true;
        }
        if(obj instanceof Identifier) {
            Identifier other = (Identifier) obj;
            return id.equals(other.id) && type.equals(other.type);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode() * 2 * type.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", id, type);
    }
}
