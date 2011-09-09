(ns geirrod.core
  (:require [clj-json.core :as json]
            [clj-http.client :as http]
            [compojure.core :as compojure]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.adapter.jetty :as jetty]
            [geirrod.pages :as pages])
  (:gen-class))

(defn- issues-list-url [user repo state]
  (str "https://github.com/api/v2/json/issues/list/" user "/" repo "/" state))

(defn issues-list [user repo state]
  (json/parse-string (:body (http/get (issues-list-url user repo state)))))

(defn- to-html-str [input] (str input))

(compojure/defroutes test-routes
  (compojure/GET "/test" [] (pages/test-page)))

(compojure/defroutes page-routes
  (compojure/GET "/" [] (to-html-str (pages/index))))

(compojure/defroutes default-routes
  (route/resources "/")
  (route/not-found "Page not found"))

(compojure/defroutes all-routes
  test-routes
  (handler/site page-routes)
  default-routes)

(def app all-routes)

(defn -main [& args]
  (let [port (Integer/parseInt (get (System/getenv) "PORT" "8080"))]
    (jetty/run-jetty app {:port port})))

(let [server (atom nil)
      set-server #(reset! server %)]
  (defn debug-start []
    (future (jetty/run-jetty (var app) {:port 8080 :configurator set-server})))
  (defn debug-stop []
    (.stop @server)))
