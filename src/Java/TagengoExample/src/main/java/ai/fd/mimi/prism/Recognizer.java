package ai.fd.mimi.prism;

import com.google.gson.Gson;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

class Recognizer {
    private String accessToken = "";

    protected Recognizer(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * @param requestData
     * @param binaryDataList
     * @param timeout        -1 : デフォルト値を使用
     * @return
     */
    protected ResponseData recognize(String url, RequestData requestData, List<byte[]> binaryDataList, int timeout) throws IOException {
        ArrayList<String> resultWord = null;
        String result = "";
        int dataLength = 0;
        URL host = null;
        HttpsURLConnection connection = null;
        for (byte[] data : binaryDataList) {
            dataLength += data.length;
        }

        host = new URL(url);
        connection = (HttpsURLConnection) host.openConnection();

        if (timeout != -1) {
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
        }

        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        //リクエストヘッダー
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        connection.setRequestProperty("Content-type", "audio/x-pcm;bit=16;rate=16000");
        connection.setRequestProperty("x-mimi-process", "nict-asr");
        connection.setRequestProperty("x-mimi-input-language", requestData.language);

        //System.out.println("dataLength: " + dataLength + "bytes");
        connection.setRequestProperty("Content-Length", String.valueOf(dataLength));
        connection.connect();

        //音声バイナリ送信
        OutputStream os = connection.getOutputStream();
        for (byte[] data : binaryDataList) {
            os.write(data);
        }
        os.flush();
        os.close();

        // レスポンスを取得
        final int status = connection.getResponseCode();
        if (status != HttpsURLConnection.HTTP_OK) {
            throw new IOException("[error] code: " + status + " " + connection.getResponseMessage());
        }
        String response = connection.getResponseMessage();
        //System.out.println("[response] code:" + status + " " + response);

        Reader in = new BufferedReader((new InputStreamReader(connection.getInputStream(), "UTF-8")));

        StringBuilder stringBuilder = new StringBuilder();
        String str = "";

        while ((str = ((BufferedReader) in).readLine()) != null) {
            //System.out.print(str);
            stringBuilder.append(str);
        }
        in.close();
        // jsonをパース
        //System.out.println("--[GSON parse]--------------------------------------");
        Gson gson = new Gson();
        MimiJSON json = gson.fromJson(stringBuilder.toString(), MimiJSON.class);
        //System.out.println("type: " + json.type);
        //System.out.println("session_id: " + json.session_id);
        //System.out.println("status: " + json.status);

        resultWord = new ArrayList<String>();
        for (MimiJSON.Response res : json.response) {
            //System.out.println("result: " + res.result);
            String[] st = res.result.split("\\|", 0);
            //result += st[0];
            resultWord.add(res.result);
        }
        //System.out.println("--[GSON parse]--------------------------------------");
        XMLUtil util = new XMLUtil();
        result = util.createResponseSR(requestData, resultWord);
        ResponseData responseData = new ResponseData();
        responseData.setXML(result);
        return responseData;
    }

    class MimiJSON {
        String type;
        String session_id;
        String status;
        List<Response> response;

        class Response {
            String result;
        }
    }
}
