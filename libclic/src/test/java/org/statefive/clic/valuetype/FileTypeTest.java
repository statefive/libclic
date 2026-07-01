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
import java.io.IOException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author rich
 */
public class FileTypeTest {

    /**
     * Test of getValue method, of class FileType.
     */
    @Test
    public void testGetFileValue() {
        String filename = "testGetFileValue";
        File expected = new File(filename);
        FileType fileType = new FileType();
        File result = fileType.getValue(filename);
        assertEquals(expected, result);
    }

    /**
     * Test of getValue method, of class FileType.
     */
    @Test
    public void testSetDefault() {
        String filename = "testGetFileValue";
        File expected = new File(filename);
        FileType fileType = new FileType();
        fileType.setDefault(filename);
        assertEquals(expected.getAbsolutePath(), fileType.toString());
    }

    /**
     * Test of exists method, of class FileType.
     */
    @Test
    public void testFileExists() throws IOException {
        String filename = "target/testFileExists";
        File expected = new File(filename);
        expected.createNewFile();
        FileType fileType = new FileType();
        fileType.setProperties("fileType=exists");
        File result = fileType.getValue(filename);
        assertEquals(expected, result);
    }

    /**
     * Test of exists method, of class FileType.
     */
    @Test
    public void testFileNotExists() throws IOException {
        String filename = "target/testDirNotExists";
        File expected = new File(filename);
        FileType fileType = new FileType();
        fileType.setProperties("fileType=!exists");
        File result = fileType.getValue(filename);
        assertEquals(expected, result);
    }

    /**
     * Test of exists method, of class FileType.
     */
    @Test
    public void testFileExistsThrowsException() throws IOException {
        String filename = "target/testFileExistsThrowsException";
        File expected = new File(filename);
        FileType fileType = new FileType();
        fileType.setProperties("fileType=exists");
        try {
            fileType.getValue(filename);
            fail("Expected an exception");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), "Specified file " + filename
                    + " does not exist.");
        }
    }

    /**
     * Test of exists method, of class FileType.
     */
    @Test
    public void testFileAsDirNotExistsThrowsException() throws IOException {
        String dirname = "target/testFileAsDirNotExistsThrowsException";
        File expected = new File(dirname);
        expected.mkdirs();
        FileType fileType = new FileType();
        try {
            fileType.getValue(dirname);
            fail("Expected an exception");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), "File " + dirname
                    + " is a directory.");
        }
    }

    /**
     * Test of exists method, of class FileType.
     */
    @Test
    public void testFileNotExistsThrowsException() throws IOException {
        String filename = "target/testFileNotExistsThrowsException";
        File expected = new File(filename);
        expected.createNewFile();
        FileType fileType = new FileType();
        fileType.setProperties("fileType=!exists");
        try {
            fileType.getValue(filename);
            fail("Expected an exception");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), "Specified file " + filename
                    + " already exists.");
        }
    }

    /**
     * Test of getFileType method, of class FileType.
     */
    @Test
    public void testGetFileTypeForFile() throws Exception {
        String filename = "target/testGetFileTypeForFile";
        File expected = new File(filename);
        expected.createNewFile();
        FileType fileType = new FileType();
        fileType.setProperties("fileType=exists");
        File result = fileType.getValue(filename);
        assertEquals(expected, result);
    }

    /**
     * Test of getFileType method, of class FileType.
     */
    @Test
    public void testGetFileTypeForFileThrowsException() throws Exception {
        String filename = "target/testGetFileTypeForFileThrowsException";
        File expected = new File(filename);
        expected.mkdir();
        FileType fileType = new FileType();
        try {
            fileType.setProperties("fileType=exists");
            fileType.getValue(filename);
            fail("Expected an exception");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), "File " + filename
                    + " is a directory.");
        }
    }

    /**
     * Test of getFileType method, of class FileType.
     */
    @Test
    public void testFileTypeForFileBadPropertyThrowsException() throws Exception {
        String filename = "target/testFileTypeForFileBadPropertyThrowsException";
        File expected = new File(filename);
        expected.mkdir();
        FileType fileType = new FileType();
        try {
            fileType.setProperties("fileType=yeahyeah");
            fileType.getValue(filename);
            fail("Expected an exception");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), "Unknown file type: yeahyeah"
                        + ". Expected " + FileType.EXISTS + " or "
                        + FileType.NOT_EXISTS);
        }
    }

    /**
     * Test of getFileType method, of class FileType.
     */
    @Test
    public void testBadFilePropertyThrowsException() throws Exception {
        String filename = "target/testBadFilePropertyThrowsException";
        File expected = new File(filename);
        expected.mkdir();
        FileType fileType = new FileType();
        try {
            fileType.setProperties("fooType=dir");
        } catch (ValueTypeCreationException cex) {
            assertTrue(cex.getMessage().contains(
                    "Unknown property: fooType=dir"));
        }
    }

    /**
     * Test of getFileType method, of class FileType.
     */
    @Test
    public void testBadFilePropertyAlsoThrowsException() throws Exception {
        String filename = "target/testBadFilePropertyAlsoThrowsException";
        File expected = new File(filename);
        expected.mkdir();
        FileType fileType = new FileType();
        try {
            fileType.setProperties("fooType-dir");
        } catch (ValueTypeCreationException cex) {
            assertTrue(cex.getMessage().contains(
                    "Invalid property: fooType-dir"));
        }
    }
    
    /**
     * 
     */
    @Test
    public void testGetPackageName() {
        FileType fileType = new FileType();
        assertEquals("java.io", fileType.getPackageName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetJavaTypeName() {
        FileType fileType = new FileType();
        assertEquals("File", fileType.getJavaClassName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetValueTypeName() {
        FileType fileType = new FileType();
        assertEquals("file", fileType.getValueTypeName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetJavaPrimitiveName() {
        FileType fileType = new FileType();
        assertNull(fileType.getJavaPrimitiveName());
    }

    /**
     * Test of toString method, of class FileType.
     */
    @Test
    public void testToString() {
        File file = new File("testToString");
        FileType fileType = new FileType();
        fileType.getValue(file.getAbsolutePath());
        assertEquals(file.getAbsolutePath(), fileType.toString());
    }

}
