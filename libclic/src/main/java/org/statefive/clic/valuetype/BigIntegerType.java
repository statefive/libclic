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

import java.math.BigInteger;

/**
 * Value type representing a Java {@code java.math.BigInteger}. Properties for
 * this type are defined in {@link AbstractNumberType}.
 *
 * @author rich
 */
public class BigIntegerType extends AbstractNumberType<BigInteger, String>
        implements NaturalNumberType {

    /**
     * 
     */
    public BigIntegerType() {
        super(BigInteger.class.getPackageName(), 
                BigInteger.class.getSimpleName(), null);
    }
    
    /**
     * Type name.
     */
    public static final String BIG_INTEGER = "biginteger";

    /**
     * Value of this type.
     */
    private BigInteger value;

    /**
     * {@inheritDoc}
     */
    @Override
    BigInteger create(String number) {
        return value = new BigInteger(number);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    boolean isGreaterThan(BigInteger minimum, BigInteger maximum) {
        boolean isGreaterThan = false;
        if (minimum != null && maximum != null) {
            isGreaterThan = minimum.compareTo(maximum) > 0;
        }
        return isGreaterThan;
    }

    /**
     * Construct a big integer based on the specified string.
     *
     * @param data non-{@code null} data to create the number for.
     *
     * @return the newly created number.
     *
     * @throws ValueTypeCreationException if the number does not conform to the
     * specified properties (min/max).
     */
    @Override
    public BigInteger getValue(String data) throws ValueTypeCreationException {
        value = new BigInteger(data);
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
        return BIG_INTEGER;
    }

    /**
     * Get the big integer as a string.
     *
     * @return the big integer as a string.
     */
    @Override
    public String toString() {
        return value.toString();
    }
}
