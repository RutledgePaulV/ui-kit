(ns ui-kit.semantic
  (:require
    [reagent.core :as r]
    [cljsjs.semantic-ui-react]
    [cljsjs.semantic-ui-calendar-react]
    [clojure.string :as strings]
    [goog.object :as goog])
  (:refer-clojure :exclude [comment list]))

(defn component [class]
  (r/adapt-react-class (goog/get js/semanticUIReact class)))

(def accordion (component "Accordion"))
(def accordion-accordion (component "AccordionAccordion"))
(def accordion-content (component "AccordionContent"))
(def accordion-panel (component "AccordionPanel"))
(def accordion-title (component "AccordionTitle"))
(def advertisement (component "Advertisement"))
(def breadcrumb (component "Breadcrumb"))
(def breadcrumb-divider (component "BreadcrumbDivider"))
(def breadcrumb-section (component "BreadcrumbSection"))
(def button (component "Button"))
(def button-content (component "ButtonContent"))
(def button-group (component "ButtonGroup"))
(def button-or (component "ButtonOr"))
(def card (component "Card"))
(def card-content (component "CardContent"))
(def card-description (component "CardDescription"))
(def card-group (component "CardGroup"))
(def card-header (component "CardHeader"))
(def card-meta (component "CardMeta"))
(def checkbox (component "Checkbox"))
(def comment (component "Comment"))
(def comment-action (component "CommentAction"))
(def comment-actions (component "CommentActions"))
(def comment-author (component "CommentAuthor"))
(def comment-avatar (component "CommentAvatar"))
(def comment-content (component "CommentContent"))
(def comment-group (component "CommentGroup"))
(def comment-metadata (component "CommentMetadata"))
(def comment-text (component "CommentText"))
(def confirm (component "Confirm"))
(def container (component "Container"))
(def dimmer (component "Dimmer"))
(def dimmer-dimmable (component "DimmerDimmable"))
(def dimmer-inner (component "DimmerInner"))
(def divider (component "Divider"))
(def dropdown (component "Dropdown"))
(def dropdown-divider (component "DropdownDivider"))
(def dropdown-header (component "DropdownHeader"))
(def dropdown-item (component "DropdownItem"))
(def dropdown-menu (component "DropdownMenu"))
(def dropdown-search-input (component "DropdownSearchInput"))
(def embed (component "Embed"))
(def feed (component "Feed"))
(def feed-content (component "FeedContent"))
(def feed-date (component "FeedDate"))
(def feed-event (component "FeedEvent"))
(def feed-extra (component "FeedExtra"))
(def feed-label (component "FeedLabel"))
(def feed-like (component "FeedLike"))
(def feed-meta (component "FeedMeta"))
(def feed-summary (component "FeedSummary"))
(def feed-user (component "FeedUser"))
(def flag (component "Flag"))
(def form (component "Form"))
(def form-button (component "FormButton"))
(def form-checkbox (component "FormCheckbox"))
(def form-dropdown (component "FormDropdown"))
(def form-field (component "FormField"))
(def form-group (component "FormGroup"))
(def form-input (component "FormInput"))
(def form-radio (component "FormRadio"))
(def form-select (component "FormSelect"))
(def form-text-area (component "FormTextArea"))
(def grid (component "Grid"))
(def grid-column (component "GridColumn"))
(def grid-row (component "GridRow"))
(def header (component "Header"))
(def header-content (component "HeaderContent"))
(def header-subheader (component "HeaderSubheader"))
(def icon (component "Icon"))
(def icon-group (component "IconGroup"))
(def image (component "Image"))
(def image-group (component "ImageGroup"))
(def input (component "Input"))
(def item (component "Item"))
(def item-content (component "ItemContent"))
(def item-description (component "ItemDescription"))
(def item-extra (component "ItemExtra"))
(def item-group (component "ItemGroup"))
(def item-header (component "ItemHeader"))
(def item-image (component "ItemImage"))
(def item-meta (component "ItemMeta"))
(def label (component "Label"))
(def label-detail (component "LabelDetail"))
(def label-group (component "LabelGroup"))
(def list (component "List"))
(def list-content (component "ListContent"))
(def list-description (component "ListDescription"))
(def list-header (component "ListHeader"))
(def list-icon (component "ListIcon"))
(def list-item (component "ListItem"))
(def list-list (component "ListList"))
(def loader (component "Loader"))
(def menu (component "Menu"))
(def menu-header (component "MenuHeader"))
(def menu-item (component "MenuItem"))
(def menu-menu (component "MenuMenu"))
(def message (component "Message"))
(def message-content (component "MessageContent"))
(def message-header (component "MessageHeader"))
(def message-item (component "MessageItem"))
(def message-list (component "MessageList"))
(def modal (component "Modal"))
(def modal-actions (component "ModalActions"))
(def modal-content (component "ModalContent"))
(def modal-description (component "ModalDescription"))
(def modal-header (component "ModalHeader"))
(def pagination (component "Pagination"))
(def pagination-item (component "PaginationItem"))
(def popup (component "Popup"))
(def popup-content (component "PopupContent"))
(def popup-header (component "PopupHeader"))
(def portal (component "Portal"))
(def portal-inner (component "PortalInner"))
(def progress (component "Progress"))
(def radio (component "Radio"))
(def rail (component "Rail"))
(def rating (component "Rating"))
(def rating-icon (component "RatingIcon"))
(def responsive (component "Responsive"))
(def reveal (component "Reveal"))
(def reveal-content (component "RevealContent"))
(def search (component "Search"))
(def search-category (component "SearchCategory"))
(def search-result (component "SearchResult"))
(def search-results (component "SearchResults"))
(def segment (component "Segment"))
(def segment-group (component "SegmentGroup"))
(def select (component "Select"))
(def sidebar (component "Sidebar"))
(def sidebar-pushable (component "SidebarPushable"))
(def sidebar-pusher (component "SidebarPusher"))
(def statistic (component "Statistic"))
(def statistic-group (component "StatisticGroup"))
(def statistic-label (component "StatisticLabel"))
(def statistic-value (component "StatisticValue"))
(def step (component "Step"))
(def step-content (component "StepContent"))
(def step-description (component "StepDescription"))
(def step-group (component "StepGroup"))
(def step-title (component "StepTitle"))
(def sticky (component "Sticky"))
(def tab (component "Tab"))
(def tab-pane (component "TabPane"))
(def table (component "Table"))
(def table-body (component "TableBody"))
(def table-cell (component "TableCell"))
(def table-footer (component "TableFooter"))
(def table-header (component "TableHeader"))
(def table-header-cell (component "TableHeaderCell"))
(def table-row (component "TableRow"))
(def text-area (component "TextArea"))
(def transition (component "Transition"))
(def transition-group (component "TransitionGroup"))
(def transitionable-portal (component "TransitionablePortal"))
(def visibility (component "Visibility"))

(clojure.core/comment
  (doseq [k (sort (map name (goog/getKeys js/semanticUIReact)))]
    (println (pr-str
               (clojure.core/list
                 'def
                 (symbol (strings/lower-case k))
               (clojure.core/list 'component k))))))