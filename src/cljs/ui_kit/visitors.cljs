(ns ui-kit.visitors
  (:require [malli.core :as malli]
            [ui-kit.semantic :as sa]
            [reagent.core :as r]
            [ui-kit.utils :as utils]
            [malli.core :as m]))

(defmulti visit
  "Turn a schema node into a reagent component.

    node - the malli schema node
    cursor - reagent cursor for that position in the form
    attrs - any settings passed to a child from the parent

   If node is not a leaf you're responsible for directly
   rendering or recursively visiting the children.
  "
  (fn [node cursor attrs]
    (malli/name node)))

(defn is-required? [props attrs]
  (if (contains? props :optional)
    (not (:optional props))
    (or (:required attrs) true)))

(defn schema-dissoc [map-schema key]
  (vec
    (remove
      (fn [x]
        (and (vector? x) (= key (first x))))
      map-schema)))

(defn get-default-value [node]
  (let [props (malli/properties node)]
    (if-some [value (:default props)]
      value
      ; TODO
      ; generate random?
      ; dispatch by type?
      ; leave as nil?
      )))

(defn error-message [node message]
  [sa/message {:error true}
   [sa/message-content
    [:div
     [:p message]
     [:code [:pre (utils/ppstr (m/form node))]]]]])

(defmethod visit :default [node cursor attrs]
  [error-message node "Unsupported schema. Add a defmethod to ui-kit.visitors/visit to remedy."])

(defmethod visit :and [node cursor attrs]
  (let [children (malli/children node)]
    (into [sa/form-group {}]
          (for [[index child] (map-indexed vector children)]
            (with-meta [visit child cursor {}] {:key index})))))

(defmethod visit :or [node cursor attrs]
  (let [children        (malli/children node)
        props           (malli/properties node)
        selected-option (r/atom 0)]
    (fn []
      [:div {}
       [sa/form-field
        [:label (or (:sui/label props) "Select variant")]
        [sa/dropdown
         {:selection true
          :search    true
          :options   (for [index (range (count children))]
                       {:key index :value index :text (str "Option " index)})
          :onChange  #(reset! selected-option (.-value %2))}]]
       [sa/form-group
        [visit (nth children @selected-option) cursor {}]]])))

(defmethod visit :map [node cursor attrs]
  (let [children (malli/children node)]
    (into [sa/form-group]
          (for [[k v] children]
            (let [label (utils/kebab->title k)]
              (with-meta [visit v (r/cursor cursor [k]) {:label label}] {:key k}))))))


(defmethod visit :multi [node cursor attrs]
  (let [props    (malli/properties node)
        children (malli/children node)
        dispatch (get-in props [:dispatch])]
    (cond
      (keyword? dispatch)
      (let [label (utils/kebab->title dispatch)
            by-dv (into {} (map (juxt first identity)) children)]
        [:div {}
         [sa/form-field {:required (not (:optional props false))}
          [:label label]
          [sa/dropdown
           {:selection    true
            :search       true
            :defaultValue (get @cursor dispatch)
            :options      (vec (for [[dispatch-value] children]
                                 {:key dispatch-value :value dispatch-value :text dispatch-value}))
            :onChange     #(swap! cursor assoc dispatch (keyword (.-value %2)))}]]
         (when-some [child (get by-dv (get-in @cursor [dispatch]))]
           (let [child-without-dispatch-field
                 (schema-dissoc (second child) dispatch)]
             [sa/form-group {}
              [visit child-without-dispatch-field cursor {}]]))])
      :otherwise
      (error-message node "only multi schemas that dispatch by keyword or equality are supported."))))

(defmethod visit :maybe [node cursor attrs]
  [visit (first (malli/children node)) cursor (assoc attrs :required false)])


; TODO: use fancier r/cursor with lens and lookup table of {row index} to avoid re-rendering
; each row each time.
(defmethod visit :vector [node cursor attrs]
  (let [[child-schema] (malli/children node)
        child-values (or @cursor [])]
    (conj (into [:div {}]
                (for [index (range (count child-values))]
                  [sa/form-group {:inline true :key (random-uuid)}
                   [visit child-schema (r/cursor cursor [index]) {}]
                   [sa/button
                    {:icon     true
                     :disabled (= 0 index)
                     :on-click (fn [] (swap! cursor (fn [old] (utils/vec-swap old index (dec index)))))}
                    [sa/icon {:name "arrow up" :size :small}]]
                   [sa/button
                    {:icon     true
                     :disabled (= (count child-values) (inc index))
                     :on-click (fn [] (swap! cursor (fn [old] (utils/vec-swap old index (inc index)))))}
                    [sa/icon {:name "arrow down" :size :small}]]
                   [sa/button
                    {:icon     true
                     :on-click (fn [] (swap! cursor (fn [old] (utils/vec-remove old index))))}
                    [sa/icon {:name "delete" :size :small}]]]))
          [sa/button {:icon     true
                      :on-click (fn [] (swap! cursor
                                              (fn [old]
                                                (let [default (get-default-value child-schema)]
                                                  (conj old default)))))}
           [sa/icon {:name "plus" :size :large}]])))

(defmethod visit :tuple [node cursor attrs]
  (into [sa/form-group {:inline true}]
        (for [[index child] (map-indexed vector (malli/children node))]
          [visit child (r/cursor cursor [index]) {}])))

(defmethod visit := [node cursor attrs]
  (let [props     (malli/properties node)
        all-attrs (utils/select-ns props :sui)
        value     (or (deref cursor) (:default props))
        label     (or (:label all-attrs) (:label attrs))
        required  (is-required? props attrs)]
    [sa/form-field
     (merge {:required required} (dissoc all-attrs :label))
     (when (some? label) [:label label])
     [sa/form-input {:value value :disabled true}]]))

(defmethod visit :enum [node cursor attrs]
  (let [props     (malli/properties node)
        all-attrs (utils/select-ns props :sui)
        value     (or (deref cursor) (:default props))
        label     (or (:label all-attrs) (:label attrs))
        required  (is-required? props attrs)]
    [sa/form-field
     (merge {:required required} (dissoc all-attrs :label))
     (when (some? label) [:label label])
     [sa/dropdown
      {:selection    true
       :search       true
       :defaultValue value
       :options      (vec (for [child (malli/children node)]
                            {:key child :value child :text child}))
       :onChange     #(reset! cursor (.-value %2))}]]))

(defn simple-leaf [node cursor attrs mixins]
  (let [props     (malli/properties node)
        all-attrs (utils/select-ns props :sui)
        value     (or (deref cursor) (:default props))
        label     (or (:label all-attrs) (:label attrs))
        required  (is-required? props attrs)]
    [sa/form-field
     (merge {:required required} (dissoc all-attrs :label))
     (when (some? label) [:label label])
     [sa/form-input
      (merge {:defaultValue (or value "")
              :error        (if nil {:content ""})
              :on-blur      #(reset! cursor (utils/event->value %))}
             mixins)]]))

(defmethod visit ':re [node cursor attrs]
  (simple-leaf node cursor attrs {:type :text}))

(defmethod visit 'string? [node cursor attrs]
  (simple-leaf node cursor attrs {:type :text}))

(defmethod visit 'int? [node cursor attrs]
  (simple-leaf node cursor attrs {:type :number}))

(defmethod visit 'pos-int? [node cursor attrs]
  (simple-leaf node cursor attrs {:type :number}))

(defmethod visit 'boolean? [node cursor attrs]
  (simple-leaf node cursor attrs {:type :checkbox}))

(defmethod visit 'inst? [node cursor attrs]
  (simple-leaf node cursor attrs {:type :datetime-local}))

(defmethod visit 'uri? [node cursor attrs]
  (simple-leaf node cursor attrs {:type :url}))
