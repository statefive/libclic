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

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.statefive.clic.ClcException;

/**
 * Builder for Apache properties configuration CLC generators.
 *
 * @since 1.1
 *
 * @author irch
 */
public class PropertiesConfigurationClcGeneratorBuilder
        extends AbstractClcGeneratorBuilder<PropertiesConfigurationClcGenerator<PropertiesConfiguration>, PropertiesConfiguration> {

    /**
     * Apache configuration properties to convert to CLC output.
     */
    private PropertiesConfiguration configuration;

    /**
     * Create a new Apache configuration properties CLC generator.
     *
     * @return non-{@code null} Apache configuration properties CLC generator.
     * 
     * @throws ClcException if the properties are {@code null}.
     */
    @Override
    public PropertiesConfigurationClcGenerator build() throws ClcException {
        if (configuration == null) {
            throw new ClcException("Properties cannot be null.");
        }
        PropertiesConfigurationClcGenerator clcGenerator = new PropertiesConfigurationClcGenerator();
        clcGenerator.setClcOverrides(clcOverrides);
        clcGenerator.setHeader(globalHeader);
        clcGenerator.setInsertDefault(insertDefaults);
        clcGenerator.setPad(pad);
        clcGenerator.setProperties(configuration);
        if (this.propertyNameFilter != null) {
            clcGenerator.setPropertyNameFilter(propertyNameFilter);
        }
        if (this.propertyVersion != null) {
            clcGenerator.setPropertyVersion(propertyVersion);
        }
        if (this.typeInferralConfig != null) {
            clcGenerator.setTypeInferralConfig(typeInferralConfig);
        }
        return clcGenerator;
    }

    /**
     * Add the given properties to the builder.
     *
     * @param configuration non-{@code null} configuration properties to add.
     *
     * @return this.
     */
    @Override
    public ClcGeneratorBuilder properties(PropertiesConfiguration configuration) {
        this.configuration = configuration;
        return this;
    }

}
