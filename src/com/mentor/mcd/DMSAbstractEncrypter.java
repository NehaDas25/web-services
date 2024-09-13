/*
 * Decompiled with CFR 0.152.
 */
package com.mentor.mcd;

import com.mentor.mcd.MCDDMSException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public abstract class DMSAbstractEncrypter {
    private final String SKF_ALGORITHM = "PBEWithMD5AndDES";
    private final String UCS_TRANS_FORMAT = "UTF8";
    private Cipher encryptCipher;
    private Cipher decryptCipher;

    public DMSAbstractEncrypter() throws MCDDMSException {
        try {
            byte[] salt = this.getEncodingSalt();
            int iIter = this.getIterationCount();
            String sPass = this.getPassString();
            PBEKeySpec keySpec = new PBEKeySpec(sPass.toCharArray(), salt, iIter);
            SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
            this.encryptCipher = Cipher.getInstance(key.getAlgorithm());
            this.decryptCipher = Cipher.getInstance(key.getAlgorithm());
            PBEParameterSpec paramSpec = new PBEParameterSpec(salt, iIter);
            this.encryptCipher.init(1, (Key)key, paramSpec);
            this.decryptCipher.init(2, (Key)key, paramSpec);
        }
        catch (InvalidKeyException e) {
            throw new MCDDMSException("Invalid Key", e);
        }
        catch (InvalidKeySpecException e) {
            throw new MCDDMSException("Invalid Key Spec", e);
        }
        catch (NoSuchAlgorithmException e) {
            throw new MCDDMSException(e.getMessage(), e);
        }
        catch (NoSuchPaddingException e) {
            throw new MCDDMSException(e.getMessage(), e);
        }
        catch (InvalidAlgorithmParameterException e) {
            throw new MCDDMSException(e.getMessage(), e);
        }
    }

    public String encryptString(String str) throws MCDDMSException {
        try {
            byte[] utf8 = str.getBytes("UTF8");
            byte[] enc = this.encryptCipher.doFinal(utf8);
            return Base64.getEncoder().encodeToString(enc);
        }
        catch (UnsupportedEncodingException e) {
            throw new MCDDMSException(e.getMessage(), e);
        }
        catch (IllegalBlockSizeException e) {
            throw new MCDDMSException(e.getMessage(), e);
        }
        catch (BadPaddingException e) {
            throw new MCDDMSException(e.getMessage(), e);
        }
    }

    public String decryptString(String str) throws MCDDMSException {
        try {
            byte[] dec = Base64.getDecoder().decode(str);
            byte[] utf8 = this.decryptCipher.doFinal(dec);
            return new String(utf8, "UTF8");
        }
        catch (IOException | BadPaddingException | IllegalBlockSizeException e) {
            throw new MCDDMSException(e.getMessage(), e);
        }
    }

    protected abstract String getPassString();

    protected abstract byte[] getEncodingSalt();

    protected abstract int getIterationCount();
}

