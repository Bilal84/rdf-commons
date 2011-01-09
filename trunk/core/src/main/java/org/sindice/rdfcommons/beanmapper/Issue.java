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

/**
 * Represents a non blocking error occurred while performing binding operations. 
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class Issue {

    private String message;

    private Throwable cause;

    protected Issue(String msg) {
        if(msg == null) {
            throw new NullPointerException();
        }
        message = msg;
    }

    protected Issue(String msg, Throwable t) {
        this(msg);
        if(t == null) {
            throw new NullPointerException();
        }
        cause = t;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getCause() {
        return cause;
    }
    
}
