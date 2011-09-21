(ns geirrod.pages
  (:require [net.cgrand.enlive-html :as enlive])
  (:use geirrod.github-api))

(defn test-page [] "Hi!")

(enlive/deftemplate issues-page "html/issues.html" [lanes]
  [:ol#lanes :li]
  (enlive/clone-for [lane lanes]
                    (enlive/content lane)))

(defn issues-html [account repo]
  (let [issues (issues account repo "open")
        labels (labels account repo)
        label-names (label-names labels)
        statuses (category-values label-names "status")]
   (apply str (issues-page statuses))))

