package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.server.Handler
 *  org.eclipse.jetty.server.Server
 *  org.eclipse.jetty.util.thread.QueuedThreadPool
 *  org.eclipse.jetty.webapp.WebAppContext
 */
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;

public class WebServ
extends JFrame {
    private static final long serialVersionUID = 1L;
    static String webServerProgVers = "WebServ version 2.1 created 2022/04/20";
    private Server m_server = null;
    private static final String WEB_SERVER_CLASS_PATH = "WebServerServlet";
    private static String WEB_SERVER_PORT = "3113";
    private static int WEB_SERVER_MAXTHREADS = 500;
    public static final String WEB_SERVER_CONTEXT = "/mgc/webservices";
    private JToggleButton jToggleButton1;
    private JLabel jLabel2;

    public WebServ() throws HeadlessException {
        System.out.println(webServerProgVers);
        System.out.println(SceGlobals.progVers + "\n");
        this.buildGUI();
    }

    public void buildGUI() {
        JPanel jPanel1 = new JPanel();
        jPanel1.setLayout(new BorderLayout());
        JLabel jLabel1 = new JLabel();
        jLabel1.setText("Click on Start to start the server");
        this.jToggleButton1 = new JToggleButton();
        this.jToggleButton1.setText("Start Server");
        this.jToggleButton1.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                WebServ.this.toggleServerActivation();
            }
        });
        this.jLabel2 = new JLabel();
        this.jLabel2.setText(" ");
        this.getContentPane().add((Component)jPanel1, "Center");
        jPanel1.add((Component)jLabel1, "North");
        jPanel1.add((Component)this.jToggleButton1, "Center");
        this.getContentPane().add((Component)this.jLabel2, "South");
    }

    private void toggleServerActivation() {
        if (this.jToggleButton1.getText().equalsIgnoreCase("Start Server")) {
            this.jToggleButton1.setText("Stop Server");
            try {
                this.startServer();
                this.jLabel2.setText("Server has been started successfully on port " + WEB_SERVER_PORT);
            }
            catch (Exception ex) {
                ex.printStackTrace();
                this.jLabel2.setText("Exception:" + ex.getMessage());
                this.jToggleButton1.setSelected(false);
                this.jToggleButton1.setText("Start Server");
            }
        } else {
            this.jToggleButton1.setText("Start Server");
            try {
                this.stopServer();
                this.jLabel2.setText("Server has been stopped successfully !!");
            }
            catch (Exception ex) {
                ex.printStackTrace();
                this.jLabel2.setText("Exception:" + ex.getMessage());
            }
        }
    }

    private void initServer() throws Exception {
        if (this.m_server == null) {
            String currentDir;
            System.setProperty("javax.xml.soap.SAAJMetaFactory", "com.sun.xml.messaging.saaj.soap.SAAJMetaFactoryImpl");
            int port = Integer.parseInt(WEB_SERVER_PORT);
            this.m_server = new Server(port);
            String webAppFolder = currentDir = WebServerServlet.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            this.pMsg("Web Application Folder:" + webAppFolder);
            WebAppContext webapp = new WebAppContext();
            webapp.setResourceBase(webAppFolder);
            webapp.addServlet(WebServerServlet.class, "/mgc/webservices/*");
            this.m_server.setHandler((Handler)webapp);
            if (WEB_SERVER_MAXTHREADS > 0) {
                QueuedThreadPool threadPool = new QueuedThreadPool();
                int minThreads = threadPool.getMinThreads();
                int maxThreads = threadPool.getMaxThreads();
                this.pMsg("Default number of socket min threads: " + minThreads);
                this.pMsg("Default number of socket max threads: " + maxThreads);
                threadPool.setMaxThreads(WEB_SERVER_MAXTHREADS);
                this.pMsg("New updated number of socket max threads: " + maxThreads);
            }
        }
    }

    private void pMsg(String msg) {
        System.out.println(msg);
    }

    public void startServer() throws Exception {
        this.initServer();
        if (this.m_server != null && this.m_server.isStarted()) {
            throw new Exception("Server already running on port " + WEB_SERVER_PORT);
        }
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        this.m_server.start();
    }

    public void stopServer() throws Exception {
        if (this.m_server == null) {
            throw new Exception("Server is not running");
        }
        this.m_server.stop();
    }

    public static void main(String[] args) throws Exception {
        WEB_SERVER_MAXTHREADS = -1;
        System.out.println("Number of command line arguments " + args.length);
        if (args.length == 0) {
            WEB_SERVER_PORT = "3113";
        } else if (args.length == 1) {
            WEB_SERVER_PORT = args[0];
        } else if (args.length == 2) {
            WEB_SERVER_PORT = args[0];
            WEB_SERVER_MAXTHREADS = Integer.parseInt(args[1]);
        } else {
            System.out.println("Since number of arguments is greater than 2 assuming the first argument is the port and using this as port " + args[0] + " ignoring other arguments");
            WEB_SERVER_PORT = args[0];
        }
        System.out.println("Setting port " + WEB_SERVER_PORT);
        if (WEB_SERVER_MAXTHREADS == -1) {
            System.out.println("Max number of socket threads not specified as second argument, so software default value of 256 will be used");
        } else {
            System.out.println("Setting max number of socket threads " + WEB_SERVER_MAXTHREADS);
        }
        NotifyService.readChsCustProps();
        boolean iS_CHS_CONNECTOR_MASTER = SceGlobals.IS_CHS_CONNECTOR_MASTER;
        if (iS_CHS_CONNECTOR_MASTER) {
            System.out.println("Is CHS CONNECTOR MASTER");
        } else {
            System.out.println("Is CHS CONNECTOR SLAVE");
        }
        WebServ server = new WebServ();
        server.setDefaultCloseOperation(3);
        server.setLocation(200, 200);
        server.setSize(350, 200);
        try {
            AddPeriodicProcess pP = new AddPeriodicProcess();
            pP.start();
            server.startServer();
            System.out.println("Server has been started successfully on port " + WEB_SERVER_PORT);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isServerRunning() {
        if (this.m_server != null) {
            return this.m_server.isStarted();
        }
        return false;
    }
}

