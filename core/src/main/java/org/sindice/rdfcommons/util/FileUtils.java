/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sindice.rdfcommons.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * General utility file functions.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class FileUtils {

    private FileUtils(){}

    /**
     * Reads the entire char content of an input stream and returns it as a {@link String}.
     *
     * @param is input stream.
     * @return string containing all the content of the input stream.
     * @throws IOException is an error occurs during reading.
     */
    public static String readStream(InputStream is) throws IOException {
        if(is == null) {
            throw new NullPointerException("Input stream cannot be null.");
        }
        final BufferedReader br = new BufferedReader( new InputStreamReader(is) );
        final StringBuilder sb = new StringBuilder();
        String line;
        try {
            while( (line = br.readLine()) != null ) {
                sb.append(line).append('\n');
            }
        } finally {
            br.close();
        }
        return sb.toString();
    }

}
