package org.sindice.rdfcommons.storage.virtuoso.sesame;

import virtuoso.sesame2.driver.VirtuosoRepository;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TestUtil {

    protected static final String NAMED_GRAPH_URI = "http://test/named/graph/sesame";

    private TestUtil() {}

    /**
     * @return a named instance of the virtuoso graph.
     */
    public static VirtuosoRepository getTestRepository() {
		final String url = "jdbc:virtuoso://localhost:1111";
        return new VirtuosoRepository(url, "dba", "dba", NAMED_GRAPH_URI);
    }

}
