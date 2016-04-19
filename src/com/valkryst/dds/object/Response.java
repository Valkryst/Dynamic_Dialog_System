package com.valkryst.dds.object;

import java.io.Serializable;

public class Response implements Serializable {
    private static final long serialVersionUID = 2097470428838106098L;

    /** The type of response. */
    private final String responseType;
    /** The data which may or may not be used depending on the response type. */
    private final String value;

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

    @Override
    public String toString() {
        return "Response:\n" +
                "\tResponseType:\n" +
                "\t\t" + responseType + "\n" +
                "\tValue:\n" +
                "\t\t" + value + "\n";
    }

    /** The type of response. */
    public String getResponseType() {
        return responseType;
    }

    /** The raw data. */
    public String getValue() {
        return value;
    }
}
