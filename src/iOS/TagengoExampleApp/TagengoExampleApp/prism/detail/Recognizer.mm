//
//  Recognizer.m
//  MimiExampleApp
//
//  Created by gonz on 2018/11/21.
//  Copyright © 2018 libmimiio_tls. All rights reserved.
//

#import "Recognizer.h"
#import "Constants.h"

@implementation Recognizer {

}

void txfunc(char *buffer, size_t *len, bool *recog_break, int *txfunc_error, void *userdata_for_tx) {
    AudioBlockingQueue *queue = static_cast<AudioBlockingQueue *>(userdata_for_tx);
    AudioChunk<int16_t> popdata;
    queue->pop(popdata);
    if (popdata.final()) {
        // NSLog(@"send break");
        *recog_break = true;
    }
    *len = popdata.data().size() * sizeof(int16_t);
    memcpy(buffer, &(popdata.data())[0], *len);
}

void rxfunc(const char *result, size_t len, int *rxfunc_error, void *userdata_for_rx) {
    NSString *resultStr = [NSString stringWithCString:result encoding:NSUTF8StringEncoding];
    std::string *resultJson = static_cast<std::string * >(userdata_for_rx);
    *resultJson = [resultStr UTF8String];
}


- (id)init {
    return [self initWithAccessToken:@""];
}

- (id)initWithAccessToken:(NSString *)accessToken {
    if ((self = [super init])) {
        _requestData = nil;
        _accessToken = accessToken;
    }
    return self;
}

- (void)startRecognize:(RequestData *)requestData completion:(void (^)(ResponseData *response, NSError *_Nullable error))completion {
    _requestData = (SRRequestData *) requestData;

    [[[NSOperationQueue alloc] init] addOperationWithBlock:^{
        const char *mimi_host = "sandbox-sr.mimi.fd.ai";
        int mimi_port = 443;
        MIMIIO_AUDIO_FORMAT format = MIMIIO_RAW_PCM;
        int samplingrate = 16000;
        int channels = 1;
        int custom_request_headers_len = 2;
        MIMIIO_HTTP_REQUEST_HEADER extra_request_headers[custom_request_headers_len];
        strcpy(extra_request_headers[0].key, "x-mimi-process");
        strcpy(extra_request_headers[0].value, "nict-asr");
        strcpy(extra_request_headers[1].key, "x-mimi-input-language");
        strcpy(extra_request_headers[1].value, [self->_requestData.lang UTF8String]);
        const char *access_token = [self->_accessToken UTF8String];
        int loglevel = MIMIIO_LOG_DEBUG;
        int errorno = 0;

        [[NSFileManager defaultManager] changeCurrentDirectoryPath:[[NSBundle mainBundle] bundlePath]];
        self->_mio = mimi_open(
                mimi_host,
                mimi_port,
                txfunc,
                rxfunc,
                static_cast<void *>(&self->_recordingQueue),
                static_cast<void *>(&self->_resultJson),
                format,
                samplingrate,
                channels,
                extra_request_headers,
                custom_request_headers_len,
                access_token,
                loglevel,
                &errorno);
        if (!self->_mio) {
            completion(NULL, [NSError errorWithDomain:@""
                                                 code:-1
                                             userInfo:@{NSLocalizedDescriptionKey: [NSString stringWithFormat:@"Error : connection failed. (%d)", errorno]}]);
            return;
        }
        mimi_start(self->_mio);
        completion(nil, nil);
    }];
}

- (void)addData:(NSData *)data completion:(void (^)(ResponseData *response, NSError *_Nullable error))completion {
    [[[NSOperationQueue alloc] init] addOperationWithBlock:^{
        assert([data length] % sizeof(int16_t) == 0);
        std::vector<int16_t> tmp([data length] / sizeof(int16_t));
        memcpy(&tmp[0], [data bytes], [data length]);
        // NSLog(@"addData length: %d", len);
        AudioChunk<int16_t> a(tmp);
        self->_recordingQueue.push(a);
        completion(NULL, NULL);
    }];
}

- (void)endRecognize:(void (^)(ResponseData *response, NSError *_Nullable error))completion {
    [[[NSOperationQueue alloc] init] addOperationWithBlock:^{
        ResponseData *res = nil;
        NSError *err = nil;
        [self endRecognize:&res error:&err];
        completion(res, err);
    }];
}

- (void)endRecognize:(ResponseData *_Nullable *_Nonnull)response error:(NSError *_Nullable *_Nonnull)error {
    //最終データの表明
    // NSLog(@"push FinalAudioChunk");
    _recordingQueue.push(FinalAudioChunk<int16_t>());

    // NSLog(@"waiting mimi inactive...");
    while (mimi_is_active(_mio)) {
        [NSThread sleepForTimeInterval:0.1];
    }
    // NSLog(@"waiting mimi inactive... ok");
    int errorno = mimi_error(_mio);
    if (errorno != 0) {
        *error = [[NSError alloc] initWithDomain:DOMAIN_NAME code:-1
                                        userInfo:@{NSLocalizedDescriptionKey: [NSString stringWithFormat:@"Error : connection error. (%d)", errorno]}];
    }
    mimi_close(self->_mio);
    *response = [[ResponseData alloc] init];
    NSError *parseerror;
    id jsonObject = [NSJSONSerialization JSONObjectWithData:[[NSString stringWithCString:_resultJson.c_str() encoding:NSUTF8StringEncoding] dataUsingEncoding:NSUTF8StringEncoding]
                                                    options:NSJSONReadingAllowFragments
                                                      error:&parseerror];
    NSMutableArray *results = [[NSMutableArray alloc] init];
    for (NSDictionary *responses in jsonObject[@"response"]) {
        [results addObject:responses[@"result"]];
    }
    NSString *xml = [self createResponseXML:_requestData results:results];
    (*response).XML = xml;
}

- (NSString *)createResponseXML:(SRRequestData *)requestData results:(NSArray *)results {
    NSError *error;
    NSString *xmlTemplate = @"<?xml version=\"1.0\" encoding=\"UTF-8\"?><STML></STML>";
    NSData *dataXml = [xmlTemplate dataUsingEncoding:NSUTF8StringEncoding];
    DDXMLDocument *doc = [[DDXMLDocument alloc] initWithData:dataXml options:0 error:&error];
    [doc.rootElement addAttribute:[DDXMLNode attributeWithName:@"Version" stringValue:requestData.version]];
    [doc.rootElement addAttribute:[DDXMLNode attributeWithName:@"UttranceID" stringValue:requestData.uttranceId]];
    // User
    DDXMLElement *userElem = [[DDXMLElement alloc] initWithName:@"User"];

    [userElem addAttribute:[DDXMLNode attributeWithName:@"ID" stringValue:requestData.userId]];
    [doc.rootElement addChild:userElem];

    // SR_OUT
    DDXMLElement *sroutElem = [[DDXMLElement alloc] initWithName:@"SR_OUT"];
    [sroutElem addAttribute:[DDXMLNode attributeWithName:@"Language" stringValue:requestData.lang]];
    DDXMLElement *nbestElem = [[DDXMLElement alloc] initWithName:@"NBest"];
    [nbestElem addAttribute:[DDXMLNode attributeWithName:@"Order" stringValue:@"1"]];

    DDXMLElement *sentenceElem = [[DDXMLElement alloc] initWithName:@"s"];
    [sentenceElem addAttribute:[DDXMLNode attributeWithName:@"Delimiter" stringValue:@" "]]; // 半角スペース固定
    NSMutableString *combined = [[NSMutableString alloc] init];
    NSMutableArray *wordElems = [[NSMutableArray alloc] init];
    BOOL isFirst = true;
    for (NSString *result in results) {
        NSString *word = [[result componentsSeparatedByString:@"|"] objectAtIndex:0];
        if (isFirst) {
            isFirst = false;
            [combined appendString:word];
        } else {
            if ([word length] > 0) {
                [combined appendString:[@" " stringByAppendingString:word]];
            }
        }
        DDXMLElement *wordElem = [[DDXMLElement alloc] initWithName:@"Word"];
        [wordElem setStringValue:result];
        [wordElems addObject:wordElem];
    }
    [sentenceElem setStringValue:combined];
    for (DDXMLElement *elem in wordElems) {
        [sentenceElem addChild:elem];
    }
    [nbestElem addChild:sentenceElem];
    [sroutElem addChild:nbestElem];
    [doc.rootElement addChild:sroutElem];
    return [doc XMLStringWithOptions:NSXMLNodeCompactEmptyElement];
}
@end
