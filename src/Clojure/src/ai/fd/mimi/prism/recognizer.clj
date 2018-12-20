(ns ai.fd.mimi.prism.recognizer
  (:require [clj-http.client :as http]
            [cheshire.core :as json]))

(defn recognize [url token lang reqdata]
  (let
   [resp (http/post url
                    {:body reqdata
                     :headers {"Authorization" (str "Bearer " token)
                               "x-mimi-input-language" lang
                               "x-mimi-process"        "nict-asr"
                               "Content-Type"          "audio/x-pcm;bit=16;rate=16000;channels=1"}})]
    (if (= (:status resp) 200)
      (-> resp :body
          (json/parse-string)
          (get "response")
          (as-> r
                (map #(-> (get % "result")
                          (clojure.string/split #"\|")
                          (first)) r))
          (clojure.string/join))
      nil)))
