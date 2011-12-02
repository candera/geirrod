(ns geirrod.form-reader
  (:import [java.io PushbackReader StringReader]))

(defn make-counting-pushback-reader
  "Given a string, returns a vector containing an atom and an instance
  of PushbackReader. The returned PushbackReader will keep track of
  its current position in the atom."
  [s]
  (let [pos (atom 0)]
    [pos
     (proxy [PushbackReader] [(StringReader. s)]
        (read
          ([]
             (println "read [] " pos)
             (let [result (proxy-super read)]
               (when (not= result -1) (swap! pos inc))
               result))
          ([cbuf off len]
             (println "read [cbuf off len] " cbuf " " off " " len " " pos)
             (let [result (proxy-super read cbuf off len)]
               (when (not= result -1) (swap! pos + result))
               result)))
        (skip [n]
          (println "skip [n] " n)
          (let [result (proxy-super skip n)]
            (swap! pos + result)
            result))
        ;; Unfortunately, proxy does not give us a good way to overload
        ;; methods on type only (i.e. where arity is the same).
        (unread
          ([c]
             (println "unread [c] " c " " pos)
             (if (instance? Number c)
               (swap! pos dec)
               (swap! pos - (alength c)))
             (proxy-super unread c))
          ([cbuf off len]
             (println "unread [cbuf off len] " cbuf " " off " " len)
             (proxy-super unread cbuf off len)
             (swap! pos - len))))]))


(defn read-forms
  "Given a string, return a sequence of forms read from that string.
Each form will be annotated with :geirrod.form-reader/source metadata
whose value is a string that is the source code of the original form."
  [s]
  (throw (Exception. "Not yet implemented.")))
