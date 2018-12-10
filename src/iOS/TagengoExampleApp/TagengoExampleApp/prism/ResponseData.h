//
//  ResponseData.h
//  MimiExampleApp
//
//  Created by gonz on 2018/11/13.
//  Copyright © 2018 libmimiio_tls. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface ResponseData : NSObject {
}
- (id)init;

@property(nonatomic) NSString *XML;
@property(nonatomic) NSData *binary;
@end

NS_ASSUME_NONNULL_END

