;;;;==================================================================
;;;; File     : core.clj
;;;; Author   : Dylan Gleason
;;;; Date     : 2013-10-15
;;;; Modified : 2013-11-17
;;;;
;;;; Contains the main program for interacting with the algorithmic
;;;; composer.
;;;;==================================================================

(ns composer.core
  (:require [composer.ui.core :as ui])
  (:gen-class))

(defn -main
  [& args]
  (ui/init))
