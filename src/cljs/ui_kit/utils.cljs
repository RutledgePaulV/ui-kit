(ns ui-kit.utils
  (:require [clojure.string :as strings]
            [cljs.pprint :as pprint]
            [clojure.walk :as walk]))

(defn vec-insert [coll pos item]
  (into (subvec coll 0 pos) (cons item (subvec coll pos))))

(defn vec-remove [coll pos]
  (into (subvec coll 0 pos) (subvec coll (inc pos))))

(defn vec-swap [coll a b]
  (-> coll (assoc b (nth coll a)) (assoc a (nth coll b))))

(defn select-ns [m nspace]
  (into {}
        (keep (fn [[k v]]
                (when (= (some-> k namespace name) (name nspace))
                  [(keyword (name k)) v]))
              m)))

(defn prettify [x]
  (walk/postwalk
    (fn [form]
      (if (map? form)
        (into (sorted-map) form)
        form))
    x))

(defn ppstr [x]
  (with-out-str
    (pprint/pprint
      (prettify x))))

(defn event->value [event]
  (-> event .-target .-value))

(defn kebab->title [k]
  (-> k
      (name)
      (strings/capitalize)
      (strings/replace
        #"[a-z]-[a-z]"
        (fn [[terminal _ start]]
          (str terminal " " (strings/upper-case start))))))

(defn camel->kebab [k]
  (-> (name k)
      (strings/replace
        #"[a-z][A-Z]"
        (fn [[terminal start]]
          (str terminal "-" start)))
      (strings/lower-case)))