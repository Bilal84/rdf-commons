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

package org.sindice.rdfcommons;

/**
 * Models an <i>RDF</i> triple.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class Triple {

    private static final long BLANK_NODE_BASE = System.currentTimeMillis();

    private static long counter = 0;

    private final String subject;
    private final String predicate;
    private final Object object;

    private final boolean objLiteral;
    private final boolean subBlank;
    private final boolean objBlank;

    public static Triple createBlankSubjectTriple(String pred, Object obj, boolean literal, boolean bobj) {
        final String blank = nextBlankNodeIdentifier();
        return new Triple(blank, pred, obj, literal, true, bobj);
    }

    public static Triple createBlankObjectTriple(String sub, boolean blank, String pred) {
        final String blankUrl = nextBlankNodeIdentifier();
        return new Triple(sub, pred, blankUrl, false, blank, true);
    }

    protected static String nextBlankNodeIdentifier() {
        return String.format("%s_%s", BLANK_NODE_BASE, counter++);
    }

    public Triple(String sub, String pred, Object obj, boolean lit, boolean bsub, boolean bobj) {
        if(sub == null || pred == null || obj == null) {
            throw new IllegalArgumentException("The terms a triple cannot be null.");
        }
        if(lit && bobj) {
            throw new IllegalArgumentException("Object cannot be both blank node and literal.");
        }
        if( (!lit || bobj) && !(obj instanceof String) ) {
            throw new IllegalArgumentException("If the object is a URI or a blank node then the object must be a string.");
        }

        subject    = sub;
        predicate  = pred;
        object     = obj;
        objLiteral = lit;
        subBlank   = bsub;
        objBlank   = bobj;
    }

    public Triple(String sub, String pred, Object obj, boolean literal) {
        this(sub, pred, obj, literal, false, false);
    }

    public Triple(String sub, String pred, Object obj) {
        this(sub, pred, obj, false);
    }

    public String getSubject() {
        return subject;
    }

    public String getPredicate() {
        return predicate;
    }

    public Object getObject() {
        return object;
    }

    public String getObjectAsString() {
        return object.toString();
    }

    public boolean isObjLiteral() {
        return objLiteral;
    }

    public boolean isBlankSubject() {
        return subBlank;
    }

    public boolean isBlankObject() {
        return objBlank;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if( o == this) {
            return true;
        }
        if( ! (o instanceof Triple) ) {
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
            objLiteral == other.objLiteral
                &&
            subBlank == other.subBlank
                &&
            objBlank == other.objBlank;
    }

    @Override
    public int hashCode() {
        return subject.hashCode() +  predicate.hashCode() * 2 + object.hashCode() * 3;
    }

    @Override
    public String toString() {
        return
                objLiteral
                    ?
                toLiteralTriple()
                    :
                toTriple();

    }

    private String toTriple() {
        return String.format("{ %s <%s> %s }" , toSubjectString(), predicate, toObjectString());
    }

    private String toLiteralTriple() {
        return String.format("{ %s <%s> \"%s\"}", toSubjectString(), predicate, object);
    }

    private String toSubjectString() {
        return subBlank ? String.format("_%s", subject) : String.format("<%s>", subject);
    }

    private String toObjectString() {
        return objBlank ? String.format("_%s", object) : String.format("<%s>", object);
    }
}
