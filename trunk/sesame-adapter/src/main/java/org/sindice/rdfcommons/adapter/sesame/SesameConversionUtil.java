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

package org.sindice.rdfcommons.adapter.sesame;

import org.openrdf.model.BNode;
import org.openrdf.model.Graph;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.datatypes.XMLDatatypeUtil;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.model.impl.GraphImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.XMLSchema;
import org.sindice.rdfcommons.adapter.DefaultDatatypeLiteral;
import org.sindice.rdfcommons.adapter.DefaultLanguageLiteral;
import org.sindice.rdfcommons.adapter.LibAdapter;
import org.sindice.rdfcommons.adapter.LiteralFactory;
import org.sindice.rdfcommons.adapter.LiteralFactoryException;
import org.sindice.rdfcommons.model.Triple;
import org.sindice.rdfcommons.model.TripleBuffer;
import org.sindice.rdfcommons.model.TripleImpl;
import org.sindice.rdfcommons.model.TripleSet;
import org.sindice.rdfcommons.storage.TripleStorageFilter;

/**
 * Utility functions to convert data structures from <i>Jena</i> API
 * to <i>RDFCommons</i> API.
 */
public class SesameConversionUtil implements LibAdapter<Literal, Object> {

    private static final SesameConversionUtil instance = new SesameConversionUtil();

    private static final ValueFactory valueFactory = new ValueFactoryImpl();

    /**
     * @return the {@link org.sindice.rdfcommons.adapter.sesame.SesameConversionUtil} singleton instance.
     */
    public static synchronized SesameConversionUtil getInstance() {
        return instance;
    }

    private SesameConversionUtil() {}

    private LiteralFactory literalFactory;

    public void setLiteralFactory(LiteralFactory lf) {
        if(lf == null) {
            throw new NullPointerException("Literal factory instance cannot be null.");
        }
        literalFactory = lf;
    }

    public LiteralFactory getLiteralFactory() {
        return literalFactory;
    }

    /**
     * Returns an appropriate object for the given
     * <i>Sesame</i> {@link Literal}.
     *
     * @param literal input sesame literal.
     * @return object value.
     * @throws LiteralFactoryException
     */
    public synchronized Object getObjectLiteral(Literal literal)
    throws LiteralFactoryException {
        final String datatype = literal.getDatatype() == null ? null : literal.getDatatype().stringValue();
        final String literalValue = literal.stringValue();
        if (datatype != null) {
            if (XMLSchema.BOOLEAN.stringValue().equals(datatype)) {
                return new DefaultDatatypeLiteral<Object>(datatype, XMLDatatypeUtil.parseBoolean(literalValue));
            }
            if (XMLSchema.BYTE.stringValue().equals(datatype)) {
                return new DefaultDatatypeLiteral<Object>(datatype,  XMLDatatypeUtil.parseByte(literalValue));
            }
            if (XMLSchema.FLOAT.stringValue().equals(datatype)) {
                return new DefaultDatatypeLiteral<Object>(datatype,  XMLDatatypeUtil.parseFloat(literalValue));
            }
            if (XMLSchema.SHORT.stringValue().equals(datatype)) {
                return new DefaultDatatypeLiteral<Object>(datatype,  XMLDatatypeUtil.parseShort(literalValue));
            }
            if (XMLSchema.INT.stringValue().equals(datatype)) {
                return new DefaultDatatypeLiteral<Object>(datatype,  XMLDatatypeUtil.parseInt(literalValue));
            }
            if (XMLSchema.INTEGER.stringValue().equals(datatype)) {
                return new DefaultDatatypeLiteral<Object>(datatype,  XMLDatatypeUtil.parseInteger(literalValue));
            }
            if (XMLSchema.LONG.stringValue().equals(datatype)) {
                return new DefaultDatatypeLiteral<Object>(datatype,  XMLDatatypeUtil.parseLong(literalValue));
            }
            if (XMLSchema.DOUBLE.stringValue().equals(datatype)) {
                return new DefaultDatatypeLiteral<Object>(datatype,  XMLDatatypeUtil.parseDouble(literalValue));
            }
            if (XMLSchema.DATETIME.stringValue().equals(datatype) || XMLSchema.DATE.stringValue().equals(datatype)) {
                return new DefaultDatatypeLiteral<Object>(datatype,  XMLDatatypeUtil.parseCalendar(literalValue));
            }
            return literalFactory.createLiteral(datatype, literalValue);
        }

        final String language = literal.getLanguage();
        if (language != null) {
            return new DefaultLanguageLiteral(literalValue, language);
        }

        return literal.stringValue();
    }

    /**
     * Converts a <i>Sesame</i> {@link Statement} to a {@link Triple}.
     *
     * @param statement input statement.
     * @return output triple representing the input statement.
     * @throws org.sindice.rdfcommons.adapter.LiteralFactoryException if an error occurs during literal mapping.
     */
    public synchronized Triple<Object> convertSesameStatementToTriple(Statement statement)
    throws LiteralFactoryException {
        final Resource subResource = statement.getSubject();
        final Value    objResource = statement.getObject();
        final Triple.SubjectType subjectType =
                subResource instanceof URI ? Triple.SubjectType.uri : Triple.SubjectType.bnode;
        final Triple.ObjectType objectType;
        final Object value;
        if(objResource instanceof URI) {
            objectType = Triple.ObjectType.uri;
            value = objResource.stringValue();
        } else if(objResource instanceof BNode) {
            objectType = Triple.ObjectType.bnode;
            value = objResource.stringValue();
        } else {
            objectType = Triple.ObjectType.literal;
            value = getObjectLiteral((Literal) objResource);
        }
        return new TripleImpl<Object>(
                subResource.stringValue(),
                statement.getPredicate().stringValue(),
                value, subjectType, objectType,
                statement.getContext().stringValue()
        );
    }

    /**
     * Creates a resource subject for the input triple.
     *
     * @param triple input triple.
     * @return resource representing the subject of this triple.
     */
    public synchronized Resource createSubjectResource(Triple triple) {
        if(triple.getSubjectType() == Triple.SubjectType.uri) {
            return valueFactory.createURI(triple.getSubject());
        }
        if(triple.getSubjectType() == Triple.SubjectType.bnode) {
            return new BNodeImpl(triple.getSubject());
        }
        throw new IllegalStateException();
    }

    /**
     * Creates an appropriate <i>Sesame</i> {@link Value} for the object
     * of the given triple.
     *
     * @param triple input triple.
     * @return sesame value.
     */
    public synchronized Value createObjectValue(Triple triple) {
        if(triple.getObjectType() == Triple.ObjectType.uri) {
            return valueFactory.createURI(triple.getObjectAsString());
        }
        if(triple.getObjectType() == Triple.ObjectType.bnode) {
            return new BNodeImpl(triple.getObjectAsString());
        }
        if(triple.getObjectType() == Triple.ObjectType.literal) {
            final String datatype = triple.getLiteralDatatype();
            if(datatype != null) {
                return new LiteralImpl(triple.getObjectAsString(), valueFactory.createURI(datatype));
            }
            final String language = triple.getLiteralLanguage();
            if(language != null) {
                return new LiteralImpl(triple.getObjectAsString(), language);
            }
            return new LiteralImpl(triple.getObjectAsString());
        }
        throw new IllegalStateException();
    }

    /**
     * Converts a {@link Triple} to a <i>Sesame</i> {@link Statement}.
     *
     * @param triple input triple.
     * @return the represeting sesame statement.
     */
    public Statement convertTripleToSesameStatement(Triple triple) {
        return new ContextStatementImpl(
                createSubjectResource(triple),
                valueFactory.createURI(triple.getPredicate()),
                createObjectValue(triple),
                valueFactory.createURI(triple.getGraph())
        );
    }

    /**
     * Converts a <i>Sesame</i> {@link Graph} to a {@link TripleSet}.
     *
     * @param graph input graph.
     * @return converted triple set.
     * @throws LiteralFactoryException if an error occurs during literal mapping.
     */
    public TripleSet convertSesameGraphToTripleSet(Graph graph) throws LiteralFactoryException {
        final TripleSet result = new TripleBuffer();
        for(Statement statement : graph) {
            result.addTriple( convertSesameStatementToTriple(statement) );
        }
        return result;
    }

    /**
     * Converts a {@link TripleSet} to a <i>Sesame</i> {@link Graph}.
     *
     * @param tripleSet input triple set.
     * @return generated graph. 
     */
    public Graph convertTripleSetToSesameGraph(TripleSet tripleSet) {
        final Graph graph = new GraphImpl();
        for(Triple triple : tripleSet) {
            final String graphURL = triple.getGraph();
            graph.add(
                createSubjectResource(triple),
                valueFactory.createURI(triple.getPredicate()),
                createObjectValue(triple),
                graphURL == null ? null : valueFactory.createURI(graphURL)
            );
        }
        return graph;
    }

    /**
     * Converts a triple storage filter in a list of values to perform a triple match with <i>Sesame</i>.
     *
     * @param filter filter to be converted.
     * @return a list of converted values.
     */
    public synchronized Value[] convertToTripleMatch(final TripleStorageFilter filter) {
        final String s = filter.getSubjectMatching();
        final String p = filter.getPredicateMatching();
        final Object o = filter.getObjectMatching();
        return new Value[] {
                s == null ? null :
                        filter.requireSubjectBlank()
                                ?
                        valueFactory.createBNode(s) : valueFactory.createURI(s),
                p == null ? null : valueFactory.createURI(p),
                o == null ? null :
                        filter.requireObjectBlank()
                                ?
                        valueFactory.createBNode(o.toString()) : valueFactory.createLiteral(o.toString())

        };
    }

    /**
     * Converts a generic <i>Sesame</i> value to the most appropriate object.
     *
     * @param value Sesame value.
     * @return generated object.
     * @throws org.sindice.rdfcommons.adapter.LiteralFactoryException
     */
    public synchronized Object convertValueToObject(Value value) throws LiteralFactoryException {
        if(value instanceof URI || value instanceof BNode) {
            return value.stringValue();
        }
        if(value instanceof Literal) {
            getObjectLiteral((Literal) value);
        }
        throw new IllegalStateException();
    }

}
