package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.AttachmentPart
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPMessage
 */
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import com.mentor.soapProcess.AbstractClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ExportPDFCustomTaskClient
extends AbstractClient {
    private static final String SUBMIT_TASK = "SubmitTask";
    private static final String INSTANCE_NAME = "Export PDF";
    private static final String TASK_CLASS_NAME = "com.inetpsa.cpm.capital.plugin.task.pdf.ExportPDFTask";
    private State state = State.SubmittingTask;
    private String taskID;

    public ExportPDFCustomTaskClient(String threadId) {
        this.threadId = threadId;
    }

    @Override
    protected String getWebServiceName() {
        if (State.SubmittingTask == this.state) {
            return SUBMIT_TASK;
        }
        return "DescribeTask";
    }

    @Override
    protected String getRequestPayload() {
        if (State.SubmittingTask == this.state) {
            return "<taskspec cron_expression='' instance_name='Export PDF' name='plugin:Java//com.inetpsa.cpm.capital.plugin.task.pdf.ExportPDFTask' application='CapitalModularXC.exe'/>";
        }
        if (State.WaitingForTaskCompletion == this.state) {
            return "<taskspec attachments='false' executions='false' id='" + this.taskID + "'/>";
        }
        return "<taskspec attachments='true' executions='false' id='" + this.taskID + "'/>";
    }

    @Override
    protected URL getServiceURL() throws MalformedURLException {
        return new URL(String.valueOf(SceGlobals.CIS_URL) + this.getWebServiceName());
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
        WebServiceUtils.createXMLAttachment(messageSOAP, "FEMParams", new FileInputStream(parametersFile));
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

    private void saveAttachments(SOAPMessage soapMessage, String folderPath) throws SOAPException, IOException {
        boolean pdfGenerated = false;
        Iterator iter = soapMessage.getAttachments();
        while (iter.hasNext()) {
            String line;
            AttachmentPart attachment = (AttachmentPart)iter.next();
            String contenID = attachment.getMimeHeader("Content-ID")[0];
            ByteArrayInputStream data = (ByteArrayInputStream)attachment.getContent();
            if (contenID.contains("PDF")) {
                ZipInputStream zipInputStream = new ZipInputStream(data);
                ZipEntry entry = zipInputStream.getNextEntry();
                byte[] buffer = new byte[1024];
                while (entry != null) {
                    int len;
                    FileOutputStream stream = new FileOutputStream(Paths.get(folderPath, entry.getName()).toFile());
                    while ((len = zipInputStream.read(buffer)) > 0) {
                        stream.write(buffer, 0, len);
                    }
                    stream.close();
                    zipInputStream.closeEntry();
                    entry = zipInputStream.getNextEntry();
                    pdfGenerated = true;
                }
                continue;
            }
            if (!contenID.contains("html")) continue;
            GZIPInputStream gzis = new GZIPInputStream(data);
            InputStreamReader streamReader = new InputStreamReader((InputStream)gzis, "UTF8");
            BufferedReader bufferedReader = new BufferedReader(streamReader);
            while ((line = bufferedReader.readLine()) != null) {
                if (!line.contains("<br>")) continue;
                String[] lineArray = line.split("<br>");
                if (lineArray == null) {
                    this.pMsgThread(line);
                    continue;
                }
                int i = 0;
                while (i < lineArray.length) {
                    this.pMsgThread(lineArray[i]);
                    ++i;
                }
            }
            bufferedReader.close();
            streamReader.close();
        }
    }

    static enum State {
        SubmittingTask,
        WaitingForTaskCompletion,
        RetrieveTaskAttachments;

    }
}

