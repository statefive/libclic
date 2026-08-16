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

import org.statefive.clic.OptionListener;

/**
 * Test implementation listener for custom help-based tests.
 *
 * @author irch
 */
public class HelpImplementationListener implements OptionListener {

    /**
     * Output to standard output the result of any 'help' option.
     * 
     * @param option non-{@code null} option; if it matches the string 'help',
     * the values will be checked and output generated.
     * 
     * @param value {@code null} for unary help switches, non-{@code null} for
     * topic-based help.
     */
    @Override
    public void option(String option, Object value) {
        if ("help".equals(option)) {
            if (value == null) {
                System.out.println("Use the internet to find the answers you are looking for.");
            } else {
                // topic-based help
                switch (value.toString()) {
                    case "foo":
                        System.out.println("Important information about topic 'foo'");
                        break;
                    case "bar":
                        System.out.println("Important information about topic 'foo'");
                        break;
                    default:
                        System.out.println("Unknown topic: " + value.toString());
                }
            }
        }
    }

}
