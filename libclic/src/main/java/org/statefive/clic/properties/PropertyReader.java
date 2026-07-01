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

import org.statefive.clic.ClcException;

/**
 * Defines calls for property implementations for overriding properties using
 * command line arguments. It is up to implementations on how to map properties
 * to the command line arguments.
 * 
 * <p>
 * All property readers must be set up with a {@link ClcGenerator}; properties
 * will be converted from their underlying form to the common CLC format.
 *
 * @author rich
 *
 * @param <P> Properties implementation.
 */
public interface PropertyReader<P> {

    /**
     * Get the properties.
     *
     * @return non-{@code null} properties.
     */
    P getProperties();

    /**
     * Set the configuration generator.
     *
     * @param configurationGenerator non-{@code null} configuration generator.
     */
    void setConfigurationGenerator(ClcGenerator configurationGenerator);

    /**
     * Read the given input stream and replace all properties with the matching
     * arguments. A default command line help configuration will be generated
     * with each help option displaying what property any given command line
     * argument overrides along with the default value from the property stream.
     * <p>
     * Property argument values are transformed from their name, prefixed with
     * two hyphens and substituting all non alpha-numeric characters with the
     * hyphen character. For example, the property {@code host_name.primary}
     * would be represented by the argument {@code --host-name-primary}.
     *
     * @param source non-{@code null} input stream to read properties from.
     *
     * @return non-{@code null} properties.
     *
     * @throws ClcException if the arguments did not match any of the
     * properties or there were duplicate arguments.
     *
     * @throws PropertiesLoadException if there was a problem reading the
     * properties.
     */
    P read(PropertiesSource source) throws ClcException, PropertiesLoadException;

    /**
     * When reading properties, assign value types to properties read in where
     * they can be determined.
     *
     * <p>
     * Many property implementations accept strings as default values, while
     * some may offer out-of-the-box coercion of values read in into various
     * types such as integer, booleans, etc.
     *
     * <p>
     * It is up to implementations on whether to offer this; it is acceptable to
     * simply do nothing or to throw a run-time exception if the implementation
     * is not capable of converting underlying values.
     *
     * @param typeInferralConfig
     */
    void setTypeInferralConfig(TypeInferralConfig typeInferralConfig);

    /**
     * Set if a global header should be generated when generating the CLC
     * (command line configuration).
     *
     * @param clcGlobalHeader
     */
    void setClcGlobalHeader(boolean clcGlobalHeader);
}
