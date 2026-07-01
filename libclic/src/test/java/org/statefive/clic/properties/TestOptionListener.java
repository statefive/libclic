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

import java.util.HashMap;
import java.util.Map;
import org.statefive.clic.CommandOptionListener;
import org.statefive.clic.OptionListener;

/**
 * Test option listener.
 *
 * @author rich
 */
public class TestOptionListener implements OptionListener, CommandOptionListener {

    /**
     * Map of received command line options. Used to determine that a filter has
     * been applied properly.
     */
    private Map<String, Object> options = new HashMap<>();

    /**
     * Get the map of received options mapped by their option name to the value
     * of the command line option.
     *
     * @return non-{@code null} map of options.
     */
    public Map<String, Object> getRecievedOptions() {
        return options;
    }

    /**
     * Option update.
     *
     * @param option non-{@code null} option value; this will be the command
     * key.
     *
     * @param value value of the option.
     */
    @Override
    public void option(String option, Object value) {
        options.put(option, value);
    }

    /**
     * Unused.
     *
     * @param command unused.
     *
     * @param option unused.
     *
     * @param value unused.
     *
     * @throws UnsupportedOperationException every time.
     */
    @Override
    public void option(String command, String option, Object value) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
