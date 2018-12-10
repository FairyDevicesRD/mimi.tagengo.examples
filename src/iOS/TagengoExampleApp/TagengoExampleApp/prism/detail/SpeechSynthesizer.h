//
//  SpeechSynthesizer.h
//  MimiExampleApp
//
//  Created by gonz on 2018/11/20.
//  Copyright © 2018 libmimiio_tls. All rights reserved.
//

#import "RequestData.h"
#import "ResponseData.h"
#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

static NSString *const SS_URL = @"https://sandbox-ss.mimi.fd.ai/speech_synthesis";

@interface SpeechSynthesizer : NSObject {
    NSString *_accessToken;
}

- (id)init;

- (id)initWithAccessToken:(NSString *)accessToken;

- (void)synthesize:(RequestData *)requestData completion:(void (^)(ResponseData *response, NSError *_Nullable error))completion;

- (NSString *)createResponseXML:(SSRequestData *)requestData;


@end

NS_ASSUME_NONNULL_END
