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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.help.TextHelpAppendable;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.statefive.clic.valuetype.BigDecimalType;
import org.statefive.clic.valuetype.BigIntegerType;
import org.statefive.clic.valuetype.BooleanType;
import org.statefive.clic.valuetype.ByteType;
import org.statefive.clic.valuetype.CharacterType;
import org.statefive.clic.valuetype.ValueTypeCreationException;
import org.statefive.clic.valuetype.DataFileType;
import org.statefive.clic.valuetype.DateType;
import org.statefive.clic.valuetype.DirType;
import org.statefive.clic.valuetype.DirUpdateListener;
import org.statefive.clic.valuetype.DoubleType;
import org.statefive.clic.valuetype.FileType;
import org.statefive.clic.valuetype.FloatingPointType;
import org.statefive.clic.valuetype.IntegralType;
import org.statefive.clic.valuetype.ListType;
import org.statefive.clic.valuetype.LongType;
import org.statefive.clic.valuetype.ShortType;
import org.statefive.clic.valuetype.StringType;
import org.statefive.clic.valuetype.ValueType;
import org.statefive.clic.valuetype.ValueTypeBuilder;
import org.statefive.clic.valuetype.ValueTypeFactory;

/**
 * Main entry point to the configuration library.
 *
 * <p>
 * Combines processing a command line configuration file and passes the options
 * to the command line parser. Listeners should register themselves with this
 * instance in order to be notified of options as they are encountered.
 *
 * <p>
 * Configuration comes in several forms, and must be defined in the correct
 * order:
 *
 * <p>
 * <ol>
 * <li><strong>Global configuration</strong> defines whether to use short, long
 * or short and long options together, as well as shortcuts to creating the most
 * well-used command line options with minimal coding - {@code help} and
 * {@code version} options;</li>
 * <li><strong>Prefix arguments</strong> (optional) are arguments that are
 * processed prior to any command line arguments are processed;</li>
 * <li><strong>options</strong> are converted into application level command
 * line switches;</li>
 * <li><strong>arguments</strong> represent the remaining data after all
 * switches have been processed; and</li>
 * <li><strong>commands</strong> are processed prior to parsing any switches and
 * arguments and can in turn take their own option and argument configurations.
 * Commands can also be nested with sub-commands to any number of nested
 * commands. Commands may not contain their own prefix arguments. Once a command
 * is defined any number of option and argument configurations may be declared.
 * If a command is detected all options and arguments will be passed to that
 * command.</li>
 * </ol>
 */
public class Clc implements ArgsListener, CommandArgsListener {

    /**
     * Singleton instance.
     */
    private static Clc instance;

    /**
     * Environmental variable to read if determining the maximum help output
     * width from the environment.
     */
    static final String ENV_MAX_COLUMNS = "COLUMNS";

    /**
     * CLC parser.
     */
    private final ClcParser clcParser = new ClcParser();

    /**
     * Listeners to be notified of updates.
     */
    private final List<OptionListener> optionListeners = new ArrayList<>();

    /**
     * Command option listeners to be notified of updates.
     */
    private final List<CommandOptionListener> commandOptionListeners = new ArrayList<>();

    /**
     * Argument listeners to be notified of updates.
     */
    private final List<ArgsListener> argsListeners = new ArrayList<>();

    /**
     * Command argument listeners to be notified of updates.
     */
    private final List<CommandArgsListener> commandArgsListeners = new ArrayList<>();

    /**
     * List of arguments remaining (if present); these are the 'raw' command
     * line arguments as strings; callers wanting the
     * {@link ArgsConfiguration}-based values should register as an
     * {@link ArgsListener} to receive updates on parsed values.
     */
    private final List<String> args = new ArrayList<>();

    /**
     * List of arguments as value types.
     */
    private final List<Object> argsValueTypes = new ArrayList<>();

    /**
     * List of prefix arguments.
     */
    private final List<String> prefixArgs = new ArrayList<>();

    /**
     * List of prefix argument value types.
     */
    private final List<Object> prefixArgsValueTypes = new ArrayList<>();

    /**
     * Directory listeners to be notified of updates.
     */
    private final List<DirUpdateListener> dirListeners = new ArrayList<>();

    /**
     *
     */
    private final Map<String, DirType> dirTypes = new HashMap<>();

    /**
     * Map of listener ID to directory blacklists.
     */
    private final Map<String, Set<File>> listenerDirBlackList = new HashMap<>();

    /**
     * Global configuration parsed by this CLI configuration.
     */
    private GlobalConfiguration globalConfig;

    /**
     * Options parsed from the command line parser.
     */
    private List<Option> parsedOptions;

    /**
     * Options built up from converting {@link OptionConfiguration}s.
     */
    private Options options;

    /**
     * Determine if to parse CLI arguments - some commands, such as the
     * ubiquitous help and version switches, do not normally process arguments.
     */
    private boolean parseArgs = true;

    /**
     * Get the command line configuration instance.
     *
     * @return non-{@code null} CLC instance.
     */
    public static Clc getInstance() {
        if (instance == null) {
            instance = new Clc();
            instance.addArgsListener(instance);
            instance.addCommandArgsListener(instance);
        }
        return instance;
    }

    /**
     * Initialise all the main types used for data-type conversion from CLI
     * string data to appropriate types.
     */
    public static void initialiseValueTypeFactory() {
        ValueTypeFactory instance = ValueTypeFactory.getInstance();
        if (!instance.isRegistered(BigDecimalType.BIG_DECIMAL)) {
            ValueTypeBuilder<BigDecimalType> genericBigDecimalType
                    = new ValueTypeBuilder<>(BigDecimalType.class);
            ValueTypeFactory.getInstance().registerValueTypeBuilder(
                    BigDecimalType.BIG_DECIMAL, genericBigDecimalType);
        }

        if (!instance.isRegistered(BigIntegerType.BIG_INTEGER)) {
            ValueTypeBuilder<BigIntegerType> genericBigIntegerType
                    = new ValueTypeBuilder<>(BigIntegerType.class);
            ValueTypeFactory.getInstance().registerValueTypeBuilder(
                    BigIntegerType.BIG_INTEGER, genericBigIntegerType);
        }

        if (!instance.isRegistered(BooleanType.BOOLEAN)) {
            ValueTypeBuilder<BooleanType> genericBooleanType
                    = new ValueTypeBuilder<>(BooleanType.class);
            ValueTypeFactory.getInstance().registerValueTypeBuilder(
                    BooleanType.BOOLEAN, genericBooleanType);
        }

        if (!instance.isRegistered(ByteType.BYTE)) {
            ValueTypeBuilder<ByteType> genericByteType
                    = new ValueTypeBuilder<>(ByteType.class);
            ValueTypeFactory.getInstance().registerValueTypeBuilder(
                    ByteType.BYTE, genericByteType);
        }

        if (!instance.isRegistered(CharacterType.CHARACTER)) {
            ValueTypeBuilder<CharacterType> genericCharType
                    = new ValueTypeBuilder<>(CharacterType.class);
            ValueTypeFactory.getInstance().registerValueTypeBuilder(
                    CharacterType.CHARACTER, genericCharType);
        }

        if (!instance.isRegistered(DateType.DATE)) {
            ValueTypeBuilder<DateType> genericDateType
                    = new ValueTypeBuilder<>(DateType.class);
            ValueTypeFactory.getInstance().registerValueTypeBuilder(
                    DateType.DATE, genericDateType);
        }

        if (!instance.isRegistered(DataFileType.DATA_FILE)) {
            ValueTypeBuilder<DataFileType> genericDataFileType
                    = new ValueTypeBuilder<>(DataFileType.class);
            ValueTypeFactory.getInstance().registerValueTypeBuilder(
                    DataFileType.DATA_FILE, genericDataFileType);
        }

        if (!instance.isRegistered(DoubleType.DOUBLE)) {
            ValueTypeBuilder<DoubleType> genericDoubleType
                    = new ValueTypeBuilder<>(DoubleType.class);
            ValueTypeFactory.getInstance().registerValueTypeBuilder(
                    DoubleType.DOUBLE, genericDoubleType);
        }

        if (!instance.isRegistered(FileType.FILE)) {
            ValueTypeBuilder<FileType> genericFileType
                    = new ValueTypeBuilder<>(FileType.class);
            ValueTypeFactory.getInstance().registerValueTypeBuilder(
                    FileType.FILE, genericFileType);
        }

        if (!instance.isRegistered(DirType.DIRECTORY)) {
            ValueTypeBuilder<DirType> genericDirType
                    = new ValueTypeBuilder<>(DirType.class);
            ValueTypeFactory.getInstance().registerValueTypeBuilder(
                    DirType.DIRECTORY, genericDirType);
        }

        if (!instance.isRegistered(FloatingPointType.FLOAT)) {
            ValueTypeBuilder<FloatingPointType> genericFloatingPointType
                    = new ValueTypeBuilder<>(FloatingPointType.class);
            ValueTypeFactory.getInstance().registerValueTypeBuilder(
                    FloatingPointType.FLOAT, genericFloatingPointType);
        }

        if (!instance.isRegistered(IntegralType.INTEGRAL)) {
            ValueTypeBuilder<IntegralType> genericIntegralType
                    = new ValueTypeBuilder<>(IntegralType.class);
            ValueTypeFactory.getInstance().registerValueTypeBuilder(
                    IntegralType.INTEGRAL, genericIntegralType);
        }

        if (!instance.isRegistered(ListType.LIST)) {
            ValueTypeBuilder<ListType> genericListType
                    = new ValueTypeBuilder<>(ListType.class);
            ValueTypeFactory.getInstance().registerValueTypeBuilder(ListType.LIST,
                    genericListType);
        }

        if (!instance.isRegistered(LongType.LONG)) {
            ValueTypeBuilder<LongType> genericLongType
                    = new ValueTypeBuilder<>(LongType.class);
            ValueTypeFactory.getInstance().registerValueTypeBuilder(LongType.LONG,
                    genericLongType);
        }

        if (!instance.isRegistered(ShortType.SHORT)) {
            ValueTypeBuilder<ShortType> genericShortType
                    = new ValueTypeBuilder<>(ShortType.class);
            ValueTypeFactory.getInstance().registerValueTypeBuilder(ShortType.SHORT,
                    genericShortType);
        }

        if (!instance.isRegistered(StringType.STRING)) {
            ValueTypeBuilder<StringType> genericStringType
                    = new ValueTypeBuilder<>(StringType.class);
            ValueTypeFactory.getInstance().registerValueTypeBuilder(
                    StringType.STRING, genericStringType);
        }
    }

    /**
     * Singleton instance.
     */
    private Clc() {
        // nothing to do.
    }

    /**
     * Used to update the argument value types.
     *
     * @param name unused.
     *
     * @param index unused.
     *
     * @param value non-{@code null} argument.
     */
    @Override
    public void argument(String name, int index, Object value) {
        argsValueTypes.add(value);
    }

    /**
     * Used to update the argument value types.
     *
     * @param command unused.
     *
     * @param name unused.
     *
     * @param index unused.
     *
     * @param value non-{@code null} argument.
     */
    @Override
    public void argument(String command, String name, int index, Object value) {
        argsValueTypes.add(value);
    }

    /**
     * Process the given file against the specified arguments, failing if the
     * MD5 sum is not {@code null} and does not match the file's MD5 sum.
     *
     * @param configFile non-{@code null} existing file with a valid CLI
     * configuration.
     *
     * @param md5sum MD5 sum to check; if {@code null}, no check will be made.
     *
     * @param args non-{@code null} arguments to parse against.
     *
     * @throws ClcException if the MD5 check fails, the file doesn't exist or is
     * not a file, the MD5 algorithm is not available via the VM, or there is a
     * problem parsing the file.
     *
     * @throws IOException if there is a problem processing the file.
     */
    public void process(final File configFile, final String md5sum,
            final String[] args) throws ClcException, IOException {
        if (!configFile.exists() || !configFile.isFile()) {
            throw new IOException("Not a file: " + configFile.getName());
        }
        if (md5sum != null) {
            checkMd5(md5sum, configFile);
        }
        FileInputStream fis = new FileInputStream(configFile);
        process(fis, args);
        fis.close();
    }

    /**
     * Take the given input stream and arguments and process them using the
     * default encoding {@code UTF-8}, informing all registered listeners of any
     * options that are parsed along with their values. Note that if both short
     * and long options are used, listeners will be notified twice for each
     * option; it is up to listeners to cater for this to their needs.
     *
     * @param is non-{@code null} readable input stream of a command line
     * configuration to parse.
     *
     * @param args non-{@code null} arguments, typically supplied via a call
     * from a command line process.
     *
     * @throws ClcException if the configuration file contains any invalid
     * definitions.
     *
     * @throws IOException if there is a problem reading the input stream.
     */
    public void process(final InputStream is, final String[] args)
            throws ClcException, IOException {
        process(is, "UTF-8", args);
    }

    /**
     * Take the given input stream and arguments and process them, informing all
     * registered listeners of any options that are parsed along with their
     * values. Note that if both short and long options are used, listeners will
     * be notified twice for each option; it is up to listeners to cater for
     * this to their needs.
     *
     * @param is non-{@code null} readable input stream of a command line
     * configuration to parse.
     *
     * @param encoding non-{@code null} encoding for the input stream.
     *
     * @param args non-{@code null} arguments, typically supplied via a call
     * from a command line process.
     *
     * @throws ClcException if the configuration file contains any invalid
     * definitions.
     *
     * @throws IOException if there is a problem reading the input stream.
     */
    public void process(final InputStream is, final String encoding,
            final String[] args) throws ClcException, IOException {
        process(is, encoding, args, true);
    }

    /**
     * Take the given input stream and arguments and process them, informing all
     * registered listeners of any options that are parsed along with their
     * values. Note that if both short and long options are used, listeners will
     * be notified twice for each option; it is up to listeners to cater for
     * this to their needs.
     *
     * @param is non-{@code null} readable input stream of a command line
     * configuration to parse.
     *
     * @param encoding non-{@code null} encoding for the input stream.
     *
     * @param args non-{@code null} arguments, typically supplied via a call
     * from a command line process.
     *
     * @param parseSubstitutions {@code true} to parse text substitutions (if
     * present); {@code false} to ignore.
     *
     * @throws ClcException if the configuration file contains any invalid
     * definitions.
     *
     * @throws IOException if there is a problem reading the input stream.
     */
    public void process(final InputStream is, final String encoding,
            final String[] args, boolean parseSubstitutions)
            throws ClcException, IOException {
        initialiseValueTypeFactory();
        globalConfig = clcParser.parse(is, encoding, parseSubstitutions);
        // what's returned once all prefix arguments and CLI switches have been
        // processed:
        List<String> finalArgs = null;
        try {
            // create copy of arguments to work with:
            String[] argsRemaining = new String[args.length];
            System.arraycopy(args, 0, argsRemaining, 0, args.length);
            boolean notCliOption = false;
            if (argsRemaining != null && argsRemaining.length > 0 && !argsRemaining[0].startsWith("-")) {
                // could be a command - otherwise, if no commands defined for
                // this configuration, will be an argument:
                notCliOption = true;
            }
            CommandRoot commandRoot = globalConfig.getCommandRoot();
            if (!notCliOption || commandRoot.getRoot().getChildren().isEmpty()) {
                // remaining arguments will be standard arguments:
                finalArgs = processRootLevelOptions(argsRemaining);
            } else {
                // expecting the argument succeeding it to be a command:
                finalArgs = processCommandLevelOptions(argsRemaining);
            }
            if (!finalArgs.isEmpty()) {
                this.args.addAll(finalArgs);
            }
            // now process all arguments that are left over (if there are any):
            boolean hasArgsConfig = globalConfig.hasArgsConfiguration();
            if (commandRoot.getCurrentCommand() != null) {
                hasArgsConfig = !commandRoot.getCurrentCommand().getArgsConfigurations().isEmpty();
            }
            if (parseArgs) {
                if (hasArgsConfig) {
                    processArgs(finalArgs);
                } else if (finalArgs.size() > 0) {
                    // no defined configuration but arguments remain; give defaults:
                    processDefaultArgs(finalArgs);
                }
            }
        } catch (ParseException ex) {
            throw new ClcException(ex.getMessage(), ex);
        }
        // finally, update any listeners if there are directories to traverse/scan:
        for (DirType dirType : dirTypes.values()) {
            updateListeners(dirType.getDirectory(), dirType);
        }
    }

    /**
     * Used if the caller wants to define their own help configuration or have
     * access to the underlying options.
     *
     * @return the non-{@code null} options.
     */
    public List<Option> getOptions() {
        return parsedOptions;
    }

    /**
     * Get the string values of command line arguments once all switches have
     * been processed.
     *
     * @return non-{@code null} list of command line arguments; may be empty.
     */
    public List<String> getArgs() {
        return args;
    }

    /**
     * Get the object values of command line arguments once all switches have
     * been processed.
     *
     * @return non-{@code null} list of command line arguments; may be empty.
     */
    public List<Object> getArgsValueTypes() {
        return argsValueTypes;
    }

    /**
     * Help will be printed before any other options are process; callers must
     * decide what to do when the {@link OptionListener} is updated.
     *
     * <p>
     * Underneath the hood, the {@link HelpFormatter} is used to print help -
     * for the help to contain informative information, see
     * {@link GlobalConfiguration} on how global options can be set to inform
     * the user of values to define in order to create a valid help listing.
     */
    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        String header = globalConfig.getHelpCommandHeader();
        PrintWriter pw = new PrintWriter(System.out);
        formatter.printHelp(pw, globalConfig.getHelpWidth(),
                globalConfig.getHelpCommandName(),
                StringEscapeUtils.unescapeJava(header), options,
                globalConfig.getHelpLeftPad(), 2,
                globalConfig.getHelpCommandFooter(), globalConfig.isHelpAutoUsage());
        pw.flush();
        // print help, then quit
//        TextHelpAppendable textHelpAppendable = getTextHelpAppendible();
//        HelpFormatter formatter = HelpFormatter.builder()
//                .setHelpAppendable(textHelpAppendable)
//                .setShowSince(false).get();
//        String header = globalConfig.getHelpCommandHeader();
//        try {
//            if (globalConfig.isHelpSortOptions()) {
//                formatter.printHelp(globalConfig.getHelpCommandName(),
//                        StringEscapeUtils.unescapeJava(header),
//                        formatter.sort(options), globalConfig.getHelpCommandFooter(),
//                        globalConfig.isHelpAutoUsage());
//            } else {
//                formatter.printHelp(globalConfig.getHelpCommandName(),
//                        StringEscapeUtils.unescapeJava(header),
//                        options, globalConfig.getHelpCommandFooter(),
//                        globalConfig.isHelpAutoUsage());
//            }
//        } catch (IOException ex) {
//            throw new IllegalArgumentException("Could not create help output: "
//                    + ex.getMessage());
//        }
    }

    /**
     * Version information will be printed before any other options are process;
     * callers must decide what to do when the {@link OptionListener} is updated
     * (a common strategy is to simply call {@code System.exit(0)}).
     */
    public void printVersion() {
        System.out.println(globalConfig.getVersionOptionText());
    }

    /**
     * Add the specified listener.
     *
     * @param listener non-{@code null} listener to add.
     *
     * @return {@code true} if the listener was added, {@code false} otherwise.
     */
    public boolean addOptionListener(final OptionListener listener) {
        return optionListeners.add(listener);
    }

    /**
     * Add the specified command listener.
     *
     * @param listener non-{@code null} listener to add.
     *
     * @return {@code true} if the listener was added, {@code false} otherwise.
     */
    public boolean addCommandOptionListener(final CommandOptionListener listener) {
        return commandOptionListeners.add(listener);
    }

    /**
     * Add the specified argument listener.
     *
     * @param listener non-{@code null} listener to add.
     *
     * @return {@code true} if the listener was added; {@code false} otherwise.
     */
    public boolean addArgsListener(final ArgsListener listener) {
        return argsListeners.add(listener);
    }

    /**
     * Add the specified command argument listener.
     *
     * @param listener non-{@code null} listener to add.
     *
     * @return {@code true} if the listener was added; {@code false} otherwise.
     */
    public boolean addCommandArgsListener(final CommandArgsListener listener) {
        return commandArgsListeners.add(listener);
    }

    /**
     * Add the specified directory update listener.
     *
     * @param listener non-{@code null} listener to add.
     *
     * @return {@code true} if the listener was added, {@code false} otherwise.
     */
    public boolean addDirUpdateListener(final DirUpdateListener listener) {
        return dirListeners.add(listener);
    }

    /**
     * Remove the specified directory update listener.
     *
     * @param listener non-{@code null} listener to remove.
     *
     * @return {@code true} if the listener was removed, {@code false}
     * otherwise.
     */
    public boolean removeDirUpdateListener(final DirUpdateListener listener) {
        return dirListeners.remove(listener);
    }

    /**
     * Callers that use (for example) threads can call this to force the command
     * line configuration to update its directory listeners.
     */
    public void updateDirectoryListeners() {
        for (DirType dirType : dirTypes.values()) {
            updateListeners(dirType.getDirectory(), dirType);
        }
    }

    /**
     * Set whether to parse CLI arguments.
     *
     * @param parseArgs {@code true} to parse CLI arguments, {@code false} to
     * ignore parsing CLI arguments.
     */
    public void setParseArgs(boolean parseArgs) {
        this.parseArgs = parseArgs;
    }

    /**
     * Determine if arguments should be parsed; if any options are invoked
     * (including command options) that have a CLC option entry defined as
     * {@code ignoreCliArgs}, then argument parsing will not be considered.
     *
     * @return {@code true} if arguments should be parsed; {@code false}
     * otherwise.
     */
    public boolean isParseArgs() {
        return this.parseArgs;
    }

    /**
     * Process the given arguments, updating listeners if any are registered.
     *
     * @param args non-{@code null} arguments to process; may be empty.
     *
     * @throws ClcException if any of the arguments are of an incorrect type,
     * there are not enough arguments, or the number of arguments exceeds the
     * total allowed number of arguments.
     */
    private void processArgs(List<String> args) throws ClcException {
        int argsProcessed = 0;
        boolean fixedLength = true;
        CommandRoot commandRoot = globalConfig.getCommandRoot();
        Map<String, ArgsConfiguration> argsConfigs = null;
        if (commandRoot.getCurrentCommand() == null) {
            argsConfigs = globalConfig.getArgsConfigurations();
        } else {
            argsConfigs = commandRoot.getCurrentCommand().getArgsConfigurations();
        }
        for (String key : argsConfigs.keySet()) {
            ArgsConfiguration argsConfig = globalConfig.getCurrentArgsConfigurations().get(key);
            if (argsConfig.getLength() != null) {
                // fixed length argument list:
                for (int i = 0; i < argsConfig.getLength(); i++) {
                    ValueType valueType = argsConfig.getValueType();
                    if (valueType == null) {
                        argsConfig.setValueType(new StringType());
                    }
                    try {
                        if (commandRoot.getCurrentCommand() != null) {
                            for (CommandArgsListener listener : commandArgsListeners) {
                                updateListener(listener, argsConfig, argsProcessed,
                                        args.get(argsProcessed));
                            }
                        } else {
                            for (ArgsListener listener : argsListeners) {
                                updateListener(listener, argsConfig, argsProcessed,
                                        args.get(argsProcessed));
                            }
                        }
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ClcException(
                                "Error processing argument index " + argsProcessed
                                + "; expected at least "
                                + globalConfig.getArgsMinLength()
                                + " arguments.");
                    }
                    argsProcessed++;
                }
            } else {
                // amy number of arguments
                fixedLength = false;
                for (int i = argsProcessed; i < args.size(); i++) {
                    ValueType valueType = argsConfig.getValueType();
                    if (valueType == null) {
                        argsConfig.setValueType(new StringType());
                    }
                    if (commandRoot.getCurrentCommand() != null) {
                        for (CommandArgsListener listener : commandArgsListeners) {
                            updateListener(listener, argsConfig, argsProcessed,
                                    args.get(argsProcessed));
                        }
                    } else {
                        for (ArgsListener listener : argsListeners) {
                            updateListener(listener, argsConfig, argsProcessed,
                                    args.get(argsProcessed));
                        }
                    }
                    argsProcessed++;
                }
            }
        }
        if (fixedLength) {
            if (commandRoot.getCurrentCommand() == null) {
                if (globalConfig.getArgsMinLength() > args.size()) {
                    throw new ClcException("Error: Processed " + args.size()
                            + " arguments, expected at least " + argsProcessed + ".");
                } else if (globalConfig.getArgsMaxLength() < args.size()) {
                    throw new ClcException("Error: Configuration accepts"
                            + " no more than " + globalConfig.getArgsMaxLength()
                            + " but got " + args.size() + " arguments.");
                }
            } else {
                Command command = commandRoot.getCurrentCommand();
                if (globalConfig.getArgsMinLength(command) > args.size()) {
                    throw new ClcException("Error: Processed " + args.size()
                            + " arguments for command '" + command.getName()
                            + "', expected " + argsProcessed + ".");
                } else if (globalConfig.getArgsMaxLength(command) < args.size()) {
                    throw new ClcException("Error: Configuration  for command '"
                            + command.getName() + "' accepts"
                            + " no more than " + globalConfig.getArgsMaxLength(command)
                            + " but got " + args.size() + " arguments.");
                }

            }
        }
    }

    /**
     * Process the given arguments as default arguments - used when no argument
     * configurations have been defined and arguments will all be processed as
     * strings.
     *
     * @param args non-{@code null} arguments to process; may be empty.
     *
     * @throws ClcException if any of the arguments are of an incorrect type,
     * there are not enough arguments, or the number of arguments exceeds the
     * total allowed number of arguments.
     */
    private void processDefaultArgs(List<String> args) throws ClcException {
        int currentPos = 0;
        for (String arg : args) {
            for (int k = 0; k < argsListeners.size(); k++) {
                argsListeners.get(k).argument(arg, currentPos, arg);
            }
            currentPos++;
        }
    }

    /**
     * Check that the given MD5 sum matches the MD5 sum of the contents of the
     * file. If the MD5 sums are equal, the method returns without error.
     *
     * @param md5sum non-{@code null} MD5 to test against.
     *
     * @param file non-{@code null} file to check.
     *
     * @throws ClcException
     */
    private void checkMd5(final String md5sum, final File file)
            throws ClcException {
        MessageDigest messageDigest;
        try {
            FileInputStream is = new FileInputStream(file);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buffer = new byte[0xFFFF];
            for (int len = is.read(buffer); len != -1; len = is.read(buffer)) {
                os.write(buffer, 0, len);
            }
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(os.toString("UTF-8").getBytes());
            byte[] digestData = messageDigest.digest();

            BigInteger bigInt = new BigInteger(1, digestData);
            String hashtext = bigInt.toString(16);
            os.close();
            is.close();
            if (!hashtext.equalsIgnoreCase(md5sum)) {
                throw new ClcException("Corrupt configuration file.");
            }
        } catch (NoSuchAlgorithmException | IOException ex) {
            throw new ClcException(ex.getMessage(), ex);
        }
    }

    /**
     * Get the appendible text help; if {@link #ENV_MAX_COLUMNS} is set by the
     * environment, this will be the width of the help.
     *
     * @return non-{@code null} appendible text help.
     */
    private TextHelpAppendable getTextHelpAppendible() {
        TextHelpAppendable textHelpAppendable = new TextHelpAppendable(
                System.out);
        textHelpAppendable.setIndent(globalConfig.getHelpColumnSpacing());
        textHelpAppendable.setLeftPad(globalConfig.getHelpLeftPad());
        textHelpAppendable.setMaxWidth(globalConfig.getHelpWidth());
        if (System.getenv().get(ENV_MAX_COLUMNS) != null
                && globalConfig.isHelpWidthFromEnv()) {
            String widthStr = System.getenv().get(ENV_MAX_COLUMNS);
            try {
                int envWidth = Integer.parseInt(widthStr);
                textHelpAppendable.setMaxWidth(envWidth);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Invalid "
                        + GlobalConfiguration.GLOBAL_HELP_FORMAT_WIDTH_FROM_ENV + ": "
                        + widthStr);
            }
        }
        return textHelpAppendable;
    }

    /**
     * <i>Root level options</i> are options that are defined before any
     * {@link Command} options.
     *
     * @param args non-{@code null} arguments to process.
     *
     * @return non-{@code null} argument list of non-switch values (may be
     * empty).
     *
     * @throws ParseException if the underlying CLI parser detects any problems
     * parsing the given arguments.
     *
     * @throws ClcException if the configuration could not be parsed.
     */
    private List<String> processRootLevelOptions(final String[] args)
            throws ParseException, ClcException {
        final Map<String, OptionConfiguration> optionConfig
                = globalConfig.getGlobalOptionConfigurations();
        options = buildOptions(optionConfig);
        final CommandLineParser parser = new DefaultParser();
        final CommandLine cli;
        Map<String, OptionConfiguration> optConfigs = globalConfig.getOptionConfigurations();
        cli = parser.parse(options, args);
        // first, find all configurations that have a default value:
        Map<String, OptionConfiguration> defaultConfigs = new HashMap<>();
        for (String option : optConfigs.keySet()) {
            OptionConfiguration defaultConfig = optConfigs.get(option);
            if (defaultConfig.getDefaultValue() != null) {
                defaultConfigs.put(defaultConfig.getName(), defaultConfig);
            }
        }
        fireUpdates(defaultConfigs);

        // if help has been defined, this is done for the user
        checkGlobalHelp(cli, optionConfig);

        // if help has been defined, this is done for the user
        checkGlobalVersion(cli, optionConfig);

        fireUpdates(cli, optionConfig);

        return cli.getArgList();
    }

    /**
     * <i>Command level options</i> are options that are defined after a
     * {@link Command} definition has been encountered; all options defined
     * after such a definition will belong to that command until a new
     * {@link Command} is defined.
     *
     * <p>
     * The first argument is expected to be a command; the arguments will be
     * traversed in order until no more commands are present, leaving
     * potentially standard arguments.
     *
     * @param args non-{@code null} arguments to process; all arguments that are
     * not prefixed with at least one hyphen will be considered command
     * arguments, with subsequent (hyphen-prefixed) arguments being arguments
     * supplied to the given command(s).
     *
     * @return non-{@code null} argument list of non-switch values (may be
     * empty).
     *
     * @throws ParseException if the underlying CLI parser detects any problems
     * parsing the given arguments.
     *
     * @throws ClcException if the configuration could not be parsed.
     */
    private List<String> processCommandLevelOptions(final String[] args)
            throws ParseException, ClcException {
        // see if any of the arguments are commands:
        List<String> paths = new ArrayList<>();
        List<String> cliArgs = new ArrayList<>();
        CommandRoot commandRoot = globalConfig.getCommandRoot();
        Command lastCommand = commandRoot.getRoot();
        for (String arg : args) {
            Command command = commandRoot.find(globalConfig.getCommandRoot().getRoot(),
                    constructCommandPath(paths, arg));
            if (command != null) {
                lastCommand = command;
                paths.add(arg);
            } else if (lastCommand != null && !lastCommand.getChildren().isEmpty()) {
                // it's an unknown command since the last parent expected to
                // have a child command (will get picked up as an error):
                paths.add(arg);
                // ... and don't process any more commands:
                lastCommand = null;
            } else {
                // everything else is a CLI switch, switch argument or a
                // standard argument:
                cliArgs.add(arg);
            }
        }
        return processCommandLevelOptions(paths, cliArgs);
    }

    /**
     * Construct a command path from the given arguments.
     *
     * @param paths non-{@code null} list (may be empty) of built-up paths.
     *
     * @param arg non-{@code null} argument to add to the path.
     *
     * @return non-{@code null} valid command path with commands after the
     * parent command separated with the command separator.
     */
    private String constructCommandPath(List<String> paths, String arg) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < paths.size(); i++) {
            sb.append(paths.get(i));
            if (i < paths.size() - 1) {
                sb.append(Command.COMMAND_PATH_SEPARATOR);
            }
        }
        if (!paths.isEmpty()) {
            sb.append(Command.COMMAND_PATH_SEPARATOR).append(arg);
        } else {
            sb.append(arg);
        }
        return sb.toString();
    }

    /**
     * Process the given list of non-empty commands with the supplied command
     * line arguments.
     *
     * @param paths non-{@code null}, non-empty list of command paths from the
     * top-level command down to the command being processed.
     *
     * @param cliArgs non-{@code null} arguments succeeding the command to
     * process; may be empty.
     *
     * @return non-{@code null} argument list of non-switch values (may be
     * empty).
     *
     * @throws ParseException if the command line arguments (if there are any)
     * cannot be parsed.
     *
     * @throws ClcException if commands are present but invalid, or any of the
     * arguments to any commands cannot be parsed.
     */
    private List<String> processCommandLevelOptions(final List<String> paths,
            final List<String> cliArgs) throws ParseException, ClcException {
        // get the command name:
        String path = String.join(Command.COMMAND_PATH_SEPARATOR, paths);
        CommandRoot commandRoot = globalConfig.getCommandRoot();
        String command = paths.get(paths.size() - 1);
        final Command currentCommand = commandRoot.find(globalConfig.getCommandRoot().getRoot(), path);
        if (currentCommand == null) {
            // command has been misspelt, show what options are available for
            // the parent and throw an exception:
            // user has supplied a command that doesn't match; get the parent
            // commands and build up a list of valid commands to show the user:
            throwCommandHelpException(globalConfig, paths, command);
        }
        commandRoot.setCurrentCommand(currentCommand);
        final Map<String, OptionConfiguration> optionConfig
                = currentCommand.getOptionConfigurations();
        options = buildOptions(optionConfig);
        final CommandLineParser parser = new DefaultParser();
        String[] commandArgs = new String[cliArgs.size()];
        cliArgs.toArray(commandArgs);
        final CommandLine cli = parser.parse(options, commandArgs);
        // first, find all configurations that have a default value:
        Map<String, OptionConfiguration> defaultConfigs = new HashMap<>();
        for (String option : optionConfig.keySet()) {
            OptionConfiguration defaultConfig = optionConfig.get(option);
            if (defaultConfig.getDefaultValue() != null) {
                defaultConfigs.put(defaultConfig.getName(), defaultConfig);
            }
        }
        fireUpdates(currentCommand.getPath(), defaultConfigs);

        // if help has been defined, this is done for the user
        final OptionConfiguration optionHelp = optionConfig.get(
                globalConfig.getHelpOptionName());
        if (optionHelp != null && (cli.hasOption(optionHelp.getShortOption())
                || cli.hasOption(optionHelp.getLongOption()))) {
            // print help, then quit

            HelpFormatter formatter = new HelpFormatter();
            String header = globalConfig.getHelpCommandHeader();
            PrintWriter pw = new PrintWriter(System.out);
            formatter.printHelp(pw, globalConfig.getHelpWidth(),
                    globalConfig.getHelpCommandName(),
                    StringEscapeUtils.unescapeJava(header), options,
                    globalConfig.getHelpLeftPad(), 2,
                    globalConfig.getHelpCommandFooter(), globalConfig.isHelpAutoUsage());
            pw.flush();
//            TextHelpAppendable textHelpAppendable = getTextHelpAppendible();
//            HelpFormatter formatter = HelpFormatter.builder()
//                    .setHelpAppendable(textHelpAppendable)
//                    .setShowSince(false).get();
//            try {
//                if (globalConfig.isHelpSortOptions()) {
//                    Iterable<Option> helpOpts = formatter.sort(options);
//                    formatter.printHelp(globalConfig.getHelpCommandName().split(" ")[0]
//                            + " " + command,
//                            StringEscapeUtils.unescapeJava(currentCommand.getUsage()), helpOpts,
//                            globalConfig.getHelpCommandFooter(), globalConfig.isHelpAutoUsage());
//                } else {
//                    formatter.printHelp(globalConfig.getHelpCommandName().split(" ")[0]
//                            + " " + command + " " + currentCommand.getUsage(),
//                            StringEscapeUtils.unescapeJava(currentCommand.getUsage()), options,
//                            globalConfig.getHelpCommandFooter(), globalConfig.isHelpAutoUsage());
//                }
//            } catch (IOException ex) {
//                throw new IllegalArgumentException(ex.getMessage());
//            }
        }
        // if version has been defined, this is done for the user
        final OptionConfiguration optionVersion = optionConfig.get(
                globalConfig.getVersionOptionName());
        if (optionVersion != null && (cli.hasOption(optionVersion.getShortOption())
                || cli.hasOption(optionVersion.getLongOption()))) {
            // print version, then quit
            System.out.println(globalConfig.getVersionOptionText());
        }
        fireUpdates(cli, currentCommand.getPath(), optionConfig);
        return cli.getArgList();
    }

    /**
     * Gather information to throw in an exception for an unknown command. If a
     * help configuration is defined with short or long (or both) options, this
     * is added to the output. Regardless of if help is defined, the list of
     * valid commands for the given path is printed.
     *
     * @param globalConfig non-{@code null} global configuration.
     *
     * @param paths non-{@code null} full list of paths currently being
     * processed.
     *
     * @param command non-{@code null} unknown command.
     *
     * @throws ClcException always: the exception either reports help-based
     * information (if help options are present) along with valid commands that
     * are available, or (in the absence of help options) the list of valid
     * commands available.
     */
    private void throwCommandHelpException(GlobalConfiguration globalConfig,
            List<String> paths, String command) throws ClcException {
        CommandRoot commandRoot = globalConfig.getCommandRoot();
        Command parent = commandRoot.getRoot();
        if (paths.size() > 1) {
            String path = String.join(Command.COMMAND_PATH_SEPARATOR,
                    paths.subList(0, paths.size() - 1));
            parent = commandRoot.find(globalConfig.getCommandRoot().getRoot(), path);
        }
        final List<String> commands = new ArrayList<>();
        // get a list of available commands from the parent:
        for (Command cmd : parent.getChildren()) {
            commands.add(cmd.getName());
        }
        StringBuilder helpOptions = new StringBuilder();
        String helpConfigName = globalConfig.getHelpOptionName();
        if (helpConfigName != null) {
            helpOptions.append(". Try ");
            OptionConfiguration helpConfig
                    = globalConfig.getGlobalOptionConfigurations().get(
                            globalConfig.getHelpOptionName());
            if (helpConfig.getShortOption() != null) {
                helpOptions.append("-");
                helpOptions.append(helpConfig.getShortOption());
            }
            if (helpConfig.getLongOption() != null) {
                if (helpConfig.getShortOption() != null) {
                    helpOptions.append("/");
                }
                helpOptions.append("--");
                helpOptions.append(helpConfig.getLongOption());
            }
            throw new ClcException("Invalid command '" + command
                    + "'; valid commands are: "
                    + StringUtils.join(commands, ", ")
                    + helpOptions);
        } else {
            throw new ClcException("Invalid command '" + command
                    + "'; valid commands are: "
                    + StringUtils.join(commands, ", ") + ".");
        }
    }

    /**
     * Update all {@link DirUpdateListener}s with the specified directory for
     * the given directory type. If the directory type is recursive, it will
     * continue processing sub-directories of the current directory once the
     * update has been called; if the directory type is non-recursive, only the
     * specified directory will be scanned and then return once listeners have
     * been updated.
     *
     * @param directory non-{@code null} directory being scanned.
     *
     * @param dirType the directory type responsible for the update.
     */
    private void updateListeners(File directory, DirType dirType) {
        File[] files = directory.listFiles(dirType.getFileFilter());
        Arrays.sort(files);
        for (DirUpdateListener listener : dirListeners) {
            String id = dirType.getListenerId();
            if (listenerDirBlackList.containsKey(id)) {
                if (this.isBlacklisted(id, directory)) {
                    continue;
                }
            }
            if (!listener.directoryTraversed(directory, files,
                    dirType.getListenerId())) {
                // don't continue searcching
                break;
            }
            if (dirType.isRecursive()) {
                File[] dirs = directory.listFiles(dirType.getDirFilter());
                Arrays.sort(dirs);
                for (File dir : dirs) {
                    updateListeners(dir, dirType);
                }
            }
        }
        for (File file : files) {
            file = null;
        }
        files = null;
    }

    /**
     * Blacklist the specified directory for the given listener ID; listeners
     * will no longer receive updates to blacklisted directories and directories
     * that contain the start of the blacklisted path name will also be ignored.
     *
     * @param listenerId non-{@code null} existing listener ID.
     *
     * @param dir non-{@code null} existing directory.
     */
    public void addToBlackList(String listenerId, File dir) {
        if (listenerDirBlackList.containsKey(listenerId)) {
            Set<File> blacklist = listenerDirBlackList.get(listenerId);
            blacklist.add(dir);
        } else {
            Set<File> blacklist = new HashSet<>();
            blacklist.add(dir);
            listenerDirBlackList.put(listenerId, blacklist);
        }
    }

    /**
     * Determine if, for the given listener ID, the given directory is
     * blacklisted.
     *
     * @param id non-{@code null} existing listener ID.
     *
     * @param directory non-{@code null} directory to check if it has already
     * been blacklisted.
     *
     * @return {@code true} if the directory has been added to the collection of
     * blacklisted directories or any blacklisted directories match the start of
     * the specified directory name (for example a directory named
     * {@code /x/y/z/a} would also be considered blacklisted if a blacklisted
     * item has been added for the specified listener if the blacklisted
     * directory was {@code /x/y/z}.
     */
    private boolean isBlacklisted(String id, File directory) {
        boolean blacklisted = false;
        Set<File> blacklist = listenerDirBlackList.get(id);
        if (blacklist.contains(directory)) {
            blacklisted = true;
        }
        Set<File> dirs = listenerDirBlackList.get(id);
        if (dirs != null && !blacklisted) {
            for (File dir : dirs) {
                if (directory.getAbsolutePath().startsWith(dir.getAbsolutePath())) {
                    blacklisted = true;
                    break;
                }
            }
        }
        return blacklisted;
    }

    /**
     * Check to see if global help options have been defined, and if they have
     * and the user has specified the help option, print the help options.
     *
     * @param cli non-{@code null} command line.
     *
     * @param optionConfig non-{@code null} option configuration.
     *
     * @throws ClcException if the option for help has been defined to have an
     * argument.
     */
    private void checkGlobalHelp(final CommandLine cli,
            final Map<String, OptionConfiguration> optionConfig)
            throws ClcException {
        if (globalConfig.getHelpOptionName() != null) {
            OptionConfiguration optionHelp = optionConfig.get(
                    globalConfig.getHelpOptionName());
            if (optionHelp.hasArg()) {
                throw new ClcException("Error: Option "
                        + optionHelp.getName() + " cannot have an argument"
                        + " associated with it.");
            }
            // see if the caller has asked for help
            String optionHelpValue = optionHelp.getShortOption();
            if (optionHelpValue == null) {
                // they specified long options only so short option will be null
                optionHelpValue = optionHelp.getLongOption();
            }
            if (cli.hasOption(optionHelpValue)) {
                printHelp();
            }
        }
    }

    /**
     * Check to see if global version options have been defined, and if they
     * have and the user has specified the version option, print the version.
     *
     * @param cli non-{@code null} command line.
     *
     * @param optionConfig non-{@code null} option configuration.
     *
     * @throws ClcException if the option for version has been defined to have
     * an argument.
     */
    private void checkGlobalVersion(final CommandLine cli,
            final Map<String, OptionConfiguration> optionConfig)
            throws ClcException {
        if (globalConfig.getVersionOptionName() != null) {
            OptionConfiguration optionVersion = optionConfig.get(
                    globalConfig.getVersionOptionName());
            if (optionVersion.hasArg()) {
                throw new ClcException("Error: Option "
                        + optionVersion.getName() + " cannot have an argument"
                        + " associated with it.");
            }
            // see if the caller has asked for version information:
            String optionVersionValue = optionVersion.getShortOption();
            if (optionVersionValue == null) {
                // they specified long options only so short option will be null
                optionVersionValue = optionVersion.getLongOption();
            }
            if (cli.hasOption(optionVersionValue)) {
                // they have, print the version information:
                printVersion();
            }
        }
    }

    /**
     * Take all of the processed options obtained from the configuration, take
     * all the CLI options passed in via the command line and update all option
     * listeners with the updated values.
     *
     * @param cli non-{@code null} command line.
     *
     * @param optionConfig non-{@code null} option configuration.
     *
     * @throws ClcException if any of the values of the option arguments are
     * invalid (such as an integer being out of range, for example).
     */
    private void fireUpdates(final CommandLine cli,
            final Map<String, OptionConfiguration> optionConfig)
            throws ClcException {
        final Option[] cliOptions = cli.getOptions();
        parsedOptions = Arrays.asList(cliOptions);
        for (final Option option : cliOptions) {
            for (int i = 0; i < optionListeners.size(); i++) {
                String optValue = option.getValue();
                String optName = option.getOpt();
                OptionConfiguration config = null;
                if (optName != null) {
                    config = getOptionConfiguration(optionConfig, optName);
                }
                if (config != null && config.isIgnoreCliArgs() != null) {
                    setParseArgs(!config.isIgnoreCliArgs());
                }
                if (optName != null) {
                    updateListener(optionListeners.get(i), optionConfig,
                            optName, optValue);
                }
                optName = option.getLongOpt();
                if (optName != null) {
                    config = getOptionConfiguration(optionConfig, optName);
                }
                if (config != null && config.isIgnoreCliArgs() != null) {
                    setParseArgs(!config.isIgnoreCliArgs());
                }
                if (optName != null) {
                    updateListener(optionListeners.get(i), optionConfig,
                            optName, optValue);
                }
            }
        }
    }

    /**
     * Update all option listeners with the default configuration values.
     *
     * @param defaultConfigs non-{@code null} default option configurations.
     *
     * @throws ClcException if any of the values of the option arguments are
     * invalid (such as an integer being out of range, for example).
     */
    private void fireUpdates(final Map<String, OptionConfiguration> defaultConfigs)
            throws ClcException {
        for (final OptionConfiguration option : defaultConfigs.values()) {
            for (int i = 0; i < optionListeners.size(); i++) {
                Object optValue = option.getDefaultValue();
                String optName = option.getShortOption();
                if (optName == null) {
                    optName = option.getLongOption();
                }
                if (optName != null) {
                    optionListeners.get(i).option(optName, optValue);
                }
            }
        }
    }

    /**
     * Take all of the processed options obtained from the configuration, take
     * all the CLI options passed in via the command line and update all command
     * option listeners with the updated values.
     *
     * @param cli non-{@code null} command line.
     *
     * @param command command path (if present) for the command from a top-level
     * command including any sub-commands separated by forward slashes; if
     * non-{@code null} listeners will be updated via
     * {@link OptionListener#option(java.lang.String, java.lang.String, java.lang.Object)};
     * may be {@code null}, in which case listeners will be updated with
     * {@link OptionListener#option(java.lang.String, java.lang.Object)}.
     *
     * @param optionConfig non-{@code null} option configuration.
     *
     * @throws ClcException if any of the values of the option arguments are
     * invalid (such as an integer being out of range, for example).
     */
    private void fireUpdates(final CommandLine cli, final String command,
            final Map<String, OptionConfiguration> optionConfig)
            throws ClcException {
        final Option[] cliOptions = cli.getOptions();
        parsedOptions = Arrays.asList(cliOptions);
        for (final Option option : cliOptions) {
            for (int i = 0; i < commandOptionListeners.size(); i++) {
                String optValue = option.getValue();
                String optName = option.getOpt();
                OptionConfiguration config = null;
                if (optName != null) {
                    config = getOptionConfiguration(optionConfig, optName);
                }
                if (config != null && config.isIgnoreCliArgs() != null) {
                    setParseArgs(!config.isIgnoreCliArgs());
                }
                if (optName != null) {
                    updateListener(commandOptionListeners.get(i), optionConfig, command,
                            optName, optValue);
                }
                optName = option.getLongOpt();
                if (optName != null) {
                    config = getOptionConfiguration(optionConfig, optName);
                }
                if (config != null && config.isIgnoreCliArgs() != null) {
                    setParseArgs(!config.isIgnoreCliArgs());
                }
                if (optName != null) {
                    updateListener(commandOptionListeners.get(i), optionConfig, command,
                            optName, optValue);
                }
            }
        }
    }

    /**
     * Update all option command listeners with the default configuration values
     * for the given command.
     *
     * @param command command path (if present) for the command from a top-level
     * command including any sub-commands separated by forward slashes; if
     * non-{@code null} listeners will be updated via
     * {@link OptionListener#option(java.lang.String, java.lang.String, java.lang.Object)};
     * may be {@code null}, in which case listeners will be updated with
     * {@link OptionListener#option(java.lang.String, java.lang.Object)}.
     *
     * @param defaultConfigs non-{@code null} default option configuration.
     *
     * @throws ClcException if any of the values of the option arguments are
     * invalid (such as an integer being out of range, for example).
     */
    private void fireUpdates(final String command,
            final Map<String, OptionConfiguration> defaultConfigs)
            throws ClcException {
        for (final OptionConfiguration option : defaultConfigs.values()) {
            for (int i = 0; i < commandOptionListeners.size(); i++) {
                Object optValue = option.getDefaultValue();
                String optName = option.getShortOption();
                if (optName == null) {
                    optName = option.getLongOption();
                }
                if (optName != null) {
                    commandOptionListeners.get(i).option(command, optName, optValue);
                }
            }
        }
    }

    /**
     * Convert the specified map of configurations to valid command line
     * options.
     *
     * @param map non-{@code null} map of configuration options.
     *
     * @return Options based on the converted map.
     */
    private Options buildOptions(final Map<String, OptionConfiguration> map) {
        options = new Options();
        for (OptionConfiguration optConfig : map.values()) {
            final String shortOption = optConfig.getShortOption();
            final String longOption = optConfig.getLongOption();
            final String description = optConfig.getDescription();
            final String argName = optConfig.getArgName();
            final boolean hasArg = optConfig.hasArg();
            final Option option = new Option(shortOption, longOption, hasArg,
                    description);
            if (argName != null) {
                option.setArgName(argName);
            }
            options.addOption(option);
        }
        return options;
    }

    /**
     * Update the specified option listener with the given option name and
     * option value.
     *
     * @param listener non-{@code null} listener to update.
     *
     * @param optionConfig non-{@code null} option configurations.
     *
     * @param option non-{@code null} option that has been passed in from the
     * command line.
     *
     * @param optionValue option value from passed in from the command line for
     * the specified option; for options that require a value this will be
     * non-{@code null}, for options that are switches without an option this
     * will be {@code null}.
     *
     * @throws ClcException if the option value cannot be constructed because it
     * is an invalid value.
     */
    private void updateListener(OptionListener listener,
            Map<String, OptionConfiguration> optionConfig, String option,
            String optionValue) throws ClcException {
        // the option could be a short option or a long option - since option
        // configurations are defined with the key being the
        // option.[name] value in the original configuration, we need to get the
        // corresponding configuration that matches the given short/long option
        OptionConfiguration optConfig = getOptionConfiguration(optionConfig,
                option);
        Object value = null;
        if (optConfig != null) {
            ValueType vt = optConfig.getValueType();
            if (vt != null) {
                try {
                    value = vt.getValue(optionValue);
                    if (vt instanceof DirType) {
                        DirType dirType = (DirType) vt;
                        if (dirType.isRecursive() != null
                                && dirType.getListenerId() != null
                                && !dirTypes.containsKey(dirType.getListenerId())) {
                            dirTypes.put(dirType.getListenerId(), dirType);
                        }
                    }
                } catch (ValueTypeCreationException cex) {
                    // construction error e.g. min = 100, specified 90
                    throw new ClcException("Error: option " + option
                            + ": " + cex.getMessage());
                }
            }
        }
        listener.option(option, value);
    }

    /**
     * Update the specified command option listener with the given command,
     * option name and option value.
     *
     * @param listener non-{@code null} listener to update.
     *
     * @param optionConfig non-{@code null} option configurations.
     *
     * @param command command name if a command was invoked; may be {@code null}
     * in which case the option will be for top-level non-command options.
     *
     * @param option non-{@code null} option that has been passed in from the
     * command line.
     *
     * @param optionValue option value from passed in from the command line for
     * the specified option; for options that require a value this will be
     * non-{@code null}, for options that are switches without an option this
     * will be {@code null}.
     *
     * @throws ClcException if the option value cannot be constructed because it
     * is an invalid value.
     */
    private void updateListener(CommandOptionListener listener,
            Map<String, OptionConfiguration> optionConfig, String command,
            String option, String optionValue) throws ClcException {
        // the option could be a short option or a long option - since option
        // configurations are defined with the key being the
        // option.[name] value in the original configuration, we need to get the
        // corresponding configuration that matches the given short/long option
        OptionConfiguration optConfig = getOptionConfiguration(optionConfig,
                option);
        Object value = null;
        if (optConfig != null) {
            ValueType vt = optConfig.getValueType();
            if (vt != null) {
                try {
                    value = vt.getValue(optionValue);
                    if (vt instanceof DirType) {
                        DirType dirType = (DirType) vt;
                        if (dirType.isRecursive() != null
                                && dirType.getListenerId() != null
                                && !dirTypes.containsKey(dirType.getListenerId())) {
                            dirTypes.put(dirType.getListenerId(), dirType);
                        }
                    }
                } catch (ValueTypeCreationException cex) {
                    // construction error e.g. min = 100, specified 90
                    throw new ClcException("Error: option " + option
                            + ": " + cex.getMessage());
                }
            }
        }
        listener.option(command, option, value);
    }

    /**
     * Update the listener with the given argument configuration, index and
     * argument value.
     *
     * @param listener non-{@code null} listener to update.
     *
     * @param argsConfig non-{@code null} argument configuration.
     *
     * @param index index, starting at 0 and incrementing by 1 for each
     * processed argument.
     *
     * @param argValue non-{@code null} argument value (may be empty).
     *
     * @throws ClcException if the value type for the argument does not exist,
     * is an invalid value for a given value type (or breaks the defined
     * properties for the value type).
     */
    private void updateListener(ArgsListener listener,
            ArgsConfiguration argsConfig, int index, String argValue)
            throws ClcException {
        Object value = null;
        ValueType vt = argsConfig.getValueType();
        try {
            value = vt.getValue(argValue);
            argsValueTypes.add(value);
            if (vt instanceof DirType) {
                DirType dirType = (DirType) vt;
                if (dirType.isRecursive() != null
                        && dirType.getListenerId() != null
                        && !dirTypes.containsKey(dirType.getListenerId())) {
                    dirTypes.put(dirType.getListenerId(), dirType);
                }
            }
        } catch (ValueTypeCreationException cex) {
            // construction error e.g. min = 100, specified 90
            throw new ClcException("Error: index " + index
                    + " - " + argsConfig.getArgName() + ": " + cex.getMessage());
        }
        listener.argument(argsConfig.getName(), index, value);
    }

    /**
     * Update the command option listener with the given argument configuration,
     * index and argument value.
     *
     * @param listener non-{@code null} listener to update.
     *
     * @param argsConfig non-{@code null} argument configuration.
     *
     * @param index index, starting at 0 and incrementing by 1 for each
     * processed argument.
     *
     * @param argValue non-{@code null} argument value (may be empty).
     *
     * @throws ClcException if the value type for the argument does not exist,
     * is an invalid value for a given value type (or breaks the defined
     * properties for the value type).
     */
    private void updateListener(CommandArgsListener listener,
            ArgsConfiguration argsConfig, int index, String argValue)
            throws ClcException {
        Object value = null;
        ValueType vt = argsConfig.getValueType();
        try {
            value = vt.getValue(argValue);
            argsValueTypes.add(value);
            if (vt instanceof DirType) {
                DirType dirType = (DirType) vt;
                if (dirType.isRecursive() != null
                        && dirType.getListenerId() != null
                        && !dirTypes.containsKey(dirType.getListenerId())) {
                    dirTypes.put(dirType.getListenerId(), dirType);
                }
            }
        } catch (ValueTypeCreationException cex) {
            // construction error e.g. min = 100, specified 90
            throw new ClcException("Error: index " + index
                    + " - " + argsConfig.getArgName() + ": " + cex.getMessage());
        }
        CommandRoot commandRoot = globalConfig.getCommandRoot();
        listener.argument(commandRoot.getCurrentCommand().getPath(),
                argsConfig.getName(), index, value);
    }

    /**
     * Get the option configuration that has a short or long option that is
     * equal to the given option.
     *
     * @param optionConfig non-{@code null} map of option configurations.
     *
     * @param option non-{@code null} command line option; may be a short or
     * long option.
     *
     * @return the option configuration that matches the given option;
     * {@code null} if there was no match.
     */
    private OptionConfiguration getOptionConfiguration(
            Map<String, OptionConfiguration> optionConfig, String option) {
        OptionConfiguration optConfig = null;
        for (OptionConfiguration config : optionConfig.values()) {
            if (option.equals(config.getShortOption())
                    || option.equals(config.getLongOption())) {
                optConfig = config;
                break;
            }
        }
        return optConfig;
    }
}
