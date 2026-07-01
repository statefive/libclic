/**
 * Copyright 2019 www.statefive.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.statefive.clic.valuetype;

/**
 * Thrown when a value type cannot be constructed, usually from a type
 * containing invalid properties or the value of the type breaking the
 * constraints of any defined properties.
 */
public class ValueTypeCreationException extends RuntimeException {

    /**
     * Throw exception with appropriate message.
     *
     * @param message non-{@code null} message.
     */
    public ValueTypeCreationException(String message) {
        super(message);
    }

    /**
     * Throw exception with appropriate message and underlying cause.
     *
     * @param message non-{@code null} message.
     *
     * @param cause cause of the exception.
     */
    public ValueTypeCreationException(String message, Throwable cause) {
        super(message, cause);
    }

}
