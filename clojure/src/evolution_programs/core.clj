(ns evolution-programs.core
  (:require [clj-genetic.core :as ga]
            [clj-genetic.objective :as objective]
            [clj-genetic.selection :as selection]
            [clj-genetic.recombination :as recombination]
            [clj-genetic.mutation :as mutation]
            [clj-genetic.crossover :as crossover]
            [clj-genetic.random-generators :as random-generators]))

(defn fitness [population] (map #(:fitness (meta %)) population))
(defn fittest [population] (apply max (fitness population)))
(defn mean-fitness [population]
  (let [fitness (fitness population)
        n (count fitness)]
    (/ (apply + fitness) n)))

(defn log-generation [population generation]
  (printf "Generation: %3d | Mean fitness %.6f | Best: %.6f %n"
          generation (mean-fitness population) (fittest population)))

(defn run [objective limits]
  (ga/run
    objective
    selection/binary-tournament-without-replacement
    (partial recombination/crossover 
             (partial crossover/simulated-binary-with-limits limits))
    (ga/terminate-max-generations? 20)
    (random-generators/generate-population 20 limits)
    log-generation))

(defn maximize [f limits] (run (objective/maximize f) limits))
