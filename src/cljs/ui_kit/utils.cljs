(ns ui-kit.utils
  (:require [clojure.string :as strings]))

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

(defn select-ns [m nspace]
  (into {}
        (keep (fn [[k v]]
                (when (= (some-> k namespace name) (name nspace))
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