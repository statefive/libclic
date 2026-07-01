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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Value type representing a character. Without specifying any properties any
 * character will be accepted.
 *
 * <p>
 * <strong>Properties</strong>
 * <ul>
 * <li><strong>includes</strong>=<i>[text]</i> (optional): regular expression or
 * string of characters to match. Mutually exclusive with the excludes
 * property;</li>
 * <li><strong>excludes</strong>=<i>[text]</i> (optional): regular expression or
 * string of characters to exclude. Mutually exclusive with the includes
 * property;</li>
 * <li><strong>regex</strong><i></i>[value]</li> (optional): if {@code true},
 * the include/excludes text will be treated as a regular expression, otherwise
 * will be treated as a text block that the character must be in/not in for
 * includes/excludes respectively.
 * </ul>
 */
public class CharacterType extends AbstractValueType<Character, String> {

    /**
     * Type name.
     */
    public static final String CHARACTER = "char";

    /**
     * Property for including a limited set of characters.
     */
    public static final String INCLUDES = "includes";

    /**
     * Property for excluding a limited set of characters.
     */
    public static final String EXCLUDES = "excludes";

    /**
     * Property for specifying that the include or exclude pattern is a regular
     * expression or not.
     */
    public static final String REGEX = "regex";

    /**
     * The character.
     */
    private Character data;

    /**
     * The include/exclude property string.
     */
    private String cludes;

    /**
     * Pattern for including characters.
     */
    private Pattern patternIncludes;

    /**
     * Pattern for excluding characters.
     */
    private Pattern patternExcludes;

    /**
     * Sets if the include or exclude string is a regular expression.
     */
    private boolean regex;

    /**
     * For non-regular expressions, whether to check for inclusion or exclusion
     * for groups of characters.
     */
    private boolean include;

    /**
     * 
     */
    public CharacterType() {
        setPackageName(Character.class.getPackageName());
        setJavaClassName(Character.class.getSimpleName());
        setJavaPrimitiveName(CHARACTER);
    }

    /**
     * Construct a character type.
     *
     * @param dataStr single character string.
     *
     * @return character representing the string.
     *
     * @throws ValueTypeCreationException if the data does not match the
     * specified regular expression match (if set).
     */
    @Override
    public Character getValue(String dataStr) throws ValueTypeCreationException {
        if (dataStr.length() == 0) {
            throw new ValueTypeCreationException("Characters generated from"
                    + " strings must be length 1.");
        } else if (dataStr.length() > 1) {
            throw new ValueTypeCreationException("Invalid string length "
                    + dataStr.length() + " to create character from.");
        }
        boolean invalidChar = false;
        if (cludes != null) {
            if (regex) {
                if (patternIncludes != null) {
                    Matcher m = patternIncludes.matcher(dataStr);
                    if (!m.matches()) {
                        invalidChar = true;
                    }
                } else {
                    Matcher m = patternExcludes.matcher(dataStr);
                    if (m.matches()) {
                        invalidChar = true;
                    }
                }
            } else {
                if (!include && cludes.contains(dataStr)) {
                    invalidChar = true;
                } else if (include && !cludes.contains(dataStr)) {
                    invalidChar = true;
                }
            }
        }
        if (invalidChar) {
            throw new ValueTypeCreationException("Invalid character: " + dataStr);
        }
        data = dataStr.charAt(0);
        return data;
    }

    /**
     * Create a character according to {@link #getValue(java.lang.String)}.
     *
     * @param data non-{@code null} data to construct the character from.
     *
     * @throws ValueTypeCreationException as per
     * {@link #getValue(java.lang.String)}.
     */
    @Override
    public void setDefault(String data) {
        getValue(data);
    }

    /**
     * Parse the given properties.
     *
     * @param properties non-{@code null} properties to parse.
     *
     * @throws ValueTypeCreationException if any include or exclude property
     * have invalid regular expression syntax, if the property is not a known
     * property or if includes and excludes are specified together.
     */
    @Override
    public void setProperties(String properties) {
        List<String> props = new ArrayList<>();
        if (properties.contains(",")) {
            props.addAll(Arrays.asList(properties.split(",")));
        } else {
            props.add(properties.trim());
        }
        for (String property : props) {
            if (!property.contains("=")) {
                throw new ValueTypeCreationException("Invalid property: "
                        + property);
            }
            String[] propertyData = property.split("=");
            switch (propertyData[0].trim()) {
                case INCLUDES:
                    if (cludes != null) {
                        throw new ValueTypeCreationException("Cannot specify"
                                + " include and exclude properties; only one"
                                + " of the two can be set.");
                    }
                    include = true;
                    cludes = propertyData[1].trim();
                    break;
                case EXCLUDES:
                    if (cludes != null) {
                        throw new ValueTypeCreationException("Cannot specify"
                                + " include and exclude properties; only one"
                                + " of the two can be set.");
                    }
                    include = false;
                    cludes = propertyData[1].trim();
                    break;
                case REGEX:
                    regex = Boolean.parseBoolean(propertyData[1].trim());
                    break;
                default:
                    throw new ValueTypeCreationException("Unknown property: " 
                            + property);
            }
        }
        if (regex) {
            if (include) {
                try {
                    patternIncludes = Pattern.compile(cludes);
                } catch (PatternSyntaxException ex) {
                    throw new ValueTypeCreationException("Invalid regular"
                            + " expression for property '" + INCLUDES
                            + "': " + cludes);
                }
            } else {
                try {
                    patternExcludes = Pattern.compile(cludes);
                } catch (PatternSyntaxException ex) {
                    throw new ValueTypeCreationException("Invalid regular"
                            + " expression for property '" + EXCLUDES
                            + "': " + cludes);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValueTypeName() {
        return CHARACTER;
    }

    /**
     * Get the character type as a string.
     *
     * @return the string.
     */
    @Override
    public String toString() {
        return data.toString();
    }

}
