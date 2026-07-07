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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.statefive.clic.Command;
import org.statefive.clic.Clc;
import org.statefive.clic.CommandRoot;
import org.statefive.clic.ClcException;
import org.statefive.clic.GlobalConfiguration;
import static org.statefive.clic.GlobalConfiguration.GLOBAL_HELP_OPTION_NAME;
import static org.statefive.clic.GlobalConfiguration.GLOBAL_HELP_SWITCH_OPTS;

/**
 * Builder to generate properties files to to override properties with command
 * line arguments.
 *
 * @author rich
 *
 * @param <P> Properties implementation.
 */
public abstract class AbstractPropertiesBuilder<P> implements PropertiesBuilder<P> {

    /**
     * Prefix for command usage, if present; this is prefixed on to the commands
     * full path and used to replace the usage for commands once all commands
     * have been processed.
     */
    static final String COMMAND_USAGE_PREFIX = "__COMMAND_USAGE_";

    /**
     * Generator for configurations based on CLC - command line configuration
     * format.
     */
    protected ClcGenerator configurationGenerator;

    /**
     * Root command if present.
     */
    protected CommandRoot commandRoot;

    /**
     * Determine if duplicate properties are allowed.
     */
    private boolean duplicatesAllowed;

    /**
     * Pad configuration data with comment-prefixed entries that have not been
     * defined for a given option configuration.
     */
    private boolean pad;

    /**
     * Set default values to the value of the property.
     */
    private boolean insertDefaults;

    /**
     * Command keys used to check for duplicates.
     */
    private final Set<String> optionNames = new HashSet<>();

    /**
     * Configuration input stream.
     */
    private InputStream configInputStream;

    /**
     * When generating blocks of properties from different files, prepend any
     * option section with the name of the file where the properties came from.
     */
    protected boolean isShowImportOrigin = false;

    /**
     * Determine if to convert underlying property values to an appropriate
     * value type.
     */
    protected TypeInferralConfig typeInferralConfig;

    /**
     * Property name filter.
     */
    private PropertyNameFilter filter;
    
    /**
     * Property key for versioning.
     * 
     * @since 1.1
     */
    private String propertyVersion;

    /**
     * Builds up information from the CLC (command line configuration) format.
     */
    protected final StringBuilder configurationData = new StringBuilder();

    /**
     * Map to keep track of sources to files the streams were generated from
     * (used to include file origin for each block of imported properties when
     * the correct option is set).
     */
    protected final Map<PropertiesSource, File> sourcesMap = new HashMap<>();

    /**
     * List of properties sources.
     */
    protected final List<PropertiesSource> propertiesSources = new ArrayList<>();

    /**
     * Adds the builder as an option listener to the {@link Clc}.
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public AbstractPropertiesBuilder() {
        Clc.getInstance().addOptionListener(this);
        PropertiesListenerBindings.getInstance().setClc(Clc.getInstance());
    }

    /**
     * Add the specified properties source.
     *
     * @param source non-{@code null} properties source.
     *
     * @return this.
     */
    @Override
    public PropertiesBuilder addPropertiesSource(PropertiesSource source) {
        if (source instanceof PropertiesFileSource) {
            // update the sources map of the given file:
            PropertiesFileSource fileSource = (PropertiesFileSource) source;
            sourcesMap.put(source, fileSource.getSource());
        } else if (source instanceof PropertiesCommandSource) {
            PropertiesCommandSource pcs = (PropertiesCommandSource) source;
            if (pcs.getSource() instanceof PropertiesFileSource) {
                PropertiesFileSource pfs = (PropertiesFileSource) pcs.getSource();
                sourcesMap.put(pfs, pfs.getSource());
            }
        }
        this.propertiesSources.add(source);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertiesBuilder withClc(File configuration) throws IOException {
        if (this.configInputStream != null) {
            throw new IllegalArgumentException("Configuration already set.");
        }
        FileInputStream fis = new FileInputStream(configuration);
        this.configInputStream = fis;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertiesBuilder withClc(InputStream is) {
        if (configInputStream != null) {
            throw new IllegalArgumentException("Configuration already set.");
        }
        this.configInputStream = is;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertiesBuilder withPropertyNameFilter(PropertyNameFilter filter) {
        this.filter = filter;
        return this;
    }

    /**
     * {@inheritDoc}
     * 
     * @since 1.1
     */
    @Override
    public PropertiesBuilder withVersion() {
        propertyVersion = GlobalConfiguration.GLOBAL_VERSION_OPTION_LONG_DEFAULT;
        return this;
    }

    /**
     * {@inheritDoc}
     * 
     * @since 1.1
     */
    @Override
    public PropertiesBuilder withVersion(String propertyVersionKey) {
        propertyVersion = propertyVersionKey;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertiesBuilder allowDuplicates(boolean allowDuplicates) {
        this.duplicatesAllowed = allowDuplicates;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertiesBuilder showImportOrigin(boolean isShowImportOrigin) {
        this.isShowImportOrigin = isShowImportOrigin;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertiesBuilder pad(boolean pad) {
        this.pad = pad;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertiesBuilder insertDefaults(boolean insertDefaults) {
        this.insertDefaults = insertDefaults;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertiesBuilder withTypeInferralConfig(TypeInferralConfig typeInferralConfig) {
        this.typeInferralConfig = typeInferralConfig;
        return this;
    }

    /**
     * Receive an option update. It is up to implementations to map the
     * specified option name to the equivalent property and set the given value
     * on it.
     *
     * @param option non-{@code null} option; this will be the
     * {@code [option.name]} part of an {@code option.[option-name]}
     * declaration.
     *
     * @param value non-{@code null} value of the option.
     */
    @Override
    public void option(String option, Object value) {
        setProperty(option, value);
    }

    /**
     * Check whether global help or version is defined; if they are, parsing of
     * arguments will be set to {@code false}.
     * 
     * @param optionName option name; may be {@code null}.
     * 
     * @param propertyValue property value; may be {@code null}.
     */
    @Override
    public void setProperty(String optionName, Object propertyValue) {
        if (optionName != null && propertyValue == null) {
            // property value is null for unary arguments; so it might be a
            // standard unary option OR a global help/version option; so check:
            Map<String, String> clcMappings = configurationGenerator.getClcMappings();
            if (optionName.equals(clcMappings.get(
                    GlobalConfiguration.GLOBAL_HELP_SWITCH_OPTS))) {
                Clc.getInstance().setParseArgs(false);
            } else if  (optionName.equals(clcMappings.get(
                    GlobalConfiguration.GLOBAL_VERSION_SWITCH_OPTS))) {
                Clc.getInstance().setParseArgs(false);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDuplicatesAllowed() {
        return duplicatesAllowed;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPad() {
        return pad;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isInsertDefaults() {
        return insertDefaults;
    }

    /**
     * Get the configuration stream.
     *
     * @return the configuration stream if there is one; {@code null} otherwise.
     */
    public InputStream getConfigInputStream() {
        return configInputStream;
    }

    /**
     * Get the filter.
     *
     * @return the filter if there is one; {@code null} otherwise.
     */
    public PropertyNameFilter getFilter() {
        return filter;
    }

    /**
     * Get the list of properties sources.
     *
     * @return non-{@code null} properties sources.
     */
    public List<PropertiesSource> getPropertiesSources() {
        return propertiesSources;
    }

    /**
     * Get the set of option names.
     *
     * @return non-{@code null} option names.
     */
    public Set<String> getOptionNames() {
        return optionNames;
    }

    /**
     * Get the named property representing the version. The given property once
     * transformed to a command line switch will undergo the same transformation
     * as all other properties.
     * 
     * @return the property key value of the version, if present; {@code null}
     * otherwise.
     * 
     * @since 1.1
     */
    public String getPropertyVersion() {
        return propertyVersion;
    }

    /**
     * Process the given commands and add default usage values; if global help
     * has been defined, help will be added to every command. The result will be
     * appended to the generated configuration data.
     *
     * @param commandRoot non-{@code null} command root to process.
     */
    public void processCommands(CommandRoot commandRoot) {
        for (Command command : commandRoot.getCommands()) {
            generateCommandUsage(command);
        }
    }

    /**
     * Process the given command and add command name and usage to the generated
     * configuration data.
     *
     * @param command non-{@code null} command to process.
     */
    protected void generateCommandUsage(Command command) {
        StringBuilder sb = new StringBuilder();
        sb.append("Usage: ")
                .append(command.getName());
        List<Command> children = command.getChildren();
        if (!children.isEmpty()) {
            if (children.size() == 1) {
                sb.append(" ").append(children.get(0).getName());
            } else {
                sb.append(" <");
                for (int i = 0; i < children.size(); i++) {
                    sb.append(children.get(i).getName());
                    if (i < children.size() - 1) {
                        sb.append(" | ");
                    }
                }
                sb.append(">");
            }
            sb.append(" <options>");
        } else {
            sb.append(" <options>");
        }
        Map<String, String> configMappings = configurationGenerator.getClcMappings();
        String helpCmdName = configMappings.get(GLOBAL_HELP_OPTION_NAME);
        String helpName = configMappings.get(GLOBAL_HELP_SWITCH_OPTS);
        sb.append(System.lineSeparator())
                .append(System.lineSeparator())
                .append("option.")
                .append(helpCmdName)
                .append(".opts = ")
                .append(helpName)
                .append(System.lineSeparator())
                .append("option.help.description = Print this help then exit.");
        // now replace the default string with the newly generated usage
        String toReplace = COMMAND_USAGE_PREFIX + command.getPath().toUpperCase();
        int start = configurationData.indexOf(toReplace);
        int end = start + toReplace.length();
        configurationData.replace(start, end, sb.toString());
    }

    /**
     * If the builder has been specified to add the import origin, add the path.
     *
     * @param source non-{@code null} properties source.
     */
    protected void addImportOrigin(PropertiesSource source) {
        PropertiesSource fileSource = source;
        if (source instanceof PropertiesCommandSource) {
            PropertiesCommandSource pcs = (PropertiesCommandSource) source;
            if (pcs.getSource() instanceof PropertiesFileSource) {
                fileSource = (PropertiesFileSource) pcs.getSource();
            }
        }
        if (isShowImportOrigin && sourcesMap.containsKey(fileSource)) {
            if (source instanceof PropertiesCommandSource) {
                PropertiesCommandSource pcs = (PropertiesCommandSource) source;
                configurationData.append("# Generated from: ")
                        .append(Paths.get(sourcesMap.get(fileSource)
                                .getAbsolutePath()).normalize())
                        .append(" for command ")
                        .append(pcs.getCommand())
                        .append(System.lineSeparator());
            } else if (source instanceof PropertiesSource) {
                configurationData.append("# Generated from: ")
                        .append(Paths.get(sourcesMap.get(fileSource)
                                .getAbsolutePath()).normalize())
                        .append(System.lineSeparator());
            }
            configurationData.append(System.lineSeparator());
        }
    }

    /**
     * Generate the start of a command with the property source data based on
     * the last command read by the API; the data will be appended to the
     * configuration data.
     *
     * @param pcs non-{@code null} property command source.
     *
     * @throws ClcException if the command cannot be parsed successfully.
     */
    protected void generateCommandStart(PropertiesCommandSource pcs)
            throws ClcException {
        if (commandRoot == null) {
            commandRoot = new CommandRoot();
        }
        commandRoot.parseCommands(pcs.getCommand());
        List<Command> cmds = commandRoot.getCommands();
        Command cmd = cmds.getLast();
        configurationData.append("command.name = ")
                .append(cmd.getPath())
                .append(System.lineSeparator())
                .append("command.usage = ")
                .append(COMMAND_USAGE_PREFIX)
                .append(cmd.getPath().toUpperCase())
                .append(System.lineSeparator())
                .append(System.lineSeparator());
    }

}
