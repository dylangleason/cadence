;;;;==================================================================
;;;; File     : ui_state.clj
;;;; Author   : Dylan Gleason
;;;; Date     : 2013-12-28
;;;; Modified : 2013-03-07
;;;;
;;;; Contains top-level definitions for all UI widgets and object
;;;; states
;;;;==================================================================

(in-ns 'composer.ui.core)

;;; MIDI sequencer and MIDI sequence state
(def ^:private midi-sequencer        (atom nil))
(def ^:private midi-sequence         (atom nil))

;;; Main UI widgets
(def ^:private main-ui               (atom nil))
(def ^:private loaded-file-ui        (atom nil))
(def ^:private menubar-ui            (atom nil))
(def ^:private splitter-ui           (atom nil))

;;; MIDI player widgets
(def ^:private midi-player-ui        (atom nil))
(def ^:private player-options-ui     (atom nil))
(def ^:private bpm-ui                (atom nil))

;;; Algorithm transformation widgets
(def ^:private algorithm-ui          (atom nil))
(def ^:private pitch-selector-ui     (atom nil))
(def ^:private rhythm-selector-ui    (atom nil))
(def ^:private transpose-selector-ui (atom nil))
(def ^:private iteration-selector-ui (atom nil))
