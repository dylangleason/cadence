;;;;==================================================================
;;;; File     : time.clj
;;;; Author   : Dylan Gleason
;;;; Date     : 2014-03-07
;;;; Modified : 2014-03-07
;;;;
;;;; Contains functionality for determining MIDI time in ticks and
;;;; manipulating MIDI tick values
;;;;==================================================================

(ns composer.midi.time
  (:require [composer.algorithm.transform :refer :all]
            [composer.algorithm.markov.transform :refer :all]
            [composer.midi.io :as io :refer [write-sequence]]))

;;; Below functions are used for obtaining MIDI tick information from
;;; a MidiEvent objects, as well as to determine timing resolution,
;;; division type, etc.

(defn events->ticks
  "Given a seq of events, return a seq of PPQ ticks for each event"
  [events]
  (map #(.getTick %) events))

(defn get-note-deltas
  "Given a sequence of javax.sound.midi.MidiEvent objects return, a
  seq of note deltas (i.e. the time between the first and next NOTE_ON
  events) in PPQ ticks"
  [events]
  (letfn [(deltas [e]
            (loop [in  (-> (io/events->on-events e) events->ticks)
                   out []]
              (if (empty? (rest in)) 
                out
                (recur (rest in)
                       (conj out (- (second in) (first in)))))))]
    (remove empty? (map deltas events))))

(defn get-note-lengths
  "Given a sequence of javax.sound.midi.MidiEvent objects, return a
  seq of durations (i.e. the time between a NOTE_ON and NOTE_OFF
  event) in PPQ ticks"
  [events]
  (letfn [(durations [e]
            (loop [on  (-> (io/events->on-events e) events->ticks)
                   off (-> (io/events->off-events e) events->ticks)
                   out []]
              (if (some empty? [on off])
                out
                (recur (rest on)
                       (rest off)
                       (conj out (- (first off) (first on)))))))]
    (remove empty? (map durations events))))

(defn get-division-type
  "Given a javax.sound.midi.Sequence object, return division type"
  [midi-seq]
  (.getDivisionType midi-seq))

(defn get-time-resolution
  "Given a javax.sound.midi.Sequence object, return time resolution
  for the sequence"
  [midi-seq]
  (.getResolution midi-seq))

;;; Below functions are used for transforming the MIDI tick data for
;;; the specified sequence

(defn- beat->ticks
  "Given a beat division (e.g. 1/4, for a quarter note) and the
  sequence's time resolution in ticks, get the corresponding tick
  value for the note subdivision"
  [div resolution]
  (int (case div
         "1/1"  (* resolution 4)
         "1/2"  (* resolution 2)
         "1/3"  (* resolution 3/2)
         "1/6"  (/ resolution 3/2)         
         "1/8"  (/ resolution 2)
         "1/12" (/ resolution 3)
         "1/16" (/ resolution 4)
         "1/32" (/ resolution 8)
         resolution)))

(defn- get-random-tick
  "Returns a randomly selected note subdivision in PPQ ticks"
  [midi-seq]
  (let [beats ["1/1" "1/2"  "1/3"  "1/4"  "1/6"
               "1/8" "1/12" "1/16" "1/24" "1/32"]
        random (rand-int (count beats))]
    (beat->ticks (nth beats random)
                 (get-time-resolution midi-seq))))

(defn get-random-ticks
  "Populate all the tick deltas with a random number PPQ tick interval"
  [midi-seq events]
  (let [deltas (get-note-deltas events)]
    (map (fn [coll]
           (into []
                 (for [i (range (count coll))]
                   (get-random-tick midi-seq))))
         deltas)))

(defn transform-rhythm
  "Given a seq of javax.sound.midi.MidiEvent objects and algorithm
   type, evaluate to the transformed MIDI tick data."
  [type events]
  (transform {:type type} (get-note-deltas events)))

(defn transform-length
  "Given a seq of javax.sound.midi.MidiEvent objects and algorithm
   type, evaluate to the transformed MIDI tick data."
  [type events]
  (transform {:type type} (get-note-lengths events)))

(defn- set-ticks!
  "Sets MIDI NOTE_ON or NOTE_OFF events with the new PPQ tick data"
  [data events]
  (let [ticks  (into [(.getTick (first events))] data)
        f      #(.setTick %2 %1)]
    (io/set-events! (reductions + ticks) events f)))

(defn- set-rhythm!
  "Sets MIDI NOTE_ON events with the new PPQ tick data"
  [data events]
  (set-ticks! data (io/events->on-events events)))

(defn- set-length!
  "Sets MIDI NOTE_OFF events with the new PPQ tick data"
  [data events]
  (set-ticks! data (io/events->off-events events)))

(defmethod write-sequence
  [:rhythm clojure.lang.LazySeq clojure.lang.LazySeq]
  [type data tracks]
  (io/set-tracks! data tracks set-rhythm!))

(defmethod write-sequence
  [:length clojure.lang.LazySeq clojure.lang.LazySeq]
  [type data tracks]
  (io/set-tracks! data tracks set-length!))

;;; @todo - need to set the EndOfTrack event's tick value to make sure
;;; it ends only after the last note event

