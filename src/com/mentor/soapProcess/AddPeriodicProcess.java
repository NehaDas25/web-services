package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.MessageFactory
 *  javax.xml.soap.SOAPConnection
 *  javax.xml.soap.SOAPConnectionFactory
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPMessage
 *  javax.xml.soap.SOAPPart
 *  org.apache.xerces.parsers.DOMParser
 */
import com.mentor.mcd.DMSEncrypter;
import com.mentor.mcd.MCDDMSException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.GregorianCalendar;
import java.util.Properties;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.mentor.taskProcessing.BackgroundTaskProcessing;
import com.mentor.taskProcessing.IBackgroundTaskProcessing;

class AddPeriodicProcess
extends Thread {
    private static String[] CHS_AUTHENTICATION_VALUES = new String[]{"", ""};
    private static String CIS_SVG_SERVICE_URL = "";
    private static int CIS_SVG_SERVICE_TIMEOUT = 1800000;
    private Transformer _xmlTransformer;
    private DocumentBuilder _docBuilder;
    private MessageFactory _msgFact;
    private static IBackgroundTaskProcessing taskProcessing = BackgroundTaskProcessing.getInstance();
    private static String pathForProcessing;
    private static String scriptForProcessing;

    AddPeriodicProcess() {
    }

    @Override
    public void run() {
        AddPeriodicProcess.readChsCustProps();
        if (SceGlobals.synGlobalCodesTime.equals("") && SceGlobals.synGlobalCodesHours.equals("") && SceGlobals.synGlobalCodesMinutes.equals("")) {
            System.out.println("No global code synchronization.");
            return;
        }
        System.out.println("Periodic process has been started. ");
        try {
            this._msgFact = MessageFactory.newInstance();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            this._docBuilder = factory.newDocumentBuilder();
            this._xmlTransformer = TransformerFactory.newInstance().newTransformer();
            while (true) {
                GregorianCalendar dat = new GregorianCalendar();
                DecimalFormat df = new DecimalFormat("00");
                Long a = Long.valueOf(dat.get(12));
                String time = String.valueOf(dat.get(11)) + ":" + df.format(a);
                if (!SceGlobals.synGlobalCodesTime.equals("")) {
                    if (SceGlobals.synGlobalCodesTime.equals(time)) {
                        System.out.println("Synchronizing... ");
                        this.writeFile();
                        AddPeriodicProcess.sleep(50000L);
                    }
                    AddPeriodicProcess.sleep(10000L);
                    continue;
                }
                Integer i1 = Integer.parseInt(SceGlobals.synGlobalCodesHours);
                long mins = i1.longValue() * 60L;
                Integer i2 = Integer.parseInt(SceGlobals.synGlobalCodesMinutes);
                long secs = (mins + i2.longValue()) * 60L * 1000L;
                System.out.println("Sec. " + secs);
                System.out.println("Synchronizing... ");
                this.writeFile();
                System.out.println("Waiting... " + secs);
                AddPeriodicProcess.sleep(secs);
            }
        }
        catch (InterruptedException e) {
            System.out.println("AddPeriodicProcess interrupted: " + e.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeFile() {
        Document request = null;
        Document response = null;
        Document systemOptionsResponse = null;
        try {
            request = this.formatSystemRequest();
            if (request != null) {
                response = this.getCISResponse(request);
            }
            this.processExportSystemResponse(response);
            systemOptionsResponse = this.getCISGlobalOptionsResponse();
            this.processExportSystemOptionsResponse(systemOptionsResponse);
            System.out.println("Synchronizing Global Code Files finished.\n");
        }
        catch (Exception e) {
            System.out.println("SOAP Exception\n" + e.getMessage());
        }
    }

    private static void setCISUrlUserPassword(Properties p, DMSEncrypter encryptCIS)
    {
        SceGlobals.CIS_URL = p.getProperty("CIS_URL");
        CIS_SVG_SERVICE_URL = SceGlobals.CIS_URL;
        NotifyService.CHS_AUTHENTICATION_VALUES[0] = SceGlobals.CIS_U = p.getProperty("CIS_U");
        SceGlobals.CIS_P = p.getProperty("CIS_P");
        try {
            NotifyService.CHS_AUTHENTICATION_VALUES[1] = encryptCIS.decryptString(p.getProperty("CIS_P"));
        }
        catch (MCDDMSException e) {
            System.err.println("Can't decrypt.");
            System.err.println(e.getMessage());
        }
    }

    private static void readChsCustProps() {
        try {
            DMSEncrypter encrypter = null;
            encrypter = new DMSEncrypter();
            if (SceGlobals.propFilePath.contains("%20") | SceGlobals.propFilePath.contains(" ")) {
                String proPath = "";
                String[] path = SceGlobals.propFilePath.split("%20");
                int i = 0;
                while (i < path.length) {
                    proPath = String.valueOf(proPath) + path[i] + " ";
                    ++i;
                }
                proPath.trim();
                SceGlobals.propFilePath = proPath;
            }
            FileInputStream propInFile = new FileInputStream(SceGlobals.propFilePath);
            Properties p2 = new Properties();
            p2.load(propInFile);
            SceGlobals.Default_Border_name = p2.getProperty("Default_Border_name") != null ? p2.getProperty("Default_Border_name") : "--";
            SceGlobals.TIFF_Name_Constant = p2.getProperty("TIFF_Name_Constant") != null ? p2.getProperty("TIFF_Name_Constant") : "---";
            SceGlobals.CIS_SVG_OUTPUT = p2.getProperty("CIS_SVG_OUTPUT");
            if(NotifyService.isCapitalXMode()) {
                pathForProcessing = taskProcessing.getOutputPath(p2.getProperty("CIS_SVG_OUTPUT"), p2.getProperty("CIS_OUTPUT_STORE"));
                scriptForProcessing = p2.getProperty("SCRIPT_PATH");
                SceGlobals.CIS_SVG_OUTPUT = pathForProcessing;
            }
            else{
                SceGlobals.CIS_SVG_OUTPUT = p2.getProperty("CIS_SVG_OUTPUT");
            }
            //Download the cis-config.properties if needed.
            if(Files.exists(Path.of(SceGlobals.cisConfigFilePath))) {
                System.out.println("cis-config.properties already exists in the shared storage.");
            }
            else {
                System.out.println("Call from AddPeriodic Process.");
                System.out.println("cis-config.properties does not exist. Downloading cis-config.properties from shared storage.");
                //Pre-processing task for downloading cis-config.properties from shared storage.
                //preProcessingTask(String cisPropertiesPath, String configFilePath, String scriptPath
                int statusCode = taskProcessing.preProcessingTask(SceGlobals.CIS_PROPERTIES_PATH, SceGlobals.configFilePath, scriptForProcessing);
                if (statusCode != 0) {
                    System.err.println("Error in pre processing task for downloading cis-config.properties from shared storage!");
                }
            }
            //Set CIS_U and CIS_P technical user.
            if(NotifyService.isCapitalXMode()) {
                FileInputStream propCISInFile = new FileInputStream(SceGlobals.cisConfigFilePath);
                Properties pCIS = new Properties();
                pCIS.load(propCISInFile);
                setCISUrlUserPassword(pCIS, encrypter);
            }
            else {
                setCISUrlUserPassword(p2, encrypter);
            }
            AddPeriodicProcess.CHS_AUTHENTICATION_VALUES[1] = SceGlobals.CIS_P;
            try {
                Integer in = Integer.parseInt(p2.getProperty("CIS_TIMEOUT"));
                SceGlobals.CIS_TIMEOUT = in;
            }
            catch (NumberFormatException nfex) {
                System.out.println("Wrong CIS timeout value " + p2.getProperty("CIS_TIMEOUT") + " in chs_cust.properties file.");
                SceGlobals.CIS_TIMEOUT = 1800000;
            }
            CIS_SVG_SERVICE_TIMEOUT = SceGlobals.CIS_TIMEOUT;
            SceGlobals.ChsU = p2.getProperty("ChsU");
            try {
                SceGlobals.ChsP = encrypter.decryptString(p2.getProperty("ChsP"));
            }
            catch (MCDDMSException e) {
                System.err.println("Can't decrypt.");
                e.printStackTrace();
            }
            SceGlobals.ChsS = p2.getProperty("ChsS");
            if (p2.getProperty("OUTDIR") != null) {
                SceGlobals.synGlobalCodesOutDir = p2.getProperty("OUTDIR");
            }
            String propValue = "";
            if (p2.getProperty("PERIOD") != null) {
                propValue = p2.getProperty("PERIOD");
                String[] tmpArray = propValue.split(":");
                if (tmpArray.length == 2) {
                    SceGlobals.synGlobalCodesHours = tmpArray[0];
                    SceGlobals.synGlobalCodesMinutes = tmpArray[1];
                }
            } else {
                SceGlobals.synGlobalCodesHours = "";
                SceGlobals.synGlobalCodesMinutes = "";
            }
            if (p2.getProperty("TIME") != null) {
                SceGlobals.synGlobalCodesTime = p2.getProperty("TIME");
            }
            SceGlobals.debug = p2.getProperty("DEBUG").equals("true");
            if (p2.getProperty("CHS_VERSION") != null) {
                SceGlobals.CHS_VERSION = p2.getProperty("CHS_VERSION");
            }
            SceGlobals.DESIGN_NAME_CHECK = p2.getProperty("DESIGN_NAME_CHECK");
            propInFile.close();
        }
        catch (FileNotFoundException e) {
            System.err.println("Can't find " + SceGlobals.propFilePath);
        }
        catch (IOException e) {
            System.err.println("I/O failed." + SceGlobals.propFilePath);
        } catch (MCDDMSException e) {
            throw new RuntimeException(e);
        }
    }

    private Document getCISResponse(Document requestPayload) throws Exception {
        Document responsePayload = null;
        URL url = new URL(String.valueOf(SceGlobals.CIS_URL) + "ExportObjectTypeInfo");
        SOAPMessage reqMsg = this._msgFact.createMessage();
        AddPeriodicProcess.insertSOAPAuthentication(reqMsg);
        this.insertInputXMLToSOAPBody(requestPayload, reqMsg.getSOAPPart(), reqMsg);
        boolean isResponseExpected = true;
        SOAPMessage respMsg = this.sendSOAPRequest(reqMsg, url, SceGlobals.CIS_TIMEOUT, isResponseExpected);
        if (respMsg != null) {
            if (AddPeriodicProcess.isAnyFault(respMsg)) {
                String fault = AddPeriodicProcess.getFaultString(respMsg);
                throw new Exception("SOAP Faultaaa = " + fault);
            }
            if (isResponseExpected) {
                responsePayload = SOAPUtils.getDocFromSoapMessage(respMsg);
            }
        }
        return responsePayload;
    }

    private Document getfixDoc() throws Exception {
        Document doc = null;
        DOMParser dom = new DOMParser();
        try {
            dom.setFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace", false);
            String filename = String.valueOf(SceGlobals.CIS_SVG_OUTPUT) + "/System-options.xml";
            dom.parse(filename);
            doc = dom.getDocument();
        }
        catch (SAXException se) {
            System.out.println("Error during system option example file parsing: " + se.getMessage());
        }
        catch (IOException e) {
            System.out.println("IO-Error during system option example file reading: " + e.getMessage());
        }
        return doc;
    }

    private Document getCISGlobalOptionsResponse() throws Exception {
        Document requestGlobalOptionsPayload = this._docBuilder.newDocument();
        Element root = requestGlobalOptionsPayload.createElement("project");
        requestGlobalOptionsPayload.appendChild(root);
        Document responsePayload = null;
        URL url = new URL(String.valueOf(SceGlobals.CIS_URL) + "ExportOptions");
        SOAPMessage reqMsg = this._msgFact.createMessage();
        AddPeriodicProcess.insertSOAPAuthentication(reqMsg);
        this.insertInputXMLToSOAPBody(requestGlobalOptionsPayload, reqMsg.getSOAPPart(), reqMsg);
        boolean isResponseExpected = true;
        SOAPMessage respMsg = this.sendSOAPRequest(reqMsg, url, SceGlobals.CIS_TIMEOUT, isResponseExpected);
        if (respMsg != null) {
            if (AddPeriodicProcess.isAnyFault(respMsg)) {
                String fault = AddPeriodicProcess.getFaultString(respMsg);
                throw new Exception("SOAP Fault during global options request = " + fault);
            }
            if (isResponseExpected) {
                responsePayload = SOAPUtils.getDocFromSoapMessage(respMsg);
            }
        }
        return responsePayload;
    }

    private static void insertSOAPAuthentication(SOAPMessage msg) throws Exception {
        SOAPUtils.insertSOAPAuthentication(msg, CHS_AUTHENTICATION_VALUES[0], CHS_AUTHENTICATION_VALUES[1]);
    }

    private SOAPMessage sendSOAPRequest(SOAPMessage requestMessage, URL url, int timeout, boolean isResponseExpected) throws Exception {
        SOAPMessage response;
        block3: {
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection connection = soapConnectionFactory.createConnection();
            response = null;
            try {
                response = connection.call(requestMessage, (Object)url);
            }
            catch (SOAPException exc) {
                AddPeriodicProcess.msgBox("Connection to CIS server failed.");
                boolean ignoreSOAPFault = false;
                if ("org.xml.sax.SAXParseException: The root element is required in a well-formed document.".equals(exc.getMessage())) {
                    boolean bl = ignoreSOAPFault = !isResponseExpected;
                }
                if (ignoreSOAPFault) break block3;
                throw exc;
            }
        }
        return response;
    }

    private void insertInputXMLToSOAPBody(Document domObject, SOAPPart part, SOAPMessage processedMessage) throws Exception {
        Element docEl;
        NodeList nList;
        Source spSrc = part.getContent();
        DOMResult domRes = new DOMResult();
        this._xmlTransformer.transform(spSrc, domRes);
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

    public void processExportSystemResponse(Document responsePayload) throws Exception {
        NodeList objectTypes = responsePayload.getDocumentElement().getElementsByTagName("objecttypeinfo");
        int nbTypes = objectTypes == null ? 0 : objectTypes.getLength();
        String fileType = "";
        File fo1 = null;
        File fo2 = null;
        File fo3 = null;
        FileWriter out1 = null;
        OutputStreamWriter out2 = null;
        OutputStreamWriter out3 = null;
        String fileName = String.valueOf(SceGlobals.synGlobalCodesOutDir) + "/" + fileType + ".psa";
        try {
            fileName = String.valueOf(SceGlobals.synGlobalCodesOutDir) + "/" + fileType + "equipot.psa";
            fo1 = new File(fileName);
            out1 = new FileWriter(fo1);
            out1.write("*DEBUT*\n");
            fileName = String.valueOf(SceGlobals.synGlobalCodesOutDir) + "/" + fileType + "listapp.psa";
            fo2 = new File(fileName);
            out2 = new FileWriter(fo2);
            fileName = String.valueOf(SceGlobals.synGlobalCodesOutDir) + "/" + fileType + "apppsa.psa";
            fo3 = new File(fileName);
            out3 = new FileWriter(fo3);
        }
        catch (IOException e) {
            System.out.println("IO-Error: " + e.getMessage());
        }
        catch (Exception e) {
            System.out.println("Cannot open file '" + fileName + "'\n" + e.getMessage());
        }
        String c1 = "";
        String c2 = "";
        String c3 = "";
        int i = 0;
        while (i < nbTypes) {
            Element type = (Element)objectTypes.item(i);
            String objTypeName = type.getAttribute("name");
            fileType = "";
            if (objTypeName.equals("NETCONDUCTOR")) {
                fileType = "equipot";
            }
            if (objTypeName.equals("DEVICE")) {
                fileType = "listapp";
            }
            if (objTypeName.equals("GROUND") || objTypeName.equals("SPLICE") || objTypeName.equals("CONNECTOR")) {
                fileType = "apppsa";
            }
            if (!fileType.equals("")) {
                NodeList elems = type.getElementsByTagName("nametemplate");
                int nbElems = elems == null ? 0 : elems.getLength();
                int j = 0;
                while (j < nbElems) {
                    Element name = (Element)elems.item(j);
                    c1 = name.getAttribute("name");
                    c2 = name.getAttribute("description");
                    c3 = name.getAttribute("shortdescription");
                    try {
                        if (fileType.equals("equipot")) {
                            out1.write(String.valueOf(AddPeriodicProcess.fillValue(c1, 5)) + " " + AddPeriodicProcess.fillValue(c3, 25) + " " + AddPeriodicProcess.fillValue(c2, 50) + "\n");
                        }
                        if (fileType.equals("listapp")) {
                            out2.write(String.valueOf(AddPeriodicProcess.fillValue(c1, 5)) + "      " + AddPeriodicProcess.fillValue(c2, 61) + "  " + AddPeriodicProcess.fillValue(c3, 40) + "\n");
                        }
                        if (fileType.equals("apppsa")) {
                            out3.write(String.valueOf(AddPeriodicProcess.fillValue(c1, 5)) + "      " + AddPeriodicProcess.fillValue(c2, 61) + "  " + AddPeriodicProcess.fillValue(c3, 40) + "\n");
                        }
                    }
                    catch (IOException e) {
                        System.out.println("IO-Error: " + e.getMessage());
                    }
                    catch (Exception e) {
                        System.out.println("Cannot write in file '" + fileType + ".psa'\n" + e.getMessage());
                    }
                    ++j;
                }
                elems = type.getElementsByTagName("propertytemplate");
                nbElems = elems == null ? 0 : elems.getLength();
                j = 0;
                while (j < nbElems) {
                    Element element = (Element)elems.item(j);
                    ++j;
                }
            }
            ++i;
        }
        out1.close();
        out2.close();
        out3.close();
    }

    public void processExportSystemOptionsResponse(Document responsePayload) throws Exception {
        NodeList options = responsePayload.getDocumentElement().getElementsByTagName("option");
        int nbOptions = options == null ? 0 : options.getLength();
        File fo1 = null;
        FileWriter out1 = null;
        String fileName = String.valueOf(SceGlobals.synGlobalCodesOutDir) + "/" + "psaoptions.TXT";
        try {
            fo1 = new File(fileName);
            out1 = new FileWriter(fo1);
        }
        catch (IOException e) {
            System.out.println("IO-Error: " + e.getMessage());
        }
        catch (Exception e) {
            System.out.println("Cannot open file '" + fileName + "'\n" + e.getMessage());
        }
        int i = 0;
        while (i < nbOptions) {
            Element type = (Element)options.item(i);
            String objTypeName = type.getAttribute("name");
            String objTypeDesc = type.getAttribute("description");
            try {
                out1.write(String.valueOf(AddPeriodicProcess.fillValue(objTypeName, 5)) + " " + AddPeriodicProcess.fillValue(objTypeDesc, 30) + "\n");
            }
            catch (IOException e) {
                System.out.println("IO-Error: " + e.getMessage());
            }
            catch (Exception e) {
                System.out.println("Cannot write in file '" + fileName + ".psa'\n" + e.getMessage());
            }
            ++i;
        }
        out1.close();
    }

    private static String fillValue(String c, int length) {
        String blanks = "                                                                      ";
        String result = c;
        if (c.length() > length) {
            result = c.substring(0, length);
        }
        if (c.length() < length) {
            result = String.valueOf(c.substring(0, c.length())) + blanks.substring(0, length - c.length());
        }
        return result;
    }

    private static boolean isAnyFault(SOAPMessage response) {
        try {
            if (response == null || response.getSOAPBody().hasFault()) {
                return true;
            }
        }
        catch (SOAPException sOAPException) {
            // empty catch block
        }
        return false;
    }

    private static String getFaultString(SOAPMessage responseSOAPMessage) {
        if (responseSOAPMessage == null) {
            return "Response is blank or NULL";
        }
        try {
            return responseSOAPMessage.getSOAPBody().getFault().getFaultString();
        }
        catch (SOAPException ex) {
            return "SOAPBody is NULL or empty";
        }
    }

    private Document formatSystemRequest() {
        Document doc = this._docBuilder.newDocument();
        Element root = doc.createElement("project");
        doc.appendChild(root);
        return doc;
    }

    public static void msgBox(String msg) {
        JFrame frame = null;
        frame = new JFrame();
        frame.show();
        frame.toFront();
        JOptionPane.showMessageDialog(frame, msg, "", 0);
        frame.dispose();
    }
}

