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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.statefive.clic.Clc;
import org.statefive.clic.ClcException;
import org.statefive.clic.ClcParser;
import org.statefive.clic.GlobalConfiguration;

/**
 *
 * @author rich
 */
public class JavaPropertiesBuilderTest
        extends AbstractPropertiesTestBuilder {

    /**
     * Test of build method, of class JavaPropertiesBuilder.
     */
    @Test
    public void testBuild() throws Exception {
        JavaPropertiesBuilder instance = new JavaPropertiesBuilder();
        InputStream is = PropertiesTestHelper.create("some.arg = y");
        String[] args = "--some-arg x".split(" ");
        instance.addPropertiesSource(new PropertiesStreamSource(is));
        Properties result = instance.build(args);
        assertEquals("x", result.get("some.arg"));
    }

    /**
     * Test of build method, of class ProperrtiesConfigurationBuilder.
     */
    @Test
    public void testBuildWithBadClc() throws Exception {
        JavaPropertiesBuilder instance = new JavaPropertiesBuilder();
        BadInputTestStream isConfig = new BadInputTestStream();
        InputStream is = PropertiesTestHelper.create("some.arg = y");
        String[] args = "--some-arg x".split(" ");
        instance.addPropertiesSource(new PropertiesStreamSource(is));
        instance.withClc(isConfig);
        try {
            instance.build(args);
        } catch (ClcException ex) {
            assertEquals("java.io.IOException: Failed to read stream.",
                    ex.getMessage());
        }
    }

    /**
     * Test of build method, of class ProperrtiesConfigurationBuilder.
     */
    @Test
    public void testBuildWithPropertiesCommandSource() throws Exception {
        JavaPropertiesBuilder instance = new JavaPropertiesBuilder();
        InputStream is = PropertiesTestHelper.create("some.arg = y");
        String[] args = "--some-arg x".split(" ");
        PropertiesStreamSource pss = new PropertiesStreamSource(is);
        PropertiesCommandSource pcs = new PropertiesCommandSource(
                name.getMethodName(), pss);
        instance.addPropertiesSource(pcs);
        try {
            instance.build(args);
        } catch (ClcException ex) {
            assertEquals("Building properties with PropertiesCommandSource is not permitted.",
                    ex.getMessage());
        }
    }

    /**
     * Test of build method, of class JavaPropertiesBuilder.
     */
    @Test
    public void testBuildNoPrpoertiesThrowsException() throws Exception {
        JavaPropertiesBuilder instance = new JavaPropertiesBuilder();
        try {
            instance.build("".split(""));
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals("No properties to build.", ex.getMessage());
        }
    }

    /**
     * Test of build method, of class JavaPropertiesBuilder.
     */
    @Test
    public void testBuildNoDuplicatesThrowsException() throws Exception {
        JavaPropertiesBuilder instance = new JavaPropertiesBuilder();
        InputStream is1 = PropertiesTestHelper.create("some.arg = y");
        InputStream is2 = PropertiesTestHelper.create("some.arg = y");
        String[] args = "--some-arg x".split(" ");
        instance.addPropertiesSource(new PropertiesStreamSource(is1))
                .addPropertiesSource(new PropertiesStreamSource(is2));
        try {
            instance.build(args);
        } catch (ClcException ex) {
            assertEquals("Duplicate already exists: some.arg", ex.getMessage());
        }
    }

    /**
     * Test of build method, of class JavaPropertiesBuilder.
     */
    @Test
    public void testBuildDuplicatesOK() throws Exception {
        JavaPropertiesBuilder instance = new JavaPropertiesBuilder();
        InputStream is1 = PropertiesTestHelper.create("some.arg = y");
        InputStream is2 = PropertiesTestHelper.create("some.arg = z");
        String[] args = "".split(" ");
        instance.addPropertiesSource(new PropertiesStreamSource(is1))
                .addPropertiesSource(new PropertiesStreamSource(is2))
                .allowDuplicates(true);
        Properties p = instance.build(args);
        assertEquals("z", p.get("some.arg"));
    }

    /**
     * Test of build method, of class JavaPropertiesBuilder.
     */
    @Test
    public void testBuildWithFile() throws Exception {
        JavaPropertiesBuilder instance = new JavaPropertiesBuilder();
        File f = PropertiesTestHelper.createFile("some.arg = y",
                JavaPropertiesBuilderTest.class.getSimpleName(),
                name.getMethodName());
        String[] args = "--some-arg x".split(" ");
        instance.addPropertiesSource(new PropertiesFileSource(f));
        Properties result = instance.build(args);
        assertEquals("x", result.get("some.arg"));
    }

    /**
     * Test of build method, of class JavaPropertiesBuilder.
     */
    @Test
    public void testBuildWithConfigurationInputStream() throws Exception {
        JavaPropertiesBuilder instance = new JavaPropertiesBuilder();
        InputStream isProps = PropertiesTestHelper.create("some.arg = y");
        InputStream isConfig = PropertiesTestHelper.create("option.some-arg.argName = something");
        String[] args = "--some-arg x".split(" ");
        instance.addPropertiesSource(new PropertiesStreamSource(isProps))
                .withClc(isConfig);
        Properties result = instance.build(args);
        assertEquals("x", result.get("some.arg"));
    }

    /**
     * Test of build method, of class JavaPropertiesBuilder.
     */
    @Test
    public void testBuildWithConfigurationFile() throws Exception {
        JavaPropertiesBuilder instance = new JavaPropertiesBuilder();
        File fProps = PropertiesTestHelper.createFile("some.arg = y",
                JavaPropertiesBuilderTest.class.getSimpleName(),
                name.getMethodName());
        File fConfig = PropertiesTestHelper.createFile("option.some-arg.argName = something",
                JavaPropertiesBuilderTest.class.getSimpleName(),
                name.getMethodName() + ".config");
        String[] args = "--some-arg x".split(" ");
        instance.addPropertiesSource(new PropertiesFileSource(fProps))
                .withClc(fConfig);
        Properties result = instance.build(args);
        assertEquals("x", result.get("some.arg"));
    }

    /**
     * Test of build method, of class JavaPropertiesBuilder.
     */
    @Test
    public void testBuildWithConfigurationFileAlreadyExists() throws Exception {
        JavaPropertiesBuilder instance = new JavaPropertiesBuilder();
        File fProps = PropertiesTestHelper.createFile("some.arg = y",
                JavaPropertiesBuilderTest.class.getSimpleName(),
                name.getMethodName());
        File fConfig = PropertiesTestHelper.createFile("option.some-arg.argName = something",
                JavaPropertiesBuilderTest.class.getSimpleName(),
                name.getMethodName() + ".config");
        try {
            instance.addPropertiesSource(new PropertiesFileSource(fProps))
                    .withClc(fConfig);
        } catch (IllegalArgumentException ex) {
            assertEquals("Configuration already set.", ex.getMessage());
        }
    }

    /**
     * Test of build method, of class JavaPropertiesBuilder.
     */
    @Test
    public void testBuildWithConfigurationInputStreamAlreadyExists() throws Exception {
        JavaPropertiesBuilder instance = new JavaPropertiesBuilder();
        File fProps = PropertiesTestHelper.createFile("some.arg = y",
                JavaPropertiesBuilderTest.class.getSimpleName(),
                name.getMethodName());
        InputStream isConfig = PropertiesTestHelper.create("option.some-arg.argName = something");
        try {
            instance.addPropertiesSource(new PropertiesFileSource(fProps))
                    .withClc(isConfig)
                    .withClc(isConfig);
        } catch (IllegalArgumentException ex) {
            assertEquals("Configuration already set.", ex.getMessage());
        }
    }

    /**
     * Test of build method, of class JavaPropertiesBuilder.
     */
    @Test
    public void testBuildWithPropertyNameFilter() throws Exception {
        JavaPropertiesBuilder instance = new JavaPropertiesBuilder();
        InputStream isProps = PropertiesTestHelper.create("some.arg = y, foo = bar");
        String[] args = "--foo foo".split(" ");
        instance.addPropertiesSource(new PropertiesStreamSource(isProps))
                .withPropertyNameFilter(
                        PropertiesTestHelper.createPropertyNameFilter("foo", false));
        try {
            instance.build(args);
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals("Unrecognized option: --foo", ex.getMessage());
        }
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testBuildNoPropertiesSupplied() throws Exception {
        JavaPropertiesBuilder instance = new JavaPropertiesBuilder();
        String[] args = "--some-arg x".split(" ");
        try {
            instance.build(args);
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals("No properties to build.", ex.getMessage());
        }
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testBuildConfiguirationDataNoPropertiesSupplied() throws Exception {
        JavaPropertiesBuilder instance = new JavaPropertiesBuilder();
        try {
            instance.buildConfigurationData();
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals("No properties to build.", ex.getMessage());
        }
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testBuildConfigurationDataWithHeader() throws Exception {
        JavaPropertiesBuilder instance = new JavaPropertiesBuilder();
        InputStream isProps = PropertiesTestHelper.create("some.arg = y");
        instance.addPropertiesSource(new PropertiesStreamSource(isProps));
        String configData = instance.buildConfigurationData();
        String expected = "some-arg";

        String[] lineData = configData.split("\n");
        Set<String> lineSet = new LinkedHashSet<>();
        for (String line : lineData) {
            if (!line.isEmpty()) {
                lineSet.add(line);
            }
        }
        Set<String> config = new HashSet<>(lineSet);
        PropertiesTestHelper.hasDefaultHelpConfigOptions(config);
        PropertiesTestHelper.hasConfigArg(config, expected);
        PropertiesTestHelper.hasConfigOpts(config, expected);
    }

    /**
     * Test that overriding the command name via a CLC overrides file works.
     *
     * @throws Exception
     */
    @Test
    public void testBuildConfigurationDataWithClcOverride() throws Exception {
        JavaPropertiesBuilder instance = new JavaPropertiesBuilder();
        InputStream isProps = PropertiesTestHelper.create("some.arg = y");
        String expectedConfig = GlobalConfiguration.GLOBAL_HELP_COMMAND_NAME + "="
                + name.getMethodName();
        InputStream configProps = PropertiesTestHelper.create(expectedConfig);
        instance.addPropertiesSource(new PropertiesStreamSource(isProps));
        instance.withClc(configProps);
        String configData = instance.buildConfigurationData();
        String expected = "some-arg";

        String[] lineData = configData.split("\n");
        Set<String> lineSet = new LinkedHashSet<>();
        for (String line : lineData) {
            if (!line.isEmpty()) {
                lineSet.add(line);
            }
        }
        Set<String> config = new HashSet<>(lineSet);
        PropertiesTestHelper.hasConfigOption(expectedConfig);
        PropertiesTestHelper.hasConfigArg(config, expected);
        PropertiesTestHelper.hasConfigOpts(config, expected);
    }

    /**
     * Test that when only input streams are used setting show origin does not
     * attempt to add file data that doesn't exist.
     *
     * @throws Exception
     */
    @Test
    public void testBuildConfigurationDataInputStreamWithShowImportOrigin() throws Exception {
        JavaPropertiesBuilder instance = new JavaPropertiesBuilder();
        InputStream isProps = PropertiesTestHelper.create("some.arg = y");
        String expectedConfig = GlobalConfiguration.GLOBAL_HELP_COMMAND_NAME + "="
                + name.getMethodName();
        InputStream configProps = PropertiesTestHelper.create(expectedConfig);
        instance.addPropertiesSource(new PropertiesStreamSource(isProps));
        instance.withClc(configProps);
        instance.showImportOrigin(true);
        String configData = instance.buildConfigurationData();
        String expected = "some-arg";

        String[] lineData = configData.split("\n");
        Set<String> lineSet = new LinkedHashSet<>();
        for (String line : lineData) {
            if (!line.isEmpty()) {
                lineSet.add(line);
            }
        }
        Set<String> config = new HashSet<>(lineSet);
        PropertiesTestHelper.hasConfigOption(expectedConfig);
        PropertiesTestHelper.hasConfigArg(config, expected);
        PropertiesTestHelper.hasConfigOpts(config, expected);
    }

    /**
     * Test that loading an input stream that fails throws an exception.
     *
     * @throws Exception
     */
    @Test
    public void testBuildConfigurationDataWithBadClc() throws Exception {
        JavaPropertiesBuilder instance = new JavaPropertiesBuilder();
        InputStream isProps = PropertiesTestHelper.create("some.arg = y");
        InputStream configProps = new BadInputTestStream();
        instance.addPropertiesSource(new PropertiesStreamSource(isProps));
        instance.withClc(configProps);
        try {
            instance.buildConfigurationData();
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals("Failed to read configuration properties:"
                    + " java.io.IOException: Failed to read stream.",
                    ex.getMessage());
        }
    }

    /**
     * Test that the import origin is added to the generated content when the
     * builder is set to show import origin.
     *
     * @throws Exception
     */
    @Test
    public void testBuildConfigurationDataWithShowImportOrigin() throws Exception {
        JavaPropertiesBuilder instance = new JavaPropertiesBuilder();
        File fileProps = PropertiesTestHelper.createFile("some.arg = y",
                JavaPropertiesBuilderTest.class.getSimpleName(), name.getMethodName());
        instance.addPropertiesSource(new PropertiesFileSource(fileProps))
                .showImportOrigin(true);
        String configData = instance.buildConfigurationData();

        String[] lineData = configData.split("\n");
        boolean foundImportLine = false;
        boolean foundFileName = false;
        for (String line : lineData) {
            if (!line.isEmpty()) {
                if (line.contains("# Generated from: ")) {
                    foundImportLine = true;
                }
                if (line.contains(fileProps.getAbsolutePath())) {
                    foundFileName = true;
                }
            }
        }
        if (!foundImportLine) {
            fail("Failed to find line containing '# Generated from: ");
        }
        if (!foundFileName) {
            fail("Failed to find line containing file name '" + fileProps.getAbsolutePath() + "'");
        }
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testBuildConfigurationDataDuplicateThrowsException() throws Exception {
        JavaPropertiesBuilder instance = new JavaPropertiesBuilder();
        InputStream is1 = PropertiesTestHelper.create("some.arg = y");
        InputStream is2 = PropertiesTestHelper.create("some.arg = z");
        instance.addPropertiesSource(new PropertiesStreamSource(is1))
                .addPropertiesSource(new PropertiesStreamSource(is2));
        try {
            instance.buildConfigurationData();
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertTrue(ex.getMessage().contains("Duplicate already exists"));
            assertTrue(ex.getMessage().contains("some.arg"));
        }

    }

    /**
     * Test that with inferred types the builder creates the correct properties.
     */
    @Test
    public void testBuildWithInferValueTypes() throws Exception {
        JavaPropertiesBuilder instance = new JavaPropertiesBuilder();
        TypeInferralConfig typeInferralConfig = new TypeInferralConfigBuilder()
                .withInferTypes().build();
        InputStream isProps = PropertiesTestHelper.create(
                PropertiesTestHelper.getMultipleValueTypePropertiesMap());
        String[] args = "--host-port 1234".split(" ");
        instance.addPropertiesSource(new PropertiesStreamSource(isProps))
                .withTypeInferralConfig(typeInferralConfig);
        Properties result = instance.build(args);
        assertEquals("localhost", result.get("host.name"));
        assertEquals(1234, result.get("host.port"));
        assertEquals(4.5f, result.get("delay"));
        assertEquals(true, result.get("reports"));
        assertEquals(false, result.get("failover"));
    }

    /**
     * Test that with inferred types the builder creates the correct property
     * configuration.
     */
    @Test
    public void testBuildWithInferValueTypesForConfiuguration() throws Exception {
        JavaPropertiesBuilder instance = new JavaPropertiesBuilder();
        TypeInferralConfig typeInferralConfig = new TypeInferralConfigBuilder()
                .withInferTypes().build();
        InputStream isProps = PropertiesTestHelper.create(
                PropertiesTestHelper.getMultipleValueTypePropertiesMap());
        instance.addPropertiesSource(new PropertiesStreamSource(isProps))
                .withTypeInferralConfig(typeInferralConfig);
        String result = instance.buildConfigurationData();
        String[] lines = result.split("\n");
        assertTrue(PropertiesTestHelper.containsProperty(lines,
                "option.host-port.type = int"));
        assertTrue(PropertiesTestHelper.containsProperty(lines,
                "option.delay.type = float"));
        assertTrue(PropertiesTestHelper.containsProperty(lines,
                "option.failover.type = boolean"));
        assertTrue(PropertiesTestHelper.containsProperty(lines,
                "option.reports.type = boolean"));
    }

    /**
     * Test that a configuration with an option specified as a directory update
     * listener receives the appropriate updates of the directory being
     * traversed.
     */
    @Test
    public void testReadPropertiesWithDirectoryUpdate() throws Exception {
        String methodName = name.getMethodName();
        PropertiesTestHelper.createTestFileSet(JavaPropertiesBuilderTest.class.getSimpleName(),
                methodName);
        InputStream is = PropertiesTestHelper.create("dir = /tmp");
        InputStream config = PropertiesTestHelper.create(
                "option.dir.type = dir\n"
                + "option.dir.properties=dirType = exists, recursive = true, listener-id = " + methodName + "\n"
                + "");
        String[] args = ("--dir target/test-classes/properties/"
                + JavaPropertiesBuilderTest.class.getSimpleName()
                + File.separator + methodName).split(" ");

        JavaPropertiesBuilder instance = new JavaPropertiesBuilder();
        instance.addPropertiesSource(new PropertiesStreamSource(is));
        instance.withClc(config);
        Clc.getInstance().addDirUpdateListener(this);
        instance.build(args);
        assertTrue(PropertiesTestHelper.checkHasFile(dirUpdateFiles,
                "dir1", "dir1File1.txt"));
        assertTrue(PropertiesTestHelper.checkHasFile(dirUpdateFiles,
                "dir1", "dir1File2.png"));
        assertTrue(PropertiesTestHelper.checkHasFile(dirUpdateFiles,
                "dir2", "dir2File1.txt"));
        assertTrue(PropertiesTestHelper.checkHasFile(dirUpdateFiles,
                "dir2", "dir2File2.png"));
    }

    /**
     * Test that a configuration with an option specified as a directory update
     * listener receives the appropriate updates of the directory being
     * traversed.
     */
    @Test
    public void testReadPropertiesWith2DirectoryUpdates() throws Exception {
        String methodName = name.getMethodName();
        String otherListenerName = "other-listener";
        PropertiesTestHelper.createTestFileSet(JavaPropertiesBuilderTest.class.getSimpleName(),
                methodName);
        PropertiesTestHelper.createTestFileSet(JavaPropertiesBuilderTest.class.getSimpleName(),
                otherListenerName);
        InputStream is = PropertiesTestHelper.create("dir1 = /tmp\n"
                + "dir2 = /tmp");
        InputStream config = PropertiesTestHelper.create(
                "option.dir1.type = dir\n"
                + "option.dir1.properties=dirType = exists, recursive = true, listener-id = " + methodName + "\n"
                + "option.dir2.type = dir\n"
                + "option.dir2.properties=dirType = exists, recursive = true, listener-id = " + otherListenerName + "\n"
                + "");
        String path1 = "target/test-classes/properties/"
                + JavaPropertiesBuilderTest.class.getSimpleName()
                + File.separator + methodName;
        String path2 = "target/test-classes/properties/"
                + JavaPropertiesBuilderTest.class.getSimpleName()
                + File.separator + otherListenerName;
        String[] args = ("--dir1 " + path1 + " --dir2 " + path2).split(" ");

        JavaPropertiesBuilder instance = new JavaPropertiesBuilder();
        instance.addPropertiesSource(new PropertiesStreamSource(is));
        instance.withClc(config);
        Clc.getInstance().addDirUpdateListener(this);
        instance.build(args);

        File dir1 = new File("dir1");
        File dir2 = new File("dir2");
        dirUpdateFiles.add(dir1);
        dirUpdateFiles.add(dir2);
        assertTrue(PropertiesTestHelper.checkHasFile(dirUpdateFiles,
                "dir1", "dir1File1.txt"));
        assertTrue(PropertiesTestHelper.checkHasFile(dirUpdateFiles,
                "dir1", "dir1File2.png"));
        assertTrue(PropertiesTestHelper.checkHasFile(dirUpdateFiles,
                "dir2", "dir2File1.txt"));
        assertTrue(PropertiesTestHelper.checkHasFile(dirUpdateFiles,
                "dir2", "dir2File2.png"));
        assertTrue(PropertiesTestHelper.checkHasFile(dirUpdateFiles,
                "dir1", "dir1File1.txt"));
        assertTrue(PropertiesTestHelper.checkHasFile(dirUpdateFiles,
                "dir1", "dir1File2.png"));
        assertTrue(PropertiesTestHelper.checkHasFile(dirUpdateFiles,
                "dir2", "dir2File1.txt"));
        assertTrue(PropertiesTestHelper.checkHasFile(dirUpdateFiles,
                "dir2", "dir2File2.png"));
    }

    /**
     * Test that when arguments are read as strings, the correct values are
     * obtained.
     */
    @Test
    public void testBuildWithArguments() throws Exception {
        JavaPropertiesBuilder instance = new JavaPropertiesBuilder();
        InputStream isProps = PropertiesTestHelper.create("some.arg = y");
        String[] args = new String[]{"--some-arg", "x", "arg1", "arg 2", "arg3"};
        instance.addPropertiesSource(new PropertiesStreamSource(isProps));
        Properties result = instance.build(args);
        assertEquals("x", result.getProperty("some.arg"));
        assertEquals(3, PropertiesListenerBindings.getInstance().getArgs().size());
        assertEquals("arg1", PropertiesListenerBindings.getInstance().getArgs().get(0));
        assertEquals("arg 2", PropertiesListenerBindings.getInstance().getArgs().get(1));
        assertEquals("arg3", PropertiesListenerBindings.getInstance().getArgs().get(2));
    }

    /**
     * Test that when value types are associated with arguments, the correct
     * values are obtained.
     */
    @Test
    public void testBuildWithArgumentsAsInts() throws Exception {
        JavaPropertiesBuilder instance = new JavaPropertiesBuilder();
        InputStream isProps = PropertiesTestHelper.create("some.arg = y");
        String[] args = new String[]{"--some-arg", "x", "1", "10", "100"};
        InputStream config = PropertiesTestHelper.create(
                "args.int-vals.type = int\n");
        instance.addPropertiesSource(new PropertiesStreamSource(isProps));
        instance.withClc(config);
        Properties result = instance.build(args);
        assertEquals("x", result.getProperty("some.arg"));
        assertEquals(3, PropertiesListenerBindings.getInstance().getArgs().size());
        assertEquals(1, PropertiesListenerBindings.getInstance().getArgsValueTypes().get(0));
        assertEquals(10, PropertiesListenerBindings.getInstance().getArgsValueTypes().get(1));
        assertEquals(100, PropertiesListenerBindings.getInstance().getArgsValueTypes().get(2));
    }

    /**
     * Test that adding the properties builder to the property listener bindings
     * updates the listener with the expected files.
     */
    @Test
    public void testBuildWithDirUpdateListener() throws Exception {
        PropertiesTestHelper.createTestFileSet(JavaPropertiesBuilderTest.class.getSimpleName(),
                name.getMethodName());
        InputStream is = PropertiesTestHelper.create("dir = /tmp");
        InputStream config = PropertiesTestHelper.create(
                "option.dir.type = dir\n"
                + "option.dir.properties=dirType = exists, recursive = true, listener-id = " + name.getMethodName() + "\n"
                + "");
        String[] args = ("--dir target/test-classes/properties/" + JavaPropertiesBuilderTest.class.getSimpleName() + File.separator + name.getMethodName()).split(" ");
        JavaPropertiesBuilder instance = new JavaPropertiesBuilder();
        instance.addPropertiesSource(new PropertiesStreamSource(is));
        instance.withClc(config);
        PropertiesListenerBindings.getInstance().addDirUpdateListener(this);
        instance.build(args);
        assertTrue(PropertiesTestHelper.checkHasFile(dirUpdateFiles, "dir1", "dir1File1.txt"));
        assertTrue(PropertiesTestHelper.checkHasFile(dirUpdateFiles, "dir1", "dir1File2.png"));
        assertTrue(PropertiesTestHelper.checkHasFile(dirUpdateFiles, "dir2", "dir2File1.txt"));
        assertTrue(PropertiesTestHelper.checkHasFile(dirUpdateFiles, "dir2", "dir2File2.png"));
    }

    /**
     * Test that properties command sources can be generated.
     *
     * @throws Exception
     */
    @Test
    public void testBuildConfigurationWithPropertiesCommandSource() throws Exception {
        JavaPropertiesBuilder instance = new JavaPropertiesBuilder();
        String cmdImport = "import";
        String cmdExport = "export";
        File fileTopLevelOptions = PropertiesTestHelper.createFile("top-level.arg = x",
                JavaPropertiesBuilderTest.class.getSimpleName(),
                name.getMethodName());
        File fileCommandImport = PropertiesTestHelper.createFile("import.arg = x",
                JavaPropertiesBuilderTest.class.getSimpleName(),
                name.getMethodName() + "-import");
        File fileCommandExport = PropertiesTestHelper.createFile("export.arg = y",
                JavaPropertiesBuilderTest.class.getSimpleName(),
                name.getMethodName() + "-export");
        PropertiesFileSource pfsTopLevel = new PropertiesFileSource(fileTopLevelOptions);
        PropertiesFileSource pfsImport = new PropertiesFileSource(fileCommandImport);
        PropertiesFileSource pfsExport = new PropertiesFileSource(fileCommandExport);
        PropertiesCommandSource pcsImport = new PropertiesCommandSource(cmdImport, pfsImport);
        PropertiesCommandSource pcsExport = new PropertiesCommandSource(cmdExport, pfsExport);
        String configData = instance.addPropertiesSource(pfsTopLevel)
                .addPropertiesSource(pcsImport)
                .addPropertiesSource(pcsExport)
                .withClc(PropertiesTestHelper.createDefaultGlobalHeader())
                .buildConfigurationData();
        String[] lineSet = configData.split("\n");
        assertTrue(PropertiesTestHelper.checkHasCommandName("import", lineSet));
        assertTrue(PropertiesTestHelper.checkHasCommandUsage("Usage: import <options>", lineSet));
        assertTrue(PropertiesTestHelper.checkHasCommandOption(cmdImport,
                "help", "help", null,
                "Print this help then exit.", lineSet));
        assertTrue(PropertiesTestHelper.checkHasCommandOption(cmdImport,
                "import-arg", "import-arg", ClcParser.TRUE,
                "Overrides property 'import.arg', default value 'x'", lineSet));

        assertTrue(PropertiesTestHelper.checkHasCommandName("export", lineSet));
        assertTrue(PropertiesTestHelper.checkHasCommandUsage("Usage: export <options>", lineSet));
        assertTrue(PropertiesTestHelper.checkHasCommandOption(cmdExport,
                "help", "help", null,
                "Print this help then exit.", lineSet));
        assertTrue(PropertiesTestHelper.checkHasCommandOption(cmdExport,
                "export-arg", "export-arg", ClcParser.TRUE,
                "Overrides property 'export.arg', default value 'y'", lineSet));
    }

    /**
     * Test that properties command sources can be generated.
     *
     * @throws Exception
     */
    @Test
    public void testBuildConfigurationWithPropertiesCommandSourceWithImportOrigin() throws Exception {
        JavaPropertiesBuilder instance = new JavaPropertiesBuilder();
        String cmdImport = "import";
        String cmdExport = "export";
        File fileTopLevelOptions = PropertiesTestHelper.createFile("top-level.arg = x",
                JavaPropertiesBuilderTest.class.getSimpleName(),
                name.getMethodName());
        File fileCommandImport = PropertiesTestHelper.createFile("import.arg = x",
                JavaPropertiesBuilderTest.class.getSimpleName(),
                name.getMethodName() + "-import");
        File fileCommandExport = PropertiesTestHelper.createFile("export.arg = y",
                JavaPropertiesBuilderTest.class.getSimpleName(),
                name.getMethodName() + "-export");
        PropertiesFileSource pfsTopLevel = new PropertiesFileSource(fileTopLevelOptions);
        PropertiesFileSource pfsImport = new PropertiesFileSource(fileCommandImport);
        PropertiesFileSource pfsExport = new PropertiesFileSource(fileCommandExport);
        PropertiesCommandSource pcsImport = new PropertiesCommandSource(cmdImport, pfsImport);
        PropertiesCommandSource pcsExport = new PropertiesCommandSource(cmdExport, pfsExport);
        String configData = instance.addPropertiesSource(pfsTopLevel)
                .addPropertiesSource(pcsImport)
                .addPropertiesSource(pcsExport)
                .showImportOrigin(true)
                .withClc(PropertiesTestHelper.createDefaultGlobalHeader())
                .buildConfigurationData();
        String[] lineSet = configData.split("\n");
        assertTrue(PropertiesTestHelper.hasImportOrigin(fileTopLevelOptions, null, lineSet));
        assertTrue(PropertiesTestHelper.hasImportOrigin(fileCommandImport, cmdImport, lineSet));
        assertTrue(PropertiesTestHelper.hasImportOrigin(fileCommandExport, cmdExport, lineSet));
    }

    /**
     * Test that a nested command usage for a command shows the correct output.
     *
     * @throws Exception
     */
    @Test
    public void testBuildConfigurationWithPropertiesCommandSourceNestedCommandHelpUsage() throws Exception {
        JavaPropertiesBuilder instance = new JavaPropertiesBuilder();
        String cmdImport = "import";
        String cmdImportSubCommand1 = "import/sub-command-1";
        File fileTopLevelOptions = PropertiesTestHelper.createFile("top-level.arg = x",
                JavaPropertiesBuilderTest.class.getSimpleName(),
                name.getMethodName());
        File fileCommandImport = PropertiesTestHelper.createFile("import.arg = x",
                JavaPropertiesBuilderTest.class.getSimpleName(),
                name.getMethodName() + "-import");
        File fileCommandImportSubCommand1 = PropertiesTestHelper.createFile("export.arg = y",
                JavaPropertiesBuilderTest.class.getSimpleName(),
                name.getMethodName() + "-import-sub-command-1");
        PropertiesFileSource pfsTopLevel = new PropertiesFileSource(fileTopLevelOptions);
        PropertiesFileSource pfsImport = new PropertiesFileSource(fileCommandImport);
        PropertiesFileSource pfsSubCommand1 = new PropertiesFileSource(fileCommandImportSubCommand1);
        PropertiesCommandSource pcsImport = new PropertiesCommandSource(cmdImport, pfsImport);
        PropertiesCommandSource pcsSubCommand1 = new PropertiesCommandSource(cmdImportSubCommand1, pfsSubCommand1);
        String configData = instance.addPropertiesSource(pfsTopLevel)
                .addPropertiesSource(pcsImport)
                .addPropertiesSource(pcsSubCommand1)
                .withClc(PropertiesTestHelper.createDefaultGlobalHeader())
                .buildConfigurationData();
        String[] lineSet = configData.split("\n");
        assertTrue(PropertiesTestHelper.checkHasCommandName("import/sub-command-1", lineSet));
        assertTrue(PropertiesTestHelper.checkHasCommandUsage("Usage: import sub-command-1 <options>", lineSet));
    }

    /**
     * Test that command usage for two nested commands shows the correct output.
     *
     * @throws Exception
     */
    @Test
    public void testBuildConfigurationWithPropertiesCommandSourceNestCommandsHelpUsage() throws Exception {
        JavaPropertiesBuilder instance = new JavaPropertiesBuilder();
        String cmdImport = "import";
        String cmdImportSubCommand1 = "import/sub-command-1";
        String cmdImportSubCommand2 = "import/sub-command-2";
        File fileTopLevelOptions = PropertiesTestHelper.createFile("top-level.arg = x",
                JavaPropertiesBuilderTest.class.getSimpleName(),
                name.getMethodName());
        File fileCommandImport = PropertiesTestHelper.createFile("import.arg = x",
                JavaPropertiesBuilderTest.class.getSimpleName(),
                name.getMethodName() + "-import");
        File fileCommandImportSubCommand1 = PropertiesTestHelper.createFile("sub-command-1.arg = y",
                JavaPropertiesBuilderTest.class.getSimpleName(),
                name.getMethodName() + "-import-sub-command-1");
        File fileCommandImportSubCommand2 = PropertiesTestHelper.createFile("sub-command-2.arg = y",
                JavaPropertiesBuilderTest.class.getSimpleName(),
                name.getMethodName() + "-import-sub-command-2");
        PropertiesFileSource pfsTopLevel = new PropertiesFileSource(fileTopLevelOptions);
        PropertiesFileSource pfsImport = new PropertiesFileSource(fileCommandImport);
        PropertiesFileSource pfsSubCommand1 = new PropertiesFileSource(fileCommandImportSubCommand1);
        PropertiesFileSource pfsSubCommand2 = new PropertiesFileSource(fileCommandImportSubCommand2);
        PropertiesCommandSource pcsImport = new PropertiesCommandSource(cmdImport, pfsImport);
        PropertiesCommandSource pcsSubCommand1 = new PropertiesCommandSource(cmdImportSubCommand1, pfsSubCommand1);
        PropertiesCommandSource pcsSubCommand2 = new PropertiesCommandSource(cmdImportSubCommand2, pfsSubCommand2);
        String configData = instance.addPropertiesSource(pfsTopLevel)
                .addPropertiesSource(pcsImport)
                .addPropertiesSource(pcsSubCommand1)
                .addPropertiesSource(pcsSubCommand2)
                .withClc(PropertiesTestHelper.createDefaultGlobalHeader())
                .buildConfigurationData();
        String[] lineSet = configData.split("\n");
        assertTrue(PropertiesTestHelper.checkHasCommandName("import/sub-command-1", lineSet));
        assertTrue(PropertiesTestHelper.checkHasCommandName("import/sub-command-2", lineSet));
        assertTrue(PropertiesTestHelper.checkHasCommandUsage("Usage: import <sub-command-1 | sub-command-2> <options>", lineSet));
    }

    /**
     * Test that with inferred types the builder creates the correct properties.
     */
    @Test
    public void testBuildWithFalseAsUnarySwitch() throws Exception {
        JavaPropertiesBuilder instance = new JavaPropertiesBuilder();
        TypeInferralConfig typeInferralConfig = new TypeInferralConfigBuilder()
                .withInferTypes().withFalseAsUnarySwitch().build();
        InputStream isProps = PropertiesTestHelper.create(
                PropertiesTestHelper.getMultipleValueTypePropertiesMap());
        String[] args = "--failover".split(" ");
        instance.addPropertiesSource(new PropertiesStreamSource(isProps))
                .withTypeInferralConfig(typeInferralConfig);
        Properties result = instance.build(args);
        assertEquals(true, result.get("failover"));
    }

    /**
     * Test some of the output of auto-generated help.
     */
    @Test
    public void testBuildGenerateHelp() throws Exception {
        JavaPropertiesBuilder instance = new JavaPropertiesBuilder();
        TypeInferralConfig typeInferralConfig = new TypeInferralConfigBuilder()
                .withInferTypes().withFalseAsUnarySwitch().build();
        InputStream isProps = PropertiesTestHelper.create(
                PropertiesTestHelper.getMultipleValueTypePropertiesMap());
        String[] args = "--help".split(" ");
        instance.addPropertiesSource(new PropertiesStreamSource(isProps))
                .withTypeInferralConfig(typeInferralConfig);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        instance.build(args);
        String output = os.toString("UTF8");
        assertTrue(output.contains("--help             Print this help then exit."));
        assertTrue(output.contains("--delay <arg>      Overrides property 'delay', default value '4.5'"));
        // Note missing <argg> entry denoting a unary switch:
        assertTrue(output.contains("--failover         Overrides property 'failover', default value"));
        assertTrue(output.contains("--host-name <arg>  Overrides property 'host.name', default value"));
        assertTrue(output.contains("--host-port <arg>  Overrides property 'host.port', default value"));
        assertTrue(output.contains("--reports <arg>    Overrides property 'reports', default value 'true'"));
    }
}
