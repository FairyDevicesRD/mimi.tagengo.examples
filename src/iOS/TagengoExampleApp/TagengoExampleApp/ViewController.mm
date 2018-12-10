#import "ViewController.h"
#import "AccessToken.h"
#import "ClientComCtrl.h"
#import "Player.h"
#import <KissXML/DDXML.h>

@interface ViewController () {
    Recorder *_recorder;
    ClientComCtrl *_client;
    BOOL isRecording;
    NS_ENUM(NSInteger, ParseType) {
        SR,
        MT
    };
}
@property(weak, nonatomic) IBOutlet UITextView *recognizedText;
@property(weak, nonatomic) IBOutlet UITextView *transratedText;
@property(weak, nonatomic) IBOutlet UIButton *srButton;

- (IBAction)mtTouchUp:(UIButton *)sender;

- (IBAction)srTouchUp:(UIButton *)sender;

- (IBAction)ssTouchUp:(UIButton *)sender;

- (NSString *)simpleParseXML:(NSString *)xml type:(ParseType)type error:(NSError **)error;
@end

NSString *_accessToken;
dispatch_queue_main_t _main_queue;

@implementation ViewController {

}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.

    // アクセストークンの取得
    _main_queue = dispatch_get_main_queue();
    
    NSString *const ID = @"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    NSString *const SECRET = @"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    AccessToken *accessToken = [[AccessToken alloc] initWithProperty
            :ID
            :SECRET];
    dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);
    [accessToken getToken:^(NSString *_Nullable token, NSError *_Nullable error) {
        if (error) {
            NSLog(@"getAccessToken failed. %@", error);
        } else {
            _accessToken = token;
            NSLog(@"アクセストークンを取得しました: %@", token);
        }
        dispatch_semaphore_signal(semaphore); //semaphore_waitを抜ける
    }];
    dispatch_semaphore_wait(semaphore, DISPATCH_TIME_FOREVER);
}

- (IBAction)mtTouchUp:(UIButton *)sender {
    NSLog(@"tap MT button");
    NSString *MTRequestTemplate = @"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                                  "<STML UtteranceID=\"0\" Version=\"1.0\">\n"
                                  "<User ID=\"N/A\"/>\n"
                                  "<MT_IN SourceLanguage=\"%1$@\" TargetLanguage=\"%2$@\">\n"
                                  "<InputTextFormat Form=\"SurfaceForm\"/>\n"
                                  "<OutputTextFormat Form=\"SurfaceForm\"/>\n"
                                  "<s>%3$@</s>\n"
                                  "</MT_IN>\n"
                                  "</STML>\n";
    _client = [[ClientComCtrl alloc] initWithAccessToken:_accessToken];
    NSString *xml = [NSString stringWithFormat:MTRequestTemplate, @"ja", @"en", self->_recognizedText.text];
    [_client request:@"dummyURL" xmlData:xml completionHandler:^(ResponseData *resData, NSError *error) {
        if (error) {
            NSLog(@"MT error: %@", error);
            return;
        }
        // メインスレッドでUIを更新
        dispatch_async(_main_queue, ^{
            NSError *parseError = nil;
            [self->_transratedText setText:[self simpleParseXML:[resData XML] type:MT error:&parseError]];
        });
    }];
}

- (IBAction)srTouchUp:(UIButton *)sender {
    NSLog(@"tap SR button");
    if (isRecording) {
        [_srButton setTitle:@"SR" forState:UIControlStateNormal];
        isRecording = false;
        // 認識を停止する
        [_recorder stopRecording];

        NSLog(@"分割送信終了");
        [_client request:@"dummyURL" completionHandler:^(ResponseData *resData, NSError *error) {
            if (error) {
                NSLog(@"分割送信終了 error: %@", error);
            }
            // メインスレッドでUIを更新
            dispatch_async(_main_queue, ^{
                NSError *parseError = nil;
                [self->_recognizedText setText:[self simpleParseXML:[resData XML] type:SR error:&parseError]];
            });
        }];
        _recorder = NULL;
    } else {
        [_srButton setTitle:@"SR(もう一度タップして録音終了)" forState:UIControlStateNormal];
        isRecording = true;
        // 認識を開始する
        _client = [[ClientComCtrl alloc] initWithAccessToken:_accessToken];
        [_client setTransferEncodingChunked:true];
        _recorder = [[Recorder alloc] init];

        // リクエストの作成
        NSString *SRRequestTemplate = @"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                                      "<STML UtteranceID=\"0\" Version=\"1\">\n"
                                      "<User ID=\"N/A\"/>\n"
                                      "<SR_IN Language=\"%1$@\">\n"
                                      "<Voice/>\n"
                                      "<InputAudioFormat Audio=\"RAW\" Endian=\"Little\" SamplingFrequency=\"16k\"/>\n"
                                      "<OutputTextFormat Form=\"SurfaceForm\"/>\n"
                                      "</SR_IN>\n"
                                      "</STML>";
        NSString *xml = [NSString stringWithFormat:SRRequestTemplate, @"ja"];

        NSLog(@"分割送信開始 xml : %@", xml);
        [_client request:@"dummyURL" xmlData:xml completionHandler:^(ResponseData *resData, NSError *error) {
            if (error) {
                NSLog(@"分割送信開始 error: %@", error);
                dispatch_async(_main_queue, ^{
                    [self->_srButton setTitle:@"SR" forState:UIControlStateNormal];
                    self->isRecording = false;
                });
            }
        }];
        // 録音開始
        [_recorder startRecording:^(NSError *error, NSData *_Nonnull buffer) {
            [self->_client request:@"dummyURL" binaryData:buffer completionHandler:^(ResponseData *resData, NSError *error) {
                if (error) {
                    NSLog(@"分割送信 音声追加中 error: %@", error);
                    [self->_recorder stopRecording];
                    dispatch_async(_main_queue, ^{
                        [self->_srButton setTitle:@"SR" forState:UIControlStateNormal];
                        self->isRecording = false;
                    });
                }
            }];
        }];
    }
}

- (IBAction)ssTouchUp:(UIButton *)sender {
    NSLog(@"tap SS button");
    NSString *SSRequestTemplate = @"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                                  "<STML UtteranceID=\"0\" Version=\"1\">\n"
                                  "<User ID=\"N/A\"/>\n"
                                  "<SS_IN Language=\"%1$@\">\n"
                                  "<Voice Age=\"30\" Gender=\"%2$@\"/>\n"
                                  "<OutputAudioFormat Audio=\"RAW\" Endian=\"Little\" SamplingFrequency=\"16k\"/>\n"
                                  "<InputTextFormat Form=\"SurfaceForm\"/>\n"
                                  "<s Delimiter=\" \">%3$@</s>\n"
                                  "</SS_IN>\n"
                                  "</STML>";
    NSString *xml = [NSString stringWithFormat:SSRequestTemplate, @"en", @"Female", self->_transratedText.text];
    _client = [[ClientComCtrl alloc] initWithAccessToken:_accessToken];
    [_client request:@"dummyURL" xmlData:xml completionHandler:^(ResponseData *resData, NSError *error) {
        if (error) {
            NSLog(@"SS error: %@", error);
            return;
        }
        NSError *err = nil;
        [[Player sharedInstance] play:[Player appendWavHeader:resData.binary] error:&err];
    }];
}

- (NSString *)simpleParseXML:(NSString *)xml type:(ParseType)type error:(NSError **)error {
    NSLog(@"parse xml : %@", xml);
    NSData *dataXml = [xml dataUsingEncoding:NSUTF8StringEncoding];
    DDXMLDocument *doc = [[DDXMLDocument alloc] initWithData:dataXml options:0 error:error];
    NSArray *nodes = nil;
    switch (type) {
        case SR:
            nodes = [doc nodesForXPath:@"/STML/SR_OUT/NBest[@Order='1']/s/text()" error:error];
            break;
        case MT:
            nodes = [doc nodesForXPath:@"/STML/MT_OUT/NBest[@Order='1']/s/text()" error:error];
            break;
    }
    if ([nodes count] > 0) {
        return [[nodes objectAtIndex:0] stringValue];
    } else {
        return @"";
    }
}

@end
