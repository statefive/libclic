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

/**
 *
 * @author rich
 */
public class ValueTypeBuilderTest {

    private class BadValueType implements ValueType<Object, String> {

        public BadValueType(int x) {

        }

        @Override
        public Object getValue(String data) throws ValueTypeCreationException {
            return data;
        }

        @Override
        public void setDefault(String data) throws ValueTypeCreationException {

        }

        @Override
        public void setProperties(String properties) throws ValueTypeCreationException {

        }

        @Override
        public String render(String renderable) {
            return renderable;
        }

        @Override
        public String getPackageName() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getJavaClassName() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getJavaPrimitiveName() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getValueTypeName() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    /**
     * Test of build method, of class ValueTypeBuilder.
     */
    @Test
    public void testBuild() {
        ValueTypeBuilder instance = new ValueTypeBuilder(BadValueType.class);
        try {
            instance.build();
            fail("Expected an exception");
        } catch (ValueTypeCreationException ex) {
            assertEquals(NoSuchMethodException.class.getName(), ex.getCause().getClass().getName());
        }
    }

}
