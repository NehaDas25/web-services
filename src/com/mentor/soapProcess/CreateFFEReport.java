package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.MessageFactory
 *  javax.xml.soap.SOAPMessage
 *  javax.xml.soap.SOAPPart
 */
import java.io.FileOutputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CreateFFEReport {
    public static MessageFactory SOAP_MESSAGE_FACTORY = null;
    public static Transformer XML_TRANSFORMER = null;
    private static String CIS_SVG_SERVICE_URL = "";
    private static int CIS_SVG_SERVICE_TIMEOUT = 1800000;
    private static final String[] CHS_AUTHENTICATION_PARAMS = new String[]{"chsusername", "chspassword"};
    private static String[] CHS_AUTHENTICATION_VALUES = new String[]{"", ""};

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java CreateFFEReport <design name> <revision> <project name>");
            return;
        }
        String svgFilePath = args[0];
        System.out.println("Starting conversion: " + svgFilePath);
        try {
            NotifyService notServ = new NotifyService();
            NotifyService.readChsCustProps();
            SceGlobals sceGlobals = new SceGlobals();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document requestDesignXMLPayloadDoc = builder.newDocument();
            Element desXMLRoot = requestDesignXMLPayloadDoc.createElement("wiringdesign");
            desXMLRoot.setAttribute("name", args[0]);
            desXMLRoot.setAttribute("revision", args[1]);
            desXMLRoot.setAttribute("projectname", args[2]);
            requestDesignXMLPayloadDoc.appendChild(desXMLRoot);
            SOAP_MESSAGE_FACTORY = MessageFactory.newInstance();
            XML_TRANSFORMER = TransformerFactory.newInstance().newTransformer();
            SOAPMessage reqDesignXMLMsg = SOAP_MESSAGE_FACTORY.createMessage();
            NotifyService.insertSOAPAuthentication(reqDesignXMLMsg);
            CreateFFEReport.insertInputXMLToSOAPBody(requestDesignXMLPayloadDoc, reqDesignXMLMsg.getSOAPPart(), reqDesignXMLMsg);
            String endPointDesXML = String.valueOf(CIS_SVG_SERVICE_URL) + "ExportProjectDesign";
            SOAPMessage respDesXMLMsg = NotifyService.sendSOAPRequest(reqDesignXMLMsg, endPointDesXML, CIS_SVG_SERVICE_TIMEOUT);
            if (NotifyService.isAnyFault(respDesXMLMsg)) {
                String fault = NotifyService.getFaultString(respDesXMLMsg);
                System.out.println("SOAP Fault = " + fault);
                System.out.println("Aborting Release Level Change service.");
            } else {
                Document responseDesXMLDOM = SOAPUtils.getDocFromSoapMessage(respDesXMLMsg);
                Element desXmlRoot = responseDesXMLDOM.getDocumentElement();
                factory = DocumentBuilderFactory.newInstance();
                builder = factory.newDocumentBuilder();
                Document requestDesignPdfPayloadDoc = builder.newDocument();
                Node importNode = requestDesignPdfPayloadDoc.importNode(desXmlRoot, true);
                requestDesignPdfPayloadDoc.appendChild(importNode);
                DOMSource source = new DOMSource(requestDesignPdfPayloadDoc);
                FileOutputStream stream = new FileOutputStream(String.valueOf(SceGlobals.NOTIFY_SVG_TIF_PATH) + sceGlobals.getDesName() + ".xml");
                StreamResult result = new StreamResult(stream);
                WebServerServlet.XML_TRANSFORMER.transform(source, result);
                stream.flush();
                stream.close();
                SOAPMessage reqDesignPdfMsg = WebServerServlet.SOAP_MESSAGE_FACTORY.createMessage();
                NotifyService.insertSOAPAuthentication(reqDesignPdfMsg);
                CreateFFEReport.insertInputXMLToSOAPBody(requestDesignPdfPayloadDoc, reqDesignPdfMsg.getSOAPPart(), reqDesignPdfMsg);
                String endPointDesPdf = String.valueOf(SceGlobals.PSA_URL) + "ffePdfReport";
                SOAPMessage respDesPdfMsg = NotifyService.sendSOAPRequest(reqDesignPdfMsg, endPointDesPdf, SceGlobals.PSA_TIMEOUT);
                if (NotifyService.isAnyFault(respDesPdfMsg)) {
                    String fault = NotifyService.getFaultString(respDesXMLMsg);
                    System.out.println("SOAP Fault = " + fault);
                    System.out.println("Aborting Release Level Change service.");
                } else {
                    Object n;
                    Document responseDesPdfDOM = SOAPUtils.getDocFromSoapMessage(respDesPdfMsg);
                    Element el1 = responseDesPdfDOM.getDocumentElement();
                    String reportUrl = "";
                    NodeList domData = el1.getElementsByTagName("reporturl");
                    int i = 0;
                    while (i < domData.getLength()) {
                        n = domData.item(i);
                        if (((Node) n).getNodeName().compareTo("reporturl") == 0) {
                            reportUrl = ((Node) n).getFirstChild().getNodeValue();
                        }
                        ++i;
                    }
                    if (reportUrl.equals("")) {
                        String fault = NotifyService.getFaultString(respDesPdfMsg);
                        System.out.println("SOAP Fault: Could not get report url = " + fault);
                    }
                    System.out.println("FEE report from URL " + reportUrl);
                    String toPath = String.valueOf(SceGlobals.NOTIFY_SVG_TIF_PATH) + sceGlobals.getDesName() + ".pdf";
                    System.out.println("Copy report to      \n" + toPath);
                    NotifyService.copyFile(reportUrl, toPath);
                    if (SceGlobals.debug && !SceGlobals.d_AcrobatPath.equals("")) {
                        try {
                            n = Runtime.getRuntime().exec(String.valueOf(SceGlobals.d_AcrobatPath) + " " + toPath + " &");
                        }
                        catch (IOException e1) {
                            System.err.println(e1);
                            System.exit(1);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertInputXMLToSOAPBody(Document domObject, SOAPPart part, SOAPMessage processedMessage) throws Exception {
        Element docEl;
        NodeList nList;
        Source spSrc = part.getContent();
        DOMResult domRes = new DOMResult();
        XML_TRANSFORMER.transform(spSrc, domRes);
        Node envRoot = domRes.getNode();
        if (envRoot.getNodeType() == 9 && (nList = (docEl = ((Document)envRoot).getDocumentElement()).getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/", "Body")).getLength() > 0) {
            Node bodyNode = nList.item(0);
            Element attRoot = domObject.getDocumentElement();
            Node importNode = ((Document)envRoot).importNode(attRoot, true);
            bodyNode.appendChild(importNode);
            DOMSource domSource = new DOMSource(envRoot);
            part.setContent((Source)domSource);
        }
        processedMessage.saveChanges();
    }
}

