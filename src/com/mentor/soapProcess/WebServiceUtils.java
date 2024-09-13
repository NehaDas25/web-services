package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 *  javax.xml.soap.AttachmentPart
 *  javax.xml.soap.MessageFactory
 *  javax.xml.soap.SOAPBody
 *  javax.xml.soap.SOAPBodyElement
 *  javax.xml.soap.SOAPConnection
 *  javax.xml.soap.SOAPConnectionFactory
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPMessage
 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Iterator;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public abstract class WebServiceUtils {
    private static final String SOAP_ATTACHMENT_FORMAT_XML = "application/xml";
    private static final String SOAP_ATTACHMENT_FORMAT_GZIP = "application/gzip";
    private static final String SOAP_ATTACHMENT_FORMAT_OCTECT = "application/octet-stream";

    private static SOAPConnection getSOAPConnection() throws SOAPException {
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection connectionSOAP = soapConnectionFactory.createConnection();
        return connectionSOAP;
    }

    private static Transformer getXMLTransformer() throws TransformerConfigurationException {
        Transformer xmlTransformer = TransformerFactory.newInstance().newTransformer();
        xmlTransformer.setOutputProperty("indent", "yes");
        return xmlTransformer;
    }

    private static DocumentBuilder getDOMDocBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder domBuilder = factory.newDocumentBuilder();
        return domBuilder;
    }

    public static void insertXMLPayloadInSOAPMessage(Document payload, SOAPMessage messageSOAP) throws SOAPException, TransformerException {
        SOAPBody body = messageSOAP.getSOAPBody();
        body.addDocument(payload);
    }

    public static SOAPMessage newBlankSOAPMessage() throws SOAPException {
        return MessageFactory.newInstance().createMessage();
    }

    public static SOAPMessage sendSOAPRequest(SOAPMessage message, URL url) throws SOAPException {
        return WebServiceUtils.getSOAPConnection().call(message, (Object)url);
    }

    public static boolean isSOAPFault(SOAPMessage messageSOAP) {
        try {
            if (messageSOAP == null || messageSOAP.getSOAPBody().hasFault()) {
                return true;
            }
        }
        catch (SOAPException sOAPException) {
            // empty catch block
        }
        return false;
    }

    public static String getSOAPFaultString(SOAPMessage messageSOAP) {
        if (messageSOAP == null) {
            return "Response is blank or NULL";
        }
        try {
            return messageSOAP.getSOAPBody().getFault().getFaultString();
        }
        catch (SOAPException ex) {
            return "SOAPBody is NULL or empty";
        }
    }

    public static Document extractXMLPayloadFromSOAPMessage(SOAPMessage messageSOAP) throws Exception {
        Iterator iter = messageSOAP.getSOAPBody().getChildElements();
        while (iter.hasNext()) {
            Object child = iter.next();
            if (!(child instanceof SOAPBodyElement)) continue;
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Node node = doc.importNode((Node)((SOAPBodyElement)child), true);
            doc.appendChild(node);
            return doc;
        }
        return null;
    }

    public static void writeDOMDocumentToFile(Document doc, String filePath) throws IOException, TransformerException {
        File outFile = new File(filePath);
        File outDir = new File(outFile.getParent());
        outDir.mkdir();
        try (FileOutputStream stream = new FileOutputStream(outFile)){
            WebServiceUtils.writeDOMDocumentToStream(doc, stream);
        }
    }

    public static void writeDOMDocumentToStream(Document doc, OutputStream stream) throws TransformerException {
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(stream);
        WebServiceUtils.getXMLTransformer().transform(source, result);
    }

    public static String writeDOMDocumentToString(Document doc) throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        WebServiceUtils.writeDOMDocumentToStream(doc, stream);
        return stream.toString("UTF8");
    }

    public static Document parseToDOM(InputStream stream) throws Exception {
        return WebServiceUtils.getDOMDocBuilder().parse(stream);
    }

    public static void createGZIPAttachment(SOAPMessage messageSOAP, String messageID, InputStream data) throws Exception {
        ByteArrayOutputStream compressedByteStream = new ByteArrayOutputStream();
        GZIPOutputStream compressedStream = new GZIPOutputStream(compressedByteStream);
        PrintWriter printWriterc = new PrintWriter(new OutputStreamWriter((OutputStream)compressedStream, "UTF8"));
        InputStreamReader streamReader = new InputStreamReader(data, "UTF8");
        while (streamReader.ready()) {
            char[] buffer = new char[1000];
            int numberOfReadBytes = streamReader.read(buffer, 0, 1000);
            if (numberOfReadBytes <= 0) continue;
            printWriterc.write(buffer, 0, numberOfReadBytes);
        }
        printWriterc.flush();
        compressedStream.finish();
        compressedStream.flush();
        ByteArrayInputStream result = new ByteArrayInputStream(compressedByteStream.toByteArray());
        DataHandler dh = new DataHandler((DataSource)new InputStreamDataSource(result, SOAP_ATTACHMENT_FORMAT_GZIP));
        AttachmentPart attachment = messageSOAP.createAttachmentPart(dh);
        attachment.setContentId(messageID);
        messageSOAP.addAttachmentPart(attachment);
    }

    public static void createXMLAttachment(SOAPMessage messageSOAP, String messageID, InputStream data) {
        DataHandler dh = new DataHandler((DataSource)new InputStreamDataSource(data, SOAP_ATTACHMENT_FORMAT_XML));
        AttachmentPart attachment = messageSOAP.createAttachmentPart(dh);
        attachment.setContentId(messageID);
        messageSOAP.addAttachmentPart(attachment);
    }

    private static void extractDocumentFromSOAPAttachment(SOAPMessage messageSOAP, OutputStream outStream) throws Exception {
        int attachCount = 0;
        Iterator iter = messageSOAP.getAttachments();
        while (iter.hasNext()) {
            InputStream in = null;
            if (++attachCount == 1) {
                in = WebServiceUtils.getInputStreamFromAttachment(outStream, iter);
            }
            ((InputStream)Objects.requireNonNull(in)).close();
            outStream.close();
        }
    }

    /*
     * Enabled aggressive block sorting
     */
    private static InputStream getInputStreamFromAttachment(OutputStream outStream, Iterator iter) throws SOAPException, IOException {
        InputStream in;
        AttachmentPart attachment = (AttachmentPart)iter.next();
        if (SOAP_ATTACHMENT_FORMAT_GZIP.equals(attachment.getContentType()) || SOAP_ATTACHMENT_FORMAT_OCTECT.equals(attachment.getContentType())) {
            in = (InputStream)attachment.getContent();
        } else {
            if (!SOAP_ATTACHMENT_FORMAT_XML.equals(attachment.getContentType())) {
                throw new RuntimeException("Expected application/gzip or application/octet-stream but received " + attachment.getContentType());
            }
            StreamSource source = (StreamSource)attachment.getContent();
            in = source.getInputStream();
        }
        while (in.available() != 0) {
            byte[] buffer = new byte[1024];
            int readBytesCount = in.read(buffer, 0, 1024);
            if (readBytesCount <= 0) continue;
            outStream.write(buffer, 0, readBytesCount);
        }
        return in;
    }

    public static void pMsgThread(String threadId, String msg) {
        System.out.println(String.valueOf(threadId) + ": " + msg);
    }

    static Document processResponseAttachments(SOAPMessage messageSOAP, String threadId) throws Exception {
        Path tempDirWithPrefix = Files.createTempDirectory("WebService", new FileAttribute[0]);
        String gzipFilePath = WebServiceUtils.getGzipFilePath(tempDirWithPrefix, ".gzip");
        WebServiceUtils.pMsgThread(threadId, "GZIP file path : " + gzipFilePath);
        WebServiceUtils.extractDocumentFromSOAPAttachment(messageSOAP, new FileOutputStream(gzipFilePath));
        String xmlFilePath = WebServiceUtils.getGzipFilePath(tempDirWithPrefix, ".xml");
        WebServiceUtils.pMsgThread(threadId, "Design XML path : " + xmlFilePath);
        try {
            GZIPInputStream in = WebServiceUtils.getGzipInputStream(gzipFilePath);
            if (in == null) {
                throw new Exception("SOAP attachment not present ..!!");
            }
            FileOutputStream out = WebServiceUtils.getDesignXMLOutputStream(xmlFilePath, in);
            if (out == null) {
                throw new Exception("Design xml not present in SOAP attachment ..!!");
            }
            in.close();
            out.close();
            Document document = WebServiceUtils.convertDesignXMLToDocument(xmlFilePath);
            return document;
        }
        finally {
            WebServiceUtils.delete(new File(tempDirWithPrefix.toString()), threadId);
        }
    }

    private static void delete(File file, String threadId) {
        if (file.isDirectory()) {
            if (Objects.requireNonNull(file.list()).length == 0) {
                WebServiceUtils.deleteWithMsg(file, "Directory is deleted : ", threadId);
            } else {
                String[] files = file.list();
                String[] stringArray = Objects.requireNonNull(files);
                int n = stringArray.length;
                int n2 = 0;
                while (n2 < n) {
                    String temp = stringArray[n2];
                    File fileDelete = new File(file, temp);
                    WebServiceUtils.delete(fileDelete, threadId);
                    ++n2;
                }
                if (Objects.requireNonNull(file.list()).length == 0) {
                    WebServiceUtils.deleteWithMsg(file, "Directory is deleted : ", threadId);
                }
            }
        } else {
            WebServiceUtils.deleteWithMsg(file, "File is deleted : ", threadId);
        }
    }

    private static void deleteWithMsg(File file, String message, String threadId) {
        file.delete();
        WebServiceUtils.pMsgThread(threadId, String.valueOf(message) + file.getAbsolutePath());
    }

    private static String getGzipFilePath(Path tempDirWithPrefix, String extention) {
        return tempDirWithPrefix + "\\" + "temp" + extention;
    }

    private static GZIPInputStream getGzipInputStream(String gzipFilePath) throws IOException {
        GZIPInputStream in = new GZIPInputStream(new FileInputStream(gzipFilePath));
        return in;
    }

    private static FileOutputStream getDesignXMLOutputStream(String xmlFilePath, GZIPInputStream in) throws IOException {
        int len;
        FileOutputStream out;
        try {
            out = new FileOutputStream(xmlFilePath);
        }
        catch (FileNotFoundException e) {
            System.err.println("Could not write to file. " + xmlFilePath);
            return null;
        }
        byte[] buf = new byte[1024];
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        return out;
    }

    private static Document convertDesignXMLToDocument(String xmlFilePath) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder parser = factory.newDocumentBuilder();
        return parser.parse(xmlFilePath);
    }

    private static class InputStreamDataSource
    implements DataSource {
        private InputStream m_inputStream;
        private String m_contentType;

        public InputStreamDataSource(InputStream is, String contentType) {
            this.m_inputStream = is;
            this.m_contentType = contentType;
        }

        public String getContentType() {
            return this.m_contentType;
        }

        public InputStream getInputStream() throws IOException {
            return this.m_inputStream;
        }

        public String getName() {
            return "Inputstream data source";
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Cannot write to this data source");
        }
    }
}

