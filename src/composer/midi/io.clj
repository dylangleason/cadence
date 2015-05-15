;;;;==================================================================
;;;; File     : io.clj
;;;; Author   : Dylan Gleason
;;;; Date     : 2013-07-14
;;;; Modified : 2014-04-08
;;;;
;;;; Contains functionality for reading/writing MIDI files, as well as
;;;; extracting MIDI data bytes and encapsulating them in Clojure data
;;;; structures for further processing.
;;;;==================================================================

(ns composer.midi.io
  (:require [clojure.java.io :as io]
            [composer.midi.message :as message])
  (:import  [javax.sound.midi MidiEvent MidiSystem Sequence Track]))

;;; conversion functions

(defn- sequence->tracks
  "Given a javax.sound.midi.Sequence object, returns a sequence of
  tracks" 
  [midi-seq]
  (let [tracks (.getTracks midi-seq)]
    (for [i (range (count tracks))]
      (aget tracks i))))

(defn- events->short-messages
  "Given a collection of javax.sound.midi.MidiEvent objects, return a
  seq of ShortMessage objects"
  [events]
  (filter message/ShortMessage? events))

(defn events->notes
  "Given a seq of MidiEvent objects, return only the MidiEvents which
  contain NOTE-ON or NOTE-OFF messages"
  [events]
  (-> (events->short-messages events)
      (#(filter message/note-status? %))))

(defn events->on-events
  "Given a collection of javax.sound.midi.MidiEvent objects, return a
  seq of NOTE_ON messages."
  [events]
  (-> (events->notes events)
      (#(filter message/note-on? %))))

(defn events->off-events
  "Given a collection of javax.sound.midi.MidiEvent objects, return a
  seq of NOTE_OFF messages"
  [events]
  (-> (events->notes events)
      (#(filter message/note-off? %))))

(defn sequence->events
  "Given a javax.sound.midi.Sequence object, return a seq of
  javax.sound.midi.MidiEvent vectors."
  [midi-seq]
  (map #(for [i (range (.size %))] (.get % i))
       (sequence->tracks midi-seq)))

(defn sequence->short-messages
  "Given a javax.sound.midi.Sequence object, return a seq of
  javax.sound.MidiEvents which are only ShortMessages"
  [midi-seq]
  (remove empty?
          (map events->short-messages
               (sequence->events midi-seq))))

(defn sequence->meta-events
  "Given a javax.sound.midi.Sequence object, return a seq of
  javax.sound.midi.MetaEvent vectors"
  [midi-seq]
  (let [meta (aget (.getTracks midi-seq) 0)]
    (for [i (range (.size meta))]
      (.get meta i))))

(defn- short-messages->bytes
  "Given a collection of javax.sound.midi.ShortMessage objects and a
  function, return a seq of data bytes"
  [short-messages f]
  (map (fn [event]
         (f (.getMessage event)))
       short-messages))

(defn events->bytes
  "Given a collection of javax.sound.midi.MidiEvent objects, return a
  vector of 'note-on' pitch events."
  [events f filter?]
  (if filter?
    (into []
          (short-messages->bytes (events->on-events events) f))
    (into []
          (short-messages->bytes (events->short-messages events) f))))

;;; setters: used for overrwriting the currently loaded MIDI sequence
;;; with newly transformed MIDI data

(defn set-events!
  "Sets each MIDI event in the vector with the new MIDI data"
  [data events f]
  (loop [data   data
         events events 
         rec    nil]
    (when-not (some empty? [events data])
      (recur  (rest data)
              (rest events)
              (f (first data) (first events))))))

(defn set-tracks!
  "Given the new MIDI data, a sequence of MidiEvent vectors and a
  function, set the sequence with the new data"
  [data tracks f]
  (loop [data   data
         tracks tracks
         rec    nil]
    (when-not (some empty? [data tracks])
      (recur  (rest data)
              (rest tracks)
              (f (first data) (first tracks))))))

(defmulti write-sequence
  "Sets MIDI data on a given MIDI sequence. Dispatches based on type
  of data."
  (fn [type data tracks]
    [(:type type)
     (class data)
     (class tracks)]))

;;; file i/o: used for reading and writing midi files

(defn read-midi-file
  "Given a string representing a file, return a
  javax.sound.midi.Sequence object."
  [name]
  (try
    (MidiSystem/getSequence (io/file name))
    (catch Exception e
      (str "Error reading MIDI file: " (.getMessage e)))))

(defn write-midi-file
  "Given a javax.sound.midi.Sequence and a filename, write a MIDI
  Sequence to a file"
  [midi-seq name]
  (try
    (MidiSystem/write midi-seq 1 (io/file name))
    (catch Exception e
      (str "Error writing midi file: " (.getMessage e)))))

