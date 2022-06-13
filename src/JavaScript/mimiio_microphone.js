"use strict";

const time_interval = 300;
let mediaRecorder = null;

const base_url = "wss://sandbox-sr.mimi.fd.ai";
const button_rec_start = document.getElementById("button_rec_start");
const button_rec_stop = document.getElementById("button_rec_stop");
const donelist = document.getElementById("donelist");
const c_type_elements = document.getElementById("content_type");
const input_lang_elements = document.getElementById("input_lang");
const v2 = document.getElementById("v2");
const progressive_on = document.getElementById("progressive_on");
const temporary_on = document.getElementById("temporary_on");
const mylog = message => {
    const e = document.createElement("li");
    e.appendChild(document.createTextNode(message));
    donelist.insertBefore(e, donelist.firstChild);
};

setOption(c_type_elements, content_types_mic)
setOption(input_lang_elements, langs)

const connect = url => {
    const socket = new WebSocket(url);
    socket.onopen = event => {
        mylog("WebSocket open.");
        navigator.mediaDevices.getUserMedia({ audio: true }).then(stream => {
            mediaRecorder = new MediaStreamRecorder(stream);
            mediaRecorder.audioChannels = 1;
            mediaRecorder.mimeType = 'audio/pcm';
            mediaRecorder.ondataavailable = blob => {
                const now = new Date();
                mylog("Sending: microphone source (time : " +
                    now.getHours().toString().padStart(2, "0") + ":" +
                    now.getMinutes().toString().padStart(2, "0") + ":" +
                    now.getSeconds().toString().padStart(2, "0") + "." +
                    now.getMilliseconds().toString().padStart(4, "0") +
                    " length: " + blob.size + "byte)");
                socket.send(blob);
            };
            mediaRecorder.onstop = () => {
                socket.send('{"command":"recog-break"}');
            };
            mediaRecorder.start(time_interval);
        }).catch(e => {
            mylog('media error: ' + e);
        });
    };
    socket.onmessage = event => {
        mylog("WebSocket message: " + event.data);
    };
    socket.onerror = event => {
        mylog("WebSocket error");
    };
    socket.onclose = event => {
        mylog("WebSocket close. code: " + event.code + ", reason: " + event.reason);
        button_rec_start.disabled = false;
        button_rec_stop.disabled = true;
    };
};

button_rec_start.onclick = async event => {
    let response_format = ""
    if (v2.checked) {
        response_format = "response_format=v2;progressive=" + progressive_on.checked + ";temporary=" + temporary_on.checked;
    } else {
        response_format = "response_format=v1";
    }

    button_rec_start.disabled = true;
    button_rec_stop.disabled = false;

    const url = base_url + "/?process=nict-asr&nict-asr-options=" + encodeURIComponent(response_format) + "&access-token=" + encodeURIComponent(access_token.value) + "&input-language=" + encodeURIComponent(input_lang_elements.value) + "&content-type=" + encodeURIComponent(c_type_elements.value);
    connect(url);
};

button_rec_stop.onclick = event => {
    mediaRecorder.stop();
    button_rec_stop.disabled = true;
}

function showV2Options() {
    let isV2 = v2.checked ? "" : "none"
    document.getElementById("v2-option").style.display = isV2;
}

window.addEventListener("load", showV2Options);
