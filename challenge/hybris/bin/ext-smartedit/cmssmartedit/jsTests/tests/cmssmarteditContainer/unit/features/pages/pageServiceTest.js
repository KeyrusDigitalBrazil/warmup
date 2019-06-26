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
describe('pageService', function() {

    var service;
    var mocks;
    var $q;

    var pageMock = unit.mockData.pages.PagesMocks;
    var pageRestMock = unit.mockData.pages.PagesRestMocks;

    var MOCK_FALLBACK_PAGE_IDS = ['somePrimaryPageId'];
    var MOCK_FALLBACK_PAGE = {
        uid: 'somePrimaryPageId'
    };

    var MOCK_VARIATION_PAGE_IDS = ['someVariationPageUid', 'someOtherVariationPageUid'];
    var MOCK_VARIATION_PAGES = [{
        uid: 'someVariationPageUid'
    }, {
        uid: 'someOtherVariationPageUid'
    }];

    var MOCK_CATALOG_DETAILS = {
        siteUID: 'mySite',
        catalogId: 'myCatalog',
        catalogVersion: 'myCatalogVersion'
    };

    var EXPECTED_REST_SERVICE_PAYLOAD = {
        typeCode: 'sometype',
        itemSearchParams: "defaultPage:true,pageStatus:ACTIVE",
        currentPage: 0,
        pageSize: 1000,
        siteUID: MOCK_CATALOG_DETAILS.siteUID,
        catalogId: MOCK_CATALOG_DETAILS.catalogId,
        catalogVersion: MOCK_CATALOG_DETAILS.catalogVersion
    };

    beforeEach(function() {
        var harness = AngularUnitTestHelper.prepareModule('pageServiceModule')
            .mock('cmsitemsRestService', 'getById')
            .mock('cmsitemsRestService', 'get')
            .mock('cmsitemsRestService', 'getByIdAndVersion')
            .mock('pageInfoService', 'getPageUUID')
            .mock('pagesRestService', 'get')
            .mock('pagesRestService', 'getById')
            .mock('pagesFallbacksRestService', 'getFallbacksForPageId')
            .mock('pagesVariationsRestService', 'getVariationsForPrimaryPageId')
            .mock('experienceService', 'getCurrentExperience')
            .mock('$routeParams', 'siteId')
            .mock('$routeParams', 'catalogId')
            .mock('$routeParams', 'catalogVersion')
            .mockConstant('CONTEXT_SITE_ID', 'CONTEXT_SITE_ID')
            .mockConstant('CONTEXT_CATALOG', 'CONTEXT_CATALOG')
            .mockConstant('CONTEXT_CATALOG_VERSION', 'CONTEXT_CATALOG_VERSION')
            .service('pageService');

        service = harness.service;
        mocks = harness.mocks;
        $q = harness.injected.$q;
    });

    describe('getPrimaryPagesForPageType', function() {
        it('should pass the correct path and query params to the rest service', function() {
            mocks.cmsitemsRestService.get.and.returnValue($q.when(true));
            service.getPrimaryPagesForPageType('sometype', MOCK_CATALOG_DETAILS);

            expect(mocks.cmsitemsRestService.get).toHaveBeenCalledWith(EXPECTED_REST_SERVICE_PAYLOAD);
        });

        it('should return the list of pages from the underlying rest service', function() {
            mocks.cmsitemsRestService.get.and.returnValue($q.when(pageRestMock.pages));
            expect(service.getPrimaryPagesForPageType('sometype', MOCK_CATALOG_DETAILS))
                .toBeResolvedWithData(pageMock);
        });
    });

    describe('getPrimaryPage', function() {
        it('should return a promise with no data if the provided UID corresponds to primary page', function() {
            mocks.pagesFallbacksRestService.getFallbacksForPageId.and.returnValue($q.when([]));
            expect(service.getPrimaryPage('somePrimaryPageId')).toBeResolvedWithData(undefined);
        });

        it('should return the primary page if the provided UID corresponds to page variation', function() {
            mocks.pagesFallbacksRestService.getFallbacksForPageId.and.returnValue($q.when(MOCK_FALLBACK_PAGE_IDS));
            mocks.pagesRestService.getById.and.returnValue(MOCK_FALLBACK_PAGE);
            expect(service.getPrimaryPage('someVariationPageUid')).toBeResolvedWithData(MOCK_FALLBACK_PAGE);
        });
    });

    describe('isPagePrimary', function() {
        it('should return a promise resolving to true if there are no fallbacks', function() {
            mocks.pagesFallbacksRestService.getFallbacksForPageId.and.returnValue($q.when([]));
            expect(service.isPagePrimary('somePrimaryPageId')).toBeResolvedWithData(true);
        });

        it('should return a promise resolving to false if there are fallbacks', function() {
            mocks.pagesFallbacksRestService.getFallbacksForPageId.and.returnValue($q.when(MOCK_FALLBACK_PAGE_IDS));
            expect(service.isPagePrimary('someVariationPageId')).toBeResolvedWithData(false);
        });
    });

    describe('getVariationPages', function() {
        it('should return a promise resolving to an empty list if there are no variations', function() {
            mocks.pagesVariationsRestService.getVariationsForPrimaryPageId.and.returnValue($q.when([]));
            expect(service.getVariationPages()).toBeResolvedWithData([]);
        });

        it('should return a list of variation pages for a primary page', function() {
            mocks.pagesVariationsRestService.getVariationsForPrimaryPageId.and.returnValue($q.when(MOCK_VARIATION_PAGE_IDS));
            mocks.pagesRestService.get.and.returnValue($q.when(MOCK_VARIATION_PAGES));
            expect(service.getVariationPages()).toBeResolvedWithData([{
                uid: 'someVariationPageUid'
            }, {
                uid: 'someOtherVariationPageUid'
            }]);
        });
    });

    it('getCurrentPageInfo will return the page information of current page loaded in the storefront', function() {

        var pageInfo = 'somePageInfo';

        mocks.cmsitemsRestService.getById.and.returnValue($q.when(pageInfo));
        mocks.pageInfoService.getPageUUID.and.returnValue($q.when('somePageUuid'));

        expect(service.getCurrentPageInfo()).toBeResolvedWithData(pageInfo);
        expect(mocks.cmsitemsRestService.getById).toHaveBeenCalledWith('somePageUuid');

    });

    it('WHEN getCurrentPageInfoByVersion is called it returns the information of the version of the current page loaded in the storefront', function() {
        // GIVEN 
        var pageInfo = 'somePageInfo';
        var pageUuid = 'somePageUuid';
        var versionId = 'some version Id';

        mocks.cmsitemsRestService.getByIdAndVersion.and.returnValue($q.when(pageInfo));
        mocks.pageInfoService.getPageUUID.and.returnValue($q.when(pageUuid));

        // WHEN 
        var result = service.getCurrentPageInfoByVersion(versionId);

        // THEN 
        expect(result).toBeResolvedWithData(pageInfo);
        expect(mocks.cmsitemsRestService.getByIdAndVersion).toHaveBeenCalledWith(pageUuid, versionId);
    });

});
