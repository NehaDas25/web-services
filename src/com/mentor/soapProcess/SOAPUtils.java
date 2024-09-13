package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.Name
 *  javax.xml.soap.SOAPElement
 *  javax.xml.soap.SOAPEnvelope
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPHeader
 *  javax.xml.soap.SOAPHeaderElement
 *  javax.xml.soap.SOAPMessage
 *  javax.xml.soap.SOAPPart
 */
import java.util.Iterator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class SOAPUtils {
    private static final String[] CHS_AUTHENTICATION_PARAMS = new String[]{"chsusername", "chspassword"};

    public static Document getDocFromSoapMessage(SOAPMessage soapMsg) throws Exception {
        SOAPElement bodyElement = SOAPUtils.getBodyElement(soapMsg);
        if (bodyElement != null) {
            return SOAPUtils.getAsDocument(bodyElement);
        }
        return null;
    }

    public static void insertSOAPAuthentication(SOAPMessage msg, String user, String password) throws Exception {
        SOAPPart part = msg.getSOAPPart();
        SOAPEnvelope envelope = part.getEnvelope();
        SOAPHeader header = envelope.getHeader();
        if (header == null) {
            header = envelope.addHeader();
        }
        int i = 0;
        while (i < CHS_AUTHENTICATION_PARAMS.length) {
            Name elemName = envelope.createName(CHS_AUTHENTICATION_PARAMS[i], "chs", "chs");
            SOAPHeaderElement elem = header.addHeaderElement(elemName);
            String value = i == 0 ? user : password;
            elem.addTextNode(value);
            ++i;
        }
    }

    private static SOAPElement getBodyElement(SOAPMessage soapMsg) throws SOAPException {
        Iterator iter = soapMsg.getSOAPBody().getChildElements();
        while (iter.hasNext()) {
            Object soapObj = iter.next();
            if (!(soapObj instanceof SOAPElement)) continue;
            return (SOAPElement)soapObj;
        }
        return null;
    }

    private static Document getAsDocument(SOAPElement bodyElement) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();
        Node node = doc.importNode((Node)bodyElement, true);
        doc.appendChild(node);
        return doc;
    }
}

