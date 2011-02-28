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
 * <i>XML</i> to <i>RDF</i> converter vocabulary used by {@link DefaultXMLToRDFConverter}.
 *
 * @see DefaultXMLToRDFConverter
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultXMLToRDFConverterVocabulary {

    /**
     * Converter prefix.
     */
    public static final String VOCABULARY_PREFIX = "http://rdfcommons.sindice.org/xmlrdf/";

    /**
     * XML attribute separator.
     */
    public static final String ATTRIBUTE_SEPARATOR = "#";

    /** Namespace property. */
    public static final String NAMESPACE       = VOCABULARY_PREFIX + "namespace";
    /** Attribute property. */
    public static final String ATTRIBUTE       = VOCABULARY_PREFIX + "attribute";
    /** Attribute value property. */
    public static final String ATTRIBUTE_VALUE = VOCABULARY_PREFIX + "value";
    /** Node content property. */
    public static final String CONTENT         = VOCABULARY_PREFIX + "content";
    /** parent child property. */
    public static final String CHILD           = VOCABULARY_PREFIX + "child";

    private DefaultXMLToRDFConverterVocabulary(){}

}
