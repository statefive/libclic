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

/**
 * Test properties 'implementation' solely for testing purposes.
 *
 * @author rich
 */
public class BasicProperties {
    
    /**
     * Map of property key - values.
     */
    Map<String, String> properties = new HashMap<>();
    
    /**
     * Get the properties.
     * 
     * @return non-{@code null} properties; may be empty.
     */
    public Map<String, String> getProperties() {
        return properties;
    }
    
    /**
     * Set the specified property.
     * 
     * @param key non-{@code null} key to set.
     * 
     * @param value non-{@code null} value to set.
     */
    public void setProperty(String key, String value) {
        properties.put(key, value);
    }
    
    /**
     * Get the specified value of the given property.
     * 
     * @param key non-{@code null} property to retrieve.
     * 
     * @return the property, if it exists; {@code null} otherwise.
     */
    public String getProperty(String key) {
        return properties.get(key);
    }
}
