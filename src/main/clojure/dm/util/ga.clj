(ns dm.util.ga
  (:require
    [clj-genetic.core :as ga]
    [clj-genetic.crossover :as crossover]
    [clj-genetic.mutation :as mutation]
    [clj-genetic.objective :as objective]
    [clj-genetic.random-generators :as random-generators]
    [clj-genetic.recombination :as recombination]
    [clj-genetic.selection :as selection]
    [dm.viz.chart :as chart]
    [incanter.stats :as i])
  (:import
    (org.jfree.chart ChartFactory)
    (org.jfree.data.function Function2D)
    (org.jfree.data.general DatasetUtils)
    (org.jfree.data.statistics BoxAndWhiskerCategoryDataset DefaultBoxAndWhiskerCategoryDataset)
    (org.jfree.data.xy XYSeriesCollection XYSeries)))

(def ^:private individual-fitness (comp :fitness meta))

(defn- console-logger [population generation]
  (let [fitness (map individual-fitness population)]
    (printf "Generation: %3d | Mean fitness: %.6f | Best: %.6f %n" generation (i/mean fitness) (apply max fitness))
    (flush)))

(def ^:private individual-series-key "Individual")

(defmethod chart/chart XYSeriesCollection [dataset]
  (let [individual-series (.getSeriesIndex dataset individual-series-key)]
    (doto (ChartFactory/createXYLineChart "Curve of function" "X" "Y" dataset)
      (->
        (.getXYPlot)
        (.getRenderer)
        (doto
          (.setSeriesLinesVisible individual-series false)
          (.setSeriesShapesVisible individual-series true))))))

(defmethod chart/chart BoxAndWhiskerCategoryDataset [dataset]
  (ChartFactory/createBoxAndWhiskerChart "Population Analysis" "Generation" "Fitness" dataset false))

(defn- ->Function2D [f]
  (reify Function2D (getValue [_ x] (f x))))

(defn- sample [f min max samples]
  (DatasetUtils/sampleFunction2DToSeries (->Function2D f) min max samples "Value"))

(defn- set-individuals [series population]
  (.clear series)
  (doseq [[x :as individual] population]
    (.add series x (individual-fitness individual) false))
  (.fireSeriesChanged series))

(defn- log-generation
  ([generation-series analysis-dataset population generation-number]
   (set-individuals generation-series population)
   (log-generation analysis-dataset population generation-number))
  ([analysis-dataset population generation-number]
   (.add analysis-dataset (map individual-fitness population) "Fitness" generation-number)
   (Thread/sleep 500)))

(defn- terminator [frame]
  (fn [_ _] (not (.isVisible frame))))

(defn make-chart-logger
  ([]
   (let [analysis-dataset (DefaultBoxAndWhiskerCategoryDataset.)]
     [(terminator (chart/frame "Population Analysis" analysis-dataset))
      (partial log-generation analysis-dataset)]))
  ([f min max samples]
   (let [analysis-dataset (DefaultBoxAndWhiskerCategoryDataset.)
         generation-series (XYSeries. individual-series-key)
         sample-dataset (doto (XYSeriesCollection. generation-series)
                          (.addSeries (sample f min max samples)))]
     [(terminator (chart/frame "Population Analysis" sample-dataset analysis-dataset))
      (partial log-generation generation-series analysis-dataset)])))

(defn- spread-logger [& loggers]
  (fn [p g]
    (doseq [l loggers] (l p g))))

(defn- preserving-fittest [f]
  (fn [& args]
    (let [population (last args)
          fittest (apply max-key individual-fitness population)
          new-pop (apply f args)]
      (if (.contains new-pop fittest)
        new-pop
        (cons fittest (rest new-pop))))))

(defn run
  ([objective limits]
   (let [[frame-visible? chart-logger] (make-chart-logger)]
     (run objective limits (spread-logger console-logger chart-logger) frame-visible?)))
  ([objective limits logger terminator]
   (let [max-generations 50]
     (ga/run
       objective
       (preserving-fittest selection/binary-tournament-without-replacement)
       (preserving-fittest (partial recombination/crossover-mutation
                                    (partial crossover/simulated-binary-with-limits limits)
                                    (partial mutation/parameter-based-with-limits limits max-generations)))
       (fn [& arg] ((some-fn
                      (partial apply terminator)
                      (partial apply (ga/terminate-max-generations? max-generations))) arg))
       (random-generators/generate-population 30 limits)
       logger))))

(defn maximize [f & options] (apply run (cons (objective/maximize f) options)))
(defn minimize [f & options] (apply run (cons (objective/minimize f) options)))
