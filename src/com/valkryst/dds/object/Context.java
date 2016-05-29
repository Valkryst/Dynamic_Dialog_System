package com.valkryst.dds.object;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ToString
public class Context implements Comparable<Context>, Serializable {
    private static final long serialVersionUID = 6080325315007575932L;

    /** A descriptive Name. */
    @Getter private final String name;
    /** The type of data held within the value variable. */
    @Getter private final ValueType valueType;
    /** The raw data. */
    @Getter @Setter @NonNull private String value;

    /**
     * Construct a new Context with the specified data.
     *
     * @param name
     *         A descriptive name.
     *
     * @param valueType
     *         The type of data held within the value variable.
     *
     * @param value
     *         The raw data.
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
}
