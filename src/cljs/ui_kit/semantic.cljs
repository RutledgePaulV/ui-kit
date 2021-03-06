(ns ui-kit.semantic
  "SemanticUI as reagent components.
   Find documentation at https://react.semantic-ui.com/
   Calendar component at https://github.com/arfedulov/semantic-ui-calendar-react"
  (:require
    [reagent.core :as r]
    [cljsjs.semantic-ui-react]
    [reagent.impl.template :as template]
    [ui-kit.utils :as utils]
    [cljsjs.semantic-ui-calendar-react])
  (:refer-clojure :exclude [comment list]))

(defonce constructor->name (atom {}))

(defn component [module class]
  (let [clazz    (aget module class)
        realized (r/adapt-react-class clazz)
        name     (symbol "ui-kit.semantic" (utils/camel->kebab class))]
    (swap! constructor->name assoc realized name)
    realized))

(extend-protocol cljs.core/IPrintWithWriter
  template/NativeWrapper
  (-pr-writer [o writer opts]
    (if-some [name (get @constructor->name o)]
      (write-all writer name)
      (write-all writer "#object[reagent.impl.template.NativeWrapper]"))))

(def accordion (component js/semanticUIReact "Accordion"))
(def accordion-accordion (component js/semanticUIReact "AccordionAccordion"))
(def accordion-content (component js/semanticUIReact "AccordionContent"))
(def accordion-panel (component js/semanticUIReact "AccordionPanel"))
(def accordion-title (component js/semanticUIReact "AccordionTitle"))
(def advertisement (component js/semanticUIReact "Advertisement"))
(def breadcrumb (component js/semanticUIReact "Breadcrumb"))
(def breadcrumb-divider (component js/semanticUIReact "BreadcrumbDivider"))
(def breadcrumb-section (component js/semanticUIReact "BreadcrumbSection"))
(def button (component js/semanticUIReact "Button"))
(def button-content (component js/semanticUIReact "ButtonContent"))
(def button-group (component js/semanticUIReact "ButtonGroup"))
(def button-or (component js/semanticUIReact "ButtonOr"))
(def card (component js/semanticUIReact "Card"))
(def card-content (component js/semanticUIReact "CardContent"))
(def card-description (component js/semanticUIReact "CardDescription"))
(def card-group (component js/semanticUIReact "CardGroup"))
(def card-header (component js/semanticUIReact "CardHeader"))
(def card-meta (component js/semanticUIReact "CardMeta"))
(def checkbox (component js/semanticUIReact "Checkbox"))
(def comment (component js/semanticUIReact "Comment"))
(def comment-action (component js/semanticUIReact "CommentAction"))
(def comment-actions (component js/semanticUIReact "CommentActions"))
(def comment-author (component js/semanticUIReact "CommentAuthor"))
(def comment-avatar (component js/semanticUIReact "CommentAvatar"))
(def comment-content (component js/semanticUIReact "CommentContent"))
(def comment-group (component js/semanticUIReact "CommentGroup"))
(def comment-metadata (component js/semanticUIReact "CommentMetadata"))
(def comment-text (component js/semanticUIReact "CommentText"))
(def confirm (component js/semanticUIReact "Confirm"))
(def container (component js/semanticUIReact "Container"))
(def date-input (component js/SemanticUiCalendarReact "DateInput"))
(def date-time-input (component js/SemanticUiCalendarReact "DateTimeInput"))
(def dates-range-input (component js/SemanticUiCalendarReact "DatesRangeInput"))
(def dimmer (component js/semanticUIReact "Dimmer"))
(def dimmer-dimmable (component js/semanticUIReact "DimmerDimmable"))
(def dimmer-inner (component js/semanticUIReact "DimmerInner"))
(def divider (component js/semanticUIReact "Divider"))
(def dropdown (component js/semanticUIReact "Dropdown"))
(def dropdown-divider (component js/semanticUIReact "DropdownDivider"))
(def dropdown-header (component js/semanticUIReact "DropdownHeader"))
(def dropdown-item (component js/semanticUIReact "DropdownItem"))
(def dropdown-menu (component js/semanticUIReact "DropdownMenu"))
(def dropdown-search-input (component js/semanticUIReact "DropdownSearchInput"))
(def embed (component js/semanticUIReact "Embed"))
(def feed (component js/semanticUIReact "Feed"))
(def feed-content (component js/semanticUIReact "FeedContent"))
(def feed-date (component js/semanticUIReact "FeedDate"))
(def feed-event (component js/semanticUIReact "FeedEvent"))
(def feed-extra (component js/semanticUIReact "FeedExtra"))
(def feed-label (component js/semanticUIReact "FeedLabel"))
(def feed-like (component js/semanticUIReact "FeedLike"))
(def feed-meta (component js/semanticUIReact "FeedMeta"))
(def feed-summary (component js/semanticUIReact "FeedSummary"))
(def feed-user (component js/semanticUIReact "FeedUser"))
(def flag (component js/semanticUIReact "Flag"))
(def form (component js/semanticUIReact "Form"))
(def form-button (component js/semanticUIReact "FormButton"))
(def form-checkbox (component js/semanticUIReact "FormCheckbox"))
(def form-dropdown (component js/semanticUIReact "FormDropdown"))
(def form-field (component js/semanticUIReact "FormField"))
(def form-group (component js/semanticUIReact "FormGroup"))
(def form-input (component js/semanticUIReact "FormInput"))
(def form-radio (component js/semanticUIReact "FormRadio"))
(def form-select (component js/semanticUIReact "FormSelect"))
(def form-text-area (component js/semanticUIReact "FormTextArea"))
(def grid (component js/semanticUIReact "Grid"))
(def grid-column (component js/semanticUIReact "GridColumn"))
(def grid-row (component js/semanticUIReact "GridRow"))
(def header (component js/semanticUIReact "Header"))
(def header-content (component js/semanticUIReact "HeaderContent"))
(def header-subheader (component js/semanticUIReact "HeaderSubheader"))
(def icon (component js/semanticUIReact "Icon"))
(def icon-group (component js/semanticUIReact "IconGroup"))
(def image (component js/semanticUIReact "Image"))
(def image-group (component js/semanticUIReact "ImageGroup"))
(def input (component js/semanticUIReact "Input"))
(def item (component js/semanticUIReact "Item"))
(def item-content (component js/semanticUIReact "ItemContent"))
(def item-description (component js/semanticUIReact "ItemDescription"))
(def item-extra (component js/semanticUIReact "ItemExtra"))
(def item-group (component js/semanticUIReact "ItemGroup"))
(def item-header (component js/semanticUIReact "ItemHeader"))
(def item-image (component js/semanticUIReact "ItemImage"))
(def item-meta (component js/semanticUIReact "ItemMeta"))
(def label (component js/semanticUIReact "Label"))
(def label-detail (component js/semanticUIReact "LabelDetail"))
(def label-group (component js/semanticUIReact "LabelGroup"))
(def list (component js/semanticUIReact "List"))
(def list-content (component js/semanticUIReact "ListContent"))
(def list-description (component js/semanticUIReact "ListDescription"))
(def list-header (component js/semanticUIReact "ListHeader"))
(def list-icon (component js/semanticUIReact "ListIcon"))
(def list-item (component js/semanticUIReact "ListItem"))
(def list-list (component js/semanticUIReact "ListList"))
(def loader (component js/semanticUIReact "Loader"))
(def menu (component js/semanticUIReact "Menu"))
(def menu-header (component js/semanticUIReact "MenuHeader"))
(def menu-item (component js/semanticUIReact "MenuItem"))
(def menu-menu (component js/semanticUIReact "MenuMenu"))
(def message (component js/semanticUIReact "Message"))
(def message-content (component js/semanticUIReact "MessageContent"))
(def message-header (component js/semanticUIReact "MessageHeader"))
(def message-item (component js/semanticUIReact "MessageItem"))
(def message-list (component js/semanticUIReact "MessageList"))
(def modal (component js/semanticUIReact "Modal"))
(def modal-actions (component js/semanticUIReact "ModalActions"))
(def modal-content (component js/semanticUIReact "ModalContent"))
(def modal-description (component js/semanticUIReact "ModalDescription"))
(def modal-header (component js/semanticUIReact "ModalHeader"))
(def month-input (component js/SemanticUiCalendarReact "MonthInput"))
(def month-range-input (component js/SemanticUiCalendarReact "MonthRangeInput"))
(def mount-node (component js/semanticUIReact "MountNode"))
(def pagination (component js/semanticUIReact "Pagination"))
(def pagination-item (component js/semanticUIReact "PaginationItem"))
(def placeholder (component js/semanticUIReact "Placeholder"))
(def placeholder-header (component js/semanticUIReact "PlaceholderHeader"))
(def placeholder-image (component js/semanticUIReact "PlaceholderImage"))
(def placeholder-line (component js/semanticUIReact "PlaceholderLine"))
(def placeholder-paragraph (component js/semanticUIReact "PlaceholderParagraph"))
(def popup (component js/semanticUIReact "Popup"))
(def popup-content (component js/semanticUIReact "PopupContent"))
(def popup-header (component js/semanticUIReact "PopupHeader"))
(def portal (component js/semanticUIReact "Portal"))
(def portal-inner (component js/semanticUIReact "PortalInner"))
(def progress (component js/semanticUIReact "Progress"))
(def radio (component js/semanticUIReact "Radio"))
(def rail (component js/semanticUIReact "Rail"))
(def rating (component js/semanticUIReact "Rating"))
(def rating-icon (component js/semanticUIReact "RatingIcon"))
(def ref (component js/semanticUIReact "Ref"))
(def responsive (component js/semanticUIReact "Responsive"))
(def reveal (component js/semanticUIReact "Reveal"))
(def reveal-content (component js/semanticUIReact "RevealContent"))
(def search (component js/semanticUIReact "Search"))
(def search-category (component js/semanticUIReact "SearchCategory"))
(def search-result (component js/semanticUIReact "SearchResult"))
(def search-results (component js/semanticUIReact "SearchResults"))
(def segment (component js/semanticUIReact "Segment"))
(def segment-group (component js/semanticUIReact "SegmentGroup"))
(def segment-inline (component js/semanticUIReact "SegmentInline"))
(def select (component js/semanticUIReact "Select"))
(def sidebar (component js/semanticUIReact "Sidebar"))
(def sidebar-pushable (component js/semanticUIReact "SidebarPushable"))
(def sidebar-pusher (component js/semanticUIReact "SidebarPusher"))
(def statistic (component js/semanticUIReact "Statistic"))
(def statistic-group (component js/semanticUIReact "StatisticGroup"))
(def statistic-label (component js/semanticUIReact "StatisticLabel"))
(def statistic-value (component js/semanticUIReact "StatisticValue"))
(def step (component js/semanticUIReact "Step"))
(def step-content (component js/semanticUIReact "StepContent"))
(def step-description (component js/semanticUIReact "StepDescription"))
(def step-group (component js/semanticUIReact "StepGroup"))
(def step-title (component js/semanticUIReact "StepTitle"))
(def sticky (component js/semanticUIReact "Sticky"))
(def tab (component js/semanticUIReact "Tab"))
(def tab-pane (component js/semanticUIReact "TabPane"))
(def table (component js/semanticUIReact "Table"))
(def table-body (component js/semanticUIReact "TableBody"))
(def table-cell (component js/semanticUIReact "TableCell"))
(def table-footer (component js/semanticUIReact "TableFooter"))
(def table-header (component js/semanticUIReact "TableHeader"))
(def table-header-cell (component js/semanticUIReact "TableHeaderCell"))
(def table-row (component js/semanticUIReact "TableRow"))
(def text-area (component js/semanticUIReact "TextArea"))
(def time-input (component js/SemanticUiCalendarReact "TimeInput"))
(def transition (component js/semanticUIReact "Transition"))
(def transition-group (component js/semanticUIReact "TransitionGroup"))
(def transitionable-portal (component js/semanticUIReact "TransitionablePortal"))
(def visibility (component js/semanticUIReact "Visibility"))
(def year-input (component js/SemanticUiCalendarReact "YearInput"))

(defn regenerate-definitions
  ([] (regenerate-definitions #{"semanticUIReact" "SemanticUiCalendarReact"}))
  ([modules]
  (->> (for [module    modules
             component (keys (js->clj (aget js/window module)))]
         [module component])
       (sort-by second)
       (run! (fn [[module component]]
               (println (pr-str
                          (clojure.core/list
                            'def
                            (symbol (utils/camel->kebab component))
                            (clojure.core/list 'component (symbol "js" module) component)))))))))