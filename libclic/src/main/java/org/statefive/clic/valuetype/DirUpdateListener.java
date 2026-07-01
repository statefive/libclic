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
 * Callers that have specified a {@link DirType} as a recursive or flat
 * directory should implement this interface. Callers that register as a
 * directory listener will receive both updates as directories are recursively
 * scanned and is up to the caller which update to act upon.
 */
public interface DirUpdateListener {

    /**
     * Called when a directory is traversed, with the specified list of files.
     * Callers that have created a {@link DirType} with file suffixes and/or
     * file matches will receive only those files specified by those properties.
     * If there are no matches or suffixes specified the list will be <i>all</i>
     * files in the specified parent directory; no directories will be included,
     * however if callers require the directory they can simply get the list
     * from the directory passed in.
     *
     * @param dir non-{@code null} directory.
     *
     * @param files array of files within the directory that have been accepted
     * by a file filter (if one is specified).
     *
     * @param listenerId non-{@code null} listener identifier. This is used when
     * multiple listeners are registered for different directories so that they
     * know if to deal with the update or not.
     * 
     * @return {@code true} if the directory should continue being traversed;
     * {@code false} otherwise.
     */
    boolean directoryTraversed(final File dir, final File[] files,
            final String listenerId);
}
