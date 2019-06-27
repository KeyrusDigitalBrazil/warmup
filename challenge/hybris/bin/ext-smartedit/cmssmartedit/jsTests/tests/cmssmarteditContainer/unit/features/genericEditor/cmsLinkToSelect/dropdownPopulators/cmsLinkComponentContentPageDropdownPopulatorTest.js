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
describe('cmsLinkComponentcontentPageDropdownPopulator', function() {
    var cmsLinkComponentcontentPageDropdownPopulator;
    var uriDropdownPopulator;
    var contextAwareCatalogService;
    var $rootScope;
    var expectItemUri;
    var expectedSearchUri;

    var CATALOGS_MOCKS = [{
        catalogVersion: 'Staged',
        catalogId: 1,
        active: true
    }, {
        catalogVersion: 'Online',
        catalogId: 2,
        active: false
    }];
    var PAGES_LIST_RESOURCE_URI = '/base_url/id/:catalogId/version/:catalogVersion';
    var PAGES_MOCKS = [{
        id: 1,
        name: 'test'
    }, {
        id: 2,
        name: 'any page'
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
            label: 'Page #1',
            value: 'page_1'
        },
        search: ''
    };

    beforeEach(angular.mock.module('functionsModule'));

    beforeEach(function() {
        expectItemUri = PAGES_LIST_RESOURCE_URI
            .replace(/:catalogId/gi, currentCatalog.catalogId)
            .replace(/:catalogVersion/gi, currentCatalog.catalogVersion);

        expectedSearchUri = expectItemUri + '?typeCode=ContentPage';

        var harness = AngularUnitTestHelper
            .prepareModule('cmsLinkComponentContentPageDropdownPopulatorModule')
            .mock('contextAwareCatalogService', 'getContentPageSearchUri').and.returnResolvedPromise(expectedSearchUri)
            .mock('contextAwareCatalogService', 'getContentPageItemUri').and.returnResolvedPromise(expectItemUri)
            .mock('uriDropdownPopulator', 'fetchPage').and.returnResolvedPromise({
                pages: PAGES_MOCKS
            })
            .mockConstant('extend', function(parent, child) {
                return child;
            })
            .mockConstant('DropdownPopulatorInterface', function() {})
            .mock('uriDropdownPopulator', 'getItem').and.returnResolvedPromise(PAGES_MOCKS[0])
            .service('CMSLinkComponentcontentPageDropdownPopulator');

        cmsLinkComponentcontentPageDropdownPopulator = harness.service;
        uriDropdownPopulator = harness.mocks.uriDropdownPopulator;
        contextAwareCatalogService = harness.mocks.contextAwareCatalogService;
    });

    beforeEach(inject(function(_$rootScope_) {
        $rootScope = _$rootScope_;
    }));

    it('should be paged', function() {
        expect(cmsLinkComponentcontentPageDropdownPopulator.isPaged()).toBe(true);
    });

    it('should be able to fetch all items with expected parameters', function() {
        // WHEN
        var promise = cmsLinkComponentcontentPageDropdownPopulator.fetchPage(PAYLOAD_MOCK);

        // ASSERT
        expect(promise).toBeResolvedWithData({
            pages: PAGES_MOCKS
        });
        $rootScope.$digest();

        expect(contextAwareCatalogService.getContentPageSearchUri).toHaveBeenCalled();

        var expectedPayload = PAYLOAD_MOCK;
        expectedPayload.field.uri = expectedSearchUri;
        expect(uriDropdownPopulator.fetchPage).toHaveBeenCalledWith(expectedPayload);
    });

    it('should be able to fetch item by id', function() {
        // WHEN
        var promise = cmsLinkComponentcontentPageDropdownPopulator.getItem(PAYLOAD_MOCK);

        // ASSERT
        expect(promise).toBeResolvedWithData(PAGES_MOCKS[0]);
        expect(contextAwareCatalogService.getContentPageItemUri).toHaveBeenCalled();

        var expectedPayload = PAYLOAD_MOCK;
        expectedPayload.field.uri = expectItemUri;
        expect(uriDropdownPopulator.getItem).toHaveBeenCalledWith(expectedPayload);
    });
});
