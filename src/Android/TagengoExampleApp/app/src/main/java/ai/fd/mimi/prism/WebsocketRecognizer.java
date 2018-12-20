package ai.fd.mimi.prism;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import jp.fairydevices.mimi.io.MimiIO;
import jp.fairydevices.mimi.io.MimiIOException;
import jp.fairydevices.mimi.io.RxEvent;
import jp.fairydevices.mimi.io.TxEvent;


class WebSocketRecognizer implements MimiIO.OnTxListener, MimiIO.OnRxListener {

    private String accessToken = "";
    private RequestData requestData = null;
    private LinkedBlockingQueue<byte[]> sendQueue = null;

    private MimiIO mio = null;
    private String host = "sandbox-sr.mimi.fd.ai";
    private int port = 443;
    private MimiIO.OnTxListener txListener = this;
    private MimiIO.OnRxListener rxListener = this;
    private MimiIO.Format format = MimiIO.Format.RAW_PCM;
    private int samplingrate = 16000;
    private int nChannels = 1;
    private Map<String, List<String>> requestHeaders = null;
    private MimiIO.LogLevel logLevel = MimiIO.LogLevel.DEBUG;

    private volatile String responseJson = null;

    @Override
    public void onTx(TxEvent txEvent) {
        try {
            byte[] popdata = sendQueue.poll(10, TimeUnit.SECONDS);
            if (popdata != null) {
                if (popdata.length == 0) {
                    txEvent.setRecogBreak();
                } else {
                    txEvent.setBuffer(popdata, 0, popdata.length);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            txEvent.setErrorCode(-1);
        }
    }

    @Override
    public void onRx(RxEvent rxEvent) {
        responseJson = rxEvent.getResult();
    }

    protected WebSocketRecognizer(String accessToken) {
        this.accessToken = accessToken;
    }

    protected void startRecognize(String url, RequestData requestData) throws MimiIOException {
        if(!"https://sandbox-sr.mimi.fd.ai".equals(url)) {
            throw new IllegalArgumentException("URL not supported.");
        }
        this.requestData = requestData;
        responseJson = "";
        sendQueue = new LinkedBlockingQueue<byte[]>();
        requestHeaders = new HashMap<String, List<String>>();
        ArrayList<String> process = new ArrayList<String>();
        process.add("nict-asr");
        ArrayList<String> inputLanguage = new ArrayList<String>();
        inputLanguage.add(requestData.language);
        requestHeaders.put("x-mimi-input-language", inputLanguage);
        requestHeaders.put("x-mimi-process", process);

        this.mio = new MimiIO(
                host,
                port,
                txListener,
                rxListener,
                format,
                samplingrate,
                nChannels,
                requestHeaders,
                this.accessToken,
                logLevel);
        if (mio != null) {
            // Log.d(getClass().getName(), "mimiio start.......");
            mio.start();
        }
    }

    protected void addData(byte[] data) {
        if (data.length > 0) {  //ここでは最終チャンク送信をさせない
            sendQueue.add(data);
        }
    }

    protected ResponseData endRecognize() throws MimiIOException {
        ResponseData responseData = null;
        try {
            sendQueue.add(new byte[0]); //最終チャンク
            while (this.mio.isActive()) { // 音声認識結果を待つ
                Thread.sleep(10);
            }
            this.mio.checkError();

            // Log.d(getClass().getName(), "mimi close.");

            Gson gson = new Gson();
            Recognizer.MimiJSON json = gson.fromJson(responseJson, Recognizer.MimiJSON.class);
            ArrayList<String> resultWord = new ArrayList<String>();
            for (Recognizer.MimiJSON.Response res : json.response) {
                resultWord.add(res.result);
            }
            XMLUtil util = new XMLUtil();
            String result = util.createResponseSR(this.requestData, resultWord);
            responseData = new ResponseData();
            responseData.setXML(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.mio.close();
        }
        return responseData;
    }
}
