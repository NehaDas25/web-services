package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 */
import java.io.InputStream;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FSCReportTaskParameters
extends FSCTaskNameValueParameters {
    private static final long serialVersionUID = 1L;

    public FSCReportTaskParameters() {
    }

    public FSCReportTaskParameters(InputStream reader) {
        super(reader);
    }

    public String getProjectName() {
        return this.getStringParameter("projectname");
    }

    public void setProjectName(String name) {
        this.setStringParameter("projectname", name);
    }

    public String getDesignUid() {
        return this.getStringParameter("designuid");
    }

    public void setDesignUid(String name) {
        this.setStringParameter("designuid", name);
    }

    public String getFilePathName() {
        return this.getStringParameter("filepathname");
    }

    public void setFilePathName(String name) {
        this.setStringParameter("filepathname", name);
    }

    public String getAuthor() {
        return this.getStringParameter("author");
    }

    public void setAuthor(String name) {
        this.setStringParameter("author", name);
    }

    public void writeTaskXmlFile(String outFileXml) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = builder.newDocument();
        Element element = doc.createElement("CustomTaskParameters");
        doc.appendChild(element);
        Set keySet = this.keySet();
        for (Object paramName : keySet) {
            String paramValue = (String)this.get(paramName);
            Element elementParam = doc.createElement("Param");
            elementParam.setAttribute("name", String.valueOf(paramName));
            elementParam.setAttribute("value", paramValue);
            element.appendChild(elementParam);
        }
        WebServiceUtils.writeDOMDocumentToFile(doc, outFileXml);
    }
}

