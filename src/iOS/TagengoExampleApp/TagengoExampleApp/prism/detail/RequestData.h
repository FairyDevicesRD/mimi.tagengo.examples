//
//  RequestData.h
//  MimiExampleApp
//
//  Created by gonz on 2018/11/09.
//  Copyright © 2018 libmimiio_tls. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface RequestData : NSObject
@property(nonatomic) NSString *version;
@property(nonatomic) NSString *uttranceId;
@property(nonatomic) NSString *userId;
@end

@interface SRRequestData : RequestData
@property(nonatomic) NSString *lang;
@end

@interface MTRequestData : RequestData
@property(nonatomic) NSString *sourceLang;
@property(nonatomic) NSString *targetLang;
@property(nonatomic) NSString *text;
@end

@interface SSRequestData : RequestData
@property(nonatomic) NSString *text;
@property(nonatomic) NSString *lang;
@property(nonatomic) NSString *gender;
@end

NS_ASSUME_NONNULL_END
