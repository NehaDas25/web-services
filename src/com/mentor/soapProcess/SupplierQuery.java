package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  oracle.jdbc.driver.OracleDriver
 */
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import oracle.jdbc.driver.OracleDriver;

public class SupplierQuery
implements ActionListener {
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    ResultSet rs1 = null;
    ResultSetMetaData md = null;
    ResultSetMetaData md1 = null;
    JScrollPane suppScrollPane = null;
    Vector org_ids = new Vector();
    Vector supporg_ids = new Vector();
    Vector org_names = new Vector();
    Vector data = new Vector();
    Vector header = new Vector();
    JTable suppTable = null;
    Vector suppPartVec = null;
    public static SceQuery sceQ;

    public SupplierQuery() {
        this.query();
        DefaultTableModel model = new DefaultTableModel(this.data, this.header);
        model.addTableModelListener(this.suppTable);
        this.suppTable = new JTable(model){

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        TableColumn column = null;
        int i = 0;
        while (i < this.suppTable.getColumnCount()) {
            column = this.suppTable.getColumnModel().getColumn(i);
            column.setMaxWidth(200);
            ++i;
        }
        this.suppTable.setSelectionMode(0);
        ListSelectionModel rowSM = this.suppTable.getSelectionModel();
        rowSM.addListSelectionListener(new ListSelectionListener(){

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                if (lsm.isSelectionEmpty()) {
                    SceGlobals.suppSelected = "";
                } else {
                    int selectedRow = lsm.getMinSelectionIndex();
                    Vector dataVec = (Vector)SupplierQuery.this.data.get(selectedRow);
                    String organisation_id = "";
                    String supplierorganisation_id = "";
                    String name = "";
                    int c = 0;
                    while (c < dataVec.size()) {
                        if (c == 0) {
                            name = dataVec.get(c).toString();
                            organisation_id = SupplierQuery.this.org_ids.get(selectedRow).toString();
                            supplierorganisation_id = SupplierQuery.this.supporg_ids.get(selectedRow).toString();
                            SceGlobals.sceEA.repaint();
                            System.out.println("\nSelected supplier: " + name);
                            SceGlobals.suppSelected = organisation_id;
                            SceGlobals.suppSelectedRow = selectedRow;
                            if (SceGlobals.sceSelected) {
                                SupplierQuery.this.querySuppPart(SceGlobals.sce_number, SceGlobals.libraryobject_id, false, "");
                            }
                        }
                        ++c;
                    }
                    sceQ = SceGlobals.sceQuery;
                }
            }
        });
        this.suppScrollPane = new JScrollPane(this.suppTable);
        this.suppScrollPane.setForeground(SceGlobals.LABEL_COLOR);
        this.suppScrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Search Supplier"), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        this.suppScrollPane.setPreferredSize(new Dimension(100, 150));
        Box suppBox = Box.createVerticalBox();
        suppBox.add(this.suppScrollPane);
        JButton unsel = new JButton("Unselect");
        unsel.setActionCommand("Unselect");
        unsel.addActionListener(this);
        unsel.setToolTipText("Unselect supplier.");
        suppBox.add(unsel);
        suppBox.add(Box.createVerticalGlue());
        SceGlobals.suppQuery = suppBox;
        suppBox.setMinimumSize(new Dimension(100, 200));
        suppBox.setMaximumSize(new Dimension(100, 200));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("Unselect".equals(e.getActionCommand())) {
            sceQ = SceGlobals.sceQuery;
            this.suppTable.clearSelection();
            SceGlobals.suppSelected = "";
            SceGlobals.sceQuery.repaint();
        }
    }

    void query() {
        try {
            this.conn = SupplierQuery.getConnection(SceGlobals.ChsS, SceGlobals.ChsU, SceGlobals.ChsP);
            this.stmt = this.conn.createStatement();
            String qStr = "select SUPPNAME, SUPPLIER_ID from SUPPLIER ";
            System.out.print("Query: " + qStr);
            this.rs = this.stmt.executeQuery(qStr);
            this.md = this.rs.getMetaData();
            int maxCol = this.md.getColumnCount();
            int maxCol1 = 0;
            int c = 1;
            while (c <= maxCol) {
                if (c == 1) {
                    this.header.addElement(this.md.getColumnName(c).toString());
                }
                ++c;
            }
            String org_id = "";
            String org_name = "";
            while (this.rs.next()) {
                Vector<String> RecSet = new Vector<String>();
                c = 1;
                while (c <= maxCol) {
                    if (c == 1) {
                        RecSet.addElement(this.rs.getString(c));
                        org_name = this.rs.getString(c);
                    }
                    if (c == 2) {
                        org_id = this.rs.getString(c);
                    }
                    ++c;
                }
                this.data.addElement(RecSet);
                this.org_names.addElement(org_name);
                this.org_ids.addElement(org_id);
                this.stmt = this.conn.createStatement();
                qStr = "select SUPPLIER_ID from SUPPLIER ";
                this.rs1 = this.stmt.executeQuery(qStr);
                this.md1 = this.rs.getMetaData();
                String supporg_id = "";
                maxCol1 = this.md1.getColumnCount();
                while (this.rs1.next()) {
                    int ic = 1;
                    while (ic <= maxCol) {
                        if (ic == 2) {
                            supporg_id = this.rs.getString(ic);
                        }
                        ++ic;
                    }
                    this.supporg_ids.addElement(supporg_id);
                }
            }
            this.rs.close();
            this.conn.close();
        }
        catch (SQLException e) {
            System.out.println(e.toString());
        }
    }

    int getSuppName(String suppOrgId) {
        String supplier = "";
        int c = 0;
        while (c < this.org_ids.size()) {
            supplier = (String)this.org_ids.get(c);
            if (supplier.equals(suppOrgId)) {
                return c;
            }
            ++c;
        }
        return -1;
    }

    void showSelectedRow(int selectedRow) {
        this.suppTable.addRowSelectionInterval(selectedRow, selectedRow);
    }

    void querySuppPart(String suppPart, String libraryobject_id, boolean selectSupplier, String supplierOrg) {
        try {
            this.suppPartVec = new Vector();
            this.conn = SupplierQuery.getConnection(SceGlobals.ChsS, SceGlobals.ChsU, SceGlobals.ChsP);
            this.stmt = this.conn.createStatement();
            String suppOrgQuery = "";
            String qStr = "";
            if (!supplierOrg.equals("")) {
                suppOrgQuery = " and SUPPLIER_ID like '" + supplierOrg + "'";
            }
            qStr = "select SUPPPART_ID, SUPPLIER_ID, SUPPPN from SUPPPART  where  COMPONEN_ID like '" + libraryobject_id + "' and " + " SUPPPN like '" + suppPart + "' " + suppOrgQuery;
            System.out.println("Query: " + qStr);
            this.rs = this.stmt.executeQuery(qStr);
            this.md = this.rs.getMetaData();
            int maxCol = this.md.getColumnCount();
            int c = 1;
            while (c <= maxCol) {
                if (c == 1) {
                    this.header.addElement(this.md.getColumnName(c).toString());
                }
                ++c;
            }
            int suppIdx = -1;
            int suppCount = 0;
            String chsSuppPartId = "";
            String suppStr = "Suppliers for SCE number : ";
            while (this.rs.next()) {
                Vector<String> RecSet = new Vector<String>();
                ++suppCount;
                c = 1;
                while (c <= maxCol) {
                    RecSet.addElement(this.rs.getString(c));
                    if (c == 1) {
                        chsSuppPartId = this.rs.getString(c);
                    }
                    ++c;
                }
                suppIdx = this.getSuppName(this.rs.getString(2));
                if (suppIdx != -1) {
                    System.out.print(String.valueOf(suppStr) + this.org_names.get(suppIdx));
                    if (!suppStr.equals(", ")) {
                        suppStr = ", ";
                    }
                }
                this.suppPartVec.addElement(RecSet);
            }
            this.rs.close();
            if (selectSupplier) {
                if (suppIdx != -1) {
                    if (suppCount == 1) {
                        SceGlobals.suppSelectedRow = suppIdx;
                        this.suppTable.addRowSelectionInterval(suppIdx, suppIdx);
                        SceGlobals.chsSuppPartId = chsSuppPartId;
                    } else {
                        SceGlobals.suppSelectedRow = suppIdx;
                        SceGlobals.chsSuppPartId = chsSuppPartId;
                        this.suppTable.clearSelection();
                    }
                } else {
                    this.suppTable.clearSelection();
                }
            } else {
                if (suppCount == 1) {
                    SceGlobals.chsSuppPartId = chsSuppPartId;
                }
                if (suppCount == 0) {
                    SceGlobals.chsSuppPartId = "";
                }
            }
            System.out.print("\nSCE number: " + SceGlobals.sce_number);
            System.out.println();
            System.out.println(" chsSuppPartId: " + SceGlobals.chsSuppPartId);
        }
        catch (SQLException e) {
            System.out.println(e.toString());
        }
    }

    private static Connection getConnection(String service, String user, String pword) throws SQLException {
        DriverManager.registerDriver((Driver)new OracleDriver());
        String connTxt = "jdbc:oracle:thin:@" + service;
        Connection conn = DriverManager.getConnection(connTxt, user, pword);
        return conn;
    }
}

