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
import java.io.PrintStream;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.statefive.clic.Clc;

/**
 *
 * @author rich
 */
public abstract class AbstractTestPropertiesReader {
    
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
     * Test name.
     */
    @Rule
    public TestName name = new TestName();

    /**
     * Disable output - prevents e.g. {@code --help} and error text from
     * underlying APIs.
     */
    @Before
    public void setUp() {
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
        System.setErr(new PrintStream(new ByteArrayOutputStream()));
    }

}
