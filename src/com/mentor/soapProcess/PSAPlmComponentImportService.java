package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 */
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PSAPlmComponentImportService
extends PSAPlmComponentServiceFactory {
    private String partNumber;
    private Document inputDoc;
    private String currentState;
    private boolean bIsDevice;

    public PSAPlmComponentImportService() throws PSAPlmChsHcException {
        super("Import-Service");
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public Document processComponentImportService(Document xmlRequest) {
        this.currentState = "Processing component import service...";
        this.processLog(this.currentState);
        this.inputDoc = xmlRequest;
        Document respDoc = null;
        try {
            try {
                this.readPartNumber(xmlRequest);
                this.currentState = "Exporting component from CHS...";
                this.validatePartExistance();
                respDoc = this.importComponent(this.inputDoc, this.partNumber);
                return respDoc;
            }
            catch (PSAPlmChsHcException e) {
                this.launchExe(e.getErrorCode(), "CHS", e.getPartNumber());
                respDoc = PSAPlmComponentImportService.generateFaultBlock("Component Import Service ", "Component importing ", e.getDescription());
                this.exceptionLogger(e.getMessage(), e);
                if (this.processLogAppender == null) return respDoc;
                this.processLogAppender.close();
                return respDoc;
            }
            catch (Exception e) {
                this.launchExe("003", "CHS", this.partNumber);
                if (respDoc == null) {
                    respDoc = PSAPlmComponentImportService.generateFaultBlock("Component Import Service ", "Component importing ", e.getMessage());
                }
                PSAPlmChsHcException psaExc = new PSAPlmChsHcException("003", this.partNumber, this.currentState, String.valueOf(PSAPlmComponentImportService.getErrorDetail("003")) + " Unknown error occurred in <Component Import Service> further processing aborted. " + e.getMessage());
                psaExc.setSoapReq(PSAPlmComponentImportService.toStringDoc(this.inputDoc));
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

    private void validatePartExistance() throws PSAPlmChsHcException {
        Document exportedDoc = this.exportComponentFromCHS(this.partNumber);
        NodeList nList = exportedDoc.getElementsByTagName("chssystem");
        int numberOfnodes = nList.getLength();
        if (numberOfnodes > 0 && nList.item(0).hasChildNodes()) {
            PSAPlmChsHcException psaExc = new PSAPlmChsHcException("007", this.partNumber, this.currentState, String.valueOf(PSAPlmComponentImportService.getErrorDetail("007")) + this.getFaultString007(this.partNumber));
            psaExc.setSoapResp(PSAPlmComponentImportService.toStringDoc(exportedDoc));
            psaExc.setSoapReq(this.currentInputRequest);
            throw psaExc;
        }
    }

    private void readPartNumber(Document xmlRequest) throws PSAPlmChsHcException {
        this.currentState = "Reading part number from XML...";
        this.processLog(this.currentState);
        NodeList deviceAttrib = xmlRequest.getElementsByTagName("devicepart");
        NodeList connectorAttrib = xmlRequest.getElementsByTagName("connectorpart");
        Node component = null;
        if (deviceAttrib.getLength() > 0) {
            component = deviceAttrib.item(0);
            this.bIsDevice = true;
        } else if (connectorAttrib.getLength() > 0) {
            component = connectorAttrib.item(0);
        } else {
            PSAPlmChsHcException psaExc = new PSAPlmChsHcException("006", this.partNumber, this.currentState, String.valueOf(PSAPlmComponentImportService.getErrorDetail("006")) + " Unable to retrieve the part number !!!!");
            psaExc.setSoapReq(PSAPlmComponentImportService.toStringDoc(this.inputDoc));
            throw psaExc;
        }
        NamedNodeMap attribMap = component.getAttributes();
        Node partAttribute = attribMap.getNamedItem("partnumber");
        if (partAttribute != null) {
            this.partNumber = partAttribute.getNodeValue();
        }
        if (this.partNumber == null || this.partNumber.trim().equalsIgnoreCase("")) {
            PSAPlmChsHcException psaExc = new PSAPlmChsHcException("006", this.partNumber, this.currentState, String.valueOf(PSAPlmComponentImportService.getErrorDetail("006")) + " Unable to retrieve the part number !!!!");
            psaExc.setSoapReq(PSAPlmComponentImportService.toStringDoc(this.inputDoc));
            throw psaExc;
        }
        this.processLog("The component number is : " + this.partNumber);
    }

    private Document importComponent(Document inputDoc, String partNumber) throws PSAPlmChsHcException {
        this.currentState = "Importing component in CHS...";
        this.processLog(this.currentState);
        Document responseDOM = null;
        try {
            Element feedbackNode;
            Element serviceNode;
            Document respDoc = null;
            responseDOM = this.importComponentInCHS(inputDoc, partNumber, this.currentState);
            Node node = responseDOM.getElementsByTagName("importfeedback").item(0);
            String status = node.getAttributes().getNamedItem("status").getNodeValue();
            if ("success".equalsIgnoreCase(status)) {
                respDoc = m_docBuilder.newDocument();
                if (this.bIsDevice) {
                    serviceNode = respDoc.createElement("CHSDeviceCreationService");
                    feedbackNode = respDoc.createElement("CHSDeviceCreationfeedback");
                } else {
                    serviceNode = respDoc.createElement("CHSHarnessComponentCreationService");
                    feedbackNode = respDoc.createElement("CHSHarnessComponentfeedback");
                }
            } else {
                String error = node.getAttributes().getNamedItem("error").getNodeValue();
                PSAPlmChsHcException psaExc = new PSAPlmChsHcException("008", partNumber, this.currentState, String.valueOf(PSAPlmComponentImportService.getErrorDetail("008")) + this.getFaultString008(error));
                psaExc.setSoapReq(PSAPlmComponentImportService.toStringDoc(inputDoc));
                psaExc.setSoapResp(PSAPlmComponentImportService.toStringDoc(responseDOM));
                throw psaExc;
            }
            serviceNode.appendChild(feedbackNode);
            feedbackNode.setAttribute("status", "success");
            this.appendChildNodeToDiffXml(serviceNode, respDoc, respDoc);
            return respDoc;
        }
        catch (PSAPlmChsHcException e) {
            throw e;
        }
        catch (Exception e) {
            PSAPlmChsHcException psaExc = new PSAPlmChsHcException("008", partNumber, this.currentState, String.valueOf(PSAPlmComponentImportService.getErrorDetail("008")) + e.getMessage());
            psaExc.setSoapReq(PSAPlmComponentImportService.toStringDoc(inputDoc));
            psaExc.setSoapResp(PSAPlmComponentImportService.toStringDoc(responseDOM));
            psaExc.setStackTrace(e.getStackTrace());
            throw psaExc;
        }
    }
}

