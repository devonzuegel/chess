(ns chess.core
  (:require [rum.core :as rum]))

(enable-console-print!)

;; define your app data so that doesn't get over-written on reload
(defonce app-state
  (atom
   {:selected-piece nil
    :board [["rook ♖", "knight ♘", "bishop ♗", "king ♔", "queen ♕", "bishop ♗", "knight ♘", "rook ♖"]
            ["pawn ♙", "pawn ♙", "pawn ♙", "pawn ♙", "pawn ♙", "pawn ♙", "pawn ♙", "pawn ♙"]
            [nil, nil, nil, nil, nil, nil, nil, nil]
            [nil, nil, nil, nil, nil, nil, nil, nil]
            [nil, nil, nil, nil, nil, nil, nil, nil]
            [nil, nil, nil, nil, nil, nil, nil, nil]
            ["pawn ♟", "pawn ♟", "pawn ♟", "pawn ♟", "pawn ♟", "pawn ♟", "pawn ♟", "pawn ♟"]
            ["rook ♜", "knight ♞", "bishop ♝", "king ♚", "queen ♛", "bishop ♝", "knight ♞", "rook ♜"]]}))

; (defn inc-counter [current-app-state] (update-in current-app-state [:counter] inc)) ;; No need to deref it
(defn update-selected-piece [current-app-state x y] (assoc-in current-app-state [:selected-piece] [x y])) ;; No need to deref it

(defn clear-selected-piece [current-app-state] (assoc-in current-app-state [:selected-piece] nil)) ;; No need to deref it

(defn get-current-selected-piece [current-app-state] (get-in current-app-state (concat [:board] (:selected-piece current-app-state))))
; (defn get-current-selected-piece [] [:board @app-state (:selected-piece @app-state)])

(defn render-piece [x y piece]
  [:td
   {:key y
    :on-click (fn [e]
                (if (= (:selected-piece @app-state) nil)
                  (do ; Pick up the piece at the x,y coordinate
                    (print "Pick up the piece")
                    (swap! app-state (fn [app-state] (update-selected-piece app-state x y))))
                  (do ; Place the piece in the new x,y coordinates
                    (print "Moving the piece that was at " x y " to _ _")
                    (swap! app-state (fn [app-state]
                                       (-> app-state
                                           (assoc-in [:board x y] (get-current-selected-piece app-state))
                                           (assoc-in (concat [:board] (:selected-piece app-state)) nil)
                                           (clear-selected-piece)))))))}
   [:span {:class "coordinates"} x "," y]
   [:div {:class "piece-name"} piece]])

(defn show-selected-piece [state]
  [:div "selected-piece: " (let [[_x _y] (:selected-piece state)] (str _x ", " _y))])

(defn render-row [x row]
  [:tr {:key x} (for [[y piece] (map-indexed vector row)] (render-piece x y piece))])

(rum/defc hello-world < rum/reactive
  []
  (let [state (rum/react app-state)] ; Register a listener to tell this component to react to the state.
    [:div
     (show-selected-piece state)
     [:pre "app state: " (pr-str @app-state)]
     [:table [:tbody
              (for [[x row] (map-indexed vector (:board state))]
                (render-row x row))]]]))

(rum/mount (hello-world)
           (. js/document (getElementById "app"))) ;; Here's how you use JS's dot operator

; TODO
; - Define movement rules per-piece
; - Give the pieces colors
; - Add interaction for moving the pieces
; - Track moves in an array