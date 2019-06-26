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
describe('pageRestrictionsService', function() {

    var pageRestrictionsService, mocks, $q;

    var MOCK_RESTRICTIONS = unit.mockData.restrictions;
    var MOCK_PAGES_RESTRICTIONS = unit.mockData.pagesRestrictions;
    var MOCK_TIME_RESTRICTIONS_TYPE = {
        code: 'CMSTimeRestriction',
        name: {
            de: 'DAS blabla',
            en: 'Time Restriction'
        }
    };

    var MOCK_CMS_ITEMS = unit.mockData.cmsItems.componentItems;

    beforeEach(function() {
        var harness = AngularUnitTestHelper.prepareModule('pageRestrictionsServiceModule')
            .mock('restrictionTypesService', 'getRestrictionTypeForTypeCode')
            .mock('pageRestrictionsRestService', 'getPagesRestrictionsForPageId')
            .mock('pageRestrictionsRestService', 'getPagesRestrictionsForCatalogVersion')
            .mock('pageRestrictionsRestService', 'update')
            .mock('typeStructureRestService', 'getStructuresByCategory')
            .mock('restrictionsService', 'getAllRestrictions')
            .mock('cmsitemsRestService', 'getByIds')
            .mock('cmsitemsRestService', 'getById')
            .service('pageRestrictionsService');

        pageRestrictionsService = harness.service;
        mocks = harness.mocks;

        $q = harness.injected.$q;
    });

    beforeEach(function() {
        mocks.restrictionTypesService.getRestrictionTypeForTypeCode.and.returnResolvedPromise(MOCK_TIME_RESTRICTIONS_TYPE);
        mocks.pageRestrictionsRestService.getPagesRestrictionsForPageId.and.returnResolvedPromise(MOCK_PAGES_RESTRICTIONS);
        mocks.pageRestrictionsRestService.getPagesRestrictionsForCatalogVersion.and.returnResolvedPromise(MOCK_PAGES_RESTRICTIONS);
        mocks.pageRestrictionsRestService.update.and.returnResolvedPromise(true);
        mocks.cmsitemsRestService.getByIds.and.returnResolvedPromise([MOCK_CMS_ITEMS[1], MOCK_CMS_ITEMS[2]]);
        mocks.cmsitemsRestService.getById.and.returnResolvedPromise(MOCK_CMS_ITEMS[0]);
    });

    describe('updateRestrictionsByPageUID', function() {
        it('should update restrictions by page id', function() {
            pageRestrictionsService.updateRestrictionsByPageUID('homepage', [{
                description: "",
                name: "Anonymous User Restriction",
                typeCode: "CMSUserRestriction",
                uid: "anonymousUserRestriction"
            }, {
                description: "Page only applies on experience level: Mobile",
                name: "Restriction for Mobile Experience",
                typeCode: "CMSUiExperienceRestriction",
                uid: "MobileExperienceRestriction"
            }]);

            expect(mocks.pageRestrictionsRestService.update).toHaveBeenCalledWith({
                pageRestrictionList: [{
                    pageId: 'homepage',
                    restrictionId: 'anonymousUserRestriction'
                }, {
                    pageId: 'homepage',
                    restrictionId: 'MobileExperienceRestriction'
                }],
                pageid: 'homepage'
            });
        });
    });

    describe('getPageRestrictionsCountMapForCatalogVersion', function() {
        it('should return a map of page to number of restrictions', function() {
            expect(pageRestrictionsService.getPageRestrictionsCountMapForCatalogVersion()).toBeResolvedWithData({
                homepage: 2
            });
        });
    });

    describe('getPageRestrictionsCountForPageUID', function() {
        it('should return the page to number of restrictions for a given page UID', function() {
            expect(pageRestrictionsService.getPageRestrictionsCountForPageUID()).toBeResolvedWithData(2);
        });
    });

    describe('getRestrictionsByPageUUID', function() {
        it('should return the page to number of restrictions for a given page UUID', function() {
            expect(pageRestrictionsService.getRestrictionsByPageUUID('somePageUUId')).toBeResolvedWithData([MOCK_CMS_ITEMS[1], MOCK_CMS_ITEMS[2]]);
        });
    });

});
