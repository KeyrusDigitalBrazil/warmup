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
describe('displayConditionsFacade', function() {

    var MOCK_PAGE = {
        uid: 'somePageUid',
        name: 'Some Page Name',
        typeCode: 'somePageTypeCode',
        label: 'some-page-label'
    };

    var MOCK_VARIATION_PAGES = [{
        uid: 'someVariationPageId',
        name: 'Some Variation Page Name',
        creationtime: '2016-07-07T14:33:37Z'
    }, {
        uid: 'someOtherVariationPageId',
        name: 'Some Other Variation Page Name',
        creationtime: '2016-07-08T14:33:37Z'
    }];

    var MOCK_PRIMARY_PAGES = [{
        uid: 'somePrimaryPageUid',
        name: 'Some Primary Page',
        label: 'some-primary-page'
    }, {
        uid: 'someOtherPrimaryPageUid',
        name: 'Some Other Primary Page',
        label: 'some-other-primary-page'
    }];

    var MOCK_VARIATION_PAGE_IDS = ['someVariationPageId', 'someOtherVariationPageId'];

    var mocks,
        resource,
        service,
        $q;

    beforeEach(function() {
        resource = jasmine.createSpyObj('resource', ['get']);
        var harness = AngularUnitTestHelper.prepareModule('displayConditionsFacadeModule')
            .mock('pageDisplayConditionsService', 'getPageDisplayConditionsDescriptionI18nKeyForPageId').and.returnValue(resource)
            .mock('pageDisplayConditionsService', 'getPageDisplayConditionsValueForPageId').and.returnValue(resource)
            .mock('pageRestrictionsService', 'getPageRestrictionsCountForPageUID')
            .mock('pageService', 'getPageById').and.returnValue(resource)
            .mock('pageService', 'getPrimaryPagesForPageType')
            .mock('pageService', 'getVariationPages')
            .mock('pageService', 'getPrimaryPage').and.returnValue(resource)
            .mock('pageService', 'isPagePrimary').and.returnValue(resource)
            .mock('pageService', 'updatePageById')
            .service('displayConditionsFacade');
        service = harness.service;
        mocks = harness.mocks;
        $q = harness.injected.$q;
    });

    describe('getPageInfoForPageUid', function() {
        beforeEach(function() {
            mocks.pageService.getPageById.and.returnValue($q.when(MOCK_PAGE));
            mocks.pageService.isPagePrimary.and.returnValue($q.when(true));
        });

        it('should retrieve the page name, type code, and whether or not the page is primary', function() {
            expect(service.getPageInfoForPageUid('somePageUid')).toBeResolvedWithData({
                pageName: 'Some Page Name',
                pageType: 'somePageTypeCode',
                isPrimary: true
            });
        });
    });

    describe('getVariationsForPageUid', function() {
        it('will return a promise resolving to an empty array if no variations are found', function() {
            mocks.pageService.getVariationPages.and.returnValue($q.when([]));
            expect(service.getVariationsForPageUid('somePageUid')).toBeResolvedWithData([]);
        });

        it('will return a list of variation pages, each of which having a page name, creation date, and number of restrictions', function() {
            mocks.pageService.getVariationPages.and.returnValue($q.when(MOCK_VARIATION_PAGES));
            mocks.pageRestrictionsService.getPageRestrictionsCountForPageUID.and.callFake(function(pageUid) {
                if (pageUid === 'someVariationPageId') {
                    return $q.when(1);
                } else {
                    return $q.when(2);
                }
            });

            expect(service.getVariationsForPageUid('somePageUid')).toBeResolvedWithData([{
                pageName: 'Some Variation Page Name',
                creationDate: '2016-07-07T14:33:37Z',
                restrictions: 1
            }, {
                pageName: 'Some Other Variation Page Name',
                creationDate: '2016-07-08T14:33:37Z',
                restrictions: 2
            }]);
        });
    });

    describe('getPrimaryPageForVariationPage', function() {
        beforeEach(function() {
            mocks.pageService.getPrimaryPage.and.returnValue($q.when(MOCK_PAGE));
        });

        it('should return the primary page uid, label, and name', function() {
            expect(service.getPrimaryPageForVariationPage('someVariationPageUid')).toBeResolvedWithData({
                uid: 'somePageUid',
                name: 'Some Page Name',
                label: 'some-page-label'
            });
        });
    });

    describe('getPrimaryPagesForVariationPageType', function() {
        beforeEach(function() {
            mocks.pageService.getPrimaryPagesForPageType.and.returnValue($q.when(MOCK_PRIMARY_PAGES));
        });

        it('should return a list of primary page uids, names, and labels', function() {
            expect(service.getPrimaryPagesForVariationPageType('someVariationPageType')).toBeResolvedWithData([{
                uid: 'somePrimaryPageUid',
                name: 'Some Primary Page',
                label: 'some-primary-page'
            }, {
                uid: 'someOtherPrimaryPageUid',
                name: 'Some Other Primary Page',
                label: 'some-other-primary-page'
            }]);
        });
    });

    describe('updatePage', function() {
        it('should delegate to the pageService to update the page', function() {
            service.updatePage('somePageUid', {});
            expect(mocks.pageService.updatePageById).toHaveBeenCalledWith('somePageUid', {});
        });
    });

    describe('isPagePrimary', function() {
        it('should delegate the call to the pageService', function() {
            service.isPagePrimary('somePageId');
            expect(mocks.pageService.isPagePrimary).toHaveBeenCalledWith('somePageId');
        });
    });

});
