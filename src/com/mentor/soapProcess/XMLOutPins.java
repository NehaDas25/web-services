package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 */
import java.util.Vector;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLOutPins {
    static String[] outPinsName;
    static String[] outPinsId;
    static String[] outPinsIMax;
    static String[] outPinsVNominal;
    static String pinContainerId;

    static {
        pinContainerId = "";
    }

    public XMLOutPins(Element root) {
        outPinsName = null;
        outPinsId = null;
        outPinsIMax = null;
        outPinsVNominal = null;
        pinContainerId = "";
        Vector<String> v1 = new Vector<String>();
        Vector<String> v2 = new Vector<String>();
        Vector<String> v3 = new Vector<String>();
        Vector<String> v4 = new Vector<String>();
        NodeList pinObjects = root.getElementsByTagName("librarycavity");
        Element pinObject = null;
        int i = 0;
        while (i < pinObjects.getLength()) {
            pinObject = (Element)pinObjects.item(i);
            if (pinObject.getAttribute("pintype").equals("OUT")) {
                v1.add(pinObject.getAttribute("cavityname"));
                v2.add(pinObject.getAttribute("cavityname"));
                v3.add(pinObject.getAttribute("imax").trim());
                v4.add(pinObject.getAttribute("vnominal").trim());
            }
            ++i;
        }
        outPinsId = new String[v1.size()];
        v1.copyInto(outPinsId);
        outPinsName = new String[v1.size()];
        v2.copyInto(outPinsName);
        outPinsIMax = new String[v1.size()];
        v3.copyInto(outPinsIMax);
        outPinsVNominal = new String[v1.size()];
        v4.copyInto(outPinsVNominal);
    }

    boolean checks() {
        String msg = "";
        if (outPinsId.length == 0) {
            return false;
        }
        boolean iError = false;
        System.out.println("Info: IMax & VNominal check for pins with pin type OUT:");
        SceGlobals.mesWin.addMess("\nInfo: IMax & VNominal check for pins with pin type OUT:", "info");
        if (outPinsId.length > 0) {
            int c = 0;
            while (c < outPinsId.length) {
                boolean iErrorC = false;
                if (outPinsIMax[c].equals("") || outPinsIMax[c].equals("0")) {
                    System.out.println("Error: Invalid or empty IMAX value for Pin " + outPinsName[c].toString());
                    msg = "\nError: Pin " + outPinsName[c].toString() + " invalid or empty IMAX value.";
                    SceGlobals.mesWin.addMess(msg, "error");
                    iError = true;
                    iErrorC = true;
                }
                if (outPinsVNominal[c].equals("") || outPinsVNominal[c].equals("0")) {
                    System.out.println("Error: Invalid or empty VNominal value for Pin " + outPinsName[c].toString());
                    msg = "\nError: Pin " + outPinsName[c].toString() + " invalid or empty VNominal value.";
                    SceGlobals.mesWin.addMess(msg, "error");
                    iError = true;
                    iErrorC = true;
                }
                ++c;
            }
        }
        if (iError) {
            msg = "\nInfo: IMax/VNominal check finished with errors.";
            SceGlobals.mesWin.addMess(msg, "info");
            return true;
        }
        msg = "\nInfo: IMax/VNominal check finished without errors.\n";
        SceGlobals.mesWin.addMess(msg, "info");
        return false;
    }

    String[] getOutPinsIds() {
        int c = 0;
        while (c < outPinsId.length) {
            ++c;
        }
        return outPinsId;
    }

    String[] getOutPinsNames() {
        int c = 0;
        while (c < outPinsName.length) {
            ++c;
        }
        return outPinsName;
    }

    public static void main(String[] args) {
        System.out.println("Starting OutPinsTest");
    }
}

