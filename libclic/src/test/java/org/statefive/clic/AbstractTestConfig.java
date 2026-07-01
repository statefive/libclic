/**
 * Copyright 2019 www.statefive.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.statefive.clic;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Test configuration for managing updates from command line processing.
 */
public abstract class AbstractTestConfig {

    /**
     * Map of command line options; the key will be the option name, the value
     * will be the option value.
     */
    private final Map<String, Object> options = new LinkedHashMap<>();

    /**
     * Map of command line options for commands; the key will be the command
     * name or full command path (up to test callers depending on what values
     * are passed to
     * {@link #addCommandOption(java.lang.String, java.lang.String, java.lang.String)}
     * and the value will be a map of the option name (for the map key) and the
     * option value (for the map value).
     */
    private final Map<String, Map<String, Object>> commandOptions = new LinkedHashMap<>();

    /**
     * Map of argument configuration arguments; the key will be (for
     * non-commands) the argument name and for commands the key will be the
     * command name suffixed with ':' and followed by the argument name; the
     * value will a map where the integer is the index of the argument and the
     * object will be the argument value.
     */
    private final Map<String, Map<Integer, Object>> args = new LinkedHashMap<>();

    /**
     * Get the top-level (non-command) options.
     *
     * @return non-{@code null} map of top-level (non-command) options; may be
     * empty.
     */
    public Map<String, Object> getOptions() {
        return Collections.unmodifiableMap(options);
    }

    /**
     * Get the options for the specified command path name.
     *
     * @return map of command options, if the command is present; {@code null}
     * otherwise.
     */
    public Map<String, Object> getCommandOptions(String commandPath) {
        return Collections.unmodifiableMap(commandOptions.get(commandPath));
    }

    /**
     * Get non-command top-level arguments.
     *
     * @return top-level (non-command) arguments, if there are any; the empty
     * map otherwise.
     */
    public Map<String, Map<Integer, Object>> getArgs() {
        return Collections.unmodifiableMap(args);
    }

    /**
     * Add the given option to the map of top-level (non-command) options.
     *
     * @param option non-{@code null} option name.
     *
     * @param value non-{@code null} option value.
     *
     * @return the option if it was added.
     */
    public Object addOption(String option, Object value) {
        return options.put(option, value);
    }

    /**
     * Add the
     *
     * @param command non-{@code null} command name to add to; for testing
     * purposes can be anything but is advised to use the command path (so long
     * as it is used consistently in testing).
     *
     * @param option non-{@code null} option name to add.
     *
     * @param value non-{@code null} value to add.
     */
    public void addCommandOption(String command, String option, Object value) {
        if (commandOptions.containsKey(command)) {
            Map<String, Object> entries = commandOptions.get(command);
            entries.put(option, value);
        } else {
            Map<String, Object> entries = new HashMap<>();
            entries.put(option, value);
            commandOptions.put(command, entries);
        }
    }

    /**
     * Add the specified named argument for the given index and value; for
     * argument configurations for commands, prefix the name with the command
     * name followed by a colon.
     *
     * @param name non-{@code null} argument name (prefixed with the command
     * name and colon if the argument is for a command).
     *
     * @param index index of the argument, beginning at zero for the first
     * argument.
     *
     * @param value non-{@code null} argument value.
     */
    public void addArg(String name, int index, Object value) {
        if (args.containsKey(name)) {
            Map<Integer, Object> entries = args.get(name);
            entries.put(index, value);
        } else {
            Map<Integer, Object> entries = new HashMap<>();
            entries.put(index, value);
            args.put(name, entries);
        }
    }
}
