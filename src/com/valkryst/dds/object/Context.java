package com.valkryst.dds.object;

import java.io.Serializable;

public class Context implements Comparable<Context>, Serializable {
    private static final long serialVersionUID = 6080325315007575932L;

    /** A descriptive Name. */
    private final String name;
    /** The type of com.valkryst.data held within the value variable. */
    private final ValueType valueType;
    /** The raw com.valkryst.data. */
    private String value;

    /**
     * Construct a new Context with the specified com.valkryst.data.
     *
     * @param name
     *         A descriptive name.
     *
     * @param valueType
     *         The type of com.valkryst.data held within the value variable.
     *
     * @param value
     *         The raw com.valkryst.data.
     */
    public Context(final String name, final ValueType valueType, final String value) {
        this.name = name;
        this.valueType = valueType;
        this.value = value;
    }

    @Override
    public int compareTo(Context context) {
        return name.compareTo(context.getName());
    }

    @Override
    public String toString() {
        return "Context:\n" +
                "\tName:\n" +
                "\t\t" + name + "\n" +
                "\tValueType:\n" +
                "\t\t" + valueType.name() + "\n" +
                "\tValue:\n" +
                "\t\t" + value + "\n";
    }

    /** @return A descriptive name. */
    public String getName() {
        return name;
    }

    /** The type of com.valkryst.data held within the value variable. */
    public ValueType getValueType() {
        return valueType;
    }

    /** The raw com.valkryst.data. */
    public String getValue() {
        return value;
    }

    /**
     * Set the raw com.valkryst.data for the Context to use.
     *
     * @param value
     *         The new raw com.valkryst.data to be used by the Context.
     */
    public void setValue(final String value) {
        this.value = value;
    }
}
