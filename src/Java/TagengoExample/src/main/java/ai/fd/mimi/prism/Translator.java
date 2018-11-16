package ai.fd.mimi.prism;

import com.google.gson.Gson;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class Translator {
    private String accessToken;

    protected Translator(String accessToken) {
        this.accessToken = accessToken;
    }

    protected ResponseData translate(RequestData requestData) throws ClientComCtrlExcepiton, IOException {
        String result;
        HttpsURLConnection connection = null;
        URL host = new URL("https://sandbox-mt.mimi.fd.ai/machine_translation");
        connection = (HttpsURLConnection) host.openConnection();

        Map<String, String> params = new LinkedHashMap<>();
        params.put("source_lang", requestData.sourceLanguage);
        params.put("target_lang", requestData.targetLanguage);
        params.put("text", requestData.sentence);
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
        if (status == HttpsURLConnection.HTTP_OK) {
            Reader in = new BufferedReader((new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)));

            StringBuilder stringBuilder = new StringBuilder();
            String str;

            while ((str = ((BufferedReader) in).readLine()) != null) {
                //System.out.print(str);
                stringBuilder.append(str);
            }
            // 結果 JSON をパース
            Gson gson = new Gson();
            String[] results = gson.fromJson(stringBuilder.toString(),  String[].class);
            result = String.join(" ", results);

        } else {
            System.err.println("[error] code:" + status + " " + connection.getResponseMessage());
            throw new ClientComCtrlExcepiton(connection.getResponseMessage());
        }
        XMLUtil util = new XMLUtil();
        ResponseData responseData = new ResponseData();
        responseData.setXML(util.createResponseMT(requestData, result));
        return responseData;
    }
}
