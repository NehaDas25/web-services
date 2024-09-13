package com.mentor.soapProcess;

import com.mentor.mcd.DMSEncrypter;
import com.mentor.mcd.MCDDMSException;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.encoding.ser.JAFDataHandlerDeserializerFactory;
import org.apache.axis.encoding.ser.JAFDataHandlerSerializerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.*;
import com.mentor.taskProcessing.BackgroundTaskProcessing;
import com.mentor.taskProcessing.IBackgroundTaskProcessing;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.swing.*;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;
import javax.xml.soap.*;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class NotifyService {
    public static final String CONST_FIELD_G_IN_FILE_NAME = "-----------";
    private static String borderName = "";
    public static String[] CHS_AUTHENTICATION_VALUES = new String[]{"", ""};
    public static String CIS_SVG_SERVICE_URL = "";
    protected static int CIS_SVG_SERVICE_TIMEOUT = 1800000;
    public static String HARNESS_RELEASE_LEVEL_NAME = "";
    private static final float TIFF_RESOLUTION_DPI = 200.0f;
    private static final String SVG_TEMP_FILE_BW = "__Tmp_SVG_BW__.svg";
    private static BufferedImage tmpImg;
    private static final String CMPT_SERVICE_DESCRIPTION_APPENDER = " >>> Validated by WebService : ";
    private static final String CMPT_SERVICE_DESCRIPTION_ATTR = "specification";
    private static final String FORMAT_ARGS_GENERIC = "generic";
    private static final String URL_Web_SERVICE_PLM = "";
    private static final String PDF = ".pdf";
    private static final String XML = ".xml";
    private static final String TIFF = ".TIFF";
    private static final String PDF_FILE = "PDF";
    private static final String XML_FILE = "XML";
    private static final String TIFF_FILE = "TIFF";
    private static String PDF_PRINT_BY_REGION;
    private static String pathForProcessing;
    private static String scriptForProcessing;
    private static FileWriter fileWriter;
    static File fo;
    static FileWriter out;
    static Document responsePayload1;
    private static IBackgroundTaskProcessing taskProcessing = BackgroundTaskProcessing.getInstance();

    static {
        PDF_PRINT_BY_REGION = URL_Web_SERVICE_PLM;
        fo = null;
        out = null;
        responsePayload1 = null;
    }

    public static void timeDiff(String msg) {
    }



    public static void processNotifyService(Document xmlDoc, String threadId) throws Exception {
        block69: {
            class NotifyThreadPrint {
                private String ntpid;

                NotifyThreadPrint(String ntpid) {
                    this.ntpid = ntpid;
                }

                void pMsgThread(String msg) {
                    System.out.println(String.valueOf(this.ntpid) + ": " + msg);
                }
            }
            NotifyThreadPrint ntp = new NotifyThreadPrint(threadId);
            SceGlobals sceGlobalsInstance = new SceGlobals();
            SceGlobals.startTime = System.currentTimeMillis();
            try {
                SceGlobals.memOut = new FileWriter(SceGlobals.memFo, true);
            }
            catch (IOException e) {
                ntp.pMsgThread("IO-Error: " + e.getMessage());
            }
            catch (Exception e) {
                ntp.pMsgThread("Cannot open file '" + SceGlobals.memFo + "'\n" + e.getMessage());
            }
            SceGlobals.r = Runtime.getRuntime();
            tmpImg = null;
            NotifyService.readChsCustProps();
            NotifyService.timeDiff("After readChsCustProps");
            try {
                Element root = xmlDoc.getDocumentElement();
                if (root.getTagName().equals("wiringdesign")) {
                    if (root.getAttribute("oldstatus").equals(URL_Web_SERVICE_PLM)) break block69;
                    ntp.pMsgThread("****************************************************************");
                    ntp.pMsgThread("* Notify " + SceGlobals.progVers);
                    ntp.pMsgThread("*");
                    if (SceGlobals.debug) {
                        ntp.pMsgThread("Received Request DOM....");
                        ntp.pMsgThread("............................................");
                        ntp.pMsgThread("............................................");
                    }
                    String designId = root.getAttribute("id");
                    String designName = root.getAttribute("name");
                    String designRevision = root.getAttribute("revision");
                    String designShortDesc = root.getAttribute("shortdesc");
                    String designType = root.getAttribute("designtype");
                    String designOldStatus = root.getAttribute("oldstatus");
                    String designNewStatus = root.getAttribute("releasestatus");
                    String projectName = root.getAttribute("projectname");
                    String projectId = root.getAttribute("projectid");
                    String author = root.getAttribute("author");
                    ntp.pMsgThread("Author:                   " + author);
                    ntp.pMsgThread("Design id:                " + designId);
                    ntp.pMsgThread("Design name:              " + designName);
                    ntp.pMsgThread("Design revision:          " + designRevision);
                    ntp.pMsgThread("Design short description: " + designShortDesc);
                    ntp.pMsgThread("Design type:              " + designType);
                    ntp.pMsgThread("Design release status:    " + designNewStatus);
                    ntp.pMsgThread("Design old status:        " + designOldStatus);
                    ntp.pMsgThread("Project name: " + projectName);
                    ntp.pMsgThread("Project id:   " + projectId);
                    String endPoint = String.valueOf(CIS_SVG_SERVICE_URL) + "SVGDiagrams";
                    String endPointDesXML = String.valueOf(CIS_SVG_SERVICE_URL) + "ExportProjectDesign";
                    if (designType.equals("logicdesign")) {
                        File fileCisSvgFolder;
                        sceGlobalsInstance.setNotivySvgTiffOutputFolderPath(author);
                        sceGlobalsInstance.setCisSvgOutputFolderPath(author);
                        File fileSvgTiffFolder = new File(sceGlobalsInstance.getNotivySvgTiffOutputFolderPath());
                        if (!fileSvgTiffFolder.exists()) {
                            fileSvgTiffFolder.mkdirs();
                        }
                        if (!(fileCisSvgFolder = new File(sceGlobalsInstance.getCisSvgOutputFolderPath())).exists()) {
                            fileCisSvgFolder.mkdirs();
                        }
                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder builder = factory.newDocumentBuilder();
                        Document requestPayloadSvgDoc = builder.newDocument();
                        factory = DocumentBuilderFactory.newInstance();
                        builder = factory.newDocumentBuilder();
                        Document requestDesignXMLPayloadDoc = builder.newDocument();
                        Element svgRoot = requestPayloadSvgDoc.createElement("wiringdesign");
                        svgRoot.setAttribute("id", designId);
                        svgRoot.setAttribute("projectid", projectId);
                        requestPayloadSvgDoc.appendChild(svgRoot);
                        Element elemDiagram = requestPayloadSvgDoc.createElement("diagram");
                        elemDiagram.setAttribute("dpi", "200");
                        elemDiagram.setAttribute("generateLegacySVG", "TRUE");
                        String diagramName = URL_Web_SERVICE_PLM;
                        elemDiagram.setAttribute("outputContextualInformation", "FALSE");
                        svgRoot.appendChild(elemDiagram);
                        Element desXMLRoot = requestDesignXMLPayloadDoc.createElement("wiringdesign");
                        desXMLRoot.setAttribute("id", designId);
                        desXMLRoot.setAttribute("projectid", projectId);
                        requestDesignXMLPayloadDoc.appendChild(desXMLRoot);
                        SOAPMessage reqMsg = WebServerServlet.SOAP_MESSAGE_FACTORY.createMessage();
                        NotifyService.insertSOAPAuthentication(reqMsg);
                        NotifyService.insertInputXMLToSOAPBody(requestPayloadSvgDoc, reqMsg.getSOAPPart(), reqMsg);
                        SOAPMessage reqDesignXMLMsg = WebServerServlet.SOAP_MESSAGE_FACTORY.createMessage();
                        NotifyService.insertSOAPAuthentication(reqDesignXMLMsg);
                        NotifyService.insertInputXMLToSOAPBody(requestDesignXMLPayloadDoc, reqDesignXMLMsg.getSOAPPart(), reqDesignXMLMsg);
                        QueryReleaseLevel qRelLev = new QueryReleaseLevel();
                        String level = qRelLev.query(designNewStatus, projectId);
                        NotifyService.timeDiff("After QueryReleaseLevel");
                        if (level.equals("released") && (designNewStatus.equals("SDP - Officiel") || designNewStatus.equals("Officiel"))) {
                            String fault;
                            D_QuerySingleDesignPart aSSPT = new D_QuerySingleDesignPart(designName, designRevision);
                            if (SceGlobals.DESIGN_NAME_CHECK.toUpperCase().equals("YES") && aSSPT.getSCEPartNo().equals(URL_Web_SERVICE_PLM)) {
                                String msg = "Design " + designName + " with revision " + designRevision + " is not a SCE part." + " Aborting Release Level Change service.";
                                ntp.pMsgThread(msg);
                                return;
                            }
                            ntp.pMsgThread("Design status " + designNewStatus + " is a release level.");
                            ntp.pMsgThread("Requesting SVG...");
                            SOAPMessage respMsg = NotifyService.sendSOAPRequest(reqMsg, endPoint, CIS_SVG_SERVICE_TIMEOUT);
                            ntp.pMsgThread("Requesting XML...");
                            SOAPMessage respDesXMLMsg = NotifyService.sendSOAPRequest(reqDesignXMLMsg, endPointDesXML, CIS_SVG_SERVICE_TIMEOUT);
                            ntp.pMsgThread("After requesting SVG/XML");
                            sceGlobalsInstance.setDesName(URL_Web_SERVICE_PLM);
                            if (NotifyService.isAnyFault(respMsg)) {
                                fault = NotifyService.getFaultString(respMsg);
                                ntp.pMsgThread("SOAP Fault = " + fault);
                                ntp.pMsgThread("Aborting Release Level Change service after requesting SVG data.");
                            } else if (NotifyService.isAnyFault(respDesXMLMsg)) {
                                fault = NotifyService.getFaultString(respDesXMLMsg);
                                ntp.pMsgThread("SOAP Fault = " + fault);
                                ntp.pMsgThread("Aborting Release Level Change service after requesting XML data.");
                            } else {
                                Document responseDesXMLDOM = SOAPUtils.getDocFromSoapMessage(respDesXMLMsg);
                                ntp.pMsgThread("Requesting design XML ... " + designName + " " + designRevision);
                                boolean res = NotifyService.processDesXML(responseDesXMLDOM, designName, designRevision, designShortDesc, CONST_FIELD_G_IN_FILE_NAME, true, null, sceGlobalsInstance, threadId);
                                if (res) {
                                    Element desXmlRoot = responseDesXMLDOM.getDocumentElement();
                                    factory = DocumentBuilderFactory.newInstance();
                                    builder = factory.newDocumentBuilder();
                                    Document requestDesignPdfPayloadDoc = builder.newDocument();
                                    Node importNode = requestDesignPdfPayloadDoc.importNode(desXmlRoot, true);
                                    requestDesignPdfPayloadDoc.appendChild(importNode);
                                    DOMSource source = new DOMSource(requestDesignPdfPayloadDoc);
                                    FileOutputStream stream = new FileOutputStream(String.valueOf(sceGlobalsInstance.getNotivySvgTiffOutputFolderPath()) + sceGlobalsInstance.getDesName() + XML);
                                    StreamResult result = new StreamResult(stream);
                                    WebServerServlet.XML_TRANSFORMER.transform(source, result);
                                    stream.flush();
                                    stream.close();
                                    ArrayList<String> borderNames = new ArrayList<String>();
                                    Element projectRoot = requestDesignPdfPayloadDoc.getDocumentElement();
                                    if (projectRoot.getNodeName().equals("project")) {
                                        int i = 0;
                                        while (i < projectRoot.getChildNodes().getLength()) {
                                            Node designmgr = projectRoot.getChildNodes().item(i);
                                            if (designmgr.getNodeName().equals("designmgr")) {
                                                int j = 0;
                                                while (j < designmgr.getChildNodes().getLength()) {
                                                    Node logicaldesign = designmgr.getChildNodes().item(j);
                                                    if (logicaldesign.getNodeName().equals("logicaldesign")) {
                                                        int n = 0;
                                                        while (n < logicaldesign.getChildNodes().getLength()) {
                                                            Node diagram = logicaldesign.getChildNodes().item(n);
                                                            if (diagram.getNodeName().equals("diagram")) {
                                                                int h = 0;
                                                                while (h < diagram.getChildNodes().getLength()) {
                                                                    Node diagramcontent = diagram.getChildNodes().item(h);
                                                                    if (diagramcontent.getNodeName().equals("diagramcontent")) {
                                                                        int k = 0;
                                                                        while (k < diagramcontent.getChildNodes().getLength()) {
                                                                            Node border = diagramcontent.getChildNodes().item(k);
                                                                            if (border.getNodeName().equals("border")) {
                                                                                int l = 0;
                                                                                while (l < border.getChildNodes().getLength()) {
                                                                                    Node properties = border.getChildNodes().item(l);
                                                                                    if (properties.getNodeName().equals("properties")) {
                                                                                        int m = 0;
                                                                                        while (m < properties.getChildNodes().getLength()) {
                                                                                            Node property = properties.getChildNodes().item(m);
                                                                                            if (property.getNodeName().equals("property") && property.getAttributes().getNamedItem("name").getNodeValue().equals("Border_name")) {
                                                                                                borderNames.add(property.getAttributes().getNamedItem("val").getNodeValue());
                                                                                            }
                                                                                            ++m;
                                                                                        }
                                                                                    }
                                                                                    ++l;
                                                                                }
                                                                            }
                                                                            ++k;
                                                                        }
                                                                    }
                                                                    ++h;
                                                                }
                                                            }
                                                            ++n;
                                                        }
                                                    }
                                                    ++j;
                                                }
                                            }
                                            ++i;
                                        }
                                    }
                                    borderName = URL_Web_SERVICE_PLM;
                                    if (borderNames.size() > 0) {
                                        if (borderNames.get(0).toString().length() > 2) {
                                            borderName = borderNames.get(0).toString().substring(0, 2);
                                        } else if (borderNames.get(0).toString().length() == 0) {
                                            borderName = SceGlobals.Default_Border_name;
                                            System.out.println("Border_name property for the border has empty value.");
                                        } else {
                                            borderName = borderNames.get(0).toString();
                                        }
                                    } else {
                                        borderName = SceGlobals.Default_Border_name;
                                        ntp.pMsgThread("Border_name property for the border doesnot exist.");
                                    }
                                    SOAPMessage reqDesignPdfMsg = WebServerServlet.SOAP_MESSAGE_FACTORY.createMessage();
                                    NotifyService.insertSOAPAuthentication(reqDesignPdfMsg);
                                    NotifyService.insertInputXMLToSOAPBody(requestDesignPdfPayloadDoc, reqDesignPdfMsg.getSOAPPart(), reqDesignPdfMsg);
                                    String endPointDesPdf = String.valueOf(SceGlobals.PSA_URL) + "ffePdfReport";
                                    SOAPMessage respDesPdfMsg = NotifyService.sendSOAPRequest(reqDesignPdfMsg, endPointDesPdf, SceGlobals.PSA_TIMEOUT);
                                    if (NotifyService.isAnyFault(respDesPdfMsg)) {
                                        String fault2 = NotifyService.getFaultString(respDesPdfMsg);
                                        ntp.pMsgThread("SOAP Fault = " + fault2);
                                        ntp.pMsgThread("Aborting Release Level Change service.");
                                    } else {
                                        Document responseDesPdfDOM = SOAPUtils.getDocFromSoapMessage(respDesPdfMsg);
                                        Element el1 = responseDesPdfDOM.getDocumentElement();
                                        String reportUrl = URL_Web_SERVICE_PLM;
                                        NodeList domData = el1.getElementsByTagName("reporturl");
                                        int i = 0;
                                        while (i < domData.getLength()) {
                                            Node n = domData.item(i);
                                            if (n.getNodeName().compareTo("reporturl") == 0) {
                                                reportUrl = n.getFirstChild().getNodeValue();
                                            }
                                            ++i;
                                        }
                                        if (reportUrl.equals(URL_Web_SERVICE_PLM)) {
                                            String fault3 = NotifyService.getFaultString(respDesPdfMsg);
                                            ntp.pMsgThread("SOAP Fault: Could not get report url = " + fault3);
                                        }
                                        ntp.pMsgThread("FEE report from URL " + reportUrl);
                                        String toPath = String.valueOf(sceGlobalsInstance.getNotivySvgTiffOutputFolderPath()) + sceGlobalsInstance.getDesName() + PDF;
                                        ntp.pMsgThread("Copy report to      \n" + toPath);
                                        NotifyService.copyFile(reportUrl, toPath);
                                        if (SceGlobals.debug && !SceGlobals.d_AcrobatPath.equals(URL_Web_SERVICE_PLM)) {
                                            try {
                                                Process n = Runtime.getRuntime().exec(String.valueOf(SceGlobals.d_AcrobatPath) + " " + toPath + " &");
                                            }
                                            catch (IOException e1) {
                                                System.err.println(e1);
                                                System.exit(1);
                                            }
                                        }
                                    }
                                    Document responseDOM = SOAPUtils.getDocFromSoapMessage(respMsg);
                                    NotifyService.processSVG(responseDOM, sceGlobalsInstance.getDesName(), false, sceGlobalsInstance);
                                }
                            }
                            String checkName = designName.substring(0, 3);
                            if (checkName.equals("DOC")) {
                                ntp.pMsgThread("designName ==> " + designName);
                                String sdpNameToPost = designName.replaceFirst("DOC", "DOC-");
                                String sdpRevision = designRevision;
                                String pathOfAttachedFiles = URL_Web_SERVICE_PLM;
                                pathOfAttachedFiles = SceGlobals.NOTIFY_SVG_TIF_PATH;
                                String attachedFileNamePdf = sceGlobalsInstance.getDesName();
                                String attachedFileNameXml = sceGlobalsInstance.getDesName();
                                String attachedFileNameTiff = sceGlobalsInstance.getDesName().replace(CONST_FIELD_G_IN_FILE_NAME, "--" + SceGlobals.TIFF_Name_Constant + borderName + "----");
                                String[] argsNameRevision = new String[]{sdpNameToPost, sdpRevision};
                                String[] argsFormat = null;
                                String[] argsAttachedFileName = null;
                                String[] argsDataHandler = null;
                                ArrayList<String> listOfFiles = new ArrayList<String>();
                                File filePdf = new File(String.valueOf(pathOfAttachedFiles) + attachedFileNamePdf + PDF);
                                File fileXml = new File(String.valueOf(pathOfAttachedFiles) + attachedFileNameXml + XML);
                                File fileTiff = new File(String.valueOf(pathOfAttachedFiles) + attachedFileNameTiff + TIFF);
                                NotifyService.validateFileSize(fileTiff);
                                int cptTestFile = 0;
                                if (!(filePdf.exists() && fileXml.exists() && fileTiff.exists())) {
                                    ntp.pMsgThread("wait 5 S ......");
                                    Thread.sleep(5000L);
                                    while (cptTestFile < 2) {
                                        if (filePdf.exists() && fileXml.exists() && fileTiff.exists()) break;
                                        ntp.pMsgThread("Encore wait 5 S ......");
                                        Thread.sleep(5000L);
                                        ++cptTestFile;
                                    }
                                }
                                ntp.pMsgThread("Exit of waiting ....");
                                if (filePdf.exists()) {
                                    listOfFiles.add(PDF_FILE);
                                    ntp.pMsgThread("PDF File found and added to the list");
                                } else {
                                    ntp.pMsgThread("PDF File not found ");
                                }
                                if (fileXml.exists()) {
                                    ntp.pMsgThread("XML File found and added to the list");
                                    listOfFiles.add(XML_FILE);
                                } else {
                                    ntp.pMsgThread("XML File not found ");
                                }
                                if (fileTiff.exists()) {
                                    ntp.pMsgThread("TIFF File found and added to the list ");
                                    listOfFiles.add(TIFF_FILE);
                                } else {
                                    ntp.pMsgThread("TIFF File not found ");
                                }
                                int size = listOfFiles.size();
                                argsFormat = new String[size];
                                int i = 0;
                                while (i < size) {
                                    argsFormat[i] = FORMAT_ARGS_GENERIC;
                                    ++i;
                                }
                                argsAttachedFileName = new String[size];
                                argsDataHandler = new String[size];
                                i = 0;
                                while (i < size) {
                                    if (((String)listOfFiles.get(i)).equals(PDF_FILE)) {
                                        argsAttachedFileName[i] = filePdf.getName();
                                        argsDataHandler[i] = filePdf.getName();
                                    }
                                    if (((String)listOfFiles.get(i)).equals(XML_FILE)) {
                                        argsAttachedFileName[i] = fileXml.getName();
                                        argsDataHandler[i] = fileXml.getName();
                                    }
                                    if (((String)listOfFiles.get(i)).equals(TIFF_FILE)) {
                                        argsAttachedFileName[i] = fileTiff.getName();
                                        argsDataHandler[i] = fileTiff.getName();
                                    }
                                    ++i;
                                }
                                URL url = new URL(SceGlobals.URL_Web_SERVICE_PLM);
                                NotifyService.checkInSDP(url, argsNameRevision, argsFormat, argsAttachedFileName, argsDataHandler, pathOfAttachedFiles);
                            }
                            break block69;
                        }
                        ntp.pMsgThread("Design status " + designNewStatus + " is not a release level.");
                        break block69;
                    }
                    if (designType.equals("harnessdesign")) {
                        if (HARNESS_RELEASE_LEVEL_NAME.equals(designNewStatus)) {
                            boolean createdFolders;
                            sceGlobalsInstance.setFscvalidOutputFolderPath(author);
                            String fscvalidOutputFolderPath = sceGlobalsInstance.getFscvalidOutputFolderPath();
                            File fileFscvalidOutputFolderPath = new File(fscvalidOutputFolderPath);
                            if (!fileFscvalidOutputFolderPath.exists() && !(createdFolders = fileFscvalidOutputFolderPath.mkdirs())) {
                                System.err.println("Could not create folder structure " + fileFscvalidOutputFolderPath.getAbsolutePath() + ". Aborting FSCVALID release process!");
                                return;
                            }
                            ntp.pMsgThread("Design status " + designNewStatus + " is a release level.");
                            ntp.pMsgThread("Exporting design xml without child designs");
                            XCExportDesignXml instanceDesignXml = new XCExportDesignXml(projectId, designName, designRevision, designShortDesc, designId, false, sceGlobalsInstance, threadId);
                            if (instanceDesignXml.isCompositeDesign()) {
                                ntp.pMsgThread("This is a composite design. Writing design xml file");
                                instanceDesignXml.writeDesignXml(sceGlobalsInstance);
                                instanceDesignXml.writeDesignOrProjectFmCodesXmlFile(sceGlobalsInstance);
                            } else {
                                ntp.pMsgThread("This is a child design. No design xml file will be written");
                            }
                            String outPutFileName = sceGlobalsInstance.getDesName();
                            XCExportFSCReports xcTaskInstance = new XCExportFSCReports(projectName, designId, outPutFileName, author, sceGlobalsInstance, threadId);
                            HashMap<String, String> allHarnessDesignWeights = xcTaskInstance.getAllHarnessDesignWeights();
                            boolean hasDesignOptions = xcTaskInstance.hasDesignOptions();
                            HashMap<String, String> terminalMaterialCodesMap = xcTaskInstance.getAllTerminalMaterialCodes();
                            if (PDF_PRINT_BY_REGION == null || PDF_PRINT_BY_REGION.isEmpty() || "true".equalsIgnoreCase(PDF_PRINT_BY_REGION)) {
                                new XCExportPDF(projectName, designId, outPutFileName, sceGlobalsInstance, threadId).export();
                            } else {
                                ntp.pMsgThread("PDF print by region is configured as disabled, no PDF will be generated");
                            }
                            //new XCExportDSI(projectId, designId, hasDesignOptions, terminalMaterialCodesMap, instanceDesignXml, xcTaskInstance, sceGlobalsInstance, author, allHarnessDesignWeights, threadId);
                            ntp.pMsgThread("Completed harness design release data generation for " + designName + " " + designRevision + " " + designShortDesc);
                            int statusCode = taskProcessing.postProcessingTask(pathForProcessing, scriptForProcessing);
                            if(statusCode != 0) {
                                ntp.pMsgThread("Error in post processing task for copy to shared storage!");
                            }
                        } else {
                            ntp.pMsgThread("Design status " + designNewStatus + " is not a release level.");
                        }
                    } else {
                        ntp.pMsgThread("SVG diagram generation is currently supported only for Logic designs.");
                    }
                    break block69;
                }
                ntp.pMsgThread("Invalid root element: " + root.getTagName());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void debugWriteMaterialCodeMapToFile(HashMap<String, String> terminalMaterialCodesMap, String outputFolder, String fileName) throws Exception {
        FileWriter fw = new FileWriter(String.valueOf(outputFolder) + "/" + fileName);
        BufferedWriter bw = new BufferedWriter(fw);
        TreeMap<String, String> sortedMap = new TreeMap<String, String>(terminalMaterialCodesMap);
        for (Map.Entry<String, String> entry : sortedMap.entrySet()) {
            bw.write(String.valueOf(entry.getKey()) + "=" + entry.getValue() + "\n");
        }
        bw.close();
        fw.close();
    }

    public static Document processComponentUpdateService(Document xmlDoc) {
        MessageWin mesWin;
        Document responsePayload = xmlDoc;
        Document rpDoc = null;
        String msg = URL_Web_SERVICE_PLM;
        SceGlobals.mesWin = mesWin = new MessageWin(false);
        mesWin.resetErrors();
        boolean validationError = false;
        String strPlmURL = null;
        DMSEncrypter encrypter = null;
        try {
            encrypter = new DMSEncrypter();
        }
        catch (MCDDMSException e) {
            System.err.println("Can't create encrypter.");
            e.printStackTrace();
        }
        try {
            FileInputStream propInFile = new FileInputStream(SceGlobals.propFilePath);
            Properties p2 = new Properties();
            p2.load(propInFile);
            SceGlobals.CustomU = p2.getProperty("CustomU");
            try {
                SceGlobals.CustomP = encrypter.decryptString(p2.getProperty("CustomP"));
                SceGlobals.ChsP = encrypter.decryptString(p2.getProperty("ChsP"));
            }
            catch (MCDDMSException e) {
                System.err.println("Can't decrypt.");
                e.printStackTrace();
            }
            SceGlobals.CustomS = p2.getProperty("CustomS");
            SceGlobals.ChsU = p2.getProperty("ChsU");
            SceGlobals.ChsS = p2.getProperty("ChsS");
            SceGlobals.SyncModeActive = p2.getProperty("SyncModeActive");
            SceGlobals.debug = p2.getProperty("DEBUG").equals("true");
            strPlmURL = p2.getProperty("PLM_URL");
            propInFile.close();
        }
        catch (FileNotFoundException e) {
            msg = "Can't find " + SceGlobals.propFilePath;
            System.err.println(msg);
            mesWin.addMess("\n" + msg, "error");
        }
        catch (IOException e) {
            msg = "I/O failed." + SceGlobals.propFilePath;
            System.err.println("\n" + msg);
            mesWin.addMess("\n" + msg, "error");
        }
        try {
            Element root;
            msg = "Component Update " + SceGlobals.progVers;
            if (SceGlobals.debug) {
                msg = String.valueOf(msg) + " is starting in debug mode.";
            }
            System.out.println("*\n* " + msg + "\n");
            mesWin.addMess(String.valueOf(msg) + "\n", "info");
            if (SceGlobals.debug) {
                System.out.println("Received Request DOM....");
            }
            if (SceGlobals.debug) {
                mesWin.showWin();
                try {
                    fo = new File("webServer.dbg");
                    out = new FileWriter(fo);
                    out.write("Debug File:\n");
                }
                catch (FileNotFoundException fne) {
                    System.out.println("Can't create debug file.");
                    mesWin.addMess("\nCan't create debug file.", "error");
                }
                catch (IOException ioe) {
                    System.out.println("IOException..." + ioe.toString());
                    msg = "\nIOException..." + ioe.toString();
                    mesWin.addMess(msg, "error");
                }
                mesWin.addMess(msg, "info");
            }
            if ((root = xmlDoc.getDocumentElement()).getTagName().equals("chssystem")) {
                ArrayList<Node> libObjects = new ArrayList<Node>();
                int i = 0;
                while (i < root.getChildNodes().getLength()) {
                    if (root.getChildNodes().item(i).getNodeName().endsWith("part")) {
                        libObjects.add(root.getChildNodes().item(i));
                    }
                    ++i;
                }
                int count = libObjects.size();
                NodeList suppObjects = ((Element)libObjects.get(0)).getElementsByTagName("supplierpartnumber");
                int suppcount = suppObjects.getLength();
                String libObjId = URL_Web_SERVICE_PLM;
                String groupname = URL_Web_SERVICE_PLM;
                String partno = URL_Web_SERVICE_PLM;
                String partstatus = URL_Web_SERVICE_PLM;
                String description = null;
                Node libObject = null;
                String responseMsg = URL_Web_SERVICE_PLM;
                if (count == 0) {
                    System.out.println("No error message possible because there is no library object.");
                    msg = "No error message possible because there is no library object.";
                    mesWin.addMess(msg, "error");
                } else if (count > 1) {
                    responseMsg = String.valueOf(responseMsg) + " " + "Invalid data: Multiple library objects are not allowed.";
                    msg = "\ninvalid data: Multiple library objects are not allowed.";
                    mesWin.addMess(msg, "error");
                    setTheAttribute(libObject, CMPT_SERVICE_DESCRIPTION_ATTR, responseMsg);
                } else if (count == 1) {
                    int i2 = 0;
                    while (i2 < count) {
                        libObject = (Element)libObjects.get(i2);
                        groupname = String.valueOf(getTheAttribute(libObject, "groupname"));
                        partno = String.valueOf(getTheAttribute(libObject, "partnumber"));
                        partstatus = String.valueOf(getTheAttribute(libObject, "partstatus"));
                        description = String.valueOf(getTheAttribute(libObject, CMPT_SERVICE_DESCRIPTION_ATTR));
                        System.out.println("Part No.: " + partno + " with status '" + partstatus + "'.");
                        msg = "\nPart No.: " + partno + " with status '" + partstatus + "'.";
                        mesWin.addMess(msg, "info");
                        description = URL_Web_SERVICE_PLM;
                        responseMsg = String.valueOf(responseMsg) + description;
                        libObjId = String.valueOf(getTheAttribute(libObject, "libraryobject_id"));
                        ++i2;
                    }
                    SceGlobals.libraryobject_id = libObjId;
                    NodeList nodes = libObject.getChildNodes();
                    Node beforeNode = null;
                    int i3 = 0;
                    while (i3 < nodes.getLength()) {
                        Node node = nodes.item(i3);
                        if (node.getNodeName().equals("housingdefinition") || node.getNodeName().equals("libraryconnector") || node.getNodeName().equals("librarycavity") || node.getNodeName().equals("librarydevicefootprint") || node.getNodeName().equals("librarymulticorewire") || node.getNodeName().equals("libraryassembly") || node.getNodeName().equals("librarysplice") || node.getNodeName().equals("librarygrommet") || node.getNodeName().equals("libraryheatshrink") || node.getNodeName().equals("libraryidcconnector") || node.getNodeName().equals("libraryidccavities") || node.getNodeName().equals("librarybackshellplug") || node.getNodeName().equals("librarybackshellseal") || node.getNodeName().equals("libraryclip") || node.getNodeName().equals("librarycavityplug") || node.getNodeName().equals("librarytape") || node.getNodeName().equals("librarytapeselection") || node.getNodeName().equals("librarysingletermination") || node.getNodeName().equals("librarymultipletermination") || node.getNodeName().equals("libraryweldwirespec") || node.getNodeName().equals("librarysoldersleeve") || node.getNodeName().equals("librarycavityseal") || node.getNodeName().equals("librarytube") || node.getNodeName().equals("librarydressedroute") || node.getNodeName().equals("librarysinglewirefitscavity") || node.getNodeName().equals("librarymultiplewirefitscavity") || node.getNodeName().equals("librarywire") || node.getNodeName().equals("librarywirespec")) {
                            beforeNode = node;
                            break;
                        }
                        ++i3;
                    }
                    QuerySingleSuppPart qSingleSuppPart = null;
                    String suppPartNo = URL_Web_SERVICE_PLM;
                    String prefSuppPart = URL_Web_SERVICE_PLM;
                    String prefSuppPartNo = URL_Web_SERVICE_PLM;
                    int prefCount = 0;
                    String wrongPartNo = URL_Web_SERVICE_PLM;
                    String scesep = " ";
                    System.out.println("suppcount " + suppcount);
                    SceGlobals.chsSuppPartId = null;
                    int i4 = 0;
                    while (i4 < suppcount) {
                        Element suppObject = (Element)suppObjects.item(i4);
                        if (libObjId.equals(suppObject.getAttribute("libraryobject_id"))) {
                            suppPartNo = suppObject.getAttribute("supplierpartnumber");
                            prefSuppPart = suppObject.getAttribute("preferred");
                            System.out.println("suppPartNo " + suppPartNo);
                            System.out.println("prefSuppPart " + prefSuppPart);
                            if (prefSuppPart.equals("1")) {
                                prefSuppPartNo = suppPartNo;
                                ++prefCount;
                                qSingleSuppPart = new QuerySingleSuppPart(suppPartNo);
                                if (qSingleSuppPart.getSCEPartNo().equals(suppPartNo)) {
                                    System.out.println("Found Part No. " + suppPartNo + " in SCE database. ");
                                    msg = "\nFound Part No. " + suppPartNo + " in SCE database. ";
                                    mesWin.addMess(msg, "info");
                                    SceGlobals.chsSuppPartId = suppPartNo;
                                } else {
                                    wrongPartNo = String.valueOf(wrongPartNo) + scesep + suppPartNo;
                                    scesep = ", ";
                                    System.out.println("Error: Part No. " + suppPartNo + " not in SCE database. ");
                                    msg = "\nError: Part No. " + suppPartNo + " not in SCE database. ";
                                    mesWin.addMess(msg, "error");
                                }
                            }
                        }
                        ++i4;
                    }
                    if (!wrongPartNo.equals(URL_Web_SERVICE_PLM)) {
                        responseMsg = responseMsg.concat("Error: Part No. " + wrongPartNo + " not in SCE database. ");
                        msg = "\nError: Part No. " + wrongPartNo + " not in SCE database. ";
                        mesWin.addMess(msg, "error");
                        setTheAttribute(libObject, CMPT_SERVICE_DESCRIPTION_ATTR, responseMsg);
                    }
                    String DESCRIPTION = String.valueOf(getTheAttribute(libObject, "description"));
                    String USERF1 = String.valueOf(getTheAttribute(libObject, "userf1"));
                    String USERF2 = String.valueOf(getTheAttribute(libObject, "userf2"));
                    String USERF3 = String.valueOf(getTheAttribute(libObject, "userf3"));
                    String USERF4 = String.valueOf(getTheAttribute(libObject, "userf4"));
                    String pressedButton = root.getAttribute("ButtonPressed");
                    boolean scePartOK = false;
                    if (prefCount == 1) {
                        QuerySingleSuppPart scePart = new QuerySingleSuppPart(prefSuppPartNo);
                        scePartOK = scePart.checkSCEPartNo(prefSuppPartNo, DESCRIPTION, USERF1, USERF2, USERF3, USERF4);
                        System.out.println("scePartOK " + scePartOK + " " + pressedButton);
                        msg = "\nSCE Part OK: " + scePartOK;
                        if (scePart.getSCEPartNo().equals(prefSuppPartNo)) {
                            String[] sceData = scePart.getSCEData();
                            setTheAttribute(libObject, "description", sceData[1]);
                            setTheAttribute(libObject, "userf1", String.valueOf(sceData[2]) + "." + sceData[3]);
                            setTheAttribute(libObject, "userf2", String.valueOf(sceData[0]) + "." + sceData[4]);
                            setTheAttribute(libObject, "userf3", sceData[5]);
                            setTheAttribute(libObject, "userf4", sceData[6]);
                        }
                    }
                    if (pressedButton.equals("Apply")) {
                        if (SceGlobals.debug) {
                            SceGlobals.startTime = System.currentTimeMillis();
                            msg = "\n*** Start validation after pressing Apply button. Group name is: " + groupname;
                            mesWin.addMess(msg, URL_Web_SERVICE_PLM);
                        }
                        if (groupname.equals("Device") && partstatus.equals("Current")) {
                            if (!NotifyService.deviceValidation((Element)libObject, mesWin, responseMsg, root, libObjId)) {
                                validationError = true;
                            }
                            if (validationError) {
                                setTheAttribute(libObject, "partstatus", "New");
                                msg = "\nStatus 'Current' change to status 'New'.";
                                System.out.println(msg);
                                mesWin.addMess(msg, "error");
                                mesWin.showWin();
                                mesWin.toFront();
                            }
                        }
                        if (SceGlobals.debug) {
                            long duration = System.currentTimeMillis() - SceGlobals.startTime;
                            msg = "\n*** Duration: " + duration + " ms";
                            mesWin.addMess(msg, URL_Web_SERVICE_PLM);
                        }
                    } else {
                        int validateOrBrowser = 1;
                        Object[] options = new Object[]{"Validation", "SCE Browser ..."};
                        JFrame frame = null;
                        System.out.println("Groupe name is " + groupname + ".");
                        if (groupname.equals("Device")) {
                            validateOrBrowser = 1;
                            frame = new JFrame();
                            frame.show();
                            frame.toFront();
                            validateOrBrowser = JOptionPane.showOptionDialog(frame, "Preferred Supplier Part Validation?\nor  \nSupplier Part Selection via SCE Browser?\n\n", "Validation or SCE Browser?", 1, 3, null, options, options[0]);
                            System.out.println("responseMsg = " + responseMsg);
                            frame.dispose();
                            if (SceGlobals.debug) {
                                SceGlobals.startTime = System.currentTimeMillis();
                                msg = "\n*** Start device validation after Preferred Supplier Part Validation dialog: ";
                                mesWin.addMess(msg, URL_Web_SERVICE_PLM);
                            }
                            if (validateOrBrowser == 0) {
                                boolean bl = NotifyService.deviceValidation((Element)libObject, mesWin, responseMsg, root, libObjId);
                            } else {
                                mesWin.addMess("No validation!", URL_Web_SERVICE_PLM);
                            }
                            if (SceGlobals.debug) {
                                long duration = System.currentTimeMillis();
                                msg = "\n*** Duration of validation: " + duration + " ms";
                                mesWin.addMess(msg, URL_Web_SERVICE_PLM);
                            }
                        }
                        if (validateOrBrowser == 1) {
                            SceQuery sceBrowser = null;
                            sceBrowser = prefCount == 1 ? new SceQuery(prefSuppPartNo, URL_Web_SERVICE_PLM) : new SceQuery(URL_Web_SERVICE_PLM, URL_Web_SERVICE_PLM);
                            sceBrowser.setUndecorated(false);
                            sceBrowser.setDefaultCloseOperation(2);
                            sceBrowser.toFront();
                            sceBrowser.show();
                            SceGlobals.finished = false;
                            SceGlobals.sce_number = URL_Web_SERVICE_PLM;
                            SceGlobals.description = URL_Web_SERVICE_PLM;
                            SceGlobals.userf1 = URL_Web_SERVICE_PLM;
                            SceGlobals.userf2 = URL_Web_SERVICE_PLM;
                            SceGlobals.userf3 = URL_Web_SERVICE_PLM;
                            SceGlobals.userf4 = URL_Web_SERVICE_PLM;
                            SceGlobals.sceSelected = false;
                            while (!SceGlobals.finished) {
                                try {
                                    Thread.sleep(5L);
                                }
                                catch (InterruptedException e) {
                                    System.out.println("InterruptException after wait  " + e);
                                    msg = "InterruptException after wait  " + e;
                                    mesWin.addMess(msg, "error");
                                }
                            }
                            if (!SceGlobals.sce_number.equals(URL_Web_SERVICE_PLM)) {
                                SceGlobals.chsSuppPartId = suppPartNo;
                                String chsSuppPartId = URL_Web_SERVICE_PLM;
                                Element sce_numberExists = null;
                                Element suppObject = null;
                                if (suppcount > 0) {
                                    int i5 = 0;
                                    while (i5 < suppcount) {
                                        suppObject = (Element)suppObjects.item(i5);
                                        if (libObjId.equals(suppObject.getAttribute("libraryobject_id")) && SceGlobals.chsSuppPartId.equals(chsSuppPartId = suppObject.getAttribute("supplierpartnumber_id"))) {
                                            sce_numberExists = suppObject;
                                            sce_numberExists.setAttribute("supplierorganisation_id", SceGlobals.suppSelected);
                                            System.out.println("Found Supplier Part No. ");
                                            break;
                                        }
                                        ++i5;
                                    }
                                }
                                if (SceGlobals.sceSelected && prefCount == 0) {
                                    setTheAttribute(libObject, "description", SceGlobals.description);
                                    setTheAttribute(libObject, "userf1", SceGlobals.userf1);
                                    setTheAttribute(libObject, "userf2", SceGlobals.userf2);
                                    setTheAttribute(libObject, "userf3", SceGlobals.userf3);
                                    setTheAttribute(libObject, "userf4", SceGlobals.userf4);
                                }
                                NodeList suppliers = xmlDoc.getElementsByTagName("supplierpartnumber");
                                int numberOfSuppliers = suppliers.getLength();
                                Element newSuppPart = null;
                                boolean supplierExistsInDocument = false;
                                int i6 = 0;
                                while (i6 < numberOfSuppliers) {
                                    String supplierNodeValue = suppliers.item(i6).getAttributes().getNamedItem("supplierpartnumber").getNodeValue();
                                    if (SceGlobals.sce_number.equals(supplierNodeValue)) {
                                        supplierExistsInDocument = true;
                                        newSuppPart = (Element)suppliers.item(i6);
                                        break;
                                    }
                                    ++i6;
                                }
                                if (sce_numberExists == null && !supplierExistsInDocument) {
                                    if (supplierExistsInDocument) {
                                        newSuppPart.setAttribute(CMPT_SERVICE_DESCRIPTION_ATTR, URL_Web_SERVICE_PLM);
                                        newSuppPart.setAttribute("supplierorganisation_id", SceGlobals.suppSelected);
                                        newSuppPart.setAttribute("supplierpartnumber", SceGlobals.sce_number);
                                        newSuppPart.setAttribute("supplierpartnumber_id", SceGlobals.chsSuppPartId);
                                    } else {
                                        newSuppPart = xmlDoc.createElement("supplierpartnumber");
                                        newSuppPart.setAttribute("libraryobject_id", libObjId);
                                        newSuppPart.setAttribute("preferred", "1");
                                        newSuppPart.setAttribute(CMPT_SERVICE_DESCRIPTION_ATTR, URL_Web_SERVICE_PLM);
                                        newSuppPart.setAttribute("supplierorganisation_id", SceGlobals.suppSelected);
                                        newSuppPart.setAttribute("supplierpartnumber", SceGlobals.sce_number);
                                        newSuppPart.setAttribute("supplierpartnumber_id", "something");
                                        if (numberOfSuppliers > 0) {
                                            newSuppPart.setAttribute("preferred", "0");
                                        } else {
                                            newSuppPart.setAttribute("preferred", "1");
                                        }
                                        if (beforeNode == null) {
                                            libObject.appendChild(newSuppPart);
                                        } else {
                                            libObject.insertBefore(newSuppPart, beforeNode);
                                        }
                                    }
                                }
                                if (SceGlobals.debug) {
                                    out.write("\n*****\n");
                                    out.write("Response XML modified by SCE Browser Session:");
                                    out.write("\n*****\n\n");
                                    out.write(libObject.getParentNode().toString());
                                    mesWin.addMess("\n*****\n", "info");
                                    mesWin.addMess("Response XML modified by SCE Browser Session:", "info");
                                    mesWin.addMess("\n*****\n\n", "info");
                                    mesWin.addMess(libObject.getParentNode().toString(), "info");
                                }
                            }
                        }
                    }
                    if (SceGlobals.debug) {
                        try {
                            out.write("\n*****\n");
                            out.write("Response XML modified by SCE Browser Session:");
                            out.write("\n*****\n\n");
                            out.close();
                        }
                        catch (FileNotFoundException fne) {
                            System.out.println("File not created...");
                            mesWin.addMess("File not created...", "error");
                        }
                        catch (IOException ioe) {
                            System.out.println("IOException..." + ioe.toString());
                            msg = "IOException..." + ioe.toString();
                            mesWin.addMess(msg, "error");
                        }
                    }
                }
            } else if (!root.getTagName().equals("librarypart")) {
                System.out.println("Invalid root element: " + root.getTagName());
                msg = "Invalid root element: " + root.getTagName();
                mesWin.addMess(msg, "error");
                try {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    rpDoc = builder.newDocument();
                    Element errorElem = rpDoc.createElement("serviceError");
                    errorElem.setAttribute("code", "Component Update Service ");
                    errorElem.setAttribute("type", msg);
                    errorElem.setAttribute("detail", "<library> is expected as root element ");
                    rpDoc.appendChild(errorElem);
                    responsePayload = rpDoc;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mesWin.disposeWithoutErrorsOrDebug();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        NotifyService.callPlmStatusChangeService(responsePayload, strPlmURL);
        return responsePayload;
    }

    private static void callPlmStatusChangeService(Document inputDocument, String strPlmURL) {
        if (inputDocument != null && !inputDocument.getDocumentElement().getTagName().equals("serviceError") && inputDocument.getDocumentElement().getTagName().equals("librarypart")) {
            try {
                NotifyService.checkForPLMServiceCall(inputDocument);
                SOAPMessage reqMsg = WebServerServlet.SOAP_MESSAGE_FACTORY.createMessage();
                NotifyService.readChsCustProps();
                NotifyService.insertSOAPAuthentication(reqMsg);
                NotifyService.insertInputXMLToSOAPBody(inputDocument, reqMsg.getSOAPPart(), reqMsg);
                if (strPlmURL != null) {
                    String endPoint = String.valueOf(strPlmURL) + "PlmChsStatusChangeService";
                    SOAPMessage sOAPMessage = NotifyService.sendSOAPRequest(reqMsg, endPoint, CIS_SVG_SERVICE_TIMEOUT);
                }
            }
            catch (SOAPException sOAPException) {
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static void checkForPLMServiceCall(Document inputDocument) throws Exception {
        NodeList nList = inputDocument.getElementsByTagName("librarypart");
        int numberOfnodes = nList.getLength();
        if (numberOfnodes > 0) {
            Node partNode = nList.item(0);
            if (partNode.getNodeType() == 1 && partNode.hasAttributes()) {
                NamedNodeMap nodeMap = partNode.getAttributes();
                String partNewStatus = nodeMap.getNamedItem("currentstatus").getNodeValue();
                String partOldStatus = nodeMap.getNamedItem("oldstatus").getNodeValue();
                if (partNewStatus != null && partOldStatus != null && partNewStatus.equals("Current") && partOldStatus.equals("New")) return;
                Exception e = new Exception(" Current status value is not present or status is not \"Current\" in the library part node !!!");
                throw e;
            }
            Exception e = new Exception(" Attributes not present for library part node!!!");
            throw e;
        }
        Exception e = new Exception(" No nodes found for librarypart node!!!");
        throw e;
    }

    static boolean deviceValidation(Element libObject, MessageWin mesWin, String responseMsg, Element root, String libObjId) {
        NodeList dfPObjects;
        responseMsg = String.valueOf(responseMsg) + CMPT_SERVICE_DESCRIPTION_APPENDER;
        libObject.setAttribute(CMPT_SERVICE_DESCRIPTION_ATTR, responseMsg);
        String msg = URL_Web_SERVICE_PLM;
        boolean result = true;
        System.out.println("*** SceGlobals.chsSuppPartId " + SceGlobals.chsSuppPartId);
        NodeList symObjects = root.getElementsByTagName("librarygraphic");
        int symcount = 0;
        Element symObject = null;
        int i = 0;
        while (i < symObjects.getLength()) {
            symObject = (Element)symObjects.item(i);
            if (symObject.getAttribute("libraryobject_id").equals(libObjId) && symObject.getAttribute("symbol_id") != URL_Web_SERVICE_PLM) {
                ++symcount;
            }
            ++i;
        }
        if (symcount == 0) {
            responseMsg = String.valueOf(responseMsg) + "Error: No symbol assigned. ";
            libObject.setAttribute(CMPT_SERVICE_DESCRIPTION_ATTR, responseMsg);
            System.out.println("Error: No symbol assigned.");
            mesWin.addMess("\nError: No symbol assigned.", "error");
            result = false;
        } else {
            System.out.println("Info: At least symbol assigned. ");
            mesWin.addMess("\nInfo: At least symbol assigned. ", "info");
        }
        NetNumbers netNos = new NetNumbers(libObjId);
        XMLOutPins xmloPins = new XMLOutPins(root);
        if (netNos.getUsageCount() == 0) {
            responseMsg = String.valueOf(responseMsg) + "No device usage defined. ";
            libObject.setAttribute(CMPT_SERVICE_DESCRIPTION_ATTR, responseMsg);
            System.out.println("No device usage defined. ");
            mesWin.addMess("\nError: No device usage defined. ", "error");
            result = false;
        }
        if ((dfPObjects = root.getElementsByTagName("librarydevicefootprint")).getLength() == 0) {
            msg = "\nThere is no footprint defined.";
            responseMsg = String.valueOf(responseMsg) + msg;
            libObject.setAttribute(CMPT_SERVICE_DESCRIPTION_ATTR, responseMsg);
            System.out.println(msg);
            mesWin.addMess(msg, "error");
            result = false;
        }
        try {
            NodeList cavityList = libObject.getElementsByTagName("librarycavity");
            ArrayList<String> contactmaterial = new ArrayList<String>();
            int i2 = 0;
            while (i2 < cavityList.getLength()) {
                if (!contactmaterial.contains(cavityList.item(i2).getAttributes().getNamedItem("contactmaterial").getNodeValue())) {
                    contactmaterial.add(cavityList.item(i2).getAttributes().getNamedItem("contactmaterial").getNodeValue());
                }
                ++i2;
            }
            ExportDevice.getDevicePart(libObject.getAttributes().getNamedItem("partnumber").getNodeValue());
            if (!ExportDevice.ignoreMaterialCheck) {
                if (ExportDevice.terminalMaterial.size() > 0) {
                    int count2 = 0;
                    int k = 0;
                    while (k < contactmaterial.size()) {
                        if (ExportDevice.terminalMaterial.contains(contactmaterial.get(k))) {
                            ++count2;
                        }
                        ++k;
                    }
                    if (count2 != contactmaterial.size()) {
                        msg = "\nWarning: The Contact Material check fail.";
                        responseMsg = String.valueOf(responseMsg) + msg;
                        libObject.setAttribute(CMPT_SERVICE_DESCRIPTION_ATTR, responseMsg);
                        System.out.println("Warning: The Contact Material check fail.");
                        mesWin.addMess("\nWarning: The Contact Material check fail.", "info");
                    }
                }
            } else {
                mesWin.addMess("\nInfo: Ignore the Contact Material check. ", "info");
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public static Document processLogicalDesignAttributesUpdateService(Document xmlDoc) {
        MessageWin mesWin;
        responsePayload1 = xmlDoc;
        SceGlobals.mesWinTitle = "Design Attributes Update";
        SceGlobals.mesWin = mesWin = new MessageWin(false);
        mesWin.resetErrors();
        String msg = URL_Web_SERVICE_PLM;
        try {
            FileInputStream propInFile = new FileInputStream(SceGlobals.propFilePath);
            Properties p2 = new Properties();
            p2.load(propInFile);
            if (p2.getProperty("DEBUG") != null) {
                SceGlobals.debug = p2.getProperty("DEBUG").equals("true");
            }
            if (p2.getProperty("SCE_RELEASE_LEVEL") != null) {
                String[] csarray = p2.getProperty("SCE_RELEASE_LEVEL").split(",");
                SceGlobals.d_SCE_controlled_status_array = new String[csarray.length];
                int i = 0;
                while (i < csarray.length) {
                    SceGlobals.d_SCE_controlled_status_array[i] = csarray[i].trim();
                    ++i;
                }
            }
            propInFile.close();
        }
        catch (FileNotFoundException e) {
            System.err.println("Can't find " + SceGlobals.propFilePath);
            msg = "\nCan't find " + SceGlobals.propFilePath;
            mesWin.addMess(msg, "error");
        }
        catch (IOException e) {
            System.err.println("I/O failed." + SceGlobals.propFilePath);
            msg = "\nI/O failed." + SceGlobals.propFilePath;
            mesWin.addMess(msg, "error");
        }
        try {
            msg = "Logical Design Attributes Update " + SceGlobals.progVers;
            if (SceGlobals.debug) {
                msg = String.valueOf(msg) + " is starting in debug mode.";
            }
            System.out.println("*\n* " + msg + "\n");
            msg = String.valueOf(msg) + "\n";
            mesWin.addMess(msg, "info");
            if (SceGlobals.debug) {
                System.out.println(String.valueOf(msg) + "\n");
                System.out.println("............................................");
                mesWin.addMess(String.valueOf(msg) + "\n", "info");
            }
            NotifyService.desAttributes(xmlDoc, true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (SceGlobals.debug) {
            try {
                fo = new File("webServer.dbg");
                out = new FileWriter(fo);
                out.write("Debug File:\n");
                mesWin.addMess("\nSent back XML: ", "info");
                mesWin.addMess(msg, "info");
                out.write(msg);
                mesWin.showWin();
            }
            catch (FileNotFoundException fne) {
                System.out.println("Can't create debug file.");
                mesWin.addMess("\nCan't create debug file.", "error");
            }
            catch (IOException ioe) {
                System.out.println("IOException..." + ioe.toString());
                msg = "\nIOException..." + ioe.toString();
                mesWin.addMess(msg, "error");
            }
            mesWin.showWin();
        }
        return responsePayload1;
    }

    static void desAttributes(Document doc, boolean isLogicDesign) {
        NotifyService.timeDiff("Started method desAttributes(doc)");
        Element root = doc.getDocumentElement();
        MessageWin mesWin = SceGlobals.mesWin;
        try {
            System.out.println(WebServiceUtils.writeDOMDocumentToString(doc));
        }
        catch (Exception e1) {
            e1.printStackTrace();
        }
        String designPartNumber = URL_Web_SERVICE_PLM;
        String designID = URL_Web_SERVICE_PLM;
        String designShortDescription = URL_Web_SERVICE_PLM;
        String designType = URL_Web_SERVICE_PLM;
        String releaselevel = URL_Web_SERVICE_PLM;
        String revision = URL_Web_SERVICE_PLM;
        String projectId = URL_Web_SERVICE_PLM;
        String harnessDesignPartNumber = URL_Web_SERVICE_PLM;
        String msg = URL_Web_SERVICE_PLM;
        Element designAttributesObject = null;
        Element object = null;
        NodeList desData = root.getElementsByTagName("designattributesdata");
        int count = desData.getLength();
        if (count != 1) {
            msg = "There is no designattributesdata element.";
            System.out.println(msg);
            mesWin.addMess(msg, "error");
            return;
        }
        designAttributesObject = (Element)desData.item(0);
        designPartNumber = designAttributesObject.getAttribute("name");
        designID = designAttributesObject.getAttribute("designid");
        designShortDescription = designAttributesObject.getAttribute("shortdescription");
        designType = designAttributesObject.getAttribute("designtype");
        if (designType.equalsIgnoreCase("harness")) {
            harnessDesignPartNumber = designAttributesObject.getAttribute("partnumber");
        }
        releaselevel = designAttributesObject.getAttribute("releaselevel");
        revision = designAttributesObject.getAttribute("revision");
        SceGlobals.projectID = projectId = designAttributesObject.getAttribute("projectid");
        if (WebServerServlet.designID == null) {
            WebServerServlet.designID = designID;
            WebServerServlet.designName1 = designPartNumber;
        }
        if (WebServerServlet.designID.equals(designID) & !WebServerServlet.designName1.equals(designPartNumber)) {
            WebServerServlet.isNewName = true;
        }
        SceGlobals.d_sce_number = designPartNumber;
        SceGlobals.d_indiceSce = revision;
        SceGlobals.d_designation20 = designShortDescription;
        NodeList propData = designAttributesObject.getElementsByTagName("property");
        Element prop = null;
        Element p1 = null;
        Element p2 = null;
        Element p3 = null;
        String p1Name = "Cader";
        String p2Name = "Decoupage";
        String p3Name = "Service";
        Element p4 = null;
        String p4Name = "PLM/SAP Description";
        String propName = URL_Web_SERVICE_PLM;
        String propValue = URL_Web_SERVICE_PLM;
        int i = 0;
        while (i < propData.getLength()) {
            prop = (Element)propData.item(i);
            propName = prop.getAttribute("name");
            propValue = prop.getAttribute("val");
            if (propName.equals(p1Name)) {
                p1 = prop;
                SceGlobals.d_codeCader = propValue;
            }
            if (propName.equals(p2Name)) {
                p2 = prop;
                SceGlobals.d_decoupage = propValue;
            }
            if (propName.equals(p3Name)) {
                p3 = prop;
                SceGlobals.d_service = propValue;
            }
            if (propName.equals(p4Name)) {
                p4 = prop;
                SceGlobals.d_designation20 = propValue;
            }
            ++i;
        }
        NodeList desContext = root.getElementsByTagName("designattributescontext");
        count = desContext.getLength();
        if (count != 1) {
            msg = "There is no designattributescontext element.";
            System.out.println(msg);
            mesWin.addMess(msg, "error");
            return;
        }
        object = (Element)desContext.item(0);
        String invocationButton = object.getAttribute("webSericeInvocationCause");
        SceGlobals.d_dialogueInvocationCause = object.getAttribute("dialogueInvocationCause");
        SceGlobals.d_sce_number = designPartNumber;
        QueryReleaseLevel qRelLev = new QueryReleaseLevel();
        String level = qRelLev.query(releaselevel, projectId);
        if (!level.equals("released") && !level.equals("obsolete")) {
            int i2 = 0;
            while (i2 < SceGlobals.d_SCE_controlled_status_array.length) {
                if (SceGlobals.d_SCE_controlled_status_array[i2].equals(releaselevel)) {
                    level = "controlled";
                    break;
                }
                ++i2;
            }
        }
        System.out.println(String.valueOf(releaselevel) + " has level " + level);
        NotifyService.timeDiff("process desAttributes till release level");
        D_QuerySingleDesignPart aSSPT = null;
        System.out.println("SCE Part " + SceGlobals.d_sce_number + " " + SceGlobals.d_indiceSce);
        String diagramName = URL_Web_SERVICE_PLM;
        try {
            if (!WebServerServlet.isNewName && isLogicDesign) {
                System.out.println("Calling Web Service ListDesignDiagrams");
                diagramName = ListDesignDiagrams.getFirstDesignDiagramName(designID, designPartNumber, projectId, URL_Web_SERVICE_PLM, revision);
                System.out.println("Completed Web Service ListDesignDiagrams");
                if (diagramName.equals(URL_Web_SERVICE_PLM) & !diagramName.equals(">1")) {
                    WebServerServlet.isEmptydesg = true;
                }
            } else {
                System.out.println("Since this is a Harness Design no call of Web Service ListDesignDiagrams");
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        NotifyService.timeDiff("process desAttributes queries");
        if (invocationButton.equals("okButton")) {
            WebServerServlet.designID = null;
            System.out.println("OK Button " + SceGlobals.d_sce_number);
            if (SceGlobals.DESIGN_NAME_CHECK.toUpperCase().equals("YES") && isLogicDesign) {
                aSSPT = new D_QuerySingleDesignPart(SceGlobals.d_sce_number, SceGlobals.d_indiceSce);
            }
            if (level.equals("released") || level.equals("controlled")) {
                if (aSSPT != null) {
                    if (aSSPT.getSCEPartNo().equals(URL_Web_SERVICE_PLM)) {
                        msg = "\nThe design name must be a SCE part number";
                        NotifyService.sendSoapFaultDAUS(doc, designAttributesObject, msg);
                        return;
                    }
                } else {
                    if (WebServerServlet.isEmptydesg & !WebServerServlet.isNewName && isLogicDesign) {
                        msg = "\nThere is no Diagram in the Design and to change the design status to controlled status the design must contain any Diagarm";
                        NotifyService.sendSoapFaultDAUS(doc, designAttributesObject, msg);
                        WebServerServlet.designID = null;
                        return;
                    }
                    if (diagramName.equals(">1") && isLogicDesign) {
                        msg = "\nMore than one diagram defined in this design.";
                        NotifyService.sendSoapFaultDAUS(doc, designAttributesObject, msg);
                        WebServerServlet.designID = null;
                        return;
                    }
                    if (diagramName.startsWith("SOAP Fault") && isLogicDesign) {
                        msg = diagramName.substring(12);
                        NotifyService.sendSoapFaultDAUS(doc, designAttributesObject, msg);
                        WebServerServlet.designID = null;
                        return;
                    }
                }
            }
            if (SceGlobals.d_dialogueInvocationCause.equals("revise") && aSSPT != null && aSSPT.getSCEPartNo().equals(URL_Web_SERVICE_PLM)) {
                msg = "\nThe design name must be a SCE part number for create revision action.";
                NotifyService.sendSoapFaultDAUS(doc, designAttributesObject, msg);
                return;
            }
            if (level.equals("obsolete")) {
                return;
            }
        }
        if (invocationButton.equals("explicitButton")) {
            if (SceGlobals.d_dialogueInvocationCause.equals("evaluate")) {
                msg = "SCE Design Data browser won't be activated for a design evaluation action.";
                System.out.println(msg);
                return;
            }
            if (level.equals("released") && SceGlobals.d_dialogueInvocationCause.equals("edit")) {
                msg = "\nSCE Design Data browser won't be activated if the design release level is released.";
                NotifyService.sendSoapFaultDAUS(doc, designAttributesObject, msg);
                return;
            }
            if (level.equals("obsolete") && SceGlobals.d_dialogueInvocationCause.equals("edit")) {
                msg = "\nSCE Design Data browser won't be activated if the design release level is obsolete.";
                NotifyService.sendSoapFaultDAUS(doc, designAttributesObject, msg);
                return;
            }
            D_SceQuery d_sceBrowser = null;
            if (!isLogicDesign) {
                NodeList derivatives = root.getElementsByTagName("derivativeattributesdata");
                NodeList functionalModules = root.getElementsByTagName("functionalmoduleattributesdata");
                NodeList productionModules = root.getElementsByTagName("productionmoduleattributesdata");
                if ((derivatives.getLength() > 0 || functionalModules.getLength() > 0 || productionModules.getLength() > 0) && (SceGlobals.d_dialogueInvocationCause.equalsIgnoreCase("copy") || SceGlobals.d_dialogueInvocationCause.equalsIgnoreCase("revise")) && invocationButton.equalsIgnoreCase("explicitButton")) {
                    D_MultiSceQuery d_multiSceQuery = null;
                    if (derivatives.getLength() > 0) {
                        d_multiSceQuery = new D_MultiSceQuery(harnessDesignPartNumber, doc, "derivativeattributesdata", designType);
                    } else if (functionalModules.getLength() > 0) {
                        d_multiSceQuery = new D_MultiSceQuery(harnessDesignPartNumber, doc, "functionalmoduleattributesdata", designType);
                    } else if (productionModules.getLength() > 0) {
                        d_multiSceQuery = new D_MultiSceQuery(harnessDesignPartNumber, doc, "productionmoduleattributesdata", designType);
                    }
                    SceGlobals.d_finished = false;
                    SceGlobals.d_sceCanceled = false;
                    d_multiSceQuery.setUndecorated(false);
                    d_multiSceQuery.setDefaultCloseOperation(2);
                    d_multiSceQuery.show();
                    d_multiSceQuery.toFront();
                    while (!SceGlobals.d_finished) {
                        try {
                            Thread.sleep(5L);
                        }
                        catch (InterruptedException e) {
                            System.out.println("InterruptException after wait  " + e);
                            msg = "InterruptException after wait  " + e;
                            mesWin.addMess(msg, "error");
                        }
                    }
                    return;
                }
                d_sceBrowser = new D_SceQuery(harnessDesignPartNumber, designType);
            } else {
                d_sceBrowser = designPartNumber.startsWith("Design") || designPartNumber.startsWith("IntegratorDesign") || designPartNumber.startsWith("HarnessDesign") || NotifyService.isTempDesignPartNumber(designPartNumber) ? new D_SceQuery(URL_Web_SERVICE_PLM, designType) : new D_SceQuery(designPartNumber, designType);
            }
            SceGlobals.d_finished = false;
            SceGlobals.d_sceCanceled = false;
            d_sceBrowser.setUndecorated(false);
            d_sceBrowser.setDefaultCloseOperation(2);
            d_sceBrowser.toFront();
            d_sceBrowser.show();
            while (!SceGlobals.d_finished) {
                try {
                    Thread.sleep(5L);
                }
                catch (InterruptedException e) {
                    System.out.println("InterruptException after wait  " + e);
                    msg = "InterruptException after wait  " + e;
                    mesWin.addMess(msg, "error");
                }
            }
            if (SceGlobals.d_sceCanceled) {
                SceGlobals.d_sce_number = designPartNumber;
                if (level.equals("controlled")) {
                    msg = "\nDesign in SCE controlled status. Design data not saved.";
                }
                if (SceGlobals.d_dialogueInvocationCause.equals("revise")) {
                    msg = "\nCreate design revision. SCE part selection required. Design data not saved.";
                }
                if (level.equals("controlled") || SceGlobals.d_dialogueInvocationCause.equals("revise")) {
                    NotifyService.sendSoapFaultDAUS(doc, designAttributesObject, msg);
                    return;
                }
            } else if (SceGlobals.d_sce_number.equals(URL_Web_SERVICE_PLM)) {
                System.out.println("No SCE design Part number selected.");
                SceGlobals.d_sce_number = designPartNumber;
            } else {
                Element newProp;
                designAttributesObject.setAttribute("revision", SceGlobals.d_indiceSce);
                if (SceGlobals.d_designType.equals("harness")) {
                    designAttributesObject.setAttribute("partnumber", SceGlobals.d_sce_number);
                } else {
                    designAttributesObject.setAttribute("name", SceGlobals.d_sce_number);
                    designAttributesObject.setAttribute("shortdescription", SceGlobals.d_designation20);
                }
                if (p1 != null) {
                    p1.setAttribute("val", SceGlobals.d_codeCader);
                } else {
                    newProp = doc.createElementNS("http://www.mentor.com/harness/Schema/bridgesdesignattributes", "property");
                    newProp.setAttribute("name", p1Name);
                    newProp.setAttribute("stability", "editable");
                    newProp.setAttribute("type", "String");
                    newProp.setAttribute("val", SceGlobals.d_codeCader);
                    designAttributesObject.appendChild(newProp);
                }
                if (p2 != null) {
                    p2.setAttribute("val", SceGlobals.d_decoupage);
                } else {
                    newProp = doc.createElementNS("http://www.mentor.com/harness/Schema/bridgesdesignattributes", "property");
                    newProp.setAttribute("name", p2Name);
                    newProp.setAttribute("stability", "editable");
                    newProp.setAttribute("type", "String");
                    newProp.setAttribute("val", SceGlobals.d_decoupage);
                    designAttributesObject.appendChild(newProp);
                }
                if (p3 != null) {
                    p3.setAttribute("val", SceGlobals.d_service);
                } else {
                    newProp = doc.createElementNS("http://www.mentor.com/harness/Schema/bridgesdesignattributes", "property");
                    newProp.setAttribute("name", p3Name);
                    newProp.setAttribute("stability", "editable");
                    newProp.setAttribute("type", "String");
                    newProp.setAttribute("val", SceGlobals.d_service);
                    designAttributesObject.appendChild(newProp);
                }
                if (p4 != null) {
                    p4.setAttribute("val", SceGlobals.d_designation20);
                } else {
                    newProp = doc.createElementNS("http://www.mentor.com/harness/Schema/bridgesdesignattributes", "property");
                    newProp.setAttribute("name", p4Name);
                    newProp.setAttribute("stability", "editable");
                    newProp.setAttribute("type", "String");
                    newProp.setAttribute("val", SceGlobals.d_designation20);
                    designAttributesObject.appendChild(newProp);
                }
            }
        }
        boolean releaseDesignNameOk = true;
        if (SceGlobals.DESIGN_NAME_CHECK.toUpperCase().equals("YES") && isLogicDesign) {
            aSSPT = new D_QuerySingleDesignPart(SceGlobals.d_sce_number, SceGlobals.d_indiceSce);
        }
        msg = URL_Web_SERVICE_PLM;
        if (aSSPT != null) {
            if (aSSPT.getSCEPartNo().equals(URL_Web_SERVICE_PLM)) {
                releaseDesignNameOk = false;
                if (!NotifyService.isTempDesignPartNumber(SceGlobals.d_sce_number)) {
                    String str1 = "\nThe design name must be a SCE part number or a TEMP design name.";
                    if (level.equals("released")) {
                        str1 = "The design name must be a SCE part number.\n";
                    }
                    msg = "Wrong design name " + SceGlobals.d_sce_number + " " + SceGlobals.d_indiceSce + "\n" + str1 + "\nUse the SCE Design Data Browser via DesignAttributesUpdate button.";
                } else {
                    msg = "Wrong Design Name " + SceGlobals.d_sce_number + " with revision " + SceGlobals.d_indiceSce + "\n" + "The design name must be a SCE part number.\n" + "Use the SCE Design Data Browser via DesignAttributesUpdate button.";
                }
            } else if (!aSSPT.checkSCEPartNo(SceGlobals.d_sce_number, SceGlobals.d_indiceSce, SceGlobals.d_service, SceGlobals.d_codeCader, SceGlobals.d_decoupage, SceGlobals.d_designation20)) {
                msg = SceGlobals.d_partDataMismatch;
                System.out.println(msg);
                mesWin.addMess("Error: " + msg, "error");
                releaseDesignNameOk = false;
            }
        }
        if (invocationButton.equals("okButton") && (level.equals("released") || level.equals("obsolete")) && !releaseDesignNameOk) {
            System.out.println("okButton and released or obsolete");
            System.out.println(msg);
            NotifyService.msgBox(msg);
            NotifyService.sendSoapFaultDAUS(doc, designAttributesObject, String.valueOf(msg) + " Wrong design name. ");
            return;
        }
        if (level.equals("controlled") && !releaseDesignNameOk) {
            System.out.println(msg);
            NotifyService.msgBox(msg);
            mesWin.addMess("Error: " + msg, "error");
            NotifyService.sendSoapFaultDAUS(doc, designAttributesObject, String.valueOf(msg) + " Design not sync with SCE. ");
            return;
        }
    }

    public static void sendSoapFaultDAUS(Document doc, Element designAttributesObject, String msg) {
        try {
            Element errorElem = doc.createElement("serviceError");
            errorElem.setAttribute("code", "Design Attrributes Update Service ");
            errorElem.setAttribute("type", msg);
            errorElem.setAttribute("detail", String.valueOf(SceGlobals.d_sce_number) + " " + SceGlobals.d_indiceSce);
            designAttributesObject.appendChild(errorElem);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void msgBox(String msg) {
        JFrame frame = null;
        frame = new JFrame();
        frame.show();
        frame.toFront();
        JOptionPane.showMessageDialog(frame, msg, URL_Web_SERVICE_PLM, 0);
        frame.dispose();
    }

    public static boolean isTempDesignPartNumber(String desPartNo) {
        if ((desPartNo.startsWith("D") || desPartNo.startsWith("T")) && desPartNo.length() == 9) {
            String num = desPartNo.substring(1, 9);
            try {
                Integer n = Integer.parseInt(num);
            }
            catch (NumberFormatException ne) {
                return false;
            }
            return true;
        }
        return false;
    }

    public static Document processComponentStatusChangeService(Document xmlDoc) {
        MessageWin mesWin;
        Document responsePayload = xmlDoc;
        SceGlobals.mesWin = mesWin = new MessageWin(false);
        String msg = URL_Web_SERVICE_PLM;
        try {
            System.out.println("Component Status Change " + SceGlobals.progVers + "\n");
            msg = String.valueOf(SceGlobals.progVers) + "\n";
            mesWin.addMess(msg, "info");
            System.out.println("Received Request DOM....");
            System.out.println("............................................");
            System.out.println(".....The Component Status Change service does nothing.............");
            System.out.println(msg);
            System.out.println("............................................");
            mesWin.addMess(msg, "info");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (SceGlobals.debug) {
            mesWin.showWin();
            try {
                fo = new File("webServer.dbg");
                out = new FileWriter(fo);
                out.write("Debug File:\n");
            }
            catch (FileNotFoundException fne) {
                System.out.println("Can't create debug file.");
                mesWin.addMess("\nCan't create debug file.", "error");
            }
            catch (IOException ioe) {
                System.out.println("IOException..." + ioe.toString());
                msg = "\nIOException..." + ioe.toString();
                mesWin.addMess(msg, "error");
            }
            mesWin.addMess(msg, "info");
        }
        return responsePayload;
    }

    public static void readChsCustProps() throws Exception {
        try {
            Integer in;
            DMSEncrypter encrypter = null;
            encrypter = new DMSEncrypter();
            System.out.println("Attempting to read properties file at: " + SceGlobals.propFilePath);
            FileInputStream propInFile = new FileInputStream(SceGlobals.propFilePath);
            Properties p2 = new Properties();
            p2.load(propInFile);
            NotifyService.loadChsConnectorMasterProperties(p2);
            if(isCapitalXMode()) {
                pathForProcessing = taskProcessing.getOutputPath(p2.getProperty("CIS_SVG_OUTPUT"), p2.getProperty("CIS_OUTPUT_STORE"));
                scriptForProcessing = p2.getProperty("SCRIPT_PATH");
                SceGlobals.CIS_SVG_OUTPUT = pathForProcessing;
                SceGlobals.NOTIFY_SVG_TIF_PATH = pathForProcessing;
                SceGlobals.FSCVALID_OUTPUT_FOLDER = pathForProcessing;
            }
            else{
                SceGlobals.CIS_SVG_OUTPUT = p2.getProperty("CIS_SVG_OUTPUT");
                SceGlobals.NOTIFY_SVG_TIF_PATH = p2.getProperty("NOTIFY_SVG_TIF_PATH") != null ? p2.getProperty("NOTIFY_SVG_TIF_PATH") : p2.getProperty("CIS_SVG_OUTPUT");
                SceGlobals.FSCVALID_OUTPUT_FOLDER = p2.getProperty("FSCVALID_OUTPUT_FOLDER") != null ? p2.getProperty("FSCVALID_OUTPUT_FOLDER") : p2.getProperty("NOTIFY_SVG_TIF_PATH");
            }
            SceGlobals.PSA_OUTPUT = p2.getProperty("PSA_SVG_OUTPUT");
            SceGlobals.PSA_URL = p2.getProperty("PSA_URL");
            SceGlobals.URL_Web_SERVICE_PLM = p2.getProperty("PSA_URL_Web_SERVICE_PLM");
            SceGlobals.LOGIN_Web_SERVICE_PLM = p2.getProperty("PSA_LOGIN_Web_SERVICE_PLM");
            SceGlobals.PWD_Web_SERVICE_PLM = p2.getProperty("PSA_PWD_Web_SERVICE_PLM");
            String propName_HarnessReleaseLevel = "HARNESS_RELEASE_LEVEL_NAME";
            HARNESS_RELEASE_LEVEL_NAME = p2.getProperty(propName_HarnessReleaseLevel);
            PDF_PRINT_BY_REGION = p2.getProperty("PDF_PRINT_REGION");
            if (HARNESS_RELEASE_LEVEL_NAME == null || HARNESS_RELEASE_LEVEL_NAME.isEmpty()) {
                System.err.println("Property " + propName_HarnessReleaseLevel + " not defined or empty! FSCVALID output file generation will not be triggered!");
                HARNESS_RELEASE_LEVEL_NAME = URL_Web_SERVICE_PLM;
            }
            if (p2.getProperty("TIFF_CONV_MEM") != null) {
                try {
                    in = Integer.parseInt(p2.getProperty("TIFF_CONV_MEM"));
                    SceGlobals.TIFF_CONV_MEM = in;
                }
                catch (NumberFormatException nfex) {
                    System.out.println("Wrong TIFF conversion memory max value " + p2.getProperty("TIFF_CONV_MEM") + " in chs_cust.properties file.");
                    SceGlobals.TIFF_CONV_MEM = 512;
                }
            }
            //Download the cis-config.properties if needed.
            if(Files.exists(Path.of(SceGlobals.cisConfigFilePath))) {
                System.out.println("cis-config.properties already exists in the shared storage.");
            }
            else {
                System.out.println("Call from Notify Service");
                System.out.println("cis-config.properties does not exist. Downloading cis-config.properties from shared storage.");
                //Pre-processing task for downloading cis-config.properties from shared storage.
                //preProcessingTask(String cisPropertiesPath, String configFilePath, String scriptPath
                int statusCode = taskProcessing.preProcessingTask(SceGlobals.CIS_PROPERTIES_PATH, SceGlobals.configFilePath, scriptForProcessing);
                if (statusCode != 0) {
                    System.err.println("Error in pre processing task for downloading cis-config.properties from shared storage!");
                }
            }
            //Set CIS_U and CIS_P technical user.
            if(isCapitalXMode()) {
                FileInputStream propCISInFile = new FileInputStream(SceGlobals.cisConfigFilePath);
                Properties pCIS = new Properties();
                pCIS.load(propCISInFile);
                setCISUrlUserPassword(pCIS, encrypter);
            }
            else {
                setCISUrlUserPassword(p2, encrypter);
            }
            try {
                in = Integer.parseInt(p2.getProperty("CIS_TIMEOUT"));
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
                throw new Exception("Can't decrypt.");
            }
            SceGlobals.ChsS = p2.getProperty("ChsS");
            SceGlobals.debug = p2.getProperty("DEBUG").equals("true");
            if (p2.getProperty("ACROBATPATH") != null) {
                SceGlobals.d_AcrobatPath = p2.getProperty("ACROBATPATH");
            }
            SceGlobals.PDF_Orientation = p2.getProperty("ORIENTATION");
            SceGlobals.PDF_Color = p2.getProperty("COLOR");
            SceGlobals.PDF_Area = p2.getProperty("AREA");
            SceGlobals.PDF_Size = p2.getProperty("SIZE");
            SceGlobals.FSCVALID_MAX_CONCURRENT_THREADS = p2.getProperty("FSCVALID_MAX_CONCURRENT_THREADS");
            propInFile.close();
        }
        catch (FileNotFoundException e) {
            System.err.println("Can't find " + SceGlobals.propFilePath);
        }
        catch (IOException e) {
            System.err.println("I/O failed." + SceGlobals.propFilePath);
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

    public static boolean isCapitalXMode()
    {
        String isCapitalXMode = System.getenv("CAPITAL_X_MODE");
        return isCapitalXMode.equals("true") || isCapitalXMode.equals("1");
    }

    private static void loadChsConnectorMasterProperties(Properties p2) throws Exception {
        String isChsConnectorMasterPropertyName = "IS_CHS_CONNECTOR_MASTER";
        String chsConnectorHarnessHost = "CHS_HARNESS_CONNECTOR_HOST";
        String chsConnectorHarnessPort = "CHS_HARNESS_CONNECTOR_PORT";
        String chsConnectorLogicHost = "CHS_LOGIC_CONNECTOR_HOST";
        String chsConnectorLogicPort = "CHS_LOGIC_CONNECTOR_PORT";
        String errMsg = "Config file needs property definition for ";
        String isChsConnectorMasterProperty = p2.getProperty(isChsConnectorMasterPropertyName);
        if (isChsConnectorMasterProperty == null) {
            throw new Exception(String.valueOf(errMsg) + isChsConnectorMasterPropertyName);
        }
        String chsConnectorHarnessHostProperty = p2.getProperty(chsConnectorHarnessHost);
        if (chsConnectorHarnessHostProperty == null) {
            throw new Exception(String.valueOf(errMsg) + chsConnectorHarnessHostProperty);
        }
        String chsConnectorHarnessPortProperty = p2.getProperty(chsConnectorHarnessPort);
        if (chsConnectorHarnessPortProperty == null) {
            throw new Exception(String.valueOf(errMsg) + chsConnectorHarnessPortProperty);
        }
        String chsConnectorLogicHostProperty = p2.getProperty(chsConnectorLogicHost);
        if (chsConnectorLogicHostProperty == null) {
            throw new Exception(String.valueOf(errMsg) + chsConnectorLogicHostProperty);
        }
        String chsConnectorLogicPortProperty = p2.getProperty(chsConnectorLogicPort);
        if (chsConnectorLogicPortProperty == null) {
            throw new Exception(String.valueOf(errMsg) + chsConnectorLogicPortProperty);
        }
        SceGlobals.IS_CHS_CONNECTOR_MASTER = Boolean.parseBoolean(isChsConnectorMasterProperty);
        SceGlobals.CHS_CONNECTOR_HARNESS_HOST = chsConnectorHarnessHostProperty;
        SceGlobals.CHS_CONNECTOR_HARNESS_PORT = Integer.parseInt(chsConnectorHarnessPortProperty);
        SceGlobals.CHS_CONNECTOR_LOGIC_HOST = chsConnectorLogicHostProperty;
        SceGlobals.CHS_CONNECTOR_LOGIC_PORT = Integer.parseInt(chsConnectorLogicPortProperty);
    }

    public static void insertInputXMLToSOAPBody(Document domObject, SOAPPart part, SOAPMessage processedMessage) throws Exception {
        Element docEl;
        NodeList nList;
        Source spSrc = part.getContent();
        DOMResult domRes = new DOMResult();
        WebServerServlet.XML_TRANSFORMER.transform(spSrc, domRes);
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

    public static SOAPMessage sendSOAPRequest(SOAPMessage requestMessage, String url, int timeout) {
        SOAPMessage responseMessage = null;
        try {
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection connection = soapConnectionFactory.createConnection();
            responseMessage = connection.call(requestMessage, (Object)url);
        }
        catch (Exception exception) {
            // empty catch block
        }
        return responseMessage;
    }

    public static boolean isAnyFault(SOAPMessage response) {
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

    public static String getFaultString(SOAPMessage responseSOAPMessage) {
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

    public static String processSVG(Document svgDoc, String dsgName, boolean deleteSVG, SceGlobals sceGlobalsInstance) throws Exception {
        DOMSource source = new DOMSource(svgDoc);
        String svgFilePathName = String.valueOf(sceGlobalsInstance.getCisSvgOutputFolderPath()) + dsgName + ".svg";
        String tiffFilePathName = String.valueOf(sceGlobalsInstance.getCisSvgOutputFolderPath()) + dsgName.replace(CONST_FIELD_G_IN_FILE_NAME, "--" + SceGlobals.TIFF_Name_Constant + borderName + "----") + TIFF;
        FileOutputStream stream = new FileOutputStream(svgFilePathName);
        StreamResult result = new StreamResult(stream);
        WebServerServlet.XML_TRANSFORMER.transform(source, result);
        stream.flush();
        stream.close();
        System.out.println();
        try {
            if (SceGlobals.debug) {
                System.out.println("Memory: " + SceGlobals.TIFF_CONV_MEM);
            }
            SVG2TiffProcess pro = new SVG2TiffProcess();
            pro.createTiff(svgFilePathName, tiffFilePathName, deleteSVG);
        }
        catch (Exception e) {
            System.out.println("Error during SVG to TIFF conversion.");
            e.printStackTrace();
        }
        return tiffFilePathName;
    }

    public static void insertSOAPAuthentication(SOAPMessage msg) throws Exception {
        SOAPUtils.insertSOAPAuthentication(msg, CHS_AUTHENTICATION_VALUES[0], CHS_AUTHENTICATION_VALUES[1]);
    }

    private static void buildDesName(String field1, String field2, String field3, String field4, String field5, String field6, String field7, boolean isLogicDesign, SceGlobals sceGlobalsInstance) {
        String f1 = NotifyService.fillValue(field1, 2).replace(' ', '-');
        String f2 = NotifyService.fillValue(field2, 6).replace(' ', '-');
        String f3 = NotifyService.fillValue(field3, 3).replace(' ', '-');
        String f4 = NotifyService.fillValue(field4, 10).replace(' ', '-');
        String f5 = NotifyService.fillValue(field5, 2).replace(' ', '-');
        String f6 = NotifyService.fillValue(field6, 20).replace(' ', '-');
        String f7 = NotifyService.fillValue(field7, 11).replace(' ', '-');
        sceGlobalsInstance.setDesName(String.valueOf(f1) + "." + f2 + "." + f3 + "." + f4 + "." + f5 + "." + f6 + "." + f7);
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

    public static void pMsgThread(String threadId, String msg) {
        System.out.println(String.valueOf(threadId) + ": " + msg);
    }

    protected static boolean processDesXML(Document xmlDoc, String field4, String field5, String field6, String field7, boolean isLogicDesign, Element elementHarnessDesign, SceGlobals sceGlobalsInstance, String threadId) {
        D_QuerySingleDesignPart aSSPT;
        Element root = xmlDoc.getDocumentElement();
        MessageWin mesWin = SceGlobals.mesWin;
        String service = URL_Web_SERVICE_PLM;
        String codeCader = URL_Web_SERVICE_PLM;
        String decoupage = URL_Web_SERVICE_PLM;
        String msg = URL_Web_SERVICE_PLM;
        Element designAttributesObject = null;
        Object object = null;
        String queryElementName = URL_Web_SERVICE_PLM;
        NodeList desData = null;
        if (isLogicDesign) {
            queryElementName = "logicaldesign";
            desData = root.getElementsByTagName(queryElementName);
            int count = desData.getLength();
            if (count != 1) {
                msg = "There is no " + queryElementName + " element.";
                NotifyService.pMsgThread(threadId, msg);
                mesWin.addMess(msg, "error");
                return false;
            }
            designAttributesObject = (Element)desData.item(0);
        } else {
            designAttributesObject = elementHarnessDesign;
            if (designAttributesObject == null) {
                msg = "There is no composite " + queryElementName + " element in the xml.";
                NotifyService.pMsgThread(threadId, msg);
                mesWin.addMess(msg, "error");
                return false;
            }
        }
        String designPartNumber = designAttributesObject.getAttribute("name");
        String designVersion = designAttributesObject.getAttribute("version");
        String designation20 = field6;
        NodeList propData = designAttributesObject.getElementsByTagName("property");
        Element prop = null;
        Object p1 = null;
        Object p2 = null;
        Object p3 = null;
        String p1Name = "Cader";
        String p2Name = "Decoupage";
        String p3Name = "Service";
        String propName = URL_Web_SERVICE_PLM;
        String propValue = URL_Web_SERVICE_PLM;
        int count = 0;
        int i = 0;
        while (i < propData.getLength()) {
            prop = (Element)propData.item(i);
            propName = prop.getAttribute("name");
            propValue = prop.getAttribute("val");
            if (propName.equals(p1Name)) {
                codeCader = propValue;
                ++count;
            }
            if (propName.equals(p2Name)) {
                decoupage = propValue;
                ++count;
            }
            if (propName.equals(p3Name)) {
                service = propValue;
                ++count;
            }
            if (count == 3) {
                i = propData.getLength();
            }
            ++i;
        }
        if (SceGlobals.DESIGN_NAME_CHECK.toUpperCase().equals("YES") && isLogicDesign && !(aSSPT = new D_QuerySingleDesignPart(designPartNumber, designVersion)).checkSCEPartNo(designPartNumber, designVersion, service, codeCader, decoupage, designation20)) {
            msg = SceGlobals.d_partDataMismatch;
            NotifyService.pMsgThread(threadId, String.valueOf(msg) + " Aborting Release Level Change service.");
            return false;
        }
        NotifyService.buildDesName(service, codeCader, decoupage, field4, field5, field6, field7, isLogicDesign, sceGlobalsInstance);
        return true;
    }

    public static void copyFile(String fromPath, String toPath) {
        try {
            int oneChar;
            URL url = new URL(fromPath);
            URLConnection urlC = url.openConnection();
            InputStream is = url.openStream();
            System.out.flush();
            FileOutputStream fos = null;
            fos = new FileOutputStream(toPath);
            int count = 0;
            while ((oneChar = is.read()) != -1) {
                fos.write(oneChar);
                ++count;
            }
            is.close();
            fos.close();
        }
        catch (MalformedURLException e) {
            System.err.println(e.toString());
        }
        catch (IOException e) {
            System.err.println(e.toString());
        }
    }

    private static void checkInSDP(URL url, String[] argsNameRevision, String[] ArgsFormat, String[] ArgsAttachedFileName, String[] argsDataHandler, String pathOfAttachedFiles) {
        boolean errorPromot;
        String error;
        MessageWin mesWin;
        block16: {
            SceGlobals.mesWinTitle = "Design ERROR CALL PLM SERVICE";
            SceGlobals.mesWin = mesWin = new MessageWin(false);
            mesWin.resetErrors();
            System.out.println("------------ Begin call webService PSA_ImpCHSLogicalDesignWebService ----------------");
            Service service = new Service();
            Call call = null;
            try {
                call = (Call)service.createCall();
            }
            catch (ServiceException e1) {
                e1.printStackTrace();
                mesWin.addMess("problem lors de l'appel du web service PLM ...", "error");
                mesWin.showWin();
                return;
            }
            call.setTargetEndpointAddress(url);
            call.setOperationName(new QName("urn:MatrixService", "checkInSDP"));
            call.setUsername(SceGlobals.LOGIN_Web_SERVICE_PLM);
            String pwdDecrypt = URL_Web_SERVICE_PLM;
            try {
                DMSEncrypter dmsEncrypter = new DMSEncrypter();
                pwdDecrypt = dmsEncrypter.decryptString(SceGlobals.PWD_Web_SERVICE_PLM);
            }
            catch (MCDDMSException e) {
                System.err.println("Can't decrypt.");
                e.printStackTrace();
                return;
            }
            call.setPassword(pwdDecrypt);
            call.addParameter("in0", javax.xml.rpc.encoding.XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("in1", javax.xml.rpc.encoding.XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("in2", javax.xml.rpc.encoding.XMLType.SOAP_ARRAY, ParameterMode.IN);
            call.addParameter("in3", javax.xml.rpc.encoding.XMLType.SOAP_ARRAY, ParameterMode.IN);
            call.addParameter("in4", javax.xml.rpc.encoding.XMLType.SOAP_ARRAY, ParameterMode.IN);
            call.setReturnType(XMLType.AXIS_VOID);
            QName qName = new QName("urn:MatrixService", "DataHandler");
            call.registerTypeMapping(DataHandler.class, qName, JAFDataHandlerSerializerFactory.class, JAFDataHandlerDeserializerFactory.class);
            Object[] checkInparams = new Object[5];
            checkInparams[0] = argsNameRevision[0];
            checkInparams[1] = argsNameRevision[1];
            checkInparams[2] = ArgsFormat;
            checkInparams[3] = ArgsAttachedFileName;
            DataHandler[] dhFiles = new DataHandler[argsDataHandler.length];
            int i = 0;
            while (i < argsDataHandler.length) {
                FileDataSource ds = new FileDataSource(String.valueOf(pathOfAttachedFiles) + argsDataHandler[i]);
                dhFiles[i] = new DataHandler((DataSource)ds);
                ++i;
            }
            checkInparams[4] = dhFiles;
            error = null;
            errorPromot = false;
            try {
                try {
                    System.out.println("****************** debut du traitement PLM *****************");
                    call.invoke(checkInparams);
                    System.out.println("Fin traitement .....");
                    System.out.println(String.valueOf(dhFiles.length) + " documents export\u00c3\u00a9s dans le PLM ...........");
                }
                catch (Exception e) {
                    System.out.println("Unable to checkin file because of the following exception:" + e.toString());
                    String exceptionMessage = e.getMessage();
                    if (exceptionMessage.endsWith("009-Promote of ECO failed")) {
                        errorPromot = true;
                        error = "ERROR : Promote of ECO failed";
                    } else {
                        int i2 = e.getMessage().lastIndexOf("-");
                        error = e.getMessage().substring(i2 + 1);
                        error = "ERROR : " + error;
                    }
                    System.gc();
                    break block16;
                }
            }
            catch (Throwable throwable) {
                System.gc();
                throw throwable;
            }
            System.gc();
        }
        if (error == null) {
            mesWin.setTitle("Success envoi PLM");
            mesWin.setSize(500, 200);
            mesWin.addMess("  les fichiers ont bien \u00c3\u00a9t\u00c3\u00a9 import\u00c3\u00a9s dans le PLM ...", "info");
            mesWin.showWin();
        } else if (errorPromot) {
            mesWin.setTitle("Erreur envoi PLM");
            mesWin.setSize(500, 200);
            mesWin.addMess("  Une erreur est survenue lors de l'envoi des documents vers le PLM : \n\t" + error, "error");
            mesWin.showWin();
        } else {
            mesWin.setTitle("Erreur envoi PLM");
            mesWin.setSize(500, 200);
            mesWin.addMess("  Une erreur est survenue lors de l'envoi des documents vers le PLM : \n\t" + error, "error");
            mesWin.showWin();
        }
    }

    public static void main(String[] args) {
        System.out.println(String.valueOf(SceGlobals.progVers) + "\n");
        System.out.println("Starting SceRecBrowser ...");
        SceQuery sceCBrowser = new SceQuery("V75%", URL_Web_SERVICE_PLM);
        SceGlobals.libraryobject_id = "UID5eb489-002831797c-d3e5c7620b204779ee755ef50f893d4e";
        MessageWin mesWC = new MessageWin(false);
        sceCBrowser.setUndecorated(false);
        sceCBrowser.toFront();
        sceCBrowser.show();
        System.out.println("SceRecBrowser finished");
    }

    private static boolean validateFileSize(File fileTiff) throws InterruptedException {
        boolean isFileWrittingCompleted = false;
        long intialSize = 0L;
        long finalSize = 0L;
        int initWaitCount = 0;
        while (finalSize == 0L && initWaitCount < 180) {
            Thread.sleep(5000L);
            ++initWaitCount;
            finalSize = NotifyService.checkFileSize(fileTiff);
        }
        System.out.println("Inside validateFileSize method :finalSize length first time : " + finalSize + " --initTimeCount-total waiting count---:" + initWaitCount);
        while (intialSize != finalSize) {
            intialSize = finalSize;
            finalSize = NotifyService.checkFileSize(fileTiff);
            System.out.println("Inside validateFileSize method :inside while loop, finalSize size : " + finalSize);
            if (intialSize == finalSize) {
                isFileWrittingCompleted = true;
                System.out.println("write operation complete: file size - " + finalSize);
                continue;
            }
            Thread.sleep(5000L);
        }
        return isFileWrittingCompleted;
    }

    private static long checkFileSize(File fileTiff) {
        return fileTiff.length();
    }

    private static void setTheAttribute(Node node, String attributeName, String value)
    {
        if(node instanceof Element element)
        {
            element.setAttribute(attributeName, value);
        }
    }

    private static Node getTheAttribute(Node node, String attributeName)
    {
        if(node instanceof Element element)
        {
            return element.getAttributeNode(attributeName);
        }
        return null;
    }
}