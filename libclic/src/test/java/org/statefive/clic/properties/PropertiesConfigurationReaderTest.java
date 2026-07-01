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
import org.apache.commons.configuration2.Configuration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.statefive.clic.ClcException;

/**
 *
 * @author rich
 */
public class PropertiesConfigurationReaderTest extends AbstractTestPropertiesReader {

    /**
     * Test that a given property can be overridden and enable short and long
     * options.
     */
    @Test
    public void testReadOverridePropertyShortAndLongOpts() throws Exception {
        InputStream is = PropertiesTestHelper.create("host.name = 127.0.0.1");
        PropertiesConfigurationReader instance = new PropertiesConfigurationReader();
        Configuration result = instance.read(new PropertiesStreamSource(is));
        assertEquals(result.getString("host.name"), "127.0.0.1");
    }

    /**
     * Test that a properties file can be read.
     *
     * @throws Exception
     */
    @Test
    public void testReadPropertiesFile() throws Exception {
        File f = new File("target/test-classes/properties/PropertiesReaderTest/testReadPropertiesFile.properties");
        PropertiesConfigurationReader instance = new PropertiesConfigurationReader();
        Configuration result = instance.read(new PropertiesFileSource(f));
        assertEquals(result.getString("host.primary_address"), "localhost");
    }

    /**
     * Test that loading an unknown property source results in an exception.
     */
    @Test
    public void testReadInvalidPropertiesSource() throws Exception {
        PropertiesConfigurationReader instance = new PropertiesConfigurationReader();
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
    public void testReadOverridePropertyBadInputStream() throws Exception {
        InputStream is = new BadInputTestStream();
        PropertiesConfigurationReader instance = new PropertiesConfigurationReader();
        try {
            PropertiesTestHelper.createFile("foo != ", "foo", "bar");
            instance.read(new PropertiesStreamSource(is));
            fail("Expected an exception");
        } catch (ClcException ex) {
            assertEquals("Failed to read properties: java.io.IOException: Failed to read stream.", ex.getMessage());
        }
    }

    /**
     * Test that loading a non-existing file generates the expected exception.
     */
    @Test
    public void testReadOverridePropertyNullInputStream() throws Exception {
        File f = new File("target/test-classes/properties/no-such-files-exists.foo");
        PropertiesConfigurationReader instance = new PropertiesConfigurationReader();
        try {
            instance.read(new PropertiesFileSource(f));
            fail("Expected an exception");
        } catch (PropertiesLoadException ex) {
            String x = ex.getMessage();
            assertEquals("target/test-classes/properties/no-such-files-exists.foo (No such file or directory)",
                    ex.getMessage());
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
        PropertiesConfigurationReader instance = new PropertiesConfigurationReader();
        PropertiesFileSource pfs = new PropertiesFileSource(f);
        PropertiesCommandSource pcs = new PropertiesCommandSource(
                name.getMethodName(), pfs);
        Configuration result = instance.read(pcs);
        assertEquals(result.getString("host.primary_address"), "localhost");
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
        PropertiesConfigurationReader instance = new PropertiesConfigurationReader();
        PropertiesStreamSource pss = new PropertiesStreamSource(is);
        PropertiesCommandSource pcs = new PropertiesCommandSource(
                name.getMethodName(), pss);
        Configuration result = instance.read(pcs);
        assertEquals(result.getString("host.name"), "127.0.0.1");
    }

    /**
     * Test that invalid properties associated with a command source throws an
     * error.
     *
     * @throws Exception
     */
    @Test
    public void testReadPropertiesCommandSourceWithBadSource() throws Exception {
        PropertiesConfigurationReader instance = new PropertiesConfigurationReader();
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
