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
package org.statefive.clic.gensrc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.statefive.clic.OptionListener;

/**
 * CLI source generator option listener.
 *
 * @author rich
 */
public class OptionHelper implements OptionListener {

    /**
     * Default class name.
     */
    public static final String DEFAULT_CLASS_NAME = "OptionHelper";

    /**
     * Default command name.
     */
    public static final String DEFAULT_COMMAND_NAME = "command";

    /**
     * Standard input notation for properties file as read from standard input.
     */
    private static final String STDIN = "-";

    /**
     * Package name of generated source (if present).
     */
    private String packageName;

    /**
     * Class name to generate.
     */
    private String className = DEFAULT_CLASS_NAME;

    /**
     * Overridden command name variable if supplied by the user.
     */
    private String commandName = DEFAULT_COMMAND_NAME;

    /**
     * Configuration input stream.
     */
    private InputStream configStream;

    /**
     * Directory to output to (if set).
     */
    private File outputDir;

    /**
     * Convert each tab to the specified number of spaces.
     */
    private int tabsAsSpaces;

    /**
     * Add header details to the generated output.
     */
    private boolean header = false;

    /**
     * Print stack trace when generating errors.
     */
    private boolean stacktrace = false;

    /**
     * Determine if to verify the specified configuration file/stream.
     */
    private boolean verify;

    /**
     * Prefixes to apply to variable and method names.
     */
    private VariablePrefixes variablePrefixes;

    /**
     * Receive updates from the underlying configuration parser.
     *
     * @param option non-{@code null} option.
     *
     * @param value non-{@code null} value.
     */
    @Override
    public void option(String option, Object value) {
        switch (option) {
            case "c":
            case "clc":
                if (STDIN.equals(value.toString())) {
                    configStream = System.in;
                } else {
                    File configFile = (File) value;
                    try {
                        configStream = new FileInputStream(configFile);
                    } catch (FileNotFoundException ex) {
                        throw new IllegalArgumentException(
                                configFile.getAbsolutePath() + " does not exist"
                                + " (or is not a file");
                    }
                }
                break;
            case "o":
            case "output-dir":
                outputDir = (File) value;
                break;
            case "P":
            case "package-name":
                packageName = (String) value;
                break;
            case "C":
            case "class-name":
                className = (String) value;
                break;
            case "n":
            case "command-name":
                commandName = (String) value;
                break;
            case "S":
            case "spaces":
                tabsAsSpaces = (int) value;
                break;
            case "p":
            case "prefixes":
                variablePrefixes = (VariablePrefixes) value;
                break;
            case "H":
            case "header":
                header = true;
                break;
            case "V":
            case "verify":
                verify = true;
            case "X":
            case "stacktrace":
                stacktrace = true;
                break;
            case "v":
            case "version":
                System.exit(0);
            case "h":
            case "help":
                System.exit(0);
            default:
                throw new IllegalArgumentException("Invalid option: " + option);
        }
    }

    /**
     * Get the package name used when generating sources.
     *
     * @return package name to set; may be {@code null}.
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Get the class name of the generated class.
     *
     * @return class name, if set; if not set, {@link #DEFAULT_CLASS_NAME} will
     * be used.
     */
    public String getClassName() {
        return className;
    }

    /**
     * Get the command name for commands.
     *
     * @return the class name, if set; if not set, {@link #DEFAULT_COMMAND_NAME}
     * will be used.
     */
    public String getCommandName() {
        return commandName;
    }

    /**
     * Standard input stream.
     *
     * @return input stream if a stream was defined; {@code null} otherwise.
     */
    public InputStream getConfigStream() {
        return configStream;
    }

    /**
     * Get the output directory to generate source in.
     *
     * @return non-{@code null} existing directory if set; {@code null}
     * otherwise (outputs to standard output instead).
     */
    public File getOutputDir() {
        return outputDir;
    }

    /**
     * Get the number of spaces per tab.
     *
     * @return number of spaces per tab if set; 0 otherwise.
     */
    public int getTabsAsSpaces() {
        return tabsAsSpaces;
    }

    /**
     * Determine if header information should be included.
     *
     * @return {@code true} if header information should be included;
     * {@code false} otherwise.
     */
    public boolean isHeader() {
        return header;
    }

    /**
     * Determine if files should be verified without generating content.
     *
     * @return {@code true} to verify provided content; {@code false} otherwise.
     */
    public boolean isVerify() {
        return verify;
    }

    /**
     * Determine if a stack trace should be generated when an error occurs.
     *
     * @return {@code true} to generate a stack trace; {@code false} to just
     * print an error, if present.
     */
    public boolean isStacktrace() {
        return stacktrace;
    }

    /**
     * Get the variable prefixes.
     *
     * @return non-{@code null} variable prefixes; if no prefixes were defined,
     * default prefixes will be returned.
     */
    public VariablePrefixes getVariablePrefixes() {
        return variablePrefixes;
    }

}
