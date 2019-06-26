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
angular.module('categoryDropdownPopulatorModule', ['contextAwareCatalogModule', 'dropdownPopulatorModule', 'functionsModule'])
    .factory('categoryDropdownPopulator', function(DropdownPopulatorInterface, extend, contextAwareCatalogService, uriDropdownPopulator) {
        var dropdownPopulator = function() {};
        dropdownPopulator = extend(DropdownPopulatorInterface, dropdownPopulator);

        dropdownPopulator.prototype.fetchPage = function(payload) {
            return contextAwareCatalogService.getProductCategorySearchUri(payload.model.productCatalog).then(function(uri) {
                payload.field.uri = uri;
                return uriDropdownPopulator.fetchPage(payload);
            }.bind(this));
        };

        dropdownPopulator.prototype.isPaged = function() {
            return true;
        };

        dropdownPopulator.prototype.getItem = function(payload) {
            return contextAwareCatalogService.getProductCategoryItemUri().then(function(uri) {
                payload.field.uri = uri;
                return uriDropdownPopulator.getItem(payload);
            }.bind(this));
        };

        return new dropdownPopulator();
    });
