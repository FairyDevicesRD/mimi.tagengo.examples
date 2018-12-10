#import <Foundation/Foundation.h>
#import <AVFoundation/AVFoundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface Recorder : NSObject {
    AVAudioEngine *_engine;
    AVAudioInputNode *_node;
}

- (void)startRecording:(void (^)(NSError *_Nullable error, NSData *_Nullable buffer))completion;

- (void)stopRecording;

@end

NS_ASSUME_NONNULL_END
