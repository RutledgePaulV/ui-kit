(ns ui-kit.walkers
  "For walking reagent trees.")

(defn normalize
  "Ensure component vectors are in [tag attrs & children] form."
  [cv]
  (cond
    (some-> cv meta ::normalized)
    cv
    (vector? cv)
    (cond
      (fn? (first cv))
      (into [(with-meta
               (fn [& args] (normalize (apply (first cv) args)))
               (merge (meta cv) {::normalized true}))]
            (rest cv))
      (map? (second cv))
      (with-meta
        (into [(first cv) (second cv)] (map normalize (drop 2 cv)))
        (merge (meta cv) {::normalized true}))
      :otherwise
      (with-meta
        (into [(first cv) {}] (map normalize (drop 1 cv)))
        (merge (meta cv) {::normalized true})))
    :otherwise
    cv))

(defn walk-components* [fun root]
  (cond
    (and (vector? root) (fn? (first root)))
    (->> (drop 1 root)
         (into [(fn [& args]
                  (walk-components* fun (apply (first root) args)))])
         (fun))
    (and (vector? root) (<= 2 (count root)) (map? (second root)))
    (->> (keep #(walk-components* fun %) (drop 2 root))
         (into [(first root) (second root)])
         (fun))
    (and (vector? root) (<= 1 (count root)) (not (map? (second root))))
    (->> (keep #(walk-components* fun %) (drop 1 root))
         (into [(first root)])
         (fun))
    :otherwise
    root))

(defn walk-components
  "Walk over every component vector within root and apply fun to each."
  [fun root]
  (walk-components* fun (normalize root)))

(defn walk-where [pred fun root]
  (walk-components #(if (pred %) (fun %) %) root))

(defn walk-tags [tagset fun root]
  (walk-where (comp (set tagset) first) fun root))

(defn walk-expand [root]
  (walk-where (comp fn? first) (partial apply apply) root))