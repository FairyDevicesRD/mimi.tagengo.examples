#import "AccessToken.h"

@implementation AccessToken

- (id)init {
    NSLog(@"call AccessToken init()");
    return self;
}

- (id)initWithProperty:(NSString *)appId :(NSString *)appSecret {
    NSLog(@"call AccessToken init(%@, %@)", appId, appSecret);
    _ID = appId;
    _secret = appSecret;
    return self;
}

- (void)getToken:(void (^)(NSString *_Nullable token, NSError *_Nullable error))completion {
    NSURL *url = [NSURL URLWithString:TOKEN_URL];
    NSURLSessionConfiguration *config = [NSURLSessionConfiguration defaultSessionConfiguration];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url];
    NSURLSession *session = [NSURLSession sessionWithConfiguration:config];

    NSString *param = [NSString stringWithFormat:@"grant_type=%@&client_id=%@&client_secret=%@&scope=%@", GRANT_TYPE, _ID, _secret, SCOPE];
    NSData *body = [param dataUsingEncoding:NSUTF8StringEncoding];

    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:body];

    NSURLSessionDataTask *task = [session dataTaskWithRequest:request completionHandler:
            ^(NSData *_Nullable data, NSURLResponse *_Nullable response, NSError *_Nullable error) {
                NSString *accessToken = nil;
                NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *) response;
                //NSLog(@"status code: %ld", (long)[httpResponse statusCode]);
                if (error) {
                    completion(accessToken, error);
                    return;
                }
                NSError *jsonError = nil;
                if ([httpResponse statusCode] == 200) {
                    NSDictionary *responseDictionary = [NSJSONSerialization JSONObjectWithData:data options:0 error:&jsonError];
                    if (responseDictionary) {
                        accessToken = responseDictionary[@"accessToken"];
                    }
                }
                completion(accessToken, jsonError);
            }];
    [task resume];
}

@end
