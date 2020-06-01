(ns ui-kit.visitors-test
  (:require-macros [cljs.test :refer [deftest is run-tests]])
  (:require [ui-kit.visitors :as visitors]
            [malli.core :as m]))

(deftest default-registry-is-implemented
  (let [default (get-method visitors/visit
                  (default-dispatch-val visitors/visit))]
    (doseq [schema (remove fn? (keys m/default-registry))]
      (let [it (get-method visitors/visit schema)]
        (is (some? it) "Visitor didn't return anything.")
        (is (not (identical? default it))
            (str "No visitor registered for schema " schema))))))


