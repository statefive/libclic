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

import org.junit.Test;
import static org.junit.Assert.*;
import org.statefive.clic.ClcParser;
import static org.statefive.clic.valuetype.BooleanType.TRUTH_MAPPINGS;

/**
 *
 * @author rich
 */
public class BooleanTypeTest {

    /**
     * Test of getValue method, of class BooleanType.
     */
    @Test
    public void testGetValue() {
        String boolValue = ClcParser.TRUE;
        boolean expected = Boolean.parseBoolean(boolValue);
        BooleanType boolType = new BooleanType();
        boolean result = boolType.getValue(boolValue);
        assertEquals(expected, result);
    }

    /**
     * Test of getValue method, of class BooleanType.
     */
    @Test
    public void testGetValueThrowsException() {
        String boolValue = "on";
        BooleanType boolType = new BooleanType();
        try {
            boolType.getValue(boolValue);
            fail("Expected an exception");
        } catch (ValueTypeCreationException ex) {
            assertEquals("Could not determine truth"
                    + " value for value 'on'", ex.getMessage());
        }

    }

    /**
     * Test of setDefault method, of class BooleanType.
     */
    @Test
    public void testSetDefault() {
        String boolValue = ClcParser.FALSE;
        BooleanType boolType = new BooleanType();
        boolType.setDefault(boolValue);
        assertEquals(boolValue, boolType.toString());
    }

    /**
     * Test of setProperties method, of class BooleanType.
     */
    @Test
    public void testSetProperties() {
        String props = BooleanType.TRUTH_MAPPINGS + "=on=true; off = false";
        BooleanType boolType = new BooleanType();
        boolType.setProperties(props);
        assertEquals(true, boolType.getValue("on"));
        assertEquals(false, boolType.getValue("off"));
    }

    /**
     * Test of setProperties method, of class BooleanType.
     */
    @Test
    public void testSetPropertiesMissingEquals() {
        String props = BooleanType.TRUTH_MAPPINGS;
        BooleanType boolType = new BooleanType();
        try {
            boolType.setProperties(props);
            fail("Expected an exception");
        } catch (ValueTypeCreationException ex) {
            assertEquals("Invalid properties: " + BooleanType.TRUTH_MAPPINGS, 
                    ex.getMessage());
        }
    }

    /**
     * Test of setProperties method, of class BooleanType.
     */
    @Test
    public void testSetPropertiesBadPropertyKey() {
        String props = BooleanType.TRUTH_MAPPINGS + "-foo-bar=bar-foo=bar-foo";
        BooleanType boolType = new BooleanType();
        try {
            boolType.setProperties(props);
            fail("Expected an exception");
        } catch (ValueTypeCreationException ex) {
            assertEquals("Invalid property: "
                    + TRUTH_MAPPINGS + "-foo-bar; expected property '" + TRUTH_MAPPINGS + "'", 
                    ex.getMessage());
        }
    }

    /**
     * Test of setProperties method, of class BooleanType.
     */
    @Test
    public void testSetPropertiesBadPropertyNoEqualsForMappings() {
        String props = BooleanType.TRUTH_MAPPINGS + "=bar-foo";
        BooleanType boolType = new BooleanType();
        try {
            boolType.setProperties(props);
            fail("Expected an exception");
        } catch (ValueTypeCreationException ex) {
            assertEquals("Expected to find true/false"
                    + " mappings separated by '=', found none: " + props, 
                    ex.getMessage());
        }
    }

    /**
     * Test of setProperties method, of class BooleanType.
     */
    @Test
    public void testSetPropertiesBadPropertyNoEqualsFor1Mapping() {
        String props = BooleanType.TRUTH_MAPPINGS + "= 0=true; 1";
        BooleanType boolType = new BooleanType();
        try {
            boolType.setProperties(props);
            fail("Expected an exception");
        } catch (ValueTypeCreationException ex) {
            assertEquals("Bad proeprty mapping: 1; no equals assignment"
                    + " to map to truth value", 
                    ex.getMessage());
        }
    }

    /**
     * Test of setProperties method, of class BooleanType.
     */
    @Test
    public void testSetPropertiesBadPropertyNotTrue() {
        String props = BooleanType.TRUTH_MAPPINGS + "= 0=true(ish)";
        BooleanType boolType = new BooleanType();
        try {
            boolType.setProperties(props);
            fail("Expected an exception");
        } catch (ValueTypeCreationException ex) {
            assertEquals("Invalid mapping value: true(ish)"
                        + "; can only be 'true' or 'false'", 
                    ex.getMessage());
        }
    }

    /**
     * Test of setProperties method, of class BooleanType.
     */
    @Test
    public void testSetPropertiesDuplicateMapping() {
        String props = BooleanType.TRUTH_MAPPINGS + "= on = true ; ON = true";
        BooleanType boolType = new BooleanType();
        try {
            boolType.setProperties(props);
            fail("Expected an exception");
        } catch (ValueTypeCreationException ex) {
            assertEquals("Duplicated truth mapping: on", 
                    ex.getMessage());
        }
    }

    /**
     * Test of setProperties method, of class BooleanType.
     */
    @Test
    public void testSetPropertiesTrueNotSet() {
        String props = BooleanType.TRUTH_MAPPINGS + "= off = false";
        BooleanType boolType = new BooleanType();
        try {
            boolType.setProperties(props);
            fail("Expected an exception");
        } catch (ValueTypeCreationException ex) {
            assertEquals("No 'true' value set for properties: " + props, 
                    ex.getMessage());
        }
    }

    /**
     * Test of setProperties method, of class BooleanType.
     */
    @Test
    public void testSetPropertiesFalseNotSet() {
        String props = BooleanType.TRUTH_MAPPINGS + "= off = true";
        BooleanType boolType = new BooleanType();
        try {
            boolType.setProperties(props);
            fail("Expected an exception");
        } catch (ValueTypeCreationException ex) {
            assertEquals("No 'false' value set for properties: " + props, 
                    ex.getMessage());
        }
    }
    
    /**
     * 
     */
    @Test
    public void testGetPackageName() {
        BooleanType boolType = new BooleanType();
        assertEquals("java.lang", boolType.getPackageName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetJavaClassName() {
        BooleanType boolType = new BooleanType();
        assertEquals("Boolean", boolType.getJavaClassName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetJavaPrimitiveName() {
        BooleanType boolType = new BooleanType();
        assertEquals("boolean", boolType.getJavaPrimitiveName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetValueTypeName() {
        BooleanType boolType = new BooleanType();
        assertEquals("boolean", boolType.getValueTypeName());
    }

}
