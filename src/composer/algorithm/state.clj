;;;;===================================================================
;;;; File     : state.clj
;;;; Author   : Dylan Gleason
;;;; Date     : 2013-07-27
;;;; Modified : 2013-11-17
;;;;
;;;; State is a protocol representing the State of a MIDI datum during
;;;; a MIDI sequence transformation. It provides an interface for
;;;; determining what the next transition State will be.
;;;;===================================================================

(ns composer.algorithm.state)

(defprotocol State
  "A protocol for determining the next MIDI state."
  (get-next [this] "Not implemented yet."))

