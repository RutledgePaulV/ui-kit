(ns ui-kit.core
  (:require [ui-kit.semantic :as sa]
            [ui-kit.utils :as utils]
            [malli.provider :as mp]
            [malli.core :as m]
            [cljs.pprint :as pprint]))

(defn dispatch-fun [form]
  (:name form))

(defmulti compile-form
  (fn [& args] (apply dispatch-fun args)))

(defmethod compile-form :default [{:keys [schema path] :as node}]
  [sa/message {:error true}
   [sa/message-content
    [:div
     [:p "You must add a defmethod to ui-kit.core/compile-form to use this schema."]
     [:code [:pre (with-out-str (pprint/pprint {:path path :schema (m/form schema)}))]]]]])

(defmethod compile-form :and [{:keys [children] :as node}]
  (into [sa/form-group {}]
        (for [[index child] (map-indexed vector children)]
          (utils/assoc-attrs child :key index))))

(defmethod compile-form :or [{:keys [props children] :as node}]
  (let [selected-option (reagent.core/atom 0)]
    (fn []
      [:div {}
       [sa/form-field
        [:label (or (:sui/label props) "Select variant")]
        [sa/form-select
         {:options   (for [[index child] (map-indexed vector children)]
                       {:key index :value index :text (str "Option " index)})
          :on-change (fn [event data]
                       (reset! selected-option (.-value data)))}]]
       [sa/form-group
        (nth children @selected-option)]])))

(defmethod compile-form :map [node]
  [sa/form-group
   (for [[attr props child] (:children node)]
     (with-meta (utils/set-label-if-unset child attr) {:key attr}))])

(defmethod compile-form :multi [{:keys [] :as node}]
  )

(defmethod compile-form :maybe [{[child] :children :as node}]
  (utils/assoc-attrs child :required false))

(defmethod compile-form :vector [{[child-template] :children :as node}]
  (let [children (reagent.core/atom [{:id (random-uuid) :node child-template}])]
    (fn []
      [:div {}
       (let [childs @children]
         (for [[index {:keys [id node]}] (map-indexed vector childs)]
           [sa/form-group {:inline true :key id}
            node
            [sa/button {:icon     true
                        :disabled (= 0 index)
                        :on-click (fn []
                                    (swap! children
                                           (fn [old]
                                             (utils/vec-swap old index (dec index)))))}
             [sa/icon {:name "arrow up" :size :small}]]
            [sa/button {:icon     true
                        :disabled (= (count childs) (inc index))
                        :on-click (fn []
                                    (swap! children
                                           (fn [old]
                                             (utils/vec-swap old index (inc index)))))}
             [sa/icon {:name "arrow down" :size :small}]]
            [sa/button {:icon     true
                        :on-click (fn []
                                    (swap! children
                                           (fn [old]
                                             (utils/vec-remove old index))))}
             [sa/icon {:name "trash" :size :small}]]]))
       [sa/button {:icon     true
                   :on-click (fn []
                               (swap! children
                                      (fn [old]
                                        (conj old {:id   (random-uuid)
                                                   :node child-template}))))}
        [sa/icon {:name "plus" :size "small"}]]])))

(defmethod compile-form :enum [{:keys [props schema] :as node}]
  (let [raw-children (m/children schema)]
    [sa/form-field {:required (not (:optional props false))}
     [sa/form-select
      {:options (for [child raw-children]
                  {:key child :value child :text child})}]]))

(defmethod compile-form := [{:keys [schema] :as node}]
  (let [[value] (m/children schema)]
    [sa/form-input {:value value :disabled true}]))

(defmethod compile-form :tuple [{:keys [children] :as node}]
  (into [sa/form-group {:inline true}]
        (for [[index child] (map-indexed vector children)]
          ^{:key index} child)))

(defn simple-input [{:keys [props]} mixins]
  (let [all-attrs (utils/select-ns props :sui)]
    [sa/form-field
     (merge {:required (not (:optional props false))} (dissoc all-attrs :label))
     (when-some [label (:label all-attrs)] [:label label])
     [sa/form-input mixins]]))

(defmethod compile-form :re [node]
  (simple-input node {:type :text}))

(defmethod compile-form 'string? [node]
  (simple-input node {:type :text}))

(defmethod compile-form 'int? [node]
  (simple-input node {:type :number}))

(defmethod compile-form 'integer? [node]
  (simple-input node {:type :number}))

(defmethod compile-form 'boolean? [node]
  (simple-input node {:type :checkbox}))

(defmethod compile-form 'pos-int? [node]
  (simple-input node {:type :number :min 1}))

(defmethod compile-form 'neg-int? [node]
  (simple-input node {:type :number :max -1}))

(defmethod compile-form 'nat-int? [node]
  (simple-input node {:type :number :min 0}))

(defmethod compile-form 'uri? [node]
  (simple-input node {:type :url}))

(defmethod compile-form 'inst? [node]
  (simple-input node {:type :datetime-local}))

(defn compile-form-wrapper [schema children path options]
  (let [schema-name  (m/name schema)
        schema-props (m/properties schema)]
    (compile-form
      {:name     schema-name
       :schema   schema
       :props    schema-props
       :children children
       :path     path
       :options  options})))

(defn schema->form
  ([schema]
   (schema->form schema {}))
  ([schema data]
   (let [result (m/accept schema compile-form-wrapper)]
     (if (fn? result)
       [sa/form {:warning true :error true} [result]]
       [sa/form {:warning true :error true} result]))))

(defn data->form
  ([data]
   (schema->form (mp/provide [data]) data)))