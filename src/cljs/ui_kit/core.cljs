(ns ui-kit.core
  (:require [ui-kit.semantic :as sa]
            [malli.core :as m]))

(defmulti compile-form
  (fn [schema children path options]
    (m/name schema)))

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

(defn schema->form [schema form-attrs]
  (m/accept schema compile-form))