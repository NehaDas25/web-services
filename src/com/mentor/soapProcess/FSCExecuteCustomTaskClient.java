package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.AttachmentPart
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPMessage
 */
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import com.mentor.soapProcess.AbstractClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FSCExecuteCustomTaskClient
extends AbstractClient {
    private State state = State.SubmittingTask;
    private String taskID;
    String instanceName;
    String classPackageName;
    private String outPutFileName;
    private Document designReportDocument;

    public FSCExecuteCustomTaskClient(String instanceName, String classPackageName, String outPutFileName, String threadId) throws Exception {
        this.instanceName = instanceName;
        this.classPackageName = classPackageName;
        this.outPutFileName = outPutFileName;
        this.threadId = threadId;
    }

    @Override
    protected URL getServiceURL() throws MalformedURLException {
        return new URL(String.valueOf(SceGlobals.CIS_URL) + this.getWebServiceName());
    }

    @Override
    protected String getWebServiceName() {
        if (State.SubmittingTask == this.state) {
            return "SubmitTask";
        }
        return "DescribeTask";
    }

    @Override
    protected String getRequestPayload() {
        if (State.SubmittingTask == this.state) {
            String application = "CapitalModularXC.exe";
            return "<taskspec cron_expression='' instance_name='" + this.instanceName + "' name='plugin:Java//" + this.classPackageName + "' application='" + application + "' />";
        }
        if (State.WaitingForTaskCompletion == this.state) {
            return "<taskspec attachments='false' executions='false' id='" + this.taskID + "'/>";
        }
        return "<taskspec attachments='true' executions='false' id='" + this.taskID + "'/>";
    }

    @Override
    protected boolean isResponseExcepted() {
        return true;
    }

    @Override
    protected boolean hasResponseAttachments() {
        return State.RetrieveTaskAttachments == this.state;
    }

    @Override
    protected void addRequestSOAPAttachments(SOAPMessage messageSOAP) throws Exception {
        if (this.clientParam == null) {
            throw new RuntimeException("!!Error: this service requires passing the task parameters file path as the last parameter on the command line.");
        }
        File parametersFile = new File(this.clientParam);
        if (!parametersFile.exists()) {
            throw new RuntimeException("!!Error: task paramaters file not found: " + this.clientParam);
        }
        if (State.SubmittingTask == this.state) {
            WebServiceUtils.createXMLAttachment(messageSOAP, "FEMParams", new FileInputStream(parametersFile));
        }
    }

    @Override
    protected void processResponse(Document responsePayload) throws Exception {
        if (State.SubmittingTask == this.state) {
            this.taskID = responsePayload.getElementsByTagName("task").item(0).getAttributes().getNamedItem("id").getNodeValue();
            this.state = State.WaitingForTaskCompletion;
            Thread.sleep(5000L);
            this.invoke();
        } else if (State.WaitingForTaskCompletion == this.state) {
            Element tasks = responsePayload.getDocumentElement();
            String stat = ((Element)tasks.getElementsByTagName("taskspec").item(0)).getAttribute("status");
            if (stat.equalsIgnoreCase("FAILED")) {
                this.pMsgThread("Task Failed");
                this.state = State.RetrieveTaskAttachments;
                this.invoke();
            } else if (stat.equalsIgnoreCase("COMPLETED")) {
                this.state = State.RetrieveTaskAttachments;
                this.invoke();
            } else {
                Thread.sleep(5000L);
                this.invoke();
            }
        }
    }

    @Override
    protected void processResponseAttachments(SOAPMessage messageSOAP) throws Exception {
        if (State.RetrieveTaskAttachments == this.state) {
            File parametersFile = new File(this.clientParam);
            this.saveAttachments(messageSOAP, parametersFile.getParentFile().getAbsolutePath());
        }
    }

    public void saveAttachments(SOAPMessage soapMessage, String folderPath) throws SOAPException, Exception {
        Iterator iter = soapMessage.getAttachments();
        while (iter.hasNext()) {
            byte[] buffer;
            FileOutputStream stream;
            String filepath;
            AttachmentPart attachment = (AttachmentPart)iter.next();
            String contenID = attachment.getMimeHeader("Content-ID")[0];
            ByteArrayInputStream data = (ByteArrayInputStream)attachment.getContent();
            if (contenID.contains("FSCFAI")) {
                filepath = String.valueOf(folderPath) + File.separator + this.outPutFileName + ".fscfai";
                stream = new FileOutputStream(filepath);
                while (((InputStream)data).available() != 0) {
                    buffer = new byte[1024];
                    int readBytesCount = ((InputStream)data).read(buffer, 0, 1024);
                    stream.write(buffer, 0, readBytesCount);
                }
                stream.close();
                this.pMsgThread("FSCFAI attachment successfully written to : " + filepath);
                continue;
            }
            if (contenID.contains("FSCNOM")) {
                filepath = String.valueOf(folderPath) + File.separator + this.outPutFileName + ".fscnom";
                stream = new FileOutputStream(filepath);
                while (((InputStream)data).available() != 0) {
                    buffer = new byte[1024];
                    int readBytesCount = ((InputStream)data).read(buffer, 0, 1024);
                    stream.write(buffer, 0, readBytesCount);
                }
                stream.close();
                this.pMsgThread("FSCNOM attachment successfully written to : " + filepath);
                continue;
            }
            if (contenID.contains("DESIGN")) {
                this.pMsgThread("Extracting DESIGN attachment into document");
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                this.designReportDocument = dBuilder.parse(data);
                continue;
            }
            if (contenID.contains("MODULERELATIONSHIP")) {
                String newFileNameForModuleFile = XCExportDesignXml.getNewFileNameForModuleFile(this.outPutFileName);
                String filepath2 = String.valueOf(folderPath) + File.separator + newFileNameForModuleFile + ".csv";
                FileOutputStream stream2 = new FileOutputStream(filepath2);
                while (((InputStream)data).available() != 0) {
                    byte[] buffer2 = new byte[1024];
                    int readBytesCount = ((InputStream)data).read(buffer2, 0, 1024);
                    stream2.write(buffer2, 0, readBytesCount);
                }
                stream2.close();
                this.pMsgThread("Module relation ship report successfully written to : " + filepath2);
                continue;
            }
            contenID.contains("html");
        }
    }

    public Document getDesignReportDocument() {
        return this.designReportDocument;
    }

    static enum State {
        SubmittingTask,
        WaitingForTaskCompletion,
        RetrieveTaskAttachments;

    }
}

