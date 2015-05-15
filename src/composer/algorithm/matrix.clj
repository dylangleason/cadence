;;;;===================================================================
;;;; File     : matrix.clj
;;;; Author   : Dylan Gleason
;;;; Date     : 2013-11-16
;;;; Modified : 2013-11-17
;;;;
;;;; Contains multimethod `build-matrix' which provides an interface
;;;; for matrix creation.
;;;;===================================================================

(ns composer.algorithm.matrix)

(defmulti matrix
  "Constructs an n-order matrix, where the first argument is the type
  (e.g. :first for first-order matrix) and the second argument is a
  collection of MIDI data"
  (fn [x y] [(:type x) (class y)]))
