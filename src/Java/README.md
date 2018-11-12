# Sample code in Java

## ビルド・実行手順（IntelliJ IDEAを使用する場合）
本プロジェクトディレクトリはIntelliJ IDEAで作成しているため、該当環境での実行手順を記載します。

### インポート手順

IntelliJ IDEA から `mimi.tagengo.example/src/Java/TagengoExample` ディレクトリをインポートします。

### 実行手順
1. `[ProjectRoot]/src/main/java/Main.java` ファイルを開き、Main クラス内 にある下記変数を 予め配布された`アプリケーションID`、 `アプリケーションシークレット` で置き換えます。
```
static final String clientID = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
static final String clientSecret = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
```
2. IntelliJ IDEA から `[ProjectRoot]/src/main/java/Main.java` -> `Run 'Main.main()'...` を実行します。

## ビルド・実行手順（IDEAを使用しない場合）

IDEAを使用しない場合は、依存ライブラリであるGsonを手動でダウンロードする必要があります。

### 手順

1. `./lib` 配下に `gson-2.8.5.jar` を配置します。
    - [配布元] : https://search.maven.org/artifact/com.google.code.gson/gson/2.8.5/jar
2. `./src/main/java/Main.java`  ファイルを開き、Main クラス内 にある下記変数を 予め配布された`アプリケーションID`、 `アプリケーションシークレット` で置き換えます。
```
static final String clientID = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
static final String clientSecret = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
```
3. 以下のコマンドで、ビルド・実行を行います。

```
$ ./gradlew build
$ java -classpath build/libs/mimi.prism-1.0.jar:lib/gson-2.8.5.jar Main
