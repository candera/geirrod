(ns geirrod.github-api
  (:use geirrod.util)
  (:require [clj-json.core :as json]
            [clj-http.client :as http]))

;;; Terminology
;;;
;;; Category - e.g. "status", "estimate". The first part of a label
;;; with spaces in it. These are the axes that we can use to divide
;;; issues into groups.
;;;
;;; Lane - e.g. "open", "ready for dev". The second part of a label
;;; with spaces in it. These are the headings for groups of issues.

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

(defn issue-title
  "Given an issue, return its title."
  [issue]
  (get issue "title"))

(defn issue-number
  "Given an issue, return its number."
  [issue]
  (get issue "number"))

(defn label-names
  "Given a seq of labels, return a seq of their names."
  [labels]
  (map #(get % "name") labels))

(defn lanes
  "Given a seq of label names, return a seq of lanes for a
  specified category. The category is the first word of a label with
  spaces in it. The lane is the rest of the label."
  [category label-names]
  (let [prefix (str category " ")
        prefix-len (count prefix)]
    (->> label-names
         (filter #(.startsWith % prefix))
         (map #(subs % prefix-len)))))

(defn lanes-for-issue
  "Given a category and an issue, return a seq of lanes that issue
  belongs in."
  [category issue]
  (->> (get issue "labels")
       (label-names)
       (lanes category)))

(defn group-by-lane
  "Given a category (e.g. 'status') and a seq of issues, return a map
  of lanes to seqs of issues in those lanes. Note that an issue can
  appear in more than one lane if it has more than one label for a
  given category."
  [category issues]
  (apply merge-to-sets
         (map #(zipmap (lanes-for-issue category %) (repeat %)) issues)))

