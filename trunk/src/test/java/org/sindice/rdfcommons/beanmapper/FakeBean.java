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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class FakeBean {

    public enum FakeEnum {
        ENUM1,
        ENUM2
    }

    private String field1;

    private boolean field2;

    private int field3;

    private Date field4;

    private URL field5;

    private float field6;

    private List<FakeAggregateBean> aggregateBeans;

    private FakeEnum fakeEnum;

    private FakeBean self;

    public FakeBean() {
        this.field1 = "value1";
        this.field2 = true;
        this.field3 = 11;
        this.field4 = new Date();
        try {
            this.field5 = new URL("http://www.afakeulr.com/aqua");
        } catch (MalformedURLException murle) {
            throw new RuntimeException(murle);
        }
        this.field6 = 3.1415f;
        aggregateBeans = Arrays.asList( new FakeAggregateBean(), new FakeAggregateBean() );
        fakeEnum = FakeEnum.ENUM1;
        this.self = this;
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

    public int getField3() {
        return field3;
    }

    public void setField3(int field3) {
        this.field3 = field3;
    }

    public Date getField4() {
        return field4;
    }

    public void setField4(Date field4) {
        this.field4 = field4;
    }

    public URL getField5() {
        return field5;
    }

    public void setField5(URL field5) {
        this.field5 = field5;
    }

    public float getField6() {
        return field6;
    }

    public void setField6(float field6) {
        this.field6 = field6;
    }

    public List<FakeAggregateBean> getInnerBeans() {
        return aggregateBeans;
    }

    public void setInnerBeans(List<FakeAggregateBean> aggregateBeans) {
        this.aggregateBeans = aggregateBeans;
    }

    public FakeEnum getFakeEnum() {
        return fakeEnum;
    }

    public void setFakeEnum(FakeEnum fakeEnum) {
        this.fakeEnum = fakeEnum;
    }

    public FakeBean getSelf() {
        return self;
    }

    public void setSelf(FakeBean self) {
        this.self = self;
    }

    @Override
    public int hashCode() {
        return (int) (
                ( field1 == null ? 1 : field1.hashCode() ) *
                ( field2 ? 2 : 1 ) *
                ( field3 * 2 ) *
                ( field4 == null ? 1 : field4.hashCode() * 3 ) *
                ( field5 == null ? 1 : field5.hashCode() * 5 ) *
                ( field6 * 7 ) *
                ( aggregateBeans == null ? 1 : aggregateBeans.hashCode() ) *
                ( fakeEnum == null ? 1 : fakeEnum.hashCode() * 11) );
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(obj == this) {
            return true;
        }
        if(obj instanceof FakeBean) {
            FakeBean other = (FakeBean) obj;
            return
                    field1 != null ? field1.equals(other.field1) : other.field1 == null
                    &&
                    field2 == other.field2
                    &&
                    field3 == other.field3
                    &&
                    field4 != null ? field4.equals(other.field4) : other.field4 == null
                    &&
                    field5 != null ? field5.equals(other.field5) : other.field5 == null
                    &&
                    field6 == other.field6
                    &&
                    aggregateBeans != null ? aggregateBeans.equals(other.aggregateBeans) : other.aggregateBeans == null
                    &&
                    fakeEnum == other.fakeEnum;
        }
        return false;
    }
}
