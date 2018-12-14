#import "ResponseData.h"
#import <Foundation/Foundation.h>

@interface ClientComCtrl : NSObject

- (id _Nonnull)initWithAccessToken:(NSString *_Nonnull)accessToken;

- (BOOL)isTransferEncodingChunked;

- (void)setTransferEncodingChunked:(BOOL)chunked;


/**
 * SR一括送信 (非同期)
 * @param url ホストURL (使用しません)
 * @param xmlData リクエストXML
 * @param binaryData 音声データ
 * @param completionHandler 結果取得コールバック
*/
- (void)  request:(NSString *_Nonnull)url
          xmlData:(NSString *_Nonnull)xmlData
       binaryData:(NSData *_Nonnull)binaryData
completionHandler:(void (^ _Nonnull)(ResponseData *_Nullable resData, NSError *_Nullable error))completionHandler;

/**
 * SR分割送信開始 / MT / SS (非同期)
 * @param url ホストURL (使用しません)
 * @param xmlData リクエストXML
 * @param completionHandler 結果取得コールバック
 */
- (void)  request:(NSString *_Nonnull)url
          xmlData:(NSString *_Nonnull)xmlData
completionHandler:(void (^ _Nonnull)(ResponseData *_Nullable resData, NSError *_Nullable error))completionHandler;

/**
 * SR分割送信データ追加 (非同期)
 * @param url ホストURL (使用しません)
 * @param binaryData 音声データ
 * @param completionHandler 結果取得コールバック
 */
- (void)  request:(NSString *_Nonnull)url
       binaryData:(NSData *_Nonnull)binaryData
completionHandler:(void (^ _Nonnull)(ResponseData *_Nullable resData, NSError *_Nullable error))completionHandler;

/**
 * SR分割送信終了 (非同期)
 * @param url ホストURL (使用しません)
 * @param completionHandler 結果取得コールバック
 */
- (void)  request:(NSString *_Nonnull)url
completionHandler:(void (^ _Nonnull)(ResponseData *_Nullable resData, NSError *_Nullable error))completionHandler;


/**
 * SR一括送信 (同期)
 * @param url ホストURL (使用しません)
 * @param xmlData リクエストXML
 * @param binaryData 音声データ
 * @param resData 結果
 */
- (int)request:(NSString *_Nonnull)url
        xmlData:(NSString *_Nonnull)xmlData
     binaryData:(NSData *_Nonnull)binaryData
        resData:(ResponseData *_Nullable *_Nonnull)resData;

/**
 * SR分割送信開始 / MT / SS (同期)
 * @param url ホストURL (使用しません)
 * @param xmlData リクエストXML
 * @param resData 結果
 */

- (int)request:(NSString *_Nonnull)url
        xmlData:(NSString *_Nonnull)xmlData
        resData:(ResponseData *_Nullable *_Nonnull)resData;

/**
 * SR分割送信データ追加 (同期)
 * @param url ホストURL (使用しません)
 * @param binaryData 音声データ
 * @param resData 結果
 */
- (int)request:(NSString *_Nonnull)url
     binaryData:(NSData *_Nonnull)binaryData
        resData:(ResponseData *_Nullable *_Nonnull)resData;

/**
 * SR分割送信終了 (同期)
 * @param url ホストURL (使用しません)
 * @param resData 音声データ
 */
- (int)request:(NSString *_Nonnull)url
        resData:(ResponseData *_Nullable *_Nonnull)resData;

@end
