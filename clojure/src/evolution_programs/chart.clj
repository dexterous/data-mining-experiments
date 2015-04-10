(ns evolution-programs.chart
  (:require [evolution-programs.population-statistic :as stat])
  (:import [javax.swing BoxLayout]
           [org.jfree.chart ChartFactory ChartPanel StandardChartTheme]
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

(defn- add-charts [frame datasets]
  (let [content-pane (.getContentPane frame)]
    (.setLayout content-pane (BoxLayout. content-pane BoxLayout/Y_AXIS))
    (doseq [dataset datasets]
      (.add content-pane (panel (chart dataset))))))

(defn- frame [title & datasets]
  (doto (ApplicationFrame. title)
    (add-charts datasets)
    (.pack)
    (.setVisible true)))

(defn- ->Function2D [f]
  (reify Function2D (getValue [_ x] (f x))))

(defn- sample [f min max samples]
    (DatasetUtilities/sampleFunction2D (->Function2D f) min max samples "Value"))

(defn- log-generation [analysis-dataset population generation-number]
   (doto analysis-dataset
     (.addValue (stat/mean-fitness population) "Mean" generation-number)
     (.addValue (stat/best-fitness population) "Best" generation-number))
   (Thread/sleep 500))

(defn logger
  ([]
   (let [analysis-dataset (DefaultCategoryDataset.)]
     (frame "Popluation Analysis" analysis-dataset)
     (partial log-generation analysis-dataset)))
  ([f min max samples]
   (let [sample-dataset (sample f min max samples)
         analysis-dataset (DefaultCategoryDataset.)]
     (frame  "Population Analysis" sample-dataset analysis-dataset)
     (partial log-generation analysis-dataset))))
