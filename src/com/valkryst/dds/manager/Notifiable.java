package com.valkryst.dds.manager;

import com.valkryst.dds.object.Response;

public interface Notifiable {
    /**
     * Construct a new ResponseManager.
     *
     * @param ddsManager
     *         The data manager that handles all cdata within the Dynamic Dialog System.
     *
     * @param response
     *         The response to fulfill.
     */
    void handleResponse(final DDSManager ddsManager, final Response response);
}
