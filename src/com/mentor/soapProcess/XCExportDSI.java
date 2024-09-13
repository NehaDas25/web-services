package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.MessageFactory
 *  javax.xml.soap.SOAPMessage
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XCExportDSI
extends NotifyService {
    public static final String USERNAMEFIELD = "! *            User:     ";
    public static final String LINESEPERATOR_COMMENT = "! *";
    private String projectId;
    private String designId;
    String endPointBridgeOutXml = String.valueOf(CIS_SVG_SERVICE_URL) + "BridgeOutDesign";
    private boolean hasDesignOptions;
    protected static final String COMMENT = "!";
    protected static final String SECTION = "%";
    protected static final String LINE_SEPERATOR_STAR = "*";
    protected static final String DELIMITER = ":";
    protected static final String COMMA = ",";
    protected static final String UTF8 = "UTF8";
    protected static final String CAPH_ENCODING = "ISO8859_1";
    private static final int STREAM_BYTES = 5000;
    private HashMap<String, String> terminalMaterialCodesMap;
    private String author;
    private String threadId;

    public XCExportDSI(String projectId, String designId, boolean hasDesignOptions, HashMap<String, String> terminalMaterialCodesMap, XCExportDesignXml instanceDesignXml, XCExportFSCReports xcTaskInstance, SceGlobals sceGlobalsInstance, String author, HashMap<String, String> allHarnessDesignWeights, String threadId) throws Exception {
        boolean delete;
        this.projectId = projectId;
        this.designId = designId;
        this.hasDesignOptions = hasDesignOptions;
        this.terminalMaterialCodesMap = terminalMaterialCodesMap;
        this.author = author;
        this.threadId = threadId;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document requestDesignXMLPayloadDoc = builder.newDocument();
        Element desXMLRoot = requestDesignXMLPayloadDoc.createElement("wiringdesign");
        desXMLRoot.setAttribute("id", designId);
        requestDesignXMLPayloadDoc.appendChild(desXMLRoot);
        SOAPMessage reqDesignXMLMsg = WebServerServlet.SOAP_MESSAGE_FACTORY.createMessage();
        XCExportDSI.insertSOAPAuthentication(reqDesignXMLMsg);
        XCExportDSI.insertInputXMLToSOAPBody(requestDesignXMLPayloadDoc, reqDesignXMLMsg.getSOAPPart(), reqDesignXMLMsg);
        this.pMsgThread("Requesting BridgeOutDesignXML for " + sceGlobalsInstance.getDesName());
        SOAPMessage respMsg = XCExportDSI.sendSOAPRequest(reqDesignXMLMsg, this.endPointBridgeOutXml, CIS_SVG_SERVICE_TIMEOUT);
        if (XCExportDSI.isAnyFault(respMsg)) {
            String fault = XCExportDSI.getFaultString(respMsg);
            this.pMsgThread("SOAP Fault = " + fault);
            this.pMsgThread("Aborting Release Level Change service after requesting XML data for " + sceGlobalsInstance.getDesName());
            throw new Exception("SOAP Fault = " + fault);
        }
        String outputFolder = sceGlobalsInstance.getFscvalidOutputFolderPath();
        String xmlFilePath = String.valueOf(outputFolder) + "BridgeOutDesign_" + sceGlobalsInstance.getDesName() + ".xml";
        String outputDsifileName = sceGlobalsInstance.getDesName();
        String dsiOutFilePath = String.valueOf(outputFolder) + sceGlobalsInstance.getDesName() + ".dsi";
        Document bridgeOutXml = SOAPUtils.getDocFromSoapMessage(respMsg);
        this.insertAllHarnessWeight(bridgeOutXml, allHarnessDesignWeights);
        WebServiceUtils.writeDOMDocumentToFile(bridgeOutXml, xmlFilePath);
        this.pMsgThread("Wrote file: " + xmlFilePath);
        this.runBatchAdaptorBridge(xmlFilePath, dsiOutFilePath, outputFolder, outputDsifileName);
        File xmlFile = new File(xmlFilePath);
        if (xmlFile.exists() && !(delete = xmlFile.delete())) {
            throw new Exception("Could not delete artifact: " + xmlFilePath);
        }
        XCExportDSI.fixDsiIssue(dsiOutFilePath, terminalMaterialCodesMap, instanceDesignXml, xcTaskInstance, author);
        DSIHarnessMainNodeComponentsModifier dsiHarnessMainNodeComponentsModifier = new DSIHarnessMainNodeComponentsModifier(dsiOutFilePath);
        dsiHarnessMainNodeComponentsModifier.modifyDSIHarnessMainNodeComponents();
    }

    public void pMsgThread(String msg) {
        System.out.println(String.valueOf(this.threadId) + ": " + msg);
    }

    /*
     * Unable to fully structure code
     */
    private static void fixDsiIssue(String dsiOutFilePath, HashMap<String, String> argTerminalMaterialCodesMap, XCExportDesignXml instanceDesignXml, XCExportFSCReports xcTaskInstance, String userName) throws Exception {
        String cr = new String(new char[]{'\r'});
        String lf = new String(new char[]{'\n'});
        FileInputStream ins = new FileInputStream(dsiOutFilePath);
        String temporaryDsiWithFixes = String.valueOf(dsiOutFilePath) + "fixed";
        FileOutputStream ops = new FileOutputStream(temporaryDsiWithFixes);
        InputStreamReader reader = new InputStreamReader(ins, "ISO8859_1");
        LineNumberReader lineReader = new LineNumberReader(reader);
        HashMap<String, String> spliceMap = new HashMap<>();
        HashMap<String, String> termMap = new HashMap<>();
        String prevLine = "";
        int sectionCounter = 1;
        String line = lineReader.readLine();
        boolean userNameSet = false;

        while (line != null) {
            if (line.startsWith("%")) {
                ++sectionCounter;
            } else if (!line.startsWith("!")) {
                String[] lineArray = line.split(":");
                if (sectionCounter == 6) {
                    if (XCExportDSI.isSpliceLine(lineArray)) {
                        spliceMap.put(lineArray[0], line);
                    }
                    if (XCExportDSI.isTerminalLine(lineArray)) {
                        termMap.put(lineArray[0], line);
                    }
                }
            }
            line = lineReader.readLine();
        }
        lineReader.close();
        reader.close();
        ins.close();

        ins = new FileInputStream(dsiOutFilePath);
        reader = new InputStreamReader(ins, "ISO8859_1");
        lineReader = new LineNumberReader(reader);
        sectionCounter = 1;
        userNameSet = false;
        line = lineReader.readLine();

        while (line != null) {
            if (line.startsWith("%")) {
                ++sectionCounter;
                if (!prevLine.equals("!")) {
                    line = "!" + cr + lf + line;
                    prevLine = "!";
                }
            } else if (!line.startsWith("!")) {
                String[] lineArray = line.split(":", -1);
                if (sectionCounter == 6) {
                    String objectName = lineArray[0];
                    String cavityName = lineArray[1];
                    String terminalKey = String.valueOf(objectName) + ":" + cavityName;
                    String objectPartNumber = lineArray[8];
                    if (XCExportDSI.isTerminalLine(lineArray)) {
                        if (!spliceMap.containsKey(objectName) && !objectPartNumber.trim().isEmpty()) {
                            if (argTerminalMaterialCodesMap.containsKey(terminalKey)) {
                                lineArray[18] = argTerminalMaterialCodesMap.get(terminalKey);
                                line = null;
                                for (int i = 0; i < lineArray.length; i++) {
                                    line = line == null ? lineArray[i] : String.valueOf(line) + ":" + lineArray[i];
                                }
                            }
                        }
                    }
                } else if (sectionCounter == 10 && xcTaskInstance != null && xcTaskInstance.isCompositeDesign()) {
                    String fmCode = lineArray[0];
                    Element hrDesign = xcTaskInstance.getFunctionalHarnessDesignElementByFMCode(fmCode);
                    if (hrDesign != null) {
                        lineArray[2] = XCExportDSI.cutString(xcTaskInstance.getCustomerHarnessNo(hrDesign), 30);
                        lineArray[3] = XCExportDSI.cutString(xcTaskInstance.getCustomerIssue(hrDesign), 30);
                        lineArray[4] = xcTaskInstance.getCustomerDate(hrDesign);
                        lineArray[7] = XCExportDSI.cutString(xcTaskInstance.getInternalHarnessNo(hrDesign), 30);
                        lineArray[8] = XCExportDSI.cutString(xcTaskInstance.getInternalIssue(hrDesign), 30);
                        lineArray[9] = xcTaskInstance.getInternalDate(hrDesign);
                        lineArray[12] = XCExportDSI.cutString(xcTaskInstance.getCustomerName(hrDesign), 30);
                        lineArray[13] = XCExportDSI.cutString(xcTaskInstance.getInternalManufacturingSite(hrDesign), 30);
                        lineArray[18] = XCExportDSI.cutString(xcTaskInstance.getWeight(hrDesign, fmCode), 30);
                        lineArray[19] = XCExportDSI.cutString(xcTaskInstance.getUserField2(hrDesign), 30);
                        lineArray[20] = XCExportDSI.cutString(xcTaskInstance.getUserField3(hrDesign), 30);
                        lineArray[21] = XCExportDSI.cutString(xcTaskInstance.getUserField4(hrDesign), 30);
                        lineArray[22] = XCExportDSI.cutString(xcTaskInstance.getUserField5(hrDesign), 30);
                        lineArray[23] = XCExportDSI.cutString(xcTaskInstance.getPropertyChecker(hrDesign), 20);
                        lineArray[31] = XCExportDSI.cutString(xcTaskInstance.getTrim(hrDesign), 10);
                        lineArray[32] = xcTaskInstance.getLRHand(hrDesign);
                        lineArray[33] = xcTaskInstance.getTubeAddOnType(hrDesign);
                        lineArray[34] = xcTaskInstance.getTubeAddOnFactor(hrDesign);
                        lineArray[35] = xcTaskInstance.getManualTerminal(hrDesign);
                        lineArray[36] = xcTaskInstance.getSealedConnectors(hrDesign);
                        line = null;
                        for (int i = 0; i < lineArray.length; i++) {
                            line = line == null ? lineArray[i] : String.valueOf(line) + ":" + lineArray[i];
                        }
                    } else {
                        XCExportDSI.pMsg("Error: No harness child design found for fmCode: " + fmCode + " This is a design setup error and a functional module design with this module code must be created.");
                    }
                } else if (sectionCounter == 11 && XCExportDSI.removeInclusiveFMCodesHavingMultipleRows(instanceDesignXml, xcTaskInstance, lineArray)) {
                    // Do nothing
                }
            } else if (line.startsWith("!") && !line.contains("! *") && !line.equals("!")) {
                if (!(prevLine.equalsIgnoreCase("!") || prevLine.contains("*") || prevLine.contains("%"))) {
                    line = "!" + cr + lf + line;
                }
                if (line.equals("!None")) {
                    line = "! None";
                }
            }
            if (!userNameSet && line.contains("! *            User:     ")) {
                userNameSet = true;
                line = "! *            User:     " + userName;
            }
            ops.write((String.valueOf(line) + cr + lf).toString().getBytes("UTF8"));
            prevLine = line;
            line = lineReader.readLine();
        }
        lineReader.close();
        reader.close();
        ins.close();
        ops.close();

        File oriDsiFile = new File(dsiOutFilePath);
        if (oriDsiFile.exists()) {
            oriDsiFile.delete();
            File file = new File(temporaryDsiWithFixes);
            file.renameTo(oriDsiFile);
        }
    }

    private static boolean removeInclusiveFMCodesHavingMultipleRows(XCExportDesignXml instanceDesignXml, XCExportFSCReports xcTaskInstance, String[] lineArray) {
        if (xcTaskInstance != null && xcTaskInstance.isCompositeDesign() && lineArray[2].equals("C")) {
            String mainFMCode = lineArray[0];
            return xcTaskInstance.getUnjustifiedModuleCodeList().contains(mainFMCode);
        }
        return false;
    }

    private static String cutString(String s, int i) {
        if (s.length() > i) {
            return s.substring(0, i);
        }
        return s;
    }

    private static boolean isSpliceLine(String[] lineArray) {
        int fieldWithObjectType = 5;
        String fieldValueWithObjectType = lineArray[fieldWithObjectType - 1];
        return "SPLICE".equalsIgnoreCase(fieldValueWithObjectType);
    }

    private static boolean isTerminalLine(String[] lineArray) {
        int fieldWithObjectType = 5;
        String fieldValueWithObjectType = lineArray[fieldWithObjectType - 1];
        return "TERM".equalsIgnoreCase(fieldValueWithObjectType);
    }

    private static void pMsg(String msg) {
        System.out.println(msg);
    }

    private static void dMsg(String msg) {
        System.out.println(msg);
    }

    private void insertAllHarnessWeight(Document bridgeOutXml, HashMap<String, String> allHarnessDesignWeights) throws Exception {
        Node nodeOptionset;
        NodeList nodeListOptionSet;
        this.pMsgThread("Taking over harness weight");
        String attributeUserField1 = "userField1";
        String harnessAttr_PartNumber = "partnumber";
        String harnessAttr_Name = "displayName";
        String queryElementName_harness = "harness";
        NodeList queryResultElement_harness = bridgeOutXml.getDocumentElement().getElementsByTagName(queryElementName_harness);
        if (queryResultElement_harness.getLength() > 1) {
            this.pMsgThread("More than one element harness in BridgeOutDesignXml. DSI Process aborted!");
        } else {
            Node harnessNode = queryResultElement_harness.item(0);
            short nodeType = harnessNode.getNodeType();
            if (1 == nodeType) {
                try {
                    Element elemHarness = (Element)harnessNode;
                    String designPartNumber = elemHarness.getAttribute(harnessAttr_PartNumber);
                    String designName = elemHarness.getAttribute(harnessAttr_Name);
                    String hashMapKey = String.valueOf(designPartNumber) + XCExportDesignXml.MAPSEPARATOR + designName;
                    if (allHarnessDesignWeights.containsKey(hashMapKey)) {
                        Object harnessWeight = allHarnessDesignWeights.get(hashMapKey);
                        harnessWeight = String.format("%.03f", Double.parseDouble((String)harnessWeight) / 1000.0).replace(',', '.');
                        elemHarness.setAttribute(attributeUserField1, (String)harnessWeight);
                    } else {
                        this.pMsgThread("Design partnumber and design name key " + hashMapKey + " not found in hashmap!");
                    }
                }
                catch (Exception e) {
                    this.pMsgThread("Harness weight format conversion failed!");
                }
            }
        }
        String derivativeModuleAttr_PartNumber = "partnumber";
        String derivativeModuleAttr_Name = "name";
        String queryElementName_derivative = "derivative";
        NodeList queryResultElement_derivative = bridgeOutXml.getDocumentElement().getElementsByTagName(queryElementName_derivative);
        ArrayList<Node> allDerivativeAndModuleNodes = new ArrayList<Node>();
        int i = 0;
        while (i < queryResultElement_derivative.getLength()) {
            allDerivativeAndModuleNodes.add(queryResultElement_derivative.item(i));
            ++i;
        }
        for (Node nodeDerivativOrModule : allDerivativeAndModuleNodes) {
            short nodeType = nodeDerivativOrModule.getNodeType();
            if (1 != nodeType) continue;
            try {
                Element elemDerivativeOrModule = (Element)nodeDerivativOrModule;
                String designPartNumber = elemDerivativeOrModule.getAttribute(derivativeModuleAttr_PartNumber);
                String designName = elemDerivativeOrModule.getAttribute(derivativeModuleAttr_Name);
                String hashMapKey = String.valueOf(designPartNumber) + XCExportDesignXml.MAPSEPARATOR + designName;
                if (allHarnessDesignWeights.containsKey(hashMapKey)) {
                    String harnessWeight = allHarnessDesignWeights.get(hashMapKey);
                    harnessWeight = String.format("%.03f", Double.parseDouble(harnessWeight) / 1000.0).replace(',', '.');
                    elemDerivativeOrModule.setAttribute(attributeUserField1, harnessWeight);
                    continue;
                }
                this.pMsgThread("Design partnumber and design name key not found in hashmap!");
            }
            catch (Exception e) {
                this.pMsgThread("Harness weight format conversion failed!");
            }
        }
        if (!this.hasDesignOptions && (nodeListOptionSet = bridgeOutXml.getDocumentElement().getElementsByTagName("optionset")).getLength() > 0 && 1 == (nodeOptionset = nodeListOptionSet.item(0)).getNodeType()) {
            Element elemOptionSet = (Element)nodeOptionset;
            NodeList childNodes = elemOptionSet.getChildNodes();
            int i2 = 0;
            while (i2 < childNodes.getLength()) {
                Node optionNode = childNodes.item(i2);
                if (1 == optionNode.getNodeType()) {
                    Element elemOption = (Element)optionNode;
                    elemOptionSet.removeChild(optionNode);
                }
                ++i2;
            }
        }
    }

    public void runBatchAdaptorBridge(String xmlFilePath, String dsiOutFile, String outputFolder, String outputDsifileName) throws Exception {
        String userName = System.getProperty("user.name");
        String threadIdFolder = "tempAdapterBatchLog" + this.threadId.replaceAll(" ", "");
        String batchAdaptorLogFileName = "CapitalAdaptorBatch_" + userName + ".log";
        String batchAdaptorOutputFolder = String.valueOf(outputFolder) + "/" + threadIdFolder;
        File fileBatchAdaptorOutputFolder = new File(batchAdaptorOutputFolder);
        String batchAdaptorLogFileString = String.valueOf(batchAdaptorOutputFolder) + "/" + batchAdaptorLogFileName;
        File batchAdaptorLogFile = new File(batchAdaptorLogFileString);
        boolean mkdir = fileBatchAdaptorOutputFolder.mkdir();
        if (!mkdir) {
            throw new Exception("Could not create sub folder " + fileBatchAdaptorOutputFolder.getAbsolutePath());
        }
        ArrayList<String> cmd = new ArrayList<String>();
        String workingDir = Paths.get(SceGlobals.resourcePath, "scripts").toString();;
        //workingDir = SceGlobals.resourcePath.contains("/bin") ? SceGlobals.resourcePath.replaceAll("/bin", "/bat") : SceGlobals.resourcePath.trim();
        if (workingDir.startsWith("/")) {
            workingDir = workingDir.substring(1);
        }
        cmd.clear();
        cmd.add(workingDir + "XML2DSI.bat");
        cmd.add(xmlFilePath);
        cmd.add(dsiOutFile);
        ProcessBuilder processBuilder = new ProcessBuilder(cmd);
        this.pMsgThread(((Object)cmd).toString());
        Map<String, String> procEnv = processBuilder.environment();
        if (batchAdaptorOutputFolder.endsWith("/")) {
            batchAdaptorOutputFolder = batchAdaptorOutputFolder.substring(0, batchAdaptorOutputFolder.length() - 1);
        }
        procEnv.put("CAPITAL_TEMP", batchAdaptorOutputFolder);
        processBuilder.redirectErrorStream(true);
        processBuilder.directory(new File(workingDir));
        Process dsiProcess = processBuilder.start();
        dsiProcess.waitFor();
        boolean batchFileError = false;
        String errorLine = "";
        if (batchAdaptorLogFile.exists()) {
            FileReader fr = new FileReader(batchAdaptorLogFile);
            BufferedReader br = new BufferedReader(fr);
            ArrayList<String> fileContent = new ArrayList<String>();
            String line = null;
            while ((line = br.readLine()) != null) {
                fileContent.add(line);
                if (!line.toLowerCase().contains("error")) continue;
                errorLine = line;
                batchFileError = true;
            }
            br.close();
            fr.close();
            if (batchFileError) {
                for (String lineOfFile : fileContent) {
                    this.pMsgThread(lineOfFile);
                }
                throw new Exception(errorLine);
            }
            if (batchAdaptorLogFile.exists()) {
                boolean bl = batchAdaptorLogFile.delete();
            }
        } else {
            this.pMsgThread("Could not find log file: " + batchAdaptorLogFileString);
        }
        if (fileBatchAdaptorOutputFolder.exists()) {
            boolean bl = fileBatchAdaptorOutputFolder.delete();
        }
    }

    public static void main(String[] args) {
        try {
            SceGlobals sceGlobals = new SceGlobals();
            WebServerServlet.SOAP_MESSAGE_FACTORY = MessageFactory.newInstance();
            XCExportDesignXml.standAloneTestXml = "C:\\psafscvalid\\system\\FromServerWithProblemsxml\\75.K0ELEC.Z9S.9834733280.A-.FSC-HABITACLE-------.-----------.xml";
            XCExportDesignXml designXml = new XCExportDesignXml("_1", "HAB1_A-PRSEL2_07/2020", "A", "FSC HABITACLE", "_218", true, sceGlobals, "dummyThreadId1");
            String dsiOutFile = "C:\\psafscvalid\\system\\input.dsi";
            String string = "xxxxxx";
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

