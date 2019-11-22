# Sample code in Swift

## Preparation

use library [StarScream](https://github.com/daltoniam/Starscream) for ASR/main.swift

1. If you use command `ASR`, `TTS`, `TRA`, prepare the text file which has access token. Program reference only the first line.

1. If you use `ASR`, prepare the raw audio file (headerless linear PCM) which has 16bit, 16kHz bitrate, and 1 channel.

1. Create Xcode project as `Command Line Tool`

1. Replace default-created file `main.swift` to the target command source file `main.swift`

1. Add library by [Project]-tab["General"]

## Debug

1. Edit arguments setting from menu [Product]-[Scheme]-[Edit scheme...]

## Build

1. When you build, take care about the scheme tab"info"'. Whether the build target is debug or release.

## Execution

### Speech Recognition 

```exec file
./ASR access_token_file audio_file

ex.
./ASR /temp/access_token.txt /tmp/audio.raw
#=> { "response" : [ { "pronunciation" : "チョット", "result" : "ちょっと", "time" : [ 1350, 1800 ] }, { "pronunciation" : "オソイ", "result" : "遅い", "time" : [ 1800, 2170 ] }, { "pronunciation" : "チューショク", "result" : "昼食", "time" : [ 2170, 2730 ] }, { "pronunciation" : "ヲ", "result" : "を", "time" : [ 2730, 2850 ] }, { "pronunciation" : "トル", "result" : "とる", "time" : [ 2850, 3090 ] }, { "pronunciation" : "タメ", "result" : "ため", "time" : [ 3090, 3370 ] }, { "pronunciation" : "ニ", "result" : "に", "time" : [ 3370, 3480 ] }, { "pronunciation" : "ファミリー", "result" : "ファミリー", "time" : [ 3480, 4000 ] }, { "pronunciation" : "レストラン", "result" : "レストラン", "time" : [ 4000, 4530 ] }, { "pronunciation" : "ニ", "result" : "に", "time" : [ 4530, 4620 ] }, { "pronunciation" : "ハイッ", "result" : "入っ", "time" : [ 4620, 4960 ] }, { "pronunciation" : "タ", "result" : "た", "time" : [ 4960, 5060 ] }, { "pronunciation" : "ノ", "result" : "の", "time" : [ 5060, 5180 ] }, { "pronunciation" : "デス", "result" : "です", "time" : [ 5180, 5630 ] } ], "session_id" : "bdae24ae-fbb1-11e9-9448-42010a920021", "status" : "recog-finished", "type" : "asr#mimilvcsr" }
recog-finished: received all from server.
```

When you use "nict-asr", the response is as follows

```shell
python3 asr.py token.txt audio.raw
#=> {"type": "asr#nictlvcsr", "session_id": "99aecbb2-fbb6-11e9-92e5-42010a92003a", "status": "recog-finished", "response": [{"result": "ちょっと|チョット|ちょっと|副詞-助詞類接続||||"},{"result": "遅い|オソイ|遅い|形容詞-自立|形容詞・アウオ段|基本形||"},{"result": "昼食|チュウショク|昼食|名詞-一般||||"},{"result": "を|ヲ|を|助詞-格助詞-一般||||"},{"result": "とる|トル|とる|動詞-自立|五段・ラ行|基本形||"},{"result": "ため|タメ|ため|名詞-非自立-副詞可能||||"},{"result": "に|ニ|に|助詞-格助詞-一般||||"},{"result": "ファミリーレストラン|ファミリーレストラン|ファミリーレストラン|名詞-一般||||"},{"result": "に|ニ|に|助詞-格助詞-一般||||"},{"result": "入っ|ハイッ|入る|動詞-自立|五段・ラ行|連用タ接続||"},{"result": "た|タ|た|助動詞|特殊・タ|基本形||"},{"result": "の|ノ|の|名詞-非自立-一般||||"},{"result": "です|デス|です|助動詞|特殊・デス|基本形||"},{"result": "|||SENT-START-END||||"},{"result": "|||UTT-END||||"}]}
recog-finished: received all from server.
```

### Machine Translation

```exec file
./TRA access_token_file input_lang input_text output_lang

ex.
./TRA /tmp/token.txt ja "こんにちは" en
#=> ['Hello.']
```

### Speech Synthesis

```exec file
./TTS access_token_file input_lang input_text output_filename

ex.
./TTS /temp/token.text ja "こんにちは" /temp/out.wav
```


