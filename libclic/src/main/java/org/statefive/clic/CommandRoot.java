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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.statefive.clic.ClcParser.COMMAND_NAME_REGEX;

/**
 * Root command that all commands (if present) are attached too. The command
 * root is the top-level node of a tree structure; top-level commands are the
 * immediate children of the command root, and, if defined, child commands are
 * associated with those commands down to the leaves of the tree. A command is a
 * leaf command if it has no child commands.
 *
 * @author rich
 */
public class CommandRoot {

    /**
     * Command root; all top-level commands will hang off this root, with
     * sub-commands defined under each appropriate command.
     */
    private final Command root = new Command();

    /**
     * List of all commands for this global configuration in the order they were
     * added; there will only be commands if the configuration file specified at
     * least one {@code command} declaration in the configuration; this list
     * will be empty (never {@code null} if no commands are defined.
     */
    private final List<Command> commands = new ArrayList<>();

    /**
     * The current command (if there is one) being examined. If {@code null} no
     * commands have been processed.
     */
    private Command command;

    /**
     * Get the root command.
     *
     * @return non-{@code null} root command.
     */
    public Command getRoot() {
        return root;
    }

    /**
     * Get the current command, if there is one.
     *
     * @return the current command; {@code null} otherwise.
     */
    public Command getCurrentCommand() {
        return command;
    }

    /**
     * Set the current command as determined by the application command line
     * arguments passed in. All succeeding command line arguments will be parsed
     * as per the defined command configuration.
     *
     * @param command current command; {@code null} to signify parsing of a CLC
     * is finished.
     */
    void setCurrentCommand(Command command) {
        this.command = command;
    }

    /**
     * Get the commands for this configuration.
     *
     * @return the commands, if there are any; the empty list otherwise.
     */
    public List<Command> getCommands() {
        return commands;
    }

    /**
     * Parse the given command path and add it to the tree of commands. If the
     * path contains no path separators it is added to the root of the global
     * configuration as a top-level command; otherwise, it is added to the tree.
     * If nested, all commands up to the top-level command must exist.
     *
     * <p>
     * Once parsed the command will be available via
     * {@link #getCurrentCommand()} until another command is parsed
     * successfully.
     *
     * @param commandPath non-{@code null} command path.
     *
     * @throws ClcException if the parent of the command does not exist, or the
     * command name is not valid.
     */
    public void parseCommands(String commandPath)
            throws ClcException {
        Command parent = getRoot();
        if (commandPath.contains(Command.COMMAND_PATH_SEPARATOR)) {
            // strip out the last command
            parent = find(parent, commandPath.substring(
                    0, commandPath.lastIndexOf(
                            Command.COMMAND_PATH_SEPARATOR)));
        }
        if (parent == null) {
            // parent to specified command does not exist, throw error:
            String[] paths = commandPath.split(Command.COMMAND_PATH_SEPARATOR);
            String parentName = paths[paths.length - 2];
            String childName = paths[paths.length - 1];
            throw new ClcException("Unknown parent command '"
                    + parentName + "' for command '" + childName + "'");
        }
        Command cmd = addCommand(parent, commandPath);
        Pattern pName = Pattern.compile(COMMAND_NAME_REGEX);
        Matcher mName = pName.matcher(cmd.getName());
        if (!mName.matches()) {
            throw new ClcException("Invalid"
                    + " command name: " + commandPath);
        }
    }

    /**
     * Find the given command. If the command path contains the command path
     * separator the path will be searched recursively until the command is
     * located.
     *
     * @param parent non-{@code null} parent to search.
     *
     * @param path non-{@code null} command path; may contain command path
     * separators, otherwise is considered a root/top-level command of the
     * global configuration.
     *
     * @return the command, if it could be found; {@code null} otherwise.
     */
    public Command find(Command parent, String path) {
        Command cmd = null;
        if (!path.contains(Command.COMMAND_PATH_SEPARATOR)) {
            for (Command c : parent.getChildren()) {
                if (path.equals(c.getName())) {
                    cmd = c;
                    break;
                }
            }
        } else {
            String cmdName = path.substring(path.indexOf(Command.COMMAND_PATH_SEPARATOR) + 1);
            for (Command child : parent.getChildren()) {
                if (cmdName.equals(child.getName())) {
                    cmd = child;
                    break;
                } else {
                    // recursive search
                    cmd = find(child, path);
                    if (cmd != null) {
                        break;
                    }
                }
            }
        }
        return cmd;
    }

    /**
     * Add the specified command to the given parent; if the path contains path
     * separators, the last element of the path is added to the given parent.
     *
     * <p>
     * Once added the command will become the current command accessible via
     * {@link #getCurrentCommand()}.
     *
     * @param parent non-{@code null} parent to add the new command to.
     *
     * @param path non-{@code null} full path of the command.
     *
     * @return non-{@code null} added command.
     *
     * @throws ClcException if the last element of the path has already been
     * added to the given parent.
     */
    private Command addCommand(Command parent, String path) throws ClcException {
        Command newCommand = null;
        String cmdName = path;
        if (path.contains(Command.COMMAND_PATH_SEPARATOR)) {
            cmdName = path.substring(path.lastIndexOf(Command.COMMAND_PATH_SEPARATOR) + 1);
        }
        for (Command child : parent.getChildren()) {
            if (cmdName.equals(child.getName())) {
                throw new ClcException("Global configuration already"
                        + " contains " + newCommand.getName() + ", command path: " + path);
            }
        }
        newCommand = new Command();
        newCommand.setName(cmdName);
        parent.addChild(newCommand);
        this.command = newCommand;
        this.commands.add(newCommand);
        return newCommand;
    }

}
