#import "ClientComCtrl.h"
#import "RequestData.h"
#import "RequestParser.h"
#import "SpeechSynthesizer.h"
#import "Transrator.h"
#import "Recognizer.h"
#import "detail/Constants.h"

@implementation ClientComCtrl {
    NSString *_accessToken;
    RequestData *_requestData;
    BOOL _transferEncodingChunked;
    Recognizer *_recognizer;
}

- (id)initWithAccessToken:(NSString *)accessToken {
    _accessToken = accessToken;
    return self;
}

- (BOOL)isTransferEncodingChunked {
    return _transferEncodingChunked;
}

- (void)setTransferEncodingChunked:(BOOL)chunked {
    _transferEncodingChunked = chunked;
}

# pragma mark SR一括送信

- (void)request:(NSString *)url xmlData:(NSString *)xmlData binaryData:(NSData *)binaryData completionHandler:(void (^ _Nonnull)(ResponseData *_Nullable resData, NSError *_Nullable error))completionHandler {
    NSError *error = nil;

    RequestParser *parser = [[RequestParser alloc] init];
    [parser parse:xmlData data:&_requestData error:&error];
    if (error) {
        // parse error
        completionHandler(nil, error);
    } else if ([_requestData isKindOfClass:[SRRequestData class]]) {
        if (_transferEncodingChunked) {
            error = [[NSError alloc] initWithDomain:DOMAIN_NAME code:-1 userInfo:@{NSLocalizedDescriptionKey: @"Error : Illegal state change."}];
            completionHandler(nil, error);
        } else {
            ;
        }
    } else {
        error = [[NSError alloc] initWithDomain:DOMAIN_NAME code:-1 userInfo:@{NSLocalizedDescriptionKey: @"Error : Illegal state change."}];
        completionHandler(nil, error);
    }
}

# pragma mark SR分割送信開始 / MT / SS

- (void)request:(NSString *_Nonnull)url xmlData:(NSString *_Nonnull)xmlData completionHandler:(void (^ _Nonnull)(ResponseData *_Nullable resData, NSError *_Nullable error))completionHandler {
    // SR分割送信開始 / MT / SS
    NSError *error = nil;
    RequestParser *parser = [[RequestParser alloc] init];
    [parser parse:xmlData data:&_requestData error:&error];
    if (error) {
        // parse error
        completionHandler(nil, error);
    } else if ([_requestData isKindOfClass:[SRRequestData class]]) {
        if (_transferEncodingChunked) {
            // SR分割送信開始処理
            _recognizer = [[Recognizer alloc] initWithAccessToken:_accessToken];
            [_recognizer startRecognize:_requestData completion:^(ResponseData *_Nullable response, NSError *_Nullable error) {
                completionHandler(nil, error);
            }];
        } else {
            error = [[NSError alloc] initWithDomain:DOMAIN_NAME code:-1 userInfo:@{NSLocalizedDescriptionKey: @"Error : Illegal state change."}];
            completionHandler(nil, error);
        }
    } else if ([_requestData isKindOfClass:[MTRequestData class]]) {
        Transrator *tran = [[Transrator alloc] initWithAccessToken:_accessToken];
        [tran transrate:_requestData completion:^(ResponseData *_Nullable response, NSError *_Nullable error) {
            completionHandler(response, error);
        }];
    } else if ([_requestData isKindOfClass:[SSRequestData class]]) {
        SpeechSynthesizer *synth = [[SpeechSynthesizer alloc] initWithAccessToken:_accessToken];
        [synth synthesize:_requestData completion:^(ResponseData *_Nullable response, NSError *_Nullable error) {
            completionHandler(response, error);
        }];
    }
}

# pragma mark SR分割送信データ追加

- (void)request:(NSString *_Nonnull)url binaryData:(NSData *_Nonnull)binaryData completionHandler:(void (^ _Nonnull)(ResponseData *_Nullable resData, NSError *_Nullable error))completionHandler {
    NSError *error = nil;

    if (!_requestData) {
        error = [[NSError alloc] initWithDomain:DOMAIN_NAME code:-1 userInfo:@{NSLocalizedDescriptionKey: @"Error : request XML is empty."}];
        completionHandler(nil, error);
    } else if ([_requestData isKindOfClass:[SRRequestData class]]) {
        if (_transferEncodingChunked) {
            // SR分割送信処理
            [_recognizer addData:binaryData completion:^(ResponseData *_Nullable response, NSError *_Nullable error) {
                completionHandler(response, error);
            }];
        } else {
            error = [[NSError alloc] initWithDomain:DOMAIN_NAME code:-1 userInfo:@{NSLocalizedDescriptionKey: @"Error : Illegal state change."}];
            completionHandler(nil, error);
        }
    } else {
        error = [[NSError alloc] initWithDomain:DOMAIN_NAME code:-1 userInfo:@{NSLocalizedDescriptionKey: @"Error : Illegal state change."}];
        completionHandler(nil, error);
    }
}

# pragma mark SR分割送信終了

- (void)request:(NSString *_Nonnull)url completionHandler:(void (^ _Nonnull)(ResponseData *_Nullable resData, NSError *_Nullable error))completionHandler; {
    NSError *error = nil;

    if (!_requestData) {
        error = [[NSError alloc] initWithDomain:DOMAIN_NAME code:-1 userInfo:@{NSLocalizedDescriptionKey: @"Error : request XML is empty."}];
        completionHandler(nil, error);
    } else if ([_requestData isKindOfClass:[SRRequestData class]]) {
        if (_transferEncodingChunked) {
            // SR分割送信終了処理
            [_recognizer endRecognize:^(ResponseData *_Nullable response, NSError *_Nullable error) {
                completionHandler(response, error);
            }];
        } else {
            error = [[NSError alloc] initWithDomain:DOMAIN_NAME code:-1 userInfo:@{NSLocalizedDescriptionKey: @"Error : Illegal state change."}];
            completionHandler(nil, error);
        }
    } else {
        error = [[NSError alloc] initWithDomain:DOMAIN_NAME code:-1 userInfo:@{NSLocalizedDescriptionKey: @"Error : Illegal state change."}];
        completionHandler(nil, error);
    }
}

- (int)request:(NSString *_Nonnull)url xmlData:(NSString *_Nonnull)xmlData binaryData:(NSData *_Nonnull)binaryData resData:(ResponseData *_Nullable *_Nonnull)resData {
    __block ResponseData *res = nil;
    __block NSError *err = nil;
    dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);
    [self request:url xmlData:xmlData binaryData:binaryData completionHandler:^(ResponseData *_Nullable resData2, NSError *_Nullable error2) {
        res = resData2;
        err = error2;
        dispatch_semaphore_signal(semaphore);
    }];
    dispatch_semaphore_wait(semaphore, DISPATCH_TIME_FOREVER);
    if (resData != nil) {
        *resData = res;
    }
    if (err != nil) {
        NSLog(@"%@",[err localizedDescription]);
        return -1;
    }
    return 0;
}

- (int)request:(NSString *_Nonnull)url xmlData:(NSString *_Nonnull)xmlData resData:(ResponseData *_Nullable *_Nonnull)resData {
    __block ResponseData *res = nil;
    __block NSError *err = nil;
    dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);
    [self request:url xmlData:xmlData completionHandler:^(ResponseData *_Nullable resData2, NSError *_Nullable error2) {
        res = resData2;
        err = error2;
        dispatch_semaphore_signal(semaphore);
    }];
    dispatch_semaphore_wait(semaphore, DISPATCH_TIME_FOREVER);

    if (resData != nil) {
        *resData = res;
    }
    if (err != nil) {
        NSLog(@"%@",[err localizedDescription]);
        return -1;
    }
    return 0;
}

- (int)request:(NSString *_Nonnull)url binaryData:(NSData *_Nonnull)binaryData resData:(ResponseData *_Nullable *_Nonnull)resData {
    __block ResponseData *res = nil;
    __block NSError *err = nil;
    dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);
    [self request:url binaryData:binaryData completionHandler:^(ResponseData *_Nullable resData2, NSError *_Nullable error2) {
        res = resData2;
        err = error2;
        dispatch_semaphore_signal(semaphore);
    }];
    dispatch_semaphore_wait(semaphore, DISPATCH_TIME_FOREVER);
    if (resData != nil) {
        *resData = res;
    }
    if (err != nil) {
        NSLog(@"%@",[err localizedDescription]);
        return -1;
    }
    return 0;
}

- (int)request:(NSString *_Nonnull)url resData:(ResponseData *_Nullable *_Nonnull)resData {
    __block ResponseData *res = nil;
    __block NSError *err = nil;
    dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);
    [self request:url completionHandler:^(ResponseData *_Nullable resData2, NSError *_Nullable error2) {
        res = resData2;
        err = error2;
        dispatch_semaphore_signal(semaphore);
    }];
    dispatch_semaphore_wait(semaphore, DISPATCH_TIME_FOREVER);
    if (resData != nil) {
        *resData = res;
    }
    if (err != nil) {
        NSLog(@"%@",[err localizedDescription]);
        return -1;
    }
    return 0;
}

@end
