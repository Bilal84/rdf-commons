/**
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sindice.rdfcommons.tripleconverter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.ntriples.NTriplesParser;
import org.openrdf.rio.ntriples.NTriplesWriter;
import org.openrdf.rio.rdfxml.RDFXMLParser;
import org.openrdf.rio.rdfxml.RDFXMLWriter;
import org.openrdf.rio.trig.TriGParser;
import org.openrdf.rio.trig.TriGWriter;
import org.openrdf.rio.turtle.TurtleParser;
import org.openrdf.rio.turtle.TurtleWriter;
import org.sindice.rdfcommons.tripleconverter.nquads.NQuadsParser;
import org.sindice.rdfcommons.tripleconverter.nquads.NQuadsWriter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

/**
 * This converter is able to transform an input triple format in
 * an output triple format. Supports the main triple formats. 
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TripleConverter {

    public static final String RDFXML_FORMAT   = "RFXML";
    public static final String NTRIPLES_FORMAT = "NTriples";
    public static final String TRIG_FORMAT     = "Trig";
    public static final String TURTLE_FORMAT   = "Turtle";
    public static final String NQUADS_FORMAT   = "NQuads";

    public static final String[] INPUT_FORMATS = new String[]{
            RDFXML_FORMAT, NTRIPLES_FORMAT, TRIG_FORMAT, TURTLE_FORMAT, NQUADS_FORMAT
    }; 

    public static final String[] OUTPUT_FORMATS = new String[]{
            RDFXML_FORMAT, NTRIPLES_FORMAT, TRIG_FORMAT, TURTLE_FORMAT, NQUADS_FORMAT
    };

    public static final String DEFAULT_BASE_URI = "http://sindice.com/base-uri";

    public static void main(String[] args) {
        final TripleConverter tripleConverter = new TripleConverter();
        try {
        tripleConverter.processCommand(args);
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private final Options options;
    private final CommandLineParser parser = new GnuParser();

    @SuppressWarnings({"AccessStaticViaInstance"})
    public TripleConverter() {
        final Option inputFormat = OptionBuilder
                            .withLongOpt("input-format")
                            .withDescription(
                                    String.format("Supported formats are: %s", Arrays.toString(INPUT_FORMATS))
                            )
                            .hasArg()
                            .isRequired()
                            .create("if");

        final Option outputFormat = OptionBuilder
                            .withLongOpt("output-format")
                            .withDescription(
                                    String.format("Supported formats are: %s", Arrays.toString(OUTPUT_FORMATS))
                            )
                            .hasArg()
                            .isRequired()
                            .create("of");

        final Option baseURI = OptionBuilder
                            .withLongOpt("base-uri")
                            .withDescription( String.format("The base uri: default '%s'", DEFAULT_BASE_URI) )
                            .hasArg()
                            .create("bu");

        final Option outputFile = OptionBuilder
                            .withLongOpt("output-file")
                            .withDescription("The output file")
                            .hasArg()
                            .isRequired(false)
                            .create("O");

        options = new Options();
        options.addOption(inputFormat);
        options.addOption(outputFormat);
        options.addOption(outputFile);
        options.addOption(baseURI);
    }

    public void processCommand(String[] args) {
        // Parsing command line.
        CommandLine cl = null;
        try {
            cl = parser.parse(options, args);
        } catch (ParseException pe) {
            printUsageAndExit(pe.getMessage());
        }

        // Input and output formats.
        final String inputFormat  = cl.getOptionValue("if");
        final String outputFormat = cl.getOptionValue("of");
        OutputStream os = null;
        if(cl.hasOption("O")) {
            try {
                os = new BufferedOutputStream( new FileOutputStream(cl.getOptionValue("O"), false) );
            } catch (FileNotFoundException fnfe) {
                printUsageAndExit("Invalid output file.");
            }
        } else {
            os = System.out;
        }
        final RDFParser rdfParser = getParser(inputFormat);
        final RDFWriter rdfWriter = getWriter(outputFormat, os);

        final String baseURI;
        if(cl.hasOption("bu")) {
            baseURI = cl.getOptionValue("bu");
            try {
                new URI(baseURI);
            } catch (URISyntaxException uriase) {
                printUsageAndExit( String.format("Invalid base-uri: '%s'", baseURI) );
            }
        } else {
            baseURI = DEFAULT_BASE_URI;
        }

        // Input file.
        if(cl.getArgs().length != 1) {
            printUsageAndExit("Expected one argument.");
        }
        final File inputFile = new File( cl.getArgs()[0] );
        if( ! inputFile.exists() || inputFile.isDirectory() ) {
            printUsageAndExit("Invalid input file.");
        }

        // Configure RDF parser.
        rdfParser.setRDFHandler(rdfWriter);
        rdfParser.setDatatypeHandling(RDFParser.DatatypeHandling.IGNORE);
        rdfParser.setStopAtFirstError(false);

        // Perform parsing.
        FileInputStream fis;
        try {
            fis = new FileInputStream(inputFile);
        } catch (FileNotFoundException fnfe) {
            throw new RuntimeException("Error while opening file.", fnfe);
        }
        BufferedInputStream bis = new BufferedInputStream(fis);
        try {
            rdfParser.parse(bis, baseURI);
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing file.", e);
        } finally {
            try {
                fis.close();
            } catch (IOException ioe) {
                throw new RuntimeException("Error while closing file buffer.", ioe);
            }
        }
    }

    private void printUsageAndExit(String error) {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(
                150,
                String.format("%s OPTIONS <inputfile>", this.getClass().getName() ),
                "Supported options are:",
                 options,
                "\n"
        );
        System.out.flush();
        throw new RuntimeException(error);
    }

    private RDFParser getParser(String inputFormat) {
        if(RDFXML_FORMAT.equalsIgnoreCase(inputFormat)) {
            return new RDFXMLParser();
        }
        if(NTRIPLES_FORMAT.equalsIgnoreCase(inputFormat)) {
            return new NTriplesParser();
        }
        if(TURTLE_FORMAT.equalsIgnoreCase(inputFormat)) {
            return new TurtleParser();
        }
        if(TRIG_FORMAT.equalsIgnoreCase(inputFormat)) {
            return new TriGParser();
        }
        if(NQUADS_FORMAT.equalsIgnoreCase(inputFormat)) {
            return new NQuadsParser();
        }
        throw new IllegalArgumentException( String.format("Invalid format %s", inputFormat) );
    }

    private RDFWriter getWriter(String outputFormat, OutputStream os) {
        if(RDFXML_FORMAT.equalsIgnoreCase(outputFormat)) {
            return new RDFXMLWriter(os);
        }
        if(NTRIPLES_FORMAT.equalsIgnoreCase(outputFormat)) {
            return new NTriplesWriter(os);
        }
        if(TURTLE_FORMAT.equalsIgnoreCase(outputFormat)) {
            return new TurtleWriter(os);
        }
        if(TRIG_FORMAT.equalsIgnoreCase(outputFormat)) {
            return new TriGWriter(os);
        }
        if(NQUADS_FORMAT.equalsIgnoreCase(outputFormat)) {
            return new NQuadsWriter(os);
        }
        throw new IllegalArgumentException( String.format("Invalid format %s", outputFormat) );
    }

}
