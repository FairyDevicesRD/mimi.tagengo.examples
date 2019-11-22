//
//  main.swift
//  TRA
//
//  Created by ishihara on 2019/10/10.
//  Copyright © 2019 FD. All rights reserved.
//

import Foundation

enum CmdTraError: Error {
    case json_parse(String)
}

func callTra (access_token: String, source_lang: String, text: String, target_lang: String) {
        
    let semaphore = DispatchSemaphore(value: 0)
    let session = URLSession.shared

    var urlComponents = URLComponents(string: "https://dev-tra.mimi.fd.ai/machine_translation")!
    urlComponents.queryItems = [
        URLQueryItem(name: "text", value: text.precomposedStringWithCanonicalMapping),
        URLQueryItem(name: "source_lang", value: source_lang),
        URLQueryItem(name: "target_lang", value: target_lang),
    ]
    var http_request = URLRequest(url: urlComponents.url!)
    http_request.httpMethod = "GET"
    http_request.setValue("Bearer " + access_token, forHTTPHeaderField: "Authorization")
    let task = session.dataTask(with: http_request) {
        data, response, error in
        print("start")

        if let error = error {
            print("client error : \(error.localizedDescription)")
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
                let decoder = JSONDecoder()
                guard let response_json = try decoder.decode([String]?.self, from: data) else {
                    throw CmdTraError.json_parse("json convert failed")
                }
                print(response_json)
            } catch {
                print("parse error")
                print(error.self)
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

    if arguments.count != 5 {
        print("Usage: access_token source_lang input_text target_lang")
        exit(1)
    }

    let access_token: String! = arguments[1]
    let source_lang: String! = arguments[2]
    let text: String! = arguments[3]
    let target_lang: String! = arguments[4]
    
    var token :String!

    token = getFileData(access_token)
    callTra(access_token: token, source_lang: source_lang, text: text, target_lang: target_lang)

}

run()
