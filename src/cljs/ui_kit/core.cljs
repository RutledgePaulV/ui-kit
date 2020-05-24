(ns ui-kit.core
  (:require [ui-kit.semantic :as sa]
            [malli.core :as m]))


(defn dispatch-fun [form]
  (println form)
  (:name form))

(defmulti compile-form
  (fn [& args] (apply dispatch-fun args)))

(defmethod compile-form :default [schema children path options]
  )

(defmethod compile-form :and [schema children path options]
  )

(defmethod compile-form :or [schema children path options]
  )

(defmethod compile-form :map [schema children path options]
  )

(defmethod compile-form :multi [schema children path options]
  )

(defmethod compile-form :enum [schema children path options]
  )

(defn compile-form-wrapper [schema children path options]
  (let [schema-name  (m/name schema)
        schema-props (m/properties schema)]
    (compile-form
      {:name     schema-name
       :props    schema-props
       :children children
       :path     path
       :options  options})))

(defn schema->form
  ([schema]
   (schema->form schema {}))
  ([schema attrs]
   [sa/form attrs (m/accept schema compile-form-wrapper)]))