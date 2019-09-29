package ai.fd.mimi;

import com.google.gson.Gson;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class Translator {
    private String url = "";
    private String accessToken = "";

    public Translator(String url, String accessToken) {
        this.url = url;
        this.accessToken = accessToken;
    }

    public ResponseData translate(String text, String sourceLang, String targetLang) throws IOException {
        String result;
        HttpsURLConnection connection = null;
        URL host = new URL(url);
        connection = (HttpsURLConnection) host.openConnection();

        Map<String, String> params = new LinkedHashMap<>();
        params.put("source_lang", sourceLang);
        params.put("target_lang", targetLang);
        params.put("text", text);
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

        Reader in = new BufferedReader((new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)));

        StringBuilder stringBuilder = new StringBuilder();
        String str;

        while ((str = ((BufferedReader) in).readLine()) != null) {
            //System.out.print(str);
            stringBuilder.append(str);
        }
        // 結果 JSON をパース
        Gson gson = new Gson();
        String[] results = gson.fromJson(stringBuilder.toString(), String[].class);
        ResponseData responseData = new ResponseData();
        responseData.setJSON(String.join(" ", results));
        return responseData;
    }
}
