package org.sindice.rdfcommons.vocabulary;

import java.text.SimpleDateFormat;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class XMLSchemaTypes {

    public static final String XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

    public static final String BOOLEAN = XML_SCHEMA + "/boolean";
    public static final String BYTE    = XML_SCHEMA + "/byte";
    public static final String SHORT   = XML_SCHEMA + "/short";
    public static final String INT     = XML_SCHEMA + "/int";
    public static final String LONG    = XML_SCHEMA + "/long";
    public static final String FLOAT   = XML_SCHEMA + "/float";
    public static final String DOUBLE  = XML_SCHEMA + "/double";
    public static final String STRING  = XML_SCHEMA + "/string";
    public static final String DATE    = XML_SCHEMA + "/date";

    public static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
}
