# Sample code in Java

## ビルド・実行手順（IntelliJ IDEAを使用する場合）
本プロジェクトディレクトリはIntelliJ IDEAで作成しているため、該当環境での実行手順を記載します。

### インポート手順

IntelliJ IDEA から `mimi.tagengo.example/src/Java/TagengoExample` ディレクトリをインポートします。
`Import Project` -> `Import project from external model (Gradle)`  -> `Finish`

### 実行手順
1. `[ProjectRoot]/src/main/java/Main.java` ファイルを開き、Main クラス内 にある下記変数を 予め配布された`アプリケーションID`、 `アプリケーションシークレット` で置き換えます。
```
private static final String clientID = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
private static final String clientSecret = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
```
2. IntelliJ IDEA から `[ProjectRoot]/src/main/java/Main.java` -> `Run 'Main.main()'...` を実行します。

## ビルド・実行手順（IDEAを使用しない場合）

IDEAを使用しない場合は、`gradle`コマンドを使って、本プログラムを実行することができます。

### 手順

1. ターミナルを開き、`TagengoExample`ディレクトリに移動します。
3. `./src/main/java/Main.java`  ファイルを開き、Main クラス内 にある下記変数を 予め配布された`アプリケーションID`、 `アプリケーションシークレット` で置き換えます。
```
static final String clientID = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
static final String clientSecret = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
```
3. 以下のコマンドで、ビルド・実行を行います。

```
$ ./gradlew build
$ ./gradlew run
```

Windows環境をご利用の場合は、`gradlew`の代わりに`gradlew.bat`を実行してください。
