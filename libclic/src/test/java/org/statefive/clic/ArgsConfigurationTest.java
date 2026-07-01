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
package org.statefive.clic;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author rich
 */
public class ArgsConfigurationTest {

    private ArgsConfiguration instance;

    /**
     * Initialise test instance.
     */
    @Before
    public void setUp() {
        instance = new ArgsConfiguration();
    }

    /**
     * Test of setLength and getLength method, of class ArgsConfiguration.
     */
    @Test
    public void testSetGetLength() throws Exception {
        Integer expResult = 10;
        instance.setLength(10);
        Integer result = instance.getLength();
        assertEquals(expResult, result);
    }

    /**
     * Test of setLength and getLength method, of class ArgsConfiguration.
     */
    @Test
    public void testSetGetLengthFailsIfLessThanZero() throws Exception {
        try {
            instance.setLength(-1);
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals("Invalid length: -1; minimum length is 0.",
                    ex.getMessage());
        }
    }

    /**
     * Test of setLength and getLength method, of class ArgsConfiguration.
     */
    @Test
    public void testSetGetLengthAsNull() throws Exception {
        Integer expResult = null;
        instance.setLength(expResult);
        assertEquals(expResult, instance.getLength());
        
    }

    /**
     * Test that isOptional evaluates to {@code false} if not set.
     */
    @Test
    public void testIsOptional() {
        boolean expResult = false;
        boolean result = instance.isOptional();
        assertEquals(expResult, result);
    }

    /**
     * Test that when not set, optional is {@code null}.
     */
    @Test
    public void testGetOptional() {
        Boolean expResult = null;
        Boolean result = instance.getOptional();
        assertEquals(expResult, result);
    }

    /**
     * Test of setOptional and getOptional method, of class ArgsConfiguration.
     */
    @Test
    public void testSetGetOptionalForFalse() {
        boolean optional = false;
        ArgsConfiguration instance = new ArgsConfiguration();
        instance.setOptional(optional);
        assertEquals(optional, instance.getOptional());
    }

    /**
     * Test of setOptional and getOptional method, of class ArgsConfiguration.
     */
    @Test
    public void testSetGetOptionalForTrue() {
        boolean optional = true;
        ArgsConfiguration instance = new ArgsConfiguration();
        instance.setOptional(optional);
        assertEquals(optional, instance.getOptional());
    }

    /**
     * Test that if the length is not set, the configuration will be classed as
     * unbounded.
     */
    @Test
    public void testIsUnbounded() {
        boolean expResult = true;
        boolean result = instance.isUnbounded();
        assertEquals(expResult, result);
    }

    /**
     * Test of isCappedAtZero method, of class ArgsConfiguration.
     */
    @Test
    public void testIsCappedAtZero() throws Exception {
        boolean expResult = true;
        instance.setLength(0);
        boolean result = instance.isCappedAtZero();
        assertEquals(expResult, result);
    }

}
