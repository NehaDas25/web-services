package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 */
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class QuerySingleSuppPart {
    static String partNoResult = "";
    static String sce_number = "";
    static String designation40 = "";
    static String plan_number = "";
    static String plan_index = "";
    static String prod_index = "";
    static String prod_nature = "";
    static String data_limit = "";

    public QuerySingleSuppPart(String partId) {
        sce_number = "";
        designation40 = "";
        plan_number = "";
        plan_index = "";
        prod_index = "";
        prod_nature = "";
        data_limit = "";
        try {
            Connection conn = DatabaseConnector.getCustomDBConnection();
            Statement stmt = conn.createStatement();
            Vector data = new Vector();
            Vector<String> header = new Vector<String>();
            String qstr = "select SCE_NUMBER, OP_CODE, PROD_INDEX, DESIGNATION40, PLAN_NUMBER, PLAN_INDEX, DATE_LIMIT, PROD_NATURE  from q549_sce_comp where SCE_NUMBER LIKE '" + partId + "'";
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
                ++maxRow;
                c = 1;
                while (c <= maxCol) {
                    RecSet.addElement(rs.getString(c));
                    if (c == 1) {
                        partNoResult = rs.getString(c);
                        sce_number = rs.getString(c);
                    }
                    if (c == 4) {
                        designation40 = rs.getString(c);
                    }
                    if (c == 5) {
                        plan_number = rs.getString(c);
                    }
                    if (c == 6) {
                        plan_index = rs.getString(c);
                    }
                    if (c == 3) {
                        prod_index = rs.getString(c);
                    }
                    if (c == 7) {
                        data_limit = rs.getString(c);
                    }
                    if (c == 8) {
                        prod_nature = rs.getString(c);
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
        String[] retVec = new String[]{sce_number, designation40, plan_number, plan_index, prod_index, prod_nature, data_limit};
        return retVec;
    }

    String getSCEPartNo() {
        return partNoResult;
    }

    boolean checkSCEPartNo(String SCE_NUMBER, String DESCRIPTION, String USERF1, String USERF2, String USERF3, String USERF4) {
        boolean check = true;
        String msg = "SCE_NUMBER " + SCE_NUMBER;
        if (this.mismatch(SCE_NUMBER, sce_number, "SCE_NUMBER")) {
            msg = "SCE_NUMBER mismatch";
            System.out.println(msg);
            check = false;
        }
        if (this.mismatch(DESCRIPTION, designation40, "DESCRIPTION")) {
            msg = "DESCRIPTION mismatch";
            System.out.println(msg);
            check = false;
        }
        if (this.mismatch(USERF1, String.valueOf(plan_number) + "." + plan_index, "USERF1")) {
            msg = "USERF1 mismatch";
            System.out.println(msg);
            check = false;
        }
        if (this.mismatch(USERF2, String.valueOf(sce_number) + "." + prod_index, "USERF2")) {
            msg = "USERF2 mismatch";
            System.out.println(msg);
            check = false;
        }
        if (this.mismatch(USERF3, prod_nature, "USERF3")) {
            msg = "USERF3 mismatch";
            System.out.println(msg);
            check = false;
        }
        if (this.mismatch(USERF4, data_limit, "USERF4")) {
            msg = "USERF4 mismatch";
            System.out.println(msg);
            check = false;
        }
        if (!check) {
            System.out.println("Not Equal");
            return false;
        }
        System.out.println("Database synchron");
        return true;
    }

    boolean mismatch(String c1, String c2, String cName) {
        String tc1 = c1.trim();
        return !tc1.equals(c2.trim());
    }

    public static void main(String[] args) {
        System.out.println("Starting QuerySingleSuppPartTest");
        QuerySingleSuppPart aSSPT = new QuerySingleSuppPart("965679958A");
        System.out.println("Get SCE Part No. " + aSSPT.getSCEPartNo());
        aSSPT.checkSCEPartNo("965679958A", "P-MODULE MPQ MQS 20V 5CC", "965679958A.", "965679958A...", "-", "");
        QuerySingleSuppPart aSSPT1 = new QuerySingleSuppPart("965679958A");
        aSSPT1.getSCEPartNo();
        aSSPT1.getSCEData();
        System.out.println("QuerySingleSuppPartTest finished");
    }
}

