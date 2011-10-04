(ns geirrod.pages
  (:require [net.cgrand.enlive-html :as enlive])
  (:use geirrod.github-api))

(defn test-page [] "Hi!")

(enlive/defsnippet issue-box "html/snippets.html" [:.issue-box] [issue]
  [:a]
  (enlive/content (str (issue-number issue)))

  [:.issue-title]
  (enlive/content (issue-title issue)))

(enlive/defsnippet lane "html/snippets.html" [:.lane] [l issues]
  [:h2]
  (enlive/content l)

  [:ol]
  (enlive/content (map issue-box issues)))

(enlive/defsnippet category-select "html/snippets.html" [:.category-select] [categories]
  [:option]
  (enlive/clone-for [category categories]
                    (enlive/do->
                     (enlive/content category)
                     (enlive/set-attr :value category))))

(enlive/deftemplate issues-page "html/issues.html" [categories]
  [:body]
  (enlive/append (category-select categories)))

;; (enlive/deftemplate issues-page "html/issues.html" [issues-by-lane lanes]
;;   [:#lanes]
;;   (enlive/content (map #(lane % (get issues-by-lane %)) lanes)))

;; (defn issues-html [account repo]
;;   (let [issues (issues account repo "open")
;;         labels (labels account repo)
;;         label-names (label-names labels)
;;         lanes (lanes "status" label-names)
;;         issues-by-lane (group-by-lane "status" issues)]
;;     (apply str (issues-page issues-by-lane lanes))))

(defn issues-html [account repo params]
  (let [labels (labels account repo)
        label-names (label-names labels)
        categories (categories label-names)]
    (issues-page categories)))