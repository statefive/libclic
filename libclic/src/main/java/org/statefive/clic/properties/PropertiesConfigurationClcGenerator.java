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
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.ex.ConversionException;
import org.statefive.clic.ClcParser;
import org.statefive.clic.valuetype.AbstractNumberType;
import org.statefive.clic.valuetype.BooleanType;
import org.statefive.clic.valuetype.ByteType;
import org.statefive.clic.valuetype.ValueTypeCreationException;
import org.statefive.clic.valuetype.DoubleType;
import org.statefive.clic.valuetype.FloatingPointType;
import org.statefive.clic.valuetype.IntegralType;
import org.statefive.clic.valuetype.LongType;
import org.statefive.clic.valuetype.NaturalNumberType;
import org.statefive.clic.valuetype.RealNumberType;
import org.statefive.clic.valuetype.ShortType;
import org.statefive.clic.valuetype.ValueType;
import org.statefive.clic.valuetype.ValueTypeFactory;

/**
 * Generates CLC-formatted data based on Apache configuration.
 *
 * @author rich
 *
 * @param <P> Properties implementation.
 */
public class PropertiesConfigurationClcGenerator<P extends Configuration>
        extends AbstractClcGenerator<PropertiesConfiguration> {

    /**
     * Properties configuration.
     */
    private PropertiesConfiguration propertiesConfiguration;

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteArrayOutputStream generateConfiguration(PropertiesConfiguration properties,
            Configuration config, PropertyNameFilter propertyFilter,
            boolean clcGlobalHeader, TypeInferralConfig typeInferralConfig,
            boolean pad, boolean insertDefaults) throws IOException {
        Configuration clcOverrides = config;
        if (clcOverrides == null) {
            clcOverrides = new PropertiesConfiguration();
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Map<String, Object> propMap = new HashMap<>();
        for (Iterator<String> it = properties.getKeys(); it.hasNext();) {
            String key = it.next();
            String value = properties.getString(key);
            propMap.put(key, value);
        }
        Map<String, String> configMap = new LinkedHashMap<>();
        for (Iterator<String> it = clcOverrides.getKeys(); it.hasNext(); ) {
            String key = it.next();
            Object value = clcOverrides.getString(key);
            configMap.put(key, value.toString());
        }
        // keep a reference, used to infer value types:
        this.propertiesConfiguration = properties;
        os.write(generateConfiguration(propMap, configMap,
                propertyFilter, clcGlobalHeader, typeInferralConfig, pad,
                insertDefaults).getBytes());
        return os;
    }

    /**
     * Get the {@link ValueType} for the given property name and value if
     * inferring of types is set to {@code true}. If the type is determined to
     * be a {@link NaturalNumberType} or {@link RealNumberType} and the the
     * {@link TypeInferralConfig} is set to use a preferred natural or real
     * number type (respectively), the type will be set to that.
     *
     * @param propertyName non-{@code null} property name to check.
     *
     * @param value non-{@code null} value of the property.
     *
     * @return if inferring of types is set to {@code false} and no type can be
     * determined (implying the value is a standard string), null is returned,
     * otherwise the value type will be as follows when the value is turned to a
     * string, in the following order of evaluation:
     *
     * <p>
     * <ul>
     * <li>{@link ByteType}</li> if the value fits into a Java {@code byte};
     * <li>{@link ShortType} if the value fits into a Java {@code short};</li>
     * <li>{@link IntegralType} if the value fits into a Java {@code int};</li>
     * <li>{@link LongType} if the value fits into a Java {@code long};</li>
     * <li>{@link FloatingPointType} if the value fits into the size of a Java
     * {@code float};</li>
     * <li>{@link DoubleType} if the value fits into the size of a Java
     * {@code double};</li>
     * <li>{@link BooleanType} if the value is the string (ignoring case)
     * {@code true} or {@code false}; and</li>
     * <li>{@code null} if none of the above apply.</li>
     * </ul>
     *
     * @throws ValueTypeCreationException if a value type is used that does not
     * exist.
     */
    @Override
    public ValueType getPropertyValueType(String propertyName, Object value)
            throws ValueTypeCreationException {
        ValueType valueType = null;
        if (this.typeInferralConfig.isInferTypes()) {
            valueType = inferValueType(propertyName, value);
            if (valueType != null
                    && valueType instanceof AbstractNumberType) {
                AbstractNumberType numberType = (AbstractNumberType) valueType;
                if (typeInferralConfig.getNaturalNumbersAs() != null) {
                    valueType = setNumberAs(numberType,
                            typeInferralConfig.getNaturalNumbersAs());
                } else if (typeInferralConfig.getRealNumbersAs() != null) {
                    valueType = setNumberAs(numberType,
                            typeInferralConfig.getRealNumbersAs());
                }
            }
        }
        if (valueType != null) {
            propertyValueTypes.put(propertyName, valueType);
        }
        return valueType;
    }

    /**
     * Determine what the value type is for the given property value. For values
     * that are determined to be numbers, the value assigned will be the
     * 'smallest' fit for that number type - in order of size for natural
     * numbers will be {@link IntegralType} or {@link LongType}, and for real
     * numbers {@link FloatingPointType} or {@link DoubleType}.
     *
     * @param propertyName non-{@code null} property name.
     *
     * @param value non-{@code null} property value.
     *
     * @return a {@link NaturalNumberType}, {@link RealNumberType} or
     * {@link BooleanType}; {@code null} otherwise.
     */
    private ValueType inferValueType(String propertyName, Object value) {
        ValueTypeFactory instance = ValueTypeFactory.getInstance();
        ValueType valueType = null;
        try {
            propertiesConfiguration.getInt(propertyName);
            valueType = instance.create(IntegralType.INTEGRAL);
            valueType.getValue(value.toString());
        } catch (ConversionException | ValueTypeCreationException ex) {
            // value doesn't fit into an int, continue
        }
        if (valueType == null) {
            try {
                propertiesConfiguration.getLong(propertyName);
                valueType = instance.create(LongType.LONG);
                valueType.getValue(value.toString());
            } catch (ConversionException | ValueTypeCreationException ex) {
                // value doesn't fit into a long, continue
            }
        }
        if (valueType == null) {
            try {
                propertiesConfiguration.getFloat(propertyName);
                valueType = instance.create(FloatingPointType.FLOAT);
                valueType.getValue(value.toString());
            } catch (ConversionException | ValueTypeCreationException ex) {
                // value doesn't fit into a float, continue
                valueType = null;
            }
        }
        if (valueType == null) {
            try {
                propertiesConfiguration.getDouble(propertyName);
                valueType = instance.create(DoubleType.DOUBLE);
                valueType.getValue(value.toString());
            } catch (ConversionException | ValueTypeCreationException ex) {
                // value doesn't fit into a double, continue
                valueType = null;
            }
        }
        if (valueType == null
                && (ClcParser.TRUE.equals(value.toString().toLowerCase())
                || ClcParser.FALSE.equals(value.toString().toLowerCase()))) {
            // we have to explicitly match on 'true' or 'false' because
            // boolean creation will accept any value and if not 'true' will
            // evaluate to false, meaning anything that doesn't match the
            // above will ALWWAYS be evaluated to a boolean, which we want
            // to avoid:
            propertiesConfiguration.getBoolean(propertyName);
            valueType = instance.create(BooleanType.BOOLEAN);
            valueType.getValue(value.toString());
        }
        return valueType;
    }

    /**
     * Set the given value type as the specified number type. If the number type
     * name is the same as the given type, no change will be made.
     *
     * @param numberType non-{@code null} number type.
     *
     * @param type non-{@code null} type name.
     *
     * @return non-{@code null} value type.
     *
     * @throws ValueTypeCreationException if the type does not exist.
     */
    private ValueType setNumberAs(AbstractNumberType numberType, String type) {
        ValueTypeFactory instance = ValueTypeFactory.getInstance();
        ValueType valueType = null;
        if (numberType.getJavaClassName().equals(type)) {
            valueType = numberType;
        } else {
            valueType = instance.create(type);
        }
        return valueType;
    }
}
