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
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author rich
 */
public class DirTypeTest {

    /**
     *
     */
    private final List<File> filesFound = new ArrayList<>();

    /**
     *
     */
    @Before
    public void setUp() {
        filesFound.clear();
    }

    /**
     * Test of getValue method, of class FileType.
     */
    @Test
    public void testGetDirValue() {
        String dirname = "testGetDirValue";
        File expected = new File(dirname);
        DirType dirType = new DirType();
        File result = dirType.getValue(dirname);
        assertEquals(expected, result);
    }

    /**
     * Test of exists method, of class FileType.
     */
    @Test
    public void testDirExists() throws IOException {
        String dirname = "target/testDirExists";
        File expected = new File(dirname);
        expected.mkdir();
        DirType dirType = new DirType();
        dirType.setProperties("dirType=exists");
        File result = dirType.getValue(dirname);
        assertEquals(expected, result);
    }

    /**
     * Test of exists method, of class FileType.
     */
    @Test
    public void testDirNotExists() throws IOException {
        String dirname = "target/testDirNotExists";
        File expected = new File(dirname);
        DirType dirType = new DirType();
        dirType.setProperties("dirType=!exists");
        File result = dirType.getValue(dirname);
        assertEquals(expected, result);
    }

    /**
     * Test of exists method, of class FileType.
     */
    @Test
    public void testDirExistsThrowsException() throws IOException {
        String dirname = "target/testDirExistsThrowsException";
        File expected = new File(dirname);
        DirType dirType = new DirType();
        dirType.setProperties("dirType=exists");
        try {
            dirType.getValue(dirname);
            fail("Expected an exception");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), "Specified directory " + dirname
                    + " does not exist.");
        }
    }

    /**
     * Test of exists method, of class FileType.
     */
    @Test
    public void testDirAsAFileNotExistsThrowsException() throws IOException {
        String dirname = "target/testDirAsAFileNotExistsThrowsException";
        File expected = new File(dirname);
        expected.createNewFile();
        DirType dirType = new DirType();
        try {
            dirType.getValue(dirname);
            fail("Expected an exception");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), "Directory " + dirname
                    + " is a file.");
        }
    }

    /**
     * Test of exists method, of class FileType.
     */
    @Test
    public void testSetDefaultDirAsAFileNotExistsThrowsException() throws IOException {
        String dirStr = "target/test-classes/testSetDefaultDirAsAFileNotExistsThrowsException";
        File expected = new File(dirStr);
        DirType dirType = new DirType();
        dirType.setProperties("dirType=!exists");
        dirType.setDefault(expected.getAbsolutePath());
        assertEquals(expected.getAbsolutePath(), dirType.toString());
    }

    /**
     * Test of exists method, of class FileType.
     */
    @Test
    public void testDirNotExistsThrowsException() throws IOException {
        String dirname = "target/testDirNotExistsThrowsException";
        File expected = new File(dirname);
        expected.mkdir();
        DirType dirType = new DirType();
        dirType.setProperties("dirType=!exists");
        try {
            dirType.getValue(dirname);
            fail("Expected an exception");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), "Specified directory " + dirname
                    + " already exists.");
        }
    }

    /**
     * Test of getFileType method, of class FileType.
     */
    @Test
    public void testGetFileTypeForDir() throws Exception {
        String dirname = "target/testGetFileTypeForDir";
        File expected = new File(dirname);
        DirType dirType = new DirType();
        File result = dirType.getValue(dirname);
        assertEquals(expected, result);
    }

    /**
     * Test of getFileType method, of class FileType.
     */
    @Test
    public void testBadDirPropertyThrowsException() throws Exception {
        String dirname = "target/testBadDirPropertyThrowsException";
        File expected = new File(dirname);
        expected.mkdir();
        DirType dirType = new DirType();
        try {
            dirType.setProperties("fooType=dir");
        } catch (ValueTypeCreationException cex) {
            assertTrue(cex.getMessage().contains(
                    "Unknown property: fooType=dir"));
        }
    }

    /**
     * Test of getFileType method, of class FileType.
     */
    @Test
    public void testGetFileTypeForDirThrowsException() throws Exception {
        String dirname = "target/testGetFileTypeForDirThrowsException";
        File expected = new File(dirname);
        expected.createNewFile();
        DirType dirType = new DirType();
        dirType.setProperties("dirType=exists");
        try {
            dirType.getValue(dirname);
            fail("Expected an exception");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), "Directory " + dirname + " is a file.");
        }
    }

    /**
     * Test of getFileType method, of class FileType.
     */
    @Test
    public void testGetFileTypeForBadDirPropertyThrowsException() throws Exception {
        String dirname = "target/testGetFileTypeForDirThrowsException";
        File expected = new File(dirname);
        expected.createNewFile();
        DirType dirType = new DirType();
        try {
            dirType.setProperties("dirType=fexists");
            dirType.getValue(dirname);
            fail("Expected an exception");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), "Unknown directory type: fexists"
                    + ". Expected " + FileType.EXISTS + " or "
                    + FileType.NOT_EXISTS);
        }
    }

    /**
     * Test that we can set recursive property without throwing an exception.
     */
    @Test
    public void testDirPropertiesRecursive() throws IOException {
        String dirname = "target/testDirPropertiesRecursive";
        DirType dirType = new DirType();
        dirType.setProperties("recursive=true, listener-id = foo,"
                + " matches = bar, suffixes = bar");
        try {
            dirType.getValue(dirname);
            assertTrue(dirType.isRecursive());
            assertEquals("foo", dirType.getListenerId());
            assertEquals(new File(dirname), dirType.getDirectory());
            DirTypeFileFilter filter = (DirTypeFileFilter) dirType.getFileFilter();
            assertNotNull(filter);
        } catch (ValueTypeCreationException cex) {
            fail(cex.getMessage());
        }
    }

    /**
     * Test that we can set recursive property without throwing an exception.
     */
    @Test
    public void testDirPropertiesMkdirs() throws IOException {
        String dirname = "target/testD/irP/ropertiesM/kdir/s";
        DirType dirType = new DirType();
        dirType.setProperties("dirType=mkdirs");
        try {
            File dir = dirType.getValue(dirname);
            assertTrue(dir.exists());
            assertTrue(dir.isDirectory());
        } catch (ValueTypeCreationException cex) {
            fail(cex.getMessage());
        }
    }

    /**
     * Test that we can set recursive property without throwing an exception.
     */
    @Test
    public void testDirPropertiesSuffixes() throws IOException {
        String dirname = "target/testDirPropertiesSuffixes";
        DirType dirType = new DirType();
        dirType.setProperties("suffixes=jpg jpeg png");
        try {
            dirType.getValue(dirname);
        } catch (ValueTypeCreationException cex) {
            fail(cex.getMessage());
        }
    }

    /**
     * Test that we can set recursive property without throwing an exception.
     */
    @Test
    public void testDirPropertiesStrategyDepthFirst() throws IOException {
        String dirname = "target/testDirPropertiesStrategyDepthFirst";
        DirType dirType = new DirType();
        dirType.setProperties("strategy=breadth");
        try {
            dirType.getValue(dirname);
        } catch (ValueTypeCreationException cex) {
            fail(cex.getMessage());
        }
    }

    /**
     * Test that we can set recursive property without throwing an exception.
     */
    @Test
    public void testDirPropertiesStrategyBreadthFirst() throws IOException {
        String dirname = "target/testDirPropertiesStrategyBreadthFirst";
        DirType dirType = new DirType();
        dirType.setProperties("strategy=depth");
        try {
            dirType.getValue(dirname);
        } catch (ValueTypeCreationException cex) {
            fail(cex.getMessage());
        }
    }

    /**
     * Test that we can set recursive property without throwing an exception.
     */
    @Test
    public void testDirPropertiesStrategyThrowsException() throws IOException {
        String dirname = "target/testDirPropertiesStrategyThrowsException";
        DirType dirType = new DirType();
        try {
            dirType.setProperties("strategy=foo");
            dirType.getValue(dirname);
            fail("Expected an exception");
        } catch (ValueTypeCreationException cex) {
            assertTrue(cex.getMessage().contains("Unknown recursion strategy:"
                    + " foo. Expected breadth or depth"));
        }
    }

    /**
     * Test that we can set recursive property without throwing an exception.
     */
    @Test
    public void testDirBadPropertiesThrowsException() throws IOException {
        String dirname = "target/testDirBadPropertiesThrowsException";
        DirType dirType = new DirType();
        try {
            dirType.setProperties("strategy-foo");
            dirType.getValue(dirname);
            fail("Expected an exception");
        } catch (ValueTypeCreationException cex) {
            assertTrue(cex.getMessage().contains("Invalid properties:"
                    + " strategy-foo"));
        }
    }

    /**
     * Test that we can set recursive property without throwing an exception.
     */
    @Test
    public void testDirPropertiesMatches() throws IOException {
        String dirname = "target/testDirPropertiesMatches";
        DirType dirType = new DirType();
        dirType.setProperties("recursive=true, listener-id=foo, dir-blacklist=foo bar");
        // TODO need to test this properly!
    }

    /**
     * Test that we can set recursive property without throwing an exception.
     */
    @Test
    public void testDirPropertiesMultiple() throws IOException {
        String dirname = "target/testDirPropertiesRecursive";
        DirType dirType = new DirType();
        dirType.setProperties("listener-id = bar, recursive=true, matches=foo,"
                + " suffixes=jpeg png");
        try {
            dirType.getValue(dirname);
        } catch (ValueTypeCreationException cex) {
            fail(cex.getMessage());
        }
    }

    /**
     * Test that we can set recursive property without throwing an exception.
     */
    @Test
    public void testDirPropertiesNoListenerIdForRecursive() throws IOException {
        DirType dirType = new DirType();
        try {
            dirType.setProperties("recursive=true");
            fail("Expected an exception");
        } catch (ValueTypeCreationException cex) {
            assertEquals("recursive property set but"
                    + " there's no listener-id property specified. You"
                    + " must specify a listener-id", cex.getMessage());
        }
    }

    /**
     * Test that we can set recursive property without throwing an exception.
     */
    @Test
    public void testDirPropertiesNoRecursiveForListenerId() throws IOException {
        DirType dirType = new DirType();
        try {
            dirType.setProperties("listener-id=foo");
            fail("Expected an exception");
        } catch (ValueTypeCreationException cex) {
            assertEquals("listener-id property set but"
                    + " there's no recursive property specified. You"
                    + " must specify recursive as either true or false.", cex.getMessage());
        }
    }

    /**
     * Test that we can set recursive property without throwing an exception.
     */
    @Test
    public void testDirPropertiesNotExistsWithOtherProperties() throws IOException {
        DirType dirType = new DirType();
        try {
            dirType.setProperties("dirType=!exists, recursive=true, listener-id=foo");
            fail("Expected an exception");
        } catch (ValueTypeCreationException cex) {
            assertEquals("!exists cannot be specified"
                    + " with any other properties.", cex.getMessage());
        }
    }

    /**
     * Test that we can set recursive property without throwing an exception.
     */
    @Test
    public void testDirPropertiesInvalidMkdirs() throws IOException {
        DirType dirType = new DirType();
        try {
            dirType.setProperties("dirType=mkdirs, recursive=true, listener-id=foo");
            fail("Expected an exception");
        } catch (ValueTypeCreationException cex) {
            System.out.println(cex.getMessage());
            assertEquals("mkdirs cannot be specified"
                    + " with any other properties.", cex.getMessage());
        }
    }
    
    /**
     * 
     */
    @Test
    public void testGetPackageName() {
        DirType dirType = new DirType();
        assertEquals("java.io", dirType.getPackageName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetJavaTypeName() {
        DirType dirType = new DirType();
        assertEquals("File", dirType.getJavaClassName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetValueTypeName() {
        DirType dirType = new DirType();
        assertEquals("dir", dirType.getValueTypeName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetJavaPrimitiveName() {
        DirType dirType = new DirType();
        assertNull(dirType.getJavaPrimitiveName());
    }

    /**
     * Test of toString method, of class FileType.
     */
    @Test
    public void testToString() {
        File file = new File("testToString");
        DirType dirType = new DirType();
        dirType.getValue(file.getAbsolutePath());
        assertEquals(file.getAbsolutePath(), dirType.toString());
    }

}
