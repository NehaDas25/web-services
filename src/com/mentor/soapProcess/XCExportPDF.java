package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 */
import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XCExportPDF {
    private static final String PARAM = "Param";
    public static final String NAME = "name";
    private static final String VALUE = "value";
    private static final String DESIGN_UID = "designuid";
    private static final String PROJECT_NAME = "projectname";
    private static final String TARGET_DIRECTORY = "targetdirectory";
    private static final String PDF_ORIENTATION = "ORIENTATION";
    private static final String PDF_COLOR = "COLOR";
    private static final String PDF_AREA = "AREA";
    private static final String PDF_SIZE = "SIZE";
    private String projectName;
    private String designId;
    private String outPutFileName;
    private SceGlobals sceGlobalsInstance;
    private String threadId;

    XCExportPDF(String projectName, String designId, String outPutFileName, SceGlobals sceGlobalsInstance, String threadId) {
        this.projectName = projectName;
        this.designId = designId;
        this.outPutFileName = outPutFileName;
        this.sceGlobalsInstance = sceGlobalsInstance;
        this.threadId = threadId;
    }

    public void pMsgThread(String msg) {
        System.out.println(String.valueOf(this.threadId) + ": " + msg);
    }

    public void export() throws Exception {
        String taskFilePath = Paths.get(this.sceGlobalsInstance.getFscvalidOutputFolderPath(), String.valueOf(this.outPutFileName) + "PDFTaskParameters.xml").toString();
        try {
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put(PROJECT_NAME, this.projectName);
            parameters.put(DESIGN_UID, this.designId);
            parameters.put(TARGET_DIRECTORY, this.sceGlobalsInstance.getFscvalidOutputFolderPath());
            parameters.put(PDF_ORIENTATION, SceGlobals.PDF_Orientation);
            parameters.put(PDF_COLOR, SceGlobals.PDF_Color);
            parameters.put(PDF_AREA, SceGlobals.PDF_Area);
            parameters.put(PDF_SIZE, SceGlobals.PDF_Size);
            this.writeTaskXmlFile(parameters, taskFilePath);
            this.pMsgThread("Start custom task for PDF report generation on design " + this.outPutFileName);
            new ExportPDFCustomTaskClient(this.threadId).invoke(false, taskFilePath);
        }
        finally {
            File taskFile = new File(taskFilePath);
            if (taskFile.exists()) {
                taskFile.delete();
            }
        }
    }

    public void writeTaskXmlFile(Map<String, String> parameters, String outFileXml) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = builder.newDocument();
        Element element = doc.createElement("CustomTaskParameters");
        doc.appendChild(element);
        Set<String> keySet = parameters.keySet();
        for (String paramName : keySet) {
            String paramValue = parameters.get(paramName);
            Element elementParam = doc.createElement(PARAM);
            elementParam.setAttribute(NAME, paramName);
            elementParam.setAttribute(VALUE, paramValue);
            element.appendChild(elementParam);
        }
        WebServiceUtils.writeDOMDocumentToFile(doc, outFileXml);
    }
}

