package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class HarnessDesignAttributeUpdateService
extends NotifyService {
    static Document responsePayload1 = null;

    public static Document processHarnessDesignAttributesUpdateService(Document xmlDoc) {
        MessageWin mesWin;
        NotifyService.timeDiff("Started method processHarnessDesignAttributesUpdateService(doc)");
        responsePayload1 = xmlDoc;
        SceGlobals.mesWinTitle = "Harness Design Attributes Update";
        SceGlobals.mesWin = mesWin = new MessageWin(false);
        mesWin.resetErrors();
        String msg = "";
        Element root = xmlDoc.getDocumentElement();
        NodeList desData = root.getElementsByTagName("designattributesdata");
        Element designAttributesObject = (Element)desData.item(0);
        String designtype = designAttributesObject.getAttribute("designtype");
        NotifyService.timeDiff("Retrieved designAttributesObject");
        try {
            FileInputStream propInFile = new FileInputStream(SceGlobals.propFilePath);
            Properties p2 = new Properties();
            p2.load(propInFile);
            if (p2.getProperty("DEBUG") != null) {
                SceGlobals.debug = p2.getProperty("DEBUG").equals("true");
            }
            if (p2.getProperty("SCE_RELEASE_LEVEL") != null) {
                String[] csarray = p2.getProperty("SCE_RELEASE_LEVEL").split(",");
                SceGlobals.d_SCE_controlled_status_array = new String[csarray.length];
                int i = 0;
                while (i < csarray.length) {
                    SceGlobals.d_SCE_controlled_status_array[i] = csarray[i].trim();
                    ++i;
                }
            }
            propInFile.close();
        }
        catch (FileNotFoundException e) {
            System.err.println("Can't find " + SceGlobals.propFilePath);
            msg = "\nCan't find " + SceGlobals.propFilePath;
            mesWin.addMess(msg, "error");
        }
        catch (IOException e) {
            System.err.println("I/O failed." + SceGlobals.propFilePath);
            msg = "\nI/O failed." + SceGlobals.propFilePath;
            mesWin.addMess(msg, "error");
        }
        try {
            msg = "Harness Design Attributes Update " + SceGlobals.progVers + " designType " + designtype;
            if (SceGlobals.debug) {
                msg = String.valueOf(msg) + " is starting in debug mode.";
            }
            System.out.println("*\n* " + msg + "\n");
            msg = String.valueOf(msg) + "\n";
            mesWin.addMess(msg, "info");
            if (SceGlobals.debug) {
                System.out.println(String.valueOf(msg) + "\n");
                System.out.println("............................................");
                mesWin.addMess(String.valueOf(msg) + "\n", "info");
            }
            NotifyService.timeDiff("Read some properties SceGlobals.propFilePath");
            HarnessDesignAttributeUpdateService.desAttributes(xmlDoc, false);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (SceGlobals.debug) {
            try {
                fo = new File("webServer.dbg");
                out = new FileWriter(fo);
                out.write("Debug File:\n");
                mesWin.addMess("\nSent back XML: ", "info");
                mesWin.addMess(msg, "info");
                out.write(msg);
                mesWin.showWin();
            }
            catch (FileNotFoundException fne) {
                System.out.println("Can't create debug file.");
                mesWin.addMess("\nCan't create debug file.", "error");
            }
            catch (IOException ioe) {
                System.out.println("IOException..." + ioe.toString());
                msg = "\nIOException..." + ioe.toString();
                mesWin.addMess(msg, "error");
            }
            mesWin.showWin();
        }
        return responsePayload1;
    }
}

