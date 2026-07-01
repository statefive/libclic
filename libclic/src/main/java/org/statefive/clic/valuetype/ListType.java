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
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * Value type representing a list of of values.
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
 */
public class ListType extends AbstractListType<List, List> {

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
     * Render the given list.
     * 
     * @param renderable non-{@code null} list.
     * 
     * @return non-{@code null} list of elements separated by the separator
     * character and a space.
     */
    @Override
    public String render(List renderable) {
        return StringUtils.join(renderable, separatorChar + " ");
    }
}
