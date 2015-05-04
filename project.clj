(defproject evolution-programs "0.0.1"
  :description "A bunch of EP experiments for my IS665 course at Pace University"
  :url "http://pace.saagermhatre.in/is665/"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clj-genetic "0.3.0" :exclusions [org.clojure/clojure]]
                 [svm-clj "0.1.3"]
               #_[hjrnunes/clj-liblinear "0.1.4"]
               #_[de.bwaldvogel/liblinear "1.95"]
               #_[edu.berkeley.compbio/jlibsvm "0.911"] ;;http://dev.davidsoergel.com/nexus/content/repositories/{releases,snapshots}
                 [org.jfree/jfreechart "1.0.19"]]
  :main ^:skip-aot clojure.main
  :source-paths ["src/main/clojure"]
  :target-path "target/%s")
