(defproject ui-kit "0.1.0-SNAPSHOT"

  :dependencies
  [[org.clojure/clojure "1.10.1"]
   [org.clojure/clojurescript "1.10.773"]
   [cljsjs/semantic-ui-react "0.88.1-0"]
   [cljsjs/semantic-ui-calendar-react "0.15.3-0"]
   [metosin/malli "a4b3ccd198cea6e3f3de77afabc9181a9ed523b5"]
   [reagent "0.10.0"]]

  :plugins
  [[reifyhealth/lein-git-down "0.3.6"]]

  :middleware
  [lein-git-down.plugin/inject-properties]

  :profiles
  {:dev {:source-paths   ["repl"]
         :dependencies   [[com.bhauman/figwheel-main "0.2.9"]
                          [cider/piggieback "0.5.0"]
                          [devcards "0.2.7"]]
         :resource-paths ["target"]
         :repl-options   {:init-ns          user
                          :nrepl-middleware [cider.piggieback/wrap-cljs-repl]}}}

  :repositories
  [["public-github" {:url "git://github.com"}]]

  :source-paths
  ["src/clj" "src/cljc" "src/cljs"]

  :test-paths
  ["test/clj" "test/cljs"])
