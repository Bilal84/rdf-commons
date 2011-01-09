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
import org.sindice.rdfcommons.beanmapper.annotations.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is meant to test the serialization of annotated collections.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 * @see SerializerManagerTestCase
 */
@Type("http://fake/Collection")
public class FakeCollectionBean {

    private List<CollectionItem> items;

    FakeCollectionBean() {
        items = new ArrayList<CollectionItem>();
        items.add( new CollectionItem() );
    }

    @Property("http://fake/hasItem")
    public List<CollectionItem> getItems() {
        return items;
    }

    @Type("http://fake/Item")
    class CollectionItem {

    }

}
