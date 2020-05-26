(ns ui-kit.walkers-test
  (:require [ui-kit.walkers :as walkers])
  (:require-macros [cljs.test :refer [deftest is run-tests]]))


(deftest normalize-test
  (is (= [:p {} "Test"] (walkers/normalize [:p "Test"])))
  (is (= [:div {} [:p {} "Test"]] (walkers/normalize [:div [:p "Test"]]))))

(deftest expand-test
  (let [inner (fn [] [:div [:p "Inner component"]])
        outer [:div [inner]]]
    (is (= [:div {}
            [:div {}
             [:p {} "Inner component"]]]
           (walkers/walk-expand outer)))))

(deftest walk-tags-test
  (let [tree   [:div [:p "Stuff"] [:p "Thing"]]
        result (walkers/walk-tags #{:p} (fn [cv] (assoc-in cv [1 :key] true)) tree)]
    (is (= [:div {} [:p {:key true} "Stuff"] [:p {:key true} "Thing"]] result))))
