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
angular.module('seProductCatalogVersionsSelectorModule', ['smarteditServicesModule', 'modalServiceModule', 'multiProductCatalogVersionSelectorModule', 'yLoDashModule', 'l10nModule', 'seDropdownModule'])
    .constant("PRODUCT_CATALOG_SINGLE_TEMPLATE", "productsCatalogSelectSingleTemplate.html")
    .constant("PRODUCT_CATALOG_MULTIPLE_TEMPLATE", "productsCatalogSelectMultipleTemplate.html")
    .constant("MULTI_PRODUCT_CATALOGS_UPDATED", "MULTI_PRODUCT_CATALOGS_UPDATED")
    .controller("seProductCatalogVersionsSelectorController", function($q, $log, $translate, lodash, l10nFilter, catalogService, modalService, systemEventService, PRODUCT_CATALOG_SINGLE_TEMPLATE, PRODUCT_CATALOG_MULTIPLE_TEMPLATE, MODAL_BUTTON_ACTIONS, MODAL_BUTTON_STYLES, MULTI_PRODUCT_CATALOGS_UPDATED, LINKED_DROPDOWN) {

        this.onClick = function(productCatalogs, selectedCatalogVersions) {
            this.isTooltipOpen = false;

            modalService.open({
                title: 'se.modal.product.catalog.configuration',
                size: 'md',
                templateUrl: 'multiProductCatalogVersionsConfigurationsTemplate.html',
                controller: ['$q', '$log', 'MULTI_PRODUCT_CATALOGS_UPDATED', 'modalManager', 'systemEventService', function($q, $log, MULTI_PRODUCT_CATALOGS_UPDATED, modalManager, systemEventService) {

                    this.productCatalogs = productCatalogs;
                    this.selectedCatalogVersions = selectedCatalogVersions;

                    this.onSave = function() {
                        systemEventService.publishAsync(MULTI_PRODUCT_CATALOGS_UPDATED, this.updatedCatalogVersions);
                        modalManager.close();
                    };

                    this.onCancel = function() {
                        modalManager.close();
                        return $q.when({});
                    };

                    this.updateSelection = function(updatedSelectedVersions) {
                        if (JSON.stringify(updatedSelectedVersions) !== JSON.stringify(this.selectedCatalogVersions)) {
                            this.updatedCatalogVersions = updatedSelectedVersions;
                            modalManager.enableButton('done');
                        } else {
                            modalManager.disableButton('done');
                        }

                    };

                    this.init = function() {
                        modalManager.setDismissCallback(this.onCancel.bind(this));

                        modalManager.setButtonHandler(function(buttonId) {
                            switch (buttonId) {
                                case 'done':
                                    return this.onSave();
                                case 'cancel':
                                    return this.onCancel();
                                default:
                                    $log.error('A button callback has not been registered for button with id', buttonId);
                                    break;
                            }
                        }.bind(this));
                    };

                }],
                buttons: [{
                    id: 'cancel',
                    label: 'se.confirmation.modal.cancel',
                    style: MODAL_BUTTON_STYLES.SECONDARY,
                    action: MODAL_BUTTON_ACTIONS.DISMISS
                }, {
                    id: 'done',
                    label: 'se.confirmation.modal.done',
                    action: MODAL_BUTTON_ACTIONS.NONE,
                    disabled: true
                }]
            });

        };

        this._updateProductCatalogsModel = function(eventId, data) {
            this.model[this.qualifier] = data;
        };

        this.parseSingleCatalogVersion = function(catalog) {
            var versions = [];
            catalog.versions.forEach(function(version) {
                versions.push({
                    id: version.uuid,
                    label: version.version
                });
            });
            return versions;
        };

        this._resetSelector = function(eventId, handle) {
            if (handle.qualifier === 'previewCatalog') {
                if (this.initialPreview !== handle.optionObject.id) {

                    this.initialPreview = handle.optionObject.id;

                    var siteId = handle.optionObject.id.split('_')[0];
                    catalogService.getProductCatalogsForSite(siteId).then(function(productCatalogs) {
                        this.productCatalogs = productCatalogs;

                        if (this.reset) {
                            this.reset();
                        }

                        catalogService.returnActiveCatalogVersionUIDs(productCatalogs).then(function(activeProductCatalogVersions) {
                            this.model[this.qualifier] = activeProductCatalogVersions;
                            this._setContent();
                        }.bind(this));
                    }.bind(this));
                }
            }
        };

        this._buildCatalogNameCatalogVersionString = function(productCatalog) {

            var productCatalogVersion = productCatalog.versions.find(function(version) {
                return this.model[this.qualifier] && this.model[this.qualifier].indexOf(version.uuid) > -1;
            }.bind(this));

            if (this.model[this.qualifier] && productCatalogVersion) {
                return l10nFilter(productCatalog.name) + ' (' + productCatalogVersion.version + ')';
            }
            return "";
        };

        this.buildMultiProductCatalogVersionsTemplate = function() {
            var sHeader = $translate.instant('se.product.catalogs.selector.headline.tooltip');
            return "<div class='se-product-catalogs-tooltip'><div class='se-product-catalogs-tooltip__h' >" + sHeader + "</div>" +
                this.productCatalogs.reduce(function(accumulator, productCatalog) {
                    accumulator += ("<div class='se-product-catalog-info'>" + this._buildCatalogNameCatalogVersionString(productCatalog));
                    return accumulator + "</div>";
                }.bind(this), '') + "</div>";
        };

        this.getMultiProductCatalogVersionsSelectedOptions = function() {
            if (this.productCatalogs) {
                return this.productCatalogs.reduce(function(accumulator, productCatalog, currentIndex) {
                    accumulator += this._buildCatalogNameCatalogVersionString(productCatalog);
                    accumulator += currentIndex < (this.productCatalogs.length - 1) ? ', ' : '';
                    return accumulator;
                }.bind(this), '');
            }
            return "";
        };

        this._setContent = function() {

            catalogService.getProductCatalogsForSite(this.initialPreview.split('_')[0]).then(function(productCatalogs) {
                this.productCatalogs = productCatalogs;

                if (this.productCatalogs.length === 1) {

                    this.fetchStrategy = {
                        fetchAll: function() {
                            var parsedVersions = this.parseSingleCatalogVersion(this.productCatalogs[0]);
                            return $q.when(parsedVersions);
                        }.bind(this)
                    };
                    this.isReady = true;
                    this.isSingleVersionSelector = true;
                    this.isMultiVersionSelector = false;
                }

                if (this.productCatalogs.length > 1) {

                    this.$unRegEventForMultiProducts = systemEventService.subscribe(MULTI_PRODUCT_CATALOGS_UPDATED, this._updateProductCatalogsModel.bind(this));
                    this.isReady = true;

                    this.isSingleVersionSelector = false;
                    this.isMultiVersionSelector = true;
                }

            }.bind(this));

        };

        this.$onInit = function() {
            this.initialPreview = lodash.cloneDeep(this.model.previewCatalog);
            if (this.initialPreview) {
                this.isTooltipOpen = false;

                this.isReady = false;
                this.isSingleVersionSelector = false;
                this.isMultiVersionSelector = false;

                this.eventId = (this.id || '') + LINKED_DROPDOWN;
                this.$unRegSiteChangeEvent = systemEventService.subscribe(this.eventId, this._resetSelector.bind(this));

                this._setContent();
            }
        };

        this.$onDestroy = function() {
            if (this.$unRegSiteChangeEvent) {
                this.$unRegSiteChangeEvent();
            }
            if (this.$unRegEventForMultiProducts) {
                this.$unRegEventForMultiProducts();
            }
        };
    })
    .component('seProductCatalogVersionsSelector', {
        templateUrl: 'productCatalogVersionsSelectorTemplate.html',
        controller: "seProductCatalogVersionsSelectorController",
        bindings: {
            field: '<',
            qualifier: '<',
            model: '<',
            id: '<'
        }
    });
