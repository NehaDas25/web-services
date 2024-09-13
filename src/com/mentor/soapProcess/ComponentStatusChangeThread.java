package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 */
import org.w3c.dom.Document;

class ComponentStatusChangeThread
extends Thread {
    private Document doc;

    public ComponentStatusChangeThread(Document doc) {
        this.doc = doc;
    }

    @Override
    public void run() {
        NotifyService.processComponentStatusChangeService(this.doc);
    }
}

