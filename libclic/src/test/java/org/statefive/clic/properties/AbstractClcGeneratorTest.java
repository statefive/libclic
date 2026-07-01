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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.joor.Reflect;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.statefive.clic.ClcParser;
import org.statefive.clic.Clc;
import org.statefive.clic.GlobalConfiguration;
import org.statefive.clic.valuetype.ValueType;

/**
 * Test that the methods
 * {@link AbstractConfigurationGenerator#generateConfiguration(java.util.Map)}
 * and
 * {@link AbstractConfigurationGenerator#generateConfiguration(java.util.Map, java.util.Map)}
 * are valid using the test implementation of
 * {@link BasicPropertiesClcGenerator}.
 *
 * @author rich
 */
public class AbstractClcGeneratorTest {

    /**
     * Configuration generation under test.
     */
    private BasicPropertiesClcGenerator generator = null;

    /**
     * Properties bindings for properties readers.
     */
    private final PropertiesListenerBindings propsBindings = PropertiesListenerBindings.getInstance();

    /**
     * Initialise value types.
     */
    @BeforeClass
    public static void setUpClass() {
        Clc.initialiseValueTypeFactory();
    }

    /**
     * Clear all value types.
     */
    @AfterClass
    public static void tearDownUpClass() {
        PropertiesTestHelper.clearValueTypes();
    }

    /**
     * Initialise the configuration generator.
     */
    @Before
    public void setUp() {
        generator = new BasicPropertiesClcGenerator();
    }

    /**
     * Inspired by:
     * https://stackoverflow.com/questions/3301635/change-private-static-final-field-using-java-reflection
     */
    @After
    public void tearDown() {
        Reflect.on(propsBindings).set("instance", null);
        Reflect.onClass(Clc.class).set("instance", null);
    }

    /**
     * Test of getPropertyMappings method, of class
     * AbstractConfigurationGenerator.
     */
    @Test
    public void testGenerateConfigurationOverrideLongOptions() {
        Map<String, String> config = new HashMap<>();
        config.put(GlobalConfiguration.GLOBAL_OPTIONS_OPTS_TYPE, "BOTH");
        String data = generator.generateConfiguration(
                getBasicPropertyMap(), config, null, true, 
                null, false, false);
        String[] lines = data.split(System.lineSeparator());
        boolean defined = isDefined(lines, "global.options.opts-type", "BOTH");
        if (!defined) {
            fail("Expected to find overridden default value for"
                    + " 'global.options.opts-type' but was not present.");
        }
    }

    /**
     * Test of getPropertyMappings method, of class
     * AbstractConfigurationGenerator.
     */
    @Test
    public void testGenerateConfigurationOverrideHelpSwitchOpts() {
        Map<String, String> config = new HashMap<>();
        config.put(GlobalConfiguration.GLOBAL_HELP_SWITCH_OPTS, "H/RTFM");
        String data = generator.generateConfiguration(
                getBasicPropertyMap(), config, null, true, 
                null, false, false);
        String[] lines = data.split(System.lineSeparator());
        boolean defined = isDefined(lines,
                GlobalConfiguration.GLOBAL_HELP_SWITCH_OPTS, "H/RTFM");
        if (!defined) {
            fail("Expected to find overridden default value for"
                    + " 'global.options.opts-type' but was not present.");
        }
    }

    /**
     * Test of getPropertyMappings method, of class
     * AbstractConfigurationGenerator.
     */
    @Test
    public void testGenerateConfigurationOverrideOpts() {
        Map<String, String> config = new HashMap<>();
        config.put("option.foo-bar.opts", "f/foo-bar");
        String data = generator.generateConfiguration(
                getBasicPropertyMap(), config, null, true, 
                null, false, false);
        String[] lines = data.split(System.lineSeparator());
        boolean defined = isDefined(lines, "option.foo-bar.opts", "f/foo-bar");
        if (!defined) {
            fail("Expected to find overridden default value for"
                    + " 'option.foo-bar.opts' but was not present.");
        }
    }

    /**
     * Test of getPropertyMappings method, of class
     * AbstractConfigurationGenerator.
     */
    @Test
    public void testGenerateConfigurationOverride() {
        Map<String, String> config = new HashMap<>();
        config.put("option.help.ignoreCliArgs", ClcParser.FALSE);
        String data = generator.generateConfiguration(
                getBasicPropertyMap(), config, null, true, 
                null, false, false);
        String[] lines = data.split(System.lineSeparator());
        boolean defined = isDefined(lines, "option.help.ignoreCliArgs", ClcParser.FALSE);
        if (!defined) {
            fail("Expected to find overridden default value for"
                    + " 'option.foo-bar.opts' but was not present.");
        }
    }

    /**
     * Test of getPropertyMappings method, of class
     * AbstractConfigurationGenerator.
     */
    @Test
    public void testGenerateConfigurationOverrideHasArg() {
        Map<String, String> config = new HashMap<>();
        config.put("option.foo-bar.hasArg", ClcParser.TRUE);
        String data = generator.generateConfiguration(
                getBasicPropertyMap(), config, null, true, 
                null, false, false);
        String[] lines = data.split(System.lineSeparator());
        boolean defined = isDefined(lines, "option.foo-bar.hasArg", ClcParser.TRUE);
        if (!defined) {
            fail("Expected to find overridden default value for"
                    + " 'option.foo-bar.hasArg' but was not present.");
        }
    }

    /**
     * Test of getPropertyMappings method, of class
     * AbstractConfigurationGenerator.
     */
    @Test
    public void testGenerateConfigurationOverrideArgValue() {
        Map<String, String> config = new HashMap<>();
        config.put("option.foo-bar.argName", "abc");
        String data = generator.generateConfiguration(
                getBasicPropertyMap(), config, null, true, 
                null, false, false);
        String[] lines = data.split(System.lineSeparator());
        boolean defined = isDefined(lines, "option.foo-bar.argName", "abc");
        if (!defined) {
            fail("Expected to find overridden default value for"
                    + " 'option.foo-bar.argName' but was not present.");
        }
    }

    /**
     * Test of getPropertyMappings method, of class
     * AbstractConfigurationGenerator.
     */
    @Test
    public void testGenerateConfigurationOverrideHasArgThrowsException() {
        Map<String, String> config = new HashMap<>();
        config.put("option.foo-bar.hasArg", ClcParser.FALSE);
        try {
            generator.generateConfiguration(getBasicPropertyMap(), config, null, true, 
                null, false, false);
            fail("Expected an exception");
        } catch (IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "Configuration"
                    + " option.foo-bar.hasArg can only be 'true', use type"
                    + " inference to override false ss unary switches.");
        }
    }

    /**
     * Test that when parsing a property as false with type inference
     * configuration specified as using false for unary switches, the
     * configuration contains {@code hasArg} as {@code false}.
     */
    @Test
    public void testGenerateConfigurationOverrideFalseAsUnarySwitch() {
        Map<String, String> config = new HashMap<>();
        config.put("option.foo-bar.hasArg", ClcParser.FALSE);
        TypeInferralConfig typeConfig = new TypeInferralConfigBuilder()
                .withFalseAsUnarySwitch().build();
        String data = generator.generateConfiguration(getMultiplePropertiesMap(),
                config, null, false, typeConfig, false, false);
        String[] lines = data.split(System.lineSeparator());
        assertTrue(isDefined(lines, "option.verbose.hasArg", ClcParser.FALSE));
    }

    /**
     * Test of getPropertyMappings method, of class
     * AbstractConfigurationGenerator.
     */
    @Test
    public void testGenerateConfigurationOverrideDescription() {
        Map<String, String> config = new HashMap<>();
        config.put("option.foo-bar.description", "foo has no bar");
        String data = generator.generateConfiguration(getBasicPropertyMap(), config, null, true, 
                null, false, false);
        String[] lines = data.split(System.lineSeparator());
        boolean defined = false;
        for (String line : lines) {
            if ("option.foo-bar.description = foo has no bar".equals(line)) {
                defined = true;
                break;
            }
        }
        if (!defined) {
            fail("Expected to find overridden default value for"
                    + " 'option.foo-bar.description' but was not present.");
        }
    }

    /**
     * Test of getPropertyMappings method, of class
     * AbstractConfigurationGenerator.
     */
    @Test
    public void testGenerateConfigurationOverrideHelpCommandName() {
        Map<String, String> config = new HashMap<>();
        config.put(GlobalConfiguration.GLOBAL_HELP_COMMAND_NAME, "program-foo");
        String data = generator.generateConfiguration(getBasicPropertyMap(), config, null, true, 
                null, false, false);
        String[] lines = data.split(System.lineSeparator());
        boolean defined = isDefined(lines, GlobalConfiguration.GLOBAL_HELP_COMMAND_NAME, "program-foo");
        if (!defined) {
            fail("Expected to find overridden default value for '"
                    + GlobalConfiguration.GLOBAL_HELP_COMMAND_NAME + "' but was not present.");
        }
    }

    /**
     * Test of getPropertyMappings method, of class
     * AbstractConfigurationGenerator.
     */
    @Test
    public void testGenerateConfigurationOverrideHelpCommandHeader() {
        Map<String, String> config = new HashMap<>();
        config.put(GlobalConfiguration.GLOBAL_HELP_COMMAND_HEADER,
                "Important header information");
        String data = generator.generateConfiguration(getBasicPropertyMap(), config, null, true, 
                null, false, false);
        String[] lines = data.split(System.lineSeparator());
        boolean defined = isDefined(lines, GlobalConfiguration.GLOBAL_HELP_COMMAND_HEADER,
                "Important header information");
        if (!defined) {
            fail("Expected to find overridden default value for '"
                    + GlobalConfiguration.GLOBAL_HELP_COMMAND_HEADER
                    + "' but was not present.");
        }
    }

    /**
     * Test of getPropertyMappings method, of class
     * AbstractConfigurationGenerator.
     */
    @Test
    public void testGenerateConfigurationOverrideHelpCommandFooter() {
        Map<String, String> config = new HashMap<>();
        config.put(GlobalConfiguration.GLOBAL_HELP_COMMAND_FOOTER,
                "Important footer information");
        String data = generator.generateConfiguration(getBasicPropertyMap(), config, null, true, 
                null, false, false);
        String[] lines = data.split(System.lineSeparator());
        boolean defined = isDefined(lines, GlobalConfiguration.GLOBAL_HELP_COMMAND_FOOTER,
                "Important footer information");
        if (!defined) {
            fail("Expected to find overridden default value for '"
                    + GlobalConfiguration.GLOBAL_HELP_COMMAND_FOOTER
                    + "' but was not present.");
        }
    }

    /**
     * Test of getPropertyMappings method, of class
     * AbstractConfigurationGenerator.
     */
    @Test
    public void testGenerateConfigurationOverrideHelpAutoUsage() {
        Map<String, String> config = new HashMap<>();
        config.put(GlobalConfiguration.GLOBAL_HELP_AUTO_USAGE,
                ClcParser.TRUE);
        String data = generator.generateConfiguration(getBasicPropertyMap(), config, null, true, 
                null, false, false);
        String[] lines = data.split(System.lineSeparator());
        boolean defined = isDefined(lines, GlobalConfiguration.GLOBAL_HELP_AUTO_USAGE,
                ClcParser.TRUE);
        if (!defined) {
            fail("Expected to find overridden default value for '"
                    + GlobalConfiguration.GLOBAL_HELP_AUTO_USAGE
                    + "' but was not present.");
        }
    }

    /**
     * Test of getPropertyMappings method, of class
     * AbstractConfigurationGenerator.
     */
    @Test
    public void testGenerateConfigurationOverrideHelpColumnSpacing() {
        Map<String, String> config = new HashMap<>();
        config.put(GlobalConfiguration.GLOBAL_HELP_FORMAT_COLUMN_SPACING,
                "1");
        String data = generator.generateConfiguration(getBasicPropertyMap(), config, null, true, 
                null, false, false);
        String[] lines = data.split(System.lineSeparator());
        boolean defined = isDefined(lines, GlobalConfiguration.GLOBAL_HELP_FORMAT_COLUMN_SPACING,
                "1");
        if (!defined) {
            fail("Expected to find overridden default value for '"
                    + GlobalConfiguration.GLOBAL_HELP_FORMAT_COLUMN_SPACING
                    + "' but was not present.");
        }
    }

    /**
     * Test of getPropertyMappings method, of class
     * AbstractConfigurationGenerator.
     */
    @Test
    public void testGenerateConfigurationOverrideHelpFormatLeftPad() {
        Map<String, String> config = new HashMap<>();
        config.put(GlobalConfiguration.GLOBAL_HELP_FORMAT_LEFT_PAD,
                "1");
        String data = generator.generateConfiguration(getBasicPropertyMap(), config, null, true, 
                null, false, false);
        String[] lines = data.split(System.lineSeparator());
        boolean defined = isDefined(lines, GlobalConfiguration.GLOBAL_HELP_FORMAT_LEFT_PAD,
                "1");
        if (!defined) {
            fail("Expected to find overridden default value for '"
                    + GlobalConfiguration.GLOBAL_HELP_FORMAT_LEFT_PAD
                    + "' but was not present.");
        }
    }

    /**
     * Test of getPropertyMappings method, of class
     * AbstractConfigurationGenerator.
     */
    @Test
    public void testGenerateConfigurationOverrideHelpFormatWidth() {
        Map<String, String> config = new HashMap<>();
        config.put(GlobalConfiguration.GLOBAL_HELP_FORMAT_WIDTH,
                "100");
        String data = generator.generateConfiguration(getBasicPropertyMap(), config, null, true, 
                null, false, false);
        String[] lines = data.split(System.lineSeparator());
        boolean defined = isDefined(lines, GlobalConfiguration.GLOBAL_HELP_FORMAT_WIDTH,
                "100");
        if (!defined) {
            fail("Expected to find overridden default value for '"
                    + GlobalConfiguration.GLOBAL_HELP_FORMAT_WIDTH
                    + "' but was not present.");
        }
    }

    /**
     * Test of getPropertyMappings method, of class
     * AbstractConfigurationGenerator.
     */
    @Test
    public void testGenerateConfigurationOverrideHelpFormatWidthFromEnv() {
        Map<String, String> config = new HashMap<>();
        config.put(GlobalConfiguration.GLOBAL_HELP_FORMAT_WIDTH_FROM_ENV,
                ClcParser.TRUE);
        String data = generator.generateConfiguration(getBasicPropertyMap(), config, null, true, 
                null, false, false);
        String[] lines = data.split(System.lineSeparator());
        boolean defined = isDefined(lines, GlobalConfiguration.GLOBAL_HELP_FORMAT_WIDTH_FROM_ENV,
                ClcParser.TRUE);
        if (!defined) {
            fail("Expected to find overridden default value for '"
                    + GlobalConfiguration.GLOBAL_HELP_FORMAT_WIDTH_FROM_ENV
                    + "' but was not present.");
        }
    }

    /**
     * Test of getPropertyMappings method, of class
     * AbstractConfigurationGenerator.
     */
    @Test
    public void testGenerateConfigurationOverrideHelpOptionName() {
        Map<String, String> config = new HashMap<>();
        config.put(GlobalConfiguration.GLOBAL_HELP_OPTION_NAME, "get-some-help");
        config.put("option.get-some-help.opts", "get-some-help");
        config.put("option.get-some-help.description", "This is what you want, this is what you get");
        String data = generator.generateConfiguration(getBasicPropertyMap(), config, null, true, 
                null, false, false);
        String[] lines = data.split(System.lineSeparator());
        boolean defined = isDefined(lines, GlobalConfiguration.GLOBAL_HELP_OPTION_NAME, "get-some-help");
        if (!defined) {
            fail("Expected to find overridden default value for '"
                    + GlobalConfiguration.GLOBAL_HELP_OPTION_NAME + "' but was not present.");
        }
    }

    /**
     * Test of getPropertyMappings method, of class
     * AbstractConfigurationGenerator.
     */
    @Test
    public void testGenerateConfigurationOverrideHelpOptions() {
        Map<String, String> config = new HashMap<>();
        config.put(GlobalConfiguration.GLOBAL_HELP_OPTION_NAME, "get-some-help");
        config.put("option.get-some-help.opts", "get-some-help");
        config.put("option.get-some-help.description",
                "This is what you want, this is what you get");
        String data = generator.generateConfiguration(getBasicPropertyMap(), config, null, true, 
                null, false, false);
        String[] lines = data.split(System.lineSeparator());
        boolean defined = isDefined(lines, "option.get-some-help.opts", "get-some-help");
        if (!defined) {
            fail("Expected to find overridden default value for"
                    + " 'option.get-some-help.opts' but was not present.");
        }
    }

    /**
     * Test of getPropertyMappings method, of class
     * AbstractConfigurationGenerator.
     */
    @Test
    public void testGenerateConfigurationOverrideHelpOptionsThrowsException() {
        Map<String, String> config = new HashMap<>();
        config.put(GlobalConfiguration.GLOBAL_HELP_OPTION_NAME, "get-some-help");
        config.put("option.get-some-help-x.opts", "get-some-help");
        try {
            generator.generateConfiguration(getBasicPropertyMap(), config, null, true, 
                null, false, false);
            fail("Expected an exception");
        } catch (IllegalArgumentException ex) {
            assertEquals(ex.getMessage(),
                    "No definition for option.get-some-help.opts");
        }
    }

    /**
     * Test of getPropertyMappings method, of class
     * AbstractConfigurationGenerator.
     */
    @Test
    public void testGenerateConfigurationOverrideHelpDescription() {
        Map<String, String> config = new HashMap<>();
        config.put(GlobalConfiguration.GLOBAL_HELP_OPTION_NAME, "get-some-help");
        config.put("option.get-some-help.opts", "get-some-help");
        config.put("option.get-some-help.description",
                "This is what you want, this is what you get");
        String data = generator.generateConfiguration(getBasicPropertyMap(), config, null, true, 
                null, false, false);
        String[] lines = data.split(System.lineSeparator());
        boolean defined = isDefined(lines, "option.get-some-help.description",
                    "This is what you want, this is what you get");
        if (!defined) {
            fail("Expected to find overridden default value for"
                    + " 'option.get-some-help.description' but was not present.");
        }
    }

    /**
     * Test of getPropertyMappings method, of class
     * AbstractConfigurationGenerator.
     */
    @Test
    public void testGenerateConfigurationOverrideHelpDescriptionThrowsException() {
        Map<String, String> config = new HashMap<>();
        config.put(GlobalConfiguration.GLOBAL_HELP_OPTION_NAME, "get-some-help");
        config.put("option.get-some-help.opts", "get-some-help");
        config.put("option.get-some-help-x.description", "get-some-help");
        try {
            generator.generateConfiguration(getBasicPropertyMap(), config, null, true, 
                null, false, false);
            fail("Expected an exception");
        } catch (IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "No definition for option.get-some-help.description");
        }
    }

    /**
     * Test of getPropertyMappings method, of class
     * AbstractConfigurationGenerator.
     */
    @Test
    public void testGenerateConfigurationOverrideDefaultThrowsException() {
        Map<String, String> config = new HashMap<>();
        config.put("option.foo-bar.default", "123");
        try {
            generator.generateConfiguration(getBasicPropertyMap(), config, null, true, 
                null, false, false);
            fail("Expected an exception");
        } catch (IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "Configuration cannot contain"
                    + " a 'default' value.");
        }
    }

    /**
     * Test of getPropertyMappings method, of class
     * AbstractConfigurationGenerator.
     */
    @Test
    public void testGenerateConfigurationWithIncludes() {
        Map<String, String> config = new HashMap<>();
        PropertyNameFilter filter = PropertiesTestHelper.createPropertyNameFilter(".*port.*", true);
        String data = generator.generateConfiguration(
                getMultiplePropertiesMap(), config, filter, true, null, false, false);
        String[] lines = data.split(System.lineSeparator());
        Set<String> results = new HashSet<>();
        for (String line : lines) {
            if (line.startsWith(ClcParser.OPTION)) {
                results.add(line);
            }
        }
        assertTrue(results.contains("option.host-port.opts = host-port"));
        assertTrue(results.contains("option.reports.opts = reports"));
        assertFalse(results.contains("option.host-name.opts = host-name"));
        assertFalse(results.contains("option.verbose.opts = verbose"));
        assertFalse(results.contains("option.inet-verbose.opts = inet-verbose"));
    }

    /**
     * Test of getPropertyMappings method, of class
     * AbstractConfigurationGenerator.
     */
    @Test
    public void testGenerateConfigurationWithExcludes() {
        Map<String, String> config = new HashMap<>();
        PropertyNameFilter filter = PropertiesTestHelper.createPropertyNameFilter(".*port.*", false);
        String data = generator.generateConfiguration(
                getMultiplePropertiesMap(), config, filter, true, null, false, false);
        String[] lines = data.split(System.lineSeparator());
        Set<String> results = new HashSet<>();
        for (String line : lines) {
            if (line.startsWith(ClcParser.OPTION)) {
                results.add(line);
            }
        }
        assertTrue(results.contains("option.host-name.opts = host-name"));
        assertTrue(results.contains("option.verbose.opts = verbose"));
        assertTrue(results.contains("option.inet-verbose.opts = inet-verbose"));
        assertFalse(results.contains("option.host-port.opts = host-port"));
        assertFalse(results.contains("option.reports.opts = reports"));
    }

    /**
     * Test of getPropertyMappings method, of class
     * AbstractConfigurationGenerator.
     */
    @Test
    public void testGenerateConfigurationWithValueTypes() {
        Map<String, String> config = new HashMap<>();
        TypeInferralConfig typeInferralConfig = new TypeInferralConfigBuilder()
                .withInferTypes().build();
        String data = generator.generateConfiguration(
                PropertiesTestHelper.getMultipleValueTypePropertiesMap(),
                config, null, true, typeInferralConfig, false, false);
        String[] lines = data.split(System.lineSeparator());
        Set<String> results = new HashSet<>();
        for (String line : lines) {
            if (line.startsWith(ClcParser.OPTION)) {
                results.add(line);
            }
        }
        ValueType valueType = (ValueType) generator.propertyValueTypes.get("host-port");
        assertEquals(8080, valueType.getValue("8080"));
        valueType = (ValueType) generator.propertyValueTypes.get("delay");
        assertEquals(4.5f, valueType.getValue("4.5"));
        valueType = (ValueType) generator.propertyValueTypes.get("reports");
        assertEquals(true, valueType.getValue(ClcParser.TRUE));
        valueType = (ValueType) generator.propertyValueTypes.get("failover");
        assertEquals(false, valueType.getValue(ClcParser.FALSE));
    }

    /**
     * Test of getPropertyMappings method, of class
     * AbstractConfigurationGenerator.
     */
    @Test
    public void testGenerateConfigurationWithEmptyFilter() {
        Map<String, String> config = new HashMap<>();
        PropertyNameFilter filter = PropertiesTestHelper.createPropertyNameFilter("", false);
        String data = generator.generateConfiguration(
                getMultiplePropertiesMap(), config, filter, true, null, false, false);
        String[] lines = data.split(System.lineSeparator());
        Set<String> results = new HashSet<>();
        for (String line : lines) {
            if (line.startsWith(ClcParser.OPTION)) {
                results.add(line);
            }
        }
        assertTrue(results.contains("option.host-name.opts = host-name"));
        assertTrue(results.contains("option.verbose.opts = verbose"));
        assertTrue(results.contains("option.inet-verbose.opts = inet-verbose"));
        assertTrue(results.contains("option.host-port.opts = host-port"));
        assertTrue(results.contains("option.reports.opts = reports"));
    }

    /**
     * Create a filter that excludes everything causing an exception to be
     * thrown.
     */
    @Test
    public void testGenerateConfigurationWithBadFilter() {
        Map<String, String> config = new HashMap<>();
        PropertyNameFilter filter = PropertiesTestHelper.createPropertyNameFilter(".*", false);
        try {
            generator.generateConfiguration(
                    getMultiplePropertiesMap(), config, filter, true, null, false, false);
            fail("Expected an exception");
        } catch (IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "Configuration not generated -"
                    + " bad filter or no properties?");
        }
    }

    /**
     * Test of getPropertyMappings method, of class
     * AbstractConfigurationGenerator.
     */
    @Test
    public void testGenerateConfigurationWithBadRegex() {
        Map<String, String> config = new HashMap<>();
        PropertyNameFilter filter = PropertiesTestHelper.createPropertyNameFilter("[a-z*", false);
        try {
            generator.generateConfiguration(
                    getMultiplePropertiesMap(), config, filter, true, null, false, false);
            fail("Expected an exception");
        } catch (IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "Bad regular expression: [a-z*");
        }
    }

    /**
     * Test that when defaults are requested in the generated configuration data
     * that the defaults have been set for the property values.
     */
    @Test
    public void testGenerateConfigurationWithInsertDefault() throws Exception {
        Map<String, String> config = new HashMap<>();
        TypeInferralConfig typeInferralConfig = new TypeInferralConfigBuilder()
                .withInferTypes().build();
        String data = generator.generateConfiguration(
                PropertiesTestHelper.getMultipleValueTypePropertiesMap(),
                config, null, true, typeInferralConfig,
                false, true);
        String[] lines = data.split(System.lineSeparator());
        Set<String> results = new HashSet<>();
        for (String line : lines) {
            if (line.startsWith(ClcParser.OPTION)) {
                results.add(line);
            }
        }
        PropertiesTestHelper.hasConfigDefault(results, "host-name", "localhost");
        PropertiesTestHelper.hasConfigDefault(results, "host-port", "8080");
        PropertiesTestHelper.hasConfigDefault(results, "delay", "4.5");
        PropertiesTestHelper.hasConfigDefault(results, "reports", ClcParser.TRUE);
        PropertiesTestHelper.hasConfigDefault(results, "failover", ClcParser.FALSE);
    }

    /**
     * Test that when inserting defaults that no default is added when the
     * property value is the empty string.
     */
    @Test
    public void testGenerateConfigurationNoInsertDefaultsForEmptyString() {
        Map<String, String> config = new HashMap<>();
        Map<String, String> props = new HashMap<>();
        props.put("foo", "");
        String data = generator.generateConfiguration(props, config,
                null, false, null, false, true);
        String[] lines = data.split(System.lineSeparator());
        for (String line : lines) {
            String expected = "option.foo.default";
            if (expected.startsWith(line)) {
                fail(line + " is present but should not have been generated.");
            }
        }
    }

    /**
     * Test that when inserting defaults that no default is added when the the
     * configuration overrides is the empty string.
     */
    @Test
    public void testGenerateConfigurationConfigBasedDefeaultNoInsertDefaultsForEmptyString() {
        Map<String, String> config = new HashMap<>();
        config.put("option.foo.default", "");
        Map<String, String> props = new HashMap<>();
        props.put("foo", "");
        String data = generator.generateConfiguration(props, config,
                null, false, null, false, true);
        String[] lines = data.split(System.lineSeparator());
        for (String line : lines) {
            String expected = "option.foo.default";
            if (expected.startsWith(line)) {
                fail(line + " is present but should not have been generated.");
            }
        }
    }

    /**
     * Test that when inserting defaults that a commented-out section is
     * generated for {@code ignoreCliArgs} for the given option.
     */
    @Test
    public void testGenerateConfigurationConfigPadIgnoreCliArgsAdded() {
        Map<String, String> config = new HashMap<>();
        Map<String, String> props = new HashMap<>();
        props.put("foo", "bar");
        String data = generator.generateConfiguration(props, config,
                null, false, null, true, false);
        String[] lines = data.split(System.lineSeparator());
        boolean defined = false;
        String expected = "# option.foo.ignoreCliArgs = false";
        for (String line : lines) {
            if (expected.contains(line)) {
                defined = true;
                break;
            }
        }
        if (!defined) {
            fail("Expected to find " + expected + " in the generated content.");
        }
    }

    /**
     * Test that when inserting defaults that a commented-out section is not
     * generated for {@code ignoreCliArgs} for the given option.
     */
    @Test
    public void testGenerateConfigurationConfigPadIgnoreCliArgsAddedWhenConfigDefined() {
        Map<String, String> config = new HashMap<>();
        config.put("option.foo.ignoreCliArgs", ClcParser.FALSE);
        Map<String, String> props = new HashMap<>();
        props.put("foo", "bar");
        String data = generator.generateConfiguration(props, config,
                null, false, null, true, false);
        String[] lines = data.split(System.lineSeparator());
        String expected = "# option.foo.ignoreCliArgs = false";
        for (String line : lines) {
            if (expected.equals(line)) {
                fail(expected + " found in generated data but should not have been present.");
            }
        }
    }

    /**
     * Test that when inserting defaults that a commented-out section is
     * generated for {@code argName} for the given option.
     */
    @Test
    public void testGenerateConfigurationConfigPadArgNameAdded() {
        Map<String, String> config = new HashMap<>();
        Map<String, String> props = new HashMap<>();
        props.put("foo", "bar");
        String data = generator.generateConfiguration(props, config,
                null, false, null, true, false);
        String[] lines = data.split(System.lineSeparator());
        boolean defined = false;
        String expected = "# option.foo.argName = argName";
        for (String line : lines) {
            if (expected.contains(line)) {
                defined = true;
                break;
            }
        }
        if (!defined) {
            fail("Expected to find " + expected + " in the generated content.");
        }
    }

    /**
     * Test that when inserting defaults that a commented-out section is not
     * generated for {@code argName} for the given option.
     */
    @Test
    public void testGenerateConfigurationConfigPadArgNameAddedWhenConfigDefined() {
        Map<String, String> config = new HashMap<>();
        config.put("option.foo.argName", "arg");
        Map<String, String> props = new HashMap<>();
        props.put("foo", "bar");
        String data = generator.generateConfiguration(props, config,
                null, false, null, true, false);
        String[] lines = data.split(System.lineSeparator());
        String expected = "# option.foo.argName = arg";
        for (String line : lines) {
            if (expected.equals(line)) {
                fail(expected + " found in generated data but should not have been present.");
            }
        }
    }

    /**
     * Test that when inserting defaults that a commented-out section is
     * generated for {@code type} for the given option.
     */
    @Test
    public void testGenerateConfigurationConfigPadTypeAdded() {
        Map<String, String> config = new HashMap<>();
        Map<String, String> props = new HashMap<>();
        props.put("foo", "bar");
        String data = generator.generateConfiguration(props, config,
                null, false, null, true, false);
        String[] lines = data.split(System.lineSeparator());
        boolean defined = false;
        String expected = "# option.foo.type = type";
        for (String line : lines) {
            if (expected.contains(line)) {
                defined = true;
                break;
            }
        }
        if (!defined) {
            fail("Expected to find " + expected + " in the generated content.");
        }
    }

    /**
     * Test that when inserting defaults that a commented-out section is not
     * generated for {@code type} for the given option.
     */
    @Test
    public void testGenerateConfigurationConfigPadTypeAddedWhenConfigDefined() {
        Map<String, String> config = new HashMap<>();
        config.put("option.foo.type", "int");
        Map<String, String> props = new HashMap<>();
        props.put("foo", "bar");
        String data = generator.generateConfiguration(props, config,
                null, false, null, true, false);
        String[] lines = data.split(System.lineSeparator());
        String expected = "# option.foo.type = type";
        for (String line : lines) {
            if (expected.equals(line)) {
                fail(expected + " found in generated data but should not have been present.");
            }
        }
    }

    /**
     * Test that when inserting defaults that a commented-out section is
     * generated for {@code properties} for the given option.
     */
    @Test
    public void testGenerateConfigurationConfigPadPropertiesAdded() {
        Map<String, String> config = new HashMap<>();
        Map<String, String> props = new HashMap<>();
        props.put("foo", "bar");
        String data = generator.generateConfiguration(props, config,
                null, false, null, true, false);
        String[] lines = data.split(System.lineSeparator());
        boolean defined = false;
        String expected = "# option.foo.properties = p1 = x, p2 = y, p3 = z";
        for (String line : lines) {
            if (expected.contains(line)) {
                defined = true;
                break;
            }
        }
        if (!defined) {
            fail("Expected to find " + expected + " in the generated content.");
        }
    }

    /**
     * Test that when inserting defaults that a commented-out section is not
     * generated for {@code type} for the given option.
     */
    @Test
    public void testGenerateConfigurationConfigPadPropertiesAddedWhenConfigDefined() {
        Map<String, String> config = new HashMap<>();
        config.put("option.foo.properties", " p1 = x, p2 = y, p3 = z");
        Map<String, String> props = new HashMap<>();
        props.put("foo", "bar");
        String data = generator.generateConfiguration(props, config,
                null, false, null, true, false);
        String[] lines = data.split(System.lineSeparator());
        String expected = "# option.foo.properties = p1 = x, p2 = y, p3 = z";
        for (String line : lines) {
            if (expected.equals(line)) {
                fail(expected + " found in generated data but should not have been present.");
            }
        }
    }

    /**
     * Test that when inserting defaults that a commented-out section is
     * generated for {@code default} for the given option.
     */
    @Test
    public void testGenerateConfigurationConfigPadDefaultAdded() {
        Map<String, String> config = new HashMap<>();
        Map<String, String> props = new HashMap<>();
        props.put("foo", "bar");
        String data = generator.generateConfiguration(props, config,
                null, false, null, true, false);
        String[] lines = data.split(System.lineSeparator());
        boolean defined = false;
        String expected = "# option.foo.default = x";
        for (String line : lines) {
            if (expected.contains(line)) {
                defined = true;
                break;
            }
        }
        if (!defined) {
            fail("Expected to find " + expected + " in the generated content.");
        }
    }

    /**
     * Test that when inserting defaults that a commented-out section is not
     * generated for {@code default} for the given option.
     */
    @Test
    public void testGenerateConfigurationConfigPadDefaultAddedWhenConfigDefined() {
        Map<String, String> config = new HashMap<>();
        config.put("option.foo.default", "x");
        Map<String, String> props = new HashMap<>();
        props.put("foo", "bar");
        String data = generator.generateConfiguration(props, config,
                null, false, null, true, true);
        String[] lines = data.split(System.lineSeparator());
        String expected = "# option.foo.default = x";
        for (String line : lines) {
            if (expected.equals(line)) {
                fail(expected + " found in generated data but should not have been present.");
            }
        }
    }

    /**
     * Get a basic property map for testing.
     *
     * @return non-{@code null} property map.
     */
    private Map<String, String> getBasicPropertyMap() {
        Map<String, String> map = new HashMap<>();
        map.put("foo-bar", "bar");
        return map;
    }

    /**
     * Get a more complex property map for testing.
     *
     * @return non-{@code null} property map.
     */
    private Map<String, String> getMultiplePropertiesMap() {
        Map<String, String> map = new HashMap<>();
        map.put("host.name", "localhost");
        map.put("host.port", "8080");
        map.put("reports", ClcParser.TRUE);
        map.put("verbose", ClcParser.FALSE);
        map.put("inet.verbose", "");
        return map;
    }

    /**
     * Check to see if any of the lines contain the given key and value
     * separated by the equals symbol.
     *
     * @param lines non-{@code null}, non-empty lines to check.
     *
     * @param key non-{@code null} key to search for.
     *
     * @param value non-{@code null} value to search for.
     *
     * @return {@code true} if any line contains the key and value separated by
     * the equals character; {@code false} otherwise.
     */
    private boolean isDefined(String[] lines, String key, String value) {
        Pattern p = Pattern.compile("^" + key + "\\s*=\\s*" + value + "$");
        boolean defined = false;
        for (String line : lines) {
            Matcher m = p.matcher(line);
            if (m.matches()) {
                defined = true;
                break;
            }
        }
        return defined;
    }

}
