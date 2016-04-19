package com.valkryst.dds.object;

import com.valkryst.dds.collection.SplayTree;

import java.io.Serializable;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class User implements Serializable {
    private static final long serialVersionUID = 7804586155533229144L;

    /** A unique ID. */
    private final long id;

    /** The SplayTree containing all currently loaded Contexts, with their Names as Keys. */
    private SplayTree<String, Context> splayTree_context = new SplayTree<>();

    /** The Lock of the splayTree_context data structure. */
    private ReentrantReadWriteLock lock_splayTree_context = new ReentrantReadWriteLock();

    /**
     * Construct a new User.
     *
     * @param id
     *         The unique ID of the user.
     */
    public User(final long id) {
        this.id = id;
    }

    /** @return The unique ID of the user. */
    public long getId() {
        return id;
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
        lock_splayTree_context.readLock().lock();

        final Context context = splayTree_context.get(contextName);

        lock_splayTree_context.readLock().unlock();

        return context;
    }

    /** @return The Lock of the splayTree_context data structure. */
    public ReentrantReadWriteLock getLock_splayTree_context() {
        return lock_splayTree_context;
    }
}
