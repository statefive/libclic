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

import java.io.InputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.statefive.clic.GlobalConfiguration.GLOBAL_OPTIONS_OPTS_TYPE;
import org.statefive.clic.valuetype.FileType;
import org.statefive.clic.valuetype.IntegralType;

/**
 *
 */
public class ClcParserTest {

    @BeforeClass
    public static void setUpClass() {
        Clc.initialiseValueTypeFactory();
    }

    /**
     * Set the default outputstream to print to stderr since JUnit no longer
     * prints to stdout (it still won't write to stdout, but will now write to a
     * file in this creates a file
     */
    @Before
    public void setUp() throws Exception {
        System.setOut(System.err);
        System.setErr(System.out);
    }

    /**
     * Test of parse method, of class ClcParser.
     */
    @Test
    public void testParseOpts() throws Exception {
        String text = "option.fail.opts=F/fail";
        Pattern p = Pattern.compile(ClcParser.OPTION_REGEX_BASIC_LINE);
        Matcher m = p.matcher(text);
        assertTrue(m.matches());
        assertEquals("fail", m.group(1));
        assertEquals("opts", m.group(2));
        assertEquals("F/fail", m.group(3));
    }

    /**
     * Test of parse method, of class ClcParser.
     */
    @Test
    public void testParseHasArg() throws Exception {
        String text = "option.fail.hasArg=true";
        Pattern p = Pattern.compile(ClcParser.OPTION_REGEX_BASIC_LINE);
        Matcher m = p.matcher(text);
        assertTrue(m.matches());
        assertEquals("fail", m.group(1));
        assertEquals("hasArg", m.group(2));
        assertEquals(ClcParser.TRUE, m.group(3));
    }

    /**
     * Test of parse method, of class ClcParser.
     */
    @Test
    public void testParseDescription() throws Exception {
        String text = "option.fail.description=fail gracefully.";
        Pattern p = Pattern.compile(ClcParser.OPTION_REGEX_BASIC_LINE);
        Matcher m = p.matcher(text);
        assertTrue(m.matches());
        assertEquals("fail", m.group(1));
        assertEquals("description", m.group(2));
        assertEquals("fail gracefully.", m.group(3));
    }

    /**
     * Test of parse method, of class ClcParser.
     */
    @Test
    public void testParseDescriptionEscaped() throws Exception {
        String text = "option.fail.description=fail gracefully, \\";
        Pattern p = Pattern.compile(ClcParser.OPTION_REGEX_BASIC_LINE);
        Matcher m = p.matcher(text);
        assertTrue(m.matches());
        assertEquals("fail", m.group(1));
        assertEquals("description", m.group(2));
        assertEquals("fail gracefully, \\", m.group(3));
    }

    /**
     * Test of parse method, of class ClcParser.
     */
    @Test
    public void testParseInputStream() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_001_short_and_long_options.clc");
        GlobalConfiguration globalConfig = configParser.parse(is, "UTF-8");
        Map<String, OptionConfiguration> optionConfig = globalConfig.getOptionConfigurations();
        is.close();
        OptionConfiguration optConfig = optionConfig.get("fail");
        checkOptionConfiguration(optConfig, "fail", "F", "fail",
                "Fail if no connection made, rather than retrying.", false);
        optConfig = optionConfig.get("host");
        checkOptionConfiguration(optConfig, "host", "h", "host",
                "Specify the host; optional. Use localhost if not set."
                + " Protocol is optional, assumes HTTP.", true);
        optConfig = optionConfig.get("port");
        checkOptionConfiguration(optConfig, "port", "p", "port",
                "Port number to use. Required.", true);
        optConfig = optionConfig.get("path");
        checkOptionConfiguration(optConfig, "path", "P", "path",
                "Comma separated list of paths to test on the server."
                + " If spaces are contained within the arguments, surround with"
                + " double quotes. Have as many lines as required, so long as"
                + " they are escaped.", true);
    }

    /**
     * Test of parse method, of class ClcParser.
     */
    @Test
    public void testParseInputStreamShortOption() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_002_short_options.clc");
        GlobalConfiguration globalConfig = configParser.parse(is, "UTF-8");
        Map<String, OptionConfiguration> optionConfig = globalConfig.getOptionConfigurations();
        is.close();
        OptionConfiguration optConfig = optionConfig.get("fail");
        checkOptionConfiguration(optConfig, "fail", "F", null,
                "Fail if no connection made, rather than retrying.", false);
        optConfig = optionConfig.get("host");
        checkOptionConfiguration(optConfig, "host", "h", null,
                "Specify the host; optional. Use localhost if not set."
                + " Protocol is optional, assumes HTTP.", true);
        optConfig = optionConfig.get("port");
        checkOptionConfiguration(optConfig, "port", "p", null,
                "Port number to use. Required.", true);
        optConfig = optionConfig.get("path");
        checkOptionConfiguration(optConfig, "path", "P", null,
                "Comma separated list of paths to test on the server."
                + " If spaces are contained within the arguments, surround with"
                + " double quotes. Have as many lines as required, so long as"
                + " they are escaped.", true);
    }

    /**
     * Test of parse method, of class ClcParser.
     */
    @Test
    public void testParseInputStreamLongOption() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_003_long_options.clc");
        GlobalConfiguration globalConfig = configParser.parse(is, "UTF-8");
        Map<String, OptionConfiguration> optionConfig = globalConfig.getOptionConfigurations();
        is.close();
        OptionConfiguration optConfig = optionConfig.get("fail");
        checkOptionConfiguration(optConfig, "fail", null, "fail",
                "Fail if no connection made, rather than retrying.", false);
        optConfig = optionConfig.get("host");
        checkOptionConfiguration(optConfig, "host", null, "host",
                "Specify the host; optional. Use localhost if not set."
                + " Protocol is optional, assumes HTTP.", true);
        optConfig = optionConfig.get("port");
        checkOptionConfiguration(optConfig, "port", null, "port",
                "Port number to use. Required.", true);
        optConfig = optionConfig.get("path");
        checkOptionConfiguration(optConfig, "path", null, "path",
                "Comma separated list of paths to test on the server."
                + " If spaces are contained within the arguments, surround with"
                + " double quotes. Have as many lines as required, so long as"
                + " they are escaped.", true);
    }

    /**
     * Test that empty opts value throws an exception.
     */
    @Test
    public void testParseInputStreamZeroLengthOption() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_005_empty_opts_value.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected exception.");
        } catch (ClcException ex) {
            assertTrue(ex.getMessage().contains("Empty option value; must be a"
                    + " non-zero length string"));
        }
    }

    /**
     * Test that having no options at all throws an exception.
     */
    @Test
    public void testParseInputStreamNoOptionsAtAll() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_006_no_options.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected exception.");
        } catch (ClcException ex) {
            assertTrue(ex.getMessage().contains("The configuration file"
                    + " contained no options to parse"));
        }
    }

    /**
     * Test that an unknown sub-option throws an exception.
     */
    @Test
    public void testParseInputStreamBadSubOptionName() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_007_unknown_config_option.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected exception.");
        } catch (ClcException ex) {
            assertTrue(ex.getMessage().contains(
                    "Unknown configuration option: "));
        }
    }

    /**
     * Test that having no white space at the start of a succeeding line
     * following in from an escaped line throws an exception.
     */
    @Test
    public void testParseInputStreamBadEscapedLine() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_008_invalid_escaped_line.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected exception.");
        } catch (ClcException ex) {
            assertTrue(ex.getMessage().contains("Invalid escaped line: "));
        }
    }

    /**
     * Test that using type BOTH when not formatted correctly throws an
     * exception.
     */
    @Test
    public void testParseInputStreamBadLongShortOptionFormat() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_009_invalid_short_long_format.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected exception.");
        } catch (ClcException ex) {
            assertTrue(ex.getMessage().contains("Invalid short and"
                    + " long option format; must be [character]/"
                    + "[text] but found "));
        }
    }

    /**
     * Test that an invalid character for opts throws an exception for SHORT
     * option.
     */
    @Test
    public void testParseInputStreamBadShortOptionFormat() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_010_invalid_short_format.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected exception.");
        } catch (ClcException ex) {
            assertTrue(ex.getMessage().contains("Expected single"
                    + " character for short option but found "));
        }
    }

    /**
     * Test that invalid characters for LONG option fails.
     */
    @Test
    public void testParseInputStreamBadLongOptionFormat() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_011_invalid_long_option_format.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected exception.");
        } catch (ClcException ex) {
            assertTrue(ex.getMessage().contains("Expected text"
                    + " for long option but found "));
        }
    }

    /**
     * Test that completely invalid option throws an exception.
     */
    @Test
    public void testParseInputStreamBadLineOptionFormat() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_012_invalid_option_definition.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected exception.");
        } catch (ClcException ex) {
            assertTrue(ex.getMessage().contains("Invalid option definition: "));
        }
    }

    /**
     * Test that when the user defines a correct option, if a succeeding option
     * contains an empty name, the error message will also inform them of what
     * option type they're using (short, long, both).
     */
    @Test
    public void testParseInputStreamZeroLength2ndOption() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_013_empty_option_value.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected exception.");
        } catch (ClcException ex) {
            assertTrue(ex.getMessage().contains("Empty option value; must be a"
                    + " non-zero length string; global configuration is defined as "));
        }
    }

    /**
     * Test that a repeated global option declaration throws the appropriate
     * exception.
     */
    @Test
    public void testParseInputStreamRepeatedGlobalConfig() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_014_option_type_defined_twice.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected exception.");
        } catch (ClcException ex) {
            assertTrue(ex.getMessage().contains(GLOBAL_OPTIONS_OPTS_TYPE
                    + " has already been defined as "
                    + OptionsTypeEnum.BOTH.getType()
                    + " but found second definition: "
                    + OptionsTypeEnum.BOTH.getType()));
        }
    }

    /**
     * Test that a global option defined after common "option." options throws
     * an exception.
     */
    @Test
    public void testParseInputStreamBadGlobalConfiguration() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_020_bad_global_config_order.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected exception.");
        } catch (ClcException ex) {
            assertTrue(ex.getMessage().contains("Invalid global"
                    + " configuration definition; global configurations"
                    + " must come BEFORE standard \"option...\" definitions"));
        }
    }

    /**
     * Test that a redefinition of a long option throws an exception.
     */
    @Test
    public void testParseInputStreamReDefinitionLongOption() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_021_redefinition_long_option.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected exception.");
        } catch (ClcException ex) {
            assertTrue(ex.getMessage().contains(
                    "opts has already been defined for option help"));
        }
    }

    /**
     * Test that a redefinition of an option's description throws an exception.
     */
    @Test
    public void testParseInputStreamDescriptionRedefinition() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_022_redefinition_description.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected exception.");
        } catch (ClcException ex) {
            assertTrue(ex.getMessage().contains(
                    "description has already been defined for option help"));
        }
    }

    /**
     * Test that a redefinition of an option's has argument throws an exception.
     */
    @Test
    public void testParseInputStreamHasArgRedefinition() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_023_redefinition_hasArg.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected exception.");
        } catch (ClcException ex) {
            assertTrue(ex.getMessage().contains(
                    "hasArg has already been defined for option help"));
        }
    }

    /**
     * Test that a redefinition of an option's argument name throws an
     * exception.
     */
    @Test
    public void testParseInputStreamArgNameRedefinition() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_024_redefinition_argName.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected exception.");
        } catch (ClcException ex) {
            assertTrue(ex.getMessage().contains(
                    "argName has already been defined for option fubar"));
        }
    }

    /**
     * Test that a redefinition of a short option throws an exception.
     */
    @Test
    public void testParseInputStreamReDefinitionShortOption() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_025_redefinition_short_option.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected exception.");
        } catch (ClcException ex) {
            assertTrue(ex.getMessage().contains(
                    " has already been defined for option help"));
        }
    }

    /**
     * Test that an option that is redefined later in the file throws an
     * exception.
     */
    @Test
    public void testParseInputStreamOptionsBadOrdering() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_026_redefinition_bad_ordering.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected exception.");
        } catch (ClcException ex) {
            System.out.println(ex.getMessage());
            assertTrue(ex.getMessage().contains("Bad configuration ordering;"
                    + " options must be grouped together. Option 'dir' has"
                    + " been defined prior to the declaration of option"
                    + " 'dir'"));
        }
    }

    /**
     * Test that an option that is redefined later in the file throws an
     * exception.
     */
    @Test
    public void testParseInputStreamHelpCommandDefinedTwice() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_027_help_command_defined_twice.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected exception.");
        } catch (ClcException ex) {
            System.out.println(ex.getMessage());
            assertTrue(ex.getMessage().contains(
                    GlobalConfiguration.GLOBAL_HELP_COMMAND_NAME
                    + " has already been defined."));
        }
    }

    /**
     * Test that an option that is redefined later in the file throws an
     * exception.
     */
    @Test
    public void testParseInputStreamHelpOptionNameDefinedTwice() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_028_help_option_defined_twice.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected exception.");
        } catch (ClcException ex) {
            System.out.println(ex.getMessage());
            assertTrue(ex.getMessage().contains(
                    GlobalConfiguration.GLOBAL_HELP_OPTION_NAME
                    + " has already been defined."));
        }
    }

    /**
     * Test that an option that is redefined later in the file throws an
     * exception.
     */
    @Test
    public void testParseInputStreamHelpFooterDefinedTwice() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_029_help_footer_defined_twice.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected exception.");
        } catch (ClcException ex) {
            System.out.println(ex.getMessage());
            assertTrue(ex.getMessage().contains(
                    GlobalConfiguration.GLOBAL_HELP_COMMAND_FOOTER
                    + " has already been defined."));
        }
    }

    /**
     * Test that an option that is redefined later in the file throws an
     * exception.
     */
    @Test
    public void testParseInputStreamHelpHeaderDefinedTwice() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_030_help_header_defined_twice.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected exception.");
        } catch (ClcException ex) {
            System.out.println(ex.getMessage());
            assertTrue(ex.getMessage().contains(
                    GlobalConfiguration.GLOBAL_HELP_COMMAND_HEADER
                    + " has already been defined."));
        }
    }

    /**
     * Test of parse method, of class ClcParser.
     */
    @Test
    public void testParseInputStreamIntOption() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_031_int_option_type.clc");
        GlobalConfiguration globalConfig = configParser.parse(is, "UTF-8");
        Map<String, OptionConfiguration> optionConfig = globalConfig.getOptionConfigurations();
        is.close();
        OptionConfiguration optConfig = optionConfig.get("port");
        assertEquals(IntegralType.class, optConfig.getValueType().getClass());
        assertEquals(80, optConfig.getValueType().getValue("80"));
    }

    /**
     * Test that a default option with the wrong type throws an error.
     */
    @Test
    public void testParseInputStreamInvalidDefaultOption() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_004_invalid_default.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals("Invalid default value 'eighty' for value type 'int'",
                    ex.getMessage());
        }
    }

    /**
     * Test of parse method, of class ClcParser.
     */
    @Test
    public void testParseInputStreamCommands() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_032_commands.clc");
        GlobalConfiguration globalConfig = configParser.parse(is, "UTF-8");
        is.close();
        assertEquals(2, globalConfig.getCommandRoot().getRoot().getChildren().size());
        assertEquals("import", globalConfig.getCommandRoot().getRoot().getChildren().get(0).getName());
        assertEquals("export", globalConfig.getCommandRoot().getRoot().getChildren().get(1).getName());
        // first check global/root level options
        Map<String, OptionConfiguration> optionConfig
                = globalConfig.getGlobalOptionConfigurations();
        OptionConfiguration optConfig = optionConfig.get("showHelp");
        checkOptionConfiguration(optConfig, "showHelp", "h", "help",
                "Print this help then exit.", false);
        // now check command options
        // first, command named import
        Command command = globalConfig.getCommandRoot().find(globalConfig.getCommandRoot().getRoot(), "import");
        optionConfig = command.getOptionConfigurations();
        optConfig = optionConfig.get("dateFrom");
        checkOptionConfiguration(optConfig, "dateFrom", "f", "from",
                "Start date to import the reports for.", true);
        optConfig = optionConfig.get("dateTo");
        checkOptionConfiguration(optConfig, "dateTo", "t", "to",
                "End date to import the reports for.", true);
        // first, command named export
        command = globalConfig.getCommandRoot().find(globalConfig.getCommandRoot().getRoot(), "export");
        optionConfig = command.getOptionConfigurations();
        optConfig = optionConfig.get("format");
        checkOptionConfiguration(optConfig, "format", "f", "format",
                "Format to export data to.", true);
        optConfig = optionConfig.get("dir");
        checkOptionConfiguration(optConfig, "dir", "d", "directory",
                "Directory to export the reports to.", true);
    }

    /**
     * Test of parse method, of class ClcParser.
     */
    @Test
    public void testParseInputStreamVersionOption() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_037_print_short_option_version.clc");
        GlobalConfiguration globalConfig = configParser.parse(is, "UTF-8");
        Map<String, OptionConfiguration> optionConfig = globalConfig.getOptionConfigurations();
        is.close();
        OptionConfiguration optConfig = optionConfig.get("v");
    }

    /**
     * Test that a repeated global option declaration throws the appropriate
     * exception.
     */
    @Test
    public void testParseInputStreamRepeatedVersionGlobalConfig() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_040_version_option_defined_twice.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected exception.");
        } catch (ClcException ex) {
            assertTrue(ex.getMessage().contains(
                    GlobalConfiguration.GLOBAL_VERSION_OPTION_NAME
                    + " has already been defined."));
        }
    }

    /**
     * Test that a repeated global option declaration throws the appropriate
     * exception.
     */
    @Test
    public void testParseInputStreamRepeatedVersionTextGlobalConfig() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_041_version_text_defined_twice.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected exception.");
        } catch (ClcException ex) {
            assertTrue(ex.getMessage().contains(
                    GlobalConfiguration.GLOBAL_VERSION_OPTION_TEXT
                    + " has already been defined."));
        }
    }

    /**
     * Check that the specified option configuration values match the other
     * arguments passed in.
     *
     * @param optConfig non-{@code null} option configuration.
     *
     * @param optionName non-{@code null} name of the option to match.
     *
     * @param shortOption short option name to match; if {@code null}, implies
     * using long options.
     *
     * @param longOption long option name to match; if {@code null}, implies
     * using short options.
     *
     * @param descrption non-{@code null} description to match.
     *
     * @param hasArg match if the option has an argument or not.
     */
    private void checkOptionConfiguration(OptionConfiguration optConfig,
            String optionName, String shortOption, String longOption,
            String descrption, boolean hasArg) {
        assertNotNull(optConfig);
        assertEquals(shortOption, optConfig.getShortOption());
        assertEquals(longOption, optConfig.getLongOption());
        assertEquals(optionName, optConfig.getName());
        assertEquals(descrption, optConfig.getDescription());
        assertEquals(hasArg, optConfig.hasArg());
    }

    /**
     * Test that an argument can be treated as a file type.
     */
    @Test
    public void testParseInputStreamArgsAsFile() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_064_args_basic_as_file.clc");
        GlobalConfiguration globalConfig = configParser.parse(is, "UTF-8");
        Map<String, OptionConfiguration> optionConfig = globalConfig.getOptionConfigurations();
        is.close();
        Map<String, ArgsConfiguration> argsConfig
                = globalConfig.getCurrentArgsConfigurations();
        assertEquals(1, argsConfig.size());
        ArgsConfiguration argConfig = argsConfig.get("dirname");
        assertNotNull(argConfig);
        assertEquals("dir", argConfig.getArgName());
        // implies multiple values can be supplied (not tested here):
        assertEquals(null, argConfig.getLength());
        assertEquals("file", argConfig.getType());
        assertTrue(argConfig.getValueType() instanceof FileType);

        OptionConfiguration optConfig = optionConfig.get("fail");
        checkOptionConfiguration(optConfig, "fail", "F", "fail",
                "Fail if no connection made, rather than retrying.", false);
        optConfig = optionConfig.get("host");
        checkOptionConfiguration(optConfig, "host", "h", "host",
                "Specify the host; optional. Use localhost if not set."
                + " Protocol is optional, assumes HTTP.", true);
        optConfig = optionConfig.get("port");
        checkOptionConfiguration(optConfig, "port", "p", "port",
                "Port number to use. Required.", true);
        optConfig = optionConfig.get("path");
        checkOptionConfiguration(optConfig, "path", "P", "path",
                "Comma separated list of paths to test on the server."
                + " If spaces are contained within the arguments, surround with"
                + " double quotes. Have as many lines as required, so long as"
                + " they are escaped.", true);
    }

    /**
     * Test that when a fix length is less than zero an appropriate error is
     * thrown.
     */
    @Test
    public void testParseInputStreamArgsInvalidFixLength() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_067_args_as_ints_invalid_fix_length.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals(ex.getMessage(),
                    "Line no. 41: Invalid argument fix length: -1");
        }
    }

    /**
     * Test that when the value type is not known an appropriate error is
     * thrown.
     */
    @Test
    public void testParseInputStreamArgsInvalidValueType() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_092_args_unknown_value_type.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals(ex.getMessage(),
                    "Line no. 41: Unknown value type: foo");
        }
    }

    /**
     * Test that when a fix length is not a number an appropriate error is
     * thrown.
     */
    @Test
    public void testParseInputStreamArgsFixLengthNotANumber() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_068_fix_length_not_a_number.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals(ex.getMessage(),
                    "Line no. 41: Invalid argument fix length: foo");
        }
    }

    /**
     * Test that when properties are defined before the type, an error is
     * thrown.
     */
    @Test
    public void testParseInputStreamArgsPropertiesDefinedBeforeType() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_069_args_properties_defined_before_type.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals(ex.getMessage(),
                    "Line no. 42: Cannot set properties on dirname as no value type has been set.");
        }
    }

    /**
     * Test that when properties are defined before the type, an error is
     * thrown.
     */
    @Test
    public void testParseInputStreamArgsPropertiesRedefined() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_070_args_properties_redefined.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals(ex.getMessage(),
                    "Line no. 44: properties have already been defined for argument dirname");
        }
    }

    /**
     * Test that when an argName is redefined an error is thrown.
     */
    @Test
    public void testParseInputStreamArgsArgNameRedefined() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_071_args_argname_redefined.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals(ex.getMessage(),
                    "Line no. 44: argName has already been defined for argument dirname");
        }
    }

    /**
     * Test that an unknown option throws an error.
     */
    @Test
    public void testParseInputStreamArgsUnknownSuboption() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_072_arg_unknown_suboption.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals(ex.getMessage(),
                    "Line no. 41: Unknown args configuration option: foo");
        }
    }

    /**
     * Test that a redefined type throws an error.
     */
    @Test
    public void testParseInputStreamArgsRededinedType() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_073_arg_redefined_type.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals(ex.getMessage(),
                    "Line no. 43: type has already been defined for argument dirname");
        }
    }

    /**
     * Test that an optional value not set to 'true' or 'false' throws an error.
     */
    @Test
    public void testParseInputStreamArgsInvalidBooleanValue() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_074_args_optional_non_boolean_value.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals(ex.getMessage(),
                    "Line no. 41: Invalid value for optional: Must be one of"
                    + " true or false, found NotTrue");
        }
    }

    /**
     * Test that an argument can be set as non-optional.
     */
    @Test
    public void testParseInputStreamArgsFileAsNonOptional() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_075_args_optional_as_false.clc");
        GlobalConfiguration globalConfig = configParser.parse(is, "UTF-8");
        is.close();
        Map<String, ArgsConfiguration> argsConfig
                = globalConfig.getCurrentArgsConfigurations();
        assertEquals(1, argsConfig.size());
        ArgsConfiguration argConfig = argsConfig.get("dirname");
        assertNotNull(argConfig);
        assertEquals("dir", argConfig.getArgName());
        assertEquals(false, argConfig.isOptional());
    }

    /**
     * Test that an argument can be set as optional.
     */
    @Test
    public void testParseInputStreamArgsFileAsOptional() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_076_args_optional_as_true.clc");
        GlobalConfiguration globalConfig = configParser.parse(is, "UTF-8");
        is.close();
        Map<String, ArgsConfiguration> argsConfig
                = globalConfig.getCurrentArgsConfigurations();
        assertEquals(1, argsConfig.size());
        ArgsConfiguration argConfig = argsConfig.get("dirname");
        assertNotNull(argConfig);
        assertEquals("dir", argConfig.getArgName());
        assertEquals(true, argConfig.isOptional());
    }

    /**
     * Test that redefining an optional value for the same argument
     * configuration throws an exception.
     */
    @Test
    public void testParseInputStreamArgsOptionalRedefinedTwiceForSameConfig() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_077_args_optional_redefined.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals(ex.getMessage(),
                    "Line no. 42: optional has already been defined for argument dirname");
        }
    }

    /**
     * Test that redefining an argument configuration throws an exception.
     */
    @Test
    public void testParseInputStreamArgsRedefined() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_079_args_redefined.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals(ex.getMessage(),
                    "Line no. 49: Bad argument configuration ordering;"
                    + " arguments must be grouped together. Argument"
                    + " 'dirname1' has been defined prior to the"
                    + " declaration of 'dirname1'");
        }
    }

    /**
     * Test that redefining an optional value for an argument configuration
     * throws an exception.
     */
    @Test
    public void testParseInputStreamArgsOptionalRedefined() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_080_args_as_optional.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals(ex.getMessage(),
                    "Line no. 45: Redefinition of an argument configuration"
                    + " that is optional; only the last argument"
                    + " configuration can be optional (lines 40 - 42).");
        }
    }

    /**
     * Test that redefining an unbounded value for an argument configuration
     * throws an exception.
     */
    @Test
    public void testParseInputStreamArgsDuplicateUnboundedArgs() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_081_args_as_unbounded_redefinition.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals(ex.getMessage(),
                    "Line no. 45: Redefinition of an argument configuration"
                    + " that is unbounded, from lines 41 - 42; only the last"
                    + " argument configuration can be unbounded. All previous"
                    + " definitions must have their 'length' set to a"
                    + " positive, non-zero value.");
        }
    }

    /**
     * Test that redefining a configuration capped at zero for an argument
     * configuration throws an exception.
     */
    @Test
    public void testParseInputStreamArgsDuplicateCappedAtZero() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_082_args_as_capped_at_zero_redefinition.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals(ex.getMessage(),
                    "Line no. 44: Redefinition of an argument configuration"
                    + " that is capped at zero (lines 41 - 41); only the last"
                    + " argument configuration can be capped at zero.");
        }
    }

    /**
     * Test that a configuration capped at zero for an argument configuration
     * throws an exception when a type is defined for that configuration.
     */
    @Test
    public void testParseInputStreamArgsCappedAtZeroWithTypeDefined() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_083_args_as_capped_at_zero_with_type_defined.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals(ex.getMessage(),
                    "Line no. 41: Current argument configuration length is zero;"
                    + " cannot define a type for such a configuration.");
        }
    }

    /**
     * Test that a configuration capped at zero for an argument configuration
     * throws an exception when a argument name is defined for that
     * configuration.
     */
    @Test
    public void testParseInputStreamArgsCappedAtZeroWithArgNameDefined() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_084_args_as_capped_at_zero_with_argname_defined.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals(ex.getMessage(),
                    "Line no. 41: Current argument configuration length is zero;"
                    + " cannot define argName for such a configuration.");
        }
    }

    /**
     * Test that a configuration capped at zero for an argument configuration
     * throws an exception when a argument name is defined for that
     * configuration.
     */
    @Test
    public void testParseInputStreamArgsCappedAtZeroWithOptionalDefined() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_085_args_as_capped_at_zero_with_optional_defined.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals(ex.getMessage(),
                    "Line no. 41: Current argument configuration length is zero;"
                    + " cannot define optional for such a configuration.");
        }
    }

    /**
     * Test that a configuration capped at zero for an argument configuration
     * throws an exception when a type is defined for that configuration prior
     * to the definition of length zero.
     */
    @Test
    public void testParseInputStreamArgsCappedAtZeroWithTypeDefinedBeforeLength() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_086_args_as_capped_at_zero_with_type_defined_before_length.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals(ex.getMessage(),
                    "Line no. 41: Type cannot be set when configuration length is zero.");
        }
    }

    /**
     * Test that a configuration capped at zero for an argument configuration
     * throws an exception when a argument name is defined for that
     * configuration, prior to the definition of length zero.
     */
    @Test
    public void testParseInputStreamArgsCappedAtZeroWithArgNameDefinedBeforeLength() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_087_args_as_capped_at_zero_with_argname_defined_before_length.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals(ex.getMessage(),
                    "Line no. 41: Argument name cannot be set when configuration length is zero.");
        }
    }

    /**
     * Test that a configuration capped at zero for an argument configuration
     * throws an exception when a argument name is defined for that
     * configuration, prior to the definition of length zero.
     */
    @Test
    public void testParseInputStreamArgsCappedAtZeroWithOptionalDefinedBeforeLength() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_088_args_as_capped_at_zero_with_optional_defined_before_length.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals(ex.getMessage(),
                    "Line no. 41: Optional cannot be set when configuration length is zero.");
        }
    }

    /**
     * Test that without help options the file is parsed successfully (the API
     * will add them in).
     */
    @Test
    public void testParseInputStreamAddHelpDefaultsLongOpts() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_099_help_add_defaults_long_opts.clc");
        configParser.parse(is, "UTF-8");
    }

    /**
     * Test that without version options the file is parsed successfully (the
     * API will add them in).
     */
    @Test
    public void testParseInputStreamAddVersionDefaultsLongOpts() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_100_version_add_defaults_long_opts.clc");
        configParser.parse(is, "UTF-8");
    }

    /**
     * Test that an option set as ignore arguments is processed correctly.
     */
    @Test
    public void testParseInputStreamOptionAsIgnoreArgs() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_105_ignore_args_option.clc");
        GlobalConfiguration globalConfig = configParser.parse(is, "UTF-8");
        Map<String, OptionConfiguration> configs = globalConfig.getOptionConfigurations();
        OptionConfiguration optConfig = configs.get("list-things");
        assertTrue(optConfig.isIgnoreCliArgs());
    }

    /**
     * Test that an option set as ignore arguments is processed correctly.
     */
    @Test
    public void testParseInputStreamOptionAsDoNotIgnoreArgs() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_106_do_not_ignore_args_option.clc");
        GlobalConfiguration globalConfig = configParser.parse(is, "UTF-8");
        Map<String, OptionConfiguration> configs = globalConfig.getOptionConfigurations();
        OptionConfiguration optConfig = configs.get("set-things");
        assertFalse(optConfig.isIgnoreCliArgs());
    }

    /**
     * Test that setting 'type/ on a unary switch causes an error.
     */
    @Test
    public void testParseInputStreamNoArgsWithTypeDefined() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_110_no_args_option_with_type_defined.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals(ex.getMessage(),
                    "Option 'noargs', lines 17 - 19, is defined as having no argument (is a unary switch) but has the property 'type' set.\n");
        }
    }

    /**
     * Test that setting 'argName/ on a unary switch causes an error.
     */
    @Test
    public void testParseInputStreamNoArgsWithArgNameDefined() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_111_no_args_option_with_argname_defined.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals(ex.getMessage(),
                    "Option 'noargs', lines 17 - 19, is defined as having no argument (is a unary switch) but has the property 'argName' set.\n");
        }
    }

    /**
     * Test that setting 'default/ on a unary switch causes an error.
     */
    @Test
    public void testParseInputStreamNoArgsWithDefaultDefined() throws Exception {
        ClcParser configParser = new ClcParser();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_112_no_args_option_with_default_defined.clc");
        try {
            configParser.parse(is, "UTF-8");
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals(ex.getMessage(),
                    "Option 'noargs', lines 17 - 19, is defined as having no argument (is a unary switch) but has the property 'default' set.\n");
        }
    }
}
