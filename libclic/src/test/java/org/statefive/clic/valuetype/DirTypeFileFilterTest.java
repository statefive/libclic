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
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author rich
 */
public class DirTypeFileFilterTest {

    /**
     * Test of accept method, of class DirTypeFileFilter.
     */
    @Test
    public void testAcceptDirectoryFalse() {
        File dir = new File("target");
        assertFalse(new DirTypeFileFilter().accept(dir));
    }

    /**
     * Test of accept method, of class DirTypeFileFilter.
     */
    @Test
    public void testAcceptFileTrue() {
        File file = new File("pom.xml");
        assertTrue(new DirTypeFileFilter().accept(file));
    }

    /**
     * Test of accept method, of class DirTypeFileFilter.
     */
    @Test
    public void testAcceptFileSuffixTrue() {
        File file = new File("pom.xml");
        DirTypeFileFilter filter = new DirTypeFileFilter();
        filter.addSuffixes(split("XML"));
        assertTrue(filter.accept(file));
    }

    /**
     * Test of accept method, of class DirTypeFileFilter.
     */
    @Test
    public void testAcceptFileSuffixFalse() {
        File file = new File("pom.xml");
        DirTypeFileFilter filter = new DirTypeFileFilter();
        filter.addSuffixes(split("txt"));
        assertFalse(filter.accept(file));
    }

    /**
     * Test of accept method, of class DirTypeFileFilter.
     */
    @Test
    public void testAcceptFileMatchTrue() {
        File file = new File("pom.xml");
        DirTypeFileFilter filter = new DirTypeFileFilter();
        filter.addMatches(split("po"));
        assertTrue(filter.accept(file));
    }

    /**
     * Test of accept method, of class DirTypeFileFilter.
     */
    @Test
    public void testAcceptFileMatchFalse() {
        File file = new File("pom.xml");
        DirTypeFileFilter filter = new DirTypeFileFilter();
        filter.addMatches(split("Pom"));
        assertFalse(filter.accept(file));
    }

    /**
     * Test of accept method, of class DirTypeFileFilter.
     */
    @Test
    public void testAcceptFileMatchNoSuffixTrue() {
        File file = new File("aFileWithoutASuffix");
        DirTypeFileFilter filter = new DirTypeFileFilter();
        filter.addMatches(split("File"));
        assertTrue(filter.accept(file));
    }

    /**
     * Test of addSuffixes method, of class DirTypeFileFilter.
     */
    @Test
    public void testAddSuffixes() {
    }

    /**
     * Test of addMatches method, of class DirTypeFileFilter.
     */
    @Test
    public void testAddMatches() {
    }
    
    private List<String> split(String data) {
        return Arrays.asList(data.split(" "));
    }
}
