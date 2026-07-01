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
package com.typesafe.clic.properties;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigValueType;
import java.util.Map;

/**
 *
 * @author rich
 */
public class TypesafeHoconUtils {

    /**
     * Traverse the root configuration adding to the properties when basic
     * properties are encountered. Any configurations that are encountered that
     * are string, boolean or numerical will be added to the properties; the
     * property key will be the path from the root with '.' replacing braces
     * within the configuration.
     *
     * @param conf non-{@code null} configuration.
     *
     * @param properties non-{@code null} properties to add to.
     */
    public static void traverseFromRoot(Config conf, Map<String, Object> properties) {
        traverse(conf.root().toConfig(), properties, null);
    }

    /**
     * Recursively traverse the configuration, addin to the properties.
     *
     * @param conf non-{@code null} configuration to traverse.
     *
     * @param properties non-{@code null} properties to add to.
     *
     * @param name name to prefix onto encountered properties. Names will be
     * separated by '.'.
     */
    public static void traverse(Config conf, Map<String, Object> properties,
            String name) {
        String prefix = "";
        if (name != null) {
            prefix = name + ".";
        }
        for (String key : conf.root().keySet()) {
            conf = conf.resolve();
            if (conf.getValue(key).valueType() == ConfigValueType.STRING) {
                properties.put(prefix + key, conf.getString(key));
            } else if (conf.getValue(key).valueType() == ConfigValueType.BOOLEAN) {
                properties.put(prefix + key, conf.getBoolean(key));
            } else if (conf.getValue(key).valueType() == ConfigValueType.NUMBER) {
                properties.put(prefix + key, conf.getNumber(key));
            } else if (conf.getValue(key).valueType() == ConfigValueType.LIST) {
                if (name != null) {
                    properties.put(prefix + key, (ConfigList) conf.getValue(key));
                } else {
                    properties.put(key, (ConfigList) conf.getValue(key));
                }
            } else {
                if (name != null) {
                    traverse(conf.getConfig(key), properties, prefix + key);
                } else {
                    traverse(conf.getConfig(key), properties, key);
                }
            }
        }
    }
}
