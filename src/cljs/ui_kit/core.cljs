(ns ui-kit.core
  (:require [malli.generator :as mg]
            [malli.provider :as mp]
            [reagent.core :as r]
            [malli.core :as malli]
            [ui-kit.semantic :as sa]
            [ui-kit.visitors :as vis]))


(defn schema->form* [schema root]
  [sa/form {:error true :warning true}
   [vis/visit (malli/schema schema) root {}]])

(defn schema->form [schema data]
  [schema->form* schema (r/atom data)])

(defn data->form
  ([data]
   (data->form data data))
  ([data-for-schema data-to-fill]
   (schema->form (mp/provide [data-for-schema]) data-to-fill)))

(defn sample-form [schema]
  (let [data (mg/generate schema {:size 10})]
    [schema->form schema data]))