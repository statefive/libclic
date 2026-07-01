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

import java.math.BigDecimal;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author rich
 */
public class BigDecimalTypeTest {

    /**
     * Test of getValue method, of class BigDecimalType.
     */
    @Test
    public void testGetValue() {
        String bigDecimalValue = "3";
        BigDecimal expected = new BigDecimal(bigDecimalValue);
        BigDecimalType bigDecimalType = new BigDecimalType();
        BigDecimal result = bigDecimalType.getValue(bigDecimalValue);
        assertEquals(expected, result);
    }

    /**
     * Test of getValue method, of class BigDecimalType.
     */
    @Test
    public void testSetDefault() {
        String bigDecimalValue = "3";
        BigDecimalType bigDecimalType = new BigDecimalType();
        bigDecimalType.setDefault(bigDecimalValue);
        assertEquals(bigDecimalValue, bigDecimalType.toString());
    }

    /**
     * Test of setMinimum method, of class BigDecimalType.
     */
    @Test
    public void testSetMinimum() {
        String bigDecimalValue = "11";
        BigDecimal minimum = new BigDecimal("10");
        BigDecimal expected = new BigDecimal(bigDecimalValue);
        BigDecimalType bigDecimalType = new BigDecimalType();
        bigDecimalType.setProperties("min=" + minimum);
        BigDecimal result = bigDecimalType.getValue(bigDecimalValue);
        assertEquals(expected, result);
    }

    /**
     * Test of setMinimum method, of class BigDecimalType.
     */
    @Test
    public void testSetMinimumThrowsException() {
        String bigDecimalValue = "9";
        BigDecimal minimum = new BigDecimal("10");
        BigDecimal expected = new BigDecimal(bigDecimalValue);
        BigDecimalType bigDecimalType = new BigDecimalType();
        bigDecimalType.setProperties("min=" + minimum);
        try {
            bigDecimalType.getValue(bigDecimalValue);
            fail("Expected an exception.");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), expected
                    + " is less than specified minimum: " + minimum);
        }
    }

    /**
     * Test of setMaximum method, of class BigDecimalType.
     */
    @Test
    public void testSetMaximum() {
        String bigDecimalValue = "9";
        BigDecimal maximum = new BigDecimal("10");
        BigDecimal expected = new BigDecimal(bigDecimalValue);
        BigDecimalType bigDecimalType = new BigDecimalType();
        bigDecimalType.setProperties("max=" + maximum);
        BigDecimal result = bigDecimalType.getValue(bigDecimalValue);
        assertEquals(expected, result);
    }

    /**
     * Test of setMaximum method, of class BigDecimalType.
     */
    @Test
    public void testSetMinMax() {
        String value = "7";
        BigDecimal minimum = new BigDecimal("0");
        BigDecimal maximum = new BigDecimal("10");
        BigDecimal expected = new BigDecimal(value);
        BigDecimalType bigDecimalType = new BigDecimalType();
        bigDecimalType.setProperties("min=" + minimum + ", max=" + maximum);
        BigDecimal result = bigDecimalType.getValue(value);
        assertEquals(expected, result);
    }

    /**
     * Test of setMinimum method, of class BigDecimalType.
     */
    @Test
    public void testSetMaximumThrowsException() {
        String bigDecimalValue = "11";
        BigDecimal maximum = new BigDecimal("10");
        BigDecimal expected = new BigDecimal(bigDecimalValue);
        BigDecimalType bigDecimalType = new BigDecimalType();
        bigDecimalType.setProperties("max=" + maximum);
        try {
            bigDecimalType.getValue(bigDecimalValue);
            fail("Expected an exception.");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), expected
                    + " is greater than specified maximum: " + maximum);
        }
    }

    /**
     * Test of setMaximum method, of class BigDecimalType.
     */
    @Test
    public void testSetMinimumMaximum() {
        String bigDecimalValue = "9";
        BigDecimal minimum = new BigDecimal("0");
        BigDecimal maximum = new BigDecimal("10");
        BigDecimal expected = new BigDecimal(bigDecimalValue);
        BigDecimalType bigDecimalType = new BigDecimalType();
        bigDecimalType.setProperties("min=" + minimum + ",max=" + maximum);
        BigDecimal result = bigDecimalType.getValue(bigDecimalValue);
        assertEquals(expected.intValue(), result.intValue());
    }

    /**
     * Test of setMinimum method, of class BigDecimalType.
     */
    @Test
    public void testSetPropertiesThrowsException() {
        BigDecimalType bigDecimalType = new BigDecimalType();
        try {
            bigDecimalType.setProperties("max");
            fail("Expected an exception.");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), "Invalid properties: max");
        }
    }

    /**
     * Test of setMaximum method, of class BigDecimalType.
     */
    @Test
    public void testSetInvalidPropertyThrowsException() {
        BigDecimalType bigDecimalType = new BigDecimalType();
        try {
            bigDecimalType.setProperties("foo=bar");
        } catch (Exception ex) {
            assertEquals(ex.getMessage(), "Invalid property: foo;"
                    + " expected property 'min' or 'max'");
        }
    }

    /**
     * Test of setMinimum method, of class BigDecimalType.
     */
    @Test
    public void testSetPropertiesBadNumberThrowsException() {
        BigDecimalType bigDecimalType = new BigDecimalType();
        try {
            bigDecimalType.setProperties("max=foo");
            fail("Expected an exception.");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), "Invalid value for max: foo");
        }
    }

    /**
     * Test of setMaximum method, of class BigDecimalType.
     */
    @Test
    public void testSetMinimumMaximumMinGreaterThanMax() {
        BigDecimal minimum = new BigDecimal("11");
        BigDecimal maximum = new BigDecimal("10");
        BigDecimalType bigDecimalType = new BigDecimalType();
        try {
            bigDecimalType.setProperties("min=" + minimum + ",max=" + maximum);
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
        BigDecimalType bigDecimalType = new BigDecimalType();
        assertEquals("java.math", bigDecimalType.getPackageName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetJavaClassName() {
        BigDecimalType bigDecimalType = new BigDecimalType();
        assertEquals("BigDecimal", bigDecimalType.getJavaClassName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetValueTypeName() {
        BigDecimalType bigDecimalType = new BigDecimalType();
        assertEquals("bigdecimal", bigDecimalType.getValueTypeName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetJavaPrimitiveName() {
        BigDecimalType bigDecimalType = new BigDecimalType();
        assertNull(bigDecimalType.getJavaPrimitiveName());
    }

    /**
     * Test of toString method, of class BigDecimalType.
     */
    @Test
    public void testToString() {
        String expected = "3";
        BigDecimalType bigDecimalType = new BigDecimalType();
        bigDecimalType.getValue(expected);
        assertEquals(expected, bigDecimalType.toString());
    }
    
}
