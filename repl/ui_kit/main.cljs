(ns ui-kit.main
  (:require-macros
    [devcards.core :refer [defcard-rg]])
  (:require [devcards.core :as cards]
            [ui-kit.semantic :as sa]
            [ui-kit.core :as ui]))



(defcard-rg my-first-card
  [sa/message {:info true} "Test"])



(cards/start-devcard-ui!*)