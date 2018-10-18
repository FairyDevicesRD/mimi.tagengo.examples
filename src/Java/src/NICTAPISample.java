import ai.fd.stml.com.client.ClientComCtrl;
import ai.fd.stml.com.client.ResponseData;
import com.google.gson.Gson;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class NICTAPISample {
    public static void main(String[] args) {

        String url = "https://tra.mimi.fd.ai/machine_translation";
        String dummyXml = args[1];
        String sourceLanguage = "ja";
        String targetLanguage = "en";

        File settingFile = null;
        String clientID = null;
        String clientSecret = null;
        String accessToken = null;

        // 認証用設定ファイル読み込み
        if(args.length == 2 && args[0] != "") {
            settingFile = new File(args[0]);

        }
        if(settingFile.exists()) {
            try {
                FileReader freader = new FileReader(settingFile);
                BufferedReader bfreader = new BufferedReader(freader);
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bfreader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                Gson gson = new Gson();
                AuthorizationParams params = gson.fromJson(stringBuilder.toString(), AuthorizationParams.class);
                clientID = params.client_id;
                clientSecret = params.client_secret;
                System.out.println("clientID:" + clientID + " clientSecret:" + clientSecret);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        } else {
            System.err.println("File not found.");
            System.exit(-1);
        }

        //アクセストークン取得 & 翻訳API呼び出し
        ResponseData responseData = null;
        try {
            System.out.println("アクセストークンを取得します...");
            NICTAPISample sample = new NICTAPISample();
            accessToken = sample.getAccessToken(clientID, clientSecret);
            System.out.println("access token: " + accessToken);

            System.out.println("翻訳APIを実行します...");
            System.out.println("source language [" + sourceLanguage + "]: " + dummyXml);

            ClientComCtrl client = new ClientComCtrl(accessToken);
            responseData = client.request(url, dummyXml, sourceLanguage, targetLanguage);

            System.out.println("target language [" + targetLanguage + "]: " + responseData.getXML());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getAccessToken(String clientID, String clientSecret) throws IOException {
        URL url = new URL("https://auth.mimi.fd.ai/v2/token");
        HttpURLConnection connection  = (HttpURLConnection) url.openConnection();
        Map<String, String> params = new LinkedHashMap<>();
        params.put("grant_type", "https://auth.mimi.fd.ai/grant_type/application_credentials");
        params.put("client_id", clientID);
        params.put("client_secret", clientSecret);
        params.put("scope", "https://apis.mimi.fd.ai/auth/nict-tra/http-api-service"); //NICT翻訳スコープ

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
        if (status == HttpURLConnection.HTTP_OK) {
            String response = connection.getResponseMessage();
            Reader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuilder stringBuilder = new StringBuilder();
            for (int c; (c = in.read()) >= 0; ) {
                stringBuilder.append((char) c);
            }
            // mimi accesstokenをパース
            Gson gson = new Gson();
            AccessToken token = gson.fromJson(stringBuilder.toString(), AccessToken.class);
            return token.accessToken;
        } else {
            System.err.println("[error] code:" + status + " " + connection.getResponseMessage());
        }
        return "";
    }

    private class AuthorizationParams {
        String client_id;
        String client_secret;
    }

    private class AccessToken {
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


