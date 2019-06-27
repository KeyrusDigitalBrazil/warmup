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
describe('categoryDropdownPopulator', function() {
    var categoryDropdownPopulator;
    var uriDropdownPopulator;
    var contextAwareCatalogService;
    var $rootScope;
    var expectedSearchUri;
    var expectedItemUri;

    var CATALOGS_MOCKS = [{
        catalogVersion: 'Staged',
        catalogId: 1
    }, {
        catalogVersion: 'Online',
        catalogId: 2
    }];
    var PRODUCT_CATEGORY_SEARCH_RESOURCE_URI = '/base_url/id/:catalogId/version/:catalogVersion';
    var PRODUCT_CATEGORY_RESOURCE_URI = '/base_url/CURRENT_CONTEXT_SITE_ID';

    var PRODUCT_CATEGORIES_MOCK = [{
        id: 1,
        name: 'test'
    }, {
        id: 2,
        name: 'any category'
    }];
    var currentCatalog = CATALOGS_MOCKS[0];
    var PAYLOAD_MOCK = {
        field: {
            idAttribute: 'id',
            labelAttributes: ['name']
        },
        model: {
            productCatalog: currentCatalog.catalogId
        },
        selection: {
            label: 'Category #1',
            value: 'category_1'
        },
        search: '',
        pageSize: 10,
        currentPage: 1
    };

    beforeEach(angular.mock.module('functionsModule'));

    beforeEach(function() {
        expectedSearchUri = PRODUCT_CATEGORY_SEARCH_RESOURCE_URI
            .replace(/:catalogId/gi, currentCatalog.catalogId)
            .replace(/:catalogVersion/gi, currentCatalog.catalogVersion);

        expectedItemUri = PRODUCT_CATEGORY_RESOURCE_URI;

        var harness = AngularUnitTestHelper
            .prepareModule('categoryDropdownPopulatorModule')
            .mockConstant('extend', function(parent, child) {
                return child;
            })
            .mockConstant('DropdownPopulatorInterface', function() {})
            .mock('contextAwareCatalogService', 'getProductCategorySearchUri').and.returnResolvedPromise(expectedSearchUri)
            .mock('contextAwareCatalogService', 'getProductCategoryItemUri').and.returnResolvedPromise(expectedItemUri)
            .mock('uriDropdownPopulator', 'fetchPage').and.returnResolvedPromise({
                productCategories: PRODUCT_CATEGORIES_MOCK
            })
            .mock('uriDropdownPopulator', 'getItem').and.returnResolvedPromise(PRODUCT_CATEGORIES_MOCK[0])
            .service('categoryDropdownPopulator');

        categoryDropdownPopulator = harness.service;
        uriDropdownPopulator = harness.mocks.uriDropdownPopulator;
        contextAwareCatalogService = harness.mocks.contextAwareCatalogService;
    });

    beforeEach(inject(function(_$rootScope_) {
        $rootScope = _$rootScope_;
    }));

    it('should be paged', function() {
        expect(categoryDropdownPopulator.isPaged()).toBe(true);
    });

    it('should be able to fetch items per page with expected parameters', function() {
        var promise = categoryDropdownPopulator.fetchPage(PAYLOAD_MOCK);

        expect(promise).toBeResolvedWithData({
            productCategories: PRODUCT_CATEGORIES_MOCK
        });
        $rootScope.$digest();

        expect(contextAwareCatalogService.getProductCategorySearchUri).toHaveBeenCalledWith(PAYLOAD_MOCK.model.productCatalog);

        var expectedPayload = PAYLOAD_MOCK;
        expectedPayload.field.uri = expectedSearchUri;
        expect(uriDropdownPopulator.fetchPage).toHaveBeenCalledWith(expectedPayload);
    });

    it('should be able to fetch item by id', function() {
        var promise = categoryDropdownPopulator.getItem(PAYLOAD_MOCK);

        expect(promise).toBeResolvedWithData(PRODUCT_CATEGORIES_MOCK[0]);
        expect(contextAwareCatalogService.getProductCategoryItemUri).toHaveBeenCalled();

        var expectedPayload = PAYLOAD_MOCK;
        expectedPayload.field.uri = expectedSearchUri;
        expect(uriDropdownPopulator.getItem).toHaveBeenCalledWith(expectedPayload);
    });
});
