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
     * Determines and returns the ValueType enum represented by the specified String.
     *
     * If the String isn't exactly "BYTE", "SHORT", "INTEGER", "MP3", etc... then the
     * method can be told to take a guess, by checking if the type's name is contained
     * anywhere within the String, to determine the Value Type.
     *
     * @param valueType
     *         A String that is, or contains, a name of a ValueType enum.
     *
     * @param attemptToGuess
     *         Whether or not the method should attempt to guess which type the String
     *         represents.
     *
     * @return
     *         The ValueType represented by the specified String.
     *
     * @throws IllegalArgumentException
     *          If the method is unable to determine the type from the specified String,
     *          then this exception is thrown.
     */
    public static ValueType getValueTypeByName(final String valueType, final boolean attemptToGuess) throws IllegalArgumentException {
        switch(valueType.toUpperCase()) {
            case "BYTE": {
                return BYTE;
            }
            case "SHORT": {
                return SHORT;
            }
            case "INTEGER": {
                return INTEGER;
            }
            case "LONG": {
                return LONG;
            }
            case "FLOAT": {
                return FLOAT;
            }
            case "DOUBLE": {
                return DOUBLE;
            }
            case "BOOLEAN": {
                return BOOLEAN;
            }
            case "STRING": {
                return STRING;
            }
            case "MP3": {
                return MP3;
            }
            case "OGG": {
                return OGG;
            }
            case "WAV": {
                return WAV;
            }
            default: {
                if(attemptToGuess) {
                    if(valueType.toUpperCase().contains("BYTE")) {
                        return BYTE;
                    } else if(valueType.toUpperCase().contains("SHORT")) {
                        return SHORT;
                    } else if(valueType.toUpperCase().contains("INTEGER")) {
                        return INTEGER;
                    } else if(valueType.toUpperCase().contains("LONG")) {
                        return LONG;
                    } else if(valueType.toUpperCase().contains("FLOAT")) {
                        return FLOAT;
                    } else if(valueType.toUpperCase().contains("DOUBLE")) {
                        return DOUBLE;
                    } else if(valueType.toUpperCase().contains("BOOLEAN")) {
                        return BOOLEAN;
                    } else if(valueType.toUpperCase().contains("STRING")) {
                        return STRING;
                    } else if(valueType.toUpperCase().contains("MP3")) {
                        return MP3;
                    } else if(valueType.toUpperCase().contains("OGG")) {
                        return OGG;
                    } else if(valueType.toUpperCase().contains("WAV")) {
                        return WAV;
                    }
                }

                throw new IllegalArgumentException("A ValueType enum described by the String '" +
                                                    valueType + "' does not exit.");
            }
        }
    }
}
