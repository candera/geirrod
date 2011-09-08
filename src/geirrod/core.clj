(ns geirrod.core
  (:require [clj-json.core :as json]
            [clj-http.client :as http]))

(defn- issues-list-url [user repo state]
  (str "https://github.com/api/v2/json/issues/list/" user "/" repo "/" state))

(defn issues-list [user repo state]
  (json/parse-string (:body (http/get (issues-list-url user repo state)))))
