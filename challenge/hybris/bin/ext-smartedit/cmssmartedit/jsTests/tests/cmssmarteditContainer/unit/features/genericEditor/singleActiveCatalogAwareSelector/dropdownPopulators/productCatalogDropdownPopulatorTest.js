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
describe('productCatalogDropdownPopulator', function() {
    var $rootScope;
    var productCatalogDropdownPopulator;
    var catalogServiceMock;
    var optionsDropdownPopulator;

    var CATALOGS_MOCKS = [{
        "catalogId": "apparelProductCatalog",
        "name": {
            "en": "Apparel Product Catalog",
            "de": "Produktkatalog Kleidung"
        },
        "versions": [{
            "active": true,
            "uuid": "apparelProductCatalog/Online",
            "version": "Online"
        }, {
            "active": false,
            "uuid": "apparelProductCatalog/Staged",
            "version": "Staged"
        }]
    }];
    var currentCatalog = CATALOGS_MOCKS[0];
    var PAYLOAD_MOCK = {
        field: {
            idAttribute: 'catalogId',
            labelAttributes: ['name'],
            editable: true,
            propertyType: 'productCatalog'
        },
        model: {
            productCatalog: currentCatalog.catalogId
        },
        search: ''
    };
    var expectedCatalogs = CATALOGS_MOCKS.filter(function(catalog) {
        return catalog.versions.find(function(version) {
            return version.active === true;
        }).length === 1;
    });

    beforeEach(angular.mock.module('functionsModule'));

    beforeEach(function() {
        var harness = AngularUnitTestHelper
            .prepareModule('productCatalogDropdownPopulatorModule')
            .mockConstant('extend', function(parent, child) {
                return child;
            })
            .mockConstant('DropdownPopulatorInterface', function() {})
            .mock('catalogService', 'getProductCatalogsForSite').and.returnResolvedPromise(CATALOGS_MOCKS)
            .mockConstant('CONTEXT_SITE_ID', 'CONTEXT_SITE_ID')
            .mock('optionsDropdownPopulator', 'populate').and.returnResolvedPromise(expectedCatalogs)
            .service('productCatalogDropdownPopulator');

        productCatalogDropdownPopulator = harness.service;
        catalogServiceMock = harness.mocks.catalogService;
        optionsDropdownPopulator = harness.mocks.optionsDropdownPopulator;
    });

    beforeEach(inject(function(_$rootScope_) {
        $rootScope = _$rootScope_;
    }));

    it('should not be paged', function() {
        expect(productCatalogDropdownPopulator.isPaged()).toBe(false);
    });

    it('should be able to fetch all items with expected parameters', function() {
        var promise = productCatalogDropdownPopulator.fetchAll(PAYLOAD_MOCK);
        $rootScope.$digest();

        PAYLOAD_MOCK.field.options = expectedCatalogs;
        expect(optionsDropdownPopulator.populate).toHaveBeenCalledWith(PAYLOAD_MOCK);
    });
});
