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
 * @name productSelectorModule
 * @description
 *
 * The productSelectorModule contains a directive that allows selecting products from a catalog.
 *
 */
angular.module('productSelectorModule', ['catalogAwareSelectorModule', 'catalogInformationServiceModule'])
    /**
     * @ngdoc object
     * @name productSelectorModule.object:PRODUCT_TEMPLATE_FOR_SELECTOR
     * @description
     * Constant that specifies the URL of the template used to display a product.
     */
    .constant('PRODUCT_TEMPLATE_FOR_SELECTOR', 'productTemplate.html')
    /**
     * @ngdoc object
     * @name productSelectorModule.object:MAX_NUM_PRODUCTS_IN_SELECTOR
     * @description
     * Constant that specifies the maximum number of products a user can select.
     */
    .constant('MAX_NUM_PRODUCTS_IN_SELECTOR', 10)
    .controller('ProductSelectorController', function($q, lodash, seCatalogInformationService, PRODUCT_TEMPLATE_FOR_SELECTOR, MAX_NUM_PRODUCTS_IN_SELECTOR) {

        // Variables
        this.$onInit = function() {
            this.productTemplate = PRODUCT_TEMPLATE_FOR_SELECTOR;
            this.itemsFetchStrategy = seCatalogInformationService.productsFetchStrategy;

            if (this.editable === undefined) {
                this.editable = true;
            }
        };

        this.getCatalogs = seCatalogInformationService.getProductCatalogsInformation;
        this.maxNumItems = MAX_NUM_PRODUCTS_IN_SELECTOR;
    })
    /**
     * @ngdoc directive
     * @name productSelectorModule.directive:seProductSelector
     * @scope
     * @restrict E
     *
     * @description
     * A component that allows users to select products from one or more catalogs. This component is catalog aware; the list of products displayed is dependent on
     * the product catalog and catalog version selected by the user within the component.
     *
     * @param {@String} id An identifier used to track down the component in the page.
     * @param {<Object} model The object where the list of selected products will be stored. The model must contain a property with the same name as the qualifier. That property must be
     * of type array and is used to store the UIDs of the products selected.
     * @param {<String} qualifier The key of the property in the model that will hold the list of products selected.
     * @param {<Boolean=} editable This property specifies whether the selector can be edited or not. If this flag is false,
     * then the selector is treated as read-only; the selection cannot be modified in any way. Optional, default value is true.
     */
    .component('seProductSelector', {
        templateUrl: 'productSelectorTemplate.html',
        controller: 'ProductSelectorController',
        controllerAs: 'ctrl',
        bindings: {
            id: '@',
            model: '<',
            qualifier: '<',
            editable: '<?'
        }
    });
