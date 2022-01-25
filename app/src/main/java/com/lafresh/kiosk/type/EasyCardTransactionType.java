package com.lafresh.kiosk.type;

/**
 * Created by Kevin on 2020/8/20.
 */

public enum EasyCardTransactionType {
    LOGIN("LOGIN"),
    PAY("PAY"),
    RETRY("RETRY"),
    NONE("NONE");

    private final String text;

    /**
     * @param text
     */
    EasyCardTransactionType(final String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }
}
