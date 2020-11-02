(defproject data-mining-experiments "0.0.0"
  :description "A bunch of data mining experiments for my IS665 course at Pace University"
  :url "http://pace.saagermhatre.in/is665/"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/data.csv "1.0.0"]
                 [clj-genetic "0.3.0" :exclusions [org.clojure/clojure]]
                 [kmeans-clj "0.1.0-SNAPSHOT"]
                 [org.jfree/jfreechart "1.5.1"]]
  :main ^:skip-aot clojure.main
  :source-paths ["src/main/clojure"]
  :resource-paths ["src/main/resources"]
  :target-path "target/%s")
