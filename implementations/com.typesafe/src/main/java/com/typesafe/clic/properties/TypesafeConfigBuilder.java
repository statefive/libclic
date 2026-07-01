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
package com.typesafe.clic.properties;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigValueFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.statefive.clic.Clc;
import org.statefive.clic.ClcException;
import org.statefive.clic.ClcParser;
import org.statefive.clic.properties.AbstractPropertiesBuilder;
import org.statefive.clic.properties.AbstractPropertiesReader;
import org.statefive.clic.properties.PropertiesCommandSource;
import org.statefive.clic.properties.PropertiesFileSource;
import org.statefive.clic.properties.PropertiesListenerBindings;
import org.statefive.clic.properties.PropertiesLoadException;
import org.statefive.clic.properties.PropertiesSource;
import org.statefive.clic.properties.PropertyReader;
import org.statefive.clic.valuetype.ValueType;

/**
 *
 * @author rich
 */
public class TypesafeConfigBuilder extends AbstractPropertiesBuilder<Config> {

    /**
     *
     */
    private Config config;

    /**
     *
     */
    static {
        Clc.initialiseValueTypeFactory();
    }

    /**
     *
     */
    public TypesafeConfigBuilder() {
        configurationGenerator = new TypesafeConfigClcGenerator();
        Clc.getInstance().addOptionListener(this);
        PropertiesListenerBindings.getInstance().setClc(Clc.getInstance());
    }

    /**
     *
     * @param args
     * @return
     * @throws ClcException
     * @throws IOException
     * @throws PropertiesLoadException
     */
    @Override
    public Config build(String[] args) throws ClcException, IOException, PropertiesLoadException {
        if (this.getPropertiesSources().isEmpty()) {
            // typesafe API will load internal using ConfigFactory.load()
            PropertyReader<Config> propertiesReader = new TypesafeConfigReader();
            propertiesReader.setConfigurationGenerator(configurationGenerator);
            propertiesReader.setTypeInferralConfig(typeInferralConfig);
            config = propertiesReader.read(new PropertiesFileSource((File) null));
        } else {
            boolean header = true;
            for (PropertiesSource source : this.getPropertiesSources()) {
                PropertyReader<Config> propertiesReader = new TypesafeConfigReader();
                propertiesReader.setConfigurationGenerator(configurationGenerator);
                propertiesReader.setTypeInferralConfig(typeInferralConfig);
                propertiesReader.setClcGlobalHeader(header);
                Config config1 = propertiesReader.read(source);
                if (config == null) {
                    config = config1;
                } else {
                    config = config1.withFallback(config);
                }
            }
            header = false;
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
                config, configProps, getFilter(), true, typeInferralConfig, false, false);
        String configurationData = new String(baos.toByteArray());
        ByteArrayInputStream bis = new ByteArrayInputStream(configurationData.getBytes());

        // ... and finally process the configuration against the arguments:
        Clc.getInstance().process(bis, args);
        bis.close();
        baos.close();
        // finally, inspect the generated configuration to determine if any
        // types have been associated with specified properties:
        for (Object propertyKey : configurationGenerator.getPropertyValueTypes().keySet()) {
            String optionName = AbstractPropertiesReader.convertToOptionName(propertyKey.toString());
            ValueType valueType = (ValueType) configurationGenerator.getPropertyValueTypes().get(optionName);
            if (valueType != null) {
                Object propertyValue = configurationGenerator.getPropertyMappings().get(propertyKey);
                if (propertyValue != null) {
                    config = config.withValue(propertyKey.toString(),
                            ConfigValueFactory.fromAnyRef(propertyValue.toString())).withFallback(config).resolve();
                }
            }
        }
        return config;
    }

    /**
     *
     * @return 
     * @throws ClcException
     * @throws IOException
     * @throws PropertiesLoadException
     */
    @Override
    public String buildConfigurationData() throws ClcException, IOException, PropertiesLoadException {
        Map<String, Object> configs = new TreeMap<>();
        boolean first = true;
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
        if (this.getPropertiesSources().isEmpty()) {
            // typesafe API will load internal using ConfigFactory.load()
            PropertyReader<Config> propertiesReader = new TypesafeConfigReader();
            propertiesReader.setTypeInferralConfig(typeInferralConfig);
            Config p = propertiesReader.read(new PropertiesFileSource((File) null));
            TypesafeHoconUtils.traverse(p, configs, null);
            ByteArrayOutputStream baos = configurationGenerator.generateConfiguration(
                    p, configurationProperties, getFilter(),
                    true, typeInferralConfig, isPad(), isInsertDefaults());
            String configData = new String(baos.toByteArray());
            first = false;
            configurationData.append(configData).append(System.lineSeparator());
            baos.close();
        } else {
            for (PropertiesSource source : this.getPropertiesSources()) {
                PropertyReader<Config> propertiesReader = new TypesafeConfigReader();
                propertiesReader.setClcGlobalHeader(first);
                propertiesReader.setTypeInferralConfig(typeInferralConfig);
                Config p = propertiesReader.read(source);
                TypesafeHoconUtils.traverse(p, configs, null);

                addImportOrigin(source);
                if (source instanceof PropertiesCommandSource) {
                    PropertiesCommandSource pcs = (PropertiesCommandSource) source;
                    generateCommandStart(pcs);
                }
                ByteArrayOutputStream baos = configurationGenerator.generateConfiguration(
                        p, configurationProperties, getFilter(),
                        first, typeInferralConfig, isPad(), isInsertDefaults());
                for (Object key : configurationGenerator.getPropertyMappings().keySet()) {
                    String optionName = AbstractPropertiesReader.convertToOptionName(
                            key.toString());
                    if (this.getOptionNames().contains(optionName) && !this.isDuplicatesAllowed()) {
                        throw new ClcException("Duplicate already exists: " + key);
                    }
                    this.getOptionNames().add(optionName);
                }
                String configData = new String(baos.toByteArray());
                first = false;
                configurationData.append(configData).append(System.lineSeparator());
                baos.close();
                first = false;
            }
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
        Object mapping = configurationGenerator.getPropertyMappings().get(optionName);
        ValueType valueType = (ValueType) configurationGenerator.getPropertyValueTypes().get(optionName);
        if (mapping != null && propertyValue != null) {
            String mapStr = mapping.toString();
            String propVal = propertyValue.toString();
            if (valueType != null) {
                config = config.withValue(mapStr,
                        ConfigValueFactory.fromAnyRef(
                                valueType.getValue(valueType.render(propertyValue))))
                        .withFallback(config).resolve();
            } else {
                config = config.withValue(mapStr,
                        ConfigValueFactory.fromAnyRef(
                                propVal)).withFallback(config).resolve();
            }
        } else if (mapping != null) {
            // it's a boolean property
            config = config.withValue(mapping.toString(),
                    ConfigValueFactory.fromAnyRef(
                            valueType.getValue(ClcParser.TRUE)))
                    .withFallback(config).resolve();
        }
    }

}
