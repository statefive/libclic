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

import java.lang.reflect.InvocationTargetException;

/**
 * Builder for value types.
 *
 * @param <T> the value type.
 */
public class ValueTypeBuilder<T extends ValueType> {

    /**
     * Class for the value type to create new instances for.
     */
    private final Class<T> t;

    /**
     * Create a new value type builder.
     *
     * @param t non-{@code null} type to create new instance for.
     */
    public ValueTypeBuilder(Class<T> t) {
        this.t = t;
    }

    /**
     * Create a new value type instance.
     *
     * @return the new value type instance.
     *
     * @throws ValueTypeCreationException if there is a problem creating the
     * value type that is required to have a no-arguments constructor.
     */
    public T build() throws ValueTypeCreationException {
        try {
            return t.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException
                | NoSuchMethodException | InvocationTargetException ex) {
            throw new ValueTypeCreationException(ex.getMessage(), ex);
        }
    }
}
