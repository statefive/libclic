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
 * Exception thrown when a properties implementation fails to load a properties
 * source.
 *
 * @author rich
 */
public class PropertiesLoadException extends Exception {

    /**
     * Create a new properties load exception.
     *
     * @param msg the detail message.
     */
    public PropertiesLoadException(String msg) {
        super(msg);
    }

    /**
     * Create a new properties load exception.
     *
     * @param msg the detail message.
     * 
     * @param cause non-{@code null} cause of the exception.
     */
    public PropertiesLoadException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
