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

import java.util.HashMap;
import java.util.Map;
import org.statefive.clic.ClcParser;

/**
 * Value type representing a boolean value.
 *
 * <p>
 * Without any properties supplied (see below) treats the string (ignoring case)
 * {@code true} as the Java Boolean value {@code true} and the string (again
 * ignoring case) {@code false} as the Java Boolean value {@code false}. The
 * {@code truthMappings} property enables callers to define different values for
 * booleans such as {@code on}, {@code off}, {@code yes}, {@code no} etc..
 *
 * <p>
 * <strong>Properties</strong>
 * <ul>
 * <li><strong>truthMappings</strong>=<i>&lt;mappings&gt;</i> (optional):
 * specify any number of semi-colon separated key-value pairs in the form
 * {@code x=y} values of truth properties. The key is a name that maps to a
 * boolean value (ignoring case) {@code true} or {@code false}. Any number of
 * mappings may be supplied but must contain at least one {@code true} value and
 * at least one {@code false} value. Duplicate keys are not permitted. All keys
 * and values are converted to lower case.
 * </li>
 * </ul>
 *
 * @author rich
 */
public class BooleanType extends AbstractValueType<Boolean, String> {

    /**
     * Type name.
     */
    public static final String BOOLEAN = "boolean";

    /**
     * Truth mappings property key name.
     */
    public static final String TRUTH_MAPPINGS = "truthMappings";

    /**
     * Map of keys that in turn map to boolean values. The key will be some
     * arbitrary text value that maps to a given truth value.
     */
    private final Map<String, Boolean> truthMappings = new HashMap<>();

    /**
     * Boolean value.
     */
    private Boolean bool;
    
    /**
     * 
     */
    public BooleanType() {
        setPackageName(Boolean.class.getPackageName());
        setJavaClassName(Boolean.class.getSimpleName());
        setJavaPrimitiveName(BOOLEAN);
    }

    /**
     * Get the given value for the data; if no truth mappings have been created
     * via property construction, omitting quotes, 'true' and 'false' will be
     * mapped to the Java primitive values {@code true} and {@code false},
     * respectively, otherwise the pre-set values will be used.
     *
     * @param data non-{@code null} data to convert to a {@code true} or
     * {@code false} value; note the case of the value does not matter when
     * mapping values.
     *
     * @return non-{@code null} boolean value if it could be converted.
     *
     * @throws ValueTypeCreationException if no mapping could be found for the given
     * value.
     */
    @Override
    public Boolean getValue(String data) throws ValueTypeCreationException {
        if (truthMappings.isEmpty()) {
            truthMappings.put(Boolean.TRUE.toString().toLowerCase(), Boolean.TRUE);
            truthMappings.put(Boolean.FALSE.toString().toLowerCase(), Boolean.FALSE);
        }
        bool = truthMappings.get(data.toLowerCase());
        if (bool == null) {
            throw new ValueTypeCreationException("Could not determine truth"
                    + " value for value '" + data + "'");
        }
        return bool;
    }

    /**
     * Set the default value; internally calls
     * {@link #getValue(java.lang.String)}.
     *
     * @param data non-{@code null} data to set the default value as.
     *
     * @throws ValueTypeCreationException see {@link #getValue(java.lang.String)}.
     */
    @Override
    public void setDefault(String data) throws ValueTypeCreationException {
        getValue(data);
    }

    /**
     * Set properties for constructing the boolean type - see the class header
     * for information on the properties.
     *
     * @param properties non-{@code null} properties to check.
     *
     * @throws ValueTypeCreationException if the properties are invalid (do not
     * contain an {@code =}, if the property is not composed of the correct key
     * {@link #TRUTH_MAPPINGS}, if any of the properties of the truth mappings
     * are not separated by the equals ({@code =}) symbol, of any of the
     * property keys are duplicated or any of the property values are not
     * {@code true} or {@code false}.
     */
    @Override
    public void setProperties(String properties) throws ValueTypeCreationException {
        if (!properties.contains("=")) {
            throw new ValueTypeCreationException("Invalid properties: " + properties);
        }
        int firstEquals = properties.indexOf("=");
        String data = properties.substring(0, firstEquals);
        if (!data.equals(TRUTH_MAPPINGS)) {
            throw new ValueTypeCreationException("Invalid property: "
                    + data + "; expected property '" + TRUTH_MAPPINGS + "'");
        }
        String props = properties.substring(firstEquals + 1, properties.length());
        boolean trueSet = false;
        boolean falseSet = false;
        if (!props.contains("=")) {
            throw new ValueTypeCreationException("Expected to find true/false"
                    + " mappings separated by '=', found none: " + properties);
        }
        for (String mapping : props.split(";")) {
            if (!mapping.contains("=")) {
                throw new ValueTypeCreationException("Bad proeprty mapping: "
                        + mapping.trim() + "; no equals assignment to map to truth value");
            }
            String mappingKey = mapping.split("=")[0].trim().toLowerCase();
            String mappingValue = mapping.split("=")[1].trim().toLowerCase();
            if (!mappingValue.equals(ClcParser.TRUE)
                    && (!mappingValue.equals(ClcParser.FALSE))) {
                throw new ValueTypeCreationException("Invalid mapping value: "
                        + mappingValue + "; can only be 'true' or 'false'");
            }
            if (!truthMappings.containsKey(mappingKey)) {
                boolean truthValue = Boolean.valueOf(mappingValue);
                truthMappings.put(mappingKey, truthValue);
                if (truthValue) {
                    trueSet = true;
                } else {
                    falseSet = true;
                }
            } else {
                throw new ValueTypeCreationException("Duplicated truth mapping: " + mappingKey);
            }
        }
        if (!trueSet) {
            throw new ValueTypeCreationException("No 'true' value set for properties: " + properties);
        }
        if (!falseSet) {
            throw new ValueTypeCreationException("No 'false' value set for properties: " + properties);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValueTypeName() {
        return BOOLEAN;
    }

    /**
     * Get the boolean as a string.
     *
     * @return the boolean as a string.
     */
    @Override
    public String toString() {
        return Boolean.toString(bool);
    }

}
