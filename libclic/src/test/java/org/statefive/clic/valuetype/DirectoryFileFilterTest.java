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
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author rich
 */
public class DirectoryFileFilterTest {

    /**
     * Test of accept method, of class DirectoryFileFilter.
     */
    @Test
    public void testAcceptIsDirectory() {
        File dir = new File("target/");
        assertTrue(new DirectoryFileFilter().accept(dir));
    }

    /**
     * Test of accept method, of class DirectoryFileFilter.
     */
    @Test
    public void testAcceptIsNotDirectory() {
        File dir = new File("pom.xml");
        assertFalse(new DirectoryFileFilter().accept(dir));
    }

    /**
     * Test of accept method, of class DirectoryFileFilter.
     */
    @Test
    public void testAcceptIsBlacklisted() {
        DirectoryFileFilter filter = new DirectoryFileFilter();
        List<String> blacklist = new ArrayList<>();
        blacklist.add("target");
        filter.addDirectoryBlacklist(blacklist);
        File dir = new File("target");
        assertFalse(filter.accept(dir));
    }
    
}
