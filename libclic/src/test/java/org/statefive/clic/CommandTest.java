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
package org.statefive.clic;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TestName;

/**
 *
 * @author rich
 */
public class CommandTest {
    
    /**
     * Test name.
     */
    @Rule
    public TestName testName = new TestName();
    
    /**
     * Test that a command child can be added to a parent command.
     */
    @Test
    public void testAddChild() {
        Command parent = new Command();
        parent.setName(testName.getMethodName());
        Command child = new Command();
        String childName = "child";
        child.setName(childName);
        assertTrue(parent.addChild(child));
        assertEquals(parent.getChildren().get(0), child);
    }
    
    /**
     * Test that adding the same command child fails and only one command is
     * added to the parent.
     */
    @Test
    public void testAddChildFailsForDuplicate() {
        Command parent = new Command();
        parent.setName(testName.getMethodName());
        Command child = new Command();
        String childName = "child";
        child.setName(childName);
        assertTrue(parent.addChild(child));
        Command duplicateChild = new Command();
        duplicateChild.setName(childName);
        assertFalse(parent.addChild(child));
        assertEquals(parent.getChildren().get(0), child);
        assertEquals(1, parent.getChildren().size());
    }

    /**
     * Test that getting the parent has the correct result.
     */
    @Test
    public void testGetParent() {
        Command parent = new Command();
        parent.setName(testName.getMethodName());
        Command child = new Command();
        String childName = "child";
        child.setName(childName);
        parent.addChild(child);
        assertEquals(parent, child.getParent());
    }

    /**
     * Test set and get name.
     */
    @Test
    public void testSetGetName() {
        Command instance = new Command();
        String expResult = testName.getMethodName();
        instance.setName(expResult);
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getUsage method, of class Command.
     */
    @Test
    public void testGetUsage() {
        Command instance = new Command();
        String expResult = testName.getMethodName();
        instance.setUsage(expResult);
        String result = instance.getUsage();
        assertEquals(expResult, result);
    }

    /**
     * Test get path for a root/top-level command.
     */
    @Test
    public void testGetPath() {
        Command command = new Command();
        command.setName(testName.getMethodName());
        assertEquals(testName.getMethodName(), command.getPath());
    }

    /**
     * Test that a root/top-level command can have a child added to it and a
     * grandchild added to the child.
     */
    @Test
    public void testGetPathForNestedCommands() {
        Command parent = new Command();
        parent.setName(testName.getMethodName());
        Command child = new Command();
        String childName = "child";
        child.setName(childName);
        parent.addChild(child);
        Command grandChild = new Command();
        String grandChildName = "grandChild";
        grandChild.setName(grandChildName);
        child.addChild(grandChild);
        assertEquals(testName.getMethodName() + "/" + childName + "/" + grandChildName, 
                grandChild.getPath());
    }

    /**
     * Test that adding two different children works when both children have
     * different names.
     */
    @Test
    public void testGetChildren() {
        Command parent = new Command();
        parent.setName("parent");
        Command c1 = new Command();
        c1.setName("c1");
        Command c2 = new Command();
        c2.setName("c2");
        parent.addChild(c1);
        parent.addChild(c2);
        assertEquals(2, parent.getChildren().size());
        assertEquals(c1, parent.getChildren().get(0));
        assertEquals(c2, parent.getChildren().get(1));
    }

    /**
     * Test of equals method, of class Command.
     */
    @Test
    public void testEquals() {
        Command c1 = new Command();
        c1.setName("c1");
        Command c2 = new Command();
        c2.setName("c1");
        assertTrue(c1.equals(c2));
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    /**
     * Test of equals method, of class Command.
     */
    @Test
    public void testNotEquals() {
        Command c1 = new Command();
        c1.setName("c1");
        Command c2 = new Command();
        c2.setName("c2");
        assertFalse(c1.equals(c2));
    }

    /**
     * Test of equals method, of class Command.
     */
    @Test
    public void testNotEqualsNull() {
        Command c1 = new Command();
        c1.setName("c1");
        assertFalse(c1.equals(null));
    }

    /**
     * Test of equals method, of class Command.
     */
    @Test
    public void testNotEqualsString() {
        Command c1 = new Command();
        c1.setName("c1");
        assertFalse(c1.equals("c1"));
    }

    /**
     * Test of equals method, of class Command.
     */
    @Test
    public void testEqualsChildrenNoParent() {
        Command c1 = new Command();
        c1.setName("c1");
        Command c2 = new Command();
        c2.setName("c1");
        assertTrue(c1.equals(c2));
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    /**
     * Test of equals method, of class Command.
     */
    @Test
    public void testNotEqualsChildrenParentOnLhs() {
        Command parentC1 = new Command();
        parentC1.setName("parent");
        Command c1 = new Command();
        c1.setName("c1");
        parentC1.addChild(c1);
        Command c2 = new Command();
        c2.setName("c1");
        assertFalse(c1.equals(c2));
    }

    /**
     * Test of equals method, of class Command.
     */
    @Test
    public void testNotEqualsChildrenParentOnRhs() {
        Command c1 = new Command();
        c1.setName("c1");
        Command c2 = new Command();
        c2.setName("c1");
        Command parentC1 = new Command();
        parentC1.setName("parent");
        parentC1.addChild(c2);
        assertFalse(c1.equals(c2));
    }

    /**
     * Test of equals method, of class Command.
     */
    @Test
    public void testNotEqualsChildrenBothSidesWithParents() {
        Command parentC1 = new Command();
        parentC1.setName("parent1");
        Command c1 = new Command();
        c1.setName("c1");
        parentC1.addChild(c1);
        Command c2 = new Command();
        c2.setName("c1");
        Command parentC2 = new Command();
        parentC2.setName("parent2");
        parentC2.addChild(c2);
        assertFalse(c1.equals(c2));
    }

    /**
     * Test that a root/top-level command can have a child added to it and a
     * grandchild added to the child.
     */
    @Test
    public void testToStringForTopLevelCommand() {
        Command parent = new Command();
        parent.setName(testName.getMethodName());
        assertEquals(testName.getMethodName(), parent.toString());
    }

    /**
     * Test that a root/top-level command can have a child added to it and a
     * grandchild added to the child.
     */
    @Test
    public void testToStringForNestedCommands() {
        Command parent = new Command();
        parent.setName(testName.getMethodName());
        Command child = new Command();
        String childName = "child";
        child.setName(childName);
        parent.addChild(child);
        Command grandChild = new Command();
        String grandChildName = "grandChild";
        grandChild.setName(grandChildName);
        child.addChild(grandChild);
        assertEquals(testName.getMethodName() + "/" + childName + "/" + grandChildName, 
                grandChild.toString());
    }
    
}
