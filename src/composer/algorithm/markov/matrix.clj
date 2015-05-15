;;;;==================================================================
;;;; File     : matrix.clj
;;;; Author   : Dylan Gleason
;;;; Date     : 2013-07-27
;;;; Modified : 2014-03-09
;;;;
;;;; Contains functionality for generating stochastic "matrices" for
;;;; use with Markov transformation algorithm. A matrix is used to
;;;; look up a possible transition state for the current MIDI datum.
;;;;==================================================================

(ns composer.algorithm.markov.matrix
  (:require [clojure.math.numeric-tower :as math])
  (:require [composer.algorithm.matrix :refer :all]))

(defn- get-percentages
  "Given a probability-vector, convert all of the values to a
  percentage."
  [coll]
  (map #(* 100 (double %)) (vals coll)))

(defn- percent->range
  "Given an integer 'n' and an integer for the offset, the function
  will return a vector representing an lower-bound and upper-bound
  range."
  [n offset]
  (conj []
        (math/round (+ 0 offset))         
        (math/round (+ (- n 1) offset))))

(defn- percentages->ranges
  "Given a sequence of percentages, convert each percentage to a
  range."
  [coll]
  (loop [prev 0 in coll out []]
    (if (empty? in)
      out
      (recur (+ prev (first in))
             (rest in)
             (conj out (percent->range (first in) prev))))))

(defn- get-row-vector
  "Given a hash-map of MIDI pitch-integer pairs (representing frequency
  a given pitch appears after a pitch), generate a probability vector
  for that pitch."
  [coll]
  (let [total (reduce + (vals coll))]
    (loop [vs (vals coll)
           ks (keys coll)
           m  {}]
      (if (empty? ks)
        (percentages->ranges (get-percentages m))
        (recur (rest vs)
               (rest ks)
               (assoc m (first ks) (-> (first vs) (#(/ % total)))))))))

(defn- pitches->probs
  "Given the current pitch and a list of possible transition pitches,
  get the probability as a percentage for each transition pitch."
  [coll]
  (loop [ks (keys coll)
         vs (get-row-vector coll)
         m  {}]
    (if (or (empty? ks) (empty? vs))
      m
      (recur (rest ks)
             (rest vs)
             (assoc m (first ks) (first vs))))))

(defn- get-matrix
  "Given the distinct pitches (or pitch pairs, for a second-order
  matrix), a collection of pitches, and a function, construct a
  probability matrix."
  [distinct coll f]
  (loop [dist distinct total coll m {}]
    (if (empty? dist)
      m
      (recur (rest dist)
             total
             (assoc m (first dist) (f (first dist) total))))))

;; First-Order matrix
(defmethod matrix [:first clojure.lang.PersistentVector]
  [t coll]
  (letfn [(first-order [x xs]
            (loop [ps xs, fs []]
              (cond
               (empty? (rest ps)) (pitches->probs (frequencies fs))
               (= x (first ps)) (recur (rest ps) (conj fs (second ps)))
               :else (recur (rest ps) fs))))]
    (get-matrix (distinct coll) coll first-order)))

;; Second-Order matrix
(defmethod matrix [:second clojure.lang.PersistentVector]
  [t coll]
  (letfn [(get-pairs [xs]
            (loop [in xs out []]
              (if (seq (next in))
                (recur (rest in)
                       (conj out (conj [] (first in) (second in))))
                out)))
          (second-order [y ys]
            (loop [ps ys, fs []]
              (cond
               (nil? (nnext ps)) (pitches->probs (frequencies fs))
               (= y (conj [] (first ps) (second ps)))
               (recur (rest ps) (conj fs (nth ps 2)))
               :else (recur (rest ps) fs))))]
    (get-matrix (distinct (get-pairs coll)) coll second-order)))

