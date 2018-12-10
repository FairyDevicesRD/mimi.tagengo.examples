# iOS 版サンプルアプリ　ビルド・実行手順

## ビルド・実行手順

1. https://github.com/FairyDevicesRD/mimi.tagengo.examples/releases から、 libmimiio-ios9-arm64-libc++.tar.gz をダウンロードし、 `mimi.tagengo.examples/src/iOS/TagengoExampleApp/Frameworks/libmimiio` 以下に `include` ディレクトリと `lib` ディレクトリを展開します。
2. ビルド用のMacにデバッグ用のiOS端末（実機）を接続します。
3. `mimi.tagengo.examples/src/iOS/TagengoExampleApp/TagengoExampleApp.xcworkspace` を Xcode で開きます。
    ( xcodeproj ファイルを開かないようにしてください。 )
4. `ViewController.mm` ファイルを開き、下記変数を予め配布された`アプリケーションID` 、 `アプリケーションシークレット` で置き換えます。
    ```
    NSString *const ID = @"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    NSString *const SECRET = @"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    ```
5. "Product" -> "Run" で実機にて実行します。
