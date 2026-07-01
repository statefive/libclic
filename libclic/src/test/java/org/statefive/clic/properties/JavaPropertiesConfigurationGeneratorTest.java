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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.statefive.clic.Clc;
import org.statefive.clic.ClcParser;
import org.statefive.clic.GlobalConfiguration;
import static org.statefive.clic.GlobalConfiguration.GLOBAL_HELP_FORMAT_SORT_OPTIONS;
import org.statefive.clic.valuetype.ByteType;
import org.statefive.clic.valuetype.ShortType;

/**
 *
 * @author rich
 */
public class JavaPropertiesConfigurationGeneratorTest {
    
    /**
     * 
     */
    @BeforeClass
    public static void setUpClass() {
        Clc.initialiseValueTypeFactory();
    }
    
    /**
     * 
     */
    @AfterClass
    public static void tearDownUpClass() {
        PropertiesTestHelper.clearValueTypes();
    }

    /**
     * Test of generateConfiguration method, of class
     * PropertiesConfigurationGenerator.
     */
    @Test
    public void testGenerateConfiguration() throws Exception {
        Properties properties = new Properties();
        properties.put("host.name", "127.0.0.1");
        JavaPropertiesClcGenerator instance = new JavaPropertiesClcGenerator();
        ByteArrayOutputStream result = instance.generateConfiguration(properties,
                (Configuration) null, null, true, null, false, false);
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
     * PropertiesConfigurationGenerator.
     */
    @Test
    public void testGenerateConfigurationWithWholeNumbersAsByte() throws Exception {
        Properties properties = new Properties();
        properties.put("byte-val", "3");
        JavaPropertiesClcGenerator instance = new JavaPropertiesClcGenerator();
        Configuration config = new PropertiesConfiguration();
        ByteArrayOutputStream result = instance.generateConfiguration(properties,
                config, (PropertyNameFilter) null, true, 
                new TypeInferralConfigBuilder()
                        .withInferTypes()
                        .withNaturalNumbersAs(ByteType.BYTE).build(),
                false, false);
        String data = new String(result.toByteArray());
        String[] lineData = data.split("\n");
        Set<String> lineSet = new LinkedHashSet<>();
        for (String line : lineData) {
            if (!line.isEmpty()) {
                lineSet.add(line);
            }
        }
        assertEquals(21, lineSet.size());
        PropertiesTestHelper.hasDefaultHelpConfigOptions(lineSet);
        PropertiesTestHelper.hasDefaultConfigComments(lineSet);
        PropertiesTestHelper.hasConfigOpts(lineSet, "byte-val");
        PropertiesTestHelper.hasConfigArg(lineSet, "byte-val");
        PropertiesTestHelper.hasConfigType(lineSet, "byte-val",  
                ByteType.BYTE);
        PropertiesTestHelper.hasConfigDescription(lineSet, "byte-val", 
                "Overrides property 'byte-val', default value '3'");
    }

    /**
     * Test of generateConfiguration method, of class
     * PropertiesConfigurationGenerator.
     */
    @Test
    public void testGenerateConfigurationWithWholeNumbersAsShort() throws Exception {
        Properties properties = new Properties();
        properties.put("short-val", "3");
        JavaPropertiesClcGenerator instance = new JavaPropertiesClcGenerator();
        Configuration config = new PropertiesConfiguration();
        ByteArrayOutputStream result = instance.generateConfiguration(properties,
                config, (PropertyNameFilter) null, true, 
                new TypeInferralConfigBuilder()
                        .withInferTypes()
                        .withNaturalNumbersAs(ShortType.SHORT).build(),
                false, false);
        String data = new String(result.toByteArray());
        String[] lineData = data.split("\n");
        Set<String> lineSet = new LinkedHashSet<>();
        for (String line : lineData) {
            if (!line.isEmpty()) {
                lineSet.add(line);
            }
        }
        assertEquals(21, lineSet.size());
        PropertiesTestHelper.hasDefaultHelpConfigOptions(lineSet);
        PropertiesTestHelper.hasDefaultConfigComments(lineSet);
        PropertiesTestHelper.hasConfigOpts(lineSet, "short-val");
        PropertiesTestHelper.hasConfigArg(lineSet, "short-val");
        PropertiesTestHelper.hasConfigType(lineSet, "short-val",  
                ShortType.SHORT);
        PropertiesTestHelper.hasConfigDescription(lineSet, "short-val", 
                "Overrides property 'short-val', default value '3'");
    }

    /**
     * Test of generateConfiguration method, of class
     * PropertiesConfigurationGenerator.
     */
    @Test
    public void testGenerateConfigurationWithHelpSortOptions() throws Exception {
        Properties properties = new Properties();
        properties.put("short-val", "3");
        JavaPropertiesClcGenerator instance = new JavaPropertiesClcGenerator();
        Configuration config = new PropertiesConfiguration();
        config.addProperty(GlobalConfiguration.GLOBAL_HELP_FORMAT_SORT_OPTIONS, ClcParser.TRUE);
        ByteArrayOutputStream result = instance.generateConfiguration(properties,
                config, (PropertyNameFilter) null, true, 
                new TypeInferralConfigBuilder()
                        .withInferTypes()
                        .withNaturalNumbersAs(ShortType.SHORT).build(),
                false, false);
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
        PropertiesTestHelper.containsProperty(lineData,
                GlobalConfiguration.GLOBAL_HELP_FORMAT_SORT_OPTIONS + " = true");
        PropertiesTestHelper.hasDefaultHelpConfigOptions(lineSet, skip);
        PropertiesTestHelper.hasDefaultConfigComments(lineSet);
        PropertiesTestHelper.hasConfigOpts(lineSet, "short-val");
        PropertiesTestHelper.hasConfigArg(lineSet, "short-val");
        PropertiesTestHelper.hasConfigType(lineSet, "short-val",  
                ShortType.SHORT);
        PropertiesTestHelper.hasConfigDescription(lineSet, "short-val", 
                "Overrides property 'short-val', default value '3'");
    }

}
