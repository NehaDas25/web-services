package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 */
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class FSCTaskNameValueParameters
extends HashMap<String, String> {
    private static final long serialVersionUID = 1L;
    private static final String PARAMETER_TYPE = "Param";

    public FSCTaskNameValueParameters() {
    }

    public FSCTaskNameValueParameters(InputStream reader) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(reader);
            NodeList list = doc.getElementsByTagName(PARAMETER_TYPE);
            int length = list.getLength();
            int i = 0;
            while (i < length) {
                Node node = list.item(i);
                String attributeName = node.getAttributes().item(0).getNodeValue();
                String attributeValue = node.getAttributes().item(1).getNodeValue();
                this.put(attributeName, attributeValue);
                ++i;
            }
        }
        catch (ParserConfigurationException e) {
            throw new IllegalArgumentException(e);
        }
        catch (SAXException e) {
            throw new IllegalArgumentException(e);
        }
        catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String getStringParameter(String parameterName) {
        return (String)this.get(parameterName);
    }

    public void setStringParameter(String parameterName, String value) {
        this.put(parameterName, value);
    }

    public Double getDoubleParameter(String parameterName) {
        String value = (String)this.get(parameterName);
        try {
            return Double.parseDouble(value);
        }
        catch (NumberFormatException numberFormatException) {
            return null;
        }
    }

    public Integer getIntegerParameter(String parameterName) {
        String value = (String)this.get(parameterName);
        try {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException numberFormatException) {
            return null;
        }
    }

    public void setDoubleParameter(String parameterName, Double value) {
        this.setStringParameter(parameterName, String.valueOf(value));
    }

    public void setIntegerParameter(String parameterName, Integer value) {
        this.setStringParameter(parameterName, String.valueOf(value));
    }

    public void setBooleanParameter(String parameterName, Boolean value) {
        this.setStringParameter(parameterName, String.valueOf(value));
    }

    public Boolean getBooleanParameter(String parameterName) {
        String value = (String)this.get(parameterName);
        return Boolean.valueOf(value);
    }
}

