package ai.fd.mimi;

public class ResponseData {
    private String message = null;
    private byte[] binary = null;

    public String getJSON() {
        return message;
    }
    public byte[] getBinary() { return binary; }

    protected void setJSON(String string) {
        message = string;
    }
    protected void setBinary(byte[] binary){
        this.binary = binary;
    }
}
