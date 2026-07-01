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
 */package org.statefive.clic.valuetype;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author irch
 */
public class AbstractValueTypeTest {

    private InnerTestClass tester;

    @Before
    public void setUp() {
        tester = new InnerTestClass();
    }

    @Test
    public void testSetGetPackageName() {
        tester.setPackageName(String.class.getPackageName());
        assertEquals("java.lang", tester.getPackageName());
    }

    @Test
    public void testGetJavaClassName() {
        tester.setJavaClassName(String.class.getSimpleName());
        assertEquals("String", tester.getJavaClassName());
    }

    @Test
    public void testGetJavaPrimitiveName() {
        tester.setJavaPrimitiveName("int");
        assertEquals("int", tester.getJavaPrimitiveName());
    }

    @Test
    public void testGetValueTypeName() {
        tester.setValueTypeName("foo");
        assertEquals("foo", tester.getValueTypeName());
    }

    /**
     *
     */
    private class InnerTestClass extends AbstractValueType {

        @Override
        public Object getValue(String data) throws ValueTypeCreationException {
            return data;
        }

        @Override
        public void setDefault(String data) throws ValueTypeCreationException {
            // does nothing
        }

        @Override
        public void setProperties(String properties) throws ValueTypeCreationException {
            // does nothing
        }

    }
}
