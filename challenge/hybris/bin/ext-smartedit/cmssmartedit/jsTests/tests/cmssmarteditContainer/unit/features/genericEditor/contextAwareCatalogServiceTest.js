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
/* jshint unused:false, undef:false */
describe('contextAwareCatalog', function() {
    var contextAwareCatalogService;
    var catalogVersion = 'Online';
    var catalogId = 'testCatalogId';
    var SEARCH_RESOURCE_URI = '/base_url/id/:catalogId/version/:catalogVersion';
    var ITEM_RESOURCE_URI = '/base_url/:siteUID';
    var searchResultUri;
    var itemResultUri;

    beforeEach(function() {
        searchResultUri = SEARCH_RESOURCE_URI
            .replace(/:catalogId/gi, catalogId)
            .replace(/:catalogVersion/gi, catalogVersion);

        itemResultUri = ITEM_RESOURCE_URI.replace(/:siteUID/gi, 'CURRENT_CONTEXT_SITE_ID');

        var harness = AngularUnitTestHelper
            .prepareModule('contextAwareCatalogModule')
            .mock('catalogService', 'getActiveProductCatalogVersionByCatalogId').and.returnResolvedPromise(catalogVersion)
            .mock('catalogService', 'getActiveContentCatalogVersionByCatalogId').and.returnResolvedPromise(catalogVersion)
            .mock('sharedDataService', 'get').and.returnResolvedPromise({
                catalogDescriptor: {
                    catalogId: catalogId
                }
            })
            .mockConstant('PRODUCT_CATEGORY_SEARCH_RESOURCE_URI', SEARCH_RESOURCE_URI)
            .mockConstant('PAGES_LIST_RESOURCE_URI', SEARCH_RESOURCE_URI)
            .mockConstant('PRODUCT_CATEGORY_RESOURCE_URI', ITEM_RESOURCE_URI)
            .mockConstant('PRODUCT_LIST_RESOURCE_API', SEARCH_RESOURCE_URI)
            .mockConstant('PRODUCT_RESOURCE_API', ITEM_RESOURCE_URI)
            .service('contextAwareCatalogService');

        contextAwareCatalogService = harness.service;
    });

    it('should be able to return proper uri for product category list', function() {
        expect(contextAwareCatalogService.getProductCategorySearchUri(catalogId)).toBeResolvedWithData(searchResultUri);
    });

    it('should be able to return proper uri for product category item', function() {
        expect(contextAwareCatalogService.getProductCategoryItemUri()).toBeResolvedWithData(itemResultUri);
    });

    it('should be able to return proper uri for content page list', function() {
        searchResultUri = searchResultUri + '?typeCode=ContentPage';
        expect(contextAwareCatalogService.getContentPageSearchUri()).toBeResolvedWithData(searchResultUri);
    });

    it('should be able to return proper uri for content page item', function() {
        expect(contextAwareCatalogService.getContentPageItemUri()).toBeResolvedWithData(searchResultUri);
    });

    it('should be able to return proper uri for product list', function() {
        expect(contextAwareCatalogService.getProductSearchUri(catalogId)).toBeResolvedWithData(searchResultUri);
    });

    it('should be able to return proper uri for product item', function() {
        expect(contextAwareCatalogService.getProductItemUri()).toBeResolvedWithData(itemResultUri);
    });
});
