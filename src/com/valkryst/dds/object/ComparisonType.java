package com.valkryst.dds.object;

import java.io.Serializable;

public enum ComparisonType implements Serializable {
    EQUAL_TO,
    LESS_THAN,
    GREATER_THAN,
    LESS_THAN_OR_EQUAL_TO,
    GREATER_THAN_OR_EQUAL_TO,
    NOT_EQUAL_TO;

    /**
     * Determines and returns the ComparisonType enum represented by the specified String.
     *
     * @param comparisonType
     *         A String that is either a name of a ValueType enum or it's symbolic representation.
     *
     * @return
     *         The ComparisonType represented by the specified String.
     *
     * @throws IllegalArgumentException
     *          If the method is unable to determine the type from the specified String,
     *          then this exception is thrown.
     */
    public static ComparisonType getComparisonTypeByName(final String comparisonType) throws IllegalArgumentException {
        switch(comparisonType) {
            case "=" : {
                return EQUAL_TO;
            }
            case "EQUAL_TO": {
                return EQUAL_TO;
            }
            case "<": {
                return LESS_THAN;
            }
            case "LESS_THAN": {
                return LESS_THAN;
            }
            case ">": {
                return GREATER_THAN;
            }
            case "GREATER_THAN": {
                return GREATER_THAN;
            }
            case "<=": {
                return LESS_THAN_OR_EQUAL_TO;
            }
            case "LESS_THAN_OR_EQUAL_TO": {
                return LESS_THAN_OR_EQUAL_TO;
            }
            case ">=": {
                return GREATER_THAN_OR_EQUAL_TO;
            }
            case "GREATER_THAN_OR_EQUAL_TO": {
                return GREATER_THAN_OR_EQUAL_TO;
            }
            case "!=": {
                return NOT_EQUAL_TO;
            }
            case "NOT_EQUAL_TO": {
                return NOT_EQUAL_TO;
            }
            default: {
                throw new IllegalArgumentException("A ComparisonType enum described by the String '" +
                        comparisonType + "' does not exit.");
            }
        }
    }
}
