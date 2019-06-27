/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
/**
 * @ngdoc overview
 * @name itemSelectorPanelModule
 * @description
 * # The itemSelectorPanelModule
 *
 * This module contains a directive used to display a panel where a user can add items from one or more catalogs.
 */
angular.module('itemSelectorPanelModule', ['sliderPanelModule', 'l10nModule', 'recompileDomModule'])
    .controller('ItemSelectorPanelController', function($q, $element, lodash) {

        // Variables
        this.catalogInfo = {};
        this.catalogItemTemplate = "catalogItemTemplate.html";
        this.saveButtonDisabled = true;

        // Initialization
        this.$onInit = function() {

            this.sliderConfiguration = {
                cssSelector: '#y-modal-dialog',
                greyedOutOverlay: false,
                modal: {
                    showDismissButton: true,
                    title: this.catalogItemType,
                    cancel: {
                        onClick: this._cancelItemsChanges,
                        label: 'se.cms.catalogaware.panel.button.cancel'
                    },
                    save: {
                        onClick: this._saveItemChanges,
                        label: 'se.cms.catalogaware.panel.button.add',
                        isDisabledFn: this._isSaveButtonDisabled
                    }
                }
            };

            this._initCatalogs();

            if (this.maxNumItems === undefined || this.maxNumItems < 0) {
                this.maxNumItems = 0;
            }

            // Basic Functions
            this.showPanel = function() {
                this.catalogInfo = {};
                this._initCatalogs();
                this._internalItemsSelected = (this.itemsSelected) ? lodash.clone(this.itemsSelected) : [];
                this._internalShowPanel();
            }.bind(this);

        };

        // Catalog
        this._initCatalogs = function() {
            return this.getCatalogs().then(function(catalogs) {
                this.catalogs = catalogs;
                if (this.catalogs.length === 1) {
                    this.catalogInfo.catalogId = this.catalogs[0].id;
                }

                this._initCatalogSelector();
                this._initCatalogVersionSelector();
                this._initItemsSelector();

            }.bind(this));
        };

        this._initCatalogSelector = function() {
            if (Array.isArray(this.catalogList)) {
                throw Error('Cannot show panel - Invalid list of catalogs.');
            }

            this.catalogSelectorFetchStrategy = {
                fetchAll: function() {
                    return $q.when(this.catalogs);
                }.bind(this)
            };
        };

        this._onCatalogSelectorChange = function() {
            if (this.resetCatalogVersionSelector) {
                this.resetCatalogVersionSelector();
            }
        }.bind(this);

        // Catalog Version
        this._initCatalogVersionSelector = function() {
            this.catalogVersionSelectorFetchStrategy = {
                fetchAll: function() {
                    var result = [];
                    if (this.catalogInfo.catalogId) {
                        result = this.catalogs.find(function(catalog) {
                            return catalog.id === this.catalogInfo.catalogId;
                        }.bind(this)).versions;
                    }

                    return $q.when(result);
                }.bind(this)
            };
        };

        this._onCatalogVersionSelectorChange = function() {
            if (this.catalogInfo.catalogId && this.catalogInfo.catalogVersion) {
                if (this.resetItemsListSelector) {
                    this.resetItemsListSelector();
                }
            }
        }.bind(this);

        // Items
        this._initItemsSelector = function() {
            var callWithCatalogInfo = function(fn, filterArguments) {
                var newArgs = lodash.concat([this.catalogInfo], Array.from(arguments).slice(2));
                return fn.apply(null, newArgs).then(function(page) {
                    if (filterArguments) {
                        // Remove the items that are already selected. Otherwise, the ySelect would duplicate them, producing an exception.
                        var itemsIndex = page.results.length;
                        while (itemsIndex--) {
                            var item = page.results[itemsIndex];
                            if (lodash.includes(this._internalItemsSelected, item.uid)) {
                                page.results.splice(itemsIndex, 1);
                                page.pagination.count--; // TODO: Review with Andres changes to see if it makes sense or not to remove it from the count.
                            }
                        }
                    }

                    return page;
                }.bind(this));
            }.bind(this);

            this._internalItemsFetchStrategy = {
                fetchAll: (this.itemsFetchStrategy.fetchAll) ? callWithCatalogInfo.bind(null, this.itemsFetchStrategy.fetchAll, false) : null,
                fetchPage: (this.itemsFetchStrategy.fetchPage) ? callWithCatalogInfo.bind(null, this.itemsFetchStrategy.fetchPage, true) : null,
                fetchEntity: this.itemsFetchStrategy.fetchEntity,
                fetchEntities: this.itemsFetchStrategy.fetchEntities
            };
        };

        this._onItemsSelectorChange = function() {
            if (this._isItemSelectorEnabled()) {
                // Only consider changes when the item is enabled. Otherwise the changes are happening during initialization.
                this.saveButtonDisabled = false;
            }
        }.bind(this);

        this._isSaveButtonDisabled = function() {
            return this.saveButtonDisabled;
        }.bind(this);

        this._cancelItemsChanges = function() {
            // Discard changes in the list.
            this.catalogInfo = {};
            this._internalHidePanel();
        }.bind(this);

        this._saveItemChanges = function() {
            // Copy the new elements into the other array. We need to use the same array so that the reference is not lost in the parent.
            this.itemsSelected.splice.apply(this.itemsSelected, [0, this.itemsSelected.length].concat(this._internalItemsSelected));

            if (this.onChange) {
                this.onChange();
            }

            this._internalHidePanel();


        }.bind(this);

        // Event Listeners
        this._isItemSelectorEnabled = function() {
            return this.catalogInfo && this.catalogInfo.catalogId && this.catalogInfo.catalogVersion;
        }.bind(this);

    })
    /**
     * @ngdoc directive
     * @name itemSelectorPanelModule.directive:seItemSelectorPanel
     * @scope
     * @restrict E
     * @element se-item-selector-panel
     *
     * @description
     * Directive used to create a panel where a user can add items from one or more catalogs. Note that this directive is intended to be used within a
     * {@link catalogAwareSelectorModule.directive:seCatalogAwareSelector seCatalogAwareSelector}; it is not designed to work alone.
     *
     * @param {<String} panelTitle The title displayed at the top of the panel.
     * @param {=Array} itemsSelected Array containing the UIDs of the items selected. There's a double binding, which
     * means that the panel will show items already selected and will update back whenever the user changes the
     * selection.
     * @param {<String} itemTemplate The URL of the template used to display items in the component.
     * @param {<Function} getCatalogs Function called by the component to retrieve the catalogs from which to select
     * items from.
     * @param {<Object} itemsFetchStrategy Object containing the strategy to retrieve items from the catalogs. There are three possible scenarios:
     * - Provide a fetchAll function in the itemsFetchStrategy. This function must retrieve all items from the catalog
     * at once. No pagination is performed.
     * - Provide fetchPage + fetchEntity functions in the itemsFetchStrategy. Information about items already selected
     * are retrieved one by one with fetchEntity. Items to choose from, on the other hand, are retrieved in a page fashion.
     * - Provide fetchPage + fetchEntities Information about items already selected are retrieved in one call with
     * fetchEntities. Items to choose from, on the other hand, are retrieved in a page fashion.
     * @param {Function} catalogItemsFetchStrategy.fetchAll (Optional)
     * @param {Function} catalogItemsFetchStrategy.fetchPage (Optional)
     * @param {Function} catalogItemsFetchStrategy.fetchEntity (Optional)
     * @param {Function} catalogItemsFetchStrategy.fetchEntities (Optional)
     * @param {=Function} onChange Function executed whenever the selection of items change in the panel.
     * @param {=Function} showPanel Function passed to the parent to be executed whenever the panel is opened.
     * @param {=Function=} hidePanel Function passed to the parent to be executed whenever the panel is hid.
     * @param {<String} catalogItemType String specifying the type of elements being selected in the panel. This string is used only for localization purposes.
     * @param {<Number=} maxNumItems The maximum number of items that a user can select within the component.
     *
     */
    .component('seItemSelectorPanel', {
        templateUrl: 'itemSelectorPanelTemplate.html',
        controller: 'ItemSelectorPanelController',
        controllerAs: 'ctrl',
        bindings: {
            panelTitle: '<',
            itemsSelected: '=',
            itemTemplate: '<',
            getCatalogs: '<',
            itemsFetchStrategy: '<',
            onChange: '=',
            showPanel: '=',
            hidePanel: '=?',
            catalogItemType: '<',
            maxNumItems: '<?'
        }
    });
