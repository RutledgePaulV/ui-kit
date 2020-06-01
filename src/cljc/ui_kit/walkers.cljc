(ns ui-kit.walkers
  "For walking reagent trees.")

(defn multi-fn? [x]
  #?(:clj  (instance? clojure.lang.MultiFn x)
     :cljs (instance? cljs.core.MultiFn x)))

(defn function? [x]
  (or (fn? x) (multi-fn? x)))

(defn expand [x]
  (if (seq? x) x [x]))

(defn normalize
  "Ensure component vectors are in [tag attrs & children] form."
  [cv]
  (cond
    (some-> cv meta ::normalized)
    cv
    (function? cv)
    (fn [& args] (normalize (apply cv args)))
    (vector? cv)
    (with-meta
      (cond
        (function? (first cv))
        (into [(comp normalize (first cv))] (rest cv))
        (map? (second cv))
        (into [(first cv) (second cv)]
              (map normalize (mapcat expand (drop 2 cv))))
        (not-empty cv)
        (into [(first cv) {}]
              (map normalize (mapcat expand (drop 1 cv)))))
      (merge (meta cv) {::normalized true}))
    :otherwise
    cv))

(defn walk-components
  "Walk over every component vector within root and apply fun to each."
  [fun root]
  (letfn [(walk-components* [fun root]
            (cond
              (function? root)
              (fun (fn [] (walk-components* fun (root))))
              (and (vector? root) (function? (first root)))
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
              root))]
    (walk-components* fun (normalize root))))

(defn walk-expand
  "Fully expands/inlines a reagent tree (bottoms out at hiccup or react classes)"
  [root]
  (walk-components
    (fn [x]
      (cond
        (function? x)
        (x)
        (and (vector? x) (function? (first x)))
        (apply (first x) (rest x))
        :otherwise
        x))
    root))