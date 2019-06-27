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
angular.module('contextAwareCatalogModule', ['resourceLocationsModule'])
    .service('contextAwareCatalogService', function($q, catalogService, PRODUCT_CATEGORY_SEARCH_RESOURCE_URI, PRODUCT_CATEGORY_RESOURCE_URI, PRODUCT_LIST_RESOURCE_API, PRODUCT_RESOURCE_API, PAGES_LIST_RESOURCE_URI, sharedDataService) {

        this._getSearchUriByProductCatalogIdAndUriConstant = function(productCatalogId, uriConstant) {
            return catalogService.getActiveProductCatalogVersionByCatalogId(productCatalogId).then(function(catalogVersion) {
                var uri = uriConstant
                    .replace(/:catalogId/gi, productCatalogId)
                    .replace(/:catalogVersion/gi, catalogVersion);
                return $q.when(uri);
            });
        };

        this._getItemUriByUriConstant = function(uriConstant) {
            var uri = uriConstant.replace(/:siteUID/gi, 'CURRENT_CONTEXT_SITE_ID');
            return $q.when(uri);
        };

        this._getContentPageUri = function() {
            return sharedDataService.get('experience').then(function(data) {
                var catalogId = data.catalogDescriptor.catalogId;
                return catalogService.getActiveContentCatalogVersionByCatalogId(catalogId).then(function(catalogVersion) {
                    var uri = PAGES_LIST_RESOURCE_URI
                        .replace(/:catalogId/gi, catalogId)
                        .replace(/:catalogVersion/gi, catalogVersion);
                    return $q.when(uri);
                });
            });
        };

        this.getProductCategorySearchUri = function(productCatalogId) {
            return this._getSearchUriByProductCatalogIdAndUriConstant(productCatalogId, PRODUCT_CATEGORY_SEARCH_RESOURCE_URI);
        };

        this.getProductCategoryItemUri = function() {
            return this._getItemUriByUriConstant(PRODUCT_CATEGORY_RESOURCE_URI);
        };

        this.getProductSearchUri = function(productCatalogId) {
            return this._getSearchUriByProductCatalogIdAndUriConstant(productCatalogId, PRODUCT_LIST_RESOURCE_API);
        };

        this.getProductItemUri = function() {
            return this._getItemUriByUriConstant(PRODUCT_RESOURCE_API);
        };

        this.getContentPageSearchUri = function() {
            return this._getContentPageUri().then(function(uri) {
                return $q.when(uri + '?typeCode=ContentPage');
            });
        };

        this.getContentPageItemUri = function() {
            return this._getContentPageUri();
        };
    });
