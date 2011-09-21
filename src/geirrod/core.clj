(ns geirrod.core
  (:require [compojure.core :as compojure]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.adapter.jetty :as jetty]
            [ring.util.response :as response]
            [geirrod.pages :as pages])
  (:gen-class))

(compojure/defroutes test-routes
  (compojure/GET "/test" [] (pages/test-page)))

(compojure/defroutes page-routes
  (compojure/GET "/" [] (response/resource-response "public/index.html"))
  ;; For this next route, we're using redirect-after-post to get a
  ;; 303. This is because we want the redirect to result in a GET, not
  ;; a POST.
  (compojure/POST "/" [account repo] (response/redirect-after-post (str "/issues/" account "/" repo)))
  (compojure/GET "/issues/:account/:repo" [account repo] (pages/issues-html account repo)))

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
