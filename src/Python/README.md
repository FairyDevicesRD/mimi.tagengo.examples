# Python sample client for mimi

## Preparation

use `python3`.

1. install python library.

    ```sh
    pip install -r requirements.txt
    ```

2. get access token and write to a file.

    - see [here](https://mimi.readme.io/docs/auth-api#section-13-%E3%82%A2%E3%83%97%E3%83%AA%E3%82%B1%E3%83%BC%E3%82%B7%E3%83%A7%E3%83%B3%E6%A8%A9%E9%99%90%E3%81%A7%E3%81%AE%E7%99%BA%E8%A1%8C%E3%82%A2%E3%83%97%E3%83%AA%E3%82%B1%E3%83%BC%E3%82%B7%E3%83%A7%E3%83%B3%E5%86%85%E3%81%AB%E9%96%89%E3%81%98%E3%81%9F-root-%E6%A8%A9%E9%99%90).

3. if you use Speech Recognition, prepare the raw audio file (headerless linear PCM) which has 16bit, 16kHz bitrate, and 1 channel.

## Execution

### Speech Recognition

```sh
python3 asr.py access_token_file audio_file

ex.
python3 asr.py token.txt audio.raw
#=> { "response" : [ { "pronunciation" : "チョット", "result" : "ちょっと", "time" : [ 580, 1030 ] }, { "pronunciation" : "オソイ", "result" : "遅い", "time" : [ 1030, 1390 ] }, { "pronunciation" : "チューショク", "result" : "昼食", "time" : [ 1390, 1890 ] }, { "pronunciation" : "ヲ", "result" : "を", "time" : [ 1890, 1980 ] }, { "pronunciation" : "トル", "result" : "とる", "time" : [ 1980, 2220 ] }, { "pronunciation" : "タメ", "result" : "ため", "time" : [ 2220, 2610 ] }, { "pronunciation" : "ファミリー", "result" : "ファミリー", "time" : [ 2930, 3480 ] }, { "pronunciation" : "レストラン", "result" : "レストラン", "time" : [ 3480, 4020 ] }, { "pronunciation" : "ニ", "result" : "に", "time" : [ 4020, 4150 ] }, { "pronunciation" : "ハイッ", "result" : "入っ", "time" : [ 4150, 4450 ] }, { "pronunciation" : "タ", "result" : "た", "time" : [ 4450, 4540 ] }, { "pronunciation" : "ノ", "result" : "の", "time" : [ 4540, 4680 ] }, { "pronunciation" : "デス", "result" : "です", "time" : [ 4680, 5100 ] } ], "session_id" : "ab86a468-ced4-11e9-915b-42010a92008b", "status" : "recog-finished", "type" : "asr#mimilvcsr" }
```

when you use "nict-asr", the response is as follows

```shell
python3 asr.py token.txt audio.raw
#=> {"type": "asr#nictlvcsr", "session_id": "d50b51e4-ced4-11e9-82e7-42010a920012", "status": "recog-finished", "response": [{"result": "ちょっと|チョット|ちょっと|副詞-助詞類接続||||"},{"result": "遅い|オソイ|遅い|形容詞-自立|形容詞・アウオ段|基本形||"},{"result": "昼食|チュウショク|昼食|名詞-一般||||"},{"result": "を|ヲ|を|助詞-格助詞-一般||||"},{"result": "とる|トル|とる|動詞-自立|五段・ラ行|基本形||"},{"result": "ため|タメ|ため|名詞-非自立-副詞可能||||"},{"result": "ファミリーレストラン|ファミリーレストラン|ファミリーレストラン|名詞-一般||||"},{"result": "に|ニ|に|助詞-格助詞-一般||||"},{"result": "入っ|ハイッ|入る|動詞-自立|五段・ラ行|連用タ接続||"},{"result": "た|タ|た|助動詞|特殊・タ|基本形||"},{"result": "の|ノ|の|名詞-非自立-一般||||"},{"result": "です|デス|です|助動詞|特殊・デス|基本形||"},{"result": "|||SENT-START-END||||"},{"result": "|||UTT-END||||"}]}
```

### Machine Translation

```sh
python3 tra.py  access_token_file input_lang input_text output_lang

ex.
python3 tra.py token.txt ja "こんにちは" en
#=> ['Hello.']
```

### Speech Synthesis

```sh
python3 tts.py access_token_file input_lang input_text output_filename

ex.
python3 tts.py token ja "こんにちは" out.wav
```
