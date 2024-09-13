package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 */
import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class XmlHelper {
    XmlHelper() {
    }

    public static Element getFirstElementWithName(String elemName, Element startElement) {
        if (startElement == null) {
            return null;
        }
        NodeList nodeList = startElement.getElementsByTagName(elemName);
        int i = 0;
        while (i < nodeList.getLength()) {
            Element elem;
            Node item = nodeList.item(i);
            if (item.getNodeType() == 1 && (elem = (Element)item).getNodeName().equals(elemName)) {
                return elem;
            }
            ++i;
        }
        return null;
    }

    public static ArrayList<Element> getAllChildrenElementsWithName(String elemName, Element startElement) {
        ArrayList<Element> ans = new ArrayList<Element>();
        if (startElement == null) {
            return ans;
        }
        NodeList nodeList = startElement.getElementsByTagName(elemName);
        int i = 0;
        while (i < nodeList.getLength()) {
            Element elem;
            Node item = nodeList.item(i);
            if (item.getNodeType() == 1 && (elem = (Element)item).getNodeName().equals(elemName)) {
                ans.add(elem);
            }
            ++i;
        }
        return ans;
    }
}

