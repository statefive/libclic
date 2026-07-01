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

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author rich
 */
public class ShortTypeTest {

    /**
     * Test of getValue method, of class ShortType.
     */
    @Test
    public void testGetValue() {
        String shortValue = "3";
        short expected = Short.parseShort(shortValue);
        ShortType shortType = new ShortType();
        short result = shortType.getValue(shortValue);
        assertEquals(expected, result);
    }

    /**
     * Test of getValue method, of class ShortType.
     */
    @Test
    public void testSetDefault() {
        String shortValue = "3";
        ShortType shortType = new ShortType();
        shortType.setDefault(shortValue);
        assertEquals(shortValue, shortType.toString());
    }

    /**
     * Test of setMinimum method, of class ShortType.
     */
    @Test
    public void testSetMinimum() {
        String shortValue = "11";
        short minimum = 10;
        short expected = Short.parseShort(shortValue);
        ShortType shortType = new ShortType();
        shortType.setProperties("min=" + minimum);
        short result = shortType.getValue(shortValue);
        assertEquals(expected, result);
    }

    /**
     * Test of setMinimum method, of class ShortType.
     */
    @Test
    public void testSetMinimumThrowsException() {
        String shortValue = "9";
        short minimum = 10;
        short expected = Short.parseShort(shortValue);
        ShortType shortType = new ShortType();
        shortType.setProperties("min=" + minimum);
        try {
            shortType.getValue(shortValue);
            fail("Expected an exception.");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), expected
                    + " is less than specified minimum: " + minimum);
        }
    }

    /**
     * Test of setMaximum method, of class ShortType.
     */
    @Test
    public void testSetMaximum() {
        String shortValue = "9";
        short maximum = 10;
        short expected = Short.parseShort(shortValue);
        ShortType shortType = new ShortType();
        shortType.setProperties("max=" + maximum);
        short result = shortType.getValue(shortValue);
        assertEquals(expected, result);
    }

    /**
     * Test of setMaximum method, of class ShortType.
     */
    @Test
    public void testSetMinMax() {
        String value = "7";
        short minimum = 0;
        short maximum = 10;
        short expected = Short.parseShort(value);
        ShortType shortType = new ShortType();
        shortType.setProperties("min=" + minimum + ", max=" + maximum);
        short result = shortType.getValue(value);
        assertEquals(expected, result);
    }

    /**
     * Test of setMinimum method, of class ShortType.
     */
    @Test
    public void testSetMaximumThrowsException() {
        String shortValue = "11";
        short maximum = 10;
        short expected = Short.parseShort(shortValue);
        ShortType shortType = new ShortType();
        shortType.setProperties("max=" + maximum);
        try {
            shortType.getValue(shortValue);
            fail("Expected an exception.");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), expected
                    + " is greater than specified maximum: " + maximum);
        }
    }

    /**
     * Test of setMaximum method, of class ShortType.
     */
    @Test
    public void testSetMinimumMaximum() {
        String shortValue = "9";
        short minimum = 0;
        short maximum = 10;
        short expected = Short.parseShort(shortValue);
        ShortType shortType = new ShortType();
        shortType.setProperties("min=" + minimum + ",max=" + maximum);
        short result = shortType.getValue(shortValue);
        assertEquals((short) expected, (short) result, 0.0f);
    }

    /**
     * Test of setMinimum method, of class ShortType.
     */
    @Test
    public void testSetPropertiesThrowsException() {
        ShortType shortType = new ShortType();
        try {
            shortType.setProperties("max");
            fail("Expected an exception.");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), "Invalid properties: max");
        }
    }

    /**
     * Test of setMaximum method, of class ShortType.
     */
    @Test
    public void testSetInvalidPropertyThrowsException() {
        ShortType shortType = new ShortType();
        try {
            shortType.setProperties("foo=bar");
        } catch (Exception ex) {
            assertEquals(ex.getMessage(), "Invalid property: foo;"
                    + " expected property 'min' or 'max'");
        }
    }

    /**
     * Test of setMinimum method, of class ShortType.
     */
    @Test
    public void testSetPropertiesBadNumberThrowsException() {
        ShortType shortType = new ShortType();
        try {
            shortType.setProperties("max=foo");
            fail("Expected an exception.");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), "Invalid value for max: foo");
        }
    }

    /**
     * Test of setMaximum method, of class ShortType.
     */
    @Test
    public void testSetMinimumMaximumMinGreaterThanMax() {
        short minimum = 11;
        short maximum = 10;
        ShortType shortType = new ShortType();
        try {
            shortType.setProperties("min=" + minimum + ",max=" + maximum);
            fail("Expected an exception.");
        } catch(Exception ex) {
            assertEquals(ex.getMessage(), "maximum (10) is less than minimum (11)");
        }
    }
    
    /**
     * 
     */
    @Test
    public void testGetPackageName() {
        ShortType shortType = new ShortType();
        assertEquals("java.lang", shortType.getPackageName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetJavaClassName() {
        ShortType shortType = new ShortType();
        assertEquals("Short", shortType.getJavaClassName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetJavaPrimitiveName() {
        ShortType shortType = new ShortType();
        assertEquals("short", shortType.getJavaPrimitiveName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetValueTypeName() {
        ShortType shortType = new ShortType();
        assertEquals("short", shortType.getValueTypeName());
    }

    /**
     * Test of toString method, of class ShortType.
     */
    @Test
    public void testToString() {
        String expected = "3";
        ShortType shortType = new ShortType();
        shortType.getValue(expected);
        assertEquals(expected, shortType.toString());
    }

}
