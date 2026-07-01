/**
 * Copyright 2019 www.statefive.org
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
package org.statefive.clic.valuetype;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;

/**
 * Value type to read a byte array from the given file which must exist.
 *
 * <p>
 * <strong>Properties</strong>
 * <ul>
 * <li><strong>encoding</strong>=<i>[encoding] (optional)</i>: specify the
 * encoding for the file to read the data from. If not supplied, the default
 * encoding {@code UTF-8} is used.</li>
 * </ul>
 */
public class DataFileType extends AbstractValueType<byte[], String> {

    /**
     * Type name.
     */
    public static final String DATA_FILE = "datafile";

    /**
     * Data from the file.
     */
    private byte[] fileData;

    /**
     * Encoding; unless set, default is {@code UTF-8}.
     */
    private String encoding = "UTF-8";
    
    /**
     * 
     */
    public DataFileType() {
        setPackageName(null);
        setJavaClassName("byte[]");
        setJavaPrimitiveName(null);
    }

    /**
     * Get the data from the file.
     *
     * @param file a valid, readable existing file to obtain the data from.
     *
     * @return the byte data from the file if it could be obtained.
     *
     * @throws ValueTypeCreationException if the file does not exist or is a
     * directory.
     */
    @Override
    public byte[] getValue(String file) throws ValueTypeCreationException {
        File fileToRead = new File(file);
        if (!fileToRead.exists()) {
            throw new ValueTypeCreationException("Specified file "
                    + fileToRead.getAbsolutePath() + " does not exist.");
        }
        if (fileToRead.isDirectory()) {
            throw new ValueTypeCreationException("Specified file "
                    + fileToRead.getAbsolutePath() + " is a directory.");
        }
        try {
            fileData = Files.readAllBytes(fileToRead.toPath());
        } catch (IOException cex) {
            throw new ValueTypeCreationException(cex.getMessage(), cex);
        }
        return fileData;
    }

    /**
     * Unused.
     *
     * @param data unused.
     *
     * @throws ValueTypeCreationException always - this type does not support default
     * data.
     */
    @Override
    public void setDefault(String data) throws ValueTypeCreationException {
        throw new UnsupportedOperationException(
                "Default data is not provided for data file types.");
    }

    /**
     * Attempt to set the encoding of the file to read.
     *
     * @param properties non-{@code null} properties.
     *
     * @throws ValueTypeCreationException if the properties are invalid.
     */
    @Override
    public void setProperties(String properties) throws ValueTypeCreationException {
        if (!properties.contains("=")) {
            throw new ValueTypeCreationException("Invalid properties: " + properties);
        }
        String[] data = properties.split("=");
        if (!data[0].trim().equals("encoding")) {
            throw new ValueTypeCreationException("Invalid property: "
                    + data[0].trim() + "; expected property 'encoding'");
        }
        encoding = data[1].trim();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValueTypeName() {
        return DATA_FILE;
    }

    /**
     * Get the file data as a string.
     *
     * @return the file data if it could be read; {@code null} if there is an
     * encoding exception.
     */
    @Override
    public String toString() {
        String data = null;
        try {
            data = new String(fileData, encoding);
        } catch (UnsupportedEncodingException ex) {
            // fail gracefully, hope the user has read the docs.
        }
        return data;
    }

}
