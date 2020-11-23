(ns chess.core
  (:require [rum.core :as rum]))

(enable-console-print!)

(println "This text is printed from src/chess/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state
  (atom
   {:board [["rook", "knight", "bishop", "king", "queen", "bishop", "knight", "rook"]
            ["pawn", "pawn", "pawn", "pawn", "pawn", "pawn", "pawn", "pawn"]
            ["-", "-", "-", "-", "-", "-", "-", "-"]
            ["-", "-", "-", "-", "-", "-", "-", "-"]
            ["-", "-", "-", "-", "-", "-", "-", "-"]
            ["-", "-", "-", "-", "-", "-", "-", "-"]
            ["pawn", "pawn", "pawn", "pawn", "pawn", "pawn", "pawn", "pawn"]
            ["rook", "knight", "bishop", "king", "queen", "bishop", "knight", "rook"]]}))

(defn inc-counter [current-app-state] (update-in current-app-state [:counter] inc)) ;; No need to deref it

(defn render-piece [x y piece] [:td {:on-click (fn [e] (print e))} x " " y " " piece])

(rum/defc hello-world < rum/reactive
  []
  (let [state (rum/react app-state)] ;; Register a listener to tell this component to react to the state.
    [:div
     [:table
      (for [[x row] (map-indexed vector (:board state))]
        [:tr
         (for [[y piece] (map-indexed vector row)] (render-piece x y piece))
         ])]]))

(rum/mount (hello-world)
           (. js/document (getElementById "app"))) ;; Here's how you use JS's dot operator

; TODO
; - Define movement rules per-piece
; - Give the pieces colors
; - Add interaction for moving the pieces