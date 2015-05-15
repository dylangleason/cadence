;;;;===================================================================
;;;; File     : transform.clj
;;;; Author   : Dylan Gleason
;;;; Date     : 2013-11-16
;;;; Modified : 2013-11-17
;;;;
;;;; The multimethod definition `transform' will dispatch based on two
;;;; parameters: a clojure.lang.PersistentArrayMap which contains a
;;;; key-value pair representing the "type" of transformation, as well
;;;; as a class representing the data to be transformed.
;;;;===================================================================

(ns composer.algorithm.transform)

(defmulti transform
  "Transfroms the given MIDI data according a specified algorithm; the
  algorithm is specified with the first parameter, and the second
  param is the MIDI data to be transformed"
  (fn [x y] [(:type x) (class y)]))

