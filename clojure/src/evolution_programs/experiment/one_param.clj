(ns evolution-programs.experiment.one-param
  (:require [evolution-programs.util.ga :as ga]
            [evolution-programs.logging.chart :as chart]))

(defn f [x]
  (+ (* x
        (Math/sin (* 10 Math/PI x)))
     1.0))

(def limits [{:min -1 :max 2}])

(defn -main [& args]
  (let [[terminator logger] (chart/logger f -1 2 1000)]
    (ga/maximize f limits logger terminator)))
