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

package org.sindice.rdfcommons.model;

import org.sindice.rdfcommons.vocabulary.XMLSchemaTypes;

import java.util.Date;

/**
 * Default implementation of {@link Triple} interface.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id: Triple.java 65 2010-12-04 23:58:14Z michele.mostarda $
 */
public class TripleImpl<O> implements Triple<O> {

    private static final long BLANK_NODE_BASE = System.currentTimeMillis();

    private static long counter = 0;

    private final String subject;
    private final String predicate;
    private final O      object;

    private final String graph;

    private final SubjectType subjectType;
    private final ObjectType  objectType;

    public static synchronized String nextBNodeIdentifier() {
        return String.format("%s_%s", BLANK_NODE_BASE, counter++);
    }

    public synchronized static <O> Triple createBNodeSubjectTriple(
            String pred, O obj, ObjectType objectType, String graph
    ) {
        final String blank = nextBNodeIdentifier();
        return new TripleImpl<O>(blank, pred, obj, SubjectType.bnode, objectType, graph);
    }

    public synchronized static <O> Triple createBNodeSubjectTriple(
            String pred, O obj, ObjectType objectType
    ) {
        return createBNodeSubjectTriple(pred, obj, objectType, null);
    }

    public synchronized static Triple createBNodeObjectTriple(
            String sub, String pred, SubjectType subjectType, String graph
    ) {
        final String blank = nextBNodeIdentifier();
        return new TripleImpl<String>(sub, pred, blank, subjectType, ObjectType.bnode, graph);
    }

    public synchronized static Triple createBNodeObjectTriple(
            String sub, String pred, SubjectType subjectType
    ) {
        return createBNodeObjectTriple(sub, pred, subjectType, null);
    }

    /**
     * Escapes characters for text appearing as literal
     * in the <i>N-Quads</i> format.
     *
     * <P>The following commonly used control characters
     * are escaped:
     * <table border='1' cellpadding='3' cellspacing='0'>
     * <tr><th> Character </th><th> Escaped As </th></tr>
     * <tr><td> " </td><td> \" </td></tr>
     * <tr><td> \ </td><td> \\ </td></tr>
     * </table>
     * <p/>
     *
     * @param in the string to be escaped.
     * @param out buffer to print out the escaped literal.
     */
    protected static void escapeLiteral(String in, StringBuilder out) {
        char character;
        for (int i = 0; i < in.length(); i++) {
            character = in.charAt(i);
            if (character == '\"') {
                out.append("\\\"");
            } else if (character == '\\') {
                out.append("\\\\");
            } else {
                out.append(character);
            }
        }
    }

    public TripleImpl(
            String sub, String pred, O obj,
            SubjectType subjectType, ObjectType objectType,
            String graph
    ) {
        if(sub == null || pred == null || obj == null) {
            throw new IllegalArgumentException("The terms of a triple cannot be null.");
        }
        if(subjectType == null || objectType == null) {
            throw new IllegalArgumentException("The types of a triple cannot be null.");
        }
        if( !(objectType == ObjectType.literal) && !(obj instanceof String) ) {
            throw new IllegalArgumentException(
                    "If the object is a URI or a blank node then the object must be a string."
            );
        }

        subject    = sub;
        predicate  = pred;
        object     = obj;
        this.subjectType = subjectType;
        this.objectType  = objectType;

        this.graph = graph;
    }

    public TripleImpl(
            String sub, String pred, O obj,
            SubjectType subjectType, ObjectType objectType
    ) {
        this(sub, pred, obj, subjectType, objectType, null);
    }

    public TripleImpl(String sub, String pred, O obj, ObjectType objectType) {
        this(sub, pred, obj, SubjectType.uri, objectType, null);
    }

    public TripleImpl(String sub, String pred, O obj, ObjectType objectType, String graph) {
        this(sub, pred, obj, SubjectType.uri, objectType, graph);
    }

    // TODO: remove this cast.
    public TripleImpl(String sub, String pred, Literal literal, String graph) {
        this(sub, pred, (O) literal, SubjectType.uri, ObjectType.literal, graph);
    }

    // TODO: remove this cast.
    public TripleImpl(String sub, String pred, Object obj, String graph) {
        this(sub, pred, (O) obj, SubjectType.uri, ObjectType.uri, graph);
    }

    public TripleImpl(String sub, String pred, O obj) {
        this(sub, pred, obj, ObjectType.uri);
    }

    public String getObjectImplicitDatatype() {
        if(object.getClass().equals(boolean.class) || object.getClass().equals(Boolean.class)) {
            return XMLSchemaTypes.BOOLEAN;
        }
        if(object.getClass().equals(byte.class) || object.getClass().equals(Byte.class)) {
            return XMLSchemaTypes.BYTE;
        }
        if(object.getClass().equals(short.class) || object.getClass().equals(Short.class)) {
            return XMLSchemaTypes.SHORT;
        }
        if(object.getClass().equals(int.class) || object.getClass().equals(Integer.class)) {
            return XMLSchemaTypes.INT;
        }
        if(object.getClass().equals(long.class) || object.getClass().equals(Long.class)) {
            return XMLSchemaTypes.LONG;
        }
        if(object.getClass().equals(float.class) || object.getClass().equals(Float.class)) {
            return XMLSchemaTypes.FLOAT;
        }
        if(object.getClass().equals(double.class) || object.getClass().equals(Double.class)) {
            return XMLSchemaTypes.DOUBLE;
        }
        if(object instanceof Date) {
            return XMLSchemaTypes.DATE;
        }
        return null;
    }

    public String getSubject() {
        return subject;
    }

    public String getPredicate() {
        return predicate;
    }

    public O getObject() {
        return object;
    }

    public String getGraph() {
        return graph;
    }

    public SubjectType getSubjectType() {
        return subjectType;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public String getLiteralDatatype() {
        if(object instanceof DatatypeLiteral) {
            final DatatypeLiteral datatypeLiteral = (DatatypeLiteral) object;
            return datatypeLiteral.getDatatype();
        }
        return getObjectImplicitDatatype();
    }

    public String getLiteralLanguage() {
        if(object instanceof LanguageLiteral) {
            final LanguageLiteral languageLiteral = (LanguageLiteral) object;
            return languageLiteral.getLanguage();
        }
        return null;
    }

    public String getObjectAsString() {
        if( object instanceof Date) {
            return XMLSchemaTypes.dateFormatter.format((Date) object);
        }
        if(object instanceof Literal) {
            return ((Literal) object).getValue();
        }
        return object.toString();
    }

    public boolean isSubjectBNode() {
        return subjectType == SubjectType.bnode;
    }

    public boolean isObjectLiteral() {
        return objectType == ObjectType.literal;
    }

    public boolean isObjectBNode() {
        return objectType == ObjectType.bnode;
    }

    public String toNTriplesString() {
        final StringBuilder sb = new StringBuilder();
        toNTriplesString(sb);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if(o == this) {
            return true;
        }
        if(! (o instanceof Triple)) {
            return false;
        }
        Triple other = (Triple) o;
        return
            subjectType == other.getSubjectType()
                &&
            objectType  == other.getObjectType()
                &&
            subject.equals( other.getSubject() )
                &&
            predicate.equals( other.getPredicate() )
                &&
            object.equals( other.getObject() );
    }

    @Override
    public int hashCode() {
        return
                subject.hashCode() *
                predicate.hashCode() * 2 *
                object.hashCode() * 3 *
                subjectType.hashCode() * 5 *
                objectType.hashCode() * 7;
    }

    @Override
    public String toString() {
        return "{" + toNTriplesString() + "}";
    }

    private void toNTriplesString(StringBuilder sb) {
        toSubjectString(sb);
        sb.append(' ');
        toPredicateString(sb);
        sb.append(' ');
        toObjectString(sb);
    }

    private void toSubjectString(StringBuilder sb) {
        if(subjectType == SubjectType.bnode) {
            sb.append("_:").append(subject);
        } else {
            sb.append('<').append(subject).append('>');
        }
    }

    private void toPredicateString(StringBuilder sb) {
        sb.append('<');
        sb.append(predicate);
        sb.append('>');
    }

    private void toObjectString(StringBuilder sb) {
        if(objectType == ObjectType.bnode) {
            sb.append("_:").append(object);
            return;
        }
        if(!(objectType == ObjectType.literal) ) {
            sb.append('<').append(object).append('>');
            return;
        }
        sb.append('"');
        escapeLiteral( getObjectAsString(), sb );
        sb.append('"');
        final String literalType = getLiteralDatatype();
        if(literalType != null) {
            sb.append("^^").append('<').append(literalType).append('>');
            return;
        }
        final String objectLang = getLiteralLanguage();
        if(objectLang != null) {
            sb.append('@').append(objectLang);
        }
    }
}

