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

import org.sindice.rdfcommons.model.Triple;

/**
 * Base class for serializer classes.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public abstract class BaseSerializer<T> extends BaseMapper implements CriteriaSerializer<T> {

    /**
     * Returns the most appropriate {@link org.sindice.rdfcommons.model.Triple.ObjectType}
     * for the given identifier.
     *
     * @param id input identifier.
     * @return object type.
     */
    protected Triple.ObjectType getObjectType(Identifier id) {
        if(id.isLiteral()) {
            return Triple.ObjectType.literal;
        }
        if(id.isBlank()) {
            return Triple.ObjectType.bnode;
        }
        return Triple.ObjectType.uri;
    }

}
