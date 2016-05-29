package com.valkryst.test.object;

import com.valkryst.dds.object.Context;
import com.valkryst.dds.object.User;
import com.valkryst.dds.object.ValueType;
import org.junit.Assert;
import org.junit.Test;

public class UserTest {
    @Test
    public void User() {
        // The value of 100 was arbitrarily chosen.
        for(int i = 0 ; i < 100 ; i++) {
            new User(i);
        }
    }

    @Test
    public void getContextByName() {
        final User user = new User(0);

        Context contextA = new Context("ContextA", ValueType.BYTE, "1");
        Context contextB = new Context("Contexta", ValueType.BYTE, "2");
        Context contextC = new Context("contextA", ValueType.BYTE, "3");
        Context contextD = new Context("contexta", ValueType.BYTE, "4");

        user.getSplayTree_context().put(contextA.getName(), contextA);
        user.getSplayTree_context().put(contextB.getName(), contextB);
        user.getSplayTree_context().put(contextC.getName(), contextC);
        user.getSplayTree_context().put(contextD.getName(), contextD);

        contextA = user.getContextByName(contextA.getName());
        contextB = user.getContextByName(contextB.getName());
        contextC = user.getContextByName(contextC.getName());
        contextD = user.getContextByName(contextD.getName());

        Assert.assertEquals(contextA.getValue(), "1");
        Assert.assertEquals(contextB.getValue(), "2");
        Assert.assertEquals(contextC.getValue(), "3");
        Assert.assertEquals(contextD.getValue(), "4");
    }
}
