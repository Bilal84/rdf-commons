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

package org.sindice.rdfcommons.jena;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import org.sindice.rdfcommons.model.Triple;
import org.sindice.rdfcommons.model.TripleBuffer;
import org.sindice.rdfcommons.model.TripleSet;
import org.sindice.rdfcommons.storage.TripleStorageFilter;

import java.io.OutputStream;
import java.util.Date;

import static org.sindice.rdfcommons.model.Triple.*;

/**
 * Utility functions to convert data from <i>jena</i> domain to
 * <i>sindice.rdfcommons</i> domain.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class JenaConversionUtil {

    private JenaConversionUtil() {}

    /**
     * Converts a <i>Jena</i> triple in a {@link org.sindice.rdfcommons.model.Triple}.
     *
     * @param triple Jena triple to convert.
     * @return the equivalent {@link org.sindice.rdfcommons.model.Triple}.
     */
    @SuppressWarnings("unchecked")
    public static Triple convertJenaTripleToTriple(com.hp.hpl.jena.graph.Triple triple) {
        final Node subject = triple.getSubject();
        final Node object  = triple.getObject();

        final String subjectStr;
        if(subject.isURI()) {
            subjectStr = subject.getURI();
        } else {
            subjectStr = subject.getBlankNodeLabel();
        }

        final Object objectValue;
        if(object.isURI()) {
            objectValue = object.getURI();
        } else if(object.isBlank()) {
            objectValue = object.getBlankNodeLabel();
        } else {
            objectValue = object.getLiteralValue();
        }

        return new Triple(
                subjectStr,
                triple.getPredicate().getURI(),
                objectValue,
                subject.isBlank() ? SubjectType.bnode : SubjectType.uri,
                getObjectType(object)
        );
    }

    /**
     * Converts a {@link org.sindice.rdfcommons.model.TripleSet} to a list of <i>Jena</i> triples.
     *
     * @param tripleSet input triple set.
     * @return list of converted Jena triples.
     */
    public static com.hp.hpl.jena.graph.Triple[] convertToJenaTriples(TripleSet tripleSet) {
        com.hp.hpl.jena.graph.Triple[] result = new com.hp.hpl.jena.graph.Triple[ tripleSet.getSize() ];
        int i = 0;
        for(Triple triple : tripleSet) {
            result[i] = new com.hp.hpl.jena.graph.Triple(
                createSubjectNode(triple),
                Node.createURI( triple.getPredicate() ),
                createObjectNode(triple)
            );
            i++;
        }
        return result;
    }

    /**
     * Converts a <i>Jena</i> model in a {@link org.sindice.rdfcommons.model.TripleSet}.
     *
     * @param model input model.
     * @return generated triple set.
     */
    public static TripleSet convertJenaModelToTripleSet(com.hp.hpl.jena.rdf.model.Model model) {
        StmtIterator iter = model.listStatements();
        TripleSet result = new TripleBuffer();
        while(iter.hasNext()) {
            Statement statement = iter.nextStatement();
            result.addTriple( convertJenaTripleToTriple( statement.asTriple() ) );
        }
        return result;
    }

    /**
     * Converts a {@link org.sindice.rdfcommons.model.TripleSet} to a Jena {@link com.hp.hpl.jena.rdf.model.Model}.
     *
     * @param ts triple set.
     * @return generated model.
     */
    public static com.hp.hpl.jena.rdf.model.Model convertTripleSetToJenaModel(TripleSet ts) {
        Model model = ModelFactory.createDefaultModel();
        for(Triple t : ts) {
            com.hp.hpl.jena.rdf.model.Resource subject;
            if( t.isSubjectBNode() ) {
                subject = model.createResource(AnonId.create( t.getSubject() ) );
            } else {
                subject = model.createResource( t.getSubject() );
            }

            com.hp.hpl.jena.rdf.model.Property predicate = model.createProperty( t.getPredicate() );

            com.hp.hpl.jena.rdf.model.RDFNode object;
            if( t.isObjectBNode() ) {
                object = model.createResource(AnonId.create( t.getObjectAsString() ) );
            } else {
                if(t.isObjLiteral()) {
                    object = model.createTypedLiteral( t.getObject() );
                } else {
                    object = model.createResource( t.getObjectAsString() );
                }
            }

            model.add(subject, predicate, object);
        }
        return model;
    }

    /**
     * Creates an appropriate <i>Jena</i> node representing the subject of the given triple.
     *
     * @param triple input triple.
     * @return subject nodel.
     */
    public static com.hp.hpl.jena.graph.Node createSubjectNode(Triple triple) {
        return
                triple.isSubjectBNode()
                        ?
                Node.createAnon( AnonId.create(triple.getSubject()) ) : Node.createURI( triple.getSubject() );
    }

    /**
     * Creates an appropriate <i>Jena</i> node representing the object of the given triple.
     *
     * @param triple input triple.
     * @return object nodel.
     */

    public static com.hp.hpl.jena.graph.Node createObjectNode(Triple triple) {
        if(triple.isObjLiteral()) {
            return Node.createLiteral( triple.getObject().toString(), null, getRDFDatatype(triple.getObject()) );
        }
        if(triple.isObjectBNode()) {
            return Node.createAnon(AnonId.create( triple.getObjectAsString() ) );
        }
        return Node.createURI( triple.getObjectAsString() );
    }

    /**
     * converts a {@link org.sindice.rdfcommons.storage.TripleStorageFilter} in a Jena <i>TripleMatch</i>.
     *
     * @param filter input filter.
     * @return generated triple match.
     */
    public static com.hp.hpl.jena.graph.TripleMatch convertToJenaTripleMatch(final TripleStorageFilter filter) {
        return new TripleMatch() {

            public Node getMatchSubject() {
                final String subjectMatch = filter.getSubjectMatching();
                if( filter.requireSubjectBlank() ) {
                    return
                            subjectMatch == null
                                    ?
                            Node.createAnon()
                                    :
                            Node.createAnon( AnonId.create(subjectMatch) );
                }
                return subjectMatch == null ? null : Node.createURI(subjectMatch);
            }

            public Node getMatchPredicate() {
                final String predicateMatch = filter.getPredicateMatching();
                return predicateMatch == null ? null : Node.createURI(predicateMatch);
            }

            public Node getMatchObject() {
                final Object objectMatch = filter.getObjectMatching();
                if( filter.requireObjectBlank() ) {
                    return
                            objectMatch == null
                                ?
                            Node.createAnon()
                                :
                            Node.createAnon( AnonId.create(objectMatch.toString()) );
                }
                return filter.requireLiteral()
                    ?
                    objectMatch == null ? null : Node.createLiteral(
                            objectMatch.toString(),
                            null,
                            getRDFDatatype(objectMatch)
                    )
                    :
                    objectMatch == null ? null : Node.createURI( objectMatch.toString() );
            }

            public com.hp.hpl.jena.graph.Triple asTriple() {
                return new com.hp.hpl.jena.graph.Triple( getMatchSubject(), getMatchPredicate(), getMatchObject() );
            }
        };
    }

    /**
     * Serialzes the content of a {@link org.sindice.rdfcommons.model.TripleSet} on the given output stream.
     *
     * @param os
     * @param ts
     */
    public static void serializeToRDF(TripleSet ts, OutputStream os) {
        Model model = convertTripleSetToJenaModel(ts);
        model.write(os, "RDF/XML-ABBREV");
    }

    /**
     * Converts a {@link org.sindice.rdfcommons.storage.TripleStorageFilter} in a <i>SPARQL Construct</i> query.
     *
     * @param tsf
     * @return the SPARQL query.
     */
    public static String convertToSparqlConstructQuery(String graphName, TripleStorageFilter tsf) {
        return String.format(
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
                "CONSTRUCT {%s %s %s} " +
                "FROM <%s> " +
                "WHERE {%s %s %s %s %s %s}",

                toTerm( "?s", tsf.getSubjectMatching()   ),
                toTerm( "?p", tsf.getPredicateMatching() ),
                toObjectTerm( "?o", tsf.getObjectMatching() ),

                graphName,

                toTerm( "?s", tsf.getSubjectMatching()   ),
                toTerm( "?p", tsf.getPredicateMatching() ),
                toObjectTerm( "?o", tsf.getObjectMatching() ),

                (tsf.getSubjectMatching() == null && tsf.requireSubjectBlank()) ? ". FILTER isBlank(?s)"   : "",
                (tsf.getObjectMatching()  == null && tsf.requireObjectBlank())  ? ". FILTER isBlank(?o)"   : "",
                (tsf.getObjectMatching()  == null && tsf.requireLiteral())      ? ". FILTER isLiteral(?o)" : ""
        );
    }

    /**
     * Returns the most appropriate <i>RDF Datatype</i> for the given object.
     *
     * @param o input object.
     * @return the datatype.
     * @throws IllegalArgumentException if the object is not supported.
     */
    public static RDFDatatype getRDFDatatype(Object o) {
        if(o instanceof String) {
            return XSDDatatype.XSDstring;
        }
        if(o instanceof Integer) {
            return XSDDatatype.XSDinteger;
        }
        if(o instanceof Long) {
            return XSDDatatype.XSDlong;
        }
        if(o instanceof Short) {
            return XSDDatatype.XSDshort;
        }
        if(o instanceof Float) {
            return XSDDatatype.XSDfloat;
        }
        if(o instanceof Double) {
            return XSDDatatype.XSDdouble;
        }
        if(o instanceof Byte) {
            return XSDDatatype.XSDbyte;
        }
        if(o instanceof Boolean) {
            return XSDDatatype.XSDboolean;
        }
        if(o instanceof Character) {
            return XSDDatatype.XSDstring;
        }
        if(o instanceof Date) {
            return XSDDatatype.XSDdateTime;
        }
        throw new IllegalArgumentException("Invalid object type: " + o);
    }

    private static String toTerm(String var, String in) {
        return in == null ? var : String.format("<%s>", in);
    }

    private static String getRDFDatatypeFragment(Object in) {
        String uri = getRDFDatatype(in).getURI();
        final int indexofFragment = uri.indexOf('#');
        if(indexofFragment == -1) {
            throw new IllegalArgumentException();
        }
        return uri.substring( indexofFragment + 1 );
    }

    private static String toObjectTerm(String var, Object in) {
        return in == null ? var : String.format("\"%s\"^^xsd:%s", in.toString(), getRDFDatatypeFragment(in) );
    }

    private static Triple.ObjectType getObjectType(Node node) {
        if(node.isLiteral()) {
            return ObjectType.literal;
        }
        if(node.isBlank()) {
            return ObjectType.bnode;
        }
        return ObjectType.uri;
    }

}
