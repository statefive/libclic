/**
 * Copyright 2019 www.statefive.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.statefive.clic.valuetype;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Factory for creating new value types.
 */
public class ValueTypeFactory {

    /**
     * Singleton instance.
     */
    private static ValueTypeFactory instance;

    /**
     * Map of registered value types; the key of the map should be the
     * {@code type} specified in the configuration file.
     */
    private final Map<String, ValueTypeBuilder> registeredValueTypes = new HashMap<>();

    /**
     * Get the singleton instance.
     *
     * @return the singleton instance.
     */
    public static ValueTypeFactory getInstance() {
        if (instance == null) {
            instance = new ValueTypeFactory();
        }
        return instance;
    }

    /**
     * Singleton constructor.
     */
    private ValueTypeFactory() {

    }

    /**
     * Get all registered value types.
     *
     * @return all registered value types; the empty set if there are none.
     */
    public Set<String> getRegisteredValueTypes() {
        return registeredValueTypes.keySet();
    }

    /**
     * Get the value type with the given name.
     *
     * @return non-{@code null} value type, if it has been registered;
     * {@code null} otherwise.
     */
    public ValueType getRegisteredValueType(String typeName) {
        return registeredValueTypes.get(typeName).build();
    }

    /**
     * Determine if the given value type by name has already been registered.
     *
     * @param typeName non-{@code null} type name to check.
     *
     * @return {@code true} if the name has already been registered.
     */
    public boolean isRegistered(String typeName) {
        return registeredValueTypes.containsKey(typeName);
    }

    /**
     * Remove the specified type name.
     *
     * @param typeName non-{@code null}
     *
     * @return the builder for that type if it is removed; {@code null}
     * otherwise.
     */
    public ValueTypeBuilder removeRegisteredValueType(String typeName) {
        return registeredValueTypes.remove(typeName);
    }

    /**
     * Create a new value type based on the specified value type name. The value
     * type must already have been registered via
     * {@link #registerValueTypeBuilder(java.lang.String, org.statefive.cli.config.valuetype.ValueTypeBuilder)}.
     *
     * @param typeName non-{@code null} name of the value type.
     *
     * @return the value type, if a valid builder has been registered against
     * the specified name.
     *
     * @throws ValueTypeCreationException if the value type for the specified
     * name does not exist or the value type cannot be constructed.
     */
    public ValueType create(String typeName) throws ValueTypeCreationException {
        if (!registeredValueTypes.containsKey(typeName)) {
            throw new ValueTypeCreationException("Unknown value type: " + typeName);
        }
        ValueTypeBuilder vtb = registeredValueTypes.get(typeName);
        ValueType vt = vtb.build();
        return vt;
    }

    /**
     * Callers can register their own value type builders through this call.
     *
     * @param name non-{@code null} name of the builder name to register.
     *
     * @param type non-{@code null} value type builder.
     *
     * @return {@code null} if there was no previous key; the previous entered
     * value type builder otherwise.
     *
     * @throws ValueTypeCreationException if the value type of the given name if
     * already present (regardless of the type implementation).
     */
    public ValueTypeBuilder registerValueTypeBuilder(String name,
            ValueTypeBuilder type) {
        if (registeredValueTypes.containsKey(name)) {
            throw new ValueTypeCreationException("Value type " + name
                    + " has already been registered");
        }
        return this.registeredValueTypes.put(name, type);
    }
}
