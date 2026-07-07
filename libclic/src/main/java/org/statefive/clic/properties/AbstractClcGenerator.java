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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.commons.cli.help.HelpFormatter;
import org.statefive.clic.ClcParser;
import org.statefive.clic.GlobalConfiguration;
import org.statefive.clic.OptionsTypeEnum;
import org.statefive.clic.valuetype.BooleanType;
import org.statefive.clic.valuetype.ByteType;
import org.statefive.clic.valuetype.DoubleType;
import org.statefive.clic.valuetype.FloatingPointType;
import org.statefive.clic.valuetype.IntegralType;
import org.statefive.clic.valuetype.LongType;
import org.statefive.clic.valuetype.ShortType;
import org.statefive.clic.valuetype.ValueType;
import org.statefive.clic.valuetype.ValueTypeCreationException;
import org.statefive.clic.valuetype.ValueTypeFactory;

/**
 * Base class for generating <i>command line configuration</i> (CLC) format.
 *
 * <p>
 * If no CLC is supplied, defaults for options type (long) and help will be
 * added to the configuration.
 *
 * @author rich
 *
 * @param <P> Properties implementation.
 */
public abstract class AbstractClcGenerator<P> implements ClcGenerator<P> {

    /**
     * Default help string.
     */
    private static final String HELP_DEFAULT = "help";

    /**
     * Manifest implementation version.
     *
     * @since 1.1
     */
    private static final String MANIFEST_IMPLEMENTATION_VERSION = "${manifest:Implementation-Version}";

    /**
     * Map of converted property names; the key will be the command line switch
     * conversion of the key value read from the property file (without the
     * leading {@code --}), which in turn will be the value of the given key.
     * The value of the map will be the original property name value.
     *
     * <p>
     * Command line switches will have all characters that are not alphanumeric
     * and not a hyphen converted to hyphens.
     *
     * <p>
     * For example, if a property file has a property named
     * {@code server.primary_address} then the key will be
     * {@code server-primary-address} and the value in the map will be
     * {@code server.primary_address}.
     *
     * @see AbstractPropertiesReader#convertToOptionName(java.lang.String)
     */
    protected final Map<String, String> propertyMappings = new HashMap<>();

    /**
     * Configuration mappings (if supplied).
     */
    protected final Map<String, String> clcMappings = new HashMap<>();

    /**
     * Map of (original) property names to value types - that is, the names of
     * the properties from the original property file, NOT the option name names
     * that properties are mapped to.
     */
    protected final Map<String, ValueType> propertyValueTypes = new HashMap<>();

    /**
     * Used to determine if to attempt to coerce underlying property values to a
     * {@link ValueType}.
     */
    protected TypeInferralConfig typeInferralConfig = new TypeInferralConfig();

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getPropertyMappings() {
        return propertyMappings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getClcMappings() {
        return clcMappings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, ValueType> getPropertyValueTypes() {
        return propertyValueTypes;
    }

    /**
     * Generate a command line configuration from the given properties. The
     * generated data will include a help option with the help data displaying
     * what command line options override a given property along with the
     * default value of the property.
     *
     * <p>
     * The configuration can be used to override default values of both the
     * default generated global and option configurations.
     *
     * @param properties non-{@code null}, non-empty map of property keys to
     * property values.
     *
     * @param config non-{@code null} command line configuration overrides, used
     * to override default generated values; may be empty.
     *
     * @param propertyFilter filter to appy to either include or exclude given
     * properties from being generated for command line help; may be
     * {@code null}, in which case all properties will be included.
     *
     * @param clcGlobalHeader {@code true} to generate global/top-level CLC
     * (command line configuration) header (including) help data as well as the
     * options configuration; {@code false} to generate only the options
     * configuration.
     *
     * @param typeInferralConfig type inference configuration to use; may be
     * {@code null}.
     *
     * @param pad if {@code true} generate comment-prefixed (hash) entries for
     * all options that have not been used when generating each configuration
     * block.
     *
     * @param insertDefaults Generate a {@link ClcParser#DEFAULT} value based on
     * the read-in property value within the configuration.
     *
     * @return non-{@code null} command line configuration format.
     *
     * @throws IllegalArgumentException if:
     *
     * <p>
     * <ul>
     * <li>an overridden {@code hasArg} does not have the value
     * {@code true};</li>
     * <li>a {@link ClcParser#DEFAULT} value is supplied when
     * {@code insertDefaults} is {@code false};</li>
     * <li>the help options {@link ClcParser#OPTS} has been overridden but not
     * found against the global option value</li>;
     * <li>No properties were found (either because there were no properties or
     * because a filter caused no properties to be accepted.; or</li>
     * <li>The filter is non-{@code null} and contains invlid regular expression
     * patterns.</li>
     * </ul>
     */
    protected String generateConfiguration(Map<String, Object> properties,
            Map<String, String> config, PropertyNameFilter propertyFilter,
            boolean clcGlobalHeader, TypeInferralConfig typeInferralConfig,
            boolean pad, boolean insertDefaults)
            throws IllegalArgumentException {
        return this.generateConfiguration(properties, config, propertyFilter,
                clcGlobalHeader, typeInferralConfig, pad, insertDefaults,
                null);
    }

    /**
     * Generate a command line configuration from the given properties. The
     * generated data will include a help option with the help data displaying
     * what command line options override a given property along with the
     * default value of the property.
     *
     * <p>
     * The configuration can be used to override default values of both the
     * default generated global and option configurations.
     *
     * @param properties non-{@code null}, non-empty map of property keys to
     * property values.
     *
     * @param config non-{@code null} command line configuration overrides, used
     * to override default generated values; may be empty.
     *
     * @param propertyFilter filter to appy to either include or exclude given
     * properties from being generated for command line help; may be
     * {@code null}, in which case all properties will be included.
     *
     * @param clcGlobalHeader {@code true} to generate global/top-level CLC
     * (command line configuration) header (including) help data as well as the
     * options configuration; {@code false} to generate only the options
     * configuration.
     *
     * @param typeInferralConfig type inference configuration to use; may be
     * {@code null}.
     *
     * @param pad if {@code true} generate comment-prefixed (hash) entries for
     * all options that have not been used when generating each configuration
     * block.
     *
     * @param insertDefaults Generate a {@link ClcParser#DEFAULT} value based on
     * the read-in property value within the configuration.
     *
     * @param propertyVersion property version to use for versioning; may be
     * {@code null}, in which case versioning will not be added to the
     * application. If specified, if the specified property does not exist, the
     * application manifest implementation version will be used as the output
     * version for the application, once the property has been converted to the
     * appropriate command line switch; otherwise the value specified by the
     * property will be used as the version output when the version switch is
     * invoked. If the given property key and manifest implementation version is
     * not present, an error will be thrown.
     *
     * @return non-{@code null} command line configuration format.
     *
     * @throws IllegalArgumentException if:
     *
     * <p>
     * <ul>
     * <li>an overridden {@code hasArg} does not have the value
     * {@code true};</li>
     * <li>a {@link ClcParser#DEFAULT} value is supplied when
     * {@code insertDefaults} is {@code false};</li>
     * <li>the help options {@link ClcParser#OPTS} has been overridden but not
     * found against the global option value</li>;
     * <li>No properties were found (either because there were no properties or
     * because a filter caused no properties to be accepted);</li>
     * <li>The filter is non-{@code null} and contains invlid regular expression
     * patterns; or</li>
     * <li>Property versioning is specified but the given property is not
     * present in the properties or the manifest implementation version is not
     * present.</li>
     * </ul>
     *
     * @since 1.1
     */
    protected String generateConfiguration(Map<String, Object> properties,
            Map<String, String> config, PropertyNameFilter propertyFilter,
            boolean clcGlobalHeader, TypeInferralConfig typeInferralConfig,
            boolean pad, boolean insertDefaults, String propertyVersion)
            throws IllegalArgumentException {
        if (typeInferralConfig != null) {
            this.typeInferralConfig = typeInferralConfig;
        }
        for (String clcKey : config.keySet()) {
            clcMappings.put(clcKey, config.get(clcKey));
        }
        StringBuilder sb = new StringBuilder();
        if (clcGlobalHeader) {
            sb.append("# Global options").append(System.lineSeparator());
            // short options are not supported:
            if (!config.containsKey(GlobalConfiguration.GLOBAL_OPTIONS_OPTS_TYPE)) {
                sb.append(GlobalConfiguration.GLOBAL_OPTIONS_OPTS_TYPE)
                        .append(" = ")
                        .append(OptionsTypeEnum.LONG.getType())
                        .append(System.lineSeparator());
            } else {
                sb.append(GlobalConfiguration.GLOBAL_OPTIONS_OPTS_TYPE)
                        .append(" = ")
                        .append(config.get(GlobalConfiguration.GLOBAL_OPTIONS_OPTS_TYPE))
                        .append(System.lineSeparator());
            }
            sb.append(generateGlobalConfiguration(config, properties, propertyVersion));
        }
        // keep track of number of generated options:
        int includes = 0;
        // now generate the configuration that will be used to show command line
        // switches and the descriptions for each:
        for (String propertyName : properties.keySet()) {
            String valueStr = properties.get(propertyName).toString();
            Object value = properties.get(propertyName);
            if (!include(propertyFilter, propertyName)) {
                continue;
            }
            includes++;
            String optionName = AbstractPropertiesReader.convertToOptionName(propertyName);
            propertyMappings.put(optionName, propertyName);
            ValueType valueType = getPropertyValueType(propertyName, value);
            if (typeInferralConfig != null && typeInferralConfig.isInferTypes()) {
                // if valueType == null -> add string property value type?
                // else:
                propertyValueTypes.put(optionName, valueType);

            }
            // if there's a value type, use the type renderer for the value:
            if (valueType != null) {
                valueStr = valueType.render(properties.get(propertyName));
            }
            processOpts(sb, config, optionName);
            processDescription(sb, config, optionName, propertyName, valueStr);
            processHasArg(sb, config, optionName, valueStr);
            String optionHasArg = createOptionName(optionName, ClcParser.HAS_ARG);
            String hasArg = config.get(optionHasArg);
            if (ClcParser.TRUE.equals(hasArg.toLowerCase())) {
                processArgName(sb, config, optionName);
                processType(sb, config, optionName);
                processProperties(sb, config, optionName);
                String keyDefault = createOptionName(optionName, ClcParser.DEFAULT);
                if (!insertDefaults) {
                    if (config.containsKey(keyDefault)) {
                        throw new IllegalArgumentException("Configuration cannot"
                                + " contain a 'default' value.");
                    }
                } else if (!config.containsKey(keyDefault) && !"".equals(valueStr)) {
                    String optsKey = createOptionName(optionName, ClcParser.DEFAULT);
                    sb.append(optsKey)
                            .append(" = ")
                            .append(valueStr)
                            .append(System.lineSeparator());
                    config.put(optsKey, valueStr);
                }
            }
            if (pad) {
                padConfiguration(sb, config, optionName);
            }
            sb.append(System.lineSeparator());
        }
        sb.append(generateArgsConfigurations(config));
        if (includes == 0) {
            throw new IllegalArgumentException("Configuration not generated -"
                    + " bad filter or no properties?");
        }
        return sb.toString();
    }

    /**
     * Apply the given filter to determine if the given key should be accepted.
     *
     * @param propertyFilter filter to apply; may be {@code null} (in chich case
     * the key will be accepted.
     *
     * @param key non-{@code null} to check.
     *
     * @return {@code true} if an include filter has been applied and the key
     * matches the filter expression (or the include list is empty), or an
     * exclude filter is being used and doesn't match the expression;
     * {@code false} otherwise.
     *
     * @throws IllegalArgumentException if there are regular expression patterns
     * that have invlid syntax.
     */
    private boolean include(PropertyNameFilter propertyFilter, String key) {
        Boolean include = null;
        if (propertyFilter != null) {
            if (propertyFilter.isFitlerable()) {
                if (propertyFilter.isInclude()) {
                    include = false;
                    for (String regex : propertyFilter.getIncludes()) {
                        if (matches(regex, key)) {
                            include = true;
                            break;
                        }
                    }
                } else {
                    include = true;
                    for (String regex : propertyFilter.getExcludes()) {
                        if (matches(regex, key)) {
                            include = false;
                            break;
                        }
                    }
                }
            }
        }
        if (include == null) {
            // nothing was actually set, so include all:
            include = true;
        }
        return include;
    }

    /**
     * Determine if the given value matches the specified regular expression.
     *
     * @param regex non-{@code null} regular expression.
     *
     * @param value non-{@code null} value.
     *
     * @return {@code true} if the value matches; {@code false} otherwise.
     *
     * @throws IllegalArgumentException if the pattern cannot be compiled.
     */
    private boolean matches(String regex, String value)
            throws IllegalArgumentException {
        boolean matches = false;
        try {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(value);
            matches = m.matches();
        } catch (PatternSyntaxException ex) {
            throw new IllegalArgumentException("Bad regular expression: "
                    + regex, ex);
        }
        return matches;
    }

    /**
     * Generate a default help configuration, unless overridden by the specified
     * configuration.
     *
     * @param config non-{@code null} configuration to override any default help
     * options; may be empty.
     *
     * @param properties
     *
     * @param propertyVersionInfo
     *
     * @return non-{@code null} command line configuration for the help.
     *
     * @throws IllegalArgumentException if any help properties are not present.
     * 
     * @deprecated
     */
    String generateGlobalConfiguration(Map<String, String> config) {
        return this.generateGlobalConfiguration(config, null, null);
    }

    /**
     * Generate a default help configuration, unless overridden by the specified
     * configuration.
     *
     * @param config non-{@code null} configuration to override any default help
     * options; may be empty.
     *
     * @param properties
     *
     * @param propertyVersionInfo
     *
     * @return non-{@code null} command line configuration for the help.
     *
     * @throws IllegalArgumentException if any help properties are not present.
     * 
     * @since 1.1
     */
    String generateGlobalConfiguration(Map<String, String> config,
            Map<String, Object> properties, String propertyVersion) {
        StringBuilder sb = new StringBuilder();
        String helpOptionName = HELP_DEFAULT;
        String helpOptionNameOverride = processHelpCommandOptionName(sb, config);
        if (helpOptionNameOverride != null) {
            helpOptionName = helpOptionNameOverride;
        }
        processHelpCommandName(sb, config);
        processHelpCommandHeader(sb, config);
        processHelpCommandFooter(sb, config);
        processHelpSwitchOpts(sb, config);
        processHelpAutoUsage(sb, config);
        processHelpFormatColumnSpacing(sb, config);
        processHelpFormatLeftPad(sb, config);
        processHelpFormatWidth(sb, config);
        processHelpFormatWidthFromEnv(sb, config);
        processHelpSortOptions(sb, config);
        if (propertyVersion != null) {
            // needs to be added in before any non-global options are generated:
            addPropertyVersionInformation(sb, propertyVersion, properties);
        }
        // coment to separate global and standard options
        sb.append(System.lineSeparator())
                .append("# Options configuration:")
                .append(System.lineSeparator());
        processHelpOptionOpts(sb, config, helpOptionName);
        processHelpOptionDescription(sb, config, helpOptionName);
        processHelpKeyIgnoreCliArgs(sb, config, helpOptionName);
        return sb.append(System.lineSeparator()).toString();
    }

    /**
     * This currently DOES NOTHING other than return the empty string builder.
     *
     * <p>
     * That's because the configuration original comes from
     * {@code java.util.Properties} which are not stored in order. When the
     * configuration is added the properties are in the wrong order and the
     * generated configuration cannot be parsed.
     *
     * @param config non-{@code null} configuration.
     *
     * @return non-{@code null} empty string builder.
     */
    String generateArgsConfigurations(Map<String, String> config) {
        StringBuilder sb = new StringBuilder();
        for (String key : config.keySet()) {
            if (key.startsWith(ClcParser.ARGS)) {
                sb.append(key)
                        .append(" = ")
                        .append(config.get(key))
                        .append(System.lineSeparator());
            }
        }
        return sb.toString();
    }

    /**
     * Generate a {@link ClcParser#OPTS} value for the specified option name; if
     * there is no user-defined value present in the supplied configuration map,
     * a default value will be added to the given builder; otherwise the
     * user-defined option will be used.
     *
     * @param sb non-{@code null} builder to append to.
     *
     * @param config non-{@code null} configuration to check; may be empty.
     *
     * @param optionName non-{@code null} option name.
     */
    private void processOpts(StringBuilder sb, Map<String, String> config,
            String optionName) {
        String keyOpts = createOptionName(optionName, ClcParser.OPTS);
        if (!config.containsKey(keyOpts)) {
            sb.append(keyOpts).append(" = ")
                    .append(optionName)
                    .append(System.lineSeparator());
        } else {
            sb.append(keyOpts).append(" = ")
                    .append(config.get(keyOpts))
                    .append(System.lineSeparator());
        }
    }

    /**
     * Generate a {@link ClcParser#HAS_ARG} value for the specified option name;
     * if there is no user-defined value present in the supplied configuration
     * map, a default value will be added to the given builder; otherwise the
     * user-defined option will be used.
     *
     * <p>
     * Properties are <i>always</i> considered to have an argument by their very
     * definition; however, this can be overridden by specifying that the type
     * inference configuration treat {@code false} property values as unary
     * switches.
     *
     * @param sb non-{@code null} builder to append to.
     *
     * @param config non-{@code null} configuration to check; may be empty.
     *
     * @param value non-{@code null} property value.
     *
     * @param optionName non-{@code null} option name.
     */
    private void processHasArg(StringBuilder sb, Map<String, String> config,
            String optionName, String value) {
        String optionHasArg = createOptionName(optionName, ClcParser.HAS_ARG);
        if (!config.containsKey(optionHasArg)) {
            // properties always have 'hasArg' as true, since by definition a
            // property always comes in the form x=y, even if y is the empty
            // string:
            String hasArg = ClcParser.TRUE;
            // ... unless they've requested 'false' as unary switches
            // AND the property value itself is 'false':
            boolean unary = typeInferralConfig != null
                    && typeInferralConfig.isFalseAsUnarySwitch();
            if (unary && ClcParser.FALSE.equals(value.toLowerCase())) {
                hasArg = ClcParser.FALSE;
            }
            sb.append(createOptionName(optionName, ClcParser.HAS_ARG))
                    .append(" = ")
                    .append(hasArg)
                    .append(System.lineSeparator());
            config.put(optionHasArg, hasArg);
        } else {
            String hasArgValue = config.get(optionHasArg).toLowerCase();
            if (!ClcParser.TRUE.equals(hasArgValue)) {
                throw new IllegalArgumentException("Configuration "
                        + optionHasArg + " can only be 'true', use type inference"
                        + " to override false ss unary switches.");
            } else {
                sb.append(createOptionName(optionName, ClcParser.HAS_ARG))
                        .append(" = true")
                        .append(System.lineSeparator());
                config.put(optionHasArg, ClcParser.TRUE);
            }
        }
    }

    /**
     * Generate a {@link ClcParser#ARG_NAME} value for the specified command
     * key; if they key is present the user-defined option will be used.
     *
     * @param sb non-{@code null} builder to append to.
     *
     * @param config non-{@code null} configuration to check; may be empty.
     *
     * @param optionName non-{@code null} option name.
     */
    private void processArgName(StringBuilder sb, Map<String, String> config,
            String optionName) {
        String optionArgName = ClcParser.OPTION + "." + optionName
                + "." + ClcParser.ARG_NAME;
        if (config.containsKey(optionArgName)) {
            sb.append(createOptionName(optionName, ClcParser.ARG_NAME))
                    .append(" = ")
                    .append(config.get(optionArgName))
                    .append(System.lineSeparator());
        }
    }

    /**
     * Generate a {@link ClcParser#DESCRIPTION} value for the specified command
     * key; if there is no user-defined value present in the supplied
     * configuration map, a default value will be added to the given builder;
     * otherwise the user-defined option will be used.
     *
     * @param sb non-{@code null} builder to append to.
     *
     * @param config non-{@code null} configuration to check; may be empty.
     *
     * @param optionName non-{@code null} option name.
     */
    private void processDescription(StringBuilder sb,
            Map<String, String> config, String optionName, String key, String value) {
        String optionDescription = createOptionName(optionName, ClcParser.DESCRIPTION);
        if (!config.containsKey(optionDescription)) {
            sb.append(createOptionName(optionName, ClcParser.DESCRIPTION))
                    .append(" = Overrides property '")
                    .append(key)
                    .append("', default value '")
                    .append(value)
                    .append("'")
                    .append(System.lineSeparator());
        } else {
            sb.append(createOptionName(optionName, ClcParser.DESCRIPTION))
                    .append(" = ")
                    .append(config.get(optionDescription))
                    .append(System.lineSeparator());
        }
    }

    /**
     * Generate a {@link ClcParser#TYPE} value for the specified option name.
     * One of two methods will be used to determine if such a value will be
     * defined:
     *
     * <ul>
     * <li>if there is a user-defined value present in the supplied
     * configuration map of the form {@code option.<command-key>.type=<type>},
     * then that value will be used; or
     * </li>
     * <li>
     * if there is an associated property value type already assigned to the
     * given option name, that value is used. This can happen in any number of
     * ways depending on the implementation - for example a regular expression
     * could be applied to a property value to determine what its type is, or an
     * implementation may already have parsed types such that the API can map
     * that to an underlying equivalent {@link ValueType}.
     * </li>
     * </ul>
     *
     * @param sb non-{@code null} builder to append to.
     *
     * @param config non-{@code null} configuration to check; may be empty.
     *
     * @param optionName non-{@code null} option name.
     *
     * @throws ValueTypeCreationException if a value type is used that is not
     * registered.
     */
    private void processType(StringBuilder sb, Map<String, String> config,
            String optionName) throws ValueTypeCreationException {
        // determine if a value type has been assigned to the property, e,g.
        // an int or float:
        String optionType = createOptionName(optionName, ClcParser.TYPE);
        if (config.containsKey(optionType)) {
            // user defined; however, the key type must be a valid API key type:
            sb.append(optionType)
                    .append(" = ")
                    .append(config.get(optionType))
                    .append(System.lineSeparator());
            ValueType valueType = ValueTypeFactory.getInstance().create(config.get(optionType));
            propertyValueTypes.put(optionName, valueType);
        } else {
            // check property types
            if (propertyValueTypes.containsKey(optionName)) {
                // underlying value has been determined to be a valid type - find
                // out what it is:
                ValueType valueType = (ValueType) propertyValueTypes.get(optionName);
                String valueTypeName = null;
                if (valueType instanceof BooleanType) {
                    valueTypeName = BooleanType.BOOLEAN;
                } else if (valueType instanceof ByteType) {
                    valueTypeName = ByteType.BYTE;
                } else if (valueType instanceof DoubleType) {
                    valueTypeName = DoubleType.DOUBLE;
                } else if (valueType instanceof FloatingPointType) {
                    valueTypeName = FloatingPointType.FLOAT;
                } else if (valueType instanceof IntegralType) {
                    valueTypeName = IntegralType.INTEGRAL;
                } else if (valueType instanceof LongType) {
                    valueTypeName = LongType.LONG;
                } else if (valueType instanceof ShortType) {
                    valueTypeName = ShortType.SHORT;
                }
                if (valueTypeName != null) {
                    sb.append(optionType)
                            .append(" = ")
                            .append(valueTypeName)
                            .append(System.lineSeparator());
                    config.put(optionType, config.get(optionType));
                }
            }
        }
    }

    /**
     * Generate a {@link ClcParser#PROPERTIES} value for the specified command
     * key; if there is no user-defined value present in the supplied
     * configuration map, a default value will be added to the given builder;
     * otherwise the user-defined option will be used.
     *
     * @param sb non-{@code null} builder to append to.
     *
     * @param config non-{@code null} configuration to check; may be empty.
     *
     * @param optionName non-{@code null} option name.
     */
    private void processProperties(StringBuilder sb,
            Map<String, String> config, String optionName) {
        // determine if there are any value type properties for the given
        // property:
        String optionProperties = createOptionName(optionName,
                ClcParser.PROPERTIES);
        if (config.containsKey(optionProperties)) {
            sb.append(optionProperties)
                    .append(" = ")
                    .append(config.get(optionProperties))
                    .append(System.lineSeparator());
        }
    }

    /**
     * Generate a {@link GlobalConfiguration#GLOBAL_HELP_COMMAND_NAME} value; if
     * there is no user-defined value present in the supplied configuration map,
     * a default value will be added to the given builder, otherwise the
     * user-defined option will be used.
     *
     * @param sb non-{@code null} builder to append to.
     *
     * @param config non-{@code null} configuration to check; may be empty.
     *
     * @param optionName non-{@code null} option name.
     */
    private void processHelpCommandName(StringBuilder sb,
            Map<String, String> config) {
        if (!config.containsKey(GlobalConfiguration.GLOBAL_HELP_COMMAND_NAME)) {
            // add default
            sb.append(GlobalConfiguration.GLOBAL_HELP_COMMAND_NAME)
                    .append(" = ")
                    .append("Default generated property help.")
                    .append(System.lineSeparator());
            clcMappings.put(GlobalConfiguration.GLOBAL_HELP_COMMAND_NAME,
                    "Default generated property help.");
        } else {
            // user defined
            sb.append(GlobalConfiguration.GLOBAL_HELP_COMMAND_NAME)
                    .append(" = ")
                    .append(config.get(GlobalConfiguration.GLOBAL_HELP_COMMAND_NAME))
                    .append(System.lineSeparator());;
        }
    }

    /**
     * Generate a {@link GlobalConfiguration#GLOBAL_HELP_COMMAND_HEADER} value;
     * if there is no user-defined value present in the supplied configuration
     * map, a default value will be added to the given builder; otherwise the
     * user-defined option will be used.
     *
     * @param sb non-{@code null} builder to append to.
     *
     * @param config non-{@code null} configuration to check; may be empty.
     */
    private void processHelpCommandHeader(StringBuilder sb,
            Map<String, String> config) {
        if (!config.containsKey(GlobalConfiguration.GLOBAL_HELP_COMMAND_HEADER)) {
            // add default
            sb.append(GlobalConfiguration.GLOBAL_HELP_COMMAND_HEADER)
                    .append(" = ")
                    .append("Auto-generated content.")
                    .append(System.lineSeparator());
            clcMappings.put(GlobalConfiguration.GLOBAL_HELP_COMMAND_HEADER,
                    "Auto-generated content.");
        } else {
            // user defined
            sb.append(GlobalConfiguration.GLOBAL_HELP_COMMAND_HEADER)
                    .append(" = ")
                    .append(config.get(GlobalConfiguration.GLOBAL_HELP_COMMAND_HEADER))
                    .append(System.lineSeparator());;
        }
    }

    /**
     * Generate a {@link GlobalConfiguration#GLOBAL_HELP_COMMAND_FOOTER} value;
     * if there is no user-defined value present in the supplied configuration
     * map, a default value will be added to the given builder; otherwise the
     * user-defined option will be used.
     *
     * @param sb non-{@code null} builder to append to.
     *
     * @param config non-{@code null} configuration to check; may be empty.
     */
    private void processHelpCommandFooter(StringBuilder sb,
            Map<String, String> config) {
        if (!config.containsKey(GlobalConfiguration.GLOBAL_HELP_COMMAND_FOOTER)) {
            // add default
            sb.append(GlobalConfiguration.GLOBAL_HELP_COMMAND_FOOTER)
                    .append(" = ")
                    .append("End of auto-generated content.")
                    .append(System.lineSeparator());
            clcMappings.put(GlobalConfiguration.GLOBAL_HELP_COMMAND_FOOTER,
                    "End of auto-generated content.");
        } else {
            // user defined
            sb.append(GlobalConfiguration.GLOBAL_HELP_COMMAND_FOOTER)
                    .append(" = ")
                    .append(config.get(GlobalConfiguration.GLOBAL_HELP_COMMAND_FOOTER))
                    .append(System.lineSeparator());;
        }
    }

    /**
     * Generate a {@link GlobalConfiguration#GLOBAL_HELP_SWITCH_OPTS} value; if
     * there is no user-defined value present in the supplied configuration map,
     * a default value will be added to the given builder; otherwise the
     * user-defined option will be used.
     *
     * @param sb non-{@code null} builder to append to.
     *
     * @param config non-{@code null} configuration to check; may be empty.
     */
    private void processHelpSwitchOpts(StringBuilder sb,
            Map<String, String> config) {
        if (!config.containsKey(GlobalConfiguration.GLOBAL_HELP_SWITCH_OPTS)) {
            // add default
            sb.append(GlobalConfiguration.GLOBAL_HELP_SWITCH_OPTS)
                    .append(" = ")
                    .append(GlobalConfiguration.GLOBAL_HELP_OPTION_LONG_DEFAULT)
                    .append(System.lineSeparator());
            clcMappings.put(GlobalConfiguration.GLOBAL_HELP_SWITCH_OPTS,
                    GlobalConfiguration.GLOBAL_HELP_OPTION_LONG_DEFAULT);
        } else {
            // user defined
            sb.append(GlobalConfiguration.GLOBAL_HELP_SWITCH_OPTS)
                    .append(" = ")
                    .append(config.get(GlobalConfiguration.GLOBAL_HELP_SWITCH_OPTS))
                    .append(System.lineSeparator());;
        }
    }

    /**
     * Generate a {@link GlobalConfiguration#GLOBAL_HELP_AUTO_USAGE} value; if
     * there is no user-defined value present in the supplied configuration map,
     * a default value will be added to the given builder; otherwise the
     * user-defined option will be used.
     *
     * @param sb non-{@code null} builder to append to.
     *
     * @param config non-{@code null} configuration to check; may be empty.
     */
    private void processHelpAutoUsage(StringBuilder sb,
            Map<String, String> config) {
        if (!config.containsKey(GlobalConfiguration.GLOBAL_HELP_AUTO_USAGE)) {
            // add default
            sb.append(GlobalConfiguration.GLOBAL_HELP_AUTO_USAGE)
                    .append(" = ")
                    .append(ClcParser.FALSE)
                    .append(System.lineSeparator());
            clcMappings.put(GlobalConfiguration.GLOBAL_HELP_AUTO_USAGE,
                    ClcParser.FALSE);
        } else {
            // user defined
            sb.append(GlobalConfiguration.GLOBAL_HELP_AUTO_USAGE)
                    .append(" = ")
                    .append(config.get(GlobalConfiguration.GLOBAL_HELP_AUTO_USAGE))
                    .append(System.lineSeparator());
        }
    }

    /**
     * Generate a {@link GlobalConfiguration#GLOBAL_HELP_FORMAT_COLUMN_SPACING}
     * value; if there is no user-defined value present in the supplied
     * configuration map, a default value will be added to the given builder;
     * otherwise the user-defined option will be used.
     *
     * @param sb non-{@code null} builder to append to.
     *
     * @param config non-{@code null} configuration to check; may be empty.
     */
    private void processHelpFormatColumnSpacing(StringBuilder sb,
            Map<String, String> config) {
        if (!config.containsKey(GlobalConfiguration.GLOBAL_HELP_FORMAT_COLUMN_SPACING)) {
            // add default
            sb.append(GlobalConfiguration.GLOBAL_HELP_FORMAT_COLUMN_SPACING)
                    .append(" = ")
                    .append(HelpFormatter.DEFAULT_COLUMN_SPACING)
                    .append(System.lineSeparator());
            clcMappings.put(GlobalConfiguration.GLOBAL_HELP_FORMAT_COLUMN_SPACING,
                    Integer.toString(HelpFormatter.DEFAULT_COLUMN_SPACING));
        } else {
            // user defined
            sb.append(GlobalConfiguration.GLOBAL_HELP_FORMAT_COLUMN_SPACING)
                    .append(" = ")
                    .append(config.get(GlobalConfiguration.GLOBAL_HELP_FORMAT_COLUMN_SPACING))
                    .append(System.lineSeparator());;
        }
    }

    /**
     * Generate a {@link GlobalConfiguration#GLOBAL_HELP_FORMAT_LEFT_PAD} value;
     * if there is no user-defined value present in the supplied configuration
     * map, a default value will be added to the given builder; otherwise the
     * user-defined option will be used.
     *
     * @param sb non-{@code null} builder to append to.
     *
     * @param config non-{@code null} configuration to check; may be empty.
     */
    private void processHelpFormatLeftPad(StringBuilder sb,
            Map<String, String> config) {
        if (!config.containsKey(GlobalConfiguration.GLOBAL_HELP_FORMAT_LEFT_PAD)) {
            // add default
            sb.append(GlobalConfiguration.GLOBAL_HELP_FORMAT_LEFT_PAD)
                    .append(" = ")
                    .append(HelpFormatter.DEFAULT_LEFT_PAD)
                    .append(System.lineSeparator());
            clcMappings.put(GlobalConfiguration.GLOBAL_HELP_FORMAT_LEFT_PAD,
                    Integer.toString(HelpFormatter.DEFAULT_LEFT_PAD));
        } else {
            // user defined
            sb.append(GlobalConfiguration.GLOBAL_HELP_FORMAT_LEFT_PAD)
                    .append(" = ")
                    .append(config.get(GlobalConfiguration.GLOBAL_HELP_FORMAT_LEFT_PAD))
                    .append(System.lineSeparator());;
        }
    }

    /**
     * Generate a {@link GlobalConfiguration#GLOBAL_HELP_FORMAT_WIDTH} value; if
     * there is no user-defined value present in the supplied configuration map,
     * a default value will be added to the given builder; otherwise the
     * user-defined option will be used.
     *
     * @param sb non-{@code null} builder to append to.
     *
     * @param config non-{@code null} configuration to check; may be empty.
     */
    private void processHelpFormatWidth(StringBuilder sb,
            Map<String, String> config) {
        if (!config.containsKey(GlobalConfiguration.GLOBAL_HELP_FORMAT_WIDTH)) {
            // add default
            sb.append(GlobalConfiguration.GLOBAL_HELP_FORMAT_WIDTH)
                    .append(" = ")
                    .append(HelpFormatter.DEFAULT_WIDTH)
                    .append(System.lineSeparator());
            clcMappings.put(GlobalConfiguration.GLOBAL_HELP_FORMAT_WIDTH,
                    Integer.toString(HelpFormatter.DEFAULT_WIDTH));
        } else {
            // user defined
            sb.append(GlobalConfiguration.GLOBAL_HELP_FORMAT_WIDTH)
                    .append(" = ")
                    .append(config.get(GlobalConfiguration.GLOBAL_HELP_FORMAT_WIDTH))
                    .append(System.lineSeparator());;
        }
    }

    /**
     * Generate a {@link GlobalConfiguration#GLOBAL_HELP_FORMAT_WIDTH_FROM_ENV}
     * value; if there is no user-defined value present in the supplied
     * configuration map, a default value will be added to the given builder;
     * otherwise the user-defined option will be used.
     *
     * @param sb non-{@code null} builder to append to.
     *
     * @param config non-{@code null} configuration to check; may be empty.
     */
    private void processHelpFormatWidthFromEnv(StringBuilder sb,
            Map<String, String> config) {
        if (!config.containsKey(GlobalConfiguration.GLOBAL_HELP_FORMAT_WIDTH_FROM_ENV)) {
            // add default
            sb.append(GlobalConfiguration.GLOBAL_HELP_FORMAT_WIDTH_FROM_ENV)
                    .append(" = ")
                    .append(ClcParser.FALSE)
                    .append(System.lineSeparator());
            clcMappings.put(GlobalConfiguration.GLOBAL_HELP_FORMAT_WIDTH_FROM_ENV,
                    ClcParser.FALSE);
        } else {
            // user defined
            sb.append(GlobalConfiguration.GLOBAL_HELP_FORMAT_WIDTH_FROM_ENV)
                    .append(" = ")
                    .append(config.get(GlobalConfiguration.GLOBAL_HELP_FORMAT_WIDTH_FROM_ENV))
                    .append(System.lineSeparator());;
        }
    }

    /**
     * Generate a {@link GlobalConfiguration#GLOBAL_HELP_FORMAT_SORT_OPTIONS}
     * value; if there is no user-defined value present in the supplied
     * configuration map, a default value will be added to the given builder;
     * otherwise the user-defined option will be used.
     *
     * @param sb non-{@code null} builder to append to.
     *
     * @param config non-{@code null} configuration to check; may be empty.
     */
    private void processHelpSortOptions(StringBuilder sb,
            Map<String, String> config) {
        if (!config.containsKey(GlobalConfiguration.GLOBAL_HELP_FORMAT_SORT_OPTIONS)) {
            // add default
            sb.append(GlobalConfiguration.GLOBAL_HELP_FORMAT_SORT_OPTIONS)
                    .append(" = ")
                    .append(ClcParser.FALSE)
                    .append(System.lineSeparator());
            clcMappings.put(GlobalConfiguration.GLOBAL_HELP_FORMAT_SORT_OPTIONS,
                    ClcParser.FALSE);
        } else {
            // user defined
            sb.append(GlobalConfiguration.GLOBAL_HELP_FORMAT_SORT_OPTIONS)
                    .append(" = ")
                    .append(config.get(GlobalConfiguration.GLOBAL_HELP_FORMAT_SORT_OPTIONS))
                    .append(System.lineSeparator());;
        }
    }

    /**
     * Add in version information in the form of globally defined version,
     * taking the version either from the given properties, or, if not present,
     * using the {@link #MANIFEST_IMPLEMENTATION_VERSION}.
     *
     * @param sb non-{@code null} string builder to append generated output to.
     * 
     * @param propertyVersion version property name to take the value of the
     * version from; may be {@code null}.
     * 
     * @param properties non-{@code null} properties to check; may be empty.
     *
     * @since 1.1
     */
    private void addPropertyVersionInformation(StringBuilder sb,
            String propertyVersion, Map<String, Object> properties) {
        String value = null;
        if (properties.get(propertyVersion) != null) {
            value = properties.get(propertyVersion).toString();
        } else {
            value = MANIFEST_IMPLEMENTATION_VERSION;
        }
        //finally, remove the properties:
        if (properties.containsKey(propertyVersion)) {
            properties.remove(propertyVersion);
        }
        sb.append(GlobalConfiguration.GLOBAL_VERSION_OPTION_NAME)
                .append(" = ")
                .append(propertyVersion)
                .append(System.lineSeparator());
        sb.append(GlobalConfiguration.GLOBAL_VERSION_OPTION_TEXT)
                .append(" = ")
                .append(value)
                .append(System.lineSeparator());
    }

    /**
     * Generate a {@link GlobalConfiguration#GLOBAL_HELP_OPTION_NAME} value; if
     * there is no user-defined value present in the supplied configuration map,
     * a default value will be added to the given builder; otherwise the
     * user-defined option will be used.
     *
     * @param sb non-{@code null} builder to append to.
     *
     * @param config non-{@code null} configuration to check; may be empty.
     *
     * @return help option name override; {@code null} when there is no user
     * defined value.
     */
    private String processHelpCommandOptionName(StringBuilder sb,
            Map<String, String> config) {
        String helpOptionNameOverride = null;
        if (!config.containsKey(GlobalConfiguration.GLOBAL_HELP_OPTION_NAME)) {
            // add default
            sb.append(GlobalConfiguration.GLOBAL_HELP_OPTION_NAME)
                    .append(" = ")
                    .append(HELP_DEFAULT)
                    .append(System.lineSeparator());
            clcMappings.put(GlobalConfiguration.GLOBAL_HELP_OPTION_NAME,
                    HELP_DEFAULT);
        } else {
            // user defined
            sb.append(GlobalConfiguration.GLOBAL_HELP_OPTION_NAME)
                    .append(" = ")
                    .append(config.get(GlobalConfiguration.GLOBAL_HELP_OPTION_NAME))
                    .append(System.lineSeparator());;
            helpOptionNameOverride = config.get(GlobalConfiguration.GLOBAL_HELP_OPTION_NAME);
        }
        return helpOptionNameOverride;
    }

    /**
     * Generate a help {@link ClcParser#OPTS} value; if there is no user-defined
     * value present in the supplied configuration map, a default value will be
     * added to the given builder; otherwise the user-defined option will be
     * used.
     *
     * @param sb non-{@code null} builder to append to.
     *
     * @param config non-{@code null} configuration to check; may be empty.
     *
     * @param helpOptionKey non-{@code null} help option key.
     */
    private void processHelpOptionOpts(StringBuilder sb,
            Map<String, String> config, String helpOptionKey) {
        String optsKey = createOptionName(helpOptionKey, ClcParser.OPTS);
        if (!config.containsKey(optsKey) && HELP_DEFAULT.equals(helpOptionKey)) {
            // add default
            sb.append(ClcParser.OPTION)
                    .append(".").append(HELP_DEFAULT).append(".")
                    .append(ClcParser.OPTS)
                    .append(" = ").append(HELP_DEFAULT)
                    .append(System.lineSeparator());
        } else if (config.containsKey(optsKey)) {
            // user defined
            String helpOptsValue = config.get(optsKey);
            sb.append(optsKey)
                    .append(" = ")
                    .append(helpOptsValue)
                    .append(System.lineSeparator());
        } else {
            // no such definition, throw error:
            throw new IllegalArgumentException("No definition for option."
                    + helpOptionKey + "." + ClcParser.OPTS);
        }
    }

    /**
     * Generate a help {@link ClcParser#DESCRIPTION} value; if there is no
     * user-defined value present in the supplied configuration map, a default
     * value will be added to the given builder; otherwise the user-defined
     * option will be used.
     *
     * @param sb non-{@code null} builder to append to.
     *
     * @param config non-{@code null} configuration to check; may be empty.
     *
     * @param helpOptionKey non-{@code null} help option key.
     *
     * @throws IllegalArgumentException if there is no help description present.
     */
    private void processHelpOptionDescription(StringBuilder sb,
            Map<String, String> config, String helpOptionKey) {
        String optsKey = createOptionName(helpOptionKey, ClcParser.DESCRIPTION);
        if (!config.containsKey(optsKey)
                && HELP_DEFAULT.equals(helpOptionKey)) {
            // add default
            sb.append(optsKey)
                    .append(" = Print this help then exit.")
                    .append(System.lineSeparator());
        } else if (config.containsKey(optsKey)) {
            // user defined
            sb.append(optsKey)
                    .append(" = ")
                    .append(config.get(optsKey))
                    .append(System.lineSeparator());;
        } else {
            // no such definition, throw error:
            throw new IllegalArgumentException("No definition for option."
                    + helpOptionKey + "." + ClcParser.DESCRIPTION);
        }
    }

    /**
     * Generate a help {@link ClcParser#IGNORE_CLI_ARGS} value; if there is no
     * user-defined value present in the supplied configuration map, a default
     * value will be added to the given builder; otherwise the user-defined
     * option will be used.
     *
     * @param sb non-{@code null} builder to append to.
     *
     * @param config non-{@code null} configuration to check; may be empty.
     *
     * @param helpOptionKey non-{@code null} help option key.
     *
     * @throws IllegalArgumentException if there is no help description present.
     */
    private void processHelpKeyIgnoreCliArgs(StringBuilder sb,
            Map<String, String> config, String helpOptionKey) {
        String optsKey = createOptionName(helpOptionKey, ClcParser.IGNORE_CLI_ARGS);
        if (!config.containsKey(optsKey)) {
            // add default
            sb.append(optsKey)
                    .append(" = true")
                    .append(System.lineSeparator());
        } else if (config.containsKey(optsKey)) {
            // user defined
            sb.append(optsKey)
                    .append(" = ")
                    .append(config.get(optsKey))
                    .append(System.lineSeparator());;
        }
    }

    /**
     * Add comment-prefixed entries for all configurations that have
     * <i>not</i> been defined for a given option.
     *
     * @param sb non-{@code null} builder to append to.
     *
     * @param config non-{@code null} configuration to check; may be empty.
     *
     * @param optionName non-{@code null} option name.
     */
    private void padConfiguration(StringBuilder sb, Map<String, String> config,
            String optionName) {
        boolean unary = true;
        String optionHasArg = createOptionName(optionName, ClcParser.HAS_ARG);
        if (config.containsKey(optionHasArg)) {
            String hasArgValue = config.get(optionHasArg);
            if (ClcParser.TRUE.equals(hasArgValue)) {
                unary = false;
            }
        }
        String optsKey = createOptionName(optionName, ClcParser.IGNORE_CLI_ARGS);
        if (!config.containsKey(optsKey)) {
            sb.append("# ")
                    .append(createOptionName(optionName,
                            ClcParser.IGNORE_CLI_ARGS))
                    .append(" = false")
                    .append(System.lineSeparator());
        }
        if (!unary) {
            optsKey = createOptionName(optionName, ClcParser.ARG_NAME);
            if (!config.containsKey(optsKey)) {
                sb.append("# ")
                        .append(createOptionName(optionName,
                                ClcParser.ARG_NAME))
                        .append(" = arg")
                        .append(System.lineSeparator());
            }
            optsKey = createOptionName(optionName, ClcParser.TYPE);
            if (!config.containsKey(optsKey)) {
                sb.append("# ")
                        .append(createOptionName(optionName,
                                ClcParser.TYPE))
                        .append(" = type")
                        .append(System.lineSeparator());
            }
            optsKey = createOptionName(optionName, ClcParser.PROPERTIES);
            if (!config.containsKey(optsKey)) {
                sb.append("# ")
                        .append(createOptionName(optionName,
                                ClcParser.PROPERTIES))
                        .append(" = p1 = x, p2 = y, p3 = z")
                        .append(System.lineSeparator());
            }
            optsKey = createOptionName(optionName, ClcParser.DEFAULT);
            if (!config.containsKey(optsKey)) {
                sb.append("# ")
                        .append(createOptionName(optionName,
                                ClcParser.DEFAULT))
                        .append(" = x")
                        .append(System.lineSeparator());
            }
        }
    }

    /**
     * Create an option-prefixed command line configuration name for the
     * specified suffix.
     *
     * @param optionName non-{@code null} option name.
     *
     * @param suffix non-{@code null} valid command line configuration suffix.
     *
     * @return non-{@code null} string of the form
     * {@code option.<commandName>.<suffix>}
     */
    private String createOptionName(String optionName, String suffix) {
        return ClcParser.OPTION + "." + optionName + "." + suffix;
    }

}
