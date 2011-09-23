(ns geirrod.test.github-api
  (:use [geirrod.github-api])
  (:use [clojure.test]))

(deftest test-group-by-lane
  (let [issue-1 {"number" 1,
                 "labels" [{"name" "test"}
                           {"name" "status new"}]}
        issue-2 {"number" 2,
                 "labels" [{"name" "status new"}
                           {"name" "status in-review"}]}]
    (is (= {"new" #{issue-1 issue-2} "in-review" #{issue-2}}
           (group-by-lane "status" [issue-1 issue-2])))))

(deftest test-lanes
  (is (= #{"new" "in-review"}
         (set (lanes "status" ["status new" "status in-review" "release 1.0"])))))

