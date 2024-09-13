package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 */
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class MessageWin
extends JFrame
implements ActionListener {
    protected JButton b1;
    private static final String b1String = "Close";
    public JTextArea textArea = new JTextArea();
    boolean showMesWin = false;
    boolean validationErrors = false;

    public MessageWin(boolean show) {
        this.setTitle(SceGlobals.mesWinTitle);
        this.setSize(SceGlobals.MAX_MESSAGE_WINDOW_WIDTH, SceGlobals.MAX_MESSAGE_WINDOW_HEIGHT);
        this.toFront();
        this.setLocation(400, 500);
        this.b1 = new JButton(b1String);
        this.b1.setActionCommand(b1String);
        this.b1.addActionListener(this);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(this.b1);
        this.textArea.setEditable(false);
        this.getContentPane().add((Component)new JScrollPane(this.textArea), "Center");
        this.textArea.append("");
        this.getContentPane().add((Component)buttonPanel, "South");
        if (show) {
            this.showMesWin = true;
            this.show();
        }
    }

    public void showWin() {
        this.showMesWin = true;
        this.show();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (b1String.equals(e.getActionCommand())) {
            this.dispose();
        }
    }

    public void addMess(String text, String messType) {
        this.textArea.append(text);
        if (messType.equals("error")) {
            System.out.println(" error");
            if (!this.showMesWin) {
                this.showMesWin = true;
                this.show();
            }
            if (!this.validationErrors) {
                this.validationErrors = true;
            }
        }
        this.setCaretPos();
    }

    public void setCaretPos() {
        this.textArea.setCaretPosition(this.textArea.getText().length());
    }

    public void resetErrors() {
        this.validationErrors = false;
    }

    public void disposeWithoutErrorsOrDebug() {
        if (!this.validationErrors) {
            if (!SceGlobals.debug) {
                this.dispose();
            }
            this.addMess("\nFinished messaging.", "info");
        } else {
            this.addMess("\nError(s) occured during validation.", "info");
        }
    }
}

