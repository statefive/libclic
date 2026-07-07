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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.statefive.clic.ClcException;
import org.statefive.clic.GlobalConfiguration;
import org.statefive.clic.OptionListener;
import org.statefive.clic.valuetype.ValueType;

/**
 * Builder to take any number of properties from any number of sources and
 * either override properties entered on the command line or generate a CLC
 * configuration from the properties.
 *
 * @author rich
 *
 * @param <P> Properties implementation.
 */
public interface PropertiesBuilder<P> extends OptionListener {

    /**
     * Add the specified properties file.
     *
     * @param source non-{@code null} properties source.
     *
     * @return this.
     */
    PropertiesBuilder addPropertiesSource(PropertiesSource source);

    /**
     * Add the specified command line configuration (CLC) file.
     *
     * @param configuration non-{@code null} existing CLC format file.
     *
     * @return this.
     *
     * @throws IOException if the file cannot be read or doesn't exist.
     *
     * @throws IllegalArgumentException if the configuration has already been
     * added.
     */
    PropertiesBuilder withClc(File configuration) throws IOException;

    /**
     * Add the specified command line configuration (CLC) input stream. Data
     * from the stream will be added to the final generated content.
     *
     * @param is non-{@code null} CLC format input stream.
     *
     * @return this.
     *
     * @throws IllegalArgumentException if the configuration has already been
     * added.
     */
    PropertiesBuilder withClc(InputStream is);

    /**
     * Add the specified filter.
     *
     * @param filter non-{@code null} filter.
     *
     * @return this.
     */
    PropertiesBuilder withPropertyNameFilter(PropertyNameFilter filter);

    /**
     * Implementations are expected to throw an error if duplicate properties
     * exist. Implementations may vary with regard to what happens when
     * properties are duplicated; some implementations may throw an error,
     * overwrite the original property of the same name, or merge the two
     * properties (among other possibilities).
     *
     * @param allowDuplicates {@code true} to allow duplicates; {@code false}
     * otherwise.
     *
     * @return this.
     */
    PropertiesBuilder allowDuplicates(boolean allowDuplicates);

    /**
     * When generating the CLC (command line configuration) data, prepend each
     * block of options with a comment prefixed showing the full path to where
     * the file was imported from.
     *
     * @param isShowImportOrigin {@code true} to include comment with original
     * file name before each block of imported properties.
     *
     * @return this.
     */
    PropertiesBuilder showImportOrigin(boolean isShowImportOrigin);

    /**
     * Set whether to pad the configuration with comment-prefixed entries for
     * each configuration; only those entries that are not defined for each
     * configuration will be added.
     *
     * @param pad {@code true} to pad entries, {@code false} otherwise.
     *
     * @return this.
     */
    PropertiesBuilder pad(boolean pad);

    /**
     * Set whether to insert the default value of an option configuration for a
     * given property, if the property is not the empty string.
     *
     * @param insertDefaults {@code true} to insert default values,
     * {@code false} otherwise.
     *
     * @return this.
     */
    PropertiesBuilder insertDefaults(boolean insertDefaults);

    /**
     * Infer underlying property types into a given equivalent
     * {@link ValueType}.
     *
     * @param typeInferralConfig {@code true} to infer types; {@code false} not
     * infer types.
     *
     * @return this.
     */
    PropertiesBuilder withTypeInferralConfig(TypeInferralConfig typeInferralConfig);

    /**
     * Ensure that global version is added. If any properties contain the
     * property {@link GlobalConfiguration#GLOBAL_VERSION_OPTION_LONG_DEFAULT},
     * that property value will be used, otherwise the application manifest
     * implementation version will be used, an error being thrown otherwise. The
     * property will be added to the global property version and then the
     * property itself (if present in the properties) will be removed from the
     * underlying properties.
     *
     * @return this.
     *
     * @since 1.1
     */
    PropertiesBuilder withVersion();

    /**
     * Ensure that global version is added using the specified properties-based
     * version key; used when the property
     * {@link GlobalConfiguration#GLOBAL_VERSION_OPTION_LONG_DEFAULT} is already
     * taken for a different purpose. The property value will be used for the
     * version, if present, otherwise the manifest file implementation version
     * will be used, an error being thrown otherwise. The property will be added
     * to the global property version and then the property itself will be
     * removed from the underlying properties.
     *
     * @param propertyVersionKey property key to use to show the application
     * version; if not present in the properties, the API will fall back to
     * using the manifest application version.
     *
     * @return this.
     *
     * @since 1.1
     */
    PropertiesBuilder withVersion(String propertyVersionKey);

    /**
     * Build the properties against the given command line options. Properties
     * not overridden by command line arguments will have the same value as
     * defined in the file(s)/stream(s) the properties came from.
     *
     * @param args non-{@code null} command line arguments to override
     * properties; may be {@code null}.
     *
     * @return non-{@code null} properties implementation.
     *
     * @throws ClcException if any of the properties or configuration (if there
     * is one) are invalid, or if there are no properties.
     *
     * @throws PropertiesLoadException if any of the streams or files cannot be
     * read.
     *
     * @throws IOException if any of the streams or files cannot be read.
     */
    P build(String[] args) throws IOException, ClcException,
            PropertiesLoadException;

    /**
     * Given the specified build properties and configuration, get the
     * underlying CLC (command line configuration) format data.
     *
     * @return non-{@code null} CLC format data.
     *
     * @throws ClcException if any of the properties or configuration (if there
     * is one) are invalid, or if there are no properties.
     *
     * @throws IOException if any of the streams or files cannot be read.
     *
     * @throws PropertiesLoadException if any of the streams or files cannot be
     * read.
     */
    String buildConfigurationData() throws ClcException, IOException,
            PropertiesLoadException;

    /**
     * Set the given property on the underlying property implementation. It is
     * up to implementations to map the option name to the original underlying
     * property key in order to set the value.
     *
     * <p>
     * As command line arguments are processed the command line key is used to
     * get the property key of the underlying implementation, from which the
     * property value is set from the corresponding command-like value.
     *
     * @param optionName non-{@code null} option name read in from the command
     * line without any hyphens prefixed; this will be the original property
     * name with non-alphanumeric characters converted to hyphens. See
     * {@link AbstractPropertiesReader#convertToCommandKey(java.lang.String)}.
     *
     * @param propertyValue original property read in from the properties; may
     * be {@code null}. For properties that have {@code <arg>} values the value
     * will always be non-{@code null}. Otherwise, if the type inference
     * configured to treat {@link TypeInferralConfig#isFalseAsUnarySwitch()},
     * the value will be {@code null} and means implementations should set the
     * property to {@code true}.
     */
    void setProperty(String optionName, Object propertyValue);
}
