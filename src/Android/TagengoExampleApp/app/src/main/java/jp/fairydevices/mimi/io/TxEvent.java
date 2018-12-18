package jp.fairydevices.mimi.io;

/**
 * mimiio 音声送信コールバッククラス
 * 詳細は libmimiio ドキュメントの txfunc 及び コールバック関数を定義する を参照してください。
 */
public class TxEvent {

	byte[] b;
	int off;
	int len;
	boolean recog_break;
	int errorcode; 

	/**
	 * 音声データ（RAW PCM）が書き込まれたバッファを指定する。
	 *
	 * @param b    音声データ（RAW PCM）が書き込まれたバッファ。音声の形式は RAW PCM データ (リトルエンディアン) であり、 signed short (16bit) サンプルであること。サンプリングレートとチャネル数は、 Mimi クラスのコンストラクタで指定されたもの。バッファの最大長は 32K バイトです。 nullが指定された場合は、NullPointerExceptionが送出されます
	 * @param off  音声データのバイト数 0未満が指定された場合は、IndexOutOfBoundsExceptionが送出されます
	 * @param len  音声データのオフセット 0未満が指定された場合は、IndexOutOfBoundsExceptionが送出されます
	 */
	public void setBuffer(byte[] b, int off, int len) {
		if(b == null) throw new NullPointerException();
		this.b = b;
		if(off < 0) throw new IndexOutOfBoundsException();
		this.off = off;
		if(len < 0) throw new IndexOutOfBoundsException();
		this.len = len;
	}

	/**
	 * 音声の区切りを示す。
	 */
	public void setRecogBreak() {
		this.recog_break = true;
	}

	/**
	 * {@link MimiIO.OnTxListener#onTx(TxEvent)} 内で継続不能なエラーが発生した場合に，ユーザーエラーを示すユーザー定義数値を Mimi に伝える。
	 *
	 * @param code  ユーザー定義エラーコード。　Mimi が利用するエラーコードとの重複を避けるため、必ず負の値であること。 0 以外の値が指定された場合、 Mimi はエラー状態となり、全処理を終了しようとする。
	 */
	public void setErrorCode(int code) {
		this.errorcode = code;
	}
}
