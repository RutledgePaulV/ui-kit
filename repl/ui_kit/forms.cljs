(ns ui-kit.forms
  (:require-macros
    [devcards.core :refer [defcard-rg defcard]])
  (:require [devcards.core :as cards]
            [ui-kit.semantic :as sa]
            [ui-kit.visitors :as ui]
            [ui-kit.components :as com]
            [reagent.core :as r]
            [ui-kit.walkers :as walkers]
            [cljs.pprint :as pprint]))

(defn view-markup [schema data]
  (->> (ui/schema->form schema data)
       (walkers/walk-expand)
       (pprint/pprint)))

(defcard-rg string-field
  (fn [data-atom _]
    [com/inspectable data-atom string?])
  (r/atom ""))

(defcard-rg enum-field
  (fn [data-atom _]
    [com/inspectable data-atom [:enum "Dogs" "Cats" "Birds"]])
  (r/atom nil))

(defcard-rg tuple
  (fn [data-atom _]
    [com/inspectable data-atom [:tuple string? string?]])
  (r/atom [nil nil]))

(defcard-rg datetime
  (fn [data-atom _]
    [com/inspectable data-atom inst?])
  (r/atom nil))

(defcard-rg uri
  (fn [data-atom _]
    [com/inspectable data-atom uri?])
  (r/atom nil))

(defcard-rg not-understood
  (fn [data-atom _]
    [com/inspectable data-atom
     [:map
      [:place
       [:map
        [:value zero?]
        [:value2 string?]]]]])
  (r/atom {}))
;
(defcard-rg vector
  (fn [data-atom _]
    [com/inspectable data-atom [:vector string?]])
  (r/atom []))

(defcard-rg predetermined
  (fn [data-atom _]
    [com/inspectable data-atom [:= "Testing"]])
  (r/atom "Testing"))

(defcard-rg anding
  (fn [data-atom _]
    [com/inspectable data-atom [:and string? int?]])
  (r/atom nil))

(defcard-rg oring
  (fn [data-atom _]
    [com/inspectable data-atom
     [:or
      [:map
       [:address string?]]
      [:map
       [:anonymous boolean?]]]])
  (r/atom {}))
;
(defcard-rg multi
  (fn [data-atom _]
    [com/inspectable data-atom
     [:multi {:dispatch :type}
      [:cat [:map
             [:type [:= :cat]]
             [:sound [:enum "meow" "hiss" "silence"]]]]
      [:dog [:map
             [:type [:= :dog]]
             [:sound [:enum "bark" "growl" "silence"]]]]]])
  (r/atom {}))

(defcard-rg maps
  (fn [data-atom _]
    [com/inspectable data-atom
     [:map
      [:street string?]
      [:city string?]
      [:zip int?]
      [:state [:enum "Illinois" "Minnesota"]]]])
  (r/atom {}))

(defcard-rg vector-of-maps
  (fn [data-atom _]
    [com/inspectable data-atom
     [:vector
      [:map
       [:street string?]
       [:city string?]
       [:state [:enum "Illinois" "Minnesota"]]]]])
  (r/atom [{:street "131 W Herring Drive"}]))

(defcard-rg data->form
  (fn [data-atom _]
    [com/inspectable data-atom
     [:map
      [:street string?]
      [:city string?]
      [:zip int?]
      [:state [:enum "Illinois" "Minnesota"]]]])
  (r/atom {:street "131 W Herring Drive"
           :city   "Chicago"
           :zip    60632
           :state  "Illinois"}))