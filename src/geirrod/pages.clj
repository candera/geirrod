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

(enlive/defsnippet category-select "html/snippets.html" [:#category-select] [categories]
  [:option]
  (enlive/clone-for [category categories]
                    (enlive/do->
                     (enlive/content category)
                     (enlive/set-attr :value category))))

(enlive/defsnippet grid "html/snippets.html" [:#issue-grid] [lanes issues-by-lane]
  (enlive/content (map #(lane % (get issues-by-lane %)) lanes)))

(enlive/deftemplate issues-page "html/issues.html" [content]
  [:body]
  (enlive/append content))

;; TODO: Consider something more flexible than a cond as this gets
;; more complicated
(defn- issues-content [account repo display category]
  (let [labels (labels account repo)
        label-names (label-names labels)]
   (cond
    (= display "grid")
    (let [issues (issues account repo "open")
          lanes (lanes category label-names)
          issues-by-lane (group-by-lane category issues)]
      (grid lanes issues-by-lane))

    :else
    (category-select (categories label-names)))))

(defn issues-html [account repo params]
  (let [{display :display category :group} params]
    (apply str (issues-page (issues-content account repo display category)))))