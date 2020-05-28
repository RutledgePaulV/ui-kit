(ns ui-kit.visitors
  (:require [malli.core :as malli]
            [ui-kit.semantic :as sa]
            [reagent.core :as r]
            [ui-kit.utils :as utils]
            [malli.core :as m]
            [malli.provider :as mp]))

(defmulti visit
  (fn [node cursor attrs]
    (malli/name node)))

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
        [sa/form-select
         {:options   (for [[index child] (map-indexed vector children)]
                       {:key index :value index :text (str "Option " index)})
          :on-change #(reset! selected-option (.-value %2))}]]
       [sa/form-group
        [visit (nth children @selected-option) cursor {}]]])))

(defmethod visit :map [node cursor attrs]
  (let [children (malli/children node)]
    (into [sa/form-group]
          (for [[k v] children]
            (let [label (utils/kebab->title k)]
              (with-meta [visit v (r/cursor cursor [k]) {:label label}] {:key k}))))))

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

(defmethod visit :enum [node cursor attrs]
  (let [props     (malli/properties node)
        all-attrs (utils/select-ns props :sui)
        value     (or (deref cursor) (:default props))
        label     (or (:label all-attrs) (:label attrs))
        required  (not (:optional props false))]
    [sa/form-field
     (merge {:required required} (dissoc all-attrs :label))
     (when (some? label) [:label label])
     [sa/dropdown
      {:selection    true
       :search       true
       :defaultValue value
       :options      (vec (for [child (malli/children node)]
                            {:key child :value child :text child}))
       :onChange     (fn [x data]
                       (reset! cursor (.-value data)))}]]))

(defmethod visit 'string? [node cursor attrs]
  (let [props     (malli/properties node)
        all-attrs (utils/select-ns props :sui)
        value     (or (deref cursor) (:default props))
        label     (or (:label all-attrs) (:label attrs))
        required  (not (:optional props false))]
    [sa/form-field
     (merge {:required required} (dissoc all-attrs :label))
     (when (some? label) [:label label])
     [sa/form-input
      {:type         :text
       :defaultValue (or value "")
       :on-blur      #(reset! cursor (utils/event->value %))}]]))

(defn schema->form [schema data]
  (let [root (r/atom data)]
    [sa/form {:error true :warning true}
     [visit (malli/schema schema) root {}]]))

(defn schema2->form [schema root]
  [sa/form {:error true :warning true}
   [visit (malli/schema schema) root {}]])

(defn data->form
  ([data]
   (data->form data data))
  ([data-for-schema data-to-fill]
   (schema->form (mp/provide [data-for-schema]) data-to-fill)))
