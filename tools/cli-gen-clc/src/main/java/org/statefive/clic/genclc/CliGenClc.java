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
package org.statefive.clic.genclc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.apache.commons.cli.Option;
import org.statefive.clic.Clc;
import org.statefive.clic.ClcException;
import static org.statefive.clic.Command.COMMAND_PATH_SEPARATOR;
import org.statefive.clic.properties.JavaPropertiesBuilder;
import org.statefive.clic.properties.PropertiesBuilder;
import org.statefive.clic.properties.PropertiesCommandSource;
import static org.statefive.clic.properties.PropertiesCommandSource.COMMAND_SOURCE_PATH_SEPARATOR;
import static org.statefive.clic.properties.PropertiesCommandSource.COMMAND_SOURCE_PREFIX_SEPARATOR;
import org.statefive.clic.properties.PropertiesConfigurationBuilder;
import org.statefive.clic.properties.PropertiesFileSource;
import org.statefive.clic.properties.PropertiesSource;
import org.statefive.clic.properties.PropertiesStreamSource;
import org.statefive.clic.properties.PropertyNameFilter;
import org.statefive.clic.valuetype.ValueType;
import org.statefive.clic.valuetype.ValueTypeBuilder;
import org.statefive.clic.valuetype.ValueTypeCreationException;
import org.statefive.clic.valuetype.ValueTypeFactory;

/**
 * Tool to generate CLC (command line configuration) output.
 *
 * @author rich
 */
public class CliGenClc {

    /**
     * Command line configuration.
     */
    private final Clc clc = Clc.getInstance();

    /**
     * Filter to include/exclude properties from the generated configuration.
     */
    private PropertyNameFilter filter;

    /**
     * Properties builder.
     */
    private PropertiesBuilder propertiesBuilder;

    /**
     * Helper; has options and arguments updated from the underlying CLC API.
     */
    private final OptionHelper optionHelper = new OptionHelper();

    /**
     * Main entry point.
     *
     * @param args non-{@code null} command line arguments to process.
     */
    public static void main(String[] args) {
        CliGenClc cliGenConfig = new CliGenClc();
        try {
            cliGenConfig.buildCliOptions(args);
            try {
                cliGenConfig.generateConfiguration();
            } catch (Exception ex) {
                if (cliGenConfig.isStackTrace()) {
                    ex.printStackTrace();
                }
                System.err.println("Error: " + ex.getMessage());
                System.exit(1);
            }
        } catch (ClcException | IOException ex) {
            if (cliGenConfig.isStackTrace()) {
                ex.printStackTrace();
            }
            System.err.println("Error: " + ex.getMessage());
            System.err.println("Try -h/--help for options.");
            System.exit(1);
        } catch (Exception ex) {
            if (cliGenConfig.isStackTrace()) {
                ex.printStackTrace();
            }
            System.err.println("Error: " + ex.getMessage());
            System.err.println("Try -h/--help for options.");
            System.exit(1);
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
     * Generate a configuration; the configuration will be output to standard
     * output unless the option to specify writing to file is supplied.
     *
     * @throws Exception if generation fails.
     */
    public void generateConfiguration() throws Exception {
        filter = new PropertyNameFilter(optionHelper.getIncludes(),
                optionHelper.getExcludes());
        if (!optionHelper.isJavaProperties()) {
            // Apache PropertiesConfiguration
            if (optionHelper.getBuilder() == null) {
                propertiesBuilder = new PropertiesConfigurationBuilder();
            } else {
                // use the defined builder and hope that the class and the
                // class path is valid:
                propertiesBuilder
                        = (PropertiesBuilder) PropertiesBuilder.class.getClassLoader()
                                .loadClass(optionHelper.getBuilder())
                                .getDeclaredConstructor().newInstance();
            }
        } else {
            // Java utility Properties
            propertiesBuilder = new JavaPropertiesBuilder();
        }
        if (optionHelper.isPropsAsInputStream()) {
            propertiesBuilder.addPropertiesSource(new PropertiesStreamSource(
                    System.in));
        } else {
            for (File file : optionHelper.getPropsFiles()) {
                PropertiesSource propertiesSource = null;
                if (file.getName().contains(COMMAND_SOURCE_PREFIX_SEPARATOR)) {
                    propertiesSource = getPropertiesCommandSource(file);
                } else {
                    if (!file.exists() && !file.isFile()) {
                        throw new ValueTypeCreationException("Specified file "
                                + Paths.get(file.getAbsolutePath()).normalize()
                                + " does not exist (or is not"
                                + " a file).");
                    }
                    propertiesSource = new PropertiesFileSource(file);
                }
                propertiesBuilder.addPropertiesSource(propertiesSource);
            }
        }
        if (optionHelper.getConfig() != null) {
            propertiesBuilder.withClc(optionHelper.getConfig());
        }
        if (optionHelper.getValueTypes() != null) {
            loadValueTypes();
        }
        if (optionHelper.isListTypes()) {
            listTypes();
            System.exit(0);
        }
        propertiesBuilder.withPropertyNameFilter(filter)
                .showImportOrigin(optionHelper.isShowImportOrigin())
                .allowDuplicates(optionHelper.isAllowDuplicates())
                .pad(optionHelper.isPad())
                .insertDefaults(optionHelper.isInsertDefaults())
                .withTypeInferralConfig(
                        optionHelper.getTypeInferralConfigBuilder().build());
        String data = propertiesBuilder.buildConfigurationData();
        if (optionHelper.isHeader()) {
            data = getHeader(data);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.writeBytes(data.getBytes());
        if (optionHelper.getOutputFile() == null) {
            System.out.println(new String(baos.toByteArray()));
        } else {
            FileOutputStream fos = new FileOutputStream(optionHelper.getOutputFile());
            fos.write(baos.toByteArray());
            fos.close();
        }
        baos.close();
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
        InputStream is = CliGenClc.class.getResourceAsStream(
                "/cli-gen-clc.clc");
        clc.addArgsListener(optionHelper);
        clc.addOptionListener(optionHelper);
        clc.process(is, "UTF-8", args);
        if (!optionHelper.getIncludes().isEmpty()
                && !optionHelper.getExcludes().isEmpty()) {
            throw new ClcException("Cannot specify both"
                    + " includes and excludes; use just one.");
        }
    }

    /**
     * Create a properties command source from the given file; the file must
     * contain the
     * {@link PropertiesCommandSource#COMMAND_SOURCE_PREFIX_SEPARATOR}
     * character, and the data before the separator will be treated as the
     * command path. Nested commands must use the
     * {@link PropertiesCommandSource#COMMAND_SOURCE_PATH_SEPARATOR}.
     *
     * @param file non-{@code null} file to convert.
     *
     * @return non-{@code null} command source.
     */
    private PropertiesCommandSource getPropertiesCommandSource(File file) {
        File parent = file.getParentFile();
        String command = file.getName().substring(0,
                file.getName().indexOf(COMMAND_SOURCE_PREFIX_SEPARATOR));
        String name = file.getName().substring(file.getName().indexOf(
                COMMAND_SOURCE_PREFIX_SEPARATOR) + 1,
                file.getName().length());
        String commandPath = command.replace(
                COMMAND_SOURCE_PATH_SEPARATOR, COMMAND_PATH_SEPARATOR);
        File cmdFile = new File(parent, name);
        if (!cmdFile.exists() || !cmdFile.isFile()) {
            throw new ValueTypeCreationException("Specified file "
                    + Paths.get(cmdFile.getAbsolutePath()).normalize()
                    + " does not exist (or is not a file).");
        }
        return new PropertiesCommandSource(commandPath,
                new PropertiesFileSource(cmdFile));
    }

    /**
     * Prepend the data with header data, consisting of:
     *
     * <p>
     * <ul>
     * <li>Application name, version and date generated;</li>
     * <li>Options used with values (for binary switches) used; and</li>
     * <li>argument values used</li>
     * </ul>
     *
     * @param data non-{@code null} CLC data.
     *
     * @return non-{@code null} data with the header prepended.
     */
    private String getHeader(String data) throws IOException {
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
        sb.append("#")
                .append(System.lineSeparator())
                .append("# Generated by ")
                .append(appName)
                .append(" version ")
                .append(version)
                .append(", ")
                .append(sdf.format(new Date()))
                .append(System.lineSeparator())
                .append("#")
                .append(System.lineSeparator());
        List<Option> options = Clc.getInstance().getOptions();
        sb.append("# Options:").append(System.lineSeparator())
                .append("#");
        for (Option option : options) {
            String value = option.getValue();
            sb.append(System.lineSeparator())
                    .append("# -")
                    .append(option.getOpt())
                    .append("/--")
                    .append(option.getLongOpt());
            if (value != null) {
                sb.append(" ").append(option.getValue());
            }
        }
        sb.append(System.lineSeparator());
        List<String> args = Clc.getInstance().getArgs();
        sb.append("#").append(System.lineSeparator());
        sb.append("# Arguments:").append(System.lineSeparator())
                .append("#");
        for (int i = 0; i < args.size(); i++) {
            sb.append(System.lineSeparator())
                    .append("# ")
                    .append(args.get(i));
        }
        sb.append(System.lineSeparator())
                .append("#")
                .append(System.lineSeparator())
                .append(System.lineSeparator());
        sb.append(data);
        return sb.toString();
    }

    /**
     * Attempt to load value types loaded from the command line. Types are
     * specified as a type name separated by a colon followed by the type class
     * name, multiple types being separated by commas.
     *
     * @throws ValueTypeCreationException if the values within the commas are
     * not separated by a colon, if there is more than colon in any given group,
     * or the given class name cannot be loaded.
     */
    private void loadValueTypes() {
        String[] types = optionHelper.getValueTypes().split(",");
        for (String type : types) {
            type = type.trim();
            if (!type.contains(":")) {
                throw new ValueTypeCreationException("Value types must be"
                        + " separated by a colon.");
            }
            String[] typeValues = type.split(":");
            if (typeValues.length != 2) {
                throw new ValueTypeCreationException("Expected 2 entries"
                        + " for value type, got: " + optionHelper.getValueTypes());
            }
            String typeName = typeValues[0].trim();
            String typeClass = typeValues[1].trim();
            try {
                Class cla$$ = Class.forName(typeClass);
                ValueTypeFactory.getInstance().removeRegisteredValueType(typeName);

                ValueTypeBuilder<ValueType> valueTypeImpl
                        = new ValueTypeBuilder<>(cla$$);
                ValueTypeFactory.getInstance().registerValueTypeBuilder(
                        typeName, valueTypeImpl);

            } catch (ClassNotFoundException ex) {
                throw new ValueTypeCreationException(
                        "Could not create value type " + typeClass
                        + ": Class not found.");
            }
        }
    }

    /**
     * Print to standard output all available types sorted alphabetically.
     */
    private void listTypes() {
        List<String> types = new ArrayList<>(
                ValueTypeFactory.getInstance().getRegisteredValueTypes());
        Collections.sort(types);
        for (String type : types) {
            ValueType valueType = ValueTypeFactory.getInstance().create(type);
            if (valueType.getPackageName() != null) {
                System.out.println(type + " : " + valueType.getPackageName() + "."
                        + valueType.getJavaClassName());
            } else {
                System.out.println(type + " : " + valueType.getJavaClassName());
            }
        }
    }
}
