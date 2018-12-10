#import <Foundation/Foundation.h>

@interface Player : NSObject

+ (instancetype)sharedInstance;

- (BOOL)play:(NSData *)audioData error:(NSError **)error;

- (void)stop;

+ (NSData *)appendWavHeader:(NSData *)audioData;

@end
