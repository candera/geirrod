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

(enlive/defsnippet nonexistent-category-error "html/snippets.html" [:#nonexistent-category-error]
  [category]
  [:span]
  (enlive/content category))

(enlive/defsnippet grid "html/snippets.html" [:#issue-grid]
  [lanes issues-by-lane]
  (enlive/content (map #(lane % (get issues-by-lane %)) lanes)))

(enlive/defsnippet flat "html/snippets.html" [:#issue-list]
  [issues]
  [:tr.issue-row]
  (enlive/clone-for [issue issues]
                    [[:td (enlive/nth-of-type 1)]]
                    (enlive/content (str (issue-number issue)))

                    [[:td (enlive/nth-of-type 2)]]
                    (enlive/content (issue-title issue))))


(enlive/deftemplate issues-page "html/issues.html" [account repo content]
  [:h1 :a]
  (enlive/do->
   (enlive/content (str account "/" repo))
   (enlive/set-attr :href (repo-link account repo)))

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
          lanes (lanes category label-names)]
      (if (empty? lanes)
        (nonexistent-category-error category)
        (grid lanes (group-by-lane category issues))))

    :else
    (concat
     (category-select (categories label-names))
     (flat (issues account repo "open"))))))

(defn issues-html [account repo params]
  (let [{:keys [display category]} params]
    (apply str (issues-page account repo
                            (issues-content account repo display category)))))