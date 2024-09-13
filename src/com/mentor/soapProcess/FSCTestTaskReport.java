package com.mentor.soapProcess;

/*
 * Decompiled with CFR 0.152.
 */
public class FSCTestTaskReport {
    public static void main(String[] args) {
        try {
            NotifyService.CHS_AUTHENTICATION_VALUES[0] = "system";
            NotifyService.CHS_AUTHENTICATION_VALUES[1] = "manager";
            String projectName = "NK";
            String designName = "CompositeHarnessDesign";
            String designRevision = "comprev1";
            boolean isModuleDesign = false;
            ListDesignsClient listDesignsClient = new ListDesignsClient(projectName, designName, designRevision);
            listDesignsClient.invoke();
            String designUid = listDesignsClient.getDesignUid();
            System.out.println("designUid=" + designUid);
            if (designUid == null) {
                throw new Exception("Design with these parameters does not exit!");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

