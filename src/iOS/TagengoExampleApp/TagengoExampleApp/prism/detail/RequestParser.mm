//
//  RequestParser.m
//  MimiExampleApp
//
//  Created by gonz on 2018/11/19.
//  Copyright © 2018 libmimiio_tls. All rights reserved.
//

#import <ModelIO/ModelIO.h>
#import "RequestParser.h"
#import "Constants.h"

@implementation RequestParser

- (id)init {
    return self;
}

- (BOOL)parse:(NSString *)xml data:(RequestData *_Nullable __strong *_Nonnull)requestData error:(NSError *_Nullable *_Nonnull)error {
    @try {
        NSData *dataXml = [xml dataUsingEncoding:NSUTF8StringEncoding];
        DDXMLDocument *doc = [[DDXMLDocument alloc] initWithData:dataXml options:0 error:error];
        NSArray *nodes = nil;

        // check SR_IN
        nodes = [doc nodesForXPath:@"/STML/SR_IN" error:nil];
        if ([nodes count] > 0) {
            NSArray *language = [doc nodesForXPath:@"/STML[1]/SR_IN[1]/@Language" error:nil];
            SRRequestData *req = [[SRRequestData alloc] init];
            [req setLang:[[language objectAtIndex:0] stringValue]];
            *requestData = req;
            return true;
        }

        // check MT_IN
        nodes = [doc nodesForXPath:@"/STML/MT_IN" error:nil];
        if ([nodes count] > 0) {
            NSArray *sourceLanguage = [doc nodesForXPath:@"/STML[1]/MT_IN[1]/@SourceLanguage" error:nil];
            NSArray *targetLanguage = [doc nodesForXPath:@"/STML[1]/MT_IN[1]/@TargetLanguage" error:nil];
            NSArray *text = [doc nodesForXPath:@"/STML[1]/MT_IN[1]/s[1]/text()" error:nil];
            MTRequestData *req = [[MTRequestData alloc] init];
            [req setSourceLang:[[sourceLanguage objectAtIndex:0] stringValue]];
            [req setTargetLang:[[targetLanguage objectAtIndex:0] stringValue]];
            [req setText:[[text objectAtIndex:0] stringValue]];
            *requestData = req;
            return true;
        }

        // check SS_IN
        nodes = [doc nodesForXPath:@"/STML/SS_IN" error:nil];
        if ([nodes count] > 0) {
            NSArray *lang = [doc nodesForXPath:@"/STML[1]/SS_IN[1]/@Language" error:nil];
            NSArray *gender = [doc nodesForXPath:@"/STML[1]/SS_IN[1]/Voice[1]/@Gender" error:nil];
            NSArray *text = [doc nodesForXPath:@"/STML[1]/SS_IN[1]/s[1]/text()" error:nil];
            SSRequestData *req = [[SSRequestData alloc] init];
            [req setLang:[[lang objectAtIndex:0] stringValue]];
            [req setGender:[[gender objectAtIndex:0] stringValue]];
            [req setText:[[text objectAtIndex:0] stringValue]];
            *requestData = req;
            return true;
        }
    }
    @catch(NSException *exception) {
        *error = [[NSError alloc] initWithDomain:DOMAIN_NAME code:-1 userInfo:@{NSLocalizedDescriptionKey: @"Error : XML parse error."}];
    }
    return false;
}


@end
