import requests
import sys

MT_SERVER_URI = 'https://sandbox-mt.mimi.fd.ai/machine_translation'


def translate(token, input_lang, text, output_lang):
    headers = {
        'Authorization': 'Bearer {}'.format(token),
    }
    data = {
        'text': text,
        'source_lang': input_lang,
        'target_lang': output_lang,
    }

    url = MT_SERVER_URI
    resp = requests.post(url, headers=headers, data=data)
    return (resp.json(), resp.status_code)


if __name__ == '__main__':
    args = sys.argv
    if len(args) < 5:
        print('Usage: python3 {} access_token input_lang input_text output_lang'.format(
            args[0]))
        sys.exit(1)
    with open(args[1], 'r') as f:
        token = f.read().strip()

    print('start translation...')
    resp, status = translate(token, args[2], args[3], args[4])
    if status != 200:
        print('ERROR: failed machine translation')
        sys.exit(1)
    print(resp)
