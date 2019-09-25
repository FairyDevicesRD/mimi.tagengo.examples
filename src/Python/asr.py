import asyncio
import websockets
import json
import sys

SAMPLES_PER_CHUNK = 1024

SR_SERVER_URI = "wss://sandbox-sr.mimi.fd.ai:443"


async def recognize(token, file_data):
    headers = {
        "Authorization": "Bearer {}".format(token),
        "x-mimi-process": "nict-asr",  # when using NICT engine use "nict-asr"
        "x-mimi-input-language": "ja",
        "Content-Type": "audio/x-pcm;bit=16;rate=16000;channels=1",
    }

    try:
        resp = ""
        async with websockets.connect(
                SR_SERVER_URI,
                extra_headers=headers
        ) as ws:

            file_size = len(file_data)
            sent_size = 0
            while sent_size < file_size:
                await ws.send(file_data[sent_size:sent_size + SAMPLES_PER_CHUNK * 2])
                sent_size += SAMPLES_PER_CHUNK * 2
            await ws.send(json.dumps({"command": "recog-break"}))
            while True:
                resp = await ws.recv()
                if json.loads(resp)['status'] == 'recog-finished':
                    print('recog-finished: received all from server.')
                    break

    except websockets.exceptions.ConnectionClosed:
        print('connection closed from server')

    return resp


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
    try:
        resp = asyncio.get_event_loop().run_until_complete(recognize(token, file_data))
        print(resp)
    except Exception as e:
        print('ERROR: {}'.format(e))
        sys.exit(1)
