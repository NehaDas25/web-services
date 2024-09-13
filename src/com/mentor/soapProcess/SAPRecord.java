package com.mentor.soapProcess;

/*
 * Decompiled with CFR 0.152.
 */
public class SAPRecord {
    public final String Action_Code;
    public final String Reference_PSA;
    public final String Indice_Plan;
    public final String Service;
    public final String Code_Cader;
    public final String Decoupage;
    public final String Plm_SAP_Description;
    public final String Modified_Date;

    public SAPRecord(String actionCode, String referencePSA, String indicePlan, String service, String codeCader, String decoupage, String plmSAPDescription, String modifiedDate) {
        this.Action_Code = actionCode;
        this.Reference_PSA = referencePSA;
        this.Indice_Plan = indicePlan;
        this.Service = service;
        this.Code_Cader = codeCader;
        this.Decoupage = decoupage;
        this.Plm_SAP_Description = plmSAPDescription;
        this.Modified_Date = modifiedDate;
    }
}

