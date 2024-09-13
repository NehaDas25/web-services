package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.MessageFactory
 *  javax.xml.soap.SOAPMessage
 */
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XCExportDesignXml
extends NotifyService {
    public static String standAloneTestXml;
    public static String MAPSEPARATOR;
    String endPointDesXML = String.valueOf(CIS_SVG_SERVICE_URL) + "ExportProjectDesign";
    private Document designXmlDoc;
    private String projectId;
    private String designName;
    private String designRevision;
    private String designDesc;
    private String harnessDesignPartNumber;
    private Element elementHarnessDesign;
    private boolean isModuleDesign = false;
    private ArrayList<Element> listChildHarnessDesigns;
    private HashMap<String, String> projectFunctionalModuleCodesMgrMap;
    private HashMap<String, String> designFunctionalModuleCodesMgrMap;
    private Map<String, String> designFunctionalModuleCodesList = new HashMap<String, String>();
    private Map<String, String> projectFunctionalModuleCodesList = new HashMap<String, String>();
    private String designId;
    private InclusiveFMCodeFilter inclusiveFMCodeFilter;
    private String threadId;
    HashMap<String, Element> refedtag_harnessDesignElement;

    static {
        MAPSEPARATOR = "####";
    }

    public XCExportDesignXml(String projectId, String designName, String designRevision, String designDesc, String designId, boolean isStandaloneTest, SceGlobals sceGlobalsInstance, String threadId) throws Exception {
        boolean res;
        this.projectId = projectId;
        this.designName = designName;
        this.designRevision = designRevision;
        this.designDesc = designDesc;
        this.designId = designId;
        this.threadId = threadId;
        this.listChildHarnessDesigns = new ArrayList();
        this.refedtag_harnessDesignElement = new HashMap();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document requestDesignXMLPayloadDoc = builder.newDocument();
        Element desXMLRoot = requestDesignXMLPayloadDoc.createElement("wiringdesign");
        desXMLRoot.setAttribute("id", designId);
        desXMLRoot.setAttribute("projectid", projectId);
        desXMLRoot.setAttribute("asattachment", "true");
        desXMLRoot.setAttribute("includeassociateddesign", "false");
        requestDesignXMLPayloadDoc.appendChild(desXMLRoot);
        SOAPMessage reqDesignXMLMsg = WebServerServlet.SOAP_MESSAGE_FACTORY.createMessage();
        SOAPMessage respDesXMLMsg = null;
        if (!isStandaloneTest) {
            XCExportDesignXml.insertSOAPAuthentication(reqDesignXMLMsg);
            XCExportDesignXml.insertInputXMLToSOAPBody(requestDesignXMLPayloadDoc, reqDesignXMLMsg.getSOAPPart(), reqDesignXMLMsg);
            this.pMsgThread("Starting design export web service");
            respDesXMLMsg = XCExportDesignXml.sendSOAPRequest(reqDesignXMLMsg, this.endPointDesXML, CIS_SVG_SERVICE_TIMEOUT);
            this.pMsgThread("Completed design export web service");
        } else {
            respDesXMLMsg = WebServerServlet.SOAP_MESSAGE_FACTORY.createMessage();
        }
        sceGlobalsInstance.setDesName("");
        if (XCExportDesignXml.isAnyFault(respDesXMLMsg)) {
            String fault = XCExportDesignXml.getFaultString(respDesXMLMsg);
            this.pMsgThread("SOAP Fault = " + fault);
            this.pMsgThread("Aborting Release Level Change service after requesting XML data.");
            throw new Exception("SOAP Fault = " + fault);
        }
        if (!isStandaloneTest) {
            this.designXmlDoc = WebServiceUtils.processResponseAttachments(respDesXMLMsg, threadId);
        } else {
            this.pMsgThread("Loading stand alone design xml");
            this.designXmlDoc = builder.parse(standAloneTestXml);
        }
        this.elementHarnessDesign = this.getElementHarnessDesign();
        this.harnessDesignPartNumber = this.getInternalHarnessNo();
        this.projectFunctionalModuleCodesMgrMap = this.getProjectFunctionalModuleCodesMgrMap();
        this.designFunctionalModuleCodesMgrMap = this.getDesignFunctionalModuleCodesMgrMap();
        this.getDesignFunctionalModuleCodesListRefIds();
        this.inclusiveFMCodeFilter = new InclusiveFMCodeFilter(this.elementHarnessDesign, this.designFunctionalModuleCodesList, this.projectFunctionalModuleCodesList, this.designXmlDoc);
        Node nodeHarnessRegister = this.elementHarnessDesign.getElementsByTagName("harnessregister").item(0);
        if (1 == nodeHarnessRegister.getNodeType()) {
            Element elemHarnessRegister = (Element)nodeHarnessRegister;
            String moduleTypeValue = elemHarnessRegister.getAttribute("module_type");
            this.isModuleDesign = !"Harness".equals(moduleTypeValue);
        }
        if (!(res = XCExportDesignXml.processDesXML(this.designXmlDoc, this.harnessDesignPartNumber, designRevision, designDesc, "-----------", false, this.elementHarnessDesign, sceGlobalsInstance, threadId))) {
            throw new Exception("The process to build the file name failed!");
        }
    }

    public void pMsgThread(String msg) {
        System.out.println(String.valueOf(this.threadId) + ": " + msg);
    }

    public void dMsg(String msg) {
        System.out.println(msg);
    }

    public static void main(String[] args) {
        try {
            SceGlobals sceGlobals = new SceGlobals();
            WebServerServlet.SOAP_MESSAGE_FACTORY = MessageFactory.newInstance();
            standAloneTestXml = "D:\\PluginDocuments\\forArun\\Testing\\sv.cd----.dc-.FAIS-PLA-B.8A.--------------------.-----------.xml";
            XCExportDesignXml designXml = new XCExportDesignXml("_1", "9822995580_OR-ECR-VRS1-CP", "8A.K0Q18-.Z9N-CP", "", "_254", true, sceGlobals, "dummyThreadId1");
            Element fmHarnessDesignElement = designXml.getFunctionalHarnessDesignElementByFMCode("M101");
            System.out.println(designXml.getInternalHarnessNo(fmHarnessDesignElement));
            System.out.println(designXml.getInternalIssue(fmHarnessDesignElement));
            System.out.println(designXml.getInternalManufacturingSite(fmHarnessDesignElement));
            System.out.println(designXml.getUserField1(fmHarnessDesignElement));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    Document getDesignXmlDocument() {
        return this.designXmlDoc;
    }

    private Optional<Element> getProjectFunctionalModuleCodesMgrElement() {
        Node item;
        HashMap tagMap = new HashMap();
        NodeList nodeListPRFMC = this.designXmlDoc.getElementsByTagName("projectfunctionalmodulecodemgr");
        if (nodeListPRFMC.getLength() == 1 && (item = nodeListPRFMC.item(0)).getNodeType() == 1) {
            Element prfmcodmgr = (Element)item;
            return Optional.of(prfmcodmgr);
        }
        return Optional.empty();
    }

    private Optional<Element> getDesignFunctionalModuleCodesMgrElement() {
        Node itemDfmcm;
        NodeList nodeListDSGFMC;
        if (this.isCompositeDesign(this.elementHarnessDesign) && (nodeListDSGFMC = this.elementHarnessDesign.getElementsByTagName("designfunctionalmodulecodemgr")).getLength() == 1 && (itemDfmcm = nodeListDSGFMC.item(0)).getNodeType() == 1) {
            Element desmcodmgr = (Element)itemDfmcm;
            return Optional.of(desmcodmgr);
        }
        return Optional.empty();
    }

    private HashMap<String, String> getProjectFunctionalModuleCodesMgrMap() {
        HashMap<String, String> tagMap = new HashMap<String, String>();
        NodeList nodeListPRFMC = this.designXmlDoc.getElementsByTagName("projectfunctionalmodulecodemgr");
        int i = 0;
        while (i < nodeListPRFMC.getLength()) {
            Node node = nodeListPRFMC.item(i);
            if (node.getNodeType() == 1) {
                Element prfmcodmgr = (Element)node;
                NodeList nodeListTag = prfmcodmgr.getElementsByTagName("tag");
                int j = 0;
                while (j < nodeListTag.getLength()) {
                    Node nodeTag = nodeListTag.item(j);
                    if (nodeTag.getNodeType() == 1) {
                        Element elemTag = (Element)nodeTag;
                        String id = elemTag.getAttribute("id");
                        String name = elemTag.getAttribute("name");
                        tagMap.put(id, name);
                        this.projectFunctionalModuleCodesList.put(name, id);
                    }
                    ++j;
                }
            }
            ++i;
        }
        return tagMap;
    }

    private HashMap<String, String> getDesignFunctionalModuleCodesMgrMap() {
        HashMap<String, String> tagMap = new HashMap<String, String>();
        for (Element harnessDesignElement : this.listChildHarnessDesigns) {
            if (this.isCompositeDesign(harnessDesignElement)) continue;
            NodeList nodeListDSGFMC = harnessDesignElement.getElementsByTagName("designfunctionalmodulecodemgr");
            int i = 0;
            while (i < nodeListDSGFMC.getLength()) {
                Node node = nodeListDSGFMC.item(i);
                if (node.getNodeType() == 1) {
                    Element prfmcodmgr = (Element)node;
                    NodeList nodeListTag = prfmcodmgr.getElementsByTagName("tag");
                    int j = 0;
                    while (j < nodeListTag.getLength()) {
                        Node nodeTag = nodeListTag.item(j);
                        if (nodeTag.getNodeType() == 1) {
                            Element elemTag = (Element)nodeTag;
                            String id = elemTag.getAttribute("id");
                            String name = elemTag.getAttribute("name");
                            tagMap.put(id, name);
                        }
                        ++j;
                    }
                }
                ++i;
            }
        }
        return tagMap;
    }

    private void getDesignFunctionalModuleCodesListRefIds() {
        NodeList nodeListDesignTags = this.elementHarnessDesign.getElementsByTagName("designfunctionalmodulecodemgr");
        int i = 0;
        while (i < nodeListDesignTags.getLength()) {
            Node node = nodeListDesignTags.item(i);
            if (node.getNodeType() == 1) {
                Element designTags = (Element)node;
                NodeList nodeListTag = designTags.getElementsByTagName("tag");
                int j = 0;
                while (j < nodeListTag.getLength()) {
                    Node nodeTag = nodeListTag.item(j);
                    if (nodeTag.getNodeType() == 1) {
                        Element elemTag = (Element)nodeTag;
                        String name = elemTag.getAttribute("name");
                        String id = elemTag.getAttribute("id");
                        this.designFunctionalModuleCodesList.put(name, id);
                    }
                    ++j;
                }
            }
            ++i;
        }
    }

    private Element getFmHarnessDesignElementByReferencedFmCode(String fmCodeTagRef) {
        Element theMatchingElement = null;
        for (Element harnessDesign : this.listChildHarnessDesigns) {
            if (this.isCompositeDesign(harnessDesign)) continue;
            NodeList nodeListDesignTags = harnessDesign.getElementsByTagName("designtags");
            int i = 0;
            while (i < nodeListDesignTags.getLength()) {
                Node node = nodeListDesignTags.item(i);
                if (node.getNodeType() == 1) {
                    Element designTags = (Element)node;
                    NodeList nodeListTag = designTags.getElementsByTagName("refedtag");
                    int j = 0;
                    while (j < nodeListTag.getLength()) {
                        Node nodeTag = nodeListTag.item(j);
                        if (nodeTag.getNodeType() == 1) {
                            Element elemTag = (Element)nodeTag;
                            String tagref = elemTag.getAttribute("tagref");
                            Element previousExistingKey = this.refedtag_harnessDesignElement.put(tagref, harnessDesign);
                            if (fmCodeTagRef.equals(tagref)) {
                                theMatchingElement = harnessDesign;
                            }
                        }
                        ++j;
                    }
                }
                ++i;
            }
        }
        return theMatchingElement;
    }

    public Element getFunctionalHarnessDesignElementByFMCode(String fmCode) {
        String fmCodeMgr;
        this.initializeRefedTagHarnessDesignMap();
        if (this.projectFunctionalModuleCodesMgrMap.values().contains(fmCode)) {
            for (String fmCodeId : this.projectFunctionalModuleCodesMgrMap.keySet()) {
                fmCodeMgr = this.projectFunctionalModuleCodesMgrMap.get(fmCodeId);
                if (!fmCodeMgr.equals(fmCode) || !this.refedtag_harnessDesignElement.containsKey(fmCodeId)) continue;
                return this.refedtag_harnessDesignElement.get(fmCodeId);
            }
        }
        if (this.designFunctionalModuleCodesMgrMap.values().contains(fmCode)) {
            for (String fmCodeId : this.designFunctionalModuleCodesMgrMap.keySet()) {
                fmCodeMgr = this.designFunctionalModuleCodesMgrMap.get(fmCodeId);
                if (!fmCodeMgr.equals(fmCode) || !this.refedtag_harnessDesignElement.containsKey(fmCodeId)) continue;
                return this.refedtag_harnessDesignElement.get(fmCodeId);
            }
        }
        return null;
    }

    private void initializeRefedTagHarnessDesignMap() {
        if (this.refedtag_harnessDesignElement.size() == 0) {
            for (Element harnessDesign : this.listChildHarnessDesigns) {
                if (this.isCompositeDesign(harnessDesign)) continue;
                NodeList nodeListDesignTags = harnessDesign.getElementsByTagName("designtags");
                int i = 0;
                while (i < nodeListDesignTags.getLength()) {
                    Node node = nodeListDesignTags.item(i);
                    if (node.getNodeType() == 1) {
                        Element designTags = (Element)node;
                        NodeList nodeListTag = designTags.getElementsByTagName("refedtag");
                        int j = 0;
                        while (j < nodeListTag.getLength()) {
                            Element elemTag;
                            String tagref;
                            Element previousExistingKey;
                            Node nodeTag = nodeListTag.item(j);
                            if (nodeTag.getNodeType() == 1 && (previousExistingKey = this.refedtag_harnessDesignElement.put(tagref = (elemTag = (Element)nodeTag).getAttribute("tagref"), harnessDesign)) != null) {
                                this.dMsg("this refedtag did exist in the map: " + tagref + " harnessdesign id: " + harnessDesign.getAttribute("id"));
                            }
                            ++j;
                        }
                    }
                    ++i;
                }
            }
        }
    }

    public void writeDesignXml(SceGlobals sceGlobalsInstance) throws Exception {
        Element desXmlRoot = this.designXmlDoc.getDocumentElement();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document emptyDoc = builder.newDocument();
        Node importNode = emptyDoc.importNode(desXmlRoot, true);
        emptyDoc.appendChild(importNode);
        DOMSource source = new DOMSource(emptyDoc);
        String xmlFilePath = String.valueOf(sceGlobalsInstance.getFscvalidOutputFolderPath()) + sceGlobalsInstance.getDesName() + ".xml";
        FileOutputStream stream = new FileOutputStream(xmlFilePath);
        StreamResult result = new StreamResult(stream);
        DocumentType doctype = this.designXmlDoc.getDoctype();
        if (doctype != null) {
            WebServerServlet.XML_TRANSFORMER.setOutputProperty("doctype-public", doctype.getPublicId());
            WebServerServlet.XML_TRANSFORMER.setOutputProperty("doctype-system", doctype.getSystemId());
        }
        WebServerServlet.XML_TRANSFORMER.transform(source, result);
        stream.flush();
        stream.close();
        this.pMsgThread("Wrote file: " + xmlFilePath);
    }

    public Element renameElement(Element element, String newName) {
        Document document = element.getOwnerDocument();
        Element newElement = this.createElement(document, newName);
        NamedNodeMap attributes = element.getAttributes();
        int i = 0;
        while (i < attributes.getLength()) {
            Node itemAttribute = attributes.item(i);
            String attrName = itemAttribute.getNodeName();
            String attrValue = itemAttribute.getNodeValue();
            newElement.setAttribute(attrName, attrValue);
            ++i;
        }
        NodeList nodeList = element.getChildNodes();
        LinkedList<Node> toBeMoved = new LinkedList<Node>();
        int i2 = 0;
        while (i2 < nodeList.getLength()) {
            toBeMoved.add(nodeList.item(i2));
            ++i2;
        }
        for (Node e : toBeMoved) {
            element.removeChild(e);
            newElement.appendChild(e);
        }
        return newElement;
    }

    private Element createElement(Document document, String elementName) {
        String prefix = this.getPrefixOfQName(elementName);
        String namespaceURI = prefix.isEmpty() ? null : document.lookupNamespaceURI(prefix);
        Element element = namespaceURI == null ? document.createElement(elementName) : document.createElementNS(namespaceURI, elementName);
        return element;
    }

    private String getPrefixOfQName(String elementName) {
        if (elementName.contains(":")) {
            return elementName.replaceAll(":.*", "");
        }
        return "";
    }

    public void writeDesignOrProjectFmCodesXmlFile(SceGlobals sceGlobalsInstance) throws Exception {
        if (!this.hasApplicableFmCodesAssigned()) {
            this.pMsgThread("Design has no functional module codes assigned, so skip it and do not write a fm code list xml file");
            return;
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document emptyDoc = builder.newDocument();
        Optional<Element> oDesignFmCodeElement = this.getDesignFunctionalModuleCodesMgrElement();
        Optional<Element> oProjectFmCodeElement = this.getProjectFunctionalModuleCodesMgrElement();
        Element fmCodeElement = null;
        if (oDesignFmCodeElement.isPresent()) {
            this.pMsgThread("Exporting design functional module codes to xml file");
            Element elementDesignFmCodes = oDesignFmCodeElement.get();
            fmCodeElement = this.renameElement(elementDesignFmCodes, "projectfunctionalmodulecodemgr");
        } else if (oProjectFmCodeElement.isPresent()) {
            this.pMsgThread("Exporting project functional module codes to xml file since no design specific functional module codes are defined");
            fmCodeElement = oProjectFmCodeElement.get();
        } else {
            this.pMsgThread("No project functional module codes found exists, so skip it and do not write a fm code list xml file");
            return;
        }
        fmCodeElement.setAttribute("baseid", "DUMMYID");
        fmCodeElement.setAttribute("id", "DUMMYID");
        Node importNode = emptyDoc.importNode(fmCodeElement, true);
        emptyDoc.appendChild(importNode);
        DOMSource source = new DOMSource(emptyDoc);
        String fileName = sceGlobalsInstance.getDesName();
        String newFileName = XCExportDesignXml.getNewFileNameForModuleFile(fileName);
        String xmlFilePath = String.valueOf(sceGlobalsInstance.getFscvalidOutputFolderPath()) + newFileName + ".xml";
        FileOutputStream stream = new FileOutputStream(xmlFilePath);
        StreamResult result = new StreamResult(stream);
        DocumentType doctype = this.designXmlDoc.getDoctype();
        if (doctype == null) {
            throw new Exception("Could not extact doctype from the exported design xml");
        }
        String publicId = doctype.getPublicId();
        publicId = publicId.replaceFirst("//Project .*//", "//FunctionalModuleCodeMgr 1.1//");
        String systemId = doctype.getSystemId();
        systemId = systemId.replaceFirst("project.dtd", "functionalmodulecodes.dtd");
        Transformer XML_TRANSFORMER = TransformerFactory.newInstance().newTransformer();
        XML_TRANSFORMER.setOutputProperty("indent", "yes");
        XML_TRANSFORMER.setOutputProperty("doctype-public", publicId);
        XML_TRANSFORMER.setOutputProperty("doctype-system", systemId);
        XML_TRANSFORMER.setOutputProperty("encoding", "UTF-8");
        XML_TRANSFORMER.transform(source, result);
        stream.flush();
        stream.close();
        this.pMsgThread("Wrote file: " + xmlFilePath);
    }

    public static String getNewFileNameForModuleFile(String mainFileName_NoXmlExtension) {
        int fileNameLength = mainFileName_NoXmlExtension.length();
        String firstPartOfTheFileNameMissingTheLastTwoCharacters = mainFileName_NoXmlExtension.substring(0, fileNameLength - 2);
        String newFileName = String.valueOf(firstPartOfTheFileNameMissingTheLastTwoCharacters) + "IC";
        return newFileName;
    }

    private boolean hasApplicableFmCodesAssigned() {
        NodeList listDesignTags = this.elementHarnessDesign.getElementsByTagName("designtags");
        if (listDesignTags.getLength() > 0) {
            int i = 0;
            while (i < listDesignTags.getLength()) {
                Node item = listDesignTags.item(i);
                if (item.getNodeType() == 1) {
                    Element eDesigntags = (Element)item;
                    NodeList listRefedtag = eDesigntags.getElementsByTagName("refedtag");
                    int j = 0;
                    while (j < listRefedtag.getLength()) {
                        Node itemRefedtag = listRefedtag.item(j);
                        if (itemRefedtag.getNodeType() == 1) {
                            Element eRefedtag = (Element)itemRefedtag;
                            String tagrefValue = eRefedtag.getAttribute("tagref");
                            for (String fmCodeId : this.projectFunctionalModuleCodesList.values()) {
                                if (!fmCodeId.equals(tagrefValue)) continue;
                                return true;
                            }
                            for (String fmCodeId : this.designFunctionalModuleCodesList.values()) {
                                if (!fmCodeId.equals(tagrefValue)) continue;
                                return true;
                            }
                        }
                        ++j;
                    }
                }
                ++i;
            }
        }
        return false;
    }

    public boolean isCompositeDesign() {
        String dTypeValue = this.elementHarnessDesign.getAttribute("dtype");
        return "Harness".equals(dTypeValue);
    }

    public boolean isCompositeDesign(Element hrDesign) {
        String dTypeValue = hrDesign.getAttribute("dtype");
        return "Harness".equals(dTypeValue);
    }

    private String getInternalHarnessNo() throws Exception {
        return this.getAttributeValueFromHarnessRegister("inteharn", this.elementHarnessDesign);
    }

    public String getInternalHarnessNo(Element hrDesign) throws Exception {
        return this.getAttributeValueFromHarnessRegister("inteharn", hrDesign);
    }

    public String getInternalManufacturingSite(Element hrDesign) throws Exception {
        return this.getAttributeValueFromHarnessRegister("sitename", hrDesign);
    }

    public String getInternalIssue(Element hrDesign) throws Exception {
        return this.getAttributeValueFromHarnessRegister("inteiss", hrDesign);
    }

    public String getInternalDate(Element hrDesign) throws Exception {
        SimpleDateFormat sourceDateFormat = new SimpleDateFormat("MM.dd.yy");
        Date date = sourceDateFormat.parse(this.getAttributeValueFromHarnessRegister("intedate", hrDesign));
        SimpleDateFormat targetDateFormat = new SimpleDateFormat("dd/MM/yy");
        return targetDateFormat.format(date);
    }

    public String getWeight(Element hrDesign, String fmCode) throws Exception {
        String weightFromDesignXml = this.getAttributeValueFromHarnessRegister("weight", hrDesign).trim();
        this.pMsgThread("Code" + fmCode + " weightFromDesignXml: " + weightFromDesignXml);
        String harnessWeight = String.format("%.03f", Double.parseDouble(weightFromDesignXml) / 1000.0).replace(',', '.');
        this.pMsgThread("Setting weight " + harnessWeight + "  in DSI for module design with code " + fmCode);
        return harnessWeight;
    }

    public String getUserField1(Element hrDesign) throws Exception {
        return this.getAttributeValueFromHarnessRegister("userf1", hrDesign).trim();
    }

    public String getUserField2(Element hrDesign) throws Exception {
        return this.getAttributeValueFromHarnessRegister("userf2", hrDesign).trim();
    }

    public String getUserField3(Element hrDesign) throws Exception {
        return this.getAttributeValueFromHarnessRegister("userf3", hrDesign).trim();
    }

    public String getUserField4(Element hrDesign) throws Exception {
        return this.getAttributeValueFromHarnessRegister("userf4", hrDesign).trim();
    }

    public String getUserField5(Element hrDesign) throws Exception {
        return this.getAttributeValueFromHarnessRegister("userf5", hrDesign).trim();
    }

    public String getLRHand(Element hrDesign) throws Exception {
        return this.getAttributeValueFromHarnessRegister("lrhand", hrDesign);
    }

    public String getTrim(Element hrDesign) throws Exception {
        String trim = this.getAttributeValueFromHarnessRegister("trimlev", hrDesign).trim();
        if ("".equals(trim)) {
            return "None";
        }
        return trim;
    }

    public String getTubeAddOnFactor(Element hrDesign) throws Exception {
        return this.getAttributeValueFromHarnessRegister("tubefactor", hrDesign);
    }

    public String getTubeAddOnType(Element hrDesign) throws Exception {
        String type = this.getAttributeValueFromHarnessRegister("tubefactort", hrDesign);
        if ("P".equals(type)) {
            return "Percentage";
        }
        if ("F".equals(type)) {
            return "Fixed";
        }
        return type;
    }

    public String getManualTerminal(Element hrDesign) throws Exception {
        return this.getAttributeValueFromHarnessRegister("manual_term", hrDesign);
    }

    public String getSealedConnectors(Element hrDesign) throws Exception {
        return this.getAttributeValueFromHarnessDesign("issealed", hrDesign);
    }

    public String getPropertyChecker(Element hrDesign) throws Exception {
        NodeList nodeListProperties = hrDesign.getElementsByTagName("property");
        int i = 0;
        while (i < nodeListProperties.getLength()) {
            Element elmProperty;
            String propertyName;
            Node nodeProperty = nodeListProperties.item(0);
            if (nodeProperty.getNodeType() == 1 && (propertyName = (elmProperty = (Element)nodeProperty).getAttribute("name")).equals("Checker")) {
                return elmProperty.getAttribute("val");
            }
            ++i;
        }
        return "";
    }

    public String getCustomerHarnessNo(Element hrDesign) throws Exception {
        return this.getAttributeValueFromHarnessRegister("custharn", hrDesign);
    }

    public String getCustomerIssue(Element hrDesign) throws Exception {
        return this.getAttributeValueFromHarnessRegister("custiss", hrDesign);
    }

    public String getCustomerName(Element hrDesign) throws Exception {
        return this.getAttributeValueFromHarnessRegister("customername", hrDesign);
    }

    public String getCustomerDate(Element hrDesign) throws Exception {
        SimpleDateFormat sourceDateFormat = new SimpleDateFormat("MM.dd.yy");
        Date date = sourceDateFormat.parse(this.getAttributeValueFromHarnessRegister("custdate", hrDesign));
        SimpleDateFormat targetDateFormat = new SimpleDateFormat("dd/MM/yy");
        return targetDateFormat.format(date);
    }

    private String getAttributeValueFromHarnessRegister(String attrName, Element hrDesign) throws Exception {
        if (hrDesign == null) {
            return "";
        }
        Node nodeHarnessRegister = hrDesign.getElementsByTagName("harnessregister").item(0);
        if (1 == nodeHarnessRegister.getNodeType()) {
            Element elemHarnessRegister = (Element)nodeHarnessRegister;
            String internalHarnessPartnumber = elemHarnessRegister.getAttribute(attrName);
            return internalHarnessPartnumber;
        }
        throw new Exception("Could not extract attribute " + attrName + " from harnessregister element of harnessdesign!");
    }

    private String getAttributeValueFromHarnessDesign(String attrName, Element hrDesign) throws Exception {
        return hrDesign.getAttribute(attrName);
    }

    public String getHarnessDesignPartNumber() throws Exception {
        return this.harnessDesignPartNumber;
    }

    private Element getElementHarnessDesign() throws Exception {
        String queryElementName = "harnessdesign";
        this.listChildHarnessDesigns = new ArrayList();
        Element thisElemHarnessDesign = null;
        NodeList desData = this.designXmlDoc.getDocumentElement().getElementsByTagName(queryElementName);
        int i = 0;
        while (i < desData.getLength()) {
            Node node = desData.item(i);
            short nodeType = node.getNodeType();
            if (1 == nodeType) {
                Element elemHarnessDesign = (Element)node;
                if (!this.isCompositeDesign(elemHarnessDesign)) {
                    this.listChildHarnessDesigns.add(elemHarnessDesign);
                }
                String shortDescriptionValue = elemHarnessDesign.getAttribute("shortdescription");
                String nameValue = elemHarnessDesign.getAttribute("name");
                String versionValue = elemHarnessDesign.getAttribute("version");
                if (this.designDesc.equals(shortDescriptionValue) && this.designName.equals(nameValue) && this.designRevision.equals(versionValue)) {
                    thisElemHarnessDesign = elemHarnessDesign;
                }
            }
            ++i;
        }
        if (thisElemHarnessDesign != null) {
            return thisElemHarnessDesign;
        }
        throw new Exception("Could not extract element harnessdesign!");
    }

    public HashMap<String, String> getAllHarnessDesignWeights() throws Exception {
        HashMap<String, String> weightMapPerDesign = new HashMap<String, String>();
        NodeList nodeListHarnessDesigns = this.designXmlDoc.getDocumentElement().getElementsByTagName("harnessdesign");
        int i = 0;
        while (i < nodeListHarnessDesigns.getLength()) {
            Node nodeHarnessDesign = nodeListHarnessDesigns.item(i);
            short nodeType = nodeHarnessDesign.getNodeType();
            if (1 == nodeType) {
                Element elemHarnessDesign = (Element)nodeHarnessDesign;
                NodeList elementsByTagNameHarnessregister = elemHarnessDesign.getElementsByTagName("harnessregister");
                int hr = 0;
                while (hr < elementsByTagNameHarnessregister.getLength()) {
                    Node nodeHarnessRegister = elementsByTagNameHarnessregister.item(hr);
                    if (1 == nodeHarnessRegister.getNodeType()) {
                        Element elemHarnessRegister = (Element)nodeHarnessRegister;
                        String weight = elemHarnessRegister.getAttribute("weight");
                        String inteharn = elemHarnessRegister.getAttribute("inteharn");
                        String name = elemHarnessDesign.getAttribute("name");
                        String mapKey = String.valueOf(inteharn) + MAPSEPARATOR + name;
                        weightMapPerDesign.put(mapKey, weight);
                    }
                    ++hr;
                }
            }
            ++i;
        }
        if (weightMapPerDesign.size() == 0) {
            throw new Exception("Could not extract attribute weight from harnessregister element of harnessdesign!");
        }
        return weightMapPerDesign;
    }

    public boolean hasDesignOptions() throws Exception {
        Node nodeDesignOptions;
        NodeList nodeListDesignOptions = this.elementHarnessDesign.getElementsByTagName("designoptions");
        if (nodeListDesignOptions.getLength() > 0 && 1 == (nodeDesignOptions = nodeListDesignOptions.item(0)).getNodeType()) {
            Element elemDesignOptions = (Element)nodeDesignOptions;
            NodeList refedoptionNodeList = elemDesignOptions.getChildNodes();
            return refedoptionNodeList.getLength() != 0;
        }
        return false;
    }

    public HashMap<String, String> getAllTerminalMaterialCodes() throws Exception {
        HashMap<String, String> terminalMCMap = new HashMap<String, String>();
        NodeList listHarnessContainer = this.elementHarnessDesign.getElementsByTagName("harnesscontainer");
        int i = 0;
        while (i < listHarnessContainer.getLength()) {
            Node nodeHarnessContainer = listHarnessContainer.item(i);
            if (nodeHarnessContainer.getNodeType() == 1) {
                Element elemHarnessContainer = (Element)nodeHarnessContainer;
                NodeList listConnectivity = elemHarnessContainer.getElementsByTagName("connector");
                int j = 0;
                while (j < listConnectivity.getLength()) {
                    Node nodeConnectivity = listConnectivity.item(j);
                    if (nodeConnectivity.getNodeType() == 1) {
                        Element elemConnectivity = (Element)nodeConnectivity;
                        String name = elemConnectivity.getAttribute("name");
                        NodeList pinList = elemConnectivity.getElementsByTagName("pin");
                        int k = 0;
                        while (k < pinList.getLength()) {
                            Node nodePin = pinList.item(k);
                            if (nodePin.getNodeType() == 1) {
                                Element elemPin = (Element)nodePin;
                                String cavityDetails = elemPin.getAttribute("name");
                                String terminalKey = String.valueOf(name) + ":" + cavityDetails;
                                NodeList nodeListCavityDetails = elemPin.getElementsByTagName("cavitydetail");
                                int m = 0;
                                while (m < nodeListCavityDetails.getLength()) {
                                    Node nodeTerminal = nodeListCavityDetails.item(m);
                                    if (nodeTerminal.getNodeType() == 1) {
                                        Element elemTerminal = (Element)nodeTerminal;
                                        String terminalMaterialcode = elemTerminal.getAttribute("terminalmaterialcode");
                                        terminalMCMap.put(terminalKey, terminalMaterialcode);
                                    }
                                    ++m;
                                }
                            }
                            ++k;
                        }
                    }
                    ++j;
                }
            }
            ++i;
        }
        return terminalMCMap;
    }

    public boolean isModuleDesign() {
        return this.isModuleDesign;
    }

    boolean isInclusiveFunctionalModuleEligible(String mainFMCode, String inclusiveFMCode) {
        return this.inclusiveFMCodeFilter.isInclusiveFunctionalModuleEligible(mainFMCode, inclusiveFMCode);
    }
}

