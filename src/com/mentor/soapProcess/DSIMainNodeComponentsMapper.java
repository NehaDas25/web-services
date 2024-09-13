package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 */
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

class DSIMainNodeComponentsMapper {
    private String pathToXMLFile;

    DSIMainNodeComponentsMapper(String pathToXMLFile) {
        this.pathToXMLFile = pathToXMLFile;
    }

    Map<String, String> generateMap() throws ParserConfigurationException, SAXException, IOException {
        LinkedHashMap<String, String> componentsMap = new LinkedHashMap<String, String>();
        File file = new File(this.pathToXMLFile);
        if (!file.exists()) {
            System.out.println("Harness Main Node Components sections grouping XML file not found at :" + this.pathToXMLFile);
            return null;
        }
        Document document = this.generateDocumentXML();
        if (document == null) {
            System.out.println("Invalid XML file :" + this.pathToXMLFile);
            return null;
        }
        NodeList harnessMainNodeComponents = document.getElementsByTagName("harnessmainnodecomponents");
        if (harnessMainNodeComponents == null || harnessMainNodeComponents.getLength() == 0) {
            System.out.println("Invalid XML format : " + this.pathToXMLFile);
            return null;
        }
        Element harnessMainNodeComponent = (Element)harnessMainNodeComponents.item(0);
        NodeList sections = harnessMainNodeComponent.getElementsByTagName("section");
        if (sections == null) {
            System.out.println("Invalid XML format : " + this.pathToXMLFile);
            return null;
        }
        if (sections.getLength() == 0) {
            return new HashMap<String, String>();
        }
        int i = 0;
        while (i < sections.getLength()) {
            Element section = (Element)sections.item(i);
            String componentTypeCode = section.getAttribute("componenttypecode");
            String name = section.getAttribute("name");
            componentsMap.put(componentTypeCode, name);
            ++i;
        }
        return componentsMap;
    }

    private Document generateDocumentXML() throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return docBuilder.parse(new File(this.pathToXMLFile));
    }
}

