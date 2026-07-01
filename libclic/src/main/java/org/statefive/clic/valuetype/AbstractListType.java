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
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

/**
 * Value type representing a list of values.
 *
 * <p>
 * List value types can contain any value type except the list type itself. By
 * default all elements of a list will be {@link StringType} unless overridden
 * by properties (see below).
 *
 * <p>
 * <strong>Properties</strong>
 * <ul>
 * <li><strong>listValueType</strong>=<i>&lt;valueType&gt;</i> (optional):
 * specify the value type that will contained within the list; assumes
 * {@link StringType} if not set;
 * </li>
 * <li><strong>listValueTypeProperties</strong>=<i>&lt;properties...&gt;</i>
 * (optional): specify the value type properties for the list value type;
 * </li>
 * <li><strong>listValueTypeDefaultValue</strong>=<i>&lt;value&gt;</li>
 * (optional): default value to set list elements to if not set.
 * </ul>
 *
 * @author rich
 * 
 * @param <R> Renderable type.
 */
public abstract class AbstractListType<T, R> extends AbstractValueType<T, R> {

    /**
     * List type name.
     */
    public static final String LIST = "list";

    /**
     * Property for the value type of the elements of the list.
     */
    public static final String LIST_VALUE_TYPE = "listValueType";

    /**
     * Property for the separator character of the elements of the list..
     */
    public static final String LIST_SEPARATOR_CHAR = "separatorChar";

    /**
     * Properties for the value type of the list elements.
     */
    public static final String LIST_VALUE_TYPE_PROPERTIES = "listValueTypeProperties";

    /**
     * Default list element value if an element is empty.
     */
    public static final String LIST_VALUE_TYPE_DEFAULT_VALUE = "listValueTypeDefaultValue";

    /**
     * Default list delimiter.
     */
    public static final char DEFAULT_SEPARATOR = ',';

    /**
     * Separator character.
     */
    protected char separatorChar = DEFAULT_SEPARATOR;

    /**
     * Value type of the elements of the list.
     */
    protected String listValueType = StringType.STRING;

    /**
     * Property of the list element value types.
     */
    protected String valueTypeProperties;

    /**
     * Default list element value.
     */
    protected String valueTypeDefaultValue;
    
    /**
     * The underlying value type for all elements in the list.
     */
    protected ValueType elementValueType;

    /**
     * Value type factory.
     */
    protected final ValueTypeFactory factory = ValueTypeFactory.getInstance();

    /**
     * List that will contain the elements.
     */
    protected List list;

    /**
     * Create a new list type.
     */
    public AbstractListType() {
        setPackageName(List.class.getPackageName());
        setJavaClassName(List.class.getSimpleName());
        setJavaPrimitiveName(null);
    }

    /**
     * Set the default value for the list type.
     *
     * @param data non-{@code null} data to construct the list.
     *
     * @throws ValueTypeCreationException if the default value cannot be set.
     */
    @Override
    public void setDefault(String data) throws ValueTypeCreationException {
        getValue(data);
    }

    /**
     * Set the properties on the list type.
     *
     * @param properties non-{@code null} valid properties.
     *
     * @throws ValueTypeCreationException if the properties cannot be parsed or are
     * invalid.
     */
    @Override
    public void setProperties(String properties) throws ValueTypeCreationException {
        List<String> props = new ArrayList<>();
        List<Integer> propertyIndices = new ArrayList<>();
        checkPropertyName(properties, LIST_VALUE_TYPE,
                LIST_VALUE_TYPE, LIST_SEPARATOR_CHAR,
                LIST_VALUE_TYPE_PROPERTIES, LIST_VALUE_TYPE_DEFAULT_VALUE);
        checkPropertyName(properties, LIST_SEPARATOR_CHAR,
                LIST_VALUE_TYPE, LIST_SEPARATOR_CHAR,
                LIST_VALUE_TYPE_PROPERTIES, LIST_VALUE_TYPE_DEFAULT_VALUE);
        checkPropertyName(properties, LIST_VALUE_TYPE_PROPERTIES,
                LIST_VALUE_TYPE, LIST_SEPARATOR_CHAR,
                LIST_VALUE_TYPE_PROPERTIES, LIST_VALUE_TYPE_DEFAULT_VALUE);
        checkPropertyName(properties, LIST_VALUE_TYPE_DEFAULT_VALUE,
                LIST_VALUE_TYPE, LIST_SEPARATOR_CHAR,
                LIST_VALUE_TYPE_PROPERTIES, LIST_VALUE_TYPE_DEFAULT_VALUE);
        if (properties.contains(LIST_VALUE_TYPE)) {
            propertyIndices.add(properties.indexOf(LIST_VALUE_TYPE));
        }
        if (properties.contains(LIST_SEPARATOR_CHAR)) {
            propertyIndices.add(properties.indexOf(LIST_SEPARATOR_CHAR));
        }
        if (properties.contains(LIST_VALUE_TYPE_PROPERTIES)) {
            propertyIndices.add(properties.indexOf(LIST_VALUE_TYPE_PROPERTIES));
        }
        if (properties.contains(LIST_VALUE_TYPE_DEFAULT_VALUE)) {
            propertyIndices.add(properties.indexOf(LIST_VALUE_TYPE_DEFAULT_VALUE));
        }
        Collections.sort(propertyIndices);
        for (int i = 0; i < propertyIndices.size(); i++) {
            int lastIndex = properties.length();
            if (i != propertyIndices.size() - 1) {
                lastIndex = propertyIndices.get(i + 1);
            }
            String data = properties.substring(propertyIndices.get(i),
                    lastIndex).trim();
            if (data.endsWith(",")) {
                data = data.substring(0, data.length() - 1);
            }
            props.add(data);
        }
        for (String property : props) {
            if (!property.contains("=")) {
                throw new ValueTypeCreationException("Invalid properties: " + properties);
            }
            String propertyName = property.substring(0, property.indexOf("=")).trim();
            String propertyValue = property.substring(property.indexOf("=") + 1, property.length()).trim();
            switch (propertyName.trim()) {
                case LIST_VALUE_TYPE:
                    listValueType = propertyValue.trim();
                    if (LIST.equals(listValueType)) {
                        throw new ValueTypeCreationException("Cannot set list"
                                + " type as '" + LIST + "' for list elements");
                    }   elementValueType = ValueTypeFactory.getInstance().create(listValueType);
                    break;
                case LIST_VALUE_TYPE_PROPERTIES:
                    valueTypeProperties = propertyValue;
                    break;
                case LIST_VALUE_TYPE_DEFAULT_VALUE:
                    valueTypeDefaultValue = propertyValue;
                    break;
                default:
                    if (propertyValue.trim().length() != 1) {
                        throw new ValueTypeCreationException("Separator"
                                + " char property must be a single character,"
                                + "found: '" + propertyValue.trim() + "'");
                    }   this.separatorChar = propertyValue.trim().charAt(0);
                    break;
            }
        }
    }

    /**
     * Get the value type name.
     *
     * @return {@link LIST}.
     */
    @Override
    public String getValueTypeName() {
        return LIST;
    }

    /**
     * Get the underlying value type for all list elements.
     * 
     * @return non-{@code null} list type; defaults to {@link StringType} of not
     * overridden by a different value type.
     */
    public ValueType getElementValueType() {
        return elementValueType;
    }

    /**
     * Check that the given property name (if present) is spelt correctly within
     * the properties against all the specified property names to check.
     *
     * @param properties non-{@code null} properties to check.
     *
     * @param propertyName non-{@code null} name that must be present and spelt
     * correctly.
     *
     * @param propertyNames non-empty matches to check.
     *
     * @throws ValueTypeCreationException if any of the properties are spelt
     * incorrectly.
     */
    private void checkPropertyName(String properties, String propertyName, String... propertyNames) {
        String regex = ".*([\\S]*" + propertyName + "[\\S]*).*";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(properties);
        if (m.matches()) {
            String groupString = m.group(1);
            if (groupString.contains("=")) {
                groupString = groupString.split("=")[0];
            }
            boolean found = false;
            String notFoundName = null;
            for (String pName : propertyNames) {
                if (groupString.equals(pName)) {
                    found = true;
                    break;
                }
                notFoundName = groupString;
            }
            if (!found) {
                throw new ValueTypeCreationException(
                        "Invalid property: " + notFoundName
                        + "; expected property '" + propertyName + "'");
            }
        }
    }

    /**
     * Get the elements of the list separated by the separator character
     * followed by a space.
     *
     * @return non-{@code null} string representation of the list.
     */
    @Override
    public String toString() {
        return StringUtils.join(list, separatorChar + " ");
    }
    
}
