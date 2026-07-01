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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A command is a sub-command to the CLI being invoked, in much the same way
 * that {@code git} is invoked using (for example) {@code git diff},
 * {@code git status} where {@code diff} and {@code status} are sub-commands to
 * the main {@code git} application.
 *
 * <p>
 * Commands can be nested such that commands can have sub-commands that each
 * have their own set of options. Options defined for commands are unique to
 * that command: This means that names of options can be duplicated across
 * command (for example a help option) since options are unique to each set of
 * commands.
 *
 * <p>
 * Each command will have its own option and arguments configurations, however
 * commands cannot be declared with prefix arguments. As with top-level
 * (non-command) options, option configurations must be defined first, followed
 * by argument configurations (if present). Commands must be defined after
 * global options, and once declared, all {@code option.[name].*} and all
 * {@code args.[name].*} definitions will be attached to the specified command
 * until a new command definition is defined.
 *
 * <p>
 * Commands are defined as follows:
 *
 * <pre>
 * command.name=[command-name]
 * command.usage=[usage]
 * </pre>
 *
 * <p>
 * When defining sub-commands of other commands the command name must be the
 * full path from the parent using the forward slash {@code /} character to
 * separate command names, with no leading or trailing slashes in the command
 * path. That is, the first command should not be prefixed with a forward slash
 * and the last defined command should not be defined with a forward slass. For
 * example, the following configuration declares 3 commands, {@code x},
 * {@code y} and {@code z} where {@code y} is a sub-command of {@code x} and
 * {@code z} is a sub-command of {@code y}:
 *
 * <pre>
 * command.name = x
 * command.usage = command x
 *
 * # command x options...
 *
 * # define command y which is a child of command x:
 * command.name = x/y
 * command.usage = command y does the following...
 *
 * # command y options...
 *
 * # define command z which is a child of command y:
 * command.name = x/y/z
 * command.usage = command z is used to...
 *
 * # command z options...
 * </pre>
 *
 * <p>
 * The command {@code usage} is used when invoking the {@code help} for each
 * command.
 *
 * <p>
 * Usage data can also be padded with newlines like the help header specified in
 * {@link GlobalConfiguration}.
 */
public class Command {

    /**
     * Separator for nested commands; the top-level/root command of a global
     * configuration is not prefixed with the command separator but all nested
     * commands must be separated by their parent.
     */
    public static final String COMMAND_PATH_SEPARATOR = "/";

    /**
     * Command parent, if a nested command, {@code null} for commands defined at
     * the root.
     */
    private Command parent;

    /**
     * Command children.
     */
    private final List<Command> children = new ArrayList<>();

    /**
     * The name of the command.
     */
    private String name;

    /**
     * Usage string for the command.
     */
    private String usage;

    /**
     * {@link OptionConfiguration} for this command; the key is the actual name
     * part of the {@code option.[name].*} declaration, in other words the
     * option name.
     */
    private final Map<String, OptionConfiguration> optionMap = new LinkedHashMap<>();

    /**
     * Argument configurations.
     */
    private final Map<String, ArgsConfiguration> argsMap = new LinkedHashMap<>();

    /**
     * Add the specified command to this command.
     *
     * @param command non-{@code null} command to add.
     *
     * @return {@code true} if the command was added; {@code false} otherwise,
     * indicating that a command of the same name already exists for this
     * command.
     */
    public boolean addChild(Command command) {
        boolean added = false;
        if (children.isEmpty()) {
            added = children.add(command);
            command.parent = this;
        } else {
            boolean duplicate = false;
            for (Command child : children) {
                if (child.getName().equals(command.getName())) {
                    duplicate = true;
                    break;
                }
            }
            if (!duplicate) {
                added = children.add(command);
                command.parent = this;
            }
        }
        return added;
    }

    /**
     * Get the parent of the command.
     *
     * @return the parent if there is one; {@code null} implies the command is a
     * top-level command.
     */
    public Command getParent() {
        return this.parent;
    }

    /**
     * Get sub-commands of the command.
     *
     * @return non-empty list of sub-commands if there are any; the empty list
     * otherwise.
     */
    public List<Command> getChildren() {
        return children;
    }

    /**
     * Get the name of the command.
     *
     * @return the command name; the root command name is {@code null}
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of this command.
     *
     * @param name non-{@code null} name of the command.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Get the usage for the command.
     *
     * @return the command usage.
     */
    public String getUsage() {
        return usage;
    }

    /**
     * Set the usage for the command.
     *
     * @param usage the usage for the command.
     */
    public void setUsage(String usage) {
        this.usage = usage;
    }

    /**
     * Get the path of the command.
     *
     * @return non-{@code null} path to the command; if the command is a
     * top-level command the path will be the name of the command, otherwise the
     * path will be the path from the top-level command with all sub-commands of
     * the command separated with forward slashes.
     */
    public String getPath() {
        return getPath(this);
    }

    /**
     * Get the path of the command, recursively traversing all parent commands
     * until the root command (the command with {@code null} name) is
     * encountered.
     *
     * @param command non-{@code null} command to print the path for.
     *
     * @return path of the command to the root command; sub-commands of a parent
     * will be separated by forward slashes, the {@code null} root command will
     * not be printed.
     * 
     * @throws NullPointerException if the command is the root command.
     */
    private String getPath(Command command) {
        String path = command.getName();
        if (command.getParent() != null && command.getParent().getName() != null) {
            path = getPath(command.getParent()) + COMMAND_PATH_SEPARATOR + path;
        }
        return path;
    }

    /**
     * Get the option map for this configuration; the key to the map will be the
     * option configuration names defined by the {@code option.[name]}
     * declarations.
     *
     * @return the non-{@code null}, non-empty option map (note that if no
     * option configurations are defined when parsing an exception will be
     * thrown.
     */
    public Map<String, OptionConfiguration> getOptionConfigurations() {
        return Collections.unmodifiableMap(optionMap);
    }

    /**
     * Get the argument configurations for this command.
     *
     * @return the argument configurations if there are any defined; the empty
     * map otherwise.
     */
    public Map<String, ArgsConfiguration> getArgsConfigurations() {
        return Collections.unmodifiableMap(argsMap);
    }

    /**
     * Determine if this command has any argument configurations.
     *
     * @return {@code true} if the command has argument configurations;
     * {@code false} otherwise.
     */
    public boolean hasArgsConfigurations() {
        return !argsMap.isEmpty();
    }

    /**
     * Add the specified option configuration.
     *
     * @param name non-{@code null} name of the option configuration; this will
     * be the {@code option.[name]} from the configuration file.
     *
     * @param optConfig non-{@code null} option configuration option to add.
     */
    public void addOptionConfiguration(final String name,
            final OptionConfiguration optConfig) {
        optionMap.put(name, optConfig);
    }

    /**
     * Add the specified argument configuration.
     *
     * @param name non-{@code null} name of the argument configuration; this
     * will be the {@code option.[name]} from the configuration file.
     *
     * @param argsConfiguration non-{@code null} argument configuration option
     * to add.
     */
    public void addArgsConfiguration(final String name,
            final ArgsConfiguration argsConfiguration) {
        argsMap.put(name, argsConfiguration);
    }

    /**
     * Hashed on the command name.
     *
     * @return the hash code for the command.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.name);
        return hash;
    }

    /**
     * Two commands are equal of their names are the same (case sensitive).
     *
     * @param obj the object to test equality for.
     *
     * @return {@code true} if the object is a command and has the same name as
     * this command; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Command other = (Command) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return equals(this.getParent(), other.getParent());
    }

    /**
     * Two commands are equal if their names are the same and all parent
     * commands (recursively traversed to the top level) are the same by the
     * same rule.
     *
     * @param lhs command to check for equality; may be {@code null}.
     *
     * @param rhs command to check for equality; may be {@code null}.
     *
     * @return {@code true} if the command names are the same and the parents
     * (recursively traversed to the top level) are the same for both commands;
     * {@code false} otherwise.
     */
    private boolean equals(Command lhs, Command rhs) {
        boolean equals = false;
        if (lhs == null && rhs == null) {
            equals = true;
        } else if (lhs != null && rhs != null) {
            if (Objects.equals(lhs.name, rhs.name)) {
                equals = equals(lhs.getParent(), rhs.getParent());
            }
        }
        return equals;
    }

    /**
     * Get the path of the command.
     *
     * @return the command path - see {@link #getPath()}.
     */
    @Override
    public String toString() {
        return getPath();
    }

}
