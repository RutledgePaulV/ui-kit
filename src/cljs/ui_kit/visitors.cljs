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

(defn error-message [{:keys [path schema]} message]
  [sa/message {:error true}
   [sa/message-content
    [:div
     [:p message]
     [:code [:pre (utils/ppstr {:path path :schema (m/form schema)})]]]]])

(defmethod visit :default [node cursor attrs]
  [sa/message {:error true}
   [sa/message-content
    [:div
     [:p "Unsupported schema"]
     [:code [:pre (utils/ppstr (malli/form node))]]]]])

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
