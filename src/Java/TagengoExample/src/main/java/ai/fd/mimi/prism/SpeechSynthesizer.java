package ai.fd.mimi.prism;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class SpeechSynthesizer {
    String accessToken = "";

    protected SpeechSynthesizer(String accessToken) {
        this.accessToken = accessToken;
    }

    protected ResponseData synthesize(String url, RequestData requestData) throws IOException {
        URL host = new URL(url);
        HttpsURLConnection connection = (HttpsURLConnection) host.openConnection();

        Map<String, String> params = new LinkedHashMap<>();
        //================================
        // required
        params.put("text", requestData.sentence);
        params.put("engine", "nict");
        params.put("lang", requestData.language);
        //================================
        // optional
        params.put("audio_format", requestData.outputAudioFormatAudio);
        params.put("audio_endian", requestData.outputAudioFormatEndian);
        params.put("gender", requestData.voiceGender.toLowerCase());
        params.put("age", requestData.voiceAge);
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
        XMLUtil util = new XMLUtil();
        ResponseData responseData = new ResponseData();
        responseData.setXML(util.createResponseSS(requestData));
        responseData.setBinary(binaryData.toByteArray());

        return responseData;
    }

}
