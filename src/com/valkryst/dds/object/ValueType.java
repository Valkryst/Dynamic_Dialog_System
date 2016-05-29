package com.valkryst.dds.object;

import java.io.Serializable;

public enum ValueType implements Serializable {
    BYTE,
    SHORT,
    INTEGER,
    LONG,
    FLOAT,
    DOUBLE,
    BOOLEAN,
    STRING,

    MP3,
    OGG,
    WAV;

    /**
     * Determines and returns the ValueType enum represented by the
     * specified String.
     *
     * @param valueType
     *         A String that is, or contains, a name of a ValueType
     *         enum.
     *
     * @return
     *         The ValueType represented by the specified String.
     *
     * @throws IllegalArgumentException
     *          If the method is unable to determine the type from
     *          the specified String,.
     */
    public static ValueType getValueTypeByName(final String valueType) throws IllegalArgumentException {
        for(final ValueType type : ValueType.values()) {
            if(type.name().toLowerCase().equals(valueType.toLowerCase())) {
                return type;
            }
        }

        throw new IllegalArgumentException("A ValueType enum described by the String '" +
                                           valueType + "' does not exit.");
    }
}
