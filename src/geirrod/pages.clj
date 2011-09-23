(ns geirrod.pages
  (:require [net.cgrand.enlive-html :as enlive])
  (:use geirrod.github-api))

(defn test-page [] "Hi!")

(enlive/defsnippet issue-box "html/snippets.html" [:#issue-box] [issue]
  identity)

(enlive/deftemplate issues-page "html/issues.html" [lanes]
  [:ol#lanes :li]
  (enlive/clone-for [[title issues] lanes]
                    (enlive/content title)))

(defn issues-html [account repo]
  "TODO: Add issues")

(comment (defn issues-html [account repo]
    (let [issues (issues account repo "open")
          labels (labels account repo)
          label-names (label-names labels)
          statuses (category-val "status" label-names)
          lanes (category-group statuses issues)]
      (apply str (issues-page statuses)))))

