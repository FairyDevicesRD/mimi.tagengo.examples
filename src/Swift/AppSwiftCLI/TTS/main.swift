//
//  main.swift
//  TTS
//
//  Created by ishihara on 2019/10/10.
//  Copyright © 2019 FD. All rights reserved.
//

import Foundation

func callTts (access_token: String, input_lang: String, text: String, filename: String) {
    
    let semaphore = DispatchSemaphore(value: 0)
    let session = URLSession.shared

    var urlComponents = URLComponents(string: "https://sandbox-ss.mimi.fd.ai/speech_synthesis")!
    urlComponents.queryItems = [
        URLQueryItem(name: "text", value: text.precomposedStringWithCanonicalMapping),
        URLQueryItem(name: "lang", value: input_lang),
        URLQueryItem(name: "engine", value: "nict"),
    ]
    var http_request = URLRequest(url: urlComponents.url!)
    http_request.httpMethod = "POST"
    http_request.setValue("Bearer " + access_token, forHTTPHeaderField: "Authorization")
    http_request.httpBody = urlComponents.percentEncodedQuery?.data(using: .utf8)
    let task = session.dataTask(with: http_request) {
        data, response, error in

        if let error = error {
            print("client error: \(error.localizedDescription)")
            print(error.localizedDescription)
            semaphore.signal()
            return
        }

        guard let data = data, let response = response as? HTTPURLResponse else {
            print("no data or no response")
            semaphore.signal()
            return
        }

        if response.statusCode == 200 {
            print("success")
            do {
                /* ファイル出力 */
                try data.write(to: URL(fileURLWithPath: filename), options: .atomic)
            } catch {
                print("output error")
                print(error.localizedDescription)
                semaphore.signal()
                return
            }
        } else {
            print("server error .. status code: \(response.statusCode)")
            print(String(data: data, encoding: String.Encoding.utf8) ?? "output error")
            semaphore.signal()
            return
        }
        semaphore.signal()
    }
    task.resume()
    semaphore.wait()
}

/**
 ファイルデータを読み込みます。
 
 Parameter filePath: ファイルパス
 Returns: ファイルデータ　読み込めない場合はnilを返却します。
 */
func getFileData(filePath: String) -> String? {
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

    if arguments.count != 5 {
        print("Usage: access_token input_lang text filename")
        exit(1)
    }

    let access_token: String! = arguments[1]
    let input_lang: String! = arguments[2]
    let text: String! = arguments[3]
    let filename: String! = arguments[4]
    
    var token :String!

    token = getFileData(filePath: access_token)
    callTts(access_token: token, input_lang: input_lang, text: text, filename: filename)

}

run()
