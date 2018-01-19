(ns owlet.components.interactive.create-klipse-code-validation
  (:require [reagent.core :as reagent]))

(defn create-klipse-code-validation-component [specify-number?]
  (if specify-number?
    [:div
     [:textarea.pane-number {:rows "1"
                             :placeholder "#"}]
     [:textarea.code-validation {:rows "3"
                                 :placeholder "Expected output"
                                 :style {:width "91%"}}]]
    [:div
     [:textarea.code-validation {:rows "3"
                                 :placeholder "Expected output"
                                 :style {:width "100%"}}]]))
