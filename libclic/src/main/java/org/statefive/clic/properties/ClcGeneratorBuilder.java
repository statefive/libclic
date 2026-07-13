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
 * Build a CLC generator.
 *
 * @since 1.1
 *
 * @author irch
 *
 * @param <G> CLC Generator implementation.
 *
 * @param <P> Properties implementation.
 */
public interface ClcGeneratorBuilder<G extends ClcGenerator<P>, P> {

    /**
     * Create a generator.
     *
     * @return non-{@code null} CLC generator.
     *
     * @throws ClcException if any of the values to build are incorrect.
     */
    G build() throws ClcException;

    /**
     * Set the properties to read for the given generator.
     *
     * @param properties non-{@code null} properties.
     *
     * @return this.
     */
    ClcGeneratorBuilder properties(P properties);

    /**
     * Set of the CLC generator should generate a global header.
     *
     * @param clcGlobalHeader {@code true} to generate a global header;
     * {@code false} otherwise.
     *
     * @return this.
     */
    ClcGeneratorBuilder globalHeader(boolean clcGlobalHeader);

    /**
     * Set if to pad the generated content with commented-out data of other
     * options that are available in the generated content; {@code false} to not
     * generate such content.
     *
     * @param pad {@code true} to pad the configuration with commented-out data;
     * {@code false} otherwise.
     *
     * @return this.
     */
    ClcGeneratorBuilder pad(boolean pad);

    /**
     * Specify whether to insert defaults from the non-empty values of
     * properties that have been read.
     *
     * @param insertDefaults {@code true} to insert defaults for non-mepty
     * values; {@code false} otherwise.
     *
     * @return this.
     */
    ClcGeneratorBuilder insertDefaults(boolean insertDefaults);

    /**
     * Override properties with the given configuration.
     *
     * @param clcOverrides Configuration containing overrides for properties;
     * may be {@code null}.
     *
     * @return this.
     */
    ClcGeneratorBuilder clcOverrides(Configuration clcOverrides);

    /**
     * Use the specified property name filter to include or exclude properties
     * when examining property keys.
     *
     * @param propertyFilter property filter to use; may be {@code null}.
     *
     * @return this.
     */
    ClcGeneratorBuilder propertyNameFilter(PropertyNameFilter propertyFilter);

    /**
     * Use the specified type inference configuration when determining property
     * types.
     *
     * @param typeInferralConfig type inference configuration to use; may be
     * {@code null}.
     *
     * @return this.
     */
    ClcGeneratorBuilder typeInferralConfig(TypeInferralConfig typeInferralConfig);

    /**
     * Set the given property version as the global property version to use when
     * encountering properties.
     *
     * @param propertyVersion property version to use; may be {@code null}.
     *
     * @return this.
     */
    ClcGeneratorBuilder propertyVersion(String propertyVersion);
}
