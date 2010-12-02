/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sindice.rdfcommons.serialization;

import org.sindice.rdfcommons.storage.ResultSet;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Default implementation of {@link ResultSetXMLSerializer}. This implementation
 * is compliant to the <a href="http://www.w3.org/TR/rdfcommons-sparql-XMLres">SPARQL Query Results XML Format</a> specification.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class DefaultResultSetXMLSerializer implements ResultSetXMLSerializer {

    public void serialize(ResultSet rs, OutputStream os) {
        PrintWriter pw = new PrintWriter(os);
        try {
            pw.write("<sparql xmlns=\"http://www.w3.org/2005/sparql-results#\">");

            // Writing results.
            pw.write("<results>");
            while (rs.hasNext()) {
                rs.next();
                printResult(rs, pw);
            }
            pw.write("</results>");

            // Writing head.
            pw.write("<head>");
            for( String variable : rs.getVariables() ) {
                pw.write( String.format("<variable name=\"%s\"/>", variable) );
            }
            pw.write("</head>");
            pw.write("</sparql>");
        } finally {
            pw.close();
        }
    }

    private void printResult(ResultSet rs, PrintWriter pw) {
        String[] vars = rs.getVariables();

        pw.write("<result>");

        // Printing bindings.
        for(String var : vars) {
            pw.write( String.format("<binding name=\"%s\">", var) );
            printBindingValue(rs, var, pw);
            pw.write("</binding>");
        }

        pw.write("</result>");
    }

    private void printBindingValue(ResultSet rs, String var, PrintWriter pw) {
        final Object value =  rs.getVariableValue(var);
        switch(rs.getVariableType(var)) {
            case RESOURCE:
                pw.write( String.format("<uri>%s</uri>", value) );
                return;
            case BLANK:
                pw.write( String.format("<bnode>%s</bnode>", value) );
                return;
            case LITERAL:
                pw.write( String.format("<literal xml:lang=\"en\">%s</literal>", value) );
                return;
            default:
                throw new IllegalArgumentException();
        }
    }


}
