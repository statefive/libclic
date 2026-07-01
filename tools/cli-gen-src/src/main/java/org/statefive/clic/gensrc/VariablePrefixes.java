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
package org.statefive.clic.gensrc;

/**
 * Variable prefix information for generating Java source code. Prefixes are
 * defined for  arguments, commands and command arguments.
 *
 * @author irch
 */
public class VariablePrefixes {

    /**
     * Prefix for commands.
     */
    private String commandPrefix;

    /**
     * Prefix for command arguments.
     */
    private String commandArgsPrefix;
    
    /**
     * Arguments (top-level) prefix.
     */
    private String argsPrefix;

    /**
     * Get the command prefix.
     * 
     * @return command prefix.
     */
    public String getCommandPrefix() {
        return commandPrefix;
    }

    /**
     * Set the given command prefix.
     * 
     * @param commandPrefix non-{@code null} command prefix.
     */
    public void setCommandPrefix(String commandPrefix) {
        this.commandPrefix = commandPrefix;
    }

    /**
     * Get the command arguments prefix.
     * 
     * @return command arguments prefix.
     */
    public String getCommandArgsPrefix() {
        return commandArgsPrefix;
    }

    /**
     * Set the command arguments prefix.
     * 
     * @param commandArgsPrefix non-{@code null} command arguments prefix.
     */
    public void setCommandArgsPrefix(String commandArgsPrefix) {
        this.commandArgsPrefix = commandArgsPrefix;
    }

    /**
     * Get the arguments prefix.
     * 
     * @return arguments prefix.
     */
    public String getArgsPrefix() {
        return argsPrefix;
    }

    /**
     * Set the arguments prefix.
     * 
     * @param argsPrefix non-{@code null} arguments prefix.
     */
    public void setArgsPrefix(String argsPrefix) {
        this.argsPrefix = argsPrefix;
    }
    
}
