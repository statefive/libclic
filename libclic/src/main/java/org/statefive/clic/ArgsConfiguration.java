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
 * Arguments that are processed once all defined command line switches have been
 * processed (and prior to which, if defined, all prefix arguments have been
 * processed). Arguments can comprise of fixed length or any number of
 * arguments, rules for arguments permitting. It is also possible for API
 * callers to limit or set the number of arguments to zero/unused if necessary
 * (for example, some applications may not take any arguments at all), thereby
 * capping the number of arguments that can be processed.
 *
 * <p>
 * By default, if no explicit configuration for arguments is defined, all
 * applications can have any number of arguments that will all be strings.
 *
 * <p>
 * Argument configurations can be applied to both standard (non-command)
 * configurations as well as be applied to {@link Command}s. In the latter case
 * the argument configurations must be defined after the command has been
 * defined in the CLC file. Commands may contain arguments only if the given
 * command does not contain any child commands.
 *
 * <p>
 * The rules for defining argument configurations are described as follows (and
 * also follow the same rules for {@link AbstractArgsConfiguration}s):
 *
 * <p>
 * <ul>
 * <li>Any number of argument configurations can be defined so long as the
 * previously defined configuration length is 1 or greater (if present);</li>
 * <li>Defining an argument as having zero length means no more argument
 * definitions can succeed the currently defined configuration; if the first
 * argument configuration is specified as zero length then the application will
 * error if any more arguments are supplied once all command line arguments are
 * processed. Furthermore, if an argument is defined as having zero length then
 * it can have no associated type, type properties or argument name;</li>
 * <li>So long as the argument configuration is not capped at zero, the
 * configuration can be of any type except {@link ListType}. List types may be
 * supported in a future release;</li>
 * <li>Defining an argument with {@code null} length implies that the argument
 * length is unbounded and can contain any number of arguments of the given
 * type. Once an argument is defined as unbounded length then no more argument
 * configurations may subsequently be defined. Such a configuration must contain
 * at least 1 value in the processed arguments (unless declared optional,
 * below); and</li>
 * <li>The last defined argument configuration can be declared as optional, in
 * which case the last argument or arguments for the defined argument
 * configuration do not need to be present.</li>
 * </ul>
 *
 * @author rich
 */
public class ArgsConfiguration extends AbstractArgsConfiguration {

    /**
     * Length of the argument configuration if present.
     */
    private Integer length;

    /**
     * Used to determine that the last set of arguments is optional.
     */
    private Boolean optional = null;

    /**
     * Get the length of the configuration.
     *
     * @return length; positive (non-zero) integer if fixed length, {@code null}
     * to signify unbounded length, zero to signify that no more configurations
     * can be defined (i.e. to 'cap' the number of arguments).
     */
    @Override
    public Integer getLength() {
        return length;
    }

    /**
     * Set the length of this configuration.
     *
     * @param length {@code null} to define a configuration with unbounded
     * number of arguments, 0 to prevent any further configurations being
     * defined, or a positive number to set a fixed length configuration.
     *
     * @throws ClcException if the length is non-{@code null} and less than
     * zero.
     */
    @Override
    public void setLength(Integer length) throws ClcException {
        if (length != null && length < 0) {
            throw new ClcException("Invalid length: " + length
                    + "; minimum length is 0.");
        }
        this.length = length;
    }

    /**
     * Determine if this configuration is optional.
     *
     * @return {@code true} if the argument configuration is optional;
     * {@code false} otherwise.
     */
    public boolean isOptional() {
        return optional != null && optional;
    }

    /**
     * Determine if this configuration is optional.
     *
     * @return {@code true} if the argument configuration is optional;
     * {@code false} if the argument configuration is non-optional, {@code null}
     * if not set.
     */
    public Boolean getOptional() {
        return optional;
    }

    /**
     * Set whether this argument is optional.
     *
     * @param optional {@code true} to set the argument configuration as an
     * optional argument; {@code false} to set the argument configuration as
     * required.
     */
    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    /**
     * Determine if this configuration has an unbounded length of arguments,
     * rather than a fixed length.
     *
     * @return {@code true} if the configuration can have any number of
     * arguments (even if optional); {@code false} otherwise.
     */
    public boolean isUnbounded() {
        return getLength() == null;
    }

    /**
     * Determine if this configuration is capped at zero.
     *
     * @return {@code true} if the configuration length is zero; {@code false}
     * otherwise.
     */
    public boolean isCappedAtZero() {
        return getLength() != null && getLength() == 0;
    }

}
