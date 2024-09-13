package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 */
import org.w3c.dom.Document;

class NotifyThread
extends Thread {
    private Document doc;
    private String currentDateStamp;

    public NotifyThread(Document doc, String currentDateStamp) {
        this.doc = doc;
        this.currentDateStamp = currentDateStamp;
    }

    @Override
    public void run() {
        try {
            NotifyService.processNotifyService(this.doc, this.currentDateStamp);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

