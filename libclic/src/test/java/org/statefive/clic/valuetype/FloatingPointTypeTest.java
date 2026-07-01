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
public class FloatingPointTypeTest {

    /**
     * Test of getValue method, of class FloatingPointType.
     */
    @Test
    public void testGetValue() {
        String floatValue = "3.1";
        float expected = Float.parseFloat(floatValue);
        FloatingPointType floatType = new FloatingPointType();
        float result = floatType.getValue(floatValue);
        assertEquals((float) expected, (float) result, 0.0f);
    }

    /**
     * Test of getValue method, of class FloatingPointType.
     */
    @Test
    public void testSetDefault() {
        String floatValue = "3.1";
        FloatingPointType floatType = new FloatingPointType();
        floatType.setDefault(floatValue);
        assertEquals(floatValue, floatType.toString());
    }

    /**
     * Test of setMinimum method, of class FloatingPointType.
     */
    @Test
    public void testSetMinimum() {
        String floatValue = "11.1";
        float minimum = 10.0f;
        float expected = Float.parseFloat(floatValue);
        FloatingPointType floatType = new FloatingPointType();
        floatType.setProperties("min=" + minimum);
        float result = floatType.getValue(floatValue);
        assertEquals((float) expected, (float) result, 0.0f);
    }

    /**
     * Test of setMinimum method, of class FloatingPointType.
     */
    @Test
    public void testSetMinimumThrowsException() {
        String floatValue = "9.9";
        float minimum = 10.0f;
        float expected = Float.parseFloat(floatValue);
        FloatingPointType floatType = new FloatingPointType();
        floatType.setProperties("min=" + minimum);
        try {
            floatType.getValue(floatValue);
            fail("Expected an exception.");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), expected
                    + " is less than specified minimum: " + minimum);
        }
    }

    /**
     * Test of setMaximum method, of class FloatingPointType.
     */
    @Test
    public void testSetMaximum() {
        String floatValue = "9.5";
        float maximum = 10.0f;
        float expected = Float.parseFloat(floatValue);
        FloatingPointType floatType = new FloatingPointType();
        floatType.setProperties("max=" + maximum);
        float result = floatType.getValue(floatValue);
        assertEquals((float) expected, (float) result, 0.0f);
    }

    /**
     * Test of setMinimum method, of class FloatingPointType.
     */
    @Test
    public void testSetMaximumThrowsException() {
        String floatValue = "11.322";
        float maximum = 10.0f;
        float expected = Float.parseFloat(floatValue);
        FloatingPointType floatType = new FloatingPointType();
        floatType.setProperties("max=" + maximum);
        try {
            floatType.getValue(floatValue);
            fail("Expected an exception.");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), expected
                    + " is greater than specified maximum: " + maximum);
        }
    }

    /**
     * Test of setMaximum method, of class FloatingPointType.
     */
    @Test
    public void testSetMinimumMaximum() {
        String floatValue = "9.5";
        float minimum = 0.0f;
        float maximum = 10.0f;
        float expected = Float.parseFloat(floatValue);
        FloatingPointType floatType = new FloatingPointType();
        floatType.setProperties("min=" + minimum + ",max=" + maximum);
        float result = floatType.getValue(floatValue);
        assertEquals((float) expected, (float) result, 0.0f);
    }

    /**
     * Test of setMinimum method, of class FloatingPointType.
     */
    @Test
    public void testSetPropertiesThrowsException() {
        String floatValue = "9.322";
        FloatingPointType floatType = new FloatingPointType();
        try {
            floatType.setProperties("max");
            fail("Expected an exception.");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), "Invalid properties: max");
        }
    }

    /**
     * Test of setMaximum method, of class FloatingPointType.
     */
    @Test
    public void testSetInvalidPropertyThrowsException() {
        FloatingPointType floatType = new FloatingPointType();
        try {
            floatType.setProperties("foo=bar");
        } catch (Exception ex) {
            assertEquals(ex.getMessage(), "Invalid property: foo;"
                    + " expected property 'min' or 'max'");
        }
    }

    /**
     * Test of setMinimum method, of class FloatingPointType.
     */
    @Test
    public void testSetPropertiesBadNumberThrowsException() {
        FloatingPointType floatType = new FloatingPointType();
        try {
            floatType.setProperties("max=foo");
            fail("Expected an exception.");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), "Invalid value for max: foo");
        }
    }

    /**
     * Test of setMaximum method, of class FloatingPointType.
     */
    @Test
    public void testSetMinimumMaximumMinGreaterThanMax() {
        float minimum = 10.1f;
        float maximum = 10.0f;
        FloatingPointType floatType = new FloatingPointType();
        try {
            floatType.setProperties("min=" + minimum + ",max=" + maximum);
            fail("Expected an exception.");
        } catch(Exception ex) {
            assertEquals(ex.getMessage(), "maximum (10.0) is less than minimum (10.1)");
        }
    }

    /**
     * Test of toString method, of class FloatingPointType.
     */
    @Test
    public void testToString() {
        String expected = "3.3";
        FloatingPointType floatType = new FloatingPointType();
        floatType.getValue(expected);
        assertEquals(expected, floatType.toString());
    }

    /**
     * Test of getValue method, of class FloatingPointType.
     */
    @Test
    public void testGetValueInfinity() {
        BigDecimal num = new BigDecimal(12.40282346638528860e+38);
        String floatValue = num.toString();
        FloatingPointType floatType = new FloatingPointType();
        try {
            floatType.getValue(floatValue);
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
        String floatValue = "not-a-number";
        FloatingPointType floatType = new FloatingPointType();
        try {
            floatType.getValue(floatValue);
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
        FloatingPointType floatType = new FloatingPointType();
        assertEquals("java.lang", floatType.getPackageName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetJavaClassName() {
        FloatingPointType floatType = new FloatingPointType();
        assertEquals("Float", floatType.getJavaClassName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetJavaPrimitiveName() {
        FloatingPointType floatType = new FloatingPointType();
        assertEquals("float", floatType.getJavaPrimitiveName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetValueTypeName() {
        FloatingPointType floatType = new FloatingPointType();
        assertEquals("float", floatType.getValueTypeName());
    }

}
