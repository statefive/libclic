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
 * Value type representing a byte. Properties for this type are defined in
 * {@link AbstractNumberType}.
 */
public class ByteType extends AbstractNumberType<Byte, String> implements NaturalNumberType {

    /**
     * 
     */
    public ByteType() {
        super(Byte.class.getPackageName(), Byte.class.getSimpleName(), 
                BYTE);
    }
    
    /**
     * Type name.
     */
    public static final String BYTE = "byte";

    /**
     * Value of this type.
     */
    private byte value;

    /**
     * {@inheritDoc}
     */
    @Override
    Byte create(String number) {
        return Byte.valueOf(number);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    boolean isGreaterThan(Byte x, Byte y) {
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
    public Byte getValue(String data) throws ValueTypeCreationException {
        value = Byte.parseByte(data);
        if (minimum != null && value < minimum) {
            throw new ValueTypeCreationException(value
                    + " is less than specified minimum: " + minimum);
        }
        if (maximum != null && value > maximum) {
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
        return BYTE;
    }

    /**
     * Get the integer as a string.
     *
     * @return the integer as a string.
     */
    @Override
    public String toString() {
        return Byte.toString(value);
    }

}
