(ns ui-kit.utils)


(defn vec-remove [coll pos]
  (vec (concat (subvec coll 0 pos) (subvec coll (inc pos)))))

(defn vec-swap [coll a b]
  (let [a-item (nth coll a)
        b-item (nth coll b)]
    (-> coll
        (assoc b a-item)
        (assoc a b-item))))