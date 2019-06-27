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
describe('productDropdownPopulator', function() {
    var $rootScope;
    var productDropdownPopulator;
    var uriDropdownPopulator;
    var expectedSearchUri;
    var expectedItemUri;

    var PRODUCT_LIST_RESOURCE_API = '/base_url/id/:catalogId/version/:catalogVersion';
    var PRODUCT_RESOURCE_API = '/base_url/CURRENT_CONTEXT_SITE_ID';

    var PRODUCTS_MOCK = [{
        id: 1,
        name: 'test'
    }, {
        id: 2,
        name: 'any product'
    }];
    var currentCatalog = {
        catalogVersion: 'Online',
        catalogId: 1
    };
    var PAYLOAD_MOCK = {
        field: {
            idAttribute: 'id',
            labelAttributes: ['name']
        },
        model: {
            productCatalog: currentCatalog.catalogId
        },
        selection: {
            label: 'Product #1',
            value: 'product_1'
        },
        search: '',
        pageSize: 10,
        currentPage: 1
    };

    beforeEach(angular.mock.module('functionsModule'));

    beforeEach(angular.mock.module(function($provide) {
        var DropdownPopulatorInterfaceMock = function() {};
        $provide.value('DropdownPopulatorInterface', DropdownPopulatorInterfaceMock);
    }));

    beforeEach(function() {
        expectedSearchUri = PRODUCT_LIST_RESOURCE_API
            .replace(/:catalogId/gi, currentCatalog.catalogId)
            .replace(/:catalogVersion/gi, currentCatalog.catalogVersion);

        expectedItemUri = PRODUCT_RESOURCE_API;

        var harness = AngularUnitTestHelper
            .prepareModule('productDropdownPopulatorModule')
            .mock('contextAwareCatalogService', 'getProductSearchUri').and.returnResolvedPromise(expectedSearchUri)
            .mock('contextAwareCatalogService', 'getProductItemUri').and.returnResolvedPromise(expectedItemUri)
            .mock('uriDropdownPopulator', 'fetchPage').and.returnResolvedPromise({
                products: PRODUCTS_MOCK
            })
            .mock('uriDropdownPopulator', 'getItem').and.returnResolvedPromise(PRODUCTS_MOCK[0])
            .service('productDropdownPopulator');

        productDropdownPopulator = harness.service;
        uriDropdownPopulator = harness.mocks.uriDropdownPopulator;
        contextAwareCatalogService = harness.mocks.contextAwareCatalogService;
    });

    beforeEach(inject(function(_$rootScope_) {
        $rootScope = _$rootScope_;
    }));

    it('should be paged', function() {
        expect(productDropdownPopulator.isPaged()).toBe(true);
    });

    it('should be able to fetch items per page with expected parameters', function() {
        // WHEN
        var promise = productDropdownPopulator.fetchPage(PAYLOAD_MOCK);

        // ASSERT
        expect(promise).toBeResolvedWithData({
            products: PRODUCTS_MOCK
        });
        $rootScope.$digest();

        expect(contextAwareCatalogService.getProductSearchUri).toHaveBeenCalledWith(PAYLOAD_MOCK.model.productCatalog);

        var expectedPayload = PAYLOAD_MOCK;
        expectedPayload.field.uri = expectedSearchUri;
        expect(uriDropdownPopulator.fetchPage).toHaveBeenCalledWith(expectedPayload);
    });

    it('should be able to fetch item by id', function() {
        // WHEN
        var promise = productDropdownPopulator.getItem(PAYLOAD_MOCK);

        // ASSERT
        expect(promise).toBeResolvedWithData(PRODUCTS_MOCK[0]);
        expect(contextAwareCatalogService.getProductItemUri).toHaveBeenCalled();

        var expectedPayload = PAYLOAD_MOCK;
        expectedPayload.field.uri = expectedSearchUri;
        expect(uriDropdownPopulator.getItem).toHaveBeenCalledWith(expectedPayload);
    });

});
