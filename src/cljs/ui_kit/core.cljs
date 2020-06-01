(ns ui-kit.core
  (:require [malli.core :as malli]
            [ui-kit.semantic :as sa]
            [ui-kit.visitors :as vis]))


(defn schema->form* [schema root]
  [sa/form {:error true :warning true}
   [vis/visit (malli/schema schema) root {}]])