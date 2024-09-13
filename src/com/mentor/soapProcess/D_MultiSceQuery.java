package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 */
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import org.w3c.dom.Document;

public class D_MultiSceQuery
extends JFrame {
    public D_MultiSceQuery(String sce_number, Document doc, String childType, String designType) {
        SceGlobals.d_multiSceQuery = this;
        try {
            String currentDirectory = new File(".").getCanonicalPath();
            this.setIconImage(ImageIO.read(new File(String.valueOf(currentDirectory) + "\\images\\icon.png")));
        }
        catch (Exception e) {
            System.out.println("Could not load icon file.");
        }
        this.setTitle("Design Data Browser");
        this.setSize(new Dimension(1720, 750));
        this.toFront();
        this.setLocation(20, 20);
        this.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent e) {
                SceGlobals.d_finished = true;
                SceGlobals.d_sceSelected = false;
                SceGlobals.d_sceCanceled = true;
                SceGlobals.d_sce_number = "";
                D_MultiSceQuery.this.exitSceRecBrowser("Exit SCE Design Data Browser");
            }
        });
        D_SceQueryPanel d_sceQueryPanel = new D_SceQueryPanel(sce_number, designType);
        d_sceQueryPanel.setSize(970, this.getHeight() - 10);
        SceGlobals.d_sceEA.setPreferredSize(new Dimension(d_sceQueryPanel.getWidth() - 20, 130));
        SceGlobals.d_jscrollPanel.setPreferredSize(new Dimension(d_sceQueryPanel.getWidth() - 20, 528));
        D_SceUpdateArea d_sceUpdateArea = new D_SceUpdateArea(doc, childType);
        D_ButtonsMultiSceQuery d_buttonsMultiSceQuery = new D_ButtonsMultiSceQuery(doc, childType);
        JScrollPane updateScrollPane = new JScrollPane(d_sceUpdateArea);
        updateScrollPane.setPreferredSize(new Dimension(750, this.getHeight() - 10));
        updateScrollPane.setVerticalScrollBarPolicy(20);
        updateScrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Custom Naming"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        this.add((Component)d_sceQueryPanel, "Center");
        this.add((Component)updateScrollPane, "East");
        this.add((Component)d_buttonsMultiSceQuery, "South");
        this.setResizable(false);
    }

    public void exitSceRecBrowser(String exitType) {
        System.out.println(exitType);
        SceGlobals.d_finished = true;
        this.dispose();
    }
}

