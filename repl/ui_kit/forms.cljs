(ns ui-kit.forms
  (:require-macros
    [devcards.core :refer [defcard-rg defcard]])
  (:require [devcards.core :as cards]
            [ui-kit.semantic :as sa]
            [ui-kit.visitors :as ui]
            [ui-kit.components :as com]
            [reagent.core :as r]))

(defcard-rg string-field
  (fn [data-atom _]
    [com/inspectable data-atom string?])
  (r/atom ""))

(defcard-rg enum-field
  (fn [data-atom _]
    [com/inspectable data-atom [:enum "Dogs" "Cats" "Birds"]])
  (r/atom nil))

;(defcard-rg tuple
;  [com/inspectable [:tuple string? string?]])
;
;(defcard-rg datetime
;  [com/inspectable inst?])
;
;(defcard-rg uri
;  [com/inspectable uri?])
;
;(defcard-rg not-understood
;  [com/inspectable
;   [:map
;    [:place
;     [:map
;      [:value zero?]
;      [:value2 string?]]]]])
;
;(defcard-rg vector
;  [com/inspectable [:vector string?]])
;
;(defcard-rg predetermined
;  [com/inspectable [:= "Testing"]])
;
;(defcard-rg anding
;  [com/inspectable [:and string? int?]])
;
;(defcard-rg oring
;  [com/inspectable
;   [:or
;    [:map
;     [:address string?]]
;    [:map
;     [:anonymous boolean?]]]])
;
;(defcard-rg multi
;  [com/inspectable
;   [:multi {:dispatch :type}
;    [:cat [:map
;           [:type [:= :cat]]
;           [:sound [:enum "meow" "hiss" "silence"]]]]
;    [:dog [:map
;           [:type [:= :dog]]
;           [:sound [:enum "bark" "growl" "silence"]]]]]])
;
;(defcard-rg maps
;  [com/inspectable
;   [:map
;    [:street string?]
;    [:city string?]
;    [:zip int?]
;    [:state [:enum "Illinois" "Minnesota"]]]])
;
;(defcard-rg vector-of-maps
;  [com/inspectable
;   [:vector
;    [:map
;     [:street string?]
;     [:city string?]
;     [:state [:enum "Illinois" "Minnesota"]]]]
;   [{:street "131 W Herring Drive"}]])
;
;(defcard-rg data->form
;  [com/inspectable
;   [:map
;    [:street string?]
;    [:city string?]
;    [:zip int?]
;    [:state [:enum "Illinois" "Minnesota"]]]
;   {:street "131 W Herring Drive"
;    :city   "Chicago"
;    :zip    60632
;    :state  "Illinois"}])