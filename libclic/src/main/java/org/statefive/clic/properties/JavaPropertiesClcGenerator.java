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
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.statefive.clic.ClcException;
import org.statefive.clic.valuetype.ValueType;

/**
 * Base class providing support for a map of property keys to values as Java
 * {@code String}s.
 *
 * @author rich
 *
 * @param <P> Properties implementation.
 */
public class JavaPropertiesClcGenerator<P extends Properties>
        extends AbstractClcGenerator<Properties> {
    
    /**
     * Properties used to generate the configuration.
     */
    private Properties properties;

    /**
     * {@inheritDoc}
     * 
     * @since 1.1
     */
    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.1
     */
    @Override
    public ByteArrayOutputStream generateConfiguration() throws ClcException, IOException {
        if (clcOverrides == null) {
            clcOverrides = new PropertiesConfiguration();
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Map<String, Object> propMap = new HashMap<>();
        for (Object key : properties.keySet()) {
            Object value = properties.get(key.toString());
            propMap.put(key.toString(), value.toString());
        }
        Map<String, String> configMap = new LinkedHashMap<>();
        for (Iterator<String> it = clcOverrides.getKeys(); it.hasNext(); ) {
            String key = it.next();
            Object value = clcOverrides.getString(key);
            configMap.put(key, value.toString());
        }
        os.write(generateConfiguration(propMap, configMap).getBytes());
        return os;
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated use {@link #generateConfiguration()}; deprecated since 1.1.
     */
    @Override
    public ByteArrayOutputStream generateConfiguration(Properties properties,
            Configuration config, PropertyNameFilter propertyFilter,
            boolean clcGlobalHeader, TypeInferralConfig typeInferralConfig,
            boolean pad, boolean insertDefaults) throws ClcException, IOException {
        clcOverrides = config;
        if (config == null) {
            clcOverrides = new PropertiesConfiguration();
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Map<String, Object> propMap = new HashMap<>();
        for (Object key : properties.keySet()) {
            Object value = properties.get(key.toString());
            propMap.put(key.toString(), value.toString());
        }
        Map<String, String> configMap = new LinkedHashMap<>();
        for (Iterator<String> it = clcOverrides.getKeys(); it.hasNext(); ) {
            String key = it.next();
            Object value = clcOverrides.getString(key);
            configMap.put(key, value.toString());
        }
        os.write(generateConfiguration(propMap, configMap,
                propertyFilter, clcGlobalHeader, typeInferralConfig,
                pad, insertDefaults).getBytes());
        return os;
    }

    /**
     * Attempt to infer the type based on the regular expression matching of the
     * given value.
     *
     * @param propertyName non-{@code null} name of the original property.
     *
     * @param value non-{@code null} value to check.
     *
     * @return a {@link ValueType} of the correct value if it is not a string;
     * {@code null} otherwise.
     */
    @Override
    public ValueType getPropertyValueType(String propertyName, Object value) {
        ValueType valueType = RegexPropertyValueTypeExtractor.getPropertyValueType(
                propertyName, value, typeInferralConfig);
        if (valueType != null) {
            propertyValueTypes.put(propertyName, valueType);
        }
        return valueType;
    }

}
