/*
 * Decompiled with CFR 0.152.
 */
package com.mentor.mcd;

import com.mentor.mcd.DMSAbstractEncrypter;
import com.mentor.mcd.MCDDMSException;
import com.mentor.mcd.PasswordField;

public class DMSEncrypter
extends DMSAbstractEncrypter {
    private final int ITERATION_COUNT = 7;
    private final String PASSWORD_PHRASE = "MCD-DMS-PASS-PHRASE";

    public DMSEncrypter() throws MCDDMSException {
    }

    @Override
    protected byte[] getEncodingSalt() {
        return new byte[]{-87, -101, -56, 50, 86, 53, -29, 3};
    }

    @Override
    protected int getIterationCount() {
        return 7;
    }

    @Override
    protected String getPassString() {
        return "MCD-DMS-PASS-PHRASE";
    }

    public static void main(String[] arg) throws Exception {
        char[] password = PasswordField.getPassword(System.in, "Enter Password: ");
        DMSEncrypter encrypter = new DMSEncrypter();
        String encStr = encrypter.encryptString(String.valueOf(password));
        System.out.println("Encrypted Password is : " + encStr);
        int i = 0;
        while (i < password.length) {
            password[i] = 32;
            ++i;
        }
        System.out.println("Decrypt              : " + encrypter.decryptString(encStr));
    }
}

