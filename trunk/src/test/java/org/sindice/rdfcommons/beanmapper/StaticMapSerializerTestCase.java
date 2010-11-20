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

package org.sindice.rdfcommons.beanmapper;

import org.sindice.rdfcommons.beanmapper.annotations.Static;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class StaticMapSerializerTestCase {

    @Static
    class AnnotationClass {
        // Empty.
    }

    @Test
    public void testSerializerClassRecognition() {
        StaticMapSerializer staticMapSerializer = new StaticMapSerializer();
        assert staticMapSerializer.acceptClass(
                HashMap.class, BaseSerializer.getAnnotations( new AnnotationClass() )
        ) : "This class must be accepted.";
        assert ! staticMapSerializer.acceptClass(
                ArrayList.class, BaseSerializer.getAnnotations( new AnnotationClass() )
        ) : "This class must be rejected.";
    }

}
