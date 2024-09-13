package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class InclusiveFMCodeFilter {
    private static final String REFEDTAG = "refedtag";
    private static final String DESIGNFUNCTIONALMODULECODEMGR = "designfunctionalmodulecodemgr";
    private static final String PROJECTFUNCTIONALMODULECODEMGR = "projectfunctionalmodulecodemgr";
    private static final String INCLUSIVETAGS = "inclusivetags";
    private static final String REFEDTAGGROUP = "refedtaggroup";
    private Element elementHarnessDesign;
    private Map<String, String> designFunctionalModuleCodesList = new HashMap<String, String>();
    private List<String> unEligibleInclusiveFMs = new ArrayList<String>();
    private Map<String, String> projectFunctionalModuleCodesList;
    private Document designXmlDoc;

    InclusiveFMCodeFilter(Element elementHarnessDesign, Map<String, String> designFunctionalModuleCodesList, Map<String, String> projectFunctionalModuleCodesList, Document designXmlDoc) {
        this.elementHarnessDesign = elementHarnessDesign;
        this.designFunctionalModuleCodesList = designFunctionalModuleCodesList;
        this.projectFunctionalModuleCodesList = projectFunctionalModuleCodesList;
        this.designXmlDoc = designXmlDoc;
    }

    boolean isInclusiveFunctionalModuleEligible(String mainFMCode, String inclusiveFMCode) {
        if (this.checkWithCachedValues(mainFMCode)) {
            return true;
        }
        NodeList nodeListDesignTags = this.elementHarnessDesign.getElementsByTagName(DESIGNFUNCTIONALMODULECODEMGR);
        NodeList nodeListProjectTags = this.designXmlDoc.getElementsByTagName(PROJECTFUNCTIONALMODULECODEMGR);
        if (nodeListDesignTags.getLength() > 0) {
            int childUnderFMMgr = 0;
            while (childUnderFMMgr < nodeListDesignTags.getLength()) {
                Node node = nodeListDesignTags.item(childUnderFMMgr);
                if (node.getNodeType() == 1) {
                    String idInclusiveFMCode;
                    Element designTags = (Element)node;
                    NodeList nodeListTag = designTags.getElementsByTagName(INCLUSIVETAGS);
                    String idMainFMCode = this.designFunctionalModuleCodesList.get(mainFMCode);
                    if (this.isMultipleRefTagGroupChildExists(idMainFMCode, idInclusiveFMCode = this.designFunctionalModuleCodesList.get(inclusiveFMCode), nodeListTag)) {
                        this.unEligibleInclusiveFMs.add(mainFMCode);
                        return true;
                    }
                }
                ++childUnderFMMgr;
            }
        } else {
            int childUnderFMMgr = 0;
            while (childUnderFMMgr < nodeListProjectTags.getLength()) {
                Node node = nodeListProjectTags.item(childUnderFMMgr);
                if (node.getNodeType() == 1) {
                    String idInclusiveFMCode;
                    Element projectTags = (Element)node;
                    NodeList nodeListTag = projectTags.getElementsByTagName(INCLUSIVETAGS);
                    String idMainFMCode = this.projectFunctionalModuleCodesList.get(mainFMCode);
                    if (this.isMultipleRefTagGroupChildExists(idMainFMCode, idInclusiveFMCode = this.projectFunctionalModuleCodesList.get(inclusiveFMCode), nodeListTag)) {
                        this.unEligibleInclusiveFMs.add(mainFMCode);
                        return true;
                    }
                }
                ++childUnderFMMgr;
            }
        }
        return false;
    }

    private boolean checkWithCachedValues(String fmCodeTagRef) {
        return this.unEligibleInclusiveFMs.contains(fmCodeTagRef);
    }

    private boolean isMultipleRefTagGroupChildExists(String idMainFMCode, String idInclusiveFMCode, NodeList nodeListTag) {
        int childTag = 0;
        while (childTag < nodeListTag.getLength()) {
            Node nodeTag = nodeListTag.item(childTag);
            if (idMainFMCode.equals(nodeTag.getAttributes().item(0).getNodeValue()) && ((Element)nodeTag).getElementsByTagName(REFEDTAGGROUP).getLength() > 1) {
                return true;
            }
            ++childTag;
        }
        return false;
    }
}

