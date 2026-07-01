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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.statefive.clic.ArgsListener;
import org.statefive.clic.Clc;
import org.statefive.clic.valuetype.DirUpdateListener;

/**
 * Convenience class for callers utilising properties.
 *
 * <p>
 * Once a {@link Clc} has been associated with it, it will
 * register itself as an {@link ArgsListener} and {@link DirUpdateListener} to
 * receive updates from the CLC.
 *
 * @author rich
 */
public class PropertiesListenerBindings implements ArgsListener {

    /**
     * Singleton instance.
     */
    private static PropertiesListenerBindings instance;

    /**
     * Get the instance; if {@code null}, the instance will be instantiated.
     *
     * @return non-{@code null} instance.
     */
    public static PropertiesListenerBindings getInstance() {
        Clc.initialiseValueTypeFactory();
        if (instance == null) {
            instance = new PropertiesListenerBindings();
        }
        return instance;
    }

    /**
     * Command line configuration.
     */
    private Clc clc;

    /**
     * List of non-switch arguments (if there are any).
     */
    private List<String> args = null;

    /**
     * Used to keep track of what arguments have been added.
     */
    private final Set<Integer> argsIndices = new HashSet<>();

    /**
     * List of arguments that were transformed into objects.
     */
    private final List<Object> argsValueTypes = new ArrayList<>();

    /**
     * Set the command line configuration.
     *
     * @param clc non-{@code null} command line configuration.
     */
    public void setClc(Clc clc) {
        this.clc = clc;
        clc.addArgsListener(this);
    }

    /**
     * Get the arguments.
     *
     * @return the arguments, if there are any and the command line
     * configuration has been loaded; {@code null} otherwise.
     */
    public List<String> getArgs() {
        return clc.getArgs();
    }

    /**
     * Get the arguments as objects; if no object conversion was performed via
     * the API, the argument value types will be the same as returned by
     * {@link #getArgs()}, that is, a list of Java strings.
     *
     * @return the arguments, if there are any; the empty list otherwise.
     */
    public List<Object> getArgsValueTypes() {
        return argsValueTypes;
    }

    /**
     * Add the specified directory update listener to the command line
     * configuration. If the configuration is not yet set the listeners will be
     * added when the configuration is set.
     *
     * @param dirUpdateListener non-{@code null} directory update listener to
     * add.
     *
     * @return {@code true} if the listener was added; {@code false} otherwise.
     */
    public boolean addDirUpdateListener(DirUpdateListener dirUpdateListener) {
        return clc.addDirUpdateListener(dirUpdateListener);
    }

    /**
     * Update the list of arguments and arguments as objects with the given
     * value.
     *
     * @param name non-{@code null} name of the argument; this will be the
     * command line switch name without any preceding hyphens.
     *
     * @param index the position of the argument in the list of provided
     * arguments.
     *
     * @param value non-{@code null} value of the argument.
     */
    @Override
    public void argument(String name, int index, Object value) {
        if (!argsIndices.contains(index)) {
            argsValueTypes.add(value);
            argsIndices.add(index);
        }
    }

}
