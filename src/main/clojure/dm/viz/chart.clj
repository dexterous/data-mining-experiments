(ns dm.viz.chart
  (:import
    (javax.swing AbstractAction Box BoxLayout JButton JFrame JOptionPane SwingUtilities)
    (org.jfree.chart ChartFactory ChartPanel StandardChartTheme)
    (org.jfree.chart.editor ChartEditorManager)))

(ChartFactory/setChartTheme (StandardChartTheme. "JFree/Shadow" true))

(defmulti chart class)

(defn- action [action-name handler-fn]
  (proxy [AbstractAction] [action-name]
    (actionPerformed [event] (handler-fn event))))

(defn- edit-confirmed? [event editor]
  (let [button (cast JButton (.getSource event))]
    (= JOptionPane/OK_OPTION
       (JOptionPane/showConfirmDialog button
                                      editor
                                      (.getText button)
                                      JOptionPane/OK_CANCEL_OPTION
                                      JOptionPane/PLAIN_MESSAGE))))

(defn- panel [chart]
  (doto (Box. BoxLayout/Y_AXIS)
    (.add (doto (ChartPanel. chart)
            (.setMaximumDrawWidth 1600)
            (.setMouseWheelEnabled true)))
    (.add (JButton. (action "Customize Chart" (fn [e]
                                                  (let [editor (ChartEditorManager/getChartEditor chart)]
                                                    (if (edit-confirmed? e editor)
                                                      (.updateChart editor chart)))))))))

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
