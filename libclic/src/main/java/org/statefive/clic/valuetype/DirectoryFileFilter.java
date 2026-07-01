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
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Filter to only accept directories.
 */
public class DirectoryFileFilter implements FileFilter {

    /**
     * List of directories to ignore when filtering; if not empty, directories
     * that match the specified name will not be considered or traversed.
     */
    private final List<String> directoryBlacklist = new ArrayList<>();
    
    /**
     * Determine if this path name is a directory.
     * 
     * @param pathname non-{@code null} path name.
     * 
     * @return {@code true} if the path name is a directory; {@code false}
     * otherwise.
     */
    @Override
    public boolean accept(File pathname) {
        boolean blacklisted = false;
        for (String dir : directoryBlacklist) {
            if (pathname.getName().matches(dir)) {
                blacklisted = true;
            }
        }
        return pathname.isDirectory() && !blacklisted;
    }

    /**
     * Add directory names to ignore when filtering.
     *
     * @param dirNames non-{@code null} list of directory names to add.
     */
    public void addDirectoryBlacklist(final List dirNames) {
        directoryBlacklist.addAll(dirNames);
    }
}
