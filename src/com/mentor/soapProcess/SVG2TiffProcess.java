package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 */
import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class SVG2TiffProcess {
    public static void main(String[] args) {
        SVG2TiffProcess t = new SVG2TiffProcess();
        t.createTiff("C:/psafscvalid/test.svg", "C:/psafscvalid/zzz.tiff", false);
    }

    public void createTiff(final String filePathName, final String tiffFilePathName, final boolean deleteSVG) {
        Thread Svg2TiffThread = new Thread(){

            @Override
            public void run() {
                super.run();
                try {
                    boolean delete;
                    File svgFile;
                    ArrayList<String> cmd = new ArrayList<String>();
                    String workingDir = null;
                    workingDir = SceGlobals.resourcePath.contains("/bin") ? SceGlobals.resourcePath.replaceAll("/bin", "/Tiff") : String.valueOf(SceGlobals.resourcePath.trim()) + "Tiff";
                    if (workingDir.startsWith("/")) {
                        workingDir = workingDir.substring(1);
                    }
                    cmd.clear();
                    cmd.add(String.valueOf(workingDir) + "/RunConverter.bat");
                    cmd.add(filePathName);
                    cmd.add(tiffFilePathName);
                    cmd.add(Integer.toString(SceGlobals.TIFF_CONV_MEM));
                    ProcessBuilder processBuilder = new ProcessBuilder(cmd);
                    System.out.println(((Object)cmd).toString());
                    Map<String, String> procEnv = processBuilder.environment();
                    processBuilder.redirectErrorStream(true);
                    processBuilder.directory(new File(workingDir));
                    Process tiffProcess = processBuilder.start();
                    tiffProcess.waitFor();
                    if (deleteSVG && (svgFile = new File(filePathName)).exists() && !(delete = svgFile.delete())) {
                        System.err.println("Could not delete artifact: " + filePathName);
                    }
                    String tempFilenameRoot = "SVG2TIFF_TEMPFILE";
                    File ff = new File(tiffFilePathName);
                    String folderPath = ff.getParent();
                    File tmpfile = new File(folderPath);
                    File[] files = tmpfile.listFiles();
                    int i = 0;
                    while (i < files.length) {
                        if (files[i].getName().indexOf(tempFilenameRoot) != -1) {
                            files[i].delete();
                        }
                        ++i;
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println(ex.getMessage());
                }
            }
        };
        Svg2TiffThread.start();
        Svg2TiffThread.isAlive();
    }
}

