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
 * Ontology of serialization.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class SerializerVocabulary extends BaseVocabulary {

    /**
     * Instance prefix.
     */
    public static final String INSTANCE = ONTOLOGY_PREFIX + "instance";

    /**
     * Defines a collection item index.
     */
    public static final String ITEM_INDEX = ONTOLOGY_PREFIX + "#itemindex";

    /**
     * Defines a collaction item value.
     */
    public static final String ITEM_VALUE = ONTOLOGY_PREFIX + "#itemvalue";

    /**
     * Defines a Map key value entry.
     */
    public static final String ENTRY = ONTOLOGY_PREFIX + "#entry";

    /**
     * Defines the key of a key / value entry.
     */
    public static final String KEY = ONTOLOGY_PREFIX + "#key";

    /**
     * Defines the value of a key / value entry.
     */
    public static final String VALUE = ONTOLOGY_PREFIX + "#value";

    private SerializerVocabulary() {}
}
