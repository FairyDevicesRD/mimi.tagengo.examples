(ns tagengoex
  (:require [ai.fd.mimi.prism.auth :as auth]
            [ai.fd.mimi.prism.recognizer :as sr]
            [ai.fd.mimi.prism.translator :as mt]
            [ai.fd.mimi.prism.synthesizer :as ss]
            [utils :refer [read-file write-file p1 p2]]
            [cheshire.core :as json]))

(def sr-url "https://sandbox-sr.mimi.fd.ai/")
(def mt-url "https://sandbox-mt.mimi.fd.ai/machine_translation")
(def ss-url "https://sandbox-ss.mimi.fd.ai/speech_synthesis")

(def config
  (-> (slurp "config.json")
      (json/parse-string)))

(defn -main []
  (let [appId     (get config "applicationId")
        appSecret (get config "applicationSecret")
        token (do (println "getting accessToken...")
                  (auth/get-token appId appSecret))
        srclang "ja"
        dstlang "en"
        infile  "audio.wav"
        outfile "output.wav"]
    (println "trying to recognize text from audio data" infile "...")
    (some->> (read-file infile)
             (sr/recognize sr-url token)
             (p2 "->")
             (p1 (str "trying to translate it from " srclang " to " dstlang))
             (assoc {:source_lang srclang :target_lang dstlang} :text)
             (mt/translate mt-url token)
             (p2 "->")
             (assoc {:engine "nict" :lang dstlang} :text)
             (p1 "trying to synthesize...")
             (ss/synthesize ss-url token)
             (p1 (str "synthesize succeeded. wrote data to " outfile))
             (write-file outfile))))
