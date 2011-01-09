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

package org.sindice.rdfcommons.adapter.jena;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.sindice.rdfcommons.adapter.DefaultDatatypeLiteral;
import org.sindice.rdfcommons.adapter.DefaultLanguageLiteral;
import org.sindice.rdfcommons.adapter.LiteralFactoryException;
import org.sindice.rdfcommons.model.DatatypeLiteral;
import org.sindice.rdfcommons.model.LanguageLiteral;
import org.sindice.rdfcommons.model.Triple;
import org.sindice.rdfcommons.model.TripleBuffer;
import org.sindice.rdfcommons.model.TripleImpl;
import org.sindice.rdfcommons.model.TripleSet;
import org.sindice.rdfcommons.storage.TripleStorageFilter;
import org.sindice.rdfcommons.storage.TripleStorageFilterImpl;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for {@link JenaConversionUtil} class.
 */
public class JenaConversionUtilTestCase {

    @Test
    public void testGetObjectType() {
        Assert.assertEquals(
                Triple.ObjectType.uri,
                JenaConversionUtil.getInstance().getObjectType(Node.createURI("http://uritest"))
        );
        Assert.assertEquals(
                Triple.ObjectType.bnode,
                JenaConversionUtil.getInstance().getObjectType(Node.createAnon())
        );
        Assert.assertEquals(
                Triple.ObjectType.literal,
                JenaConversionUtil.getInstance().getObjectType(Node.createLiteral("value"))
        );
    }

    @Test
    public void testGetObjectLiteral() throws LiteralFactoryException {
        Object literal;

        literal = JenaConversionUtil.getInstance().getObjectLiteral(Node.createLiteral("A value."));
        Assert.assertTrue(literal instanceof String);
        Assert.assertEquals(literal, "A value.");

        literal = JenaConversionUtil.getInstance().getObjectLiteral(
                Node.createLiteral("10", null, XSDDatatype.XSDinteger)
        );
        Assert.assertTrue(literal instanceof DatatypeLiteral);
        Assert.assertEquals(((DatatypeLiteral) literal).getTypedValue(), 10);

        literal = JenaConversionUtil.getInstance().getObjectLiteral(Node.createLiteral("Some text", "en", null));
        Assert.assertTrue(literal instanceof LanguageLiteral);
        Assert.assertEquals(((LanguageLiteral) literal).getValue(), "Some text");
        Assert.assertEquals(((LanguageLiteral) literal).getLanguage(), "en");
    }

    @Test
    public void testConvertJenaTripleToTriple() throws LiteralFactoryException {
        final com.hp.hpl.jena.graph.Triple triple1 = new com.hp.hpl.jena.graph.Triple(
                Node.createURI("http://sub"),
                Node.createURI("http://pred"),
                Node.createURI("http://obj")
        );
        final Triple converted1 = JenaConversionUtil
                .getInstance().convertJenaTripleToTriple(triple1, "http://defaultgraph");

        Assert.assertEquals(converted1.getSubject(), "http://sub");
        Assert.assertEquals(converted1.getPredicate(), "http://pred");
        Assert.assertEquals(converted1.getObject(), "http://obj");
        Assert.assertEquals(converted1.getGraph(), "http://defaultgraph");

        final com.hp.hpl.jena.graph.Triple triple2 = new com.hp.hpl.jena.graph.Triple(
                Node.createURI("http://sub"),
                Node.createURI("http://pred"),
                Node.createLiteral("12.3", null, XSDDatatype.XSDfloat)
        );
        final Triple converted2 = JenaConversionUtil
                .getInstance().convertJenaTripleToTriple(triple2, "http://defaultgraph");

        Assert.assertEquals(converted2.getSubject(), "http://sub");
        Assert.assertEquals(converted2.getPredicate(), "http://pred");
        Assert.assertTrue(converted2.getObject() instanceof DatatypeLiteral);
        Assert.assertEquals(((DatatypeLiteral) converted2.getObject()).getTypedValue(), new Float(12.3));
        Assert.assertEquals(converted2.getGraph(), "http://defaultgraph");
    }

    @Test
    public void testCreateSubjectNode() {
        Triple triple;
        Node subNode;

        triple = new TripleImpl<String>(
                "http://sub",
                "http://pred",
                "http://obj"
        );
        subNode = JenaConversionUtil.getInstance().createSubjectNode(triple);
        Assert.assertTrue(subNode.isURI());
        Assert.assertEquals(subNode.getURI(), "http://sub");

        triple = TripleImpl.createBNodeSubjectTriple("http://pred", "http://obj", Triple.ObjectType.uri);
        subNode = JenaConversionUtil.getInstance().createSubjectNode(triple);
        Assert.assertTrue(subNode.isBlank());
    }

    @Test
    public void testCreateObjectNode() {
        Triple triple;
        Node objNode;

        // URI
        triple = new TripleImpl<String>(
                "http://sub",
                "http://pred",
                "http://obj"
        );
        objNode = JenaConversionUtil.getInstance().createObjectNode(triple);
        Assert.assertTrue(objNode.isURI());
        Assert.assertEquals(objNode.getURI(), "http://obj");

        // BNode
        triple = TripleImpl.createBNodeObjectTriple("http://sub", "http://pred", Triple.SubjectType.uri);
        objNode = JenaConversionUtil.getInstance().createObjectNode(triple);
        Assert.assertTrue(objNode.isBlank());

        // Datatype Literal
        triple = new TripleImpl<DefaultDatatypeLiteral>(
                "http://sub",
                "http://pred",
                new DefaultDatatypeLiteral<Float>(XSDDatatype.XSDfloat.getURI(), new Float(12.3)),
                Triple.ObjectType.literal
        );
        objNode = JenaConversionUtil.getInstance().createObjectNode(triple);
        Assert.assertTrue(objNode.isLiteral());
        Assert.assertEquals(objNode.getLiteralLanguage(), "");
        Assert.assertEquals(objNode.getLiteralDatatype().getURI(), XSDDatatype.XSDfloat.getURI());
        Assert.assertEquals(objNode.getLiteralValue(), new Float(12.3));

        // Language Literal
        triple = new TripleImpl<LanguageLiteral>(
                "http://sub",
                "http://pred",
                new DefaultLanguageLiteral("This is the literal text", "en"),
                Triple.ObjectType.literal
        );
        objNode = JenaConversionUtil.getInstance().createObjectNode(triple);
        Assert.assertTrue(objNode.isLiteral());
        Assert.assertNull(objNode.getLiteralDatatype());
        Assert.assertEquals(objNode.getLiteralLanguage(), "en");
        Assert.assertEquals(objNode.getLiteralValue(), "This is the literal text");
    }

    @Test
    public void testConvertTripleToJenaTriple() {
        final Triple triple = new TripleImpl<DefaultDatatypeLiteral>(
                "http://sub",
                "http://pred",
                new DefaultDatatypeLiteral<String>("http://mydatatype", "literal value"),
                Triple.ObjectType.literal
        );
        final com.hp.hpl.jena.graph.Triple converted =
                JenaConversionUtil.getInstance().convertTripleToJenaTriple(triple);
        Assert.assertEquals(converted.getSubject().getURI(), "http://sub");
        Assert.assertEquals(converted.getPredicate().getURI(), "http://pred");
        Assert.assertEquals(converted.getObject().getLiteralDatatype().getURI(), "http://mydatatype");
        Assert.assertEquals(converted.getObject().getLiteralLexicalForm(), "literal value");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConvertToJenaTriples() {
        final TripleSet tripleSet = new TripleBuffer();
        for (int i = 0; i < 10; i++) {
            tripleSet.addTriple( new TripleImpl("http://sub" + i, "http://pred" + i, "http://obj" + i) );
        }

        com.hp.hpl.jena.graph.Triple[] convertedTriples =
                JenaConversionUtil.getInstance().convertToJenaTriples(tripleSet);

        Assert.assertEquals(convertedTriples.length, 10);
        for(int i = 0; i < 10; i++) {
            Assert.assertEquals( convertedTriples[i].getSubject().toString(), "http://sub" + i);
            Assert.assertEquals( convertedTriples[i].getPredicate().toString(), "http://pred" + i);
            Assert.assertEquals( convertedTriples[i].getObject().toString(), "http://obj" + i);
        }
    }

    @Test
    public void testConvertJenaModelToTripleSet() throws LiteralFactoryException {
        final Model model = createFakeJenaModel();
        final TripleSet tripleSet =
                JenaConversionUtil.getInstance().convertJenaModelToTripleSet(model, "http://defaultgraph");
        for(int i = 0; i < 10; i++) {
            tripleSet.containsTriple(
                    "http://subject/" + i,
                    "http://property/" + i,
                    "http://object/" + i,
                    Triple.SubjectType.uri,
                    Triple.ObjectType.uri
            );
        }
    }

    @Test
    public void testConvertTripleSetToJenaModel() {
        final Model model = JenaConversionUtil.getInstance().convertTripleSetToJenaModel(createFakeTripleSet());
        for (int i = 0; i < 10; i++) {
            model.contains(
                    model.createResource("http://subject/"  + i),
                    model.createProperty("http://property/" + i),
                    model.createResource("http://object/"   + i)
            );
        }
    }

    @Test
    public void testConvertToJenaTripleMatch() {
        TripleStorageFilter tsf;
        TripleMatch tm;

        tsf = new TripleStorageFilterImpl(
                "http://sub", "http://pred", "http://obj",
                Triple.SubjectType.uri, Triple.ObjectType.uri
        );
        tm = JenaConversionUtil.getInstance().convertToJenaTripleMatch(tsf);
        Assert.assertTrue( tm.getMatchSubject().isURI() );
        Assert.assertEquals(tm.getMatchSubject().toString(), "http://sub");
        Assert.assertTrue( tm.getMatchPredicate().isURI() );
        Assert.assertEquals(tm.getMatchPredicate().toString(), "http://pred");
        Assert.assertTrue(tm.getMatchObject().isURI());
        Assert.assertEquals(tm.getMatchObject().toString(), "http://obj");

        tsf = new TripleStorageFilterImpl(
                "http://sub", "http://pred", "text",
                Triple.SubjectType.uri, Triple.ObjectType.literal
        );
        tm = JenaConversionUtil.getInstance().convertToJenaTripleMatch(tsf);
        Assert.assertTrue( tm.getMatchSubject().isURI() );
        Assert.assertEquals(tm.getMatchSubject().toString(), "http://sub");
        Assert.assertTrue( tm.getMatchPredicate().isURI() );
        Assert.assertEquals(tm.getMatchPredicate().toString(), "http://pred");
        Assert.assertTrue( tm.getMatchObject().isLiteral() );
        Assert.assertEquals(tm.getMatchObject().getLiteralLexicalForm(), "text");
    }

    private Model createFakeJenaModel() {
        final Model model = ModelFactory.createDefaultModel();
        for (int i = 0; i < 10; i++) {
            model.add(
                    model.createResource("http://subject/"  + i),
                    model.createProperty("http://property/" + i),
                    model.createResource("http://object/"   + i)
            );
        }
        return model;
    }

    private TripleSet createFakeTripleSet() {
        final TripleSet tripleSet = new TripleBuffer();
        for (int i = 0; i < 10; i++) {
            tripleSet.addTriple(
                    "http://subject/"  + i,
                    "http://property/" + i,
                    "http://object/"   + i
            );
        }
        return tripleSet;
    }

}
