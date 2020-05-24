(ns ui-kit.utils
  (:require [ui-kit.semantic :as sa]
            [clojure.string :as strings]))

(defn vec-insert [coll pos item]
  (vec (concat (subvec coll 0 pos) [item] (subvec coll pos))))

(defn vec-remove [coll pos]
  (vec (concat (subvec coll 0 pos) (subvec coll (inc pos)))))

(defn vec-swap [coll a b]
  (let [a-item (nth coll a)
        b-item (nth coll b)]
    (-> coll
        (assoc b a-item)
        (assoc a b-item))))

(defn has-attrs? [cv]
  (map? (second cv)))

(defn normalize [cv]
  (if (has-attrs? cv)
    cv
    (vec-insert cv 1 {})))

(defn get-attr
  ([component-vector key]
   (get-attr component-vector key nil))
  ([component-vector key default]
   (or (get-in (normalize component-vector) [1 key]) default)))

(defn assoc-attrs [component-vector & key+value]
  (assert (even? (count key+value)) "Must have a value for every key.")
  (update (normalize component-vector) 1 merge (apply hash-map key+value)))

(defn update-attr [component-vector key fun & args]
  (let [existing  (get-attr component-vector key)
        new-value (apply fun existing args)]
    (assoc-attrs component-vector key new-value)))

(defn get-attrs [cv]
  (second (normalize cv)))

(defn form-field? [cv]
  (identical? sa/form-field (first cv)))

(defn get-children [cv]
  (drop 2 (normalize cv)))

(defn has-children? [cv]
  (not-empty (get-children cv)))

(defn has-label? [cv]
  (or
    (some? (:label (get-attrs cv)))
    (some (fn [x] (= (first x) :label)) (get-children cv))))

(defn prepend-child [cv child]
  (vec-insert (normalize cv) 2 child))

(defn set-label-if-unset [cv label]
  (let [has (has-label? cv)]
    (cond
      (and (form-field? cv) (has-children? cv) (not has))
      (prepend-child cv [:label label])
      (and (form-field? cv) (not has))
      (assoc-attrs cv :label label)
      :otherwise
      cv)))

(defn select-ns [m nspace]
  (into {}
        (keep (fn [[k v]]
                  (when (= (some-> (namespace k) name) (name nspace))
                    [(keyword (name k)) v]))
              m)))

(defn key->label [k]
  (-> k
      (name)
      (strings/capitalize)
      (strings/replace
        #"[a-z]-[a-z]"
        (fn [match]
          (str (first match) " " (strings/upper-case (second match)))))))