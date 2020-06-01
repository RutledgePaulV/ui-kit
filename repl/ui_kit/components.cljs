(ns ui-kit.components
  (:require [ui-kit.semantic :as sa]
            [devcards.util.edn-renderer :as edn-render]
            [ui-kit.utils :as utils]
            [ui-kit.walkers :as walkers]
            [clojure.string :as strings]
            [ui-kit.core :as ui]
            [reagent.core :as r]
            [malli.core :as malli]))

(defn edn-viewer [edn]
  [:div.com-rigsomelight-rendered-edn.com-rigsomelight-devcards-typog
   {:key "devcards-edn-block"}
   (binding [edn-render/*key-counter* (atom 0)]
     (edn-render/html (utils/prettify edn)))])

(defn inspectable
  ([ratom schema]
   (let [state (r/atom :visual)]
     (fn []
       [sa/grid
        [sa/grid-column {:width 2}
         [sa/menu {:fluid true :vertical true :tabular true}
          (doall
            (for [item [:visual :data :source :expanded]]
              [sa/menu-item
               {:key     item
                :name    (name item)
                :active  (= item @state)
                :onClick #(reset! state item)}
               (strings/capitalize (name item))]))]]
        [sa/grid-column {:width 14 :stretched true}
         [sa/segment
          (cond
            (= :visual @state)
            [ui/schema->form* schema ratom]
            (= :data @state)
            [edn-viewer @ratom]
            (= :source @state)
            [edn-viewer
             `[ui/schema->form*
               ~(malli/form (malli/schema schema))
               ~(list 'reagent.core/atom @ratom)]]
            (= :expanded @state)
            [edn-viewer
             (->> [ui/schema->form* schema ratom]
                  (walkers/walk-expand))])]]]))))

