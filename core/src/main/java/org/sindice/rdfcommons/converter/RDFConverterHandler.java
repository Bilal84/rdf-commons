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

package org.sindice.rdfcommons.converter;

import org.sindice.rdfcommons.model.Triple;

/**
 * This interface models an handler receiving
 * conversion events from an {@link XMLToRDFConverter} implementation.
 *
 * @see XMLToRDFConverter
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface RDFConverterHandler {

    /**
     * Notifies the beginning of the <i>RDF</i> stream.
     *
     * @throws RDFConverterHandlerException if an error occurs while handling this message.
     */
    void beginStream() throws RDFConverterHandlerException;

    /**
     * Notifies the end of the <i>RDF</i> stream.
     *
     * @throws RDFConverterHandlerException if an error occurs while handling this message.
     */
    void endStream() throws RDFConverterHandlerException;

    /**
     * Notifies the creation of an <i>RDF</i> statement for a certain <i>XML</i> path.
     *
     * @param path the current XML path.
     * @param triple the generated statement.
     * @throws RDFConverterHandlerException if an error occurs while handling this message.
     */
    void handleStatement(XMLLocation path, Triple triple) throws RDFConverterHandlerException;

    /**
     * Notifies a non blocking error during conversion.
     *
     * @param path path to the XML section generating error.
     * @param error error description.
     * @throws RDFConverterHandlerException if an error occurs while handling this message.
     */
    void notifyError(XMLLocation path, String error) throws RDFConverterHandlerException;

    /**
     * Notifies a fatal error during conversion.
     *
     * @param path path to the XML section generating error.
     * @param t the fatal error.
     * @throws RDFConverterHandlerException if an error occurs while handling this message.
     */
    void notifyFatalError(XMLLocation path, Throwable t) throws RDFConverterHandlerException;

}
