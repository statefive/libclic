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

/**
 * Configuration for inferring types. This can (but doesn't need to) be used by
 * implementations that do not automatically convert underlying properties into
 * a specific value type.
 *
 * <p>
 * Implementations do not need to consider using this class if they already
 * provide out-of-the-box configuration of underlying values of types. In such a
 * case, it is up to implementations on how to deal with
 * {@link PropertiesBuilder#withTypeInferralConfig(org.statefive.cli.properties.TypeInferralConfig)}
 * calls; the option being to fail gracefully if type inference is not supported
 * or if to throw a run-time exception.
 *
 * @author rich
 */
public class TypeInferralConfig {

    /**
     * {@code true} to infer underlying property value types, {@code false}
     * otherwise.
     */
    private boolean inferTypes;

    /**
     * If {@code true}, treat all option configurations that have {@code hasArg}
     * set to {@code false} as unary switches, not binary {@code x = y}
     * switches.
     */
    private boolean falseAsUnarySwitch;

    /**
     * Value type to assign natural numbers to.
     */
    private String naturalNumbersAs;

    /**
     * Value type to assign real numbers to.
     */
    private String realNumbersAs;

    /**
     * Determine if inference of types is supported.
     *
     * @return {@code true} if type inference is supported; {@code false}
     * otherwise.
     */
    public boolean isInferTypes() {
        return inferTypes;
    }

    /**
     * Set if type inference is supported.
     *
     * @param inferTypes {@code true} to infer types, {@code false} otherwise.
     */
    public void setInferTypes(boolean inferTypes) {
        this.inferTypes = inferTypes;
    }

    /**
     * Get the type to set natural numbers to.
     *
     * @return the type to set natural numbers to; may be {@code null}.
     */
    public String getNaturalNumbersAs() {
        return naturalNumbersAs;
    }

    /**
     * Set the value type name to convert natural numbers to.
     *
     * @param naturalNumbersAs type name to set when converting natural numbers;
     * may be {@code null}.
     */
    public void setNaturalNumbersAs(String naturalNumbersAs) {
        this.naturalNumbersAs = naturalNumbersAs;
    }

    /**
     * Get the type to set real numbers to.
     *
     * @return the type to set real numbers to; may be {@code null}.
     */
    public String getRealNumbersAs() {
        return realNumbersAs;
    }

    /**
     * Set the value type name to convert real numbers to.
     *
     * @param realNumbersAs type name to set when converting real numbers; may
     * be {@code null}.
     */
    public void setRealNumbersAs(String realNumbersAs) {
        this.realNumbersAs = realNumbersAs;
    }

    /**
     * Determine if treating properties that are {@code false} and have
     * {@code hasArg} set to false as unary switches.
     *
     * @return {@code true} to treat properties that are {@code false} as unary
     * switches; {@code false} otherwise.
     */
    public boolean isFalseAsUnarySwitch() {
        return falseAsUnarySwitch;
    }

    /**
     * Set if the configuration should treat properties that are {@code false}
     * as unary switches.
     *
     * @param falseAsUnarySwitch {@code true} to treat properties as unary
     * switches; {@code false} otherwise.
     */
    public void setFalseAsUnarySwitch(boolean falseAsUnarySwitch) {
        this.falseAsUnarySwitch = falseAsUnarySwitch;
    }

}
