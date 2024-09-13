package com.mentor.soapProcess;

/*
 * Decompiled with CFR 0.152.
 */
class Part
implements Comparable {
    int index;
    String lastIndex;

    public int compareTo(Object b) {
        char[] lChar;
        Part p = (Part)b;
        char[] pChar = p.lastIndex.toCharArray();
        if (pChar.length > (lChar = this.lastIndex.toCharArray()).length) {
            return -1;
        }
        if (pChar.length < lChar.length) {
            return 1;
        }
        int i = 0;
        while (i < pChar.length) {
            if (pChar[i] > lChar[i]) {
                return -1;
            }
            if (pChar[i] < lChar[i]) {
                return 1;
            }
            ++i;
        }
        return 0;
    }
}

