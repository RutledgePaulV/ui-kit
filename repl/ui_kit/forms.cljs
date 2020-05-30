(ns ui-kit.forms
  (:require-macros
    [devcards.core :refer [defcard-rg defcard]])
  (:require [devcards.core :as cards]
            [ui-kit.semantic :as sa]
            [ui-kit.core :as ui]
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
    [com/inspectable data-atom
     [string? {:sui/label "Text"}]])
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

(defcard-rg uri
  (fn [data-atom _]
    [com/inspectable data-atom
     [uri? {:sui/label "Uri"}]])
  (r/atom nil))

(defcard-rg vector
  (fn [data-atom _]
    [com/inspectable data-atom
     [:vector {:sui/label "Grocery List"} string?]])
  (r/atom []))

(defcard-rg predetermined
  (fn [data-atom _]
    [com/inspectable data-atom [:= "Testing"]])
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
          [:map
           [:amount int?]
           [:timestamp inst?]]]]]]]])
  (r/atom {}))

(defcard-rg inferred-schema
  "## Inferred Schemas

  Since Malli can infer schemas, you can generate a form given only the data
  you want to generate the form for.

  ```clojure
  [ui/data->form
    {:street \"131 W Herring Drive\"
     :city   \"Chicago\"
     :zip    60632
     :state  \"Illinois\"}]
  ```

  ---"
  (fn [data-atom _]
    [ui/data->form* data-atom])
  (r/atom {:street "131 W Herring Drive"
           :city   "Chicago"
           :zip    60632
           :state  "Illinois"}))

(defcard-rg generators
  "## Generators

   Since Malli has generators it's trivial to create a form with random data.

   ```clojure
   [ui/sample-form
     [:vector
       [:map
        [:street string?]
        [:zip pos-int?]]]]
   ```
   ---"
  (fn [data-atom _]
    [sa/segment
     [sa/button {:icon true :on-click #(swap! data-atom inc)}
      [sa/icon {:name "refresh"}]]
     [sa/divider {:hidden true}]
     [ui/sample-form
      [:vector
       [:map
        [:street string?]
        [:zip [pos-int? {:counter @data-atom}]]]]]])
  (r/atom 0))