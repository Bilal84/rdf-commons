package org.sindice.rdfcommons.model;

/**
 * Models a generic <i>RDF triple</i> literal.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface Literal {

    /**
     * Returns the literal value.
     *
     * @return the literal value.
     */
    String getValue();

}
