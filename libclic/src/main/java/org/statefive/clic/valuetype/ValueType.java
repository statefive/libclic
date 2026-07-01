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

/**
 * High level interface for transforming command line arguments into a specific
 * object type. Implementations must have a no-arguments constructor publicly
 * available.
 *
 * <p>
 * The implementations of {@link #getJavaClassName()},
 * {@link #getJavaPrimitiveName()} and {@link #getPackageName()} are cosmetic
 * functions used in source generation and represent the values that would be
 * used by classes that implement any of the option listeners when using a
 * non-properties, CLC-only based approach.
 *
 * @param <T> data type to convert to.
 * 
 * @param <R> renderable object.
 */
public interface ValueType<T, R> {

    /**
     * Given the specified data (typically passed in via data from the command
     * line), attempt to convert to the appropriate type.
     *
     * @param data data to construct the type from; may be {@code null}.
     *
     * @return the type, if it could be created.
     *
     * @throws ValueTypeCreationException if the type could not be created.
     */
    T getValue(String data) throws ValueTypeCreationException;

    /**
     * Set the default value; this will be used when the option is given a
     * default value but the user doesn't supply a value via the command line;
     * note that the implementors should construct the value according to the
     * constraints passed in by any properties set against the type; for
     * example, if an {@code int} type has a minimum of 0 and a maximum of 10
     * set for its properties, the default value should conform to this and
     * throw an exception if it does not comply.
     *
     * @param data default data to construct the type from.
     *
     * @throws ValueTypeCreationException if there is a problem constructing the
     * default value against any property constraints place on the underlying
     * type; implements that do not deem the type can have default data (like
     * {@link DataFileType} should throw an exception stating the fact that
     * default data is not supported.
     */
    void setDefault(String data) throws ValueTypeCreationException;

    /**
     * Set any properties for the type.
     *
     * @param properties properties as a raw string - it is up to implementors
     * on how to parse this information.
     *
     * @throws ValueTypeCreationException if any of the properties are invalid.
     */
    void setProperties(String properties) throws ValueTypeCreationException;

    /**
     * Render the value based on the given type.
     *
     * @param renderable non-{@code null} renderable implementation.
     *
     * @return non-{@code null} string.
     */
    String render(R renderable);

    /**
     * Get the package name of the type to convert to; classes using the default
     * package and primitive types should return {@code null}.
     *
     * @return the package name if there is one; {@code null} otherwise.
     */
    public String getPackageName();

    /**
     * Get the underlying Java class name.
     *
     * @return non-{@code null} Java class name.
     */
    public String getJavaClassName();

    /**
     * Get the Java primitive name.
     *
     * @return the Java primitive name if this value type is a Java primitive;
     * {@code null} otherwise.
     */
    public String getJavaPrimitiveName();

    /**
     * Get the value type name; this is the type name exposed via the command
     * line configuration API for conversion into underlying Java types and set
     * via {@code option.<option-name>.type=<type-name>}.
     *
     * @return non-{@code null} value type name.
     */
    public String getValueTypeName();
}
