package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 */
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SceEntryArea
extends JPanel
implements ActionListener {
    private static final String newline = "\n";
    JTextField operationField;
    JTextField compSapField;
    JTextField designation40Field;
    JTextField modifiedDateField;
    static final int GAP = 10;

    public SceEntryArea() {
        this.setLayout(new BoxLayout(this, 2));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.add(this.createEntryFields());
        this.add(Box.createRigidArea(new Dimension(300, 10)), "After");
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        boolean settingChanged = false;
        int i = 0;
        while (i < 7) {
            JCheckBox jcb = null;
            if (SceGlobals.checkBoxName.get(i).equals(evt.getActionCommand())) {
                jcb = (JCheckBox)SceGlobals.checkBoxPointer.get(i);
                settingChanged = true;
                if (i == 0) {
                    this.setUnsetOpCode(jcb.isSelected());
                }
                if (i == 6) {
                    SceGlobals.latIssueQuery = jcb.isSelected() ? " and SYNC_FLAG = 0" : "";
                }
            }
            ++i;
        }
        if (settingChanged) {
            this.setOpCodeQuery();
        }
    }

    public void showValues() {
        System.out.println("operationField \t\t" + this.operationField.getText());
        System.out.println("compSapField \t\t" + this.compSapField.getText());
        System.out.println("designation40Field \t" + this.designation40Field.getText());
        System.out.println("modifiedDateField \t" + this.modifiedDateField.getText());
    }

    public void setValues(String f1, String f2, String f3, String f4) {
        this.compSapField.setText(f2);
        this.designation40Field.setText(f3);
        this.modifiedDateField.setText(f4);
    }

    public String[] getValues() {
        String[] retVal = new String[]{this.operationField.getText(), this.compSapField.getText(), this.designation40Field.getText(), this.modifiedDateField.getText()};
        return retVal;
    }

    protected JComponent createEntryFields() {
        SceGlobals.checkBoxName = new Vector();
        SceGlobals.checkBoxPointer = new Vector();
        SceGlobals.checkBoxValueT = new Vector();
        Box b = Box.createHorizontalBox();
        Box checkBox = Box.createVerticalBox();
        checkBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Operation Filters: "), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        checkBox.add(this.addCheckBox(checkBox, "Check All", false, "ALL"));
        checkBox.add(Box.createVerticalStrut(10));
        JCheckBox tmpBox = this.addCheckBox(checkBox, "MODI", true, "MODI");
        checkBox.add(tmpBox);
        tmpBox.setMnemonic(68);
        tmpBox = this.addCheckBox(checkBox, "EXIS", true, "EXIS");
        checkBox.add(tmpBox);
        tmpBox.setMnemonic(88);
        tmpBox = this.addCheckBox(checkBox, "CREA", true, "CREA");
        checkBox.add(tmpBox);
        tmpBox.setMnemonic(82);
        tmpBox = this.addCheckBox(checkBox, "OBSO", false, "OBSO");
        checkBox.add(tmpBox);
        tmpBox.setMnemonic(66);
        tmpBox = this.addCheckBox(checkBox, "SUPP", false, "SUPP");
        checkBox.add(tmpBox);
        tmpBox.setMnemonic(85);
        Box latestIssueBox = Box.createVerticalBox();
        latestIssueBox.add(new JLabel(" "));
        latestIssueBox.add(Box.createVerticalStrut(10));
        tmpBox = this.addCheckBox(checkBox, "To be synchronized", false, "0");
        latestIssueBox.add(tmpBox);
        tmpBox.setMnemonic(90);
        latestIssueBox.add(Box.createVerticalGlue());
        Box panel = Box.createHorizontalBox();
        String[] labelStrings = new String[]{"Operation: ", "SAP Number: ", "Designation40: ", "Modifie Date: "};
        JLabel[] labels = new JLabel[labelStrings.length];
        JComponent[] fields = new JComponent[labelStrings.length];
        int fieldNum = 0;
        this.operationField = new JTextField("", 5);
        this.operationField.setEditable(false);
        this.operationField.setMinimumSize(new Dimension(260, 0));
        this.operationField.setMaximumSize(new Dimension(300, 0));
        fields[fieldNum++] = this.operationField;
        this.compSapField = new JTextField("", 10);
        this.compSapField.setMinimumSize(new Dimension(260, 0));
        this.compSapField.setMaximumSize(new Dimension(300, 0));
        fields[fieldNum++] = this.compSapField;
        this.designation40Field = new JTextField("", 8);
        this.designation40Field.setMinimumSize(new Dimension(260, 0));
        this.designation40Field.setMaximumSize(new Dimension(300, 0));
        fields[fieldNum++] = this.designation40Field;
        this.modifiedDateField = new JTextField("", 8);
        this.modifiedDateField.setMinimumSize(new Dimension(260, 0));
        this.modifiedDateField.setMaximumSize(new Dimension(300, 0));
        fields[fieldNum++] = this.modifiedDateField;
        Box labelPane = Box.createVerticalBox();
        Box fieldPane = Box.createVerticalBox();
        labelPane.setMinimumSize(new Dimension(200, 160));
        labelPane.setMaximumSize(new Dimension(200, 160));
        fieldPane.setPreferredSize(new Dimension(280, 160));
        fieldPane.setMinimumSize(new Dimension(300, 160));
        fieldPane.setMaximumSize(new Dimension(300, 160));
        int i = 0;
        while (i < labelStrings.length) {
            labels[i] = new JLabel(labelStrings[i], 10);
            labels[i].setLabelFor(fields[i]);
            labels[i].setLabelFor(fields[i]);
            labels[i].setForeground(SceGlobals.LABEL_COLOR);
            labelPane.add(labels[i]);
            labelPane.add(Box.createVerticalStrut(12));
            fieldPane.add(fields[i]);
            fieldPane.add(Box.createVerticalStrut(5));
            ++i;
        }
        labelPane.add(Box.createVerticalGlue());
        fieldPane.add(Box.createVerticalGlue());
        panel.add(Box.createHorizontalStrut(10));
        panel.add(labelPane);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(fieldPane);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(checkBox);
        panel.add(latestIssueBox);
        panel.add(SceGlobals.suppQuery);
        panel.add(Box.createHorizontalGlue());
        this.setOpCodeQuery();
        return panel;
    }

    public JCheckBox addCheckBox(Box box, String name, boolean set, String valT) {
        JCheckBox checkBox = new JCheckBox(name);
        checkBox.addActionListener(this);
        checkBox.setSelected(set);
        box.add(checkBox);
        SceGlobals.checkBoxName.add(name);
        SceGlobals.checkBoxPointer.add(checkBox);
        SceGlobals.checkBoxValueT.add(valT);
        return checkBox;
    }

    public void setUnsetOpCode(boolean all) {
        int i = 1;
        while (i < 6) {
            JCheckBox jcb = (JCheckBox)SceGlobals.checkBoxPointer.get(i);
            jcb.setSelected(all);
            ++i;
        }
    }

    public void getOpCode() {
        System.out.println("Check box vector size " + SceGlobals.checkBoxName.size());
        System.out.println(SceGlobals.checkBoxName);
    }

    public void setOpCodeQuery() {
        String txt = "";
        String txt1 = "";
        String sep = "";
        int i = 1;
        while (i < 6) {
            JCheckBox jcb = (JCheckBox)SceGlobals.checkBoxPointer.get(i);
            if (jcb.isSelected()) {
                txt = String.valueOf(txt) + sep + "'" + SceGlobals.checkBoxValueT.get(i) + "'";
                txt1 = String.valueOf(txt1) + sep + " " + SceGlobals.checkBoxValueT.get(i);
                if (sep.equals("")) {
                    sep = ",";
                }
            }
            ++i;
        }
        if (!txt.equals("")) {
            txt = " and OP_CODE IN (" + txt + ")";
        }
        SceGlobals.opCodeQuery = txt;
        SceGlobals.opCodeQueryTxt = txt1;
        this.operationField.setText(txt1);
    }
}

