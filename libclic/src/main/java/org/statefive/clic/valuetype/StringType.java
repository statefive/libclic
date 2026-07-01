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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Value type representing a string.
 *
 * <p>
 * <strong>Properties</strong>
 * <ul>
 * <li><strong>match</strong>=<i>[regularExpression]</i> (optional): specify a
 * valid regular expression that the string must match; failure to match will
 * report the error to the user.</li>
 * </ul>
 */
public class StringType extends AbstractValueType<String, String> {

    /**
     * 
     */
    public StringType() {
        setPackageName(String.class.getPackageName());
        setJavaClassName(String.class.getSimpleName());
        setJavaPrimitiveName(null);
    }
    
    /**
     * Type name.
     */
    public static final String STRING = "string";
    
    /**
     * 
     */
    private String data;

    /**
     * Regular expression to test against data to determine if the input is
     * valid (optional, may be {@code null}).
     */
    private String match;

    /**
     * Construct a string type.
     *
     * @param data the data for the string.
     *
     * @return the data.
     *
     * @throws ValueTypeCreationException if the data does not match the specified
     * regular expression match (if set).
     */
    @Override
    public String getValue(String data) throws ValueTypeCreationException {
        this.data = data;
        if (match != null) {
            Pattern p = Pattern.compile(match);
            Matcher m = p.matcher(this.data);
            if (!m.matches()) {
                throw new ValueTypeCreationException("Data '" + this.data
                        + "' is an invalid format.");
            }
        }
        return this.data;
    }

    /**
     * Create a string according to {@link #getValue(java.lang.String)}.
     *
     * @param data non-{@code null} data to construct the string from.
     *
     * @throws ValueTypeCreationException as per {@link #getValue(java.lang.String)}.
     */
    @Override
    public void setDefault(String data) {
        getValue(data);
    }

    /**
     * Set the properties (regular expression match) for the string type.
     *
     * @param properties valid string type property; may be {@code null}.
     *
     * @throws ValueTypeCreationException if any of the properties are invalid.
     */
    @Override
    public void setProperties(String properties) throws ValueTypeCreationException {
        if (!properties.contains("=")) {
            throw new ValueTypeCreationException("Invalid properties: " + properties);
        }
        String[] propData = properties.split("=");
        if (!propData[0].trim().equals("match")) {
            throw new ValueTypeCreationException("Invalid property: "
                    + propData[0].trim() + "; expected property 'match'");
        }
        match = propData[1].trim();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValueTypeName() {
        return STRING;
    }

    /**
     * Get the string type as a string.
     *
     * @return the string.
     */
    @Override
    public String toString() {
        return data;
    }

}
