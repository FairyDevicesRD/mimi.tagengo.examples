import requests
import json
import sys


SR_SERVER_URI_HTTP = "https://sandbox-sr.mimi.fd.ai"


def recognize(token, file_data):
    headers = {
        "Authorization": "Bearer {}".format(token),
        "x-mimi-process": "nict-asr",  # when using NICT engine use "nict-asr"
        "x-mimi-input-language": "ja",
        "Content-Type": "audio/x-pcm;bit=16;rate=16000;channels=1",
    }

    url = SR_SERVER_URI_HTTP
    resp = requests.post(url, headers=headers, data=file_data)
    return (resp.json(), resp.status_code)


if __name__ == '__main__':
    args = sys.argv
    if len(args) < 3:
        print('Usage: python3 {} access_token audio_file'.format(args[0]))
        sys.exit(1)
    with open(args[1], 'r') as f:
        token = f.read().strip()
    with open(args[2], 'rb') as f:
        file_data = f.read()

    print('start recognition...')
    resp, status = recognize(token, file_data)
    if status != 200:
        print('ERROR: failed speech recognition')
        sys.exit(1)
    print(resp)
