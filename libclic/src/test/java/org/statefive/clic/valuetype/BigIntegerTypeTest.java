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

import java.math.BigInteger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author rich
 */
public class BigIntegerTypeTest {

    /**
     * Test of getValue method, of class BigIntegerType.
     */
    @Test
    public void testGetValue() {
        String bigIntegerValue = "3";
        BigInteger expected = new BigInteger(bigIntegerValue);
        BigIntegerType bigIntegerType = new BigIntegerType();
        BigInteger result = bigIntegerType.getValue(bigIntegerValue);
        assertEquals(expected, result);
    }

    /**
     * Test of getValue method, of class BigIntegerType.
     */
    @Test
    public void testSetDefault() {
        String bigIntegerValue = "3";
        BigIntegerType bigIntegerType = new BigIntegerType();
        bigIntegerType.setDefault(bigIntegerValue);
        assertEquals(bigIntegerValue, bigIntegerType.toString());
    }

    /**
     * Test of setMinimum method, of class BigIntegerType.
     */
    @Test
    public void testSetMinimum() {
        String bigIntegerValue = "11";
        BigInteger minimum = new BigInteger("10");
        BigInteger expected = new BigInteger(bigIntegerValue);
        BigIntegerType bigIntegerType = new BigIntegerType();
        bigIntegerType.setProperties("min=" + minimum);
        BigInteger result = bigIntegerType.getValue(bigIntegerValue);
        assertEquals(expected, result);
    }

    /**
     * Test of setMinimum method, of class BigIntegerType.
     */
    @Test
    public void testSetMinimumThrowsException() {
        String bigIntegerValue = "9";
        BigInteger minimum = new BigInteger("10");
        BigInteger expected = new BigInteger(bigIntegerValue);
        BigIntegerType bigIntegerType = new BigIntegerType();
        bigIntegerType.setProperties("min=" + minimum);
        try {
            bigIntegerType.getValue(bigIntegerValue);
            fail("Expected an exception.");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), expected
                    + " is less than specified minimum: " + minimum);
        }
    }

    /**
     * Test of setMaximum method, of class BigIntegerType.
     */
    @Test
    public void testSetMaximum() {
        String bigIntegerValue = "9";
        BigInteger maximum = new BigInteger("10");
        BigInteger expected = new BigInteger(bigIntegerValue);
        BigIntegerType bigIntegerType = new BigIntegerType();
        bigIntegerType.setProperties("max=" + maximum);
        BigInteger result = bigIntegerType.getValue(bigIntegerValue);
        assertEquals(expected, result);
    }

    /**
     * Test of setMaximum method, of class BigIntegerType.
     */
    @Test
    public void testSetMinMax() {
        String value = "7";
        BigInteger minimum = new BigInteger("0");
        BigInteger maximum = new BigInteger("10");
        BigInteger expected = new BigInteger(value);
        BigIntegerType bigIntegerType = new BigIntegerType();
        bigIntegerType.setProperties("min=" + minimum + ", max=" + maximum);
        BigInteger result = bigIntegerType.getValue(value);
        assertEquals(expected, result);
    }

    /**
     * Test of setMinimum method, of class BigIntegerType.
     */
    @Test
    public void testSetMaximumThrowsException() {
        String bigIntegerValue = "11";
        BigInteger maximum = new BigInteger("10");
        BigInteger expected = new BigInteger(bigIntegerValue);
        BigIntegerType bigIntegerType = new BigIntegerType();
        bigIntegerType.setProperties("max=" + maximum);
        try {
            bigIntegerType.getValue(bigIntegerValue);
            fail("Expected an exception.");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), expected
                    + " is greater than specified maximum: " + maximum);
        }
    }

    /**
     * Test of setMaximum method, of class BigIntegerType.
     */
    @Test
    public void testSetMinimumMaximum() {
        String bigIntegerValue = "9";
        BigInteger minimum = new BigInteger("0");
        BigInteger maximum = new BigInteger("10");
        BigInteger expected = new BigInteger(bigIntegerValue);
        BigIntegerType bigIntegerType = new BigIntegerType();
        bigIntegerType.setProperties("min=" + minimum + ",max=" + maximum);
        BigInteger result = bigIntegerType.getValue(bigIntegerValue);
        assertEquals(expected.intValue(), result.intValue());
    }

    /**
     * Test of setMinimum method, of class BigIntegerType.
     */
    @Test
    public void testSetPropertiesThrowsException() {
        BigIntegerType bigIntegerType = new BigIntegerType();
        try {
            bigIntegerType.setProperties("max");
            fail("Expected an exception.");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), "Invalid properties: max");
        }
    }

    /**
     * Test of setMaximum method, of class BigIntegerType.
     */
    @Test
    public void testSetInvalidPropertyThrowsException() {
        BigIntegerType bigIntegerType = new BigIntegerType();
        try {
            bigIntegerType.setProperties("foo=bar");
        } catch (Exception ex) {
            assertEquals(ex.getMessage(), "Invalid property: foo;"
                    + " expected property 'min' or 'max'");
        }
    }

    /**
     * Test of setMinimum method, of class BigIntegerType.
     */
    @Test
    public void testSetPropertiesBadNumberThrowsException() {
        BigIntegerType bigIntegerType = new BigIntegerType();
        try {
            bigIntegerType.setProperties("max=foo");
            fail("Expected an exception.");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), "Invalid value for max: foo");
        }
    }

    /**
     * Test of setMaximum method, of class BigIntegerType.
     */
    @Test
    public void testSetMinimumMaximumMinGreaterThanMax() {
        BigInteger minimum = new BigInteger("11");
        BigInteger maximum = new BigInteger("10");
        BigIntegerType bigIntegerType = new BigIntegerType();
        try {
            bigIntegerType.setProperties("min=" + minimum + ",max=" + maximum);
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
        BigIntegerType bigIntegerType = new BigIntegerType();
        assertEquals("java.math", bigIntegerType.getPackageName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetJavaTypeName() {
        BigIntegerType bigIntegerType = new BigIntegerType();
        assertEquals("BigInteger", bigIntegerType.getJavaClassName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetValueTypeName() {
        BigIntegerType bigIntegerType = new BigIntegerType();
        assertEquals("biginteger", bigIntegerType.getValueTypeName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetJavaPrimitiveName() {
        BigIntegerType bigIntegerType = new BigIntegerType();
        assertNull(bigIntegerType.getJavaPrimitiveName());
    }

    /**
     * Test of toString method, of class BigIntegerType.
     */
    @Test
    public void testToString() {
        String expected = "3";
        BigIntegerType bigIntegerType = new BigIntegerType();
        bigIntegerType.getValue(expected);
        assertEquals(expected, bigIntegerType.toString());
    }
    
}
