//
//  main.swift
//  GetAccessToken
//
//  Created by ishihara on 2019/10/10.
//  Copyright © 2019 FD. All rights reserved.
//


import Foundation

enum GetTokenError: Error {
    case json_parse(String)
    case response_data(String)
}

func getAccessToken (grant_type: String, client_id: String, client_secret: String, scope: String) {

    let semaphore = DispatchSemaphore(value: 0)
    let session = URLSession.shared

    var urlComponents = URLComponents(string: "https://auth.mimi.fd.ai/v2/token")!
    urlComponents.queryItems = [
        URLQueryItem(name: "grant_type", value: grant_type),
        URLQueryItem(name: "client_id", value: client_id),
        URLQueryItem(name: "client_secret", value: client_secret),
        URLQueryItem(name: "scope", value: scope),
    ]
    var http_request = URLRequest(url: urlComponents.url!)
    http_request.httpMethod = "POST"

    let task = session.dataTask(with: http_request) {
        data, response, error in
        print("start")
        if let error = error {
            print("client error: \(error.localizedDescription) ")
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
                guard let response_json = try JSONSerialization.jsonObject(with: data, options: JSONSerialization.ReadingOptions.allowFragments) as? Dictionary<String, Any> else {
                    print("json convert failed")
                    throw GetTokenError.json_parse("json convert failed")
                }
                guard let access_token: String = response_json["accessToken"] as? String else {
                    print("json key does not exist")
                    throw GetTokenError.response_data("json key does not exist")
                }
                print("got access_token: \(access_token)")
                /* ファイル出力 */
                try access_token.write(toFile: "access_token.txt", atomically: true, encoding: String.Encoding.utf8)
            } catch {
                print("Error")
                print(error.self)
                print(error.localizedDescription)
                semaphore.signal()
                return
            }

        } else {
            print("server error .. status code: \(response.statusCode)\n")
            let responseData = String(data: data, encoding: String.Encoding.utf8)
            semaphore.signal()
            print(responseData ?? "no response data")
        }
        semaphore.signal()
    }
    task.resume()
    semaphore.wait()
}

func run() {
    let arguments: [String] = CommandLine.arguments

    if arguments.count != 5 {
        print("Usage: grant_type client_id client_secret scope")
        exit(1)
    }

    let grant_type: String! = arguments[1]
    let client_id: String! = arguments[2]
    let client_secret: String! = arguments[3]
    let scope: String! = arguments[4]

    getAccessToken(
        grant_type: grant_type,
        client_id: client_id,
        client_secret: client_secret,
        scope: scope
    )

}

run()
