package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 */
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class NetNumbers {
    static String[] deviceUsageIds = null;
    static String[] deviceUsageNames = null;
    static String[] pinUsageIds = null;
    static String[] pinUsagePinName = null;
    static String[] pinUsageName = null;
    static String[] pinNetNames = null;
    static String[] deviceUsageNamesWithoutNets = null;

    public NetNumbers(String libraryref) {
        deviceUsageIds = null;
        deviceUsageNames = null;
        pinUsageIds = null;
        pinUsagePinName = null;
        pinUsageName = null;
        pinNetNames = null;
        deviceUsageNamesWithoutNets = null;
        UsageDefinition uesDef = new UsageDefinition(libraryref);
        String usagedef_id = uesDef.getUseDefId();
        if (usagedef_id.length() == 0) {
            deviceUsageIds = new String[0];
            deviceUsageNames = new String[0];
            pinUsageIds = new String[0];
            return;
        }
        try {
            Connection conn = DatabaseConnector.getChsDBConnection();
            Statement stmt = conn.createStatement();
            Vector data = new Vector();
            Vector<String> header = new Vector<String>();
            String query = "select * from PROJECTDEVICEUSAGE where USAGEDEF_ID='";
            ResultSet rs = stmt.executeQuery(String.valueOf(query) + usagedef_id + "'");
            ResultSetMetaData md = rs.getMetaData();
            int maxCol = md.getColumnCount();
            int c = 1;
            while (c <= maxCol) {
                header.addElement(md.getColumnName(c).toString());
                ++c;
            }
            Vector<String> v = new Vector<String>();
            Vector<String> v1 = new Vector<String>();
            while (rs.next()) {
                Vector<String> RecSet = new Vector<String>();
                c = 1;
                while (c <= maxCol) {
                    RecSet.addElement(rs.getString(c));
                    if (c == 2) {
                        v.add(rs.getString(c));
                    }
                    if (c == 3) {
                        v1.add(rs.getString(c));
                    }
                    ++c;
                }
                data.addElement(RecSet);
                deviceUsageIds = new String[v.size()];
                v.copyInto(deviceUsageIds);
                deviceUsageNames = new String[v.size()];
                v1.copyInto(deviceUsageNames);
            }
            rs.close();
            stmt = conn.createStatement();
            data = new Vector();
            header = new Vector();
            Vector<String> v2 = new Vector<String>();
            Vector<String> v3 = new Vector<String>();
            Vector<String> v4 = new Vector<String>();
            query = "select * from PROJECTPINUSAGE where DEVICEUSAGE_ID='";
            c = 0;
            while (c < deviceUsageIds.length) {
                rs = stmt.executeQuery(String.valueOf(query) + deviceUsageIds[c].toString() + "'");
                md = rs.getMetaData();
                maxCol = md.getColumnCount();
                int ci = 1;
                while (ci <= maxCol) {
                    header.addElement(md.getColumnName(ci).toString());
                    ++ci;
                }
                while (rs.next()) {
                    Vector<String> RecSet = new Vector<String>();
                    int cj = 1;
                    while (cj <= maxCol) {
                        RecSet.addElement(rs.getString(cj));
                        if (cj == 2) {
                            v2.add(rs.getString(cj));
                        }
                        if (cj == 3) {
                            v3.add(rs.getString(cj));
                            v4.add(deviceUsageNames[c].toString());
                        }
                        ++cj;
                    }
                }
                rs.close();
                ++c;
            }
            pinUsageIds = new String[v2.size()];
            v2.copyInto(pinUsageIds);
            pinUsagePinName = new String[v3.size()];
            v3.copyInto(pinUsagePinName);
            pinUsageName = new String[v4.size()];
            v4.copyInto(pinUsageName);
            conn.close();
        }
        catch (SQLException e) {
            System.out.println(e.toString());
        }
    }

    int getUsageCount() {
        return deviceUsageIds.length;
    }

    String[] getDeviceUsageIds() {
        System.out.println("Usage Names:  ");
        int c = 0;
        while (c < deviceUsageNames.length) {
            System.out.println(String.valueOf(deviceUsageNames[c].toString()) + " " + deviceUsageIds[c].toString());
            ++c;
        }
        return deviceUsageIds;
    }

    boolean existNetNames(String[] outPinNames) {
        boolean netCheckError = false;
        String msg = "";
        if (pinUsageIds.length == 0) {
            return netCheckError;
        }
        Vector<String> netNames = new Vector<String>();
        boolean isOutPin = false;
        Vector<String> noNetsUsageDef = new Vector<String>();
        int c = 0;
        while (c < pinUsageIds.length) {
            isOutPin = false;
            int c1 = 0;
            while (c1 < outPinNames.length) {
                if (outPinNames[c1].equals(pinUsagePinName[c])) {
                    isOutPin = true;
                    break;
                }
                ++c1;
            }
            boolean existsNet = false;
            if (isOutPin) {
                try {
                    Connection conn = DatabaseConnector.getChsDBConnection();
                    Statement stmt = conn.createStatement();
                    stmt = conn.createStatement();
                    Vector data = new Vector();
                    Vector<String> header = new Vector<String>();
                    data = new Vector();
                    header = new Vector();
                    String query = "select * from PROJECTPINUSAGENETNAME where PINUSAGE_ID='";
                    ResultSet rs = stmt.executeQuery(String.valueOf(query) + pinUsageIds[c].toString() + "'");
                    ResultSetMetaData md = rs.getMetaData();
                    int maxCol = md.getColumnCount();
                    int ci = 1;
                    while (ci <= maxCol) {
                        header.addElement(md.getColumnName(ci).toString());
                        ++ci;
                    }
                    while (rs.next()) {
                        Vector<String> RecSet = new Vector<String>();
                        int cj = 1;
                        while (cj <= maxCol) {
                            RecSet.addElement(rs.getString(cj));
                            if (cj == 2) {
                                netNames.add(rs.getString(cj));
                                existsNet = true;
                            }
                            ++cj;
                        }
                    }
                    rs.close();
                    pinNetNames = new String[netNames.size()];
                    netNames.copyInto(pinNetNames);
                    if (!existsNet) {
                        netCheckError = true;
                        boolean devUsageFound = false;
                        String devUsage = "";
                        int f = 0;
                        while (f < noNetsUsageDef.size()) {
                            devUsage = (String)noNetsUsageDef.get(f);
                            if (devUsage.equals(pinUsageName[c].toString())) {
                                devUsageFound = true;
                                break;
                            }
                            ++f;
                        }
                        if (!devUsageFound) {
                            noNetsUsageDef.add(pinUsageName[c].toString());
                        }
                        System.out.println("Error: Pin " + outPinNames[c1] + " has no net assigned for device usage " + pinUsageName[c].toString() + ".");
                        msg = "\nError: Pin " + outPinNames[c1] + " has no net assigned for device usage " + pinUsageName[c].toString() + ".";
                        SceGlobals.mesWin.addMess(msg, "error");
                    }
                    conn.close();
                }
                catch (SQLException e) {
                    System.out.println(e.toString());
                    SceGlobals.mesWin.addMess(e.toString(), "error");
                }
            }
            deviceUsageNamesWithoutNets = new String[noNetsUsageDef.size()];
            noNetsUsageDef.copyInto(deviceUsageNamesWithoutNets);
            ++c;
        }
        return netCheckError;
    }

    String getUseDefsWithoutNets() {
        String nets = "";
        int cj = 0;
        while (cj < deviceUsageNamesWithoutNets.length) {
            nets = String.valueOf(nets) + deviceUsageNamesWithoutNets[cj].toString() + " ";
            ++cj;
        }
        return nets;
    }

    public static void main(String[] args) {
        System.out.println("Starting NetNumbersTest");
        NetNumbers netNos = new NetNumbers("UID18622f-ffdb0a2dec-d3e5c7620b204779ee755ef50f893d4e");
        System.out.println("Count of Usages:  " + netNos.getUsageCount());
        int c = 0;
        while (c < deviceUsageIds.length) {
            System.out.println(String.valueOf(deviceUsageIds[c].toString()) + " " + deviceUsageNames[c].toString());
            ++c;
        }
        OutPins oPins = new OutPins("UID18622f-ffdb0a2dec-d3e5c7620b204779ee755ef50f893d4e");
        netNos.existNetNames(oPins.getOutPinsNames());
        System.out.println(netNos.getUseDefsWithoutNets());
        System.out.println("NetNumbersTest finished");
    }
}

