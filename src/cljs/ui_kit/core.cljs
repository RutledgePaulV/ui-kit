(ns ui-kit.core
  (:require [ui-kit.semantic :as sa]
            [ui-kit.utils :as utils]
            [malli.provider :as mp]
            [malli.core :as m]))


(defn dispatch-fun [form]
  (:name form))

(defmulti compile-form
  (fn [& args] (apply dispatch-fun args)))

(defmethod compile-form :default [{:keys [] :as node}]
  node)

(defmethod compile-form :and [{:keys [children] :as node}]
  (into [sa/form-group {}] children))

(defmethod compile-form :or [{:keys [children] :as node}]
  (let [selected-option (reagent.core/atom 0)]
    (fn []
      [sa/form-group
       [sa/form-select
        {:options   (for [[index child] (map-indexed vector children)]
                      {:key index :value index :text (str "Option " index)})
         :on-change (fn [event data]
                      (reset! selected-option (.-value data)))}]
       (nth children @selected-option)])))

(defmethod compile-form :map [{:keys [children] :as node}]
  [sa/form-group
   (for [[index [attr _ child]] (map-indexed vector children)]
     [sa/form-field {:key index} [:label attr] child])])

(defmethod compile-form :multi [{:keys [] :as node}]
  )

(defmethod compile-form :maybe [{:keys [] :as node}]
  )

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

(defmethod compile-form :enum [{:keys [schema] :as node}]
  (let [raw-children (m/children schema)]
    [sa/form-select
     {:options (for [child raw-children]
                 {:key child :value child :text child})}]))

(defmethod compile-form := [{:keys [schema] :as node}]
  (let [[value] (m/children schema)]
    [sa/form-input {:value value :disabled true}]))

(defmethod compile-form :tuple [{:keys [children] :as node}]
  [sa/form-group {:inline true :widths :equal}
   (for [[index child] (map-indexed vector children)]
     [sa/form-field {:key index} child])])

(defmethod compile-form :re [node]
  [sa/form-input {:type :text}])

(defmethod compile-form 'string? [node]
  [sa/form-input {:type :text}])

(defmethod compile-form 'int? [node]
  [sa/form-input {:type :number}])

(defmethod compile-form 'integer? [node]
  [sa/form-input {:type :number}])

(defmethod compile-form 'boolean? [node]
  [sa/form-input {:type :checkbox}])

(defmethod compile-form 'number? [node]
  [sa/form-input {:type :number}])

(defmethod compile-form 'pos-int? [node]
  [sa/form-input {:type :number :min 1}])

(defmethod compile-form 'neg-int? [node]
  [sa/form-input {:type :number :max -1}])

(defmethod compile-form 'nat-int? [node]
  [sa/form-input {:type :number :min 0}])

(defmethod compile-form 'double? [node]
  [sa/form-input {:type :number}])

(defmethod compile-form 'float? [node]
  [sa/form-input {:type :number}])

(defmethod compile-form 'ident? [node]
  [sa/form-input {:type :text}])

(defmethod compile-form 'simple-ident? [node]
  [sa/form-input {:type :text}])

(defmethod compile-form 'qualified-ident? [node]
  [sa/form-input {:type :text}])

(defmethod compile-form 'keyword? [node]
  [sa/form-input {:type :text}])

(defmethod compile-form 'simple-keyword? [node]
  [sa/form-input {:type :text}])

(defmethod compile-form 'qualified-keyword? [node]
  [sa/form-input {:type :text}])

(defmethod compile-form 'symbol? [node]
  [sa/form-input {:type :text}])

(defmethod compile-form 'simple-symbol? [node]
  [sa/form-input {:type :text}])

(defmethod compile-form 'qualified-symbol? [node]
  [sa/form-input {:type :text}])

(defmethod compile-form 'uuid? [node]
  [sa/form-input {:type :text}])

(defmethod compile-form 'uri? [node]
  [sa/form-input {:type :url}])

(defmethod compile-form 'decimal? [node]
  [sa/form-input {:type :text}])

(defmethod compile-form 'inst? [node]
  [sa/form-input {:type :datetime-local}])


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
       [sa/form {} [result]]
       [sa/form {} result]))))

(defn data->form
  ([data]
   (schema->form (mp/provide [data]) data)))