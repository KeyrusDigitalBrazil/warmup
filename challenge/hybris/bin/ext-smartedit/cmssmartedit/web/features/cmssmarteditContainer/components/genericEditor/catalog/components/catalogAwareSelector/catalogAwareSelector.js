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
 * @name catalogAwareSelectorModule
 * @description
 * # The catalogAwareSelectorModule
 *
 * The catalogAwareSelectorModule contains a directive that allows users to select items that depend on a catalog.
 *
 */
angular.module('catalogAwareSelectorModule', ['itemSelectorPanelModule', 'yEditableListModule'])
    .controller('CatalogAwareSelectorController', function($q, $log, $translate, lodash) {

        // Constants
        var DEFAULT_ITEM_TYPE_KEY = 'se.cms.catalogaware.catalogitemtype.default';

        // Variables
        this.model = [];
        this.currentOptions = {};
        this.itemsList = [];

        // Initialization
        this.$onInit = function() {
            if (this.editable === undefined) {
                this.editable = true;
            }

            // First, connect with the outside model.
            this.exposedModel.$viewChangeListeners.push(this._syncFromExternalModel);
            this.exposedModel.$render = this._syncFromExternalModel;

            this.catalogItemTypeKey = (this.catalogItemTypeKey) ? this.catalogItemTypeKey : DEFAULT_ITEM_TYPE_KEY;
            $translate(this.catalogItemTypeKey).then(function(itemType) {
                this.catalogItemType = itemType;
            }.bind(this));
        };

        // Basic Functions
        /**
         * This method is used to sync the internal model with the changes happening outside. It is mostly used when
         * the component first opens and we need to synchronize the internal model with the items that have already
         * been selected.
         */
        this._syncFromExternalModel = function() {
            if (this.model.length === 0) {
                this.model.push.apply(this.model, this.exposedModel.$modelValue);
                this._syncFromModelToItemsList();
            }
        }.bind(this);

        /**
         * This method is used to synchronize changes that happen in the yEditableList with the internal model.
         */
        this._syncFromItemsListToModel = function() {
            this.model.length = 0;
            this.itemsList.map(function(item) {
                this.model.push(item.id);
            }.bind(this));
        };

        /**
         * This method is used to synchronize changes that happen in the panel with the internal model.
         */
        this._syncFromModelToItemsList = function() {
            var itemsFound = {};
            var itemsMissing = [];

            // Find which items have already been retrieved and which ones are missing.
            this.model.map(function(itemKey) {
                var match = lodash.find(this.itemsList, function(item) {
                    return item.id === itemKey;
                });

                if (match) {
                    itemsFound[itemKey] = match;
                } else {
                    itemsMissing.push(itemKey);
                }
            });

            // Get the missing items
            this._fetchItems(itemsMissing, itemsFound).then(function() {
                // Update the itemsList with the new items. They are added in the order of the model.
                this.itemsList.length = 0;
                this.model.map(function(itemKey) {
                    var item = itemsFound[itemKey];
                    if (!item) {
                        $log.warn("[seCatalogAwareSelector] - Cannot find item with key ", itemKey);
                    } else {
                        this.itemsList.push(item);
                    }

                }.bind(this));

                if (this._refreshListWidget && lodash.isFunction(this._refreshListWidget)) {
                    this._refreshListWidget();
                }

            }.bind(this));


        };

        this._fetchItems = function(itemsToRetrieve, itemsFound) {
            var result = $q.when(null);

            if (itemsToRetrieve.length > 0) {
                if (this.itemsFetchStrategy.fetchAll) {
                    result = this.itemsFetchStrategy.fetchAll().then(function(itemsList) {
                        itemsToRetrieve.map(function(itemKey) {
                            itemsFound[itemKey] = itemsList.find(function(itemInList) {
                                return itemInList.id === itemKey;
                            });
                        }.bind(this));
                    });
                } else if (this.itemsFetchStrategy.fetchEntities) {
                    result = this.itemsFetchStrategy.fetchEntities(this.model).then(function(itemsList) {
                        itemsList.map(function(itemInList) {
                            itemsFound[itemInList.id] = itemInList;
                        });
                    }.bind(this));
                } else if (this.itemsFetchStrategy.fetchEntity) {
                    var promises = [];
                    itemsToRetrieve.map(function(itemKey) {
                        promises.push(this.itemsFetchStrategy.fetchEntity(itemKey));
                    }.bind(this));

                    return $q.all(promises).then(function(itemsList) {
                        itemsList.map(function(itemInList) {
                            itemsFound[itemInList.id] = itemInList;
                        });
                    });
                } else {
                    throw Error('[seCatalogAwareSelector] Invalid items fetch strategy. Cannot retrieve information.');
                }
            }

            return result;
        };

        // Helper Functions
        this.listIsEmpty = function() {
            return (this.model.length === 0);
        };

        this.openEditingPanel = function() {
            this.showPanel();
        };

        this.onPanelChange = function() {
            this._syncFromModelToItemsList();

            // Sync with the outside
            this.exposedModel.$setViewValue(this.model);
        }.bind(this);

        this.onListChange = function() {
            this._syncFromItemsListToModel();

            // Sync with the outside
            this.exposedModel.$setViewValue(this.model);
        }.bind(this);
    })

    /**
     * @ngdoc directive
     * @name catalogAwareSelectorModule.directive:seCatalogAwareSelector
     * @scope
     * @restrict E
     * @element ANY
     *
     * @description
     * A component that allows users to select items from one or more catalogs. This component is catalog aware; the list
     * of items displayed is dependent on the catalog and catalog version selected by the user within the component.
     *
     * @param {@String} id Identifier used to track the component in the page.
     * @param {<Function} getCatalogs Function called with no arguments by the component to retrieve the list of catalogs
     * where the items to select reside.
     * @param {<Object} itemsFetchStrategy Object that defines the strategies necessary to retrieve the items from a
     * particular catalog. There are three possible scenarios:
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
     * @param {<String=} itemTemplate The URL of the template used to display the items in the selector. If none is chosen
     * a default one is used (it shows the property name of each item).
     * @param {@String=} catalogItemTypeKey This property is a localized key. It is used to display the type of item being
     * selected in the component. If no key is provided, the type defaults to item.
     * @param {<Boolean=} editable This property specifies whether the selector can be edited or not. If this flag is false,
     * then the selector is treated as read-only; the selection cannot be modified in any way.
     * @param {<maxNumItems=} maxNumItems The maximum number of items that can be selected in the control.
     */
    .component('seCatalogAwareSelector', {
        templateUrl: 'catalogAwareSelectorTemplate.html',
        controller: 'CatalogAwareSelectorController',
        controllerAs: 'ctrl',
        require: {
            exposedModel: 'ngModel'
        },
        bindings: {
            id: '@',
            getCatalogs: '<',
            itemsFetchStrategy: '<',
            itemTemplate: '<?',
            catalogItemTypeKey: '@?',
            editable: '<?',
            maxNumItems: '<?'
        }
    });
