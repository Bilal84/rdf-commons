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

import java.io.InputStream;
import java.net.URI;

/**
 * Defines a generic <i>XML</i> to <i>RDF</i> converter.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface XMLToRDFConverter {

    /**
     * Converts an input <i>XML</i> stream to a sequence of {@link RDFConverterHandler} messages.
     *
     * @param is input XML stream.
     * @param graphURI the generated RDF graph <i>URI</i>.
     * @param handler converter handler.
     * @throws ConversionException if an error occurred during conversion.
     * @throws RDFConverterHandlerException if an error occurred within the handler.
     */
    void convertXMLStream(InputStream is, URI graphURI, RDFConverterHandler handler)
    throws ConversionException, RDFConverterHandlerException;

}
