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

import java.math.BigDecimal;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author rich
 */
public class DoubleTypeTest {

    /**
     * Test of getValue method, of class DoubleType.
     */
    @Test
    public void testGetValue() {
        String doubleValue = "3.1";
        double expected = Double.parseDouble(doubleValue);
        DoubleType doubleType = new DoubleType();
        double result = doubleType.getValue(doubleValue);
        assertEquals((double) expected, (double) result, 0.0f);
    }

    /**
     * Test of getValue method, of class DoubleType.
     */
    @Test
    public void testSetDefault() {
        String doubleValue = "3.1";
        DoubleType doubleType = new DoubleType();
        doubleType.setDefault(doubleValue);
        assertEquals(doubleValue, doubleType.toString());
    }

    /**
     * Test of setMinimum method, of class DoubleType.
     */
    @Test
    public void testSetMinimum() {
        String doubleValue = "11.1";
        double minimum = 10.0d;
        double expected = Double.parseDouble(doubleValue);
        DoubleType doubleType = new DoubleType();
        doubleType.setProperties("min=" + minimum);
        double result = doubleType.getValue(doubleValue);
        assertEquals((double) expected, (double) result, 0.0f);
    }

    /**
     * Test of setMinimum method, of class DoubleType.
     */
    @Test
    public void testSetMinimumThrowsException() {
        String doubleValue = "9.9";
        double minimum = 10.0d;
        double expected = Double.parseDouble(doubleValue);
        DoubleType doubleType = new DoubleType();
        doubleType.setProperties("min=" + minimum);
        try {
            doubleType.getValue(doubleValue);
            fail("Expected an exception.");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), expected
                    + " is less than specified minimum: " + minimum);
        }
    }

    /**
     * Test of setMaximum method, of class DoubleType.
     */
    @Test
    public void testSetMaximum() {
        String doubleValue = "9.5";
        double maximum = 10.0d;
        double expected = Double.parseDouble(doubleValue);
        DoubleType doubleType = new DoubleType();
        doubleType.setProperties("max=" + maximum);
        double result = doubleType.getValue(doubleValue);
        assertEquals((double) expected, (double) result, 0.0f);
    }

    /**
     * Test of setMinimum method, of class DoubleType.
     */
    @Test
    public void testSetMaximumThrowsException() {
        String doubleValue = "11.322";
        double maximum = 10.0d;
        double expected = Double.parseDouble(doubleValue);
        DoubleType doubleType = new DoubleType();
        doubleType.setProperties("max=" + maximum);
        try {
            doubleType.getValue(doubleValue);
            fail("Expected an exception.");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), expected
                    + " is greater than specified maximum: " + maximum);
        }
    }

    /**
     * Test of setMaximum method, of class DoubleType.
     */
    @Test
    public void testSetMinimumMaximum() {
        String doubleValue = "9.5";
        double minimum = 0.0d;
        double maximum = 10.0d;
        double expected = Double.parseDouble(doubleValue);
        DoubleType doubleType = new DoubleType();
        doubleType.setProperties("min=" + minimum + ",max=" + maximum);
        double result = doubleType.getValue(doubleValue);
        assertEquals((double) expected, (double) result, 0.0f);
    }

    /**
     * Test of setMinimum method, of class DoubleType.
     */
    @Test
    public void testSetPropertiesThrowsException() {
        String doubleValue = "9.322";
        DoubleType doubleType = new DoubleType();
        try {
            doubleType.setProperties("max");
            fail("Expected an exception.");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), "Invalid properties: max");
        }
    }

    /**
     * Test of setMaximum method, of class DoubleType.
     */
    @Test
    public void testSetInvalidPropertyThrowsException() {
        DoubleType doubleType = new DoubleType();
        try {
            doubleType.setProperties("foo=bar");
        } catch (Exception ex) {
            assertEquals(ex.getMessage(), "Invalid property: foo;"
                    + " expected property 'min' or 'max'");
        }
    }

    /**
     * Test of setMinimum method, of class DoubleType.
     */
    @Test
    public void testSetPropertiesBadNumberThrowsException() {
        DoubleType doubleType = new DoubleType();
        try {
            doubleType.setProperties("max=foo");
            fail("Expected an exception.");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), "Invalid value for max: foo");
        }
    }

    /**
     * Test of setMaximum method, of class DoubleType.
     */
    @Test
    public void testSetMinimumMaximumMinGreaterThanMax() {
        double minimum = 10.1d;
        double maximum = 10.0d;
        DoubleType doubleType = new DoubleType();
        try {
            doubleType.setProperties("min=" + minimum + ",max=" + maximum);
            fail("Expected an exception.");
        } catch(Exception ex) {
            assertEquals(ex.getMessage(), "maximum (10.0) is less than minimum (10.1)");
        }
    }

    /**
     * Test of toString method, of class DoubleType.
     */
    @Test
    public void testToString() {
        String expected = "3.3";
        DoubleType doubleType = new DoubleType();
        doubleType.getValue(expected);
        assertEquals(expected, doubleType.toString());
    }

    /**
     * Test of getValue method, of class FloatingPointType.
     */
    @Test
    public void testGetValueInfinity() {
        BigDecimal num = new BigDecimal("2.7976931348623157E+308");
        String doubleValue = num.toString();
        DoubleType doubleType = new DoubleType();
        try {
            doubleType.getValue(doubleValue);
            fail("Expected an exception");
        } catch(Exception ex) {
            assertEquals("Infinite value: Infinity", ex.getMessage());
        }
    }

    /**
     * Test of getValue method, of class FloatingPointType.
     */
    @Test
    public void testGetValueNotANumber() {
        String doubleValue = "not-a-number";
        DoubleType doubleType = new DoubleType();
        try {
            doubleType.getValue(doubleValue);
            fail("Expected an exception");
        } catch(Exception ex) {
            assertEquals("For input string: \"not-a-number\"", ex.getMessage());
        }
    }
    
    /**
     * 
     */
    @Test
    public void testGetPackageName() {
        DoubleType doubleType = new DoubleType();
        assertEquals("java.lang", doubleType.getPackageName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetJavaClassName() {
        DoubleType doubleType = new DoubleType();
        assertEquals("Double", doubleType.getJavaClassName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetJavaPrimitiveName() {
        DoubleType doubleType = new DoubleType();
        assertEquals("double", doubleType.getJavaPrimitiveName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetValueTypeName() {
        DoubleType doubleType = new DoubleType();
        assertEquals("double", doubleType.getValueTypeName());
    }

}
