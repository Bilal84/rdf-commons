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

package org.sindice.rdfcommons.storage;

/**
 * Defines an exception raised during the storaging of the result of
 * a triple.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public class StorageException extends Exception {

    public StorageException() {
        super();
    }

    public StorageException(String s) {
        super(s);
    }

    public StorageException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public StorageException(Throwable throwable) {
        super(throwable);
    }
}