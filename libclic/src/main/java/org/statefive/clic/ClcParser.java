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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.statefive.clic.valuetype.ValueTypeCreationException;
import org.statefive.clic.valuetype.StringType;
import org.statefive.clic.valuetype.ValueType;
import org.statefive.clic.valuetype.ValueTypeFactory;

/**
 * The CLC (command line configuration) parser takes an input stream and
 * generates the {@link GlobalConfiguration}, {@link OptionConfiguration} and
 * {@link ArgsConfiguration} objects obtained from the stream.
 *
 */
public class ClcParser {

    /**
     * Prefix to all user-defined options.
     */
    public static final String OPTION = "option";

    /**
     * Prefix to all standard arguments (those processed after all switches have
     * been processed).
     */
    public static final String ARGS = "args";

    /**
     * Defines the option value(s) for a given configuration - these will be the
     * long and/or short option for the configuration.
     */
    public static final String OPTS = "opts";

    /**
     * Defines the constant string used to define a description for a
     * configuration.
     */
    public static final String DESCRIPTION = "description";

    /**
     * Defines the constant string to define if a given configuration uses an
     * argument.
     */
    public static final String HAS_ARG = "hasArg";

    /**
     * Defines the constant for the configuration argument name, if the
     * configuration has an argument; this is the name that will be suffixed
     * (for example) when a user invokes help in order to print out the
     * different options.
     */
    public static final String ARG_NAME = "argName";

    /**
     * Used to define the last set of arguments as optional.
     */
    public static final String ARGS_OPTIONAL = "optional";

    /**
     * Defines the constant for providing the {@link ValueType} for a
     * configuration.
     */
    public static final String TYPE = "type";

    /**
     * Defines the constant for providing properties for the {@link ValueType}
     * for a configuration.
     */
    public static final String PROPERTIES = "properties";

    /**
     * Defines the constant for providing a default value for a configuration.
     */
    public static final String DEFAULT = "default";

    /**
     * Defines the constant for defining if command line arguments (not
     * options/switches) need to be processed.
     */
    public static final String IGNORE_CLI_ARGS = "ignoreCliArgs";

    /**
     * Length of prefix and standard argument definitions (if present).
     */
    public static final String FIX_LENGTH = "length";

    /**
     * True value.
     */
    public static final String TRUE = Boolean.TRUE.toString().toLowerCase();

    /**
     * False value.
     */
    public static final String FALSE = Boolean.FALSE.toString().toLowerCase();

    /**
     * All alphabetic character regular expression.
     */
    static final String A_Z = "a-zA-Z";

    /**
     * Number regular expression.
     */
    static final String ALPHA_NUM = A_Z + "0-9";

    /**
     * Regular expression to match short options - options containing just one
     * alphanumeric character.
     */
    static final String SHORT_OPTION_FORMAT = "([" + ALPHA_NUM + "])";

    /**
     * Regular expression to match long options - options beginning with an
     * alphanumeric character and containing any number of the same separated by
     * hyphens.
     */
    static final String LONG_OPTION_FORMAT = "([" + ALPHA_NUM + "]+[" + ALPHA_NUM + "\\-]+)";

    /**
     * Regular expression for lines matching the start of a
     * {@link OptionConfiguration} declaration (before the '=').
     */
    static final String OPTION_REGEX_PREFIX = "\\s*" + OPTION + "\\.([" + ALPHA_NUM
            + "\\-_]*)\\.([" + A_Z + "\\-_]*)";
    /**
     * Regular expression for lines matching the start of a
     * {@link ArgsConfiguration} declaration (before the '=').
     */
    static final String ARGS_REGEX_PREFIX = "\\s*" + ARGS + "\\.(["
            + ALPHA_NUM + "\\-_]*)\\.([" + A_Z + "\\-_]*)";

    /**
     * Regular expression for an entire line of an {@link OptionConfiguration}.
     */
    static final String OPTION_REGEX_BASIC_LINE = OPTION_REGEX_PREFIX
            + "\\s*=\\s*(.*)\\s*\\\\{0,1}";

    /**
     * Regular expression for an entire line of an {@link OptionConfiguration}.
     */
    static final String ARGS_REGEX_BASIC_LINE = ARGS_REGEX_PREFIX
            + "\\s*=\\s*(.*)\\s*\\\\{0,1}";

    /**
     * Regular expression for an escaped line.
     */
    static final String OPTION_REGEX_ESCAPED_LINE = "\\s+(.*)\\s*\\\\{0,1}";

    /**
     * Regular expression for a command.
     */
    static final String OPTION_COMMAND = "command\\.([" + ALPHA_NUM
            + "]+)\\s*=\\s*(.+)\\s*";

    /**
     * Name suffix to the main command string.
     */
    static final String COMMAND_NAME = "name";

    /**
     * Regular expression for a command name.
     */
    static final String COMMAND_NAME_REGEX = "[a-zA-Z]+[a-zA-Z0-9\\-]*";

    /**
     * Usage suffix to the main command string.
     */
    static final String COMMAND_USAGE = "usage";

    /**
     * The global configuration.
     */
    final GlobalConfiguration globalConfig = new GlobalConfiguration();

    /**
     * Parse a short, long, or short and long option together (separated by a
     * forward slash).
     *
     * @param options non-{@code null} options.
     *
     * @param currentLineNo current line number; may be {@code null} if the line
     * number is not available.
     *
     * @return non-{@code null} pair; if the short option is set, the left value
     * will be non-{@code null}, if the long option is set the right value will
     * be non-{@code null}.
     *
     * @throws ClcException if either options cannot be parsed; the line number
     * will be included if not {@code null}.
     */
    public static Pair<String, String> parseShortLongOptions(String options,
            Integer currentLineNo) throws ClcException {
        Pair<String, String> optionValues = null;
        String error = null;
        if (options.contains("/")) {
            // both, also accept spaces between the options and the slash:
            final String forwardSlash = "\\s*/\\s*";
            final Pattern p = Pattern.compile(SHORT_OPTION_FORMAT
                    + forwardSlash + LONG_OPTION_FORMAT);
            final Matcher m = p.matcher(options);
            if (m.matches()) {
                optionValues = new ImmutablePair<>(m.group(1), m.group(2));
            } else {
                error = "Invalid short and long option format; must be"
                        + " [character]/[text] but found " + options;
            }
        } else if (options.trim().length() == 1) {
            // short
            final Pattern p = Pattern.compile(SHORT_OPTION_FORMAT);
            final Matcher m = p.matcher(options);
            if (m.matches()) {
                optionValues = new ImmutablePair<>(m.group(1), null);
            } else {
                error = "Expected single character for short option but found "
                        + options;
            }
        } else {
            // long
            final Pattern p = Pattern.compile(LONG_OPTION_FORMAT);
            final Matcher m = p.matcher(options);
            if (m.matches()) {
                optionValues = new ImmutablePair<>(null, m.group(1));
            } else {
                error = "Expected text for long option but found " + options;
            }
        }
        if (error != null) {
            if (currentLineNo != null) {
                throw new ClcException(currentLineNo, error);
            } else {
                throw new ClcException(error);
            }
        }
        return optionValues;
    }

    /**
     * Parse the input stream and create the option configuration.
     *
     * @param is non-{@code null} input stream to read.
     *
     * @param encoding non-{@code null} encoding for the input stream.
     *
     * @return the option configuration read from the input stream.
     *
     * @throws ClcException if any of the configuration options are not defined
     * correctly, or there are no options defined.
     *
     * @throws IOException if the input stream could not be read.
     */
    public GlobalConfiguration parse(final InputStream is, final String encoding)
            throws ClcException, IOException {
        return parse(is, encoding, true);
    }

    /**
     * Parse the input stream and create the option configuration.
     *
     * @param is non-{@code null} input stream to read.
     *
     * @param encoding non-{@code null} encoding for the input stream.
     *
     * @param parseSubstitutions {@code true} to parse text substitutions (if
     * present); {@code false} to ignore.
     *
     * @return the option configuration read from the input stream.
     *
     * @throws ClcException if any of the configuration options are not defined
     * correctly, or there are no options defined.
     *
     * @throws IOException if the input stream could not be read.
     */
    public GlobalConfiguration parse(final InputStream is, final String encoding,
            boolean parseSubstitutions) throws ClcException, IOException {
        globalConfig.setParseSubstitutions(parseSubstitutions);
        final InputStreamReader isr = new InputStreamReader(is, encoding);
        final BufferedReader buf = new BufferedReader(isr);
        String line = null;
        StringBuilder builtLine = null;
        int currentLineNo = 0;
        boolean globalConfigParsed = false;
        boolean optionsParsed = false;
        boolean argsParsed = false;
        while ((line = buf.readLine()) != null) {
            currentLineNo++;
            if (line.trim().startsWith("#") || line.trim().isEmpty()) {
                continue;
            }
            boolean lineEscaped = false;
            if (builtLine != null && line.matches(OPTION_REGEX_ESCAPED_LINE)) {
                // it's not null so we must be building up the data; the
                // previous line must have been an escaped line (as could this one)
                // first, remove leading spaces/tabs:
                final String stripLeading = line.replaceAll("^\\s+", "");
                int lastIndex = stripLeading.length();
                if (stripLeading.trim().endsWith("\\")) {
                    // it's another escaped line - keep building it up:
                    lineEscaped = true;
                    lastIndex = stripLeading.lastIndexOf('\\');
                } else {
                    // the line is complete based on previous escaped lines
                }
                builtLine.append(stripLeading.substring(0, lastIndex));
                if (lineEscaped) {
                    continue;
                }
            } else if (builtLine != null) {
                // doesn't match our regular expression
                throw new ClcException(currentLineNo,
                        "Invalid escaped line: " + line);
            }
            if (builtLine == null) {
                // new line data to read
                builtLine = new StringBuilder();
                if (line.endsWith("\\")) {
                    // start of an escaped line, build it up from here:
                    builtLine.append(line.substring(0, line.lastIndexOf("\\")));
                    continue;
                } else {
                    // new line and it's complete and ready to go
                    builtLine.append(line);
                }
            }
            if (builtLine.toString().trim().matches(OPTION_COMMAND)) {
                // user-defined command; there cannot be any more global 
                // definitions from hereon in, and all defined options from this
                // point will be command-based options (including prefix and
                // standard arguments):
                globalConfigParsed = true;
                final Pattern p = Pattern.compile(OPTION_COMMAND);
                final Matcher m = p.matcher(builtLine);
                m.matches(); // don't need to test, matches (above) passed
                final String arg = m.group(1);
                final String value = m.group(2);
                if (COMMAND_NAME.equals(arg)) {
                    globalConfig.getCommandRoot().parseCommands(value);
                    // reset built line
                    builtLine = null;
                } else if (COMMAND_USAGE.equals(arg)) {
                    final Command command = globalConfig.getCommandRoot().getCurrentCommand();
                    command.setUsage(value);
                    // reset built line
                    builtLine = null;
                }
            } else if (builtLine.toString().trim().matches(
                    GlobalConfiguration.GLOBAL_OPTION_ASSIGNMENT_REGEX)) {
                // if this is /after/ any standard options, throw an exception
                // since all global options must come /before/ standard options
                if (globalConfigParsed) {
                    throw new ClcException(currentLineNo,
                            "Invalid global configuration definition; global"
                            + " configurations must come BEFORE standard"
                            + " \"option...\" definitions");
                }
                // it's a pattern of the form ABC_XYZ=[text], let the global
                // configuration deal with it:
                try {
                    globalConfig.updateGlobalConfiguration(builtLine.toString());
                    // global configuration updated, carry on
                    builtLine = null;
                } catch (ClcException ex) {
                    throw new ClcException(currentLineNo, ex.getMessage());
                }
            } else if (builtLine.toString().matches(OPTION_REGEX_BASIC_LINE)) {
                // it's a standard option.[name].* definition;
                // there shall be no more global definitions or prefix arguments
                // after this:
                globalConfigParsed = true;
                if (argsParsed) {
                    throw new ClcException(currentLineNo,
                            "Invalid option definition; option configurations"
                            + "  must come BEFORE standard"
                            + " \"args...\" definitions");
                }
                final Pattern p = Pattern.compile(ClcParser.OPTION_REGEX_BASIC_LINE);
                final Matcher m = p.matcher(builtLine);
                m.matches();
                // reset the line for next time, we're done
                final String name = m.group(1);
                final String subOption = m.group(2);
                final String value = m.group(3);
                updateCurrentOption(name, subOption, value, currentLineNo);
                builtLine = null;
            } else if (builtLine.toString().matches(ARGS_REGEX_BASIC_LINE)) {
                // it's an argument args.[name].* definition; no more global
                // definitions, prefix arguments or options after this:
                globalConfigParsed = true;
                optionsParsed = true;
                final Pattern p = Pattern.compile(ClcParser.ARGS_REGEX_BASIC_LINE);
                final Matcher m = p.matcher(builtLine);
                m.matches();
                // reset the line for next time, we're done
                final String name = m.group(1);
                final String subOption = m.group(2);
                final String value = m.group(3);
                updateCurrentArgs(name, subOption, value, currentLineNo);
                builtLine = null;
            } else {
                // doesn't match our regular expression
                throw new ClcException(currentLineNo, "Invalid option"
                        + " definition: " + line);
            }
        }
        checkAddDefaults();
        if (globalConfig.getOptionConfigurations().isEmpty()) {
            // there were no options in the file
            throw new ClcException("The configuration file contained no"
                    + " options to parse");
        }
        buf.close();
        isr.close();
        Map<String, OptionConfiguration> options = globalConfig.getGlobalOptionConfigurations();
        setDefaultValuesIfNotSet(options);
        checkOptionsValidForHasNoArg(options);
        for (Command command : globalConfig.getCommandRoot().getCommands()) {
            options = command.getOptionConfigurations();
            setDefaultValuesIfNotSet(options);
            checkOptionsValidForHasNoArg(options);
        }
        globalConfig.getCommandRoot().setCurrentCommand(null);
        return globalConfig;
    }

    /**
     * Check that if global help or version are set, if they are not defined in
     * the option configurations, add them in using defaults.
     */
    private void checkAddDefaults() {
        String globalHelpOptionName = globalConfig.getHelpOptionName();
        if (globalHelpOptionName != null) {
            if (globalConfig.getGlobalOptionConfigurations().get(globalHelpOptionName) == null) {
                globalConfig.addDefaultHelp();
            }
        }
        String globalVersionOptionName = globalConfig.getVersionOptionName();
        if (globalVersionOptionName != null) {
            if (globalConfig.getGlobalOptionConfigurations().get(globalVersionOptionName) == null) {
                globalConfig.addDefaultVersion();
            }
        }
    }

    /**
     * Set all options that have not explicitly set
     * {@link OptionConfiguration#hasArg()} have their option configuration set
     * to {code false}. If {@link OptionConfiguration#hasArg()} is set to
     * {@code true} and they have no value type set, it is set to
     * {@code java.lang.String}, and if it has a
     * {@link OptionConfiguration#getDefaultValue()} then the value will be
     * converted into the value type for the
     *
     * @param options non-{@code null} options to check.
     *
     * @throws ClcException if the default value is set but does not comply to
     * the option type conversion rules.
     */
    private void setDefaultValuesIfNotSet(
            final Map<String, OptionConfiguration> options) throws ClcException {
        for (OptionConfiguration optConfig : options.values()) {
            // any options that did not have their hasArg set will be null;
            // in which case set them to false
            if (optConfig.hasArg() == null) {
                optConfig.setHasArg(false);
            }
            // coerce value type to string if not set. This means the user did
            // not create any other value type, for example file, date or
            // integer etc.
            if (optConfig.hasArg()) {
                if (optConfig.getValueType() == null) {
                    optConfig.setValueType(ValueTypeFactory.getInstance().create(
                            StringType.STRING));
                }
                if (optConfig.getDefaultValue() != null) {
                    // if not set by the user will be a string:
                    String defaultValue = optConfig.getDefaultValue().toString();
                    try {
                        Object value = optConfig.getValueType().getValue(defaultValue);
                        // replace the default value with the type-converted value:
                        optConfig.setDefaultValue(value);
                    } catch (ValueTypeCreationException ex) {
                        throw new ClcException("Invalid default value '"
                                + defaultValue + "' for value type '"
                                + optConfig.getValueType().getValueTypeName() + "'");
                    }
                }
            }
        }
    }

    /**
     * Given the specified name, update the current option detected in the
     * configuration file and determine if this option is a new configuration,
     * an existing configuration that is still being built, or the next
     * configuration.
     *
     * @param name non-{@code null} name of the option currently being examined.
     *
     * @param subOption non-{@code null} the sub-option being parsed, for
     * example 'description', 'hasArg'.
     *
     * @param value non-{@code null} the value of the option being examined.
     *
     * @param currentLineNo current line number of the input stream being
     * parsed.
     *
     * @throws ClcException if there are any problems extracting the data.
     */
    private void updateCurrentOption(final String name, final String subOption,
            final String value, final int currentLineNo) throws ClcException {
        checkOptionNotRedefined(globalConfig.getOptionConfigurations(), name,
                currentLineNo);
        OptionConfiguration currentOption = globalConfig.getOptionConfigurations().get(name);
        if (currentOption == null) {
            // it's a new option configuration:
            currentOption = new OptionConfiguration();
            currentOption.setName(name);
            // add to root-level option configuration *or* currently processed cmd:
            globalConfig.addOptionConfiguration(currentOption);
            if (!OPTS.equals(subOption)) {
                // first definition for a new option must be OPTS:
                throw new ClcException(currentLineNo,
                        "First definition of a new option must be '"
                        + OPTS + "', found: '" + subOption + "'");
            }
            currentOption.setLineNumberStart(currentLineNo);
            currentOption.setLineNumberEnd(currentLineNo);
        } else {
            currentOption.setLineNumberEnd(currentLineNo);
        }
        switch (subOption) {
            case OPTS:
                OptionsTypeEnum optionsType = null;
                final String opt = value.trim();
                if (globalConfig.getOptionsType() == null) {
                    // No global definition, auto-detect it:
                    optionsType = inferOptionsType(opt);
                    globalConfig.setOptionsType(optionsType);
                }
                if ("".equals(opt)) {
                    String message = "Empty option value; must be a non-zero length"
                            + " string";
                    // zero characters or null
                    if (globalConfig.getOptionsType() != null) {
                        // already set, inform them of their decision
                        message += "; global configuration is defined as "
                                + globalConfig.getOptionsType().getType();
                    }
                    throw new ClcException(currentLineNo, message);
                } else {
                    optionsType = globalConfig.getOptionsType();

                }
                if (currentOption.getShortOption() != null
                        || currentOption.getLongOption() != null) {
                    throw new ClcException(currentLineNo, OPTS
                            + " has already been defined for option " + name);
                }
                checkCurrentOption(optionsType, currentOption, opt,
                        currentLineNo);
                break;
            case DESCRIPTION:
                if (currentOption.getDescription() != null) {
                    throw new ClcException(currentLineNo, DESCRIPTION
                            + " has already been defined for option " + name);
                }
                currentOption.setDescription(globalConfig.makeSubstitutions(
                        value));
                break;
            case HAS_ARG:
                if (currentOption.hasArg() != null) {
                    throw new ClcException(currentLineNo, HAS_ARG
                            + " has already been defined for option " + name);
                }
                currentOption.setHasArg(Boolean.parseBoolean(value));
                break;
            case IGNORE_CLI_ARGS:
                if (currentOption.isIgnoreCliArgs() != null) {
                    throw new ClcException(currentLineNo,
                            IGNORE_CLI_ARGS + " has already been defined for option "
                            + name);
                }
                currentOption.setIgnoreCliArgs(Boolean.parseBoolean(value));
                break;
            case DEFAULT:
                if (currentOption.getDefaultValue() != null) {
                    throw new ClcException(currentLineNo,
                            DEFAULT + " has already been defined for option "
                            + name);
                }
                currentOption.setDefaultValue(value);
                break;
            case ARG_NAME:
                if (currentOption.getArgName() != null) {
                    throw new ClcException(currentLineNo, ARG_NAME
                            + " has already been defined for option " + name);
                }
                currentOption.setArgName(value);
                break;
            case TYPE:
                if (currentOption.getType() != null) {
                    throw new ClcException(currentLineNo, TYPE
                            + " has already been defined for option " + name);
                }
                // type must be defined before properties and default value
                if (currentOption.getTypeProperties() != null) {
                    throw new ClcException(currentLineNo,
                            TYPE + " must be defined before " + PROPERTIES
                            + " value");
                }
                if (currentOption.getDefaultValue() != null) {
                    throw new ClcException(currentLineNo,
                            PROPERTIES + " must be defined before " + DEFAULT
                            + " value");
                }
                currentOption.setType(value);
                currentOption.setValueType(
                        ValueTypeFactory.getInstance().create(value));
                break;
            case PROPERTIES:
                if (currentOption.getTypeProperties() != null) {
                    throw new ClcException(currentLineNo,
                            PROPERTIES + " has already been defined for option "
                            + name);
                }
                // properties must be defined before default
                if (currentOption.getDefaultValue() != null) {
                    throw new ClcException(currentLineNo,
                            PROPERTIES + " must be defined before " + DEFAULT
                            + " value");
                }   // if no type defined, assume string type
                if (currentOption.getValueType() == null) {
                    currentOption.setValueType(
                            ValueTypeFactory.getInstance().create(StringType.STRING));
                }   // now parse the properties
                currentOption.getValueType().setProperties(value);
                break;
            default:
                throw new ClcException(currentLineNo,
                        "Unknown configuration option: " + subOption);
        }
    }

    /**
     * Update the current argument configuration with the given values. If the
     * configuration does not exist it will be added to the global configuration
     * (if a top-level, non-command) configuration), otherwise will be added to
     * the current command being processed.
     *
     * @param name non-{@code null} name of the configuration.
     *
     * @param subOption non-{@code null} sub-option key name.
     *
     * @param value non-{@code null} value of the configuration.
     *
     * @param currentLineNo current line number being examined.
     *
     * @throws ClcException if, for the current top-level non-command argument
     * configuration or the current command argument configuration:
     *
     * <p>
     * <ul>
     * <li>the configuration has a previously defined optional value for a
     * previous argument configuration (only the last argument definition can be
     * optional);</li>
     * <li>the configuration has a previously defined unbounded (i.e., length
     * not set) value for a previous argument configuration (only the last
     * argument definition can be unbounded);</li>
     * <li>the configuration is capped at zero but a previous argument
     * configuration is capped at zero (only the last definition can be capped
     * at zero);</li>
     * <li>If the definition is capped at zero and contains either a type,
     * argument name or optional value defined before or after the length
     * (definitions capped at zero can have none of the listed values);</li>
     * <li>If type, properties, length, argument name or optional value is
     * redefined for the same argument configuration;</li>
     * <li>a declared value type does not exist;</li>
     * <li>declared properties for a given value type are invalid;</li>
     * <li>if defined, the length is not a valid number or is less than zero;
     * or</li>
     * <li>the sub-option is not a recognised value</li>
     * </ul>
     */
    private void updateCurrentArgs(final String name, final String subOption, final String value,
            final int currentLineNo) throws ClcException {
        // check not redefined:
        checkArgNotRedefined(globalConfig.getCurrentArgsConfigurations(),
                name, currentLineNo);
        ArgsConfiguration currentArg = globalConfig.getCurrentArgsConfigurations().get(name);
        if (currentArg == null) {
            // it's a new args configuration, none for the new argument have
            // been defined yet, check that any previous entries (if present)
            // have not broken anything:
            currentArg = new ArgsConfiguration();
            currentArg.setName(name);
            currentArg.setLineNumberStart(currentLineNo);
            currentArg.setLineNumberEnd(currentLineNo);
            checkOptionalIsLast(globalConfig.getCurrentArgsConfigurations(),
                    currentLineNo);
            checkUnboundedIsLast(globalConfig.getCurrentArgsConfigurations(),
                    currentLineNo);
            checkCappedAtZeroIsLast(globalConfig.getCurrentArgsConfigurations(),
                    currentLineNo);
            globalConfig.addArgsConfiguration(currentArg);
        } else {
            currentArg.setLineNumberEnd(currentLineNo);
        }
        switch (subOption) {
            case TYPE:
                if (currentArg.getType() != null) {
                    throw new ClcException(currentLineNo, TYPE
                            + " has already been defined for argument " + name);
                }
                if (currentArg.isCappedAtZero()) {
                    throw new ClcException(currentLineNo,
                            "Current argument configuration length is zero;"
                            + " cannot define a type for such a configuration.");
                }
                currentArg.setType(value);
                try {
                    currentArg.setValueType(ValueTypeFactory.getInstance().create(
                            value));
                } catch (ValueTypeCreationException ex) {
                    throw new ClcException(currentLineNo, ex.getMessage());

                }
                break;
            case PROPERTIES:
                if (currentArg.getTypeProperties() != null) {
                    throw new ClcException(currentLineNo, PROPERTIES
                            + " have already been defined for argument " + name);
                }
                if (currentArg.getValueType() == null) {
                    throw new ClcException(currentLineNo,
                            "Cannot set properties on "
                            + currentArg.getName() + " as no value type"
                            + " has been set.");
                }
                // no need to check for capped-at-zero, will be dealt with by
                // type, above
                currentArg.setTypeProperties(value);
                // now parse the properties
                currentArg.getValueType().setProperties(value);
                break;
            case FIX_LENGTH:
                    try {
                int length = Integer.parseInt(value);
                currentArg.setLength(length);
            } catch (NumberFormatException | ClcException ex) {
                throw new ClcException(currentLineNo,
                        "Invalid argument fix length: " + value);
            }
            checkNoOtherConfigurations(currentArg, currentLineNo);
            break;
            case ARG_NAME:
                if (currentArg.getArgName() != null) {
                    throw new ClcException(currentLineNo, ARG_NAME
                            + " has already been defined for argument " + name);
                }
                if (currentArg.isCappedAtZero()) {
                    throw new ClcException(currentLineNo,
                            "Current argument configuration length is zero;"
                            + " cannot define argName for such a configuration.");
                }
                currentArg.setArgName(value);
                break;
            case ARGS_OPTIONAL:
                if (currentArg.getOptional() != null) {
                    throw new ClcException(currentLineNo, ARGS_OPTIONAL
                            + " has already been defined for argument " + name);
                }
                if (!value.equals(Boolean.TRUE.toString().toLowerCase())
                        && !value.equals(Boolean.FALSE.toString().toLowerCase())) {
                    throw new ClcException(currentLineNo,
                            "Invalid value for optional: Must be one of"
                            + " true or false, found " + value);
                }
                if (currentArg.isCappedAtZero()) {
                    throw new ClcException(currentLineNo,
                            "Current argument configuration length is zero;"
                            + " cannot define optional for such a configuration.");
                }
                currentArg.setOptional(Boolean.parseBoolean(value));
                break;
            default:
                throw new ClcException(currentLineNo,
                        "Unknown args configuration option: " + subOption);
        }
    }

    /**
     * Given the option, auto-detect the global options type, according to the
     * following rules:
     *
     * <p>
     * <ul>
     * <li>if the option contains a forward slash, the option type will be
     * {@link OptionsTypeEnum#BOTH};</li>
     * <li>if the option is greater than length 1, the option type will be
     * {@link OptionsTypeEnum#LONG};</li>
     * <li>if the option is a single character, the option type will be
     * {@link OptionsTypeEnum#SHORT};</li>
     * </ul>
     *
     * @param opt non-{@code null} options type.
     *
     * @return option type if it could be detedted according to the above rules;
     * {@code null} otherwise.
     */
    private OptionsTypeEnum inferOptionsType(String opt) {
        OptionsTypeEnum optionsType = null;
        if (opt.contains("/")) {
            optionsType = OptionsTypeEnum.BOTH;
        } else if (opt.length() == 1) {
            optionsType = OptionsTypeEnum.SHORT;
        } else if (opt.length() > 1) {
            optionsType = OptionsTypeEnum.LONG;
        }
        return optionsType;
    }

    /**
     * Check that an option that has already been defined is not re-defined
     * after another option (or options) have been defined - options must be
     * declared together.
     *
     * @param options non-{@code null} configuration options.
     *
     * @param name non-{@code null} name of the option currently being examined.
     *
     * @param currentLineNo current line number of the input stream being
     * parsed.
     *
     * @throws ClcException if the named option has already been defined but the
     * latest option is not the same.
     */
    private void checkOptionNotRedefined(
            final Map<String, OptionConfiguration> options, final String name,
            final int currentLineNo)
            throws ClcException {
        if (!options.isEmpty()) {
            // check to ensure we've not defined an option that has already
            // been defined, and is defined after another option later on:
            Object[] names = options.keySet().toArray();
            for (int i = 0; i < names.length - 1; i++) {
                if (name.equals(names[i])) {
                    throw new ClcException(currentLineNo,
                            "Bad configuration ordering; options must be"
                            + " grouped together. Option '" + name + "' has"
                            + " been defined prior to the declaration of option"
                            + " '" + name + "'");
                }
            }
        }
    }

    /**
     * Check all options that do not have arguments and ensure that they do not
     * have an argument name, type or properties set.
     *
     * @param options non-{@code null} configuration options to check.
     *
     * @throws ClcException if an option does not take an argument but has any
     * of the following set: argument name, type or properties.
     */
    private void checkOptionsValidForHasNoArg(
            final Map<String, OptionConfiguration> options) throws ClcException {
        StringBuilder errors = new StringBuilder();
        for (String name : options.keySet()) {
            OptionConfiguration optConfig = options.get(name);
            if (!optConfig.hasArg()) {
                if (optConfig.getArgName() != null) {
                    errors.append(generateNoArgError(optConfig, name,
                            ClcParser.ARG_NAME));
                }
                if (optConfig.getType() != null) {
                    errors.append(generateNoArgError(optConfig, name,
                            ClcParser.TYPE));
                }
                if (optConfig.getDefaultValue() != null) {
                    errors.append(generateNoArgError(optConfig, name,
                            ClcParser.DEFAULT));
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new ClcException(errors.toString());
        }
    }

    /**
     * Generate an error for the given configuration and option name detailing
     * that a no-argument option has been defined with an option type that is
     * only valid for options that require an argument.
     *
     * @param optConfig non-{@code null} option configuration.
     *
     * @param name non-{@code null} name of the option.
     *
     * @param badPropertyName non-{@code null} offending configuration property
     * name that is only applicable to argument-based option configurations.
     *
     * @return non-{@code null} error line containing details of the error.
     */
    private String generateNoArgError(OptionConfiguration optConfig, String name,
            String badPropertyName) {
        return "Option '" + name + "', lines "
                + optConfig.getLineNumberStart() + " - "
                + optConfig.getLineNumberEnd() + ", is defined as having no"
                + " argument (is a unary switch) but has the property '"
                + badPropertyName + "' set." + System.lineSeparator();
    }

    /**
     * Check that an argument that has already been defined is not re-defined
     * after the same argument have been defined - argument configuration
     * definitions must be declared together.
     *
     * @param args non-{@code null} configuration arguments.
     *
     * @param name non-{@code null} name of the prefix currently being examined.
     *
     * @param currentLineNo current line number of the input stream being
     * parsed.
     *
     * @throws ClcException if the named option has already been defined but the
     * latest option is not the same.
     */
    private void checkArgNotRedefined(
            final Map<String, ArgsConfiguration> args, final String name,
            final int currentLineNo)
            throws ClcException {
        if (!args.isEmpty()) {
            // check to ensure we've not defined an option that has already
            // been defined, and is defined after another option later on:
            Object[] names = args.keySet().toArray();
            for (int i = 0; i < names.length - 1; i++) {
                if (name.equals(names[i])) {
                    throw new ClcException(currentLineNo,
                            "Bad argument configuration ordering; arguments must"
                            + " be grouped together. Argument '" + name + "' has"
                            + " been defined prior to the declaration of"
                            + " '" + name + "'");
                }
            }
        }
    }

    /**
     * Check that there are no previous definitions of an optional argument
     * configuration, where the optional value is set as {@code true}; only the
     * last argument configuration may be optional.
     *
     * @param args non-{@code null} arguments to check; may be empty.
     *
     * @param currentLineNo ccurrent line number being examined.
     *
     * @throws ClcException if there is a previous argument configuration with a
     * value set to optional.
     */
    private void checkOptionalIsLast(
            final Map<String, ArgsConfiguration> args, final int currentLineNo)
            throws ClcException {
        // check to ensure we've not defined an option that has already
        // been defined, and is defined after another option later on:
        Object[] names = args.keySet().toArray();
        for (int i = 0; i < names.length; i++) {
            if (args.get(names[i]).isOptional()) {
                ArgsConfiguration argConfig = args.get(names[i]);
                throw new ClcException(currentLineNo,
                        "Redefinition of an argument configuration"
                        + " that is optional; only the last argument"
                        + " configuration can be optional (lines "
                        + argConfig.getLineNumberStart() + " - "
                        + argConfig.getLineNumberEnd() + ").");
            }
        }
    }

    /**
     * Check that there are no previous definitions of an argument configuration
     * where the previous configuration has an unbounded number of arguments
     * only the last argument configuration may have unbounded arguments.
     *
     * @param args non-{@code null} arguments to check; may be empty.
     *
     * @param currentLineNo current line number being examined.
     *
     * @throws ClcException if there is a previous argument configuration with a
     * value set to optional.
     */
    private void checkUnboundedIsLast(
            final Map<String, ArgsConfiguration> args, final int currentLineNo)
            throws ClcException {
        // check to ensure we've not defined an option that has already
        // been defined, and is defined after another option later on:
        Object[] names = args.keySet().toArray();
        for (int i = 0; i < names.length; i++) {
            ArgsConfiguration argsConfig = args.get(names[i]);
            if (args.get(names[i]).isUnbounded()) {
                ArgsConfiguration argConfig = args.get(names[i]);
                throw new ClcException(currentLineNo,
                        "Redefinition of an argument configuration"
                        + " that is unbounded, from lines "
                        + argConfig.getLineNumberStart() + " - "
                        + argConfig.getLineNumberEnd()
                        + "; only the last argument"
                        + " configuration can be unbounded. All previous"
                        + " definitions must have their 'length' set to a"
                        + " positive, non-zero value.");
            }
        }
    }

    /**
     * Check that there are no previous definitions of an argument configuration
     * where the previous configuration is capped at zero; only the last
     * argument configuration can be capped at zero.
     *
     * @param args non-{@code null} arguments to check; may be empty.
     *
     * @param currentLineNo current line number being examined.
     *
     * @throws ClcException if there is a previous argument configuration with a
     * value set to optional.
     */
    private void checkCappedAtZeroIsLast(
            final Map<String, ArgsConfiguration> args, final int currentLineNo)
            throws ClcException {
        // check to ensure we've not defined an option that has already
        // been defined, and is defined after another option later on:
        Object[] names = args.keySet().toArray();
        for (int i = 0; i < names.length; i++) {
            if (args.get(names[i]).getLength() != null
                    && args.get(names[i]).getLength() == 0) {
                ArgsConfiguration argConfig = args.get(names[i]);
                throw new ClcException(currentLineNo,
                        "Redefinition of an argument configuration"
                        + " that is capped at zero (lines "
                        + argConfig.getLineNumberStart() + " - "
                        + argConfig.getLineNumberEnd()
                        + "); only the last argument"
                        + " configuration can be capped at zero.");
            }
        }
    }

    /**
     * Check that for the given argument configuration that type, optional and
     * argument name are not defined if the configuration length is zero.
     *
     * @param argsConfig non-{@code null} argument configuration to check.
     *
     * @param currentLineNo current line number being evaluated.
     *
     * @throws ClcException if the argument length is zero and one of type,
     * optional or argument name are defined.
     */
    private void checkNoOtherConfigurations(ArgsConfiguration argsConfig,
            final int currentLineNo) throws ClcException {
        if (argsConfig.getLength() != null && argsConfig.getLength() == 0) {
            if (argsConfig.getType() != null) {
                throw new ClcException(currentLineNo,
                        "Type cannot be set when configuration length is zero.");
            } else if (argsConfig.isOptional()) {
                throw new ClcException(currentLineNo, "Optional"
                        + " cannot be set when configuration length is zero.");
            } else if (argsConfig.getArgName() != null) {
                throw new ClcException(currentLineNo, "Argument"
                        + " name cannot be set when configuration length is zero.");

            }
        }
    }

    /**
     * Check that the option specified is of the correct type - short, long or
     * both - according to what has been defined in the global configuration (if
     * it has been defined), setting it if it hasn't and this is the first
     * option in the configuration and the option conforms to the expected type.
     *
     * @param expectedType non-{@code null} expected type.
     *
     * @param currentOption non-{@code null} current option being parsed that
     * does not yet have the specified option set on it yet.
     *
     * @param option non-{@code null} option.
     *
     * @param currentLineNo current line number of where the data is encountered
     * in the input stream.
     *
     * @throws ClcException if the specified option does not match the global
     * configuration's option type or the option formatting is incorrect (such
     * as specifying a short option when the option type is long option).
     */
    private void checkCurrentOption(final OptionsTypeEnum expectedType,
            final OptionConfiguration currentOption, final String option,
            final int currentLineNo) throws ClcException {
        final OptionsTypeEnum globalOptType = globalConfig.getOptionsType();
        if (globalOptType != null && !(globalOptType.equals(expectedType))) {
            throw new ClcException(currentLineNo,
                    "Configuration type specifies "
                    + GlobalConfiguration.GLOBAL_OPTIONS_OPTS_TYPE + " as "
                    + globalOptType.getType() + " but found "
                    + expectedType);
        }
        if (globalOptType == null) {
            globalConfig.setOptionsType(expectedType);
        }
        if (null == globalConfig.getOptionsType()) {
            currentOption.setShortOption(null);
            currentOption.setLongOption(
                    parseShortLongOptions(option, currentLineNo)
                            .getRight());
        } else {
            switch (globalConfig.getOptionsType()) {
                case BOTH: {
                    Pair<String, String> opts = parseShortLongOptions(option,
                            currentLineNo);
                    currentOption.setShortOption(opts.getLeft());
                    currentOption.setLongOption(opts.getRight());
                    break;
                }
                case SHORT: {
                    currentOption.setLongOption(null);
                    currentOption.setShortOption(
                            parseShortLongOptions(option,
                                    currentLineNo).getLeft());
                    break;
                }
                default: {
                    currentOption.setShortOption(null);
                    currentOption.setLongOption(
                            parseShortLongOptions(option,
                                    currentLineNo).getRight());
                    break;
                }
            }
        }
    }
}
