"use strict"

const application_id = document.getElementById("application_id");
const application_secret = document.getElementById("application_secret");
const button_token = document.getElementById("button_token");
const access_token = document.getElementById("access_token");

button_token.onclick = async () => {
    try {
        access_token.value = await getAccessToken(application_id.value, application_secret.value);
    } catch (error) {
        mylog(error);
    }
}

async function getAccessToken(application_id, application_secret) {

    const AUTH_SERVER_URI = "https://auth.mimi.fd.ai/v2/token";
    const SCOPE = "https://apis.mimi.fd.ai/auth/asr/http-api-service;https://apis.mimi.fd.ai/auth/asr/websocket-api-service;https://apis.mimi.fd.ai/auth/nict-asr/http-api-service;https://apis.mimi.fd.ai/auth/nict-asr/websocket-api-service;https://apis.mimi.fd.ai/auth/nict-tts/http-api-service;https://apis.mimi.fd.ai/auth/nict-tra/http-api-service";

    const response = await fetch(AUTH_SERVER_URI, {
        method: "POST",
        body: new URLSearchParams({
            "client_id": application_id,
            "client_secret": application_secret,
            "grant_type": "https://auth.mimi.fd.ai/grant_type/application_credentials",
            "scope": SCOPE
        })
    })

    if (!response.ok) {
        throw Error("failed to issue an access token");
    }

    const data = await response.json();
    const token = data["accessToken"];

    return token;
}
