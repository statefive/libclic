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
package org.statefive.clic;

/**
 * Listener for receiving updates for command line arguments (arguments that are
 * determined once all command line arguments have been processed) from the
 * result of a command being processed.
 *
 * <p>
 * Listeners will only be updated with arguments based on the command that was
 * invoked; for receiving updates top-based arguments, see {@link ArgsListener}.
 *
 * @author rich
 */
public interface CommandArgsListener {

    /**
     * Update with the specified command path, argument name, index and value.
     *
     * @param command non-{@code null} full path to the command of the argument.
     *
     * @param name non-{@code null} name; this is the {@code args.[name]} value
     * of the defined argument for the given command.
     *
     * @param index index of the argument for the specified name, starting at 0
     * for the named argument and incrementing by one for each newly processed
     * argument.
     *
     * @param value non-{@code null} value of the argument.
     */
    void argument(String command, String name, int index, Object value);

}
