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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(use 'net.cgrand.enlive-html)

(enlive/defsnippet issue-box "html/snippets.html" [:.issue-box] [issue]
  [:a]
  (content (get issue "number"))

  [:.issue-title]
  (content (get issue "name")))

(enlive/defsnippet lane "html/snippets.html" [:.lane] [l issues]
  [:h2]
  (enlive/content l)

  [:ol]
  (content (map issue-box issues)))

(use 'geirrod.github-api)

(def i (issues "candera" "geirrod" "open"))

(clojure.pprint/pprint (first i))

(get (first i) "number")
(get (first i) "title")
(get (first i) "html_url")

(apply str (emit* (at (html-resource "html/issues.html")

            [:ol#lanes]
            (content (map #(lane % (get issues-by-lane %)) ["a" "b" "c"])))))

(lane "foo")


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(let [account "candera"
      repo "geirrod"
      issues (issues account repo "open")
      labels (labels account repo)
      label-names (label-names labels)
      lanes (lanes "status" label-names)]
  (group-by-lane "status" issues))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(use 'compojure.core)
(use 'clojure.pprint)
(require '[compojure.handler :as handler])
(require '[ring.middleware.params :as params])
(def mr (params/wrap-params
         (GET "/issues/:user/:repo"
              [user repo & params]
              (with-out-str (pprint [user repo params])))))

(print
 (:body
  (mr {:uri "/issues/candera/geirrod"
       :query-string "this=that&this=the-other-thing"
       :request-method :get})))
