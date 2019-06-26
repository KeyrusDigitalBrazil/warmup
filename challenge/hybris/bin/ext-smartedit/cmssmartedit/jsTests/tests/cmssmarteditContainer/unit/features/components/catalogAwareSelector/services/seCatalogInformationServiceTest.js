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
describe('seCatalogInformationService', function() {

    var catalogInformationService, mocks, $rootScope;
    var SITE_1_UID = 'some site uid';
    var SITE_2_UID = 'some site 2 uid';

    var experience;

    var site1CatalogInfo = [{
        catalogId: 'catalog1',
        name: 'Catalog 1',
        versions: [{
            version: 'version1_1'
        }, {
            version: 'version1_2'
        }]
    }, {
        catalogId: 'catalog2',
        name: 'Catalog 2',
        versions: [{
            version: 'version2_1'
        }, {
            version: 'version2_2'
        }]
    }];

    var site2CatalogInfo = [{
        catalogId: 'catalog3',
        name: 'Catalog 3',
        versions: [{
            version: 'version3_1'
        }, {
            version: 'version3_2'
        }]
    }, {
        catalogId: 'catalog4',
        name: 'Catalog 4',
        versions: [{
            version: 'version4_1'
        }, {
            version: 'version4_2'
        }]
    }];


    var products = {
        pagination: 'some pagination',
        products: [{
            uid: 'some product'
        }]
    };

    var categories = {
        pagination: 'some pagination',
        productCategories: [{
            uid: 'some category'
        }]
    };

    beforeEach(angular.mock.module('yLoDashModule'));

    beforeEach(function() {
        var harness = AngularUnitTestHelper.prepareModule('catalogInformationServiceModule')
            .mock('catalogService', 'getProductCatalogsForSite')
            .mock('sharedDataService', 'get')
            .mock('productService', 'getProductById')
            .mock('productService', 'findProducts')
            .mock('productCategoryService', 'getCategoryById')
            .mock('productCategoryService', 'getCategories')
            .mock('productCategoryService', 'getProductById')
            .service('seCatalogInformationService');

        catalogInformationService = harness.service;
        mocks = harness.mocks;

        experience = {
            siteDescriptor: {
                uid: SITE_1_UID
            }
        };
    });

    beforeEach(inject(function(_$rootScope_, $q) {
        $rootScope = _$rootScope_;
        mocks.sharedDataService.get.and.returnResolvedPromise(experience);
        mocks.catalogService.getProductCatalogsForSite.and.callFake(function(siteUID) {
            var deferred = $q.defer();
            if (siteUID === SITE_1_UID) {
                deferred.resolve(site1CatalogInfo);
            } else if (siteUID === SITE_2_UID) {
                deferred.resolve(site2CatalogInfo);
            }

            return deferred.promise;
        });

        // Products
        mocks.productService.findProducts.and.returnResolvedPromise(products);
        mocks.productService.getProductById.and.returnResolvedPromise(products.products[0]);

        // Categories
        mocks.productCategoryService.getCategories.and.returnResolvedPromise(categories);
        mocks.productCategoryService.getCategoryById.and.returnResolvedPromise(categories.productCategories[0]);
    }));

    it('GIVEN a site has catalogs ' +
        'WHEN getProductCatalogsInformation is called ' +
        'THEN the catalog information should return that information',
        function() {
            // GIVEN 
            var expectedResult = [{
                id: 'catalog1',
                name: 'Catalog 1',
                versions: [{
                    id: 'version1_1',
                    label: 'version1_1'
                }, {
                    id: 'version1_2',
                    label: 'version1_2'
                }]
            }, {
                id: 'catalog2',
                name: 'Catalog 2',
                versions: [{
                    id: 'version2_1',
                    label: 'version2_1'
                }, {
                    id: 'version2_2',
                    label: 'version2_2'
                }]
            }];

            // WHEN 
            var resultPromise = catalogInformationService.getProductCatalogsInformation();
            $rootScope.$digest();

            // THEN
            expect(mocks.catalogService.getProductCatalogsForSite).toHaveBeenCalledWith(SITE_1_UID);
            resultPromise.then(function(result) {
                expect(result).toEqual(expectedResult);
            });
            $rootScope.$digest();
        });

    it('GIVEN the service has catalogs of a site already cached AND that site has changed' +
        'WHEN getProductCatalogsInformation is called ' +
        'THEN the catalog information should be retrieved again',
        function() {
            // GIVEN 
            catalogInformationService.getProductCatalogsInformation();
            mocks.catalogService.getProductCatalogsForSite.calls.reset();
            experience.siteDescriptor.uid = SITE_2_UID;

            // WHEN 
            catalogInformationService.getProductCatalogsInformation();
            $rootScope.$digest();

            // THEN
            expect(mocks.catalogService.getProductCatalogsForSite).toHaveBeenCalledWith(SITE_2_UID);
        });

    it('GIVEN the service has catalogs of a site already cached AND that site has not changed' +
        'WHEN getProductCatalogsInformation is called ' +
        'THEN the catalog information should not be retrieved again',
        function() {
            // GIVEN 
            catalogInformationService.getProductCatalogsInformation();
            $rootScope.$digest();
            mocks.catalogService.getProductCatalogsForSite.calls.reset();

            // WHEN 
            catalogInformationService.getProductCatalogsInformation();
            $rootScope.$digest();

            // THEN
            expect(mocks.catalogService.getProductCatalogsForSite).not.toHaveBeenCalled();
        });

    it('WHEN productsFetchStrategy is called ' +
        'THEN it returns an object with the right fetchPage to retrieve multiple products',
        function() {
            // GIVEN
            var catalogInfo = {
                siteUID: SITE_1_UID
            };
            var mask = "some mask";
            var pageSize = 'some page size';
            var currentPage = 'some page';

            // WHEN
            var productsFetchStrategy = catalogInformationService.productsFetchStrategy;
            var promise = productsFetchStrategy.fetchPage(catalogInfo, mask, pageSize, currentPage);

            // THEN 
            promise.then(function(result) {
                expect(mocks.productService.findProducts).toHaveBeenCalledWith(catalogInfo, {
                    mask: mask,
                    pageSize: pageSize,
                    currentPage: currentPage
                });
                expect(result.pagination).toBe(products.pagination);
                expect(result.results).toEqual([{
                    uid: 'some product',
                    id: 'some product'
                }]);
            });
            $rootScope.$digest();
        });

    it('WHEN productsFetchStrategy is called ' +
        'THEN it returns an object with the right fetchEntity to retrieve a single product',
        function() {
            // GIVEN
            var productId = "someProductId";

            // WHEN
            var productsFetchStrategy = catalogInformationService.productsFetchStrategy;
            var promise = productsFetchStrategy.fetchEntity(productId);

            // THEN 
            promise.then(function(result) {
                expect(mocks.productService.getProductById).toHaveBeenCalledWith(SITE_1_UID, productId);
                expect(result).toEqual({
                    uid: 'some product',
                    id: 'some product'
                });
            });
            $rootScope.$digest();
        });

    it('WHEN categoriesFetchStrategy is called ' +
        'THEN it returns an object with the right fetchPage to retrieve multiple categories',
        function() {
            // GIVEN
            var catalogInfo = {
                siteUID: SITE_1_UID
            };
            var mask = "some mask";
            var pageSize = 'some page size';
            var currentPage = 'some page';

            // WHEN
            var categoriesFetchStrategy = catalogInformationService.categoriesFetchStrategy;
            var promise = categoriesFetchStrategy.fetchPage(catalogInfo, mask, pageSize, currentPage);

            // THEN 
            promise.then(function(result) {
                expect(mocks.productCategoryService.getCategories).toHaveBeenCalledWith(catalogInfo, mask, pageSize, currentPage);
                expect(result.pagination).toBe(categories.pagination);
                expect(result.results).toEqual([{
                    uid: 'some category',
                    id: 'some category'
                }]);
            });
            $rootScope.$digest();
        });

    it('WHEN categoriesFetchStrategy is called ' +
        'THEN it returns an object with the right fetchEntity to retrieve a single category',
        function() {
            // GIVEN
            var categoryId = "someCategoryId";

            // WHEN
            var categoriesFetchStrategy = catalogInformationService.categoriesFetchStrategy;
            var promise = categoriesFetchStrategy.fetchEntity(categoryId);

            // THEN 
            promise.then(function(result) {
                expect(mocks.productCategoryService.getCategoryById).toHaveBeenCalledWith(SITE_1_UID, categoryId);
                expect(result).toEqual({
                    uid: 'some category',
                    id: 'some category'
                });
            });
            $rootScope.$digest();
        });
});
