/*
 * Decompiled with CFR 0.152.
 */
package com.mentor.mcd;

import com.mentor.mcd.DMSAbstractEncrypter;
import com.mentor.mcd.MCDDMSException;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

public class MCDDMSEncrypter
extends DMSAbstractEncrypter {
    private final int ITERATION_COUNT = 7;
    private final String PASSWORD_PHRASE = "MCD-DMS-ENCRYPTER";

    public MCDDMSEncrypter() throws MCDDMSException {
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
        return "MCD-DMS-ENCRYPTER";
    }

    public static void main(String[] arg) throws Exception {
        MCDDMSEncrypter encrypter = new MCDDMSEncrypter();
        System.out.println("Encrypted Password is : " + MCDDMSEncrypter.getEncryptedPassword(MCDDMSEncrypter.askPassword()));
    }

    public static String getEncryptedPassword(String password) throws MCDDMSException {
        MCDDMSEncrypter encrypter = new MCDDMSEncrypter();
        return encrypter.encryptString(password);
    }

    public static String getDecryptedPassword(String password) throws MCDDMSException {
        MCDDMSEncrypter encrypter = new MCDDMSEncrypter();
        return encrypter.decryptString(password);
    }

    public static String askEncryptedPassword() throws MCDDMSException {
        return MCDDMSEncrypter.getEncryptedPassword(MCDDMSEncrypter.askPassword());
    }

    public static String askPlainPassword() throws MCDDMSException {
        return MCDDMSEncrypter.askPassword();
    }

    private static String askPassword() {
        String pword = null;
        JPasswordField tPasswordField = new JPasswordField(10);
        tPasswordField.setEchoChar('*');
        tPasswordField.hasFocus();
        JOptionPane.showMessageDialog(null, tPasswordField, "Enter password", 3);
        char[] chars = tPasswordField.getPassword();
        pword = new String(chars);
        return pword;
    }
}

