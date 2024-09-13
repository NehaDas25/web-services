/*
 * Decompiled with CFR 0.152.
 */
package com.mentor.mcd;

import com.mentor.mcd.EraserThread;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Arrays;

public class PasswordField {
    public static final char[] getPassword(InputStream in, String prompt) throws IOException {
        char[] lineBuffer;
        EraserThread maskingthread = new EraserThread(prompt);
        Thread thread = new Thread(maskingthread);
        thread.start();
        char[] buf = lineBuffer = new char[128];
        int room = buf.length;
        int offset = 0;
        block4: while (true) {
            int c = in.read();
            switch (c) {
                case -1: 
                case 10: {
                    break block4;
                }
                case 13: {
                    int c2 = in.read();
                    if (c2 == 10 || c2 == -1) break block4;
                    if (!(in instanceof PushbackInputStream)) {
                        in = new PushbackInputStream(in);
                    }
                    ((PushbackInputStream)in).unread(c2);
                }
                default: {
                    System.out.print("\b*");
                    if (--room < 0) {
                        buf = new char[offset + 128];
                        room = buf.length - offset - 1;
                        System.arraycopy(lineBuffer, 0, buf, 0, offset);
                        Arrays.fill(lineBuffer, ' ');
                        lineBuffer = buf;
                    }
                    buf[offset++] = (char)c;
                }
            }
        }
        maskingthread.stopMasking();
        if (offset == 0) {
            return null;
        }
        char[] ret = new char[offset];
        System.arraycopy(buf, 0, ret, 0, offset);
        Arrays.fill(buf, ' ');
        return ret;
    }
}

