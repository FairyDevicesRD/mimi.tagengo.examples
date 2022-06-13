import argparse
import asyncio
import json
import logging
import signal
import sounddevice as sd
import sys
import websockets

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

SIGNAL_INT = False

SR_SERVER_URI = "wss://sandbox-sr.mimi.fd.ai:443"

async def recognize(token, sampling_rate, response_format, enable_progressive, enable_temporary):
    headers = {
        "Authorization": "Bearer {}".format(token),
        # when using mimi ASR (not mimi ASR powered by NICT) use "asr"
        "x-mimi-process": "nict-asr",
        "x-mimi-input-language": "ja",
        "Content-Type": "audio/x-pcm;bit=16;rate=" + str(sampling_rate) + ";channels=1",
    }
    if headers["x-mimi-process"] == "nict-asr" and response_format == "v2":
        headers["x-mimi-nict-asr-options"] = f"response_format={response_format};progressive={enable_progressive};temporary={enable_temporary}"
    else:
        headers["x-mimi-nict-asr-options"] = f"response_format={response_format}"

    try:
        async with websockets.connect(
                SR_SERVER_URI,
                extra_headers=headers,
                ping_interval=None
        ) as ws:
            await asyncio.gather(
                send(ws),
                receive(ws)
            )
    except websockets.exceptions.ConnectionClosed:
        logger.error("connection closed from server")


async def record():
    q = asyncio.Queue()
    loop = asyncio.get_event_loop()

    def callback(indata, frame_count, time_info, status):
        if status:
            logger.info(status)
        if SIGNAL_INT:
            loop.call_soon_threadsafe(
                q.put_nowait, (indata.copy(), True))
            raise sd.CallbackStop

        loop.call_soon_threadsafe(q.put_nowait, (indata.copy(), False))

    stream = sd.InputStream(channels=1, dtype="int16", callback=callback)
    with stream:
        while True:
            audio, is_final = await q.get()
            yield audio, is_final


async def send(ws):
    async for audio, is_final in record():
        await ws.send(audio.tobytes())
        if is_final:
            await ws.send(json.dumps({"command": "recog-break"}))
            break


async def receive(ws):
    async for message in ws:
        print(message)
        if json.loads(message)["status"] == "recog-finished":
            logger.info("recog-finished: received all from server.")


def signal_handler(signal, frame):
    global SIGNAL_INT
    SIGNAL_INT = True


if __name__ == "__main__":
    signal.signal(signal.SIGINT, signal_handler)

    parser = argparse.ArgumentParser()
    parser.add_argument('token')
    parser.add_argument('-f', '--response-format', default='v1')
    parser.add_argument('--no-progressive', action='store_true',
                        help='disable progressive option')
    parser.add_argument('--no-temporary', action='store_true',
                        help='disable temporary option')
    args = parser.parse_args()

    with open(args.token, "r") as f:
        token = f.read().strip()

    device_info = sd.query_devices(kind="input")
    sampling_rate = int(device_info["default_samplerate"])

    print("#" * 80, file=sys.stderr)
    print("start recording...\npress Ctrl+C to stop recording", file=sys.stderr)
    print("#" * 80, file=sys.stderr)

    asyncio.run(
        recognize(token, sampling_rate, args.response_format, not args.no_progressive, not args.no_temporary))
