(ns ui-kit.walkers)

(defn normalize
  "Ensure component vectors are in [tag attrs & children] form."
  [cv]
  (if (some-> cv meta ::normalized)
    cv
    (cond
      (fn? cv)
      (with-meta
        (fn [& args] (normalize (apply cv args)))
        (merge (meta cv) {::normalized true}))
      (and (vector? cv) (map? (second cv)))
      (with-meta
        (into [(first cv) (second cv)] (map normalize (drop 2 cv)))
        (merge (meta cv) {::normalized true}))
      (vector? cv)
      (with-meta
        (into [(first cv) {}] (map normalize (drop 1 cv)))
        (merge (meta cv) {::normalized true}))
      :otherwise
      cv)))

(defn walk-components* [fun root]
  (cond
    (and (vector? root) (<= 2 (count root)))
    (->> (keep #(walk-components* fun %) (drop 2 root))
         (into [(first root) (second root)])
         (fun))
    (fn? root)
    (fn [& args]
      (walk-components* fun (apply root args)))
    :otherwise
    root))

(defn walk-components
  "Walk over every component vector within root and apply fun to each.
   Returns new tree."
  [fun root]
  (walk-components* fun (normalize root)))

(defn walk-where [pred fun root]
  (walk-components #(if (pred %) (fun %) %) root))
