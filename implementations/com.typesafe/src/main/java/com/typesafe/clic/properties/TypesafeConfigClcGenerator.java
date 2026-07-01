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
import com.typesafe.config.ConfigList;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.statefive.clic.properties.AbstractClcGenerator;
import org.statefive.clic.properties.PropertyNameFilter;
import org.statefive.clic.properties.TypeInferralConfig;
import org.statefive.clic.valuetype.BooleanType;
import org.statefive.clic.valuetype.FloatingPointType;
import org.statefive.clic.valuetype.IntegralType;
import org.statefive.clic.valuetype.ValueType;
import org.statefive.clic.valuetype.ValueTypeFactory;
import org.statefive.clic.valuetype.ListType;

/**
 * Supported types:
 *
 * <p>
 * <ul>
 * <li>{@code int};</li>
 * <li>{@code float};</li>
 * <li>{@code boolean};</li>
 * <li>{@code list} via the {@code com.typesafe.config.ConfigList}
 * implementation;</li>
 * </ul>
 *
 * @author rich
 * @param <P>
 */
public class TypesafeConfigClcGenerator<P extends Config> extends AbstractClcGenerator<Config> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteArrayOutputStream generateConfiguration(Config properties,
            Configuration config, PropertyNameFilter propertyFilter,
            boolean clcGlobalHeader, TypeInferralConfig typeInferralConfig,
            boolean pad, boolean insertDefaults) throws IOException {
        if (typeInferralConfig != null) {
            this.typeInferralConfig = typeInferralConfig;
        }
        Configuration clcOverrides = config;
        if (clcOverrides == null) {
            clcOverrides = new PropertiesConfiguration();
        }
        Map<String, Object> p = new HashMap<>();
        Map<String, Object> propsMap = new HashMap<>();
        TypesafeHoconUtils.traverseFromRoot(properties, p);
        for (String key : p.keySet()) {
            propsMap.put(key, p.get(key));
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Map<String, String> propMap = new HashMap<>();
        for (Object key : p.keySet()) {
            Object value = p.get(key.toString());
            propMap.put(key.toString(), value.toString());
        }
        Map<String, String> configMap = new LinkedHashMap<>();
        for (Iterator<String> it = clcOverrides.getKeys(); it.hasNext();) {
            String key = it.next();
            Object value = clcOverrides.getString(key);
            configMap.put(key, value.toString());
        }
        for (String clcKey : configMap.keySet()) {
            clcMappings.put(clcKey, configMap.get(clcKey));
        }
        baos.write(generateConfiguration(propsMap, configMap,
                propertyFilter, clcGlobalHeader,
                this.typeInferralConfig, pad, insertDefaults).getBytes());
        return baos;
    }

    /**
     *
     * @param propertyName
     * @param value
     * @return
     */
    @Override
    public ValueType getPropertyValueType(String propertyName, Object value) {
        ValueTypeFactory instance = ValueTypeFactory.getInstance();
        ValueType valueType = null;
        if (value instanceof Integer) {
            valueType = instance.create(IntegralType.INTEGRAL);
            valueType.getValue(value.toString());
        } else if (value instanceof Double) {
            valueType = instance.create(FloatingPointType.FLOAT);
            valueType.getValue(value.toString());
        } else if (value instanceof Boolean) {
            valueType = instance.create(BooleanType.BOOLEAN);
            valueType.getValue(value.toString());
        } else if (value instanceof ConfigList) {
            valueType = instance.create(ListType.LIST);
            ConfigList configList = (ConfigList) value;
            valueType.getValue(StringUtils.join(configList.unwrapped(),
                    ConfigListType.LIST_SEPARATOR_CHAR));
        } else if (!(value instanceof String)) {
            throw new UnsupportedOperationException("Unexpected type: " + value.getClass().getName());
        }
        if (valueType != null) {
            propertyValueTypes.put(propertyName, valueType);
        }
        return valueType;
    }

}
