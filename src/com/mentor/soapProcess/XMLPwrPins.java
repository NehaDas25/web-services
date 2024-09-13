package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 */
import java.util.Vector;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLPwrPins {
    static String[] pwrPinsName;
    static String[] pwrPinsId;
    static String[] pwrPinstIMax;
    static String[] pwrPinsIMax;
    static String[] pwrPinsINominal;
    static String[] pwrPinsVNominal;
    static String pinContainerId;
    boolean iError = false;
    boolean iErrorC = false;
    String msg = "";

    static {
        pinContainerId = "";
    }

    public XMLPwrPins(Element root) {
        pwrPinsName = null;
        pwrPinsId = null;
        pwrPinstIMax = null;
        pwrPinsIMax = null;
        pwrPinsINominal = null;
        pwrPinsVNominal = null;
        pinContainerId = "";
        Vector<String> v1 = new Vector<String>();
        Vector<String> v2 = new Vector<String>();
        Vector<String> v3 = new Vector<String>();
        Vector<String> v4 = new Vector<String>();
        Vector<String> v5 = new Vector<String>();
        Vector<String> v6 = new Vector<String>();
        NodeList pinObjects = root.getElementsByTagName("librarycavity");
        Element pinObject = null;
        int i = 0;
        while (i < pinObjects.getLength()) {
            pinObject = (Element)pinObjects.item(i);
            if (pinObject.getAttribute("pintype").equals("PWR")) {
                v1.add(pinObject.getAttribute("cavityname"));
                v2.add(pinObject.getAttribute("cavityname"));
                v3.add(pinObject.getAttribute("timax").trim());
                v4.add(pinObject.getAttribute("imax").trim());
                v5.add(pinObject.getAttribute("inominal").trim());
                v6.add(pinObject.getAttribute("vnominal").trim());
            }
            ++i;
        }
        pwrPinsId = new String[v1.size()];
        v1.copyInto(pwrPinsId);
        pwrPinsName = new String[v1.size()];
        v2.copyInto(pwrPinsName);
        pwrPinstIMax = new String[v1.size()];
        v3.copyInto(pwrPinstIMax);
        pwrPinsIMax = new String[v1.size()];
        v4.copyInto(pwrPinsIMax);
        pwrPinsINominal = new String[v1.size()];
        v5.copyInto(pwrPinsINominal);
        pwrPinsVNominal = new String[v1.size()];
        v6.copyInto(pwrPinsVNominal);
    }

    boolean checks() {
        if (pwrPinsId.length == 0) {
            return false;
        }
        System.out.println("Info: tIMax, IMax, INominal & VNominal check for pins with pin type PWR:");
        SceGlobals.mesWin.addMess("\nInfo: tIMax, IMax, INominal & VNominal check for pins with pin type PWR:", "info");
        if (pwrPinsId.length > 0) {
            int c = 0;
            while (c < pwrPinsId.length) {
                this.iErrorC = false;
                if (pwrPinstIMax[c].equals("")) {
                    System.out.println("Error: Empty tIMAX value for Pin " + pwrPinsName[c].toString());
                    this.msg = "\nError: Pin " + pwrPinsName[c].toString() + " empty tIMAX value.";
                    SceGlobals.mesWin.addMess(this.msg, "error");
                    this.iError = true;
                    this.iErrorC = true;
                } else {
                    this.checkValue(pwrPinstIMax[c], pwrPinsName[c].toString(), " invalid tIMAX value. Value must be higher than 0.");
                }
                if (pwrPinsIMax[c].equals("")) {
                    System.out.println("Error: Empty IMAX value for Pin " + pwrPinsName[c].toString());
                    this.msg = "\nError: Pin " + pwrPinsName[c].toString() + " empty IMAX value.";
                    SceGlobals.mesWin.addMess(this.msg, "error");
                    this.iError = true;
                    this.iErrorC = true;
                } else {
                    this.checkValue(pwrPinsIMax[c], pwrPinsName[c].toString(), " invalid IMAX value. Value must be higher than 0.");
                }
                if (pwrPinsINominal[c].equals("")) {
                    System.out.println("Error: Empty INominal value for Pin " + pwrPinsName[c].toString());
                    this.msg = "\nError: Pin " + pwrPinsName[c].toString() + " empty INominal value.";
                    SceGlobals.mesWin.addMess(this.msg, "error");
                    this.iError = true;
                    this.iErrorC = true;
                } else {
                    this.checkValue(pwrPinsINominal[c], pwrPinsName[c].toString(), " invalid INominal value. Value must be higher than 0.");
                }
                if (pwrPinsVNominal[c].equals("")) {
                    System.out.println("Error: Empty VNominal value for Pin " + pwrPinsName[c].toString());
                    this.msg = "\nError: Pin " + pwrPinsName[c].toString() + " empty VNominal value.";
                    SceGlobals.mesWin.addMess(this.msg, "error");
                    this.iError = true;
                    this.iErrorC = true;
                } else {
                    this.checkValue(pwrPinsVNominal[c], pwrPinsName[c].toString(), " invalid VNominal value. Value must be higher than 0.");
                }
                ++c;
            }
        }
        if (this.iError) {
            this.msg = "\nInfo: tIMax/IMax/INominal/VNominal check finished with errors.";
            SceGlobals.mesWin.addMess(this.msg, "info");
            return true;
        }
        this.msg = "\nInfo: tIMax/IMax/INominal/VNominal check finished without errors.\n";
        SceGlobals.mesWin.addMess(this.msg, "info");
        return false;
    }

    void checkValue(String inputValue, String pinName, String erMsg) {
        Double dValue = null;
        try {
            dValue = Double.parseDouble(inputValue);
        }
        catch (NumberFormatException nfex) {
            dValue = Double.valueOf(0.0);
        }
        if (dValue <= 0.0) {
            System.out.println("Error: Pin " + pinName);
            this.msg = "\nError: Pin " + pinName + erMsg;
            SceGlobals.mesWin.addMess(this.msg, "error");
            this.iError = true;
            this.iErrorC = true;
        }
    }

    String[] getPwrPinsIds() {
        int c = 0;
        while (c < pwrPinsId.length) {
            ++c;
        }
        return pwrPinsId;
    }

    String[] getPwrPinsNames() {
        int c = 0;
        while (c < pwrPinsName.length) {
            ++c;
        }
        return pwrPinsName;
    }

    public static void main(String[] args) {
        System.out.println("Starting PwrPinsTest");
    }
}

