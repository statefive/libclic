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
package org.statefive.clic.valuetype;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Base class for number types.
 *
 * <p>
 * <strong>Properties</strong>
 * <ul>
 * <li><strong>min</strong>=<i>[n]</i> (optional): specify the minimum value
 * that the number must be equal to or greater than;</li>
 * <li><strong>max</strong>=<i>[n]</i> (optional): specify the maximum value
 * that the number must be equal to or less than;</li>
 * </ul>
 *
 * @author rich
 *
 * @param <N> Numerical implementation.
 */
public abstract class AbstractNumberType<N extends Number, R> extends AbstractValueType<Object, R> {


    /**
     * 
     * @param packageName
     * @param javaClassName
     * @param javaPrimitiveName 
     */
    public AbstractNumberType(String packageName, String javaClassName, 
            String javaPrimitiveName) {
        setPackageName(packageName);
        setJavaClassName(javaClassName);
        setJavaPrimitiveName(javaPrimitiveName);
    }
    
    /**
     * Create the given number.
     *
     * @param number non-{@code null} number to create; must be valid for the
     * given number implementation.
     *
     * @return created number.
     */
    abstract N create(String number);

    /**
     * Check that {@code x} is greater than {@code y}.
     *
     * @param x number to check; may be {@code null}.
     *
     * @param y number to check; may be {@code null}.
     *
     * @return {@code true} if both numbers are non-{@code null} and the first
     * number is greater than the second; {@code false} otherwise.
     */
    abstract boolean isGreaterThan(N x, N y);

    /**
     * Minimum value for this type.
     */
    protected N minimum;

    /**
     * Maximum value for this type.
     */
    protected N maximum;

    /**
     * Get the minimum value represented by this number.
     *
     * @return minimum number; may be {@code null}.
     */
    N getMinium() {
        return minimum;
    }

    /**
     * Get the maximum value represented by this number.
     *
     * @return maximum number; may be {@code null}.
     */
    N getMaximum() {
        return maximum;
    }

    /**
     * Create the properties ({@code min}/{@code max}) for the number type if
     * set.
     *
     * @param properties non-{@code null} valid minimum and/or maximum values.
     *
     * @throws ValueTypeCreationException if any of the properties are invalid.
     */
    @Override
    public void setProperties(String properties) throws ValueTypeCreationException {
        List<String> props = new ArrayList<>();
        if (properties.contains(",")) {
            props.addAll(Arrays.asList(properties.split(",")));
        } else {
            props.add(properties.trim());
        }
        for (String property : props) {
            if (!property.contains("=")) {
                throw new ValueTypeCreationException("Invalid properties: " 
                        + properties);
            }
            String[] data = property.split("=");
            try {
                switch (data[0].trim()) {
                    case "min":
                        minimum = create(data[1].trim());
                        break;
                    case "max":
                        maximum = create(data[1].trim());
                        break;
                    default:
                        throw new ValueTypeCreationException(
                                "Invalid property: " + data[0].trim()
                                + "; expected property 'min' or 'max'");
                }
            } catch (NumberFormatException nfex) {
                throw new ValueTypeCreationException("Invalid value for "
                        + data[0].trim() + ": " + data[1].trim(), nfex);
            }
            if (isGreaterThan(minimum, maximum)) {
                throw new ValueTypeCreationException("maximum (" + maximum
                        + ") is less than minimum (" + minimum + ")");
            }
        }
    }

}
