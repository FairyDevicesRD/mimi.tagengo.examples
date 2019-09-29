import ai.fd.mimi.ResponseData;
import ai.fd.mimi.SpeechRecognizer;
import ai.fd.mimi.SpeechSynthesizer;
import ai.fd.mimi.Translator;

import com.google.gson.Gson;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

class AccessTokenResponse {
    String accessToken;
}

public class Main implements SpeechRecognizer.OnMesseageListener{
    private static final String clientID = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    private static final String clientSecret = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    private static final String SRURL = "wss://sandbox-sr.mimi.fd.ai";
    private static final String SSURL = "https://sandbox-mt.mimi.fd.ai/speech_synthesis";
    private static final String MTURL = "https://sandbox-ss.mimi.fd.ai/machine_translation";

    private static String getAccessToken() throws IOException {
        URL url = new URL("https://auth.mimi.fd.ai/v2/token");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        Map<String, String> params = new LinkedHashMap<>();
        params.put("grant_type", "https://auth.mimi.fd.ai/grant_type/application_credentials");
        params.put("client_id", Main.clientID);
        params.put("client_secret", Main.clientSecret);
        params.put("scope", "https://apis.mimi.fd.ai/auth/nict-asr/websocket-api-service;" +
                "https://apis.mimi.fd.ai/auth/nict-tra/http-api-service;" +
                "https://apis.mimi.fd.ai/auth/nict-tts/http-api-service");

        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, String> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), StandardCharsets.UTF_8));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), StandardCharsets.UTF_8));
        }
        byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        connection.setDoOutput(true);
        connection.getOutputStream().write(postDataBytes);
        connection.connect();
        final int status = connection.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK) {
            throw new IOException("[error] code: " + status + " " + connection.getResponseMessage());
        }
        Reader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder stringBuilder = new StringBuilder();
        for (int c; (c = in.read()) >= 0; ) {
            stringBuilder.append((char) c);
        }
        Gson gson = new Gson();
        AccessTokenResponse token = gson.fromJson(stringBuilder.toString(), AccessTokenResponse.class);
        return token.accessToken;
    }

    private static ArrayList<byte[]> loadRawFile(String fileName) throws IOException {
        ArrayList<byte[]> binaryDataList = new ArrayList<>();
        FileInputStream fIn = new FileInputStream(new File(fileName));
        int readLen;
        byte[] readBuf = new byte[1024];
        while ((readLen = fIn.read(readBuf, 0, readBuf.length)) != -1) {
            byte[] data = new byte[readLen];
            System.arraycopy(readBuf, 0, data, 0, readLen);
            binaryDataList.add(data);
        }
        return binaryDataList;
    }

    private static void writeRawFile(String fileName, byte[] data) throws IOException {
        FileOutputStream fOut = new FileOutputStream(new File(fileName));
        fOut.write(data);
        fOut.close();
    }

    public static void main(String[] args) throws IOException {
        // 0. Set up ClientComCtrl
        String accessToken = getAccessToken();
        System.out.println("access token: " + accessToken);

        Main main = new Main();
        ResponseData response;

        // Machine Translation / 機械翻訳
        Translator translator = new Translator(MTURL, accessToken);

        // Placeholders:
        // %s: Sentence to translate
        // %s: SourceLanguage (ja, en, es, fr, id, ko, my, th, vi, zh)
        // %s: TargetLanguage (ja, en, es, fr, id, ko, my, th, vi, zh)

        String before_text = "今日はいい天気ですね。";
        response = translator.translate(before_text, "ja", "en");
        System.out.println("[MT] input:  " + before_text);
        System.out.println("[MT] output: " + response.getJSON());


        // Speech Synthesis / 音声合成
        SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(SSURL, accessToken);

        // Placeholders: [required params]
        // %s: text to synthesize
        // %s: lang (ja, en, id, ko, my, th, vi, zh)
        String input_text = "今日はいい天気ですね。";
        response = speechSynthesizer.synthesize(input_text, "ja");

        // Placeholders: [optional params]
        // %s: text to synthesize
        // %s: audio_format (WAV, RAW, ADPCM, Speex)
        // %s: audio_endian (Little, Big)
        // %s: gender (female, male, unknown)
        // %s: age (30)
        // %s: native (yes, no)
        // %s: lang (ja, en, id, ko, my, th, vi, zh)
        // speechSynthesizer.synthesize("今日はいい天気ですね", "WAV", "Little", "unknown", "30", "yes", "ja");

        writeRawFile("data/ss.wav", response.getBinary());
        System.out.println("[SS] input:  " + input_text);
        System.out.println("[SS] output: data/ss.wav (" + response.getBinary().length + "bytes)");

        // Speech Recognition / 音声認識
        // Placeholder:
        // %s: InputLanguage (ja, en, es, fr, id, ko, my, th, vi, zh)
        SpeechRecognizer speechRecognizer = new SpeechRecognizer(SRURL, accessToken, "ja");
        speechRecognizer.setOnMessageListener(main);

        ArrayList<byte[]> binaryDataList = loadRawFile("data/test.raw");
        for (byte[] b : binaryDataList) {
            speechRecognizer.sendByteDate(b);
        }
        // 音声の終端を表明
        speechRecognizer.sendRecogBreak();
    }

    // FIXME: 音声認識 コールバックメソッド
    @Override
    public void onOpen(short status) {
        System.out.println("[SR] onOpen: " + status);
    }

    @Override
    public void onMessage(String message) {
        System.out.println("[SR] onMessage: " + message);
    }

    @Override
    public void onClose(int code, String reason) {
        System.out.println("[SR] onClose: " + code);
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("[SR] onError: " + ex.getMessage());
    }
}
