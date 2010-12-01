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

import org.sindice.rdfcommons.model.TripleSet;

import java.lang.annotation.Annotation;

/**
 * Defines a serializer for the enumerations.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class EnumSerializer implements CriteriaSerializer {

    public boolean acceptClass(Class clazz, Annotation[] annotations) {
        return clazz.isEnum();
    }

    public boolean isComplex() {
        return false;
    }

    public Identifier getIdentifier(SerializationContext context, Object object, Annotation[] annotations, TripleSet buffer) {
        throw new UnsupportedOperationException();
    }

    public Identifier serialize(SerializationContext context, Object object, Annotation[] annotations, TripleSet buffer)
    throws SerializationException {
        return new Identifier( ((Enum) object).name(), Identifier.Type.literal);
    }

}
