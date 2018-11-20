package jp.fairydevices.mimi.example;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 依存ライブラリ：　GSON
 * build.gradle の dependencies に以下を追記
 * implementation 'com.google.code.gson:gson:2.8.5'
 */
class AccessToken {

    String getToken(String id, String secret) throws IOException {
        URL url = new URL("https://auth.mimi.fd.ai/v2/token");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        Map<String, String> params = new LinkedHashMap<>();
        params.put("grant_type", "https://auth.mimi.fd.ai/grant_type/application_credentials");
        params.put("client_id", id);
        params.put("client_secret", secret);
        params.put("scope", "https://apis.mimi.fd.ai/auth/nict-asr/http-api-service;https://apis.mimi.fd.ai/auth/nict-tra/http-api-service;https://apis.mimi.fd.ai/auth/nict-tts/http-api-service"); //NICT翻訳スコープ

        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, String> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        connection.setDoOutput(true);
        connection.getOutputStream().write(postDataBytes);
        connection.connect();
        final int status = connection.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK) {
            throw new IOException("[error] code: " + status + " " + connection.getResponseMessage());
        }
        String response = connection.getResponseMessage();
        Reader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        StringBuilder stringBuilder = new StringBuilder();
        for (int c; (c = in.read()) >= 0; ) {
            stringBuilder.append((char) c);
        }
        // mimi accesstokenをパース
        Gson gson = new Gson();
        AccessTokenSchema token = gson.fromJson(stringBuilder.toString(), AccessTokenSchema.class);
        return token.accessToken;
    }

    private class AccessTokenSchema {
        String selfLink;
        int expires_in;
        String kind;
        int progress;
        String status;
        String accessToken;
        String error;
        String operationId;
        int startTimestamp;
        int endTimestamp;
        String targetLink;
    }
}
