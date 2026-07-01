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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Value type representing a date/time.
 *
 * <p>
 * <strong>Properties</strong>
 * <ul>
 * <li><strong>dateFormat</strong>=<i>[format]</i> (required): specify date
 * format according to Java date formatting rules, for example
 * {@code yyyy/MM/dd HH:mm:ss}.</li>
 * </ul>
 */
public class DateType extends AbstractValueType<Date, String> {

    /**
     * Type name.
     */
    public static final String DATE = "date";

    /**
     * The date for the type.
     */
    private Date date;
    
    /**
     * Date format; must comply to Java date formatting rules.
     */
    private String dateFormat;
    
    /**
     * 
     */
    public DateType() {
        setPackageName(Date.class.getPackageName());
        setJavaClassName(Date.class.getSimpleName());
        setJavaPrimitiveName(null);
    }
    
    /**
     * Attempt to construct a date from the specified string.
     *
     * @param data non-{@code null} data that can be built from the specified
     * date format property.
     *
     * @return the date, if it could be constructed.
     *
     * @throws ValueTypeCreationException if the date cannot be parsed against the
     * specified format.
     */
    @Override
    public Date getValue(String data) throws ValueTypeCreationException {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        try {
            date = sdf.parse(data);
        } catch (ParseException ex) {
            throw new ValueTypeCreationException(ex.getMessage(), ex);
        }
        return date;
    }

    /**
     * Create a date according to {@link #getValue(java.lang.String)}.
     *
     * @param data non-{@code null} data to construct the date from.
     *
     * @throws ValueTypeCreationException as per {@link #getValue(java.lang.String)}.
     */
    @Override
    public void setDefault(String data) throws ValueTypeCreationException {
        getValue(data);
    }

    /**
     * Set the properties for this date type.
     * 
     * @param properties non-{@code null} properties for constructing the date.
     * 
     * @throws ValueTypeCreationException if the properties are invalid.
     */
    @Override
    public void setProperties(String properties) throws ValueTypeCreationException {
        if (!properties.contains("=")) {
            throw new ValueTypeCreationException("Invalid properties: " + properties);
        }
        String[] data = properties.split("=");
        if (!data[0].trim().equals("dateFormat")) {
            throw new ValueTypeCreationException("Invalid property: "
                    + data[0].trim() + "; expected property 'dateFormat'");
        }
        dateFormat = data[1].trim();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValueTypeName() {
        return DATE;
    }

    /**
     * Return the date as a string.
     *
     * @return the date as a string.
     */
    @Override
    public String toString() {
        return new SimpleDateFormat(dateFormat).format(date);
    }

}
