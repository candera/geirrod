(require '[clj-http.client :as http])

(http/get "http://google.com")
(http/get "https://github.com")         ; This fails under slime, but
                                        ; works at the REPL. WTF?