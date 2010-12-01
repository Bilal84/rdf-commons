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

package org.sindice.rdfcommons.serialization;

import org.sindice.rdfcommons.storage.ResultSet;

import java.io.OutputStream;

/**
 * This class is responsible for serializing RDF objects in XML.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public interface ResultSetXMLSerializer {

    /**
     * Serializes the content of the given {@link ResultSet}
     * in the output stream.
     *
     * @param rs result set to serialize.
     * @param os output strem to dump the content.
     */
    void serialize(ResultSet rs, OutputStream os);

}
