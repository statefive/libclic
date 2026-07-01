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
 * A source for properties.
 *
 * @author rich
 * 
 * @param <S> Source implementation.
 */
public interface PropertiesSource<S> {
    
    /**
     * Get the properties source.
     * 
     * @return non-{@code null} properties source.
     */
    S getSource();
    
}
