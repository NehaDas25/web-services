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

public class D_QuerySingleDesignPart {
    static String partNoResult = "";
    static String sce_number = "";
    static String indiceSce = "";
    static String service = "";
    static String codeCader = "";
    static String decoupage = "";
    static String designation20 = "";
    String errMsg = "";

    public D_QuerySingleDesignPart(String partId, String revision) {
        sce_number = "";
        indiceSce = "";
        service = "";
        codeCader = "";
        decoupage = "";
        designation20 = "";
        DMSEncrypter encrypter = null;
        try {
            encrypter = new DMSEncrypter();
        }
        catch (MCDDMSException e) {
            System.err.println("Can't create MCDEncrypter.");
            e.printStackTrace();
        }
        try {
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
        }
        catch (FileNotFoundException e) {
            System.err.println("Can't find " + SceGlobals.propFilePath);
        }
        catch (IOException e) {
            System.err.println("I/O failed." + SceGlobals.propFilePath);
        }
        try {
            Connection conn = DatabaseConnector.getCustomDBConnection();
            Statement stmt = conn.createStatement();
            Vector data = new Vector();
            Vector<String> header = new Vector<String>();
            String qstr = "select SCE_CODE, LAST_INDEX, SERVICE, CADER_CODE, DECOUPAGE, DESIGNATION20  from q549_sce_plan  where SCE_CODE LIKE '" + partId + "' and LAST_INDEX like '%" + revision + "%'";
            ResultSet rs = stmt.executeQuery(qstr);
            ResultSetMetaData md = rs.getMetaData();
            int maxCol = md.getColumnCount();
            int c = 1;
            while (c <= maxCol) {
                header.addElement(md.getColumnName(c).toString());
                ++c;
            }
            int maxRow = 0;
            while (rs.next()) {
                Vector<String> RecSet = new Vector<String>();
                if (!rs.getString(2).trim().equals(revision)) continue;
                ++maxRow;
                c = 1;
                while (c <= maxCol) {
                    RecSet.addElement(rs.getString(c));
                    if (c == 1) {
                        partNoResult = rs.getString(c);
                        sce_number = rs.getString(c);
                    }
                    if (c == 2) {
                        indiceSce = rs.getString(c);
                    }
                    if (c == 3) {
                        service = rs.getString(c);
                    }
                    if (c == 4) {
                        codeCader = rs.getString(c);
                    }
                    if (c == 5) {
                        decoupage = rs.getString(c);
                    }
                    if (c == 6) {
                        designation20 = rs.getString(c);
                    }
                    ++c;
                }
                data.addElement(RecSet);
            }
            rs.close();
            if (maxRow != 1) {
                partNoResult = "";
            }
            conn.close();
        }
        catch (SQLException e) {
            System.out.println(e.toString());
        }
    }

    String[] getSCEData() {
        String[] retVec = new String[]{sce_number, indiceSce, service, codeCader, decoupage, designation20};
        return retVec;
    }

    String getSCEPartNo() {
        return partNoResult;
    }

    boolean checkSCEPartNo(String SCE_NUMBER, String INDICEPLAN, String SERVICE, String CODECADER, String DECOUPAGE, String DESIGNATION20) {
        boolean check = true;
        this.errMsg = "";
        String msg = "SCE_NUMBER " + SCE_NUMBER;
        if (this.mismatch(SCE_NUMBER, sce_number, "Numero Plan")) {
            check = false;
        }
        if (this.mismatch(INDICEPLAN, indiceSce, "Indice Plan")) {
            check = false;
        }
        if (this.mismatch(SERVICE, service, "Service")) {
            check = false;
        }
        if (this.mismatch(CODECADER, codeCader, "Code Cader")) {
            check = false;
        }
        if (this.mismatch(DECOUPAGE, decoupage, "Decoupage")) {
            check = false;
        }
        if (this.mismatch(DESIGNATION20, designation20, "Description")) {
            check = false;
        }
        if (!check) {
            SceGlobals.d_partDataMismatch = "Part " + sce_number + " not synchron with SCE design database." + this.errMsg;
            return false;
        }
        System.out.println("Database synchron");
        return true;
    }

    boolean mismatch(String c1, String c2, String cName) {
        String tc1 = c1.trim();
        if (!tc1.equals(c2.trim())) {
            this.errMsg = String.valueOf(this.errMsg) + "\n" + cName + " mismach:   \t|" + c1 + "|  \t|" + c2.trim() + "|";
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.println("Starting QuerySingleDesignPartTest");
        D_QuerySingleDesignPart aSSPT = new D_QuerySingleDesignPart("140001938A", "A");
        System.out.println("Get SCE Design Part No. " + aSSPT.getSCEPartNo());
        aSSPT.checkSCEPartNo("140016768A", "B", "NJgg", "VQQ51Axx", "ZALv", "GOUTTI-FAISCE-PRINCI");
        D_QuerySingleDesignPart aSSPT1 = new D_QuerySingleDesignPart("965679958A", "C");
        aSSPT1.getSCEPartNo();
        aSSPT1.getSCEData();
        System.out.println("QuerySingleDesignPartTest finished");
    }
}

