package com.valkryst.dds.object;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@ToString
public class Response implements Serializable {
    private static final long serialVersionUID = 2097470428838106098L;

    /** The type of response. */
    @Getter private final String responseType;
    /** The data which may or may not be used depending on the response type. */
    @Getter private final String value;

    /**
     * Construct a new Response with the specified data.
     *
     * @param responseType
     *         The type of response.
     *
     * @param value
     *         The raw data.
     */
    public Response(final String responseType, final String value) {
        this.responseType = responseType;
        this.value = value;
    }
}
