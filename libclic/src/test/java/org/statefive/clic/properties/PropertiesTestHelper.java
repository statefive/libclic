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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.statefive.clic.ClcException;
import org.statefive.clic.ClcParser;
import static org.statefive.clic.GlobalConfiguration.GLOBAL_HELP_AUTO_USAGE;
import static org.statefive.clic.GlobalConfiguration.GLOBAL_HELP_COMMAND_FOOTER;
import static org.statefive.clic.GlobalConfiguration.GLOBAL_HELP_COMMAND_HEADER;
import static org.statefive.clic.GlobalConfiguration.GLOBAL_HELP_COMMAND_NAME;
import static org.statefive.clic.GlobalConfiguration.GLOBAL_HELP_FORMAT_COLUMN_SPACING;
import static org.statefive.clic.GlobalConfiguration.GLOBAL_HELP_FORMAT_LEFT_PAD;
import static org.statefive.clic.GlobalConfiguration.GLOBAL_HELP_FORMAT_SORT_OPTIONS;
import static org.statefive.clic.GlobalConfiguration.GLOBAL_HELP_FORMAT_WIDTH;
import static org.statefive.clic.GlobalConfiguration.GLOBAL_HELP_FORMAT_WIDTH_FROM_ENV;
import static org.statefive.clic.GlobalConfiguration.GLOBAL_HELP_OPTION_NAME;
import static org.statefive.clic.GlobalConfiguration.GLOBAL_HELP_SWITCH_OPTS;
import static org.statefive.clic.GlobalConfiguration.GLOBAL_OPTIONS_OPTS_TYPE;
import org.statefive.clic.valuetype.ValueType;
import org.statefive.clic.valuetype.ValueTypeFactory;
import org.statefive.clic.OptionsTypeEnum;

/**
 * Provide helper calls for tests.
 *
 * @author rich
 */
public class PropertiesTestHelper {

    /**
     * Remove all registered value types from the value type factory.
     */
    public static void clearValueTypes() {
        Set<String> toRemove = new HashSet<>(ValueTypeFactory.getInstance().getRegisteredValueTypes());
        for (Iterator<String> it = toRemove.iterator(); it.hasNext();) {
            ValueTypeFactory.getInstance().getRegisteredValueTypes().remove(it.next());
        }
    }

    /**
     * Create a file with the given properties in
     * {@code target/test-classes/properties/} in a directory named after the
     * given test name and file name named after the given file name.
     *
     * @param props non-{@code null} properties that can be parsed by
     * {@link #create(java.lang.String)}.
     *
     * @param testName non-{@code null} test name.
     *
     * @param filename non-{@code null} filename to give the file.
     *
     * @return non-{@code null} file with it's contents written to with the
     * specified properties.
     *
     * @throws IOException If the file cannot be written.
     */
    public static File createFile(String props, String testName, String filename)
            throws IOException {
        File dir = new File("target/test-classes/properties/" + testName);
        dir.mkdirs();
        File f = new File(dir + File.separator + filename);
        InputStream isProps = create(props);
        Files.copy(isProps, Paths.get(f.getAbsolutePath()));
        isProps.close();
        return f;
    }

    /**
     * Generate a stream of newline-separated {@code key=value} lines. No error
     * checking is made on the given string. Leading and trailing spaces are
     * trimmed.
     *
     * @param props non-{@code null} newline-separated {@code key = value}
     * pairs.
     *
     * @return non-{@code null} input stream.
     */
    public static InputStream create(String props) {
        Map<String, String> properties = new HashMap<>();
        String[] propsSplit = props.split("\n");
        for (String line : propsSplit) {
            int index = line.indexOf("=");
            String key = line.substring(0, index).trim();
            String value = line.substring(index + 1).trim();
            properties.put(key, value);
        }
        StringBuilder sb = new StringBuilder();
        for (String key : properties.keySet()) {
            sb.append(key)
                    .append(" = ")
                    .append(properties.get(key))
                    .append(System.lineSeparator());
        }
        return new ByteArrayInputStream(sb.toString().getBytes());
    }

    /**
     * Generate a default global header for long options and help.
     *
     * @return non-{@code null} stream containing default header.
     */
    public static InputStream createDefaultGlobalHeader() {
        Map<String, String> properties = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        properties.put(GLOBAL_OPTIONS_OPTS_TYPE, OptionsTypeEnum.LONG.name());
        properties.put(GLOBAL_HELP_OPTION_NAME, "help");
        properties.put(GLOBAL_HELP_COMMAND_NAME, "Default generated property help.");
        properties.put(GLOBAL_HELP_COMMAND_HEADER, "Auto-generated content.");
        properties.put(GLOBAL_HELP_COMMAND_FOOTER, "End of auto-generated content.");
        properties.put(GLOBAL_HELP_SWITCH_OPTS, "help");
        properties.put(GLOBAL_HELP_AUTO_USAGE, "false");
        properties.put(GLOBAL_HELP_FORMAT_COLUMN_SPACING, "5");
        properties.put(GLOBAL_HELP_FORMAT_LEFT_PAD, "1");
        properties.put(GLOBAL_HELP_FORMAT_WIDTH, "74");
        properties.put(GLOBAL_HELP_FORMAT_WIDTH_FROM_ENV, "false");
        properties.put(GLOBAL_HELP_FORMAT_SORT_OPTIONS, "false");
        for (String key : properties.keySet()) {
            sb.append(key)
                    .append(" = ")
                    .append(properties.get(key))
                    .append(System.lineSeparator());
        }
        return new ByteArrayInputStream(sb.toString().getBytes());
    }

    /**
     * Generate a stream of newline-separated {@code key=value} lines. No error
     * checking is made on the given string. Leading and trailing spaces are
     * trimmed.
     *
     * @param props non-{@code null} newline-separated {@code key = value}
     * pairs.
     *
     * @return non-{@code null} input stream.
     */
    public static InputStream create(Map<String, String> props) {
        StringBuilder sb = new StringBuilder();
        for (String key : props.keySet()) {
            sb.append(key)
                    .append(" = ")
                    .append(props.get(key))
                    .append(System.lineSeparator());
        }
        return create(sb.toString());
    }

    /**
     * Check that the given configuration contains default values for global
     * options and help options.
     *
     * @param config non-{@code null}, non-empty configurations.
     *
     * @throws Exception if any of the configurations are missing.
     */
    public static void hasDefaultHelpConfigOptions(Set<String> config)
            throws Exception {
        hasDefaultHelpConfigOptions(config, new HashSet<String>());
    }

    /**
     * Check that the given configuration contains default values for global
     * options and help options.
     *
     * @param config non-{@code null}, non-empty configurations.
     *
     * @param skip set of string to not not consider for testing; may be empty.
     *
     * @throws Exception if any of the configurations are missing.
     */
    public static void hasDefaultHelpConfigOptions(Set<String> config, Set<String> skip)
            throws Exception {
        if (!config.contains(GLOBAL_OPTIONS_OPTS_TYPE + " = LONG") && !config.contains(GLOBAL_OPTIONS_OPTS_TYPE + " = LONG")) {
            throw new Exception("Missing default configuration: "
                    + GLOBAL_OPTIONS_OPTS_TYPE + " = LONG");
        }
        if (!config.contains(GLOBAL_HELP_COMMAND_NAME + " = Default generated property help.") && !skip.contains(GLOBAL_HELP_COMMAND_NAME + " = Default generated property help.")) {
            throw new Exception("Missing default configuration: " + GLOBAL_HELP_COMMAND_NAME + " = Default generated property help.");
        }
        if (!config.contains(GLOBAL_HELP_COMMAND_HEADER + " = Auto-generated content.") && !skip.contains(GLOBAL_HELP_COMMAND_HEADER + " = Auto-generated content.")) {
            throw new Exception("Missing default configuration: " + GLOBAL_HELP_COMMAND_HEADER + " = Auto-generated content.");
        }
        if (!config.contains(GLOBAL_HELP_COMMAND_FOOTER + " = End of auto-generated content.") && !skip.contains(GLOBAL_HELP_COMMAND_FOOTER + " = End of auto-generated content.")) {
            throw new Exception("Missing default configuration: " + GLOBAL_HELP_COMMAND_FOOTER + " = End of auto-generated content.");
        }
        if (!config.contains(GLOBAL_HELP_OPTION_NAME + " = help") && !skip.contains(GLOBAL_HELP_OPTION_NAME + " = help")) {
            throw new Exception("Missing default configuration: " + GLOBAL_HELP_OPTION_NAME + " = help");
        }
        if (!config.contains(GLOBAL_HELP_AUTO_USAGE + " = false") && !skip.contains(GLOBAL_HELP_AUTO_USAGE + " = false")) {
            throw new Exception("Missing default configuration: " + GLOBAL_HELP_AUTO_USAGE + " = false");
        }
        if (!config.contains(GLOBAL_HELP_FORMAT_COLUMN_SPACING + " = 5") && !skip.contains(GLOBAL_HELP_FORMAT_COLUMN_SPACING + " = 5")) {
            throw new Exception("Missing default configuration: " + GLOBAL_HELP_FORMAT_COLUMN_SPACING + " = 5");
        }
        if (!config.contains(GLOBAL_HELP_FORMAT_LEFT_PAD + " = 1") && !skip.contains(GLOBAL_HELP_FORMAT_LEFT_PAD + " = 1")) {
            throw new Exception("Missing default configuration: " + GLOBAL_HELP_FORMAT_LEFT_PAD + " = 1");
        }
        if (!config.contains(GLOBAL_HELP_FORMAT_WIDTH + " = 74") && !skip.contains(GLOBAL_HELP_FORMAT_WIDTH + " = 74")) {
            throw new Exception("Missing default configuration: " + GLOBAL_HELP_FORMAT_WIDTH + " = 74");
        }
        if (!config.contains(GLOBAL_HELP_FORMAT_WIDTH_FROM_ENV + " = false") && !skip.contains(GLOBAL_HELP_FORMAT_WIDTH_FROM_ENV + " = false")) {
            throw new Exception("Missing default configuration: " + GLOBAL_HELP_FORMAT_WIDTH_FROM_ENV + " = false");
        }
        if (!config.contains(GLOBAL_HELP_FORMAT_SORT_OPTIONS + " = false") && !skip.contains(GLOBAL_HELP_FORMAT_SORT_OPTIONS + " = false")) {
            throw new Exception("Missing default configuration: " + GLOBAL_HELP_FORMAT_SORT_OPTIONS + " = false");
        }
        if (!config.contains(GLOBAL_HELP_SWITCH_OPTS + " = help") && !skip.contains(GLOBAL_HELP_SWITCH_OPTS + " = help")) {
            throw new Exception("Missing default configuration: " + GLOBAL_HELP_SWITCH_OPTS + " = help");
        }
        if (!config.contains("option.help.opts = help")) {
            throw new Exception("Missing default configuration: option.help.opts = help");
        }
        if (!config.contains("option.help.ignoreCliArgs = true")) {
            throw new Exception("Missing default configuration: option.help.ignoreCliArgs = true");
        }
        if (!config.contains("option.help.description = Print this help then exit.")) {
            throw new Exception("Missing default configuration: option.help.description = Print this help then exit.");
        }
    }

    /**
     * Check that the given configuration contains the specified option. Options
     * are defined in the form {@code x=y}.
     *
     * @param line non-{@code null}, non-empty configuration lines to check.
     *
     * @throws Exception if any of the configurations are missing.
     */
    public static void hasConfigOption(String line) throws Exception {
        if (!line.contains(line)) {
            throw new Exception("Expected to find '" + line
                    + "' in the configuration but it was missing.");
        }
    }

    /**
     * Check that the default comments are present.
     *
     * @param config non-{@code null}, non-empty configurations.
     *
     * @throws Exception if there are missing defaults.
     */
    public static void hasDefaultConfigComments(Set<String> config)
            throws Exception {
        if (!config.contains("# Global options")) {
            throw new Exception("Missing default comment: # Global options");
        }
        if (!config.contains("# Options configuration:")) {
            throw new Exception("Missing default comment: # Options configuration:");
        }
    }

    /**
     * Check that the given name is present in the form
     * {@code option.<name>.opts=<name>}.
     *
     * @param config non-{@code null}, non-empty configurations.
     *
     * @param name non-{@code null} name to check for the presence of.
     *
     * @throws Exception if the configuration is not present.
     */
    public static void hasConfigOpts(Set<String> config, String name) throws Exception {
        if (!config.contains("option." + name + ".opts = " + name)) {
            throw new Exception("Missing " + "option." + name + ".opts = " + name);
        }
    }

    /**
     * Check that the given name is present in the form
     * {@code option.<name>.type=<type>}.
     *
     * @param config non-{@code null}, non-empty configurations.
     *
     * @param name non-{@code null} option name to check for the presence of.
     *
     * @param type non-{@code null} valid {@link ValueType} type string.
     *
     * @throws Exception if the configuration is not present.
     */
    public static void hasConfigType(Set<String> config, String name, String type) throws Exception {
        if (!config.contains("option." + name + ".type = " + type)) {
            throw new Exception("Missing " + "option." + name + ".type = " + type);
        }
    }

    /**
     * Check that the given name is present in the form
     * {@code option.<name>.hasArg=true}.
     *
     * @param config non-{@code null}, non-empty configurations.
     *
     * @param name non-{@code null} name to check for the presence of.
     *
     * @throws Exception if the configuration is not present.
     */
    public static void hasConfigArg(Set<String> config, String name) throws Exception {
        if (!config.contains("option." + name + ".hasArg = true")) {
            throw new Exception("Missing " + "option." + name + ".hasArg = true");
        }
    }

    /**
     * Check that the given name is present in the form
     * {@code option.<name>.description=<description>}.
     *
     * @param config non-{@code null}, non-empty configurations.
     *
     * @param name non-{@code null} name to check for the presence of.
     *
     * @param description non-{@code null} description.
     *
     * @throws Exception if the configuration is not present.
     */
    public static void hasConfigDescription(Set<String> config, String name, String description) throws Exception {
        if (!config.contains("option." + name + ".description = " + description)) {
            throw new Exception("Missing " + "option." + name + ".description = " + description);
        }
    }

    /**
     * Check that the given name is present in the form
     * {@code option.<name>.default=<value>}.
     *
     * @param config non-{@code null}, non-empty configurations.
     *
     * @param name non-{@code null} name to check for the presence of.
     *
     * @param value non-{@code null} value.
     *
     * @throws Exception if the configuration is not present.
     */
    public static void hasConfigDefault(Set<String> config, String name, String value) throws Exception {
        if (!config.contains("option." + name + ".default = " + value)) {
            throw new Exception("Missing " + "option." + name + ".default = " + value);
        }
    }

    /**
     * Create a property filter from the given arguments.
     *
     * @param filters non-{@code null} comma-separated values; the values will
     * be split on the comma, if there are no commas the value will be filter
     * will consider the value to be a single include/exclude. Extraneous
     * leading and trailing white space will be trimmed.
     *
     * @param include {@code true} to create includes values for the filter;
     * {@code false} to create excludes.
     *
     * @return non-{@code null} property filter.
     */
    public static PropertyNameFilter createPropertyNameFilter(String filters,
            boolean include) {
        List<String> includes = new ArrayList<>();
        List<String> excludes = new ArrayList<>();
        if (filters.length() > 0) {
            String[] filterArray = filters.split(",");
            for (String item : filterArray) {
                if (include) {
                    includes.add(item.trim());
                } else {
                    excludes.add(item.trim());
                }
            }
        }
        return new PropertyNameFilter(includes, excludes);
    }

    /**
     * Get a more complex property map for testing.
     *
     * @return non-{@code null} property map.
     */
    public static Map<String, String> getMultipleValueTypePropertiesMap() {
        Map<String, String> map = new HashMap<>();
        // string:
        map.put("host.name", "localhost");
        // integer:
        map.put("host.port", "8080");
        // float:
        map.put("delay", "4.5");
        // boolean:
        map.put("reports", ClcParser.TRUE);
        // boolean:
        map.put("failover", ClcParser.FALSE);
        return map;
    }

    /**
     * Determine if any of the given lines contains the specified property.
     *
     * @param lines non-{@code null} lines to check.
     *
     * @param property non-{@code null} property to check.
     *
     * @return {@code true} if any of the lines match exactly with the specified
     * property; {@code false} otherwise.
     */
    public static boolean containsProperty(String[] lines, String property) {
        boolean contains = false;
        for (String line : lines) {
            if (line.equals(property)) {
                contains = true;
            }
        }
        return contains;
    }

    /**
     * Create a test file set used for directory traversal testing.
     *
     * @param dir non-{@code null} directory to create; created if it doesn't
     * exist.
     *
     * @param subdir non-{@code null} subdirectory to create within the given
     * directory.
     *
     * @throws IOException if the directories and files cannot be created.
     */
    public static void createTestFileSet(String dir, String subdir) throws IOException {
        File testDir = new File("target/test-classes/properties"
                + File.separator + dir + File.separator + subdir);
        if (!testDir.exists()) {
            testDir.mkdirs();
        }
        File dir1 = new File(testDir, "dir1");
        dir1.mkdirs();
        File dir1File1 = new File(dir1, "dir1File1.txt");
        dir1File1.createNewFile();
        File dir1File2 = new File(dir1, "dir1File2.png");
        dir1File2.createNewFile();
        File dir2 = new File(testDir, "dir2");
        dir2.mkdirs();
        File dir2File1 = new File(dir2, "dir2File1.txt");
        dir2File1.createNewFile();
        File dir2File2 = new File(dir2, "dir2File2.png");
        dir2File2.createNewFile();
    }

    /**
     * Check that the given file exists and that it's parent directory has the
     * specified directory name.
     *
     * @param dirUpdateFiles non-{@code null} set of files to check; may be
     * empty.
     *
     * @param parentDir non-{@code null} parent directory name of the file.
     *
     * @param filename non-{@code null} file name to look for.
     *
     * @return {@code true} if the file exists; {@code false} otherwise.
     */
    public static boolean checkHasFile(Set<File> dirUpdateFiles, String parentDir,
            String filename) {
        boolean found = false;
        for (Iterator<File> it = dirUpdateFiles.iterator(); it.hasNext();) {
            File f = it.next();
            if (f.getParentFile() == null) {
                // not a valid file for checking, carry on:
                continue;
            }
            String parent = f.getParentFile().getName();
            String name = f.getName();
            if (parent.equals(parentDir) && name.equals(filename)) {
                found = true;
                break;
            }
        }
        return found;
    }

    /**
     * Check that there is a command entry for the given name.
     *
     * @param name non-{@code null} name to check.
     *
     * @param lineData non-{@code null}, non-empty lines to check.
     *
     * @return {@code true} if the command name has an entry; {@code false}
     * otherwise.
     */
    public static boolean checkHasCommandName(String name, String[] lineData) {
        boolean hasCommandName = false;
        for (String line : lineData) {
            if (line.equals("command.name = " + name)) {
                hasCommandName = true;
                break;
            }
        }
        return hasCommandName;
    }

    /**
     * Check that there is a command usage entry for the given string.
     *
     * @param usage non-{@code null} usage.
     *
     * @param lineData non-{@code null}, non-empty lines to check.
     *
     * @return {@code true} if there is a command entry that matches the given
     * usage; {@code false} otherwise.
     */
    public static boolean checkHasCommandUsage(String usage, String[] lineData) {
        boolean hasCommandName = false;
        for (String line : lineData) {
            if (line.equals("command.usage = " + usage)) {
                hasCommandName = true;
                break;
            }
        }
        return hasCommandName;
    }

    /**
     * Check that the given data is present for the given command name.
     *
     * @param commandName non-{@code null} command name to find.
     *
     * @param optionName non-{@code null} option key value to find.
     *
     * @param opts non-{@code null} options value to find.
     *
     * @param arg argument value to find; may be {@code null}, in which case,
     * will not be searched for.
     *
     * @param description description value to find; may be {@code null}, in
     * which case, will not be searched for.
     *
     * @param lineData non-{@code null} line data to check.
     *
     * @return {@code true} if the command name is found and the option name and
     * options are present - if non-{@code null}, the argument and description
     * are present.
     */
    public static boolean checkHasCommandOption(String commandName,
            String optionName, String opts, String arg, String description,
            String[] lineData) {
        boolean hasCommandOpts = false;
        boolean hasCommandArg = false;
        boolean hasCommandDescription = false;
        if (arg == null) {
            hasCommandArg = true;
        }
        if (description == null) {
            hasCommandDescription = true;
        }
        boolean commandFound = false;
        for (String line : lineData) {
            if (line.strip().isEmpty()) {
                continue;
            }
            if (line.startsWith("command.name")) {
                if (line.equals("command.name = " + commandName)) {
                    commandFound = true;
                } else {
                    commandFound = false;
                }
            }
            if (commandFound) {
                if (line.equals("option." + optionName + ".opts = " + opts)) {
                    hasCommandOpts = true;
                }
                if (line.equals("option." + optionName + ".hasArg = " + arg)) {
                    hasCommandArg = true;
                }
                if (line.equals("option." + optionName + ".description = " + description)) {
                    hasCommandDescription = true;
                }
            }
        }
        return hasCommandOpts && hasCommandArg && hasCommandDescription;
    }

    /**
     * Check that the given path is present for the given command name.
     *
     * @param path non-{@code null} file path to check.
     *
     * @param commandName non-{@code null} command name associated with the
     * import origin.
     *
     * @param lineData non-{@code null} lines to check.
     *
     * @return {@code true} if the import origin file is present for the given
     * command name; {@code false} otherwise.
     */
    public static boolean hasImportOrigin(File path, String commandName, String[] lineData) {
        boolean hasImportOrigin = false;
        for (String line : lineData) {
            if (line.strip().isEmpty()) {
                continue;
            }
            if (line.startsWith("# Generated from: ")) {
                String toMatch = "# Generated from: " + path.getAbsolutePath();
                if (commandName != null) {
                    toMatch += " for command " + commandName;
                }
                if (line.equals(toMatch)) {
                    hasImportOrigin = true;
                    break;
                }
            }
        }
        return hasImportOrigin;
    }

    /**
     * Create a Java CLC generator from the given settings.
     *
     * @param properties non-{@code null} properties.
     *
     * @param clcOverrides CLC overrides; may be {@code null}.
     *
     * @param filter filter to apply; may be {@code null}.
     *
     * @param typeInferralConfig type inference configuration; may be
     * {@code null}.
     *
     * @param propertyVersion property to use for versioning; may be
     * {@code null}.
     *
     * @param globalHeader {@code true} to include the global header,
     * {@code false} otherwise.
     *
     * @param pad {@code true} to include hash-commented lines of extra options,
     * {@code false} otherwise.
     *
     * @param insertDefaults {@code true} to insert the property values as
     * default values into the configuration, {@code false} otherwise.
     *
     * @return non-{@code null} CLC generator.
     *
     * @throws ClcException if the generator cannot be created; see
     * {@link JavaPropertiesClcGeneratorBuilder#build()}.
     */
    public static ClcGenerator create(Properties properties,
            Configuration clcOverrides, PropertyNameFilter filter,
            TypeInferralConfig typeInferralConfig, String propertyVersion,
            boolean globalHeader, boolean pad, boolean insertDefaults) throws ClcException {
        return new JavaPropertiesClcGeneratorBuilder()
                .globalHeader(globalHeader)
                .insertDefaults(insertDefaults)
                .pad(pad)
                .properties(properties)
                .propertyNameFilter(filter)
                .propertyVersion(propertyVersion)
                .typeInferralConfig(typeInferralConfig)
                .build();
    }

    /**
     * Create a Apache properties configuration CLC generator from the given
     * settings.
     *
     * @param properties non-{@code null} properties.
     *
     * @param clcOverrides CLC overrides; may be {@code null}.
     *
     * @param filter filter to apply; may be {@code null}.
     *
     * @param typeInferralConfig type inference configuration; may be
     * {@code null}.
     *
     * @param propertyVersion property to use for versioning; may be
     * {@code null}.
     *
     * @param globalHeader {@code true} to include the global header,
     * {@code false} otherwise.
     *
     * @param pad {@code true} to include hash-commented lines of extra options,
     * {@code false} otherwise.
     *
     * @param insertDefaults {@code true} to insert the property values as
     * default values into the configuration, {@code false} otherwise.
     *
     * @return non-{@code null} CLC generator.
     *
     * @throws ClcException if the generator cannot be created; see
     * {@link PropertiesConfigurationClcGeneratorBuilder#build()}.
     */
    public static ClcGenerator create(PropertiesConfiguration properties,
            Configuration clcOverrides, PropertyNameFilter filter,
            TypeInferralConfig typeInferralConfig, String propertyVersion,
            boolean globalHeader, boolean pad, boolean insertDefaults) throws ClcException {

        return new PropertiesConfigurationClcGeneratorBuilder()
                .globalHeader(globalHeader)
                .insertDefaults(insertDefaults)
                .pad(pad)
                .properties(properties)
                .propertyNameFilter(filter)
                .propertyVersion(propertyVersion)
                .typeInferralConfig(typeInferralConfig)
                .build();
    }
}
