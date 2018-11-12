# Android版サンプルアプリ　ビルド・実行手順

## インポート手順

AndroidStudioから `mimi.tagengo.example/src/Android/TagengoExampleAPP` ディレクトリをインポートします。

本プロジェクトファイルは ai.fd.mimi.prism ライブラリを使用しています。
シンボリックリンクが 解決できない環境では
`mimi.tagengo.examples/src/Java/TagengoExample/src/main/java/* ` を `mimi.tagengo.examples/src/Java/TagengoExampleAPP/app/src/main/java/` にコピーしてください。

## 実行手順

1. `app/src/main/java/jp/fairydevices/mimi/example/PrismClient.java` ファイルを開き、PrismClient クラス内にある下記変数を 予め配布された`アプリケーションID` 、 `アプリケーションシークレット` で置き換えます。
```
private static final String ID = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
private static final String SECRET = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
```
2.  AndroidStudioから `Run` -> `Run 'app'` と実行します。
