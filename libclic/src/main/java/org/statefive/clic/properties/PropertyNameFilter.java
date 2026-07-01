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
package org.statefive.clic.properties;

import java.util.ArrayList;
import java.util.List;

/**
 * Include or exclude command line switches from the list of command line
 * options. Typically used against very large properties files where only some
 * properties need to be displayed when invoking command line help.
 *
 * @author rich
 */
public class PropertyNameFilter {

    /**
     * List of strings to match to include a given property.
     */
    private final List<String> includes = new ArrayList<>();

    /**
     * List of strings to match to exclude a given property.
     */
    private final List<String> excludes = new ArrayList<>();

    /**
     * Create a new property filter with the given list of includes and
     * excludes. A filter without any includes or excludes will include
     * everything.
     *
     * @param includes non-{@code null} includes; may be empty.
     *
     * @param excludes non-{@code null} excludes; may be empt.
     */
    public PropertyNameFilter(List<String> includes, List<String> excludes) {
        this.includes.addAll(includes);
        this.excludes.addAll(excludes);
    }

    /**
     * Determine if this filter is capable of filtering.
     *
     * @return {@code true} if there are any includes or excludes; {@code false}
     * otherwise.
     */
    public boolean isFitlerable() {
        return !includes.isEmpty() || !excludes.isEmpty();
    }

    /**
     * Determine if this filter is for including or excluding properties.
     *
     * @return {@code true} if there are any includes; {@code false} otherwise.
     */
    public boolean isInclude() {
        return !includes.isEmpty();
    }

    /**
     * Get the list of string matches to include.
     *
     * @return non-{@code null} includes.
     */
    public List<String> getIncludes() {
        return includes;
    }

    /**
     * Get the list of string matches to exclude.
     *
     * @return non-{@code null} excludes.
     */
    public List<String> getExcludes() {
        return excludes;
    }

}
