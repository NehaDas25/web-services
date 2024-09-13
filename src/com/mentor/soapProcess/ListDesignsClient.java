package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 */
import com.mentor.soapProcess.AbstractClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ListDesignsClient
extends AbstractClient {
    String projectName;
    String designName;
    String designRevision;
    String designUid;

    public ListDesignsClient(String projectName, String designName, String designRevision) throws Exception {
        this.projectName = projectName;
        this.designName = designName;
        this.designRevision = designRevision;
    }

    public String getDesignUid() {
        return this.designUid;
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
            if (design.getAttribute("name").equals(this.designName) && design.getAttribute("revision").equals(this.designRevision)) {
                this.designUid = design.getAttribute("id");
                break;
            }
            ++i;
        }
    }
}

