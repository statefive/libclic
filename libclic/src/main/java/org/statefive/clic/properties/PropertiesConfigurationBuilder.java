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
import java.util.Iterator;
import java.util.List;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.statefive.clic.Clc;
import org.statefive.clic.ClcException;
import org.statefive.clic.ClcParser;
import org.statefive.clic.valuetype.ValueType;

/**
 * Apache properties configuration implementation.
 *
 * @author rich
 */
public class PropertiesConfigurationBuilder
        extends AbstractPropertiesBuilder<Configuration> {

    /**
     * List delimiter. TODO when appropriate bug tracker set up, need to make
     * this a configurable AApache properties value:
     */
    public static final char DEFAULT_LIST_DELIMITER = ',';

    /**
     * Amalgamated properties configurations from all property files and
     * property streams.
     */
    private PropertiesConfiguration properties = new PropertiesConfiguration();

    /**
     * Constructor.
     */
    public PropertiesConfigurationBuilder() {
        configurationGenerator = new PropertiesConfigurationClcGenerator();
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
    public Configuration build(String[] args) throws ClcException, IOException,
            PropertiesLoadException {
        if (this.getPropertiesSources().isEmpty()) {
            throw new ClcException("No properties to build.");
        }
        boolean header = true;
        List<PropertiesConfigurationReader> readers = new ArrayList<>();
        // first, run through all input streams and read all the properties:
        for (PropertiesSource source : this.getPropertiesSources()) {
            if (source instanceof PropertiesCommandSource) {
                throw new ClcException("Building properties with "
                        + PropertiesCommandSource.class.getSimpleName()
                        + " is not permitted.");
            }
            PropertyReader<Configuration> propertiesReader = new PropertiesConfigurationReader();
            propertiesReader.setConfigurationGenerator(configurationGenerator);
            propertiesReader.setClcGlobalHeader(header);
            propertiesReader.setTypeInferralConfig(typeInferralConfig);
            readers.add((PropertiesConfigurationReader) propertiesReader);
            Configuration c = propertiesReader.read(source);
            // check there's no duplicates allowed, check all existing keys
            // to check there are no repeated keys:
            Iterator<String> it = ((PropertiesConfiguration) c).getKeys();
            for (; it.hasNext();) {
                String key = it.next();
                String optionName = AbstractPropertiesReader.convertToOptionName(
                        key.toString());
                if (this.getOptionNames().contains(optionName)
                        && !this.isDuplicatesAllowed()) {
                    throw new ClcException("Duplicate already exists: " + key);
                } else {
                    this.getOptionNames().add(optionName);
                    properties.addProperty(key, c.getString(key));
                }
            }
            header = false;
        }

        // add a custom CLC if defined:
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

        // now generate a CLC from all the properties:
        ByteArrayOutputStream baos = configurationGenerator.generateConfiguration(
                properties, configurationProperties, getFilter(),
                true, typeInferralConfig, isPad(), isInsertDefaults());
        String configData = new String(baos.toByteArray());
        ByteArrayInputStream bis = new ByteArrayInputStream(configData.getBytes());
        // the properties are the 'original' properties read from the streams;
        // so clear then and re-insert the original properties - if any were
        // overridden via the command line, they will be updated:
        properties = new PropertiesConfiguration();
        properties.setListDelimiterHandler(new DefaultListDelimiterHandler(
                DEFAULT_LIST_DELIMITER));
        
        // finally, amalgamate all of the above properties:
        for (PropertiesConfigurationReader reader : readers) {
            for (Iterator<String> it = reader.getProperties().getKeys(); it.hasNext();) {
                String key = it.next();
                String optionName = AbstractPropertiesReader.convertToOptionName(key);
                ValueType valueType = (ValueType) configurationGenerator.getPropertyValueTypes().get(optionName);
                Object propertyValue = reader.getProperties().getProperty(key);
                if (valueType != null) {
                    properties.addProperty(key, valueType.getValue(propertyValue.toString()));
                } else {
                    properties.addProperty(key, propertyValue);
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
    public String buildConfigurationData() throws ClcException, IOException, PropertiesLoadException {
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
        boolean first = true;
        for (PropertiesSource source : this.getPropertiesSources()) {
            PropertyReader<Configuration> propertiesReader = new PropertiesConfigurationReader();
            propertiesReader.setConfigurationGenerator(configurationGenerator);
            propertiesReader.setClcGlobalHeader(first);
            propertiesReader.setTypeInferralConfig(typeInferralConfig);
            Configuration c = propertiesReader.read(source);
            // check there's no duplicates allowed, check all existing keys
            // to check there are no repeated keys:
            Iterator<String> it = ((PropertiesConfiguration) c).getKeys();
            for (; it.hasNext();) {
                String key = it.next();
                String optionName = AbstractPropertiesReader.convertToOptionName(
                        key);
                if (this.getOptionNames().contains(optionName)
                        && !this.isDuplicatesAllowed()) {
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
                    c, configurationProperties, getFilter(),
                    first, typeInferralConfig, isPad(), isInsertDefaults());
            String configData = new String(baos.toByteArray());
            first = false;
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
                properties.setProperty(mapping.toString(), valueType.getValue(
                        valueType.render(propertyValue)));
            } else {
                properties.setProperty(mapping.toString(), propertyValue.toString());
            }
        } else if (mapping != null) {
            // it's a boolean property - not a default help or version
            ValueType valueType = (ValueType) configurationGenerator.getPropertyValueTypes().get(optionName);
            properties.setProperty(mapping.toString(), valueType.getValue(ClcParser.TRUE));
        }
    }

}
