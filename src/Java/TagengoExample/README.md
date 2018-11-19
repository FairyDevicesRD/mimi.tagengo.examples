# Java sample code


```
public class MimiAPI {
    private String accessToken = "";
    private STMLUtil util = null;

    public MimiAPI(String accessToken);

    public void setAccessToken();

    // HTTP API
    public ResponseData requestSR(String stml, List<byte[]> binaryList) throws MimiApiExcepiton;
    public ResponseData requestSS(String stml) throws MimiApiExcepiton;
    public ResponseData requestMT(String stml) throws MimiApiExcepiton;

    // WebSocket API
    public void setOnReceivedSRResponseListener(OnReceivedSRResponseListener listener);
    public boolean requestSR_start(String stml) throws MimiApiExcepiton;
    public boolean requestSR_add(List<byte[]> binaryList) throws MimiApiExcepiton ;
    public boolean requestSR_end() throws MimiApiExcepiton;
}

public class ResponseData {
    public ResponseData();
    public String getXML();
    public byte[] getBinary();
}

interface OnReceivedSRResponseListener {
    void onReceivedSRResponse(String response);
}
```
