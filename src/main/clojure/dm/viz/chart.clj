(ns dm.viz.chart
  (:require [dm.util.population-statistic :as stat])
  (:import [javax.swing BoxLayout JFrame SwingUtilities]
           [org.jfree.chart ChartFactory ChartPanel StandardChartTheme]
           [org.jfree.data.function Function2D]
           [org.jfree.data.general DatasetUtilities]
           [org.jfree.data.xy XYSeries XYSeriesCollection]
           [org.jfree.data.statistics BoxAndWhiskerCategoryDataset DefaultBoxAndWhiskerCategoryDataset]))

(def ^:private individual-series-key "Individual")

(ChartFactory/setChartTheme (StandardChartTheme. "JFree/Shadow" true))

(defmulti chart class)

(defmethod chart XYSeriesCollection [dataset]
  (let [individual-series (.getSeriesIndex dataset individual-series-key)]
    (doto (ChartFactory/createXYLineChart "Curve of function" "X" "Y" dataset)
      (->
        (.getXYPlot)
        (.getRenderer)
        (doto
          (.setSeriesLinesVisible individual-series false)
          (.setSeriesShapesVisible individual-series true))))))

(defmethod chart BoxAndWhiskerCategoryDataset [dataset]
  (ChartFactory/createBoxAndWhiskerChart "Population Analysis" "Generation" "Fitness" dataset false))

(defn- panel [chart]
  (doto (ChartPanel. chart)
    (.setMaximumDrawWidth 1600)
    (.setMouseWheelEnabled true)))

(defn- add-charts [frame datasets]
  (let [content-pane (.getContentPane frame)]
    (.setLayout content-pane (BoxLayout. content-pane BoxLayout/Y_AXIS))
    (doseq [dataset datasets]
      (.add content-pane (panel (chart dataset))))))

(defn frame [title & datasets]
  (letfn [(show-later [f] (SwingUtilities/invokeLater #(.setVisible f true)))]
    (doto (JFrame. title)
      (add-charts datasets)
      (.setExtendedState JFrame/MAXIMIZED_BOTH)
      (.setDefaultCloseOperation JFrame/DISPOSE_ON_CLOSE)
      (show-later))))

(defn- ->Function2D [f]
  (reify Function2D (getValue [_ x] (f x))))

(defn- sample [f min max samples]
  (DatasetUtilities/sampleFunction2DToSeries (->Function2D f) min max samples "Value"))

(defn- set-individuals [series population]
  (.clear series)
  (doseq [[x :as individual] population]
    (.add series x (:fitness (meta individual)) false))
  (.fireSeriesChanged series))

(defn- log-generation
  ([generation-series analysis-dataset population generation-number]
   (set-individuals generation-series population)
   (log-generation analysis-dataset population generation-number))
  ([analysis-dataset population generation-number]
   (.add analysis-dataset (stat/fitness population) "Fitness" generation-number)
   (Thread/sleep 500)))

(defn- terminator [frame]
  (fn [_ _] (not (.isVisible frame))))

(defn logger
  ([]
   (let [analysis-dataset (DefaultBoxAndWhiskerCategoryDataset.)]
     [(terminator (frame "Population Analysis" analysis-dataset))
      (partial log-generation analysis-dataset)]))
  ([f min max samples]
   (let [analysis-dataset (DefaultBoxAndWhiskerCategoryDataset.)
         generation-series (XYSeries. individual-series-key)
         sample-dataset (doto (XYSeriesCollection. generation-series)
                          (.addSeries (sample f min max samples)))]
     [(terminator (frame  "Population Analysis" sample-dataset analysis-dataset))
      (partial log-generation generation-series analysis-dataset)])))
