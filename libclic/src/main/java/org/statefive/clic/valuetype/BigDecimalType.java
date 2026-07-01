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

import java.math.BigDecimal;

/**
 * Value type representing a Java {@code java.math.BigDecimal}. Properties for
 * this type are defined in {@link AbstractNumberType}.
 *
 * @author rich
 */
public class BigDecimalType extends AbstractNumberType<BigDecimal, String> 
        implements RealNumberType {

    /**
     * 
     */
    public BigDecimalType() {
        super(BigDecimal.class.getPackageName(), 
                BigDecimal.class.getSimpleName(), null);
    }
    
    /**
     * Type name.
     */
    public static final String BIG_DECIMAL = "bigdecimal";

    /**
     * Value of this type.
     */
    private BigDecimal value;

    /**
     * {@inheritDoc}
     */
    @Override
    BigDecimal create(String number) {
        return value = new BigDecimal(number);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    boolean isGreaterThan(BigDecimal minimum, BigDecimal maximum) {
        boolean isGreaterThan = false;
        if (minimum != null && maximum != null) {
            isGreaterThan = minimum.compareTo(maximum) > 0;
        }
        return isGreaterThan;
    }

    /**
     * Construct a big decimal based on the specified string.
     *
     * @param data non-{@code null} data to create the number for.
     *
     * @return the newly created number.
     *
     * @throws ValueTypeCreationException if the number does not conform to the
     * specified properties (min/max).
     */
    @Override
    public BigDecimal getValue(String data) throws ValueTypeCreationException {
        value = new BigDecimal(data);
        if (minimum != null && value.compareTo(minimum) < 0) {
            throw new ValueTypeCreationException(value
                    + " is less than specified minimum: " + minimum);
        }
        if (maximum != null && value.compareTo(maximum) > 0) {
            throw new ValueTypeCreationException(value
                    + " is greater than specified maximum: " + maximum);
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
        return BIG_DECIMAL;
    }

    /**
     * Get the big decimal as a string.
     *
     * @return the big decimal as a string.
     */
    @Override
    public String toString() {
        return value.toString();
    }
}
