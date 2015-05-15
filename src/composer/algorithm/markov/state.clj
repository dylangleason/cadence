;;;;==================================================================
;;;; File     : state.clj
;;;; Author   : Dylan Gleason
;;;; Date     : 2013-06-19
;;;; Modified : 2013-11-17
;;;;
;;;; State machine definitions for Markov Chain algorithm and
;;;; associated helper functions.
;;;;==================================================================

(ns composer.algorithm.markov.state
  (:require [composer.algorithm.state :refer :all]))

(defn- in-range?
  "Given an Int and a PersistentVector, the predicate function will
  determine if the given number x is between the min and max range for
  lower and upperbound values in the vector."
  [x coll]
  (let [min (first (val coll))
        max (second (val coll))]
    (<= min x max)))

(defn- transition!
  "Given a PersistentArrayMap representing a stochastic row vector,
  the function will evaluate to the next state. Function generates a
  random number, so contains side-effects."
  [coll]
  (let [random (rand-int 100)]
    (ffirst (filter #(in-range? random %) coll))))

;;; FirstOrderState
;;; ------------------------------------------------------------------
;;; FirstOrderState is a record type which implements the State
;;; protocol. It describes State data for a first-order matrix, i.e.
;;; the Markov chain is dependent on only the current state.

(defrecord FirstOrderState [curr mtrx]
  State
  (get-next [this]
    (FirstOrderState.
     (transition! (get mtrx (:curr this))) mtrx)))

;;; SecondOrderState
;;; ------------------------------------------------------------------
;;; SecondOrderState is a record type which implements the State
;;; protocol. It describes State data for a second-order matrix, i.e.
;;; the Markov chain is dependent on the current and previous states.

(defrecord SecondOrderState [prev curr mtrx]
  State
  (get-next [this]
    (SecondOrderState.
     (:curr this)
     (transition! (get mtrx (conj [] (:prev this) (:curr this))))
     mtrx)))

