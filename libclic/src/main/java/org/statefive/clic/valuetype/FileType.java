/**
 * Copyright 2019 www.statefive.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.statefive.clic.valuetype;

import java.io.File;

/**
 * Value type representing a file; by default, no checks are made, although
 * users can specify the file type property that will perform necessary checks.
 *
 * <p>
 * <strong>Properties</strong>
 * <ul>
 * <li><strong>fileType</strong>=<i>[type]</i> (optional): one of:
 * <ul>
 * <li><strong>exists</strong>: the created type must exist as a file on the
 * file system;</li>
 * <li><strong>!exists</strong>: the created type must not exist as a file on
 * the file system;</li>
 * </ul>
 * If not supplied, no checks are performed whether the file exists or if it's a
 * directory or file.
 * </li>
 * </ul>
 */
public class FileType extends AbstractValueType<File, String> {

    /**
     * Type name.
     */
    public static final String FILE = "file";

    /**
     * Exists string for both file and {@link DirType}s.
     */
    public static final String EXISTS = "exists";

    /**
     * Not-exists string for both file and {@link DirType}s.
     */
    public static final String NOT_EXISTS = "!" + EXISTS;

    /**
     * File type to determine if the file should exist or not.
     */
    private static final String FILE_TYPE = "fileType";

    /**
     * The file constructed from this type.
     */
    private File file;

    /**
     * By default, this type does not check for the existence (or non-existence)
     * of any created file/directory; by setting this to {@code true}, callers
     * imply any file created must exist and must be a file, or a exception will
     * be raised; conversely, setting it to {@code false} implies the caller
     * wants to ensure that the specified file does not already exist.
     */
    private Boolean exists = null;
    
    /**
     * 
     */
    public FileType() {
        setPackageName(File.class.getPackageName());
        setJavaClassName(File.class.getSimpleName());
        setJavaPrimitiveName(null);
    }

    /**
     * Get the file from the specified data. The data is converted to a Java
     * {@code java.io.File} object and then necessary checks made against any
     * properties supplied.
     *
     * @param data non-{@code null} data representing a file.
     *
     * @return the file it it could be constructed.
     *
     * @throws ValueTypeCreationException if any of the specified properties
     * conflict with the created file, or the file is an existing directory
     * (which conflicts with the type being a file type).
     */
    @Override
    public File getValue(String data) throws ValueTypeCreationException {
        file = new File(data);
        if (file.isDirectory()) {
            throw new ValueTypeCreationException("File " + data
                    + " is a directory.");
        }
        if (exists != null) {
            if (exists == true && !file.exists()) {
                throw new ValueTypeCreationException("Specified file " + data
                        + " does not exist.");
            } else if (exists == false && file.exists()) {
                throw new ValueTypeCreationException("Specified file " + data
                        + " already exists.");
            }
        }
        return file;
    }

    /**
     * As {@link #getValue(java.lang.String)}.
     *
     * @param data non-{@code null} data representing a file.
     *
     * @throws ValueTypeCreationException as
     * {@link #getValue(java.lang.String)}.
     */
    @Override
    public void setDefault(String data) throws ValueTypeCreationException {
        getValue(data);
    }

    /**
     * Construct the file according to the specified properties.
     *
     * @param property valid file type properties or {@code null} if no
     * properties are used.
     *
     * @throws ValueTypeCreationException if the properties cannot be parsed, or
     * if the parsed properties do not conform to the specified file (for
     * example, a directory that exists but the file type was {@code !exists}.
     */
    @Override
    public void setProperties(String property) throws ValueTypeCreationException {
        if (!property.contains("=")) {
            throw new ValueTypeCreationException("Invalid property: "
                    + property);
        }
        String[] data = property.split("=");
        if (FILE_TYPE.equals(data[0].trim())) {
            switch (data[1].trim()) {
                case FileType.EXISTS:
                    exists = true;
                    break;
                case FileType.NOT_EXISTS:
                    exists = false;
                    break;
                default:
                    throw new ValueTypeCreationException(
                            "Unknown file type: " + data[1]
                            + ". Expected " + FileType.EXISTS + " or "
                            + FileType.NOT_EXISTS);
            }
        } else {
            throw new ValueTypeCreationException("Unknown property: " + property);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValueTypeName() {
        return FILE;
    }

    /**
     * Return a string representation of the file.
     *
     * @return the file as a string as absolute path.
     */
    @Override
    public String toString() {
        return file.getAbsolutePath();
    }

}
