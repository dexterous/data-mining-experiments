(ns dm.expt.reddit-recommender
  (:require [clojure.data [csv :as data]]
            [clojure.java [io :as io]]
            [kmeans-clj [core :as impl]]
            [incanter [stats :as i]]
            [clojure [pprint :as pp]]
            [dm.viz [chart :as chart]])
  (:import [org.jfree.chart ChartFactory]
           [org.jfree.data.xy DefaultXYDataset XYDataset]))

(defprotocol FeatureExtractor
  (features [this user-weight subreddit-weight]))

(defprotocol Affinity
  (value [this]))

(defrecord RedditAffinity [user subreddit value-str]
  Affinity
  (value [this] (Float/valueOf value-str))

  FeatureExtractor
  (features [this user-weight subreddit-weight]
    [#_(user-weight user) (subreddit-weight subreddit) (value this)]))

(defn analyze [cluster user-weight subreddit-weight]
  {:count      (count cluster)
   :affinities (let [affs (map value cluster)]
                 (map #(% affs) [i/quantile i/mean]))
   :users      (let [subsciptions (vals (select-keys user-weight (set (map :user cluster))))]
                 (map #(% subsciptions) [i/quantile count]))
   :subreddits (let [user-base (vals (select-keys subreddit-weight (set (map :subreddit cluster))))]
                 (map #(% user-base) [i/quantile count]))})

(defn clusters [affinities distance k max-iter]
  (let [user->subreddits (frequencies (map :user affinities))
        subreddit->users (frequencies (map :subreddit affinities))]
    (->> (impl/k-means affinities distance #(features % user->subreddits subreddit->users) k max-iter)
         (map-indexed
           (fn [index cluster]
             (merge
               (analyze cluster user->subreddits subreddit->users)
               {:name (format "Cluster %d" index)}))))))

(defn ->affinities [rows]
  (map (partial apply ->RedditAffinity) rows))

(defn affinities->doubles [affinities]
  (let [user->subreddits (frequencies (map :user affinities))
        subreddit->users (frequencies (map :subreddit affinities))]
    (into-array
      (apply map
             (comp double-array vector)
             (map #(features % user->subreddits subreddit->users) affinities)))))

(defn plot [affinities]
  (chart/frame
    "Affinity scatter"
    (doto (DefaultXYDataset.)
      (.addSeries "Features" (affinities->doubles affinities)))))

(defmethod chart/chart XYDataset [dataset]
  (ChartFactory/createScatterPlot "Affinity scatter" "Subreddit weight" "Affinity" dataset))

(defn -main
  ([]
   (-main (io/resource "reddit/affinities.dump.csv") 10))

  ([file sample-size]
   (with-open [r (io/reader file)]
     (-> (data/read-csv r)
         next ;ignore header row
         (i/sample :replacement false :size sample-size)
         ->affinities
         (doto
           (plot))
         (clusters i/euclidean-distance 10 100)
         (doto
           (pp/pprint))))))