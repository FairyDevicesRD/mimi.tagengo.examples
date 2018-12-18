(ns utils)

(defn read-file [filename]
  (let [f   (java.io.File. filename)
        ary (byte-array (.length f))
        is  (java.io.FileInputStream. f)]
    (.read is ary)
    (.close is)
    ary))

(defn write-file [outfile inputstream]
  (with-open [os (clojure.java.io/output-stream outfile)]
    (loop []
      (let [b (.read inputstream)]
        (when (>= b 0)
          (.write os b)
          (recur))))))

; for debug
(defn p1 [s1 s2] (do (println s1)    s2))
(defn p2 [s1 s2] (do (println s1 s2) s2))
