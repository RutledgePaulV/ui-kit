(ns ui-kit.core
  (:require [ui-kit.semantic :as sa]
            [ui-kit.utils :as utils]
            [ui-kit.lenses :as lens]
            [malli.provider :as mp]
            [malli.core :as m]))

(defn dispatch-fun [form]
  (:name form))

(defmulti compile-form
  (fn [& args] (apply dispatch-fun args)))

(defn error-message [{:keys [path schema]} message]
  [sa/message {:error true}
   [sa/message-content
    [:div
     [:p message]
     [:code [:pre (utils/ppstr {:path path :schema (m/form schema)})]]]]])

(defmethod compile-form :default [node]
  (error-message node "You must add a defmethod to ui-kit.core/compile-form to use this schema."))

(defmethod compile-form :and [{:keys [children] :as node}]
  (into [sa/form-group {}]
        (for [[index child] (map-indexed vector children)]
          (lens/assoc-attr child :key index))))

(defmethod compile-form :or [{:keys [props children] :as node}]
  (let [selected-option (reagent.core/atom 0)]
    (fn []
      [:div {}
       [sa/form-field
        [:label (or (:sui/label props) "Select variant")]
        [sa/form-select
         {:options   (for [[index child] (map-indexed vector children)]
                       {:key index :value index :text (str "Option " index)})
          :on-change #(reset! selected-option (.-value %2))}]]
       [sa/form-group
        (nth children @selected-option)]])))

(defmethod compile-form :map [node]
  [sa/form-group
   (for [[attr props child] (:children node)]
     (let [label (utils/kebab->title attr)]
       (with-meta (lens/set-label-if-unset child label) {:key attr})))])

(defmethod compile-form :multi [{:keys [props children] :as node}]
  (let [dispatch (get-in props [:dispatch])]
    (cond
      (keyword? dispatch)
      (let [label           (utils/kebab->title dispatch)
            by-dv           (into {} (map (juxt first identity)) children)
            selected-option (reagent.core/atom nil)]
        (fn []
          [:div {}
           [sa/form-field {:required (not (:optional props false))}
            [:label label]
            [sa/form-select
             {:options   (for [[dispatch-value] children]
                           {:key dispatch-value :value dispatch-value :text dispatch-value})
              :on-change #(reset! selected-option (keyword (.-value %2)))}]]
           [sa/form-group {}
            (some-> (get by-dv @selected-option) (nth 2))]]))
      :otherwise
      (error-message node "only multi schemas that dispatch by keyword or equality are supported."))))

(defmethod compile-form :maybe [{[child] :children :as node}]
  (lens/assoc-attr child :required false))

(defmethod compile-form :vector [{[child-template] :children :as node}]
  (let [children (reagent.core/atom [{:id (random-uuid) :node child-template}])]
    (fn []
      [:div {}
       (let [childs @children]
         (for [[index {:keys [id node]}] (map-indexed vector childs)]
           [sa/form-group {:inline true :key id}
            node
            [sa/button {:icon     true
                        :disabled (= 0 index)
                        :on-click (fn []
                                    (swap! children
                                           (fn [old]
                                             (utils/vec-swap old index (dec index)))))}
             [sa/icon {:name "arrow up" :size :small}]]
            [sa/button {:icon     true
                        :disabled (= (count childs) (inc index))
                        :on-click (fn []
                                    (swap! children
                                           (fn [old]
                                             (utils/vec-swap old index (inc index)))))}
             [sa/icon {:name "arrow down" :size :small}]]
            [sa/button {:icon     true
                        :on-click (fn []
                                    (swap! children
                                           (fn [old]
                                             (utils/vec-remove old index))))}
             [sa/icon {:name "delete" :size :small}]]]))
       [sa/button {:icon     true
                   :on-click (fn []
                               (swap! children
                                      (fn [old]
                                        (conj old {:id   (random-uuid)
                                                   :node child-template}))))}
        [sa/icon {:name "plus" :size :large}]]])))

(defmethod compile-form :tuple [{:keys [children] :as node}]
  (into [sa/form-group {:inline true}]
        (for [[index child] (map-indexed vector children)]
          ^{:key index} child)))

(defn simple-input [{:keys [props data path] :as node} input-field]
  (let [all-attrs (utils/select-ns props :sui)]
    [sa/form-field
     (merge {:required (not (:optional props false))} (dissoc all-attrs :label))
     (when-some [label (:label all-attrs)] [:label label])
     (cond-> input-field
       (some? (get-in data path))
       (lens/assoc-attr :value (get-in data path))
       (and (nil? (get-in data path)) (some? (:default props)))
       (lens/assoc-attr :value (:default props)))]))

(defmethod compile-form :enum [{:keys [schema] :as node}]
  (let [raw-children (m/children schema)]
    (simple-input node [sa/form-select
                        {:options (for [child raw-children]
                                    {:key child :value child :text child})}])))

(defmethod compile-form := [{:keys [schema] :as node}]
  (let [[value] (m/children schema)]
    (simple-input node [sa/form-input {:value value :disabled true}])))

(defmethod compile-form :re [node]
  (simple-input node [sa/form-input {:type :text}]))

(defmethod compile-form 'string? [node]
  (simple-input node [sa/form-input {:type :text}]))

(defmethod compile-form 'int? [node]
  (simple-input node [sa/form-input {:type :number}]))

(defmethod compile-form 'integer? [node]
  (simple-input node [sa/form-input {:type :number}]))

(defmethod compile-form 'boolean? [node]
  (simple-input node [sa/form-input {:type :checkbox}]))

(defmethod compile-form 'pos-int? [node]
  (simple-input node [sa/form-input {:type :number :min 1}]))

(defmethod compile-form 'neg-int? [node]
  (simple-input node [sa/form-input {:type :number :max -1}]))

(defmethod compile-form 'nat-int? [node]
  (simple-input node [sa/form-input {:type :number :min 0}]))

(defmethod compile-form 'uri? [node]
  (simple-input node [sa/form-input {:type :url}]))

(defmethod compile-form 'inst? [node]
  (simple-input node [sa/form-input {:type :datetime-local}]))

(defn compile-form-wrapper [data schema children path options]
  (let [schema-name  (m/name schema)
        schema-props (m/properties schema)]
    (compile-form
      {:name     schema-name
       :schema   schema
       :data     data
       :props    schema-props
       :children children
       :path     path
       :options  options})))

(defn schema->form
  ([schema]
   (schema->form schema nil))
  ([schema data]
   (let [result (m/accept schema (partial compile-form-wrapper data))]
     (if (fn? result)
       [sa/form {:warning true :error true} [result]]
       [sa/form {:warning true :error true} result]))))

(defn data->form
  ([data]
   (data->form data data))
  ([data-for-schema data]
   (schema->form (mp/provide [data-for-schema]) data)))