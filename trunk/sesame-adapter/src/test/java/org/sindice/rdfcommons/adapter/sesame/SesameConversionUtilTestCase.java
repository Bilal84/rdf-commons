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

import org.openrdf.model.Graph;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.XMLSchema;
import org.sindice.rdfcommons.adapter.DefaultDatatypeLiteral;
import org.sindice.rdfcommons.adapter.LiteralFactoryException;
import org.sindice.rdfcommons.model.DatatypeLiteral;
import org.sindice.rdfcommons.model.LanguageLiteral;
import org.sindice.rdfcommons.model.Triple;
import org.sindice.rdfcommons.model.TripleBuffer;
import org.sindice.rdfcommons.model.TripleImpl;
import org.sindice.rdfcommons.model.TripleSet;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Iterator;

/**
 * Test case for {@link org.sindice.rdfcommons.adapter.sesame.SesameConversionUtil}.
 */
public class SesameConversionUtilTestCase {

    @Test
    public void testGetObjectLiteral() throws LiteralFactoryException {
        Object literal;

        literal = SesameConversionUtil.getInstance().getObjectLiteral(new LiteralImpl("A value."));
        Assert.assertTrue(literal instanceof String);
        Assert.assertEquals(literal, "A value.");

        literal = SesameConversionUtil.getInstance().getObjectLiteral(
                new LiteralImpl("10", XMLSchema.INT)
        );
        Assert.assertTrue(literal instanceof DatatypeLiteral);
        Assert.assertEquals(((DatatypeLiteral) literal).getTypedValue(), 10);

        literal = SesameConversionUtil.getInstance().getObjectLiteral(
                new LiteralImpl("Some text", "en")
        );
        Assert.assertTrue(literal instanceof LanguageLiteral);
        Assert.assertEquals(((LanguageLiteral) literal).getValue(), "Some text");
        Assert.assertEquals(((LanguageLiteral) literal).getLanguage(), "en");
    }

    @Test
    public void testConvertSesameStatementToTriple() throws LiteralFactoryException {
        final Statement statement = new ContextStatementImpl(
                new URIImpl("http://sub"),
                new URIImpl("http://pred"),
                new URIImpl("http://obj"),
                new URIImpl("http://graph")
        );
        final Triple triple = SesameConversionUtil.getInstance().convertSesameStatementToTriple(statement);
        Assert.assertEquals(triple.getSubject(), "http://sub");
        Assert.assertEquals(triple.getPredicate(), "http://pred");
        Assert.assertEquals(triple.getObject(),    "http://obj");
        Assert.assertEquals(triple.getGraph(),     "http://graph");
        Assert.assertFalse(triple.isSubjectBNode());
        Assert.assertFalse(triple.isObjectLiteral());
        Assert.assertFalse(triple.isObjectLiteral());
    }

    @Test
    public void testConvertTripleSetToSesameGraph() {
        final Triple triple = new TripleImpl(
                "http://sub",
                "http://pred",
                new DefaultDatatypeLiteral<String>(XMLSchema.DATETIME.stringValue(), "2011-01-01 12:00"),
                "http://graph"
        );
        final TripleSet tripleSet = new TripleBuffer();
        tripleSet.addTriple(triple);

        final Graph graph = SesameConversionUtil.getInstance().convertTripleSetToSesameGraph(tripleSet);
        final Iterator iterator = graph.match(
                SesameConversionUtil.getInstance().createSubjectResource(triple),
                new URIImpl("http://pred"),
                SesameConversionUtil.getInstance().createObjectValue(triple)
        );
        Assert.assertTrue(iterator.hasNext());
        iterator.next();
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testConvertSesameGraphToTripleSet() throws LiteralFactoryException {
        final Graph graph = new org.openrdf.model.impl.GraphImpl();
        final int SIZE = 10;
        for(int i = 0; i < SIZE; i++) {
            graph.add(
                    new URIImpl("http://sub/"     + i),
                    new URIImpl("http://pred/"    + i),
                    new LiteralImpl(Integer.toString(i), XMLSchema.INT),
                    new URIImpl("http://graph")
            );
        }
        final TripleSet ts = SesameConversionUtil.getInstance().convertSesameGraphToTripleSet(graph);
        for (int i = 0; i < SIZE; i++) {
            Assert.assertTrue(
                    ts.containsTriple(
                            "http://sub/" + i,
                            "http://pred/" + i,
                            new DefaultDatatypeLiteral<Integer>(XMLSchema.INT.stringValue(), i),
                            Triple.SubjectType.uri, Triple.ObjectType.literal,
                            "http://graph"
                    )
            );
        }
    }

}
