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
 * File filter for {@link DirType}s.
 */
public class DirTypeFileFilter implements FileFilter {

    /**
     * List of suffixes to match when filtering files; if not empty, only files
     * containing the specified suffix (ignoring case) will be included.
     */
    private final List<String> suffixes = new ArrayList<>();

    /**
     * List of text to match when filtering files; if not empty, only files
     * containing the specified text (case sensitive) will be included.
     */
    private final List<String> matches = new ArrayList<>();

    /**
     * Determine if to accept the specified file; only files will be considered,
     * directories will be ignored.
     *
     * @param pathname non-{@code null} path name to examine.
     *
     * @return if both the suffixes and matches are non-empty, if any file
     * matches the suffix (ignoring case) AND contains a (case sensitive) match
     * it will be accepted, respectively; else if both are empty, all files that
     * are not directories will be accepted. If only one of suffixes or matches
     * is set, a file will be included if it matches either set of matches.
     */
    @Override
    public boolean accept(File pathname) {
        boolean accept = false;
        if (!pathname.isDirectory()) {
            boolean suffixMatch = false;
            boolean textMatch = false;
            if (!suffixes.isEmpty()) {
                for (String suffix : suffixes) {
                    String pathnameLc = pathname.getName().toLowerCase();
                    if (pathnameLc.endsWith(suffix.toLowerCase())) {
                        suffixMatch = true;
                        break;
                    }
                }
                if (matches.isEmpty()) {
                    accept = suffixMatch;
                }
            }
            if (!matches.isEmpty()) {
                // still not found a suffix match, so now try file name matches:
                for (String match : matches) {
                    String name = pathname.getName();
                    int dot = name.lastIndexOf('.');
                    if (dot > 0) {
                        name = name.substring(0, dot);
                    }
                    if (name.contains(match)) {
                        textMatch = true;
                        break;
                    }
                }
                if (!suffixes.isEmpty()) {
                    // conjunctive 'and' check
                    accept = suffixMatch && textMatch;
                } else {
                    accept = textMatch;
                }
            }
            if (suffixes.isEmpty() && matches.isEmpty()) {
                // they've not specified any suffixes or matches, so we accept all
                accept = true;
            }
        }
        return accept;
    }

    /**
     * Add suffixes for matching file suffixes.
     *
     * @param fileSuffixes non-{@code null} list of suffixes to add.
     */
    public void addSuffixes(final List fileSuffixes) {
        suffixes.addAll(fileSuffixes);
    }

    /**
     * Add matches to match against file names.
     *
     * @param fileMatches non-{@code null} list of matches to add.
     */
    public void addMatches(final List fileMatches) {
        matches.addAll(fileMatches);
    }
}
