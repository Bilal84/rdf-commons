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

import org.sindice.rdfcommons.beanmapper.annotations.Namespace;
import org.sindice.rdfcommons.beanmapper.annotations.Property;

/**
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
@Namespace("http://something/strange")
public class DefaultNamespaceBean {

    private String prop1;

    private String prop2;

    DefaultNamespaceBean() {
        prop1 = "value1";
        prop2 = "value2";
    }

    @Property("http://my.default/property")
    public String getProp1() {
        return prop1;
    }

    public void setProp1(String prop1) {
        this.prop1 = prop1;
    }

    public String getProp2() {
        return prop2;
    }

    public void setProp2(String prop2) {
        this.prop2 = prop2;
    }
}
