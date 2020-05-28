(ns ui-kit.components
  (:require [ui-kit.semantic :as sa]
            [devcards.util.edn-renderer :as edn-render]
            [cljs.reader :as edn]
            [ui-kit.utils :as utils]
            [ui-kit.walkers :as walkers]
            [clojure.string :as strings]
            [ui-kit.visitors :as visit]
            [reagent.core :as r]
            [malli.core :as malli]))

(defn edn-viewer [edn]
  [:div.com-rigsomelight-rendered-edn.com-rigsomelight-devcards-typog
   {:key "devcards-edn-block"}
   (binding [edn-render/*key-counter* (atom 0)]
     (edn-render/html edn))])

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
            [visit/schema2->form schema ratom]
            (= :data @state)
            [edn-viewer @ratom]
            (= :source @state)
            [edn-viewer
             `[visit/schema->form
               ~(malli/form (malli/schema schema))
               ~(deref ratom)]]
            (= :expanded @state)
            [edn-viewer
             (->> (visit/schema->form schema @ratom)
                  (walkers/walk-expand)
                  (utils/ppstr)
                  (edn/read-string {:default tagged-literal}))])]]]))))

