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

package org.sindice.rdfcommons.parser;

import org.sindice.rdfcommons.adapter.LiteralFactoryException;
import org.sindice.rdfcommons.model.TripleSet;

import java.io.InputStream;

/**
 * Parser for <i>RDF</i> streams.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public interface RDFParser {

    /**
     * Parsers the input stream and returns a triple set.
     *
     * @param is input stream.
     * @param graph default graph for triples without an explicit context.
     * @return the triple set.
     * @throws RDFParserException if an error occurs during the parsing.
     * @throws org.sindice.rdfcommons.adapter.LiteralFactoryException if an error occurs during literal mapping.
     */
    TripleSet parse(InputStream is, String graph) throws RDFParserException, LiteralFactoryException;

}
