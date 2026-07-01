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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.cli.help.HelpFormatter;
import org.apache.commons.lang3.tuple.Pair;
import org.statefive.clic.properties.PropertiesBuilder;

/**
 * The global configuration is the configuration for an entire file with at
 * least one {@link OptionConfiguration}. Option configurations will be
 * associated with the main application unless {@link Command}s are defined, in
 * which case all option configurations are associated with the {@link Command}
 * that precedes them. This means that all configurations must contain top-level
 * (non-command) options first (if present), succeeded in turn by different
 * commands as they are processed in the order that the commands are defined.
 *
 * <p>
 * Lines beginning with &#35; are ignored. Each global configuration can only be
 * declared once; redeclaring a global option will produce an error.
 *
 * <p>
 * Global configuration items are as follows - the global options beginning with
 * {@code HELP_} enable callers to print help linked to an option without any
 * code whatsoever (more on this below):
 *
 * <ul>
 * <li>{@code global.options.opts-type=[GLOBAL_OPTIONS_OPTS_TYPE]}: where
 * {@code [GLOBAL_OPTIONS_OPTS_TYPE]} is one of {@code BOTH} where both short
 * and long options are used, specified as {@code [char]/[text]}, {@code SHORT}
 * where only short options are specified as a single character, and
 * {@code LONG} where only long options are specified. However,
 * {@code code global.options.opts-type} is not strictly required so long as the
 * options specified are all consistent (although it aids readability for others
 * maintaining the file to implicitly define
 * {@code code global.options.opts-type});</li>
 *
 * <li>{@code global.help.option.name=[optionName]}: define an option named
 * {@code [optionName]} for help. It is optional whether to define any
 * help-based options; if none are defined, a short and/or long option will be
 * defined as {@code -h} and {@code --help} (respectively) with the text
 * {@code Print this help then exit.};</li>
 *
 * <li>{@code global.help.command.usage=[commandName]}: The command name is the
 * name of the command that will be printed when the "Usage: commandName..." is
 * printed from a call to invoking the help option;</li>
 *
 * <li>{@code global.help.command.header=[headerText]}: where
 * {@code [headerText]} is the text that will be displayed as the header text
 * from an invocation to call for help to be printed. The command header is
 * optional; and</li>
 *
 * <li>{@code global.help.command.footer=[footerText]}: where
 * {@code [footerText]} is the text that will be displayed as the footer text
 * from an invocation to call for help to be printed. Like the header, the
 * command footer is optional.
 * </li>
 *
 * <li>{@code global.help.format.auto-usage}: Print all options along with the
 * command name;
 * </li>
 *
 * <li>{@code global.help.format.column-spacing}: Column spacing applied to
 * paragraphs for the help output; if the underlying commons CLI API deems that
 * the value is invalid for formatting, defaults will be used instead;</li>
 *
 * <li>{@code global.help.format.left-pad}: padding applied to the far left of
 * the command line switches; if the underlying commons CLI API deems that the
 * value is invalid for formatting, defaults will be used instead</li>
 *
 * <li>{@code global.help.format.width}: maximum width to write lines; if the
 * underlying commons CLI API deems that the value is invalid for formatting,
 * defaults will be used instead</li>
 *
 * <li>{@code global.help.format.width-from-env}: Obtain the maximum width from
 * the environmental variable {@code COLUMNS}. If the underlying commons CLI API
 * deems that the value is invalid for formatting, defaults will be used
 * instead;</li>
 *
 * <li>{@code global.help.format.sort-options}: By default options are printed
 * in the order that they are defined. Setting this to {@code true} will sort
 * listed switches alphabetically;</li>
 *
 * <li>{@code global.version.name=[optionName]}: like the
 * {@code global.help.option.name} define an option named {@code [optionName]}
 * for version. It is optional whether to define any version-based options; if
 * none are defined, a short and/or long option will be defined as {@code -v}
 * and {@code --version} (respectively) with the text
 * {@code Print version then exit.}</li>
 *
 * <li>{@code global.version.text=[versionText]}: The text to display when
 * printing the version. Dynamic entries can be read from the manifest file
 * {@code META-INF/MANIFEST.MF} by supplying the value
 * {@code ${manifest:<MANIFEST-KEY>}} where {@code <MANIFFEST-KEY>} is a
 * manifest entry. For example if the manifest file contains an entry with {@code
 * Implementation-Version: 1.2.3}, then giving the version option text as
 * {@code Version ${manifest:Implementation-Version}} then the text of the
 * version information will be "Version 1.2.3"</li>
 *
 * </ul>
 *
 * In all cases, lines may be escaped; escaped lines must end in a trailing
 * backslash character; lines to be appended must be indented using white space
 * (space character and/or tab characters). For example:
 *
 * <pre>
 * global.help.command.header=Show some useful information, \
 * with some extra escaped lines. \
 * Also, there's some extra information here about the command.
 * </pre>
 *
 * Note in the above example the spaces before the backslashes - this is so
 * sentences are not 'glued' together and provide spacing that is easy on the
 * eye to readers of the output.
 *
 * <p>
 * Usage may contain the character {@code \n} in which case the lines will be
 * padded with newlines when printing help. Since escaped lines are stripped of
 * whitespace at the start of succeeding lines, be sure to put newline
 * declarations at the start of the next line that they are defined especially
 * in the case where spaces are required to indent the help, for example:
 *
 * <pre>
 * global.help.command.header=Run the specified command. Command options are:\
 * \n  import: import the specified data.\
 * \n  export: export files to JSON format.\
 * \nTry\
 * \n  reporttool [command] -h\
 * \nor \
 * \n  reporttool [command] --help\
 * \nfor options.
 * </pre>
 *
 * <p>
 * ... Will print the following when help is invoked:
 *
 * <pre>
 * Run the specified command. Command options are:
 *   import: import the specified data.
 *   export: export files to JSON format.
 * Try
 *   reporttool [command] -h
 * or
 *   reporttool [command] --help
 * for options.
 * </pre>
 *
 * <p>
 * Regardless of how the lines are escaped with regard to the number of
 * characters per line, this will not affect the CLI help output since the CLI
 * {@link HelpFormatter} will format this according to the API rules for line
 * sizes.
 *
 * <p>
 * For example, by adding the following global options to the start of the
 * example file defined in {@link OptionConfiguration}:
 *
 * <pre>
 * # The following definition implies there must be an option.showHelp option
 * # defined in the options following these global definitions:
 * global.help.option.name=showHelp
 * global.help.command.usage=writeData
 * global.help.command.header=Write the specified text to the specified file. If the \
 * options contain spaces or special characters, supply the arguments in \
 * double quotes.
 * global.help.command.footer=Copyleft Foo, Bar &amp; Baz International.
 * </pre>
 *
 * <p>
 * ... When invoking {@code --help} or {@code -h} via the {@link Clc}:
 *
 * <pre>
 * InputStream is = ConfigurationParserTest.class.getResourceAsStream("opt.config");
 * Clc cliConfig = new Clc();
 * cliConfig.addOptionListener(listener);
 * // args[] from the public static void main(String[] args) call:
 * cliConfig.process(is, args);
 * </pre>
 *
 * <p>
 * ... Would produce the following output:
 *
 * <pre>
 * usage: writeData
 * Write the specified text to the specified file. If the options contain
 * spaces or special characters, supply the arguments in double quotes.
 *  -f,--file &lt;file&gt;   File to write to.
 *  -h,--help          Print this help then quit.
 *  -o,--overwrite     If set, write over the existing file; otherwise,
 *                     append to the file.
 *  -t,--text &lt;text&gt;   Text to write to the file.
 * Copyleft Foo, Bar &amp; Baz International.
 * </pre>
 *
 * <p>
 * Callers are required to decide what to do when help is invoked in this
 * manner; typically, in the {@link OptionListener}, callers will check for the
 * call to help and then exit gracefully.
 *
 * <p>
 * The {@code HELP_} global configuration options are not mandatory and are
 * there for convenience; however, configuration creators can omit these if they
 * wish to define their own help (in which case, the {@link OptionListener} must
 * cater for the call to help).
 *
 */
public class GlobalConfiguration {

    /**
     * Regular expression to match global option configurations. The general
     * form of a global configuration is upper case characters using underscores
     * (if necessary) with the value separated by an equals symbol, with the
     * value being any number of characters (with a minimum of one).
     */
    public static final String GLOBAL_OPTION_ASSIGNMENT_REGEX = "(global\\.[a-z0-9\\-\\.]+)\\s*=\\s*(.+)";

    /**
     * Prefix for all global help options.
     */
    public static final String GLOBAL_HELP = "global.help";

    /**
     * Default short option for help.
     */
    public static final String GLOBAL_HELP_OPTION_SHORT_DEFAULT = "h";

    /**
     * Default long option for help.
     */
    public static final String GLOBAL_HELP_OPTION_LONG_DEFAULT = "help";

    /**
     * Prefix for all global version options.
     */
    public static final String GLOBAL_VERSION = "global.version";

    /**
     * Default short option for version.
     */
    public static final String GLOBAL_VERSION_OPTION_SHORT_DEFAULT = "v";

    /**
     * Default long option for version.
     */
    public static final String GLOBAL_VERSION_OPTION_LONG_DEFAULT = "version";

    /**
     * Manifest entry substitution regex; used to replace version text with
     * entries from a manifest file.
     */
    public static final String OPTION_VERSION_MF_SUBS_REGEX = "\\{[a-zA-Z0-9]+\\}";

    /**
     * Manifest entry property name (e.g. {@code Implementation-Version} regex.
     */
    public static final String OPTION_VERSION_MF_PROP_REGEX
            = "[a-zA-Z0-9]+[a-zA-Z0-9\\-]*[a-zA-Z0-9]*";

    /**
     * Declaration for global option type (short, long, both).
     */
    public static final String GLOBAL_OPTIONS_OPTS_TYPE = "global.options.opts-type";

    /**
     * Declaration for the name of the command that will be invoked to show help
     * options.
     */
    public static final String GLOBAL_HELP_COMMAND_NAME = "global.help.command.usage";

    /**
     * Declaration for the switch options (long and short) for help.
     */
    public static final String GLOBAL_HELP_SWITCH_OPTS = "global.help.switch.opts";

    /**
     * Declaration for the header of the command that will be shown when
     * invoking help.
     */
    public static final String GLOBAL_HELP_COMMAND_HEADER = "global.help.command.header";

    /**
     * Declaration for the footer of the command that will be shown when
     * invoking help.
     */
    public static final String GLOBAL_HELP_COMMAND_FOOTER = "global.help.command.footer";

    /**
     * Declaration for the help option name (e.g. {@code option.help} of that is
     * defined in the {@link OptionConfiguration} such that when that CLI option
     * is invoked, help will be printed.
     */
    public static final String GLOBAL_HELP_OPTION_NAME = "global.help.option.name";

    /**
     * Boolean value used to determine if to supply auto-usage details about the
     * different options available.
     */
    public static final String GLOBAL_HELP_AUTO_USAGE = "global.help.format.auto-usage";

    /**
     * Width to indent paragraphs after the first line.
     */
    public static final String GLOBAL_HELP_FORMAT_COLUMN_SPACING = "global.help.format.column-spacing";

    /**
     * Number of spaces to pad from the left-hand side of the display.
     */
    public static final String GLOBAL_HELP_FORMAT_LEFT_PAD = "global.help.format.left-pad";

    /**
     * Maximum width lines will take up.
     */
    public static final String GLOBAL_HELP_FORMAT_WIDTH = "global.help.format.width";

    /**
     * Whether to attempt to get the maximum width from the environment.
     */
    public static final String GLOBAL_HELP_FORMAT_WIDTH_FROM_ENV = "global.help.format.width-from-env";

    /**
     * Whether to sort options alphabetically.
     */
    public static final String GLOBAL_HELP_FORMAT_SORT_OPTIONS = "global.help.format.sort-options";

    /**
     * Declaration for the version option name (e.g. {@code option.version} of
     * that is defined in the {@link OptionConfiguration} such that when that
     * CLI option is invoked, the version will be printed.
     */
    public static final String GLOBAL_VERSION_OPTION_NAME = "global.version.name";

    /**
     * The text output when the version command is invoked.
     */
    public static final String GLOBAL_VERSION_OPTION_TEXT = "global.version.text";

    /**
     * Declaration for the switch options (long and short) for help.
     */
    public static final String GLOBAL_VERSION_SWITCH_OPTS = "global.version.switch.opts";

    /**
     * Regular expression to detect manifest replacement text in version text.
     */
    public static final String REGEX_MANIFEST = "(?<!\\\\)\\$\\{manifest:([a-zA-Z\\-]*)\\}";

    /**
     * Regular expression to detect resource replacement text in version text.
     */
    public static final String REGEX_RESOURCE = "(?<!\\\\)\\$\\{resource:(.*)\\}";

    /**
     * The key is the actual name part of the {@code option.[name].*}
     * declaration.
     */
    private final Map<String, OptionConfiguration> optionMap = new LinkedHashMap<>();

    /**
     * Map of root-level arguments; the key is the actual name part of the
     * {@code args.[name].*} declaration.
     */
    private final Map<String, ArgsConfiguration> argsMap = new LinkedHashMap<>();

    /**
     * The option type of the configuration.
     */
    private OptionsTypeEnum optionsType;

    /**
     * If the global configuration has help defined, this is the name of the
     * command that will be printed with the help text.
     */
    private String helpCommandName;

    /**
     * If the global configuration has help defined, this is the header text of
     * the command that will be printed with the help text.
     */
    private String helpCommandHeader;

    /**
     * If the global configuration has help defined, this is the footer text of
     * the command that will be printed with the help text.
     */
    private String helpCommandFooter;

    /**
     * The option configuration name for the configuration help value,
     * {@code option.[name]}, for example, {@code option.help}.
     */
    private String helpOptionName;

    /**
     * Help short option.
     */
    private String helpOptionShort;

    /**
     * Help long option.
     */
    private String helpOptionLong;

    /**
     * Version short option.
     */
    private String versionOptionShort;

    /**
     * Version long option.
     */
    private String versionOptionLong;

    /**
     * Help auto usage.
     */
    private boolean helpAutoUsage = false;

    /**
     * Help column spacing.
     */
    private int helpColumnSpacing = HelpFormatter.DEFAULT_COLUMN_SPACING;

    /**
     * Help width.
     */
    private int helpWidth = HelpFormatter.DEFAULT_WIDTH;

    /**
     * Help left pad.
     */
    private int helpLeftPad = HelpFormatter.DEFAULT_LEFT_PAD;

    /**
     * Wether to obtain the maximum width from the environment, specifically an
     * environment variable named {@link Clc#ENV_MAX_COLUMNS}.
     */
    private boolean helpWidthFromEnv = false;

    /**
     * Whether to sort options alphabetically.
     */
    private boolean helpSortOptions = false;

    /**
     * The option configuration name for the configuration version value
     * {@code option.[name]}, for example, {@code option.version}.
     */
    private String versionOptionName;

    /**
     * The output when the version option is invoked; typically contains
     * copyright. company and license information.
     */
    private String versionOptionText;

    /**
     * Optional entries to be substituted in the version option text from
     * properties contained within the {@code META-INF/MANIFEST.MF} file.
     */
    private final Map<String, String> versionManifestEntries = new HashMap<>();

    /**
     * Determine whether to parse substitutions.
     */
    private boolean parseSubstitutions = true;

    /**
     * Root of all commands.
     */
    private final CommandRoot commandRoot = new CommandRoot();

    /**
     * Adapted from
     * {@link https://stackoverflow.com/questions/11306811/how-to-get-the-caller-class-in-java}.
     *
     * @return the caller class name if it could be determined.
     *
     * @throws ClcException if the class cannot be loaded.
     */
    public static String getCallerCallerClassName() throws ClcException {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        String callerClassName = null;
        for (int i = 1; i < stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            String cla$$Name = ste.getClassName();
            boolean propertiesBuilder = false;
            try {
                Class cls = Class.forName(cla$$Name);
                if (PropertiesBuilder.class.isAssignableFrom(cls)) {
                    propertiesBuilder = true;
                }
            } catch (Exception ex) {
                throw new ClcException(ex.getMessage(), ex);
            }
            String threadClassName = Thread.class.getName();
            if (!cla$$Name.equals(Clc.class.getName())
                    && !cla$$Name.equals(GlobalConfiguration.class.getName())
                    && !propertiesBuilder
                    && cla$$Name.indexOf(threadClassName) != 0) {
                if (callerClassName == null) {
                    callerClassName = ste.getClassName();
                } else if (!callerClassName.equals(ste.getClassName())) {
                    callerClassName = cla$$Name;
                    break;
                }
            }
        }
        return callerClassName;
    }

    /**
     * Update the given global configuration with the specified line data read.
     *
     * @param line non-{@code null} line data to parse that matches
     * {@link #GLOBAL_OPTION_ASSIGNMENT_REGEX}.
     *
     * @throws ClcException if the configuration is defined incorrectly.
     */
    public void updateGlobalConfiguration(String line) throws ClcException {
        String[] data = line.split("=");
        if (data[0].trim().matches(GlobalConfiguration.GLOBAL_OPTIONS_OPTS_TYPE)) {
            parseOptionType(data[1].trim());
        } else if (data[0].trim().startsWith(GLOBAL_HELP)) {
            parseHelp(line);
        } else if (data[0].trim().startsWith(GLOBAL_VERSION)) {
            parseVersion(line);
        } else {
            throw new ClcException(
                    "Unknown global configuration declaration: " + data[0].trim());
        }
    }

    /**
     * Get the name of the command that will be printed when (if) the user has
     * defined help.
     *
     * @return the name of the command that the help displays information for.
     */
    public String getHelpCommandName() {
        return helpCommandName;
    }

    /**
     * Get the help command header.
     *
     * @return the command header, if it has been set; {@code null} otherwise.
     */
    public String getHelpCommandHeader() {
        return helpCommandHeader;
    }

    /**
     * Get the help command footer.
     *
     * @return the command footer, if it has been set; {@code null} otherwise.
     */
    public String getHelpCommandFooter() {
        return helpCommandFooter;
    }

    /**
     * Get the option name specified by the global configuration; the name is
     * the name of the {@link OptionConfiguration} that must exist in the option
     * configurations.
     *
     * @return the help option name if it is set; {@code null} otherwise.
     */
    public String getHelpOptionName() {
        return helpOptionName;
    }

    /**
     * Get the help short option.
     *
     * @return the help short optin, if set;
     * {@link #GLOBAL_HELP_OPTION_SHORT_DEFAULT} otherwise.
     */
    public String getHelpOptionShort() {
        if (helpOptionShort == null) {
            helpOptionShort = GLOBAL_HELP_OPTION_SHORT_DEFAULT;
        }
        return helpOptionShort;
    }

    /**
     * Get the help long option.
     *
     * @return the help short optin, if set;
     * {@link #GLOBAL_HELP_OPTION_LONG_DEFAULT} otherwise.
     */
    public String getHelpOptionLong() {
        if (helpOptionLong == null) {
            helpOptionLong = GLOBAL_HELP_OPTION_LONG_DEFAULT;
        }
        return helpOptionLong;
    }

    /**
     * Get the version short option.
     *
     * @return the version short optin, if set;
     * {@link #GLOBAL_VERSION_OPTION_SHORT_DEFAULT} otherwise.
     */
    public String getVersionOptionShort() {
        if (versionOptionShort == null) {
            versionOptionShort = GLOBAL_VERSION_OPTION_SHORT_DEFAULT;
        }
        return versionOptionShort;
    }

    /**
     * Get the version long option.
     *
     * @return the version short optin, if set;
     * {@link #GLOBAL_VERSION_OPTION_LONG_DEFAULT} otherwise.
     */
    public String getVersionOptionLong() {
        if (versionOptionLong == null) {
            versionOptionLong = GLOBAL_VERSION_OPTION_LONG_DEFAULT;
        }
        return versionOptionLong;
    }

    /**
     * Set whether to parse substitutions.
     *
     * @param parseSubstitutions {@code true} to parse substitutions;
     * {@code false} otherwise.
     */
    public void setParseSubstitutions(boolean parseSubstitutions) {
        this.parseSubstitutions = parseSubstitutions;
    }

    /**
     * Determine if auto usage is enabled.
     *
     * @return {@code true} if auto usage is enabled, {@code false} otherwise.
     */
    public boolean isHelpAutoUsage() {
        return helpAutoUsage;
    }

    /**
     * Get the help colummn spacing.
     *
     * @return the column spacing if set; if not set, defaults to the commons
     * CLI value {@code HelpFormatter.DEFAULT_COLUMN_SPACING}.
     */
    public int getHelpColumnSpacing() {
        return helpColumnSpacing;
    }

    /**
     * Get the help width.
     *
     * @return the help width if set; if not set, defaults to the commons CLI
     * value {@code HelpFormatter.DEFAULT_WIDTH}.
     */
    public int getHelpWidth() {
        return helpWidth;
    }

    /**
     * Determine if the help width should be picked up from the environment.
     *
     * @return {@code true} if the help width should be derived from the
     * environmental variable {@link Clc#ENV_MAX_COLUMNS};
     * {@code false} otherwise.
     */
    public boolean isHelpWidthFromEnv() {
        return helpWidthFromEnv;
    }

    /**
     * Get the help left padding.
     *
     * @return the left padding if set; if not set, defaults to the commons CLI
     * value {@code HelpFormatter.DEFAULT_LEFT_PAD}.
     */
    public int getHelpLeftPad() {
        return helpLeftPad;
    }

    /**
     * Determine whether to sort options alphabetically.
     *
     * @return {@code true} to sort help options alphabetically, {@code false}
     * otherwise.
     */
    public boolean isHelpSortOptions() {
        return helpSortOptions;
    }

    /**
     * Get the name of the version option name - the name will be the name found
     * in the {@code option.[option-name]...} declarations.
     *
     * @return the option name if it is set; {@code null} otherwise.
     */
    public String getVersionOptionName() {
        return versionOptionName;
    }

    /**
     * Get the text to display when the version option (if defined) is invoked.
     *
     * @return the option value text if it is defined; {@code null} otherwise.
     */
    public String getVersionOptionText() {
        return versionOptionText;
    }

    /**
     * Get the (optional) version manifest entries.
     *
     * @return the version manifest entries if there are any; the empty map
     * otherwise.
     */
    public Map<String, String> getVersionManifestEntries() {
        return versionManifestEntries;
    }

    /**
     * Get the command root.
     *
     * @return non-{@code null} command root.
     */
    public CommandRoot getCommandRoot() {
        return commandRoot;
    }

    /**
     * Add the specified option configuration. All configurations will be
     * assigned to the root-level configuration until a command is processed,
     * after which configurations will be assigned commands as they are
     * processed.
     *
     * @param optionConfig non-{@code null} option configuration to add.
     */
    public void addOptionConfiguration(OptionConfiguration optionConfig) {
        if (commandRoot.getCurrentCommand() != null) {
            commandRoot.getCurrentCommand().addOptionConfiguration(optionConfig.getName(),
                    optionConfig);
        } else {
            // add it to the top-level of this global configuration
            optionMap.put(optionConfig.getName(), optionConfig);
        }
    }

    /**
     * Add the specified arguments configuration.
     *
     * @param argsConfig non-{@code null} option configuration to add.
     */
    public void addArgsConfiguration(ArgsConfiguration argsConfig) {
        if (commandRoot.getCurrentCommand() != null) {
            commandRoot.getCurrentCommand().addArgsConfiguration(argsConfig.getName(),
                    argsConfig);
        } else {
            // add it to the top-level of this global configuration
            argsMap.put(argsConfig.getName(), argsConfig);
        }
    }

    /**
     *
     * @return
     */
    public boolean hasArgsConfiguration() {
        return !argsMap.isEmpty();
    }

    /**
     * Get the minimum length of arguments for root-level command line
     * arguments.
     *
     * @return 0 if there are no argument configurations; otherwise, for all
     * non-optional argument configurations the sum of the length of those
     * configurations; if the last configuration is optional but unbounded, the
     * length will be the length of all non-optional configurations (if present)
     * plus 1.
     */
    public Integer getArgsMinLength() {
        int len = 0;
        if (hasArgsConfiguration()) {
            for (String key : argsMap.keySet()) {
                ArgsConfiguration argsConfig = argsMap.get(key);
                if (!argsConfig.isOptional()) {
                    if (argsConfig.getLength() != null) {
                        // fixed length:
                        len += argsConfig.getLength();
                    } else {
                        // unbounded, non-optional value so +1:
                        len += 1;
                    }
                }
            }
        }
        return len;
    }

    /**
     * Get the maximum length of arguments for root-level command line
     * arguments.
     *
     * @return the total maximum length of arguments, if there are any (not
     * including optional arguments); {@link Integer#MAX_VALUE} otherwise. If
     * the last configuration is variable length the maximum length will be
     * {@link Integer#MAX_VALUE}.
     */
    public Integer getArgsMaxLength() {
        int len = Integer.MAX_VALUE;
        if (hasArgsConfiguration()) {
            for (String key : argsMap.keySet()) {
                ArgsConfiguration argsConfig = argsMap.get(key);
                if (argsConfig.getLength() != null) {
                    if (len == Integer.MAX_VALUE) {
                        len = 0;
                    }
                    // fixed length:
                    int lenCheck = len + argsConfig.getLength();
                    if (lenCheck < 0) {
                        len = Integer.MAX_VALUE;
                    } else {
                        len += argsConfig.getLength();
                    }
                } else {
                    len = Integer.MAX_VALUE;
                    // cannot exceed this, quit the loop:
                    break;
                }
            }
        }
        return len;
    }

    /**
     * Get the minimum length of arguments for root-level command line
     * arguments.
     *
     * @param command non-{@code null} existing command to check.
     *
     * @return 0 if there are no argument configurations; otherwise, for all
     * non-optional argument configurations the sum of the length of those
     * configurations; if the last configuration is optional but unbounded, the
     * length will be the length of all non-optional configurations (if present)
     * plus 1.
     */
    public Integer getArgsMinLength(Command command) {
        int len = 0;
        if (!command.getArgsConfigurations().isEmpty()) {
            for (String key : command.getArgsConfigurations().keySet()) {
                ArgsConfiguration argsConfig = command.getArgsConfigurations().get(key);
                if (!argsConfig.isOptional()) {
                    if (argsConfig.getLength() != null) {
                        // fixed length:
                        len += argsConfig.getLength();
                    } else {
                        // unbounded, non-optional value so +1:
                        len += 1;
                    }
                }
            }
        }
        return len;
    }

    /**
     * Get the minimum length of arguments for root-level command line
     * arguments.
     *
     * @param command non-{@code null} existing command to check.
     *
     * @return If there are no argument configurations or the last argument
     * configuration is unbounded, return {@link Integer#MAX_VALUE}; otherwise
     * the sum-total of all argument configurations that are non-optional.
     */
    public Integer getArgsMaxLength(Command command) {
        int len = Integer.MAX_VALUE;
        if (!command.getArgsConfigurations().isEmpty()) {
            for (String key : command.getArgsConfigurations().keySet()) {
                ArgsConfiguration argsConfig = command.getArgsConfigurations().get(key);
                if (argsConfig.getLength() != null) {
                    if (len == Integer.MAX_VALUE) {
                        len = 0;
                    }
                    // fixed length:
                    int lenCheck = len + argsConfig.getLength();
                    if (lenCheck < 0) {
                        len = Integer.MAX_VALUE;
                    } else {
                        len += argsConfig.getLength();
                    }
                } else {
                    len = Integer.MAX_VALUE;
                    // cannot exceed this, quit the loop:
                    break;
                }
            }
        }
        return len;
    }

    /**
     * Get the option map for this configuration; the current command's
     * configuration will be returned if there is one, otherwise the global
     * configuration will be returned - the key to the map will be the option
     * configuration names defined by the {@code option.[name]} declarations.
     *
     * @return the non-{@code null}, non-empty option map either of the global
     * configuration or the configuration for the current command (note that if
     * no option configurations are defined when parsing an exception will be
     * thrown).
     */
    public Map<String, OptionConfiguration> getOptionConfigurations() {
        Map<String, OptionConfiguration> configs = Collections.unmodifiableMap(
                optionMap);
        if (commandRoot.getCurrentCommand() != null) {
            configs = Collections.unmodifiableMap(commandRoot.getCurrentCommand().getOptionConfigurations());
        }
        return configs;
    }

    /**
     * Get the current argument configurations being processed, either for the
     * top-level configuration or the current command being processed.
     *
     * @return map of configurations either for the top-level arguments or the
     * command being parsed; if either the top-level arguments are not present
     * or the current command has no argument configuration, the empty map is
     * returned.
     */
    public Map<String, ArgsConfiguration> getCurrentArgsConfigurations() {
        Map<String, ArgsConfiguration> configs = Collections.unmodifiableMap(
                argsMap);
        if (commandRoot.getCurrentCommand() != null) {
            configs = Collections.unmodifiableMap(commandRoot.getCurrentCommand().getArgsConfigurations());
        }
        return configs;
    }

    /**
     * Get the top-level (non-command) argument configurations. Callers wishing
     * to access argument configurations for commands should do so by accessing
     * the commands via {@link #getCommands()}.
     *
     * @return the argument configurations, if there are any; the empty map
     * otherwise.
     */
    public Map<String, ArgsConfiguration> getArgsConfigurations() {
        return Collections.unmodifiableMap(argsMap);
    }

    /**
     * Get the option map for this configuration - the key to the map will be
     * the option configuration names defined by the {@code option.[name]}
     * declarations.
     *
     * @return the non-{@code null}, non-empty option map either of the global
     * configuration or the configuration for the current command (note that if
     * no option configurations are defined when parsing an exception will be
     * thrown).
     */
    public Map<String, OptionConfiguration> getGlobalOptionConfigurations() {
        return Collections.unmodifiableMap(optionMap);
    }

    /**
     * Get the options type for this global configuration.
     *
     * @return the options type; may be {@code null}.
     */
    public OptionsTypeEnum getOptionsType() {
        return optionsType;
    }

    /**
     * Set the options type for this global configuration.
     *
     * @param optionsType the options type.
     */
    public void setOptionsType(OptionsTypeEnum optionsType) {
        this.optionsType = optionsType;
    }

    /**
     * Add default option values for help.
     */
    public void addDefaultHelp() {
        String globalHelpOptionName = getHelpOptionName();
        OptionConfiguration optConfigHelp = new OptionConfiguration();
        optConfigHelp.setName(getHelpOptionName());
        optConfigHelp.setDescription("Print this help then exit.");
        optConfigHelp.setHasArg(false);
        optConfigHelp.setIgnoreCliArgs(true);
        if (null == getOptionsType()) {
            optConfigHelp.setShortOption(getHelpOptionShort());
            optConfigHelp.setLongOption(getHelpOptionLong());
        } else {
            switch (getOptionsType()) {
                case SHORT:
                    optConfigHelp.setShortOption(getHelpOptionShort());
                    break;
                case LONG:
                    optConfigHelp.setLongOption(getHelpOptionLong());
                    break;
                case BOTH:
                default:
                    optConfigHelp.setShortOption(getHelpOptionShort());
                    optConfigHelp.setLongOption(getHelpOptionLong());
                    break;
            }
        }
        optionMap.put(globalHelpOptionName, optConfigHelp);

    }

    /**
     * Add default option values for version.
     */
    public void addDefaultVersion() {
        String globalVersionOptionName = getVersionOptionName();
        OptionConfiguration optConfigVersion = new OptionConfiguration();
        optConfigVersion.setName(getVersionOptionName());
        optConfigVersion.setDescription("Print version then exit.");
        optConfigVersion.setHasArg(false);
        optConfigVersion.setIgnoreCliArgs(true);
        if (null == getOptionsType()) {
            optConfigVersion.setShortOption(getVersionOptionShort());
            optConfigVersion.setLongOption(getVersionOptionLong());
        } else {
            switch (getOptionsType()) {
                case SHORT:
                    optConfigVersion.setShortOption(getVersionOptionShort());
                    break;
                case LONG:
                    optConfigVersion.setLongOption(getVersionOptionLong());
                    break;
                case BOTH:
                default:
                    optConfigVersion.setShortOption(getVersionOptionShort());
                    optConfigVersion.setLongOption(getVersionOptionLong());
                    break;
            }
        }
        optionMap.put(globalVersionOptionName, optConfigVersion);
    }

    /**
     * Perform substitutions for {@code ${manifest:&lt;key&gt;}} and
     * {@code ${manifest:&lt;key&gt;}} declarations; if the specified text
     * contains either substitutions they will be performed. Resources
     * containing {@code ${manifest:&lt;key&gt;}} entries will also be parsed,
     * although {@code ${resource:&lt;key&gt;}} entries may not contain nested
     * {@code ${resource:&lt;key&gt;}} declarations.
     *
     * @param input non-{@code null} input to convert.
     *
     * @return text with any substitutions for manifest entries or resource
     * entries performed; if there are no such substitutions, the return value
     * will be the same as the input.
     *
     * @throws ClcException if any manifest declarations are not present in the
     * manifest file, the manifest file doesn't exist or the resource doesn't
     * exist.
     */
    String makeSubstitutions(String input) throws ClcException {
        String transformed = input;
        if (parseSubstitutions) {
            transformed = substituteManifestEntries(input);
            transformed = substituteResourceEntries(transformed);
        }
        return transformed;
    }

    /**
     * Substitute any {@code ${manifest:&lt;key&gt;}} entries with the values
     * stored in the application manifest file.
     *
     * @param input input to convert.
     *
     * @return the same value as the input if there were no replacements;
     * otherwise, all entries that have a key entry of the form
     * {@code ${manifest:&lt;key&gt;}} will have that text replaced with the
     * corresponding value from the manifest file.
     *
     * @throws ClcException if an manifest entry is specified that doesn't
     * exist.
     */
    private String substituteManifestEntries(String input) throws ClcException {
        String output = input;
        Pattern p = Pattern.compile(REGEX_MANIFEST);
        Matcher m = p.matcher(input);
        List<String> manifestEntries = new ArrayList<>();
        while (m.find()) {
            manifestEntries.add(m.group(1));
        }
        if (!manifestEntries.isEmpty()) {
            // try and extract the entries:
            Map<String, String> mfEntries = getManifestEntries(manifestEntries);
            for (String key : mfEntries.keySet()) {
                output = output.replace("${manifest:" + key + "}",
                        mfEntries.get(key));
            }
        }
        return output;
    }

    /**
     * Substitute any {@code ${resource:&lt;key&gt;}} entries with the values
     * stored in the specified embedded resource.
     *
     * @param non-{@code null} input input to convert.
     *
     * @return the same value as the input if there were no replacements;
     * otherwise, all entries that have a key entry of the form
     * {@code ${resource:&lt;key&gt;}} will have that text replaced with the
     * corresponding value from the given embedded resource file.
     *
     * @throws ClcException if the resource does not exist or the content of the
     * resource references any non-existant manifest entries, or when a manifest
     * entry is present the manifest file is not present.
     */
    private String substituteResourceEntries(String input) throws ClcException {
        String output = input;
        Pattern p = Pattern.compile(REGEX_RESOURCE);
        Matcher m = p.matcher(input);
        List<String> resourceEntries = new ArrayList<>();
        while (m.find()) {
            String key = m.group(1);
            resourceEntries.add(key);
        }
        for (String resource : resourceEntries) {
            StringBuilder sb = new StringBuilder();
            // try and extract the entries:
            InputStream is = GlobalConfiguration.class.getResourceAsStream(resource);
            try {
                InputStreamReader reader = new InputStreamReader(is);
                LineNumberReader lineReader = new LineNumberReader(reader);
                String line = null;
                while ((line = lineReader.readLine()) != null) {
                    sb.append(substituteManifestEntries(line));
                }
            } catch (IOException | NullPointerException ex) {
                throw new ClcException(ex.getMessage());
            }
            output = output.replace("${resource:" + resource + "}", sb.toString());
        }
        return output;
    }

    /**
     * Parse the option type.
     *
     * @param data data containing the option type - one of
     * {@link #GLOBAL_OPTION_TYPE_SHORT}, {@link #GLOBAL_OPTION_TYPE_LONG}, or
     * {@link #GLOBAL_OPTION_TYPE_BOTH}.
     *
     * @throws ClcException if the global options type has already been set, or
     * if the options type did not match a known type.
     */
    private void parseOptionType(String data) throws ClcException {
        if (getOptionsType() != null) {
            throw new ClcException(GLOBAL_OPTIONS_OPTS_TYPE
                    + " has already been defined as "
                    + getOptionsType().getType()
                    + " but found second definition: " + data);
        }
        if (OptionsTypeEnum.BOTH.getType().equals(data)) {
            setOptionsType(OptionsTypeEnum.BOTH);
        } else if (OptionsTypeEnum.SHORT.getType().equals(data)) {
            setOptionsType(OptionsTypeEnum.SHORT);
        } else if (OptionsTypeEnum.LONG.getType().equals(data)) {
            setOptionsType(OptionsTypeEnum.LONG);
        } else {
            throw new ClcException("Unknown options type: " + data);
        }
    }

    /**
     * Parses global configurations that begin with {@code global.help}.
     *
     * @param line non-{@code null} line to parse.
     */
    private void parseHelp(final String line) throws ClcException {
        String[] data = line.split("=");
        if (GLOBAL_HELP_OPTION_NAME.equals(data[0].trim())) {
            if (helpOptionName != null) {
                throw new ClcException(GLOBAL_HELP_OPTION_NAME
                        + " has already been defined.");
            }
            helpOptionName = makeSubstitutions(data[1].trim());
        } else {
            if (helpOptionName == null) {
                throw new ClcException("Global help definition "
                        + data[0].trim() + " must come after "
                        + GLOBAL_HELP_OPTION_NAME + ", but no such definition"
                        + " exists.");
            }
            switch (data[0].trim()) {
                case GLOBAL_HELP_COMMAND_NAME:
                    if (helpCommandName != null) {
                        throw new ClcException(GLOBAL_HELP_COMMAND_NAME
                                + " has already been defined.");
                    }
                    helpCommandName = makeSubstitutions(data[1].trim());
                    break;
                case GlobalConfiguration.GLOBAL_HELP_COMMAND_HEADER:
                    if (helpCommandHeader != null) {
                        throw new ClcException(GLOBAL_HELP_COMMAND_HEADER
                                + " has already been defined.");
                    }
                    helpCommandHeader = makeSubstitutions(data[1].trim());
                    break;
                case GLOBAL_HELP_COMMAND_FOOTER:
                    if (helpCommandFooter != null) {
                        throw new ClcException(GLOBAL_HELP_COMMAND_FOOTER
                                + " has already been defined.");
                    }
                    helpCommandFooter = makeSubstitutions(data[1].trim());
                    break;
                case GLOBAL_HELP_SWITCH_OPTS:
                    if (helpOptionShort != null || helpOptionLong != null) {
                        throw new ClcException(GLOBAL_HELP_SWITCH_OPTS
                                + " has already been defined.");
                    }
                    Pair<String, String> options = ClcParser.parseShortLongOptions(
                            data[1].trim(), null);
                    helpOptionShort = options.getLeft();
                    helpOptionLong = options.getRight();
                    break;
                case GlobalConfiguration.GLOBAL_HELP_AUTO_USAGE:
                    helpAutoUsage = Boolean.parseBoolean(data[1].trim());
                    break;
                case GlobalConfiguration.GLOBAL_HELP_FORMAT_COLUMN_SPACING:
                try {
                    helpColumnSpacing = Integer.parseInt(
                            makeSubstitutions(data[1].trim()));
                } catch (NumberFormatException ex) {
                    throw new ClcException("Invalid "
                            + GLOBAL_HELP_FORMAT_COLUMN_SPACING + "; must be a number.");
                }
                break;
                case GlobalConfiguration.GLOBAL_HELP_FORMAT_LEFT_PAD:
                try {
                    helpLeftPad = Integer.parseInt(
                            makeSubstitutions(data[1].trim()));
                } catch (NumberFormatException ex) {
                    throw new ClcException("Invalid "
                            + GLOBAL_HELP_FORMAT_LEFT_PAD + "; must be a number.");
                }
                break;
                case GlobalConfiguration.GLOBAL_HELP_FORMAT_WIDTH:
                try {
                    helpWidth = Integer.parseInt(
                            makeSubstitutions(data[1].trim()));
                } catch (NumberFormatException ex) {
                    throw new ClcException("Invalid "
                            + GLOBAL_HELP_FORMAT_WIDTH + "; must be a number.");
                }
                break;
                case GlobalConfiguration.GLOBAL_HELP_FORMAT_WIDTH_FROM_ENV:
                    helpWidthFromEnv = Boolean.parseBoolean(
                            makeSubstitutions(data[1].trim()));
                    break;
                case GlobalConfiguration.GLOBAL_HELP_FORMAT_SORT_OPTIONS:
                    helpSortOptions = Boolean.parseBoolean(
                            makeSubstitutions(data[1].trim()));
                    break;
                default:
                    throw new ClcException("Unknown global help configuration: " + line);
            }
        }
    }

    /**
     * Parses global configurations that begin with {@code global.version}.
     *
     * @param line non-{@code null} line to parse.
     */
    private void parseVersion(final String line) throws ClcException {
        String[] data = line.split("=");
        switch (data[0].trim()) {
            case GLOBAL_VERSION_OPTION_NAME:
                if (versionOptionName != null) {
                    throw new ClcException(GLOBAL_VERSION_OPTION_NAME
                            + " has already been defined.");
                }
                versionOptionName = makeSubstitutions(data[1].trim());
                break;
            case GLOBAL_VERSION_OPTION_TEXT:
                if (versionOptionText != null) {
                    throw new ClcException(GLOBAL_VERSION_OPTION_TEXT
                            + " has already been defined.");
                }
                versionOptionText = makeSubstitutions(data[1].trim());
                break;
            case GLOBAL_VERSION_SWITCH_OPTS:
                if (versionOptionShort != null || versionOptionLong != null) {
                    throw new ClcException(GLOBAL_VERSION_SWITCH_OPTS
                            + " has already been defined.");
                }
                Pair<String, String> options = ClcParser.parseShortLongOptions(
                        data[1].trim(), null);
                versionOptionShort = options.getLeft();
                versionOptionLong = options.getRight();
                break;
            default:
                throw new ClcException("Unknown global version configuration: "
                        + line);
        }
    }

    /**
     * Substitute the given text with manifest entry keys contained within the
     * given list.
     *
     * @param entries non-{@code null}list manifest entries to use for the
     * substitutions; all entries must exist as manifest entry keys; may be
     * empty.
     *
     * @return text with the substitutions made.
     *
     * @throws ClcException if the manifest is missing or does not contain any
     * of the specified values.
     */
    private Map<String, String> getManifestEntries(List<String> keys)
            throws ClcException {
        Class c = null;
        InputStream is = null;
        try {
            c = Class.forName(getCallerCallerClassName());
        } catch (ClassNotFoundException ex) {
            throw new ClcException("Looking to main class failed: "
                    + ex.getMessage());
        }
        // substitute declarations e.g. {1}: Foo-Impl, where manifest contains
        // Foo-Impl: 3.2.1X such that we get {1} mapped to '3.2.1X' etc.:
        Map<String, String> mfEntries = new HashMap<>();
        try {
            CodeSource src = c.getProtectionDomain().getCodeSource();
            Attributes attributes = null;
            boolean isDir = true;
            File path = null;
            Manifest mf = null;
            if (src != null) {
                URL jar = src.getLocation();
                path = Paths.get(jar.toURI()).toFile();
                isDir = path.isDirectory();
//                System.out.println("CodeSource : " + path.getAbsolutePath());
                if (!isDir) {
                    is = new FileInputStream(path);
                    JarInputStream jarStream = new JarInputStream(is);
                    mf = jarStream.getManifest();
                    attributes = mf.getMainAttributes();
//                    System.out.println(">>> MANIFEST: getting from jar");
                }
            }
            if (src == null || isDir) {
                // INTERESTING!
                // URL url = getClass().getResource("/META-INF/MANIFEST.MF");
                // returns:
                // jar:file:/usr/share/java/java-atk-wrapper.jar!/META-INF/MANIFEST.MF
                // this will be different per-OS, so need to re-think this one ^
                if (path == null) {
//                    System.out.println("CWD: " + new File(".").getAbsolutePath());
                    // read from external file - implies will load from  ./META-INF/MANIFEST.M
                    path = new File(".");
                }
                path = new File(path.getAbsolutePath()
                        + File.separator + "META-INF/MANIFEST.MF");
//                System.out.println("URL=" + url);
                if (path.exists() && path.isFile()) {
//                    System.out.println(">>> MANIFEST: getting from file");
//                    File file = Paths.get(url.toURI()).toFile();
//                    System.out.println(file.getAbsolutePath());
//                    if (file.exists()) {
                    is = new FileInputStream(path);
                    mf = new Manifest(is);
                    attributes = mf.getMainAttributes();
//                    }
                } else {
                    // try and load from JAR file manifest
                    is = getClass().getResourceAsStream("/META-INF/MANIFEST.MF");
                    mf = new Manifest(is);
                    attributes = mf.getMainAttributes();
//                    System.out.println(">>> MANIFEST: getting from jar");
                }
            }
//            System.out.println("Listing retrieved .mf contents:");
//            for (Object key : attributes.keySet()) {
//                System.out.println(key.toString() + " : " + attributes.getValue(
//                        key.toString()));
//            }
            // finally: get the entries per defined entries mapping to get the
            // specified values; an exception will be thrown if any are missing:
            for (String key : keys) {
                String value = mf.getMainAttributes().getValue(key);
                if (value != null) {
                    mfEntries.put(key, value);
                } else {
                    throw new ClcException("Could not find manifest"
                            + " entry '" + key + "'");
                }
            }
        } catch (Exception ex) {
            throw new ClcException(ex.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    throw new ClcException(ex.getMessage());
                }
            }
        }
        return mfEntries;
    }
}
