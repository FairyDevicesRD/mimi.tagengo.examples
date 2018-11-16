package ai.fd.mimi.prism;


import java.util.ArrayList;

public class ResponseData {
    private String message = null;
    private byte[] binary = null;

    public String getXML() {
        return message;
    }
    public byte[] getBinary() { return binary; }

    protected void setXML(String string) {
        message = string;
    }
    protected void setBinary(byte[] binary){
        this.binary = binary;
    }
}
