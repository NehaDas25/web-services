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

public class D_SceEntryArea
extends JPanel
implements ActionListener {
    private static final String newline = "\n";
    JTextField numeroPlanField;
    JTextField designation20Field;
    JTextField modifiedDateField;
    static final int GAP = 10;

    public D_SceEntryArea() {
        this.setLayout(new BoxLayout(this, 2));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.add(this.createEntryFields());
        SceGlobals.d_sce_number_start_value = "";
        System.out.println("SceGlobals.d_indiceSce: " + SceGlobals.d_indiceSce);
        if (SceGlobals.d_indiceSce.contains("revision")) {
            this.numeroPlanField.setEditable(false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        boolean settingChanged = false;
        int i = 0;
        while (i < 4) {
            JCheckBox jcb = null;
            if (SceGlobals.d_checkBoxName.get(i).equals(evt.getActionCommand())) {
                jcb = (JCheckBox)SceGlobals.d_checkBoxPointer.get(i);
                settingChanged = true;
            }
            ++i;
        }
        JCheckBox jcb = (JCheckBox)SceGlobals.d_checkBoxPointer.get(4);
        SceGlobals.d_latestIssue = jcb.isSelected();
        if (settingChanged) {
            this.setOpCodeQuery();
        }
    }

    public void showValues() {
        System.out.println("numeroPlanField \t\t" + this.numeroPlanField.getText());
        System.out.println("designation20Field \t\t" + this.designation20Field.getText());
        System.out.println("modifiedDateField \t" + this.modifiedDateField.getText());
    }

    public void setValues(String f1, String f2, String f3) {
        this.numeroPlanField.setText(f1);
        this.designation20Field.setText(f2);
        this.modifiedDateField.setText(f3);
    }

    public String[] getValues() {
        String[] retVal = new String[]{this.numeroPlanField.getText(), this.designation20Field.getText(), this.modifiedDateField.getText()};
        return retVal;
    }

    protected JComponent createEntryFields() {
        JCheckBox tmpBox;
        SceGlobals.d_checkBoxName = new Vector();
        SceGlobals.d_checkBoxPointer = new Vector();
        SceGlobals.d_checkBoxValueT = new Vector();
        Box checkBox = Box.createHorizontalBox();
        checkBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Action Filters: "), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        if (SceGlobals.d_designType.equalsIgnoreCase("harness")) {
            tmpBox = this.addCheckBox(checkBox, "  C", true, "C");
            tmpBox.setEnabled(true);
        } else {
            tmpBox = this.addCheckBox(checkBox, "  C", true, "C");
            tmpBox.setEnabled(true);
        }
        checkBox.add(tmpBox);
        checkBox.add(Box.createHorizontalStrut(20));
        tmpBox = this.addCheckBox(checkBox, "  M", true, "M");
        tmpBox.setEnabled(true);
        checkBox.add(tmpBox);
        checkBox.add(Box.createHorizontalStrut(20));
        tmpBox = this.addCheckBox(checkBox, "  O", false, "O");
        tmpBox.setEnabled(true);
        checkBox.add(tmpBox);
        checkBox.add(Box.createHorizontalStrut(20));
        tmpBox = this.addCheckBox(checkBox, "  A,  E,  R", false, "A','E','R");
        tmpBox.setEnabled(true);
        checkBox.add(tmpBox);
        checkBox.setMinimumSize(new Dimension(300, 70));
        checkBox.setMaximumSize(new Dimension(300, 70));
        Box checkBox1 = Box.createVerticalBox();
        checkBox1.add(checkBox);
        checkBox1.add(Box.createVerticalGlue());
        Box latestIssueBox = Box.createVerticalBox();
        tmpBox = this.addCheckBox(checkBox, "Latest Issue", true, "0");
        latestIssueBox.add(tmpBox);
        tmpBox.setMnemonic(85);
        latestIssueBox.add(Box.createVerticalGlue());
        Box panel = Box.createHorizontalBox();
        String[] labelStrings = new String[]{"Numero Plan: ", "Designation20", "Modified Date: "};
        JLabel[] labels = new JLabel[labelStrings.length];
        JComponent[] fields = new JComponent[labelStrings.length];
        int fieldNum = 0;
        this.numeroPlanField = new JTextField("", 10);
        this.numeroPlanField.setMinimumSize(new Dimension(260, 0));
        this.numeroPlanField.setMaximumSize(new Dimension(300, 0));
        fields[fieldNum++] = this.numeroPlanField;
        this.designation20Field = new JTextField("", 8);
        this.designation20Field.setMinimumSize(new Dimension(260, 0));
        this.designation20Field.setMaximumSize(new Dimension(300, 0));
        fields[fieldNum++] = this.designation20Field;
        this.modifiedDateField = new JTextField("", 8);
        this.modifiedDateField.setMinimumSize(new Dimension(260, 0));
        this.modifiedDateField.setMaximumSize(new Dimension(300, 0));
        fields[fieldNum++] = this.modifiedDateField;
        Box labelPane = Box.createVerticalBox();
        Box fieldPane = Box.createVerticalBox();
        labelPane.setMinimumSize(new Dimension(200, 100));
        labelPane.setMaximumSize(new Dimension(200, 100));
        fieldPane.setPreferredSize(new Dimension(280, 100));
        fieldPane.setMinimumSize(new Dimension(300, 100));
        fieldPane.setMaximumSize(new Dimension(300, 100));
        int i = 0;
        while (i < labelStrings.length) {
            labels[i] = new JLabel(labelStrings[i], 10);
            labels[i].setLabelFor(fields[i]);
            labels[i].setLabelFor(fields[i]);
            labels[i].setForeground(SceGlobals.LABEL_COLOR);
            labels[i].setToolTipText(">,<,=,>=,<=  Default: > ");
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
        panel.add(latestIssueBox);
        panel.add(Box.createHorizontalGlue());
        panel.add(checkBox1);
        this.setOpCodeQuery();
        return panel;
    }

    public JCheckBox addCheckBox(Box box, String name, boolean set, String valT) {
        JCheckBox checkBox = new JCheckBox(name);
        checkBox.addActionListener(this);
        checkBox.setSelected(set);
        box.add(checkBox);
        SceGlobals.d_checkBoxName.add(name);
        SceGlobals.d_checkBoxPointer.add(checkBox);
        SceGlobals.d_checkBoxValueT.add(valT);
        return checkBox;
    }

    public void getOpCode() {
        System.out.println("Check box vector size " + SceGlobals.d_checkBoxName.size());
        System.out.println(SceGlobals.d_checkBoxName);
    }

    public void setOpCodeQuery() {
        String txt = "";
        String txt1 = "";
        String sep = "";
        int i = 0;
        while (i < 4) {
            JCheckBox jcb = (JCheckBox)SceGlobals.d_checkBoxPointer.get(i);
            if (jcb.isSelected()) {
                txt = String.valueOf(txt) + sep + "'" + SceGlobals.d_checkBoxValueT.get(i) + "'";
                txt1 = String.valueOf(txt1) + sep + " " + SceGlobals.d_checkBoxValueT.get(i);
                if (sep.equals("")) {
                    sep = ",";
                }
            }
            ++i;
        }
        txt = txt.equals("") ? " and ACTION_CODE IN ('')" : " and ACTION_CODE IN (" + txt + ")";
        SceGlobals.d_opCodeQuery = txt;
        SceGlobals.d_opCodeQueryTxt = txt1;
    }
}

