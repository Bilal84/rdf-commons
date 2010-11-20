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

import org.sindice.rdfcommons.beanmapper.annotations.Property;
import org.sindice.rdfcommons.beanmapper.annotations.Static;
import org.sindice.rdfcommons.beanmapper.annotations.Subject;

/**
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
@Static
@Subject("http://path/to#Class")
public class CustomSubjectBean {

    private String field1;

    private String field2;

    public CustomSubjectBean() {
        field1 = "value1";
        field2 = "value2";
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    @Property("http://custom/predicate")
    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }
}
