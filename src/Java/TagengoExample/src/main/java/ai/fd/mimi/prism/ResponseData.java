package ai.fd.mimi.prism;


import java.util.ArrayList;

public class ResponseData {
    private String message = null;
    private ArrayList<byte[]> binaryList = null;

    public String getXML() {
        return message;
    }

    public byte[] getBinary() {
        if (binaryList.size() == 0) {
            return null;
        }
        return binaryList.get(0);
    }

    public ArrayList<byte[]> getBinaryList() {
        return binaryList;
    }

    protected void setXML(String string) {
        message = string;
    }

    protected void setBinary(byte[] binary){
        binaryList.set(0, binary);
    }

    protected void setBinaryList(ArrayList<byte[]> binaryList) {
        this.binaryList = binaryList;
    }
}
