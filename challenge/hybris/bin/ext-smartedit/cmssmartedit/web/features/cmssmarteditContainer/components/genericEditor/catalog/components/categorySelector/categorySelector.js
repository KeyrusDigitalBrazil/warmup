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
 * @name categorySelectorModule
 * @description
 *
 * The categorySelectorModule contains a directive that allows selecting categories from a catalog.
 *
 */
angular.module('categorySelectorModule', ['catalogAwareSelectorModule', 'catalogInformationServiceModule'])
    /**
     * @ngdoc object
     * @name categorySelectorModule.object:CATEGORY_TEMPLATE_FOR_SELECTOR
     * @description
     * Constant that specifies the URL of the template used to display a category.
     */
    .constant('CATEGORY_TEMPLATE_FOR_SELECTOR', 'categoryTemplate.html')
    .controller('CategorySelectorController', function($q, lodash, sharedDataService, seCatalogInformationService, CATEGORY_TEMPLATE_FOR_SELECTOR) {
        this.$onInit = function() {

            this.categoryTemplate = CATEGORY_TEMPLATE_FOR_SELECTOR;
            this.getCatalogs = seCatalogInformationService.getProductCatalogsInformation;
            this.itemsFetchStrategy = seCatalogInformationService.categoriesFetchStrategy;

            if (this.editable === undefined) {
                this.editable = true;
            }
        };

    })
    /**
     * @ngdoc directive
     * @name categorySelectorModule.directive:seCategorySelector
     * @scope
     * @restrict E
     *
     * @description
     * A component that allows users to select categories from one or more catalogs. This component is catalog aware; the list of categories displayed is dependent on
     * the product catalog and catalog version selected by the user within the component.
     *
     * @param {@String} id An identifier used to track down the component in the page.
     * @param {<Object} model The object where the list of selected categories will be stored. The model must contain a property with the same name as the qualifier. That property must be
     * of type array and is used to store the UIDs of the categories selected.
     * @param {<String} qualifier The key of the property in the model that will hold the list of categories selected.
     * @param {<Boolean=} editable A flag that specifies whether the component can be modified or not. If the component cannot be modified, then the categories selected are read-only. Optional, default value is true.
     */
    .component('seCategorySelector', {
        templateUrl: 'categorySelectorTemplate.html',
        controller: 'CategorySelectorController',
        controllerAs: 'ctrl',
        bindings: {
            id: '@',
            model: '<',
            qualifier: '<',
            editable: '<?'
        }
    });
