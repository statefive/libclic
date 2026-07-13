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
import org.statefive.clic.ClcException;
import org.statefive.clic.GlobalConfiguration;
import org.statefive.clic.OptionConfiguration;
import org.statefive.clic.valuetype.ValueTypeCreationException;
import org.statefive.clic.valuetype.ValueType;

/**
 * Generate an underlying stream in <i>command line configuration</i> (CLC)
 * format from properties files.
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
 * <p>
 * Implementations can either create generators by setting fields on a per-field
 * basis, or implement an associated {@link ClcGeneratorBuilder}.
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
     * Generate a CLC stream from the underlying properties.
     *
     * @return non-{@code null} valid CLC stream with properties converted to
     * valid format CLC.
     *
     * @throws ClcException
     *
     * @throws IOException if the properties cannot be read.
     *
     * @since 1.1
     */
    ByteArrayOutputStream generateConfiguration() throws ClcException, IOException;

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
     * @throws ClcException
     *
     * @throws IOException if there is a problem reading the properties.
     *
     * @deprecated use {@link #generateConfiguration()}; deprecated since 1.1.
     */
    ByteArrayOutputStream generateConfiguration(P properties, Configuration config,
            PropertyNameFilter propertyFilter, boolean clcGlobalHeader,
            TypeInferralConfig typeInferralConfig, boolean pad,
            boolean insertDefaults) throws ClcException, IOException;

    /**
     * Add the given properties to this generator.
     *
     * @param properties non-{@code null} properties.
     *
     * @since 1.1
     */
    public void setProperties(P properties);

    /**
     * Pad the generated CLC format with HASH-commented-out (hash-prefixed)
     * CLC-based options that were <i>not</i> included in the generated output.
     *
     * @param pad {@code true} to pad CLC data with commented-out sections of
     * other options that are available; {@code false} to only output valid CLC
     * sections.
     *
     * @since 1.1
     */
    public void setPad(boolean pad);

    /**
     * For properties that are not the empty string (or {@code null}), ensure
     * the property is included as the default value when generating CLC data.
     *
     * @param insertDefaults {@code true} to insert defaults; {@code false}
     * otherwise.
     *
     * @since 1.1
     */
    public void setInsertDefault(boolean insertDefaults);

    /**
     * Set the global header for the section of generated CLC content; this
     * could include global help and version information.
     *
     * @param header {@code true} to include global header data; {@code false}
     * otherwise.
     *
     * @since 1.1
     */
    public void setHeader(boolean header);

    /**
     * Set the version as the given property value text. If {@code null}, do not
     * include versioning information. If supplied the property will be
     * available as an option to print the application version once converted to
     * the appropriate command line switch.
     *
     * @param propertyVersion non-{@code null} property version text; will be
     * used as the version supplied by the global version; may be {@code null},
     * in which case no property version will be used.
     *
     * @since 1.1
     */
    public void setPropertyVersion(String propertyVersion);

    /**
     * Add the specified CLC overrides file. Any properties encountered that are
     * present in the overrides file will use the value in the specified
     * configuration.
     *
     * @param clcOverrides Overrides configuration data; may be {@code null}.
     * The keys must be valid CLC key definitions and the values of the keys
     * must be valid for the properties they are defining.
     *
     * @since 1.1
     */
    public void setClcOverrides(Configuration clcOverrides);

    /**
     * Use the specified property filter to filter for either including or
     * excluding given properties.
     *
     * @param propertyNameFilter property filter; may be {@code null}, whereby
     * all properties will be included.
     *
     * @since 1.1
     */
    public void setPropertyNameFilter(PropertyNameFilter propertyNameFilter);

    /**
     * Set the given type inference configuration.
     *
     * @param typeInferralConfig type inference configuration; may be
     * {@code null}, in which case all properties will be treated as strings.
     *
     * @since 1.1
     */
    public void setTypeInferralConfig(TypeInferralConfig typeInferralConfig);

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
