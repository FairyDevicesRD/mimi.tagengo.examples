(ns ai.fd.mimi.prism.translator
  (:require [clj-http.client :as http]
            [cheshire.core :as json]))

(defn translate [url token req]
  (let
   [resp (http/post url
                    {:form-params req
                     :headers {"Authorization" (str "Bearer " token)}})]
    (if (= (:status resp) 200)
      (-> resp :body
          (json/parse-string)
          (first))
      nil)))
