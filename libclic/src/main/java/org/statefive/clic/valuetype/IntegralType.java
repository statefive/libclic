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
package org.statefive.clic.valuetype;

/**
 * Value type representing an integer. Properties for this type are defined in
 * {@link AbstractNumberType}.
 */
public class IntegralType extends AbstractNumberType<Integer, String>
        implements NaturalNumberType {

    /**
     * 
     */
    public IntegralType() {
        super(Integer.class.getPackageName(), 
                Integer.class.getSimpleName(), INTEGRAL);
    }
    
    /**
     * Type name.
     */
    public static final String INTEGRAL = "int";

    /**
     * Value of this type.
     */
    private int value;

    /**
     * {@inheritDoc}
     */
    @Override
    Integer create(String number) {
        return Integer.valueOf(number);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    boolean isGreaterThan(Integer x, Integer y) {
        boolean isGreaterThan = false;
        if (x != null && y != null) {
            isGreaterThan = x > y;
        }
        return isGreaterThan;
    }

    /**
     * Construct an integer based on the specified string.
     *
     * @param data non-{@code null} data to create the number for.
     *
     * @return the newly created number.
     *
     * @throws ValueTypeCreationException if the number does not conform to the
     * specified properties (min/max).
     */
    @Override
    public Integer getValue(String data) throws ValueTypeCreationException {
        try {
            value = Integer.parseInt(data);
        } catch (NumberFormatException ex) {
            throw new ValueTypeCreationException("Invalid integer value: '" + data + "'.");
        }
        if (minimum != null && value < minimum) {
            throw new ValueTypeCreationException(value
                    + " is less than specified minimum: " + getMinium());
        }
        if (maximum != null && value > maximum) {
            throw new ValueTypeCreationException(value
                    + " is greater than specified maximum: " + getMaximum());
        }
        return value;
    }

    /**
     * Create a number according to {@link #getValue(java.lang.String)}.
     *
     * @param data non-{@code null} data to construct the number from.
     *
     * @throws ValueTypeCreationException as per {@link #getValue(java.lang.String)}.
     */
    @Override
    public void setDefault(String data) throws ValueTypeCreationException {
        getValue(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValueTypeName() {
        return INTEGRAL;
    }

    /**
     * Get the integer as a string.
     *
     * @return the integer as a string.
     */
    @Override
    public String toString() {
        return Integer.toString(value);
    }

}
