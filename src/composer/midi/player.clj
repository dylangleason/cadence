;;;;==================================================================
;;;; File     : player.clj
;;;; Author   : Dylan Gleason
;;;; Date     : 2013-07-19
;;;; Modified : 2014-03-16
;;;;
;;;; Contains functions for playing back a MIDI sequence.
;;;;==================================================================

(ns composer.midi.player
  (:import [javax.sound.midi MidiSystem MetaEventListener]))

(defn start
  "Given a javax.sound.midi.Sequencer object, the function will stop
  playback of the javax.sound.midi.Sequencer."
  [sequencer]
  (try
    (.start sequencer)
    (catch Exception e (println e))))

(defn stop
  "Given a javax.sound.midi.Sequencer object, the function will stop
   playback of the javax.sound.midi.Sequencer."
  [sequencer]
  (try
    (.stop sequencer)
    (.setTickPosition sequencer 0)
    (catch Exception e (println e))))

(defn pause
  "Given a javax.sound.midi.Sequencer object, the function will pause
  playback of the javax.sound.midi.Sequencer."
  [sequencer]
  (try
    (.stop sequencer)
    (catch Exception e (println e))))

(defn close
  "Given a javax.sound.midi.Sequencer object, the function will close
  the javax.sound.midi.Sequencer."
  [sequencer]
  (try
    (.close sequencer)
    (catch Exception e (println e))))

(defn get-sequencer
  "Returns a javax.sound.midi.Sequencer object"
  []  
  (MidiSystem/getSequencer))

(defn- add-end-listener
  "Given a Sequencer object Adds a MetaEventListener for the sequencer
  in order to detect when the track ends. When it ends, we want to
  stop playback"
  [sequencer]
  (let [listener
        (proxy [MetaEventListener] []
          (meta [e]
            (when (= (.getType e) 0x2F) (stop sequencer))))]
    (.addMetaEventListener sequencer listener)))

(defn load-sequence
  "Given a javax.sound.midi.Sequencer object and a
  javax.sound.midi.Sequence object, the function will play back the
  sequence."
  [sequencer midi-seq]  
  (try
    (do (add-end-listener sequencer)
        (.open sequencer)
        (.setSequence sequencer midi-seq))
    (catch Exception e (println e))))

(defn get-bpm
  "Given a javax.sound.midi.Sequencer instance, get the tempo in BPM
  of the MIDI sequencer"
  [sequencer]
  (try
    (.getTempoInBPM sequencer)
    (catch Exception e (println e))))

(defn set-bpm
  "Given a javax.sound.midi.Sequencer instance and a tempo in BPM, set
  the sequencer tempo to the BPM passed"
  [sequencer bpm]
  (try
    (.setTempoInBPM sequencer bpm)
    (catch Exception e (println e))))
