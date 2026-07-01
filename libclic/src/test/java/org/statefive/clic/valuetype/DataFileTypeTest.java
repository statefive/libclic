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
import java.io.FileOutputStream;
import java.io.IOException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author rich
 */
public class DataFileTypeTest {

    /**
     * Test of getValue method, of class FileType.
     */
    @Test
    public void testGetValue() throws Exception {
        String expected = "target/testGetValue";
        File fileToWrite = new File(expected);
        DataFileType fileType = new DataFileType();
        writeData(expected, null, fileToWrite);
        byte[] result = fileType.getValue(fileToWrite.getAbsolutePath());
        assertEquals(expected, new String(result));
    }

    /**
     * Test of getValue method, of class FileType.
     */
    @Test
    public void testGetValueNoSuchFileThrowsException() throws Exception {
        String expected = "target/testGetValueNoSuchFileThrowsException";
        File dir = new File(expected);
        DataFileType fileType = new DataFileType();

        try {
            fileType.getValue(dir.getAbsolutePath());
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), "Specified file "
                    + dir.getAbsolutePath() + " does not exist.");
        }
    }

    /**
     * Test of getValue method, of class FileType.
     */
    @Test
    public void testGetValueAsDirThrowsException() throws Exception {
        String expected = "target/testGetValueAsDirThrowsException";
        File dir = new File(expected);
        DataFileType fileType = new DataFileType();
        dir.mkdir();
        try {
            fileType.getValue(dir.getAbsolutePath());
            fail("Expected an exception.");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), "Specified file "
                    + dir.getAbsolutePath() + " is a directory.");
        }
    }

    /**
     * Test of getValue method, of class FileType.
     */
    @Test
    public void testEncodingExceptionReturnsNullForToString() throws Exception {
        String expected = "target/testEncodingExceptionReturnsNullForToString";
        File fileToWrite = new File(expected);
        DataFileType fileType = new DataFileType();
        writeData(expected, null, fileToWrite);
        byte[] result = fileType.getValue(fileToWrite.getAbsolutePath());
        fileType.setProperties("encoding=\\?");
        assertEquals(null, fileType.toString());
    }

    /**
     * Test of getValue method, of class FileType.
     */
    @Test
    public void testSetPropertiesThrowsException() throws Exception {
        DataFileType fileType = new DataFileType();
        try {
            fileType.setProperties("encoding");
        } catch (ValueTypeCreationException ex) {
            assertEquals("Invalid properties: encoding", ex.getMessage());
        }
    }

    /**
     * Test of getValue method, of class FileType.
     */
    @Test
    public void testSetPropertiesWrongPropKeyThrowsException() throws Exception {
        DataFileType fileType = new DataFileType();
        try {
            fileType.setProperties("no-such-property=x");
        } catch (ValueTypeCreationException ex) {
            assertEquals("Invalid property: no-such-property; expected property 'encoding'", ex.getMessage());
        }
    }

    /**
     * Test of toString method, of class FileType.
     */
    @Test
    public void testToString() throws Exception {
        String expected = "target/testToString";
        File fileToWrite = new File(expected);
        DataFileType fileType = new DataFileType();
        writeData(expected, null, fileToWrite);
        fileType.getValue(fileToWrite.getAbsolutePath());
        assertEquals(expected, fileType.toString());
    }

    /**
     * Test that attempting to set a default value throws an exception.
     */
    @Test
    public void testSetDefaultThrowsException() throws Exception {
        try {
            DataFileType fileType = new DataFileType();
            fileType.setDefault("cannot be done");
            fail("Expected an exception");
        } catch (UnsupportedOperationException ex) {
            assertEquals("Default data is not provided for data file types.",
                    ex.getMessage());
        }
    }
    
    /**
     * 
     */
    @Test
    public void testGetPackageName() {
        DataFileType dataFileType = new DataFileType();
        assertNull(dataFileType.getPackageName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetJavaTypeName() {
        DataFileType dataFileType = new DataFileType();
        assertEquals("byte[]", dataFileType.getJavaClassName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetValueTypeName() {
        DataFileType dataFileType = new DataFileType();
        assertEquals("datafile", dataFileType.getValueTypeName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetJavaPrimitiveName() {
        DataFileType dataFileType = new DataFileType();
        assertNull(dataFileType.getJavaPrimitiveName());
    }

    /**
     *
     * @param data
     * @param encoding
     * @param fileToWrite
     * @throws IOException
     */
    private void writeData(String data, String encoding, File fileToWrite)
            throws IOException {
        String enc = "UTF-8";
        if (encoding != null) {
            enc = encoding;
        }
        FileOutputStream fos = new FileOutputStream(fileToWrite);
        fos.write(data.getBytes());
        fos.close();
    }

}
