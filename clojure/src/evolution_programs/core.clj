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

(defn preserving-fittest [f]
  (fn [& args]
    (let [population (last args)
          fittest (apply max-key #(:fitness (meta %)) population)
          new-pop (apply f args)]
      (if (.contains new-pop fittest)
        new-pop
        (cons fittest (rest new-pop))))))

(defn run
  ([objective limits]
   (run objective limits (spread-logger (console/logger) (chart/logger))))
  ([objective limits logger]
   (let [max-generations 50]
     (ga/run
       objective
       (preserving-fittest selection/binary-tournament-without-replacement)
       (preserving-fittest (partial recombination/crossover-mutation
                                    (partial crossover/simulated-binary-with-limits limits)
                                    (partial mutation/parameter-based-with-limits limits max-generations)))
       (ga/terminate-max-generations? max-generations)
       (random-generators/generate-population 30 limits)
       logger))))

(defn maximize [f & options] (apply run (cons (objective/maximize f) options)))
