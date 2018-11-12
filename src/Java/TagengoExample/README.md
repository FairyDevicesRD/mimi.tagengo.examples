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



---
# 旧情報
## 準備

`./lib` 配下に `gson-2.8.5.jar` を配置します。

[配布元] : https://search.maven.org/artifact/com.google.code.gson/gson/2.8.5/jar

## ビルド手順(社内)
```
$ . create_jarlib.sh
$ java -classpath bin:lib/gson-2.8.5.jar:lib/NICTAPI.jar NICTAPISample data/AuthorizationParams.json "今日はいい天気ですね。"
```


## ビルド手順(コンテスト参加者)
```
$ mkdir bin
$ javac -d bin -classpath src:lib/gson-2.8.5.jar:lib/NICTAPI.jar src/NICTAPISample.java
$ java -classpath bin:lib/gson-2.8.5.jar:lib/NICTAPI.jar NICTAPISample data/AuthorizationParams.json "今日はいい天気ですね。"
```

## 納品物

納品前には `README.md`　及び `file/AuthorizationParams.json` を編集してください。

```
.
├── README.md ※要編集
├── data
│   └── AuthorizationParams.json ※要編集
├── lib
│   └── NICTAPI.jar
└── src
    └── NICTAPISample.java
```
