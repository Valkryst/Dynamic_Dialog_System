package com.valkryst.dds.object;

import java.io.Serializable;
import java.util.Objects;

public class Criterion implements Serializable {
    private static final long serialVersionUID = 183257350555918563L;

    /** The Context to compare to the comparisonValue when evaluating whether or not the Criterion isTrue. */
    private final Context context;
    /** The operator to use when comparing the Context and comparisonValue. */
    private final ComparisonType comparisonType;
    /** The type of data held within the comparisonValue variable. */
    private final ValueType comparisonValue_valueType;
    /** The value to be compared with the Context when evaluating whether or not the Criterion isTrue. */
    private final String comparisonValue;
    /** Whether the Criterion evaluates to true or false. */
    private boolean isTrue;

    /**
     * Construct a new Criterion with the specified data.
     *
     * @param context
     *         The Context to compare to the comparisonValue when evaluating whether or not the Criterion isTrue.
     *
     * @param comparisonType
     *         The operator to use when comparing the Context and comparisonValue.
     *
     * @param comparisonValue_valueType
     *         The type of data held within the comparisonValue variable.
     *
     * @param comparisonValue
     *         The value to be compared with the Context when evaluating whether or not the Criterion isTrue.
     *
     * @param isTrue
     *         Whether the Criterion evaluates to true or false.
     */
    public Criterion(final Context context, final ComparisonType comparisonType, final ValueType comparisonValue_valueType, final String comparisonValue, final boolean isTrue) {
        this.context = context;
        this.comparisonType = comparisonType;
        this.comparisonValue_valueType = comparisonValue_valueType;
        this.comparisonValue = comparisonValue;
        this.isTrue = isTrue;
    }

    /**
     * Updates the state of the Criterion, specifically whether or not the Criterion evaluates
     * to TRUE or FALSE.
     *
     * @throws UnsupportedOperationException
     *          Thrown if the ValueType of the Criterion's value and the ValueType of the
     *          Context to compare with are different.
     *
     *          Thrown if the ComparisonType to use, when comparing the Criterion's value to
     *          the value of the Context, cannot be used with the ValueType of the values.
     */
    public void update() throws UnsupportedOperationException {
        if(context.getValueType() != comparisonValue_valueType) {
            throw new UnsupportedOperationException("The value type of the following Context does not match the " +
                    "ComparisonValue_ValueType of this Criterion.\n" +
                    context.toString() + comparisonValue_valueType);
        }

        switch(comparisonValue_valueType) {
            case BYTE: {
                final Byte thisByte = Byte.valueOf(comparisonValue);
                final Byte otherByte = Byte.valueOf(context.getValue());

                switch(comparisonType) {
                    case EQUAL_TO: {
                        isTrue = (Objects.equals(thisByte, otherByte));
                        break;
                    }

                    case LESS_THAN: {
                        isTrue = (thisByte < otherByte);
                        break;
                    }

                    case GREATER_THAN: {
                        isTrue = (thisByte > otherByte);
                        break;
                    }

                    case LESS_THAN_OR_EQUAL_TO: {
                        isTrue = (thisByte <= otherByte);
                        break;
                    }

                    case GREATER_THAN_OR_EQUAL_TO: {
                        isTrue = (thisByte >= otherByte);
                        break;
                    }

                    case NOT_EQUAL_TO: {
                        isTrue = (!Objects.equals(thisByte, otherByte));
                        break;
                    }

                    default: {
                        throw new UnsupportedOperationException("The specified comparison type '" + comparisonType + "' cannot be used with the BYTE value type.");
                    }
                }

                break;
            }

            case SHORT: {
                final Short thisShort = Short.valueOf(comparisonValue);
                final Short otherShort = Short.valueOf(context.getValue());

                switch(comparisonType) {
                    case EQUAL_TO: {
                        isTrue = (Objects.equals(thisShort, otherShort));
                        break;
                    }

                    case LESS_THAN: {
                        isTrue = (thisShort < otherShort);
                        break;
                    }

                    case GREATER_THAN: {
                        isTrue = (thisShort > otherShort);
                        break;
                    }

                    case LESS_THAN_OR_EQUAL_TO: {
                        isTrue = (thisShort <= otherShort);
                        break;
                    }

                    case GREATER_THAN_OR_EQUAL_TO: {
                        isTrue = (thisShort >= otherShort);
                        break;
                    }

                    case NOT_EQUAL_TO: {
                        isTrue = (!Objects.equals(thisShort, otherShort));
                        break;
                    }

                    default: {
                        throw new UnsupportedOperationException("The specified comparison type '" + comparisonType + "' cannot be used with the SHORT value type.");
                    }
                }

                break;
            }

            case INTEGER: {
                final Integer thisInteger = Integer.valueOf(comparisonValue);
                final Integer otherInteger = Integer.valueOf(context.getValue());

                switch(comparisonType) {
                    case EQUAL_TO: {
                        isTrue = (Objects.equals(thisInteger, otherInteger));
                        break;
                    }

                    case LESS_THAN: {
                        isTrue = (thisInteger < otherInteger);
                        break;
                    }

                    case GREATER_THAN: {
                        isTrue = (thisInteger > otherInteger);
                        break;
                    }

                    case LESS_THAN_OR_EQUAL_TO: {
                        isTrue = (thisInteger <= otherInteger);
                        break;
                    }

                    case GREATER_THAN_OR_EQUAL_TO: {
                        isTrue = (thisInteger >= otherInteger);
                        break;
                    }

                    case NOT_EQUAL_TO: {
                        isTrue = (!Objects.equals(thisInteger, otherInteger));
                        break;
                    }

                    default: {
                        throw new UnsupportedOperationException("The specified comparison type '" + comparisonType + "' cannot be used with the INTEGER value type.");
                    }
                }

                break;
            }

            case LONG: {
                final Long thisLong = Long.valueOf(comparisonValue);
                final Long otherLong = Long.valueOf(context.getValue());

                switch(comparisonType) {
                    case EQUAL_TO: {
                        isTrue = (Objects.equals(thisLong, otherLong));
                        break;
                    }

                    case LESS_THAN: {
                        isTrue = (thisLong < otherLong);
                        break;
                    }

                    case GREATER_THAN: {
                        isTrue = (thisLong > otherLong);
                        break;
                    }

                    case LESS_THAN_OR_EQUAL_TO: {
                        isTrue = (thisLong <= otherLong);
                        break;
                    }

                    case GREATER_THAN_OR_EQUAL_TO: {
                        isTrue = (thisLong >= otherLong);
                        break;
                    }

                    case NOT_EQUAL_TO: {
                        isTrue = (!Objects.equals(thisLong, otherLong));
                        break;
                    }

                    default: {
                        throw new UnsupportedOperationException("The specified comparison type '" + comparisonType + "' cannot be used with the LONG value type.");
                    }
                }

                break;
            }

            case FLOAT: {
                final Float thisFloat = Float.valueOf(comparisonValue);
                final Float otherFloat = Float.valueOf(context.getValue());

                switch(comparisonType) {
                    case EQUAL_TO: {
                        isTrue = (Objects.equals(thisFloat, otherFloat));
                        break;
                    }

                    case LESS_THAN: {
                        isTrue = (thisFloat < otherFloat);
                        break;
                    }

                    case GREATER_THAN: {
                        isTrue = (thisFloat > otherFloat);
                        break;
                    }

                    case LESS_THAN_OR_EQUAL_TO: {
                        isTrue = (thisFloat <= otherFloat);
                        break;
                    }

                    case GREATER_THAN_OR_EQUAL_TO: {
                        isTrue = (thisFloat >= otherFloat);
                        break;
                    }

                    case NOT_EQUAL_TO: {
                        isTrue = (!Objects.equals(thisFloat, otherFloat));
                        break;
                    }

                    default: {
                        throw new UnsupportedOperationException("The specified comparison type '" + comparisonType + "' cannot be used with the FLOAT value type.");
                    }
                }

                break;
            }

            case DOUBLE: {
                final Double thisDouble = Double.valueOf(comparisonValue);
                final Double otherDouble = Double.valueOf(context.getValue());

                switch(comparisonType) {
                    case EQUAL_TO: {
                        isTrue = (Objects.equals(thisDouble, otherDouble));
                        break;
                    }

                    case LESS_THAN: {
                        isTrue = (thisDouble < otherDouble);
                        break;
                    }

                    case GREATER_THAN: {
                        isTrue = (thisDouble > otherDouble);
                        break;
                    }

                    case LESS_THAN_OR_EQUAL_TO: {
                        isTrue = (thisDouble <= otherDouble);
                        break;
                    }

                    case GREATER_THAN_OR_EQUAL_TO: {
                        isTrue = (thisDouble >= otherDouble);
                        break;
                    }

                    case NOT_EQUAL_TO: {
                        isTrue = (!Objects.equals(thisDouble, otherDouble));
                        break;
                    }

                    default: {
                        throw new UnsupportedOperationException("The specified comparison type '" + comparisonType + "' cannot be used with the DOUBLE value type.");
                    }
                }

                break;
            }


            case BOOLEAN: {
                final Boolean thisBoolean = Boolean.valueOf(comparisonValue);
                final Boolean otherBoolean = Boolean.valueOf(context.getValue());

                switch(comparisonType) {
                    case EQUAL_TO: {
                        isTrue = (thisBoolean == otherBoolean);
                        break;
                    }

                    case NOT_EQUAL_TO: {
                        isTrue = (thisBoolean != otherBoolean);
                        break;
                    }

                    default: {
                        throw new UnsupportedOperationException("The specified comparison type '" + comparisonType + "' cannot be used with the BOOLEAN value type.");
                    }
                }

                break;
            }

            case STRING: {
                switch(comparisonType) {
                    case EQUAL_TO: {
                        isTrue = (comparisonValue.equals(context.getValue()));
                        break;
                    }

                    case NOT_EQUAL_TO: {
                        isTrue = (! comparisonValue.equals(context.getValue()));
                        break;
                    }

                    default: {
                        throw new UnsupportedOperationException("The specified comparison type '" + comparisonType + "' cannot be used with the STRING value type.");
                    }
                }

                break;
            }
        }
    }

    @Override
    public String toString() {
        return "Criterion:\n" +
                "\tContext:\n" +
                "\t\t" + context.toString() + "\n" +
                "\tComparisonType:\n" +
                "\t\t" + comparisonType.name() + "\n" +
                "\tComparisonValue:\n" +
                "\t\t" + comparisonValue + "\n" +
                "\tIsTrue:\n" +
                "\t\t" + isTrue + "\n";
    }

    /** @return The Context to compare to the comparisonValue when evaluating whether or not the Criterion isTrue. */
    public Context getContext() {
        return context;
    }

    /** @return The operator to use when comparing the Context and comparisonValue. */
    public ComparisonType getComparisonType() {
        return comparisonType;
    }

    /** @return The value to be compared with the Context when evaluating whether or not the Criterion isTrue. */
    public String getComparisonValue() {
        return comparisonValue;
    }

    /** @return Whether the Criterion evaluates to true or false. */
    public boolean getIsTrue() {
        return isTrue;
    }
}
