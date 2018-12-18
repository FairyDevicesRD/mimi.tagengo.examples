(ns ai.fd.mimi.prism.auth
    (:require [clj-http.client :as http]
              [cheshire.core :as json]))

(def scope "https://apis.mimi.fd.ai/auth/nict-tra/http-api-service;https://apis.mimi.fd.ai/auth/nict-tts/http-api-service;https://apis.mimi.fd.ai/auth/nict-tra/http-api-service;https://apis.mimi.fd.ai/auth/nict-asr/http-api-service;https://apis.mimi.fd.ai/auth/nict-asr/websocket-api-service;https://apis.mimi.fd.ai/auth/applications.r")

(def grant_type "https://auth.mimi.fd.ai/grant_type/application_credentials")

(defn get-token [appId appSecret]
  (let
    [resp (http/post "https://auth.mimi.fd.ai/v2/token"
                     {:form-params
                      {:client_id appId
                       :client_secret appSecret
                       :scope scope
                       :grant_type grant_type}})]
    (if (= (:status resp) 200)
      (-> resp :body
        (json/parse-string)
        (get "accessToken"))
      nil)))


