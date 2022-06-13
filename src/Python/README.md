# Sample code in Python

## Preparation

use `python3`.

1. Install python library.

    ```sh
    pip install -r requirements.txt
    ```

1. Get an access token and write the access token to any file. Before executing access_token.py, do NOT forget to write your ids to the following variables in the file.
   - APPLICATION_ID
   - APPLICATION_SECRET

    ```sh
    python3 access_token.py > token.txt
    ```

    - see [here](https://mimi.readme.io/docs/auth-api#section-13-%E3%82%A2%E3%83%97%E3%83%AA%E3%82%B1%E3%83%BC%E3%82%B7%E3%83%A7%E3%83%B3%E6%A8%A9%E9%99%90%E3%81%A7%E3%81%AE%E7%99%BA%E8%A1%8C%E3%82%A2%E3%83%97%E3%83%AA%E3%82%B1%E3%83%BC%E3%82%B7%E3%83%A7%E3%83%B3%E5%86%85%E3%81%AB%E9%96%89%E3%81%98%E3%81%9F-root-%E6%A8%A9%E9%99%90) for more information.

1. Execute speech recognition, machine translation, or speech synthesize. 

## Execution

### Speech Recognition with an audio file
An audio file must be raw (headerless linear PCM), 16 bits, and monaural. The sampling rate is your option, but write the rate like `rate=16000` in the content-type of headesrs.

```sh
python3 asr.py access_token_file audio_file

ex.
python3 asr.py token.txt audio.raw
#=> {"type": "asr#nictlvcsr", "session_id": "d50b51e4-ced4-11e9-82e7-42010a920012", "status": "recog-finished", "response": [{"result": "ちょっと|チョット|ちょっと|副詞-助詞類接続||||"},{"result": "遅い|オソイ|遅い|形容詞-自立|形容詞・アウオ段|基本形||"},{"result": "昼食|チュウショク|昼食|名詞-一般||||"},{"result": "を|ヲ|を|助詞-格助詞-一般||||"},{"result": "とる|トル|とる|動詞-自立|五段・ラ行|基本形||"},{"result": "ため|タメ|ため|名詞-非自立-副詞可能||||"},{"result": "ファミリーレストラン|ファミリーレストラン|ファミリーレストラン|名詞-一般||||"},{"result": "に|ニ|に|助詞-格助詞-一般||||"},{"result": "入っ|ハイッ|入る|動詞-自立|五段・ラ行|連用タ接続||"},{"result": "た|タ|た|助動詞|特殊・タ|基本形||"},{"result": "の|ノ|の|名詞-非自立-一般||||"},{"result": "です|デス|です|助動詞|特殊・デス|基本形||"},{"result": "|||SENT-START-END||||"},{"result": "|||UTT-END||||"}]}
```

when you use "asr", the response is as follows

```shell
python3 asr.py token.txt audio.raw
#=> { "response" : [ { "pronunciation" : "チョット", "result" : "ちょっと", "time" : [ 580, 1030 ] }, { "pronunciation" : "オソイ", "result" : "遅い", "time" : [ 1030, 1390 ] }, { "pronunciation" : "チューショク", "result" : "昼食", "time" : [ 1390, 1890 ] }, { "pronunciation" : "ヲ", "result" : "を", "time" : [ 1890, 1980 ] }, { "pronunciation" : "トル", "result" : "とる", "time" : [ 1980, 2220 ] }, { "pronunciation" : "タメ", "result" : "ため", "time" : [ 2220, 2610 ] }, { "pronunciation" : "ファミリー", "result" : "ファミリー", "time" : [ 2930, 3480 ] }, { "pronunciation" : "レストラン", "result" : "レストラン", "time" : [ 3480, 4020 ] }, { "pronunciation" : "ニ", "result" : "に", "time" : [ 4020, 4150 ] }, { "pronunciation" : "ハイッ", "result" : "入っ", "time" : [ 4150, 4450 ] }, { "pronunciation" : "タ", "result" : "た", "time" : [ 4450, 4540 ] }, { "pronunciation" : "ノ", "result" : "の", "time" : [ 4540, 4680 ] }, { "pronunciation" : "デス", "result" : "です", "time" : [ 4680, 5100 ] } ], "session_id" : "ab86a468-ced4-11e9-915b-42010a92008b", "status" : "recog-finished", "type" : "asr#mimilvcsr" }
```

When you use "nict-asr" and add "v2" as the option, the response is as follows

```shell
python3 asr.py token.txt audio.raw -f v2
#=> {"type":"asr#nictlvcsr2","session_id":"5dfae48d-59fd-49dd-ba53-e148b5507a28","status":"recog-in-progress","response":[{"result":"ちょっと遅い。","words":["ちょっと","遅い","。"],"determined":false,"time":0}]}
#=> {"type":"asr#nictlvcsr2","session_id":"5dfae48d-59fd-49dd-ba53-e148b5507a28","status":"recog-in-progress","response":[{"result":"ちょっと遅い昼食をとるため。","words":["ちょっと","遅い","昼食","を","とる","ため","。"],"determined":false,"time":0}]}
#=> {"type":"asr#nictlvcsr2","session_id":"5dfae48d-59fd-49dd-ba53-e148b5507a28","status":"recog-in-progress","response":[{"result":"ちょっと遅い昼食","words":["ちょっと","遅い","昼食"],"determined":true,"time":0}]}
#=> {"type":"asr#nictlvcsr2","session_id":"5dfae48d-59fd-49dd-ba53-e148b5507a28","status":"recog-in-progress","response":[{"result":"をとるためファミリーレストランに","words":["を","とる","ため","ファミリーレストラン","に"],"determined":false,"time":1890}]}
#=> {"type":"asr#nictlvcsr2","session_id":"5dfae48d-59fd-49dd-ba53-e148b5507a28","status":"recog-in-progress","response":[{"result":"をとるためファミリーレストランに入ったの","words":["を","とる","ため","ファミリーレストラン","に","入っ","た","の"],"determined":false,"time":1890}]}
#=> {"type":"asr#nictlvcsr2","session_id":"5dfae48d-59fd-49dd-ba53-e148b5507a28","status":"recog-in-progress","response":[{"result":"をとるためファミリーレストランに入っ","words":["を","とる","ため","ファミリーレストラン","に","入っ"],"determined":true,"time":1890}]}
#=> {"type":"asr#nictlvcsr2","session_id":"5dfae48d-59fd-49dd-ba53-e148b5507a28","status":"recog-finished","response":[{"result":"たのです。","words":["た","の","です"],"determined":true,"time":4440}]}
#=> recog-finished: received all from server.
#=> {"type":"asr#nictlvcsr2","session_id":"5dfae48d-59fd-49dd-ba53-e148b5507a28","status":"recog-finished","response":[{"result":"たのです。","words":["た","の","です"],"determined":true,"time":4440}]}
```

### Speech Recognition with a microphone
When executing "microphone.py", "start recording" is displayed in your console. Once it's displayed, the system is ready to record. You can stop recording by pressing ctrl + c.

```sh
python3 microphone.py token.txt -f v2 --no-progressive --no-temporary
#=> {"type":"asr#nictlvcsr2","session_id":"5636ae8f-414f-4feb-aa44-256231306a43","status":"recog-finished","response":[{"result":"あらゆる現 実をすべて自分のほうへねじ曲げたのだ。","words":["あらゆる","現実","を","すべて","自分","の","ほう","へ","ねじ曲げ","た","の","だ"],"determined":true,"time":0}]}
#=> recog-finished: received all from server.
```
- Note: 
  - When `input overflow` is displayed, add the `blocksize` parameter to sd.InputStream() function and adjust it.  
    e.g. `sd.InputStream(channels=1, dtype="int16", blocksize=4096, callback=callback)`

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
