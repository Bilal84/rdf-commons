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

package org.sindice.rdfcommons.adapter.jena;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.sindice.rdfcommons.adapter.LiteralFactoryException;
import org.sindice.rdfcommons.parser.RDFParser;
import org.sindice.rdfcommons.parser.RDFParserException;
import org.sindice.rdfcommons.model.TripleSet;

import java.io.InputStream;

/**
 * Default implementation of {@link org.sindice.rdfcommons.parser.RDFParser}.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class JenaRDFXMLParser implements RDFParser {

    private static JenaRDFXMLParser instance;

    /**
     * Lazy constructor.
     * 
     * @return the singleton instance.
     */
    public static final JenaRDFXMLParser getInstance() {
        if(instance == null) {
            instance = new JenaRDFXMLParser();
        }
        return instance;
    }

    /**
     * Singleton.
     */
    private JenaRDFXMLParser() {}

    public TripleSet parse(InputStream is, String defaultGraph)
    throws RDFParserException, LiteralFactoryException {
        Model model = ModelFactory.createDefaultModel();
        try {
            model.read(is, "");
        } catch (Exception e) {
            throw new RDFParserException("An error occurred during stream reading.", e);
        }
        return JenaConversionUtil.getInstance().convertJenaModelToTripleSet(model, defaultGraph);
    }

}
