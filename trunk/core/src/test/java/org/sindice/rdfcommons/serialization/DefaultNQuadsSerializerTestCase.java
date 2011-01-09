package org.sindice.rdfcommons.serialization;

import org.sindice.rdfcommons.model.Triple;
import org.sindice.rdfcommons.model.TripleBuffer;
import org.sindice.rdfcommons.model.TripleSet;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.util.GregorianCalendar;

/**
 * Test case for {@link org.sindice.rdfcommons.serialization.NQuadsSerializer}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultNQuadsSerializerTestCase {

    private NQuadsSerializer nQuadsSerializer;

    @BeforeClass
    public void setUp() {
        nQuadsSerializer = new DefaultNQuadsSerializer("http://fake-graph/test");
    }

    @AfterClass
    public void tearDown() {
        nQuadsSerializer = null;
    }

    @Test
    public void testSerializeTripleSet() {
        final GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(2010, 0, 1);
        final TripleSet tripleSet = new TripleBuffer();
        tripleSet.addTriple("http://sub1", "http://pred1", "10", Triple.ObjectType.literal);
        tripleSet.addTriple("http://sub2", "http://pred2", 10, Triple.ObjectType.literal);
        tripleSet.addTriple("http://sub3", "http://pred3", calendar.getTime(), Triple.ObjectType.literal);
        tripleSet.addTriple(
                "bnode4", "http://pred4", calendar.getTime(),
                Triple.SubjectType.bnode, Triple.ObjectType.literal
        );
        tripleSet.addTriple(
                "bnodesub5", "http://pred5","bnodeobj5",
                Triple.SubjectType.bnode, Triple.ObjectType.bnode
        );
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        nQuadsSerializer.serialize(tripleSet, baos);

        System.out.println(baos.toString());
        final String expected =
        "<http://sub1> <http://pred1> \"10\" <http://fake-graph/test>.\n" +
            "<http://sub2> <http://pred2> \"10\"^^<http://www.w3.org/2001/XMLSchema/int> <http://fake-graph/test>.\n" +
            "<http://sub3> <http://pred3> \"2010-01-01\"^^<http://www.w3.org/2001/XMLSchema/date> " +
                "<http://fake-graph/test>.\n" +
            "_:bnode4 <http://pred4> \"2010-01-01\"^^<http://www.w3.org/2001/XMLSchema/date> " +
                "<http://fake-graph/test>.\n" +
            "_:bnodesub5 <http://pred5> _:bnodeobj5 <http://fake-graph/test>.\n";

        Assert.assertEquals(baos.toString(), expected, "Unexpected serialization result.");
    }

}
