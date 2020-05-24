(ns ui-kit.forms
  (:require-macros
    [devcards.core :refer [defcard-rg]])
  (:require [devcards.core :as cards]
            [ui-kit.semantic :as sa]
            [ui-kit.core :as ui]))


(defcard-rg string-field
  [ui/schema->form string?])

(defcard-rg inline-label
  [ui/schema->form [string? {:sui/label "My Label"}]])

(defcard-rg int-field
  [ui/schema->form int?])

(defcard-rg boolean-field
  [ui/schema->form [boolean? {:sui/label "Do you like cheese?"}]])

(defcard-rg enum-field
  [ui/schema->form [:enum "Dogs" "Cats" "Birds"]])

(defcard-rg tuple
  [ui/schema->form [:tuple string? string?]])

(defcard-rg datetime
  [ui/schema->form inst?])

(defcard-rg uri
  [ui/schema->form uri?])

(defcard-rg not-understood
  [ui/schema->form
   [:map
    [:place
     [:map
      [:value zero?]
      [:value2 string?]]]]])

(defcard-rg vector
  [ui/schema->form [:vector string?]])

(defcard-rg predetermined
  [ui/schema->form [:= "Testing"]])

(defcard-rg anding
  [ui/schema->form [:and string? int?]])

(defcard-rg oring
  [ui/schema->form
   [:or
    [:map
     [:address string?]]
    [:map
     [:anonymous boolean?]]]])

(defcard-rg address-form
  [ui/schema->form
   [:map
    [:street string?]
    [:city string?]
    [:zip int?]
    [:state [:enum "Illinois" "Minnesota"]]]])

(defcard-rg multi-address
  [ui/schema->form
   [:vector
    [:map
     [:street string?]
     [:city string?]
     [:state [:enum "Illinois" "Minnesota"]]]]])

(defcard-rg data->form
  [ui/data->form
   {:street "131 W Herring Drive"
    :city   "Chicago"
    :zip    60632
    :state  "Illinois"}])