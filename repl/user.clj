(ns user
  (:require [figwheel.main.api :as fig]))


(defn frontend []
  (fig/start
    {:id      "dev"
     :options {:main 'ui-kit.main :devcards true}
     :config  {:watch-dirs ["src/cljc" "src/cljs" "repl"]}}))
