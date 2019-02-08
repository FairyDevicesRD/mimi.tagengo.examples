# Sample code in JavaScript

## 実行手順

- 事前準備
    1. 音声ファイルを準備します
    2. アクセストークンを取得しておきます
- 実行
    1. `mimiio.html` をChromeなどのWebブラウザで開きます
    2. アクセストークンを入力します
    3. `Content-type`の項目を、準備した音声ファイルに合わせて選択します
    4. `x-mimi-input-language`の項目を、音声ファイルで話されている言語に合わせて選択します
    5. `Input File`に、準備した音声ファイルを選択します
    6. 「認識開始」ボタンを押します

## 出力例

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

