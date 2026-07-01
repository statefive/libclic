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

/**
 * Base class for prefix and standard argument configurations. For all
 * implementations, arguments are always parsed in the order they are
 * encountered on the command line and associated with the relevant
 * configuration associated with the specified argument.
 *
 * <p>
 * Prefix and standard arguments are similar to standard command line switches
 * in that they can comprise of:
 * <p>
 * <ul>
 * <li>A value type: arguments can be deemed to be of a specific type as managed
 * by the API, for example files, numbers, booleans etc.;</li>
 * <li>Type properties: Properties can be defined to limit or add checking to
 * arguments parsed for each argument; and</li>
 * <li>Argument name: Names can be applied to arguments to make arguments more
 * meaningful to users.</li>
 * </ul>
 *
 * <p>
 * In addition both prefix and standard arguments can also comprise of a
 * <i>length</i>, which is documented in the appropriate implementation.
 * Regardless of implementation, the length, when present, is used to denote how
 * many arguments occupy the argument configuration when read from the command
 * line.
 *
 * <p>
 * Rules permitting according to base class documentation, repeated blocks of
 * configurations can be defined, enabling an application to define any number
 * of different data types for any number of different arguments.
 *
 * @author rich
 */
public abstract class AbstractArgsConfiguration extends AbstractConfiguration {

    /**
     * Get the length of the argument configuration; refer to implementation
     * documentation.
     *
     * @return refer to implementation documentation for details on what value
     * is returned.
     */
    public abstract Integer getLength();

    /**
     * Set the length of the argument configuration; refer to implementation
     * documentation.
     *
     * @param length length to set; it is up to implementations to determine if
     * {@code null} values are permitted.
     *
     * @throws ClcException if the length is invalid.
     */
    public abstract void setLength(Integer length) throws ClcException;

}
