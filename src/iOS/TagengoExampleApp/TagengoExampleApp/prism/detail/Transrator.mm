//
//  Transrator.m
//  MimiExampleApp
//
//  Created by gonz on 2018/11/09.
//  Copyright © 2018 libmimiio_tls. All rights reserved.
//

#import "Transrator.h"
#import <KissXML/DDXML.h>

@implementation Transrator

- (id)init {
    return self;
}

- (id)initWithAccessToken:(NSString *)accessToken {
    _accessToken = accessToken;
    return self;
}

- (void)transrate:(RequestData *)requestData completion:(void (^)(ResponseData *response, NSError *_Nullable error))completion {
    MTRequestData *req = (MTRequestData *) requestData;
    NSString *source_lang = req.sourceLang;
    NSString *target_lang = req.targetLang;
    NSString *text = req.text;

    NSURL *url = [NSURL URLWithString:MT_URL];
    NSURLSessionConfiguration *config = [NSURLSessionConfiguration defaultSessionConfiguration];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url];
    NSURLSession *session = [NSURLSession sessionWithConfiguration:config];

    NSString *param = [NSString stringWithFormat:@"source_lang=%@&target_lang=%@&text=%@", source_lang, target_lang, text];
    NSData *body = [param dataUsingEncoding:NSUTF8StringEncoding];
    if(_accessToken == nil) {
        _accessToken = @"";
    }
    NSDictionary *headerDict = @{@"Authorization": [@"Bearer " stringByAppendingString:_accessToken]};
    [request setAllHTTPHeaderFields:headerDict];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:body];

    NSURLSessionDataTask *task = [session dataTaskWithRequest:request completionHandler:
            ^(NSData *_Nullable data, NSURLResponse *_Nullable response, NSError *_Nullable error) {
                NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse*) response;
                if (httpResponse.statusCode != 200 ) {
                    completion(nil,[NSError errorWithDomain:@""
                                                       code:-1
                                                   userInfo:@{NSLocalizedDescriptionKey: [NSString stringWithFormat:@"Error : connection failed. (%ld)",(long)httpResponse.statusCode]}]);
                    return;
                }
                ResponseData *responseData = [[ResponseData alloc] init];
                NSString *responseXML = [self createResponseXML:(MTRequestData *) requestData result:[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]];
                [responseData setXML:responseXML];
                completion(responseData, error);
            }];
    [task resume];
}

- (NSString *)createResponseXML:(MTRequestData *)requestData result:(NSString *)result {

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

    // MT_OUT
    DDXMLElement *mtoutElem = [[DDXMLElement alloc] initWithName:@"MT_OUT"];
    [mtoutElem addAttribute:[DDXMLNode attributeWithName:@"SourceLanguage" stringValue:requestData.sourceLang]];
    [mtoutElem addAttribute:[DDXMLNode attributeWithName:@"TargetLanguage" stringValue:requestData.targetLang]];
    DDXMLElement *nbestElem = [[DDXMLElement alloc] initWithName:@"NBest"];
    [nbestElem addAttribute:[DDXMLNode attributeWithName:@"Order" stringValue:@"1"]];
    DDXMLElement *sentenceElem = [[DDXMLElement alloc] initWithName:@"s"];
    [sentenceElem addAttribute:[DDXMLNode attributeWithName:@"Delimiter" stringValue:@" "]]; // 半角スペース固定
    NSString *replacedResult = [[result stringByReplacingOccurrencesOfString:@"[\"" withString:@""] stringByReplacingOccurrencesOfString:@"\"]" withString:@""];
    [sentenceElem setStringValue:replacedResult];

    [nbestElem addChild:sentenceElem];
    [mtoutElem addChild:nbestElem];
    [doc.rootElement addChild:mtoutElem];

    //NSLog(@"error: %@", error);

    return [doc XMLStringWithOptions:NSXMLNodeCompactEmptyElement];
}
@end



