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

import org.sindice.rdfcommons.model.Triple;

/**
 * Defines an <i>RDF</i> event based parser handler.
 *
 * @see RDFParser
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface RDFHandler {

    /**
     * Notifies the beginning of an <i>RDF</i> stream.
     */
    void beginRDFStream();

    /**
     *  Notifies the ending of an <i>RDF</i> stream.
     */
    void endRDFStream();

    /**
     * Notifies the detection of a triple.
     *
     * @param triple the detected triple.
     * @throws RDFHandlerException if an error occurs during this triple handling.
     */
    void handleStatement(Triple triple) throws RDFHandlerException;

    /**
     * Notifies a non blocking error during parsing.
     *
     * @param row error input stream row location.
     * @param col error input stream col location.
     * @param msg error message.
     */
    void error(int row, int col, String msg);

    /**
     * Notifies a fatal error during parsing.
     *
     * @param row fatal error input stream row location.
     * @param col fatal error input stream col location.
     * @param t error.
     */
    void fatalError(int row, int col, Throwable t);
}
