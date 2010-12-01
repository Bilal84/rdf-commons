package org.sindice.rdfcommons;

/**
 * Declares any {@link org.sindice.rdfcommons.Triple}
 * object literal with language attribute.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface LanguageLiteral {

    /**
     * Returns the literal language.
     *
     * @return a string representing a language.
     */
    String getLanguage();

}
