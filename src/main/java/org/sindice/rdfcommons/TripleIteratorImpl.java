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

package org.sindice.rdfcommons;

import java.util.Iterator;

/**
 * Default implementation of {@link TripleIterator}.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class TripleIteratorImpl implements TripleIterator {

    private Iterator<Triple> iterator;

    private Triple next;

    public TripleIteratorImpl(Iterator<Triple> iterator) {
        this.iterator = iterator;
        this.next     = null;
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public void next() {
        next = iterator.next();
    }

    public String subject() {
        return next.getSubject();
    }

    public String predicate() {
        return next.getPredicate();
    }

    public Object object() {
        return next.getObject();
    }

    public String objectStr() {
        return next.getObjectAsString();
    }

    public boolean isBlankSubject() {
        return next.isBlankSubject();
    }

    public boolean isBlankObject() {
        return next.isBlankObject();
    }

    public boolean isObjLiteral() {
        return next.isObjLiteral();
    }
}
