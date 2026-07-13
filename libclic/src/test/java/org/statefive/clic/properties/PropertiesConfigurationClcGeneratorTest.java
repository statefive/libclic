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

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;
import org.statefive.clic.Clc;
import org.statefive.clic.ClcParser;
import org.statefive.clic.GlobalConfiguration;
import static org.statefive.clic.GlobalConfiguration.GLOBAL_HELP_FORMAT_SORT_OPTIONS;
import org.statefive.clic.valuetype.ByteType;
import org.statefive.clic.valuetype.DoubleType;
import org.statefive.clic.valuetype.FloatingPointType;
import org.statefive.clic.valuetype.LongType;
import org.statefive.clic.valuetype.ShortType;

/**
 *
 * @author rich
 */
public class PropertiesConfigurationClcGeneratorTest {

    /**
     * Initialise value types.
     */
    @BeforeClass
    public static void setUpClass() {
        Clc.initialiseValueTypeFactory();
    }

    /**
     * Clear all value types.
     */
    @AfterClass
    public static void tearDownUpClass() {
        PropertiesTestHelper.clearValueTypes();
    }

    /**
     * Test of generateConfiguration method, of class
     * PropertiesConfigurationClcGenerator.
     */
    @Test
    public void testGenerateConfiguration() throws Exception {
        PropertiesConfiguration properties = new PropertiesConfiguration();
        properties.addProperty("host.name", "127.0.0.1");
        PropertiesConfigurationClcGenerator instance = (PropertiesConfigurationClcGenerator) PropertiesTestHelper.create(
                properties, null, null, null,
                null, true, false, false);
        ByteArrayOutputStream result = instance.generateConfiguration();
        String data = new String(result.toByteArray());
        String[] lineData = data.split("\n");
        Set<String> lineSet = new LinkedHashSet<>();
        for (String line : lineData) {
            if (!line.isEmpty()) {
                lineSet.add(line);
            }
        }
        assertEquals(20, lineSet.size());
        PropertiesTestHelper.hasDefaultHelpConfigOptions(lineSet);
        PropertiesTestHelper.hasDefaultConfigComments(lineSet);
        PropertiesTestHelper.hasConfigOpts(lineSet, "host-name");
        PropertiesTestHelper.hasConfigArg(lineSet, "host-name");
        PropertiesTestHelper.hasConfigDescription(lineSet, "host-name",
                "Overrides property 'host.name', default value '127.0.0.1'");
    }

    /**
     * Test of generateConfiguration method, of class
     * PropertiesConfigurationClcGenerator.
     */
    @Test
    public void testGenerateConfigurationWithHelpSort() throws Exception {
        PropertiesConfiguration properties = new PropertiesConfiguration();
        properties.addProperty("host.name", "127.0.0.1");
        Configuration config = new PropertiesConfiguration();
        config.addProperty(GlobalConfiguration.GLOBAL_HELP_FORMAT_SORT_OPTIONS, ClcParser.TRUE);
        PropertiesConfigurationClcGenerator instance = (PropertiesConfigurationClcGenerator) PropertiesTestHelper.create(
                properties, config, null, null,
                null, true, false, false);
        ByteArrayOutputStream result = instance.generateConfiguration();

        String data = new String(result.toByteArray());
        String[] lineData = data.split("\n");
        Set<String> lineSet = new LinkedHashSet<>();
        for (String line : lineData) {
            if (!line.isEmpty()) {
                lineSet.add(line);
            }
        }
        Set<String> skip = new HashSet<>();
        skip.add(GLOBAL_HELP_FORMAT_SORT_OPTIONS + " = false");
        assertEquals(20, lineSet.size());
        PropertiesTestHelper.containsProperty(lineData,
                GlobalConfiguration.GLOBAL_HELP_FORMAT_SORT_OPTIONS + " = true");
        PropertiesTestHelper.hasDefaultHelpConfigOptions(lineSet, skip);
        PropertiesTestHelper.hasDefaultConfigComments(lineSet);
        PropertiesTestHelper.hasConfigOpts(lineSet, "host-name");
        PropertiesTestHelper.hasConfigArg(lineSet, "host-name");
        PropertiesTestHelper.hasConfigDescription(lineSet, "host-name",
                "Overrides property 'host.name', default value '127.0.0.1'");
    }

    /**
     * Test of generateConfiguration method, of class
     * PropertiesConfigurationClcGenerator.
     */
    @Test
    public void testGenerateConfigurationForByte() throws Exception {
        TypeInferralConfig typeInferralConfig = new TypeInferralConfigBuilder()
                .withInferTypes()
                .withNaturalNumbersAs(ByteType.BYTE).build();
        PropertiesConfiguration properties = new PropertiesConfiguration();
        properties.addProperty("definitley-byte", "128");
        PropertiesConfigurationClcGenerator instance = (PropertiesConfigurationClcGenerator) PropertiesTestHelper.create(
                properties, new PropertiesConfiguration(), null, typeInferralConfig,
                null, true, false, false);
        ByteArrayOutputStream result = instance.generateConfiguration();

        String data = new String(result.toByteArray());
        String[] lineData = data.split("\n");
        Set<String> lineSet = new LinkedHashSet<>();
        for (String line : lineData) {
            if (!line.isEmpty()) {
                lineSet.add(line);
            }
        }
        PropertiesTestHelper.hasConfigType(lineSet, "definitley-byte", ByteType.BYTE);
    }

    /**
     * Test of generateConfiguration method, of class
     * PropertiesConfigurationClcGenerator.
     */
    @Test
    public void testGenerateConfigurationForShort() throws Exception {
        TypeInferralConfig typeInferralConfig = new TypeInferralConfigBuilder()
                .withInferTypes()
                .withNaturalNumbersAs(ShortType.SHORT).build();
        PropertiesConfiguration properties = new PropertiesConfiguration();
        properties.addProperty("definitley-short", "1000");
        PropertiesConfigurationClcGenerator instance = (PropertiesConfigurationClcGenerator) PropertiesTestHelper.create(
                properties, new PropertiesConfiguration(), null, typeInferralConfig,
                null, true, false, false);
        ByteArrayOutputStream result = instance.generateConfiguration();

        String data = new String(result.toByteArray());
        String[] lineData = data.split("\n");
        Set<String> lineSet = new LinkedHashSet<>();
        for (String line : lineData) {
            if (!line.isEmpty()) {
                lineSet.add(line);
            }
        }
        PropertiesTestHelper.hasConfigType(lineSet, "definitley-short", ShortType.SHORT);
    }

    /**
     * Test of generateConfiguration method, of class
     * PropertiesConfigurationClcGenerator.
     */
    @Test
    public void testGenerateConfigurationForInt() throws Exception {
        TypeInferralConfig typeInferralConfig = new TypeInferralConfigBuilder()
                .withInferTypes().build();
        PropertiesConfiguration properties = new PropertiesConfiguration();
        properties.addProperty("definitley.int", "50000");
        PropertiesConfigurationClcGenerator instance = (PropertiesConfigurationClcGenerator) PropertiesTestHelper.create(
                properties, new PropertiesConfiguration(), null, typeInferralConfig,
                null, true, false, false);
        ByteArrayOutputStream result = instance.generateConfiguration();

        String data = new String(result.toByteArray());
        String[] lineData = data.split("\n");
        Set<String> lineSet = new LinkedHashSet<>();
        for (String line : lineData) {
            if (!line.isEmpty()) {
                lineSet.add(line);
            }
        }
        PropertiesTestHelper.hasConfigType(lineSet, "definitley-int", "int");
    }

    /**
     * Test of generateConfiguration method, of class
     * PropertiesConfigurationClcGenerator.
     */
    @Test
    public void testGenerateConfigurationForLong() throws Exception {
        TypeInferralConfig typeInferralConfig = new TypeInferralConfigBuilder()
                .withInferTypes().build();
        PropertiesConfiguration properties = new PropertiesConfiguration();
        properties.addProperty("definitley.long", "9223372036854775807");
        PropertiesConfigurationClcGenerator instance = (PropertiesConfigurationClcGenerator) PropertiesTestHelper.create(
                properties, new PropertiesConfiguration(), null, typeInferralConfig,
                null, true, false, false);
        ByteArrayOutputStream result = instance.generateConfiguration();

        String data = new String(result.toByteArray());
        String[] lineData = data.split("\n");
        Set<String> lineSet = new LinkedHashSet<>();
        for (String line : lineData) {
            if (!line.isEmpty()) {
                lineSet.add(line);
            }
        }
        PropertiesTestHelper.hasConfigType(lineSet, "definitley-long", "long");
    }

    /**
     * Test of generateConfiguration method, of class
     * PropertiesConfigurationClcGenerator.
     */
    @Test
    public void testGenerateConfigurationForFloat() throws Exception {
        TypeInferralConfig typeInferralConfig = new TypeInferralConfigBuilder()
                .withInferTypes().build();
        PropertiesConfiguration properties = new PropertiesConfiguration();
        properties.addProperty("definitley.float", "1.1");
        PropertiesConfigurationClcGenerator instance = (PropertiesConfigurationClcGenerator) PropertiesTestHelper.create(
                properties, new PropertiesConfiguration(), null, typeInferralConfig,
                null, true, false, false);
        ByteArrayOutputStream result = instance.generateConfiguration();

        String data = new String(result.toByteArray());
        String[] lineData = data.split("\n");
        Set<String> lineSet = new LinkedHashSet<>();
        for (String line : lineData) {
            if (!line.isEmpty()) {
                lineSet.add(line);
            }
        }
        PropertiesTestHelper.hasConfigType(lineSet, "definitley-float", "float");
    }

    /**
     * Test of generateConfiguration method, of class
     * PropertiesConfigurationClcGenerator.
     */
    @Test
    public void testGenerateConfigurationForDouble() throws Exception {
        TypeInferralConfig typeInferralConfig = new TypeInferralConfigBuilder()
                .withInferTypes().build();
        PropertiesConfiguration properties = new PropertiesConfiguration();
        // upper range of a float:
        // 3.40282346638528860e+38
        BigDecimal num = new BigDecimal(12.40282346638528860e+38);
        properties.addProperty("definitley.adouble", num.toString());
        PropertiesConfigurationClcGenerator instance = (PropertiesConfigurationClcGenerator) PropertiesTestHelper.create(
                properties, new PropertiesConfiguration(), null, typeInferralConfig,
                null, true, false, false);
        ByteArrayOutputStream result = instance.generateConfiguration();
        String data = new String(result.toByteArray());
        String[] lineData = data.split("\n");
        Set<String> lineSet = new LinkedHashSet<>();
        for (String line : lineData) {
            if (!line.isEmpty()) {
                lineSet.add(line);
            }
        }
        PropertiesTestHelper.hasConfigType(lineSet, "definitley-adouble", "double");
    }

    /**
     * Test of generateConfiguration method, of class
     * PropertiesConfigurationClcGenerator.
     */
    @Test
    public void testGenerateConfigurationForString() throws Exception {
        PropertiesConfiguration properties = new PropertiesConfiguration();
        // upper range of a float:
        // 3.40282346638528860e+38
        String value = "some arbitrary string";
        properties.addProperty("definitley.astring", value);
        PropertiesConfigurationClcGenerator instance = (PropertiesConfigurationClcGenerator) PropertiesTestHelper.create(
                properties, new PropertiesConfiguration(), null, null,
                null, true, false, false);
        ByteArrayOutputStream result = instance.generateConfiguration();
        String data = new String(result.toByteArray());
        String[] lineData = data.split("\n");
        Set<String> lineSet = new LinkedHashSet<>();
        for (String line : lineData) {
            if (!line.isEmpty()) {
                lineSet.add(line);
            }
        }
    }

    /**
     * Test of generateConfiguration method, of class
     * PropertiesConfigurationClcGenerator.
     */
    @Test
    public void testGenerateConfigurationForNumberAsDouble() throws Exception {
        TypeInferralConfig typeInferralConfig = new TypeInferralConfigBuilder()
                .withInferTypes().withRealNumbersAs(DoubleType.DOUBLE).build();
        PropertiesConfiguration properties = new PropertiesConfiguration();
        String value = "1.23";
        properties.addProperty("definitley.adouble", value);
        PropertiesConfigurationClcGenerator instance = (PropertiesConfigurationClcGenerator) PropertiesTestHelper.create(
                properties, new PropertiesConfiguration(), null, typeInferralConfig,
                null, true, false, false);
        ByteArrayOutputStream result = instance.generateConfiguration();
        String data = new String(result.toByteArray());
        String[] lineData = data.split("\n");
        Set<String> lineSet = new LinkedHashSet<>();
        for (String line : lineData) {
            if (!line.isEmpty()) {
                lineSet.add(line);
            }
        }
        PropertiesTestHelper.hasConfigType(lineSet, "definitley-adouble", "double");
    }

    /**
     * Test of generateConfiguration method, of class
     * PropertiesConfigurationClcGenerator.
     */
    @Test
    public void testGenerateConfigurationForNumberAsLong() throws Exception {
        TypeInferralConfig typeInferralConfig = new TypeInferralConfigBuilder()
                .withInferTypes().withNaturalNumbersAs(LongType.LONG).build();
        PropertiesConfiguration properties = new PropertiesConfiguration();
        String value = "1234567890";
        properties.addProperty("definitley.along", value);
        PropertiesConfigurationClcGenerator instance = (PropertiesConfigurationClcGenerator) PropertiesTestHelper.create(
                properties, new PropertiesConfiguration(), null, typeInferralConfig,
                null, true, false, false);
        ByteArrayOutputStream result = instance.generateConfiguration();
        String data = new String(result.toByteArray());
        String[] lineData = data.split("\n");
        Set<String> lineSet = new LinkedHashSet<>();
        for (String line : lineData) {
            if (!line.isEmpty()) {
                lineSet.add(line);
            }
        }
        PropertiesTestHelper.hasConfigType(lineSet, "definitley-along", "long");
    }

    /**
     * Test of generateConfiguration method, of class
     * PropertiesConfigurationClcGenerator.
     */
    @Test
    public void testGenerateConfigurationForFloatWhenSameAs() throws Exception {
        TypeInferralConfig typeInferralConfig = new TypeInferralConfigBuilder()
                .withInferTypes().withRealNumbersAs(FloatingPointType.FLOAT).build();
        PropertiesConfiguration properties = new PropertiesConfiguration();
        properties.addProperty("definitley.float", "1.1");
        PropertiesConfigurationClcGenerator instance = (PropertiesConfigurationClcGenerator) PropertiesTestHelper.create(
                properties, new PropertiesConfiguration(), null, typeInferralConfig,
                null, true, false, false);
        ByteArrayOutputStream result = instance.generateConfiguration();
        String data = new String(result.toByteArray());
        String[] lineData = data.split("\n");
        Set<String> lineSet = new LinkedHashSet<>();
        for (String line : lineData) {
            if (!line.isEmpty()) {
                lineSet.add(line);
            }
        }
        PropertiesTestHelper.hasConfigType(lineSet, "definitley-float", "float");
    }

    /**
     * Test of deprecated generateConfiguration method, of class
     * PropertiesConfigurationClcGenerator.
     */
    @Test
    public void testGenerateConfigurationForNumberAsLongDeprecated() throws Exception {
        TypeInferralConfig typeInferralConfig = new TypeInferralConfigBuilder()
                .withInferTypes().withNaturalNumbersAs(LongType.LONG).build();
        PropertiesConfiguration properties = new PropertiesConfiguration();
        String value = "1234567890";
        properties.addProperty("definitley.along", value);
        PropertiesConfigurationClcGenerator instance = new PropertiesConfigurationClcGenerator();
        ByteArrayOutputStream result = instance.generateConfiguration(properties,
                new PropertiesConfiguration(), null, true, typeInferralConfig, false, false);
        String data = new String(result.toByteArray());
        String[] lineData = data.split("\n");
        Set<String> lineSet = new LinkedHashSet<>();
        for (String line : lineData) {
            if (!line.isEmpty()) {
                lineSet.add(line);
            }
        }
        PropertiesTestHelper.hasConfigType(lineSet, "definitley-along", "long");
    }

    /**
     * Test of deprecated generateConfiguration method, of class
     * PropertiesConfigurationClcGenerator.
     */
    @Test
    public void testGenerateConfigurationNullConfigForNumberAsLongDeprecated() throws Exception {
        TypeInferralConfig typeInferralConfig = new TypeInferralConfigBuilder()
                .withInferTypes().withNaturalNumbersAs(LongType.LONG).build();
        PropertiesConfiguration properties = new PropertiesConfiguration();
        String value = "1234567890";
        properties.addProperty("definitley.along", value);
        PropertiesConfigurationClcGenerator instance = new PropertiesConfigurationClcGenerator();
        ByteArrayOutputStream result = instance.generateConfiguration(properties,
                null, null, true, typeInferralConfig, false, false);
        String data = new String(result.toByteArray());
        String[] lineData = data.split("\n");
        Set<String> lineSet = new LinkedHashSet<>();
        for (String line : lineData) {
            if (!line.isEmpty()) {
                lineSet.add(line);
            }
        }
        PropertiesTestHelper.hasConfigType(lineSet, "definitley-along", "long");
    }

    /**
     * Test of deprecated generateConfiguration method, of class
     * PropertiesConfigurationClcGenerator.
     */
    @Test
    public void testGenerateConfigurationWithHelpSortDeprecated() throws Exception {
        PropertiesConfiguration properties = new PropertiesConfiguration();
        properties.addProperty("host.name", "127.0.0.1");
        PropertiesConfigurationClcGenerator instance = new PropertiesConfigurationClcGenerator();
        Configuration config = new PropertiesConfiguration();
        config.addProperty(GlobalConfiguration.GLOBAL_HELP_FORMAT_SORT_OPTIONS, ClcParser.TRUE);
        ByteArrayOutputStream result = instance.generateConfiguration(properties,
                config, null, true, null, false, false);
        String data = new String(result.toByteArray());
        String[] lineData = data.split("\n");
        Set<String> lineSet = new LinkedHashSet<>();
        for (String line : lineData) {
            if (!line.isEmpty()) {
                lineSet.add(line);
            }
        }
        Set<String> skip = new HashSet<>();
        skip.add(GLOBAL_HELP_FORMAT_SORT_OPTIONS + " = false");
        assertEquals(20, lineSet.size());
        PropertiesTestHelper.containsProperty(lineData,
                GlobalConfiguration.GLOBAL_HELP_FORMAT_SORT_OPTIONS + " = true");
        PropertiesTestHelper.hasDefaultHelpConfigOptions(lineSet, skip);
        PropertiesTestHelper.hasDefaultConfigComments(lineSet);
        PropertiesTestHelper.hasConfigOpts(lineSet, "host-name");
        PropertiesTestHelper.hasConfigArg(lineSet, "host-name");
        PropertiesTestHelper.hasConfigDescription(lineSet, "host-name",
                "Overrides property 'host.name', default value '127.0.0.1'");
    }

}
