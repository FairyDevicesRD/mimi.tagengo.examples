# Android版サンプルアプリ　ビルド・実行手順

## インポート手順

1. Android Studio から mimi.tagengo.example/src/Android/TagengoExampleApp ディレクトリをインポートします。
2. https://github.com/FairyDevicesRD/mimi.tagengo.examples/releases から、`libmimiio-android-19-armeabi-v7a-libc++.tar.gz` をダウンロードします。
3. 展開した `libmimiio_jni.so` を次のように配置します。
```
mimi.tagengo.examples/src/Android/TagengoExampleApp/app/src/main/jniLibs/armeabi-v7a/libmimiio_jni.so
```
## 実行手順

1. `app/src/main/java/jp/fairydevices/mimi/example/PrismClient.java` ファイルを開き、PrismClient クラス内にある下記変数を 予め配布された`アプリケーションID` 、 `アプリケーションシークレット` で置き換えます。
```
private static final String ID = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
private static final String SECRET = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
```
2. Android Studio から `Run` -> `Run 'app'` と実行します。

## ライセンスについて
`libmimiio-android-19-armeabi-v7a-libc++.tar.gz` に含まれる `libmimiio_jni.so` は以下のソフトウェアを結合したものです。

- libmimiio (Dec 12, 2018)
- OpenSSL (1.0.2q)
- Poco (1.9.0)
- FLAC （1.3.2）

それぞれのライセンスついては同梱の LICENCE ファイルをご確認ください。
