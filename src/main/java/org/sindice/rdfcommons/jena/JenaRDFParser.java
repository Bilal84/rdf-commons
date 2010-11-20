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

package org.sindice.rdfcommons.jena;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.sindice.rdfcommons.RDFParser;
import org.sindice.rdfcommons.RDFParserException;
import org.sindice.rdfcommons.TripleSet;

import java.io.InputStream;

/**
 * Default implementation of {@link org.sindice.rdfcommons.RDFParser}.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class JenaRDFParser implements RDFParser {

    private static JenaRDFParser instance;

    /**
     * Lazy constructor.
     * 
     * @return the singleton instance.
     */
    public static final JenaRDFParser getInstance() {
        if(instance == null) {
            instance = new JenaRDFParser();
        }
        return instance;
    }

    /**
     * Singleton.
     */
    private JenaRDFParser() {}

    public TripleSet parse(InputStream is) throws RDFParserException {
        Model model = ModelFactory.createDefaultModel();
        try {
            model.read(is, "");
        } catch (Exception e) {
            throw new RDFParserException("An error occurred during stream reading.", e);
        }
        return JenaConversionUtil.convertJenaModelToTripleSet(model);
    }

}
