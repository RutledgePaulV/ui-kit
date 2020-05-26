(ns user
  (:require [figwheel.main.api :as fig]
            [clojure.java.io :as io])
  (:import (java.io File)
           (java.nio.file Path)))

(def BUILD_ID "devcards")

(defn frontend []
  (fig/start
    {:id      BUILD_ID
     :options {:main 'ui-kit.main :devcards true}
     :config  {:watch-dirs ["src/cljc" "src/cljs" "repl"]}}))

(defn tail-recursive-delete
  [& fs]
  (when-let [f (first fs)]
    (if-let [cs (seq (.listFiles (io/file f)))]
      (recur (concat cs fs))
      (do (io/delete-file f true)
          (recur (rest fs))))))

(defn copy [source target]
  (let [source-f (io/file source)
        target-f (io/file target)]
    (io/make-parents target-f)
    (if (.isFile source-f)
      (with-open [in  (io/input-stream source-f)
                  out (io/output-stream target-f)]
        (io/copy in out))
      (let [source-path (.toPath source-f)
            target-path (.toPath target-f)]
        (doseq [^File source-file (remove (partial identical? source-f) (file-seq source-f))]
          (let [relative-to-source (.relativize source-path (.toPath source-file))
                relative-to-target (.resolve target-path relative-to-source)
                target-file        (.toFile relative-to-target)]
            (copy source-file target-file)))))))


(defn rebuild-docs []
  (try
    (tail-recursive-delete "docs" (io/file "target"))
    (fig/start
      {:id      BUILD_ID
       :options {:main 'ui-kit.main :devcards true :optimizations :advanced}
       :config  {:mode       :serve
                 :watch-dirs ["src/cljc" "src/cljs" "repl"]}})
    (copy (io/file "resources/public/index.html") (io/file "docs/index.html"))
    (copy (io/file "target/public/cljs-out/devcards-main.js") (io/file "docs/cljs-out/devcards-main.js"))
    (finally
      (fig/stop-all))))

