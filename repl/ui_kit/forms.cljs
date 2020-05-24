(ns ui-kit.forms
  (:require-macros
    [devcards.core :refer [defcard-rg]])
  (:require [devcards.core :as cards]
            [ui-kit.semantic :as sa]
            [ui-kit.core :as ui]))


(def Address
  [:map {:attrs 1}
   [:id string?]
   [:tags [:set keyword?]]
   [:address
    [:map
     [:street string?]
     [:city string?]
     [:zip int?]
     [:lonlat [:tuple double? double?]]]]])


(defcard-rg address-form
  [ui/schema->form Address])