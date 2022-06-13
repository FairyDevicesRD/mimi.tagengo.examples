import argparse
import asyncio
import json
import sys
import websockets

SAMPLES_PER_CHUNK = 1024

SR_SERVER_URI = "wss://sandbox-sr.mimi.fd.ai:443"

async def recognize(token, file_data, response_format, enable_progressive, enable_temporary):
    headers = {
        "Authorization": "Bearer {}".format(token),
        "x-mimi-process": "nict-asr",  # when using mimi ASR (not mimi ASR powered by NICT) use "asr"
        "x-mimi-input-language": "ja",
        "Content-Type": "audio/x-pcm;bit=16;rate=16000;channels=1",
    }
    if headers["x-mimi-process"] == "nict-asr" and response_format == "v2":
        headers["x-mimi-nict-asr-options"] = f"response_format={response_format};progressive={enable_progressive};temporary={enable_temporary}"
    else:
        headers["x-mimi-nict-asr-options"] = f"response_format={response_format}"

    try:
        resp = ""
        async with websockets.connect(
                SR_SERVER_URI,
                extra_headers=headers,
                ping_interval=None
        ) as ws:

            file_size = len(file_data)
            sent_size = 0
            while sent_size < file_size:
                await ws.send(file_data[sent_size:sent_size + SAMPLES_PER_CHUNK * 2])
                sent_size += SAMPLES_PER_CHUNK * 2
            await ws.send(json.dumps({"command": "recog-break"}))
            while True:
                resp = await ws.recv()
                print(resp)
                if json.loads(resp)["status"] == "recog-finished":
                    print("recog-finished: received all from server.", file=sys.stderr)
                    break

    except websockets.exceptions.ConnectionClosed:
        print("connection closed from server", file=sys.stderr)

    return resp


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument('token')
    parser.add_argument('audio')
    parser.add_argument('-f', '--response-format', default='v1')
    parser.add_argument('--no-progressive', action='store_true', help='disable progressive option')
    parser.add_argument('--no-temporary', action='store_true', help='disable temporary option')
    args = parser.parse_args()

    with open(args.token, "r") as f:
        token = f.read().strip()
    with open(args.audio, "rb") as f:
        file_data = f.read()

    print("start recognition...", file=sys.stderr)
    try:
        asyncio.run(
            recognize(token, file_data, args.response_format, not args.no_progressive, not args.no_temporary))
    except Exception as e:
        print("ERROR: {}".format(e), file=sys.stderr)
        sys.exit(1)
