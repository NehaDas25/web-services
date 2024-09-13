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

public class OutPins {
    static String[] outPinsName;
    static String[] outPinsId;
    static String[] outPinsIMax;
    static String[] outPinsVNominal;
    static String pinContainerId;

    static {
        pinContainerId = "";
    }

    public OutPins(String libraryref) {
        String msg = "";
        outPinsName = null;
        outPinsId = null;
        outPinsIMax = null;
        outPinsVNominal = null;
        pinContainerId = "";
        try {
            Connection conn = DatabaseConnector.getChsDBConnection();
            Statement stmt = conn.createStatement();
            Vector data = new Vector();
            Vector<String> header = new Vector<String>();
            String query = "select * from LIBRARYPINCONTAINER where LIBRARYOBJECT_ID='" + libraryref + "'";
            System.out.println("Query " + query);
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
            String pType = "";
            String pId = "";
            String pName = "";
            String pIMax = "";
            String pVNominal = "";
            while (rs.next()) {
                Vector<String> RecSet = new Vector<String>();
                pType = "";
                pId = "";
                pName = "";
                pIMax = "";
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
                    if (cj == 9 && rs.getString(cj) != null) {
                        pVNominal = rs.getString(cj);
                    }
                    ++cj;
                }
                if (!pType.equals("OUT")) continue;
                v1.add(pId);
                v2.add(pName);
                v3.add(pIMax);
                v4.add(pVNominal);
            }
            rs.close();
            outPinsId = new String[v1.size()];
            v1.copyInto(outPinsId);
            outPinsName = new String[v1.size()];
            v2.copyInto(outPinsName);
            outPinsIMax = new String[v1.size()];
            v3.copyInto(outPinsIMax);
            outPinsVNominal = new String[v1.size()];
            v4.copyInto(outPinsVNominal);
            conn.close();
        }
        catch (SQLException e) {
            System.out.println(e.toString());
        }
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
        OutPins oPins = new OutPins("UID5eb489-00283179a0-d3e5c7620b204779ee755ef50f893d4e");
        System.out.println("Count of Out Pins:  " + outPinsId.length);
        int c = 0;
        while (c < outPinsId.length) {
            System.out.print(String.valueOf(outPinsId[c].toString()) + " - ");
            System.out.print(String.valueOf(outPinsName[c].toString()) + " - ");
            System.out.print(String.valueOf(outPinsIMax[c].toString()) + " - ");
            System.out.print(outPinsVNominal[c].toString());
            System.out.println();
            ++c;
        }
        System.out.println("OutPinsTest finished '");
        oPins.getOutPinsIds();
        oPins.getOutPinsNames();
    }
}

