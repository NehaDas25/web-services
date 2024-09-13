package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.SOAPMessage
 */
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.SOAPMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ListDesignDiagrams {
    private static int CIS_SVG_SERVICE_TIMEOUT = 1800000;

    public static String getFirstDesignDiagramName(String id, String name, String projectid, String projectname, String revision) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document requestPayloadDoc = builder.newDocument();
        factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
        Document requestDesignXMLPayloadDoc = builder.newDocument();
        Element elemWiringDesign = requestPayloadDoc.createElement("wiringdesign");
        elemWiringDesign.setAttribute("id", id);
        elemWiringDesign.setAttribute("name", name);
        elemWiringDesign.setAttribute("projectid", projectid);
        elemWiringDesign.setAttribute("projectname", projectname);
        elemWiringDesign.setAttribute("revision", revision);
        requestPayloadDoc.appendChild(elemWiringDesign);
        SOAPMessage reqMsg = WebServerServlet.SOAP_MESSAGE_FACTORY.createMessage();
        NotifyService.insertSOAPAuthentication(reqMsg);
        NotifyService.insertInputXMLToSOAPBody(requestPayloadDoc, reqMsg.getSOAPPart(), reqMsg);
        String endPoint = String.valueOf(NotifyService.CIS_SVG_SERVICE_URL) + "ListDesignDiagrams";
        SOAPMessage respMsg = NotifyService.sendSOAPRequest(reqMsg, endPoint, CIS_SVG_SERVICE_TIMEOUT);
        if (NotifyService.isAnyFault(respMsg)) {
            String fault = NotifyService.getFaultString(respMsg);
            System.err.println("SOAP Fault = " + fault);
            return "SOAP Fault = " + fault;
        }
        Document responseDOM = SOAPUtils.getDocFromSoapMessage(respMsg);
        Element body = responseDOM.getDocumentElement();
        NodeList elementsByTagNameDiagram = body.getElementsByTagName("diagram");
        String diagramName = "";
        if (elementsByTagNameDiagram.getLength() > 1) {
            System.out.println("More than one diagram defined in this design");
            diagramName = ">1";
        } else if (elementsByTagNameDiagram.getLength() != 0) {
            diagramName = elementsByTagNameDiagram.item(0).getAttributes().getNamedItem("name").getNodeValue();
        }
        return diagramName;
    }
}

