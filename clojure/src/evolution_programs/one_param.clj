(ns evolution-programs.one-param
  (:require [evolution-programs.core :as core])
  (:gen-class))

(defn f [x]
  (+ (* x
        (Math/sin (* 10 Math/PI x)))
     1.0))

(def limits [{:min -1 :max 2}])

(defn -main [& args]
  (println (core/maximize f limits)))
