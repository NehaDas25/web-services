package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 */
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class DSIMainNodeComponentsGrouping {
    private String pathToDSIFile;
    private String[] lines;
    private int firstIndexOfMainNodeComponents;
    private int lastIndexOfMainNodeComponents;
    private List<String> nonGroupedElements;
    private Map<String, List<String>> groupedElements;
    private static final String HARNESS_MAIN_NODE_COMPONENTS = "Harness main node components";
    private static final String COMMENT = "!";
    private static final String SECTION = "%";
    private Map<String, String> subSections;
    private static final int COMPONENT_TYPE_CODE_INDEX = 4;

    DSIMainNodeComponentsGrouping(String pathToDSIFile, Map<String, String> subSections) throws IOException {
        this.pathToDSIFile = pathToDSIFile;
        this.firstIndexOfMainNodeComponents = -1;
        this.lastIndexOfMainNodeComponents = -1;
        this.nonGroupedElements = new LinkedList<String>();
        this.groupedElements = new HashMap<String, List<String>>();
        for (String key : subSections.keySet()) {
            this.groupedElements.put(key, new LinkedList());
        }
        this.subSections = subSections;
        this.setHarnessMainNodeComponentsSectionRange();
    }

    private void setHarnessMainNodeComponentsSectionRange() throws IOException {
        DSIFileReader dsiFileReader = new DSIFileReader(this.pathToDSIFile);
        this.lines = dsiFileReader.getLines();
        if (this.lines == null || this.lines.length == 0) {
            System.out.println("Could not read DSI file contents or empty file");
            return;
        }
        boolean started = false;
        int i = 0;
        while (i < this.lines.length) {
            if (this.lines[i].startsWith(SECTION)) {
                if (started) {
                    this.lastIndexOfMainNodeComponents = i - 1;
                    break;
                }
                if (this.lines[i].endsWith(HARNESS_MAIN_NODE_COMPONENTS)) {
                    started = true;
                    this.firstIndexOfMainNodeComponents = i + 1;
                }
            }
            ++i;
        }
    }

    void groupAllElements() {
        if (this.getFirstLineNumber() == -1 || this.getLastLineNumber() == -1) {
            System.out.println("Unable to find range of Harness Main Node Components Section - cannot group all elements");
            return;
        }
        int i = this.getFirstLineNumber();
        while (i <= this.getLastLineNumber()) {
            if (!this.lines[i].startsWith(COMMENT) && !this.lines[i].startsWith(SECTION)) {
                String[] record = this.lines[i].split(":");
                if (this.subSections.containsKey(record[4])) {
                    this.groupedElements.get(record[4]).add(this.lines[i]);
                } else {
                    this.nonGroupedElements.add(this.lines[i]);
                }
            }
            ++i;
        }
    }

    public List<String> getGroupedElementsWithComments() {
        this.groupAllElements();
        LinkedList<String> elements = new LinkedList<String>();
        for (String key : this.subSections.keySet()) {
            List<String> components = this.groupedElements.get(key);
            if (components.size() == 0) {
                elements.add("! " + this.subSections.get(key));
                elements.add("! None");
            } else {
                elements.add("! " + this.subSections.get(key));
                elements.addAll(components);
            }
            elements.add(COMMENT);
        }
        elements.addAll(this.nonGroupedElements);
        if (elements.size() == 0) {
            elements.add("! None");
            elements.add(COMMENT);
            return elements;
        }
        if (this.nonGroupedElements.size() > 0) {
            elements.add(COMMENT);
        }
        return elements;
    }

    public int getFirstLineNumber() {
        return this.firstIndexOfMainNodeComponents;
    }

    public int getLastLineNumber() {
        return this.lastIndexOfMainNodeComponents;
    }
}

