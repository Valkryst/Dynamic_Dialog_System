package com.valkryst.dds.object;

import com.valkryst.dds.collection.SplayTree;
import lombok.Getter;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 7804586155533229144L;

    /** A unique ID. */
    @Getter private final long id;

    /** The SplayTree containing all currently loaded Contexts, with their Names as Keys. */
    private SplayTree<String, Context> splayTree_context = new SplayTree<>();

    /**
     * Construct a new User.
     *
     * @param id
     *         The unique ID of the user.
     */
    public User(final long id) {
        this.id = id;
    }

    /** @return The SplayTree containing all currently loaded Contexts, with their Names as Keys. */
    public SplayTree<String, Context> getSplayTree_context() {
        return splayTree_context;
    }

    /**
     * Attempts to locate, and return, a Context by it's name.
     *
     * @param contextName
     *         The Context name to search for.
     *
     * @return
     *         If a context using the specified name exists, then it is returned.
     *         Else NULL is returned.
     */
    public Context getContextByName(final String contextName) {
        return splayTree_context.get(contextName);
    }
}
