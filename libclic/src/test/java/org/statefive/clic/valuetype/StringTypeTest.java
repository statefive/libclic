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
public class StringTypeTest {

    /**
     * Test of getValue method, of class StringType.
     */
    @Test
    public void testGetValue() {
        String expected = "some value";
        StringType stringType = new StringType();
        String result = stringType.getValue(expected);
        assertEquals(expected, result);
    }

    /**
     * Test of getValue method, of class StringType.
     */
    @Test
    public void testSetDefault() {
        String expected = "some value";
        StringType stringType = new StringType();
        stringType.setDefault(expected);
        assertEquals(expected, stringType.toString());
    }

    /**
     * Test of getValue method, of class StringType.
     */
    @Test
    public void testGetValueMatchesRegex() {
        String expected = "some value";
        String match = "[A-Za-z\\s]+";
        StringType stringType = new StringType();
        stringType.setProperties("match=" + match);
        String result = stringType.getValue(expected);
        assertEquals(expected, result);
    }

    /**
     * Test of getValue method, of class StringType.
     */
    @Test
    public void testSetPropertiesThrowsExceptionNoEquals() {
        StringType stringType = new StringType();
        try {
            stringType.setProperties("match");
            fail("Expected an exception");
        } catch (Exception ex) {
            assertEquals("Invalid properties: match", ex.getMessage());
        }
    }

    /**
     * Test of getValue method, of class StringType.
     */
    @Test
    public void testSetPropertiesThrowsExceptionBadName() {
        StringType stringType = new StringType();
        try {
            stringType.setProperties("foo=bar");
            fail("Expected an exception");
        } catch (Exception ex) {
            assertEquals("Invalid property: foo; expected property 'match'", 
                    ex.getMessage());
        }
    }

    /**
     * Test of getValue method, of class StringType.
     */
    @Test
    public void testGetValueMatchRegexThrowsException() {
        String expected = "some value";
        String match = "[0-9]+";
        StringType stringType = new StringType();
        stringType.setProperties("match=" + match);
        try {
            stringType.getValue(expected);
            fail("Expected an exception.");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), "Data '" + expected
                    + "' is an invalid format.");
        }
    }
    
    /**
     * 
     */
    @Test
    public void testGetPackageName() {
        StringType stringType = new StringType();
        assertEquals("java.lang", stringType.getPackageName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetJavaTypeName() {
        StringType stringType = new StringType();
        assertEquals("String", stringType.getJavaClassName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetValueTypeName() {
        StringType stringType = new StringType();
        assertEquals("string", stringType.getValueTypeName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetJavaPrimitiveName() {
        StringType stringType = new StringType();
        assertNull(stringType.getJavaPrimitiveName());
    }

    /**
     * Test of toString method, of class StringType.
     */
    @Test
    public void testToString() {
        String expected = "some value";
        StringType stringType = new StringType();
        stringType.getValue(expected);
        assertEquals(expected, stringType.toString());
    }

}
