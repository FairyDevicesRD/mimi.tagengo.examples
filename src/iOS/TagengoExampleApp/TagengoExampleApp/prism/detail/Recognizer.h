//
//  Recognizer.h
//  MimiExampleApp
//
//  Created by gonz on 2018/11/21.
//  Copyright © 2018 libmimiio_tls. All rights reserved.
//

#import "RequestData.h"
#import "ResponseData.h"
#include "BlockingQueue.h"

#import <Foundation/Foundation.h>
#import <KissXML/DDXML.h>
#include <vector>
#include <mimiio.h>

NS_ASSUME_NONNULL_BEGIN

static NSString *const SR_URL = @"https://sandbox-sr.mimi.fd.ai";

@interface Recognizer : NSObject {
    NSString *_accessToken;
    SRRequestData *_requestData;
    MIMI_IO *_mio;
    AudioBlockingQueue _recordingQueue;
    std::string _resultJson;
}

- (id)initWithAccessToken:(NSString *)accessToken;

- (void)startRecognize:(RequestData *)requestData completion:(void (^)(ResponseData *response, NSError *_Nullable error))completion;

- (void)addData:(NSData *)data completion:(void (^)(ResponseData *response, NSError *_Nullable error))completion;

- (void)endRecognize:(void (^)(ResponseData *response, NSError *_Nullable error))completion;

- (void)endRecognize:(ResponseData *_Nullable *_Nonnull)response error:(NSError *_Nullable *_Nonnull)error;

- (NSString *)createResponseXML:(SRRequestData *)requestData results:(NSArray *)results;


@end

NS_ASSUME_NONNULL_END
