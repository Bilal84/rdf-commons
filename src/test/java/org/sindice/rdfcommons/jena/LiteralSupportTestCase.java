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

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.testng.annotations.Test;

/**
 * Test case to validate the Jena literal support.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class LiteralSupportTestCase {

    /**
     * Tests the literal boxing and unboxing capabilities.
     */
    @Test
    public void testBasicLiteralBoxingUnboxing() {
        final double value = 123.456;
        Model model = ModelFactory.createDefaultModel();
        Literal  obj = model.createTypedLiteral(value);
        assert obj.getValue().equals(value);
        assert obj.getString().equals( Double.toString(value) );
        assert obj.getLexicalForm().equals( Double.toString(value) );
        assert obj.getDatatypeURI().equals("http://www.w3.org/2001/XMLSchema#double");
    }

}
