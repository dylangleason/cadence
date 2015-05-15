;;;;==================================================================
;;;; File     : ui_helper.clj
;;;; Author   : Dylan Gleason
;;;; Date     : 2014-04-12
;;;; Modified : 2014-04-12
;;;;
;;;; Contains UI helper functions
;;;;==================================================================

(in-ns 'composer.ui.core)

(defn- make-choose-file-ui
  [type]
  (chooser/choose-file
   @main-ui
   :type type
   :filters [["*.mid, *.midi" ["mid" "midi"]]]
   :success-fn (fn [fc file] (.getAbsolutePath file))))

(defn- make-progress-dialog []
  (seesaw/custom-dialog
   :parent @main-ui
   :content
   (seesaw/grid-panel
    :rows 2
    :columns 1
    :vgap 2
    :border (border/empty-border :thickness 15)
    :items [(seesaw/label "Transforming MIDI sequence...")
            (seesaw/progress-bar
             :orientation :horizontal
             :indeterminate? true)])
   :modal? true
   :minimum-size [300 :by 50]
   :resizable? false))

(defn- make-algorithm-selector []
  (seesaw/combobox
   :font :monospaced
   :model ["--"
           "1st-Order Markov"
           "2nd-Order Markov"]))

(defn- get-algorithm
  "Returns the currently selected algorithm value"
  [selector]
  (when-not (nil? selector)
    (case (seesaw/selection selector)
      "1st-Order Markov" :first
      "2nd-Order Markov" :second
      nil)))

(defn- transform-ticks
  "Given a javax.sound.midi.Sequence object, write the sequence with
  the new PPQ tick data"
  [midi-seq]
  (let [events (io/sequence->short-messages midi-seq)
        ticks  (time/transform-rhythm
                (get-algorithm @rhythm-selector-ui)
                events)]
    (io/write-sequence {:type :rhythm} ticks events)
    (io/write-sequence {:type :length} ticks events)))

(defn- transpose-pitch
  "Given a javax.sound.midi.Sequence object, write the sequence with
  the new tranposed pitch data"
  [midi-seq]
  (let [events (io/sequence->short-messages midi-seq)
        pitch  (short/transpose-pitch
                events
                (seesaw/selection @transpose-selector-ui))]
    (io/write-sequence {:type :pitch} pitch events)))

(defn- transform-data
  "Given a javax.sound.midi.Sequence object, write the sequence with
  the new pitch and velocity data"
  [midi-seq]
  (dotimes [x (seesaw/selection @iteration-selector-ui)]
    (let [events (io/sequence->short-messages midi-seq)
          algo   (get-algorithm @pitch-selector-ui)
          pitch  (short/transform-pitch algo events)
          veloc  (short/transform-velocity algo events)]
      (io/write-sequence {:type :pitch} pitch events)
      (io/write-sequence {:type :velocity} veloc events))))

