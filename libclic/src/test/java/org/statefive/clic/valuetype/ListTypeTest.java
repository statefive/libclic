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

import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.Test;
import org.statefive.clic.Clc;

/**
 *
 * @author rich
 */
public class ListTypeTest {

    /**
     * 
     */
    @BeforeClass
    public static void setUpClass() {
        Clc.initialiseValueTypeFactory();
    }

    /**
     *
     */
    @Test
    public void testGetValue() {
        ListType listType = new ListType();
        String listData = "1, 2, 3, 4, 5";
        List list = listType.getValue(listData);
        assertEquals(5, list.size());
        for (int i = 0; i < 5; i++) {
            assertEquals(Integer.toString(i + 1), list.get(i));
        }
    }

    /**
     *
     */
    @Test
    public void testGetValueForEmptyStrings() {
        ListType listType = new ListType();
        String listData = "one, , two, , three";
        List list = listType.getValue(listData);
        assertEquals(5, list.size());
        assertEquals("one", list.get(0));
        assertEquals("", list.get(1));
        assertEquals("two", list.get(2));
        assertEquals("", list.get(3));
        assertEquals("three", list.get(4));
    }

    /**
     *
     */
    @Test
    public void testGetValueForEscapedString() {
        ListType listType = new ListType();
        String listData = "one\\, two, buckle my shoe, three\\, four, knock at the door";
        List list = listType.getValue(listData);
        assertEquals(4, list.size());
        assertEquals("one\\, two", list.get(0));
        assertEquals("buckle my shoe", list.get(1));
        assertEquals("three\\, four", list.get(2));
        assertEquals("knock at the door", list.get(3));
    }

    /**
     *
     */
    @Test
    public void testGetValueForEscapedStringWithDifferentSeparatorChar() {
        ListType listType = new ListType();
        listType.setProperties(ListType.LIST_SEPARATOR_CHAR + "= : ");
        String listData = "One is less than two : And\\: three is greater than two";
        List list = listType.getValue(listData);
        assertEquals(2, list.size());
        assertEquals("One is less than two", list.get(0));
        assertEquals("And\\: three is greater than two", list.get(1));
    }

    /**
     *
     */
    @Test
    public void testGetValueForSingleElementList() {
        ListType listType = new ListType();
        String listData = "1";
        List list = listType.getValue(listData);
        assertEquals(1, list.size());
        assertEquals(Integer.toString(1), list.get(0));
    }

    /**
     *
     */
    @Test
    public void testGetValueForUnknownListValueType() {
        ListType listType = new ListType();
        String methodName = "testGetValueForUnknownListValueType";
        try {
            listType.setProperties(ListType.LIST_VALUE_TYPE + "=" + methodName);
            fail("Expected an exception");
        } catch (ValueTypeCreationException ex) {
            assertEquals("Unknown value type: " + methodName, ex.getMessage());
        }
    }

    /**
     *
     */
    @Test
    public void testSetDefault() {
        ListType listType = new ListType();
        String listData = "1, 2, 3, 4, 5";
        listType.setDefault(listData);
        assertEquals(listData, listType.toString());
    }

    /**
     *
     */
    @Test
    public void testGetPackageName() {
        ListType listType = new ListType();
        assertEquals("java.util", listType.getPackageName());
    }

    /**
     *
     */
    @Test
    public void testGetJavaTypeName() {
        ListType listType = new ListType();
        assertEquals("List", listType.getJavaClassName());
    }

    /**
     *
     */
    @Test
    public void testGetValueTypeName() {
        ListType listType = new ListType();
        assertEquals(ListType.LIST, listType.getValueTypeName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetJavaPrimitiveName() {
        ListType listType = new ListType();
        assertNull(listType.getJavaPrimitiveName());
    }

    /**
     *
     */
    @Test
    public void testSetPropertiesNoEquals() {
        ListType listType = new ListType();
        try {
            listType.setProperties(ListType.LIST_VALUE_TYPE);
            fail("Expected an excception");
        } catch (ValueTypeCreationException ex) {
            assertEquals("Invalid properties: " + ListType.LIST_VALUE_TYPE,
                    ex.getMessage());
        }
    }

    /**
     *
     */
    @Test
    public void testSetPropertiesUnknownProperty() {
        ListType listType = new ListType();
        try {
            listType.setProperties(ListType.LIST_VALUE_TYPE + "_x=x");
            fail("Expected an excception");
        } catch (ValueTypeCreationException ex) {
            assertEquals("Invalid property: " + ListType.LIST_VALUE_TYPE
                    + "_x; expected property '" + ListType.LIST_VALUE_TYPE + "'",
                    ex.getMessage());
        }
    }

    /**
     *
     */
    @Test
    public void testSetPropertiesAsListValueType() {
        ListType listType = new ListType();
        try {
            listType.setProperties(ListType.LIST_VALUE_TYPE + "=" + ListType.LIST);
            fail("Expected an excception");
        } catch (ValueTypeCreationException ex) {
            assertEquals("Cannot set list type as '" + ListType.LIST
                    + "' for list elements", ex.getMessage());
        }
    }

    /**
     *
     */
    @Test
    public void testSetPropertiesForIntType() {
        ListType listType = new ListType();
        listType.setProperties(ListType.LIST_VALUE_TYPE + "=int, "
                + ListType.LIST_SEPARATOR_CHAR + " = ;");
        String listData = "1; 2; 3; 4; 5";
        List list = listType.getValue(listData);
        assertEquals(5, list.size());
        for (int i = 0; i < 5; i++) {
            assertEquals(i + 1, list.get(i));
        }
    }

    /**
     *
     */
    @Test
    public void testSetPropertiesForIntTypeMissingElementFails() {
        ListType listType = new ListType();
        listType.setProperties(ListType.LIST_VALUE_TYPE + "=int");
        String listData = "1, 2, , 4, 5";
        try {
            listType.getValue(listData);
            fail("Expected an exception");
        } catch (ValueTypeCreationException ex) {
            assertEquals("Invalid integer value: ''.", ex.getMessage());
        }
    }

    /**
     *
     */
    @Test
    public void testSetPropertiesForIntTypeWithDefaultValue() {
        ListType listType = new ListType();
        listType.setProperties(ListType.LIST_VALUE_TYPE + "=int, "
                + ListType.LIST_VALUE_TYPE_DEFAULT_VALUE + " = 123");
        String listData = "1, 2, , 4, 5";
        List list = listType.getValue(listData);
        assertEquals(5, list.size());
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(123, list.get(2));
        assertEquals(4, list.get(3));
        assertEquals(5, list.get(4));
    }

    /**
     *
     */
    @Test
    public void testSetPropertiesForIntTypeWithProperties() {
        ListType listType = new ListType();
        listType.setProperties(ListType.LIST_VALUE_TYPE + "=int, "
                + ListType.LIST_VALUE_TYPE_PROPERTIES + " = min=1, max=5");
        String listData = "1, 2, 3, 4, 5";
        List list = listType.getValue(listData);
        assertEquals(5, list.size());
        for (int i = 0; i < 5; i++) {
            assertEquals(i + 1, list.get(i));
        }
    }

    /**
     *
     */
    @Test
    public void testSetPropertiesForIntTypeWithBadProperties() {
        ListType listType = new ListType();
        listType.setProperties(ListType.LIST_VALUE_TYPE + "=int, "
                + ListType.LIST_VALUE_TYPE_PROPERTIES + " = min=1, max=3");
        String listData = "1, 2, 3, 4, 5";
        try {
            listType.getValue(listData);
            fail("Expected an exception");
        } catch (ValueTypeCreationException ex) {
            assertEquals("4 is greater than specified maximum: 3", ex.getMessage());
        }
    }

    /**
     *
     */
    @Test
    public void testGetValueForDifferentSeparatorChar() {
        ListType listType = new ListType();
        String listData = "1; 2; 3; 4; 5";
        listType.setProperties(ListType.LIST_SEPARATOR_CHAR + " = ;");
        List list = listType.getValue(listData);
        assertEquals(5, list.size());
        for (int i = 0; i < 5; i++) {
            assertEquals(Integer.toString(i + 1), list.get(i));
        }
    }

    /**
     *
     */
    @Test
    public void testGetValueForBadSeparatorChar() {
        ListType listType = new ListType();
        try {
            listType.setProperties(ListType.LIST_SEPARATOR_CHAR + " = ;+!");
            fail("Expected an exception");
        } catch (ValueTypeCreationException ex) {
            assertEquals("Separator char property must be a single character,"
                    + "found: ';+!'", ex.getMessage());
        }
    }

}
