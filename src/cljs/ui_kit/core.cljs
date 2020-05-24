(ns ui-kit.core
  (:require [ui-kit.semantic :as sa]
            [malli.core :as m]))

(defmulti compile-form
  (fn [schema children path options]
    (m/name schema)))

(defmethod compile-form :and [_ children _ _]
  )

(defmethod compile-form :or [_ children _ _]
  )

(defmethod compile-form :map [_ children _ _]
  )

(defmethod compile-form :multi [_ children _ _]
  )

(defmethod compile-form :enum [_ children _ _]
  )

(defn schema->form [schema form-attrs]
  (m/accept schema compile-form))