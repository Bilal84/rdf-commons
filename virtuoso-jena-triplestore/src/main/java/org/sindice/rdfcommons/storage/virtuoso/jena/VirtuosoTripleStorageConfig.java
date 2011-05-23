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

package org.sindice.rdfcommons.storage.virtuoso.jena;

import org.sindice.rdfcommons.storage.TripleStorageConfig;

/**
 * <i>Virtuoso</i> triple storage configuration.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id$
 */
public class VirtuosoTripleStorageConfig implements TripleStorageConfig {

    /**
     * URL locating the triple storage.
     */
    private String url;

    /**
     * Name of default graph.
     */
    private String defaultGraphName;

    /**
     * Connection username.
     */
    private String username;

    /**
     * Connection password.
     */
    private String password;

    /**
     * Constructor.
     *
     * @param url URL locating the triple storage.
     * @param defaultGraphName name of default graph.
     * @param username connection username.
     * @param password connection password.
     */
    public VirtuosoTripleStorageConfig(String url, String defaultGraphName, String username, String password) {
        if(url == null) {
            throw new IllegalArgumentException("invalid url");
        }
        if(defaultGraphName == null) {
            throw new IllegalArgumentException("invalid defaultGraphName");
        }
        if(username == null) {
            throw new IllegalArgumentException("invalid username");
        }
        if(password == null) {
            throw new IllegalArgumentException("invalid password");
        }
        this.url       = url;
        this.defaultGraphName = defaultGraphName;
        this.username  = username;
        this.password  = password;
    }

    /**
     * @return the connection URL.
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return the defualt graph name.
     */
    public String getDefaultGraphName() {
        return defaultGraphName;
    }

    /**
     * @return the default username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return the default password.
     */
    public String getPassword() {
        return password;
    }

}
