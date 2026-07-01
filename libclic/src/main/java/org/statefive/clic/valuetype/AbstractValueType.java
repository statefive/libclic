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
package org.statefive.clic.valuetype;

/**
 * Base class for value types.
 *
 * @author rich
 *
 * @param <T> data type to convert to.
 */
public abstract class AbstractValueType<T, R> implements ValueType<T, R> {
    
    /**
     * Package name; may be {@code null}.
     */
    private String packageName;

    /**
     * non-{@code null} Java class name. For value types representing a
     * primitive type this will be the class-name equivalent.
     */
    private String javaClassName;

    /**
     * Java primitive name; {@code null} if not a primitive.
     */
    private String javaPrimitiveName;

    /**
     * Value type name.
     */
    private String valueTypeName;

    /**
     * Render the given type based on it's {@code toString()} method.
     * 
     * @param renderable non-{@code null} renderable type.
     * 
     * @return non-{@code null} string value.
     */
    @Override
    public String render(R renderable) {
        return renderable.toString();
    }

    /**
     * Package name of the value type, if there is one. Value types that are
     * Java classes and are set in the default package and types representing a
     * primitive type should return {@code null}.
     *
     * @return Valid package name if not the default package or a Java primitive
     * type, {@code null} otherwise.
     */
    @Override
    public String getPackageName() {
        return packageName;
    }

    /**
     * Class name of the underlying Java class represented by this value type.
     *
     * @return non-{@code null} type name.
     */
    @Override
    public String getJavaClassName() {
        return javaClassName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavaPrimitiveName() {
        return javaPrimitiveName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValueTypeName() {
        return valueTypeName;
    }

    /**
     * Set the package name.
     *
     * @param packageName package name; may be {@code null}.
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * {@inheritDoc}
     */
    public void setJavaClassName(String javaClassName) {
        this.javaClassName = javaClassName;
    }

    /**
     * {@inheritDoc}
     */
    public void setJavaPrimitiveName(String javaPrimitiveName) {
        this.javaPrimitiveName = javaPrimitiveName;
    }

    /**
     * {@inheritDoc}
     */
    public void setValueTypeName(String valueTypeName) {
        this.valueTypeName = valueTypeName;
    }
    
}
