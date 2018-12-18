package jp.fairydevices.mimi.io;

/**
 * mimiio 音声受信コールバッククラス
 * 詳細は libmimiio ドキュメントの rxfunc 及び コールバック関数を定義する を参照してください。
 */
public class RxEvent {

	private String result;
	private int errorcode;

	protected RxEvent(String result){
		this.result = result;
	}
	/**
	 * サーバーからの応答結果を取得する。
	 *
	 * @return サーバーからの応答結果
	 */
	public String getResult() {
		return this.result;
	}
	/**
	 * {@link MimiIO.OnRxListener#onRx(RxEvent)} 内で継続不能なエラーが発生した場合に，ユーザーエラーを示すユーザー定義数値を Mimi に伝える。
	 * 
	 * @param code  ユーザー定義エラーコード。　Mimi が利用するエラーコードとの重複を避けるため、必ず負の値であること。 0 以外の値が指定された場合、 Mimi はエラー状態となり、全処理を終了しようとする。
	 */
	public void setErrorCode(int code) {
		this.errorcode = code;
	}
}