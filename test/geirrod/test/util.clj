(ns geirrod.test.util
  (:use [geirrod.util])
  (:use [clojure.test]))

(deftest test-merge-to-sets
  (is (= {} (merge-to-sets {})))
  (is (= {:a #{1}} (merge-to-sets {:a 1})))
  (is (= {:a #{1} :b #{2}} (merge-to-sets {:a 1} {:b 2})))
  (is (= {:a #{1 2}} (merge-to-sets {:a 1} {:a 2})))
  (is (= {:a #{1 2 3} :b #{2 3}}
         (merge-to-sets {:a 1 :b 2}
                        {:a 2 :b 3}
                        {:a 3}))))
