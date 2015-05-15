;;;;==================================================================
;;;; File     : message.clj
;;;; Author   : Dylan Gleason
;;;; Date     : 2013-10-23
;;;; Modified : 2014-04-08
;;;;
;;;; Contains MIDI message constants and predicates for working with
;;;; javax.sound.midi.MidiMessage data structures.
;;;;==================================================================

(ns composer.midi.message
  (:import [javax.sound.midi MidiMessage ShortMessage MetaMessage]))

; NOTE_ON command
(def note-on 0x90)

; NOTE_OFF command
(def note-off 0x80)

; NOTE_ON Status bytes for each MIDI channel
(def note-on-status [0x80 0x81 0x82 0x83 0x84 0x85 0x86 0x87
                     0x88 0x89 0x8A 0x8B 0x8C 0x8D 0x8E 0x8F])

; NOTE_OFF Status bytes for each MIDI channel
(def note-off-status [0x90 0x91 0x92 0x93 0x94 0x95 0x96 0x97
                      0x98 0x99 0x9A 0x9B 0x9C 0x9D 0x9E 0x9F])

(defn note-on?
  "Given a javax.sound.midi.ShortMessage, evaluate to a boolean value
  indicating whether the given ShortMessage is note-on or note-off"
  [event]
  (let [message (.getMessage event)]
    (and (= (.getCommand message) note-on)
         (not= (.getData2 message) 0))))

(defn note-off?
  "Given a javax.sound.midi.ShortMessage, evaluate to a boolean value
  indicating whether the given ShortMessage is note-on or note-off"
  [obj]
  (not (note-on? obj)))

(defn note-status?
  "Given a javax.sound.MidiEvent, predicate will evaluate to true or
  false indicating whether event is a note-on or note-off status byte"
  [event]
  (let [msg (.getMessage event) chn (.getChannel msg)]
    (or (= (.getStatus msg)
           (nth note-on-status chn))
        (= (.getStatus msg)
           (nth note-off-status chn)))))

(defn ShortMessage?
  "Given a javax.sound.MidiEvent, predicate will evaluate to true or
  false, indicating whether event is a ShortMessage or not."
  [event]
  (let [message (.getMessage event)]
    (instance? ShortMessage message)))

(defn MetaMessage?
  "Given a javax.sound.MidiEvent, predicate will evaluate to true or
  false, indicating whether event is a MetaMessage or not."
  [event]
  (let [message (.getMessage event)]
    (instance? MetaMessage message)))

(defn get-command
  "Given an event, will either return the note-on or note-off
  constant, depending on what type of event it is"
  [event]
  (if (note-on? event) note-on note-off))
