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
 * Models an <i>RDF</i> triple.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class Triple<O> {

    public enum SubjectType {
        bnode,
        uri
    }

    public enum ObjectType {
        bnode,
        uri,
        literal
    }

    private static final long BLANK_NODE_BASE = System.currentTimeMillis();

    private static long counter = 0;

    private final String subject;
    private final String predicate;
    private final O      object;

    private final SubjectType subjectType;
    private final ObjectType  objectType;

    public synchronized static <O> Triple createBNodeSubjectTriple(
            String pred, O obj, ObjectType objectType
    ) {
        final String blank = nextBNodeIdentifier();
        return new Triple<O>(blank, pred, obj, objectType);
    }

    public synchronized static Triple createBNodeObjectTriple(String sub, String pred, SubjectType subjectType) {
        final String blank = nextBNodeIdentifier();
        return new Triple<String>(sub, pred, blank, subjectType, ObjectType.bnode);
    }

    protected static String nextBNodeIdentifier() {
        return String.format("%s_%s", BLANK_NODE_BASE, counter++);
    }

    public Triple(String sub, String pred, O obj, SubjectType subjectType, ObjectType objectType) {
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
    }

    public Triple(String sub, String pred, O obj, ObjectType objectType) {
        this(sub, pred, obj, SubjectType.uri, objectType);
    }

    public Triple(String sub, String pred, O obj) {
        this(sub, pred, obj, ObjectType.uri);
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

    public SubjectType getSubjectType() {
        return subjectType;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public String getObjectImplicitType() {
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

    public String getLiteralType() {
        if(object instanceof TypedLiteral) {
            final TypedLiteral typedLiteral = (TypedLiteral) object;
            return typedLiteral.getType();
        }
        return getObjectImplicitType();
    }

    public String getObjectLanguage() {
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

    public boolean isObjLiteral() {
        return objectType == ObjectType.literal;
    }

    public boolean isObjectBNode() {
        return objectType == ObjectType.bnode;
    }

    public String toTripleString() {
        final StringBuilder sb = new StringBuilder();
        toTripleString(sb);
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
            subject.equals( other.subject )
                &&
            predicate.equals( other.predicate )
                &&
            object.equals( other.object )
                &&
            subjectType == other.subjectType
                &&
            objectType  == other.objectType;
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
        return "{" + toTripleString() + "}";
    }

    private void toTripleString(StringBuilder sb) {
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
        sb.append('"').append( getObjectAsString() ).append('"');
        final String literalType = getLiteralType();
        if(literalType != null) {
            sb.append("^^").append('<').append(literalType).append('>');
            return;
        }
        final String objectLang = getObjectLanguage();
        if(objectLang != null) {
            sb.append('@').append(objectLang);
        }
    }
}
