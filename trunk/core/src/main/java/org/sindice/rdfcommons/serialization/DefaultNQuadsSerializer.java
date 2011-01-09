package org.sindice.rdfcommons.serialization;

import org.sindice.rdfcommons.model.Triple;
import org.sindice.rdfcommons.model.TripleSet;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Default implementation of
 * {@link org.sindice.rdfcommons.serialization.NQuadsSerializer}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultNQuadsSerializer implements NQuadsSerializer {

    private final String graph;

    public DefaultNQuadsSerializer(String graph) {
        if(graph == null) {
            throw new IllegalArgumentException("The graph is invalid.");
        }
        this.graph = graph;
    }

    public void serialize(final TripleSet tripleSet, final OutputStream os) {
        final PrintStream ps = new PrintStream(os);
        try {
            printNQuads(tripleSet, ps);
        } finally {
            ps.flush();
        }
    }

    private void printNQuads(TripleSet ts, PrintStream ps) {
        for(Triple triple : ts) {
            serialize(triple, ps);
        }
    }

    private void serialize(final Triple triple, final PrintStream ps) {
        ps.print( triple.toNTriplesString() );
        ps.print(' ');
        ps.print('<');
        ps.print(graph);
        ps.print('>');
        ps.print('.');
        ps.print('\n');
    }

}
