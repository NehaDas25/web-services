package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 */
import com.mentor.mcd.DMSEncrypter;
import com.mentor.mcd.MCDDMSException;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
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
import java.util.Arrays;
import java.util.Properties;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

class D_SceQueryPanel
extends JPanel {
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private ResultSetMetaData md = null;
    private D_SceEntryArea d_sceEntryArea = null;
    private Vector data = new Vector();
    private Vector header = new Vector();
    private JTable sapTable = null;
    private String oldPartNumber = "";
    private int recIndex = 0;
    private Vector tmpSCEParts = null;
    private int partStartIndex = -1;

    public D_SceQueryPanel(String sce_number, String designType) {
        JScrollPane d_scrollPane;
        SceGlobals.d_sceQueryPanel = this;
        this.d_sceEntryArea = null;
        SceGlobals.d_designType = designType;
        DMSEncrypter encrypter = null;
        try {
            encrypter = new DMSEncrypter();
        }
        catch (MCDDMSException e) {
            System.err.println("Can't create MCDEncrypter.");
            e.printStackTrace();
        }
        try {
            int i;
            int i2;
            String[] tmpArray;
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
            if (p2.getProperty("DEBUG") != null) {
                SceGlobals.debug = p2.getProperty("DEBUG").equals("true");
            }
            if (p2.getProperty("UI_DATE_FORMAT") != null) {
                SceGlobals.DATE_FORMAT_UI = p2.getProperty("UI_DATE_FORMAT");
            }
            if (p2.getProperty("DB_DATE_FORMAT") != null) {
                SceGlobals.DATE_FORMAT_DB = p2.getProperty("DB_DATE_FORMAT");
            }
            String propValue = "";
            if (SceGlobals.d_designType.equalsIgnoreCase("harness")) {
                propValue = p2.getProperty("HARNESS_ACTION_CODE") != null ? p2.getProperty("HARNESS_ACTION_CODE") : p2.getProperty("A,E,O,C");
                tmpArray = propValue.split(",");
                SceGlobals.d_action_codes = new String[tmpArray.length];
                i2 = 0;
                while (i2 < tmpArray.length) {
                    SceGlobals.d_action_codes[i2] = tmpArray[i2];
                    ++i2;
                }
            } else if (p2.getProperty("ACTION_CODE") != null) {
                propValue = p2.getProperty("ACTION_CODE");
                tmpArray = propValue.split(",");
                SceGlobals.d_action_codes = new String[tmpArray.length + 1];
                SceGlobals.d_action_codes[0] = "O";
                i2 = 0;
                while (i2 < tmpArray.length) {
                    SceGlobals.d_action_codes[i2 + 1] = tmpArray[i2];
                    ++i2;
                }
            } else {
                System.out.println("No action codes");
            }
            if (p2.getProperty("INTEGRATOR") != null) {
                propValue = p2.getProperty("INTEGRATOR");
                String[] tmpStr = propValue.split(",");
                i = 0;
                while (i < tmpStr.length) {
                    if (i == 0) {
                        SceGlobals.d_integratorFilterQueryString = " and DECOUPAGE in (";
                    }
                    if (i != 0) {
                        SceGlobals.d_integratorFilterQueryString = String.valueOf(SceGlobals.d_integratorFilterQueryString) + ",";
                    }
                    SceGlobals.d_integratorFilterQueryString = String.valueOf(SceGlobals.d_integratorFilterQueryString) + "'" + tmpStr[i] + "'";
                    ++i;
                }
                if (tmpStr.length > 0) {
                    SceGlobals.d_integratorFilterQueryString = String.valueOf(SceGlobals.d_integratorFilterQueryString) + ")";
                }
            } else {
                SceGlobals.d_integratorFilterQueryString = "";
            }
            if (p2.getProperty("LOGIC") != null) {
                propValue = p2.getProperty("LOGIC");
                String[] tmpStr = propValue.split(",");
                i = 0;
                while (i < tmpStr.length) {
                    if (i == 0) {
                        SceGlobals.d_logicFilterQueryString = " and DECOUPAGE in (";
                    }
                    if (i != 0) {
                        SceGlobals.d_logicFilterQueryString = String.valueOf(SceGlobals.d_logicFilterQueryString) + ",";
                    }
                    SceGlobals.d_logicFilterQueryString = String.valueOf(SceGlobals.d_logicFilterQueryString) + "'" + tmpStr[i] + "'";
                    ++i;
                }
                if (tmpStr.length > 0) {
                    SceGlobals.d_logicFilterQueryString = String.valueOf(SceGlobals.d_logicFilterQueryString) + ")";
                }
            } else {
                SceGlobals.d_logicFilterQueryString = "";
            }
            if (p2.getProperty("HARNESS") != null) {
                propValue = p2.getProperty("HARNESS");
                System.out.println("DEBUG0: HARNESS = " + propValue);
                if (propValue != null && !propValue.equalsIgnoreCase("")) {
                    String[] tmpStr = propValue.split(",");
                    i = 0;
                    while (i < tmpStr.length) {
                        if (i == 0) {
                            SceGlobals.d_harnessFilterQueryString = " and DECOUPAGE in (";
                        }
                        if (i != 0) {
                            SceGlobals.d_harnessFilterQueryString = String.valueOf(SceGlobals.d_harnessFilterQueryString) + ",";
                        }
                        SceGlobals.d_harnessFilterQueryString = String.valueOf(SceGlobals.d_harnessFilterQueryString) + "'" + tmpStr[i] + "'";
                        ++i;
                    }
                    if (tmpStr.length > 0) {
                        SceGlobals.d_harnessFilterQueryString = String.valueOf(SceGlobals.d_harnessFilterQueryString) + ")";
                    }
                } else {
                    SceGlobals.d_harnessFilterQueryString = "";
                }
            } else {
                SceGlobals.d_harnessFilterQueryString = "";
            }
        }
        catch (FileNotFoundException e) {
            System.err.println("Can't find " + SceGlobals.propFilePath);
        }
        catch (IOException e) {
            System.err.println("I/O failed." + SceGlobals.propFilePath);
        }
        SceGlobals.d_sceEA = this.d_sceEntryArea = new D_SceEntryArea();
        NotifyService.timeDiff("Will start query sce_number");
        this.query(sce_number, "", "");
        NotifyService.timeDiff("After query sce_number");
        DefaultTableModel model = new DefaultTableModel(this.data, this.header);
        SortFilterModel d_sorter = new SortFilterModel(model);
        this.sapTable = new JTable(d_sorter){

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public String getToolTipText(MouseEvent e) {
                String tip = "";
                Point p = e.getPoint();
                int rowIndex = this.rowAtPoint(p);
                String action_code = (String)this.getValueAt(rowIndex, 0);
                int i = 0;
                while (i < SceGlobals.d_action_codes.length) {
                    if (action_code.equals(SceGlobals.d_action_codes[i]) && !D_SceQueryPanel.this.partSelectable(SceGlobals.d_action_codes[i])) {
                        tip = "Parts with action code '" + action_code + "' are not selectable.";
                        this.removeRowSelectionInterval(rowIndex, rowIndex);
                        return tip;
                    }
                    ++i;
                }
                return tip;
            }
        };
        model.addTableModelListener(this.sapTable);
        TableColumn column = null;
        int i = 0;
        while (i < this.sapTable.getColumnCount()) {
            column = this.sapTable.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(10);
                column.setHeaderValue("Action Code");
            } else if (i == 1) {
                column.setPreferredWidth(30);
                if (SceGlobals.d_designType.equals("harness")) {
                    column.setHeaderValue("Reference PSA");
                } else {
                    column.setHeaderValue("Numero Plan");
                }
            } else if (i == 2) {
                column.setPreferredWidth(10);
                column.setHeaderValue("Indice Plan");
            } else if (i == 3) {
                column.setPreferredWidth(20);
                column.setHeaderValue("Service");
            } else if (i == 4) {
                column.setPreferredWidth(30);
                column.setHeaderValue("Code Cader");
            } else if (i == 5) {
                column.setPreferredWidth(50);
                column.setHeaderValue("Decoupage");
            } else if (i == 6) {
                column.setPreferredWidth(150);
                if (SceGlobals.d_designType.equals("harness")) {
                    column.setHeaderValue("PLM/SAP Description");
                } else {
                    column.setHeaderValue("Designation");
                }
            } else if (i == 7) {
                column.setPreferredWidth(50);
                column.setHeaderValue("Modified Date");
            }
            ++i;
        }
        String actionCode = "";
        int i3 = 0;
        while (i3 < this.sapTable.getRowCount()) {
            actionCode = (String)this.sapTable.getValueAt(i3, 0);
            if (actionCode.equals("O")) {
                this.sapTable.setRowSelectionAllowed(false);
            }
            ++i3;
        }
        SceGlobals.d_data = this.data;
        SceGlobals.d_header = this.header;
        SceGlobals.d_sapTable = this.sapTable;
        SceGlobals.d_sorter = d_sorter;
        this.sapTable.setSelectionMode(0);
        ListSelectionModel rowSM = this.sapTable.getSelectionModel();
        rowSM.addListSelectionListener(new ListSelectionListener(){

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                if (!lsm.isSelectionEmpty()) {
                    int selectedRow = lsm.getMinSelectionIndex();
                    Vector dataVec = (Vector)SceGlobals.d_data.get(selectedRow);
                    if (!D_SceQueryPanel.this.partSelectable(dataVec.get(0).toString())) {
                        D_SceQueryPanel.this.sapTable.removeRowSelectionInterval(selectedRow, selectedRow);
                        SceGlobals.d_sceSelected = false;
                        SceGlobals.d_sce_number = "";
                        SceGlobals.d_actionCode = "";
                        SceGlobals.d_indiceSce = "";
                        SceGlobals.d_service = "";
                        SceGlobals.d_codeCader = "";
                        SceGlobals.d_decoupage = "";
                        SceGlobals.d_designation20 = "";
                        return;
                    }
                    int c = 0;
                    while (c < dataVec.size()) {
                        if (c == 1) {
                            SceGlobals.d_sce_number = dataVec.get(c).toString().trim();
                        }
                        if (c == 0) {
                            SceGlobals.d_actionCode = dataVec.get(c).toString().trim();
                        }
                        if (c == 2) {
                            SceGlobals.d_indiceSce = dataVec.get(c).toString().trim();
                        }
                        if (c == 3) {
                            SceGlobals.d_service = dataVec.get(c).toString().trim();
                        }
                        if (c == 4) {
                            SceGlobals.d_codeCader = dataVec.get(c).toString().trim();
                        }
                        if (c == 5) {
                            SceGlobals.d_decoupage = dataVec.get(c).toString().trim();
                        }
                        if (c == 6) {
                            SceGlobals.d_designation20 = dataVec.get(c).toString();
                        }
                        ++c;
                    }
                    SceGlobals.d_sceSelected = true;
                }
            }
        });
        SceGlobals.d_jscrollPanel = d_scrollPane = new JScrollPane(this.sapTable);
        d_scrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Search Results"), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        d_scrollPane.setVerticalScrollBarPolicy(20);
        this.add((Component)this.d_sceEntryArea, "North");
        this.add((Component)d_scrollPane, "Center");
        d_sorter.addMouseListener(this.sapTable);
    }

    boolean partSelectable(String action_code) {
        int i = 0;
        while (i < SceGlobals.d_action_codes.length) {
            if (action_code.equals(SceGlobals.d_action_codes[i])) {
                return false;
            }
            ++i;
        }
        return true;
    }

    void query(String sce_number, String designation20, String modDate) {
        block38: {
            String a1 = sce_number.trim();
            String a2 = designation20;
            String a3 = modDate;
            String qStr = "";
            SimpleDateFormat formater = new SimpleDateFormat(SceGlobals.DATE_FORMAT_UI);
            SimpleDateFormat formaterDB = new SimpleDateFormat(SceGlobals.DATE_FORMAT_DB);
            try {
                if (a1.equals("")) {
                    a1 = "%";
                }
                if (a2.equals("")) {
                    a2 = "%";
                }
                if (a3.equals("")) {
                    a3 = "";
                }
                this.d_sceEntryArea.setValues(a1, a2, a3);
                this.data.removeAllElements();
                this.conn = DatabaseConnector.getCustomDBConnection();
                NotifyService.timeDiff("Connected to Oracle");
                this.stmt = this.conn.createStatement();
                NotifyService.timeDiff("building select statement select VALUE from  nls_session_parameters");
                String nlsDateStr = "select VALUE from  nls_session_parameters where PARAMETER like 'NLS_DATE_FORMAT'";
                NotifyService.timeDiff("Building select statement select VALUE from  nls_session_parameters");
                this.rs = this.stmt.executeQuery(nlsDateStr);
                this.md = this.rs.getMetaData();
                NotifyService.timeDiff("retrieved result of select statement select VALUE from  nls_session_parameters");
                while (this.rs.next()) {
                    if (SceGlobals.debug) {
                        System.out.print("Info : NLS_DATE_FORMAT from database is ");
                    }
                    Vector RecSet = new Vector();
                    int i = 1;
                    while (i <= this.md.getColumnCount()) {
                        String value = this.rs.getString(i);
                        if (SceGlobals.debug) {
                            System.out.println(String.valueOf(value) + ".");
                        }
                        ++i;
                    }
                }
                this.rs.close();
                NotifyService.timeDiff("processed result of select statement select VALUE from  nls_session_parameters");
            }
            catch (SQLException e) {
                JOptionPane.showMessageDialog(SceGlobals.sceQuery, "Looking for NLS_DATE_FORMAT has failed: " + e.toString());
            }
            try {
                String dateStr = "";
                System.out.println("a3: " + a3 + "!");
                String dateOperator = SceGlobals.d_dateQueryOp;
                if (a3.startsWith(">=")) {
                    a3 = a3.substring(2, a3.length());
                    dateOperator = ">=";
                }
                if (a3.startsWith("<=")) {
                    a3 = a3.substring(2, a3.length());
                    dateOperator = "<=";
                }
                if (a3.startsWith("=")) {
                    a3 = a3.substring(2, a3.length());
                    dateOperator = "=";
                }
                if (a3.startsWith(">")) {
                    a3 = a3.substring(2, a3.length());
                    dateOperator = ">";
                }
                if (a3.startsWith("<")) {
                    a3 = a3.substring(2, a3.length());
                    dateOperator = "<";
                }
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
                    dateStr = " and MODIFIED_DATE " + dateOperator + " '" + a3DB + "'";
                }
                NotifyService.timeDiff("building select statement select ACTION_CODE, SCE_CODE, LAST_INDEX, SERVICE, CADER_CODE,...");
                qStr = "select ACTION_CODE, SCE_CODE, LAST_INDEX, SERVICE, CADER_CODE, DECOUPAGE, DESIGNATION20, MODIFIED_DATE from q549_sce_plan  where SCE_CODE LIKE '" + a1 + "'" + " and UPPER(DESIGNATION20) LIKE UPPER('" + a2 + "') " + SceGlobals.d_opCodeQuery + dateStr;
                if (SceGlobals.d_designType.equals("integrator")) {
                    qStr = String.valueOf(qStr) + SceGlobals.d_integratorFilterQueryString;
                }
                if (SceGlobals.d_designType.equals("logic")) {
                    qStr = String.valueOf(qStr) + SceGlobals.d_logicFilterQueryString;
                }
                if (SceGlobals.d_designType.equals("harness")) {
                    qStr = String.valueOf(qStr) + SceGlobals.d_harnessFilterQueryString;
                }
                qStr = String.valueOf(qStr) + " order by SCE_CODE";
                if (SceGlobals.debug) {
                    System.out.println("Query: " + qStr);
                }
                NotifyService.timeDiff("Prepare query");
                this.rs = this.stmt.executeQuery(qStr);
                NotifyService.timeDiff("Query execution time");
                this.md = this.rs.getMetaData();
                NotifyService.timeDiff("Query getMetaData time");
                NotifyService.timeDiff("retrieved result select statement select ACTION_CODE, SCE_CODE, LAST_INDEX, SERVICE, CADER_CODE,...");
                int maxCol = this.md.getColumnCount();
                int c = 1;
                while (c <= maxCol) {
                    this.header.addElement(this.md.getColumnName(c).toString());
                    ++c;
                }
                NotifyService.timeDiff("Filled header vector");
                this.tmpSCEParts = new Vector();
                boolean insertLastIndexofPart = false;
                this.partStartIndex = -1;
                while (this.rs.next()) {
                    Vector<String> RecSet = new Vector<String>();
                    Part RecIdxVec = new Part();
                    c = 1;
                    while (c <= maxCol) {
                        String value = this.rs.getString(c);
                        if (c == 2) {
                            if (this.oldPartNumber.equals("") || !this.oldPartNumber.equals(value)) {
                                this.recIndex = 0;
                            }
                            RecIdxVec.index = this.recIndex;
                        }
                        if (c == 3) {
                            RecIdxVec.lastIndex = value = value.trim();
                            if (value.equals("OR")) {
                                RecIdxVec.lastIndex = "1";
                            }
                        }
                        if (c == 8) {
                            String formatedDate;
                            Date foundDate = this.rs.getDate(8);
                            value = formatedDate = formater.format(foundDate);
                        }
                        RecSet.addElement(value);
                        ++c;
                    }
                    if (SceGlobals.d_latestIssue) {
                        this.keepLatestIssue((String)RecSet.get(1));
                    }
                    ++this.recIndex;
                    this.tmpSCEParts.addElement(RecIdxVec);
                    this.data.addElement(RecSet);
                }
                NotifyService.timeDiff("processed result of select statement select ACTION_CODE, SCE_CODE, LAST_INDEX, SERVICE, CADER_CODE,...");
                if (SceGlobals.d_latestIssue) {
                    this.keepLatestIssue("");
                }
                System.out.println(" Count of entries: " + this.data.size());
                this.rs.close();
                this.conn.close();
            }
            catch (SQLException e) {
                System.out.println(e.toString());
                String dateErr = "\nPlease check your query \n" + qStr + "\n";
                if (!a2.equals("%")) {
                    JOptionPane.showMessageDialog(SceGlobals.sceQuery, dateErr);
                }
                if (SceGlobals.debug) {
                    System.out.println("DATE_FORMAT_UI: " + SceGlobals.DATE_FORMAT_UI);
                }
                if (!SceGlobals.debug) break block38;
                System.out.println("DATE_FORMAT_DB: " + SceGlobals.DATE_FORMAT_DB);
            }
        }
    }

    void keepLatestIssue(String currentPartNumber) {
        String latestIssue = "";
        Object[] parts = null;
        if (this.oldPartNumber.equals("") || !this.oldPartNumber.equals(currentPartNumber)) {
            if (this.tmpSCEParts.size() > 1) {
                parts = new Part[this.tmpSCEParts.size()];
                int i = 0;
                while (i < parts.length) {
                    Part p;
                    parts[i] = p = (Part)this.tmpSCEParts.get(i);
                    ++i;
                }
                Part[] origParts = new Part[parts.length];
                int i2 = 0;
                while (i2 < parts.length) {
                    origParts[i2] = (Part) parts[i2];
                    ++i2;
                }
                Arrays.sort(parts);
                latestIssue = ((Part)parts[parts.length - 1]).lastIndex;
                i2 = 0;
                while (i2 < parts.length) {
                    ++i2;
                }
                int deleted = 0;
                int i3 = 0;
                while (i3 < origParts.length) {
                    int rmIdx = origParts[i3].index + this.partStartIndex - deleted;
                    if (!origParts[i3].lastIndex.equals(latestIssue)) {
                        ++deleted;
                        this.data.remove(rmIdx);
                    }
                    ++i3;
                }
            }
            this.partStartIndex = this.data.size();
            this.oldPartNumber = currentPartNumber;
            this.tmpSCEParts = new Vector();
            this.recIndex = 0;
        }
    }
}

