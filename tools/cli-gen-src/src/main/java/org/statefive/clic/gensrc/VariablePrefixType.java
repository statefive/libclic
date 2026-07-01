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
package org.statefive.clic.gensrc;

import org.statefive.clic.valuetype.AbstractValueType;
import org.statefive.clic.valuetype.ValueTypeCreationException;

/**
 * Value type for variable prefixes.
 *
 * <p>
 * Source code generation prefixes different types of variables so that they
 * don't clash when generating variable and method names. Default prefixes are
 * determined by the application {@code CLC} file within the appropriate
 * {@code default} definition for variable prefixes. The following prefixes are
 * supported:
 *
 * <p>
 * <ul>
 * <li>{@code args} : All generated variable names for top-level argument
 * configurations will contain the defined prefix;
 * </li>
 * <li>{@code command} : All generated variable names for command variables will
 * contain the defined prefix; and</li>
 * <li>{@code command-args}: All variables for command argument configurations
 * will contain the defined prefix.</li>
 * </ul>
 *
 * <p>
 * The values of the prefixes will be converted from strings with any
 * underscores used to denote camel case conversion; for example, specifying the
 *
 *
 * @author irch
 */
public class VariablePrefixType extends AbstractValueType<VariablePrefixes, String> {
    
    /**
     * Prefixes type name.
     */
    public static final String VARIABLE_PREFIXES_TYPE_NAME = "variable-prefixes";

    /**
     * Variable prefixes.
     */
    private final VariablePrefixes variablePrefixes = new VariablePrefixes();

    /**
     * Create a new variable prefix type.
     */
    public VariablePrefixType() {
        setPackageName(VariablePrefixes.class.getPackageName());
        setJavaClassName(VariablePrefixes.class.getSimpleName());
        setJavaPrimitiveName(null);
    }

    /**
     * Get the variable prefixes from the given data.
     *
     * @param data non-{@code null} data to parse; must contain valid variable
     * prefix definitions.
     *
     * @return non-{@code null} variable prefixes if they could be parsed.
     *
     * @throws ValueTypeCreationException if the data is invalid.
     */
    @Override
    public VariablePrefixes getValue(String data) throws ValueTypeCreationException {
        String[] prefixes = data.split(",");
        for (String prefix : prefixes) {
            String[] parts = prefix.split("=");
            if (parts.length != 2) {
                throw new ValueTypeCreationException("Expected pair of values"
                        + " separated by '=', found: " + prefix);
            }
            String prefixPart = parts[0].trim();
            String prefixValue = parts[1].trim();
            switch (prefixPart) {
                case "args":
                    variablePrefixes.setArgsPrefix(prefixValue);
                    break;
                case "command":
                    variablePrefixes.setCommandPrefix(prefixValue);
                    break;
                case "command-args":
                    variablePrefixes.setCommandArgsPrefix(prefixValue);
                    break;
                default:
                    throw new ValueTypeCreationException("Unknown variable"
                            + " prefix: " + prefixPart + ". Valid values are"
                            + " 'args', 'command' or 'command-args'.");
            }
        }
        return variablePrefixes;
    }

    /**
     * Set the default data.
     *
     * @param data non-{@code null} data.
     *
     * @throws ValueTypeCreationException if the data is invalid.
     */
    @Override
    public void setDefault(String data) throws ValueTypeCreationException {
        getValue(data);
    }

    /**
     *
     *
     * @param properties
     * @throws ValueTypeCreationException
     */
    @Override
    public void setProperties(String properties) throws ValueTypeCreationException {
        throw new UnsupportedOperationException("Vairable prefixes do not"
                + " support properties.");
    }

    /**
     *
     * @return
     */
    @Override
    public String getPackageName() {
        return VariablePrefixes.class.getPackageName();
    }

    /**
     *
     * @return
     */
    @Override
    public String getJavaClassName() {
        return VariablePrefixes.class.getSimpleName();
    }

    /**
     *
     * @return
     */
    @Override
    public String getJavaPrimitiveName() {
        return null;
    }

    /**
     *
     * @return
     */
    @Override
    public String getValueTypeName() {
        return "variable-prefixes";
    }

}
