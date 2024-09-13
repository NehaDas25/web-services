package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.FileUtils
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

public class DSIHarnessMainNodeComponentsModifier {
    private String pathToDSIFile;
    private String[] originalLines;
    private DSIMainNodeComponentsGrouping dsiMainNodeComponentsGrouping;

    public DSIHarnessMainNodeComponentsModifier(String pathToDSIFile) throws IOException, ParserConfigurationException, SAXException {
        this.pathToDSIFile = pathToDSIFile;
        if (!new File(this.pathToDSIFile).exists()) {
            throw new FileNotFoundException("DSI File not found at: " + pathToDSIFile);
        }
        DSIFileReader dsiFileReader = new DSIFileReader(this.pathToDSIFile);
        this.originalLines = dsiFileReader.getLines();
        File propertiesFile = new File(SceGlobals.propFilePath);
        DSIMainNodeComponentsMapper dsiMainNodeComponentsMapper = new DSIMainNodeComponentsMapper(String.valueOf(propertiesFile.getParent()) + "\\dsi_section_comment_template.xml");
        Map<String, String> componentsMap = dsiMainNodeComponentsMapper.generateMap();
        if (componentsMap == null) {
            throw new NullPointerException("No map was created hence cannot group Harness Main Node Components in DSI file");
        }
        this.dsiMainNodeComponentsGrouping = new DSIMainNodeComponentsGrouping(pathToDSIFile, componentsMap);
    }

    public void modifyDSIHarnessMainNodeComponents() throws IOException {
        List<String> linesBeforeMainNodeComponents = this.getLinesBeforeMainNodeComponents();
        List<String> groupedMainNodeComponents = this.dsiMainNodeComponentsGrouping.getGroupedElementsWithComments();
        List<String> linesAfterMainNodeComponents = this.getLinesAfterMainNodeComponents();
        LinkedList<String> allElements = new LinkedList<String>();
        allElements.addAll(linesBeforeMainNodeComponents);
        allElements.addAll(groupedMainNodeComponents);
        allElements.addAll(linesAfterMainNodeComponents);
        StringBuilder sb = new StringBuilder();
        for (String line : allElements) {
            sb.append(line);
            sb.append("\r\n");
        }
        String newContents = sb.toString().trim();
        FileUtils.writeStringToFile((File)new File(this.pathToDSIFile), (String)newContents);
    }

    private List<String> getLinesBeforeMainNodeComponents() {
        return this.extractLines(0, this.dsiMainNodeComponentsGrouping.getFirstLineNumber() - 1);
    }

    private List<String> getLinesAfterMainNodeComponents() {
        return this.extractLines(this.dsiMainNodeComponentsGrouping.getLastLineNumber() + 1, this.originalLines.length - 1);
    }

    private List<String> extractLines(int start, int end) {
        return Arrays.stream(this.originalLines, start, end + 1).collect(Collectors.toList());
    }
}

