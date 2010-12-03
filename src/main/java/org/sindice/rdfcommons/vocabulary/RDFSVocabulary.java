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
 * Defines the <i>RDFS</i> vocabulary.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class RDFSVocabulary extends RDFVocabulary {

    /**
     * Base vocabulary prefix.
     */
    public static final String RDFS_PREFIX = "http://www.w3.org/2000/01/rdf-schema";

    /* ------ BEGIN PROPERTIES ------ */

    /**
     * <pre>rdfs:subClassOf Indicates membership of a class.</pre>
     */
    public static final String SUBCLASSOF = RDFS_PREFIX  + "#subClassOf";

    /**
     * <pre>rdfs:subPropertyOf Indicates specialization of properties.</pre>
     */
    public static final String SUBPROPERTYOF = RDFS_PREFIX  + "#subPropertyOf";

    /**
     * <pre>rdfs:domain A domain class for a property type.</pre>
     */
    public static final String DOMAIN = RDFS_PREFIX  + "#domain";

    /**
     * <pre>rdfs:range A range class for a property type.</pre>
     */
    public static final String RANGE = RDFS_PREFIX  + "#range";

    /**
     * <pre>rdfs:label Provides a human-readable version of a resource name.</pre>
     */
    public static final String LABEL = RDFS_PREFIX  + "#label";

    /**
     * <pre>rdfs:comment Use this for descriptions.</pre>
     */
    public static final String COMMENT = RDFS_PREFIX  + "#comment";

    /**
     * <pre>rdfs:member A member of a container.</pre>
     */
    public static final String MEMBER = RDFS_PREFIX  + "#member";

    /**
     * <pre>rdfs:seeAlso A resource that provides information about the subject resource.</pre>
     */
    public static final String SEEALSO = RDFS_PREFIX  + "#seeAlso";

    /**
     * <pre>rdfs:isDefinedBy Indicates the namespace of a resource.</pre>
     */
    public static final String ISDEFINEDBY = RDFS_PREFIX  + "#isDefinedBy";

    /* ------ END PROPERTIES ------ */

    /* ------ BEGIN CLASSES ------ */

    //rdfs:Resource  	The class resource, everything.
    //rdfs:Literal 	    This represents the set of atomic values, eg. textual strings.
    //rdfs:XMLLiteral 	The class of XML literals.
    //rdfs:Class 	    The concept of Class
    //rdfs:Datatype 	The class of datatypes.
    //rdfs:Container 	This represents the set Containers.
    //rdfs:ContainerMembershipProperty 	The container membership properties, rdfcommons:1, rdfcommons:2, ..., all of which are sub-properties of 'member'.

    /* ------ END CLASSES ------ */

}
