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
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.joor.Reflect;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 */
public class ClcTest {

    /**
     * Set the default outputstream to print to stderr since JUnit no longer
     * prints to stdout (it still won't write to stdout, but will now write to a
     * file in this creates a file.
     */
    @Before
    public void setUp() throws Exception {
        System.setOut(System.err);
        System.setErr(System.out);
    }

    /**
     * Inspired by:
     * https://stackoverflow.com/questions/3301635/change-private-static-final-field-using-java-reflection
     */
    @After
    public void tearDown() {
        Reflect.onClass(Clc.class).set("instance", null);
    }

    /**
     * Test that throws an exception when a directory is passed to the process
     * method.
     *
     * @throws Exception
     */
    @Test
    public void testProcessOnDirectory() throws Exception {
        File dir = new File("target");
        try {
            Clc.getInstance().process(dir, "abc",
                    new String[]{});
            fail("Expected an exception");
        } catch (IOException ex) {
            assertEquals("Not a file: target", ex.getMessage());
        }
    }

    /**
     * Test that throws an exception when a directory is passed to the process
     * method.
     *
     * @throws Exception
     */
    @Test
    public void testProcessOnNonExistantFile() throws Exception {
        File nonExisting = new File("testProcessOnNonExistantFile");
        try {
            Clc.getInstance().process(nonExisting, "abc",
                    new String[]{});
            fail("Expected an exception");
        } catch (IOException ex) {
            assertEquals("Not a file: testProcessOnNonExistantFile", ex.getMessage());
        }
    }

    /**
     * Test that once the configuration is parsed, the command line
     * configuration is created with the specified arguments and the listener
     * has been updated with the values specified by the CLI call. Since the
     * configuration is of type 'both' because options are specified as
     * char/string, we expect both short and long options to have been updated
     * in the listener.
     */
    @Test
    public void testProcessShortAndLongOptions() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_001_short_and_long_options.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "-F -h 192.168.1.2 -p 80 -P /tmp".split(" ");
        cliConfig.process(is, "UTF-8", arguments);
        // short options:
        assertEquals("80", listener.getOptions().get("p"));
        assertEquals("192.168.1.2", listener.getOptions().get("h"));
        assertEquals("/tmp", listener.getOptions().get("P"));
        assertTrue(listener.getOptions().containsKey("F"));
        // long options:
        assertEquals("80", listener.getOptions().get("port"));
        assertEquals("192.168.1.2", listener.getOptions().get("host"));
        assertEquals("/tmp", listener.getOptions().get("path"));
        assertTrue(listener.getOptions().containsKey("fail"));
    }

    /**
     * Test that once the configuration is parsed, the command line
     * configuration is created with the specified arguments and the listener
     * has been updated with the values specified by the CLI call. Since the
     * configuration is of type 'short' because options are specified as a char,
     * we expect only short options to have been updated in the listener.
     */
    @Test
    public void testProcessShortOptionsConfig() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_002_short_options.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "-F -h 192.168.1.2 -p 80 -P /tmp".split(" ");
        cliConfig.process(is, "UTF-8", arguments);
        // short options:
        assertEquals("80", listener.getOptions().get("p"));
        assertEquals("192.168.1.2", listener.getOptions().get("h"));
        assertEquals("/tmp", listener.getOptions().get("P"));
        assertTrue(listener.getOptions().containsKey("F"));
        // these long options should not be set:
        assertEquals(null, listener.getOptions().get("port"));
        assertEquals(null, listener.getOptions().get("host"));
        assertEquals(null, listener.getOptions().get("path"));
        assertFalse(listener.getOptions().containsKey("fail"));
    }

    /**
     * Test that once the configuration is parsed, the command line
     * configuration is created with the specified arguments and the listener
     * has been updated with the values specified by the CLI call. Since the
     * configuration is of type 'long' because options are specified as a
     * string, we expect only long options to have been updated in the listener.
     */
    @Test
    public void testProcessLongOptionsConfig() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_003_long_options.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "--fail --host 192.168.1.2 --port 80 --path /tmp".split(" ");
        cliConfig.process(is, "UTF-8", arguments);
        // long options:
        assertEquals("80", listener.getOptions().get("port"));
        assertEquals("192.168.1.2", listener.getOptions().get("host"));
        assertEquals("/tmp", listener.getOptions().get("path"));
        assertTrue(listener.getOptions().containsKey("fail"));
        // these short options should not be set:
        assertEquals(null, listener.getOptions().get("p"));
        assertEquals(null, listener.getOptions().get("h"));
        assertEquals(null, listener.getOptions().get("P"));
        assertFalse(listener.getOptions().containsKey("F"));
    }

    /**
     * Test that once the configuration is parsed, the command line
     * configuration is created with the specified arguments and the listener
     * has been updated with the values specified by the CLI call. Since the
     * configuration is of type 'both' because options are specified as
     * char/string, we expect both short and long options to have been updated
     * in the listener.
     */
    @Test
    public void testGetOptions() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_001_short_and_long_options.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "-F -h 192.168.1.2 -p 80 -P /tmp".split(" ");
        cliConfig.process(is, "UTF-8", arguments);
        List<Option> options = cliConfig.getOptions();
        // short options:
        assertEquals("80", getOptionValue(options, "p"));
        assertEquals("192.168.1.2", getOptionValue(options, "h"));
        assertEquals("/tmp", getOptionValue(options, "P"));
        assertNotNull(getOptionValue(options, "F"));
    }

    /**
     * Test of process method, ensuring that when an unknown option is provided,
     * the appropriate exception is thrown.
     */
    @Test
    public void testProcessFailsParseException() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_001_short_and_long_options.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "--no-such-option -F -h 192.168.1.2".split(" ");
        try {
            cliConfig.process(is, "UTF-8", arguments);
        } catch (ClcException ex) {
            assertTrue(ex.getMessage().contains("Unrecognized option"));
            assertEquals(UnrecognizedOptionException.class,
                    ex.getCause().getClass());
        }
    }

    /**
     * Test that invoking help prints the help then quits with exit status 0.
     */
    @Test
    public void testProcessPrintHelpShortOption() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_015_print_short_option_help.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "-h".split(" ");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        cliConfig.process(is, "UTF-8", arguments);
        String output = os.toString("UTF8");
        assertTrue(output.contains("Print this then quit."));
        assertTrue(output.contains("Fail if no connection made, rather than retrying."));
        assertTrue(output.contains("Specify the host; optional. Use localhost if not set. Protocol"));
        assertTrue(output.contains(" is optional, assumes HTTP."));
        assertTrue(output.contains("Port number to use. Required."));
        ps.close();
        os.close();
    }

    /**
     * Test that specifying auto-print help via the command line configuration
     * without the arguments containing the help option does not print the help.
     */
    @Test
    public void testProcessDoesNotPrintHelp() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_015_print_short_option_help.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "-f -H localhost".split(" ");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        cliConfig.process(is, "UTF-8", arguments);
        String output = os.toString("UTF8");
        assertFalse(output.contains("Print this then quit."));
        assertFalse(output.contains("Fail if no connection made, rather than retrying."));
        assertFalse(output.contains("Specify the host; optional. Use localhost if not set."));
        assertFalse(output.contains(" Protocol is optional, assumes HTTP."));
        assertFalse(output.contains("Port number to use. Required."));
        ps.close();
        os.close();
    }

    /**
     * Test that invoking help prints the help then quits with exit status 0.
     */
    @Test
    public void testProcessPrintHelpLongOption() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_016_print_long_option_help.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "--help".split(" ");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        cliConfig.process(is, "UTF-8", arguments);
        String output = os.toString("UTF8");
        assertTrue(output.contains("Print this then quit."));
        assertTrue(output.contains("Fail if no connection made, rather than retrying."));
        assertTrue(output.contains("Specify the host; optional. Use localhost if not set."));
        assertTrue(output.contains(" Protocol is optional, assumes HTTP."));
        assertTrue(output.contains("Port number to use. Required."));
        ps.close();
        os.close();
    }

    /**
     * Test that invoking help when hasArg = true throws an error.
     */
    @Test
    public void testProcessPrintHelpHasArgThrowsError() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_017_bad_help_option.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "--help foobarbaz".split(" ");
        try {
            cliConfig.process(is, "UTF-8", arguments);
            fail("Expected exception");
        } catch (ClcException ex) {
            assertEquals("Error: Option help cannot have an argument"
                    + " associated with it.", ex.getMessage());
        }
    }

    /**
     * Test that the specified header and footer are included.
     */
    @Test
    public void testHelpHeaderFooter() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_018_header_and_footer.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "--help".split(" ");

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        cliConfig.process(is, "UTF-8", arguments);
        String output = os.toString("UTF8");
        assertTrue(output.contains("foo_command"));
        assertTrue(output.contains("Show some useful information"));
        assertTrue(output.contains("Copyright Apache Software Foundation"));
        ps.close();
        os.close();
    }

    /**
     * Test that the specified escaped header and footer are included.
     */
    @Test
    public void testHelpHeaderFooterEscaped() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_019_header_and_footer_escaped.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "--help".split(" ");

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        cliConfig.process(is, "UTF-8", arguments);
        String output = os.toString("UTF8");
        assertTrue(output.contains("foo_command"));
        assertTrue(output.contains("Show some useful information, with some"
                + " extra escaped lines"));
        assertTrue(output.contains("Copyright Apache Software Foundation"
                + " Submit escaped lines to System.out()"));
    }

    /**
     * Test that we can define different commands in the configuration.
     */
    @Test
    public void testProcessReportToolNoCommandSupplied() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_032_commands.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "-h".split(" ");
        cliConfig.process(is, "UTF-8", arguments);
        // short options:
        assertTrue(listener.getOptions().containsKey("h"));
        // long options:
        assertTrue(listener.getOptions().containsKey("help"));
    }

    /**
     * Test that we can define a command named 'export' and passing export
     * options gives the results we expect.
     */
    @Test
    public void testProcessReportToolImportCommand() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_032_commands.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        cliConfig.addCommandOptionListener(listener);
        String[] arguments = "import -f yesterday -t today".split(" ");
        cliConfig.process(is, "UTF-8", arguments);
        assertEquals("import", listener.getCommand());
        // short options:
        assertTrue(listener.getCommandOptions("import").containsKey("f"));
        assertTrue(listener.getCommandOptions("import").get("f").equals("yesterday"));
        assertTrue(listener.getCommandOptions("import").containsKey("t"));
        assertTrue(listener.getCommandOptions("import").get("t").equals("today"));
        // long options:
        assertTrue(listener.getCommandOptions("import").containsKey("from"));
        assertTrue(listener.getCommandOptions("import").get("from").equals("yesterday"));
        assertTrue(listener.getCommandOptions("import").containsKey("to"));
        assertTrue(listener.getCommandOptions("import").get("to").equals("today"));
    }

    /**
     * Test that we can define a command named 'export' and passing export
     * options gives the results we expect.
     */
    @Test
    public void testProcessReportToolForInvalidCommand() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_032_commands.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "x -f yesterday -t today".split(" ");
        try {
            cliConfig.process(is, "UTF-8", arguments);
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals("Invalid command 'x'; valid commands are:"
                    + " import, export. Try -h/--help", ex.getMessage());
        }
    }

    /**
     * Test that we can define a command named 'import' and the invoking help
     * with said command gives us the options we expect for the import command.
     */
    @Test
    public void testProcessReportToolImportHelp() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_032_commands.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        cliConfig.addCommandOptionListener(listener);
        String[] arguments = "import -h".split(" ");
        cliConfig.process(is, "UTF-8", arguments);
    }

    /**
     * Test that we can define a command named 'export' and the invoking help
     * with said command gives us the options we expect for export.
     */
    @Test
    public void testProcessReportToolExportCommand() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_032_commands.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        cliConfig.addCommandOptionListener(listener);
        String[] arguments = "export -f png -d exports/png".split(" ");
        cliConfig.process(is, "UTF-8", arguments);
        assertEquals("export", listener.getCommand());
        // short options:
        assertTrue(listener.getCommandOptions("export").containsKey("f"));
        assertTrue(listener.getCommandOptions("export").get("f").equals("png"));
        assertTrue(listener.getCommandOptions("export").containsKey("d"));
        assertTrue(listener.getCommandOptions("export").get("d").equals("exports/png"));
        // long options:
        assertTrue(listener.getCommandOptions("export").containsKey("format"));
        assertTrue(listener.getCommandOptions("export").get("format").equals("png"));
        assertTrue(listener.getCommandOptions("export").containsKey("directory"));
        assertTrue(listener.getCommandOptions("export").get("directory").equals("exports/png"));
    }

    /**
     * Test that parsing succeeds when the MD5 matches the given file.
     */
    @Test
    public void testProcessMd5Check() throws Exception {
        ConfigListener listener = new ConfigListener();
        File configFile = new File("target/test-classes/config/config_033_md5_succeeds.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "-p 80".split(" ");
        try {
            cliConfig.process(configFile, "3f369774b73503f0dfe149bc869c380a",
                    arguments);
        } catch (ClcException cex) {
            fail("Expected MD5 check to succeed but received an error: "
                    + cex.getMessage());
        }
    }

    /**
     * Test that an invalid MD5 value throws a corrupted file exception.
     */
    @Test
    public void testProcessMd5CheckFails() throws Exception {
        ConfigListener listener = new ConfigListener();
        File configFile = new File("target/test-classes/config/config_034_md5_fails.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "-p 80".split(" ");
        try {
            cliConfig.process(configFile, "381ebb14d65530e0f4f320617ec680a5",
                    arguments);
            fail("Expected an exception.");
        } catch (ClcException cex) {
            assertEquals("Corrupt configuration file.", cex.getMessage());
        }
    }

    /**
     * Test that a short version option (-v) can be configured to print out
     * required version information.
     */
    @Test
    public void testProcessPrintVersionpShortOption() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_037_print_short_option_version.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "-v".split(" ");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        cliConfig.process(is, "UTF-8", arguments);
        String output = os.toString("UTF8");
        assertTrue(output.contains("Copyright (C) Some Company 2021; licensed"
                + " under the MIT license"));
        ps.close();
        os.close();
    }

    /**
     * Test that supplying an argument to version will report an error.
     */
    @Test
    public void testProcessPrintVersionpShortOptionWithHasArgThrowsException() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_038_version_with_hasArg.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "-v foobarbaz".split(" ");
        try {
            cliConfig.process(is, "UTF-8", arguments);
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals(ex.getMessage(), "Error: Option version cannot have an"
                    + " argument associated with it.");
        }
    }

    /**
     * Test that a long version option (--version) can be configured to print
     * out required version information.
     */
    @Test
    public void testProcessPrintVersionpLongOption() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_039_print_long_option_version.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "--version".split(" ");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        cliConfig.process(is, "UTF-8", arguments);
        String output = os.toString("UTF8");
        assertTrue(output.contains("Copyright (C) Some Company 2021; licensed"
                + " under the MIT license"));
        ps.close();
        os.close();
    }

    /**
     * Test that version information from a manifest file is output correctly.
     */
    @Test
    public void testProcessPrintVersionShortOptionManifestProps() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_042_version_manifest_entries.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "-v".split(" ");
        Map<String, String> mfMap = getManifestMap();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        cliConfig.process(is, "UTF-8", arguments);
        String output = os.toString("UTF8");
        assertTrue(output.contains("Copyright (C) " + mfMap.get("Company")
                + " 2021 version " + mfMap.get("Version")
                + "."));
        ps.close();
        os.close();
    }

    /**
     * Test that version information from a manifest file is output correctly.
     */
    @Test
    public void testProcessHelpOptionsSorted() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_043_help_options_sorted.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "-h".split(" ");
        Map<String, String> mfMap = getManifestMap();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        cliConfig.process(is, "UTF-8", arguments);
        // COMMENT
        ps.close();
        os.close();
    }

    /**
     * Test that version information from a manifest that is missing reports an
     * error.
     */
    @Test
    public void testProcessPrintVersionpShortOptionManifestPropNotFound() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_044_version_manifest_entry_missing.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "-v".split(" ");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        try {
            cliConfig.process(is, "UTF-8", arguments);
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals(ex.getMessage(), "Line no. 18: Could not"
                    + " find manifest entry 'Version-Information'");
        }
    }

    /**
     * Test that version information from a manifest fails if the appropriate
     * text substitution is missing.
     */
    @Test
    public void testProcessPrintVersionpShortOptionManifestNoTextSubs() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_045_version_manifest_sub_text_missing.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "-v".split(" ");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        try {
            cliConfig.process(is, "UTF-8", arguments);
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals(ex.getMessage(), "Line no. 21: Could not"
                    + " find manifest entry 'No-A-Property'");
        }
    }

    /**
     * Test that nested commands can be processed successfully.
     *
     * @throws Exception
     */
    @Test
    public void testProcessNestedCommands() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_046_nested_commands.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        cliConfig.addCommandOptionListener(listener);
        String[] arguments = "import file-type --type jpg".split(" ");
        cliConfig.process(is, "UTF-8", arguments);
        assertEquals("import/file-type", listener.getCommand());
        // short options:
        assertTrue(listener.getCommandOptions("import/file-type").containsKey("t"));
        assertTrue(listener.getCommandOptions("import/file-type").get("t").equals("jpg"));
        // long options:
        assertTrue(listener.getCommandOptions("import/file-type").containsKey("type"));
        assertTrue(listener.getCommandOptions("import/file-type").get("type").equals("jpg"));
    }

    /**
     * Test that when a configuration has short and long commands, an error is
     * thrown when an unknown command is supplied.
     *
     * @throws Exception
     */
    @Test
    public void testProcessNestedCommandsFailsForUnknownCommand() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_046_nested_commands.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "import unknown-command --type jpg".split(" ");
        try {
            cliConfig.process(is, "UTF-8", arguments);
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals("Invalid command 'unknown-command'; valid commands"
                    + " are: file-type. Try -h/--help",
                    ex.getMessage());
        }
    }

    /**
     * Test that when a configuration has short commands only, an error is
     * thrown when an unknown command is supplied.
     *
     * @throws Exception
     */
    @Test
    public void testProcessNestedCommandsForUnknownShortCommand() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_047_unknown_nested_short_commands.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "import unknown-command -t jpg".split(" ");
        try {
            cliConfig.process(is, "UTF-8", arguments);
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals("Invalid command 'unknown-command'; valid commands"
                    + " are: file-type. Try -h",
                    ex.getMessage());
        }
    }

    /**
     * Test that when a configuration has long commands only, an error is thrown
     * when an unknown command is supplied.
     *
     * @throws Exception
     */
    @Test
    public void testProcessNestedCommandsForUnknownLongCommand() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_048_unknown_nested_long_commands.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "import unknown-command --type jpg".split(" ");
        try {
            cliConfig.process(is, "UTF-8", arguments);
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals("Invalid command 'unknown-command'; valid commands"
                    + " are: file-type. Try --help",
                    ex.getMessage());
        }
    }

    /**
     * Test that when a configuration has short and long commands, an error is
     * thrown when an unknown command is supplied but no .
     *
     * @throws Exception
     */
    @Test
    public void testProcessNestedCommandsFailsForUnknownCommandWithNoHelpDefined() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_049_unknown_nested_commands_no_help_defined.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "import unknown-command --type jpg".split(" ");
        try {
            cliConfig.process(is, "UTF-8", arguments);
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals("Invalid command 'unknown-command'; valid"
                    + " commands are: file-type.",
                    ex.getMessage());
        }
    }

    /**
     * Test that when a configuration has short and long commands, an error is
     * thrown when an unknown command is supplied but no .
     *
     * @throws Exception
     */
    @Test
    public void testProcessNestedCommandsFailsForUnknownNestedCommandCommand() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_050_nested_commands_contains_unknown_command.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "import unknown-command --type jpg".split(" ");
        try {
            cliConfig.process(is, "UTF-8", arguments);
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals("Unknown parent command 'unknown' for command 'x'",
                    ex.getMessage());
        }
    }

    /**
     * Test that all arguments are treated as strings once all switches have
     * been processed and the listener updated.
     */
    @Test
    public void testProcessArgAsStringsWithListener() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_063_args_basic.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addArgsListener(listener);
        cliConfig.addOptionListener(listener);
        String[] arguments = "-F -h 192.168.1.2 -p 80 -P /tmp string1 2 file3".split(" ");
        cliConfig.process(is, "UTF-8", arguments);

        assertEquals(3, listener.getArgs().size());
        Map<String, Map<Integer, Object>> args = listener.getArgs();
        String[] argArray = args.keySet().toArray(new String[]{});

        assertEquals(argArray[0], "string1");
        Map<Integer, Object> argMap = args.get("string1");
        assertEquals(1, argMap.size());
        assertEquals("string1", argMap.get(0));

        assertEquals(argArray[1], "2");
        argMap = args.get("2");
        assertEquals(1, argMap.size());
        assertEquals("2", argMap.get(1));

        assertEquals(argArray[2], "file3");
        argMap = args.get("file3");
        assertEquals(1, argMap.size());
        assertEquals("file3", argMap.get(2));
    }

    /**
     * Test that all arguments are treated as strings once all switches have
     * been processed.
     */
    @Test
    public void testProcessArgAsStrings() throws Exception {
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_063_args_basic.clc");
        Clc cliConfig = Clc.getInstance();
        String[] arguments = "-F -h 192.168.1.2 -p 80 -P /tmp string1 2 file3".split(" ");
        cliConfig.process(is, "UTF-8", arguments);

        assertEquals(3, cliConfig.getArgs().size());
        assertEquals("string1", cliConfig.getArgs().get(0));
        assertEquals("2", cliConfig.getArgs().get(1));
        assertEquals("file3", cliConfig.getArgs().get(2));
    }

    /**
     * Test that all arguments are treated as integers once all switches have
     * been processed and the listener updated. Any number of arguments may be
     * supplied as long as there at least one (since optional wasn't set on the
     * args configuration value).
     */
    @Test
    public void testProcessArgAsIntsUnbounded() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_065_args_as_ints.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addArgsListener(listener);
        cliConfig.addOptionListener(listener);
        String[] arguments = "-F -h 192.168.1.2 -p 80 -P /tmp 5 4 3".split(" ");
        cliConfig.process(is, "UTF-8", arguments);

        assertEquals(1, listener.getArgs().size());
        Map<String, Map<Integer, Object>> args = listener.getArgs();
        String[] argArray = args.keySet().toArray(new String[]{});
        assertEquals(argArray[0], "intvals");
        Map<Integer, Object> argMap = args.get("intvals");
        assertEquals(3, argMap.size());
        assertEquals(5, argMap.get(0));
        assertEquals(4, argMap.get(1));
        assertEquals(3, argMap.get(2));
    }

    /**
     * Test that all arguments are treated as integers once all switches have
     * been processed and the listener updated. Any number of arguments may be
     * supplied as long as there at least one (since optional wasn't set on the
     * args configuration value).
     */
    @Test
    public void testProcessArgAsIntsUnboundedMinimum1() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_065_args_as_ints.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addArgsListener(listener);
        cliConfig.addOptionListener(listener);
        String[] arguments = "-F -h 192.168.1.2 -p 80 -P /tmp 5".split(" ");
        cliConfig.process(is, "UTF-8", arguments);

        assertEquals(1, listener.getArgs().size());
        Map<String, Map<Integer, Object>> args = listener.getArgs();
        String[] argArray = args.keySet().toArray(new String[]{});
        assertEquals(argArray[0], "intvals");
        Map<Integer, Object> argMap = args.get("intvals");
        assertEquals(1, argMap.size());
        assertEquals(5, argMap.get(0));
    }

    /**
     * Test that when the last set of args is optional, they don't need to be
     * supplied; though 1 boolean, 1 char and 2 ints will have to be supplied.
     */
    @Test
    public void testProcessArgAs1Boolean1Char2IntsNoUnboundedStrings() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_091_args_1bool_1char_2ints_rest_unbounded_optional.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addArgsListener(listener);
        cliConfig.addOptionListener(listener);
        String[] arguments = "-F -h 192.168.1.2 -p 80 -P /tmp true A 101 102".split(" ");
        cliConfig.process(is, "UTF-8", arguments);

        assertEquals(3, listener.getArgs().size());
        Map<String, Map<Integer, Object>> args = listener.getArgs();
        String[] argArray = args.keySet().toArray(new String[]{});
        assertEquals(argArray[0], "boolean-val");
        assertEquals(argArray[1], "char-val");
        assertEquals(argArray[2], "int-vals");
        Map<Integer, Object> argMap = args.get("boolean-val");
        assertEquals(true, argMap.get(0));
        argMap = args.get("char-val");
        assertEquals('A', argMap.get(1));
        argMap = args.get("int-vals");
        assertEquals(101, argMap.get(2));
        assertEquals(102, argMap.get(3));
    }

    /**
     * Test that all arguments are treated as integers once all switches have
     * been processed and the listener updated. Any number of arguments may be
     * supplied as long as there at least one.
     */
    @Test
    public void testProcessArgAs1Boolean1Char2IntsRestUnboundedStrings() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_091_args_1bool_1char_2ints_rest_unbounded_optional.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addArgsListener(listener);
        cliConfig.addOptionListener(listener);
        String[] arguments = "-F -h 192.168.1.2 -p 80 -P /tmp true A 101 102 string1 string2".split(" ");
        cliConfig.process(is, "UTF-8", arguments);

        Map<String, Map<Integer, Object>> args = listener.getArgs();
        String[] argArray = args.keySet().toArray(new String[]{});
        assertEquals(argArray[0], "boolean-val");
        assertEquals(argArray[1], "char-val");
        assertEquals(argArray[2], "int-vals");
        assertEquals(argArray[3], "string-vals");
        Map<Integer, Object> argMap = args.get("boolean-val");
        assertEquals(true, argMap.get(0));
        argMap = args.get("char-val");
        assertEquals('A', argMap.get(1));
        argMap = args.get("int-vals");
        assertEquals(101, argMap.get(2));
        assertEquals(102, argMap.get(3));
        argMap = args.get("string-vals");
        assertEquals("string1", argMap.get(4));
        assertEquals("string2", argMap.get(5));
    }

    /**
     * Test that fixed-length arguments are processed correctly.
     */
    @Test
    public void testProcessArgAsIntsFixedLength() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_066_args_as_ints_fixed_length.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addArgsListener(listener);
        cliConfig.addOptionListener(listener);
        String[] arguments = "-F -h 192.168.1.2 -p 80 -P /tmp 5 4 3".split(" ");
        cliConfig.process(is, "UTF-8", arguments);

        // even though the arguments for default arguments will just be the
        // key set of arguments, we still test that each map of each set is of
        // size 1 and that the value of the map is the value of the original
        // ket set entry:
        assertEquals(1, listener.getArgs().size());
        Map<String, Map<Integer, Object>> args = listener.getArgs();
        String[] argArray = args.keySet().toArray(new String[]{});
        assertEquals(argArray[0], "intvals");
        Map<Integer, Object> argMap = args.get("intvals");
        assertEquals(3, argMap.size());
        assertEquals(5, argMap.get(0));
        assertEquals(4, argMap.get(1));
        assertEquals(3, argMap.get(2));
    }

    /**
     * Test that when a value type is invalid an appropriate error is thrown.
     */
    @Test
    public void testProcessArgAsIntWithInvalidValueTypeValue() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_094_args_with_invalid_value_type_value.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addArgsListener(listener);
        cliConfig.addOptionListener(listener);
        String[] arguments = "-F -h 192.168.1.2 -p 80 -P /tmp 5 foo 3".split(" ");
        try {
            cliConfig.process(is, "UTF-8", arguments);
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals("Error: index 1 - null: Invalid integer value: 'foo'.",
                    ex.getMessage());
        }
    }

    //
    /**
     * Test that fixed-length arguments that are not all present creates the
     * correct error.
     */
    @Test
    public void testProcessArgAsIntsFixedLengthTooShort() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_066_args_as_ints_fixed_length.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addArgsListener(listener);
        cliConfig.addOptionListener(listener);
        String[] arguments = "-F -h 192.168.1.2 -p 80 -P /tmp 5 4".split(" ");
        try {
            cliConfig.process(is, "UTF-8", arguments);
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals("Error processing argument index 2; expected at least 3 arguments.",
                    ex.getMessage());
        }
    }

    /**
     * Test that fixed-length arguments are exceeded by the number of command
     * line arguments passed in.
     */
    @Test
    public void testProcessArgAsIntsFixedLengthTooLong() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_066_args_as_ints_fixed_length.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addArgsListener(listener);
        cliConfig.addOptionListener(listener);
        String[] arguments = "-F -h 192.168.1.2 -p 80 -P /tmp 5 4 3 2".split(" ");
        try {
            cliConfig.process(is, "UTF-8", arguments);
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals("Error: Configuration accepts no more than 3"
                    + " but got 4 arguments.",
                    ex.getMessage());
        }
    }

    /**
     * Test that if arguments are capped at zero an exception is thrown when at
     * least one argument is supplied.
     */
    @Test
    public void testProcessArgAsCappedAtZero() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_089_args_capped_at_zero.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addArgsListener(listener);
        cliConfig.addOptionListener(listener);
        String[] arguments = "-F -h 192.168.1.2 -p 80 -P /tmp cannot-have-arguments".split(" ");
        try {
            cliConfig.process(is, "UTF-8", arguments);
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals("Error: Configuration accepts no more than 0"
                    + " but got 1 arguments.",
                    ex.getMessage());
        }
    }

    /**
     * Test that all arguments are treated as strings once all switches have
     * been processed and the listener updated.
     */
    @Test
    public void testProcessArgAsInts() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_065_args_as_ints.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addArgsListener(listener);
        cliConfig.addOptionListener(listener);
        String[] arguments = "-F -h 192.168.1.2 -p 80 -P /tmp 5 4 3".split(" ");
        cliConfig.process(is, "UTF-8", arguments);

        // even though the arguments for default arguments will just be the
        // key set of arguments, we still test that each map of each set is of
        // size 1 and that the value of the map is the value of the original
        // ket set entry:
        assertEquals(1, listener.getArgs().size());
        Map<String, Map<Integer, Object>> args = listener.getArgs();
        String[] argArray = args.keySet().toArray(new String[]{});
        assertEquals(argArray[0], "intvals");
        Map<Integer, Object> argMap = args.get("intvals");
        assertEquals(3, argMap.size());
        assertEquals(5, argMap.get(0));
        assertEquals(4, argMap.get(1));
        assertEquals(3, argMap.get(2));
    }

    /**
     * Test that all fixed length arguments for the 'command' are treated as
     * integers once all switches have been processed and the listener updated.
     */
    @Test
    public void testProcessCommandFixedLengthArgsAsInts() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_095_args_command_as_ints.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addArgsListener(listener);
        cliConfig.addCommandArgsListener(listener);
        cliConfig.addOptionListener(listener);
        String[] arguments = "foo 10 11".split(" ");
        cliConfig.process(is, "UTF-8", arguments);

        // even though the arguments for default arguments will just be the
        // key set of arguments, we still test that each map of each set is of
        // size 1 and that the value of the map is the value of the original
        // ket set entry:
        assertEquals(1, listener.getArgs().size());
        Map<String, Map<Integer, Object>> args = listener.getArgs();
        String[] argArray = args.keySet().toArray(new String[]{});
        assertEquals(argArray[0], "foo:intvals");
        Map<Integer, Object> argMap = args.get("foo:intvals");
        assertEquals(2, argMap.size());
        assertEquals(10, argMap.get(0));
        assertEquals(11, argMap.get(1));
    }

    /**
     * Test that all variable length arguments for the 'command' are treated as
     * integers once all switches have been processed and the listener updated.
     */
    @Test
    public void testProcessCommandVariableLengthArgsAsInts() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_109_args_not_fixed_len_command_as_ints_.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addArgsListener(listener);
        cliConfig.addCommandArgsListener(listener);
        cliConfig.addOptionListener(listener);
        String[] arguments = "foo 101 100 99 1".split(" ");
        cliConfig.process(is, "UTF-8", arguments);

        assertEquals(1, listener.getArgs().size());
        Map<String, Map<Integer, Object>> args = listener.getArgs();
        String[] argArray = args.keySet().toArray(new String[]{});
        assertEquals(argArray[0], "foo:intvals");
        Map<Integer, Object> argMap = args.get("foo:intvals");
        assertEquals(4, argMap.size());
        assertEquals(101, argMap.get(0));
        assertEquals(100, argMap.get(1));
        assertEquals(99, argMap.get(2));
        assertEquals(1, argMap.get(3));
    }

    /**
     * Test that fixed-length arguments for a command named 'foo' are exceeded
     * by the number of command line arguments passed in.
     */
    @Test
    public void testProcessCommandArgsAsIntsFixedLengthTooLong() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_096_args_command_as_ints_fixed_length_fails.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addArgsListener(listener);
        cliConfig.addOptionListener(listener);
        String[] arguments = "foo 5 4 3 2".split(" ");
        try {
            cliConfig.process(is, "UTF-8", arguments);
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals("Error: Configuration  for command 'foo' accepts"
                    + " no more than 2 but got 4 arguments.",
                    ex.getMessage());
        }
    }

    /**
     * Test that fixed-length arguments for a command named 'foo' are exceeded
     * by the number of command line arguments passed in.
     */
    @Test
    public void testProcessCommandArgsAsHavingNoDefinedArgs() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_097_args_command_as_no_defined_args.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addArgsListener(listener);
        cliConfig.addOptionListener(listener);
        String[] arguments = "foo 5 4 3 2".split(" ");
        try {
            cliConfig.process(is, "UTF-8", arguments);
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals("Error: Configuration  for command 'foo'"
                    + " accepts no more than 0 but got 4 arguments.",
                    ex.getMessage());
        }
    }

    /**
     * Test that a file with long options will have default help values added by
     * the API.
     */
    @Test
    public void testProcessPrintHelpAddsDefaultsLongOption() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_099_help_add_defaults_long_opts.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "--help".split(" ");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        cliConfig.process(is, "UTF-8", arguments);
        String output = os.toString("UTF8");
        assertTrue(output.contains("Print this help then exit."));
        ps.close();
        os.close();
    }

    /**
     * Test that a file with short options will have default help values added
     * by the API.
     */
    @Test
    public void testProcessPrintHelpAddsDefaultsShortOption() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_101_help_add_defaults_short_opts.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "-h".split(" ");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        cliConfig.process(is, "UTF-8", arguments);
        String output = os.toString("UTF8");
        assertTrue(output.contains("Print this help then exit."));
        ps.close();
        os.close();
    }

    /**
     * Test that a file with long options will have default version values added
     * by the API.
     */
    @Test
    public void testProcessPrintVersionAddsDefaultsLongOption() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_100_version_add_defaults_long_opts.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "--version".split(" ");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        cliConfig.process(is, "UTF-8", arguments);
        String output = os.toString("UTF8");
        assertTrue(output.contains("Copyright (C) Some Company 2021; licensed under the MIT license."));
        ps.close();
        os.close();
    }

    /**
     * Test that a file with short options will have default help values added
     * by the API.
     */
    @Test
    public void testProcessPrintVersionAddsDefaultsShortOption() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_102_version_add_defaults_short_opts.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "-v".split(" ");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        cliConfig.process(is, "UTF-8", arguments);
        String output = os.toString("UTF8");
        assertTrue(output.contains("Copyright (C) Some Company 2021; licensed under the MIT license."));
        ps.close();
        os.close();
    }

    /**
     * Test that a file with long and short options will have default help
     * values added by the API.
     */
    @Test
    public void testProcessPrintVersionHelpAddsDefaultsUsingLongOption() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_103_help_add_defaults_long_and_short_opts.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "--help".split(" ");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        cliConfig.process(is, "UTF-8", arguments);
        String output = os.toString("UTF8");
        assertTrue(output.contains("Print this help then exit."));
        ps.close();
        os.close();
        is.close();
    }

    /**
     * Test that a file with long and short options will have default help
     * values added by the API.
     */
    @Test
    public void testProcessPrintVersionHelpAddsDefaultsUsingShortOption() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_103_help_add_defaults_long_and_short_opts.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "-h".split(" ");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        cliConfig.process(is, "UTF-8", arguments);
        String output = os.toString("UTF8");
        assertTrue(output.contains("Print this help then exit."));
        ps.close();
        os.close();
        is.close();
    }

    /**
     * Test that a file with long and short options will have default version
     * values added by the API.
     */
    @Test
    public void testProcessPrintVersionAddsDefaultsUsingLongOption() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_104_version_add_defaults_long_and_short_opts.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "--version".split(" ");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        cliConfig.process(is, "UTF-8", arguments);
        String output = os.toString("UTF8");
        assertTrue(output.contains("Copyright (C) Some Company 2021; licensed under the MIT license."));
        ps.close();
        os.close();
        is.close();
    }

    /**
     * Test that a file with long and short options will have default version
     * values added by the API.
     */
    @Test
    public void testProcessPrintVersionAddsDefaultsUsingShortOption() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_104_version_add_defaults_long_and_short_opts.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "-v".split(" ");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        cliConfig.process(is, "UTF-8", arguments);
        String output = os.toString("UTF8");
        assertTrue(output.contains("Copyright (C) Some Company 2021; licensed under the MIT license."));
        ps.close();
        os.close();
        is.close();
    }

    /**
     * Test that a CLC that has non-optional arguments is processed correctly
     * when a switch is defined that does not require arguments and that no
     * error is thrown because of a lack of arguments.
     *
     * @throws Exception
     */
    @Test
    public void testProcessIgnoreArgs() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_105_ignore_args_option.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "--list-things".split(" ");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        // we expect that no exception will be thrown
        cliConfig.process(is, "UTF-8", arguments);
        ps.close();
        os.close();
        is.close();
    }

    /**
     * Test that a CLC that has non-optional arguments is processed correctly
     * when a switch is defined that does require arguments and that no error is
     * thrown because of a lack of arguments.
     *
     * @throws Exception
     */
    @Test
    public void testProcessDoNotIgnoreArgs() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_106_do_not_ignore_args_option.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "--set-things foo definitely-only-1-arg".split(" ");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        // we expect that no exception will be thrown
        cliConfig.process(is, "UTF-8", arguments);
        ps.close();
        os.close();
        is.close();
    }

    /**
     * Test that a CLC that has non-optional arguments is processed correctly
     * when the help option requires that arguments are processed.
     *
     * @throws Exception
     */
    @Test
    public void testProcessHelpAsRequiresArgs() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_107_help_as_requires_args.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "--help chapter1,chapter2".split(" ");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        // we expect that no exception will be thrown
        cliConfig.process(is, "UTF-8", arguments);
        ps.close();
        os.close();
        is.close();
    }

    /**
     * Test that a CLC that has non-optional arguments is processed correctly
     * when the version option requires that arguments are processed.
     *
     * @throws Exception
     */
    @Test
    public void testProcessVersionAsRequiresArgs() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_108_version_as_requires_args.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = "--version license,year,company,is-copyleft".split(" ");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        // we expect that no exception will be thrown
        cliConfig.process(is, "UTF-8", arguments);
        ps.close();
        os.close();
        is.close();
    }

    /**
     * Test that when no value for a CLI option is specified and a default
     * exists, the correct value type default is returned.
     */
    @Test
    public void testGetDefaultRootLevelOptions() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_031_int_option_type.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addOptionListener(listener);
        String[] arguments = new String[]{};
        cliConfig.addOptionListener(listener);
        cliConfig.process(is, "UTF-8", arguments);

        assertEquals(1, listener.getOptions().size());
        Map<String, Object> options = listener.getOptions();
        // p == port
        assertTrue(options.containsKey("p"));
        Object optValue = options.get("p");
        assertEquals((int) 80, optValue);
    }

    /**
     * Test that when no value for a CLI option is specified for a command and a
     * default exists, the correct value type default is returned.
     *
     * @throws Exception
     */
    @Test
    public void testGetDefaultCommandOptions() throws Exception {
        ConfigListener listener = new ConfigListener();
        InputStream is = ClcParserTest.class.getResourceAsStream(
                "/config/config_046_nested_commands.clc");
        Clc cliConfig = Clc.getInstance();
        cliConfig.addCommandOptionListener(listener);
        String[] arguments = "import/file-type -t jpg".split(" ");
        cliConfig.process(is, "UTF-8", arguments);
        assertEquals("import/file-type", listener.getCommand());
        Map<String, Object> options = listener.getCommandOptions("import/file-type");
        // b == batchSize
        assertTrue(options.containsKey("b"));
        Object optValue = options.get("b");
        assertEquals((int) 200, optValue);
    }

    /**
     * Get all key/value entries from the hash map.
     *
     * @return the map of values read from the manifest.
     *
     * @throws IOException if the manifest cannot be read or does not exist.
     */
    public Map<String, String> getManifestMap() throws IOException {
        Map<String, String> map = new HashMap<>();
        InputStream is = new FileInputStream("target/test-classes/META-INF/MANIFEST.MF");
        Manifest mf = new Manifest(is);
        Attributes atts = mf.getMainAttributes();
        for (Object v : atts.keySet()) {
            map.put(v.toString(), atts.getValue(v.toString()));
        }
        return map;
    }

    /**
     * Get the option value from the list where the option name equals the
     * specified key.
     *
     * @param options non-{@code null}, non-empty option list.
     *
     * @param key non-{@code null} key to search for.
     *
     * @return the option value if it could be retrieved, or the empty string if
     * the option does not have an argument; {@code null} otherwise.
     */
    private String getOptionValue(final List<Option> options, String key) {
        String result = null;
        for (Option option : options) {
            if (key.equals(option.getOpt())) {
                if (option.hasArg()) {
                    result = option.getValue();
                } else {
                    result = "";
                }
            }
        }
        return result;
    }
}
