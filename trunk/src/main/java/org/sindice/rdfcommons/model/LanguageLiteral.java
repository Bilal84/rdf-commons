package org.sindice.rdfcommons.model;

/**
 * Declares any {@link org.sindice.rdfcommons.model.Triple}
 * object literal with language attribute.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface LanguageLiteral extends Literal {

    /**
     * Returns the literal language.
     *
     * @return a string representing a language.
     */
    String getLanguage();

}
