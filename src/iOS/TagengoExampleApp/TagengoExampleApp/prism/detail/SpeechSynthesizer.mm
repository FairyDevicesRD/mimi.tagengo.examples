//
//  SpeechSynthesizer.m
//  MimiExampleApp
//
//  Created by gonz on 2018/11/20.
//  Copyright © 2018 libmimiio_tls. All rights reserved.
//

#import "SpeechSynthesizer.h"
#import <KissXML/DDXML.h>


@implementation SpeechSynthesizer

- (id)init {
    return self;
}

- (id)initWithAccessToken:(NSString *)accessToken {
    _accessToken = accessToken;
    return self;
}

- (void)synthesize:(RequestData *)requestData completion:(void (^)(ResponseData *response, NSError *_Nullable error))completion {
    SSRequestData *req = (SSRequestData *) requestData;
    NSString *engine = @"nict";
    NSString *text = req.text;
    NSString *lang = req.lang;
    NSString *audio_format = @"RAW";
    NSString *audio_endian = @"Little";
    NSString *gender = [req.gender lowercaseString];
    NSString *age = @"30";

    NSURL *url = [NSURL URLWithString:SS_URL];
    NSURLSessionConfiguration *config = [NSURLSessionConfiguration defaultSessionConfiguration];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url];
    NSURLSession *session = [NSURLSession sessionWithConfiguration:config];

    NSString *param = [NSString stringWithFormat:@"engine=%@&text=%@&lang=%@&audio_format=%@&endian=%@&gender=%@&age=%@", engine, text, lang, audio_format, audio_endian, gender, age];
    NSData *body = [param dataUsingEncoding:NSUTF8StringEncoding];
    if (_accessToken == nil) {
        _accessToken = @"";
    }
    NSDictionary *headerDict = @{@"Authorization": [@"Bearer " stringByAppendingString:_accessToken]};
    [request setAllHTTPHeaderFields:headerDict];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:body];

    NSURLSessionDataTask *task = [session dataTaskWithRequest:request completionHandler:
            ^(NSData *_Nullable data, NSURLResponse *_Nullable response, NSError *_Nullable error) {
                NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *) response;
                if (httpResponse.statusCode != 200) {
                    completion(nil, [NSError errorWithDomain:@""
                                                        code:-1
                                                    userInfo:@{NSLocalizedDescriptionKey: [NSString stringWithFormat:@"Error : connection failed. (%ld)", (long)httpResponse.statusCode]}]);
                    return;
                }
                ResponseData *responseData = [[ResponseData alloc] init];
                NSString *responseXML = [self createResponseXML:(SSRequestData *) requestData];
                [responseData setXML:responseXML];
                [responseData setBinary:data];
                completion(responseData, error);
            }];
    [task resume];
}

- (NSString *)createResponseXML:(SSRequestData *)requestData {
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

    // SS_OUT
    DDXMLElement *ssoutElem = [[DDXMLElement alloc] initWithName:@"SS_OUT"];
    [ssoutElem addAttribute:[DDXMLNode attributeWithName:@"Language" stringValue:requestData.lang]];
    DDXMLElement *outAudioFormatElem = [[DDXMLElement alloc] initWithName:@"OutputAudioFormat"];
    [outAudioFormatElem addAttribute:[DDXMLNode attributeWithName:@"Audio" stringValue:@"RAW"]];
    [outAudioFormatElem addAttribute:[DDXMLNode attributeWithName:@"Endian" stringValue:@"Little"]];
    [outAudioFormatElem addAttribute:[DDXMLNode attributeWithName:@"SamplingFrequency" stringValue:@"16k"]];
    [doc.rootElement addChild:outAudioFormatElem];
    [doc.rootElement addChild:ssoutElem];
    return [doc XMLStringWithOptions:NSXMLNodeCompactEmptyElement];
}
@end
