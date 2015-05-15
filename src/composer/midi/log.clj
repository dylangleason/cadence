;;;;==================================================================
;;;; File        : log.clj
;;;; Author      : Dylan Gleason
;;;; Description : Contains routines for debugging MIDI data
;;;; Date        : 2013-07-14
;;;; Modified    : 2014-03-09
;;;;==================================================================

(ns composer.midi.log
  (:require [composer.midi.message :refer :all]))

(defn log-midi-messages
  "Given a collection of javax.sound.midi.MidiEvent objects, log
  information for each javax.sound.midi.MidiEvent object."
  [events]
  (doseq [event events]
    (let [msg (.getMessage event)
          tck (str "@" (.getTick event) ": ")
          cmd #(if (= (.getCommand %) note-on) "ON,  " "OFF, ")]
      (if (ShortMessage? event)
        (println (str tck (cmd msg))
                 (str "Channel: "  (.getChannel msg) ", ")
                 (str "Status: "   (.getStatus msg) ", ")
                 (str "Pitch: "    (.getData1 msg) ", ")
                 (str "Velocity: " (.getData2 msg)))
        (println (str tck "Other message: " (.getClass msg)))))))
