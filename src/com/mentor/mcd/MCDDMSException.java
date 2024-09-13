/*
 * Decompiled with CFR 0.152.
 */
package com.mentor.mcd;

public class MCDDMSException
extends Exception {
    private static final long serialVersionUID = 1L;
    String sUserMessage = "";

    public MCDDMSException() {
    }

    public MCDDMSException(String arg0) {
        super(arg0);
    }

    public MCDDMSException(String sTechMessage, String sUserMessage) {
        super(sTechMessage);
        this.sUserMessage = sUserMessage;
    }

    public MCDDMSException(Throwable arg0) {
        super(arg0);
    }

    public MCDDMSException(String sTechMessage, String sUserMessage, Throwable arg1) {
        super(sTechMessage, arg1);
        this.sUserMessage = sUserMessage;
    }

    public MCDDMSException(String sTechMessage, Throwable arg1) {
        super(sTechMessage, arg1);
    }

    public String getUserMessage() {
        return this.sUserMessage;
    }
}

