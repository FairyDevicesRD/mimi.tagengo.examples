package jp.fairydevices.mimi.io;

/**
 * mimiio 例外クラス
 * <p>発声した例外のエラーコードに関しては libmimiio ドキュメントのエラーコード一覧を参照してください。</p>
 */
public class MimiIOException extends java.io.IOException {

	private static final long serialVersionUID = -2801449855032932311L;
	protected final int code;
	public MimiIOException(int code, String message) {
		super(message);
		this.code = code;
	}
	public int getCode() {
		return this.code;
	}
}
