package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.SOAPMessage
 */
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.SOAPMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ExportDevice {
    private static int CIS_SVG_SERVICE_TIMEOUT = 1800000;
    public static ArrayList terminalMaterial = new ArrayList();
    public static boolean ignoreMaterialCheck = false;

    public static void getDevicePart(String deviceName) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document requestPayloadDoc = builder.newDocument();
        terminalMaterial = new ArrayList();
        Element parts = requestPayloadDoc.createElement("Parts");
        parts.setAttribute("includescoping", "false");
        parts.setAttribute("exportrevisions", "false");
        requestPayloadDoc.appendChild(parts);
        Element devicePart = requestPayloadDoc.createElement("Part");
        devicePart.setAttribute("partnumber", deviceName);
        parts.appendChild(devicePart);
        SOAPMessage reqMsg = WebServerServlet.SOAP_MESSAGE_FACTORY.createMessage();
        NotifyService.insertSOAPAuthentication(reqMsg);
        NotifyService.insertInputXMLToSOAPBody(requestPayloadDoc, reqMsg.getSOAPPart(), reqMsg);
        String endPoint = String.valueOf(NotifyService.CIS_SVG_SERVICE_URL) + "ExportLibraryParts";
        SOAPMessage respMsg = NotifyService.sendSOAPRequest(reqMsg, endPoint, CIS_SVG_SERVICE_TIMEOUT);
        Document responseDOM = SOAPUtils.getDocFromSoapMessage(respMsg);
        if (NotifyService.isAnyFault(respMsg)) {
            String fault = NotifyService.getFaultString(respMsg);
            System.err.println("SOAP Fault = " + fault);
            System.out.println("Failed to request ExportLibraryParts service");
            return;
        }
        terminalMaterial = ExportDevice.readDeviceDocument(responseDOM);
    }

    public static ArrayList readDeviceDocument(Document xmlDoc) {
        Element root = xmlDoc.getDocumentElement();
        ArrayList<String> deviceConnectorList = new ArrayList<String>();
        ArrayList<String> harnessConnector = new ArrayList<String>();
        ArrayList<String> terminals = new ArrayList<String>();
        ArrayList<String> housingDef = new ArrayList<String>();
        ArrayList<String> terminalMaterial = new ArrayList<String>();
        NodeList deviceFootPrint = root.getElementsByTagName("librarydevicefootprint");
        int i = 0;
        while (i < deviceFootPrint.getLength()) {
            NodeList libraryfootprintpinmapping = ((Element)deviceFootPrint.item(i)).getElementsByTagName("libraryfootprintpinmapping");
            int j = 0;
            while (j < libraryfootprintpinmapping.getLength()) {
                if (!deviceConnectorList.contains(libraryfootprintpinmapping.item(j).getAttributes().getNamedItem("libraryobject_id").getNodeValue())) {
                    deviceConnectorList.add(libraryfootprintpinmapping.item(j).getAttributes().getNamedItem("libraryobject_id").getNodeValue());
                }
                ++j;
            }
            ++i;
        }
        NodeList mating = root.getElementsByTagName("librarymating");
        int i2 = 0;
        while (i2 < mating.getLength()) {
            if (deviceConnectorList.contains(mating.item(i2).getAttributes().getNamedItem("matedconnector_id").getNodeValue())) {
                harnessConnector.add(mating.item(i2).getAttributes().getNamedItem("libraryobject_id").getNodeValue());
            } else if (deviceConnectorList.contains(mating.item(i2).getAttributes().getNamedItem("libraryobject_id").getNodeValue())) {
                harnessConnector.add(mating.item(i2).getAttributes().getNamedItem("matedconnector_id").getNodeValue());
            }
            ++i2;
        }
        NodeList connectorpart = root.getElementsByTagName("connectorpart");
        int i3 = 0;
        while (i3 < connectorpart.getLength()) {
            if (harnessConnector.contains(connectorpart.item(i3).getAttributes().getNamedItem("libraryobject_id").getNodeValue())) {
                int j = 0;
                while (j < connectorpart.item(i3).getChildNodes().getLength()) {
                    if (connectorpart.item(i3).getChildNodes().item(j).getNodeName().equals("housingdefinition") && !housingDef.contains(connectorpart.item(i3).getChildNodes().item(j).getAttributes().getNamedItem("libraryobject_id").getNodeValue())) {
                        housingDef.add(connectorpart.item(i3).getChildNodes().item(j).getAttributes().getNamedItem("libraryobject_id").getNodeValue());
                    }
                    ++j;
                }
            }
            ++i3;
        }
        boolean isIDCConnetor = false;
        NodeList idcconnectorpart = root.getElementsByTagName("idcconnectorpart");
        int i4 = 0;
        while (i4 < idcconnectorpart.getLength()) {
            if (harnessConnector.contains(idcconnectorpart.item(i4).getAttributes().getNamedItem("libraryobject_id").getNodeValue())) {
                isIDCConnetor = true;
                break;
            }
            ++i4;
        }
        ignoreMaterialCheck = isIDCConnetor & housingDef.size() == 0;
        ArrayList<String> cavitygroupTerminals = new ArrayList<String>();
        NodeList cavitygrouppart = root.getElementsByTagName("cavitygrouppart");
        int i5 = 0;
        while (i5 < cavitygrouppart.getLength()) {
            if (housingDef.contains(cavitygrouppart.item(i5).getAttributes().getNamedItem("libraryobject_id").getNodeValue())) {
                int j = 0;
                while (j < cavitygrouppart.item(i5).getChildNodes().getLength()) {
                    if (cavitygrouppart.item(i5).getChildNodes().item(j).getNodeName().equals("librarycavitygroupdetails") && !cavitygroupTerminals.contains(cavitygrouppart.item(i5).getChildNodes().item(j).getAttributes().getNamedItem("subcomponen_id").getNodeValue())) {
                        cavitygroupTerminals.add(cavitygrouppart.item(i5).getChildNodes().item(j).getAttributes().getNamedItem("subcomponen_id").getNodeValue());
                    }
                    ++j;
                }
            }
            ++i5;
        }
        NodeList terminalpart = root.getElementsByTagName("terminalpart");
        int i6 = 0;
        while (i6 < terminalpart.getLength()) {
            if (housingDef.contains(terminalpart.item(i6).getAttributes().getNamedItem("libraryobject_id").getNodeValue()) && !terminals.contains(terminalpart.item(i6).getAttributes().getNamedItem("librarymaterial_id").getNodeValue())) {
                terminals.add(terminalpart.item(i6).getAttributes().getNamedItem("librarymaterial_id").getNodeValue());
            }
            if (cavitygroupTerminals.contains(terminalpart.item(i6).getAttributes().getNamedItem("libraryobject_id").getNodeValue()) && !terminals.contains(terminalpart.item(i6).getAttributes().getNamedItem("librarymaterial_id").getNodeValue())) {
                terminals.add(terminalpart.item(i6).getAttributes().getNamedItem("librarymaterial_id").getNodeValue());
            }
            ++i6;
        }
        NodeList librarymaterial = root.getElementsByTagName("librarymaterial");
        int i7 = 0;
        while (i7 < librarymaterial.getLength()) {
            if (terminals.contains(librarymaterial.item(i7).getAttributes().getNamedItem("librarymaterial_id").getNodeValue()) && !terminalMaterial.contains(librarymaterial.item(i7).getAttributes().getNamedItem("materialcode").getNodeValue())) {
                terminalMaterial.add(librarymaterial.item(i7).getAttributes().getNamedItem("materialcode").getNodeValue());
            }
            ++i7;
        }
        return terminals;
    }
}

