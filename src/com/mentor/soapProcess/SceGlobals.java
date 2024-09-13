package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 */
import java.awt.Color;
import java.awt.Container;
import java.io.File;
import java.io.FileWriter;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

public class SceGlobals {
    static String progVers = "PSA WebService version 7.0 Date: 2022/05/11";
    public static boolean debug = true;
    public static MessageWin mesWin = null;
    public static String mesWinTitle = "Component Update Messages";
    public static Runtime r = null;
    public static File memFo = new File("./memory.txt");
    public static FileWriter memOut = null;
    public static long startTime = 0L;
    public static String CHS_VERSION = "";
    //public static String resourcePath = ClassLoader.getSystemResource(".").getPath();
    //private static String propFilePathSource = SceGlobals.replaceSpacesInPath(String.valueOf(resourcePath) + "config/chs_cust.properties");
    //public static String propFilePath = propFilePathSource.replaceAll("/bin/config/", "/config/");
//    public static String configFilePath = Paths.get(resourcePathCIS, "config").toString();
//    public static String cisConfigFilePath = Paths.get(resourcePathCIS, "cis_config.properties").toString();
//    public static String propFilePath = Paths.get(resourcePathCIS, "chs_cust.properties").toString();

    private static final String PROPERTIES_FOLDER = "config";
    public static final String CHS_CUST_PROPERTIES_FILE = "chs_cust.properties";
    public static final String CIS_CONFIG_PROPERTIES_FILE = "cis_config.properties";

    public static String resourcePath = getResourcePath();
    public static String configFilePath = Paths.get(resourcePath, PROPERTIES_FOLDER).toString();
    public static String propFilePath = Paths.get(configFilePath, CHS_CUST_PROPERTIES_FILE).toString();
    public static String cisConfigFilePath = Paths.get(configFilePath, CIS_CONFIG_PROPERTIES_FILE).toString();

    public static String tempSvg = "temp/empty.svg";
    public static String tempTiff = "temp/test.tiff";
    public static String CustomU = "";
    public static String CustomP = "";
    public static String CustomS = "";
    public static String ChsU = "";
    public static String ChsP = "";
    public static String ChsS = "";
    public static String SyncModeActive = "";
    public static String CIS_SVG_OUTPUT = "";
    public static String CIS_OUTPUT_STORE = "femoutput";
    public static String CIS_PROPERTIES_PATH = "input/customTask/cis_config.properties";
    public static String CIS_URL = "";
    public static String CIS_U = "";
    public static String CIS_P = "";
    public static int CIS_TIMEOUT = 0;
    public static String NOTIFY_SVG_TIF_PATH = "";
    public static String FSCVALID_OUTPUT_FOLDER = "";
    public static int TIFF_CONV_MEM = 512;
    public static String PSA_OUTPUT = "";
    public static String PSA_URL = "";
    public static int PSA_TIMEOUT = 0;
    public static String DESIGN_NAME_CHECK = "";
    public static String DATE_FORMAT_UI = "dd/MM/yy";
    public static String DATE_FORMAT_DB = "dd-MMM-yy";
    public static SceEntryArea sceEA = null;
    public static D_SceEntryArea d_sceEA = null;
    public static final int labelColor = 0x666699;
    public static final int checkBoxBackColor = 0;
    public static final Color LABEL_COLOR = new Color(0x666699);
    public static final Color CHECK_BOX_BACK_COLOR = new Color(0x666699);
    public static String Default_Border_name = "";
    public static String TIFF_Name_Constant = "";
    public static int MAX_WINDOW_WIDTH = 1400;
    public static int MAX_WINDOW_HEIGHT_DIFF = 1;
    public static int MAX_WINDOW_HEIGHT = 700;
    public static int D_MAX_WINDOW_WIDTH = 1400;
    public static int D_MAX_WINDOW_HEIGHT_DIFF = 1;
    public static int D_MAX_WINDOW_HEIGHT = 700;
    public static int MAX_MESSAGE_WINDOW_WIDTH = 800;
    public static int MAX_MESSAGE_WINDOW_HEIGHT = 400;
    public static Container contentPane;
    public static SceQuery sceQuery;
    public static JScrollPane jscrollPanel;
    public static Container d_contentPane;
    public static D_SceQuery d_sceQuery;
    public static JScrollPane d_jscrollPanel;
    public static JTable sapTable;
    public static SortFilterModel sorter;
    public static Vector data;
    public static Vector header;
    public static String sce_number;
    public static String description;
    public static String userf1;
    public static String userf2;
    public static String userf3;
    public static String userf4;
    public static String libraryobject_id;
    public static String chsSuppPartId;
    public static JTable d_sapTable;
    public static SortFilterModel d_sorter;
    public static Vector d_data;
    public static Vector d_header;
    public static String d_sce_number;
    public static String d_sce_number_start_value;
    public static String d_actionCode;
    public static String d_haress_actionCode;
    public static String d_indiceSce;
    public static String d_service;
    public static String d_codeCader;
    public static String d_decoupage;
    public static String d_designation20;
    private String desName = "";
    private String fscvalidOutputFolderPath = "";
    private String notifySvgTiffPathInclusiveUser = "";
    private String cisSvgOutputFolderInclusiveUser = "";
    public static String projectID;
    public static boolean sceSelected;
    public static boolean d_sceSelected;
    public static boolean d_sceCanceled;
    public static String d_partDataMismatch;
    public static String d_dialogueInvocationCause;
    public static String d_AcrobatPath;
    public static String d_SCE_controlled_status;
    public static String[] d_SCE_controlled_status_array;
    public static Box suppQuery;
    public static String suppSelected;
    public static int suppSelectedRow;
    public static boolean finished;
    public static boolean d_finished;
    public static Vector checkBoxName;
    public static Vector checkBoxPointer;
    public static Vector checkBoxValueT;
    public static String opCodeQuery;
    public static String opCodeQueryTxt;
    public static Vector d_checkBoxName;
    public static Vector d_checkBoxPointer;
    public static Vector d_checkBoxValueT;
    public static String d_opCodeQuery;
    public static String d_opCodeQueryTxt;
    public static String d_dateQueryOp;
    public static String latIssueQuery;
    public static boolean d_latestIssue;
    public static String[] d_action_codes;
    public static String d_designType;
    public static String d_logicFilterQueryString;
    public static String d_integratorFilterQueryString;
    public static String d_harnessFilterQueryString;
    public static boolean d_harnessTempButtonSelected;
    public static int imgWidth;
    public static int imgHeight;
    public static String synGlobalCodesOutDir;
    public static String synGlobalCodesHours;
    public static String synGlobalCodesMinutes;
    public static String synGlobalCodesTime;
    public static String URL_Web_SERVICE_PLM;
    public static String LOGIN_Web_SERVICE_PLM;
    public static String PWD_Web_SERVICE_PLM;
    public static JTextField parentNewName;
    public static JTextField parentNewPartNumber;
    public static JTextField parentNewRevision;
    public static JTextField[] childNewName;
    public static JTextField[] childNewPartNumber;
    public static JTextField[] childNewRevision;
    public static SAPRecord updatedParentRecord;
    public static SAPRecord[] updatedChildRecords;
    public static final String DESIGN_ATTRIBUTES_DATA = "designattributesdata";
    public static final String DESIGN_ATTRIBUTES_CONTEXT = "designattributescontext";
    public static final String DIALOGUE_INVOCATION_CAUSE = "dialogueInvocationCause";
    public static D_SceQueryPanel d_sceQueryPanel;
    public static D_MultiSceQuery d_multiSceQuery;
    public static String PDF_Orientation;
    public static String PDF_Color;
    public static String PDF_Area;
    public static String PDF_Size;
    public static String FSCVALID_MAX_CONCURRENT_THREADS;
    public static boolean IS_CHS_CONNECTOR_MASTER;
    public static String CHS_CONNECTOR_HARNESS_HOST;
    public static int CHS_CONNECTOR_HARNESS_PORT;
    public static String CHS_CONNECTOR_LOGIC_HOST;
    public static int CHS_CONNECTOR_LOGIC_PORT;

    static {
        sce_number = "";
        description = "";
        userf1 = "";
        userf2 = "";
        userf3 = "";
        userf4 = "";
        libraryobject_id = "";
        chsSuppPartId = null;
        d_sce_number = "";
        d_sce_number_start_value = "";
        d_actionCode = "";
        d_haress_actionCode = "";
        d_indiceSce = "";
        d_service = "";
        d_codeCader = "";
        d_decoupage = "";
        d_designation20 = "";
        projectID = "";
        sceSelected = false;
        d_sceSelected = false;
        d_sceCanceled = false;
        d_partDataMismatch = "";
        d_dialogueInvocationCause = "";
        d_AcrobatPath = "";
        d_SCE_controlled_status = "";
        d_SCE_controlled_status_array = null;
        suppSelected = "";
        suppSelectedRow = -1;
        finished = false;
        d_finished = false;
        checkBoxName = new Vector();
        checkBoxPointer = new Vector();
        checkBoxValueT = new Vector();
        opCodeQuery = " and OP_CODE LIKE '%'";
        opCodeQueryTxt = "";
        d_checkBoxName = new Vector();
        d_checkBoxPointer = new Vector();
        d_checkBoxValueT = new Vector();
        d_opCodeQuery = " and ACTION_CODE LIKE '%'";
        d_opCodeQueryTxt = "";
        d_dateQueryOp = ">";
        latIssueQuery = "";
        d_latestIssue = true;
        d_harnessTempButtonSelected = false;
        synGlobalCodesOutDir = "C:/temp";
        synGlobalCodesHours = "";
        synGlobalCodesMinutes = "";
        synGlobalCodesTime = "";
        URL_Web_SERVICE_PLM = "";
        LOGIN_Web_SERVICE_PLM = "";
        PWD_Web_SERVICE_PLM = "";
        parentNewName = null;
        parentNewPartNumber = null;
        parentNewRevision = null;
        childNewName = null;
        childNewPartNumber = null;
        childNewRevision = null;
        updatedParentRecord = null;
        updatedChildRecords = null;
        d_sceQueryPanel = null;
        d_multiSceQuery = null;
    }

    private static String replaceSpacesInPath(String thePath) {
        String cleanPath = thePath;
        if (thePath.contains("%20") || thePath.contains(" ")) {
            String proPath = "";
            String[] path = thePath.split("%20");
            int i = 0;
            while (i < path.length) {
                proPath = String.valueOf(proPath) + path[i] + " ";
                ++i;
            }
            proPath.trim();
            cleanPath = proPath;
        }
        return cleanPath;
    }

    private static String setResourceFilePath()
    {
        Path localPath = null;
        try {
            // Use Paths.get() and URIs to ensure proper path handling
            localPath = Paths.get(ClassLoader.getSystemResource(".").toURI());

            // Remove leading slash for Windows paths
            if (localPath.toString().startsWith("/") && System.getProperty("os.name").toLowerCase().contains("win")) {
                localPath = Paths.get(localPath.toString().substring(1));
            }

        } catch (URISyntaxException e) {
            System.err.println("URI Syntax Exception: " + e.getMessage());
        }

        return localPath.toString();
    }

    private static String getResourcePath() {
        try {
            Path path = Paths.get(SceGlobals.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
            return path.toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getDesName() {
        return this.desName;
    }

    public void setDesName(String desName) {
        this.desName = desName;
    }

    public String getFscvalidOutputFolderPath() {
        return this.fscvalidOutputFolderPath;
    }

    public void setFscvalidOutputFolderPath(String userName) {
        this.fscvalidOutputFolderPath = FSCVALID_OUTPUT_FOLDER.endsWith("/") ? String.valueOf(FSCVALID_OUTPUT_FOLDER) + userName + "/" : String.valueOf(FSCVALID_OUTPUT_FOLDER) + "/" + userName + "/";
    }

    public void setNotivySvgTiffOutputFolderPath(String userName) {
        this.notifySvgTiffPathInclusiveUser = NOTIFY_SVG_TIF_PATH.endsWith("/") ? String.valueOf(NOTIFY_SVG_TIF_PATH) + userName + "/" : String.valueOf(NOTIFY_SVG_TIF_PATH) + "/" + userName + "/";
    }

    public String getNotivySvgTiffOutputFolderPath() {
        return this.notifySvgTiffPathInclusiveUser;
    }

    public void setCisSvgOutputFolderPath(String userName) {
        this.cisSvgOutputFolderInclusiveUser = CIS_SVG_OUTPUT.endsWith("/") ? String.valueOf(CIS_SVG_OUTPUT) + userName + "/" : String.valueOf(CIS_SVG_OUTPUT) + "/" + userName + "/";
    }

    public String getCisSvgOutputFolderPath() {
        return this.cisSvgOutputFolderInclusiveUser;
    }
}

