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

import org.statefive.clic.properties.PropertiesSource;

/**
 * Represents a resource packaged within the application.
 *
 * @author irch
 */
public class TypesafePropertiesStreamResource implements PropertiesSource<String> {

    /**
     * Path to the resource.
     */
    private final String resource;
    
    /**
     * Create a new resource from the given resource path.
     * 
     * @param resource non-{@code null} existing resource.
     */
    public TypesafePropertiesStreamResource(String resource) {
        this.resource = resource;
    }
    
    /**
     * Get the resource path.
     * 
     * @return non-{@code null} resource path.
     */
    @Override
    public String getSource() {
        return resource;
    }
    
}
