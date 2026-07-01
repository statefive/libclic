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
public class LongTypeTest {

    /**
     * Test of getValue method, of class LongType.
     */
    @Test
    public void testGetValue() {
        String longValue = "3";
        long expected = Long.parseLong(longValue);
        LongType longType = new LongType();
        long result = longType.getValue(longValue);
        assertEquals(expected, result);
    }

    /**
     * Test of getValue method, of class LongType.
     */
    @Test
    public void testSetDefault() {
        String longValue = "3";
        LongType longType = new LongType();
        longType.setDefault(longValue);
        assertEquals(longValue, longType.toString());
    }

    /**
     * Test of setMinimum method, of class LongType.
     */
    @Test
    public void testSetMinimum() {
        String longValue = "11";
        int minimum = 10;
        long expected = Long.parseLong(longValue);
        LongType longType = new LongType();
        longType.setProperties("min=" + minimum);
        long result = longType.getValue(longValue);
        assertEquals(expected, result);
    }

    /**
     * Test of setMinimum method, of class LongType.
     */
    @Test
    public void testSetMinimumThrowsException() {
        String longValue = "9";
        int minimum = 10;
        long expected = Long.parseLong(longValue);
        LongType longType = new LongType();
        longType.setProperties("min=" + minimum);
        try {
            longType.getValue(longValue);
            fail("Expected an exception.");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), expected
                    + " is less than specified minimum: " + minimum);
        }
    }

    /**
     * Test of setMaximum method, of class LongType.
     */
    @Test
    public void testSetMaximum() {
        String longValue = "9";
        int maximum = 10;
        long expected = Long.parseLong(longValue);
        LongType longType = new LongType();
        longType.setProperties("max=" + maximum);
        long result = longType.getValue(longValue);
        assertEquals(expected, result);
    }

    /**
     * Test of setMaximum method, of class LongType.
     */
    @Test
    public void testSetMinMax() {
        String value = "7";
        int minimum = 0;
        int maximum = 10;
        int expected = Integer.parseInt(value);
        LongType longType = new LongType();
        longType.setProperties("min=" + minimum + ", max=" + maximum);
        long result = longType.getValue(value);
        assertEquals(expected, result);
    }

    /**
     * Test of setMinimum method, of class LongType.
     */
    @Test
    public void testSetMaximumThrowsException() {
        String longValue = "11";
        int maximum = 10;
        long expected = Long.parseLong(longValue);
        LongType longType = new LongType();
        longType.setProperties("max=" + maximum);
        try {
            longType.getValue(longValue);
            fail("Expected an exception.");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), expected
                    + " is greater than specified maximum: " + maximum);
        }
    }

    /**
     * Test of setMaximum method, of class LongType.
     */
    @Test
    public void testSetMinimumMaximum() {
        String intValue = "9";
        int minimum = 0;
        int maximum = 10;
        int expected = Integer.parseInt(intValue);
        LongType longType = new LongType();
        longType.setProperties("min=" + minimum + ",max=" + maximum);
        long result = longType.getValue(intValue);
        assertEquals((int) expected, (int) result, 0.0f);
    }

    /**
     * Test of setMinimum method, of class LongType.
     */
    @Test
    public void testSetPropertiesThrowsException() {
        LongType longType = new LongType();
        try {
            longType.setProperties("max");
            fail("Expected an exception.");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), "Invalid properties: max");
        }
    }

    /**
     * Test of setMaximum method, of class LongType.
     */
    @Test
    public void testSetInvalidPropertyThrowsException() {
        LongType longType = new LongType();
        try {
            longType.setProperties("foo=bar");
        } catch (Exception ex) {
            assertEquals(ex.getMessage(), "Invalid property: foo;"
                    + " expected property 'min' or 'max'");
        }
    }

    /**
     * Test of setMinimum method, of class LongType.
     */
    @Test
    public void testSetPropertiesBadNumberThrowsException() {
        LongType longType = new LongType();
        try {
            longType.setProperties("max=foo");
            fail("Expected an exception.");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), "Invalid value for max: foo");
        }
    }

    /**
     * Test of setMaximum method, of class LongType.
     */
    @Test
    public void testSetMinimumMaximumMinGreaterThanMax() {
        int minimum = 11;
        int maximum = 10;
        LongType longType = new LongType();
        try {
            longType.setProperties("min=" + minimum + ",max=" + maximum);
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
        LongType longType = new LongType();
        assertEquals("java.lang", longType.getPackageName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetJavaClassName() {
        LongType longType = new LongType();
        assertEquals("Long", longType.getJavaClassName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetJavaPrimitiveName() {
        LongType longType = new LongType();
        assertEquals("long", longType.getJavaPrimitiveName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetValueTypeName() {
        LongType longType = new LongType();
        assertEquals("long", longType.getValueTypeName());
    }

    /**
     * Test of toString method, of class LongType.
     */
    @Test
    public void testToString() {
        String expected = "3";
        LongType longType = new LongType();
        longType.getValue(expected);
        assertEquals(expected, longType.toString());
    }

}
