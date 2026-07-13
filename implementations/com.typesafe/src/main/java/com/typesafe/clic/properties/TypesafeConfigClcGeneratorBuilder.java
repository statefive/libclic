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
package com.typesafe.clic.properties;

import com.typesafe.config.Config;
import org.statefive.clic.properties.AbstractClcGeneratorBuilder;
import org.statefive.clic.properties.ClcGeneratorBuilder;

/**
 * 
 * @since 1.1
 *
 * @author irch
 */
public class TypesafeConfigClcGeneratorBuilder extends AbstractClcGeneratorBuilder<TypesafeConfigClcGenerator<Config>, Config> {

    /**
     * 
     */
    private Config config;
    
    /**
     * 
     * @return 
     */
    @Override
    public TypesafeConfigClcGenerator build() {
        TypesafeConfigClcGenerator clcGenerator = new TypesafeConfigClcGenerator();
        clcGenerator.setClcOverrides(clcOverrides);
        clcGenerator.setHeader(globalHeader);
        clcGenerator.setInsertDefault(insertDefaults);
        clcGenerator.setPad(pad);
        clcGenerator.setProperties(config);
        clcGenerator.setPropertyNameFilter(propertyNameFilter);
        clcGenerator.setPropertyVersion(propertyVersion);
        clcGenerator.setTypeInferralConfig(typeInferralConfig);
        return clcGenerator;
    }

    /**
     * 
     * @param config
     * @return 
     */
    @Override
    public ClcGeneratorBuilder properties(Config config) {
        this.config = config;
        return this;
    }
    
}
