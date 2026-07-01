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

/**
 * {@link TypeInferralConfig} builder.
 *
 * @author rich
 */
public class TypeInferralConfigBuilder {
    
    /**
     * Type inference configuration.
     */
    private final TypeInferralConfig typeInferralConfig = new TypeInferralConfig();
    
    /**
     * Set type inference configuration inference of types to {@code true}.
     * 
     * @return this.
     */
    public TypeInferralConfigBuilder withInferTypes() {
        typeInferralConfig.setInferTypes(true);
        return this;
    }
    
    /**
     * Set the type inference configuration to treat all natural numbers as the
     * given type.
     * 
     * @param type non-{@code null} type.
     * 
     * @return this.
     */
    public TypeInferralConfigBuilder withNaturalNumbersAs(String type) {
        typeInferralConfig.setNaturalNumbersAs(type);
        return this;
    }
    
    /**
     * Set the type inference configuration to treat all real numbers as the
     * given type.
     * 
     * @param type non-{@code null} type.
     * 
     * @return this.
     */
    public TypeInferralConfigBuilder withRealNumbersAs(String type) {
        typeInferralConfig.setRealNumbersAs(type);
        return this;
    }
    
    /**
     * Treat all properties that are {@code false} as <i>unary</i> switches: the
     * default value will be {@code false} and supplying the switch-name of the
     * property will set the value to {@code true}.
     * 
     * @return this.
     */
    public TypeInferralConfigBuilder withFalseAsUnarySwitch() {
        typeInferralConfig.setFalseAsUnarySwitch(true);
        return this;
    }
    
    /**
     * Build the type inference configuration.
     * 
     * @return non-{@code null} type inference configuration.
     */
    public TypeInferralConfig build() {
        return typeInferralConfig;
    }
}
