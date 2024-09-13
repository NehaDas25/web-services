package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 */
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PSAPlmComponentVersionService
extends PSAPlmComponentServiceFactory {
    private String partOldNumber;
    private String partNewNumber;
    private String descriptionSAP;
    private String planReferenceSAP;
    private String planRevisionSAP;
    private String partReferenceSAP;
    private String partRevisionSAP;
    private String inputXML;
    private Document exportPartDOM;
    private String currentState;

    public PSAPlmComponentVersionService() throws PSAPlmChsHcException {
        super("Version-Service");
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public Document processComponentVersionService(Document xmlRequest) {
        this.inputXML = PSAPlmComponentVersionService.toStringDoc(xmlRequest);
        this.currentState = "Processing component version service...";
        this.processLog(this.currentState);
        Document respDoc = null;
        try {
            try {
                this.readPartDetails(xmlRequest);
                this.readSAPDetails(xmlRequest);
                this.exportPartDOM = this.exportComponentFromCHS(this.partOldNumber);
                this.validateOldVersionPart();
                this.validateNewPartExistance();
                this.updateVersionDetails();
                respDoc = this.importNewVersionPart();
                return respDoc;
            }
            catch (PSAPlmChsHcException e) {
                this.launchExe(e.getErrorCode(), "CHS", e.getPartNumber());
                respDoc = PSAPlmComponentVersionService.generateFaultBlock("Component revision Service ", "Component revisioning", e.getDescription());
                this.exceptionLogger(e.getMessage(), e);
                if (this.processLogAppender == null) return respDoc;
                this.processLogAppender.close();
                return respDoc;
            }
            catch (Exception e) {
                this.launchExe("003", "CHS", this.partOldNumber);
                respDoc = PSAPlmComponentVersionService.generateFaultBlock("Component revision Service ", "Component revisioning", e.getMessage());
                PSAPlmChsHcException psaExc = new PSAPlmChsHcException("003", this.partOldNumber, this.currentState, String.valueOf(PSAPlmComponentVersionService.getErrorDetail("003")) + " Unknown error occurred in <Component revision Service> further processing aborted. " + e.getMessage());
                psaExc.setSoapReq(this.inputXML);
                psaExc.setStackTrace(e.getStackTrace());
                this.exceptionLogger(psaExc.getMessage(), psaExc);
                if (this.processLogAppender == null) return respDoc;
                this.processLogAppender.close();
                return respDoc;
            }
        }
        finally {
            if (this.processLogAppender != null) {
                this.processLogAppender.close();
            }
        }
    }

    private void validateNewPartExistance() throws PSAPlmChsHcException {
        Document exportedDoc = this.exportComponentFromCHS(this.partNewNumber);
        NodeList nList = exportedDoc.getElementsByTagName("chssystem");
        int numberOfnodes = nList.getLength();
        if (numberOfnodes > 0 && nList.item(0).hasChildNodes()) {
            PSAPlmChsHcException psaExc = new PSAPlmChsHcException("007", this.partNewNumber, this.currentState, String.valueOf(PSAPlmComponentVersionService.getErrorDetail("007")) + this.getFaultString007(this.partNewNumber));
            psaExc.setSoapResp(PSAPlmComponentVersionService.toStringDoc(exportedDoc));
            psaExc.setSoapReq(this.currentInputRequest);
            throw psaExc;
        }
    }

    private void readPartDetails(Document xmlRequest) throws PSAPlmChsHcException {
        PSAPlmChsHcException psaExc;
        Node partNode;
        this.currentState = "Reading part details from XML!!!";
        this.processLog(this.currentState);
        NodeList nList = xmlRequest.getElementsByTagName("revision");
        int numberOfnodes = nList.getLength();
        if (numberOfnodes > 0) {
            partNode = nList.item(0);
            if (partNode.getNodeType() != 1 || !partNode.hasAttributes()) {
                PSAPlmChsHcException psaExc2 = new PSAPlmChsHcException("006", null, this.currentState, String.valueOf(PSAPlmComponentVersionService.getErrorDetail("006")) + "Attributes not fount for import tag in XML.");
                psaExc2.setSoapReq(PSAPlmComponentVersionService.toStringDoc(xmlRequest));
                throw psaExc2;
            }
        } else {
            PSAPlmChsHcException psaExc3 = new PSAPlmChsHcException("006", null, this.currentState, String.valueOf(PSAPlmComponentVersionService.getErrorDetail("006")) + " Revision node not found in XML.");
            psaExc3.setSoapReq(PSAPlmComponentVersionService.toStringDoc(xmlRequest));
            throw psaExc3;
        }
        NamedNodeMap nodeMap = partNode.getAttributes();
        this.partOldNumber = this.getAttributeValue(nodeMap, "OLDCHSPartNumber");
        this.partNewNumber = this.getAttributeValue(nodeMap, "newCHSPartNumber");
        if (this.partOldNumber == null) {
            psaExc = new PSAPlmChsHcException("006", null, this.currentState, String.valueOf(PSAPlmComponentVersionService.getErrorDetail("006")) + " Old Part details missing !!!");
            psaExc.setSoapReq(PSAPlmComponentVersionService.toStringDoc(xmlRequest));
            throw psaExc;
        }
        if (this.partNewNumber == null) {
            psaExc = new PSAPlmChsHcException("006", null, this.currentState, String.valueOf(PSAPlmComponentVersionService.getErrorDetail("006")) + " New Part details missing !!!");
            psaExc.setSoapReq(PSAPlmComponentVersionService.toStringDoc(xmlRequest));
            throw psaExc;
        }
        if (this.partOldNumber.equalsIgnoreCase(this.partNewNumber)) {
            psaExc = new PSAPlmChsHcException("006", null, this.currentState, String.valueOf(PSAPlmComponentVersionService.getErrorDetail("006")) + " Both the part numbers are same!!!");
            psaExc.setSoapReq(PSAPlmComponentVersionService.toStringDoc(xmlRequest));
            throw psaExc;
        }
    }

    private String getAttributeValue(NamedNodeMap nodeMap, String attribute) {
        String attributeValue = null;
        Node node = nodeMap.getNamedItem(attribute);
        if (node != null) {
            attributeValue = node.getNodeValue();
        }
        return attributeValue;
    }

    private void readSAPDetails(Document xmlRequest) throws PSAPlmChsHcException {
        Node partNode;
        this.currentState = "Reading SAP details from XML!!!";
        this.processLog(this.currentState);
        NodeList nList = xmlRequest.getElementsByTagName("revision");
        int numberOfnodes = nList.getLength();
        if (numberOfnodes > 0) {
            partNode = nList.item(0);
            if (partNode.getNodeType() != 1 || !partNode.hasAttributes()) {
                PSAPlmChsHcException psaExc = new PSAPlmChsHcException("006", null, this.currentState, String.valueOf(PSAPlmComponentVersionService.getErrorDetail("006")) + " Attributes not found for import tag in XML.");
                psaExc.setSoapReq(PSAPlmComponentVersionService.toStringDoc(xmlRequest));
                throw psaExc;
            }
        } else {
            PSAPlmChsHcException psaExc = new PSAPlmChsHcException("006", null, this.currentState, String.valueOf(PSAPlmComponentVersionService.getErrorDetail("006")) + " Import node not found in XML.");
            psaExc.setSoapReq(PSAPlmComponentVersionService.toStringDoc(xmlRequest));
            throw psaExc;
        }
        NamedNodeMap nodeMap = partNode.getAttributes();
        this.descriptionSAP = this.getAttributeValue(nodeMap, "SAP_description");
        this.planReferenceSAP = this.getAttributeValue(nodeMap, "SAP_plane_reference");
        this.planRevisionSAP = this.getAttributeValue(nodeMap, "SAP_plane_revision");
        this.partReferenceSAP = this.getAttributeValue(nodeMap, "SAP_part_reference");
        this.partRevisionSAP = this.getAttributeValue(nodeMap, "SAP_part_revision");
    }

    private Document importNewVersionPart() throws PSAPlmChsHcException {
        this.currentState = "Importing new version part...";
        Document respDoc = null;
        Document responseDOM = null;
        try {
            responseDOM = this.importComponentInCHS(this.exportPartDOM, this.partNewNumber, this.currentState);
            Node node = responseDOM.getElementsByTagName("importfeedback").item(0);
            String status = node.getAttributes().getNamedItem("status").getNodeValue();
            if (!"success".equalsIgnoreCase(status)) {
                String error = node.getAttributes().getNamedItem("error").getNodeValue();
                PSAPlmChsHcException psaExc = new PSAPlmChsHcException("008", this.partNewNumber, this.currentState, String.valueOf(PSAPlmComponentVersionService.getErrorDetail("008")) + this.getFaultString008(error));
                psaExc.setSoapReq(PSAPlmComponentVersionService.toStringDoc(this.exportPartDOM));
                psaExc.setSoapResp(PSAPlmComponentVersionService.toStringDoc(responseDOM));
                throw psaExc;
            }
            respDoc = m_docBuilder.newDocument();
            Element serviceNode = respDoc.createElement("CHSRevisionService");
            Element feedbackNode = respDoc.createElement("CHSRevisionfeedback");
            serviceNode.appendChild(feedbackNode);
            feedbackNode.setAttribute("status", "success");
            respDoc.appendChild(serviceNode);
        }
        catch (PSAPlmChsHcException e) {
            throw e;
        }
        catch (Exception e) {
            PSAPlmChsHcException psaExc = new PSAPlmChsHcException("008", this.partNewNumber, this.currentState, String.valueOf(PSAPlmComponentVersionService.getErrorDetail("008")) + e.getMessage());
            psaExc.setSoapResp(PSAPlmComponentVersionService.toStringDoc(responseDOM));
            psaExc.setSoapReq(PSAPlmComponentVersionService.toStringDoc(this.exportPartDOM));
            psaExc.setStackTrace(e.getStackTrace());
            throw psaExc;
        }
        return respDoc;
    }

    private void validateOldVersionPart() throws PSAPlmChsHcException {
        this.currentState = "Validating Old version part...";
        this.processLog(this.currentState);
        NodeList nodes = this.exportPartDOM.getElementsByTagName("chssystem");
        if (nodes.getLength() < 1) {
            PSAPlmChsHcException psaExc = new PSAPlmChsHcException("009", this.partOldNumber, this.currentState, String.valueOf(PSAPlmComponentVersionService.getErrorDetail("009")) + this.getFaultString009(this.partOldNumber));
            psaExc.setSoapReq(PSAPlmComponentVersionService.toStringDoc(this.exportPartDOM));
            throw psaExc;
        }
        if (!nodes.item(0).hasChildNodes()) {
            PSAPlmChsHcException psaExc = new PSAPlmChsHcException("009", this.partOldNumber, this.currentState, String.valueOf(PSAPlmComponentVersionService.getErrorDetail("009")) + this.getFaultString009(this.partOldNumber));
            psaExc.setSoapReq(PSAPlmComponentVersionService.toStringDoc(this.exportPartDOM));
            throw psaExc;
        }
        Element partElement = this.getNodeWithAttribute(this.exportPartDOM, "partnumber", this.partOldNumber);
        if (partElement == null) {
            PSAPlmChsHcException psaExc = new PSAPlmChsHcException("009", this.partOldNumber, this.currentState, String.valueOf(PSAPlmComponentVersionService.getErrorDetail("009")) + this.getFaultString009(this.partOldNumber));
            psaExc.setSoapReq(PSAPlmComponentVersionService.toStringDoc(this.exportPartDOM));
            throw psaExc;
        }
        String status = partElement.getAttribute("partstatus");
        if (status == null || !status.equalsIgnoreCase("Current")) {
            PSAPlmChsHcException psaExc = new PSAPlmChsHcException("010", this.partOldNumber, this.currentState, String.valueOf(PSAPlmComponentVersionService.getErrorDetail("010")) + this.getFaultString010(this.partOldNumber, status));
            psaExc.setSoapReq(PSAPlmComponentVersionService.toStringDoc(this.exportPartDOM));
            throw psaExc;
        }
        if (status.equalsIgnoreCase("New")) {
            PSAPlmChsHcException psaExc = new PSAPlmChsHcException("011", this.partOldNumber, this.currentState, String.valueOf(PSAPlmComponentVersionService.getErrorDetail("011")) + this.getFaultString011(this.partOldNumber));
            psaExc.setSoapReq(PSAPlmComponentVersionService.toStringDoc(this.exportPartDOM));
            throw psaExc;
        }
    }

    private void updateVersionDetails() throws PSAPlmChsHcException {
        this.currentState = "Updating version details...";
        this.processLog(this.currentState);
        Element partNode = this.getNodeWithAttribute(this.exportPartDOM, "partnumber", this.partOldNumber);
        if (partNode == null) {
            PSAPlmChsHcException psaExc = new PSAPlmChsHcException("006", this.partOldNumber, this.currentState, String.valueOf(PSAPlmComponentVersionService.getErrorDetail("006")) + " Part number not found in the response!!!");
            psaExc.setSoapReq(PSAPlmComponentVersionService.toStringDoc(this.exportPartDOM));
            throw psaExc;
        }
        if (partNode.getNodeType() == 1 && partNode.hasAttributes()) {
            NamedNodeMap nodeMap = partNode.getAttributes();
            nodeMap.getNamedItem("partnumber").setNodeValue(this.partNewNumber);
            nodeMap.getNamedItem("partstatus").setNodeValue("New");
            if (this.descriptionSAP != null) {
                nodeMap.getNamedItem("description").setNodeValue(this.descriptionSAP);
            }
            if (this.planReferenceSAP != null && this.planReferenceSAP.trim().length() > 0 && this.planRevisionSAP != null && this.planRevisionSAP.trim().length() > 0) {
                nodeMap.getNamedItem("userf1").setNodeValue(String.valueOf(this.planReferenceSAP) + "." + this.planRevisionSAP);
            }
            if (this.partReferenceSAP != null && this.partReferenceSAP.trim().length() > 0 && this.partRevisionSAP != null && this.partRevisionSAP.trim().length() > 0) {
                nodeMap.getNamedItem("userf2").setNodeValue(String.valueOf(this.partReferenceSAP) + "." + this.partRevisionSAP);
            }
        } else {
            PSAPlmChsHcException psaExc = new PSAPlmChsHcException("006", this.partNewNumber, this.currentState, String.valueOf(PSAPlmComponentVersionService.getErrorDetail("006")) + " Attributes not found.");
            psaExc.setSoapReq(PSAPlmComponentVersionService.toStringDoc(this.exportPartDOM));
            throw psaExc;
        }
    }
}

