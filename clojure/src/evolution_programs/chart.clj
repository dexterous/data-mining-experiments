(ns evolution-programs.chart
  (:require [evolution-programs.population-statistic :as stat])
  (:import [org.jfree.chart ChartFactory ChartPanel StandardChartTheme]
           [org.jfree.data.category CategoryDataset DefaultCategoryDataset]
           [org.jfree.data.function Function2D]
           [org.jfree.data.general DatasetUtilities]
           [org.jfree.data.xy XYDataset]
           [org.jfree.ui ApplicationFrame]))

(ChartFactory/setChartTheme (StandardChartTheme. "JFree/Shadow" true))

(defmulti ^:private chart class)

(defmethod chart XYDataset [dataset]
  (ChartFactory/createXYLineChart "Curve of function" "X" "Y" dataset))

(defmethod chart CategoryDataset [dataset]
  (doto (ChartFactory/createLineChart "Population Analysis" "Generation" "Fitness" dataset)
    (->
      (.getCategoryPlot)
      (.getRenderer)
      (.setShapesVisible true))))

(defn- panel [chart]
  (doto (ChartPanel. chart)
    (.setMouseWheelEnabled true)))

(defn frame [title dataset]
  (doto (ApplicationFrame. title)
    (.setContentPane (panel (chart dataset)))
    (.pack)
    (.setVisible true)))

(defn log-generation [dataset population generation]
  (.addValue dataset (stat/mean-fitness population) "Mean" generation)
  (.addValue dataset (stat/best-fitness population) "Best" generation)
  (Thread/sleep 200))

(defn logger []
  (let [dataset (DefaultCategoryDataset.)]
    (frame  "Population Analysis" dataset)
    (partial log-generation dataset)))

(defn sample [f min max]
  (DatasetUtilities/sampleFunction2D
    (reify Function2D (getValue [this x] (f x)))
    min max 1000 "Value"))
