#import <AVFoundation/AVFoundation.h>
#import "Player.h"
#import "Constants.h"

@interface Player () <AVAudioPlayerDelegate>
@end

@implementation Player {
    AVAudioPlayer *_player;
}

+ (instancetype)sharedInstance {
    static dispatch_once_t once;
    static id instance;
    dispatch_once(&once, ^{
        instance = [[self alloc] init];
    });
    return instance;
}

- (BOOL)play:(NSData *)audioData error:(NSError **)error {
    if (audioData) {
        _player = [[AVAudioPlayer alloc] initWithData:audioData error:error];
        if (*error) {
            return false;
        }
        _player.delegate = self;
        if (![_player play]) {
            *error = [[NSError alloc] initWithDomain:DOMAIN_NAME code:-1 userInfo:@{NSLocalizedDescriptionKey: @"Error : AVAudioPlayer play failed."}];
            return false;
        }
        return true;
    }
    NSLog(@"audioData is nil");
    return false;
}

- (void)stop {
    [_player stop];
    _player = nil;
}

static NSData *nsStringToNSData(NSString *value) {
    return [NSData dataWithBytes:[value UTF8String] length:[value lengthOfBytesUsingEncoding:NSUTF8StringEncoding]];
}

static NSData *int32tToNSData(int32_t value) {
    return [NSData dataWithBytes:(char *) &value length:sizeof(int32_t)];
}

static NSData *int16tToNSData(int16_t value) {
    return [NSData dataWithBytes:(char *) &value length:sizeof(int16_t)];
}

+ (NSData *)appendWavHeader:(NSData *)audioData {
    const int SampleRate = 16000;
    const int BitPerSample = 16;
    const int ChannelN = 1;
    const int HeaderSize = 44;

    NSString *riff = @"RIFF";
    int32_t fileSize = static_cast<int32_t>(HeaderSize + audioData.length - 8);
    NSString *wave = @"WAVE";
    NSString *fmt = @"fmt ";
    int32_t byteN = 16;// fmt チャンクのバイト数
    int16_t formatId = 1;
    int16_t channelN = ChannelN;
    int32_t sampleRate = SampleRate;
    int32_t dataSpeed = SampleRate * (BitPerSample / 2) * ChannelN;
    int16_t blockSize = (BitPerSample / 2) * ChannelN;
    int16_t bitPerSample = BitPerSample;
    NSString *data = @"data";
    int32_t waveSize = (fileSize + 8) - HeaderSize;

    NSMutableData *d = [[NSMutableData alloc] initWithCapacity:fileSize];
    [d appendData:nsStringToNSData(riff)];
    [d appendData:int32tToNSData(fileSize)];
    [d appendData:nsStringToNSData(wave)];
    [d appendData:nsStringToNSData(fmt)];
    [d appendData:int32tToNSData(byteN)];
    [d appendData:int16tToNSData(formatId)];
    [d appendData:int16tToNSData(channelN)];
    [d appendData:int32tToNSData(sampleRate)];
    [d appendData:int32tToNSData(dataSpeed)];
    [d appendData:int16tToNSData(blockSize)];
    [d appendData:int16tToNSData(bitPerSample)];
    [d appendData:nsStringToNSData(data)];
    [d appendData:int32tToNSData(waveSize)];
    [d appendData:audioData];

    return [NSData dataWithData:d];
}
@end
