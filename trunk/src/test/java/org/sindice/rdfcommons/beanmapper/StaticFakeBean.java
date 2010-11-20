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

import org.sindice.rdfcommons.beanmapper.annotations.Ignore;
import org.sindice.rdfcommons.beanmapper.annotations.Property;
import org.sindice.rdfcommons.beanmapper.annotations.Static;

import java.util.Date;

/**
 * Utility bean used to test RDF serialization.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
@Static
public class StaticFakeBean {

    private String field1;

    private boolean field2;

    private int field3;

    private Date field4;

    private String field5;

    private StaticFakeBean fakeBean;

    public StaticFakeBean() {
        field1 = "field1 value";
        field2 = true;
        field3 = 1;
        field4 = new Date();
        field5 = "this should be skipped.";
        fakeBean = this;
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public boolean isField2() {
        return field2;
    }

    public void setField2(boolean field2) {
        this.field2 = field2;
    }

    @Property("http://specific/property#field3")
    public int getField3() {
        return field3;
    }

    @Property("http://specific/property#field3")
    public void setField3(int field3) {
        this.field3 = field3;
    }

    public Date getField4() {
        return field4;
    }

    public void setField4(Date field4) {
        this.field4 = field4;
    }

    @Ignore
    public String getField5() {
        return field5;
    }

    public void setField5(String field5) {
        this.field5 = field5;
    }

    public StaticFakeBean getFakeBean() {
        return fakeBean;
    }

    public void setFakeBean(StaticFakeBean fakeBean) {
        this.fakeBean = fakeBean;
    }

    @Override
    public int hashCode() {
        return
            field1.hashCode()   *
            ( field2 ? 0 : 1)   * 2 *
            field3              * 3 *
            field4.hashCode()   * 5 *
            field5.hashCode()   * 7 *
            (fakeBean == this ? 1 : fakeBean.hashCode() * 11);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(obj == this) {
            return true;
        }
        if(obj instanceof StaticFakeBean) {
            StaticFakeBean other = (StaticFakeBean) obj;
            return
                    field1.equals(other.field1)
                        &&
                    field2 == other.field2
                        &&
                    field3 == other.field3
                        &&
                    field4.getTime() - other.field4.getTime() < 1000
                        &&
                    field5.equals(other.field5)
                        &&
                    (fakeBean == this || fakeBean.equals(other.fakeBean));
        }
        return false;
    }
}
