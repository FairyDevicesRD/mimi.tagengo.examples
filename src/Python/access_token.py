import requests
import sys
import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

AUTH_SERVER_URI = 'https://auth.mimi.fd.ai/v2/token'
APPLICATION_ID = 'XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX'  # Fix me
APPLICATION_SECRET = 'XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX'  # Fix me
SCOPE = 'https://apis.mimi.fd.ai/auth/asr/http-api-service;https://apis.mimi.fd.ai/auth/asr/websocket-api-service;https://apis.mimi.fd.ai/auth/nict-asr/http-api-service;https://apis.mimi.fd.ai/auth/nict-asr/websocket-api-service;https://apis.mimi.fd.ai/auth/nict-tts/http-api-service;https://apis.mimi.fd.ai/auth/nict-tra/http-api-service'


def get_access_token():
    resp, status = http_request()
    if status != 200:
        logger.error('failed to get an access token')
        sys.exit(1)
    access_token = resp['accessToken']
    return access_token


def http_request():
    data = {
        'client_id': APPLICATION_ID,
        'client_secret': APPLICATION_SECRET,
        'grant_type': 'https://auth.mimi.fd.ai/grant_type/application_credentials',
        'scope': SCOPE
    }
    url = AUTH_SERVER_URI
    resp = requests.post(url, data=data)
    return (resp.json(), resp.status_code)


if __name__ == '__main__':
    logger.info('getting an access token...')
    print(get_access_token())
