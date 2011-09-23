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
(use :reload-all 'geirrod.github-api)

(issues "candera" "artifact" "open")
(labels (issues "candera" "artifact" "open"))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(use 'net.cgrand.enlive-html)

(deftemplate t1 "html/issues.html" [lanes]
  [:ol#lanes :li]
  (clone-for [lane lanes]
             (content lane)))

(apply str (t1 ["open" "in dev" "ready for qa"]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(use :reload-all 'geirrod.pages)
(use 'geirrod.github-api)

(def i (issues "candera" "geirrod" "open"))

(category-values (label-names (labels i)) "status")

(issues-html "candera" "artifact")

(require '[net.cgrand.enlive-html :as enlive])
(enlive/defsnippet b "html/snippets.html" [:#issue-box] []
  [:div]
  enlive/unwrap)

(b)

i

