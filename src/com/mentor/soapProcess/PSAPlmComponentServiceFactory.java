package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.SOAPConnection
 *  javax.xml.soap.SOAPConnectionFactory
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPMessage
 *  org.apache.log4j.Appender
 *  org.apache.log4j.FileAppender
 *  org.apache.log4j.Layout
 *  org.apache.log4j.Level
 *  org.apache.log4j.Logger
 *  org.apache.log4j.PatternLayout
 */
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class PSAPlmComponentServiceFactory {
    protected static DocumentBuilder m_docBuilder;
    protected static String logPath;
    protected final String xmlLibraryPartTagname = "librarypart";
    protected final String xmlAttributeNamePartNumber = "partnumber";
    protected final String xmlAttiubuteNameCurrentStatus = "currentstatus";
    protected final String xmlAttributeNameGroupName = "groupname";
    protected final String xmlTagNameImportFeedBack = "importfeedback";
    protected final String xmlTagNameDevicePart = "devicepart";
    protected final String xmlTagnameConnectorPart = "connectorpart";
    protected final String xmlAttributeNameStatus = "status";
    protected final String xmlTagNameRevision = "revision";
    protected final String xmlAttributeNameOldChsPartNumber = "OLDCHSPartNumber";
    protected final String xmlAttributeNameFaultString = "faultstring";
    protected final String xmlAttributeNameNewChsPartNumber = "newCHSPartNumber";
    protected final String xmlAttributeNameSapDescription = "SAP_description";
    protected final String xmlAttributeNameSapPlaneRefrence = "SAP_plane_reference";
    protected final String xmlAttributeNameSapPlaneRevision = "SAP_plane_revision";
    protected final String xmlAttributeNameSapPartReference = "SAP_part_reference";
    protected final String xmlAttributeNameSapPartRevision = "SAP_part_revision";
    protected final String xmlAttributeNamePartStatus = "partstatus";
    protected final String xmlAttributeNameDescription = "description";
    protected final String xmlAttributeNameUserf1 = "userf1";
    protected final String xmlAttributeNameUserf2 = "userf2";
    protected final String partStatusCurrent = "Current";
    protected final String partStatusNew = "New";
    protected final String xmlTagNameChsSystem = "chssystem";
    protected final PatternLayout exceptionLayout = new PatternLayout("%m%n");
    protected final PatternLayout processLayout = new PatternLayout("%m%n");
    protected Logger processLogger;
    protected FileAppender processLogAppender;
    protected String currentInputRequest;
    protected String webServiceName;
    protected final int soapTimeOut = 1800000;
    protected String exportLocationForDevice;
    protected String exportLocationForHarness;
    private String indusScriptWithLocation;
    protected String configKeyExportPartForDevice = "CHS_EXPORT_PATH_DEVICE";
    protected String configKeyExportPartForHarness = "CHS_EXPORT_PATH_HARNESS";
    protected String configKeyLogsPath = "CHS_LOGS_PATH";
    protected String configKeyindusScript = "INDUS_SCRIPT";
    protected static final String UNDERSCORE = "_";
    protected static final String COLON = ":";
    protected static final String HYPHEN = "-";
    protected static final String FORWARD_SLASH = "/";
    protected static final String EMPTY_STRING = "";
    protected static final String NEW_LINE = "\n";
    protected static final String YES = "yes";
    protected static final String NO = "no";
    protected static final String XML = "xml";
    protected static final String FALSE = "false";
    protected static final String TRUE = "true";
    protected static final String ENCODING_UTF8 = "UTF-8";
    protected static final String OVERWRITEMODE_TRUNCATE = "truncate";
    protected static final String xmlTagNameServiceError = "serviceError";
    protected static final String xmlTagNameServiceParts = "Parts";
    protected static final String xmlTagNameServicePart = "Part";
    protected static final String xmlTagNameServiceImportService = "importservice";
    protected static final String xmlTagNameServiceImport = "import";
    protected static final String xmlAttributeNameCode = "code";
    protected static final String xmlAttributeNameType = "type";
    protected static final String xmlAttributeNamedetail = "detail";
    protected static final String xmlAttributeNameContent = "content";
    protected static final String xmlAttributeNameOverWriteCodeDesc = "overwritecodedesc";
    protected static final String xmlAttributeNameOverWritePart = "overwritepart";
    protected static final String xmlAttributeNameScopeImport = "scopeimport";
    protected static final String xmlAttributeNameOverWriteMode = "overwritemode";
    protected static final String xmlAttributeNameIncludeScoping = "includescoping";
    protected static final String xmlAttributeNameExportRevisions = "exportrevisions";
    protected static final String xmlAttributeNameSchemaValidation = "SchemaValidation";
    protected static String exportLibraryPartsWSName;
    protected static String importLibraryPartsWSName;
    protected final String connetionNameChs = "CHS";
    private static Map<String, String> errorDetailsMap;
    private static final String CONFIG_ERROR_CODE_003 = "ERROR_CODE_003";
    private static final String CONFIG_ERROR_CODE_004 = "ERROR_CODE_004";
    private static final String CONFIG_ERROR_CODE_005 = "ERROR_CODE_005";
    private static final String CONFIG_ERROR_CODE_006 = "ERROR_CODE_006";
    private static final String CONFIG_ERROR_CODE_007 = "ERROR_CODE_007";
    private static final String CONFIG_ERROR_CODE_008 = "ERROR_CODE_008";
    private static final String CONFIG_ERROR_CODE_009 = "ERROR_CODE_009";
    private static final String CONFIG_ERROR_CODE_010 = "ERROR_CODE_010";
    private static final String CONFIG_ERROR_CODE_011 = "ERROR_CODE_011";
    private static final String CONFIG_ERROR_CODE_012 = "ERROR_CODE_012";
    private static final String CONFIG_ERROR_CODE_013 = "ERROR_CODE_013";
    private static final String CONFIG_ERROR_CODE_014 = "ERROR_CODE_014";

    static {
        logPath = "logs";
        exportLibraryPartsWSName = "ExportLibraryParts";
        importLibraryPartsWSName = "ImportLibraryParts";
        errorDetailsMap = new HashMap<String, String>();
    }

    public PSAPlmComponentServiceFactory(String webServiceName) throws PSAPlmChsHcException {
        this.webServiceName = webServiceName;
        this.initializeProperties();
        this.validateProperties();
        this.initializeProcessLogger();
        this.initializeDomParser();
    }

    private static void readErrorDetails(Properties prop) {
        errorDetailsMap.clear();
        errorDetailsMap.put("003", prop.getProperty(CONFIG_ERROR_CODE_003));
        errorDetailsMap.put("004", prop.getProperty(CONFIG_ERROR_CODE_004));
        errorDetailsMap.put("005", prop.getProperty(CONFIG_ERROR_CODE_005));
        errorDetailsMap.put("006", prop.getProperty(CONFIG_ERROR_CODE_006));
        errorDetailsMap.put("007", prop.getProperty(CONFIG_ERROR_CODE_007));
        errorDetailsMap.put("008", prop.getProperty(CONFIG_ERROR_CODE_008));
        errorDetailsMap.put("009", prop.getProperty(CONFIG_ERROR_CODE_009));
        errorDetailsMap.put("010", prop.getProperty(CONFIG_ERROR_CODE_010));
        errorDetailsMap.put("011", prop.getProperty(CONFIG_ERROR_CODE_011));
        errorDetailsMap.put("012", prop.getProperty(CONFIG_ERROR_CODE_012));
        errorDetailsMap.put("013", prop.getProperty(CONFIG_ERROR_CODE_013));
        errorDetailsMap.put("014", prop.getProperty(CONFIG_ERROR_CODE_014));
    }

    public static String getErrorDetail(String errorCode) {
        if (errorDetailsMap.containsKey(errorCode)) {
            return errorDetailsMap.get(errorCode);
        }
        return EMPTY_STRING;
    }

    private void initializeProperties() throws PSAPlmChsHcException {
        FileInputStream propInFile = null;
        try {
            try {
                propInFile = new FileInputStream(SceGlobals.propFilePath);
                Properties prop = new Properties();
                prop.load(propInFile);
                this.exportLocationForDevice = prop.getProperty(this.configKeyExportPartForDevice);
                this.exportLocationForHarness = prop.getProperty(this.configKeyExportPartForHarness);
                logPath = prop.getProperty(this.configKeyLogsPath);
                this.indusScriptWithLocation = prop.getProperty(this.configKeyindusScript);
                PSAPlmComponentServiceFactory.readErrorDetails(prop);
            }
            catch (FileNotFoundException e) {
                PSAPlmChsHcException psaExc = new PSAPlmChsHcException("012", null, "READING PROPERTIES!!!", "READING PROPERTIES FILE FAILED!!! " + e.getMessage());
                psaExc.setStackTrace(e.getStackTrace());
                System.err.println(psaExc.getMessage());
                psaExc.printStackTrace();
                throw psaExc;
            }
            catch (IOException e) {
                PSAPlmChsHcException psaExc = new PSAPlmChsHcException("012", null, "READING PROPERTIES!!!", "READING PROPERTIES FILE FAILED!!! " + e.getMessage());
                psaExc.setStackTrace(e.getStackTrace());
                System.err.println(psaExc.getMessage());
                psaExc.printStackTrace();
                throw psaExc;
            }
        }
        finally {
            if (propInFile != null) {
                try {
                    propInFile.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void validateProperties() throws PSAPlmChsHcException {
        StringBuilder errorMessage = new StringBuilder();
        this.checkIfValid(errorMessage, this.exportLocationForDevice, this.configKeyExportPartForDevice);
        this.checkIfValid(errorMessage, this.exportLocationForHarness, this.configKeyExportPartForHarness);
        this.checkIfValid(errorMessage, logPath, this.configKeyLogsPath);
        this.checkIfValid(errorMessage, this.indusScriptWithLocation, this.configKeyindusScript);
        if (errorMessage.length() > 0) {
            PSAPlmChsHcException psaExc = new PSAPlmChsHcException("012", null, "READING PROPERTIES!!!", "READING PROPERTIES FILE FAILED!!! " + errorMessage.toString());
            System.err.println(psaExc.getMessage());
            psaExc.printStackTrace();
            throw psaExc;
        }
    }

    private void checkIfValid(StringBuilder errorMessage, String propertyValue, String propertyKey) {
        if (propertyValue == null) {
            errorMessage.append("Invalid configuration found for key : ");
            errorMessage.append(propertyKey);
            errorMessage.append(NEW_LINE);
        }
    }

    private void initializeProcessLogger() {
        String fileName = new Date().toString().replaceAll(COLON, HYPHEN);
        this.processLogger = Logger.getLogger((String)("PROCESS_LOGGER_" + fileName));
        this.processLogger.setLevel(Level.INFO);
        StringBuilder completeFileName = new StringBuilder();
        completeFileName.append(logPath);
        completeFileName.append(FORWARD_SLASH);
        completeFileName.append(this.webServiceName);
        completeFileName.append("_Process_");
        completeFileName.append(fileName);
        completeFileName.append(".log");
        try {
            this.processLogAppender = new FileAppender((Layout)this.processLayout, completeFileName.toString(), false);
            this.processLogger.addAppender((Appender)this.processLogAppender);
        }
        catch (IOException e1) {
            System.err.println("Unable to initialize Process Logger!!!");
            e1.printStackTrace();
        }
    }

    private void initializeDomParser() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
            m_docBuilder = factory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            PSAPlmChsHcException psaExc = new PSAPlmChsHcException("003", null, "Initializing", String.valueOf(PSAPlmComponentServiceFactory.getErrorDetail("003")) + "Unable to configure parser...");
            psaExc.setStackTrace(e.getStackTrace());
            this.exceptionLogger("Initializing", psaExc);
        }
    }

    public void processLog(String msg) {
        this.processLogger.info((Object)msg);
    }

    public void exceptionLogger(String msg, PSAPlmChsHcException e) {
        try {
            String partNumber = e.getPartNumber();
            partNumber = partNumber != null ? HYPHEN + partNumber : EMPTY_STRING;
            StringBuilder fileName = new StringBuilder();
            fileName.append(e.getErrorCode());
            fileName.append(UNDERSCORE);
            fileName.append(new Date().toString().replaceAll(COLON, HYPHEN));
            fileName.append(partNumber);
            Logger exceptionLogger = Logger.getLogger((String)("EXCEPTION_LOGGER_" + fileName));
            exceptionLogger.setLevel(Level.ERROR);
            StringBuilder completeFileName = new StringBuilder();
            completeFileName.append(logPath);
            completeFileName.append(FORWARD_SLASH);
            completeFileName.append(this.webServiceName);
            completeFileName.append("_Error_");
            completeFileName.append((CharSequence)fileName);
            completeFileName.append(".log");
            FileAppender fa = new FileAppender((Layout)this.exceptionLayout, completeFileName.toString(), false);
            exceptionLogger.addAppender((Appender)fa);
            exceptionLogger.error(null, (Throwable)e);
            fa.close();
        }
        catch (IOException e1) {
            System.err.println("Not able to Initialize exception logger.");
            e1.printStackTrace();
        }
    }

    protected static String toStringDoc(Document doc) {
        try {
            StringWriter sw = new StringWriter();
            if (doc != null) {
                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer transformer = tf.newTransformer();
                transformer.setOutputProperty("omit-xml-declaration", NO);
                transformer.setOutputProperty("method", XML);
                transformer.setOutputProperty("indent", YES);
                transformer.setOutputProperty("encoding", ENCODING_UTF8);
                transformer.transform(new DOMSource(doc), new StreamResult(sw));
            }
            return sw.toString();
        }
        catch (Exception ex) {
            throw new RuntimeException("Error converting to String", ex);
        }
    }

    protected static Document generateFaultBlock(String faultCode, String faultType, String faultDetail) {
        Document respDoc = m_docBuilder.newDocument();
        Element errorElem = respDoc.createElement(xmlTagNameServiceError);
        errorElem.setAttribute(xmlAttributeNameCode, faultCode);
        errorElem.setAttribute(xmlAttributeNameType, faultType);
        errorElem.setAttribute(xmlAttributeNamedetail, faultDetail);
        respDoc.appendChild(errorElem);
        return respDoc;
    }

    protected Document exportComponentFromCHS(String partNumber) throws PSAPlmChsHcException {
        String currentState = "Exporting component from CHS...";
        this.processLog(currentState);
        Document docmesg = m_docBuilder.newDocument();
        Element root = docmesg.createElement(xmlTagNameServiceParts);
        root.setAttribute(xmlAttributeNameIncludeScoping, FALSE);
        root.setAttribute(xmlAttributeNameExportRevisions, FALSE);
        Element part = docmesg.createElement(xmlTagNameServicePart);
        String attrName = "partnumber";
        part.setAttribute(attrName, partNumber);
        root.appendChild(part);
        docmesg.appendChild(root);
        Document responseDOM = null;
        try {
            SOAPMessage reqMsg = WebServerServlet.SOAP_MESSAGE_FACTORY.createMessage();
            NotifyService.readChsCustProps();
            NotifyService.insertSOAPAuthentication(reqMsg);
            NotifyService.insertInputXMLToSOAPBody(docmesg, reqMsg.getSOAPPart(), reqMsg);
            this.currentInputRequest = PSAPlmComponentServiceFactory.toStringDoc(docmesg);
            String endPoint = String.valueOf(NotifyService.CIS_SVG_SERVICE_URL) + exportLibraryPartsWSName;
            SOAPMessage respMsg = PSAPlmComponentServiceFactory.sendSOAPRequest(reqMsg, endPoint, 1800000);
            responseDOM = SOAPUtils.getDocFromSoapMessage(respMsg);
            if (responseDOM == null) {
                PSAPlmChsHcException psaExc = new PSAPlmChsHcException("005", partNumber, currentState, String.valueOf(PSAPlmComponentServiceFactory.getErrorDetail("005")) + " Invalid response received!!!");
                psaExc.setSoapReq(PSAPlmComponentServiceFactory.toStringDoc(docmesg));
                psaExc.setSoapResp(PSAPlmComponentServiceFactory.toStringDoc(responseDOM));
                throw psaExc;
            }
            this.checkForFaultCode(responseDOM, this.currentInputRequest, PSAPlmComponentServiceFactory.toStringDoc(SOAPUtils.getDocFromSoapMessage(respMsg)), partNumber);
        }
        catch (PSAPlmChsHcException e) {
            throw e;
        }
        catch (Exception e) {
            PSAPlmChsHcException psaExc = new PSAPlmChsHcException("005", partNumber, currentState, String.valueOf(PSAPlmComponentServiceFactory.getErrorDetail("005")) + e.getMessage());
            psaExc.setSoapReq(PSAPlmComponentServiceFactory.toStringDoc(docmesg));
            psaExc.setSoapResp(PSAPlmComponentServiceFactory.toStringDoc(responseDOM));
            psaExc.setStackTrace(e.getStackTrace());
            throw psaExc;
        }
        return responseDOM;
    }

    protected void appendChildNodeToDiffXml(Node nodeToBeAdded, Document document, Node newDocumentNode) {
        Node convertedNode = document.importNode(nodeToBeAdded, true);
        newDocumentNode.appendChild(convertedNode);
    }

    protected Document importComponentInCHS(Document inputDoc, String partNumber, String currentState) throws PSAPlmChsHcException {
        this.processLog(currentState);
        Document responseDOM = null;
        try {
            Document docmesg = m_docBuilder.newDocument();
            Element root = docmesg.createElement(xmlTagNameServiceImportService);
            root.setAttribute(xmlAttributeNameSchemaValidation, TRUE);
            docmesg.appendChild(root);
            Element childNode = docmesg.createElement(xmlTagNameServiceImport);
            root.appendChild(childNode);
            childNode.setAttribute(xmlAttributeNameContent, XML);
            childNode.setAttribute(xmlAttributeNameOverWriteCodeDesc, FALSE);
            childNode.setAttribute(xmlAttributeNameOverWritePart, FALSE);
            childNode.setAttribute(xmlAttributeNameScopeImport, FALSE);
            childNode.setAttribute(xmlAttributeNameOverWriteMode, OVERWRITEMODE_TRUNCATE);
            NodeList inputNodeList = inputDoc.getChildNodes();
            int i = 0;
            while (i < inputNodeList.getLength()) {
                this.appendChildNodeToDiffXml(inputNodeList.item(i), docmesg, childNode);
                ++i;
            }
            SOAPMessage reqMsg = WebServerServlet.SOAP_MESSAGE_FACTORY.createMessage();
            NotifyService.readChsCustProps();
            NotifyService.insertSOAPAuthentication(reqMsg);
            NotifyService.insertInputXMLToSOAPBody(docmesg, reqMsg.getSOAPPart(), reqMsg);
            this.currentInputRequest = PSAPlmComponentServiceFactory.toStringDoc(docmesg);
            String endPoint = String.valueOf(NotifyService.CIS_SVG_SERVICE_URL) + importLibraryPartsWSName;
            SOAPMessage respMsg = PSAPlmComponentServiceFactory.sendSOAPRequest(reqMsg, endPoint, 1800000);
            responseDOM = SOAPUtils.getDocFromSoapMessage(respMsg);
            if (responseDOM == null) {
                PSAPlmChsHcException psaExc = new PSAPlmChsHcException("008", partNumber, currentState, String.valueOf(PSAPlmComponentServiceFactory.getErrorDetail("008")) + " Invalid response received!!!");
                psaExc.setSoapReq(PSAPlmComponentServiceFactory.toStringDoc(inputDoc));
                psaExc.setSoapResp(PSAPlmComponentServiceFactory.toStringDoc(responseDOM));
                throw psaExc;
            }
            this.checkForFaultCode(responseDOM, this.currentInputRequest, PSAPlmComponentServiceFactory.toStringDoc(SOAPUtils.getDocFromSoapMessage(respMsg)), partNumber);
        }
        catch (PSAPlmChsHcException e) {
            throw e;
        }
        catch (Exception e) {
            PSAPlmChsHcException psaExc = new PSAPlmChsHcException("008", partNumber, currentState, String.valueOf(PSAPlmComponentServiceFactory.getErrorDetail("008")) + e.getMessage());
            psaExc.setSoapReq(PSAPlmComponentServiceFactory.toStringDoc(inputDoc));
            psaExc.setSoapResp(PSAPlmComponentServiceFactory.toStringDoc(responseDOM));
            psaExc.setStackTrace(e.getStackTrace());
            throw psaExc;
        }
        return responseDOM;
    }

    protected static SOAPMessage sendSOAPRequest(SOAPMessage requestMessage, String url, int timeout) throws PSAPlmChsHcException {
        try {
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection connection = soapConnectionFactory.createConnection();
            return connection.call(requestMessage, (Object)url);
        }
        catch (SOAPException e) {
            PSAPlmChsHcException psaExc = new PSAPlmChsHcException("004", null, "Sending SOAPRequest", String.valueOf(PSAPlmComponentServiceFactory.getErrorDetail("004")) + e.getMessage());
            try {
                psaExc.setSoapReq(PSAPlmComponentServiceFactory.toStringDoc(SOAPUtils.getDocFromSoapMessage(requestMessage)));
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }
            psaExc.setStackTrace(e.getStackTrace());
            throw psaExc;
        }
    }

    protected Element getNodeWithAttribute(Node root, String attrName, String attrValue) {
        NodeList nodelist = root.getChildNodes();
        int i = 0;
        while (i < nodelist.getLength()) {
            Node node = nodelist.item(i);
            if (node instanceof Element) {
                Element el = (Element)node;
                if (el.getAttribute(attrName).equalsIgnoreCase(attrValue)) {
                    return el;
                }
                el = this.getNodeWithAttribute(node, attrName, attrValue);
                if (el != null) {
                    return el;
                }
            }
            ++i;
        }
        return null;
    }

    protected void validatePartExistance(Document exportedDoc, String partNumber) throws PSAPlmChsHcException {
        boolean isValidResponse = true;
        if (exportedDoc == null) {
            isValidResponse = false;
        } else {
            NodeList nList = exportedDoc.getElementsByTagName("chssystem");
            int numberOfnodes = nList.getLength();
            if (numberOfnodes < 1 || !nList.item(0).hasChildNodes()) {
                isValidResponse = false;
            }
        }
        if (!isValidResponse) {
            PSAPlmChsHcException psaExc = new PSAPlmChsHcException("009", partNumber, "Validating Part Existance!!!", String.valueOf(PSAPlmComponentServiceFactory.getErrorDetail("009")) + this.getFaultString009(partNumber));
            psaExc.setSoapReq(this.currentInputRequest);
            psaExc.setSoapResp(PSAPlmComponentServiceFactory.toStringDoc(exportedDoc));
            throw psaExc;
        }
    }

    public void checkForFaultCode(Document xmlDoc, String soapReqMessage, String soapRespMessage, String partNumber) throws PSAPlmChsHcException {
        String currentState = "Checking for Fault code in XML...";
        this.processLog(currentState);
        NodeList nodes = xmlDoc.getElementsByTagName("faultstring");
        if (nodes.getLength() > 0) {
            String faultString = nodes.item(0).getFirstChild().getNodeValue();
            this.processLog("Fault String found : " + faultString);
            PSAPlmChsHcException psaExc = new PSAPlmChsHcException("013", partNumber, currentState, String.valueOf(PSAPlmComponentServiceFactory.getErrorDetail("013")) + faultString);
            psaExc.setSoapReq(soapReqMessage);
            psaExc.setSoapResp(soapRespMessage);
            throw psaExc;
        }
    }

    public void launchExe(String errorCode, String connectionName, String partNumber) {
        block15: {
            String localPartNumber = partNumber;
            this.processLog("INDUS script launched...");
            System.out.println("launchExe start");
            if (localPartNumber == null) {
                localPartNumber = HYPHEN;
            }
            ProcessBuilder pb = new ProcessBuilder(this.indusScriptWithLocation, errorCode, connectionName, localPartNumber);
            Process pr = null;
            try {
                try {
                    pb.redirectErrorStream(true);
                    pr = pb.start();
                    this.readInputStreams(pr);
                    int exitVal = pr.waitFor();
                    System.out.println("launchExe exited with error code " + exitVal);
                }
                catch (Exception e) {
                    System.out.println("Error in executing " + e.getMessage());
                    PSAPlmChsHcException psaExc = new PSAPlmChsHcException("014", partNumber, "Running INDUS Script", String.valueOf(PSAPlmComponentServiceFactory.getErrorDetail("014")) + e.getMessage());
                    psaExc.setStackTrace(e.getStackTrace());
                    this.exceptionLogger(EMPTY_STRING, psaExc);
                    if (pr != null) {
                        try {
                            pr.getInputStream().close();
                            pr.getOutputStream().close();
                            pr.getErrorStream().close();
                        }
                        catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                    break block15;
                }
            }
            catch (Throwable throwable) {
                if (pr != null) {
                    try {
                        pr.getInputStream().close();
                        pr.getOutputStream().close();
                        pr.getErrorStream().close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                throw throwable;
            }
            if (pr != null) {
                try {
                    pr.getInputStream().close();
                    pr.getOutputStream().close();
                    pr.getErrorStream().close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("launchExe end");
    }

    private void readInputStreams(Process pr) throws IOException {
        InputStreamReader inputSR = null;
        InputStreamReader errorSR = null;
        try {
            inputSR = new InputStreamReader(pr.getInputStream());
            BufferedReader input = new BufferedReader(inputSR);
            String line = null;
            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }
            errorSR = new InputStreamReader(pr.getErrorStream());
            BufferedReader error = new BufferedReader(errorSR);
            String errorLine = null;
            while ((errorLine = error.readLine()) != null) {
                System.out.println(errorLine);
            }
        }
        finally {
            if (inputSR != null) {
                inputSR.close();
            }
            if (errorSR != null) {
                errorSR.close();
            }
        }
    }

    protected String getFaultString007(String partNumber) {
        StringBuilder faultString = new StringBuilder();
        faultString.append("Component <");
        if (partNumber != null) {
            faultString.append(partNumber);
        }
        faultString.append("> already exists.");
        return faultString.toString();
    }

    protected String getFaultString008(String errorMessage) {
        StringBuilder faultString = new StringBuilder();
        faultString.append("Importation of component in CHS failed. <");
        if (errorMessage != null) {
            faultString.append(errorMessage);
        }
        faultString.append(">");
        return faultString.toString();
    }

    protected String getFaultString009(String partNumber) {
        StringBuilder faultString = new StringBuilder();
        faultString.append("The Component <");
        if (partNumber != null) {
            faultString.append(partNumber);
        }
        faultString.append("> is not found in CHS.");
        return faultString.toString();
    }

    protected String getFaultString010(String partNumber, String partStatus) {
        StringBuilder faultString = new StringBuilder();
        faultString.append("The Component <");
        if (partNumber != null) {
            faultString.append(partNumber);
        }
        faultString.append("> has status <");
        if (partStatus != null) {
            faultString.append(partStatus);
        }
        faultString.append("> in CHS than the required status CURRENT.");
        return faultString.toString();
    }

    protected String getFaultString011(String partNumber) {
        StringBuilder faultString = new StringBuilder();
        faultString.append("The Component <");
        if (partNumber != null) {
            faultString.append(partNumber);
        }
        faultString.append("> has status NEW in CHS.");
        return faultString.toString();
    }
}

