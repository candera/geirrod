(ns geirrod.test.github-api
  (:use [geirrod.github-api])
  (:use [clojure.test]))

(deftest test-group-by-lanes
  (let [issue-1 {"number" 1,
                 "labels" [{"name" "test"}
                           {"name" "status new"}]}
        issue-2 {"number" 2,
                 "labels" [{"name" "status new"}
                           {"name" "status in-review"}]}]
    (is (= {"new" #{issue-1 issue-2} "in-review" #{issue-2}}
           (group-by-lanes "status" [issue-1 issue-2])))))

