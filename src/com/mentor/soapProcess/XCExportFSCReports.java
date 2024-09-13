package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 */
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XCExportFSCReports {
    private String threadId;
    private Document designReportDocument;
    public static final String ELEM_DESIGNHASOPTIONS = "designhasoptions";
    public static final String ELEM_DESIGNREPORT = "designreport";
    public static final String ELEM_DESIGNTYPE = "designtype";
    public static final String ELEM_HARNESSWEIGHTS = "harnessweights";
    public static final String ELEM_HARNESSWEIGHT = "harnessweight";
    public static final String ELEM_WEIGHT = "weight";
    public static final String ELEM_INTERNALHARNESSNAME = "internalharnessname";
    public static final String ELEM_INTERNALHARNESSISSUE = "internalharnessissue";
    public static final String ELEM_DESIGNNAME = "designname";
    public static final String ELEM_TERMINALMATERIALCODES = "terminalmaterialcodes";
    public static final String ELEM_VALUE = "value";
    public static final String ELEM_CONN_PIN = "conn_pin";
    public static final String ELEM_TMC = "tmc";
    public static final String ATTR_INTERNALDATE = "internaldate";
    public static final String ATTR_CUSTDATE = "custdate";
    public static final String ELEM_HARNESSREGISTER = "harnessregister";
    public static final String ELEM_APPLICABLEFMCODES = "applicablefmcodes";
    public static final String ELEM_APPLICABLEFMCODE = "applicablefmcode";
    public static final String ELEM_HARNESSDESIGN = "harnessdesign";
    public static final String PROP_ATTR_VAL = "val";
    public static final String PROP_ATTR_NAME = "name";
    public static final String ELEM_PROPERTY = "property";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_VAL = "val";
    public static final String ATTR_ISCOMPOSITE = "iscomposite";
    public static final String ELEM_UNJUSTIFIEDFUNCTIONALMODULECODES = "unjustifiedfunctionalmodulecodes";
    public static final String ELEM_UNJUSTIFIEDFUNCTIONALMODULECODE = "unjustifiedfunctionalmodulecode";
    public static String MAPSEPARATOR = "####";

    public XCExportFSCReports(String projectName, String designId, String outPutFileName, String author, SceGlobals sceGlobalsInstance, String threadId) throws Exception {
        this.threadId = threadId;
        String taskFilePath = String.valueOf(sceGlobalsInstance.getFscvalidOutputFolderPath()) + "/" + outPutFileName + "TaskParameters.xml";
        try {
            this.pMsgThread("Preparing FSC-Reports task paramter " + taskFilePath);
            FSCReportTaskParameters parameters = new FSCReportTaskParameters();
            parameters.setProjectName(projectName);
            parameters.setDesignUid(designId);
            parameters.setFilePathName(outPutFileName);
            parameters.setAuthor(author);
            parameters.writeTaskXmlFile(taskFilePath);
            this.pMsgThread("Start custom task for FSCFAI & FSCNOM reports generation on design " + outPutFileName);
            FSCExecuteCustomTaskClient client = new FSCExecuteCustomTaskClient("FSC " + outPutFileName, "com.inetpsa.cpm.capital.plugin.task.FSCReportTask", outPutFileName, threadId);
            client.invoke(false, taskFilePath);
            this.designReportDocument = client.getDesignReportDocument();
        }
        finally {
            File taskFile = new File(taskFilePath);
            if (taskFile.exists()) {
                taskFile.delete();
            }
        }
    }

    public void pMsgThread(String msg) {
        System.out.println(String.valueOf(this.threadId) + ": " + msg);
    }

    public HashMap<String, String> getAllHarnessDesignWeights() {
        HashMap<String, String> designWeights = new HashMap<String, String>();
        Element elemDesignReport = this.designReportDocument.getDocumentElement();
        Element elemHarnessWeights = XmlHelper.getFirstElementWithName(ELEM_HARNESSWEIGHTS, elemDesignReport);
        ArrayList<Element> allHarnessWeigthElements = XmlHelper.getAllChildrenElementsWithName(ELEM_HARNESSWEIGHT, elemHarnessWeights);
        for (Element elementHarnessWeight : allHarnessWeigthElements) {
            Element elemInternalHarnessname = XmlHelper.getFirstElementWithName(ELEM_INTERNALHARNESSNAME, elementHarnessWeight);
            Element elemDesignName = XmlHelper.getFirstElementWithName(ELEM_DESIGNNAME, elementHarnessWeight);
            Element elemWeight = XmlHelper.getFirstElementWithName(ELEM_WEIGHT, elementHarnessWeight);
            String mapKey = String.valueOf(elemInternalHarnessname.getTextContent()) + MAPSEPARATOR + elemDesignName.getTextContent();
            designWeights.put(mapKey, elemWeight.getTextContent());
        }
        return designWeights;
    }

    public boolean hasDesignOptions() {
        Element elemDesignReport = this.designReportDocument.getDocumentElement();
        Element elemDesignHasOptions = XmlHelper.getFirstElementWithName(ELEM_DESIGNHASOPTIONS, elemDesignReport);
        if (elemDesignHasOptions != null && elemDesignHasOptions.getTextContent() != null) {
            String true_false = elemDesignHasOptions.getTextContent();
            boolean parseBoolean = Boolean.parseBoolean(true_false);
            return parseBoolean;
        }
        return false;
    }

    public ArrayList<String> getUnjustifiedModuleCodeList() {
        ArrayList<String> unjustifiedModuleCodeList = new ArrayList<String>();
        Element elemDesignReport = this.designReportDocument.getDocumentElement();
        Element elemUnjustifiedCodes = XmlHelper.getFirstElementWithName(ELEM_UNJUSTIFIEDFUNCTIONALMODULECODES, elemDesignReport);
        if (elemUnjustifiedCodes != null) {
            ArrayList<Element> listAllUnjustifiedElements = XmlHelper.getAllChildrenElementsWithName(ELEM_UNJUSTIFIEDFUNCTIONALMODULECODE, elemUnjustifiedCodes);
            for (Element elementUnjustified : listAllUnjustifiedElements) {
                unjustifiedModuleCodeList.add(elementUnjustified.getTextContent());
            }
        }
        return unjustifiedModuleCodeList;
    }

    public HashMap<String, String> getAllTerminalMaterialCodes() {
        HashMap<String, String> terminalMaterialCodesMap = new HashMap<String, String>();
        Element elemDesignReport = this.designReportDocument.getDocumentElement();
        Element elemTerminalMaterialCodes = XmlHelper.getFirstElementWithName(ELEM_TERMINALMATERIALCODES, elemDesignReport);
        ArrayList<Element> listAllTmcElements = XmlHelper.getAllChildrenElementsWithName(ELEM_TMC, elemTerminalMaterialCodes);
        for (Element elementTmc : listAllTmcElements) {
            Element elemeConnPin = XmlHelper.getFirstElementWithName(ELEM_CONN_PIN, elementTmc);
            Element elemeValue = XmlHelper.getFirstElementWithName(ELEM_VALUE, elementTmc);
            terminalMaterialCodesMap.put(elemeConnPin.getTextContent(), elemeValue.getTextContent());
        }
        return terminalMaterialCodesMap;
    }

    public boolean isCompositeDesign() {
        Element elemDesignReport = this.designReportDocument.getDocumentElement();
        Element elemDesignType = XmlHelper.getFirstElementWithName(ELEM_DESIGNTYPE, elemDesignReport);
        if (elemDesignType != null) {
            String designTypeValue = elemDesignType.getTextContent();
            if (DESIGNTYPES.composite.name().equals(designTypeValue)) {
                return true;
            }
        }
        return false;
    }

    public boolean isFmModuleDesign() {
        Element elemDesignReport = this.designReportDocument.getDocumentElement();
        Element elemDesignType = XmlHelper.getFirstElementWithName(ELEM_DESIGNTYPE, elemDesignReport);
        if (elemDesignType != null) {
            String designTypeValue = elemDesignType.getTextContent();
            if (DESIGNTYPES.fmodule.name().equals(designTypeValue)) {
                return true;
            }
        }
        return false;
    }

    public boolean isDerivativeDesign() {
        Element elemDesignReport = this.designReportDocument.getDocumentElement();
        Element elemDesignType = XmlHelper.getFirstElementWithName(ELEM_DESIGNTYPE, elemDesignReport);
        if (elemDesignType != null) {
            String designTypeValue = elemDesignType.getTextContent();
            if (DESIGNTYPES.derivative.name().equals(designTypeValue)) {
                return true;
            }
        }
        return false;
    }

    public Element getFunctionalHarnessDesignElementByFMCode(String fmCode) {
        if (fmCode == null) {
            return null;
        }
        Element elemDesignReport = this.designReportDocument.getDocumentElement();
        for (Element elemHarnessDesign : XmlHelper.getAllChildrenElementsWithName(ELEM_HARNESSDESIGN, elemDesignReport)) {
            Element elemApplicableFmCodes;
            boolean thisIsACompositeDesign = Boolean.parseBoolean(elemHarnessDesign.getAttribute(ATTR_ISCOMPOSITE));
            if (thisIsACompositeDesign || (elemApplicableFmCodes = XmlHelper.getFirstElementWithName(ELEM_APPLICABLEFMCODES, elemHarnessDesign)) == null) continue;
            ArrayList<Element> allFmCodeElements = XmlHelper.getAllChildrenElementsWithName(ELEM_APPLICABLEFMCODE, elemApplicableFmCodes);
            for (Element elementFmCode : allFmCodeElements) {
                String fmCodeName = elementFmCode.getAttribute("name");
                if (!fmCode.equals(fmCodeName)) continue;
                return elemHarnessDesign;
            }
        }
        return null;
    }

    private String getAttributeValueFromHarnessRegister(String attrName, Element hrDesign) throws Exception {
        if (hrDesign == null) {
            return "";
        }
        Element elemHarnessRegister = XmlHelper.getFirstElementWithName(ELEM_HARNESSREGISTER, hrDesign);
        if (elemHarnessRegister != null) {
            String attributeValue = elemHarnessRegister.getAttribute(attrName);
            return attributeValue;
        }
        throw new Exception("Could not extract attribute " + attrName + " from harnessregister element of harnessdesign!");
    }

    private String getAttributeValueFromHarnessDesign(String attrName, Element hrDesign) throws Exception {
        if (hrDesign == null) {
            return "";
        }
        String attributeValue = hrDesign.getAttribute(attrName);
        if (attributeValue != null) {
            return attributeValue;
        }
        throw new Exception("Could not extract attribute " + attrName + " from harnessdesign!");
    }

    public String getCustomerHarnessNo(Element hrDesign) throws Exception {
        return this.getAttributeValueFromHarnessRegister("customerharnessname", hrDesign);
    }

    public String getCustomerIssue(Element hrDesign) throws Exception {
        return this.getAttributeValueFromHarnessRegister("customerharnessissue", hrDesign);
    }

    public String getCustomerDate(Element hrDesign) throws Exception {
        return this.getAttributeValueFromHarnessRegister(ATTR_CUSTDATE, hrDesign);
    }

    public String getInternalHarnessNo(Element hrDesign) throws Exception {
        return this.getAttributeValueFromHarnessRegister(ELEM_INTERNALHARNESSNAME, hrDesign);
    }

    public String getInternalIssue(Element hrDesign) throws Exception {
        return this.getAttributeValueFromHarnessRegister(ELEM_INTERNALHARNESSISSUE, hrDesign);
    }

    public String getInternalDate(Element hrDesign) throws Exception {
        return this.getAttributeValueFromHarnessRegister(ATTR_INTERNALDATE, hrDesign);
    }

    public String getCustomerName(Element hrDesign) throws Exception {
        return this.getAttributeValueFromHarnessRegister("customername", hrDesign);
    }

    public String getInternalManufacturingSite(Element hrDesign) throws Exception {
        return this.getAttributeValueFromHarnessRegister("manufacturingsitename", hrDesign);
    }

    public String getWeight(Element hrDesign, String fmCode) throws Exception {
        String weightFromDesignXml = this.getAttributeValueFromHarnessRegister(ELEM_WEIGHT, hrDesign).trim();
        this.pMsgThread("Code" + fmCode + " weightFromDesignXml: " + weightFromDesignXml);
        String harnessWeight = String.format("%.03f", Double.parseDouble(weightFromDesignXml) / 1000.0).replace(',', '.');
        this.pMsgThread("Setting weight " + harnessWeight + "  in DSI for module design with code " + fmCode);
        return harnessWeight;
    }

    public String getUserField1(Element hrDesign) throws Exception {
        return this.getAttributeValueFromHarnessRegister("userfield1", hrDesign).trim();
    }

    public String getUserField2(Element hrDesign) throws Exception {
        return this.getAttributeValueFromHarnessRegister("userfield2", hrDesign).trim();
    }

    public String getUserField3(Element hrDesign) throws Exception {
        return this.getAttributeValueFromHarnessRegister("userfield3", hrDesign).trim();
    }

    public String getUserField4(Element hrDesign) throws Exception {
        return this.getAttributeValueFromHarnessRegister("userfield4", hrDesign).trim();
    }

    public String getUserField5(Element hrDesign) throws Exception {
        return this.getAttributeValueFromHarnessRegister("userfield5", hrDesign).trim();
    }

    public String getPropertyChecker(Element hrDesign) {
        ArrayList<Element> allProperties = XmlHelper.getAllChildrenElementsWithName(ELEM_PROPERTY, hrDesign);
        for (Element elementProperty : allProperties) {
            String propertyName = elementProperty.getAttribute("name");
            if (!propertyName.equals("Checker")) continue;
            return elementProperty.getAttribute("val");
        }
        return "";
    }

    public String getTrim(Element hrDesign) throws Exception {
        String trim = this.getAttributeValueFromHarnessRegister("trimlev", hrDesign).trim();
        if ("".equals(trim)) {
            return "None";
        }
        return trim;
    }

    public String getLRHand(Element hrDesign) throws Exception {
        return this.getAttributeValueFromHarnessRegister("lrhand", hrDesign);
    }

    public String getTubeAddOnType(Element hrDesign) throws Exception {
        String type = this.getAttributeValueFromHarnessRegister("tubefactort", hrDesign);
        return type;
    }

    public String getTubeAddOnFactor(Element hrDesign) throws Exception {
        return this.getAttributeValueFromHarnessRegister("tubefactor", hrDesign);
    }

    public String getManualTerminal(Element hrDesign) throws Exception {
        return this.getAttributeValueFromHarnessRegister("manual_term", hrDesign);
    }

    public String getSealedConnectors(Element hrDesign) throws Exception {
        return this.getAttributeValueFromHarnessDesign("sealed", hrDesign);
    }

    public static enum DESIGNTYPES {
        composite,
        fmodule,
        derivative;

    }
}

