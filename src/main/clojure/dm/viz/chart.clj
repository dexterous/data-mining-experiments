(ns dm.viz.chart
  (:import
    (javax.swing Box BoxLayout JFrame SwingUtilities)
    (org.jfree.chart ChartFactory ChartPanel StandardChartTheme)))

(ChartFactory/setChartTheme (StandardChartTheme. "JFree/Shadow" true))

(defmulti chart class)

(defn- panel [chart]
  (doto (Box. BoxLayout/Y_AXIS)
    (.add (doto (ChartPanel. chart)
            (.setMaximumDrawWidth 1600)
            (.setMouseWheelEnabled true)))))

(defn- add-charts [frame datasets]
  (let [box (Box. BoxLayout/Y_AXIS)]
    (.setContentPane frame box)
    (doseq [dataset datasets]
      (.add box (panel (chart dataset))))))

(defn frame [title & datasets]
  (letfn [(show-later [f] (SwingUtilities/invokeLater #(.setVisible f true)))]
    (doto (JFrame. title)
      (.setExtendedState JFrame/MAXIMIZED_BOTH)
      (.setDefaultCloseOperation JFrame/DISPOSE_ON_CLOSE)
      (add-charts datasets)
      (show-later))))
