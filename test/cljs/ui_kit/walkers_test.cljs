(ns ui-kit.walkers-test
  (:require [ui-kit.walkers :as walkers])
  (:require-macros [cljs.test :refer [deftest is run-tests]]))


(deftest normalize-test
  (is (= [:p {} "Test"] (walkers/normalize [:p "Test"])))
  (is (= [:div {} [:p {} "Test"]] (walkers/normalize [:div [:p "Test"]]))))

(deftest normalize-sequences
  (is (= [:p {} [:div {} "a"] [:div {} "b"]]
         (walkers/normalize [:p (for [x ["a" "b"]] [:div x])]))))

(deftest expand-fun
  (is (= [:div {} "Test"] (walkers/walk-expand (fn [] [:div "Test"])))))

(deftest expand-test
  (let [inner2 (fn [] [:div [:p "inner 2"]])
        inner  (fn [] [:div [:p "Inner 1"] [inner2]])
        outer  [:div [inner]]]
    (is (= [:div {}
            [:div {}
             [:p {} "Inner 1"]
             [:div {}
              [:p {} "inner 2"]]]]
           (walkers/walk-expand outer)))))
