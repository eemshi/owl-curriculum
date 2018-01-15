(ns owlet.components.interactive.create-klipse-panel
  (:require [owlet.components.interactive.klipse :refer [klipse-component]]
            [reagent.core :as reagent]
            [cljsjs.simplemde]))

(defn remount-klipse [remount?]
  (js/setTimeout #(swap! remount? not) 100))

(defn create-klipse-panel-component [panel-number]
  (let [text-id-base (str "panel-" panel-number "-text-")
        language (reagent/atom "python")
        remount? (reagent/atom true)]
    (reagent/create-class
      {:component-did-mount
       (fn []
         (let [smde-1 (js/SimpleMDE. #js {:element (js/document.querySelector (str "#" text-id-base "1"))
                                          :lineWrapping true})
               smde-2 (js/SimpleMDE. #js {:element (js/document.querySelector (str "#" text-id-base "2"))
                                          :lineWrapping true})]))
       :reagent-render
       (fn [panel-number]
        [:div.activity-info-wrap.box-shadow
         [:div.panel-heading.flexcontainer-wrap
          [:div.panel-number (str panel-number)]
          [:div {:style {:width "82%"}}
           [:h2 [:textarea {:id (str "panel-" panel-number "-heading")
                            :rows "2"
                            :placeholder "Heading"}]]]]
         [:div.panel-text
          [:textarea {:id (str text-id-base "1")
                      :placeholder "Optional text (markdown)"}]]
         [:span {:style {:font-weight "500"
                         :margin "0 0.3em 0 0.05em"}}
                [:mark "Code Evaluator"]]
         [:select {:id (str "panel-" panel-number "-language")
                   :value @language
                   :on-change (fn [e]
                                (when (not= @language (-> e .-target .-value))
                                  (swap! remount? not)
                                  (remount-klipse remount?))
                                (reset! language (-> e .-target .-value)))}
          [:option {:value ""} "None"]
          [:option {:value "python"} "Python"]
          [:option {:value "javascript"} "JavaScript"]
          [:option {:value "clojure"} "Clojure"]]
         [:div.panel-klipse
          (when @remount?
            (case @language
              "python" [klipse-component @language "# type here"]
              "javascript" [klipse-component @language "// type here"]
              "clojure" [klipse-component @language ";; type here"]
              "" [:h2 ""]))]
         [:div.panel-text
          [:textarea {:id (str text-id-base "2")
                      :placeholder "Optional text (markdown)"}]]])})))
