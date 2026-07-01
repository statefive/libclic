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

import org.statefive.clic.Clc;
import org.statefive.clic.valuetype.ValueType;

/**
 * Base class for property implementations.
 *
 * <p>
 * Properties are mapped as key-value pairs of the command line property name
 * against the actual property names read in. Command line property names are
 * created by taking the named underlying property read in and converting all
 * non-alphanumeric characters that are not hyphens with hyphens. For example,
 * for a property named {@code host_name.primary}, the command line property key
 * will be {@code host-name-primary}.
 *
 * @author rich
 *
 * @param <P> Property implementation.
 */
public abstract class AbstractPropertiesReader<P> implements PropertyReader<P> {

    /**
     * Configuration generator.
     */
    protected ClcGenerator configurationGenerator;

    /**
     * Regular expression NOT matching alphanumeric and hyphen characters, used
     * to replace non-matching characters with hyphens.
     */
    private static final String REGEX = "[^A-Za-z0-9\\-]";

    /**
     * Standard command-line separator.
     */
    private static final String WORD_SEPARATOR = "-";

    /**
     * Properties listener bindings.
     */
    protected PropertiesListenerBindings propsListenerBindings = PropertiesListenerBindings.getInstance();

    /**
     * Determines if to generate default/global and help data when creating the
     * configuration data.
     */
    protected boolean clcGlobalHeader = true;

    /**
     * Determine if to attempt to covert properties to a given
     * {@link ValueType}.
     */
    protected TypeInferralConfig typeInferralConfig = new TypeInferralConfig();

    /**
     * Command line configuration.
     */
    protected Clc cliconfig = Clc.getInstance();

    /**
     * Convert the given property to an appropriate command line switch without
     * the leading double-hyphen prefix. All non-alphanumeric characters that
     * are not hyphens are replaced with hyphens and the switch will not contain
     * the prefixed {@code --} prefix.
     *
     * @param propertyName non-{@code null} property name to convert.
     *
     * @return the converted property; if the property contains only
     * alphanumeric characters and hyphens the returned value will be the same
     * as the key.
     */
    public static String convertToOptionName(String propertyName) {
        return propertyName.replaceAll(REGEX, WORD_SEPARATOR);
    }

    /**
     * Constructor.
     */
    public AbstractPropertiesReader() {
        cliconfig.addArgsListener(propsListenerBindings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setConfigurationGenerator(ClcGenerator configurationGenerator) {
        this.configurationGenerator = configurationGenerator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTypeInferralConfig(TypeInferralConfig typeInferralConfig) {
        this.typeInferralConfig = typeInferralConfig;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setClcGlobalHeader(boolean clcGlobalHaeder) {
        this.clcGlobalHeader = clcGlobalHaeder;
    }
}
