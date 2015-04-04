(ns evolution-programs.chart
  (:require [evolution-programs.population-statistic :as stat])
  (:import [org.jfree.chart ChartFactory ChartPanel StandardChartTheme]
           [org.jfree.data.category DefaultCategoryDataset]
           [org.jfree.ui ApplicationFrame]))

(ChartFactory/setChartTheme (StandardChartTheme. "JFree/Shadow" true))

(defn- chart [dataset]
  (doto (ChartFactory/createLineChart "Population Analysis" "Generation" "Fitness" dataset)
    (->
      (.getCategoryPlot)
      (.getRenderer)
      (.setShapesVisible true))))

(defn- panel [chart]
  (doto (ChartPanel. chart)
    (.setMouseWheelEnabled true)))

(defn- frame [dataset]
  (doto (ApplicationFrame. "Population Analysis")
    (.setContentPane (panel (chart dataset)))
    (.pack)
    (.setVisible true)))

(defn log-generation [dataset population generation]
  (.addValue dataset (stat/mean-fitness population) "Mean" generation)
  (.addValue dataset (stat/best-fitness population) "Best" generation)
  (Thread/sleep 200))

(defn logger []
  (let [dataset (DefaultCategoryDataset.)]
    (frame dataset)
    (partial log-generation dataset)))
