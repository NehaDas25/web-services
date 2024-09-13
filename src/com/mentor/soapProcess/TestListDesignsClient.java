package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 */
import java.net.MalformedURLException;
import java.net.URL;

import com.mentor.soapProcess.AbstractClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TestListDesignsClient
extends AbstractClient {
    String projectName;
    String designName;
    String designRevision;
    String designUid;
    private String cisUrl;
    private String designShortDesc;

    public TestListDesignsClient(String projectName, String designName, String designRevision, String designShortDesc, String cisUrl) throws Exception {
        this.projectName = projectName;
        this.designName = designName;
        this.designRevision = designRevision;
        this.designShortDesc = designShortDesc;
        this.cisUrl = cisUrl;
    }

    public String getDesignUid() {
        return this.designUid;
    }

    @Override
    protected URL getServiceURL() throws MalformedURLException {
        return new URL(String.valueOf(this.cisUrl) + "/" + this.getWebServiceName());
    }

    @Override
    protected String getWebServiceName() {
        return "ListDesigns";
    }

    @Override
    protected String getRequestPayload() {
        return "<project name='" + this.projectName + "'/>";
    }

    @Override
    protected boolean isResponseExcepted() {
        return true;
    }

    @Override
    protected void processResponse(Document responsePayload) throws Exception {
        NodeList nodes = responsePayload.getElementsByTagName("design");
        int nbNodes = nodes.getLength();
        int i = 0;
        while (i < nbNodes) {
            Element design = (Element)nodes.item(i);
            if (design.getAttribute("name").equals(this.designName) && design.getAttribute("revision").equals(this.designRevision) && design.getAttribute("shortdesc").equals(this.designShortDesc)) {
                this.designUid = design.getAttribute("id");
                System.out.println("---------------------------------------------------------");
                System.out.println("> Design name: " + design.getAttribute("name"));
                System.out.println("         short description: " + design.getAttribute("shortdesc"));
                System.out.println("         revision: " + design.getAttribute("revision"));
                System.out.println("         type: " + design.getAttribute("designtype"));
                System.out.println("         id: " + design.getAttribute("id"));
                break;
            }
            ++i;
        }
    }
}

