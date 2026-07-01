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
package org.statefive.clic;

/**
 * Represents an exception for incorrect definition and usage of a configuration
 * file.
 */
public class ClcException extends Exception {

    /**
     * Constructs an instance of <code>ConfigException</code> with the specified
     * detail message.
     *
     * @param message the detail message.
     */
    public ClcException(final String message) {
        super(message);
    }

    /**
     * Constructs an instance of <code>ConfigException</code> with the specified
     * detail message.
     *
     * @param lineNo line number
     *
     * @param message the detail message.
     */
    public ClcException(final Integer lineNo, final String message) {
        super("Line no. " + lineNo + ": " + message);
    }

    /**
     * Creates a new instance of <code>ConfigException</code> with the specified
     * detail message and cause.
     *
     * @param message the message detail.
     *
     * @param cause the cause of the exception.
     */
    public ClcException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
