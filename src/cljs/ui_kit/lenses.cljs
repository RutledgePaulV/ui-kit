(ns ui-kit.lenses
  (:require [ui-kit.walkers :as walkers]
            [ui-kit.semantic :as sa]
            [ui-kit.utils :as utils]))

(defn get-attrs [cv]
  (let [normalized (walkers/normalize cv)]
    (when (vector? normalized)
      (second cv))))

(defn get-attr
  ([cv key]
   (get-attr cv key nil))
  ([cv key default]
   (or (get (get-attrs cv) key) default)))

(defn assoc-attr [cv & key+value]
  (assert (even? (count key+value)) "Must have a value for every key.")
  (let [normalized (walkers/normalize cv)]
    (update normalized 1 merge (apply hash-map key+value))))

(defn update-attr [cv key fun & args]
  (let [normalized (walkers/normalize cv)
        existing   (get-attr normalized key)
        new-value  (apply fun existing args)]
    (assoc-attr normalized key new-value)))

(defn get-children [cv]
  (drop 2 (walkers/normalize cv)))

(defn has-children? [cv]
  (not-empty (get-children cv)))

(defn form-field? [cv]
  (identical? sa/form-field (first cv)))

(defn form-input? [cv]
  (contains? #{sa/form-input sa/form-select} (first cv)))

(defn has-label? [cv]
  (or
    (some? (:label (get-attrs cv)))
    (some (fn [x] (= (first x) :label)) (get-children cv))))

(defn prepend-child [cv child]
  (utils/vec-insert (walkers/normalize cv) 2 child))

(defn set-label-if-unset [cv label]
  (let [normal (walkers/normalize cv)]
    (let [has (has-label? normal)]
      (cond
        (and (form-field? normal) (has-children? normal) (not has))
        (prepend-child normal [:label label])
        (and (form-field? normal) (not has))
        (assoc-attr normal :label label)
        :otherwise
        normal))))