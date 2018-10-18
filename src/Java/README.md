# Sample code in Java

## 準備

1. `./lib` 配下に `gson-2.8.5.jar` を配置します。
    - [配布元] : https://search.maven.org/artifact/com.google.code.gson/gson/2.8.5/jar
2. [mimi Developer Console](https://console.mimi.fd.ai/)にて取得した`client_id`および`client_secret`を`config.json.sample`内に記載し、`config.json`という名前で保存します。

## ビルド手順

```
$ mkdir bin
$ javac -d bin -classpath src:lib/gson-2.8.5.jar:lib/NICTAPIWrapper.jar src/NICTAPISample.java
$ java -classpath bin:lib/gson-2.8.5.jar:lib/NICTAPIWrapper.jar NICTAPISample config.json "今日はいい天気ですね。"
```
