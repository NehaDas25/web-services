/*
 * Decompiled with CFR 0.152.
 */
package com.mentor.mcd;

class EraserThread
implements Runnable {
    private volatile boolean stop;
    private char echochar = (char)42;

    public EraserThread(String prompt) {
        System.out.println(prompt);
    }

    @Override
    public void run() {
        int priority = Thread.currentThread().getPriority();
        Thread.currentThread().setPriority(10);
        try {
            this.stop = true;
            while (this.stop) {
                System.out.print("\b" + this.echochar);
                try {
                    Thread.sleep(1L);
                }
                catch (InterruptedException iex) {
                    Thread.currentThread().interrupt();
                    Thread.currentThread().setPriority(priority);
                    return;
                }
            }
        }
        finally {
            Thread.currentThread().setPriority(priority);
        }
    }

    public void stopMasking() {
        this.stop = false;
    }
}

