package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPMessage
 */
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import org.w3c.dom.Document;

public abstract class AbstractClient {
    public String threadId = "";
    protected static final String CIS_HOST = "localhostxxx";
    protected static final int CIS_PORT = 5555444;
    protected static final int CIS_SECURE_PORT = 49932444;
    private static final String CHS_USER = "systemxxx";
    private static final String CHS_PASSWORD = "managerxxx";
    protected static final String OUTPUT_DIRECTORY = "C:/temp/CIS";
    private static final String CIS_CONTEXT = "/chs/cis/";
    private static final String[] CIS_AUTHENTICATION_PARAMS = new String[]{"chsusername", "chspassword"};
    private static final String CIS_NULL_RESPONSE_FAULT = "chs.bridges.webservices.exceptions.WebServiceNullResponseException";
    private static final String EXTERNAL_NULL_RESPONSE_FAULT = "org.xml.sax.SAXParseException: The root element is required in a well-formed document.";
    private IClientLogger m_logger = new ClientLogger();
    private boolean loggingMode = false;
    protected String clientParam;
    public boolean m_secureMode = false;
    private String m_keyStore;
    private String m_keyPassword;

    public void pMsgThread(String msg) {
        System.out.println(String.valueOf(this.threadId) + ": " + msg);
    }

    protected abstract String getWebServiceName();

    protected abstract String getRequestPayload();

    protected abstract boolean isResponseExcepted();

    public void setSecureMode(boolean flag, String key, String password) {
        this.m_secureMode = flag;
        this.m_keyStore = key;
        this.m_keyPassword = password;
    }

    protected URL getServiceURL() throws MalformedURLException {
        if (this.m_secureMode) {
            return new URL("https", CIS_HOST, 49932444, CIS_CONTEXT + this.getWebServiceName());
        }
        return new URL("http", CIS_HOST, 5555444, CIS_CONTEXT + this.getWebServiceName());
    }

    public void invoke(boolean loggingMode, String param) throws Exception {
        this.loggingMode = loggingMode;
        this.clientParam = param;
        this.invoke();
    }

    public void invoke(boolean loggingMode) throws Exception {
        this.invoke(loggingMode, null);
    }

    public void invoke() throws Exception {
        Document responsePayload = this.getCISResponse();
        if (responsePayload != null) {
            this.processResponse(responsePayload);
        }
    }

    protected void processResponse(Document responsePayload) throws Exception {
        this.logMsg(WebServiceUtils.writeDOMDocumentToString(responsePayload));
    }

    protected Document getCISResponse() throws Exception {
        SOAPMessage requestMsg = WebServiceUtils.newBlankSOAPMessage();
        Document requestPayload = WebServiceUtils.parseToDOM(new ByteArrayInputStream(this.getRequestPayload().getBytes("UTF8")));
        WebServiceUtils.insertXMLPayloadInSOAPMessage(requestPayload, requestMsg);
        NotifyService.insertSOAPAuthentication(requestMsg);
        this.addRequestSOAPAttachments(requestMsg);
        URL serviceURL = this.getServiceURL();
        SOAPMessage responseMsg = this.sendSOAPRequest(requestMsg, serviceURL, this.isResponseExcepted());
        if (this.loggingMode) {
            System.out.println();
            System.out.println(">>>>>> Request SOAP message:");
            requestMsg.writeTo((OutputStream)System.out);
            System.out.println();
            System.out.flush();
            System.out.println();
            System.out.println(">>>>>> Response SOAP message:");
            if (responseMsg == null) {
                System.out.println("null");
            } else if (!this.hasResponseAttachments()) {
                responseMsg.writeTo((OutputStream)System.out);
                System.out.println();
                System.out.flush();
            }
        }
        Document responsePayload = null;
        if (responseMsg != null) {
            if (WebServiceUtils.isSOAPFault(responseMsg)) {
                String fault = WebServiceUtils.getSOAPFaultString(responseMsg);
                if (this.isResponseExcepted() || !CIS_NULL_RESPONSE_FAULT.equals(fault) && !EXTERNAL_NULL_RESPONSE_FAULT.equals(fault)) {
                    throw new Exception("SOAP Fault = " + fault);
                }
            } else if (this.isResponseExcepted()) {
                if (this.hasResponseAttachments()) {
                    this.processResponseAttachments(responseMsg);
                }
                responsePayload = WebServiceUtils.extractXMLPayloadFromSOAPMessage(responseMsg);
            }
        }
        return responsePayload;
    }

    private SOAPMessage sendSOAPRequest(SOAPMessage messageSOAP, URL url, boolean isResponseExpected) throws SOAPException {
        SOAPMessage response = null;
        try {
            response = WebServiceUtils.sendSOAPRequest(messageSOAP, url);
        }
        catch (SOAPException exc) {
            boolean ignoreSOAPFault = false;
            if (EXTERNAL_NULL_RESPONSE_FAULT.equals(exc.getMessage())) {
                boolean bl = ignoreSOAPFault = !isResponseExpected;
            }
            if (!ignoreSOAPFault) {
                throw exc;
            }
            response = null;
        }
        return response;
    }

    protected boolean hasResponseAttachments() {
        return false;
    }

    protected void addRequestSOAPAttachments(SOAPMessage messageSOAP) throws Exception {
    }

    protected void processResponseAttachments(SOAPMessage messageSOAP) throws Exception {
    }

    public void logMsg(String msg) {
        this.m_logger.logMessage(msg);
    }

    public void setLogger(IClientLogger logger) {
        this.m_logger = logger;
    }

    public class ClientLogger
    implements IClientLogger {
        @Override
        public void logMessage(String message) {
            AbstractClient.this.pMsgThread(message);
        }
    }

    public static interface IClientLogger {
        public void logMessage(String var1);
    }
}

