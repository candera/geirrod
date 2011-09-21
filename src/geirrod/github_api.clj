(ns geirrod.github-api
  (:require [clj-json.core :as json]
            [clj-http.client :as http]))

;;; URLs

(def ^:private ^{:documentation "The base GitHub API URL"}
  base-url
  "https://api.github.com/")

(defn- repo-url
  "Returns the GitHub URL that's the basis for operations against a
  particular user's repo."
  [user repo]
  (str base-url "repos/" user "/" repo))

(defn- issues-url
  "Returns the GitHub URL for retrieving issues in a particular user's
  repo that have state 'state', which is either \"open\" or
  \"closed\""
  [user repo state]
  (str  (repo-url user repo) "/issues?state=" state))

(defn- labels-url
  "Returns the GitHub URL for retrieving labels in a particular user's
  repo."
  [user repo]
  (str  (repo-url user repo) "/labels"))

;;; API wrappers

(defn issues
  "Returns the issues in a user's repository in the specified
  state (either \"open\" or \"closed\") as a seq of maps."
  [user repo state]
  (json/parse-string (:body (http/get (issues-url user repo state)))))

(defn labels
  "Returns the labels in a user's repository as a seq of maps."
  [user repo]
  (json/parse-string (:body (http/get (labels-url user repo)))))

;;; Helpers

(defn issue-labels
  "Given a seq of issue maps, return a seq of the unique labels, each
  of which is a map."
  [issues]
  (reduce (fn [labels issue]
            (into labels (get issue "labels")))
          #{}
          issues))

(defn label-names
  "Given a seq of labels, return a seq of their names."
  [labels]
  (map #(get % "name") labels))

(defn category-values
  "Given a seq of label names, return a seq of the values for a
  specified category. The category is the first word of a label with
  spaces in it. The value is the rest of the label."
  [label-names category]
  (let [category-prefix (str category " ")]
    (->> label-names
         (filter #(.startsWith % category-prefix))
         (map #(subs % (count category-prefix))))))
