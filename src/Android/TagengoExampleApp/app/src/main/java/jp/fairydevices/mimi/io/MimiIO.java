package jp.fairydevices.mimi.io;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * mimi (R) WebSocket API への接続、音声の送信、結果の出力を取り扱うクラスです。
 * <p>詳細は libmimiio ドキュメントを参照してください。</p>
 */
public class MimiIO {

	private OnTxListener txListener = null;
	private OnRxListener rxListener = null;
	private Map<String, List<String>> requestHeaders;
	private long handle = 0;
	private long thiz = 0; //mimiioスレッドからこのインスタンスを参照するGlovalReferenceをJava側で保持
	private TxEvent txEvent;//c側からこのインスタンスを参照します
	private RxEvent rxEvent;//c側からこのインスタンスを参照します
	private String[] headerKeys = null;//c側からこのインスタンスを参照します
	private String[] headerValues = null;//c側からこのインスタンスを参照します

	static {
		System.loadLibrary("mimiio_jni");
	}

	/**
	 * mimiio 音声送信コールバックインターフェイス
	 * <p>詳細は libmimiio ドキュメントの txfunc 及び コールバック関数を定義する を参照してください。</p>
	 */
	public interface OnTxListener extends java.util.EventListener {
		void onTx(TxEvent e);
	}

	/**
	 * mimiio 音声受信コールバックインターフェイス
	 * <p>詳細は libmimiio ドキュメントの txfunc 及び コールバック関数を定義する を参照してください。</p>
	 */
	public interface OnRxListener extends java.util.EventListener {
		void onRx(RxEvent e);
	}

	public enum Format {
		RAW_PCM,
		FLAC_0,
		FLAC_1,
		FLAC_2,
		FLAC_3,
		FLAC_4,
		FLAC_5,
		FLAC_6,
		FLAC_7,
		FLAC_8,
		FLAC_PASS_THROUGH
	}
	public enum StreamState {
		WAIT,
		CLOSED,
		BOTH,
		SEND,
		RECV
	}
	public enum LogLevel {
		ERROR(3),
		WARNING(4),
		INFO(6),
		DEBUG(7);

		private LogLevel(int id) {
			this.id = id;
		}
		protected final int id;
	}

	/**
	 * mimi(R) リモートホストへの接続を開き、クライアント側・サーバー側双方の初期化を行う。
	 * <p>詳細は libmimiio ドキュメントの mimi_open	 及び 接続の開始と終了 を参照してください。</p>
	 * 
	 * @param host
	 * @param port
	 * @param txListener
	 * @param rxListener
	 * @param format
	 * @param samplingrate
	 * @param nChannels
	 * @param requestHeaders
	 * @param accessToken
	 * @param logLevel
	 * @throws MimiIOException
	 */
	public MimiIO(String host,
			int port,
			OnTxListener txListener,
			OnRxListener rxListener,
			Format format,
			int samplingrate,
			int nChannels,
			Map<String,List<String>> requestHeaders,
			String accessToken,
			LogLevel logLevel)
					throws MimiIOException {
		this.txListener = txListener;
		this.rxListener = rxListener;
		this.requestHeaders = requestHeaders;
		if(this.requestHeaders != null) parseRequestHeader();
		if(host == null) throw new NullPointerException();
		handle = mimi_open(host, port, format.ordinal(), samplingrate, nChannels, accessToken, logLevel.id);
	}

	/**
	 * mimi(R) リモートホストへの双方向の通信ストリームによる音声送信及び結果受信を開始する。
	 * <p>詳細は libmimiio ドキュメントの mimi_start 及び 音声送信の開始と終了の監視 を参照してください。</p>
	 * @throws MimiIOException
	 * @throws NullPointerException 既に{@link #close()}されている場合に発生します
	 */
	public void start() throws MimiIOException {
		if(handle == 0) throw new NullPointerException();
		int errorno = mimi_start(handle);
		if(errorno != 0){
			throw new MimiIOException(errorno, mimi_str_error(handle, errorno));
		}
	}

	/**
	 * 双方向の通信ストリームが有効であるかどうかの状態を取得する
	 * <p>詳細は libmimiio ドキュメントの mimi_is_active 及び 音声送信の開始と終了の監視 を参照してください。</p>
	 * @throws NullPointerException 既に{@link #close()}されている場合に発生します
	 * @return
	 */
	public boolean isActive() {
		if(handle == 0) throw new NullPointerException();
		return mimi_is_active(handle);
	}
	/**
	 * リモートホストへの接続状態を取得する
	 * 
	 * @return リモートホストへの接続状態
	 * @throws UnsupportedOperationException 現時点ではこのメソッドはサポートされていません。
	 * @throws NullPointerException 既に{@link #close()}されている場合に発生します
	 */
	public StreamState getStreamState() {
		if(handle == 0) throw new NullPointerException();
		throw new UnsupportedOperationException();
		//        return mimi_stream_state(handle);
	}
	/**
	 * mimi(R) リモートホストへの接続を終了する
	 * <p>詳細は libmimiio ドキュメントの mimi_close 及び 音声送信の開始と終了の監視 を参照してください。</p>
	 *
	 * @throws NullPointerException 既に{@link #close()}されている場合に発生します
	 */
	public void close(){
		if(handle == 0) throw new NullPointerException();
		mimi_close(handle);
		handle = 0;
	}

	/**
	 * エラー発生の有無を確認する
	 * <p>詳細は libmimiio ドキュメントの mimi_error 及び 音声送信の開始と終了の監視 を参照してください。</p>
	 * 
	 * @throws MimiIOException
	 * @throws NullPointerException 既に{@link #close()}されている場合に発生します
	 */
	public void checkError() throws MimiIOException{
		if(handle == 0) throw new NullPointerException();
		mimi_error(handle);
	}

	/**
	 * 内部的に利用しているネイティブ実装のバージョン情報を取得する
	 * @return バージョン情報
	 */
	public static String version() {
		return mimi_version();
	}
	
	private void parseRequestHeader(){
		int size = requestHeaders.keySet().size();
		headerKeys = new String[size];
		headerValues = new String[size];
		Iterator<String> iteK = requestHeaders.keySet().iterator();
		Iterator<String> iteV;
		String keyStr;
		String valueStr;
		int keyCount = 0;
		StringBuilder sb;
		while(iteK.hasNext()){
			keyStr = iteK.next();
			if(keyStr == null) {
				throw new NullPointerException();
			}
			iteV =  requestHeaders.get(keyStr).iterator();
			sb = new StringBuilder();
			while(iteV.hasNext()){
				valueStr = iteV.next();
				if(valueStr == null) {
					throw new NullPointerException();
				}
				if(sb.length() > 0){
					sb.append(",").append(valueStr);
				}else{
					sb.append(valueStr);
				}
			}
			headerKeys[keyCount] = keyStr;
			headerValues[keyCount++]= sb.toString();
		}
	}

	private void txFuncFromC(){
		txEvent = new TxEvent();
		if(this.txListener != null){
			this.txListener.onTx(txEvent);
		}
	}

	private void rxFuncFromC(String result){
		rxEvent = new RxEvent(result);
		if(this.rxListener != null){
			this.rxListener.onRx(rxEvent);
		}
	}

	private native long mimi_open(String host, int port, int format, int samplingrate, int nChannels, String accessToken, int log_level) throws MimiIOException;
	private native int mimi_start(long handle) throws MimiIOException;
	private native void mimi_close(long handle);
	private native boolean mimi_is_active(long handle) ;
	private native int mimi_stream_state(long handle);
	private native String mimi_str_error(long handle, int errorno);
	private native void mimi_error(long handle) throws MimiIOException;
	private native static String mimi_version();
}
