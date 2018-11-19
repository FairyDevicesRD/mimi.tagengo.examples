import ai.fd.mimi.prism.ClientComCtrl;
import ai.fd.mimi.prism.ClientComCtrlExcepiton;
import ai.fd.mimi.prism.ResponseData;

import com.google.gson.Gson;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

class AccessTokenResponse {
    String accessToken;
}

class XMLSimpleParser {
    DocumentBuilderFactory dbFactory = null;
    DocumentBuilder dBuilder = null;
    Document doc = null;
    XPathFactory xpFactory = null;
    XPath xpath = null;

    public XMLSimpleParser(String xml) throws IOException, SAXException, ParserConfigurationException {
        dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        dBuilder = dbFactory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        doc = dBuilder.parse(is);
        xpFactory = XPathFactory.newInstance();
        xpath = xpFactory.newXPath();
    }

    public String getMT_OUTSentence() throws XPathExpressionException {
        XPathExpression exp = xpath.compile("/STML/MT_OUT/NBest[@Order='1']/s/text()");
        NodeList nodelist = (NodeList)exp.evaluate(doc, XPathConstants.NODESET);
        return nodelist.item(0).getNodeValue().trim();
    }

    public String getSR_OUTSentence() throws XPathExpressionException {
        XPathExpression exp = xpath.compile("/STML/SR_OUT/NBest[@Order='1']/s/text()");
        NodeList nodelist = (NodeList)exp.evaluate(doc, XPathConstants.NODESET);
        return nodelist.item(0).getNodeValue().trim();
    }
}

public class Main {
    static final String clientID = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
    static final String clientSecret = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
    static final String SRURL = "https://sandbox-sr.mimi.fd.ai";
    static final String SSURL = "https://sandbox-ss.mimi.fd.ai/speech_synthesis";
    static final String MTURL = "https://sandbox-mt.mimi.fd.ai/machine_translation";

    static final String SRRequestTemplate = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<STML UtteranceID=\"0\" Version=\"1\">\n" +
            "<User ID=\"N/A\"/>\n" +
            "<SR_IN Language=\"%s\">\n" +
            "<Voice/>\n" +
            "<InputAudioFormat Audio=\"RAW\" Endian=\"Little\" SamplingFrequency=\"16k\"/>\n" +
            "<OutputTextFormat Form=\"SurfaceForm\"/>\n" +
            "</SR_IN>\n" +
            "</STML>";

    static final String MTRequestTemplate = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<STML UtteranceID=\"0\" Version=\"1.0\">\n" +
            "<User ID=\"N/A\"/>\n" +
            "<MT_IN SourceLanguage=\"%s\" TargetLanguage=\"%s\">\n" +
            "<InputTextFormat Form=\"SurfaceForm\"/>\n" +
            "<OutputTextFormat Form=\"SurfaceForm\"/>\n" +
            "<s>%s</s>\n" +
            "</MT_IN>\n" +
            "</STML>\n";
    static final String SSRequestTemplate = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<STML UtteranceID=\"0\" Version=\"1\">\n" +
            "<User ID=\"N/A\"/>\n" +
            "<SS_IN Language=\"en\">\n" +
            "<Voice Age=\"30\" Gender=\"%s\"/>\n" +
            "<OutputAudioFormat Audio=\"RAW\" Endian=\"Little\" SamplingFrequency=\"16k\"/>\n" +
            "<InputTextFormat Form=\"SurfaceForm\"/>\n" +
            "<s Delimiter=\" \">%s</s>\n" +
            "</SS_IN>\n" +
            "</STML>";

    static String getAccessToken(String clientID, String clientSecret) throws IOException {
        URL url = new URL("https://auth.mimi.fd.ai/v2/token");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        Map<String, String> params = new LinkedHashMap<>();
        params.put("grant_type", "https://auth.mimi.fd.ai/grant_type/application_credentials");
        params.put("client_id", clientID);
        params.put("client_secret", clientSecret);
        params.put("scope", "https://apis.mimi.fd.ai/auth/nict-asr/http-api-service;" +
                "https://apis.mimi.fd.ai/auth/nict-tra/http-api-service;" +
                "https://apis.mimi.fd.ai/auth/nict-tts/http-api-service");

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
            Reader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuilder stringBuilder = new StringBuilder();
            for (int c; (c = in.read()) >= 0; ) {
                stringBuilder.append((char) c);
            }
            Gson gson = new Gson();
            AccessTokenResponse token = gson.fromJson(stringBuilder.toString(), AccessTokenResponse.class);
            return token.accessToken;
        } else {
            throw new IOException("[error] code: " + status + " " + connection.getResponseMessage());
        }
    }

    static ArrayList<byte[]> loadRawFile(String fileName) throws IOException {
        ArrayList<byte[]> binaryDataList = new ArrayList<byte[]>();
        FileInputStream fIn = new FileInputStream(new File(fileName));
        int readLen = 0;
        byte[] readBuf = new byte[1024];
        while ((readLen = fIn.read(readBuf, 0, readBuf.length)) != -1) {
            byte[] data = new byte[readLen];
            System.arraycopy(readBuf, 0, data, 0, readLen);
            binaryDataList.add(data);
        }
        return binaryDataList;
    }

    static int writeRawFile(String fileName, byte[] data) throws IOException {
        FileOutputStream fOut = new FileOutputStream(new File(fileName));
        fOut.write(data);
        fOut.close();
        return data.length;
    }

    public static void main(String[] args) throws IOException, ClientComCtrlExcepiton, SAXException, ParserConfigurationException, XPathExpressionException {
        // 0. Set up ClientComCtrl
        String accessToken = getAccessToken(clientID, clientSecret);
        ClientComCtrl client = new ClientComCtrl(accessToken);

        ResponseData response;
        String result;

        // 1. SR

        // Placeholder:
        // %s: Language (ja, en, es, fr, id, ko, my, th, vi, zh)
        final String SRRequest = String.format(SRRequestTemplate, "ja");

        client.setTransferEncodingChunked(true); // 分割送信モード
        client.request(SRURL, SRRequest); // XML リクエスト
        ArrayList<byte[]> binaryDataList = loadRawFile("data/test.raw");
        for(byte[] b : binaryDataList) {
            client.request(SRURL, b); // 複数回の音声リクエスト
        }
        response = client.request(SRURL); // リクエスト終了、結果を得る
        System.out.println("result: " + response.getXML());
        result = new XMLSimpleParser(response.getXML()).getSR_OUTSentence();
        System.out.println("result: " + result);

        // 2. MT

        // Placeholders:
        // %s: SourceLanguage (ja, en, es, fr, id, ko, my, th, vi, zh)
        // %s: TargetLanguage (ja, en, es, fr, id, ko, my, th, vi, zh)
        // %s: Sentence to translate
        final String MTRequest = String.format(MTRequestTemplate, "ja", "en", result);

        response = client.request(MTURL, MTRequest);
        System.out.println("result: " + response.getXML());
        result = new XMLSimpleParser(response.getXML()).getMT_OUTSentence();
        System.out.println("result: " + result);

        // 3. SS

        // Placeholders:
        // %s: Gender (Male, Female)
        // %s: Sentence to synthesize
        final String SSRequest = String.format(SSRequestTemplate, "Female", result);

        response = client.request(SSURL, SSRequest);
        System.out.println("result: " + response.getXML());
        int dataLength = response.getBinary().length;
        System.out.println("result binary: " + dataLength + "byte");
        writeRawFile("data/ss.raw", response.getBinary());
    }
}
