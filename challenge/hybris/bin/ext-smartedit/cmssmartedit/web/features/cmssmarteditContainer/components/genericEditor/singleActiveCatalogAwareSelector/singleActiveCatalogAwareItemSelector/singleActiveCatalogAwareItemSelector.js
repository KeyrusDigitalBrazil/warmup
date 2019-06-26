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
angular.module('singleActiveCatalogAwareItemSelectorModule', ['resourceLocationsModule', 'yLoDashModule'])
    /**
     * @internal
     * This constant defines the mapping between Cms Type and seDropdown property type. This is used to determine 
     * which populator the internal seDropdown should use. 
     */
    .constant('SINGLE_CATALOG_AWARE_ITEM_MAPPING', {
        SingleOnlineProductSelector: 'product',
        SingleOnlineCategorySelector: 'category'
    })
    .controller('SingleActiveCatalogAwareItemSelectorController', function(catalogService, CONTEXT_SITE_ID, SINGLE_CATALOG_AWARE_ITEM_MAPPING) {

        this.$onInit = function() {
            augmentDropdownsAttributes.call(this);
            initProductCatalogs.call(this);
        };

        /**
         * Augment the seDropdown attributes bindings to init the seDropdown with proper settings
         * The 'propertyType' value enable the usage of custom populators in seDropdown
         */
        var augmentDropdownsAttributes = function() {
            this.propertyType = getFieldPropertyType(this.field);
            if (this.propertyType) {
                this.productCatalogField = {
                    idAttribute: 'catalogId',
                    labelAttributes: ['name'],
                    editable: true,
                    propertyType: 'productCatalog'
                };

                this.mainDropDownI18nKey = this.field.i18nKey;
                delete this.field.i18nKey;
                this.field.paged = true;
                this.field.editable = true;
                this.field.idAttribute = 'uid';
                this.field.labelAttributes = ['name'];
                this.field.dependsOn = 'productCatalog';
                this.field.propertyType = this.propertyType;
            }
        };

        /*
            Filter on active product catalogs:
            - If there is only one product catalog, will hide the product catalog seDropDown and show the product catalog name
            - If there is more than one product catalog, will show the product catalog seDropDown
        */
        var initProductCatalogs = function() {
            catalogService.getProductCatalogsForSite(CONTEXT_SITE_ID).then(function(catalogs) {
                this.catalogs = catalogs;
                if (this.catalogs.length === 1) {
                    this.model.productCatalog = this.catalogs[0].catalogId;
                    this.editor.pristine.productCatalog = this.catalogs[0].catalogId;
                    this.catalogName = this.catalogs[0].name;
                }
            }.bind(this));
        };

        var getFieldPropertyType = function(field) {
            return SINGLE_CATALOG_AWARE_ITEM_MAPPING[field.cmsStructureType];
        };
    })
    .component('singleActiveCatalogAwareItemSelector', {
        templateUrl: 'singleActiveCatalogAwareItemSelectorTemplate.html',
        controller: 'SingleActiveCatalogAwareItemSelectorController',
        bindings: {
            field: '<',
            id: '<',
            model: '<',
            qualifier: '<',
            editor: '='
        }
    });
