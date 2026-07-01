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
 * Listener for receiving updates from the process of parsing an option
 * configuration for a given command.
 *
 * @author rich
 */
public interface CommandOptionListener {

    /**
     * Invoked when a command is invoked from the CLI.
     * 
     * @param command non-{@code null} command being invoked.
     *
     * @param option non-{@code null} option, either in short form or long form.
     *
     * @param value the value of the argument; for options that do not have an
     * argument, the value will be {@code null}.
     */
    void option(final String command, final String option, final Object value);
    
}
