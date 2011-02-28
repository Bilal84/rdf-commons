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

package org.sindice.rdfcommons.converter;

import org.xml.sax.Locator;

import java.util.Stack;

/**
 * Default implementation of {@link XMLLocation}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultXMLLocation implements XMLLocation {

    private final Stack<String> path = new Stack<String>();

    private final Locator locator;

    private final StringBuilder pathSB = new StringBuilder();
    private String pathStr = null;

    /**
     * Constructor.
     *
     * @param locator the SAX parser {@link Locator}.
     */
    public DefaultXMLLocation(Locator locator) {
        this.locator = locator;
    }

    /**
     * Pushes a node within.
     *
     * @param node
     */
    public void push(String node) {
        path.push(node);
        pathStr = null;
    }

    /**
     * Pops out to the previous node.
     */
    public void pop() {
        path.pop();
        pathStr = null;
    }

    @Override
    public int row() {
        return locator.getLineNumber();
    }

    @Override
    public int col() {
        return locator.getColumnNumber();
    }

    @Override
    public String path() {
        if(pathStr != null) {
            return pathStr;
        }
        pathSB.delete(0, pathSB.length());
        for(String node : path) {
            pathSB.append(node).append('/');
        }
        pathStr = pathSB.toString();
        return pathStr;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " " + path();
    }
}
