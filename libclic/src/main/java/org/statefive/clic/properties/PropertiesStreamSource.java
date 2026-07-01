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

import java.io.InputStream;

/**
 * Input stream-based properties source.
 *
 * @author rich
 */
public class PropertiesStreamSource implements PropertiesSource<InputStream> {

    /**
     * Properties input stream source.
     */
    private final InputStream source;
    
    /**
     * Create a new properties stream source.
     * 
     * @param source non-{@code null} properties input stream.
     */
    public PropertiesStreamSource(InputStream source) {
        this.source = source;
    }
    
    /**
     * Get the properties input stream.
     * 
     * @return non-{@code null} properties input stream.
     */
    @Override
    public InputStream getSource() {
        return source;
    }
    
}
