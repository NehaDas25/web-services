package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.MessageFactory
 */
import javax.xml.soap.MessageFactory;
import javax.xml.transform.TransformerFactory;

public class TestXCExportDesignXml {
    public static void main(String[] args) {
        try {
            TestXCExportDesignXml.dMsg("args " + args.length);
            if (args.length != 5) {
                throw new Exception("Number of command line arguments is incorrected. Excpecting 5 arguments");
            }
            TestXCExportDesignXml.dMsg("Started " + TestXCExportDesignXml.class.getSimpleName());
            String projectId = args[0];
            String projectName = args[1];
            String designName = args[2];
            String designRevision = args[3];
            String designDesc = args[4];
            TestXCExportDesignXml.dMsg("Argument projectId=" + projectId);
            TestXCExportDesignXml.dMsg("Argument projectName=" + projectName);
            TestXCExportDesignXml.dMsg("Argument designName=" + designName);
            TestXCExportDesignXml.dMsg("Argument designRevision=" + designRevision);
            TestXCExportDesignXml.dMsg("Argument designShortDesc=" + designDesc);
            boolean isStandaloneTest = false;
            SceGlobals sceGlobalsInstance = new SceGlobals();
            String threadId = "testId";
            NotifyService.readChsCustProps();
            WebServerServlet.SOAP_MESSAGE_FACTORY = MessageFactory.newInstance();
            WebServerServlet.XML_TRANSFORMER = TransformerFactory.newInstance().newTransformer();
            TestXCExportDesignXml.dMsg("NotifyService.CIS_SVG_SERVICE_URL=" + NotifyService.CIS_SVG_SERVICE_URL);
            TestXCExportDesignXml.dMsg("NotifyService.CHS_AUTHENTICATION_VALUES[0]=" + NotifyService.CHS_AUTHENTICATION_VALUES[0]);
            TestXCExportDesignXml.dMsg("NotifyService.CHS_AUTHENTICATION_VALUES[1]=" + NotifyService.CHS_AUTHENTICATION_VALUES[1]);
            TestListDesignsClient listDesignsClient = new TestListDesignsClient(projectName, designName, designRevision, designDesc, NotifyService.CIS_SVG_SERVICE_URL);
            listDesignsClient.invoke();
            String designId = listDesignsClient.getDesignUid();
            TestXCExportDesignXml.dMsg("Extracted designId=" + designId);
            XCExportDesignXml xCExportDesignXml = new XCExportDesignXml(projectId, designName, designRevision, designDesc, designId, isStandaloneTest, sceGlobalsInstance, threadId);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dMsg(String msg) {
        System.out.println(msg);
    }
}

