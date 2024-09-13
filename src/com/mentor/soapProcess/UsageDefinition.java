package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  oracle.jdbc.driver.OracleDriver
 */
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import oracle.jdbc.driver.OracleDriver;

public class UsageDefinition {
    static String useDef = "";
    static String useDefId = "";

    public UsageDefinition(String libraryref) {
        useDef = "";
        useDefId = "";
        try {
            Connection conn = UsageDefinition.getConnection("CHS", "CHS");
            Statement stmt = conn.createStatement();
            Vector data = new Vector();
            Vector<String> header = new Vector<String>();
            String query = "select * from PROJECTUSAGEDEF where LIBRARYREF='";
            ResultSet rs = stmt.executeQuery(String.valueOf(query) + libraryref + "'");
            ResultSetMetaData md = rs.getMetaData();
            int maxCol = md.getColumnCount();
            int c = 1;
            while (c <= maxCol) {
                header.addElement(md.getColumnName(c).toString());
                ++c;
            }
            while (rs.next()) {
                Vector<String> RecSet = new Vector<String>();
                c = 1;
                while (c <= maxCol) {
                    RecSet.addElement(rs.getString(c));
                    if (c == 5 && useDef.equals("")) {
                        useDef = rs.getString(c);
                    }
                    if (c == 2 && useDefId.equals("")) {
                        useDefId = rs.getString(c);
                    }
                    ++c;
                }
                data.addElement(RecSet);
            }
            rs.close();
            conn.close();
        }
        catch (SQLException e) {
            System.out.println(e.toString());
        }
    }

    String getUseDefName() {
        return useDef;
    }

    String getUseDefId() {
        return useDefId;
    }

    private static Connection getConnection(String username, String password) throws SQLException {
        DriverManager.registerDriver((Driver)new OracleDriver());
        String connTxt = "jdbc:oracle:thin:@" + SceGlobals.ChsS;
        Connection conn = DriverManager.getConnection(connTxt, SceGlobals.ChsU, SceGlobals.ChsP);
        return conn;
    }

    public static void main(String[] args) {
        System.out.println("Starting UsageDefinitionTest");
        UsageDefinition aSSPT = new UsageDefinition("UID18622f-ffdb0a2dec-d3e5c7620b204779ee755ef50f893d4e");
        System.out.println("UsageDefinitionTest finished '" + aSSPT.getUseDefName() + "' " + aSSPT.getUseDefId());
    }
}

