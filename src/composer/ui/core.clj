;;;;==================================================================
;;;; File     : main.clj
;;;; Author   : Dylan Gleason
;;;; Date     : 2013-12-28
;;;; Modified : 2013-12-30
;;;;
;;;; Contains main UI definitions and loads other UI definitions
;;;;==================================================================

(ns composer.ui.core
  (:require [seesaw
             [mig :as mig]
             [core :as seesaw]
             [border :as border]
             [chooser :as chooser]])
  (:require [composer.midi
             [io :as io]
             [time :as time]
             [player :as player]
             [short-message :as short]])
  (:gen-class))

(seesaw/native!)

;;; load each ui file in the current namespace

(load "ui_state")
(load "ui_helper")
(load "ui_player")
(load "ui_algorithm")

;;; set the main UI widget state

(reset! splitter-ui
        (mig/mig-panel
         :constraints ["" "" ""]
         :items [[@algorithm-ui ""]
                 [@midi-player-ui "grow"]]))

(reset! menubar-ui
        (let [open (seesaw/menu-item
                    :text "Open"
                    :mnemonic \O
                    :listen [:action on-open])
              save (seesaw/menu-item
                    :text "Save"
                    :mnemonic \S
                    :listen [:action on-save])
              exit (seesaw/menu-item
                    :text "Exit"
                    :mnemonic \E
                    :listen [:action on-exit])]
          (seesaw/menubar
           :items [(seesaw/menu
                    :text "File"
                    :mnemonic \F
                    :items [open save exit])
                   (seesaw/menu
                    :text "Help"
                    :mnemonic \H
                    :items [(seesaw/menu-item
                             :text "About"
                             :mnemonic \A)])])))

(reset! main-ui
        (seesaw/frame
         :title "Cadence"
         :content @splitter-ui
         :menubar @menubar-ui
         :minimum-size [500 :by 100]
         :resizable? false
         :on-close :exit))

(defn init
  "Initializes the main UI and event listeners"
  []
  (do
    (seesaw/selection! @transpose-selector-ui 0)
    (reset! midi-sequencer (player/get-sequencer))
    (-> @main-ui seesaw/pack! seesaw/show!)))

