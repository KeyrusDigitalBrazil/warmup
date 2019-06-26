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
angular.module('productCategoryServiceModule', ['smarteditServicesModule', 'resourceLocationsModule'])
    .factory('productCategoryService', function(restServiceFactory, PRODUCT_CATEGORY_RESOURCE_URI, PRODUCT_CATEGORY_SEARCH_RESOURCE_URI) {
        var productCategoryService = restServiceFactory.get(PRODUCT_CATEGORY_RESOURCE_URI);
        var productCategorySearchService = restServiceFactory.get(PRODUCT_CATEGORY_SEARCH_RESOURCE_URI);

        return {
            getCategoryById: function(siteUID, categoryUID) {
                return productCategoryService.get({
                    siteUID: siteUID,
                    categoryUID: categoryUID
                });
            },
            getCategories: function(productCatalogInfo, mask, pageSize, currentPage) {
                this._validateProductCatalogInfo(productCatalogInfo);
                return productCategorySearchService.get({
                    catalogId: productCatalogInfo.catalogId,
                    catalogVersion: productCatalogInfo.catalogVersion,
                    text: mask,
                    pageSize: pageSize,
                    currentPage: currentPage
                });
            },
            _validateProductCatalogInfo: function(productCatalogInfo) {
                if (!productCatalogInfo.siteUID) {
                    throw Error("[productService] - site UID missing.");
                }
                if (!productCatalogInfo.catalogId) {
                    throw Error("[productService] - catalog ID missing.");
                }
                if (!productCatalogInfo.catalogVersion) {
                    throw Error("[productService] - catalog version  missing.");
                }
            }
        };
    });
