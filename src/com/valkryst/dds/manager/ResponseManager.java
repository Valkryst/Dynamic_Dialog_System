package com.valkryst.dds.manager;

import com.valkryst.dds.object.Response;

import java.io.Serializable;

public abstract class ResponseManager implements Serializable {
    private static final long serialVersionUID = 5352280441453602823L;

    /** The AudioManager to use when fufilling audio responses. */
    protected static final AudioManager AUDIO_MANAGER = new AudioManager();

    /**
     * Construct a new ResponseManager.
     *
     * @param dataManager
     *         The com.valkryst.data manager that handles all com.valkryst.data within the Dynamic Dialog System.
     *
     * @param response
     *         The response to fulfill.
     */
    public abstract void respond(final DataManager dataManager, final Response response);
}

