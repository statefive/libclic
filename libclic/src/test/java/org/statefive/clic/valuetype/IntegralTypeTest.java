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
public class IntegralTypeTest {

    /**
     * Test of getValue method, of class IntegralType.
     */
    @Test
    public void testGetValue() {
        String intValue = "3";
        int expected = Integer.parseInt(intValue);
        IntegralType intType = new IntegralType();
        int result = intType.getValue(intValue);
        assertEquals(expected, result);
    }

    /**
     * Test of getValue method, of class IntegralType.
     */
    @Test
    public void testSetDefault() {
        String intValue = "3";
        IntegralType intType = new IntegralType();
        intType.setDefault(intValue);
        assertEquals(intValue, intType.toString());
    }

    /**
     * Test of setMinimum method, of class IntegralType.
     */
    @Test
    public void testSetMinimum() {
        String intValue = "11";
        int minimum = 10;
        int expected = Integer.parseInt(intValue);
        IntegralType intType = new IntegralType();
        intType.setProperties("min=" + minimum);
        int result = intType.getValue(intValue);
        assertEquals(expected, result);
    }

    /**
     * Test of setMinimum method, of class IntegralType.
     */
    @Test
    public void testSetMinimumThrowsException() {
        String intValue = "9";
        int minimum = 10;
        int expected = Integer.parseInt(intValue);
        IntegralType intType = new IntegralType();
        intType.setProperties("min=" + minimum);
        try {
            intType.getValue(intValue);
            fail("Expected an exception.");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), expected
                    + " is less than specified minimum: " + minimum);
        }
    }

    /**
     * Test of setMaximum method, of class IntegralType.
     */
    @Test
    public void testSetMaximum() {
        String intValue = "9";
        int maximum = 10;
        int expected = Integer.parseInt(intValue);
        IntegralType intType = new IntegralType();
        intType.setProperties("max=" + maximum);
        int result = intType.getValue(intValue);
        assertEquals(expected, result);
    }

    /**
     * Test of setMaximum method, of class IntegralType.
     */
    @Test
    public void testSetMinMax() {
        String value = "7";
        int minimum = 0;
        int maximum = 10;
        int expected = Integer.parseInt(value);
        IntegralType intType = new IntegralType();
        intType.setProperties("min=" + minimum + ", max=" + maximum);
        int result = intType.getValue(value);
        assertEquals(expected, result);
    }

    /**
     * Test of setMinimum method, of class IntegralType.
     */
    @Test
    public void testSetMaximumThrowsException() {
        String intValue = "11";
        int maximum = 10;
        int expected = Integer.parseInt(intValue);
        IntegralType intType = new IntegralType();
        intType.setProperties("max=" + maximum);
        try {
            intType.getValue(intValue);
            fail("Expected an exception.");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), expected
                    + " is greater than specified maximum: " + maximum);
        }
    }

    /**
     * Test of setMaximum method, of class IntegralType.
     */
    @Test
    public void testSetMinimumMaximum() {
        String intValue = "9";
        int minimum = 0;
        int maximum = 10;
        int expected = Integer.parseInt(intValue);
        IntegralType intType = new IntegralType();
        intType.setProperties("min=" + minimum + ",max=" + maximum);
        int result = intType.getValue(intValue);
        assertEquals((int) expected, (int) result, 0.0f);
    }

    /**
     * Test of setMinimum method, of class IntegralType.
     */
    @Test
    public void testSetPropertiesThrowsException() {
        IntegralType intType = new IntegralType();
        try {
            intType.setProperties("max");
            fail("Expected an exception.");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), "Invalid properties: max");
        }
    }

    /**
     * Test of setMaximum method, of class IntegralType.
     */
    @Test
    public void testSetInvalidPropertyThrowsException() {
        IntegralType intType = new IntegralType();
        try {
            intType.setProperties("foo=bar");
        } catch (Exception ex) {
            assertEquals(ex.getMessage(), "Invalid property: foo;"
                    + " expected property 'min' or 'max'");
        }
    }

    /**
     * Test of setMinimum method, of class IntegralType.
     */
    @Test
    public void testSetPropertiesBadNumberThrowsException() {
        IntegralType intType = new IntegralType();
        try {
            intType.setProperties("max=foo");
            fail("Expected an exception.");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), "Invalid value for max: foo");
        }
    }

    /**
     * Test of setMaximum method, of class IntegralType.
     */
    @Test
    public void testSetMinimumMaximumMinGreaterThanMax() {
        int minimum = 11;
        int maximum = 10;
        IntegralType intType = new IntegralType();
        try {
            intType.setProperties("min=" + minimum + ",max=" + maximum);
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
        IntegralType intType = new IntegralType();
        assertEquals("java.lang", intType.getPackageName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetJavaClassName() {
        IntegralType intType = new IntegralType();
        assertEquals("Integer", intType.getJavaClassName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetJavaPrimitiveName() {
        IntegralType intType = new IntegralType();
        assertEquals("int", intType.getJavaPrimitiveName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetValueTypeName() {
        IntegralType intType = new IntegralType();
        assertEquals("int", intType.getValueTypeName());
    }

    /**
     * Test of toString method, of class IntegralType.
     */
    @Test
    public void testToString() {
        String expected = "3";
        IntegralType intType = new IntegralType();
        intType.getValue(expected);
        assertEquals(expected, intType.toString());
    }

}
