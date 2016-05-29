package com.valkryst.test.object;

import com.valkryst.dds.object.Context;
import com.valkryst.dds.object.ValueType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class ContextTest {
    @Test
    public void compareTo() {
        // Test to see if a Context is equal to itself:
        final Context contextA = new Context("TestA", ValueType.BOOLEAN, "TRUE");
        assertEquals(contextA.compareTo(contextA), 0);

        // Test to see if two different Contexts aren't equal:
        final Context contextB = new Context("TestB", ValueType.BOOLEAN, "TRUE");
        assertNotSame(contextA.compareTo(contextB), 0);
    }

    @Test
    public void getName() {
        final Context contextA = new Context("TestA", ValueType.BOOLEAN, "TRUE");
        assertEquals("TestA", contextA.getName());
    }

    @Test
    public void getValueType() {
        final Context contextA = new Context("TestA", ValueType.BOOLEAN, "TRUE");
        assertEquals(ValueType.BOOLEAN, contextA.getValueType());
    }

    @Test
    public void getValue() {
        final Context contextA = new Context("TestA", ValueType.BOOLEAN, "TRUE");
        assertEquals("TRUE", contextA.getValue());
    }

    @Test
    public void setValue() {
        final Context contextA = new Context("TestA", ValueType.BOOLEAN, "TRUE");
        contextA.setValue("FALSE");
        assertEquals("FALSE", contextA.getValue());
    }
}
