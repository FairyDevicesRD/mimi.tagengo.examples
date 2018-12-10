//
//  Transrator.h
//  MimiExampleApp
//
//  Created by gonz on 2018/11/09.
//  Copyright © 2018 libmimiio_tls. All rights reserved.
//
#import "RequestData.h"
#import "ResponseData.h"
#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

static NSString *const MT_URL = @"https://sandbox-mt.mimi.fd.ai/machine_translation";

@interface Transrator : NSObject {
    NSString *_accessToken;
}

- (id)init;

- (id)initWithAccessToken:(NSString *)accessToken;

- (void)transrate:(RequestData *)requestData completion:(void (^)(ResponseData *response, NSError *_Nullable error))completion;

- (NSString *)createResponseXML:(MTRequestData *)requestData result:(NSString *)result;

@end

NS_ASSUME_NONNULL_END
