package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Buttons
extends JPanel
implements ActionListener {
    protected JButton b1;
    protected JButton b2;
    protected JButton b3;
    protected JButton b4;
    private static final String b1String = "Search";
    private static final String b2String = "Clear";
    private static final String b3String = "Insert";
    private static final String b4String = "Cancel";
    public static final int labelColor = 0x666699;
    public static final Color LABEL_COLOR = new Color(0x666699);
    public static SceEntryArea sceEA;
    public static SceQuery sceQ;

    public Buttons(SceEntryArea sceEntryArea, SceQuery sceQuery) {
        sceEA = sceEntryArea;
        sceQ = sceQuery;
        this.b1 = new JButton(b1String);
        this.b1.setActionCommand(b1String);
        this.b2 = new JButton(b2String);
        this.b2.setActionCommand(b2String);
        this.b3 = new JButton(b3String);
        this.b3.setActionCommand(b3String);
        this.b4 = new JButton(b4String);
        this.b4.setActionCommand(b4String);
        this.b1.addActionListener(this);
        this.b2.addActionListener(this);
        this.b3.addActionListener(this);
        this.b4.addActionListener(this);
        this.b1.setToolTipText("Looking for Component SAP Numbers");
        this.b2.setToolTipText("Clear all entry fileds ");
        this.b3.setToolTipText("Send selected component data to Component Maintenance Window");
        this.b4.setToolTipText("Cancel without actions.");
        this.setLayout(new BoxLayout(this, 2));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.add(this.b1);
        this.add(Box.createRigidArea(new Dimension(10, 0)));
        this.add(this.b2);
        this.add(Box.createRigidArea(new Dimension(10, 0)));
        this.add(this.b3);
        this.add(Box.createRigidArea(new Dimension(10, 0)));
        this.add(this.b4);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String[] entryValue = new String[4];
        if (b1String.equals(e.getActionCommand())) {
            entryValue = sceEA.getValues();
            SceGlobals.sceQuery.query(entryValue[1], entryValue[2], entryValue[3]);
            SceGlobals.sceQuery.resize(sceQ.getWidth(), sceQ.getHeight() + SceGlobals.MAX_WINDOW_HEIGHT_DIFF);
            SceGlobals.MAX_WINDOW_HEIGHT_DIFF = SceGlobals.MAX_WINDOW_HEIGHT_DIFF == 1 ? -1 : 1;
            SceGlobals.sapTable.clearSelection();
            SceGlobals.sceSelected = false;
            SceGlobals.sorter.updateRows();
            SceGlobals.sceQuery.show();
        } else if (b2String.equals(e.getActionCommand())) {
            sceEA.setValues("", "", "", "");
        } else if (b3String.equals(e.getActionCommand())) {
            if (SceGlobals.sceSelected && SceGlobals.suppSelected.equals("")) {
                JOptionPane.showMessageDialog(SceGlobals.sceQuery, "You have to select a supplier in order to propagate a supplier part.");
                return;
            }
            if (!SceGlobals.sceSelected && !SceGlobals.suppSelected.equals("")) {
                JOptionPane.showMessageDialog(SceGlobals.sceQuery, "You have to select a supplier part for inserting.");
                return;
            }
            sceQ.exitSceRecBrowser("Closing SCE Browser");
        } else if (b4String.equals(e.getActionCommand())) {
            SceGlobals.finished = true;
            SceGlobals.sceSelected = false;
            SceGlobals.suppSelected = "";
            SceGlobals.sce_number = "";
            sceQ.dispose();
        }
    }
}

