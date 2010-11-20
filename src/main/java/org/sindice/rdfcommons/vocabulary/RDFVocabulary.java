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

package org.sindice.rdfcommons.vocabulary;

/**
 * Defines some constants for the <i>RDFS</i> vocabulary.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class RDFVocabulary {

    /* ------ BEGIN PROPERTIES ------ */

    /**
     * Base vocabulary prefix.
     */
    public static final String PREFIX = "http://www.w3.org/1999/02/22-rdfcommons-syntax-ns";

    /**
     * <pre>rdfcommons:type Indicates membership of a class.</pre>
     */
    public static final String TYPE = PREFIX  + "#type";

    /**
     * <pre>rdfcommons:first The first item in an RDF list. Also often called the head.</pre>
     */
    public static final String FIRST = PREFIX  + "#first";

    /**
     * <pre>rdfcommons:rest The rest of an RDF list after the first item. Also often called the tail.</pre>
     */
    public static final String REST = PREFIX  + "#rest";

    /**
     * <pre>rdfcommons:subject The subject of an RDF statement.</pre>
     */
    public static final String SUBJECT = PREFIX  + "#subject";

    /**
     * <pre>rdfcommons:predicate The predicate of an RDF statement.</pre>
     */
    public static final String PREDICATE = PREFIX  + "#predicate";

    /**
     * <pre>rdfcommons:object The object of an RDF statement.</pre>
     */
    public static final String OBJECT = PREFIX  + "#object";

    /**
     * <pre>rdfcommons:value Identifies the principal value (usually a string)
     * of a property when the property value is a structured resource.</pre>
     */
    public static final String VALUE = PREFIX  + "#value";

    /* ------ END PROPERTIES ------ */

    /* ------ BEGIN CLASSES ------ */

    /**
     * <pre>rdfcommons:Property The concept of a Property.</pre>
     */
    public static final String PROPERTY = PREFIX  + "#Property";

    /**
     * <pre>rdfcommons:Statement The class of RDF statements.</pre>
     */
    public static final String STATEMENT = PREFIX  + "#Statement";

    /**
     * <pre>rdfcommons:Bag An unordered collection.</pre>
     */
    public static final String BAG = PREFIX  + "#Bag";

    /**
     * <pre>rdfcommons:Seq An ordered collection.</pre>
     */
    public static final String SEQ = PREFIX  + "#Seq";

    /**
     * <pre>rdfcommons:Alt A collection of alternatives.</pre>
     */
    public static final String ALT = PREFIX  + "#Alt";

    /**
     * <pre>rdfcommons:List The class of RDF Lists.</pre>
     */
    public static final String LIST = PREFIX  + "#List";

    /* ------ END CLASSES ------ */

}
