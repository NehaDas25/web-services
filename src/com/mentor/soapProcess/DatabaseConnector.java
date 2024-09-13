package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  oracle.jdbc.driver.OracleDriver
 */
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import oracle.jdbc.driver.OracleDriver;

public class DatabaseConnector {
    private static Connection getConnection(String server, String username, String password) throws SQLException {
        DriverManager.registerDriver((Driver)new OracleDriver());
        String connTxt = "jdbc:oracle:thin:@" + server;
        return DriverManager.getConnection(connTxt, username, password);
    }

    public static Connection getChsDBConnection() throws SQLException {
        return DatabaseConnector.getConnection(SceGlobals.ChsS, SceGlobals.ChsU, SceGlobals.ChsP);
    }

    public static Connection getCustomDBConnection() throws SQLException {
        return DatabaseConnector.getConnection(SceGlobals.CustomS, SceGlobals.CustomU, SceGlobals.CustomP);
    }
}

