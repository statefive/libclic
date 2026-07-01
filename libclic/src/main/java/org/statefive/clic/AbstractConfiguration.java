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
package org.statefive.clic;

import org.statefive.clic.valuetype.ValueType;

/**
 * Base class for all configuration types.
 *
 * @author rich
 */
public abstract class AbstractConfiguration {

    /**
     * The name of the property; this is the name as it appears in the
     * configuration after the {@code option.} declaration.
     */
    private String name;

    /**
     * The 'type' (string, integer, file etc.) that this configuration
     * represents. If not set, it is implicit that the type is of type
     * {@code java.lang.String}. This will be converted into an appropriate
     * {@link ValueType}.
     */
    protected String type;

    /**
     * Properties declaration of the configuration; may be {@code null}. This
     * will be a comma separated list of {@code prop=value} values.
     */
    protected String typeProperties;

    /**
     * The value type is a data type that represents this configuration.
     */
    protected ValueType valueType;

    /**
     * The argument name (if required).
     */
    private String argName;

    /**
     * The first line number where the specified configuration appeared in a
     * given file or stream.
     */
    private int lineNumberStart;

    /**
     * The last line number where the specified configuration appeared in a
     * given file or stream.
     */
    private int lineNumberEnd;

    /**
     * Get the name.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name.
     *
     * @param name the name.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Get the type of the configuration.
     *
     * @return the configuration type.
     */
    public String getType() {
        return type;
    }

    /**
     * Set the type of the configuration.
     *
     * @param type the configuration type.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Get the type properties used to construct the type for the value.
     *
     * @return the type properties.
     */
    public String getTypeProperties() {
        return typeProperties;
    }

    /**
     * Set the type properties.
     *
     * @param typeProperties type properties for the configuration.
     */
    public void setTypeProperties(String typeProperties) {
        this.typeProperties = typeProperties;
    }

    /**
     * Get the value type for this configuration.
     *
     * @return the value type.
     */
    public ValueType getValueType() {
        return valueType;
    }

    /**
     * Set the value type for this configuration.
     *
     * @param valueType the configuration's value type.
     */
    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    /**
     * Get the argument name.
     *
     * @return the argument name.
     */
    public String getArgName() {
        return argName;
    }

    /**
     * Set the argument name.
     *
     * @param argName the argument name.
     */
    public void setArgName(final String argName) {
        this.argName = argName;
    }

    /**
     * Get the first line number where this configuration appeared in a given
     * file or stream.
     *
     * @return the line number.
     */
    public int getLineNumberStart() {
        return lineNumberStart;
    }

    /**
     * Set the line number where this configuration was first defined.
     *
     * @param lineNumberStart the line number.
     */
    public void setLineNumberStart(int lineNumberStart) {
        this.lineNumberStart = lineNumberStart;
    }

    /**
     * Get the last line number where this configuration appeared in a given
     * file or stream.
     *
     * @return the line number.
     */
    public int getLineNumberEnd() {
        return lineNumberEnd;
    }
    
    /**
     * Set the line number where this configuration was last defined.
     * 
     * @param lineNumberEnd the line number.
     */
    public void setLineNumberEnd(int lineNumberEnd) {
        this.lineNumberEnd = lineNumberEnd;
    }

}
