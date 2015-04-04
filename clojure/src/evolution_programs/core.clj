(ns evolution-programs.core
  (:require [clj-genetic.core :as ga]
            [clj-genetic.objective :as objective]
            [clj-genetic.selection :as selection]
            [clj-genetic.recombination :as recombination]
            [clj-genetic.mutation :as mutation]
            [clj-genetic.crossover :as crossover]
            [clj-genetic.random-generators :as random-generators]
            [evolution-programs.console :as console]
            [evolution-programs.chart :as chart]))

(defn- spread-logger [& loggers]
  (fn [p g]
    (doseq [l loggers] (l p g))))

(defn run
  ([objective limits]
   (run objective limits (spread-logger (console/logger) (chart/logger))))
  ([objective limits logger]
   (ga/run
     objective
     selection/binary-tournament-without-replacement
     (partial recombination/crossover
              (partial crossover/simulated-binary-with-limits limits))
     (ga/terminate-max-generations? 20)
     (random-generators/generate-population 20 limits)
     logger)))

(defn maximize [f & options] (apply run (cons (objective/maximize f) options)))
