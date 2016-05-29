package com.valkryst.test.object;

import com.valkryst.dds.object.ValueType;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ValueTypeTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void getValueTypeByName() {
        // Test success:
        for(final ValueType type : ValueType.values()) {
            final String name = type.name().toLowerCase();
            final ValueType result = ValueType.getValueTypeByName(name);

            Assert.assertEquals(type, result);
        }


        // Test failure:
        final String[] madeUpValueTypes = {"blah", "blob", "blue", "btye", "intargar", "shourt", "boorean"};

        for(final String s : madeUpValueTypes) {
            final String name = s.toLowerCase();


            expectedException.expect(IllegalArgumentException.class);
            final ValueType result = ValueType.getValueTypeByName(name);
        }
    }
}
