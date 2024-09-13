package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.xml.soap.MessageFactory
 *  javax.xml.soap.SOAPBody
 *  javax.xml.soap.SOAPEnvelope
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPFault
 *  javax.xml.soap.SOAPHeader
 *  javax.xml.soap.SOAPMessage
 *  javax.xml.soap.SOAPPart
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WebServerServlet
extends HttpServlet {
    private static final long serialVersionUID = 1L;
    public static MessageFactory SOAP_MESSAGE_FACTORY = null;
    public static Transformer XML_TRANSFORMER = null;
    public static boolean isEmptydesg = false;
    public static boolean isNewName = false;
    public static String designID = null;
    public static String designName1 = null;
    ExecutorService pool = null;
    private String designType;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            block7: {
                NotifyService.readChsCustProps();
                String msgNoConfiguration = "No configuration property found FSCVALID_MAX_CONCURRENT_THREADS Not using maximum thread control";
                if (SceGlobals.FSCVALID_MAX_CONCURRENT_THREADS != null) {
                    try {
                        int numberOfConcurrectThreads = Integer.parseInt(SceGlobals.FSCVALID_MAX_CONCURRENT_THREADS);
                        if (numberOfConcurrectThreads > 0) {
                            System.out.println("Maximum number of concurrent FSCVALID threads defined by configruation file is: " + numberOfConcurrectThreads);
                            this.pool = Executors.newFixedThreadPool(numberOfConcurrectThreads);
                            break block7;
                        }
                        System.out.println("Configuration property FSCVALID_MAX_CONCURRENT_THREADS ist set to 0. Not using maximum thread control");
                    }
                    catch (Exception e) {
                        System.out.println(msgNoConfiguration);
                    }
                } else {
                    System.out.println(msgNoConfiguration);
                }
            }
            SOAP_MESSAGE_FACTORY = MessageFactory.newInstance();
            XML_TRANSFORMER = TransformerFactory.newInstance().newTransformer();
        }
        catch (Exception except) {
            except.printStackTrace();
            throw new ServletException((Throwable)except);
        }
    }

    public synchronized void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request, response);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public synchronized void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SceGlobals.startTime = Calendar.getInstance().getTimeInMillis();
        ServletOutputStream responseOut = response.getOutputStream();
        String requestURL = request.getRequestURI();
        String serviceName = "";
        Object body = null;
        SOAPMessage message = null;
        boolean isPLMService = false;
        StringTokenizer tokens = new StringTokenizer(requestURL, "/");
        while (tokens.hasMoreTokens()) {
            serviceName = tokens.nextToken();
        }
        Document doc = null;
        if (request.getContentLength() > 0) {
            response.setContentType("text/xml");
            BufferedReader reader = request.getReader();
            StreamSource source = new StreamSource(reader);
            try {
                MessageFactory msgFact = MessageFactory.newInstance();
                message = msgFact.createMessage();
                SOAPPart part = message.getSOAPPart();
                part.setContent((Source)source);
                message.saveChanges();
                doc = SOAPUtils.getDocFromSoapMessage(message);
                isEmptydesg = false;
                isNewName = false;
                NotifyService.readChsCustProps();
                Element root = doc.getDocumentElement();
                if (root.getTagName().equals("wiringdesign") && !root.getAttribute("oldstatus").equals("")) {
                    String designName = root.getAttribute("name");
                    String designRevision = root.getAttribute("revision");
                    this.designType = root.getAttribute("designtype");
                    String projectName = root.getAttribute("projectname");
                    String string = root.getAttribute("projectid");
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        Document responsePayload = null;
        if (doc != null) {
            try {
                String notifyService = "NotifyService";
                String componentUpdateService = "ComponentUpdateService";
                String designAttributesUpdateService = "DesignAttributesUpdateService";
                String harnessDesignAttributesUpdateService = "HarnessDesignAttributesUpdateService";
                String componentStatusChangeService = "ComponentStatusChangeService";
                String plmChsImportService = "PlmChsImportService";
                String plmChsVersionChangeService = "PlmChsVersionChangeService";
                String plmChsStatusChangeService = "PlmChsStatusChangeService";
                if (!(serviceName.equals(notifyService) || serviceName.equals(componentUpdateService) || serviceName.equals(designAttributesUpdateService) || serviceName.equals(harnessDesignAttributesUpdateService) || serviceName.equals(componentStatusChangeService) || serviceName.equals(plmChsImportService) || serviceName.equals(plmChsVersionChangeService) || serviceName.equals(plmChsStatusChangeService))) {
                    WebServerServlet.createReceiverSOAPFault("This service is not implemented: " + serviceName.toString() + " \navailable services are: \n" + notifyService + " \n" + componentUpdateService + " \n" + designAttributesUpdateService + " \n" + harnessDesignAttributesUpdateService + " \n" + componentStatusChangeService + " \n" + plmChsImportService + " \n" + plmChsVersionChangeService + " \n" + plmChsStatusChangeService).writeTo((OutputStream)responseOut);
                } else if (serviceName.equals(notifyService)) {
                    if (SceGlobals.IS_CHS_CONNECTOR_MASTER) {
                        System.out.println("######## Master received the request");
                        if (this.designType.equals("logicdesign")) {
                            URL url = new URL("http", SceGlobals.CHS_CONNECTOR_LOGIC_HOST, SceGlobals.CHS_CONNECTOR_LOGIC_PORT, "/mgc/webservices/" + notifyService);
                            WebServiceUtils.sendSOAPRequest(message, url);
                            System.out.println("######## Master forward request to " + url.toString());
                        } else {
                            if (!this.designType.equals("harnessdesign")) throw new Exception("Design type " + this.designType + " not supported by NotifyService implementation");
                            URL url = new URL("http", SceGlobals.CHS_CONNECTOR_HARNESS_HOST, SceGlobals.CHS_CONNECTOR_HARNESS_PORT, "/mgc/webservices/" + notifyService);
                            WebServiceUtils.sendSOAPRequest(message, url);
                            System.out.println("######## Master forward request to " + url.toString());
                        }
                    } else {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                        Date date = new Date();
                        String currentDateStamp = dateFormat.format(date);
                        String threadId = "threadId" + currentDateStamp;
                        if (this.pool != null) {
                            this.pool.execute(new NotifyThread(doc, threadId));
                        } else {
                            NotifyThread notifyThread = new NotifyThread(doc, threadId);
                            notifyThread.start();
                        }
                    }
                } else if (serviceName.equals(componentUpdateService)) {
                    responsePayload = NotifyService.processComponentUpdateService(doc);
                } else if (serviceName.equals(designAttributesUpdateService)) {
                    responsePayload = NotifyService.processLogicalDesignAttributesUpdateService(doc);
                } else if (serviceName.equals(harnessDesignAttributesUpdateService)) {
                    responsePayload = HarnessDesignAttributeUpdateService.processHarnessDesignAttributesUpdateService(doc);
                } else if (serviceName.equals(componentStatusChangeService)) {
                    ComponentStatusChangeThread componentStatusChangeThread = new ComponentStatusChangeThread(doc);
                    componentStatusChangeThread.start();
                } else if (serviceName.equals(plmChsImportService)) {
                    isPLMService = true;
                    PSAPlmComponentImportService service = new PSAPlmComponentImportService();
                    responsePayload = service.processComponentImportService(doc);
                } else if (serviceName.equals(plmChsVersionChangeService)) {
                    isPLMService = true;
                    PSAPlmComponentVersionService service = new PSAPlmComponentVersionService();
                    responsePayload = service.processComponentVersionService(doc);
                } else if (serviceName.equals(plmChsStatusChangeService)) {
                    try {
                        PSAPlmComponentExportService r = new PSAPlmComponentExportService(doc);
                        Thread t = new Thread(r);
                        t.start();
                    }
                    catch (PSAPlmChsHcException e) {
                        e.printStackTrace();
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                System.err.println(e.toString());
                try {
                    WebServerServlet.createReceiverSOAPFault(e.toString()).writeTo((OutputStream)responseOut);
                }
                catch (SOAPException se) {
                    se.printStackTrace();
                    System.err.println(se.toString());
                }
            }
        }
        if (responsePayload != null) {
            try {
                NodeList desData = responsePayload.getElementsByTagName("serviceError");
                int count = desData.getLength();
                if (count > 0) {
                    Element designAttributesObject = (Element)desData.item(0);
                    System.out.println("Creating soap fault for " + designAttributesObject.getAttribute("code") + "\n");
                    SOAPMessage sm = WebServerServlet.createSOAPFault(designAttributesObject.getAttribute("code"), designAttributesObject.getAttribute("type"), designAttributesObject.getAttribute("detail"), isPLMService);
                    sm.writeTo((OutputStream)responseOut);
                    System.out.println(sm.getSOAPBody().getFault().getFaultString());
                    return;
                }
                response.setContentType("text/xml");
                SOAPMessage messageEmpty = SOAP_MESSAGE_FACTORY.createMessage();
                messageEmpty.saveChanges();
                WebServerServlet.wrapXMLInSOAP(responsePayload, messageEmpty);
                messageEmpty.writeTo((OutputStream)responseOut);
                return;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            SOAPMessage messageEmpty = SOAP_MESSAGE_FACTORY.createMessage();
            messageEmpty.writeTo((OutputStream)responseOut);
            return;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static SOAPMessage createReceiverSOAPFault(String detail) {
        System.out.println("creating receiver soap fault...\n" + detail);
        return WebServerServlet.createSOAPFault("SOAP-ENV:Receiver", "Internal Error", detail, false);
    }

    private static SOAPMessage createSOAPFault(String faultCode, String faultString, String detail, boolean isPlmService) {
        try {
            SOAPMessage responsemsg = SOAP_MESSAGE_FACTORY.createMessage();
            SOAPPart sp = responsemsg.getSOAPPart();
            SOAPEnvelope env = sp.getEnvelope();
            SOAPHeader hdr = env.getHeader();
            SOAPBody bdy = env.getBody();
            SOAPFault sf = bdy.addFault();
            if (isPlmService) {
                sf.setFaultCode(new QName("http://schemas.xmlsoap.org/soap/envelope/", "SERVICE"));
            }
            sf.setFaultString(String.valueOf(faultString) + ": " + detail);
            responsemsg.saveChanges();
            return responsemsg;
        }
        catch (SOAPException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getServletInfo() {
        return "MGCWebServer Servlet";
    }

    public synchronized void destroy() {
    }

    private static String toString(Object o) {
        if (o == null) {
            return null;
        }
        if (o.getClass().isArray()) {
            StringBuffer sb = new StringBuffer();
            Object[] array = (Object[])o;
            int i = 0;
            while (i < array.length) {
                if (i > 0) {
                    sb.append("\n");
                }
                sb.append(array.getClass().getComponentType().getName());
                sb.append("[");
                sb.append(i);
                sb.append("]=");
                sb.append(WebServerServlet.toString(array[i]));
                ++i;
            }
            return sb.toString();
        }
        return o.toString();
    }

    private static void wrapXMLInSOAP(Document domDoc, SOAPMessage message) throws Exception {
        Element docEl;
        NodeList nList;
        SOAPPart part = message.getSOAPPart();
        Source domSrc = part.getContent();
        DOMResult domRes = new DOMResult();
        XML_TRANSFORMER.transform(domSrc, domRes);
        Node envRoot = domRes.getNode();
        if (envRoot.getNodeType() == 9 && (nList = (docEl = ((Document)envRoot).getDocumentElement()).getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/", "Body")).getLength() > 0) {
            Node bodyNode = nList.item(0);
            Element attRoot = domDoc.getDocumentElement();
            Node importNode = ((Document)envRoot).importNode(attRoot, true);
            bodyNode.appendChild(importNode);
            DOMSource domSource = new DOMSource(envRoot);
            part.setContent((Source)domSource);
        }
        message.saveChanges();
    }
}

