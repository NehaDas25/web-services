package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class D_Buttons
extends JPanel
implements ActionListener {
    protected JButton b1;
    protected JButton b2;
    protected JButton b3;
    protected JButton b4;
    protected JButton b5;
    private static final String b1String = "Search";
    private static final String b2String = "Clear";
    private static final String b3String = "Insert";
    private static final String b4String = "Cancel";
    private static final String b5String = "Temp";
    public static final int labelColor = 0x666699;
    public static final Color LABEL_COLOR = new Color(0x666699);
    public static D_SceEntryArea sceEA;
    public static D_SceQuery sceQ;
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    ResultSetMetaData md = null;

    public D_Buttons(D_SceEntryArea d_sceEntryArea, D_SceQuery d_sceQuery) {
        sceEA = d_sceEntryArea;
        sceQ = d_sceQuery;
        this.b1 = new JButton(b1String);
        this.b1.setActionCommand(b1String);
        this.b2 = new JButton(b2String);
        this.b2.setActionCommand(b2String);
        this.b3 = new JButton(b3String);
        this.b3.setActionCommand(b3String);
        this.b4 = new JButton(b4String);
        this.b4.setActionCommand(b4String);
        this.b5 = new JButton(b5String);
        this.b5.setActionCommand(b5String);
        this.b1.addActionListener(this);
        this.b2.addActionListener(this);
        this.b3.addActionListener(this);
        this.b4.addActionListener(this);
        this.b5.addActionListener(this);
        this.b1.setToolTipText("Looking for SCE desifn part number");
        this.b2.setToolTipText("Clear all entry fileds ");
        this.b3.setToolTipText("Send selected SCE design part number to design edit window");
        this.b4.setToolTipText("Cancel without actions.");
        this.b5.setToolTipText("Exit browser.Creating a temporary design name");
        this.setLayout(new BoxLayout(this, 2));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.add(this.b1);
        this.add(Box.createRigidArea(new Dimension(10, 0)));
        this.add(this.b2);
        this.add(Box.createRigidArea(new Dimension(10, 0)));
        this.add(this.b3);
        this.add(Box.createRigidArea(new Dimension(10, 0)));
        this.add(this.b4);
        this.add(Box.createHorizontalGlue());
        this.add(this.b5);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String[] entryValue = new String[4];
        SceGlobals.d_harnessTempButtonSelected = false;
        if (b1String.equals(e.getActionCommand())) {
            entryValue = sceEA.getValues();
            System.out.println("entryValue[0] : " + entryValue[0] + " entryValue[1] : " + entryValue[1]);
            System.out.println("SceGlobals.d_indiceSce value " + SceGlobals.d_indiceSce);
            if (SceGlobals.d_indiceSce.contains("revision")) {
                System.out.println("Search click, entryValue[0]- value :" + entryValue[0]);
                JOptionPane.showMessageDialog(SceGlobals.d_sceQuery, "You cannot change the reference after a revision creation. If a reference change is required, you need previously do a copy of your design.");
                return;
            }
            SceGlobals.d_sceQuery.query(entryValue[0], entryValue[1], entryValue[2]);
            SceGlobals.d_sceQuery.resize(sceQ.getWidth(), sceQ.getHeight() + SceGlobals.D_MAX_WINDOW_HEIGHT_DIFF);
            SceGlobals.D_MAX_WINDOW_HEIGHT_DIFF = SceGlobals.D_MAX_WINDOW_HEIGHT_DIFF == 1 ? -1 : 1;
            SceGlobals.d_sapTable.clearSelection();
            SceGlobals.d_sceSelected = false;
            SceGlobals.d_sorter.updateRows();
            SceGlobals.d_sceQuery.show();
        } else if (b2String.equals(e.getActionCommand())) {
            if (!SceGlobals.d_indiceSce.contains("revision")) {
                sceEA.setValues("%", "%", "");
            }
        } else if (b3String.equals(e.getActionCommand())) {
            if (!SceGlobals.d_sceSelected) {
                JOptionPane.showMessageDialog(SceGlobals.d_sceQuery, "You have to select a design part number or use TEMP button for a temporary design name.");
                return;
            }
            System.out.println("Insert SCE design part number: " + SceGlobals.d_sce_number);
            sceQ.exitSceRecBrowser("Closing SCE Design Data Browser");
        } else if (b4String.equals(e.getActionCommand())) {
            SceGlobals.d_finished = true;
            SceGlobals.d_sceSelected = false;
            SceGlobals.d_sce_number = "";
            SceGlobals.d_sceCanceled = true;
            sceQ.dispose();
        } else if (b5String.equals(e.getActionCommand())) {
            String designNo = "";
            try {
                this.conn = DatabaseConnector.getCustomDBConnection();
                this.stmt = this.conn.createStatement();
                String qStr = "";
                qStr = SceGlobals.d_designType.equals("harness") ? "select seq_q549_harnessdesign_no.nextval from dual" : "select seq_q549_design_no.nextval from dual";
                this.rs = this.stmt.executeQuery(qStr);
                this.md = this.rs.getMetaData();
                while (this.rs.next()) {
                    designNo = this.rs.getString(1);
                }
            }
            catch (SQLException sqle) {
                JOptionPane.showMessageDialog(SceGlobals.sceQuery, "SQL error: " + sqle.toString() + "\nCheck whether sequence seq_q549_design_no is defined.");
                return;
            }
            DecimalFormat df = new DecimalFormat("00000000");
            Long a = Long.parseLong(designNo);
            SceGlobals.d_finished = true;
            SceGlobals.d_sceSelected = true;
            if (SceGlobals.d_designType.equals("harness")) {
                SceGlobals.d_sce_number = "T" + df.format(a);
                SceGlobals.d_harnessTempButtonSelected = true;
            } else {
                SceGlobals.d_sce_number = "D" + df.format(a);
            }
            SceGlobals.d_actionCode = "";
            SceGlobals.d_indiceSce = "-";
            SceGlobals.d_service = "";
            SceGlobals.d_codeCader = "";
            SceGlobals.d_decoupage = "";
            SceGlobals.d_designation20 = "";
            sceQ.exitSceRecBrowser("Closing SCE Browser");
        }
    }
}

