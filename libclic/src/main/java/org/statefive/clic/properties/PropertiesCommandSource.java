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

import org.statefive.clic.Command;

/**
 * Properties source representing properties for a specific command.
 *
 * @author rich
 */
public class PropertiesCommandSource implements PropertiesSource {
    
    /**
     * Character to separate a properties source with a prefixed command name.
     */
    public static final String COMMAND_SOURCE_PREFIX_SEPARATOR = ":";
    
    /**
     * Character to separate nested command names; will ultimately be converted
     * to {@link Command#COMMAND_PATH_SEPARATOR}s when converted to a command.
     */
    public static final String COMMAND_SOURCE_PATH_SEPARATOR = "_";

    /**
     * Command path for the properties; if defining nested commands, the
     * {@link #COMMAND_SOURCE_PATH_SEPARATOR} must be used.
     */
    private final String command;
    
    /**
     * Underlying source that the command properties are built from.
     */
    private final PropertiesSource source;
    
    /**
     * Create a new properties file source.
     * 
     * @param command non-{@code null} command path.
     * 
     * @param source non-{@code null} existing file.
     */
    public PropertiesCommandSource(String command, PropertiesSource source) {
        this.command = command;
        this.source = source;
    }

    /**
     * Get the command name.
     * 
     * @return non-{@code null} command name.
     */
    public String getCommand() {
        return command;
    }

    /**
     * Get the underlying properties source.
     * 
     * @return non-{@code null} properties source.
     */
    @Override
    public Object getSource() {
        return source;
    }
    
}
