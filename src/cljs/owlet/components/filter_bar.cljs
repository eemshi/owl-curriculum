(ns owlet.components.filter-bar
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]
            [cljsjs.jquery]
            [goog.string :as goog-string]))

(defonce filters (reagent/atom '()))

(defn element-is-in-view [el right?]
  (let [rect (.getBoundingClientRect el)
        parent-rect (.getBoundingClientRect (.-parentElement el))]
      (if right?
        (>= (.-right rect) (.-right parent-rect))
        (<= (.-left rect) (.-left parent-rect)))))

(defn scroll-filters [right?]
  (let [filter-elements (array-seq (js/document.getElementsByClassName "filter"))
        scroll-to-rect (if right?
                        (.getBoundingClientRect (first (filter #(element-is-in-view % right?) filter-elements)))
                        (.getBoundingClientRect (last (filter #(element-is-in-view % right?) filter-elements))))
        filter-items-rect (.getBoundingClientRect (js/document.getElementById "filter-items"))
        current-scroll (.scrollLeft (js/jQuery "#filter-items"))]
    (if right?
      (.scrollLeft (js/jQuery "#filter-items") (+ current-scroll (- (.-left scroll-to-rect) (.-left filter-items-rect))))
      (.scrollLeft (js/jQuery "#filter-items") (+ current-scroll (- (.-right scroll-to-rect) (.-right filter-items-rect)))))))

(defn toggle-filter [e filter-term]
  (if (.-checked (.-target e))
   (rf/dispatch [:filter-activities-by-selected-terms (reset! filters (distinct (conj @filters filter-term)))])
   (rf/dispatch [:filter-activities-by-selected-terms (reset! filters (distinct (remove #(= % filter-term) @filters)))])))

(defn is-checked? [filter]
  (let [activities-by-filter @(rf/subscribe [:activities-by-filter])]
    (or (= filter (:pre-filter activities-by-filter))
        (some #(= filter %) (:filters activities-by-filter)))))

(defn filter-bar []
  (if (some #(= @(rf/subscribe [:active-view]) %) [:branches-view :filtered-activities-view])
    [:div#filter-bar
     (rf/dispatch [:filter-bar-terms])
     [:span.arrow-left {:on-click #(scroll-filters false)}
      (goog-string/unescapeEntities "&lt;")]
     [:div#filter-items
      (doall
        (for [term @(rf/subscribe [:filter-bar-terms])
              :let [name (:name term)
                    type (:type term)
                    filter-term (hash-map :name (:name term)
                                          :type (:type term))]]
           (case type
             "Branch"   ^{:key (gensym "branch-")}
                         [:div.filter
                          [:input {:id (str name "-filter")
                                   :type "checkbox"
                                   :on-click #(toggle-filter % filter-term)
                                   :defaultChecked (is-checked? filter-term)}]
                          [:label {:for (str name "-filter")}
                           (clojure.string/upper-case name)]]
             "Platform" ^{:key (gensym "platform-")}
                         [:div.filter
                          [:input {:id (str name "-filter")
                                   :type "checkbox"
                                   :on-click #(toggle-filter % filter-term)
                                   :defaultChecked (is-checked? filter-term)}]
                          [:label {:for (str name "-filter")}
                           (clojure.string/upper-case name)]]
             "Tag"      ^{:key (gensym "tag-")}
                         [:div.filter
                          [:input {:id (str name "-filter")
                                   :type "checkbox"
                                   :on-click #(toggle-filter % filter-term)
                                   :defaultChecked (is-checked? filter-term)}]
                          [:label {:for (str name "-filter")}
                           (clojure.string/upper-case name)]])))]
     [:span.arrow-right {:on-click #(scroll-filters true)}
      (goog-string/unescapeEntities "&gt;")]]
    [:div#spacer]))
