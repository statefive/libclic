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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.statefive.clic.Clc;
import org.statefive.clic.ClcException;
import org.statefive.clic.ClcParser;
import org.statefive.clic.valuetype.ValueType;

/**
 * Java utility properties implementation.
 *
 * @author rich
 */
public class JavaPropertiesBuilder
        extends AbstractPropertiesBuilder<Properties> {

    /**
     * Amalgamated properties from all property files and property streams.
     */
    private final Properties properties = new Properties();

    /**
     * Constructor.
     */
    public JavaPropertiesBuilder() {
        configurationGenerator = new JavaPropertiesClcGenerator();
    }

    /**
     * Build with arguments inserted when the builder was made.
     *
     * @param args non-{@code null} command line arguments (may be empty).
     *
     * @return non-{@code null} properties.
     *
     * @throws IOException if there is an I/O error readings sources of
     * properties.
     *
     * @throws ClcException if any of the properties or configuration (if there
     * is one) are invalid, or if there are no properties.
     *
     * @throws PropertiesLoadException if any of the sources are invalid (not
     * all builders cater for all types of sources), or the underlying sources
     * cannot be read.
     */
    @Override
    public Properties build(String[] args)
            throws IOException, ClcException, PropertiesLoadException {
        if (this.getPropertiesSources().isEmpty()) {
            throw new ClcException("No properties to build.");
        }
        // keep track of when to insert global header prior to adding properties:
        boolean first = true;
        List<JavaPropertiesReader> propertiesReaders = new ArrayList<>();
        // first, run through all input streams and read all the properties:
        for (PropertiesSource source : this.getPropertiesSources()) {
            if (source instanceof PropertiesCommandSource) {
                throw new ClcException("Building properties with "
                        + PropertiesCommandSource.class.getSimpleName()
                        + " is not permitted.");
            }
            PropertyReader<Properties> propertiesReader = new JavaPropertiesReader();
            propertiesReader.setConfigurationGenerator(configurationGenerator);
            propertiesReader.setClcGlobalHeader(first);
            propertiesReader.setTypeInferralConfig(typeInferralConfig);
            Properties p = propertiesReader.read(source);
            propertiesReaders.add((JavaPropertiesReader) propertiesReader);
            // check there's no duplicates allowed, check all existing keys
            // to check there are no repeated keys:
            for (Object key : p.keySet()) {
                String optionName = AbstractPropertiesReader.convertToOptionName(
                        key.toString());
                if (this.getOptionNames().contains(optionName) && !this.isDuplicatesAllowed()) {
                    throw new ClcException("Duplicate already exists: " + key);
                } else {
                    this.getOptionNames().add(optionName);
                    properties.put(key, p.get(key));
                }
            }
            first = false;
        }

        // add a custom CLC if defined:
        Configuration configProps = new PropertiesConfiguration();
        if (getConfigInputStream() != null) {
            Reader reader = new InputStreamReader(getConfigInputStream());
            try {
                ((PropertiesConfiguration) configProps).read(reader);
            } catch (ConfigurationException ex) {
                throw new ClcException(ex.getMessage());
            }
        }

        // now generate a CLC from all the properties:
        ByteArrayOutputStream baos = configurationGenerator.generateConfiguration(
                properties, configProps, getFilter(), true, 
                typeInferralConfig, isPad(), isInsertDefaults());
        String configurationData = new String(baos.toByteArray());
        ByteArrayInputStream bis = new ByteArrayInputStream(configurationData.getBytes());

        // finally, amalgamate all of the above properties:
        properties.clear();
        for (JavaPropertiesReader r : propertiesReaders) {
            for (Object o : r.getProperties().keySet()) {
                String key = o.toString();
                String optionName = AbstractPropertiesReader.convertToOptionName(key);
                ValueType valueType = (ValueType) configurationGenerator.getPropertyValueTypes().get(optionName);
                String propertyValue = r.getProperties().getProperty(key);
                if (valueType != null) {
                    properties.put(key, valueType.getValue(propertyValue));
                } else {
                    properties.setProperty(key, propertyValue);
                }
            }
        }
        // ... and finally process the configuration against the arguments:
        Clc.getInstance().process(bis, args);
        bis.close();
        baos.close();
        return properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String buildConfigurationData()
            throws ClcException, IOException, PropertiesLoadException {
        Clc.initialiseValueTypeFactory();
        if (this.getPropertiesSources().isEmpty()) {
            throw new ClcException("No properties to build.");
        }
        Configuration configurationProperties = new PropertiesConfiguration();
        if (getConfigInputStream() != null) {
            Reader reader = new InputStreamReader(getConfigInputStream());
            try {
                ((PropertiesConfiguration) configurationProperties).read(reader);
            } catch (ConfigurationException ex) {
                throw new ClcException("Failed to read configuration properties: "
                        + ex.getMessage(), ex);
            }
            reader.close();
        }
        // keep track of when to insert global header prior to adding properties:
        boolean header = true;
        for (PropertiesSource source : this.getPropertiesSources()) {
            PropertyReader<Properties> propertiesReader = new JavaPropertiesReader();
            propertiesReader.setClcGlobalHeader(header);
            propertiesReader.setTypeInferralConfig(typeInferralConfig);
            Properties p = propertiesReader.read(source);
            // check there's no duplicates allowed, check all existing keys
            // to check there are no repeated keys:
            for (Object key : p.keySet()) {
                String optionName = AbstractPropertiesReader.convertToOptionName(
                        key.toString());
                if (this.getOptionNames().contains(optionName) && !this.isDuplicatesAllowed()) {
                    throw new ClcException("Duplicate already exists: " + key);
                }
                this.getOptionNames().add(optionName);
            }
            addImportOrigin(source);
            if (source instanceof PropertiesCommandSource) {
                PropertiesCommandSource pcs = (PropertiesCommandSource) source;
                generateCommandStart(pcs);
            }
            ByteArrayOutputStream baos = configurationGenerator.generateConfiguration(
                    p, configurationProperties, getFilter(),
                    header, typeInferralConfig, isPad(), isInsertDefaults());
            String configData = new String(baos.toByteArray());
            header = false;
            configurationData.append(configData).append(System.lineSeparator());
            baos.close();
        }
        if (commandRoot != null) {
            // we've got commands, fill out the usage and add default help if
            // help has been specified
            processCommands(commandRoot);
        }
        return configurationData.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProperty(String optionName, Object propertyValue) {
        super.setProperty(optionName, propertyValue);
        Object mapping = configurationGenerator.getPropertyMappings().get(optionName);
        if (mapping != null && propertyValue != null) {
            ValueType valueType = (ValueType) configurationGenerator.getPropertyValueTypes().get(optionName);
            if (valueType != null) {
                properties.put(mapping, valueType.getValue(
                        valueType.render(propertyValue)));
            } else {
                properties.put(mapping, propertyValue.toString());
            }
        } else if (mapping != null) {
            // it's a boolean property - not a default help or version
            ValueType valueType = (ValueType) configurationGenerator.getPropertyValueTypes().get(optionName);
            properties.put(mapping, valueType.getValue(ClcParser.TRUE));
        }
    }

}
