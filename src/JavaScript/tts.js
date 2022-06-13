'use strict'

const button_tts = document.getElementById("button_tts");
const lang = document.getElementById("lang");
const gender = document.getElementById("gender");
const user_text = document.getElementById("user_text")
const ss_audio = document.getElementById("ss_audio")
const error_text = document.getElementById("error_text")
const donelist = document.getElementById("donelist");
const mylog = message => {
    const e = document.createElement("li");
    e.appendChild(document.createTextNode(message));
    donelist.insertBefore(e, donelist.firstChild);
};

setOption(lang, langs)
setOption(gender, genders)

async function synthesize() {
    const alert_msg = validateInput()
    if (alert_msg) {
        alert(alert_msg)
        return -1;
    }

    fetch('https://sandbox-ss.mimi.fd.ai/speech_synthesis', {
        method: 'POST',
        headers: {
            'Authorization': 'Bearer ' + access_token.value
        },
        body: new URLSearchParams({
            lang: lang.value,
            gender: gender.value,
            engine: 'nict',
            text: user_text.value
        }),
    }).then(response => {
        if (!response.ok) {
            throw new Error();
        }
        return response.blob()
    }).then(res_blob => {
        window.URL = window.URL || window.webkitURL;
        ss_audio.src = window.URL.createObjectURL(res_blob);
    }).catch((error) => {
        error_text.innerHTML = error
    })
}

function validateInput() {
    let alert_msg = '';
    if (lang.selectedIndex === 0) {
        alert_msg = '言語を選択してください。\n';
    }
    if (gender.selectedIndex === 0) {
        alert_msg += '性別を選択してください。\n';
    }
    if (user_text.value === '') {
        alert_msg += 'テキストを入力してください';
    }
    return alert_msg
}

button_tts.addEventListener('click', synthesize);
