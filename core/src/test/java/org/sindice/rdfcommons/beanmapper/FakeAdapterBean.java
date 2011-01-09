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

import org.sindice.rdfcommons.beanmapper.annotations.Adapt;

/**
 * Fake bean used for demonstrating the {@link org.sindice.rdfcommons.beanmapper.annotations.Adapt}
 * annotation.
 *
 * @see org.sindice.rdfcommons.beanmapper.annotations.Adapt
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class FakeAdapterBean {

    private String f1 = "value1";
    private String f2 = "value2";
    private String f3 = "value3";

    public FakeAdapterBean() {
    }

    public String getF1() {
        return f1;
    }

    public void setF1(String f1) {
        this.f1 = f1;
    }

    public String getF2() {
        return f2;
    }

    public void setF2(String f2) {
        this.f2 = f2;
    }

    public String getF3() {
        return f3;
    }

    public void setF3(String f3) {
        this.f3 = f3;
    }

    @Adapt
    public Adapted toAdapted() {
        return new Adapted();
    }

    public class Adapted {

        private String field1 = "adapted_v1";
        private String field2 = "adapted_v2";
        private String field3 = "adapted_v3";

        public Adapted() {
        }

        public String getField1() {
            return field1;
        }

        public void setField1(String field1) {
            this.field1 = field1;
        }

        public String getField2() {
            return field2;
        }

        public void setField2(String field2) {
            this.field2 = field2;
        }

        public String getField3() {
            return field3;
        }

        public void setField3(String field3) {
            this.field3 = field3;
        }
    }

}
