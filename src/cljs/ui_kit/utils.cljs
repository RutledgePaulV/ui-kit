(ns ui-kit.utils
  (:require [clojure.string :as strings]
            [cljs.pprint :as pprint]))

(defn vec-insert [coll pos item]
  (vec (concat (subvec coll 0 pos) [item] (subvec coll pos))))

(defn vec-remove [coll pos]
  (vec (concat (subvec coll 0 pos) (subvec coll (inc pos)))))

(defn vec-swap [coll a b]
  (-> coll (assoc b (nth coll a)) (assoc a (nth coll b))))

(defn select-ns [m nspace]
  (into {}
        (keep (fn [[k v]]
                (when (= (some-> k namespace name) (name nspace))
                  [(keyword (name k)) v]))
              m)))

(defn ppstr [x]
  (with-out-str (pprint/pprint x)))

(defn event->value [event]
  (-> event .-target .-value))

(defn kebab->title [k]
  (-> k
      (name)
      (strings/capitalize)
      (strings/replace
        #"[a-z]-[a-z]"
        (fn [match]
          (str (first match) " " (strings/upper-case (nth match 2)))))))

(defn camel->kebab [k]
  (-> (name k)
      (strings/replace
        #"[a-z][A-Z]"
        (fn [match]
          (str (first match) "-" (second match))))
      (strings/lower-case)))