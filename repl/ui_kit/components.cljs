(ns ui-kit.components
  (:require-macros
    [devcards.core :refer [defcard-rg]])
  (:require [devcards.core :as cards]
            [ui-kit.semantic :as sa]
            [ui-kit.core :as ui]))


(defcard-rg error-message
  [sa/message {:error true} "Test"])

