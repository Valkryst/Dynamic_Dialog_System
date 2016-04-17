package com.valkryst.dds.object;

import java.io.Serializable;

public class Rule implements Serializable {
    private static final long serialVersionUID = -2662752086898875054L;

    /** The Event which triggers the use of the Rule. */
    private final String event;
    /** A description of the Rule. */
    private final String description;
    /** The time at which the Rule was last used. */
    private long lastUsedTime;

    /**
     * Construct a new Rule with the specified com.valkryst.data.
     *
     * @param event
     *         The Event which triggers the use of the Rule.
     *
     * @param description
     *         A description of the Rule.
     *
     * @param lastUsedTime
     *         The time at which the Rule was last used.
     */
    public Rule(final String event, final String description, final long lastUsedTime) {
        this.event = event;
        this.description = description;
        this.lastUsedTime = lastUsedTime;
    }

    @Override
    public String toString() {
        return "Rule:\n" +
                "\tEvent:\n" +
                "\t\t" + event + "\n" +
                "\tDescription:\n" +
                "\t\t" + description + "\n" +
                "\tLastUsedTime:\n" +
                "\t\t" + lastUsedTime + "\n";
    }

    /** @return The Event which triggers the use of the Rule. */
    public String getEvent() {
        return event;
    }

    /** @return A description of the Rule. */
    public String getDescription() {
        return description;
    }

    /** @return The time at which the Rule was last used. */
    public long getLastUsedTime() {
        return lastUsedTime;
    }

    /** Updates the lastUsedTime to the current system time. */
    public void updateLastUsedTime() {
        this.lastUsedTime = System.currentTimeMillis();
    }
}
