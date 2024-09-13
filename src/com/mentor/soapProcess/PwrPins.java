package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 */
import com.mentor.mcd.DMSEncrypter;
import com.mentor.mcd.MCDDMSException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Vector;

public class PwrPins {
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

    public PwrPins(String libraryref) {
        String msg = "";
        pwrPinsName = null;
        pwrPinsId = null;
        pwrPinstIMax = null;
        pwrPinsIMax = null;
        pwrPinsINominal = null;
        pwrPinsVNominal = null;
        pinContainerId = "";
        try {
            Connection conn = DatabaseConnector.getChsDBConnection();
            Statement stmt = conn.createStatement();
            Vector data = new Vector();
            Vector<String> header = new Vector<String>();
            String query = "select * from LIBRARYPINCONTAINER where LIBRARYOBJECT_ID='" + libraryref + "'";
            ResultSet rs = stmt.executeQuery(query);
            ResultSetMetaData md = rs.getMetaData();
            int maxCol = md.getColumnCount();
            int c = 1;
            while (c <= maxCol) {
                header.addElement(md.getColumnName(c).toString());
                ++c;
            }
            int containerCount = 0;
            while (rs.next()) {
                Vector<String> RecSet = new Vector<String>();
                ++containerCount;
                c = 1;
                while (c <= maxCol) {
                    RecSet.addElement(rs.getString(c));
                    if (c == 1 && pinContainerId.equals("")) {
                        pinContainerId = rs.getString(c);
                    }
                    ++c;
                }
                data.addElement(RecSet);
            }
            rs.close();
            if (containerCount > 1) {
                System.out.println("ERROR: Only one pin container belongs to one library object!!!");
                SceGlobals.mesWin.addMess(msg, "error");
            }
            stmt = conn.createStatement();
            data = new Vector();
            header = new Vector();
            query = "select * from LIBRARYCAVITY where LIBRARYPINCONTAINER_ID='";
            rs = stmt.executeQuery(String.valueOf(query) + pinContainerId + "'");
            md = rs.getMetaData();
            maxCol = md.getColumnCount();
            int ci = 1;
            while (ci <= maxCol) {
                header.addElement(md.getColumnName(ci).toString());
                ++ci;
            }
            Vector<String> v1 = new Vector<String>();
            Vector<String> v2 = new Vector<String>();
            Vector<String> v3 = new Vector<String>();
            Vector<String> v4 = new Vector<String>();
            Vector<String> v5 = new Vector<String>();
            Vector<String> v6 = new Vector<String>();
            String pType = "";
            String pId = "";
            String pName = "";
            String ptIMax = "";
            String pIMax = "";
            String pINominal = "";
            String pVNominal = "";
            while (rs.next()) {
                Vector<String> RecSet = new Vector<String>();
                pType = "";
                pId = "";
                pName = "";
                ptIMax = "";
                pIMax = "";
                pINominal = "";
                pVNominal = "";
                int cj = 1;
                while (cj <= maxCol) {
                    RecSet.addElement(rs.getString(cj));
                    if (cj == 1) {
                        pId = rs.getString(cj);
                    }
                    if (cj == 3) {
                        pName = rs.getString(cj);
                    }
                    if (cj == 5 && rs.getString(cj) != null) {
                        pType = rs.getString(cj);
                    }
                    if (cj == 7 && rs.getString(cj) != null) {
                        pIMax = rs.getString(cj);
                    }
                    if (cj == 8 && rs.getString(cj) != null) {
                        pINominal = rs.getString(cj);
                    }
                    if (cj == 9 && rs.getString(cj) != null) {
                        pVNominal = rs.getString(cj);
                    }
                    if (cj == 14 && rs.getString(cj) != null) {
                        ptIMax = rs.getString(cj);
                    }
                    ++cj;
                }
                if (!pType.equals("PWR")) continue;
                v1.add(pId);
                v2.add(pName);
                v3.add(ptIMax);
                v4.add(pIMax);
                v5.add(pINominal);
                v6.add(pVNominal);
            }
            rs.close();
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
            conn.close();
        }
        catch (SQLException e) {
            System.out.println(e.toString());
        }
    }

    boolean checks() {
        String msg = "";
        if (pwrPinsId.length == 0) {
            return false;
        }
        System.out.println("Info: tIMax, IMax, INominal & VNominal check for pins with pin type PWR:");
        SceGlobals.mesWin.addMess("\nInfo: tIMax, IMax, INominal & VNominal check for pins with pin type PWR:", "info");
        if (pwrPinsId.length > 0) {
            int c = 0;
            while (c < pwrPinsId.length) {
                this.iErrorC = false;
                Object intValue = null;
                if (pwrPinstIMax[c].equals("")) {
                    System.out.println("Error: Empty tIMAX value for Pin " + pwrPinsName[c].toString());
                    msg = "\nError: Pin " + pwrPinsName[c].toString() + " empty tIMAX value.";
                    SceGlobals.mesWin.addMess(msg, "error");
                    this.iError = true;
                    this.iErrorC = true;
                }
                this.checkValue(pwrPinstIMax[c], pwrPinsName[c].toString(), " invalid tIMAX value. Value must be higher than 0.");
                if (pwrPinsIMax[c].equals("")) {
                    System.out.println("Error: Empty IMAX value for Pin " + pwrPinsName[c].toString());
                    msg = "\nError: Pin " + pwrPinsName[c].toString() + " empty IMAX value.";
                    SceGlobals.mesWin.addMess(msg, "error");
                    this.iError = true;
                    this.iErrorC = true;
                }
                this.checkValue(pwrPinsIMax[c], pwrPinsName[c].toString(), " invalid IMAX value. Value must be higher than 0.");
                if (pwrPinsINominal[c].equals("")) {
                    System.out.println("Error: Empty INominal value for Pin " + pwrPinsName[c].toString());
                    msg = "\nError: Pin " + pwrPinsName[c].toString() + " empty INominal value.";
                    SceGlobals.mesWin.addMess(msg, "error");
                    this.iError = true;
                    this.iErrorC = true;
                }
                this.checkValue(pwrPinsINominal[c], pwrPinsName[c].toString(), " invalid INominal value. Value must be higher than 0.");
                if (pwrPinsVNominal[c].equals("")) {
                    System.out.println("Error: Empty VNominal value for Pin " + pwrPinsName[c].toString());
                    msg = "\nError: Pin " + pwrPinsName[c].toString() + " empty VNominal value.";
                    SceGlobals.mesWin.addMess(msg, "error");
                    this.iError = true;
                    this.iErrorC = true;
                }
                this.checkValue(pwrPinsVNominal[c], pwrPinsName[c].toString(), " invalid VNominal value. Value must be higher than 0.");
                ++c;
            }
        }
        if (this.iError) {
            msg = "\nInfo: tIMax/IMax/INominal/VNominal check finished with errors.";
            SceGlobals.mesWin.addMess(msg, "info");
            return true;
        }
        msg = "\nInfo: tIMax/IMax/INominal/VNominal check finished without errors.\n";
        SceGlobals.mesWin.addMess(msg, "info");
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
        MessageWin mesWin;
        System.out.println("Starting PwrPinsTest");
        SceGlobals.mesWin = mesWin = new MessageWin(false);
        try {
            DMSEncrypter encrypter = null;
            encrypter = new DMSEncrypter();
            FileInputStream propInFile = new FileInputStream(SceGlobals.propFilePath);
            Properties p2 = new Properties();
            p2.load(propInFile);
            SceGlobals.CustomU = p2.getProperty("CustomU");
            try {
                SceGlobals.CustomP = encrypter.decryptString(p2.getProperty("CustomP"));
            }
            catch (MCDDMSException e) {
                System.err.println("Can't decrypt.");
                e.printStackTrace();
            }
            SceGlobals.CustomS = p2.getProperty("CustomS");
            SceGlobals.ChsU = p2.getProperty("ChsU");
            try {
                SceGlobals.ChsP = encrypter.decryptString(p2.getProperty("ChsP"));
            }
            catch (MCDDMSException e) {
                System.err.println("Can't decrypt.");
                e.printStackTrace();
            }
            SceGlobals.ChsS = p2.getProperty("ChsS");
            SceGlobals.SyncModeActive = p2.getProperty("SyncModeActive");
        }
        catch (FileNotFoundException e) {
            System.err.println("Can't find " + SceGlobals.propFilePath);
        }
        catch (IOException e) {
            System.err.println("I/O failed." + SceGlobals.propFilePath);
        } catch (MCDDMSException e) {
            throw new RuntimeException(e);
        }
        PwrPins pwrPins = new PwrPins("UID3936e1-1052f5cb78f-36e668748b0bfea4a6ed9be299953e48");
        System.out.println("Count of Pwr Pins:  " + pwrPinsId.length);
        int c = 0;
        while (c < pwrPinsId.length) {
            System.out.print(String.valueOf(pwrPinsId[c].toString()) + " - ");
            System.out.print(String.valueOf(pwrPinsName[c].toString()) + " - ");
            System.out.print(String.valueOf(pwrPinstIMax[c].toString()) + " - ");
            System.out.print(String.valueOf(pwrPinsIMax[c].toString()) + " - ");
            System.out.print(pwrPinsINominal[c].toString());
            System.out.print(pwrPinsVNominal[c].toString());
            System.out.println();
            ++c;
        }
        System.out.println("pwrPinsTest finished");
        pwrPins.getPwrPinsIds();
        pwrPins.getPwrPinsNames();
    }
}

