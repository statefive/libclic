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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import org.joor.Reflect;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import static org.statefive.clic.GlobalConfiguration.GLOBAL_HELP_OPTION_NAME;
import static org.statefive.clic.GlobalConfiguration.GLOBAL_HELP_SWITCH_OPTS;
import static org.statefive.clic.GlobalConfiguration.GLOBAL_OPTIONS_OPTS_TYPE;
import static org.statefive.clic.GlobalConfiguration.GLOBAL_VERSION_SWITCH_OPTS;

/**
 *
 */
public class GlobalConfigurationTest {

    /**
     * Test name.
     */
    @Rule
    public TestName name = new TestName();

    /**
     *
     */
    GlobalConfiguration globalConfig;

    /**
     * Inspired by:
     * https://stackoverflow.com/questions/3301635/change-private-static-final-field-using-java-reflection
     */
    @After
    public void tearDown() {
        Reflect.onClass(Clc.class).set("instance", null);
    }

    /**
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        globalConfig = new GlobalConfiguration();
    }

    /**
     * Test of updateGlobalConfiguration method, of class GlobalConfiguration,
     * for option type {@link GlobalConfiguration#GLOBAL_OPTION_TYPE_BOTH}.
     */
    @Test
    public void testUpdateGlobalConfigurationOptionTypeBoth() throws Exception {
        final String data = GlobalConfiguration.GLOBAL_OPTIONS_OPTS_TYPE + "="
                + OptionsTypeEnum.BOTH.getType();
        globalConfig.updateGlobalConfiguration(generateHelpOptionName());
        globalConfig.updateGlobalConfiguration(data);
        assertEquals(OptionsTypeEnum.BOTH, globalConfig.getOptionsType());
    }

    /**
     * Test of updateGlobalConfiguration method, of class GlobalConfiguration,
     * for option type {@link GlobalConfiguration#GLOBAL_OPTION_TYPE_SHORT}.
     */
    @Test
    public void testUpdateGlobalConfigurationOptionTypeShort() throws Exception {
        final String data = GlobalConfiguration.GLOBAL_OPTIONS_OPTS_TYPE + "="
                + OptionsTypeEnum.SHORT.getType();
        globalConfig.updateGlobalConfiguration(generateHelpOptionName());
        globalConfig.updateGlobalConfiguration(data);
        assertEquals(OptionsTypeEnum.SHORT, globalConfig.getOptionsType());
    }

    /**
     * Test of updateGlobalConfiguration method, of class GlobalConfiguration,
     * for option type {@link GlobalConfiguration#GLOBAL_OPTION_TYPE_LONG}.
     */
    @Test
    public void testUpdateGlobalConfigurationOptionTypeLong() throws Exception {
        final String data = GlobalConfiguration.GLOBAL_OPTIONS_OPTS_TYPE + "="
                + OptionsTypeEnum.LONG.getType();
        globalConfig.updateGlobalConfiguration(generateHelpOptionName());
        globalConfig.updateGlobalConfiguration(data);
        assertEquals(OptionsTypeEnum.LONG, globalConfig.getOptionsType());
    }

    /**
     * Test of updateGlobalConfiguration method, of class GlobalConfiguration,
     * for option type {@link GlobalConfiguration#GLOBAL_OPTION_TYPE_LONG}.
     */
    @Test
    public void testUpdateGlobalConfigurationCommandFooter() throws Exception {
        final String data = GlobalConfiguration.GLOBAL_HELP_COMMAND_FOOTER + "="
                + "Some useful footer information";
        globalConfig.updateGlobalConfiguration(generateHelpOptionName());
        globalConfig.updateGlobalConfiguration(data);
        assertEquals("Some useful footer information",
                globalConfig.getHelpCommandFooter());
    }

    /**
     * Test of updateGlobalConfiguration method, of class GlobalConfiguration,
     * for option type {@link GlobalConfiguration#GLOBAL_OPTION_TYPE_LONG}.
     */
    @Test
    public void testUpdateGlobalConfigurationCommandHeader() throws Exception {
        final String data = GlobalConfiguration.GLOBAL_HELP_COMMAND_HEADER + "="
                + "Some useful header information";
        globalConfig.updateGlobalConfiguration(generateHelpOptionName());
        globalConfig.updateGlobalConfiguration(data);
        assertEquals("Some useful header information",
                globalConfig.getHelpCommandHeader());
    }

    /**
     * Test of updateGlobalConfiguration method, of class GlobalConfiguration,
     * for option type {@link GlobalConfiguration#GLOBAL_OPTION_TYPE_LONG}.
     */
    @Test
    public void testUpdateGlobalConfigurationOptionTypeUnknown() throws Exception {
        // The Good, the Bad and the Ugly: Unknown grave, Bill Carson:
        final String data = GlobalConfiguration.GLOBAL_OPTIONS_OPTS_TYPE
                + "=Bill Carson";
        globalConfig.updateGlobalConfiguration(generateHelpOptionName());
        try {
            globalConfig.updateGlobalConfiguration(data);
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals(ex.getMessage(), "Unknown options type: Bill Carson");
        }
    }

    /**
     * Test of updateGlobalConfiguration method, of class GlobalConfiguration,
     * for option type {@link GlobalConfiguration#GLOBAL_OPTION_TYPE_LONG}.
     */
    @Test
    public void testUpdateGlobalConfigurationUnknownOptionType() throws Exception {
        // The Good, the Bad and the Ugly: Unknown grave, Bill Carson:
        final String data = "BILL_CARSON=Bill Carson";
        globalConfig.updateGlobalConfiguration(generateHelpOptionName());
        try {
            globalConfig.updateGlobalConfiguration(data);
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals(ex.getMessage(),
                    "Unknown global configuration declaration: BILL_CARSON");
        }
    }

    /**
     * Test of updateGlobalConfiguration method, testing that when a global
     * option type is specified more than once, an error occurs.
     */
    @Test
    public void testUpdateGlobalConfigurationReDefinedOptionType() throws Exception {
        // The Good, the Bad and the Ugly: Unknown grave, Bill Carson:
        final String data = GlobalConfiguration.GLOBAL_OPTIONS_OPTS_TYPE + "="
                + OptionsTypeEnum.BOTH.getType();
        globalConfig.setOptionsType(OptionsTypeEnum.BOTH);
        globalConfig.updateGlobalConfiguration(generateHelpOptionName());
        try {
            globalConfig.updateGlobalConfiguration(data);
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals(ex.getMessage(), GLOBAL_OPTIONS_OPTS_TYPE
                    + " has already been defined as "
                    + OptionsTypeEnum.BOTH.getType()
                    + " but found second definition: "
                    + OptionsTypeEnum.BOTH.getType());
        }
    }

    /**
     * Test of updateGlobalConfiguration method, testing that a badly named help
     * option throws an exception
     */
    @Test
    public void testUpdateGlobalConfigurationBadHelpOption() throws Exception {
        final String data = "global.help.foo=exception!";
        globalConfig.updateGlobalConfiguration(generateHelpOptionName());
        try {
            globalConfig.updateGlobalConfiguration(data);
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals(ex.getMessage(),
                    "Unknown global help configuration: global.help.foo=exception!");
        }
    }

    /**
     * Test of updateGlobalConfiguration method, testing that a badly named help
     * option throws an exception
     */
    @Test
    public void testUpdateGlobalConfigurationBadVersionOption() throws Exception {
        final String data = "global.version.foo=exception!";
        globalConfig.updateGlobalConfiguration(generateHelpOptionName());
        try {
            globalConfig.updateGlobalConfiguration(data);
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals(ex.getMessage(),
                    "Unknown global version configuration: global.version.foo=exception!");
        }
    }

    /**
     * Test that sorting of options can be set.
     */
    @Test
    public void testUpdateGlobalConfigurationHelpSortOptions() throws Exception {
        final String data = GlobalConfiguration.GLOBAL_HELP_FORMAT_SORT_OPTIONS + "=true";
        globalConfig.updateGlobalConfiguration(generateHelpOptionName());
        globalConfig.updateGlobalConfiguration(data);
        assertTrue(globalConfig.isHelpSortOptions());
    }

    /**
     * Test that auto usage of help options can be set.
     */
    @Test
    public void testUpdateGlobalConfigurationHelpAutoUsage() throws Exception {
        final String data = GlobalConfiguration.GLOBAL_HELP_AUTO_USAGE + "=true";
        globalConfig.updateGlobalConfiguration(generateHelpOptionName());
        globalConfig.updateGlobalConfiguration(data);
        assertTrue(globalConfig.isHelpAutoUsage());
    }

    /**
     * Test that help column spacing can be set.
     */
    @Test
    public void testUpdateGlobalConfigurationHelpFormatColumnSpacing() throws Exception {
        final String data = GlobalConfiguration.GLOBAL_HELP_FORMAT_COLUMN_SPACING + "= 77";
        globalConfig.updateGlobalConfiguration(generateHelpOptionName());
        globalConfig.updateGlobalConfiguration(data);
        assertEquals(77, globalConfig.getHelpColumnSpacing());
    }

    /**
     * Test that setting help column spacing to not a natural number throws an
     * exception.
     */
    @Test
    public void testUpdateGlobalConfigurationHelpFormatColumnSpacingFailsForNotANumber() throws Exception {
        final String data = GlobalConfiguration.GLOBAL_HELP_FORMAT_COLUMN_SPACING + "= twelvty";
        globalConfig.updateGlobalConfiguration(generateHelpOptionName());
        try {
            globalConfig.updateGlobalConfiguration(data);
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals("Invalid " + GlobalConfiguration.GLOBAL_HELP_FORMAT_COLUMN_SPACING
                    + "; must be a number.", ex.getMessage());
        }
    }

    /**
     * Test that help left pad can be set.
     */
    @Test
    public void testUpdateGlobalConfigurationHelpFormatLeftPad() throws Exception {
        final String data = GlobalConfiguration.GLOBAL_HELP_FORMAT_LEFT_PAD + "= 77";
        globalConfig.updateGlobalConfiguration(generateHelpOptionName());
        globalConfig.updateGlobalConfiguration(data);
        assertEquals(77, globalConfig.getHelpLeftPad());
    }

    /**
     * Test that setting help left pad to not a natural number throws an
     * exception.
     */
    @Test
    public void testUpdateGlobalConfigurationHelpFormatLeftPadFailsForNotANumber() throws Exception {
        final String data = GlobalConfiguration.GLOBAL_HELP_FORMAT_LEFT_PAD + "= twelvty";
        globalConfig.updateGlobalConfiguration(generateHelpOptionName());
        try {
            globalConfig.updateGlobalConfiguration(data);
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals("Invalid " + GlobalConfiguration.GLOBAL_HELP_FORMAT_LEFT_PAD
                    + "; must be a number.", ex.getMessage());
        }
    }

    /**
     * Test that help left pad can be set.
     */
    @Test
    public void testUpdateGlobalConfigurationHelpFormatWidth() throws Exception {
        final String data = GlobalConfiguration.GLOBAL_HELP_FORMAT_WIDTH + "= 77";
        globalConfig.updateGlobalConfiguration(generateHelpOptionName());
        globalConfig.updateGlobalConfiguration(data);
        assertEquals(77, globalConfig.getHelpWidth());
    }

    /**
     * Test that setting help left pad to not a natural number throws an
     * exception.
     */
    @Test
    public void testUpdateGlobalConfigurationHelpFormatWidthFailsForNotANumber() throws Exception {
        final String data = GlobalConfiguration.GLOBAL_HELP_FORMAT_WIDTH + "= twelvty";
        globalConfig.updateGlobalConfiguration(generateHelpOptionName());
        try {
            globalConfig.updateGlobalConfiguration(data);
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals("Invalid " + GlobalConfiguration.GLOBAL_HELP_FORMAT_WIDTH
                    + "; must be a number.", ex.getMessage());
        }
    }

    /**
     * Test that help width from environment can be set.
     */
    @Test
    public void testUpdateGlobalConfigurationHelpFormatWidthFromEnvTrue() throws Exception {
        final String data = GlobalConfiguration.GLOBAL_HELP_FORMAT_WIDTH_FROM_ENV + "= true";
        globalConfig.updateGlobalConfiguration(generateHelpOptionName());
        globalConfig.updateGlobalConfiguration(data);
        assertTrue(globalConfig.isHelpWidthFromEnv());
    }

    /**
     * Test that help width from environment can be set.
     */
    @Test
    public void testUpdateGlobalConfigurationHelpFormatWidthFromEnvFalse() throws Exception {
        final String data = GlobalConfiguration.GLOBAL_HELP_FORMAT_WIDTH_FROM_ENV + "= FaLse";
        globalConfig.updateGlobalConfiguration(generateHelpOptionName());
        globalConfig.updateGlobalConfiguration(data);
        assertFalse(globalConfig.isHelpWidthFromEnv());
    }

    /**
     * Test that help width from environment can be set.
     */
    @Test
    public void testUpdateGlobalConfigurationHelpFormatWidthFromEnvFalseSpeltBad() throws Exception {
        final String data = GlobalConfiguration.GLOBAL_HELP_FORMAT_WIDTH_FROM_ENV + "= flase";
        globalConfig.updateGlobalConfiguration(generateHelpOptionName());
        globalConfig.updateGlobalConfiguration(data);
        assertFalse(globalConfig.isHelpWidthFromEnv());
    }

    /**
     * Test that setting help options gets the correct values.
     */
    @Test
    public void testUpdateGlobalConfigurationHelpSwitchOptsAsDefaults() throws Exception {
        final String data = GlobalConfiguration.GLOBAL_HELP_SWITCH_OPTS + "=H/RTFM";
        globalConfig.updateGlobalConfiguration(generateHelpOptionName());
        globalConfig.updateGlobalConfiguration(data);
        assertEquals("H", globalConfig.getHelpOptionShort());
        assertEquals("RTFM", globalConfig.getHelpOptionLong());
    }

    /**
     * Test that setting help options for short option gets the correct values.
     */
    @Test
    public void testUpdateGlobalConfigurationHelpSwitchOptsAsDefaultsForShort() throws Exception {
        final String data = GlobalConfiguration.GLOBAL_HELP_SWITCH_OPTS + "=H";
        globalConfig.updateGlobalConfiguration(generateHelpOptionName());
        globalConfig.updateGlobalConfiguration(data);
        assertEquals("H", globalConfig.getHelpOptionShort());
    }

    /**
     * Test that setting help options for long option gets the correct values.
     */
    @Test
    public void testUpdateGlobalConfigurationHelpSwitchOptsAsDefaultsForLong() throws Exception {
        final String data = GlobalConfiguration.GLOBAL_HELP_SWITCH_OPTS + "=RTFM";
        globalConfig.updateGlobalConfiguration(generateHelpOptionName());
        globalConfig.updateGlobalConfiguration(data);
        assertEquals("RTFM", globalConfig.getHelpOptionLong());
    }

    /**
     * Test that redefining global switch options for help creates an error.
     */
    @Test
    public void testUpdateGlobalConfigurationHelpSwitchOptsDefinedTwice() throws Exception {
        final String data = GlobalConfiguration.GLOBAL_HELP_SWITCH_OPTS + "=H/RTFM";
        globalConfig.updateGlobalConfiguration(generateHelpOptionName());
        globalConfig.updateGlobalConfiguration(data);
        try {
            globalConfig.updateGlobalConfiguration(data);
        } catch (ClcException ex) {
            assertEquals(GLOBAL_HELP_SWITCH_OPTS + " has already been defined.",
                    ex.getMessage());
        }
    }

    /**
     * Test that redefining global switch options for help creates an error.
     */
    @Test
    public void testUpdateGlobalConfigurationHelpSwitchOptsDefinedTwiceForLongOption() throws Exception {
        final String data = GlobalConfiguration.GLOBAL_HELP_SWITCH_OPTS + "=RTFM";
        globalConfig.updateGlobalConfiguration(generateHelpOptionName());
        globalConfig.updateGlobalConfiguration(data);
        try {
            globalConfig.updateGlobalConfiguration(data);
        } catch (ClcException ex) {
            assertEquals(GLOBAL_HELP_SWITCH_OPTS + " has already been defined.",
                    ex.getMessage());
        }
    }

    /**
     * Test that redefining global switch options for help creates an error.
     */
    @Test
    public void testUpdateGlobalConfigurationHelpSwitchOptsDefinedTwiceForShortOption() throws Exception {
        final String data = GlobalConfiguration.GLOBAL_HELP_SWITCH_OPTS + "=H";
        globalConfig.updateGlobalConfiguration(generateHelpOptionName());
        globalConfig.updateGlobalConfiguration(data);
        try {
            globalConfig.updateGlobalConfiguration(data);
        } catch (ClcException ex) {
            assertEquals(GLOBAL_HELP_SWITCH_OPTS + " has already been defined.",
                    ex.getMessage());
        }
    }

    /**
     * Test that setting version options gets the correct values.
     */
    @Test
    public void testUpdateGlobalConfigurationVersionSwitchOptsAsDefaults() throws Exception {
        final String data = GlobalConfiguration.GLOBAL_VERSION_SWITCH_OPTS + "=V/what-is-it-that-it-is";
        globalConfig.updateGlobalConfiguration(generateHelpOptionName());
        globalConfig.updateGlobalConfiguration(data);
        assertEquals("V", globalConfig.getVersionOptionShort());
        assertEquals("what-is-it-that-it-is", globalConfig.getVersionOptionLong());
    }

    /**
     * Test that redefining global switch options for version creates an error.
     */
    @Test
    public void testUpdateGlobalConfigurationVersionSwitchOptsDefinedTwice() throws Exception {
        final String data = GlobalConfiguration.GLOBAL_VERSION_SWITCH_OPTS + "=V/what-is-it-that-it-is";
        globalConfig.updateGlobalConfiguration(generateHelpOptionName());
        globalConfig.updateGlobalConfiguration(data);
        try {
            globalConfig.updateGlobalConfiguration(data);
        } catch (ClcException ex) {
            assertEquals(GLOBAL_VERSION_SWITCH_OPTS + " has already been defined.",
                    ex.getMessage());
        }
    }

    /**
     * Test that redefining global switch options for version creates an error.
     */
    @Test
    public void testUpdateGlobalConfigurationVersionSwitchOptsDefinedTwiceForLongOption() throws Exception {
        final String data = GlobalConfiguration.GLOBAL_VERSION_SWITCH_OPTS + "=what-is-it-that-it-is";
        globalConfig.updateGlobalConfiguration(generateHelpOptionName());
        globalConfig.updateGlobalConfiguration(data);
        try {
            globalConfig.updateGlobalConfiguration(data);
        } catch (ClcException ex) {
            assertEquals(GLOBAL_VERSION_SWITCH_OPTS + " has already been defined.",
                    ex.getMessage());
        }
    }

    /**
     * Test that redefining global switch options for version creates an error.
     */
    @Test
    public void testUpdateGlobalConfigurationVersionSwitchOptsDefinedTwiceForShortOption() throws Exception {
        final String data = GlobalConfiguration.GLOBAL_VERSION_SWITCH_OPTS + "=V";
        globalConfig.updateGlobalConfiguration(generateHelpOptionName());
        globalConfig.updateGlobalConfiguration(data);
        try {
            globalConfig.updateGlobalConfiguration(data);
        } catch (ClcException ex) {
            assertEquals(GLOBAL_VERSION_SWITCH_OPTS + " has already been defined.",
                    ex.getMessage());
        }
    }

    /**
     * Test that adding default help sets the correct global configuration
     * values.
     */
    @Test
    public void testAddDefaultHelp() {
        globalConfig.addDefaultHelp();
        assertEquals(GlobalConfiguration.GLOBAL_HELP_OPTION_SHORT_DEFAULT,
                globalConfig.getHelpOptionShort());
        assertEquals(GlobalConfiguration.GLOBAL_HELP_OPTION_LONG_DEFAULT,
                globalConfig.getHelpOptionLong());
    }

    /**
     * Test that adding default version sets the correct global configuration
     * values.
     */
    @Test
    public void testAddDefaultVersion() {
        globalConfig.addDefaultVersion();
        assertEquals(GlobalConfiguration.GLOBAL_VERSION_OPTION_SHORT_DEFAULT,
                globalConfig.getVersionOptionShort());
        assertEquals(GlobalConfiguration.GLOBAL_VERSION_OPTION_LONG_DEFAULT,
                globalConfig.getVersionOptionLong());
    }

    /**
     * Test of addOptionConfiguration method, of class GlobalConfiguration.
     */
    @Test
    public void testAddOptionConfiguration() {
    }

    /**
     * Test of getCommandOptionConfigurations method, of class
     * GlobalConfiguration.
     */
    @Test
    public void testGetOptionMap() {
    }

    /**
     * Test of getOptionsType method, of class GlobalConfiguration.
     */
    @Test
    public void testGetOptionsType() {
    }

    /**
     * Test of setOptionsType method, of class GlobalConfiguration.
     */
    @Test
    public void testSetOptionsType() {
    }

    /**
     *
     * @return
     */
    private String generateHelpOptionName() {
        return GLOBAL_HELP_OPTION_NAME + "=showHelp\n";
    }

    /**
     * Test that the minimum length is 0 when no argument configuration is set.
     *
     * @throws Exception
     */
    @Test
    public void testNoArgsConfig() throws Exception {
        assertEquals(Integer.valueOf(0), globalConfig.getArgsMinLength());
    }

    /**
     * Test that the minimum length os 0 for a command with no argument
     * configuration.
     *
     * @throws Exception
     */
    @Test
    public void testCommandNoArgsConfig() throws Exception {
        assertEquals(Integer.valueOf(0), globalConfig.getArgsMinLength(new Command()));
    }

    /**
     * Test that when a configuration is unbounded and non-optional, the minimum
     * length is 1.
     *
     * @throws Exception
     */
    @Test
    public void testArgsConfigMinLengthNoLengthSetNonOptional() throws Exception {
        globalConfig.addArgsConfiguration(create(0, null, false));
        assertEquals(Integer.valueOf(1), globalConfig.getArgsMinLength());
    }

    /**
     * Test that when a configuration for a command is unbounded and
     * non-optional, the minimum length is 1.
     *
     * @throws Exception
     */
    @Test
    public void testCommandArgsConfigMinLengthNoLengthSetNonOptional() throws Exception {
        Command cmd = create(new Command(), 0, null, false);
        assertEquals(Integer.valueOf(1), globalConfig.getArgsMinLength(cmd));
    }

    /**
     * Test that when a configuration is unbounded and optional, the minimum
     * length is 1.
     *
     * @throws Exception
     */
    @Test
    public void testArgsConfigMinLengthNoLengthSetOptional() throws Exception {
        globalConfig.addArgsConfiguration(create(0, null, true));
        assertEquals(Integer.valueOf(0), globalConfig.getArgsMinLength());
    }

    /**
     * Test that when a configuration for a command is unbounded and optional,
     * the minimum length is 1.
     *
     * @throws Exception
     */
    @Test
    public void testCommandArgsConfigMinLengthNoLengthSetOptional() throws Exception {
        Command cmd = create(new Command(), 0, null, true);
        assertEquals(Integer.valueOf(0), globalConfig.getArgsMinLength(cmd));
    }

    /**
     * Test that a non-optional configuration of length 3 sets the minimum
     * length to 3.
     *
     * @throws Exception
     */
    @Test
    public void testArgsConfigMinLengthLengthSetTo3NonOptional() throws Exception {
        globalConfig.addArgsConfiguration(create(0, 3, false));
        assertEquals(Integer.valueOf(3), globalConfig.getArgsMinLength());
    }

    /**
     * Test that a non-optional command configuration of length 3 sets the
     * minimum length to 3.
     *
     * @throws Exception
     */
    @Test
    public void testCommandArgsConfigMinLengthLengthSetTo3NonOptional() throws Exception {
        Command cmd = create(new Command(), 0, 3, false);
        assertEquals(Integer.valueOf(3), globalConfig.getArgsMinLength(cmd));
    }

    /**
     * Test that an optional configuration of length 3 has minimum length 0.
     *
     * @throws Exception
     */
    @Test
    public void testArgsConfigMinLengthLengthSetTo3Optional() throws Exception {
        globalConfig.addArgsConfiguration(create(0, 3, true));
        assertEquals(Integer.valueOf(0), globalConfig.getArgsMinLength());
    }

    /**
     * Test that an optional command configuration of length 3 has minimum
     * length 0.
     *
     * @throws Exception
     */
    @Test
    public void testCommandArgsConfigMinLengthLengthSetTo3Optional() throws Exception {
        Command cmd = create(new Command(), 0, 3, true);
        assertEquals(Integer.valueOf(0), globalConfig.getArgsMinLength(cmd));
    }

    /**
     * Test that for two configurations of length 3 and 2 respectively that are
     * both non-optional, the minimum length is 5.
     *
     * @throws Exception
     */
    @Test
    public void test2ArgsConfigsFixedBothNonOptional() throws Exception {
        globalConfig.addArgsConfiguration(create(0, 3, false));
        globalConfig.addArgsConfiguration(create(1, 2, false));
        assertEquals(Integer.valueOf(5), globalConfig.getArgsMinLength());
    }

    /**
     * Test that for two command configurations of length 3 and 2 respectively
     * that are both non-optional, the minimum length is 5.
     *
     * @throws Exception
     */
    @Test
    public void testCommmand2ArgsConfigsFixedBothNonOptional() throws Exception {
        Command cmd1 = create(new Command(), 0, 3, false);
        cmd1 = create(cmd1, 1, 2, false);
        globalConfig.addArgsConfiguration(create(1, 2, false));
        assertEquals(Integer.valueOf(5), globalConfig.getArgsMinLength(cmd1));
    }

    /**
     * Test that for a non-optional configuration of length 3 and an optional
     * configuration of length 2 that the minimum length is 3.
     *
     * @throws Exception
     */
    @Test
    public void test2ArgsConfigsFixedBothLastOptional() throws Exception {
        globalConfig.addArgsConfiguration(create(0, 3, false));
        globalConfig.addArgsConfiguration(create(1, 2, true));
        assertEquals(Integer.valueOf(3), globalConfig.getArgsMinLength());
    }

    /**
     * Test that for a command configuration, a non-optional configuration of
     * length 3 and an optional configuration of length 2 that the minimum
     * length is 3.
     *
     * @throws Exception
     */
    @Test
    public void testCommand2ArgsConfigsFixedBothLastOptional() throws Exception {
        Command cmd1 = create(new Command(), 0, 3, false);
        cmd1 = create(cmd1, 1, 2, true);
        assertEquals(Integer.valueOf(3), globalConfig.getArgsMinLength(cmd1));
    }

    /**
     * Test that for a configuration, a non-optional configuration of length 3
     * and an optional configuration of length 2 that the minimum length is 3.
     *
     * @throws Exception
     */
    @Test
    public void test2ArgsConfigs1Fixed1UnboundedBothNonOptional() throws Exception {
        globalConfig.addArgsConfiguration(create(0, 3, false));
        globalConfig.addArgsConfiguration(create(1, null, false));
        assertEquals(Integer.valueOf(4), globalConfig.getArgsMinLength());
    }

    /**
     * Test that for a command configuration, a non-optional configuration of
     * length 3 and an optional configuration of length 2 that the minimum
     * length is 3.
     *
     * @throws Exception
     */
    @Test
    public void testCommand2ArgsConfigs1Fixed1UnboundedBothNonOptional() throws Exception {
        Command cmd1 = create(new Command(), 0, 3, false);
        cmd1 = create(cmd1, 1, null, false);
        assertEquals(Integer.valueOf(4), globalConfig.getArgsMinLength(cmd1));
    }

    /**
     * Test that with no argument configuration the maximum length
     * {@link Integer#MAX_VALUE}.
     *
     * @throws Exception
     */
    @Test
    public void testNoArgsConfigMaxLength() throws Exception {
        assertEquals(Integer.valueOf(Integer.MAX_VALUE), globalConfig.getArgsMaxLength());
    }

    /**
     * Test that with no command argument configuration the maximum length
     * {@link Integer#MAX_VALUE}.
     *
     * @throws Exception
     */
    @Test
    public void testCommandNoArgsConfigMaxLength() throws Exception {
        assertEquals(Integer.valueOf(Integer.MAX_VALUE), globalConfig.getArgsMaxLength(new Command()));
    }

    /**
     * Test that an argument configuration with unbounded non-optional arguments
     * has maximum length {@link Integer#MAX_VALUE}.
     *
     * @throws Exception
     */
    @Test
    public void testArgsConfigMaxLengthNoLengthSetNonOptional() throws Exception {
        globalConfig.addArgsConfiguration(create(0, null, false));
        assertEquals(Integer.valueOf(Integer.MAX_VALUE), globalConfig.getArgsMaxLength());
    }

    /**
     * Test that a command argument configuration with unbounded non-optional
     * arguments has maximum length {@link Integer#MAX_VALUE}.
     *
     * @throws Exception
     */
    @Test
    public void testCommandArgsConfigMaxLengthNoLengthSetNonOptional() throws Exception {
        Command cmd1 = create(new Command(), 0, null, false);
        assertEquals(Integer.valueOf(Integer.MAX_VALUE), globalConfig.getArgsMaxLength(cmd1));
    }

    /**
     * Test that an argument configuration with unbounded optional arguments has
     * maximum length {@link Integer#MAX_VALUE}.
     *
     * @throws Exception
     */
    @Test
    public void testArgsConfigMaxLengthNoLengthSetOptional() throws Exception {
        globalConfig.addArgsConfiguration(create(0, null, true));
        assertEquals(Integer.valueOf(Integer.MAX_VALUE), globalConfig.getArgsMaxLength());
    }

    /**
     * Test that a command argument configuration with unbounded optional
     * arguments has maximum length {@link Integer#MAX_VALUE}.
     *
     * @throws Exception
     */
    @Test
    public void testCommandArgsConfigMaxLengthNoLengthSetOptional() throws Exception {
        Command cmd1 = create(new Command(), 0, null, true);
        assertEquals(Integer.valueOf(Integer.MAX_VALUE), globalConfig.getArgsMaxLength(cmd1));
    }

    /**
     * Test that an argument configuration with length 3 non-optional arguments
     * has maximum length 3.
     *
     * @throws Exception
     */
    @Test
    public void testArgsConfigMaxLengthLength3NonOptional() throws Exception {
        globalConfig.addArgsConfiguration(create(0, 3, false));
        assertEquals(Integer.valueOf(3), globalConfig.getArgsMaxLength());
    }

    /**
     * Test that a command argument configuration with length 3 non-optional
     * arguments has maximum length 3.
     *
     * @throws Exception
     */
    @Test
    public void testCommandArgsConfigMaxLengthLength3NonOptional() throws Exception {
        Command cmd1 = create(new Command(), 0, 3, false);
        assertEquals(Integer.valueOf(3), globalConfig.getArgsMaxLength(cmd1));
    }

    /**
     * Test that an argument configuration with length 3 optional arguments has
     * maximum length 3.
     *
     * @throws Exception
     */
    @Test
    public void testArgsConfigMaxLengthLength3Optional() throws Exception {
        globalConfig.addArgsConfiguration(create(0, 3, true));
        assertEquals(Integer.valueOf(3), globalConfig.getArgsMaxLength());
    }

    /**
     * Test that a command argument configuration with length 3 optional
     * arguments has maximum length 3.
     *
     * @throws Exception
     */
    @Test
    public void testCommandArgsConfigMaxLengthLength3Optional() throws Exception {
        Command cmd1 = create(new Command(), 0, 3, true);
        assertEquals(Integer.valueOf(3), globalConfig.getArgsMaxLength(cmd1));
    }

    /**
     * Test that two configurations, length 3 and 2 that are both non-optional
     * has a maximum length of 5.
     *
     * @throws Exception
     */
    @Test
    public void test2ArgsConfigsMaxLength3n2NonOptional() throws Exception {
        globalConfig.addArgsConfiguration(create(0, 3, false));
        globalConfig.addArgsConfiguration(create(1, 2, false));
        assertEquals(Integer.valueOf(5), globalConfig.getArgsMaxLength());
    }

    /**
     * Test that two argument configurations, length 3 and 2 that are both
     * non-optional has a maximum length of 5.
     *
     * @throws Exception
     */
    @Test
    public void testCommand2ArgsConfigsMaxLength3n2NonOptional() throws Exception {
        Command cmd1 = create(new Command(), 0, 3, false);
        cmd1 = create(cmd1, 1, 2, false);
        assertEquals(Integer.valueOf(5), globalConfig.getArgsMaxLength(cmd1));
    }

    /**
     * Test that two argument configurations, length 3 non-optional, length 2
     * optional, has a maximum length of 5.
     *
     * @throws Exception
     */
    @Test
    public void test2ArgsConfigsMaxLength3n21NonOptional1Optional() throws Exception {
        globalConfig.addArgsConfiguration(create(0, 3, false));
        globalConfig.addArgsConfiguration(create(1, 2, true));
        assertEquals(Integer.valueOf(5), globalConfig.getArgsMaxLength());
    }

    /**
     * Test that two command argument configurations, length 3 non-optional,
     * length 2 optional, has a maximum length of 5.
     *
     * @throws Exception
     */
    @Test
    public void testCommand2ArgsConfigsMaxLength3n21NonOptional1Optional() throws Exception {
        Command cmd1 = create(new Command(), 0, 3, false);
        cmd1 = create(cmd1, 1, 2, true);
        assertEquals(Integer.valueOf(5), globalConfig.getArgsMaxLength(cmd1));
    }

    /**
     * Test that two configurations, 1 non-optional with length 3 and an
     * unbounded non-optional has length {@link Integer#MAX_VALUE}.
     *
     * @throws Exception
     */
    @Test
    public void test2ArgsConfigsMaxLen3AndNoMaxLengthBothNonOptional() throws Exception {
        globalConfig.addArgsConfiguration(create(0, 3, false));
        globalConfig.addArgsConfiguration(create(1, null, false));
        assertEquals(Integer.valueOf(Integer.MAX_VALUE), globalConfig.getArgsMaxLength());
    }

    /**
     * Test that two argument configurations, 1 non-optional with length 3 and
     * an unbounded non-optional has length {@link Integer#MAX_VALUE}.
     *
     * @throws Exception
     */
    @Test
    public void testCommand2ArgsConfigsMaxLen3AndNoMaxLengthBothNonOptional() throws Exception {
        Command cmd1 = create(new Command(), 0, 3, false);
        cmd1 = create(cmd1, 1, null, false);
        assertEquals(Integer.valueOf(Integer.MAX_VALUE), globalConfig.getArgsMaxLength(cmd1));
    }

    /**
     * Test that when two argument configurations that are both non-optional
     * with two very large numbers that exceed {@link Integer#MAX_VALUE} has
     * maximum length {@link Integer#MAX_VALUE}.
     *
     * @throws Exception
     */
    @Test
    public void test2ArgsConfigsMaxLenSillyForBothBothNonOptional() throws Exception {
        int sillyBigNumber1 = (Integer.MAX_VALUE / 2) + 1;
        int sillyBigNumber2 = (Integer.MAX_VALUE / 2) + 1;
        // ^ together these will exceed Integer MAX_VALUE
        globalConfig.addArgsConfiguration(create(0, sillyBigNumber1, false));
        globalConfig.addArgsConfiguration(create(1, sillyBigNumber2, false));
        assertEquals(Integer.valueOf(Integer.MAX_VALUE), globalConfig.getArgsMaxLength());
    }

    /**
     * Test that when two command argument configurations that are both
     * non-optional with two very large numbers that exceed
     * {@link Integer#MAX_VALUE} has maximum length {@link Integer#MAX_VALUE}.
     *
     * @throws Exception
     */
    @Test
    public void test2CommandArgsConfigsMaxLenSillyForBothBothNonOptional() throws Exception {
        int sillyBigNumber1 = (Integer.MAX_VALUE / 2) + 1;
        int sillyBigNumber2 = (Integer.MAX_VALUE / 2) + 1;
        // ^ together these will exceed Integer MAX_VALUE
        Command cmd1 = create(new Command(), 0, sillyBigNumber1, false);
        cmd1 = create(cmd1, 1, sillyBigNumber2, false);
        assertEquals(Integer.valueOf(Integer.MAX_VALUE), globalConfig.getArgsMaxLength(cmd1));
    }

    /**
     * Test that the ${resource:...} declaration includes the correct resource.
     */
    @Test
    public void testProcessParseResourcesSuccessfully() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_113_include_resource.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "-h".split(" ");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        cliConfig.process(is, "UTF-8", arguments);
        String output = os.toString("UTF8");
        assertTrue(output.contains("ABC Sample help header information, obtained from file. XYZ"));
        ps.close();
        os.close();
    }

    /**
     * Test that the ${resource:...} declaration includes the correct resource.
     */
    @Test
    public void testProcessParseResourcesFails() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_114_include_resource_errors.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "-h".split(" ");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        try {
            cliConfig.process(is, "UTF-8", arguments);
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals("Line no. 19: null", ex.getMessage());
        }
    }

    /**
     * Test that the {@code ${resource:...}} declaration includes the correct resource
     * and parse any {@code ${manifest:...}} entries within the resource.
     */
    @Test
    public void testProcessParseResourcesWithManifestEntriesSuccessfully() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_115_include_resource.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "-h".split(" ");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        cliConfig.process(is, "UTF-8", arguments);
        String output = os.toString("UTF8");
        assertTrue(output.contains("ABC Sample header information with company foobar and version 1.0. XYZ"));
        ps.close();
        os.close();
    }

    /**
     * Test that the when specifying parse substitutions as {@code false},
     * resource entries are left in-place and not substituted.
     */
    @Test
    public void testProcessNoParseResourcesWithManifestEntriesSuccessfully() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_115_include_resource.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "-h".split(" ");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        cliConfig.process(is, "UTF-8", arguments, false);
        String output = os.toString("UTF8");
        assertTrue(output.contains("ABC ${resource:/config/config_115b_include_resource_header.txt} XYZ"));
        ps.close();
        os.close();
    }

    /**
     * Create a simple argument configuration.
     *
     * <p>
     * Callers should be aware that only the last configuration can ever be
     * optional or unbounded (i.e. length {@code null}); this is dealt with by a
     * different part of the API with regard to error checking and should be
     * used carefully when testing.
     *
     * @param len length of the configuration; must be zero or greater or be
     * {@code null}.
     *
     * @param optional {@code true} for optional, {@code false} or {@code null}
     * otherwise.
     *
     * @return non-{@code null} argument configuration.
     *
     * @throws ClcException
     */
    private ArgsConfiguration create(int index, Integer len, Boolean optional) throws ClcException {
        ArgsConfiguration argsConfig = new ArgsConfiguration();
        argsConfig.setLength(len);
        if (optional != null) {
            argsConfig.setOptional(optional);
        }
        argsConfig.setName(String.valueOf(index));

        return argsConfig;
    }

    /**
     * Create a simple argument configuration.
     *
     * <p>
     * Callers should be aware that only the last command argument configuration
     * can ever be optional or unbounded (i.e. length {@code null}); this is
     * dealt with by a different part of the API with regard to error checking
     * and should be used carefully when testing.
     *
     * @param len length of the configuration; must be zero or greater or be
     * {@code null}.
     *
     * @param optional {@code true} for optional, {@code false} or {@code null}
     * otherwise.
     *
     * @return non-{@code null} argument configuration.
     *
     * @throws ClcException
     */
    private Command create(Command cmd, int index, Integer len, Boolean optional) throws ClcException {
        Command command = null;
        if (cmd == null) {
            command = new Command();
            command.setName(String.valueOf(index));
        } else {
            command = cmd;
        }
        ArgsConfiguration argsConfig = new ArgsConfiguration();
        argsConfig.setLength(len);
        if (optional != null) {
            argsConfig.setOptional(optional);
        }
        argsConfig.setName(String.valueOf(index));
        command.addArgsConfiguration(String.valueOf(index), argsConfig);
        return command;
    }

}
