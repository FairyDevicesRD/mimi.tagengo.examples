#import "Recorder.h"

@implementation Recorder

- (void)startRecording:(void (^)(NSError *_Nullable, NSData *_Nullable))completion {
    NSError *error = nil;

    _engine = [[AVAudioEngine alloc] init];
    _node = _engine.inputNode;
    AVAudioFormat *inputFormat = [_node outputFormatForBus:0];
    AVAudioFormat *outputFormat = [[AVAudioFormat alloc] initWithCommonFormat:AVAudioPCMFormatInt16 sampleRate:16000 channels:1 interleaved:false];
    AVAudioConverter *converter = [[AVAudioConverter alloc] initFromFormat:inputFormat toFormat:outputFormat];

    int bufferSize = 2048;

    [_node installTapOnBus:0 bufferSize:bufferSize format:inputFormat block:^(AVAudioPCMBuffer *_Nonnull buffer, AVAudioTime *_Nonnull when) {
        AVAudioFrameCount resampledFrameSize = buffer.frameCapacity * (16000 / inputFormat.sampleRate);
        AVAudioPCMBuffer *outputBuffer = [[AVAudioPCMBuffer alloc] initWithPCMFormat:outputFormat frameCapacity:resampledFrameSize];
        // ダウンサンプリング
        NSError *resamplingError = nil;

        [converter convertToBuffer:outputBuffer error:&resamplingError withInputFromBlock:^AVAudioBuffer *(AVAudioPacketCount inNumberOfPackets, AVAudioConverterInputStatus *outStatus) {
            //NSLog(@"call downsampling... error:%@", resamplingError);
            *outStatus = AVAudioConverterInputStatus_HaveData;
            return buffer;
        }];

        //NSLog(@"outputBuffer.frameLength: %u", outputBuffer.frameLength);
        NSData *data = [[NSData alloc] initWithBytes:outputBuffer.int16ChannelData[0] length:outputBuffer.frameLength * sizeof(int16_t)];

        if (data) {
            completion(error, data);
        }
    }];
    // start
    //NSLog(@"start recording.");
    if (![_engine startAndReturnError:&error]) {
        completion(error, nil);
    }
}

- (void)stopRecording {
    [_engine stop];
    [_node removeTapOnBus:0];
}

@end


