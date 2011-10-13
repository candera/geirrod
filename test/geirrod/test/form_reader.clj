(ns geirrod.test.form-reader
  (:use [geirrod.form-reader]
        [clojure.test]))

(deftest reads-empty-form
  (is (= []
         (read-forms ))))