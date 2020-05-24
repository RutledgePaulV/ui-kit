(ns ui-kit.utils)

(defn vec-insert [coll pos item]
  (vec (concat (subvec coll 0 pos) [item] (subvec coll (inc pos)))))

(defn vec-remove [coll pos]
  (vec (concat (subvec coll 0 pos) (subvec coll (inc pos)))))

(defn vec-swap [coll a b]
  (let [a-item (nth coll a)
        b-item (nth coll b)]
    (-> coll
        (assoc b a-item)
        (assoc a b-item))))

(defn get-attr
  ([component-vector key]
   (get-attr component-vector key nil))
  ([component-vector key default]
   (if (map? (second component-vector))
     (or (get-in component-vector [1 key]) default)
     default)))

(defn assoc-attrs [component-vector & key+value]
  (assert (even? (count key+value)) "Must have a value for every key.")
  (if (map? (second component-vector))
    (update component-vector 1 merge (apply hash-map key+value))
    (vec-insert component-vector 1 (apply hash-map key+value))))

(defn update-attr [component-vector key fun & args]
  (let [existing  (get-attr component-vector key)
        new-value (apply fun existing args)]
    (assoc-attrs component-vector key new-value)))