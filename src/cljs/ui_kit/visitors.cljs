(ns ui-kit.visitors
  (:require [ui-kit.semantic :as sa]
            [reagent.core :as r]
            [ui-kit.utils :as utils]
            [malli.core :as m]
            [malli.transform :as mt]
            [malli.error :as me]))


(defn power-transformer []
  (mt/transformer
    mt/json-transformer
    mt/string-transformer
    mt/collection-transformer
    mt/strip-extra-keys-transformer
    mt/default-value-transformer))

(defn validate [node value]
  (when-not (m/validate node value)
    (me/humanize (m/explain node value))))

(defn ctx->transformer [context]
  (or (get-in context [:malli :transformer]) power-transformer))

(defn coerce [node value context]
  (let [transformer (ctx->transformer context)]
    (m/decode node value transformer)))

(defmulti visit
  "Turn a schema node into a reagent component.

    node - the malli schema node
    cursor - reagent cursor for that position in the form
    context - any settings passed to a child from the parent

   If node is not a leaf you're responsible for directly
   rendering or recursively visiting the children.
  "
  (fn [node cursor context]
    (m/name node)))

(defn is-required? [props context]
  (if (contains? props :optional)
    (not (:optional props))
    (or (:required context) true)))

(defn child-context [context & more]
  (apply merge (select-keys context [:malli]) more))

(defn schema-dissoc [map-schema key]
  (vec
    (remove
      (fn [x]
        (and (vector? x) (= key (first x))))
      map-schema)))

(defn get-default-value [node]
  (let [props (m/properties node)]
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

(defmethod visit :default [node cursor context]
  [error-message node "Missing defmethod of ui-kit.visitors/visit for schema."])

(defmethod visit :and [node cursor context]
  [:<> (for [[index child] (map-indexed vector (m/children node))]
         (with-meta [visit child cursor (child-context context)] {:key index}))])

(defmethod visit :or [node cursor context]
  (let [children        (m/children node)
        props           (m/properties node)
        selected-option (r/atom 0)]
    (fn []
      [:div
       [sa/form-field
        [:label (or (:sui/label props) "Select variant")]
        [sa/dropdown
         {:selection true
          :search    true
          :options   (for [index (range (count children))]
                       {:key index :value index :text (str "Option " index)})
          :onChange  #(reset! selected-option (.-value %2))}]]
       [visit (nth children @selected-option) cursor (child-context context)]])))

(defmethod visit :map [node cursor context]
  [:<> (for [[k v] (m/children node)]
         (let [label (utils/kebab->title k)
               ctx   (child-context context {:label label})]
           (with-meta [visit v (r/cursor cursor [k]) ctx] {:key k})))])

(defmethod visit :multi [node cursor context]
  (let [props    (m/properties node)
        children (m/children node)
        dispatch (get-in props [:dispatch])]
    (cond
      (keyword? dispatch)
      (let [label (utils/kebab->title dispatch)
            by-dv (into {} (map (juxt first identity)) children)]
        [:div
         [sa/form-field {:required (is-required? props context)}
          [:label label]
          [sa/dropdown
           {:selection    true
            :search       true
            :defaultValue (get @cursor dispatch)
            :options      (vec (for [[dispatch-value] children]
                                 {:key dispatch-value :value dispatch-value :text dispatch-value}))
            :onChange     #(swap! cursor assoc dispatch (keyword (.-value %2)))}]]
         (when-some [child (get by-dv (get-in @cursor [dispatch]))]
           [visit (schema-dissoc (second child) dispatch) cursor (child-context context)])])
      :otherwise
      (error-message node "only multi schemas that dispatch by keyword or equality are supported."))))

(defmethod visit :maybe [node cursor context]
  [visit (first (m/children node)) cursor (assoc context :required false)])


; TODO: use fancier r/cursor with lens and lookup table of {row index} to avoid re-rendering
; each row each time.
(defmethod visit :vector [node cursor context]
  (let [[child-schema] (m/children node)
        props        (m/properties node)
        child-values @cursor
        child-count  (count child-values)
        all-context  (utils/select-ns props :sui)
        label        (or (:label all-context) (:label context))
        required     (is-required? props context)]
    [sa/form-field
     (merge {:required required} (dissoc all-context :label))
     (when (some? label) [:label label])
     (conj (into [sa/grid {:columns 2 :stackable true :divided "vertically"}]
                 (for [index (range child-count)]
                   [sa/grid-row {:key (random-uuid)}
                    [sa/grid-column {:width 13 :floated "left"}
                     [visit child-schema (r/cursor cursor [index]) (child-context context)]]
                    [sa/grid-column {:width 3 :floated "right" :textAlign "right"}
                     [sa/button
                      {:icon     true
                       :disabled (= 0 index)
                       :on-click (fn [] (swap! cursor (fnil (fn [old] (utils/vec-swap old index (dec index))) [])))}
                      [sa/icon {:name "arrow up" :size :small}]]
                     [sa/button
                      {:icon     true
                       :disabled (= child-count (inc index))
                       :on-click (fn [] (swap! cursor (fnil (fn [old] (utils/vec-swap old index (inc index))) [])))}
                      [sa/icon {:name "arrow down" :size :small}]]
                     [sa/button
                      {:icon     true
                       :class    "red"
                       :on-click (fn [] (swap! cursor (fnil (fn [old] (utils/vec-remove old index)) [])))}
                      [sa/icon {:name "delete" :size :small}]]]]))
           [sa/grid-row
            [sa/grid-column {:width 4 :floated "right" :textAlign "right"}
             [sa/button
              {:icon     true
               :on-click (fn [] (swap! cursor (fnil (fn [old] (conj old (get-default-value child-schema))) [])))}
              [sa/icon {:name "plus" :size :large}]]]])]))

(defmethod visit :tuple [node cursor context]
  (into [sa/form-group {:inline true}]
        (for [[index child] (map-indexed vector (m/children node))]
          [visit child (r/cursor cursor [index]) (child-context context)])))

(defmethod visit := [node cursor context]
  (let [props       (m/properties node)
        all-context (utils/select-ns props :sui)
        value       (or (deref cursor) (:default props))
        label       (or (:label all-context) (:label context))
        required    (is-required? props context)]
    [sa/form-field
     (merge {:required required} (dissoc all-context :label))
     (when (some? label) [:label label])
     [sa/form-input {:value value :readOnly true}]]))

(defmethod visit :enum [node cursor context]
  (let [props       (m/properties node)
        all-context (utils/select-ns props :sui)
        value       (or (deref cursor) (:default props))
        label       (or (:label all-context) (:label context))
        required    (is-required? props context)]
    [sa/form-field
     (merge {:required required} (dissoc all-context :label))
     (when (some? label) [:label label])
     [sa/dropdown
      {:selection    true
       :search       true
       :defaultValue value
       :options      (vec (for [child (m/children node)]
                            {:key child :value child :text child}))
       :onChange     #(reset! cursor (.-value %2))}]]))

(defn simple-leaf [node cursor context mixins]
  (let [props       (m/properties node)
        all-context (utils/select-ns props :sui)
        value       (or (deref cursor) (:default props))
        label       (or (:label all-context) (:label context))
        required    (is-required? props context)]
    [sa/form-field
     (merge {:required required} (dissoc all-context :label))
     (when (some? label) [:label label])
     [sa/form-input
      (merge {:defaultValue (or value "")
              :error        (if nil {:content ""})
              :on-blur      #(reset! cursor (utils/event->value %))}
             mixins)]]))

(defmethod visit ':re [node cursor context]
  (simple-leaf node cursor context {:type :text}))

(defmethod visit 'string? [node cursor context]
  (simple-leaf node cursor context {:type :text}))

(defmethod visit 'int? [node cursor context]
  (simple-leaf node cursor context {:type :number}))

(defmethod visit 'pos-int? [node cursor context]
  (simple-leaf node cursor context {:type :number}))

(defmethod visit 'boolean? [node cursor context]
  (let [props       (m/properties node)
        all-context (utils/select-ns props :sui)
        value       (or (deref cursor) (:default props))
        label       (or (:label all-context) (:label context))
        required    (is-required? props context)]
    [sa/form-field
     (merge {:required required} (dissoc all-context :label))
     (when (some? label) [:label label])
     [sa/form-radio
      {:defaultChecked (if (some? value) true false)
       :toggle         true
       :error          (if nil {:content ""})
       :on-change      #(reset! cursor (.-checked %2))}]]))

(defmethod visit 'inst? [node cursor context]
  (let [props       (m/properties node)
        all-context (utils/select-ns props :sui)
        value       (or (deref cursor) (:default props))
        label       (or (:label all-context) (:label context))
        required    (is-required? props context)]
    [sa/form-field
     (merge {:required required} (dissoc all-context :label))
     (when (some? label) [:label label])
     [sa/date-time-input
      {:value            (or value "")
       :dateFormat       "MM/DD/YYYY"
       :timeFormat       "ampm"
       :error            (if nil {:content ""})
       :closable         true
       :preserveViewMode false
       :on-change        #(reset! cursor (.-value %2))}]]))

