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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.statefive.clic.valuetype.AbstractNumberType;
import org.statefive.clic.valuetype.BooleanType;
import org.statefive.clic.valuetype.ValueTypeCreationException;
import org.statefive.clic.valuetype.FloatingPointType;
import org.statefive.clic.valuetype.IntegralType;
import org.statefive.clic.valuetype.ValueType;
import org.statefive.clic.valuetype.ValueTypeFactory;

/**
 * Determine property value types based on regular expressions.
 *
 * @author rich
 */
public class RegexPropertyValueTypeExtractor {

    /**
     * Whole number match.
     */
    public static String REGEX_NATURAL_NUMBER = "[\\-]?[0-9]+";

    /**
     * Decimal place number match.
     */
    public static String REGEX_REAL_NUMBER = "[\\-]?[0-9]+\\.[0-9]+";

    /**
     * True match.
     */
    public static String BOOLEAN_TRUE = Boolean.TRUE.toString();

    /**
     * False match.
     */
    public static String BOOLEAN_FALSE = Boolean.FALSE.toString();

    /**
     * Attempt to infer the type based on pattern matching, according to the
     * following rules:
     *
     * <p>
     * <ol>
     * <li>If the string, ignoring case, is {@code true} or {@code false}, the
     * returned value will be {@link BooleanType};</li>
     * <li>If the string is a positive or negative number the returned value
     * will be {@link IntegralType}, unless the inference natural number type is
     * not {@code null} in which case that type will be used;</li>
     * <li>If the string is a positive or negative number with a decimal place
     * the return value will be {@link FloatingPointType}, unless the inference
     * real number type is not {@code null} in which case that type will be
     * used;</li>
     * <li>Otherwise the returned value will be {@code null}.</li>
     * </ol>
     *
     * @param propertyName non-{@code null} name of the original property.
     *
     * @param value non-{@code null} value to check.
     *
     * @param typeInferralConfig non-{@code null} type inference configuration.
     *
     * @return a {@link ValueType} of the correct value if it is not a string;
     * {@code null} otherwise, or if inferral of value types was not set.
     *
     * @throws ValueTypeCreationException if the type does not exist.
     */
    public static ValueType getPropertyValueType(String propertyName,
            Object value, TypeInferralConfig typeInferralConfig) {
        ValueTypeFactory instance = ValueTypeFactory.getInstance();
        ValueType valueType = null;
        String valueStr = value.toString();
        String regex = REGEX_NATURAL_NUMBER;
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(valueStr);
        if (m.matches()) {
            valueType = instance.create(IntegralType.INTEGRAL);
        }
        if (valueType == null) {
            regex = REGEX_REAL_NUMBER;
            p = Pattern.compile(regex);
            m = p.matcher(valueStr);
            if (m.matches()) {
                valueType = instance.create(FloatingPointType.FLOAT);
            }
        }
        if (valueType != null) {
            // must be a number
            AbstractNumberType numberType = (AbstractNumberType) valueType;
            if (typeInferralConfig.getNaturalNumbersAs() != null) {
                valueType = setNumberAs(numberType, typeInferralConfig.getNaturalNumbersAs());
            } else if (typeInferralConfig.getRealNumbersAs() != null) {
                valueType = setNumberAs(numberType, typeInferralConfig.getRealNumbersAs());
            }
        }
        if (valueType == null && (BOOLEAN_TRUE.equals(valueStr.toLowerCase())
                || BOOLEAN_FALSE.equals(valueStr.toLowerCase()))) {
            valueType = instance.create(BooleanType.BOOLEAN);
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
     * @return non-{@code null} the value type.
     *
     * @throws ValueTypeCreationException if the type does not exist.
     */
    private static ValueType setNumberAs(AbstractNumberType numberType, String type) {
        ValueTypeFactory instance = ValueTypeFactory.getInstance();
        ValueType valueType = null;
        if (numberType.getJavaClassName().equals(type)) {
            // it's the same, no change:
            valueType = numberType;
        } else {
            // it's different, create it:
            valueType = instance.create(type);
        }
        return valueType;
    }

}
