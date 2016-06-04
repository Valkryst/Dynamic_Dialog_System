package com.valkryst.dds.object;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Objects;

@ToString
public class Criterion implements Serializable {
    private static final long serialVersionUID = -7479779125882019268L;

    /** The Context to compare to the comparisonValue when evaluating whether or not the Criterion isTrue. */
    @Getter private final Context context;
    /** The operator to use when comparing the Context and comparisonValue. */
    @Getter private final ComparisonType comparisonType;
    /** The type of data held within the comparisonValue variable. */
    private final ValueType comparisonValue_valueType;
    /** The value to be compared with the Context when evaluating whether or not the Criterion isTrue. */
    @Getter private final String comparisonValue;
    /** The weight, or importance, of the Criterion to be used when determining whether or not a Rule should be responded to. */
    @Getter private double weight;
    /** Whether the Criterion evaluates to true or false. */
    @Getter private boolean isTrue;

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
     * @param weight
     *         The weight, or importance, of the Criterion to be used when determining whether or not a Rule should be responded to.
     *
     * @param isTrue
     *         Whether the Criterion evaluates to true or false.
     */
    public Criterion(final Context context, final ComparisonType comparisonType, final ValueType comparisonValue_valueType, final String comparisonValue, final double weight, final boolean isTrue)  throws IllegalArgumentException {
        this.context = context;
        this.comparisonType = comparisonType;
        this.comparisonValue_valueType = comparisonValue_valueType;
        this.comparisonValue = comparisonValue;
        this.isTrue = isTrue;

        // Ensure the specified weight is within the allowed range.
        if(weight > 1) {
            throw new IllegalArgumentException("A Criterion cannot have a weight greater than 1.");
        } else if(weight < 0) {
            throw new IllegalArgumentException("A Criterion cannot have a weight less than 0.");
        }
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
                final byte thisByte = Byte.parseByte(comparisonValue);
                final byte otherByte = Byte.parseByte(context.getValue());

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
                final short thisShort = Short.parseShort(comparisonValue);
                final short otherShort = Short.parseShort(context.getValue());

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
                final int thisInteger = Integer.parseInt(comparisonValue);
                final int otherInteger = Integer.parseInt(context.getValue());

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
                final long thisLong = Long.parseLong(comparisonValue);
                final long otherLong = Long.parseLong(context.getValue());

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
                final float thisFloat = Float.parseFloat(comparisonValue);
                final float otherFloat = Float.parseFloat(context.getValue());

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
                final double thisDouble = Double.parseDouble(comparisonValue);
                final double otherDouble = Double.parseDouble(context.getValue());

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
                final boolean thisBoolean = Boolean.parseBoolean(comparisonValue);
                final boolean otherBoolean = Boolean.parseBoolean(context.getValue());

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
}
