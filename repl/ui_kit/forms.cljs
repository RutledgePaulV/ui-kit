(ns ui-kit.forms
  (:require-macros
    [devcards.core :refer [defcard-rg defcard]])
  (:require [devcards.core :as cards]
            [ui-kit.semantic :as sa]
            [ui-kit.components :as com]
            [reagent.core :as r]))

(defcard-rg string-field
  (fn [data-atom _]
    [com/inspectable data-atom
     [string? {:sui/label "Text"}]])
  (r/atom ""))

(defcard-rg boolean-field
  (fn [data-atom _]
    [com/inspectable data-atom
     [boolean? {:sui/label "Boolean"}]])
  (r/atom false))

(defcard-rg regex-field
  (fn [data-atom _]
    [com/inspectable data-atom
     [:re {:sui/label "Regex field"} #"test"]])
  (r/atom ""))

(defcard-rg enum-field
  (fn [data-atom _]
    [com/inspectable data-atom
     [:enum {:sui/label "Pet"} "Dogs" "Cats" "Birds"]])
  (r/atom nil))

(defcard-rg tuple
  (fn [data-atom _]
    [com/inspectable data-atom
     [:tuple
      [string? {:sui/label "Latitude"}]
      [string? {:sui/label "Longitude"}]]])
  (r/atom [nil nil]))

(defcard-rg datetime
  (fn [data-atom _]
    [com/inspectable data-atom
     [inst? {:sui/label "Instant"}]])
  (r/atom nil))

(defcard-rg vector
  (fn [data-atom _]
    [com/inspectable data-atom
     [:vector {:sui/label "Grocery List"}
      [string? {:sui/label "Item"}]]])
  (r/atom []))

(defcard-rg predetermined
  (fn [data-atom _]
    [com/inspectable data-atom [:= {:sui/label "Read only"} "Testing"]])
  (r/atom "Testing"))

(defcard-rg anding
  (fn [data-atom _]
    [com/inspectable data-atom
     [:and
      [string? {:sui/label "Text"}]
      [int? {:sui/label "Number"}]]])
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

(defcard-rg large
  (fn [data-atom _]
    [com/inspectable data-atom
     [:map
      [:first-name string?]
      [:last-name string?]
      [:properties
       [:vector
        [:map
         [:address string?]
         [:city string?]
         [:state [:enum "Alabama" "Alaska" "Arizona" "Arkansas"]]
         [:zip int?]
         [:quotes
          [:vector
           [:map
            [:amount int?]]]]]]]]])
  (r/atom {}))