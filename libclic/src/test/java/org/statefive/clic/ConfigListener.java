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

/**
 * Class for test classes to utilise; they add this listener before they make
 * any calls to
 * {@link CommandLineConfig#process(java.io.InputStream, java.lang.String[])}
 * and interrogate this listener using {@link AbstractTestConfig#getOptions()}
 * after processing has occurred, to determine that the specified values were
 * registered during the parsing process.
 */
public class ConfigListener extends AbstractTestConfig
        implements OptionListener, CommandOptionListener, ArgsListener,
        CommandArgsListener {

    /**
     * Command being run, if not a standard top-level option.
     */
    private String command;

    /**
     * Callers implementing this would normally set members or take actions when
     * receiving updates.
     *
     * @param option non-{@code null} command line option updated from the
     * result of the command line parsing process.
     *
     * @param value value of the command line value, if the command line option
     * has an argument; {@code null} otherwise (and implies the command line
     * option does not have an argument).
     */
    @Override
    public void option(String option, Object value) {
        addOption(option, value);
    }

    /**
     * Callers implementing this would normally set members or take actions when
     * receiving updates.
     *
     * @param command non-{@code null} command name.
     *
     * @param option non-{@code null} command line option updated from the
     * result of the command line parsing process.
     *
     * @param value value of the command line value, if the command line option
     * has an argument; {@code null} otherwise (and implies the command line
     * option does not have an argument).
     */
    @Override
    public void option(String command, String option, Object value) {
        this.command = command;
        addCommandOption(command, option, value);
    }

    /**
     * Fired when an argument is processed.
     *
     * @param name non-{@code null} name of the argument being processed.
     *
     * @param index index of the argument; indices begin at 0.
     *
     * @param value non-{@code null} value of the argument.
     */
    @Override
    public void argument(String name, int index, Object value) {
        addArg(name, index, value);
    }

    /**
     * Called when an argument is processed for the given path of the command.
     *
     * @param command non-{@code null} command path of the command that the
     * argument is associated with.
     *
     * @param name non-{@code null} name of the argument.
     *
     * @param index argument index starting at zero.
     *
     * @param value non-{@code null} value of the argument.
     */
    @Override
    public void argument(String command, String name, int index, Object value) {
        this.command = command;
        addArg(command + ":" + name, index, value);
    }

    /**
     * Get the command invoked if
     * {@link #option(java.lang.String, java.lang.String, java.lang.Object)} was
     * called.
     *
     * @return the command, if it was set; {@code null} otherwise.
     */
    public String getCommand() {
        return command;
    }

}
