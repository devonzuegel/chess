(ns chess.core
  (:require [rum.core :as rum] [clojure.pprint :as pp]))

(enable-console-print!)

;; define your app data so that doesn't get over-written on reload
(defonce app-state
  (atom
   {:selected-piece nil
    :moves []
    :board [[:black/rook :black/knight :black/bishop :black/king :black/queen :black/bishop :black/knight :black/rook]
            [:black/pawn :black/pawn :black/pawn :black/pawn :black/pawn :black/pawn :black/pawn :black/pawn]
            [nil, nil, nil, nil, nil, nil, nil, nil]
            [nil, nil, nil, nil, nil, nil, nil, nil]
            [nil, nil, nil, nil, nil, nil, nil, nil]
            [nil, nil, nil, nil, nil, nil, nil, nil]
            [:white/rook :white/knight :white/bishop :white/king :white/queen :white/bishop :white/knight :white/rook]
            [:white/pawn :white/pawn :white/pawn :white/pawn :white/pawn :white/pawn :white/pawn :white/pawn]]}))

(defn get-color [piece] (keyword (namespace piece)))

(defn get-piece [piece] (keyword (name piece)))

(defn render-piece-emoji [piece] (case piece
                                   :black/bishop "♝"
                                   :black/king "♚"
                                   :black/knight "♞"
                                   :black/pawn "♟"
                                   :black/queen "♛"
                                   :black/rook "♜"
                                   :white/bishop "♗"
                                   :white/king "♔"
                                   :white/knight "♘"
                                   :white/pawn "♙"
                                   :white/queen "♕"
                                   :white/rook "♖"
                                   nil))

; (defn inc-counter [current-app-state] (update-in current-app-state [:counter] inc)) ;; No need to deref it
(defn update-selected-piece [current-app-state col row] (assoc-in current-app-state [:selected-piece] [col row])) ;; No need to deref it

(defn clear-selected-piece [current-app-state] (assoc-in current-app-state [:selected-piece] nil)) ;; No need to deref it

(defn get-current-selected-piece [current-app-state]
  (get-in current-app-state
          (concat [:board] (:selected-piece current-app-state))))
; (defn get-current-selected-piece [] [:board @app-state (:selected-piece @app-state)])

(defn move-allowed? [new-coords current-coords]
  (let [selected-piece  (get-current-selected-piece @app-state)]
    (print "current selected piece:" selected-piece)
    (print (get-color selected-piece))
    (print (get-piece selected-piece))
    (print "new-coords:" new-coords "; current-coords" current-coords)
  ; TODO: Add logic for testing if moves are allowed
    true))

(defn click-piece [col row e]
  (if (= (:selected-piece @app-state) nil)
    (do ; Pick up the piece at the col,y coordinate
      ; (print "Pick up the piece")
      (swap! app-state (fn [app-state] (update-selected-piece app-state col row))))
    (if (move-allowed? [col row] (:selected-piece @app-state))
      (do ; Place the piece in the new col,y coordinates
        ; (print "Moving the piece that was at " col row " to _ _")
        (swap! app-state (fn [app-state]
                           (-> app-state 
                               ; This function threads the preceding return value as the front argument of the next fn
                               (assoc-in [:board col row] (get-current-selected-piece app-state))
                               (assoc-in (concat [:board] (:selected-piece app-state)) nil)
                               (clear-selected-piece))))))))

(defn render-piece [col row piece]
  [:td
   {:key row
    :class (if (= (:selected-piece @app-state) [col row]) "selected" "not-selected")
    :on-click (partial click-piece col row)}
   [:div {:class "coordinates"} col "," row]
   [:div {:class "piece-name"} (render-piece-emoji piece)]])

(defn render-row [col row]
  [:tr {:key col} (for [[row piece] (map-indexed vector row)] (render-piece col row piece))])

(rum/defc app < rum/reactive
  []
  (let [state (rum/react app-state)] ; Register a listener to tell this component to react to the state.
    [:div
     [:pre "app state: " (pr-str @app-state)]
     [:table [:tbody
              (for [[col row] (map-indexed vector (:board state))]
                (render-row col row))]]]))

(rum/mount (app)
           (. js/document (getElementById "app"))) ;; Here's how you use JS's dot operator

; TODO: Define movement rules per-piece
; TODO: Give the pieces colors
; TODO: Add interaction for moving the pieces
; TODO: Track moves in an array
; TODO: Deselect piece upon clicking "esc"
; TODO: Only allow selection of a piece, not an empty space
; TODO: Show all possible moves