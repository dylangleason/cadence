;;;;==================================================================
;;;; File     : ui_player.clj
;;;; Author   : Dylan Gleason
;;;; Date     : 2013-12-28
;;;; Modified : 2013-03-07
;;;;
;;;; Contains widgets for MIDI player and their associated event
;;;; handlers
;;;;==================================================================

(in-ns 'composer.ui.core)

;;; event handlers for the MIDI player

(defn on-save [e]
  (when-not (nil? @midi-sequence)
    (let [file-path (make-choose-file-ui :save)]
      (when-not (nil? file-path)
        (io/write-midi-file @midi-sequence file-path)))))

(defn on-open [e]
  (let [file-path (make-choose-file-ui :open)]
    (when-not (= file-path "No File Loaded")
      (reset! midi-sequence (io/read-midi-file file-path))      
      (player/load-sequence @midi-sequencer @midi-sequence)
      (seesaw/text! @bpm-ui (player/get-bpm @midi-sequencer))
      (seesaw/text! @loaded-file-ui file-path))))

(defn on-reload [e]
  (let [file-path (seesaw/text @loaded-file-ui)]
    (when-not (= file-path "No File Loaded")
      (reset! midi-sequence (io/read-midi-file file-path))
      (player/load-sequence @midi-sequencer @midi-sequence))))

(defn on-bpm-set [e]
  (let [bpm (seesaw/text @bpm-ui)]
    (when-not (some nil? [bpm @midi-sequence])
      (player/set-bpm @midi-sequencer (read-string bpm)))))

(defn on-play [e]
  (when-not (nil? @midi-sequence)
    (on-bpm-set nil)
    (player/start @midi-sequencer)))

(defn on-stop [e]
  (when-not (nil? @midi-sequence)
    (player/stop @midi-sequencer)))

(defn on-pause [e]
  (when-not (nil? @midi-sequence)
    (player/pause @midi-sequencer)))

(defn on-exit [e]
  (do (player/close @midi-sequencer)
      (java.lang.System/exit 0)))

;;; MIDI player widgets

(reset! loaded-file-ui
        (seesaw/text
         :font :monospaced
         :multi-line? true
         :wrap-lines? true
         :margin 5
         :rows 10
         :size [385 :by 125]
         :text "No File Loaded"
         :editable? false))

(reset! bpm-ui
        (seesaw/text
         :font :monospaced
         :columns 10
         :multi-line? false
         :editable? true))

(reset! player-options-ui
        (mig/mig-panel
         :constraints ["" "" ""]
         :items [[(seesaw/button
                   :size [75 :by 25]
                   :text "Reload"
                   :listen [:action on-reload]) "west"]
                 [(seesaw/flow-panel
                   :items
                   [(seesaw/label :text "BPM")
                    @bpm-ui
                    (seesaw/button
                     :size [50 :by 25]
                     :text "Set!"
                     :listen [:action on-bpm-set])]) "gapleft 135"]]))

(reset! midi-player-ui
        (mig/mig-panel
         :bounds [:* :* :* 305]
         :border "MIDI Player"
         :constraints ["wrap" "" ""]
         :items [[@player-options-ui]
                 [@loaded-file-ui ""]
                 [(seesaw/flow-panel
                   :align :center
                   :items
                   [(seesaw/button
                     :icon (seesaw/icon (clojure.java.io/resource "play.png"))
                     :size [100 :by 50]
                     :listen [:action on-play])
                    (seesaw/button
                     :icon (seesaw/icon (clojure.java.io/resource "pause.png"))
                     :size [100 :by 50]
                     :listen [:action on-pause])
                    (seesaw/button
                     :icon (seesaw/icon (clojure.java.io/resource "stop.png"))
                     :size [100 :by 50]
                     :listen [:action on-stop])]) "gaptop 30, gapleft 35"]]))
