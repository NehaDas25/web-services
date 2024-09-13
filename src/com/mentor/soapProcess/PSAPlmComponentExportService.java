package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PSAPlmComponentExportService
extends PSAPlmComponentServiceFactory
implements Runnable {
    private String partNumber;
    private String partStatus;
    private boolean isDevice;
    private String currentState;
    private Document inputDoc;

    public PSAPlmComponentExportService(Document doc) throws PSAPlmChsHcException {
        super("Export-Service");
        this.inputDoc = doc;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void run() {
        try {
            try {
                this.processComponent();
                return;
            }
            catch (PSAPlmChsHcException psaExc) {
                this.launchExe(psaExc.getErrorCode(), "CHS", psaExc.getPartNumber());
                this.exceptionLogger(psaExc.getMessage(), psaExc);
                psaExc.printStackTrace();
                if (this.processLogAppender == null) return;
                this.processLogAppender.close();
                return;
            }
            catch (Exception e) {
                this.launchExe("003", "CHS", this.partNumber);
                PSAPlmChsHcException psaExc = new PSAPlmChsHcException("003", this.partNumber, this.currentState, String.valueOf(PSAPlmComponentExportService.getErrorDetail("003")) + " Unknown error occurred in <Component Export Service> further processing aborted. " + e.getMessage());
                psaExc.setSoapReq(PSAPlmComponentExportService.toStringDoc(this.inputDoc));
                psaExc.setStackTrace(e.getStackTrace());
                this.exceptionLogger(psaExc.getMessage(), psaExc);
                e.printStackTrace();
                if (this.processLogAppender == null) return;
                this.processLogAppender.close();
                return;
            }
        }
        finally {
            if (this.processLogAppender != null) {
                this.processLogAppender.close();
            }
        }
    }

    /*
     * Enabled aggressive block sorting
     */
    private void processComponent() throws PSAPlmChsHcException {
        if (this.inputDoc == null) {
            PSAPlmChsHcException psaExc = new PSAPlmChsHcException("006", this.partNumber, this.currentState, String.valueOf(PSAPlmComponentExportService.getErrorDetail("006")) + " The input XML is not proper.");
            psaExc.setSoapReq(PSAPlmComponentExportService.toStringDoc(this.inputDoc));
            throw psaExc;
        }
        this.readPartDetails(this.inputDoc);
        if (this.partStatus != null && this.partStatus.trim().equalsIgnoreCase("Current")) {
            Document doc = this.exportComponentFromCHS(this.partNumber);
            this.validatePartExistance(doc, this.partNumber);
            this.isDevicePart(doc);
            if (this.isDevice) {
                this.copyComponent(this.exportLocationForDevice, doc);
                return;
            }
            this.copyComponent(this.exportLocationForHarness, doc);
            return;
        }
        PSAPlmChsHcException psaExc = new PSAPlmChsHcException("010", this.partNumber, this.currentState, String.valueOf(PSAPlmComponentExportService.getErrorDetail("010")) + this.getFaultString010(this.partNumber, this.partStatus));
        psaExc.setSoapReq(PSAPlmComponentExportService.toStringDoc(this.inputDoc));
        throw psaExc;
    }

    private void isDevicePart(Document doc) throws PSAPlmChsHcException {
        Element partElement = this.getNodeWithAttribute(doc, "partnumber", this.partNumber);
        if (partElement == null) {
            PSAPlmChsHcException psaExc = new PSAPlmChsHcException("006", this.partNumber, this.currentState, String.valueOf(PSAPlmComponentExportService.getErrorDetail("006")) + " Part not found in response of CHS");
            psaExc.setSoapReq(PSAPlmComponentExportService.toStringDoc(this.inputDoc));
            psaExc.setSoapResp(PSAPlmComponentExportService.toStringDoc(doc));
            throw psaExc;
        }
        String partNodeName = partElement.getNodeName();
        this.isDevice = partNodeName.equalsIgnoreCase("devicepart");
        this.processLog("IS Device : " + this.isDevice);
    }

    /*
     * Enabled aggressive block sorting
     */
    private void readPartDetails(Document xmlDoc) throws PSAPlmChsHcException {
        this.currentState = "Reading Part Details from XML!!!";
        this.processLog(this.currentState);
        xmlDoc.getDocumentElement().normalize();
        NodeList nList = xmlDoc.getElementsByTagName("librarypart");
        int numberOfnodes = nList.getLength();
        if (numberOfnodes <= 0) {
            PSAPlmChsHcException psaExc = new PSAPlmChsHcException("006", this.partNumber, this.currentState, String.valueOf(PSAPlmComponentExportService.getErrorDetail("006")) + " No nodes found for librarypart node!!!");
            psaExc.setSoapReq(PSAPlmComponentExportService.toStringDoc(this.inputDoc));
            throw psaExc;
        }
        Node partNode = nList.item(0);
        if (partNode.getNodeType() == 1 && partNode.hasAttributes()) {
            NamedNodeMap nodeMap = partNode.getAttributes();
            this.partNumber = nodeMap.getNamedItem("partnumber").getNodeValue();
            if (this.partNumber == null) {
                PSAPlmChsHcException psaExc = new PSAPlmChsHcException("006", this.partNumber, this.currentState, String.valueOf(PSAPlmComponentExportService.getErrorDetail("006")) + " The input XML is not proper to during the extraction part number.");
                psaExc.setSoapReq(PSAPlmComponentExportService.toStringDoc(this.inputDoc));
                throw psaExc;
            }
            this.processLog("Part Number : " + this.partNumber);
            this.partStatus = nodeMap.getNamedItem("currentstatus").getNodeValue();
            if (this.partStatus == null) {
                PSAPlmChsHcException psaExc = new PSAPlmChsHcException("006", this.partNumber, this.currentState, String.valueOf(PSAPlmComponentExportService.getErrorDetail("006")) + " The input XML is not proper to during the extraction part status.");
                psaExc.setSoapReq(PSAPlmComponentExportService.toStringDoc(this.inputDoc));
                throw psaExc;
            }
            this.processLog("Part Status : " + this.partStatus);
            this.processLog("Part Details read successfully...");
            return;
        }
        PSAPlmChsHcException psaExc = new PSAPlmChsHcException("006", this.partNumber, this.currentState, String.valueOf(PSAPlmComponentExportService.getErrorDetail("006")) + " Attributes not present for library part node!!!");
        psaExc.setSoapReq(PSAPlmComponentExportService.toStringDoc(this.inputDoc));
        throw psaExc;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void copyComponent(String exportLocation, Document doc) throws PSAPlmChsHcException {
        this.currentState = "Saving component to the specified location..";
        this.processLog(this.currentState);
        FileOutputStream fOut = null;
        File f = new File(String.valueOf(exportLocation) + "/" + this.partNumber + ".xml");
        try {
            try {
                fOut = new FileOutputStream(f);
                TransformerFactory tFactory = TransformerFactory.newInstance();
                Transformer transformer = tFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(fOut);
                transformer.transform(source, result);
                this.processLog("Component Exported Successfully...");
                return;
            }
            catch (FileNotFoundException e) {
                this.copyComponentException(this.inputDoc, doc, e);
                if (fOut == null) return;
                try {
                    fOut.close();
                    return;
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }
                return;
            }
            catch (TransformerException e) {
                this.copyComponentException(this.inputDoc, doc, e);
                if (fOut == null) return;
                try {
                    fOut.close();
                    return;
                }
                catch (IOException e3) {
                    e3.printStackTrace();
                }
                return;
            }
        }
        finally {
            if (fOut != null) {
                try {
                    fOut.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void copyComponentException(Document reqDoc, Document respDoc, Exception e) throws PSAPlmChsHcException {
        PSAPlmChsHcException psaExc = new PSAPlmChsHcException("005", this.partNumber, this.currentState, String.valueOf(PSAPlmComponentExportService.getErrorDetail("005")) + "  Not able to copy the component to the specified location!!! FILE SAVING FAILED : " + e.getMessage());
        psaExc.setSoapReq(PSAPlmComponentExportService.toStringDoc(this.inputDoc));
        psaExc.setSoapResp(PSAPlmComponentExportService.toStringDoc(respDoc));
        psaExc.setStackTrace(e.getStackTrace());
        throw psaExc;
    }
}

