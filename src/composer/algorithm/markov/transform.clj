;;;;==================================================================
;;;; File     : transform.clj
;;;; Author   : Dylan Gleason
;;;; Date     : 2013-11-16
;;;; Modified : 2014-03-07
;;;;
;;;; The following definitions contain methods and helper functions
;;;; for transforming a MIDI sequence data using a Markov Chain
;;;; process.
;;;;==================================================================

(ns composer.algorithm.markov.transform
  (:require [composer.algorithm.state :refer :all])
  (:require [composer.algorithm.matrix :refer :all])
  (:require [composer.algorithm.transform :refer :all])
  (:require [composer.algorithm.markov.state :refer :all])
  (:require [composer.algorithm.markov.matrix :refer :all]))

(defn- get-seed
  "Given a state and a transition matrix, evaluate to a new State
  object"
  [t mtrx]
  (let [seed (rand-nth (keys mtrx))]
    (if (= t :first)
      (->FirstOrderState seed mtrx)
      (->SecondOrderState (first seed) (second seed) mtrx))))

(defn- get-transformation
  "Given a track and an initial State, the function will evaluate to a
   new PersistentVector representing the transformed MIDI track."
  [t track mtrx]
  (loop [index 0
         seed  (get-seed t mtrx)
         vect  []]
    (cond
     (= index (count track)) vect
     (nil? (:curr seed)) (recur index (get-seed t mtrx) vect)
     :else (recur (inc index) (get-next seed) (conj vect (:curr seed))))))

;; First-Order
(defmethod transform
  [:first clojure.lang.LazySeq] [t data]
  (map (fn [x]
         (let [mtrx (matrix {:type :first} x)]
           (get-transformation :first x mtrx)))
       data))

;; Second-Order
(defmethod transform
  [:second clojure.lang.LazySeq] [t data]
  (map (fn [x]
         (let [mtrx (matrix {:type :second} x)]
           (get-transformation :second x mtrx)))
       data))
