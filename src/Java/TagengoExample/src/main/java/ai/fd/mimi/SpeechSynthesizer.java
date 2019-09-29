package ai.fd.mimi;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class SpeechSynthesizer {
    String url = "";
    String accessToken = "";

    public SpeechSynthesizer(String url, String accessToken) {
        this.url = url;
        this.accessToken = accessToken;
    }

    // required param
    public ResponseData synthesize(String text, String lang) throws IOException {
        return synthesize(text, "WAV", "Little", "unknown", "30", "yes", lang);
    }

    // optional param
    public ResponseData synthesize(String text, String audioFormat, String audioEndian, String gender, String age, String native_, String lang) throws IOException {
        URL host = new URL(url);
        HttpsURLConnection connection = (HttpsURLConnection) host.openConnection();

        Map<String, String> params = new LinkedHashMap<>();
        //================================
        // required
        params.put("text", text);
        params.put("engine", "nict");
        params.put("lang", lang);
        //================================
        // optional
        params.put("audio_format", audioFormat);
        params.put("audio_endian", audioEndian);
        params.put("gender", gender);
        params.put("age", age);
        params.put("native", native_);
        //================================
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, String> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        connection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        connection.setDoOutput(true);
        connection.getOutputStream().write(postDataBytes);
        connection.connect();

        final int status = connection.getResponseCode();
        if (status != HttpsURLConnection.HTTP_OK) {
            throw new IOException("[error] code: " + status + " " + connection.getResponseMessage());
        }

        // 音声バイナリを取得
        ByteArrayOutputStream binaryData = new ByteArrayOutputStream();
        InputStream is = connection.getInputStream();

        int readLen = 0;
        byte[] readBuf = new byte[2048];

        while ((readLen = is.read(readBuf, 0, readBuf.length)) != -1) {
            binaryData.write(readBuf, 0, readLen);
        }
        ResponseData responseData = new ResponseData();
        responseData.setBinary(binaryData.toByteArray());
        return responseData;
    }
}
