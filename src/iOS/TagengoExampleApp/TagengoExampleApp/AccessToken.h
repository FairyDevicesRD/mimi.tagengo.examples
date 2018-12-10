#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

static NSString *const TOKEN_URL = @"https://auth.mimi.fd.ai/v2/token";
static NSString *const GRANT_TYPE = @"https://auth.mimi.fd.ai/grant_type/application_credentials";
static NSString *const SCOPE = @"https://apis.mimi.fd.ai/auth/nict-asr/http-api-service;"
                               "https://apis.mimi.fd.ai/auth/nict-asr/websocket-api-service;"
                               "https://apis.mimi.fd.ai/auth/nict-tra/http-api-service;"
                               "https://apis.mimi.fd.ai/auth/nict-tts/http-api-service";

@interface AccessToken : NSObject {
    NSString *_ID;
    NSString *_secret;
}

- (id)init;

- (id)initWithProperty:(NSString *)appId :(NSString *)appSecret;

- (void)getToken:(void (^)(NSString *_Nullable token, NSError *_Nullable error))completion;

@end

NS_ASSUME_NONNULL_END
