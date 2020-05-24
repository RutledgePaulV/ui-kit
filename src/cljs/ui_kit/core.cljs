(ns ui-kit.core
  (:require [ui-kit.semantic :as sa]
            [malli.provider :as mp]
            [malli.core :as m]))


(defn dispatch-fun [form]
  (:name form))

(defmulti compile-form
  (fn [& args] (apply dispatch-fun args)))

(defmethod compile-form :default [{:keys [name] :as node}]
  node)

(defmethod compile-form :and [{:keys [name] :as node}]
  )

(defmethod compile-form :or [{:keys [name] :as node}]
  )

(defmethod compile-form :map [{:keys [name children] :as node}]
  [sa/form-group
   (for [[index [attr props child]] (map-indexed vector children)]
     [sa/form-field {:key index}
      [:label attr]
      child])])

(defmethod compile-form :multi [{:keys [name] :as node}]
  )

(defmethod compile-form :enum [{:keys [name path children] :as node}]
  [sa/form-select
   {:options (for [child children]
               {:key child :value child :text child})}])

(defmethod compile-form :tuple [{:keys [name children] :as node}]
  [sa/form-group {:inline true :widths :equal}
   (for [[index child] (map-indexed vector children)]
     [sa/form-field {:key index} child])])

(defmethod compile-form :re [node]
  [sa/form-input {:type :text}])

(defmethod compile-form 'string? [node]
  [sa/form-input {:type :text}])

(defmethod compile-form 'int? [node]
  [sa/form-input {:type :number}])

(defmethod compile-form 'double? [node]
  [sa/form-input {:type :number}])

(defmethod compile-form 'boolean? [node]
  [sa/form-input {:type :checkbox}])

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
   (m/accept schema compile-form-wrapper)))

(defn data->form
  ([data]
   (schema->form (mp/provide [data]) data)))