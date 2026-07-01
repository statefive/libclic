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

import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import org.junit.Test;
import static org.junit.Assert.*;
import org.statefive.clic.ClcException;

/**
 *
 * @author rich
 */
public class JavaPropertiesReaderTest extends AbstractTestPropertiesReader {

    /**
     * Test that a given property can be overridden and enable short and long
     * options.
     */
    @Test
    public void testReadOverridePropertyShortAndLongOpts() throws Exception {
        InputStream is = PropertiesTestHelper.create("host.name = 127.0.0.1");
        JavaPropertiesReader instance = new JavaPropertiesReader();
        Properties result = instance.read(new PropertiesStreamSource(is));
        assertEquals(result.get("host.name"), "127.0.0.1");
    }

    /**
     * Test that a properties file can be read.
     *
     * @throws Exception
     */
    @Test
    public void testReadPropertiesFile() throws Exception {
        File f = new File("target/test-classes/properties/PropertiesReaderTest/testReadPropertiesFile.properties");
        JavaPropertiesReader instance = new JavaPropertiesReader();
        Properties result = instance.read(new PropertiesFileSource(f));
        assertEquals(result.get("host.primary_address"), "localhost");
    }

    /**
     * Test that loading an unknown property source results in an exception.
     */
    @Test
    public void testReadInvalidPropertiesSource() throws Exception {
        JavaPropertiesReader instance = new JavaPropertiesReader();
        try {
            instance.read(new InvalidPropertiesSource());
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals("Cannot load properties using InvalidPropertiesSource",
                    ex.getMessage());
        }
    }

    /**
     * Test that loading a bad input stream generates the expected exception.
     */
    @Test
    public void testReadOverridePropertyWithBadInputStream() throws Exception {
        InputStream is = new BadInputTestStream();
        JavaPropertiesReader instance = new JavaPropertiesReader();
        try {
            instance.read(new PropertiesStreamSource(is));
            fail("Expected an exception");
        } catch (PropertiesLoadException ex) {
            assertEquals("Failed to read stream.", ex.getMessage());
        }
    }

    /**
     * Test that a properties file can be read against a properties command
     * source.
     *
     * @throws Exception
     */
    @Test
    public void testReadPropertiesCommandSourceWithFile() throws Exception {
        File f = new File("target/test-classes/properties/PropertiesReaderTest/testReadPropertiesFile.properties");
        JavaPropertiesReader instance = new JavaPropertiesReader();
        PropertiesFileSource pfs = new PropertiesFileSource(f);
        PropertiesCommandSource pcs = new PropertiesCommandSource(
                name.getMethodName(), pfs);
        Properties result = instance.read(pcs);
        assertEquals(result.get("host.primary_address"), "localhost");
    }

    /**
     * Test that a properties stream can be read against a properties command
     * source.
     *
     * @throws Exception
     */
    @Test
    public void testReadPropertiesCommandSourceWithStream() throws Exception {
        InputStream is = PropertiesTestHelper.create("host.name = 127.0.0.1");
        JavaPropertiesReader instance = new JavaPropertiesReader();
        PropertiesStreamSource pss = new PropertiesStreamSource(is);
        PropertiesCommandSource pcs = new PropertiesCommandSource(
                name.getMethodName(), pss);
        Properties result = instance.read(pcs);
        assertEquals(result.get("host.name"), "127.0.0.1");
    }

    /**
     * Test that invalid properties associated with a command source throws an
     * error.
     *
     * @throws Exception
     */
    @Test
    public void testReadPropertiesCommandSourceWithBadSource() throws Exception {
        JavaPropertiesReader instance = new JavaPropertiesReader();
        InvalidPropertiesSource ips = new InvalidPropertiesSource();
        PropertiesCommandSource pcs = new PropertiesCommandSource(
                name.getMethodName(), ips);
        try {
            instance.read(pcs);
        } catch (ClcException ex) {
            assertEquals("Cannot load properties using "
                    + InvalidPropertiesSource.class.getSimpleName(), ex.getMessage());
        }
    }
}
