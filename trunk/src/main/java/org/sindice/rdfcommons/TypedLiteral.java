package org.sindice.rdfcommons;

/**
 * Declares any typed
 * {@link org.sindice.rdfcommons.Triple} object literal.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface TypedLiteral {

    /**
     * Returns the literal type.
     *
     * @return a valid <i>URI</i> representing a literal type.
     */
    String getType();

}
