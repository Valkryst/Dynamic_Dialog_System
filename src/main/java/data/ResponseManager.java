package data;

import data.collection.DataManager;
import handler.AudioHandler;

import java.io.Serializable;

public abstract class ResponseManager implements Serializable {
    private static final long serialVersionUID = 5352280441453602823L;

    /** The AudioHandler to use when fufilling audio responses. */
    protected static final AudioHandler AUDIO_HANDLER = new AudioHandler();

    /**
     * Construct a new ResponseManager.
     *
     * @param dataManager
     *         The data manager that handles all data within the Dynamic Dialog System.
     *
     * @param response
     *         The response to fulfill.
     */
    public abstract void respond(final DataManager dataManager, final Response response);
}

