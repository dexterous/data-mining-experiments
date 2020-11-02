(ns dm.viz.chart
  (:import
    (javax.swing BoxLayout JFrame SwingUtilities)
    (org.jfree.chart ChartFactory ChartPanel StandardChartTheme)))

(ChartFactory/setChartTheme (StandardChartTheme. "JFree/Shadow" true))

(defmulti chart class)

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
