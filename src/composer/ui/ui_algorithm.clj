;;;;==================================================================
;;;; File     : ui_algorithm.clj
;;;; Author   : Dylan Gleason
;;;; Date     : 2014-04-12
;;;; Modified : 2013-04-12
;;;;
;;;; Contains algorithm widget and associated event handler
;;;;==================================================================

(in-ns 'composer.ui.core)

;;; event handler

(defn on-transform [e]
  (when-not (nil? @midi-sequence)
    (let [d (make-progress-dialog)]
      (future  ; spawn a new thread to perform algorithm
        (when-not (= 0 (seesaw/selection @transpose-selector-ui))
          (transpose-pitch @midi-sequence))
        (when-not (nil? (get-algorithm @pitch-selector-ui))
          (transform-data  @midi-sequence))
        (when-not (nil? (get-algorithm @rhythm-selector-ui))
          (transform-ticks @midi-sequence))
        (seesaw/dispose! d))
      (seesaw/show! d))))

;;; algorithm widgets

(reset! pitch-selector-ui
        (make-algorithm-selector))

(reset! rhythm-selector-ui
        (make-algorithm-selector))

(reset! transpose-selector-ui
        (seesaw/combobox
         :font :monospaced
         :model (vec (range 12 -13 -1))))

(reset! iteration-selector-ui
        (seesaw/combobox
         :font :monospaced
         :model [1 10 100 1000]))

(reset! algorithm-ui
        (mig/mig-panel
         :border "Algorithm"
         :bounds [:* :* :* 305]
         :constraints ["wrap" "" ""]
         :items [[(seesaw/label
                   :text "Pitch") "gapleft 10"]
                 [@pitch-selector-ui "growx"]
                 [(seesaw/label
                   :text "Rhythm") "gapleft 10"]
                 [@rhythm-selector-ui "growx"]
                 [(seesaw/label
                   :text "Transpose Key") "gapleft 10"]
                 [@transpose-selector-ui  "growx"]
                 [(seesaw/label
                   :text "# Iterations")  "gapleft 10"]
                 [@iteration-selector-ui "growx"]
                 [(seesaw/button
                   :text "Transform!"
                   :size [140 :by 50]
                   :listen [:action on-transform]) "gaptop 20, align center"]]))

