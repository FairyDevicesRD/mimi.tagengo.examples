//
//  RequestParser.h
//  MimiExampleApp
//
//  Created by gonz on 2018/11/19.
//  Copyright © 2018 libmimiio_tls. All rights reserved.
//


#import "RequestData.h"
#import <Foundation/Foundation.h>
#import <KissXML/DDXML.h>

NS_ASSUME_NONNULL_BEGIN

@interface RequestParser : NSObject

- (id)init;

- (BOOL)parse:(NSString *)xml data:(RequestData *_Nullable __strong *_Nonnull)requestData error:(NSError *_Nullable *_Nonnull)error;
@end

NS_ASSUME_NONNULL_END
