package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 */
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class D_SceUpdateArea
extends JPanel
implements ActionListener {
    private final String NAME = "name";
    private final String PART_NUMBER = "partnumber";
    private final String REVISION = "revision";
    private final String ERROR_MESSAGE_NOT_UNIQUE_RECORD = "Part Number : referencePSA and Revision : indicePlan combination already present in database.";
    private final String ERROR_MESSAGE_NOTHING_SELECTED = "Nothing selected from SAP table, please select a record from SAP table to Add or select Temp button";
    private JButton parentTemp;
    private JButton parentAdd;
    private JButton[] childTemp;
    private JButton[] childAdd;
    private JTextField parentCurrentName;
    private JTextField parentCurrentPartNumber;
    private JTextField parentCurrentRevision;
    private JTextField parentNewName;
    private JTextField parentNewPartNumber;
    private JTextField parentNewRevision;
    private JTextField[] childCurrentName;
    private JTextField[] childCurrentPartNumber;
    private JTextField[] childCurrentRevision;
    private JTextField[] childNewName;
    private JTextField[] childNewPartNumber;
    private JTextField[] childNewRevision;
    private String copyOrRevise;
    private Element designAttributesObject;
    private Element[] childAttributesObject;
    private Set<String> updateRecordsSet;
    private String textForChildrenLabel;
    private final String TEMP_BUTTON_TOOL_TIP = "Assign temporary record";
    private final String ADD_BUTTON_TOOL_TIP = "Assign selected record";

    public D_SceUpdateArea(Document document, String childType) {
        NodeList designAttributes = document.getElementsByTagName("designattributesdata");
        NodeList childAttributes = document.getElementsByTagName(childType);
        this.textForChildrenLabel = childType.equals("derivativeattributesdata") ? "DERIVATIVES" : "MODULES";
        this.designAttributesObject = (Element)designAttributes.item(0);
        this.childAttributesObject = new Element[childAttributes.getLength()];
        int i = 0;
        while (i < this.childAttributesObject.length) {
            this.childAttributesObject[i] = (Element)childAttributes.item(i);
            ++i;
        }
        this.childTemp = new JButton[childAttributes.getLength()];
        this.childAdd = new JButton[childAttributes.getLength()];
        this.childCurrentName = new JTextField[childAttributes.getLength()];
        this.childCurrentPartNumber = new JTextField[childAttributes.getLength()];
        this.childCurrentRevision = new JTextField[childAttributes.getLength()];
        this.childNewName = new JTextField[childAttributes.getLength()];
        this.childNewPartNumber = new JTextField[childAttributes.getLength()];
        this.childNewRevision = new JTextField[childAttributes.getLength()];
        SceGlobals.childNewName = this.childNewName;
        SceGlobals.childNewPartNumber = this.childNewPartNumber;
        SceGlobals.childNewRevision = this.childNewRevision;
        SceGlobals.updatedChildRecords = new SAPRecord[childAttributes.getLength()];
        this.copyOrRevise = SceGlobals.d_dialogueInvocationCause.equalsIgnoreCase("copy") ? "copy" : "revise";
        this.updateRecordsSet = new HashSet<String>();
        this.initialize();
    }

    private void initialize() {
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        this.setLayout(layout);
        JLabel jLabelCurrent = new JLabel();
        String font = UIManager.getDefaults().getFont("Label.font").toString();
        jLabelCurrent.setFont(new Font(font, 1, 16));
        jLabelCurrent.setText("CURRENT");
        JLabel jLabelNew = new JLabel();
        jLabelNew.setFont(new Font(font, 1, 16));
        jLabelNew.setText("NEW");
        gbc.ipady = 20;
        gbc.gridx = 3;
        gbc.gridy = 0;
        this.add((Component)jLabelCurrent, gbc);
        gbc.gridx = 7;
        gbc.gridy = 0;
        this.add((Component)jLabelNew, gbc);
        JLabel jLabelParent1 = new JLabel();
        jLabelParent1.setText("COMPOSITE");
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.ipady = 10;
        this.add((Component)jLabelParent1, gbc);
        JLabel jLabelParent2 = new JLabel();
        jLabelParent2.setText("COMPOSITE");
        gbc.gridx = 7;
        gbc.gridy = 1;
        gbc.ipady = 10;
        this.add((Component)jLabelParent2, gbc);
        gbc.ipady = 0;
        gbc.gridy = 2;
        String[] columnNamesParent = new String[]{"", "", "NAME", "PART NUMBER", "REVISION", "      ", "NAME", "PART NUMBER", "REVISION", ""};
        int i = 0;
        while (i < columnNamesParent.length) {
            JLabel jColumnHeader = new JLabel();
            jColumnHeader.setText(columnNamesParent[i]);
            gbc.gridx = i++;
            this.add((Component)jColumnHeader, gbc);
        }
        this.populateParent();
        JLabel jLabelDerivatives1 = new JLabel();
        jLabelDerivatives1.setText(this.textForChildrenLabel);
        gbc.gridx = 3;
        gbc.gridy = 5;
        gbc.ipady = 30;
        this.add((Component)jLabelDerivatives1, gbc);
        JLabel jLabelDerivatives2 = new JLabel();
        jLabelDerivatives2.setText(this.textForChildrenLabel);
        gbc.gridx = 7;
        gbc.gridy = 5;
        gbc.ipady = 30;
        this.add((Component)jLabelDerivatives2, gbc);
        String[] columnNamesDerivatives = new String[]{"", "", "NAME", "PART NUMBER", "REVISION", "", "NAME", "PART NUMBER", "REVISION", ""};
        gbc.gridy = 6;
        gbc.ipady = 0;
        int i2 = 0;
        while (i2 < columnNamesDerivatives.length) {
            JLabel jColumnHeader = new JLabel();
            jColumnHeader.setText(columnNamesDerivatives[i2]);
            gbc.gridx = i2++;
            this.add((Component)jColumnHeader, gbc);
        }
        gbc.ipady = 0;
        this.populateChildren();
    }

    private void populateParent() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 5);
        this.parentTemp = new JButton();
        this.parentTemp.setText("Temp");
        this.parentTemp.setActionCommand("TEMP_TO_PARENT");
        this.parentTemp.addActionListener(this);
        this.parentTemp.setToolTipText("Assign temporary record");
        gbc.gridx = 0;
        gbc.gridy = 3;
        this.add((Component)this.parentTemp, gbc);
        this.parentAdd = new JButton();
        this.parentAdd.setText("Add");
        this.parentAdd.setActionCommand("ADD_TO_PARENT");
        this.parentAdd.addActionListener(this);
        this.parentAdd.setToolTipText("Assign selected record");
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 0, 10);
        this.add((Component)this.parentAdd, gbc);
        gbc.insets = new Insets(0, 0, 0, 5);
        String currentNameOfParent = this.designAttributesObject.getAttribute("name");
        String currentPartNumberOfParent = this.designAttributesObject.getAttribute("partnumber");
        String currentRevisionOfParent = this.designAttributesObject.getAttribute("revision");
        this.parentCurrentName = new JTextField(8);
        this.parentCurrentName.setText(currentNameOfParent);
        this.parentCurrentName.setToolTipText(currentNameOfParent);
        this.parentCurrentName.setEditable(false);
        gbc.gridx = 2;
        gbc.gridy = 3;
        this.add((Component)this.parentCurrentName, gbc);
        this.parentCurrentPartNumber = new JTextField(8);
        this.parentCurrentPartNumber.setText(currentPartNumberOfParent);
        this.parentCurrentPartNumber.setToolTipText(currentPartNumberOfParent);
        this.parentCurrentPartNumber.setEditable(false);
        gbc.gridx = 3;
        gbc.gridy = 3;
        this.add((Component)this.parentCurrentPartNumber, gbc);
        this.parentCurrentRevision = new JTextField(5);
        this.parentCurrentRevision.setText(currentRevisionOfParent);
        this.parentCurrentRevision.setToolTipText(currentRevisionOfParent);
        this.parentCurrentRevision.setEditable(false);
        gbc.gridx = 4;
        gbc.gridy = 3;
        this.add((Component)this.parentCurrentRevision, gbc);
        this.parentNewName = new JTextField(8);
        this.parentNewName.setText(currentNameOfParent);
        if (this.isCopy()) {
            this.parentNewName.setEditable(true);
        } else {
            this.parentNewName.setEditable(false);
            this.parentNewName.setToolTipText(currentNameOfParent);
        }
        gbc.gridx = 6;
        gbc.gridy = 3;
        this.add((Component)this.parentNewName, gbc);
        this.parentNewPartNumber = new JTextField(8);
        this.parentNewPartNumber.setEditable(false);
        this.parentNewPartNumber.setBackground(Color.WHITE);
        gbc.gridx = 7;
        gbc.gridy = 3;
        this.add((Component)this.parentNewPartNumber, gbc);
        this.parentNewRevision = new JTextField(5);
        this.parentNewRevision.setEditable(false);
        this.parentNewRevision.setBackground(Color.WHITE);
        gbc.gridx = 8;
        gbc.gridy = 3;
        this.add((Component)this.parentNewRevision, gbc);
        SceGlobals.parentNewName = this.parentNewName;
        SceGlobals.parentNewPartNumber = this.parentNewPartNumber;
        SceGlobals.parentNewRevision = this.parentNewRevision;
    }

    private void populateChildren() {
        GridBagConstraints gbc = new GridBagConstraints();
        int grid_y = 7;
        int i = 0;
        while (i < this.childAttributesObject.length) {
            gbc.insets = new Insets(0, 0, 5, 5);
            Element currentChild = this.childAttributesObject[i];
            String currentNameOfChild = currentChild.getAttribute("name");
            String currentPartNumberOfChild = currentChild.getAttribute("partnumber");
            String currentRevisionOfChild = currentChild.getAttribute("revision");
            this.childTemp[i] = new JButton();
            this.childTemp[i].setText("Temp");
            this.childTemp[i].setActionCommand("TEMP_TO_" + String.valueOf(i));
            this.childTemp[i].addActionListener(this);
            this.childTemp[i].setToolTipText("Assign temporary record");
            gbc.gridx = 0;
            gbc.gridy = grid_y;
            this.add((Component)this.childTemp[i], gbc);
            this.childAdd[i] = new JButton();
            this.childAdd[i].setText("Add");
            this.childAdd[i].setActionCommand("ADD_TO_" + String.valueOf(i));
            this.childAdd[i].addActionListener(this);
            this.childAdd[i].setToolTipText("Assign selected record");
            gbc.gridx = 1;
            gbc.gridy = grid_y;
            gbc.insets = new Insets(0, 0, 5, 10);
            this.add((Component)this.childAdd[i], gbc);
            gbc.insets = new Insets(0, 0, 5, 5);
            this.childCurrentName[i] = new JTextField(8);
            this.childCurrentName[i].setText(currentNameOfChild);
            this.childCurrentName[i].setToolTipText(currentNameOfChild);
            this.childCurrentName[i].setEditable(false);
            gbc.gridx = 2;
            gbc.gridy = grid_y;
            this.add((Component)this.childCurrentName[i], gbc);
            this.childCurrentPartNumber[i] = new JTextField(8);
            this.childCurrentPartNumber[i].setText(currentPartNumberOfChild);
            this.childCurrentPartNumber[i].setToolTipText(currentPartNumberOfChild);
            this.childCurrentPartNumber[i].setEditable(false);
            gbc.gridx = 3;
            gbc.gridy = grid_y;
            this.add((Component)this.childCurrentPartNumber[i], gbc);
            this.childCurrentRevision[i] = new JTextField(5);
            this.childCurrentRevision[i].setText(currentRevisionOfChild);
            this.childCurrentRevision[i].setToolTipText(currentRevisionOfChild);
            this.childCurrentRevision[i].setEditable(false);
            gbc.gridx = 4;
            gbc.gridy = grid_y;
            this.add((Component)this.childCurrentRevision[i], gbc);
            this.childNewName[i] = new JTextField(8);
            this.childNewName[i].setText(currentNameOfChild);
            if (this.isCopy()) {
                this.childNewName[i].setEditable(true);
            } else {
                this.childNewName[i].setEditable(false);
                this.childNewName[i].setToolTipText(currentNameOfChild);
            }
            gbc.gridx = 6;
            gbc.gridy = grid_y;
            this.add((Component)this.childNewName[i], gbc);
            this.childNewPartNumber[i] = new JTextField(8);
            this.childNewPartNumber[i].setEditable(false);
            this.childNewPartNumber[i].setBackground(Color.WHITE);
            gbc.gridx = 7;
            gbc.gridy = grid_y;
            this.add((Component)this.childNewPartNumber[i], gbc);
            this.childNewRevision[i] = new JTextField(5);
            this.childNewRevision[i].setEditable(false);
            this.childNewRevision[i].setBackground(Color.WHITE);
            gbc.gridx = 8;
            gbc.gridy = grid_y++;
            this.add((Component)this.childNewRevision[i], gbc);
            ++i;
        }
        gbc.weighty = 1.0;
        ++gbc.gridy;
        this.add((Component)new JLabel(" "), gbc);
    }

    private boolean isCopy() {
        return this.copyOrRevise.equalsIgnoreCase("copy");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String[] command = e.getActionCommand().split("_");
        if (command[0].equalsIgnoreCase("TEMP")) {
            String temp = this.getNextTemp();
            if (temp != null) {
                while (this.checkIfPartNumberRevisionExists(temp, "-")) {
                    temp = this.getNextTemp();
                }
                SAPRecord selectedRecord = new SAPRecord(null, temp, "-", null, null, null, null, null);
                if (command[2].equalsIgnoreCase("PARENT")) {
                    this.parentNewPartNumber.setText(temp);
                    this.parentNewPartNumber.setToolTipText(null);
                    this.parentNewRevision.setText("-");
                    this.parentNewRevision.setToolTipText(null);
                    SceGlobals.updatedParentRecord = selectedRecord;
                } else {
                    int index = Integer.parseInt(command[2]);
                    this.childNewPartNumber[index].setText(temp);
                    this.childNewPartNumber[index].setToolTipText(null);
                    this.childNewRevision[index].setText("-");
                    this.childNewRevision[index].setToolTipText(null);
                    SceGlobals.updatedChildRecords[index] = selectedRecord;
                }
            }
        } else if (command[0].equalsIgnoreCase("ADD")) {
            JTable sapTable = SceGlobals.d_sapTable;
            int selectedIndex = sapTable.getSelectedRow();
            if (selectedIndex > -1) {
                String actionCode = sapTable.getModel().getValueAt(selectedIndex, 0).toString();
                String referencePSA = sapTable.getModel().getValueAt(selectedIndex, 1).toString();
                String indicePlan = sapTable.getModel().getValueAt(selectedIndex, 2).toString();
                String service = sapTable.getModel().getValueAt(selectedIndex, 3).toString();
                String codeCader = sapTable.getModel().getValueAt(selectedIndex, 4).toString();
                String decoupage = sapTable.getModel().getValueAt(selectedIndex, 5).toString();
                String plmSAPDescription = sapTable.getModel().getValueAt(selectedIndex, 6).toString();
                String modifiedDate = sapTable.getModel().getValueAt(selectedIndex, 7).toString();
                SAPRecord selectedRecord = new SAPRecord(actionCode, referencePSA, indicePlan, service, codeCader, decoupage, plmSAPDescription, modifiedDate);
                if (!this.checkIfPartNumberRevisionExists(referencePSA, indicePlan)) {
                    if (command[2].equalsIgnoreCase("PARENT")) {
                        if (this.checkIfPartNumberRevisionUsed(referencePSA, indicePlan, this.parentNewPartNumber, this.parentNewRevision)) {
                            String error_message = "Part Number : " + referencePSA + " Revision : " + indicePlan + " already used here";
                            JOptionPane.showMessageDialog(null, error_message, "Cannot Assign Record", 0);
                            return;
                        }
                        this.parentNewPartNumber.setText(referencePSA);
                        this.parentNewPartNumber.setToolTipText(referencePSA);
                        this.parentNewRevision.setText(indicePlan);
                        this.parentNewRevision.setToolTipText(indicePlan);
                        SceGlobals.updatedParentRecord = selectedRecord;
                    } else {
                        int index = Integer.parseInt(command[2]);
                        if (this.checkIfPartNumberRevisionUsed(referencePSA, indicePlan, this.childNewPartNumber[index], this.childNewRevision[index])) {
                            String error_message = "Part Number : " + referencePSA + " Revision : " + indicePlan + " already used here";
                            JOptionPane.showMessageDialog(null, error_message, "Cannot Assign Record", 0);
                            return;
                        }
                        this.childNewPartNumber[index].setText(referencePSA);
                        this.childNewPartNumber[index].setToolTipText(referencePSA);
                        this.childNewRevision[index].setText(indicePlan);
                        this.childNewRevision[index].setToolTipText(indicePlan);
                        SceGlobals.updatedChildRecords[index] = selectedRecord;
                    }
                } else {
                    String error_message = "Part Number : referencePSA and Revision : indicePlan combination already present in database.".replaceFirst("referencePSA", referencePSA);
                    error_message = error_message.replaceFirst("indicePlan", indicePlan);
                    JOptionPane.showMessageDialog(null, error_message, "Cannot Assign Record", 0);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Nothing selected from SAP table, please select a record from SAP table to Add or select Temp button", "Nothing Selected", 0);
            }
        }
    }

    private boolean checkIfPartNumberRevisionUsed(String partNumber, String revision, JTextField partNumberField, JTextField revisionField) {
        String hashString = String.valueOf(partNumber) + "_" + revision;
        if (!partNumberField.getText().isEmpty() && !revisionField.getText().isEmpty()) {
            if (partNumber.equals(partNumberField.getText()) && revision.equals(revisionField.getText())) {
                return false;
            }
            if (this.updateRecordsSet.contains(hashString)) {
                return true;
            }
            this.updateRecordsSet.remove(String.valueOf(partNumberField.getText()) + "_" + revisionField.getText());
            this.updateRecordsSet.add(hashString);
            return false;
        }
        if (this.updateRecordsSet.contains(hashString)) {
            return true;
        }
        this.updateRecordsSet.add(hashString);
        return false;
    }

    private boolean checkIfPartNumberRevisionExists(String partNumber, String revision) {
        try {
            Connection connection = DatabaseConnector.getChsDBConnection();
            Statement statement = connection.createStatement();
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("select count(*) as count ");
            queryBuilder.append("from HARNESS where inteharn='");
            queryBuilder.append(partNumber);
            queryBuilder.append("' and inteiss='");
            queryBuilder.append(revision);
            queryBuilder.append("'");
            String query = new String(queryBuilder);
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                String count = resultSet.getString("count");
                int occurrence = Integer.parseInt(count);
                resultSet.close();
                statement.close();
                connection.close();
                return occurrence != 0;
            }
            return true;
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", 0);
            return true;
        }
    }

    private String getNextTemp() {
        try {
            Connection connection = DatabaseConnector.getCustomDBConnection();
            Statement statement = connection.createStatement();
            String query = "select seq_q549_harnessdesign_no.nextval from dual";
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                String designNumber = resultSet.getString(1);
                DecimalFormat df = new DecimalFormat("00000000");
                Long designNumberLong = Long.parseLong(designNumber);
                resultSet.close();
                statement.close();
                connection.close();
                return "T" + df.format(designNumberLong);
            }
            return null;
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", 0);
            return null;
        }
    }
}

