(ns dm.expt.two-param
  (:require
    [dm.util.ga :as ga]))

(defn f [x y]
  (+ 21.5
     (* x
        (Math/sin (* 4 Math/PI x)))
     (* y
        (Math/sin (* 20 Math/PI y)))))

(def limits [{:min -3.0 :max 12.1} {:min 4.1 :max 5.8}])

(defn -main [& args]
  (ga/maximize f limits))
