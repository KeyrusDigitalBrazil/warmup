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
describe('pageFacade', function() {

    var MOCK_PAGE = {
        uid: 'somePageUid',
        name: 'Some Page Name',
        typeCode: 'somePageTypeCode',
        label: 'some-page-label',
        catalogVersion: 'someCatalogVersionUid'
    };

    var MOCK_CATALOG_VERSION = {
        uid: 'someCatalogVersionUid',
        catalogId: 'someCatalogId',
        version: 'someVersion'
    };

    var MOCK_RESULT_WITH_RESPONSE = {
        response: [MOCK_PAGE]
    };

    var MOCK_RESULT_WITH_NO_RESPONSE = {
        response: []
    };

    var mocks,
        resource,
        mockCreatePageForSiteResource,
        restServiceFactory,
        service,
        $q;

    beforeEach(function() {
        resource = jasmine.createSpyObj('resource', ['get']);
        var harness = AngularUnitTestHelper.prepareModule('pageFacadeModule')
            .mock('cmsitemsRestService', 'get').and.returnValue(resource)
            .mock('cmsitemsRestService', 'create')
            .mock('catalogService', 'getCatalogVersionUUid')
            .mock('crossFrameEventService', 'publish')
            .mock('sharedDataService', 'get').and.returnValue(resource)
            .mock('restServiceFactory', 'get').and.returnValue(mockCreatePageForSiteResource)
            .mock('urlService', 'buildUriContext').and.returnValue(resource)
            .mock('catalogService', 'getCatalogVersionUUid').and.returnValue(resource)
            .mock('cmsitemsUri').and.returnValue('testUrl')
            .mockConstant('EVENTS', {
                PAGE_CREATED: 'PAGE_CREATED_EVENT'
            })
            .service('pageFacade');
        service = harness.service;
        mocks = harness.mocks;
        $q = harness.injected.$q;

        mockCreatePageForSiteResource = jasmine.createSpyObj('mockCreatePageForSiteResource', ['save']);
    });

    describe('contentPageWithLabelExists', function() {
        it('will return a promise resolving to true if the content page with given label exists', function() {
            mocks.cmsitemsRestService.get.and.returnValue($q.when(MOCK_RESULT_WITH_RESPONSE));

            expect(service.contentPageWithLabelExists(MOCK_PAGE.label, MOCK_CATALOG_VERSION.catalogId, MOCK_CATALOG_VERSION.version)).toBeResolvedWithData(true);
        });

        it('will return a promise resolving to false if the content page with given label does not exist', function() {
            mocks.cmsitemsRestService.get.and.returnValue($q.when(MOCK_RESULT_WITH_NO_RESPONSE));

            expect(service.contentPageWithLabelExists('labelDoesNotExist', MOCK_CATALOG_VERSION.catalogId, MOCK_CATALOG_VERSION.version)).toBeResolvedWithData(false);
        });
    });

    describe('createPage', function() {

        var mockCatVerId;
        var page;

        beforeEach(function() {
            mockCatVerId = 'mockCatVersionUuid';
            page = {
                catalogVersion: "bla"
            };

            mocks.catalogService.getCatalogVersionUUid.and.returnValue($q.when(mockCatVerId));
        });

        it("will use the provided page catalogVersion, and default onlyOneRestrictionMustApply", function() {
            service.createPage(page).then(function() {
                expect(mocks.cmsitemsRestService.create).toHaveBeenCalledWith({
                    catalogVersion: page.catalogVersion,
                    onlyOneRestrictionMustApply: false
                });
            });
        });

        it("will use the current page catalogVersion, and accept the provided onlyOneRestrictionMustApply", function() {
            page.onlyOneRestrictionMustApply = true;
            service.createPage(page).then(function() {
                expect(mocks.cmsitemsRestService.create).toHaveBeenCalledWith({
                    catalogVersion: mockCatVerId,
                    onlyOneRestrictionMustApply: true
                });
            });
        });

        it("will send the EVENTS.PAGE_CREATED event on successful creation", function() {

            var page = {
                unique: "1"
            };

            // success
            mocks.cmsitemsRestService.create.and.returnValue($q.when(page));

            service.createPage(page).then(function() {
                expect(mocks.crossFrameEventService.publish).toHaveBeenCalledWith("PAGE_CREATED_EVENT", page);
            });
        });

        it("will NOT send the EVENTS.PAGE_CREATED event on failure to create page", function() {

            // failure
            mocks.cmsitemsRestService.create.and.returnValue($q.reject({}));

            service.createPage(page).then(function() {
                expect(mocks.crossFrameEventService.publish).not.toHaveBeenCalled();
            });
        });
    });

    describe('createPageForSite', function() {

        var mockCatVerId;
        var page;
        var siteUid = "someSiteUid";

        beforeEach(function() {
            mockCatVerId = 'mockCatVersionUuid';
            page = {
                catalogVersion: "bla"
            };

            mocks.catalogService.getCatalogVersionUUid.and.returnValue($q.when(mockCatVerId));
        });

        it("will use the provided page catalogVersion, and default onlyOneRestrictionMustApply", function() {
            service.createPageForSite(page, siteUid).then(function() {
                expect(mockCreatePageForSiteResource.save).toHaveBeenCalledWith({
                    catalogVersion: page.catalogVersion,
                    onlyOneRestrictionMustApply: false
                });
            });
        });

        it("will use the current page catalogVersion, and accept the provided onlyOneRestrictionMustApply", function() {
            page.onlyOneRestrictionMustApply = true;
            service.createPageForSite(page, siteUid).then(function() {
                expect(mockCreatePageForSiteResource.save).toHaveBeenCalledWith({
                    catalogVersion: mockCatVerId,
                    onlyOneRestrictionMustApply: true
                });
            });
        });

        it("will send the EVENTS.PAGE_CREATED event on successful creation", function() {

            var page = {
                unique: "1"
            };

            // success
            mocks.cmsitemsRestService.create.and.returnValue($q.when(page));

            service.createPageForSite(page, siteUid).then(function() {
                expect(mocks.crossFrameEventService.publish).toHaveBeenCalledWith("PAGE_CREATED_EVENT", page);
            });
        });

        it("will NOT send the EVENTS.PAGE_CREATED event on failure to create page", function() {

            // failure
            mocks.cmsitemsRestService.create.and.returnValue($q.reject({}));

            service.createPageForSite(page, siteUid).then(function() {
                expect(mocks.crossFrameEventService.publish).not.toHaveBeenCalled();
            });
        });
    });
});
