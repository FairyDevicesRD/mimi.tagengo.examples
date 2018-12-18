(ns ai.fd.mimi.prism.synthesizer
  (:require [clj-http.client :as http]
            [cheshire.core :as json]))

(defn synthesize [url token req]
  (let
   [resp (http/post url
                    {:form-params req
                     :headers {"Authorization" (str "Bearer " token)}
                     :as :stream})]
    (if (= (:status resp) 200)
      (-> resp :body)
      nil)))
