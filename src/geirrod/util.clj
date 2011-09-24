(ns geirrod.util)

(defn- setify-map
  "Take a map and turn it into another map with the same keys and
  values which are sets containing the values of the corresponding
  key. E.g. {:a 1 :b 2} => {:a #{1} :b #{2}}."
  [m]
  (into {} (map (fn [[k v]] [k #{v}]) m)))

(defn merge-to-sets
  "Merge maps, turning the values into sets containing all the unique
  items from the corresponding keys of all maps."
  [& maps]
  (reduce #(merge-with into %1 %2) (map setify-map maps)))
