package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 */
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DSIFileReader {
    private String pathToDSIFile;

    public DSIFileReader(String pathToDSIFile) {
        this.pathToDSIFile = pathToDSIFile;
    }

    String getDSIFileContents() throws IOException {
        File dsiFile = new File(this.pathToDSIFile);
        if (!dsiFile.exists()) {
            System.out.println("Could not read DSI file at " + this.pathToDSIFile);
            return null;
        }
        byte[] encoded = Files.readAllBytes(Paths.get(this.pathToDSIFile, new String[0]));
        return new String(encoded, Charset.defaultCharset());
    }

    String[] getLines() throws IOException {
        String contents = this.getDSIFileContents();
        if (contents != null) {
            contents = contents.trim();
            return contents.split("\r\n");
        }
        return null;
    }
}

