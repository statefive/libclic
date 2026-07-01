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
import java.util.HashSet;
import java.util.Set;
import org.joor.Reflect;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.statefive.clic.Clc;
import org.statefive.clic.valuetype.DirUpdateListener;

/**
 *
 * @author rich
 */
public abstract class AbstractPropertiesTestBuilder implements DirUpdateListener {
    
    /**
     * Test name.
     */
    @Rule
    public TestName name = new TestName();

    
    /**
     * Set of files received from directory update tests.
     */
    protected Set<File> dirUpdateFiles;
    
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
     * Initialise file set.
     */
    @Before
    public void setUp() {
        dirUpdateFiles = new HashSet<>();
        System.setOut(System.err);
        System.setErr(System.out);
    }
    
    /**
     * Inspired by: https://stackoverflow.com/questions/3301635/change-private-static-final-field-using-java-reflection
     */
    @After
    public void tearDown() {
        Reflect.onClass(Clc.class).set("instance", null);
    }

    /**
     * Receive a directory update when directories are traversed.
     *
     * @param dir non-{@code null} directory being traversed.
     * 
     * @param files non-{@code null} list of files (not directories) within the
     * specified directory.
     * 
     * @param listenerId non-{@code null} listener ID.
     * 
     * @return {@code true} if the directory should be traversed; {@code false}
     * otherwise.
     */
    @Override
    public boolean directoryTraversed(File dir, File[] files, String listenerId) {
        String methodName = name.getMethodName();
        if (name.getMethodName() == null) {
            // hack for basic dir update listrener because it's not a test class:
            methodName = listenerId;
        }
        boolean traverse = false;
        if (listenerId.equals(methodName)) {
            traverse = true;
            for (File file : files) {
                dirUpdateFiles.add(file);
            }
        }
        return traverse;
    }
    
}
