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
 * determined once all command line arguments have been processed).
 * 
 * <p>
 * Listeners will only be updated with top-level i.e. non-command-based
 * arguments; for receiving updates for command-based arguments, see
 * {@link CommandArgsListener}.
 *
 * @author rich
 */
public interface ArgsListener {

    /**
     * Update with the specified argument name, index and value.
     *
     * @param name non-{@code null} name; this is the {@code args.[name]} value
     * of the defined argument; if no {@link ArgsConfiguration} has been defined
     * then the name value will be the actual argument read in via the command
     * line.
     *
     * @param index index of the argument for the specified name, starting at 0
     * for the named argument and incrementing by one for each newly processed
     * argument.
     *
     * @param value non-{@code null} value of the argument; for configurations
     * that have no defined {@link ArgsConfiguration}s, the value will be the
     * same as the given {@code name} value.
     */
    void argument(String name, int index, Object value);

}
