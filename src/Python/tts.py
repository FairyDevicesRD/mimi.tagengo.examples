import requests
import sys

SS_SERVER_URI = 'https://sandbox-ss.mimi.fd.ai/speech_synthesis'


def synthesize(token, input_lang, text, filename):
    headers = {
        'Authorization': 'Bearer {}'.format(token),
    }
    data = {
        'text': text,
        'lang': input_lang,
        'engine': 'nict',
    }

    url = SS_SERVER_URI
    resp = requests.post(url, headers=headers, data=data)
    if resp.status_code == 200:
        with open(filename, 'wb') as fout:
            fout.write(resp.content)

        return '', 200
    else:
        return resp.json(), resp.status_code


if __name__ == '__main__':
    args = sys.argv
    if len(args) < 5:
        print('Usage: python3 {} access_token input_lang input_text output_filename'.format(
            args[0]))
        sys.exit(1)
    with open(args[1], 'r') as f:
        token = f.read().strip()

    print('start speech synthesis...')
    resp, status = synthesize(token, args[2], args[3], args[4])
    if status != 200:
        print('ERROR: failed speech synthesis')
        sys.exit(1)
    print('saved wav file: {}'.format(args[4]))
