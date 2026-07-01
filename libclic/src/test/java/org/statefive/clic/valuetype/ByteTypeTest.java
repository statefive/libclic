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
public class ByteTypeTest {

    /**
     * Test of getValue method, of class ByteType.
     */
    @Test
    public void testGetValue() {
        String byteValue = "3";
        byte expected = Byte.parseByte(byteValue);
        ByteType byteType = new ByteType();
        byte result = byteType.getValue(byteValue);
        assertEquals(expected, result);
    }

    /**
     * Test of getValue method, of class ByteType.
     */
    @Test
    public void testSetDefault() {
        String byteValue = "3";
        ByteType byteType = new ByteType();
        byteType.setDefault(byteValue);
        assertEquals(byteValue, byteType.toString());
    }

    /**
     * Test of setMinimum method, of class ByteType.
     */
    @Test
    public void testSetMinimum() {
        String byteValue = "11";
        byte minimum = 10;
        byte expected = Byte.parseByte(byteValue);
        ByteType byteType = new ByteType();
        byteType.setProperties("min=" + minimum);
        byte result = byteType.getValue(byteValue);
        assertEquals(expected, result);
    }

    /**
     * Test of setMinimum method, of class ByteType.
     */
    @Test
    public void testSetMinimumThrowsException() {
        String byteValue = "9";
        byte minimum = 10;
        byte expected = Byte.parseByte(byteValue);
        ByteType byteType = new ByteType();
        byteType.setProperties("min=" + minimum);
        try {
            byteType.getValue(byteValue);
            fail("Expected an exception.");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), expected
                    + " is less than specified minimum: " + minimum);
        }
    }

    /**
     * Test of setMaximum method, of class ByteType.
     */
    @Test
    public void testSetMaximum() {
        String byteValue = "9";
        byte maximum = 10;
        byte expected = Byte.parseByte(byteValue);
        ByteType byteType = new ByteType();
        byteType.setProperties("max=" + maximum);
        byte result = byteType.getValue(byteValue);
        assertEquals(expected, result);
    }

    /**
     * Test of setMaximum method, of class ByteType.
     */
    @Test
    public void testSetMinMax() {
        String value = "7";
        byte minimum = 0;
        byte maximum = 10;
        byte expected = Byte.parseByte(value);
        ByteType byteType = new ByteType();
        byteType.setProperties("min=" + minimum + ", max=" + maximum);
        byte result = byteType.getValue(value);
        assertEquals(expected, result);
    }

    /**
     * Test of setMinimum method, of class ByteType.
     */
    @Test
    public void testSetMaximumThrowsException() {
        String byteValue = "11";
        byte maximum = 10;
        byte expected = Byte.parseByte(byteValue);
        ByteType byteType = new ByteType();
        byteType.setProperties("max=" + maximum);
        try {
            byteType.getValue(byteValue);
            fail("Expected an exception.");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), expected
                    + " is greater than specified maximum: " + maximum);
        }
    }

    /**
     * Test of setMaximum method, of class ByteType.
     */
    @Test
    public void testSetMinimumMaximum() {
        String byteValue = "9";
        byte minimum = 0;
        byte maximum = 10;
        byte expected = Byte.parseByte(byteValue);
        ByteType byteType = new ByteType();
        byteType.setProperties("min=" + minimum + ",max=" + maximum);
        byte result = byteType.getValue(byteValue);
        assertEquals((int) expected, (int) result, 0.0f);
    }

    /**
     * Test of setMinimum method, of class ByteType.
     */
    @Test
    public void testSetPropertiesThrowsException() {
        ByteType byteType = new ByteType();
        try {
            byteType.setProperties("max");
            fail("Expected an exception.");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), "Invalid properties: max");
        }
    }

    /**
     * Test of setMaximum method, of class ByteType.
     */
    @Test
    public void testSetInvalidPropertyThrowsException() {
        ByteType byteType = new ByteType();
        try {
            byteType.setProperties("foo=bar");
        } catch (Exception ex) {
            assertEquals(ex.getMessage(), "Invalid property: foo;"
                    + " expected property 'min' or 'max'");
        }
    }

    /**
     * Test of setMinimum method, of class ByteType.
     */
    @Test
    public void testSetPropertiesBadNumberThrowsException() {
        ByteType byteType = new ByteType();
        try {
            byteType.setProperties("max=foo");
            fail("Expected an exception.");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), "Invalid value for max: foo");
        }
    }

    /**
     * Test of setMaximum method, of class ByteType.
     */
    @Test
    public void testSetMinimumMaximumMinGreaterThanMax() {
        byte minimum = 11;
        byte maximum = 10;
        ByteType byteType = new ByteType();
        try {
            byteType.setProperties("min=" + minimum + ",max=" + maximum);
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
        ByteType byteType = new ByteType();
        assertEquals("java.lang", byteType.getPackageName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetJavaClassName() {
        ByteType byteType = new ByteType();
        assertEquals("Byte", byteType.getJavaClassName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetJavaPrimitiveName() {
        ByteType byteType = new ByteType();
        assertEquals("byte", byteType.getJavaPrimitiveName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetValueTypeName() {
        ByteType byteType = new ByteType();
        assertEquals("byte", byteType.getValueTypeName());
    }

    /**
     * Test of toString method, of class ByteType.
     */
    @Test
    public void testToString() {
        String expected = "3";
        ByteType byteType = new ByteType();
        byteType.getValue(expected);
        assertEquals(expected, byteType.toString());
    }

}
