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
package org.statefive.clic.genclc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.statefive.clic.ArgsListener;
import org.statefive.clic.OptionListener;
import org.statefive.clic.properties.TypeInferralConfigBuilder;

/**
 * CLI configuration generator option and arguments listener.
 *
 * @author rich
 */
public class OptionHelper implements OptionListener, ArgsListener {

    /**
     * Standard input notation for properties file as read from standard input.
     */
    private static final String STDIN = "-";

    /**
     * Builder to use, if specified.
     */
    private String builder;

    /**
     * {@code true} to read as Java properties, {@code false} to read as Apache
     * properties configuration.
     */
    private boolean javaProperties = false;

    /**
     * Allows duplicate properties.
     */
    private boolean allowDuplicates = false;

    /**
     * Include original file path to generated property sections (as a comment).
     */
    private boolean showImportOrigin = false;

    /**
     * Whether to pad configuration data with hash-commented strings.
     */
    private boolean pad = false;

    /**
     * Whether to insert default property values if they are not the empty
     * string.
     */
    private boolean insertDefaults = false;

    /**
     * Add header details to the generated output.
     */
    private boolean header = false;

    /**
     * Print stack trace when generating errors.
     */
    private boolean stacktrace = false;

    /**
     * List of properties files.
     */
    private final List<File> propsFiles = new ArrayList<>();

    /**
     * Read properties from standard input.
     */
    private boolean propsAsInputStream = false;

    /**
     * {@code true} to list types to standard output, {@code false} otherwise.
     */
    private boolean listTypes = false;

    /**
     * Value read from the command line for value types to load.
     */
    private String valueTypes;

    /**
     * File to output to (if set).
     */
    private File outputFile;

    /**
     * CLC overrides file.
     */
    private File config;

    /**
     * Include properties.
     */
    private final List<String> includes = new ArrayList<>();

    /**
     * Exclude properties.
     */
    private final List<String> excludes = new ArrayList<>();

    /**
     * Type inference configuration builder.
     */
    private final TypeInferralConfigBuilder typeInferralConfigBuilder = new TypeInferralConfigBuilder();

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
            case "o":
            case "output":
                outputFile = (File) value;
                break;
            case "c":
            case "config":
                config = (File) value;
                break;
            case "j":
            case "java-properties":
                javaProperties = true;
                break;
            case "s":
            case "show-import-origin":
                showImportOrigin = true;
                break;
            case "i":
            case "includes":
                buildCludes(includes, value.toString());
                break;
            case "x":
            case "excludes":
                buildCludes(excludes, value.toString());
                break;
            case "A":
            case "allow-duplicates":
                allowDuplicates = true;
                break;
            case "I":
            case "infer-types":
                typeInferralConfigBuilder.withInferTypes();
                break;
            case "N":
            case "natural-numbers-as":
                typeInferralConfigBuilder.withInferTypes()
                        .withNaturalNumbersAs((String) value);
                break;
            case "R":
            case "real-numbers-as":
                typeInferralConfigBuilder.withInferTypes()
                        .withRealNumbersAs((String) value);
                break;
            case "b":
            case "builder":
                builder = (String) value;
                break;
            case "H":
            case "header":
                header = true;
                break;
            case "X":
            case "stacktrace":
                stacktrace = true;
                break;
            case "p":
            case "pad":
                pad = true;
                break;
            case "D":
            case "insert-defaults":
                insertDefaults = true;
                break;
            case "F":
            case "false-as-unary":
                typeInferralConfigBuilder.withInferTypes()
                        .withFalseAsUnarySwitch();
                break;
            case "l":
            case "list-types":
                listTypes = true;
                break;
            case "value-types":
                valueTypes = (String) value;
                break;
            case "v":
            case "version":
                System.exit(0);
            case "h":
            case "help":
                System.exit(0);
            default:
                break;
        }
    }

    /**
     * Receive a file argument from the API. If the name matches
     * {@link CliGenConfig#STDIN}, the properties data will be read from
     * standard input instead of from the file.
     *
     * @param name non-{@code null} argument name; unused.
     *
     * @param index index in the sequence of arguments read.
     *
     * @param value non-{@code null} file name.
     */
    @Override
    public void argument(String name, int index, Object value) {
        File propsFile = (File) value;
        if (STDIN.equals(((File) value).getName())) {
            propsAsInputStream = true;
        } else {
            propsFiles.add(propsFile);
        }
    }

    /**
     * Add to the specified list the value split on commas and trimmed.
     *
     * @param cludes non-{@code null} list to add to.
     *
     * @param value non-{@code null} value to split and trim.
     */
    private void buildCludes(List<String> cludes, String value) {
        String[] values = value.split(",");
        for (int i = 0; i < values.length; i++) {
            cludes.add(values[i].trim());
        }
    }

    /**
     * Get the builder.
     *
     * @return Implementation specific builder, if provided; {@code null}
     * otherwise.
     */
    public String getBuilder() {
        return builder;
    }

    /**
     * Determine if Java properties should be used.
     *
     * @return {@code true} to use Java properties; {@code false} otherwise.
     */
    public boolean isJavaProperties() {
        return javaProperties;
    }

    /**
     * Determine if duplciates are allowed.
     *
     * @return {@code true} to allow duplciates, {@code false} otherwise.
     */
    public boolean isAllowDuplicates() {
        return allowDuplicates;
    }

    /**
     * Determine if import files should be included as hashed-out comments in
     * the generated configuration.
     *
     * @return {@code true} to show the origin of imports; {@code false}
     * otherwise.
     */
    public boolean isShowImportOrigin() {
        return showImportOrigin;
    }

    /**
     * Determine if header information should be generated.
     *
     * @return {@code true} to include header information; {@code false}
     * otherwise.
     */
    public boolean isHeader() {
        return header;
    }

    /**
     * Determine if hash-commented lines of other possible options should be
     * included.
     *
     * @return {@code true} to pad with other options, commented out;
     * {@code false} to not include them.
     */
    public boolean isPad() {
        return pad;
    }

    /**
     * Determine if to embed property defaults read from properties files.
     *
     * @return {@code true} to include the default values of properties;
     * {@code false} otherwise.
     */
    public boolean isInsertDefaults() {
        return insertDefaults;
    }

    /**
     * Get the list of properties files.
     *
     * @return non-{@code null} list of prpoerty files; if only standard input
     * was read from, the list will be empty.
     */
    public List<File> getPropsFiles() {
        return propsFiles;
    }

    /**
     * Determine if any properties were read from standard input.
     *
     * @return {@code true} if any properties were read from standard input;
     * {@code false} otherwise.
     */
    public boolean isPropsAsInputStream() {
        return propsAsInputStream;
    }

    /**
     * Determine if a stack trace should be generated when an error occurs.
     *
     * @return {@code true} to generate a stacktrace; {@code false} to just
     * print an error, if present.
     */
    public boolean isStacktrace() {
        return stacktrace;
    }

    /**
     * Get the list of value types to add to the application.
     *
     * @return defined list of colon-separated type names to class names of
     * value types to load; {@code null} if none were provided.
     */
    public String getValueTypes() {
        return valueTypes;
    }

    /**
     * Determine if listing of types should be displayed via standard output.
     *
     * @return {@code true} to list types; {@code false} otherwise.
     */
    public boolean isListTypes() {
        return listTypes;
    }

    /**
     * Get the output file to write the generated CLC output to.
     *
     * @return non-{@code null} file to write output to; {@code null} otherwise
     * (and output will be written to standard output).
     */
    public File getOutputFile() {
        return outputFile;
    }

    /**
     * Get the CLC overrides file.
     *
     * @return non-{@code null} existing CLC file, if set; {@code null}
     * otherwise.
     */
    public File getConfig() {
        return config;
    }

    /**
     * Get the list of properties to include.
     *
     * @return non-{@code null}, non-empty list of properties to include, if
     * set; the empty list otherwise.
     */
    public List<String> getIncludes() {
        return includes;
    }

    /**
     * Get the list of properties to exclude.
     *
     * @return non-{@code null}, non-empty list of properties to exclude, if
     * set; the empty list otherwise.
     */
    public List<String> getExcludes() {
        return excludes;
    }

    /**
     * Get the type inference configuration builder.
     *
     * @return non-{@code null} type configuration builder.
     */
    public TypeInferralConfigBuilder getTypeInferralConfigBuilder() {
        return typeInferralConfigBuilder;
    }
}
