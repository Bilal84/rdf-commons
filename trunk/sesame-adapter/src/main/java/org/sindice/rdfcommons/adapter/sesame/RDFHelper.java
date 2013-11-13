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
import org.openrdf.model.impl.GraphImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.sindice.rdfcommons.util.MathUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * RDF helper class.
 */
public class RDFHelper {

    private static final ValueFactoryImpl factory = ValueFactoryImpl.getInstance();

    private RDFHelper(){}

    public static URI uri(String uri) {
        return factory.createURI(uri);
    }

    public static Literal literal(String s) {
        return factory.createLiteral(s);
    }

    public static Literal literal(String s, String l) {
        return factory.createLiteral(s, l);
    }

    public static Literal literal(String s, URI datatype) {
        return factory.createLiteral(s, datatype);
    }

    public static Literal literal(boolean b) {
        return factory.createLiteral(b);
    }

    public static Literal literal(byte t) {
        return factory.createLiteral(t);
    }

    public static Literal literal(short s) {
        return factory.createLiteral(s);
    }

    public static Literal literal(int i) {
        return factory.createLiteral(i);
    }

    public static Literal literal(float f) {
        return factory.createLiteral(f);
    }

    public static Literal literal(double d) {
        return factory.createLiteral(d);
    }

    public static BNode getBNode(String id) {
        return factory.createBNode(
            "node" + MathUtils.md5(id)
        );
    }

    public static Statement triple(Resource s, URI p, Value o) {
        return factory.createStatement(s, p, o);
    }

    public static Statement quad(Resource s, URI p, Value o, Resource g) {
        return factory.createStatement(s, p, o, g);
    }

    public static String toXSDDateTime(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        String s = simpleDateFormat.format(date);
        StringBuilder sb = new StringBuilder(s);
        sb.insert(22, ':');
        return sb.toString();
    }

    public static Graph createNewGraph() {
        return new GraphImpl();
    }

}
