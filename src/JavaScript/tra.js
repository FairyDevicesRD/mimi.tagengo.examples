'use strict'

const button_tra = document.getElementById("button_tra");
const src_lang = document.getElementById("src_lang");
const target_lang = document.getElementById("target_lang");
const user_text = document.getElementById("user_text");
const donelist = document.getElementById("donelist");
const mylog = message => {
    const e = document.createElement("li");
    e.appendChild(document.createTextNode(message));
    donelist.insertBefore(e, donelist.firstChild);
};

setOption(src_lang, langs);
setOption(target_lang, langs);

async function translate() {
    const alert_msg = validateInput()
    if (alert_msg) {
        alert(alert_msg)
        return -1;
    }

    fetch('https://sandbox-mt.mimi.fd.ai/machine_translation', {
        method: 'POST',
        headers: {
            'Authorization': 'Bearer ' + access_token.value
        },
        body: new URLSearchParams({
            text: user_text.value,
            source_lang: src_lang.value,
            target_lang: target_lang.value
        }),
    }).then(response => {
        if (!response.ok) {
            throw new Error();
        }
        return response.json()
    }).then(translation => {
        mylog(translation[0])
    }).catch((error) => {
        mylog(error)
    })
}

function preventSetSameLangs() {
    if (this.src_or_dst == 'src') {
        let lang = getCurrentLang(src_lang)
        if (isForeignLang(lang)) {
            target_lang.selectedIndex = 1; // 1 == 'ja'
        }
        else {
            target_lang.selectedIndex = 2; // 2 == 'en'
        }
    }
    else if (this.src_or_dst == 'dst') {
        let lang = getCurrentLang(target_lang)
        if (isForeignLang(lang)) {
            src_lang.selectedIndex = 1; // 1 == 'ja'
        }
        else {
            src_lang.selectedIndex = 2; // 2 == 'en'
        }
    }
}

function getCurrentLang(langObj) {
    let langNum = langObj.selectedIndex;
    let lang = langObj.options[langNum].value;
    return lang;
}

function isForeignLang(lang) {
    return (lang != 'ja' ? true : false)
}

function validateInput(event) {
    let alert_msg = '';
    if (src_lang.selectedIndex === 0 || target_lang.selectedIndex === 0) {
        alert_msg += '言語を選択してください。\n';
    }

    if (user_text.value === '') {
        alert_msg += '文章を入力してください。';
    }
    return alert_msg
}

src_lang.addEventListener('change', { src_or_dst: 'src', handleEvent: preventSetSameLangs });
target_lang.addEventListener('change', { src_or_dst: 'dst', handleEvent: preventSetSameLangs });
button_tra.addEventListener('click', translate);
