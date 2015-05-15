;;;;==================================================================
;;;; File     : short-message.clj
;;;; Author   : Dylan Gleason
;;;; Date     : 2014-03-07
;;;; Modified : 2014-04-08
;;;;
;;;; Contains functionality for determining MIDI pitch and velocity
;;;; and manipulating them
;;;;==================================================================

(ns composer.midi.short-message
  (:require [composer.midi
             [io :as io :refer [write-sequence]]
             [message :as message]]
            [composer.algorithm.transform :refer :all]
            [composer.algorithm.markov.transform :refer :all]))

;;; conversion functions

(defn- events->data
  "Given a collection and a function, return a seq of vectors
  containing ShortMessage data"
  [events f filter?]
  (map #(io/events->bytes % f filter?) events))

(defn events->pitches
  "Given a collection, return a seq of vectors containing MIDI pitch
  bytes."
  [events]
  (events->data events #(.getData1 %) true))

(defn events->velocities
  "Given a collection, return a seq of vectors containing MIDI
  velocity bytes."
  [events]
  (events->data events #(.getData2 %) true))

;;; transformations: functions are used to manipulate MIDI
;;; short-message byte data. Output data is used to overwrite
;;; currently loaded MIDI sequence

(defn transpose-pitch
  "Given a collection of MIDI data1 bytes, transpose each pitch by the
  given offset"
  [events n]
  (map (fn [v] (into [] (map #(+ % n) v))) (events->pitches events)))

(defn transform-pitch
  "For each javax.sound.midi.MidiEvent object, transform the event's
  pitch data using the specified algorithm"
  [type events]
  (transform {:type type} (events->pitches events)))

(defn transform-velocity
  "For each javax.sound.midi.MidiEvent object, transform the event's
  velocity data using the specified algorithm"
  [type events]
  (transform {:type type} (events->velocities events)))

;;; setters: below functions are used to overwrite MIDI sequence with
;;; the new MIDI data

(defn- set-midi-data!
  "Sets MIDI data1 and data2 bytes with the new data"
  [data events f]
  (let [on-events  (io/events->on-events events) 
        off-events (io/events->off-events events)]
    (io/set-events! data on-events f)
    (io/set-events! data off-events f)))

(defn- set-pitch!
  "Sets MIDI events with new pitch data"
  [data events]
  (letfn [(pitch [pitch event]
            (let [cmd   (message/get-command event)
                  msg   (.getMessage event)
                  chn   (.getChannel msg)
                  veloc (.getData2 msg)]
              (.setMessage msg cmd chn pitch veloc)))]
    (set-midi-data! data events pitch)))

(defn- set-velocity!
  "Sets MIDI events with new velocity data"
  [data events]
  (letfn [(velocity [veloc event]
            (let [cmd   (message/get-command event)
                  msg   (.getMessage event)
                  chn   (.getChannel msg)
                  pitch (.getData1 msg)]
              (.setMessage msg cmd chn pitch veloc)))]
    (set-midi-data! data events velocity)))

; write-sequence: writes pitch data to the sequence
(defmethod write-sequence
  [:pitch clojure.lang.LazySeq clojure.lang.LazySeq]
  [type data tracks]
  (io/set-tracks! data tracks set-pitch!))

; write-sequence: writes velocity data to the sequence
(defmethod write-sequence
  [:velocity clojure.lang.LazySeq clojure.lang.LazySeq]
  [type data tracks]
  (io/set-tracks! data tracks set-velocity!))
