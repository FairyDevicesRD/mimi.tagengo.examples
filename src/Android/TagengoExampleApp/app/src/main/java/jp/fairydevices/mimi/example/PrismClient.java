package jp.fairydevices.mimi.example;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import ai.fd.mimi.prism.ClientComCtrl;
import ai.fd.mimi.prism.ResponseData;

import static android.media.AudioTrack.MODE_STREAM;

class PrismClient {

    private static final String ID = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
    private static final String SECRET = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
    private static final String SRURL = "wss://sandbox-sr.mimi.fd.ai";
    private static final String SSURL = "https://sandbox-ss.mimi.fd.ai/speech_synthesis";
    private static final String MTURL = "https://sandbox-mt.mimi.fd.ai/machine_translation";

    private String accessToken = "";
    private ClientComCtrl client = null;
    private ExecutorService pool;

    private SRRecordingTask srRecordingTask = null;
    private AudioTrack ssPlayer;

    private EditText srOutputView;
    private EditText mtOutputView;

    private LinkedBlockingQueue<byte[]> recQueue = new LinkedBlockingQueue<>();
    private Recorder recorder = new Recorder(recQueue);

    PrismClient(View srOutputView, View mtOutputView) {
        this.srOutputView = (EditText) srOutputView;
        this.mtOutputView = (EditText) mtOutputView;

        pool = Executors.newSingleThreadExecutor();

        int bufferSize = AudioTrack.getMinBufferSize(16000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            ssPlayer = new AudioTrack.Builder()
                    .setAudioAttributes(new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build())
                    .setAudioFormat(new AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .setSampleRate(16000)
                            .build())
                    .setBufferSizeInBytes(8192)
                    .build();
        } else {
            ssPlayer = new AudioTrack(
                    AudioManager.STREAM_MUSIC
                    , 16000
                    , AudioFormat.CHANNEL_CONFIGURATION_MONO
                    , AudioFormat.ENCODING_PCM_16BIT
                    , bufferSize
                    , MODE_STREAM);
        }
        ssPlayer.play();
    }

    void updateToken() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    accessToken = new AccessToken().getToken(ID, SECRET);
                    Log.d(getClass().getName(), "accessToken: " + accessToken);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        pool.execute(task);
    }

    void SRInputStart() {
        client = new ClientComCtrl(accessToken);
        recQueue = new LinkedBlockingQueue<>();
        recorder = new Recorder(recQueue);
        recorder.startRecording();

        client.setTransferEncodingChunked(true); //分割送信
        srRecordingTask = new SRRecordingTask();

        pool.execute(srRecordingTask);
    }

    void SRInputEnd() {
        if (srRecordingTask != null) {
            recorder.stopRecording();
            srRecordingTask.stopRecording();
            if (client.isTransferEncodingChunked()) {
                // recQueue.take() が block している場合がある為、ダミーの音声データででblockを解除する
                recQueue.add(new byte[0]);
            }
        }
    }

    void MT() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    // MT をリクエスト
                    String inputLanguage = "ja";
                    String targetLanguage = "en";
                    String text = srOutputView.getText().toString();
                    String MTRequestTemplate = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                            "<STML UtteranceID=\"0\" Version=\"1.0\">\n" +
                            "<User ID=\"N/A\"/>\n" +
                            "<MT_IN SourceLanguage=\"%s\" TargetLanguage=\"%s\">\n" +
                            "<InputTextFormat Form=\"SurfaceForm\"/>\n" +
                            "<OutputTextFormat Form=\"SurfaceForm\"/>\n" +
                            "<s>%s</s>\n" +
                            "</MT_IN>\n" +
                            "</STML>\n";
                    String requestXML = String.format(MTRequestTemplate, inputLanguage, targetLanguage, text);
                    client = new ClientComCtrl(accessToken);
                    ResponseData response = client.request(MTURL, requestXML);

                    // 結果を view に返す
                    Log.d(getClass().getName(), "MT result: " + response.getXML());
                    XMLSimpleParser parser = new XMLSimpleParser();
                    final String mtResult = parser.getMT_OUTSentence(response.getXML());
                    mtOutputView.post(new Runnable() {
                        @Override
                        public void run() {
                            mtOutputView.setText(mtResult);
                        }
                    });

                } catch (SAXException | XPathExpressionException | ParserConfigurationException | IOException e) {
                    e.printStackTrace();
                }
            }
        };
        pool.submit(task);
    }

    void SS() {

        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    // SS をリクエスト
                    String inputLanguage = "en";
                    String text = mtOutputView.getText().toString();
                    String SSRequestTemplate = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                            "<STML UtteranceID=\"0\" Version=\"1\">\n" +
                            "<User ID=\"N/A\"/>\n" +
                            "<SS_IN Language=\"%s\">\n" +
                            "<Voice Age=\"30\" Gender=\"%s\"/>\n" +
                            "<OutputAudioFormat Audio=\"RAW\" Endian=\"Little\" SamplingFrequency=\"16k\"/>\n" +
                            "<InputTextFormat Form=\"SurfaceForm\"/>\n" +
                            "<s Delimiter=\" \">%s</s>\n" +
                            "</SS_IN>\n" +
                            "</STML>";
                    String requestXML = String.format(SSRequestTemplate, inputLanguage, "Female", text);
                    client = new ClientComCtrl(accessToken);
                    ResponseData response = client.request(SSURL, requestXML);

                    //結果を再生する
                    Log.d(getClass().getName(), "SS result: " + response.getXML());
                    ssPlayer.write(response.getBinary(), 0, response.getBinary().length);
                } catch (SAXException | IOException e) {
                    e.printStackTrace();
                }
            }
        };
        pool.submit(task);
    }

    class SRRecordingTask implements Runnable {
        private volatile boolean taskDone = false;

        @Override
        public void run() {

            if (!client.isTransferEncodingChunked()) {
                return;
            }
            String inputLanguage = "ja";
            String SRRequestTemplate = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<STML UtteranceID=\"0\" Version=\"1\">\n" +
                    "<User ID=\"N/A\"/>\n" +
                    "<SR_IN Language=\"%s\">\n" +
                    "<Voice/>\n" +
                    "<InputAudioFormat Audio=\"RAW\" Endian=\"Little\" SamplingFrequency=\"16k\"/>\n" +
                    "<OutputTextFormat Form=\"SurfaceForm\"/>\n" +
                    "</SR_IN>\n" +
                    "</STML>";
            String requestXML = String.format(SRRequestTemplate, inputLanguage);
            ResponseData response;
            try {
                client.request(SRURL, requestXML);
                // 録音終了まで 録音Queueから取り出し続ける
                while (!taskDone) {
                    try {
                        byte[] data = recQueue.take(); // blocking
                        client.request(SRURL, data);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // 音声の終了を表明
                response = client.request(SRURL);

                Log.d(getClass().getName(), "SR result: " + response.getXML());
                // パースした結果を view に返す
                XMLSimpleParser parser = new XMLSimpleParser();
                final String srResult = parser.getSR_OUTSentence(response.getXML());
                srOutputView.post(new Runnable() {
                    @Override
                    public void run() {
                        srOutputView.setText(srResult);
                    }
                });
            } catch (SAXException | XPathExpressionException | ParserConfigurationException | IOException e) {
                e.printStackTrace();
            }
        }

        void stopRecording() {
            taskDone = true;
        }
    }
}
