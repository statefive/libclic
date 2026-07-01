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
package org.statefive.clic.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.statefive.clic.Clc;
import org.statefive.clic.valuetype.BooleanType;
import org.statefive.clic.valuetype.ByteType;
import org.statefive.clic.valuetype.DoubleType;
import org.statefive.clic.valuetype.FloatingPointType;
import org.statefive.clic.valuetype.IntegralType;
import org.statefive.clic.valuetype.ValueType;

/**
 *
 * @author rich
 */
public class RegexPropertyValueTypeExtractorTest {
    
    /**
     * 
     */
    @Rule
    public TestName name = new TestName();
    
    /**
     * 
     */
    @BeforeClass
    public static void setUpClass() {
        Clc.initialiseValueTypeFactory();
    }

    /**
     * Test of getPropertyValueType method, of class RegexPropertyValueTypeExtractor.
     */
    @Test
    public void testGetPropertyValueTypeForInt() {
        String propertyName = name.getMethodName();
        String value = "123";
        ValueType valueType = getValueType(propertyName, value, 
                new TypeInferralConfig());
        assertTrue(valueType instanceof IntegralType);
        assertEquals(123, (int) valueType.getValue(value));
    }

    /**
     * Test of getPropertyValueType method, of class RegexPropertyValueTypeExtractor.
     */
    @Test
    public void testGetPropertyValueTypeForFloat() {
        String propertyName = name.getMethodName();
        String value = "1.23";
        ValueType valueType = getValueType(propertyName, value, 
                new TypeInferralConfig());
        assertTrue(valueType instanceof FloatingPointType);
        assertEquals(1.23f, (float) valueType.getValue(value), 0.01);
    }

    /**
     * Test of getPropertyValueType method, of class RegexPropertyValueTypeExtractor.
     */
    @Test
    public void testGetPropertyValueTypeForBooleanTrue() {
        String propertyName = name.getMethodName();
        String value = "TrUe";
        ValueType valueType = getValueType(propertyName, value, 
                new TypeInferralConfig());
        assertTrue(valueType instanceof BooleanType);
        assertEquals(true, (boolean) valueType.getValue(value));
    }

    /**
     * Test of getPropertyValueType method, of class RegexPropertyValueTypeExtractor.
     */
    @Test
    public void testGetPropertyValueTypeForBooleanFalse() {
        String propertyName = name.getMethodName();
        String value = "fAlSe";
        ValueType valueType = getValueType(propertyName, value, 
                new TypeInferralConfig());
        assertTrue(valueType instanceof BooleanType);
        assertEquals(false, (boolean) valueType.getValue(value));
    }

    /**
     * Test of getPropertyValueType method, of class RegexPropertyValueTypeExtractor.
     */
    @Test
    public void testGetPropertyValueTypeForStringMatchesNothingElse() {
        String propertyName = name.getMethodName();
        String value = "some random string";
        ValueType valueType = getValueType(propertyName, value, 
                new TypeInferralConfig());
        assertNull(valueType);
    }

    /**
     * Test of getPropertyValueType method, of class RegexPropertyValueTypeExtractor.
     */
    @Test
    public void testGetPropertyValueTypeForTypeAsByte() {
        String propertyName = name.getMethodName();
        String value = "123";
        TypeInferralConfig typeInferralConfig = new TypeInferralConfigBuilder()
                .withNaturalNumbersAs(ByteType.BYTE).build();
        ValueType valueType = getValueType(propertyName, value, 
                typeInferralConfig);
        assertTrue(valueType instanceof ByteType);
        assertEquals(123, (byte) valueType.getValue(value));
    }

    /**
     * Test of getPropertyValueType method, of class RegexPropertyValueTypeExtractor.
     */
    @Test
    public void testGetPropertyValueTypeForTypeAsDouble() {
        String propertyName = name.getMethodName();
        String value = "1.23";
        TypeInferralConfig typeInferralConfig = new TypeInferralConfigBuilder()
                .withRealNumbersAs(DoubleType.DOUBLE).build();
        ValueType valueType = getValueType(propertyName, value, 
                typeInferralConfig);
        assertTrue(valueType instanceof DoubleType);
        assertEquals(1.23d, (double) valueType.getValue(value), 0.01);
    }

    /**
     * Test of getPropertyValueType method, of class RegexPropertyValueTypeExtractor.
     */
    @Test
    public void testGetPropertyValueTypeForTypeAsFloat() {
        String propertyName = name.getMethodName();
        String value = "1.23";
        TypeInferralConfig typeInferralConfig = new TypeInferralConfigBuilder()
                .withRealNumbersAs(FloatingPointType.FLOAT).build();;
        ValueType valueType = getValueType(propertyName, value, 
                typeInferralConfig);
        assertTrue(valueType instanceof FloatingPointType);
        assertEquals(1.23f, (float) valueType.getValue(value), 0.01);
    }

    /**
     * Test of getPropertyValueType method, of class RegexPropertyValueTypeExtractor.
     */
    @Test
    public void testGetPropertyValueTypeForTypeAsFloatSameAs() {
        String propertyName = name.getMethodName();
        String value = "1.23";
        TypeInferralConfig typeInferralConfig = new TypeInferralConfigBuilder()
                .withRealNumbersAs(FloatingPointType.FLOAT).build();
        ValueType valueType = getValueType(propertyName, value, 
                typeInferralConfig);
        assertTrue(valueType instanceof FloatingPointType);
        assertEquals(1.23f, (float) valueType.getValue(value), 0.01);
    }
    
    /**
     * 
     * @param propertyName
     * @param value
     * @param typeInferralConfig
     * @return 
     */
    private ValueType getValueType(String propertyName, String value, 
            TypeInferralConfig typeInferralConfig) {
        return RegexPropertyValueTypeExtractor.getPropertyValueType(
                propertyName, value, typeInferralConfig);
    }
}
