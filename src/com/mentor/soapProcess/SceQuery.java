package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 */
import com.mentor.mcd.DMSEncrypter;
import com.mentor.mcd.MCDDMSException;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class SceQuery
extends JFrame {
    public static final int MAX_WINDOW_WIDTH = 1400;
    public static final int MAX_WINDOW_HEIGHT = 700;
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    ResultSetMetaData md = null;
    JScrollPane scrollPane = null;
    SceEntryArea sceEntryArea = null;
    Vector data = new Vector();
    Vector header = new Vector();
    JTable sapTable = null;

    public SceQuery(String sce_number, String description) {
        Container contentPane;
        this.setTitle("PSA SCE Component Browser");
        this.sceEntryArea = null;
        try {
            Integer in;
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
            try {
                in = Integer.parseInt(p2.getProperty("MAX_WINDOW_WIDTH"));
                SceGlobals.MAX_WINDOW_WIDTH = in;
            }
            catch (NumberFormatException nfex) {
                System.out.println("Wrong MAX_WINDOW_WIDTH value " + p2.getProperty("MAX_WINDOW_WIDTH") + " in chs_cust.properties file.");
                SceGlobals.MAX_WINDOW_WIDTH = 1400;
            }
            try {
                in = Integer.parseInt(p2.getProperty("MAX_WINDOW_HEIGHT"));
                SceGlobals.MAX_WINDOW_HEIGHT = in;
            }
            catch (NumberFormatException nfex) {
                System.out.println("Wrong MAX_WINDOW_HEIGHT value " + p2.getProperty("MAX_WINDOW_HEIGHT") + " in chs_cust.properties file.");
                SceGlobals.MAX_WINDOW_HEIGHT = 700;
            }
            SceGlobals.SyncModeActive = p2.getProperty("SyncModeActive");
            SceGlobals.debug = p2.getProperty("DEBUG").equals("true");
            if (p2.getProperty("UI_DATE_FORMAT") != null) {
                SceGlobals.DATE_FORMAT_UI = p2.getProperty("UI_DATE_FORMAT");
            }
            if (p2.getProperty("DB_DATE_FORMAT") != null) {
                SceGlobals.DATE_FORMAT_DB = p2.getProperty("DB_DATE_FORMAT");
            }
        }
        catch (FileNotFoundException e) {
            System.err.println("Can't find " + SceGlobals.propFilePath);
        }
        catch (IOException e) {
            System.err.println("I/O failed." + SceGlobals.propFilePath);
        } catch (MCDDMSException e) {
            throw new RuntimeException(e);
        }
        final SupplierQuery suppQuery = new SupplierQuery();
        SceGlobals.sceEA = this.sceEntryArea = new SceEntryArea();
        this.query(sce_number, description, "");
        DefaultTableModel model = new DefaultTableModel(this.data, this.header);
        SortFilterModel sorter = new SortFilterModel(model);
        this.sapTable = new JTable(sorter){};
        model.addTableModelListener(this.sapTable);
        TableColumn column = null;
        int i = 0;
        while (i < this.sapTable.getColumnCount()) {
            column = this.sapTable.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(100);
            } else if (i == 1) {
                column.setPreferredWidth(10);
            } else if (i == 2) {
                column.setPreferredWidth(10);
            } else if (i == 3) {
                column.setPreferredWidth(220);
            } else if (i == 4) {
                column.setPreferredWidth(50);
            } else if (i == 8) {
                column.setPreferredWidth(50);
            } else if (i == 3) {
                column.setPreferredWidth(150);
            } else {
                column.setPreferredWidth(20);
            }
            ++i;
        }
        SceGlobals.sceQuery = this;
        SceGlobals.data = this.data;
        SceGlobals.header = this.header;
        SceGlobals.sapTable = this.sapTable;
        SceGlobals.sorter = sorter;
        this.setSize(SceGlobals.MAX_WINDOW_WIDTH, SceGlobals.MAX_WINDOW_HEIGHT);
        this.toFront();
        this.setLocation(20, 20);
        this.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent e) {
                SceQuery.this.exitSceRecBrowser("Exit SCE Record Browser");
            }
        });
        this.sapTable.setSelectionMode(0);
        ListSelectionModel rowSM = this.sapTable.getSelectionModel();
        rowSM.addListSelectionListener(new ListSelectionListener(){

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                if (lsm.isSelectionEmpty()) {
                    System.out.println("No row selected.");
                } else {
                    int selectedRow = lsm.getMinSelectionIndex();
                    Vector dataVec = (Vector)SceGlobals.data.get(selectedRow);
                    String plan_number = "";
                    String plan_index = "";
                    String prod_index = "";
                    int c = 0;
                    while (c < dataVec.size()) {
                        if (c == 0) {
                            SceGlobals.sce_number = dataVec.get(c).toString();
                        }
                        if (c == 2) {
                            prod_index = dataVec.get(c).toString();
                        }
                        if (c == 3) {
                            SceGlobals.description = dataVec.get(c).toString();
                        }
                        if (c == 4) {
                            plan_number = dataVec.get(c).toString();
                        }
                        if (c == 5) {
                            plan_index = dataVec.get(c).toString();
                        }
                        if (c == 7) {
                            SceGlobals.userf3 = dataVec.get(c).toString();
                        }
                        if (c == 6) {
                            try {
                                SceGlobals.userf4 = dataVec.get(c).toString();
                            }
                            catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                        ++c;
                    }
                    SceGlobals.userf1 = String.valueOf(plan_number) + "." + plan_index;
                    SceGlobals.userf2 = String.valueOf(SceGlobals.sce_number) + "." + prod_index;
                    suppQuery.querySuppPart(SceGlobals.sce_number, SceGlobals.libraryobject_id, true, "");
                    SceGlobals.sceSelected = true;
                }
            }
        });
        SceGlobals.jscrollPanel = this.scrollPane = new JScrollPane(this.sapTable);
        this.scrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Search Results"), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        SceGlobals.contentPane = contentPane = this.getContentPane();
        contentPane.add((Component)this.sceEntryArea, "North");
        contentPane.add((Component)this.scrollPane, "Center");
        Buttons buttons = new Buttons(this.sceEntryArea, this);
        buttons.setOpaque(true);
        contentPane.add((Component)buttons, "South");
        sorter.addMouseListener(this.sapTable);
    }

    void query(String sce_number, String description, String modDate) {
        block26: {
            String a0 = "";
            String a1 = sce_number.trim();
            String a2 = description.trim();
            String a3 = modDate;
            String qStr = "";
            SimpleDateFormat formater = new SimpleDateFormat(SceGlobals.DATE_FORMAT_UI);
            SimpleDateFormat formaterDB = new SimpleDateFormat(SceGlobals.DATE_FORMAT_DB);
            try {
                if (a0.equals("")) {
                    a0 = "%";
                }
                if (a1.equals("")) {
                    a1 = "%";
                }
                if (a2.equals("")) {
                    a2 = "%";
                } else if (!a2.endsWith("%")) {
                    a2 = String.valueOf(a2) + "%";
                }
                if (a3.equals("")) {
                    a3 = "";
                }
                this.sceEntryArea.setValues("", a1, a2, a3);
                this.data.removeAllElements();
                this.conn = DatabaseConnector.getCustomDBConnection();
                this.stmt = this.conn.createStatement();
                String nlsDateStr = "select VALUE from  nls_session_parameters where PARAMETER like 'NLS_DATE_FORMAT'";
                this.rs = this.stmt.executeQuery(nlsDateStr);
                this.md = this.rs.getMetaData();
                while (this.rs.next()) {
                    if (SceGlobals.debug) {
                        System.out.print("Info : NLS_DATE_FORMAT from database is ");
                    }
                    Vector RecSet = new Vector();
                    int i = 1;
                    while (i <= this.md.getColumnCount()) {
                        String value = this.rs.getString(i);
                        if (SceGlobals.debug) {
                            System.out.println(value);
                        }
                        ++i;
                    }
                }
                this.rs.close();
            }
            catch (SQLException e) {
                JOptionPane.showMessageDialog(SceGlobals.sceQuery, "Looking for NLS_DATE_FORMAT has failed: " + e.toString());
            }
            try {
                String dateStr = "";
                if (!a3.equals("")) {
                    java.util.Date a3Date = null;
                    try {
                        a3Date = formater.parse(a3);
                    }
                    catch (ParseException p) {
                        JOptionPane.showMessageDialog(SceGlobals.sceQuery, "Wrong date format:  " + a3 + "\nUse date format:        " + SceGlobals.DATE_FORMAT_UI + "\n\n");
                        return;
                    }
                    String a3DB = "";
                    try {
                        a3DB = formaterDB.format(a3Date);
                    }
                    catch (Exception p) {
                        JOptionPane.showMessageDialog(SceGlobals.sceQuery, "Conversion Ui Date to DM date failed: \n" + p.toString());
                    }
                    if (SceGlobals.debug) {
                        System.out.println("UI date " + a3 + " was converted to DB date for query " + a3DB);
                    }
                    dateStr = " and MODIFIED_DATE > '" + a3DB + "'";
                }
                qStr = "select SCE_NUMBER, OP_CODE, PROD_INDEX, DESIGNATION40, PLAN_NUMBER, PLAN_INDEX, DATE_LIMIT, PROD_NATURE, MODIFIED_DATE from q549_sce_comp  where SCE_NUMBER LIKE '" + a1 + "' and UPPER(DESIGNATION40) LIKE UPPER('" + a2 + "') " + SceGlobals.opCodeQuery + SceGlobals.latIssueQuery + dateStr;
                System.out.println("Query: " + qStr);
                this.rs = this.stmt.executeQuery(qStr);
                this.md = this.rs.getMetaData();
                int maxCol = this.md.getColumnCount();
                int c = 1;
                while (c <= maxCol) {
                    this.header.addElement(this.md.getColumnName(c).toString());
                    ++c;
                }
                while (this.rs.next()) {
                    Vector<String> RecSet = new Vector<String>();
                    c = 1;
                    while (c <= maxCol) {
                        String value = this.rs.getString(c);
                        if (c == 9) {
                            String formatedDate;
                            Date foundDate = this.rs.getDate(9);
                            value = formatedDate = formater.format(foundDate);
                        }
                        RecSet.addElement(value);
                        ++c;
                    }
                    this.data.addElement(RecSet);
                }
                System.out.println(" Count of entries: " + this.data.size());
                this.rs.close();
                this.conn.close();
            }
            catch (SQLException e) {
                System.out.println(e.toString());
                String dateErr = "\nPlease check the date format '" + SceGlobals.DATE_FORMAT_UI + "' of the Modified Date Field '" + a3 + "'.\n";
                if (!a3.equals("%")) {
                    JOptionPane.showMessageDialog(SceGlobals.sceQuery, dateErr);
                }
                if (SceGlobals.debug) {
                    System.out.println("DATE_FORMAT_UI: " + SceGlobals.DATE_FORMAT_UI);
                }
                if (!SceGlobals.debug) break block26;
                System.out.println("DATE_FORMAT_DB: " + SceGlobals.DATE_FORMAT_DB);
            }
        }
    }

    void setSyncFlag(String sce_number) {
        if (sce_number.equals("")) {
            return;
        }
        try {
            this.conn = DatabaseConnector.getCustomDBConnection();
            this.stmt = this.conn.createStatement();
            String qStr = "update q549_sce_comp set SYNC_FLAG = 1  where SCE_NUMBER LIKE '" + sce_number + "'";
            int r = this.stmt.executeUpdate(qStr);
            if (r == 1) {
                System.out.println("Successfully set SYNC_Flag for SCE_PART '" + sce_number + "'.");
            } else {
                System.out.println("Error during setting SYNC_Flag for SCE_PART " + sce_number + "'. Result : " + r);
            }
        }
        catch (SQLException e) {
            System.out.println("Error during update SYNC_FLAG. " + e.toString());
        }
    }

    public void exitSceRecBrowser(String exitType) {
        System.out.println(exitType);
        if (SceGlobals.SyncModeActive.equals("true") && exitType.equals("Closing SCE Browser")) {
            this.setSyncFlag(SceGlobals.sce_number);
        }
        SceGlobals.finished = true;
        this.dispose();
    }
}

