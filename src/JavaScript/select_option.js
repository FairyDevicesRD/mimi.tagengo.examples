'use strict'

const content_types = {
    "": "Select Content-Type",
    "audio/x-pcm;bit=16;rate=16000;channels=1": "audio/x-pcm;bit=16;rate=16000;channels=1",
    "audio/x-pcm;bit=16;rate=48000;channels=1": "audio/x-pcm;bit=16;rate=48000;channels=1",
    "audio/x-flac;bit=16;rate=16000;channels=1": "audio/x-flac;bit=16;rate=16000;channels=1",
    "audio/x-flac;bit=16;rate=48000;channels=1": "audio/x-flac;bit=16;rate=48000;channels=1",
}

const content_types_mic = {
    "": "Select Content-Type",
    "audio/x-pcm;bit=16;rate=44100;channels=1": "audio/x-pcm;bit=16;rate=44100;channels=1",
    "audio/x-pcm;bit=16;rate=48000;channels=1": "audio/x-pcm;bit=16;rate=48000;channels=1",
}

const langs = {
    "": "Select language",
    "ja": "日",
    "en": "英",
    "zh": "中(簡体)",
    "zh-TW": "中(繁体)",
    "ko": "韓",
    "vi": "ベトナム",
    "my": "ミャンマー",
    "th": "タイ",
    "id": "インドネシア",
    "es": "スペイン",
    "fr": "フランス"
}

const genders = {
    "": "Select gender",
    "female": "女性",
    "male": "男性",
}

function setOption(selectObj, data) {
    for(let key in data) {
        let option = document.createElement("option");
        option.value = key;
        option.innerHTML = data[key];
        selectObj.appendChild(option);
    }
}
