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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.apache.commons.cli.Option;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.text.CaseUtils;
import org.statefive.clic.AbstractArgsConfiguration;
import org.statefive.clic.ArgsConfiguration;
import org.statefive.clic.ArgsListener;
import org.statefive.clic.Clc;
import org.statefive.clic.ClcException;
import org.statefive.clic.ClcParser;
import org.statefive.clic.Command;
import org.statefive.clic.CommandArgsListener;
import org.statefive.clic.CommandOptionListener;
import org.statefive.clic.GlobalConfiguration;
import org.statefive.clic.OptionConfiguration;
import org.statefive.clic.OptionListener;
import org.statefive.clic.OptionsTypeEnum;
import static org.statefive.clic.gensrc.VariablePrefixType.VARIABLE_PREFIXES_TYPE_NAME;
import org.statefive.clic.valuetype.BooleanType;
import org.statefive.clic.valuetype.ListType;
import org.statefive.clic.valuetype.StringType;
import org.statefive.clic.valuetype.ValueType;
import org.statefive.clic.valuetype.ValueTypeBuilder;
import org.statefive.clic.valuetype.ValueTypeFactory;

/**
 * Tool to generate Java source from a CLC file or stream. The source will
 * include all required listeners for receiving updates from read-in options and
 * if present, options for commands and values for prefix arguments and standard
 * arguments.
 *
 * <p>
 * Caveat codificator (coder beware): Hacked, untested code.
 *
 * @author rich
 */
public class CliGenSrc {

    /**
     * Substitution string for adding in the correct implementations for the
     * generated source.
     */
    private static final String IMPLEMENTATIONS = "__IMPLEMENTATIONS__";

    /**
     * Command line configuration
     */
    private final Clc clc = Clc.getInstance();

    /**
     * Global configuration read from input stream or file.
     */
    private GlobalConfiguration config;

    /**
     * Implementations that will be built up. By default, {@link OptionListener}
     * will be present; also possibly {@link CommandOptionListener},
     * {@link ArgsListener} and {@link CommandArgsListener} if those are defined
     * within the read configuration.
     */
    private final List<String> implementations = new ArrayList<>();

    /**
     * Set of fully-qualified imports to add to; options that are associated
     * with a value type will (if required) add to the import list to be added
     * to the start of any generated source
     */
    private final Set<String> imports = new TreeSet<>();

    /**
     * Map of variable names; the key will be the name of the variable, the
     * value will be the Java type (class or primitive) of the variable.
     */
    private final Map<String, String> variableNames = new TreeMap<>();

    /**
     * List-based variables; the key will be the list variable name, the value
     * to list {@link ValueType}.
     */
    private final Map<String, ListType> listVariables = new HashMap<>();

    /**
     * List-list-based variables; the key will be the list variable name, the
     * value to list {@link ValueType}. These are formed when a non-fixed length
     * list is defined as the last argument configuration to a group of argument
     * configuration.
     */
    private final Map<String, ListType> listListVariables = new HashMap<>();

    /**
     * Variable declarations; these will be built up of the form
     * {@code private [type] [variableName]}.
     */
    private final List<String> declarations = new ArrayList<>();

    /**
     * Helper; has options updated from the underlying CLC API.
     */
    private final OptionHelper optionHelper = new OptionHelper();

    /**
     * Main entry point.
     *
     * @param args non-{@code null} command line arguments to process.
     */
    public static void main(String[] args) {
        if (!ValueTypeFactory.getInstance().isRegistered(VARIABLE_PREFIXES_TYPE_NAME)) {
            ValueTypeBuilder<VariablePrefixType> variablePrefixType
                    = new ValueTypeBuilder<>(VariablePrefixType.class);
            ValueTypeFactory.getInstance().registerValueTypeBuilder(
                    VARIABLE_PREFIXES_TYPE_NAME, variablePrefixType);
        }
        CliGenSrc cliGenSrc = new CliGenSrc();
        try {
            cliGenSrc.buildCliOptions(args);
            cliGenSrc.generateSource();
        } catch (ClcException | IOException ex) {
            if (cliGenSrc.isStackTrace()) {
                ex.printStackTrace();
            }
            System.err.println("Error: " + ex.getMessage());
            System.err.println("Try -h/--help for options.");
            System.exit(1);
        }

    }

    /**
     * Process the given command line arguments.
     *
     * @param args non-{@code null} arguments.
     *
     * @throws ClcException
     *
     * @throws IOException
     */
    void buildCliOptions(String... args) throws ClcException, IOException {
        InputStream is = CliGenSrc.class.getResourceAsStream(
                "/cli-gen-src.clc");
        clc.addOptionListener(optionHelper);
        clc.process(is, "UTF-8", args);
        if (optionHelper.getConfigStream() == null) {
            throw new ClcException("-c/--configuration must be set.");
        }
    }

    /**
     * Determine if a stack trace should be generated.
     *
     * @return {@code true} to include stack trace data; {@code false}
     * otherwise.
     */
    public boolean isStackTrace() {
        return optionHelper.isStacktrace();
    }

    /**
     * Generate a configuration.
     *
     * @throws ClcException if generation fails.
     *
     * @throws IOException if generation fails.
     */
    public void generateSource() throws ClcException, IOException {
        ClcParser parser = new ClcParser();
        try {
            config = parser.parse(optionHelper.getConfigStream(), "UTF-8",
                    false);
            if (optionHelper.isVerify()) {
                // we wouldn't have got here if the parsing wasn't successful:
                System.out.println("Verification succeeded.");
                System.exit(0);
            }
            String source = generateSourceBody();
            source = source.replace(IMPLEMENTATIONS, String.join(", ",
                    implementations));
            if (optionHelper.getOutputDir() == null) {
                System.out.println(source);
            } else {
                File outfile = new File(optionHelper.getOutputDir(),
                        optionHelper.getClassName() + ".java");
                FileOutputStream fos = new FileOutputStream(outfile);
                fos.write(source.getBytes());
                fos.close();
            }
        } catch (ClcException | IOException ex) {
            if (optionHelper.isVerify()) {
                System.err.println("Verification failed: " + ex.getMessage());
                System.exit(1);
            }
            throw ex;
        }
    }

    /**
     * Generate tabs.
     *
     * @param level number of tabular indents to generate.
     *
     * @return tabs equal to the level; the empty string if less than 1.
     */
    private String generateIndent(int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            if (optionHelper.getTabsAsSpaces() > 0) {
                for (int k = 0; k < optionHelper.getTabsAsSpaces(); k++) {
                    sb.append(" ");
                }
            } else {
                sb.append("\t");
            }
        }
        return sb.toString();
    }

    /**
     *
     * @return
     */
    private String generateSourceBody() {
        implementations.add(OptionListener.class.getSimpleName());
        imports.add(OptionListener.class.getName());
        if (config.hasArgsConfiguration()) {
            implementations.add(ArgsListener.class.getSimpleName());
            imports.add(ArgsListener.class.getName());
//            processArgsConfiguration(config.getCurrentArgsConfigurations());
            processArgsConfiguration();
        }
        StringBuilder sb = new StringBuilder();
        if (optionHelper.getPackageName() != null) {
            sb.append("package ").append(optionHelper.getPackageName())
                    .append(";")
                    .append(System.lineSeparator())
                    .append(System.lineSeparator());
        }
        Map<String, OptionConfiguration> options = config.getGlobalOptionConfigurations();
        processOptionConfiguration(options);
        if (config.getCommandRoot().getCommands() != null) {
            processCommandConfigurations();
        }
        for (String importName : imports) {
            if (importName != null) {
                sb.append("import ").append(importName).append(";")
                        .append(System.lineSeparator());
            }
        }
        sb.append(System.lineSeparator());
        if (optionHelper.isHeader()) {
            try {
                sb.append(getHeader());
            } catch (IOException ex) {
                throw new IllegalArgumentException(ex);
            }
        }
        sb.append(generateClassStart(optionHelper.getClassName()));
        sb.append(System.lineSeparator());
        for (String declaration : declarations) {
            sb.append(declaration).append(";").append(System.lineSeparator());
        }
        sb.append(System.lineSeparator());
        sb.append(generateMethodStart());
        sb.append(generateIndent(2)).append("switch(option) {\n");
        for (String option : options.keySet()) {
            OptionConfiguration optConfig = options.get(option);
            String variableName = null;
            String optionNameLong = null;
            String optionNameShort = null;
            if (null != config.getOptionsType()) {
                switch (config.getOptionsType()) {
                    case SHORT:
                        variableName = getVariableName(optConfig.getShortOption());
                        optionNameShort = optConfig.getShortOption();
                        break;
                    case LONG:
                        variableName = getVariableName(optConfig.getLongOption());
                        optionNameLong = optConfig.getLongOption();
                        break;
                    case BOTH:
                        variableName = getVariableName(optConfig.getLongOption());
                        optionNameShort = optConfig.getShortOption();
                        optionNameLong = optConfig.getLongOption();
                        break;
                    default:
                        break;
                }
            }
            String optClassName = null;
            String optPrimitiveName = null;
            if (optConfig.getValueType() != null) {
                optClassName = optConfig.getValueType().getJavaClassName();
                optPrimitiveName = optConfig.getValueType().getJavaPrimitiveName();
            }
            // NB non-args options will return null and have no option value
            // type, 
            ListType listType = null;
            if (listVariables.containsKey(variableName)) {
                listType = listVariables.get(variableName);
            }
//            variableName = getVariableName(variableName);
            sb.append(generateCaseBody(3, variableName, optionNameShort,
                    optionNameLong, optClassName, optPrimitiveName,
                    listType));
        }
        sb.append(generateIndent(2)).append("}\n");
        sb.append(generateMethodEnd());
        if (!config.getCommandRoot().getCommands().isEmpty()) {
            generateCommandSwitchMethod(sb);
        }
        if (config.hasArgsConfiguration()) {
            generateArgsSwitchMethod(sb);
        }
        sb.append(generateGetters(variableNames, listVariables));
        sb.append(generateClassEnd());
        return sb.toString();
    }

    /**
     * Process top-level options not including command options. This will build
     * up the import, variable names, list variables and member declarations.
     *
     * @param options non-{@code null} option configuration to interrogate.
     */
    private void processOptionConfiguration(Map<String, OptionConfiguration> options) {
        for (String option : options.keySet()) {
            OptionConfiguration optConfig = options.get(option);
            generateImportAndVariableData(optConfig, null);
        }
    }

    /**
     *
     * @param argsConfigs
     */
    private void processArgsConfiguration() {
        int currentPrefix = 0;
        ArgsConfiguration last = null;
        // first, process top-level/non-command args:
        Map<String, ArgsConfiguration> argsConfigs = config.getArgsConfigurations();
        for (String option : argsConfigs.keySet()) {
            ArgsConfiguration argsConfig = argsConfigs.get(option);
            if (last != null) {
                currentPrefix += last.getLength();
            }
            generateImportAndVariableData(currentPrefix, null, argsConfig);
            last = argsConfig;
        }
        // now process command args (if present) on a per-command basis:
        for (Command cmd : config.getCommandRoot().getCommands()) {
            last = null;
            currentPrefix = 0;
            argsConfigs = cmd.getArgsConfigurations();
            for (String key : argsConfigs.keySet()) {
                ArgsConfiguration argsConfig = argsConfigs.get(key);
                if (last != null && last.getLength() != null) {
                    currentPrefix += last.getLength();
                }
                generateImportAndVariableData(currentPrefix, cmd, argsConfig);
                last = argsConfig;
            }
        }
    }

    /**
     *
     */
    private void processCommandConfigurations() {
        List<Command> commands = config.getCommandRoot().getCommands();
        if (!commands.isEmpty()) {
            declarations.add(getDeclarationLine("String", null,
                    optionHelper.getCommandName()));
            variableNames.put(optionHelper.getCommandName(), "java.lang.String");
            for (Command command : commands) {
                Map<String, OptionConfiguration> options
                        = command.getOptionConfigurations();
                for (String keyName : options.keySet()) {
                    OptionConfiguration optConfig = options.get(keyName);
                    generateImportAndVariableData(optConfig, command.getPath());
                }
            }
        }
    }

    /**
     *
     * @param optConfig
     * @param commandName
     */
    private void generateImportAndVariableData(OptionConfiguration optConfig, String commandName) {
        String variableName = null;
        String variablePrefix = "";
        if (commandName != null) {
            variablePrefix = generateCommandVariableName(commandName);
        }
        boolean isHelp = false;
        if (config.getHelpOptionName() != null
                && (config.getHelpOptionName().equals(optConfig.getShortOption())
                || config.getHelpOptionName().equals(optConfig.getLongOption()))) {
            isHelp = true;
        }
        boolean isVersion = false;
        if (config.getVersionOptionName() != null
                && (config.getVersionOptionName().equals(optConfig.getShortOption())
                || config.getVersionOptionName().equals(optConfig.getLongOption()))) {
            isVersion = true;
        }
        if (config.getOptionsType() == OptionsTypeEnum.SHORT) {
            variableName = getVariableName(variablePrefix + "_" + optConfig.getShortOption());
        } else {
            variableName = getVariableName(variablePrefix + "_" + optConfig.getLongOption());
        }
        if (!isHelp && !isVersion) {
            if (optConfig.hasArg() == null || !optConfig.hasArg()) {
                // it's a unary/binary switch; default value will be false
                variableNames.put(variableName, "boolean");
                String lineDecl = getDeclarationLine(null,
                        "boolean", variableName);
                declarations.add(lineDecl);

            } else if (optConfig.getValueType() != null) {
                // package name, class name, primitive name:
                Triple<String, String, String> triple = null;
                if (optConfig.getValueType() != null) {
                    triple = new ImmutableTriple<>(
                            optConfig.getValueType().getPackageName(),
                            optConfig.getValueType().getJavaClassName(),
                            optConfig.getValueType().getJavaPrimitiveName());
                } else {
                    // it's a no-args CLI option switch, e.g. --help etc.; we
                    // need to set some values so that this still gets processed
                    triple = new ImmutableTriple<>(
                            null, Boolean.class.getSimpleName(),
                            "boolean");
                }
                if (triple.getMiddle() != null) {
                    if (triple.getLeft() != null) {
                        imports.add(triple.getLeft() + "." + triple.getMiddle());
                    }
                    // e.g. optToDoSomething, java.lang.String
                    variableNames.put(variableName, triple.getMiddle());
                    String lineDecl = null;
                    if (optConfig.getValueType() instanceof ListType) {
                        ListType listType = (ListType) optConfig.getValueType();
                        lineDecl = getListDeclarationLine(listType, variableName);
                        listVariables.put(variableName, listType);
                        String listElementPackageName = String.class.getPackageName();
                        String listElementClassName = String.class.getSimpleName();
                        if (listType.getElementValueType() != null) {
                            listElementPackageName = listType.getElementValueType().getPackageName();
                            listElementClassName = listType.getElementValueType().getJavaClassName();
                        }
                        if (listElementPackageName != null) {
                            // caters for types of arrays that do not specifiy a
                            // package name, for example DataFileType
                            imports.add(listElementPackageName
                                    + "." + listElementClassName);
                        }
                    } else {
                        lineDecl = getDeclarationLine(triple.getMiddle(),
                                triple.getRight(), variableName);
                    }
                    declarations.add(lineDecl);
                }
            }
        }
    }

    /**
     *
     * @param sb
     */
    private void generateCommandArgsConfigurationData(StringBuilder sb, int currentPrefix) {
        // just a repeat of the above but with commands:
//        int currentPrefix = 0;
        sb.append(generateIndent(2)).append("switch(command) {\n");
        ArgsConfiguration last = null;
        for (Command command : config.getCommandRoot().getCommands()) {
            currentPrefix = 0;
            if (command.hasArgsConfigurations()) {
                sb.append(generateIndent(3))
                        .append("case \"")
                        .append(getVariableName(getCommandName(command.getPath())))
                        .append("\":\n");
                Map<String, ArgsConfiguration> argsConfigs = command.getArgsConfigurations();
                sb.append(generateIndent(4))
                        .append("switch(name) {\n");
                int previousLen = 0;
                for (String argsConfigName : argsConfigs.keySet()) {
                    ArgsConfiguration argsConfig = argsConfigs.get(argsConfigName);
                    sb.append(generateIndent(5))
                            .append("case \"")
                            .append(argsConfig.getName())
                            .append("\":\n");
                    if (argsConfig.getLength() != null) {
//                        currentPrefix += argsConfig.getLength();
                        previousLen = argsConfig.getLength();
                        sb.append(generateIndexedCaseBody(6, currentPrefix,
                                command, argsConfig,
                                optionHelper.getVariablePrefixes().getCommandArgsPrefix()));
                    } else {
                        // extra check, if argsConfig.getLength == null, then is a list type
                        sb.append(generateIndent(6));

                        String variableName = generatePrefixVariableName(
                                optionHelper.getVariablePrefixes().getCommandArgsPrefix() + command.getPath(),
                                null, argsConfig.getName());
                        String typeName = argsConfig.getValueType().getJavaClassName();
                        if (argsConfig.getValueType().getJavaPrimitiveName() != null) {
                            typeName = argsConfig.getValueType().getJavaPrimitiveName();
                        }
                        sb.append(variableName)
                                .append(".add((")
                                .append(typeName)
                                .append(") value);");
                        sb.append(System.lineSeparator());
                    }
                    sb.append(generateIndent(5)).append("break;\n");
                    last = argsConfig;
                }
                sb.append(generateIndent(4)).append("}\n");
                sb.append(generateIndent(3)).append("break;\n");
            }
        }
        sb.append(generateIndent(2)).append("}");
    }

    /**
     *
     * @param currentPrefix
     *
     * @param command
     *
     * @param argsConfig
     */
    private void generateImportAndVariableData(int currentPrefix,
            Command command, ArgsConfiguration argsConfig) {
        if (argsConfig.getLength() != null) {
            // cannot be list if length set:
            // TODO CHECK HERE ValueType is not 'list'
            for (int i = 0; i < argsConfig.getLength(); i++) {
                String variableName = null;
                if (command != null) {
                    variableName = generatePrefixVariableName(
                            optionHelper.getVariablePrefixes().getCommandArgsPrefix() + command.getPath(),
                            i, argsConfig.getName());
                } else {
                    variableName = generatePrefixVariableName(
                            optionHelper.getVariablePrefixes().getArgsPrefix(),
                            currentPrefix + i, argsConfig.getName());
                }
                if (argsConfig.getValueType() == null) {
                    argsConfig.setValueType(new StringType());
                }
                // triples will be formed as follows:
                // Left: package name, Middle: class name, Right: primitive name
                Triple<String, String, String> triple = null;
                if (argsConfig.getValueType() != null) {
                    triple = new ImmutableTriple<>(
                            argsConfig.getValueType().getPackageName(),
                            argsConfig.getValueType().getJavaClassName(),
                            argsConfig.getValueType().getJavaPrimitiveName());
                } else {
                    // it's a no-args CLI option switch, e.g. --help etc.; we
                    // need to set some values so that this still gets processed
                    triple = new ImmutableTriple<>(
                            null, Boolean.class.getSimpleName(),
                            "boolean");
                }
                if (triple.getMiddle() != null) {
                    if (triple.getLeft() != null) {
                        imports.add(triple.getLeft() + "." + triple.getMiddle());
                    }
                    variableNames.put(variableName, triple.getMiddle());
                    String lineDecl = null;
                    if (argsConfig.getValueType() instanceof ListType) {
                        // TODO NOT SURE THIS SHOULD BE HERE
                        ListType listType = (ListType) argsConfig.getValueType();
                        lineDecl = getArgsListDeclarationLine(argsConfig.getLength(), listType, variableName);
                        if (lineDecl.contains("List<List<")) {
                            listListVariables.put(variableName, listType);
                        } else {
                            listVariables.put(variableName, listType);
                        }
                        String listElementPackageName = String.class.getPackageName();
                        String listElementClassName = String.class.getSimpleName();
                        if (listType.getElementValueType() != null) {
                            listElementPackageName = listType.getElementValueType().getPackageName();
                            listElementClassName = listType.getElementValueType().getJavaClassName();
                        }
                        if (listElementPackageName != null) {
                            // caters for types of arrays that do not specifiy a
                            // package name, for example DataFileType
                            imports.add(listElementPackageName
                                    + "." + listElementClassName);
                        }
                    } else {
                        lineDecl = getDeclarationLine(triple.getMiddle(),
                                triple.getRight(), variableName);
                    }
                    declarations.add(lineDecl);
                }
            }
        } else {
            // if not a list, turn it into one:
            String variableName = null;
            if (command != null) {
                variableName = generatePrefixVariableName(
                        optionHelper.getVariablePrefixes().getCommandArgsPrefix() + command.getPath(),
                        null, argsConfig.getName());
            } else {
                variableName = generatePrefixVariableName(optionHelper.getVariablePrefixes().getArgsPrefix(),
                        null, argsConfig.getName());
            }
            if (argsConfig.getValueType() == null) {
                argsConfig.setValueType(new StringType());
            }
            String lineDecl = null;
//            ListType listType = null;
            ValueType valueType = argsConfig.getValueType();
//            if (valueType instanceof ListType) {
//                listType = (ListType) valueType;
//                if (listType.getElementValueType() != null) {
//                    valueType = ValueTypeFactory.getInstance().create(listType.getValueTypeName());
//                } else {
//                    valueType = new StringType();
//                }
//            } else {
//                listType = new ListType();
//            }
            //listType.setProperties(ListType.LIST_VALUE_TYPE + "=" + valueType.getValueTypeName());
            lineDecl = getArgsListDeclarationLine(null, valueType, variableName) + " = new ArrayList<>()";
            variableNames.put(variableName, valueType.getJavaClassName());
            if (lineDecl.contains(new ListType().getJavaClassName()
                    + "<" + new ListType().getJavaClassName() + "<")) {
                listListVariables.put(variableName, (ListType) valueType);
            } else {
                ListType listType = new ListType();
                listType.setProperties(ListType.LIST_VALUE_TYPE
                        + " = " + valueType.getValueTypeName());
                ValueTypeFactory.getInstance().create(listType.getValueTypeName());
                listVariables.put(variableName, listType);
            }
            String listElementPackageName = String.class.getPackageName();
            String listElementClassName = String.class.getSimpleName();
//            if (listType.getElementValueType() != null) {
//                listElementPackageName = valueType.getPackageName();
//                listElementClassName = valueType.getJavaClassName();
//            }
            imports.add(valueType.getPackageName()
                    + "." + valueType.getJavaClassName());
            imports.add(java.util.ArrayList.class.getName());
            imports.add(java.util.List.class.getName());
            if (listElementPackageName != null) {
                // caters for types of arrays that do not specifiy a
                // package name, for example DataFileType
                imports.add(listElementPackageName
                        + "." + listElementClassName);
            }
            declarations.add(lineDecl);
        }
    }

    /**
     *
     * @param prefix
     * @param index
     * @param name
     * @return
     */
    private String generatePrefixVariableName(String prefix, Integer index, String name) {
        String indexVal = "";
        if (index != null) {
            indexVal = Integer.toString(index);
        }
        return CaseUtils.toCamelCase(prefix + indexVal + "_"
                + name.replace("-", "_"),
                false, new char[]{'_'});
    }

    /**
     *
     * @param name
     * @return
     */
    private String getVariableName(String name) {
        return CaseUtils.toCamelCase(name.replace("-", "_"), false, new char[]{'_'});
    }

    /**
     *
     * @param name
     * @return
     */
    private String getCommandName(String name) {
        return name.replace("-", "_");
    }

    /**
     *
     * @param commandName
     * @return
     */
    private String generateCommandVariableName(String commandName) {
        String[] segments = commandName.split("/");
        String[] newSegments = null;
        int index = 0;
        if (!"".equals(optionHelper.getVariablePrefixes().getCommandPrefix())) {
            // make space for the command prefix
            newSegments = new String[segments.length + 1];
            newSegments[0] = optionHelper.getVariablePrefixes().getCommandPrefix();
            index = 1;
        } else {
            newSegments = new String[segments.length];
        }
        for (int i = index; i < newSegments.length; i++) {
            newSegments[i] = getCommandName(segments[i - index]);
        }
        return String.join("_", newSegments) + "_";
    }

    /**
     * Get the member declaration for a standard (non-list) variable.
     *
     * @param className class name of the variable; may be {@code null}, in
     * which case the primitive name must be present.
     *
     * @param primitiveName Java primitive type name; may be {@code null}, in
     * which case the class name must be present.
     *
     * @param variableName non-{@code null} variable name of the member
     * variable.
     *
     * @return non-{@code null} {@code private} member declaration.
     */
    private String getDeclarationLine(String className, String primitiveName,
            String variableName) {
        String type = className;
        if (className == null) {
            type = primitiveName;
        }
        return generateIndent(1) + "private " + type + " " + variableName;
    }

    /**
     * Get the member declaration line for a list variable.
     *
     * @param listType non-{@code null} list type.
     *
     * @param variableName non-{@code null} list variable name.
     *
     * @return non-{@code null} member declaration for a list type.
     */
    private String getListDeclarationLine(ValueType valueType, String variableName) {
        ValueType listValueType = new StringType();
        String line = null;
        if (valueType instanceof ListType) {
            ListType listType = (ListType) valueType;
            if (listType.getElementValueType() != null) {
                listValueType = listType.getElementValueType();
            }
            String className = listValueType.getJavaClassName();
            String importName = listValueType.getPackageName() + "." + listValueType.getJavaClassName();
            if (!imports.contains(importName)) {
                imports.add(importName);
            }
            if (listType.getElementValueType() != null) {
                className = listType.getElementValueType().getJavaClassName();
            }
            line = generateIndent(1) + "private " + listType.getJavaClassName()
                    + "<" + className + ">" + " " + variableName;
        }
        return line;
    }

    /**
     * Get the member declaration line for a list variable for an argument
     * configuration.
     *
     * @param length argument configuration length; {@code null} for unbounded
     * argument configurations.
     *
     * @param valueType non-{@code null} value type.
     *
     * @param variableName non-{@code null} list variable name.
     *
     * @return non-{@code null} member declaration for a list type; if the
     * length is non-{@code null}, the declaration will be a list of the given
     * value type; if {@code null} (an unbounded argument configuration) a list
     * of lists of the specified type will be the declaration.
     */
    private String getArgsListDeclarationLine(Integer length, ValueType valueType,
            String variableName) {
        ValueType listValueType = new StringType();
        String className = valueType.getJavaClassName();
        String line = null;
        if (valueType instanceof ListType) {
            ListType listType = (ListType) valueType;
            if (listType.getElementValueType() != null) {
                listValueType = listType.getElementValueType();
                className = listType.getElementValueType().getJavaClassName();
            } else {
                className = String.class.getSimpleName();
            }
            String importName = listValueType.getPackageName() + "."
                    + listValueType.getJavaClassName();
            if (!imports.contains(importName)) {
                imports.add(importName);
            }
            if (length == null) {
                line = generateIndent(1) + "private "
                        + new ListType().getJavaClassName()
                        + "<" + new ListType().getJavaClassName()
                        + "<" + className + ">>" + " " + variableName;
            } else {
                line = generateIndent(1) + "private "
                        + new ListType().getJavaClassName()
                        + "<" + className + ">" + " " + variableName;
            }
        } else {
            line = generateIndent(1) + "private "
                    + new ListType().getJavaClassName()
                    + "<" + className + ">" + " " + variableName;
        }
        return line;
    }

    /**
     *
     * @param className
     * @return
     */
    private String generateClassStart(String className) {
        // imeplementations will get replaced with correct values prior to output:
        return "public class " + className + " implements " + IMPLEMENTATIONS + " {\n";
    }

    /**
     *
     * @return
     */
    private String generateClassEnd() {
        return "}\n";
    }

    /**
     *
     * @param sb
     */
    private void generateCommandSwitchMethod(StringBuilder sb) {
        sb.append(generateMethodCommandStart(!config.getCommandRoot().getCommands().isEmpty()));
        if (config.getCommandRoot().getCommands().isEmpty()) {
            sb.append(generateIndent(2)).append("throw new UnsupportedOperationException(\"Not yet implemented\");\n");
        } else {
            // just a repeat of the above but with commands:
            sb.append(generateIndent(2)).append("switch(command) {\n");
            for (Command command : config.getCommandRoot().getCommands()) {
                sb.append(generateIndent(3)).append("case \"")
                        .append(command.getPath())
                        .append("\": \n");
                sb.append(generateIndent(4)).append("switch(option) {\n");
                for (String option : command.getOptionConfigurations().keySet()) {
                    Map<String, OptionConfiguration> options = command.getOptionConfigurations();
                    OptionConfiguration optConfig = options.get(option);
                    String variableName = null;
                    String optionNameLong = null;
                    String optionNameShort = null;
                    if (null != config.getOptionsType()) {
                        switch (config.getOptionsType()) {
                            case SHORT:
                                variableName = generateCommandVariableName(command.getPath()) + getVariableName(optConfig.getShortOption());
                                optionNameShort = optConfig.getShortOption();
                                break;
                            case LONG:
                                variableName = generateCommandVariableName(command.getPath()) + getVariableName(optConfig.getLongOption());
                                optionNameLong = optConfig.getLongOption();
                                break;
                            case BOTH:
                                variableName = generateCommandVariableName(command.getPath()) + getVariableName(optConfig.getLongOption());
                                optionNameShort = optConfig.getShortOption();
                                optionNameLong = optConfig.getLongOption();
                                break;
                            default:
                                break;
                        }
                    }
                    variableName = getVariableName(variableName);
                    String optClassName = null;
                    String optPrimitiveName = null;
                    if (optConfig.getValueType() != null) {
                        optClassName = optConfig.getValueType().getJavaClassName();
                        optPrimitiveName = optConfig.getValueType().getJavaPrimitiveName();
                    }
                    // NB non-args options will return null and have no option value
                    // type, 
                    ListType listType = null;
                    if (listVariables.containsKey(variableName)) {
                        listType = listVariables.get(variableName);
                    }
                    sb.append(generateCaseBody(5, variableName, optionNameShort,
                            optionNameLong, optClassName, optPrimitiveName,
                            listType));
                }
                sb.append(generateIndent(4))
                        .append("}")
                        .append(System.lineSeparator());
                sb.append(generateIndent(4)).append("break;")
                        .append("\n");
            }
            sb.append(generateIndent(2))
                    .append("}")
                    .append(System.lineSeparator());
        }
        sb.append(generateMethodCommandEnd());
    }

    /**
     * Generate both the standard, top-level argument configurations as a single
     * method, followed by a command-based method of argument configurations. In
     * both cases, if the top-level arguments are not present or no argument
     * configurations are present for any commands, the generated method will be
     * a placeholder that throws an exception when called. Otherwise the
     * generated data will contain the switches necessary to determine when
     * given argument configurations are called from the underlying API.
     *
     * @param sb non-{@code null} string builder to append data to.
     */
    private void generateArgsSwitchMethod(StringBuilder sb) {
        sb.append(generateArgsConfigMethodStart());
        // just a repeat of the above but with commands:
        int currentPrefix = 0;
        sb.append(generateIndent(2)).append("switch(name) {\n");
        ArgsConfiguration last = null;
        for (String prefix : config.getArgsConfigurations().keySet()) {
            ArgsConfiguration argsConfig = config.getArgsConfigurations().get(prefix);
            if (argsConfig.getLength() == 0) {
                // must be the last one; don't generate a switch, it doesn't need one:
                break;
            }
            sb.append(generateIndent(3)).append("case \"").append(argsConfig.getName()).append("\":\n");
//            if (last != null && argsConfig.getLength() != null) {
//                currentPrefix = argsConfig.getLength() - 1;
//            }
            if (argsConfig.getLength() != null) {
                sb.append(generateIndexedCaseBody(4, currentPrefix, null,
                        argsConfig, optionHelper.getVariablePrefixes().getArgsPrefix()));
                currentPrefix += argsConfig.getLength();
            } else {
                // extra check, if argsConfig.getLength == null, then is a list type
                sb.append(generateIndent(4));
                String variableName = generatePrefixVariableName(
                        optionHelper.getVariablePrefixes().getArgsPrefix(),
                        null, argsConfig.getName());
                String typeName = argsConfig.getValueType().getJavaClassName();
                if (argsConfig.getValueType().getJavaPrimitiveName() != null) {
                    typeName = argsConfig.getValueType().getJavaPrimitiveName();
                }
                sb.append(variableName)
                        .append(".add((")
                        .append(typeName)
                        .append(") value);")
                        .append(System.lineSeparator());
            }
            sb.append(generateIndent(3)).append("break;\n");
            last = argsConfig;
        }
        sb.append(generateIndent(2)).append("}\n");
        sb.append(generateArgsConfigMethodEnd());
        sb.append(System.lineSeparator());
        boolean commandsHaveArgsConfigs = false;
        for (Command command : config.getCommandRoot().getCommands()) {
            if (command.hasArgsConfigurations()) {
                commandsHaveArgsConfigs = true;
                break;
            }
        }
        if (commandsHaveArgsConfigs) {
            sb.append(generateArgsConfigCommandMethodStart());
            currentPrefix = 0;
            generateCommandArgsConfigurationData(sb, currentPrefix);
            sb.append(generateArgsConfigMethodEnd()).append(System.lineSeparator());
        }
    }

    /**
     *
     * @return
     */
    private String generateMethodStart() {
        String override = generateIndent(1) + "@Override\n";
        return override + generateIndent(1) + "public void option(String option, Object value) {\n";
    }

    /**
     *
     * @return
     */
    private String generateMethodEnd() {
        return "\n " + generateIndent(1) + "}\n";
    }

    /**
     *
     * @return
     */
    private String generateArgsConfigMethodStart() {
        String override = generateIndent(1) + "@Override\n";
        return override + generateIndent(1) + "public void argument(String name, int index, Object value) {\n";
    }

    /**
     *
     * @return
     */
    private String generateArgsConfigCommandMethodStart() {
        String override = generateIndent(1) + "@Override\n";
        return override + generateIndent(1) + "public void argument(String command, String name, int index, Object value) {\n";
    }

    /**
     *
     * @return
     */
    private String generateArgsConfigMethodEnd() {
        return "\n" + generateIndent(1) + "}\n";
    }

    /**
     * Generate the method body start for the method for command options.
     *
     * @param initialiseCommand {@code true} to override the default named
     * command variable name; {@code false} will leave the default variable name
     * as-is.
     *
     * @return non-{@code null} command method header.
     */
    private String generateMethodCommandStart(boolean initialiseCommand) {
        String override = generateIndent(1) + "@Override\n";
        if (initialiseCommand) {
            return override + generateIndent(1) + "public void option(String command, String option, Object value) {\n"
                    + "\n" + generateIndent(2) + "this." + optionHelper.getCommandName() + " = command;\n";
        } else {
            return override + generateIndent(1) + "public void option(String command, String option, Object value) {\n";
        }
    }

    /**
     * Generate the brace for the end of the command.
     *
     * @return non-{@code null} end command brace.
     */
    private String generateMethodCommandEnd() {
        return generateIndent(1) + "}\n\n";
    }

    /**
     * Generate the case body for the given option and optiona values.
     *
     * @param indent indent spaces to apply when generating code.
     *
     * @param variableName non-{@code null} variable name.
     *
     * @param optionNameShort short option name; may be {@code null}, in which
     * case the long option name must be present.
     *
     * @param optionNameLong long option name; may be {@code null}, in which
     * case the short option name must be present.
     *
     * @param className class name of the variable; may be null, in which case
     * the primitive name must be present.
     *
     * @param primitiveName primitive name of the variable; may be {@code null},
     * in which case the class name must be present.
     *
     * @param listType list type, if the variable is a list type; may be
     * {@code null}.
     *
     * @return non-{@code null} source code for the given variables as a case
     * statement.
     */
    private String generateCaseBody(int indent, String variableName,
            String optionNameShort, String optionNameLong, String className,
            String primitiveName, ListType listType) {
        StringBuilder sb = new StringBuilder();
        Boolean isHelp = null;
        if (config.getHelpOptionShort() != null
                && config.getHelpOptionShort().equals(optionNameShort)) {
            isHelp = true;
        } else if (config.getHelpOptionLong() != null
                && config.getHelpOptionLong().equals(optionNameLong)) {
            isHelp = true;
        } else if (className == null) {
            isHelp = false;
        }
        Boolean isVersion = null;
        if (config.getVersionOptionShort() != null
                && config.getVersionOptionShort().equals(optionNameShort)) {
            isVersion = true;
        } else if (config.getVersionOptionLong() != null
                && config.getVersionOptionLong().equals(optionNameLong)) {
            isVersion = true;
        } else if (className == null) {
            isVersion = false;
        }
        if (optionNameShort != null) {
            sb.append(generateIndent(indent)).append("case \"")
                    .append(optionNameShort).append("\":\n");
        }
        if (optionNameLong != null) {
            sb.append(generateIndent(indent)).append("case \"")
                    .append(optionNameLong).append("\":\n");
        }
        String typeName = className;
        if (primitiveName != null) {
            typeName = primitiveName;
        }
        if (className != null || (!isHelp && !isVersion)) {
            if (listType == null) {
                if (isHelp == null && isVersion == null) {
                    sb.append(generateIndent(indent + 1))
                            .append(variableName)
                            .append(" = (")
                            .append(typeName)
                            .append(") value;")
                            .append(System.lineSeparator());
                } else {
                    sb.append(generateIndent(indent + 1))
                            .append(variableName)
                            .append(" = true;")
                            .append(System.lineSeparator());
                }
            } else {
                String listElementClassName = String.class.getSimpleName();
                if (listType.getElementValueType() != null) {
                    listElementClassName = listType.getElementValueType().getJavaClassName();
                }
                sb.append(generateIndent(indent + 1)).append(variableName)
                        .append(" = (")
                        .append(typeName).append("<")
                        .append(listElementClassName)
                        .append(">")
                        .append(") value;")
                        .append(System.lineSeparator());
            }
        } else {
            if (isHelp) {
                sb.append(generateIndent(indent + 1))
                        .append("System.exit(0);")
                        .append(System.lineSeparator());
            } else if (isVersion) {
                sb.append(generateIndent(indent + 1))
                        .append("System.exit(0);")
                        .append(System.lineSeparator());
            } else {
                // it's a non-help, no-args CLI option:
                sb.append(System.lineSeparator());
            }
        }
        if ((isHelp == null || !isHelp) && (isVersion == null || !isVersion)) {
            sb.append(generateIndent(indent + 1)).append("break;\n");
        }
        return sb.toString();
    }

    /**
     *
     * @param indent
     * @param currentPrefix
     * @param argConfig
     * @param prefix
     * @return
     */
    private String generateIndexedCaseBody(int indent, int currentPrefix,
            Command command, AbstractArgsConfiguration argConfig, String prefix) {
        StringBuilder sb = new StringBuilder();
        sb.append(generateIndent(indent)).append("switch(index) {\n");
        if (argConfig.getLength() != null) {
            for (int i = 0; i < argConfig.getLength(); i++) {
                String variableName = generatePrefixVariableName(prefix,
                        currentPrefix + i, argConfig.getName());
                if (command != null) {
                    variableName = generatePrefixVariableName(prefix + "_" + command.getPath(),
                            currentPrefix + i, argConfig.getName());
                }
                String className = null;
                String primitiveName = null;
                if (argConfig.getValueType() != null) {
                    className = argConfig.getValueType().getJavaClassName();
                    primitiveName = argConfig.getValueType().getJavaPrimitiveName();
                }
                ListType listType = null;
                if (listVariables.containsKey(variableName)) {
                    listType = listVariables.get(variableName);
                }
                sb.append(generateIndent(indent + 1))
                        .append("case ")
                        .append(i + currentPrefix).append(":\n");
                String typeName = className;
                if (primitiveName != null) {
                    typeName = primitiveName;
                }
                if (className != null) {
                    if (listType == null) {
                        sb.append(generateIndent(indent + 2))
                                .append(variableName)
                                .append(" = (")
                                .append(typeName)
                                .append(") value;")
                                .append(System.lineSeparator());
                    } else {
                        // leaving this code here for now; currently this will
                        // never get hit since the API does not allow argument
                        // configurations to be a list type (yet):
                        String listElementClassName = String.class.getSimpleName();
                        if (listType.getElementValueType() != null) {
                            listElementClassName = listType.getElementValueType().getJavaClassName();
                        }
                        sb.append(generateIndent(indent + 2))
                                .append(variableName)
                                .append(" = (")
                                .append(typeName).append("<")
                                .append(listElementClassName)
                                .append(">")
                                .append(") value;")
                                .append(System.lineSeparator());
                    }
                } else {
                    // it's a non-help, no-args CLI option:
                    sb.append(System.lineSeparator());
                    // it's a non-help, no-args CLI option:
                    sb.append(System.lineSeparator());
                }
                sb.append(generateIndent(indent + 2)).append("break;\n");
            }
            sb.append(generateIndent(indent + 1)).append("}\n");
        } else {
            // TODO remove variable and add to list-based variables
            // if not a list, turn it into one:
            String variableName = null;
            if (command != null) {
                variableName = generatePrefixVariableName(
                        optionHelper.getVariablePrefixes().getCommandArgsPrefix() + command.getPath(),
                        null, argConfig.getName());
            } else {
                variableName = generatePrefixVariableName(
                        optionHelper.getVariablePrefixes().getArgsPrefix(),
                        null, argConfig.getName());
            }
            if (argConfig.getValueType() == null) {
                argConfig.setValueType(new StringType());
            }
            ListType listType = new ListType();
            ValueType valueType = argConfig.getValueType();
            if (valueType == null) {
                valueType = new StringType();
            }
            listType.setProperties(ListType.LIST_VALUE_TYPE + "=" + valueType.getValueTypeName());
            String lineDecl = getArgsListDeclarationLine(null, listType, variableName) + " = new ArrayList<>()";
////            if (valueType.getJavaClassName() != null) {
//            // e.g. optToDoSomething, java.lang.String
//            variableNames.put(variableName, listType.getJavaClassName());
////            } else {
////                // e.g. optToDoSomething, int
////                variableNames.put(variableName, valueType.getJavaPrimitiveName());
////            }
//            listVariables.put(variableName, listType);
//            System.out.println("Put: " + variableName);
            String listElementPackageName = String.class.getPackageName();
            String listElementClassName = String.class.getSimpleName();
            if (listType.getElementValueType() != null) {
                listElementPackageName = listType.getElementValueType().getPackageName();
                listElementClassName = listType.getElementValueType().getJavaClassName();
            }
            if (listElementPackageName != null) {
                // caters for types of arrays that do not specifiy a
                // package name, for example DataFileType
                imports.add(listElementPackageName
                        + "." + listElementClassName);
            }
            declarations.add(lineDecl);
        }
        return sb.toString();
    }

    /**
     * Generate getters for the give variables and list-based variables.
     *
     * @param variables non-{@code null} map of variables; the key will be the
     * name of the variable, the value will be the Java type (class or
     * primitive) of the variable.
     *
     * @param listVariables non-{@code null} list of list-based variables; the
     * key will be the list variable name, the value to list {@link ValueType}.
     * May be empty.
     *
     * @return non-{@code null} string containing public getteres.
     */
    private String generateGetters(Map<String, String> variables,
            Map<String, ListType> listVariables) {
        StringBuilder sb = new StringBuilder();
        for (String varName : variables.keySet()) {
            String type = variables.get(varName);
            if (listVariables.containsKey(varName)) {
                ListType listType = listVariables.get(varName);
                String listElementClassName = String.class.getSimpleName();
                if (listType.getElementValueType() != null) {
                    listElementClassName = listType.getElementValueType().getJavaClassName();
                }
                type = "List<" + listElementClassName + ">";
            } else if (listListVariables.containsKey(varName)) {
                ListType listType = listListVariables.get(varName);
                String listElementClassName = String.class.getSimpleName();
                if (listType.getElementValueType() != null) {
                    listElementClassName = listType.getElementValueType().getJavaClassName();
                }
                type = new ListType().getJavaClassName() + "<" + type + "<" + listElementClassName + ">>";
            }
            String getter = "get";
            if (BooleanType.BOOLEAN.equals(type)) {
                getter = "is";
            }
            String methodName = getter + varName.substring(0, 1).toUpperCase() + varName.substring(1);
            sb.append(generateIndent(1))
                    .append("public ")
                    .append(type)
                    .append(" ")
                    .append(methodName)
                    .append("() {")
                    .append(System.lineSeparator());
            sb.append(generateIndent(2))
                    .append("return ")
                    .append(varName)
                    .append(";")
                    .append(System.lineSeparator());
            sb.append(generateIndent(1))
                    .append("}")
                    .append(System.lineSeparator())
                    .append(System.lineSeparator());
        }
        return sb.toString();
    }

    /**
     * Get header data, consisting of:
     *
     * <p>
     * <ul>
     * <li>Application name and date generated; and</li>
     * <li>Options used with values (for binary switches) used</li>
     * </ul>
     *
     * @param data non-{@code null} CLC data.
     *
     * @return non-{@code null} data with the header prepended.
     */
    private String getHeader() throws IOException {
        StringBuilder sb = new StringBuilder();
        Enumeration<URL> resources = getClass().getClassLoader()
                .getResources("META-INF/MANIFEST.MF");
        String appName = null;
        String version = null;
        while (resources.hasMoreElements()) {
            Manifest manifest = new Manifest(resources.nextElement().openStream());
            Attributes attr = manifest.getMainAttributes();
            if (attr.getValue("app-name") != null) {
                appName = attr.getValue("app-name");
            }
            if (attr.getValue("Implementation-Version") != null) {
                version = attr.getValue("Implementation-Version");
            }
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        sb.append("/**")
                .append(System.lineSeparator())
                .append(" * Generated by {@code ")
                .append(appName)
                .append("} version ")
                .append(version)
                .append(", ")
                .append(sdf.format(new Date()))
                .append(System.lineSeparator())
                .append(" * <p>")
                .append(System.lineSeparator());
        List<Option> options = Clc.getInstance().getOptions();
        sb.append(" * Options:").append(System.lineSeparator())
                .append(" * <p>");
        for (Option option : options) {
            String value = option.getValue();
            sb.append(System.lineSeparator())
                    .append(" * <p>")
                    .append(System.lineSeparator())
                    .append(" * {@code -")
                    .append(option.getOpt())
                    .append("/--")
                    .append(option.getLongOpt());
            if (value != null) {
                sb.append(" ")
                        .append(option.getValue())
                        .append("}");
            } else {
                sb.append("}");
            }
        }
        sb.append(System.lineSeparator())
                .append(" */")
                .append(System.lineSeparator());
        return sb.toString();
    }
}
