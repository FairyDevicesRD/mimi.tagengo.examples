# Sample code in JavaScript

## mimiio.html

### 実行手順

- 事前準備
    1. 音声ファイルを準備します
- 実行
    1. `mimiio.html` をChromeなどのWebブラウザで開きます
    1. `Application ID`と`Application Secret`を入力し、`アクセストークン発行`ボタンを押下します
    1. `Content-type`の項目を、準備した音声ファイルに合わせて選択します
    1. `x-mimi-input-language`の項目を、音声ファイルで話されている言語に合わせて選択します
    1. `Input File`に、準備した音声ファイルを選択します
    1. `response format` の項目を利用したい nict-asr のバージョンに合わせて選択します
    1. `response format` で v2 を選択した場合は、`progressive`と`temporary`の ON/OFF を選択します
    1. 「認識開始」ボタンを押します

### 出力例

以下の例では、`16.`の行に認識結果がログ出力されています。

```
17. WebSocket close. code: 1000, reason:
16. WebSocket message: {"type": "asr#nictlvcsr", "session_id": "82e186c6-2ab1-11e9-947d-42010a920032", "status": "recog-finished", "response": [{"result": "ちょっと|チョット|ちょっと|副詞-助詞類接続||||"},{"result": "遅い|オソイ|遅い|形容詞-自立|形容詞・アウオ段|基本形||"},{"result": "昼食|チュウショク|昼食|名詞-一般||||"},{"result": "を|ヲ|を|助詞-格助詞-一般||||"},{"result": "とる|トル|とる|動詞-自立|五段・ラ行|基本形||"},{"result": "ため|タメ|ため|名詞-非自立-副詞可能||||"},{"result": "ファミリーレストラン|ファミリーレストラン|ファミリーレストラン|名詞-一般||||"},{"result": "に|ニ|に|助詞-格助詞-一般||||"},{"result": "入っ|ハイッ|入る|動詞-自立|五段・ラ行|連用タ接続||"},{"result": "た|タ|た|助動詞|特殊・タ|基本形||"},{"result": "の|ノ|の|名詞-非自立-一般||||"},{"result": "です|デス|です|助動詞|特殊・デス|基本形||"},{"result": "|||SENT-START-END||||"},{"result": "|||UTT-END||||"}]}
15. Sending: audio.raw(196608 / 200692)
14. Sending: audio.raw(180224 / 200692)
13. Sending: audio.raw(163840 / 200692)
12. Sending: audio.raw(147456 / 200692)
11. Sending: audio.raw(131072 / 200692)
10. Sending: audio.raw(114688 / 200692)
 9. Sending: audio.raw(98304 / 200692)
 8. Sending: audio.raw(81920 / 200692)
 7. Sending: audio.raw(65536 / 200692)
 6. Sending: audio.raw(49152 / 200692)
 5. Sending: audio.raw(32768 / 200692)
 4. Sending: audio.raw(16384 / 200692)
 3. Sending: audio.raw(0 / 200692)
 2. WebSocket open.
 1. Please drag & drop your audio file into above input control. Supported type: 16 bit signed int (Little Endian), monoa
```


## mimiio_microphone.html

### 実行手順

- 実行
    1. `mimiio_microphone.html`をChromeなどのWebブラウザで開きます
    1. `Application ID`と`Application Secret`を入力し、`アクセストークン発行`ボタンを押下します
    1. `Content-Type`の項目を、お使いのPCに合わせて選択します
    1. `x-mimi-input-language`の項目を、発話される言語に合わせて選択します
    1. `response format` の項目を利用したい nict-asr のバージョンに合わせて選択します
    1. `response format` で v2 を選択した場合は、`progressive`と`temporary`のオプションが表示されるので ON/OFF を選択します
    1. 「録音開始」ボタンを押します ※ `このファイルが次の許可を求めています　マイクを使用する`が表示された場合、`許可`を押してください
    1.  マイクに向かって発話します
    1. 「録音停止」ボタンを押します

### 出力例

以下の例では、response format で`v2`を選択しており、音声認識結果が逐次返っています。

```
27. WebSocket close. code: 1000, reason:
26. WebSocket message: {"type":"asr#nictlvcsr2","session_id":"4ec2123d-1d32-4d07-b205-8e08c9b4a7a2","status":"recog-finished","response":[{"result":"あらゆる現実をすべて自分のほうへねじ曲げたのだ。","words":["あらゆる","現実","を","すべて","自分","の","ほう","へ","ねじ曲げ","た","の","だ"],"determined":true,"time":0}]}
25. WebSocket message: {"type":"asr#nictlvcsr2","session_id":"4ec2123d-1d32-4d07-b205-8e08c9b4a7a2","status":"recog-in-progress","response":[{"result":"あらゆる現実をすべて自分のほうへねじ曲げたのだ。","words":["あらゆる","現実","を","すべて","自分","の","ほう","へ","ねじ曲げ","た","の","だ","。"],"determined":false,"time":0}]}
24. Sending: microphone source (time : 15:56:27.0281 length: 4096byte)
23. Sending: microphone source (time : 15:56:27.0228 length: 28672byte)
22. Sending: microphone source (time : 15:56:26.0927 length: 28672byte)
21. Sending: microphone source (time : 15:56:26.0627 length: 32768byte)
20. Sending: microphone source (time : 15:56:26.0326 length: 28672byte)
19. WebSocket message: {"type":"asr#nictlvcsr2","session_id":"4ec2123d-1d32-4d07-b205-8e08c9b4a7a2","status":"recog-in-progress","response":[{"result":"あらゆる現実をすべて自分の。","words":["あらゆる","現実","を","すべて","自分","の","。"],"determined":false,"time":0}]}
18. Sending: microphone source (time : 15:56:26.0030 length: 28672byte)
17. Sending: microphone source (time : 15:56:25.0729 length: 28672byte)
16. Sending: microphone source (time : 15:56:25.0428 length: 28672byte)
15. Sending: microphone source (time : 15:56:25.0128 length: 28672byte)
14. WebSocket message: {"type":"asr#nictlvcsr2","session_id":"4ec2123d-1d32-4d07-b205-8e08c9b4a7a2","status":"recog-in-progress","response":[{"result":"あらゆる現実を。","words":["あらゆる","現実","を","。"],"determined":false,"time":0}]}
13. Sending: microphone source (time : 15:56:24.0828 length: 28672byte)
12. Sending: microphone source (time : 15:56:24.0530 length: 24576byte)
11. Sending: microphone source (time : 15:56:24.0228 length: 32768byte)
10. Sending: microphone source (time : 15:56:23.0928 length: 28672byte)
 9. Sending: microphone source (time : 15:56:23.0628 length: 28672byte)
 8. Sending: microphone source (time : 15:56:23.0327 length: 28672byte)
 7. Sending: microphone source (time : 15:56:23.0027 length: 28672byte)
 6. Sending: microphone source (time : 15:56:22.0727 length: 28672byte)
 5. Sending: microphone source (time : 15:56:22.0428 length: 28672byte)
 4. Sending: microphone source (time : 15:56:22.0128 length: 28672byte)
 3. Sending: microphone source (time : 15:56:21.0829 length: 28672byte)
 2. Sending: microphone source (time : 15:56:21.0538 length: 16384byte)
 1. WebSocket open.
```

## tra.html

### 実行手順

- 実行
    1. `tra.html`をChromeなどのWebブラウザで開きます
    1. `Application ID`と`Application Secret`を入力し、`アクセストークン発行`ボタンを押下します
    1. `source-language`の項目を、入力するテキストの言語に合わせて選択します
    1. `target-language`の項目を、出力したい言語に合わせて選択します。日本語から外国語または外国語から日本語への翻訳が可能です。
    1. `text`の項目に、翻訳したいテキストを入力します
    1. 「機械翻訳実行」ボタンを押します

### 出力例

以下の例では、`おはようございます`を日本語から英語へ機械翻訳した結果がログ出力されています。

```
1. Good morning.
```

## tts.html

### 実行手順

- 実行
    1. `tts.html`をChromeなどのWebブラウザで開きます
    1. `Application ID`と`Application Secret`を入力し、`アクセストークン発行`ボタンを押下します
    1. `language`の項目を、音声合成したい言語に合わせて選択します
    1. `gender`の項目は、男性の音声としたい場合は male、女性の音声としたい場合は female を選択します
    1. `text`の項目に、音声合成したいテキストを入力します
    1. 「音声合成実行」ボタンを押します

### 出力例

再生ボタンが押下可能 (`▷` が `▶` になる) になり次第、ボタンを押下すると音声が再生されます
