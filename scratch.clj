(require '[clj-http.client :as http])

(http/get "http://google.com")
(http/get "https://github.com")         ; This fails under slime, but
                                        ; works at the REPL. WTF?

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(import [java.net URL URLConnection])
(import [java.io BufferedReader InputStreamReader])
(let [conn (.openConnection (URL. "http://wangdera.com"))
      in (BufferedReader. (InputStreamReader. (.getInputStream conn)))]
  (dotimes [i 100]
    (let [line (.readLine in)]
      (if (zero? (mod i 2))
        (println i line)))))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(require '[clj-http.client :as http])
(require '[clj-json.core :as json])

(json/parse-string
 (:body
  (http/get "https://github.com/api/v2/json/issues/list/candera/artifact/open")))