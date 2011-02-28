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

/**
 * This exception models any error raised by an {@link RDFConverterHandler} instance
 * while processing <i>RDF</i> conversion messages.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class RDFConverterHandlerException extends Exception {

    public RDFConverterHandlerException(String message, Throwable cause) {
        super(message, cause);
    }

    public RDFConverterHandlerException(String message) {
        super(message);
    }

}
