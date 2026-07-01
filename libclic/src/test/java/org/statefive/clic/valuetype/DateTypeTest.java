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
package org.statefive.clic.valuetype;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author rich
 */
public class DateTypeTest {

    /**
     * Test of getValue method, of class DateType.
     */
    @Test
    public void testGetValue() throws Exception {
        String dateFormat = "yyyy/MM/dd HH:mm:ss";
        DateType dateType = new DateType();
        dateType.setProperties("dateFormat=" + dateFormat);
        String dateStr = "2019/10/17 17:26:26";
        Date date = dateType.getValue(dateStr);
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date expected = sdf.parse(dateStr);
        assertEquals(expected, date);
    }

    /**
     * Test of getValue method, of class DateType.
     */
    @Test
    public void testGetValueThrowsException() throws Exception {
        String dateFormat = "yyyy/MM/dd HH:mm:ss";
        String dateStr = "2019/10/17";
        DateType dateType = new DateType();
        dateType.setProperties("dateFormat=" + dateFormat);
        try {
            dateType.getValue(dateStr);
            fail("Expected an exception");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), "Unparseable date: \"2019/10/17\"");
            assertEquals(ParseException.class, cex.getCause().getClass());
        }
    }

    /**
     * Test of getValue method, of class DateType.
     */
    @Test
    public void testSetDefault() throws Exception {
        String dateFormat = "yyyy/MM/dd";
        String dateStr = "2019/10/17";
        DateType dateType = new DateType();
        dateType.setProperties("dateFormat=" + dateFormat);
        dateType.setDefault(dateStr);
        assertEquals(dateStr, dateType.toString());
    }

    /**
     * Test of getValue method, of class DateType.
     */
    @Test
    public void testSetPropertiesThrowsException() throws Exception {
        DateType dateType = new DateType();
        try {
            dateType.setProperties("what-no-equals");
            fail("Expected an exception");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), "Invalid properties: what-no-equals");
        }
    }

    /**
     * Test of getValue method, of class DateType.
     */
    @Test
    public void testSetBadPropertyThrowsException() throws Exception {
        DateType dateType = new DateType();
        try {
            dateType.setProperties("foo-prop=throwsException");
            fail("Expected an exception");
        } catch (ValueTypeCreationException cex) {
            assertEquals(cex.getMessage(), "Invalid property: "
                    + "foo-prop; expected property 'dateFormat'");
        }
    }
    
    /**
     * 
     */
    @Test
    public void testGetPackageName() {
        DateType dateType = new DateType();
        assertEquals("java.util", dateType.getPackageName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetJavaTypeName() {
        DateType dateType = new DateType();
        assertEquals("Date", dateType.getJavaClassName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetValueTypeName() {
        DateType dateType = new DateType();
        assertEquals("date", dateType.getValueTypeName());
    }
    
    /**
     * 
     */
    @Test
    public void testGetJavaPrimitiveName() {
        DateType dateType = new DateType();
        assertNull(dateType.getJavaPrimitiveName());
    }

    /**
     * Test of toString method, of class DateType.
     */
    @Test
    public void testToString() throws Exception {
        String dateFormat = "yyyy/MM/dd HH:mm:ss";
        DateType dateType = new DateType();
        dateType.setProperties("dateFormat=" + dateFormat);
        String dateStr = "2019/10/17 17:26:26";
        dateType.getValue(dateStr);
        assertEquals(dateStr, dateType.toString());
    }

}
