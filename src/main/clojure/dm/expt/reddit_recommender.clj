(ns dm.expt.reddit-recommender
  (:require [clojure.data [csv :as data]]
            [clojure.java [io :as io]]
            [kmeans-clj [core :as cluster]]
            [incanter [stats :as i]]
            [clojure [pprint :as pp]]))

(defprotocol FeatureExtractor
  (features [this subreddit-weight]))

(defprotocol Affinity
  (value [this]))

(defrecord RedditAffinity [user subreddit value-str]
  Affinity
  (value [this] (Float/valueOf value-str))

  FeatureExtractor
  (features [this subreddit-weight]
    [(subreddit-weight subreddit) (value this)]))

(defn -main
  ([]
   (-main (io/resource "reddit/affinities.dump.csv")))

  ([file]
   (with-open [r (io/reader file)]
     (let [data (i/sample (next (data/read-csv r)) :size 10000 :replacement false)
           affinities (map (partial apply ->RedditAffinity) data)
           subreddit->user-count (reduce-kv #(assoc %1 %2 (count (into #{} (map :user %3))))
                                            {}
                                            (group-by :subreddit affinities))
           feature-extractor #(features % subreddit->user-count)
           k 4
           max-iters 10
           clusters (cluster/k-means affinities i/euclidean-distance feature-extractor k max-iters)]
       (pp/pprint
         (map-indexed
           (fn [i a]
             [(str "Cluster" i)
              (let [affs (map value a)]
                (map #(% affs) [i/quantile i/mean i/median]))
              (select-keys subreddit->user-count (->> a (map :subreddit) set))])
           clusters))))))
