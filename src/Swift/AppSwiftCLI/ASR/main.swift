//
//  main.swift
//  ASR
//
//  Created by ishihara on 2019/10/10.
//  Copyright © 2019 FD. All rights reserved.
//

import Foundation
import Starscream

let SAMPLES_PER_CHUNK: Int = 1024

class WsClient: WebSocketAdvancedDelegate {
    let socket: WebSocket
    var access_token: String
    var audio_file: String
    var done: Bool

    init(access_token: String, audio_file: String) {
        self.access_token = access_token
        self.audio_file = audio_file
        self.done = false
        var urlRequest = URLRequest(url: URL(string: "wss://dev-service.mimi.fd.ai")!)

        urlRequest.httpMethod = "GET"
        urlRequest.setValue("Bearer " + access_token, forHTTPHeaderField: "Authorization")
        urlRequest.setValue("asr", forHTTPHeaderField: "x-mimi-process")
        urlRequest.setValue("ja", forHTTPHeaderField: "x-mimi-input-language")
        urlRequest.setValue("audio/x-pcm;bit=16;rate=16000;channels=1", forHTTPHeaderField: "Content-Type")

        socket = WebSocket(request: urlRequest)
        socket.advancedDelegate = self

    }

    func start() {
        print("Connecting")
        socket.connect()
    }

    func websocketDidConnect(socket: WebSocket) {
        print("Connected")
        if let stream: InputStream = InputStream(fileAtPath: self.audio_file) {
            var buf: [UInt8] = [UInt8](repeating: 0, count: SAMPLES_PER_CHUNK * 2)
            stream.open()

            while true {
                let len = stream.read(&buf, maxLength: buf.count)
                socket.write(data: Data(buf)) { }
                if len < buf.count {
                    do {
                        let jsonData = try JSONSerialization.data(withJSONObject: ["command": "recog-break"], options: [])
                        socket.write(string: String(bytes: jsonData, encoding: .utf8)!)
                        break
                    } catch {
                        print("json serialize or send failed")
                        return
                    }
                }
            }
            stream.close()
        }

    }

    func websocketDidReceiveMessage(socket: WebSocket, text: String, response: WebSocket.WSResponse) {
        print(text)
        do {
            let response_json: Dictionary<String, Any> = try JSONSerialization.jsonObject(
                with: text.data(using: .utf8)!,
                options: JSONSerialization.ReadingOptions.allowFragments
            ) as! Dictionary<String, Any>
            guard let response_status: String = response_json["status"] as? String else {
                print("json key doesn't exist")
                self.done = true
                return
            }
            if response_status == "recog-finished" {
                print("recog-finished: received all from server.")
                socket.disconnect()
                self.done = true
            }
        } catch {
            print("json serialize failed")
            self.done = true
            return
        }
    }

    func websocketDidReceiveData(socket: WebSocket, data: Data, response: WebSocket.WSResponse) {
        print("received data \(data)")
    }

    func websocketHttpUpgrade(socket: WebSocket, request: String) {
//        print("request : \(request)")
    }

    func websocketHttpUpgrade(socket: WebSocket, response: String) {
//        print("response : \(response)")
    }

    func websocketDidDisconnect(socket: WebSocket, error: Error?) {
        print("Disconnect")
        self.done = true
        if let wserror = error as? WSError {
            if wserror.code != CloseCode.normal.rawValue {
                print("error: \(wserror.localizedDescription)")
            }
        }
    }

}

/**
 ファイルデータを読み込みます。
 
 Parameter filePath: ファイルパス
 Returns: ファイルデータ　読み込めない場合はnilを返却します。
 */
func getFileData(_ filePath: String) -> String? {
    let fileData: String?
    let fileUrl = URL(fileURLWithPath: filePath)
    let fileread_result = Result { try String(contentsOf: fileUrl) }
    switch(fileread_result) {
    case let .success(value):
        let value_lines: [String] = value.components(separatedBy: .newlines)
        print("first line : \(value_lines[0])")
        fileData = (value_lines[0])
    case let .failure(error):
        print(error)
        fileData = nil
    }
    return fileData
}

func run() {

    let arguments: [String] = CommandLine.arguments

    if arguments.count != 3 {
        print("Usage: access_token audio_file")
        exit(1)
    }

    let access_token: String = arguments[1]
    let audio_file: String = arguments[2]
    var token: String

    token = getFileData(access_token)!

    let client = WsClient(access_token: token, audio_file: audio_file)
    client.start()

    while(true) {
        RunLoop.current.run(until: Date())
        if client.done {
            break
        }
    }
}

run()
