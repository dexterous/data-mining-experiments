(ns evolution-programs.logging.console
   (:require [evolution-programs.util.population-statistic :as stat]))

(defn- log-generation [population generation]
  (printf "Generation: %3d | Mean fitness: %.6f | Best: %.6f %n"
          generation (stat/mean-fitness population) (stat/best-fitness population))
  (flush))

(defn logger [] log-generation)