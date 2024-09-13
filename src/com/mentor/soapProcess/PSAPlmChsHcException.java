package com.mentor.soapProcess;

/*
 * Decompiled with CFR 0.152.
 */
public class PSAPlmChsHcException
extends Exception {
    private String errorCode;
    private String partNumber;
    private String soapReq;
    private String soapResp;
    private String processState;
    private String description;
    private StringBuilder errorMessage = new StringBuilder();

    public PSAPlmChsHcException(String errorCode, String partNumber, String processState, String description) {
        this.errorCode = errorCode;
        this.partNumber = partNumber;
        this.processState = processState;
        this.description = description;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public String getPartNumber() {
        return this.partNumber;
    }

    public String getSoapReq() {
        return this.soapReq;
    }

    public String getSoapResp() {
        return this.soapResp;
    }

    public String getProcessState() {
        return this.processState;
    }

    public String getDescription() {
        return this.description;
    }

    @Override
    public String getMessage() {
        this.errorMessage.setLength(0);
        this.setErrorMessage("ERROR_CODE", this.errorCode);
        this.setErrorMessage("PART_NUMBER", this.partNumber);
        this.setErrorMessage("SOAP_REQUEST_MESSAGE", this.soapReq);
        this.setErrorMessage("SOAP_RESPONSE_MESSAGE", this.soapResp);
        this.setErrorMessage("PROCESS_STATE", this.processState);
        this.setErrorMessage("DESCRIPTION", this.description);
        return this.errorMessage.toString();
    }

    private void setErrorMessage(String key, String value) {
        if (value != null) {
            this.errorMessage.append("\n[");
            this.errorMessage.append(key);
            this.errorMessage.append("]\n");
            this.errorMessage.append(value);
            this.errorMessage.append("\n");
        }
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public void setSoapReq(String soapReq) {
        this.soapReq = soapReq;
    }

    public void setSoapResp(String soapResp) {
        this.soapResp = soapResp;
    }

    public void setProcessState(String processState) {
        this.processState = processState;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

