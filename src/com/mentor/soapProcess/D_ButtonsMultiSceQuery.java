package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 */
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.LinkedList;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class D_ButtonsMultiSceQuery
extends JPanel
implements ActionListener {
    private Element designAttributesObject;
    private Element[] childAttributesObject;
    private D_SceQueryPanel d_sceQueryPanel;
    private D_MultiSceQuery d_multiSceQuery;
    private D_SceEntryArea d_sceEntryArea;
    private Document document;
    private String invocationCause;

    D_ButtonsMultiSceQuery(Document document, String childType) {
        this.document = document;
        this.d_sceQueryPanel = SceGlobals.d_sceQueryPanel;
        this.d_multiSceQuery = SceGlobals.d_multiSceQuery;
        this.d_sceEntryArea = SceGlobals.d_sceEA;
        this.invocationCause = SceGlobals.d_dialogueInvocationCause;
        NodeList designAttributes = document.getElementsByTagName("designattributesdata");
        NodeList childAttributes = document.getElementsByTagName(childType);
        this.designAttributesObject = (Element)designAttributes.item(0);
        this.childAttributesObject = new Element[childAttributes.getLength()];
        int i = 0;
        while (i < childAttributes.getLength()) {
            this.childAttributesObject[i] = (Element)childAttributes.item(i);
            ++i;
        }
        String SEARCH_BUTTON_TOOL_TIP = "Search in the SAP Table";
        String CLEAR_BUTTON_TOOL_TIP = "Clear all the data in the search fields";
        String OK_BUTTON_TOOL_TIP = "Save and assign all the added values";
        String CANCEL_BUTTON_TOOL_TIP = "Cancel all the changes and close this window";
        JButton searchButton = new JButton();
        searchButton.setText("Search");
        searchButton.setActionCommand("Search");
        searchButton.addActionListener(this);
        searchButton.setToolTipText("Search in the SAP Table");
        JButton clearButton = new JButton();
        clearButton.setText("Clear");
        clearButton.setActionCommand("Clear");
        clearButton.addActionListener(this);
        clearButton.setToolTipText("Clear all the data in the search fields");
        JButton okButton = new JButton();
        okButton.setText("OK");
        okButton.setActionCommand("OK");
        okButton.addActionListener(this);
        okButton.setToolTipText("Save and assign all the added values");
        JButton cancelButton = new JButton();
        cancelButton.setText("Cancel");
        cancelButton.setActionCommand("Cancel");
        cancelButton.addActionListener(this);
        cancelButton.setToolTipText("Cancel all the changes and close this window");
        this.setLayout(new BoxLayout(this, 2));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.add(searchButton);
        this.add(Box.createRigidArea(new Dimension(10, 0)));
        this.add(clearButton);
        this.add(Box.createHorizontalGlue());
        this.add(okButton);
        this.add(Box.createRigidArea(new Dimension(10, 0)));
        this.add(cancelButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand;
        switch (actionCommand = e.getActionCommand()) {
            case "Cancel": {
                this.executeCancelButton();
                break;
            }
            case "Clear": {
                this.executeClearButton();
                break;
            }
            case "OK": {
                this.executeOKButton();
                break;
            }
            case "Search": {
                this.executeSearchButton();
            }
        }
    }

    private void executeCancelButton() {
        SceGlobals.d_finished = true;
        SceGlobals.d_sceSelected = false;
        SceGlobals.d_sce_number = "";
        SceGlobals.d_sceCanceled = true;
        this.d_multiSceQuery.dispose();
    }

    private void executeClearButton() {
        this.d_sceEntryArea.setValues("%", "%", "");
    }

    private void executeSearchButton() {
        String[] entryValue = this.d_sceEntryArea.getValues();
        this.d_sceQueryPanel.query(entryValue[0], entryValue[1], entryValue[2]);
        SceGlobals.d_sapTable.clearSelection();
        this.d_sceQueryPanel.remove(SceGlobals.d_jscrollPanel);
        this.d_sceQueryPanel.add((Component)SceGlobals.d_jscrollPanel, "Center");
        SceGlobals.d_sceSelected = false;
        SceGlobals.d_sorter.updateRows();
    }

    private void executeOKButton() {
        if (this.isAnyNameFieldEmpty()) {
            JOptionPane.showMessageDialog(null, "No Name field should be empty", "Error", 0);
            return;
        }
        if (this.isAnyPartNumberFieldEmpty()) {
            JOptionPane.showMessageDialog(null, "No Part Number field should be empty", "Error", 0);
            return;
        }
        if (this.isAnyRevisionFieldEmpty()) {
            JOptionPane.showMessageDialog(null, "No Revision field should be empty", "Error", 0);
            return;
        }
        LinkedList<String> duplicates = this.anyDuplicateNames();
        if (duplicates.size() > 0) {
            StringBuilder sbOfNames = new StringBuilder();
            int counter = 0;
            for (String name : duplicates) {
                if (counter == duplicates.size() - 1) {
                    sbOfNames.append(name);
                } else {
                    sbOfNames.append(name).append(", ");
                }
                ++counter;
            }
            JOptionPane.showMessageDialog(null, "Duplicate names exist : " + sbOfNames.toString(), "Error", 0);
            return;
        }
        if (this.invocationCause.equalsIgnoreCase("copy")) {
            LinkedList<String> names = this.findNamesExistingInProject();
            if (names == null) {
                return;
            }
            if (names.size() > 0) {
                StringBuilder sbOfNames = new StringBuilder();
                int counter = 0;
                for (String name : names) {
                    if (counter == names.size() - 1) {
                        sbOfNames.append(name);
                    } else {
                        sbOfNames.append(name).append(", ");
                    }
                    ++counter;
                }
                JOptionPane.showMessageDialog(null, "Design names already existing in project. Please update design names : " + sbOfNames.toString(), "Error", 0);
                return;
            }
        }
        this.designAttributesObject.setAttribute("name", SceGlobals.parentNewName.getText());
        this.designAttributesObject.setAttribute("partnumber", SceGlobals.parentNewPartNumber.getText());
        this.designAttributesObject.setAttribute("revision", SceGlobals.parentNewRevision.getText());
        this.addProperties(this.designAttributesObject, SceGlobals.updatedParentRecord);
        int i = 0;
        while (i < this.childAttributesObject.length) {
            this.childAttributesObject[i].setAttribute("name", SceGlobals.childNewName[i].getText());
            this.childAttributesObject[i].setAttribute("partnumber", SceGlobals.childNewPartNumber[i].getText());
            this.childAttributesObject[i].setAttribute("revision", SceGlobals.childNewRevision[i].getText());
            this.addProperties(this.childAttributesObject[i], SceGlobals.updatedChildRecords[i]);
            ++i;
        }
        this.executeCancelButton();
    }

    private void addProperties(Element designAttributesObject, SAPRecord sapRecord) {
        boolean serviceAdded = false;
        boolean caderAdded = false;
        boolean plmSAPDescAdded = false;
        boolean decoupageAdded = false;
        NodeList properties = designAttributesObject.getElementsByTagName("property");
        int i = 0;
        while (i < properties.getLength()) {
            Element property = (Element)properties.item(i);
            switch (property.getAttribute("name")) {
                case "Service": {
                    this.updateProperty(property, sapRecord.Service);
                    serviceAdded = true;
                    break;
                }
                case "Cader": {
                    this.updateProperty(property, sapRecord.Code_Cader);
                    caderAdded = true;
                    break;
                }
                case "PLM/SAP Description": {
                    this.updateProperty(property, sapRecord.Plm_SAP_Description);
                    plmSAPDescAdded = true;
                    break;
                }
                case "Decoupage": {
                    this.updateProperty(property, sapRecord.Decoupage);
                    decoupageAdded = true;
                }
            }
            ++i;
        }
        if (!serviceAdded && sapRecord.Service != null && !sapRecord.Service.isEmpty()) {
            designAttributesObject.appendChild(this.createNewProperty("Service", sapRecord.Service));
        }
        if (!caderAdded && sapRecord.Code_Cader != null && !sapRecord.Code_Cader.isEmpty()) {
            designAttributesObject.appendChild(this.createNewProperty("Cader", sapRecord.Code_Cader));
        }
        if (!plmSAPDescAdded && sapRecord.Plm_SAP_Description != null && !sapRecord.Plm_SAP_Description.isEmpty()) {
            designAttributesObject.appendChild(this.createNewProperty("PLM/SAP Description", sapRecord.Plm_SAP_Description));
        }
        if (!decoupageAdded && sapRecord.Decoupage != null && !sapRecord.Decoupage.isEmpty()) {
            designAttributesObject.appendChild(this.createNewProperty("Decoupage", sapRecord.Decoupage));
        }
    }

    private void updateProperty(Element property, String value) {
        property.setAttribute("val", value);
    }

    private Element createNewProperty(String name, String value) {
        Element newProperty = this.document.createElementNS("http://www.mentor.com/harness/Schema/bridgesdesignattributes", "property");
        newProperty.setAttribute("name", name);
        newProperty.setAttribute("stability", "editable");
        newProperty.setAttribute("type", "String");
        newProperty.setAttribute("val", value);
        return newProperty;
    }

    private boolean isAnyNameFieldEmpty() {
        if (SceGlobals.parentNewName.getText() == null || SceGlobals.parentNewName.getText().isEmpty()) {
            return true;
        }
        JTextField[] jTextFieldArray = SceGlobals.childNewName;
        int n = SceGlobals.childNewName.length;
        int n2 = 0;
        while (n2 < n) {
            JTextField childNewName = jTextFieldArray[n2];
            if (childNewName.getText() == null || childNewName.getText().isEmpty()) {
                return true;
            }
            ++n2;
        }
        return false;
    }

    private boolean isAnyPartNumberFieldEmpty() {
        if (SceGlobals.parentNewPartNumber.getText() == null || SceGlobals.parentNewPartNumber.getText().isEmpty()) {
            return true;
        }
        JTextField[] jTextFieldArray = SceGlobals.childNewPartNumber;
        int n = SceGlobals.childNewPartNumber.length;
        int n2 = 0;
        while (n2 < n) {
            JTextField childNewPartNumber = jTextFieldArray[n2];
            if (childNewPartNumber.getText() == null || childNewPartNumber.getText().isEmpty()) {
                return true;
            }
            ++n2;
        }
        return false;
    }

    private boolean isAnyRevisionFieldEmpty() {
        if (SceGlobals.parentNewRevision == null || SceGlobals.parentNewRevision.getText().isEmpty()) {
            return true;
        }
        JTextField[] jTextFieldArray = SceGlobals.childNewRevision;
        int n = SceGlobals.childNewRevision.length;
        int n2 = 0;
        while (n2 < n) {
            JTextField childNewRevision = jTextFieldArray[n2];
            if (childNewRevision.getText() == null || childNewRevision.getText().isEmpty()) {
                return true;
            }
            ++n2;
        }
        return false;
    }

    private LinkedList<String> anyDuplicateNames() {
        HashSet<String> names = new HashSet<String>();
        LinkedList<String> duplicates = new LinkedList<String>();
        names.add(SceGlobals.parentNewName.getText().toLowerCase());
        JTextField[] jTextFieldArray = SceGlobals.childNewName;
        int n = SceGlobals.childNewName.length;
        int n2 = 0;
        while (n2 < n) {
            JTextField childNewName = jTextFieldArray[n2];
            if (names.contains(childNewName.getText().toLowerCase())) {
                duplicates.addLast(childNewName.getText());
            } else {
                names.add(childNewName.getText().toLowerCase());
            }
            ++n2;
        }
        return duplicates;
    }

    private LinkedList<String> findNamesExistingInProject() {
        LinkedList<String> listOfExistingNames = new LinkedList<String>();
        StringBuilder queryBuilder = new StringBuilder();
        try {
            Connection connection = DatabaseConnector.getChsDBConnection();
            Statement statement = connection.createStatement();
            queryBuilder.append("select name from (select HD.NAME as name from HARNESSDESIGN HD inner join DESIGNMGR DM on dm.id = hd.designmgr_id and dm.project_id = '").append(SceGlobals.projectID).append("' ");
            queryBuilder.append("union ");
            queryBuilder.append("select CTD.NAME as name from CAPITALTOPOLOGYDESIGN CTD inner join DESIGNMGR DM on dm.id = ctd.designmgr_id and dm.project_id = '").append(SceGlobals.projectID).append("' ");
            queryBuilder.append("union ");
            queryBuilder.append("select FD.NAME as name from FUNCTIONDESIGN FD inner join DESIGNMGR DM on dm.id = fd.designmgr_id and dm.project_id = '").append(SceGlobals.projectID).append("' ");
            queryBuilder.append("union ");
            queryBuilder.append("select PTD.NAME as name from PLATFORMTOPOLOGYDESIGN PTD inner join DESIGNMGR DM on dm.id = ptd.designmgr_id and dm.project_id = '").append(SceGlobals.projectID).append("' ");
            queryBuilder.append("union ");
            queryBuilder.append("select PD.NAME as name from PROJECTDESIGN PD inner join DESIGNMGR DM on dm.id = pd.designmgr_id and dm.project_id = '").append(SceGlobals.projectID).append("' ");
            queryBuilder.append("union ");
            queryBuilder.append("select TD.NAME as name from TOPOLOGYDESIGN TD inner join DESIGNMGR DM on dm.id = td.designmgr_id and dm.project_id = '").append(SceGlobals.projectID).append("') where name in (");
            queryBuilder.append("'").append(SceGlobals.parentNewName.getText()).append("', ");
            int i = 0;
            while (i < SceGlobals.childNewName.length) {
                queryBuilder.append("'").append(SceGlobals.childNewName[i].getText());
                if (i < SceGlobals.childNewName.length - 1) {
                    queryBuilder.append("', ");
                } else {
                    queryBuilder.append("'");
                }
                ++i;
            }
            queryBuilder.append(")");
            String query = queryBuilder.toString();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                listOfExistingNames.addLast(resultSet.getString("name"));
            }
            resultSet.close();
            statement.close();
            connection.close();
            return listOfExistingNames;
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", 0);
            return null;
        }
    }
}

