package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  oracle.jdbc.driver.OracleDriver
 */
import com.mentor.mcd.DMSEncrypter;
import com.mentor.mcd.MCDDMSException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import oracle.jdbc.driver.OracleDriver;

public class QueryReleaseLevel {
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    ResultSet rs1 = null;
    ResultSetMetaData md = null;
    ResultSetMetaData md1 = null;
    public static SceQuery sceQ;

    String query(String releaseStatusName, String projectID) {
        String chsReleaseLevel = "";
        try {
            this.conn = QueryReleaseLevel.getConnection(SceGlobals.ChsS, SceGlobals.ChsU, SceGlobals.ChsP);
            this.stmt = this.conn.createStatement();
            int maxCol = 0;
            int maxCol1 = 0;
            String qStr = "";
            this.stmt = this.conn.createStatement();
            qStr = "select RELEASELEVELMGR_ID from PROJECTRELEASELEVELMGR  where PROJECT_ID LIKE '" + projectID + "'";
            this.rs = this.stmt.executeQuery(qStr);
            this.md = this.rs.getMetaData();
            String releaseLevelMgrId = "";
            maxCol = this.md.getColumnCount();
            while (this.rs.next()) {
                int ic = 1;
                while (ic <= maxCol) {
                    if (ic == 1) {
                        releaseLevelMgrId = this.rs.getString(ic);
                    }
                    ++ic;
                }
            }
            this.rs.close();
            this.stmt = this.conn.createStatement();
            qStr = "select CHSRELEASELEVEL from PROJECTRELEASELEVEL  where RELEASELEVELMGR_ID LIKE '" + releaseLevelMgrId + "' and NAME like '" + releaseStatusName + "'";
            this.rs = this.stmt.executeQuery(qStr);
            this.md = this.rs.getMetaData();
            maxCol1 = this.md.getColumnCount();
            while (this.rs.next()) {
                int id = 1;
                while (id <= maxCol) {
                    if (id == 1) {
                        chsReleaseLevel = this.rs.getString(id);
                    }
                    ++id;
                }
            }
            this.rs.close();
            this.conn.close();
        }
        catch (SQLException e) {
            System.out.println(e.toString());
        }
        return chsReleaseLevel;
    }

    private static Connection getConnection(String service, String user, String pword) throws SQLException {
        DriverManager.registerDriver((Driver)new OracleDriver());
        String connTxt = "jdbc:oracle:thin:@" + service;
        Connection conn = DriverManager.getConnection(connTxt, user, pword);
        return conn;
    }

    public static void main(String[] args) {
        System.out.println(String.valueOf(SceGlobals.progVers) + "\n");
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
            propInFile.close();
        }
        catch (FileNotFoundException e) {
            System.err.println("Can't find " + SceGlobals.propFilePath);
        }
        catch (IOException e) {
            System.err.println("I/O failed." + SceGlobals.propFilePath);
        } catch (MCDDMSException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Starting QueryReleaseLevel ...");
        QueryReleaseLevel qRelLev = new QueryReleaseLevel();
        String level = qRelLev.query("OFFI", "");
        System.out.println("QueryReleaseLevel finished: " + level);
    }
}

