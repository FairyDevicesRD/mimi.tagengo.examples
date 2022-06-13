"use strict";

const base_url = "wss://sandbox-sr.mimi.fd.ai";
const audiofile = document.getElementById("audiofile");
const button_start = document.getElementById("button_start");
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

setOption(c_type_elements, content_types)
setOption(input_lang_elements, langs)

const connect = url => {
    const socket = new WebSocket(url);
    socket.onopen = event => {
        button_start.disabled = true;
        mylog("WebSocket open.");

        const file = audiofile.files[0];
        const buflen = 16384;
        for (let cursor = 0; cursor < file.size; cursor += buflen) {
            const blob = file.slice(cursor, cursor + buflen);
            mylog("Sending: " + file.name + "(" + cursor + " / " + file.size + ")");
            event.target.send(blob);
        }
        event.target.send('{"command":"recog-break"}');
        audiofile.value = "";
    };
    socket.onmessage = event => {
        mylog("WebSocket message: " + event.data);
    };
    socket.onerror = event => {
        mylog("WebSocket error");
    };
    socket.onclose = event => {
        mylog("WebSocket close. code: " + event.code + ", reason: " + event.reason);
        button_start.disabled = false;
    };
};

button_start.onclick = async event => {
    if (audiofile.files.length != 1) {
        mylog("ERROR: Specify one and only audio file.");
        throw new Error("Specify one and only audio file.");
    }

    let response_format = ""
    if (v2.checked) {
        response_format = "response_format=v2;progressive=" + progressive_on.checked + ";temporary=" + temporary_on.checked;
    } else {
        response_format = "response_format=v1";
    }

    button_start.disabled = true;
    const url = base_url + "/?process=nict-asr&nict-asr-options=" + encodeURIComponent(response_format) + "&access-token=" + encodeURIComponent(access_token.value) + "&input-language=" + encodeURIComponent(input_lang_elements.value) + "&content-type=" + encodeURIComponent(c_type_elements.value);
    connect(url);
};

function showV2Options() {
    let isV2 = v2.checked ? "" : "none"
    document.getElementById("v2-option").style.display = isV2;
}
window.addEventListener("load", showV2Options);

button_start.disabled = false;
mylog("Please drag & drop your audio file into above input control. Supported type: 16 bit signed int (Little Endian), mono");
