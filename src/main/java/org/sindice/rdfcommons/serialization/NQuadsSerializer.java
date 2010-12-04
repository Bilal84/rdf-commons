package org.sindice.rdfcommons.serialization;

import org.sindice.rdfcommons.model.TripleSet;

import java.io.OutputStream;

/**
 * Defines an <i>N-Quads</i> serializer for a
 * {@link org.sindice.rdfcommons.model.TripleSet}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface NQuadsSerializer {

    /**
     * Serializes the given triple set.
     *
     * @param tripleSet input triple set.
     * @param os output stream to write serialization.
     */
    void serialize(TripleSet tripleSet, OutputStream os);

}
