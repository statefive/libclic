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
import static org.statefive.clic.valuetype.CharacterType.EXCLUDES;
import static org.statefive.clic.valuetype.CharacterType.INCLUDES;
import static org.statefive.clic.valuetype.CharacterType.REGEX;

/**
 *
 * @author rich
 */
public class CharacterTypeTest {

    /**
     * Test of getValue method, of class CharacterType.
     */
    @Test
    public void testGetValue() {
        Character expected = 'a';
        CharacterType charType = new CharacterType();
        Character result = charType.getValue("a");
        assertEquals(expected, result);
    }

    /**
     * Test of getValue method, of class CharacterType.
     */
    @Test
    public void testGetValueExceptionForZeroLengthString() {
        CharacterType charType = new CharacterType();
        try {
            charType.getValue("");
            fail("Expected an exception");
        } catch (Exception ex) {
            assertEquals("Characters generated from"
                    + " strings must be length 1.", ex.getMessage());
        }
    }

    /**
     * Test of getValue method, of class CharacterType.
     */
    @Test
    public void testGetValueExceptionForStringTooLong() {
        CharacterType charType = new CharacterType();
        try {
            charType.getValue("1234");
            fail("Expected an exception");
        } catch (Exception ex) {
            assertEquals("Invalid string length 4 to create character from.",
                    ex.getMessage());
        }
    }

    /**
     * Test of getValue method, of class CharacterType.
     */
    @Test
    public void testSetDefault() {
        String expected = "x";
        CharacterType charType = new CharacterType();
        charType.setDefault("x");
        assertEquals(expected, charType.toString());
    }

    /**
     *
     */
    @Test
    public void testGetPackageName() {
        CharacterType charType = new CharacterType();
        assertEquals("java.lang", charType.getPackageName());
    }

    /**
     *
     */
    @Test
    public void testGetJavaClassName() {
        CharacterType charType = new CharacterType();
        assertEquals("Character", charType.getJavaClassName());
    }

    /**
     *
     */
    @Test
    public void testGetValuePrimitiveName() {
        CharacterType charType = new CharacterType();
        assertEquals("char", charType.getJavaPrimitiveName());
    }

    /**
     *
     */
    @Test
    public void testGetValueTypeName() {
        CharacterType charType = new CharacterType();
        assertEquals("char", charType.getValueTypeName());
    }

    /**
     * Test of toString method, of class CharacterType.
     */
    @Test
    public void testToString() {
        String expected = "z";
        CharacterType charType = new CharacterType();
        charType.getValue("z");
        assertEquals(expected, charType.toString());
    }

    /**
     * Test that when a badly formed regular expression is set via properties,
     * an exception is thrown.
     */
    @Test
    public void testBadIncludesRegexProperty() {
        CharacterType charType = new CharacterType();
        try {
            charType.setProperties(INCLUDES + "=[a-z, " + REGEX + "=true");
            fail("Expected an exception");
        } catch (ValueTypeCreationException ex) {
            assertEquals("Invalid regular expression for property '"
                    + INCLUDES + "': [a-z", ex.getMessage());
        }
    }

    /**
     * Test that when a badly formed regular expression is set via properties,
     * an exception is thrown.
     */
    @Test
    public void testBadExcludesRegexProperty() {
        CharacterType charType = new CharacterType();
        try {
            charType.setProperties(EXCLUDES + "=[a-z, " + REGEX + "=true");
            fail("Expected an exception");
        } catch (ValueTypeCreationException ex) {
            assertEquals("Invalid regular expression for property '"
                    + EXCLUDES + "': [a-z", ex.getMessage());
        }
    }

    /**
     * Test that when both includes and excludes are specified, an exception is
     * thrown.
     */
    @Test
    public void testExcludesAndIncludesSepcified() {
        CharacterType charType = new CharacterType();
        try {
            charType.setProperties(EXCLUDES + "=ab,"
                    + CharacterType.INCLUDES + "=ba");
            fail("Expected an exception");
        } catch (ValueTypeCreationException ex) {
            assertEquals("Cannot specify include and exclude properties; only"
                    + " one of the two can be set.", ex.getMessage());
        }
    }

    /**
     * Test that when both excludes and includes are specified, an exception is
     * thrown.
     */
    @Test
    public void testIncludesAndExcludesSepcified() {
        CharacterType charType = new CharacterType();
        try {
            charType.setProperties(INCLUDES + "=ba,"
                    + EXCLUDES + "=ab,");
            fail("Expected an exception");
        } catch (ValueTypeCreationException ex) {
            assertEquals("Cannot specify include and exclude properties; only"
                    + " one of the two can be set.", ex.getMessage());
        }
    }

    /**
     * Test that when an invalid property combination is specified, an exception
     * is thrown.
     */
    @Test
    public void testUnknownProperty() {
        CharacterType charType = new CharacterType();
        try {
            charType.setProperties("x=y");
            fail("Expected an exception");
        } catch (ValueTypeCreationException ex) {
            assertEquals("Unknown property: x=y", ex.getMessage());
        }
    }

    /**
     * Test that when no equals is specified an exception is thrown.
     */
    @Test
    public void testInvalidProperty() {
        CharacterType charType = new CharacterType();
        try {
            charType.setProperties("x");
            fail("Expected an exception");
        } catch (ValueTypeCreationException ex) {
            assertEquals("Invalid property: x", ex.getMessage());
        }
    }

    /**
     * Test that when includes characters are specified as a non-regular
     * expression, the test passes.
     */
    @Test
    public void testValidIncludesPropertyAsNonRegex() {
        CharacterType charType = new CharacterType();
        charType.setProperties(INCLUDES + "=abc");
        charType.getValue("c");
    }

    /**
     * Test that when includes characters are specified as a non-regular
     * expression and the input does not match, an exception is thrown.
     */
    @Test
    public void testValidIncludesPropertyAsNonRegexMissing() {
        CharacterType charType = new CharacterType();
        charType.setProperties(INCLUDES + "=abc");
        try {
            charType.getValue("d");
            fail("Expected an exception");
        } catch (ValueTypeCreationException ex) {
            assertEquals("Invalid character: d", ex.getMessage());
        }
    }

    /**
     * Test that when includes characters are specified as a non-regular
     * expression and the input does not match, an exception is thrown.
     */
    @Test
    public void testValidIncludesPropertyAsRegexMissing() {
        CharacterType charType = new CharacterType();
        charType.setProperties(INCLUDES + "=[abc]," + REGEX + "=true");
        try {
            charType.getValue("d");
            fail("Expected an exception");
        } catch (ValueTypeCreationException ex) {
            assertEquals("Invalid character: d", ex.getMessage());
        }
    }

    /**
     * Test that when includes characters are specified as a regular expression,
     * the test passes.
     */
    @Test
    public void testValidIncludesPropertyAsRegex() {
        CharacterType charType = new CharacterType();
        charType.setProperties(INCLUDES + "=[a-c]," + REGEX + "=true");
        charType.getValue("c");
    }

    /**
     * Test that when includes characters are specified as a regular expression,
     * the test passes.
     */
    @Test
    public void testValidExcludesPropertyAsRegex() {
        CharacterType charType = new CharacterType();
        charType.setProperties(EXCLUDES + "=[a-c]," + REGEX + "=true");
        charType.getValue("d");
    }

    /**
     * Test that when includes characters are specified as a regular expression,
     * the test passes.
     */
    @Test
    public void testValidExcludesPropertyAsNonRegex() {
        CharacterType charType = new CharacterType();
        charType.setProperties(EXCLUDES + "=abc,");
        charType.getValue("d");
    }

    /**
     * Test that when excludes characters are specified as a non-regular
     * expression and the input does not match, an exception is thrown.
     */
    @Test
    public void testValidExcludesPropertyAsNonRegexMissing() {
        CharacterType charType = new CharacterType();
        charType.setProperties(EXCLUDES + "=abc");
        try {
            charType.getValue("a");
            fail("Expected an exception");
        } catch (ValueTypeCreationException ex) {
            assertEquals("Invalid character: a", ex.getMessage());
        }
    }

    /**
     * Test that when excludes characters are specified as a non-regular
     * expression and the input does not match, an exception is thrown.
     */
    @Test
    public void testValidExcludesPropertyAsRegexMissing() {
        CharacterType charType = new CharacterType();
        charType.setProperties(EXCLUDES + "=[abc]," + REGEX + "=true");
        try {
            charType.getValue("c");
            fail("Expected an exception");
        } catch (ValueTypeCreationException ex) {
            assertEquals("Invalid character: c", ex.getMessage());
        }
    }

}
