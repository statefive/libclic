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
package com.typesafe.clic.properties;

import com.typesafe.config.ConfigList;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.statefive.clic.valuetype.AbstractListType;
import org.statefive.clic.valuetype.StringType;
import org.statefive.clic.valuetype.ValueType;
import org.statefive.clic.valuetype.ValueTypeCreationException;

/**
 * {@code com.typesafe} list representation. Only used for properties-based
 * command line applications (does not work for example when generating a
 * CLC-based application).
 *
 * <p>
 * The list uses the same type name as the standard list implementation.
 *
 * @author irch
 */
public class ConfigListType extends AbstractListType<List, Object> {

    /**
     * Get the value of the list from the given data. Elements will be split on
     * the defined separator character; escaped separator characters will not be
     * split.
     *
     * @param data non-{@code null} data to construct the list from.
     *
     * @return non-{@code null} list; the elements of the list will be the value
     * type specified by the properties (if it was set), or {@link StringType}
     * otherwise.
     *
     * @throws ValueTypeCreationException if the list cannot be constructed.
     */
    @Override
    public List getValue(String data) throws ValueTypeCreationException {
        list = new ArrayList<>();
        // ignore escaped characters:
        String[] listData = data.split("(?<=[^\\\\])" + Character.toString(separatorChar));
        ValueType vt = factory.create(listValueType);
        for (int i = 0; i < listData.length; i++) {
            if (valueTypeProperties != null) {
                vt.setProperties(valueTypeProperties);
            }
            String dataValue = listData[i].trim();
            if (dataValue.isEmpty() && valueTypeDefaultValue != null) {
                list.add(vt.getValue(valueTypeDefaultValue));
            } else {
                list.add(vt.getValue(dataValue));
            }
        }
        return list;
    }

    /**
     * Set the properties; overriding the default list separator character is
     * not permitted.
     *
     * @param properties non-{@code null} properties to parse.
     *
     * @throws ValueTypeCreationException if any of the properties cannot be
     * parsed or are invalid.
     */
    @Override
    public void setProperties(String properties) throws ValueTypeCreationException {
        super.setProperties(properties);
        if (separatorChar != AbstractListType.DEFAULT_SEPARATOR) {
            throw new ValueTypeCreationException("Cannot override default"
                    + " list value " + AbstractListType.DEFAULT_SEPARATOR);
        }
    }

    /**
     * Render the given list.
     *
     * @param source non-{@code null} data source. The source type can be one of
     * three values:
     *
     * <ol>
     * <li>{@code com.typesafe.config.ConfigList} when the object is rendered
     * directly from being passed the list from the parsed Typesafe
     * configuration;</li>
     * <li>{@code java.util.List} when the option {@code type} is set as a
     * {@code list}; and</li>
     * <li>{@code java.lang.String} when the option {@code type} is set as a
     * {@code string} type (or no type has been set).</li>
     * </ol>
     *
     * @return non-{@code null} string representation of the list.
     */
    @Override
    public String render(Object source) {
        String data = source.toString();
        if (source instanceof ConfigList) {
            data = StringUtils.join(((ConfigList) source).unwrapped(), ", ");
        } else if (source instanceof List) {
            data = StringUtils.join(((List) source), ", ");
        }
        return data;
    }

}
