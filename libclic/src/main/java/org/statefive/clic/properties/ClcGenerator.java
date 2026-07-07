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
import java.util.Map;
import org.apache.commons.configuration2.Configuration;
import org.statefive.clic.Clc;
import org.statefive.clic.GlobalConfiguration;
import org.statefive.clic.OptionConfiguration;
import org.statefive.clic.valuetype.ValueTypeCreationException;
import org.statefive.clic.valuetype.ValueType;

/**
 * Generate an underlying stream in <i>command line configuration</i> (CLC)
 * format.
 *
 * <p>
 * Implementations must provide the ability to take the given properties and
 * generate a valid command line configuration stream; an overview of CLC format
 * is described in {@link Clc} with global options detailed in
 * {@link GlobalConfiguration} and (standard) options detailed in
 * {@link OptionConfiguration}.
 *
 * <p>
 * Implementations are required to only provide the minimal amount of CLC format
 * properties required to enable mapping command line arguments to underlying
 * properties.
 *
 * @author rich
 *
 * @see Clc
 *
 * @see GlobalConfiguration
 *
 * @see OptionConfiguration
 *
 * @param <P> Property implementation.
 */
public interface ClcGenerator<P> {

    /**
     * Create a CLC stream based on the given properties and configuration; the
     * configuration provides the ability to override the default generated CLC
     * configuration.
     *
     * @param properties non-{@code null} properties.
     *
     * @param config configuration properties to override underlying CLC values;
     * may be {@code null}.
     *
     * @param propertyFilter filter to apply to include or exclude properties
     * when displaying them via command line help; may be {@code null}.
     *
     * @param clcGlobalHeader {@code true} to generated top-level/global CLC
     * header (including help) (as well as the options read in), {@code false}
     * to generate the option configuration only.
     *
     * @param typeInferralConfig non-{@code null} type inference configuration.
     *
     * @param pad {@code true} to add hash-commented sections to each defined
     * option configuration for all properties that have not been defined.
     *
     * @param insertDefaults {@code true} to insert a default value for any
     * property that is not the empty string.
     *
     * @return non-{@code null} valid CLC stream.
     *
     * @throws IOException if there is a problem reading the properties.
     */
    ByteArrayOutputStream generateConfiguration(P properties, Configuration config,
            PropertyNameFilter propertyFilter, boolean clcGlobalHeader,
            TypeInferralConfig typeInferralConfig, boolean pad,
            boolean insertDefaults) throws IOException;

    /**
     * Create a CLC stream based on the given properties and configuration; the
     * configuration provides the ability to override the default generated CLC
     * configuration.
     *
     * @param properties non-{@code null} properties.
     *
     * @param config configuration properties to override underlying CLC values;
     * may be {@code null}.
     *
     * @param propertyFilter filter to apply to include or exclude properties
     * when displaying them via command line help; may be {@code null}.
     *
     * @param clcGlobalHeader {@code true} to generated top-level/global CLC
     * header (including help) (as well as the options read in), {@code false}
     * to generate the option configuration only.
     *
     * @param typeInferralConfig non-{@code null} type inference configuration.
     *
     * @param pad {@code true} to add hash-commented sections to each defined
     * option configuration for all properties that have not been defined.
     *
     * @param insertDefaults {@code true} to insert a default value for any
     * property that is not the empty string.
     *
     * @param propertyVersion property version to use for versioning; may be
     * {@code null}, in which case versioning will not be added to the
     * application. If specified, if the specified property does not exist, the
     * application manifest implementation version will be used as the output
     * version for the application, once the property has been converted to the
     * appropriate command line switch; otherwise the value specified by the
     * property will be used as the version output when the version switch is
     * invoked. If the given property key and manifest implementation version is
     * not present, an error will be thrown.
     *
     * @return non-{@code null} valid CLC stream.
     *
     * @throws IOException if there is a problem reading the properties.
     *
     * @since 1.1
     */
    ByteArrayOutputStream generateConfiguration(P properties, Configuration config,
            PropertyNameFilter propertyFilter, boolean clcGlobalHeader,
            TypeInferralConfig typeInferralConfig, boolean pad,
            boolean insertDefaults, String propertyVersion) throws IOException;

    /**
     * Get the map of command line keys mapped to the underlying property keys.
     *
     * @return non-{@code null} map of property mappings.
     */
    Map<String, String> getPropertyMappings();

    /**
     * Get the CLC mappings from the configuration properties (if supplied).
     *
     * @return non-empty map of configuration mappings, if any were supplied;
     * the empty map otherwise.
     */
    Map<String, String> getClcMappings();

    /**
     * Get the map of property keys mapped to the underlying property value
     * types.
     *
     * @return non-{@code null} map of property mappings, if there are any;
     * empty otherwise.
     */
    Map<String, ValueType> getPropertyValueTypes();

    /**
     * Get the value type for the specified property name, if there is one; lack
     * of an associated value type means the underlying property is a string and
     * was not coerced to a different type.
     *
     * @param propertyName non-{@code null} property name from the underlying
     * property implementation (not the option name of the property name).
     *
     * @param value property value.
     *
     * @return the value type if there is one; {@code null} otherwise.
     *
     * @throws ValueTypeCreationException if the value type does not exist.
     */
    ValueType getPropertyValueType(String propertyName, Object value)
            throws ValueTypeCreationException;
}
