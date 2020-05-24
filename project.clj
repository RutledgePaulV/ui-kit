(defproject ui-kit "0.1.0-SNAPSHOT"

  :dependencies
  [[org.clojure/clojure "1.10.1"]
   [org.clojure/clojurescript "1.10.764"]
   [cljsjs/semantic-ui-react "0.88.1-0"]
   [metosin/malli "master"]
   [reagent "0.10.0"]]

  :plugins
  [[reifyhealth/lein-git-down "0.3.6"]]

  :middleware
  [lein-git-down.plugin/inject-properties]

  :repositories
  [["public-github" {:url "git://github.com"}]]

  :source-paths
  ["src/clj" "src/cljc" "src/cljs"]

  :test-paths
  ["test/clj" "test/cljs"]

  :repl-options
  {:init-ns ui-kit.core})