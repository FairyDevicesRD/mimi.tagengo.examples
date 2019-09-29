package ai.fd.mimi;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class SpeechRecognizer {
    private static int PORT = 443;
    private WsClient wsc;
    private OnMesseageListener listener = null;

    public interface OnMesseageListener {
        void onOpen(short status);

        void onMessage(String message);

        void onClose(int code, String reason);

        void onError(Exception ex);
    }

    class WsClient extends WebSocketClient {

        WsClient(URI serverUri, Draft draft) {
            super(serverUri, draft);
        }

        public WsClient(URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onOpen(ServerHandshake handshakeData) {
            if (listener != null) {
                listener.onOpen(handshakeData.getHttpStatus());
            }
        }

        @Override
        public void onMessage(String message) {
            if (listener != null) {
                listener.onMessage(message);
            }
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            if (listener != null) {
                listener.onClose(code, reason);
            }
        }

        @Override
        public void onError(Exception ex) {
            if (listener != null) {
                listener.onError(ex);
            }
        }
    }

    public SpeechRecognizer(String url, String accessToken, String inputLanguage) {
        URI uri = null;
        try {
            uri = new URI(url + "?process=nict-asr&input-language=" + inputLanguage
                    + "&access-token=" + accessToken
                    + "&content-type=audio/x-pcm;bit=16;rate=16000");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        wsc = new WsClient(uri, new Draft_6455());

        try {
            wsc.connectBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendByteDate(byte[] b) {
        wsc.send(b);
    }

    public void sendRecogBreak() {
        wsc.send("{\"command\": \"recog-break\"}");
    }

    public void setOnMessageListener(OnMesseageListener listener) {
        this.listener = listener;
    }

    public void removeOnMessageListener() {
        this.listener = null;
    }
}

