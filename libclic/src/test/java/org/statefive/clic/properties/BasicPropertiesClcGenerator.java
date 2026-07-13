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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.commons.configuration2.Configuration;
import org.statefive.clic.valuetype.ValueType;

/**
 * Basic property generator used solely for testing, used to test
 * {@link #generateConfiguration(java.util.Map)} and
 * {@link #generateConfiguration(java.util.Map, java.util.Map)}.
 *
 * @author rich
 * @param <P>
 */
public class BasicPropertiesClcGenerator<P extends BasicProperties> 
        extends AbstractClcGenerator<P> implements ClcGenerator<P> {

    /**
     * 
     * @return
     * @throws IOException 
     * 
     * @since 1.1
     */
    @Override
    public ByteArrayOutputStream generateConfiguration() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void setProperties(P properties) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    /**
     * Unused. 
     * 
     * @param properties unused.
     * 
     * @param config unused.
     * 
     * @param propertyFilter unused.
     * 
     * @param clcGlobalHaeder unused.
     * 
     * @param typeInferralConfig unused.
     * 
     * @param pad unused.
     * 
     * @param insertDefaults unused.
     * 
     * @return nothing.
     * 
     * @throws IOException never.
     * 
     * @throws UnsupportedOperationException when called.
     */
    @Override
    public ByteArrayOutputStream generateConfiguration(P properties, 
            Configuration config, PropertyNameFilter propertyFilter, 
            boolean clcGlobalHaeder, TypeInferralConfig typeInferralConfig,
            boolean pad, boolean insertDefaults) throws IOException {
        throw new UnsupportedOperationException("Operation not supported.");
    }

    /**
     * If inferal of value types is set, for the given value, attempt to infer
     * the type based on
     * {@link RegexPropertyValueTypeExtractor#getPropertyValueType(java.lang.String, java.lang.Object)}.
     *
     * @param propertyName non-{@code null} name of the original property.
     *
     * @param value non-{@code null} value to check.
     *
     * @return a {@link ValueType} of the correct value if it is not a string;
     * {@code null} otherwise, or if inferral of value types was not set.
     */
    @Override
    public ValueType getPropertyValueType(String propertyName, Object value) {
        ValueType valueType = RegexPropertyValueTypeExtractor.getPropertyValueType(
                propertyName, value, typeInferralConfig);
        if (valueType != null) {
            propertyValueTypes.put(propertyName, valueType);
        }
        return valueType;
    }
    
}
