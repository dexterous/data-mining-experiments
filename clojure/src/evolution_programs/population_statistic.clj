(ns evolution-programs.population-statistic)

(defn fitness [population] (map #(:fitness (meta %)) population))
(defn best-fitness [population] (apply max (fitness population)))
(defn mean-fitness [population]
  (let [fitness (fitness population)
        n (count fitness)]
    (/ (apply + fitness) n)))
