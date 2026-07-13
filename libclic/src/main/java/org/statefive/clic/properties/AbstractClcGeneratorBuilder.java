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

import org.apache.commons.configuration2.Configuration;
import org.statefive.clic.ClcException;

/**
 * Abstract implementation CLC generator builder.
 *
 * @param <G> CLC generator implementation.
 *
 * @param <P> Properties implementation.
 *
 * @since 1.1
 *
 * @author irch
 */
public abstract class AbstractClcGeneratorBuilder<G extends ClcGenerator<P>, P> implements ClcGeneratorBuilder<G, P> {

    /**
     * Determine if to define a global header in the CLC generator output.
     */
    protected boolean globalHeader;

    /**
     * Determine of to pad generated output in the CLC generator with
     * hash-commented lines of extra options that can be used.
     */
    protected boolean pad;

    /**
     * Determine if insert property values as a default for the CLC generator.
     */
    protected boolean insertDefaults;

    /**
     * Global property version.
     */
    protected String propertyVersion;

    /**
     * CLC file of overrides.
     */
    protected Configuration clcOverrides;

    /**
     * Property name filter to include or exclude properties.
     */
    protected PropertyNameFilter propertyNameFilter;

    /**
     * Type inference configuration.
     */
    protected TypeInferralConfig typeInferralConfig;

    /**
     * Build the CLC generator.
     *
     * @return non-{@code null} CLC generator.
     *
     * @throws ClcException if any of the values to build are incorrect.
     */
    @Override
    public abstract G build() throws ClcException;

    /**
     * Add the given properties to the CLC generator.
     *
     * @param properties non-{@code null} properties to add to the generator.
     *
     * @return this.
     */
    @Override
    public abstract ClcGeneratorBuilder properties(P properties);

    /**
     * Include the value of the global header to the CLC generator.
     *
     * @param clcGlobalHeader {@code true} to include global header information
     * and included properties, {@code false} to only include the properties,
     *
     * @return this.
     */
    @Override
    public ClcGeneratorBuilder globalHeader(boolean clcGlobalHeader) {
        this.globalHeader = clcGlobalHeader;
        return this;
    }

    /**
     * Set whether to pad properties with commented-out (had prefixed) lines of
     * other options to include.
     *
     * @param pad {@code true} to include commented-out properties;
     * {@code false} to only include the properties themselves.
     *
     * @return this.
     */
    @Override
    public ClcGeneratorBuilder pad(boolean pad) {
        this.pad = pad;
        return this;
    }

    /**
     * Set whether to include property defaults.
     *
     * @param insertDefaults {@code true} to include as a default the value of
     * any properties in the properties files; {@code false} to leave property
     * entries blank/empty when generating the CLC.
     *
     * @return this.
     */
    @Override
    public ClcGeneratorBuilder insertDefaults(boolean insertDefaults) {
        this.insertDefaults = insertDefaults;
        return this;
    }

    /**
     * Include the specified CLC overrides to override generated properties.
     *
     * @param clcOverrides CLC overrides configuration file; {@code null} to not
     * override any properties.
     *
     * @return this.
     */
    @Override
    public ClcGeneratorBuilder clcOverrides(Configuration clcOverrides) {
        this.clcOverrides = clcOverrides;
        return this;
    }

    /**
     * Include the specified filter to include/exclude properties from the
     * generated output.
     *
     * @param propertyFilter property filter to apply; if {@code null}, accept
     * all properties.
     *
     * @return this.
     */
    @Override
    public ClcGeneratorBuilder propertyNameFilter(PropertyNameFilter propertyFilter) {
        this.propertyNameFilter = propertyFilter;
        return this;
    }

    /**
     * Infer types according to the rules of the given type inference
     * configuration.
     *
     * @param typeInferralConfig type inference configuration to determine
     * underlying types; if {@code null}, do not infer types.
     *
     * @return this.
     */
    @Override
    public ClcGeneratorBuilder typeInferralConfig(TypeInferralConfig typeInferralConfig) {
        this.typeInferralConfig = typeInferralConfig;
        return this;
    }

    /**
     * Use the specified property as the global version value when traversing
     * properties.
     *
     * @param propertyVersion property version to use; if {@code null}, do not
     * use versioning for properties.
     *
     * @return this.
     */
    @Override
    public ClcGeneratorBuilder propertyVersion(String propertyVersion) {
        this.propertyVersion = propertyVersion;
        return this;
    }

}
