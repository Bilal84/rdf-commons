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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Useful base class for writing a {@link Deserializer}.
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
public abstract class BaseDeserializer extends BaseMapper implements CriteriaDeserializer {

    private static final String URL_REGEX =
            "^http[s]?:\\/?\\/?([^:\\/\\s]+)(:([^\\/]*))?((\\/\\w+)*\\/)([\\w\\-\\.]+[^#?\\s]+)(\\?([^#]*))?(#(.*))?$";

    private static final int PACK_INDEX  = 1;
    private static final int CLASS_INDEX = 6;

    /**
     * Returns the class represented by a given URL.
     * This method is complementary to {BaseMapper#getClassURL() }
     *
     * @param url URL to be converted to related class.
     * @return the class associated to the URL.
     */
    protected static String urlToClass(String url) {
        Pattern pattern = Pattern.compile(URL_REGEX);
        Matcher matcher = pattern.matcher(url);
        if( !matcher.find()) {
            throw new IllegalArgumentException("Unexpected URL '" + url + "'");
        }
        String pack  = matcher.group(PACK_INDEX);
        String clazz = matcher.group(CLASS_INDEX);
        return pack + "." + clazz;
    }

}
